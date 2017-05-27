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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;


/**
 * Default implementation of a {@link Repository}
 * 
 * @param <K> The key type
 * @param <V> The value type
 */
final class DefaultRepository<K, V> implements Repository<K, V>
{
    /**
     * The function that provides the keys for the values
     */
    private final Function<V, K> keyFunction;
    
    /**
     * The mapping from keys to values
     */
    private final Map<K, V> map;
    
    /**
     * The reference counters for the keys
     */
    private final Map<K, Integer> referenceCounters;
    
    /**
     * The list of {@link RepositoryListener} instances
     */
    private final List<RepositoryListener<K, V>> repositoryListeners;
    
    /**
     * Creates a new repository that uses the given function to obtain the
     * keys for values that are added or removed.
     * 
     * @param keyFunction The {@link Function} 
     */
    DefaultRepository(Function<V, K> keyFunction)
    {
        this.keyFunction = Objects.requireNonNull(
            keyFunction, "The keyFunction may not be null");
        this.map = new LinkedHashMap<K, V>();
        this.referenceCounters = new LinkedHashMap<K, Integer>();
        this.repositoryListeners = 
            new CopyOnWriteArrayList<RepositoryListener<K, V>>();
    }
    
    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(
            new LinkedHashSet<K>(map.keySet()));
    }
    
    @Override
    public V get(K moduleInfo)
    {
        return map.get(moduleInfo);
    }
    
    @Override
    public List<V> getAll(Iterable<? extends K> keys)
    {
        List<V> result = new ArrayList<V>();
        for (K key : keys)
        {
            V value = get(key);
            result.add(value);
        }
        return Collections.unmodifiableList(result);
    }
    
    @Override
    public void add(Iterable<? extends V> values)
    {
        List<V> addedValues = new ArrayList<V>();
        for (V value : values)
        {
            boolean wasNew = addInternal(value);
            if (wasNew)
            {
                addedValues.add(value);
            }
        }
        if (!addedValues.isEmpty())
        {
            fireValuesAdded(addedValues);
        }
    }
    
    @Override
    public void remove(Iterable<? extends V> values)
    {
        List<V> removedValues = new ArrayList<V>();
        for (V value : values)
        {
            boolean wasLast = removeInternal(value);
            if (wasLast)
            {
                removedValues.add(value);
            }
        }
        if (!removedValues.isEmpty())
        {
            fireValuesRemoved(removedValues);
        }
    }
    
    
    @Override
    public void addRepositoryListener(
        RepositoryListener<K, V> repositoryListener)
    {
        repositoryListeners.add(repositoryListener);
    }
    
    @Override
    public void removeRepositoryListener(
        RepositoryListener<K, V> repositoryListener)
    {
        repositoryListeners.remove(repositoryListener);
    }
    
    /**
     * Notify each registered {@link RepositoryListener} that the given 
     * values have been added
     * 
     * @param addedValues The added values
     */
    private void fireValuesAdded(List<V> addedValues)
    {
        if (!repositoryListeners.isEmpty())
        {
            RepositoryEvent<K, V> repositoryEvent = 
                new RepositoryEvent<K, V>(this, addedValues, null);
            for (RepositoryListener<K, V> listener : repositoryListeners)
            {
                listener.valuesAdded(repositoryEvent);
            }
        }
    }

    /**
     * Notify each registered {@link RepositoryListener} that the given 
     * values have been removed
     * 
     * @param removedValues The removed values
     */
    private void fireValuesRemoved(List<V> removedValues)
    {
        if (!repositoryListeners.isEmpty())
        {
            RepositoryEvent<K, V> repositoryEvent = 
                new RepositoryEvent<K, V>(this, null, removedValues);
            for (RepositoryListener<K, V> listener : repositoryListeners)
            {
                listener.valuesRemoved(repositoryEvent);
            }
        }
    }
    
    /**
     * Internal method to add the given value
     * 
     * @param value The value to add
     * @return Whether the value was new (i.e. not yet present) in this 
     * repository
     */
    private boolean addInternal(V value)
    {
        K key = keyFunction.apply(value);
        Integer referenceCounter = referenceCounters.get(key);
        boolean wasNew = false;
        if (referenceCounter == null)
        {
            referenceCounter = 0;
            wasNew = true;
        }
        referenceCounters.put(key, referenceCounter + 1);
        map.put(key, value);
        return wasNew;
    }

    /**
     * Internal method to remove the given value
     * 
     * @param value The value to remove
     * @return Whether the value was the last instance in this repository
     * (i.e. whether it is no longer present after this remove operation)
     */
    private boolean removeInternal(V value)
    {
        K key = keyFunction.apply(value);
        Integer referenceCounter = referenceCounters.get(key);
        if (referenceCounter == null)
        {
            return false;
        }
        boolean wasLast = false;
        if (referenceCounter == 1)
        {
            referenceCounters.remove(key);
            map.remove(key);
            wasLast = true;
        }
        else
        {
            referenceCounters.put(key, referenceCounter - 1);
        }
        return wasLast;
    }
    
}
