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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleUtils;

/**
 * Utility methods for {@link FlowExecutor} implementations 
 */
class FlowExecutorUtils
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(FlowExecutorUtils.class.getName());
    
    /**
     * The log level for execution process messages
     */
    private static final Level level = Level.FINE;
    
    /**
     * Create a list containing one callable object for each {@link Module} 
     * of the given sequence, where the callable will call the 
     * {@link Module#execute()} method.
     * 
     * @param modules The {@link Module} instances
     * @return The callable objects
     */
    static Collection<Callable<Object>> createCallables(
        Iterable<? extends Module> modules)
    {
        Collection<Callable<Object>> callables = 
            new ArrayList<Callable<Object>>();
        for (Module module : modules)
        {
            callables.add(FlowExecutorUtils.createCallable(module));
        }
        return callables;
    }
    
    /**
     * Create a callable that calls the {@link Module#execute()} method of
     * the given {@link Module}.
     * 
     * @param module The {@link Module}
     * @return The callable.
     */
    private static Callable<Object> createCallable(Module module)
    {
        Objects.requireNonNull(module, "The module may not be null");
        return Executors.callable(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    module.execute();
                }
                catch (Throwable t)
                {
                    RuntimeException r = new RuntimeException(
                        "Error in module " + module, t);
                    throw r;
                }
            }
            
            @Override
            public String toString()
            {
                return "Task for " + module;
            }
        });
    }
    
    /**
     * Computes the unmodifiable set of "root" {@link Module} instances from 
     * the given collection. The "root" modules are those that do not have 
     * any predecessors in the given collection (although they may have 
     * predecessors that are <b>not</b> contained in the given collection!)
     * 
     * @param modules The {@link Module} instances
     * @return The "root" modules
     */
    private static Set<Module> computeRootModules(
        Collection<? extends Module> modules)
    {
        Set<Module> rootModules = new LinkedHashSet<Module>();
        for (Module module : modules)
        {
            Set<Module> predecessors = 
                new LinkedHashSet<Module>(
                    ModuleUtils.computePredecessors(module));
            predecessors.retainAll(modules);
            if (predecessors.isEmpty())
            {
                rootModules.add(module);
            }
        }
        return Collections.unmodifiableSet(rootModules);
    }
    
    /**
     * Compute a list of execution sets from the given {@link Module} 
     * collection. The first set will contain all modules that do not 
     * require any inputs. Each subsequent set will contain all modules 
     * whose predecessors have been contained in any of the previous sets.
     * 
     * @param modules The input {@link Module} collection
     * @return The execution sets
     */
    static List<Set<Module>> computeExecutionSets(
        Collection<? extends Module> modules)
    {
        List<Set<Module>> sets = new ArrayList<Set<Module>>();
        Set<Module> rootModules = computeRootModules(modules);
        sets.add(rootModules);
        Set<Module> remaining = new LinkedHashSet<Module>(modules);
        remaining.removeAll(rootModules);
        Set<Module> processed = new LinkedHashSet<Module>();
        processed.addAll(rootModules);
        
        while (!remaining.isEmpty())
        {
            Set<Module> set = new LinkedHashSet<Module>();
            for (Module module : remaining)
            {
                Set<Module> predecessors = 
                    ModuleUtils.computePredecessors(module);
                if (processed.containsAll(predecessors))
                {
                    set.add(module);
                }
            }
            sets.add(set);
            processed.addAll(set);
            remaining.removeAll(set);
        }
        return sets;
    }
    
    
    /**
     * Starts a watchdog thread that waits for the termination of the given
     * executor service, for an unspecified time. If the executor service
     * does not terminate in this time, it is attempted to shut it down.
     * 
     * @param executorService The executor service
     */
    static void startWatchdog(ExecutorService executorService)
    {
        Thread thread = new Thread(() -> runWatchdog(executorService));
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Implementation for the {@link #startWatchdog(ExecutorService)} method.
     * 
     * @param executorService The executor service
     */
    private static void runWatchdog(ExecutorService executorService)
    {
        int timeout = 30;
        TimeUnit unit = TimeUnit.SECONDS;
        
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
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (executorService.isTerminated())
        {
            long afterNs = System.nanoTime();
            double seconds = (afterNs - beforeNs) * 1e-9;
            logger.log(level, String.format(Locale.ENGLISH, 
                "Execution completed after %.2f seconds", seconds));
        }
        else
        {
            logger.warning("Execution blocked, shutting down NOW");
            executorService.shutdownNow();
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private FlowExecutorUtils()
    {
        // Private constructor to prevent instantiation
    }
    
    
    
}
