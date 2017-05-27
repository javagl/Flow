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
package de.javagl.flow.repository;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Implementation of the {@link RepositoryListener} interface that
 * simply prints all operations as logging messages.
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
public final class LoggingRepositoryListener<K, V> 
    implements RepositoryListener<K, V>
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(LoggingRepositoryListener.class.getName());
    
    /**
     * The log level that will be used for the output
     */
    private static Level level = Level.INFO;
    
    /**
     * Convenience factory method
     * 
     * @param <K> The key type
     * @param <V> The value type
     * @return The LoggingRepositoryListener
     */
    public static <K, V> LoggingRepositoryListener<K, V> create()
    {
        return new LoggingRepositoryListener<K, V>();
    }
    
    @Override
    public void valuesAdded(RepositoryEvent<K, V> repositoryEvent)
    {
        logger.log(level, "valuesAdded   "
            + repositoryEvent.getAddedValues());
    }

    @Override
    public void valuesRemoved(RepositoryEvent<K, V> repositoryEvent)
    {
        logger.log(level, "valuesRemoved "
            + repositoryEvent.getRemovedValues());
    }
       
}