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
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.flow.Flow;
import de.javagl.flow.module.Module;

/**
 * Default implementation of a {@link FlowExecutor}
 */
class DefaultFlowExecutor implements FlowExecutor
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

    @Override
    public void execute(Flow flow)
    {
        logger.log(level, "Executing flow");

        Set<Module> modules = flow.getModules();
        if (modules.isEmpty())
        {
            return;
        }
        
        executorService = 
            ExecutorExtensions.newExceptionAwareCachedThreadPool();
        FlowExecutorUtils.startWatchdog(executorService);
        
        boolean done = false;
        try
        {
            done = execute(modules);
        }
        finally
        {
            executorService.shutdown();
        }
        
        if (!done)
        {
            logger.warning("Executing flow caused an error");
        }
        else
        {
            logger.log(level, "Executing flow DONE");
        }
    }

    /**
     * Execute the given collection of {@link Module} instances
     * 
     * @param modules The {@link Module} instances
     * @return Whether the execution completed normally
     */
    private boolean execute(Collection<? extends Module> modules)
    {
        List<Set<Module>> executionSets = 
            FlowExecutorUtils.computeExecutionSets(modules);
        for (Set<Module> executionSet : executionSets)
        {
            log("Executing ", executionSet);
            
            boolean done = executeAll(executionSet);
            if (!done)
            {
                return false;
            }
            
            log("Executing DONE", executionSet);
        }
        return true;
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
     * @return Whether the execution completed normally
     */
    private boolean executeAll(Iterable<? extends Module> modules)
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
            return false;
        }
    }
    
    /**
     * Execute the given callables. If one of them throws an ExecutionException,
     * the remaining ones are cancelled.
     * 
     * @param callables The callables
     * @return Whether the execution completed normally
     * @throws InterruptedException If the thread was interrupted
     */
    private boolean executeAll(Collection<Callable<Object>> callables) 
        throws InterruptedException
    {
        CompletionService<Object> completionService =
            new ExecutorCompletionService<Object>(executorService);
        int n = callables.size();
        List<Future<Object>> futures = new ArrayList<Future<Object>>(n);
        boolean caughtException = false;
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
                    logger.severe("Exception during module execution");
                    caughtException = true;
                    break;
                }
            }
        } 
        finally
        {
            if (caughtException)
            {
                logger.severe("Canceling execution of remaining modules");
                for (Future<Object> f : futures)
                {
                    f.cancel(true);
                }
            }
        }
        return !caughtException;
    }    
    
}
