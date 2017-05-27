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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods to create special ExecutorService instances 
 */
class ExecutorExtensions
{
    /**
     * Returns an ExecutorService that is similar to one returned by
     * <code>Executors#newCachedThreadPool</code>, but re-throws 
     * exceptions that happen in one of the submitted tasks.
     * 
     * @return The new ExecutorService
     */
    static ExecutorService newExceptionAwareCachedThreadPool()
    {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>())
        {
            @Override
            protected void afterExecute(Runnable r, Throwable t)
            {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>)
                {
                    try
                    {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone())
                        {
                            future.get();
                        }
                    }
                    catch (CancellationException ce)
                    {
                        t = ce;
                    }
                    catch (ExecutionException ee)
                    {
                        t = ee.getCause();
                    }
                    catch (InterruptedException ie)
                    {
                        Thread.currentThread().interrupt();
                    }
                }
                if (t != null)
                {
                    throw new RuntimeException("Error in task", t);
                }
            }
        };
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private ExecutorExtensions()
    {
        // Private constructor to prevent instantiation
    }
}
