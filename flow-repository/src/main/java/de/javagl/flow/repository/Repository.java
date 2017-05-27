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

import java.util.List;
import java.util.Set;

/**
 * Interface for a repository. A repository stores key-value pairs. 
 * In contrast to a map, the key for each value is defined by a 
 * <code>Function&lt;V,K&gt;</code>, so that each value can only be 
 * stored under one key. <br>
 * <br>
 * Internally, a repository maintains a counter storing how often a 
 * certain key-value mapping has been added. Thus, when a key-value 
 * pair is added twice to the repository, it also has to be removed 
 * twice in order to be no longer present.
 *   
 * @param <K> The key type
 * @param <V> The value type
 */
public interface Repository<K, V>
{
    /**
     * Returns an unmodifiable set containing all keys of this repository. 
     * Changes in the repository will not be visible in the returned set.  
     * 
     * @return The key set of this repository
     */
    Set<K> keySet();

    /**
     * Returns the value for the given key, or <code>null</code> if there 
     * is no value stored for the given key.
     * 
     * @param key The key
     * @return The value for the given key, or <code>null</code>
     */
    V get(K key);

    /**
     * Returns an unmodifiable (possibly empty) list containing the values 
     * for the given keys. Changes in the repository will not be visible 
     * in the returned list. If the given sequence contains a key for which 
     * no entry can be found in this repository, then the corresponding 
     * entry in the list will be <code>null</code>.
     * 
     * @param keys The keys
     * @return The values
     */
    List<V> getAll(Iterable<? extends K> keys);
    
    /**
     * Add the given values to this repository. A single value may be 
     * added with 
     * <code>repository.add(Collections.singleton(value));</code>
     * 
     * @param values The values to add.
     */
    void add(Iterable<? extends V> values);

    /**
     * Remove the given values from this repository. A single value may be
     * removed with
     *  <code>repository.remove(Collections.singleton(value));</code>
     * 
     * @param values The values to remove.
     */
    void remove(Iterable<? extends V> values);
    
    /**
     * Add the given {@link RepositoryListener} to be informed about changes
     * in this repository. <br>
     * <br>
     * The listener will only be informed about actual changes of the
     * values. When {@link #add(Iterable)} is called, then the 
     * the {@link RepositoryEvent} that is passed to the listener will
     * <i>only</i> contain the values that have not already been present.
     * When {@link #remove(Iterable)} is called, then the 
     * {@link RepositoryEvent} will <i>only</i> contain the values that
     * are no longer present in the repository.
     * 
     * @param repositoryListener The listener to add
     */
    void addRepositoryListener(RepositoryListener<K, V> repositoryListener);

    /**
     * Remove the given {@link RepositoryListener} from this repository
     * 
     * @param repositoryListener The listener to remove
     */
    void removeRepositoryListener(RepositoryListener<K, V> repositoryListener);
    
}