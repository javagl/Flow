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
package de.javagl.flow.module.slot;

import java.lang.reflect.Type;

import de.javagl.flow.module.Module;


/**
 * Utility methods to create {@link Slot} instances.
 */
public class Slots
{
    /**
     * Create an {@link OutputSlot} for the given {@link Module} at 
     * the given index, which is providing objects of the
     * given type.
     * 
     * @param module The {@link Module}
     * @param index The index of the slot to create
     * @param formalType The {@link Slot#getFormalType() formal type} of the 
     * slot
     * @return The slot
     */
    public static OutputSlot createOutputSlot(
        Module module, int index, Type formalType)
    {
        return new DefaultOutputSlot(module, index, formalType);
    }
    
    /**
     * Create an {@link InputSlot} for the given {@link Module} at 
     * the given index, which is expecting objects of the
     * given type.
     * 
     * @param module The {@link Module}
     * @param index The index of the slot to create
     * @param formalType The {@link Slot#getFormalType() formal type} of the 
     * slot
     * @return The slot
     */
    public static InputSlot createInputSlot(
        Module module, int index, Type formalType)
    {
        return new DefaultInputSlot(module, index, formalType);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Slots()
    {
        // Private constructor to prevent instantiation
    }
}
