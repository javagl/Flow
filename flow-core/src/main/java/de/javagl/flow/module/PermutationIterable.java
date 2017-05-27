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
package de.javagl.flow.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class providing an iterator over all permutations of a set of 
 * elements. For a set S with n=|S|, there are m=n! different 
 * permutations. Example: <br>
 * <pre>
 * S = { A,B,C }, n = |S| = 3
 * m = n! = 6
 * 
 * Permutations:
 * [A, B, C]
 * [A, C, B]
 * [B, A, C]
 * [B, C, A]
 * [C, A, B]
 * [C, B, A]
 * </pre>
 *  
 * @param <T> The type of the elements 
 */
class PermutationIterable<T> implements Iterable<List<T>>
{
    /**
     * The input elements
     */
    private final List<T> input;
    
    /**
     * The total number of permutations that will be provided by the iterator
     */
    private final int numPermutations;

    /**
     * Creates an iterable over all permutations of the given elements
     * 
     * @param input The input elements
     */
    public PermutationIterable(List<T> input)
    {
        this.input = input;
        numPermutations = factorial(input.size());
    }

    /**
     * Utility method for computing the factorial n! of a number n.
     * The factorial of a number n is n*(n-1)*(n-2)*...*1, or more
     * formally:<br>
     * 0! = 1 <br>
     * 1! = 1 <br>
     * n! = n*(n-1)!<br>
     *
     * @param n The number of which the factorial should be computed
     * @return The factorial, i.e. n!
     */
    private static int factorial(int n)
    {
        int f = 1;
        for (int i = 2; i <= n; i++)
        {
            f *= i;
        }
        return f;
    }
    

    @Override
    public Iterator<List<T>> iterator()
    {
        return new Iterator<List<T>>()
        {
            /**
             * The index of the current permutation
             */
            private int current = 0;

            @Override
            public boolean hasNext()
            {
                return current < numPermutations;
            }

            @Override
            public List<T> next()
            {
                // Adapted from http://en.wikipedia.org/wiki/Permutation
                List<T> result = new ArrayList<T>(input);
                int factorial = numPermutations / input.size();
                for (int i = 0; i < result.size() - 1; i++)
                {
                    int tempIndex = (current / factorial) %
                        (result.size() - i);
                    T temp = result.get(i + tempIndex);
                    for (int j = i + tempIndex; j > i; j--)
                    {
                        result.set(j, result.get(j - 1));
                    }
                    result.set(i, temp);
                    factorial /= (result.size() - (i + 1));
                }
                current++;
                return result;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException(
                    "May not remove elements from a permutation");
            }
        };
    }
}