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
package de.javagl.flow.gui.compatibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods and classes related to combinatorics.
 */
class Combinatorics
{
    /**
     * Computes the power set of the given list. The result will be 
     * an unmodifiable list containing unmodifiable lists, each 
     * representing one subset of the given list. The lists will be
     * sorted by their size, is ascending order.
     * 
     * @param <T> The type of the elements
     * @param input The input
     * @return The power set
     */
    static <T> List<List<T>> computeSortedPowerSet(List<T> input)
    {
        List<List<T>> powerSet = new ArrayList<List<T>>();
        Iterable<List<T>> iterable = new PowerSetIterable<T>(input);
        for (List<T> element : iterable)
        {
            powerSet.add(Collections.unmodifiableList(element));
        }
        Collections.sort(powerSet, new Comparator<List<T>>()
        {
            @Override
            public int compare(List<T> e0, List<T> e1)
            {
                return e0.size() - e1.size();
            }
        });
        return Collections.unmodifiableList(powerSet);
    }
    
    /**
     * A class providing an iterator over all elements of the power set of a 
     * given set of elements. <br>
     * <br>
     * The power set of a set S is the set of all subsets of S. For a set 
     * with n=|S| elements, the power set contains m=2^n elements, each being
     * one possible subset of S. These elements are computed from the bit 
     * patterns of the binary representations of the numbers i=0 ... m-1:
     * When the bit number b in the binary representation of i is set, then
     * the b'th element of the input array is put into the respective subset.
     * Example: <br>
     * <pre>
     * S = { A,B,C }, n = |S| = 3
     * m = 2^n = 8
     * 
     * Elements of the power set:
     * i = 0, binary: 000, element: {     }
     * i = 1, binary: 001, element: {A    }
     * i = 2, binary: 010, element: {  B  }
     * i = 3, binary: 011, element: {A,B  }
     * i = 4, binary: 100, element: {    C}
     * i = 5, binary: 101, element: {A,  C}
     * i = 6, binary: 110, element: {  B,C}
     * i = 7, binary: 111, element: {A,B,C}
     * </pre>
     * 
     * @param <T> The type of the elements
     */
    private static class PowerSetIterable<T> implements Iterable<List<T>>
    {
        /**
         * The input elements
         */
        private final List<T> input;
        
        /**
         * The total number of elements that the iterator will provide
         */
        private final int numElements;
     
        /**
         * Creates a new iterable over all elements of the power set
         * of the given elements
         * 
         * @param input The input elements
         */
        private PowerSetIterable(List<T> input)
        {
            this.input = input;
            numElements = 1 << input.size();
        }
     
        @Override
        public Iterator<List<T>> iterator()
        {
            return new Iterator<List<T>>()
            {
                /**
                 * The current index in the power set
                 */
                private int current = 0;
     
                @Override
                public boolean hasNext()
                {
                    return current < numElements;
                }
     
                @Override
                public List<T> next()
                {
                    List<T> element = new ArrayList<T>();
     
                    // Insert into the current power set element
                    // all elements of the input set that are at
                    // indices where the current counter value
                    // has a '1' in its binary representation
                    for (int i = 0; i < input.size(); i++)
                    {
                        long b = 1 << i;
                        if ((current & b) != 0)
                        {
                            element.add(input.get(i));
                        }
                    }
                    current++;
                    return element;
                }
     
                @Override
                public void remove()
                {
                    throw new UnsupportedOperationException(
                        "May not remove elements from a power set");
                }
            };
        }
    }    
    
    /**
     * Private constructor to prevent instantiation
     */
    private Combinatorics()
    {
        // Private constructor to prevent instantiation
    }
}
