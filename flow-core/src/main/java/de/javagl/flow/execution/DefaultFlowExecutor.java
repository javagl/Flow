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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.flow.Flow;
import de.javagl.flow.module.Module;

/**
 * Default implementation of a {@link FlowExecutor}.<br>
 * <br>
 * This {@link FlowExecutor} will execute all {@link Module} instances in 
 * a given {@link Flow} using "wave fronts": At each step, it will 
 * execute all modules whose predecessors have already been executed. 
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
    private static final Level level = Level.FINE;
    
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
        
        // Immediately attempt an orderly shutdown, waiting infinitely
        // for all tasks to be completed (unless the calling thread is
        // interrupted)
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
        
        // Forward information about the completion, or about possible errors 
        // or the cancellation state to the registered listeners 
        Collection<Throwable> errors = null;
        if (error != null)
        {
            logger.warning("Executing flow DONE, error: " + error);
            errors = Collections.singleton(error);
        }
        else if (cancelled)
        {
            logger.warning("Executing flow DONE, but cancelled");
        }
        else 
        {
            logger.log(level, "Executing flow DONE");
        }
        fireAfterExecution(flow, cancelled, errors);
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
        
        // First, try to wait if the execution terminates normally
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
        
        // If the execution terminated normally, everything is fine
        if (executorService.isTerminated())
        {
            long afterNs = System.nanoTime();
            double seconds = (afterNs - beforeNs) * 1e-9;
            logger.log(level, String.format(Locale.ENGLISH, 
                "Execution completed after %.2f seconds", seconds));
            return null;
        }

        // Try to force the execution to finish, and wait once more
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
        
        // That was close. But managed to shut down normally.
        if (success)
        {
            logger.log(level, "Terminated after forced shutdown within " 
                + timeout + " " + unit);
            return null;
        }
        
        // Shutting down failed
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
     * <code>InterruptedException</code>
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
            FlowExecutorUtils.log(level, "Executing ", executionSet);
            Exception error = executeAll(executionSet);
            if (error != null)
            {
                logger.fine("Executing failed: " + error);
                return error;
            }
            FlowExecutorUtils.log(level, "Executing DONE", executionSet);
        }
        return null;
    }

    /**
     * Execute all the given {@link Module} instances (in parallel) by 
     * calling their {@link Module#execute()} methods.
     * 
     * @param modules The {@link Module} instances
     * @return The first exception that was caused, or <code>null</code> if
     * the execution finished normally. The returned exception will usually
     * be an <code>ExecutionException</code> or an 
     * <code>InterruptedException</code>
     */
    private Exception executeAll(Iterable<? extends Module> modules)
    {
        try
        {
            Collection<Callable<Object>> callables = 
                FlowExecutorUtils.createCallables(modules);
            return FlowExecutorUtils.executeAll(executorService, callables);
        } 
        catch (InterruptedException e)
        {
            logger.severe(
                "Interrupted while invoking module execution tasks");
            Thread.currentThread().interrupt();
            return e;
        }
    }
    
}
