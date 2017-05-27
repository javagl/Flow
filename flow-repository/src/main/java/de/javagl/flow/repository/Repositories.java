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

import java.util.function.Function;

/**
 * Methods to create {@link Repository} instances.
 */
public class Repositories
{
    /**
     * Creates a new {@link Repository} that uses the given 
     * {@link Function} to obtain the keys for values that 
     * are added. This function must always return the same
     * key for the same value.
     * 
     * @param <K> The key type
     * @param <V> The value type
     * 
     * @param keyFunction The key function. May not be <code>null</code>.
     * @return The new {@link Repository}
     */
    public static <K, V> Repository<K, V> create(Function<V, K> keyFunction)
    {
        return new DefaultRepository<K, V>(keyFunction);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Repositories()
    {
        // Private constructor to prevent instantiation
    }
}
