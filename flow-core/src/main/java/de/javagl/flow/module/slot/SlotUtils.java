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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.javagl.flow.Flow;
import de.javagl.flow.TypeContext;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * Utility methods related to {@link Slot} instances.<br>
 * <br>
 * <b>Note</b>: These methods might become part of a more generic 
 * "compatibility model" for slots.<br> 
 * <br>
 * Note: The methods in this class might be moved to the {@link Slots}
 * class in the future, and this class may be omitted.
 */
public class SlotUtils
{
    /**
     * Compute an unmodifiable set of {@link OutputSlot} instances that are 
     * compatible to the given {@link InputSlot}. <br>
     * <br>
     * Informally, these are the possible start points for the creation 
     * of a {@link Link} ending at the given endSlot.<br>
     * <br>
     * Formally, this is the set of output slots from the given set  
     * of modules that provide an 
     * {@link OutputSlot#getActualType() actual type} that the
     * {@link InputSlot#getActualType() actual type} of the 
     * {@link InputSlot} is assignable from.<br>
     * 
     * @param endSlot The {@link InputSlot} 
     * @param allModules The set of available {@link Module} instances
     * @return The (possibly empty) set of valid {@link OutputSlot} instances
     */
    public static Set<OutputSlot> computeCompatibleStartSlots(
        InputSlot endSlot, Iterable<? extends Module> allModules)
    {
        Set<OutputSlot> result = new LinkedHashSet<OutputSlot>();
        Module endModule = endSlot.getModule();
        Flow flow = endModule.getFlow();
        TypeContext typeContext = flow.getTypeContext();
        for (Module module : allModules)
        {
            if (module == endModule)
            {
                continue;
            }
            List<OutputSlot> outputSlots = module.getOutputSlots();
            for (int i = 0; i < outputSlots.size(); i++)
            {
                Type outputType = outputSlots.get(i).getActualType();
                if (typeContext.isAssignable(
                    endSlot.getActualType(), outputType))
                {
                    result.add(outputSlots.get(i));
                }
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Compute an unmodifiable set of {@link InputSlot} instances that are 
     * compatible to the given {@link OutputSlot}.<br>
     * <br>
     * Informally, these are the possible end points for the creation of a 
     * {@link Link} starting at the given startSlot.<br>
     * <br>
     * Formally, this is the set of input slots from the given set  
     * of modules that do not yet have an 
     * {@link InputSlot#getInputLink() input link}, and have an 
     * {@link InputSlot#getExpectedType() expected type} that is assignable
     * from the {@link OutputSlot#getActualType() actual type} of the
     * given {@link OutputSlot}.
     * 
     * @param startSlot The {@link OutputSlot} 
     * @param allModules The set of available {@link Module} instances
     * @return The (possibly empty) set of valid {@link InputSlot} instances
     */
    public static Set<InputSlot> computeCompatibleEndSlots(
        OutputSlot startSlot, Iterable<? extends Module> allModules)
    {
        Set<InputSlot> result = new LinkedHashSet<InputSlot>();
        Module startModule = startSlot.getModule();
        Flow flow = startModule.getFlow();
        TypeContext typeContext = flow.getTypeContext();
        for (Module module : allModules)
        {
            if (module == startModule)
            {
                continue;
            }
            List<InputSlot> inputSlots = module.getInputSlots();
            for (int i = 0; i < inputSlots.size(); i++)
            {
                if (inputSlots.get(i).getInputLink() != null)
                {
                    continue;
                }
                Type inputType = inputSlots.get(i).getExpectedType();
                if (typeContext.isAssignable(
                    inputType, startSlot.getActualType()))
                {
                    result.add(inputSlots.get(i));
                }
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SlotUtils()
    {
        // Private constructor to prevent instantiation
    }

}
