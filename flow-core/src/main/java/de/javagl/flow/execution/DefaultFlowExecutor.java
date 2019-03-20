/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.flow.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.flow.Flow;
import de.javagl.flow.module.Module;

/**
 * Default implementation of a {@link FlowExecutor}
 */
class DefaultFlowExecutor extends AbstractFlowExecutor implements FlowExecutor
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(DefaultFlowExecutor.class.getName());
    
    /**
     * The log level for execution process messages
     */
    private static final Level level = Level.INFO;
    
    /**
     * The executor service for the current call to {@link #execute(Flow)}
     */
    private ExecutorService executorService;
    
    /**
     * Whether the execution should be cancelled
     */
    private volatile boolean cancelled;

    @Override
    public void execute(Flow flow)
    {
        logger.log(level, "Executing flow");
        cancelled = false;
        fireBeforeExecution(flow);

        executorService = 
            ExecutorExtensions.newExceptionAwareCachedThreadPool();
        
        Set<Module> modules = flow.getModules();
        Exception error = execute(modules);
        if (error != null)
        {
            logger.warning("Executing flow caused an error: " + error);
        }
        else
        {
            logger.log(level, "Executing flow DONE");
        }
        
        executorService.shutdown();
        try
        {
            boolean terminated = executorService.awaitTermination(
                Long.MAX_VALUE, TimeUnit.DAYS);
            if (terminated)
            {
                executorService = null;
            }
        } 
        catch (InterruptedException e)
        {
            logger.warning(
                "Interrupted while waiting for execution to complete. " + e);
            Thread.currentThread().interrupt();
            if (error == null)
            {
                error = e;
            }
        }
        Collection<Throwable> errors = null;
        if (error != null)
        {
            errors = Collections.singleton(error);
        }
        fireAfterExecution(flow, errors);
    }

    @Override
    public Exception finishExecution(long timeout, TimeUnit unit)
    {
        if (executorService == null)
        {
            return null;
        }
        cancelled = true;
        executorService.shutdown();
        
        logger.log(level, "Waiting for up to " + timeout + " " + unit
            + " for the execution to complete...");
        long beforeNs = System.nanoTime();
        try
        {
            executorService.awaitTermination(timeout, unit);
        } 
        catch (InterruptedException e)
        {
            logger.warning(
                "Interrupted while waiting for execution to complete. " + e);
            Thread.currentThread().interrupt();
            return e;
        }
        
        if (executorService.isTerminated())
        {
            long afterNs = System.nanoTime();
            double seconds = (afterNs - beforeNs) * 1e-9;
            logger.log(level, String.format(Locale.ENGLISH, 
                "Execution completed after %.2f seconds", seconds));
            return null;
        }

        logger.log(level, "Timeout of " + timeout + " " + unit
            + " passed, shutting down NOW");
        executorService.shutdownNow();
        boolean success = false;
        try
        {
            success = executorService.awaitTermination(timeout, unit);
        } 
        catch (InterruptedException e)
        {
            logger.warning(
                "Interrupted while waiting for execution to finish. " + e);
            Thread.currentThread().interrupt();
            return e;
        }
        if (success)
        {
            logger.log(level, "Terminated after forced shutdown within " 
                + timeout + " " + unit);
            return null;
        }
        return new TimeoutException(
            "Could not shut down within " + timeout + " " + unit);
    }
    
    /**
     * Execute the given collection of {@link Module} instances
     * 
     * @param modules The {@link Module} instances
     * @return The first exception that was caused, or <code>null</code> if
     * the execution finished normally. The returned exception will usually
     * be an <code>ExecutionException</code> or an 
     * <code>InterruptedException</code>, or a 
     * <code>RejectedExecutionException</code> if the execution was
     * cancelled
     */
    private Exception execute(Collection<? extends Module> modules)
    {
        List<Set<Module>> executionSets = 
            FlowExecutorUtils.computeExecutionSets(modules);
        for (Set<Module> executionSet : executionSets)
        {
            if (cancelled)
            {
                return null;
            }
            log("Executing ", executionSet);
            Exception error = executeAll(executionSet);
            if (error != null)
            {
                log("Executing failed: " + error, executionSet);
                return error;
            }
            log("Executing DONE", executionSet);
        }
        return null;
    }

    /**
     * Log the give message and the string representations of the given modules
     * 
     * @param message The message
     * @param modules The modules
     */
    private static void log(String message, Iterable<? extends Module> modules)
    {
        logger.log(level, message);
        for (Module module : modules)
        {
            logger.log(level, "    " + module);
        }
    }
    
    
    /**
     * Execute all the given {@link Module} instances (in parallel) by 
     * calling their {@link Module#execute()} methods.
     * 
     * @param modules The {@link Module} instances
     * @return The first exception that was caused, or <code>null</code> if
     * the execution finished normally. The returned exception will usually
     * be an <code>ExecutionException</code> or an 
     * <code>InterruptedException</code>, or a 
     * <code>RejectedExecutionException</code> if the execution was
     * cancelled
     */
    private Exception executeAll(Iterable<? extends Module> modules)
    {
        try
        {
            Collection<Callable<Object>> callables = 
                FlowExecutorUtils.createCallables(modules);
            return executeAll(callables);
        } 
        catch (InterruptedException e)
        {
            logger.severe(
                "Interrupted while invoking module execution tasks");
            Thread.currentThread().interrupt();
            return e;
        }
    }
    
    /**
     * Execute the given callables. If one of them throws an ExecutionException,
     * the remaining ones are cancelled.
     * 
     * @param callables The callables
     * @return The first exception that was caused, or 
     * <code>null</code> if the execution finished normally
     * @throws InterruptedException If the thread was interrupted
     */
    private Exception executeAll(
        Collection<Callable<Object>> callables) 
        throws InterruptedException
    {
        CompletionService<Object> completionService =
            new ExecutorCompletionService<Object>(executorService);
        int n = callables.size();
        List<Future<Object>> futures = new ArrayList<Future<Object>>(n);
        Exception caughtException = null;
        try
        {
            for (Callable<Object> callable : callables)
            {
                futures.add(completionService.submit(callable));
            }
            for (int i = 0; i < n; ++i)
            {
                try
                {
                    Future<Object> future = completionService.take();
                    future.get();
                } 
                catch (ExecutionException e)
                {
                    logger.severe("Exception during module execution: " + e);
                    caughtException = e;
                    break;
                }
            }
        } 
        catch (RejectedExecutionException e)
        {
            logger.severe("Cannot schedule module execution: " + e);
            caughtException = e;
        }
        finally
        {
            if (caughtException != null)
            {
                logger.severe("Canceling execution of remaining modules");
                for (Future<Object> f : futures)
                {
                    f.cancel(true);
                }
            }
        }
        return caughtException;
    }    
    
}
