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
package de.javagl.flow.link;

import java.lang.reflect.Type;
import java.util.List;

import de.javagl.flow.Flow;
import de.javagl.flow.MutableFlow;
import de.javagl.flow.TypeContext;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;


/**
 * Utility methods for creating and verifying {@link Link} instances 
 */
public class Links
{
    /**
     * Create a {@link Link} between the specified slots of 
     * the given source and target {@link Module}. <br> 
     * <br>
     * Note that this method does not add the {@link Link} to the respective 
     * slots. It will only be added to the respective slots when it is 
     * inserted into a {@link Flow} using {@link MutableFlow#addLink(Link)}.  
     * 
     * @param source The source {@link Module}. May not be <code>null</code>.
     * @param sourceOutputIndex The index of the {@link OutputSlot} of the
     * source {@link Module}
     * @param target The target {@link Module}. . May not be <code>null</code>.
     * @param targetInputIndex The index of the {@link InputSlot} of the
     * target {@link Module}
     * @return The new {@link Link}
     * @throws NullPointerException If the source or the target {@link Module}
     * is <code>null</code>
     * @throws IllegalArgumentException If one of the indices is negative
     * or not smaller than the number of the respective slots in the 
     * {@link Module}
     */
    public static Link create(
        Module source, int sourceOutputIndex, 
        Module target, int targetInputIndex)
    {
        List<OutputSlot> outputSlots = source.getOutputSlots();
        List<InputSlot> inputSlots = target.getInputSlots();
        validateIndex(sourceOutputIndex, 
            outputSlots.size(), "sourceOutputIndex");
        validateIndex(targetInputIndex, 
            inputSlots.size(), "targetInputIndex");
        
        OutputSlot sourceSlot = outputSlots.get(sourceOutputIndex);
        InputSlot targetSlot = inputSlots.get(targetInputIndex);
        return create(sourceSlot, targetSlot);
    }
    
    /**
     * Create a {@link Link} between the given {@link InputSlot} and 
     * {@link OutputSlot}.<br> 
     * <br>
     * Note that this method does not add the {@link Link} to the respective 
     * slots. It will only be added to the respective slots when it is 
     * inserted into a {@link Flow} using {@link MutableFlow#addLink(Link)}.  
     * 
     * @param sourceSlot The source {@link OutputSlot}
     * @param targetSlot The target {@link InputSlot}
     * @return The new {@link Link}
     */
    public static Link create(OutputSlot sourceSlot, InputSlot targetSlot)
    {
        Link link = new DefaultLink(sourceSlot, targetSlot);
        return link;
    }
    
    /**
     * Returns whether the given {@link Link} is valid. That is, whether the
     * {@link InputSlot#getExpectedType() expected type} of the 
     * {@link InputSlot} is assignable from the
     * {@link OutputSlot#getActualType() actual type} of the 
     * {@link OutputSlot}. <br>
     * 
     * @param typeContext The {@link TypeContext}
     * @param link The {@link Link} 
     * @return Whether the {@link Link} is valid.
     */
    public static boolean isValid(TypeContext typeContext, Link link)
    {
        OutputSlot sourceSlot = link.getSourceSlot();
        InputSlot targetSlot = link.getTargetSlot();
        Type sourceOutputType = sourceSlot.getActualType();
        Type targetInputType = targetSlot.getExpectedType();
        
        boolean result = typeContext.isAssignable(
            targetInputType, sourceOutputType);
        
        boolean log = false;
        //log = true;
        if (log)
        {
            System.out.println("Links#isValid");
            System.out.println("    check if           " + targetInputType);
            System.out.println("    is assignable from " + sourceOutputType);
            System.out.println("    yields             " + result);
        }
        
        return result;
    }
    
    /**
     * Validate the given index for the given list size
     * 
     * @param index The index
     * @param size The size
     * @param name The name of the index
     * @throws IllegalArgumentException If the index is not valid
     */
    private static void validateIndex(int index, int size, String name)
    {
        if (index < 0) 
        {
            throw new IllegalArgumentException(
                "The " + name + " may not be negative, but is " + index);
        }
        if (index >= size) 
        {
            throw new IllegalArgumentException(
                "The " + name + " must be smaller than " + size + ", "
                + "but is " + index);
        }
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private Links()
    {
        // Private constructor to prevent instantiation
    }
}
