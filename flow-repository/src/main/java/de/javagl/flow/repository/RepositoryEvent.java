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
import java.util.EventObject;
import java.util.List;

/**
 * An event that is passed to a {@link RepositoryListener}, indicating a 
 * change in a {@link Repository}
 * 
 * @param <K> The key type of the {@link Repository}
 * @param <V> The value type of the {@link Repository}
 */
public final class RepositoryEvent<K, V> extends EventObject
{
    /**
     * The {@link Repository} from which this event originated
     */
    private final Repository<K, V> repository;
    
    /**
     * The (optional) values that have been added, causing this event. 
     */
    private final List<V> addedValues;
    
    /**
     * The (optional) values that have been removed, causing this event. 
     */
    private final List<V> removedValues;
    
    /**
     * Creates a new instance, originating from the given {@link Repository}
     * 
     * @param repository The {@link Repository}
     * @param addedValues The (optional) values that have been added
     * @param removedValues The (optional) values that have been removed
     */
    public RepositoryEvent(Repository<K, V> repository,
        List<V> addedValues, List<V> removedValues)
    {
        super(repository);
        this.repository = repository;
        this.addedValues = addedValues == null ? null : 
            Collections.unmodifiableList(new ArrayList<V>(addedValues));
        this.removedValues = removedValues == null ? null : 
            Collections.unmodifiableList(new ArrayList<V>(removedValues));
    }
    
    /**
     * Returns the {@link Repository} from which this event originated
     * 
     * @return The {@link Repository}
     */
    public Repository<K, V> getRepository()
    {
        return repository;
    }
    
    /**
     * Returns an unmodifiable list containing the values that have been
     * added to the {@link Repository}, causing this event, or 
     * <code>null</code> if this event was not caused by adding values.
     * 
     * @return The added values
     */
    public List<V> getAddedValues()
    {
        return addedValues;
    }

    /**
     * Returns an unmodifiable list containing the values that have been
     * removed from the {@link Repository}, causing this event, or 
     * <code>null</code> if this event was not caused by removing values.
     * 
     * @return The added values
     */
    public List<V> getRemovedValues()
    {
        return removedValues;
    }
    
}
