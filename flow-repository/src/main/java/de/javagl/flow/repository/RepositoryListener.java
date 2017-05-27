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

/**
 * Interface for classes that want to be informed about changes in a
 * {@link Repository}
 * 
 * @param <K> The key type of the {@link Repository}
 * @param <V> The value type of the {@link Repository}
 */
public interface RepositoryListener<K, V>
{
    /**
     * Will be called when new values have been added to 
     * the {@link Repository}.
     * 
     * @param repositoryEvent The {@link RepositoryEvent} describing the change
     */
    void valuesAdded(RepositoryEvent<K, V> repositoryEvent);
    
    /**
     * Will be called when values have been removed from 
     * the {@link Repository}.
     * 
     * @param repositoryEvent The {@link RepositoryEvent} describing the change
     */
    void valuesRemoved(RepositoryEvent<K, V> repositoryEvent);
    
}
