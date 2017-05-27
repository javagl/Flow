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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.javagl.flow.TypeContext;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.SlotInfo;

/**
 * Utility methods related to {@link ModuleInfo} objects.<br>
 * <br>
 * Note: The methods in this class might be moved to the {@link ModuleInfos}
 * class in the future, and this class may be omitted.
 */
public class ModuleInfoUtils
{
    /**
     * Returns whether the given {@link ModuleInfo} has no input slots
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @return Whether the {@link ModuleInfo} has no input slots
     */
    public static boolean hasNoInputs(ModuleInfo moduleInfo)
    {
        return moduleInfo.getInputSlotInfos().isEmpty();
    }


    /**
     * Returns whether the given {@link ModuleInfo} has {@link InputSlot} 
     * objects whose type (according to the {@link SlotInfo}) are assignable 
     * from the given types, in any order
     * 
     * @param typeContext The {@link TypeContext}
     * @param moduleInfo The {@link ModuleInfo}
     * @param typesToAssignFrom The types to assign the inputs from
     * @return Whether the {@link ModuleInfo} has a matching input types
     */
    public static boolean hasInputTypesAssignableFromAll(
        TypeContext typeContext, ModuleInfo moduleInfo, 
        List<? extends Type> typesToAssignFrom)
    {
        List<Type> typesToAssignTo = 
            getTypes(moduleInfo.getInputSlotInfos());
        PermutationIterable<Type> permutationIterable = 
            new PermutationIterable<Type>(
                Collections.unmodifiableList(typesToAssignFrom));
        for (List<Type> permutation : permutationIterable)
        {
            if (ModuleInfoUtils.allAssignable(
                typeContext, typesToAssignTo, permutation))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether the given {@link ModuleInfo} has an {@link OutputSlot}
     * whose type (according to the {@link SlotInfo}) is assignable to 
     * any of the given types.
     * 
     * @param typeContext The {@link TypeContext}
     * @param moduleInfo The {@link ModuleInfo}
     * @param typesToAssignTo The types to assign any output to
     * @return Whether the {@link ModuleInfo} has any matching output type
     */
    public static boolean hasOutputTypeAssignableToAny(
        TypeContext typeContext, ModuleInfo moduleInfo, 
        Iterable<? extends Type> typesToAssignTo)
    {
        for (Type type : typesToAssignTo)
        {
            if (hasOutputTypeAssignableTo(
                typeContext, moduleInfo, type))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether the given {@link ModuleInfo} has an {@link OutputSlot}
     * whose type (according to the {@link SlotInfo}) is assignable to the
     * given type.
     * 
     * @param typeContext The {@link TypeContext}
     * @param moduleInfo The {@link ModuleInfo}
     * @param typeToAssignTo The type to assign any output to
     * @return Whether the {@link ModuleInfo} has a matching output type
     */
    public static boolean hasOutputTypeAssignableTo(
        TypeContext typeContext, ModuleInfo moduleInfo, Type typeToAssignTo)
    {
        List<SlotInfo> outputSlotInfos = 
            moduleInfo.getOutputSlotInfos();
        for (SlotInfo outputSlotInfo : outputSlotInfos)
        {
            if (typeContext.isAssignable(
                typeToAssignTo, outputSlotInfo.getType()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given {@link ModuleInfo} has an {@link InputSlot}
     * whose type (according to the {@link SlotInfo}) is assignable from 
     * any of the given types.
     * 
     * @param typeContext The {@link TypeContext}
     * @param moduleInfo The {@link ModuleInfo}
     * @param typesToAssignFrom The types to assign any input from
     * @return Whether the {@link ModuleInfo} has any matching input type
     */
    public static boolean hasInputTypeAssignableFromAny(
        TypeContext typeContext, ModuleInfo moduleInfo, 
        Iterable<? extends Type> typesToAssignFrom)
    {
        for (Type type : typesToAssignFrom)
        {
            if (hasInputTypeAssignableFrom(typeContext, moduleInfo, type))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether the given {@link ModuleInfo} has an {@link InputSlot}
     * whose type (according to the {@link SlotInfo}) is assignable from the
     * given type.
     * 
     * @param typeContext The {@link TypeContext}
     * @param moduleInfo The {@link ModuleInfo}
     * @param typeToAssignFrom The type to assign any input from
     * @return Whether the {@link ModuleInfo} has a matching input type
     */
    public static boolean hasInputTypeAssignableFrom(
        TypeContext typeContext, ModuleInfo moduleInfo, Type typeToAssignFrom)
    {
        List<SlotInfo> inputSlotInfos = 
            moduleInfo.getInputSlotInfos();
        for (SlotInfo inputSlotInfo : inputSlotInfos)
        {
            if (typeContext.isAssignable(
                inputSlotInfo.getType(), typeToAssignFrom))
            {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Returns a list containing the {@link SlotInfo#getType() type}
     * of each of the given {@link SlotInfo} objects.
     * 
     * @param slotInfos The {@link SlotInfo} objects
     * @return The list of types
     */
    private static List<Type> getTypes(Iterable<? extends SlotInfo> slotInfos)
    {
        List<Type> result = new ArrayList<Type>();
        for (SlotInfo slotInfo : slotInfos)
        {
            result.add(slotInfo.getType());
        }
        return result;
    }

    
    /**
     * Returns whether each of the <code>typesToAssignTo</code> is assignable
     * from the corresponding <code>typesToAssignFrom</code>.
     *  
     * @param typeContext The {@link TypeContext}
     * @param typesToAssignTo The types to assign to
     * @param typesToAssignFrom The types to assign from
     * @return Whether all types are assignable
     */
    private static boolean allAssignable(
        TypeContext typeContext,
        List<? extends Type> typesToAssignTo, 
        List<? extends Type> typesToAssignFrom)
    {
        if (typesToAssignTo.size() != typesToAssignFrom.size())
        {
            return false;
        }
        for (int i = 0; i < typesToAssignTo.size(); i++)
        {
            Type typeToAssignTo = typesToAssignTo.get(i);
            Type typeToAssignFrom = typesToAssignFrom.get(i);
            if (!typeContext.isAssignable(typeToAssignTo, typeToAssignFrom))
            {
                return false;
            }
        }
        return true;
    }


    /**
     * Private constructor to prevent instantiation
     */
    private ModuleInfoUtils()
    {
        // Private constructor to prevent instantiation
    }
    
}
