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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import de.javagl.flow.module.slot.SlotInfo;
import de.javagl.reflection.Methods;
import de.javagl.reflection.ReflectionException;
import de.javagl.types.PrimitiveTypes;
import de.javagl.types.Types;

/**
 * A class for creating {@link ModuleInfo} instances.
 */
public class ModuleInfos
{
    /**
     * Returns a {@link ModuleInfoBuilder} for a {@link ModuleInfo} instance
     * that describes a {@link Module} with the given name and
     * description.
     * 
     * @param name The name of the {@link Module}
     * @param description The description of the {@link Module}
     * @return The {@link ModuleInfoBuilder}
     * @throws NullPointerException If any argument is <code>null</code>
     */
    public static ModuleInfoBuilder create(String name, String description)
    {
        return new ModuleInfoBuilder(name, description);
    }
    
    /**
     * Create a {@link ModuleInfo} with the given name and description,
     * for supplier that produces a single output. The output will receive 
     * an unspecified, generic name and description.
     * 
     * @param <T> The supplied type
     * 
     * @param name The name 
     * @param description The description
     * @param type The type of the supplied objects
     * @return The {@link ModuleInfo}
     */
    public static <T> ModuleInfo createForSupplier(
        String name, String description, Class<T> type)
    {
        ModuleInfo moduleInfo = ModuleInfos.create(name, description)
            .addOutput(type, "Output", "The supplied object")
            .build();
        return moduleInfo;
    }

    /**
     * Create a {@link ModuleInfo} with the given name and description,
     * for consumer that accepts a single input. The input will receive 
     * an unspecified, generic name and description.
     * 
     * @param <T> The consumed type
     * 
     * @param name The name 
     * @param description The description
     * @param type The consumed type 
     * @return The {@link ModuleInfo}
     */
    public static <T> ModuleInfo createForConsumer(
        String name, String description, Class<T> type)
    {
        ModuleInfo moduleInfo = ModuleInfos.create(name, description)
            .addInput(type, "Input", "The consumed object")
            .build();
        return moduleInfo;
    }
    
    /**
     * Create a {@link ModuleInfo} with the given name and description,
     * for a function that receives a single input and produces
     * a single output. The input and output will receive unspecified, 
     * generic names and descriptions.
     * 
     * @param <S> The argument type
     * @param <T> The result type
     * 
     * @param name The name 
     * @param description The description
     * @param argumentType The argument type
     * @param resultType The result type
     * @return The {@link ModuleInfo}
     */
    public static <S, T> ModuleInfo createForFunction(
        String name, String description, 
        Class<S> argumentType, Class<T> resultType)
    {
        ModuleInfo moduleInfo = ModuleInfos.create(name, description)
            .addInput(argumentType, "Input", "The function argument")
            .addOutput(resultType, "Output", "The function result")
            .build();
        return moduleInfo;
    }
    
    /**
     * Create a {@link ModuleInfo} that describes a call to the specified 
     * method.
     * 
     * @param shortName A short name that should be used as the name 
     * instead of the full method string. May be <code>null</code>.
     * @param declaringClass The class that declares the method
     * @param methodName The name of the method
     * @param parameterTypes The parameter types of the method
     * @return The {@link ModuleInfo}
     * @throws IllegalArgumentException If the method cannot be obtained
     */
    public static ModuleInfo createForMethod(
        String shortName, Class<?> declaringClass, 
        String methodName, Class<?> ... parameterTypes)
    {
        Method method = null;
        try
        {
            method = Methods.getDeclaredMethodUnchecked(
                declaringClass, methodName, parameterTypes);
        }
        catch (ReflectionException e)
        {
            throw new IllegalArgumentException(e);
        }
        return createForMethod(method, shortName);
    }
    
    /**
     * Create a {@link ModuleInfo} that describes a call to the
     * given method.
     * 
     * @param method The method
     * @param shortName A short name that should be used as the name 
     * instead of the full method string. May be <code>null</code>.
     * @return The {@link ModuleInfo}
     */
    public static ModuleInfo createForMethod(Method method, String shortName)
    {
        // Initialize the ModuleInfoBuilder
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        String description = "Calls the ";
        if (isStatic) 
        {
            description += "static ";
        }
        else
        {
            description += "instance ";
        }
        description += "method: " + method.toGenericString();
        String name = "Call " + createShortMethodName(method);
        if (shortName != null)
        {
            name = shortName;
        }
        ModuleInfoBuilder b = create(name, description);
        
        // The instance that the method is called on is input as well as output
        Class<?> c = method.getDeclaringClass();
        if (!isStatic)
        {
            Type instanceType = Types.asParameterizedType(c);
            b.addInput(instanceType, "Instance", 
                "The instance to call the method on");
            b.addOutput(instanceType, "Instance", 
                "The instance that the method was called on");
        }

        // Add the method parameters as input
        Type[] parameterTypes = method.getGenericParameterTypes();
        int n = 1;
        for (Type parameterType : parameterTypes)
        {
            Type t = parameterType;
            if (Types.isPrimitive(parameterType))
            {
                // TODO Always boxing here - is this OK? What about ambiguities?
                t = PrimitiveTypes.getBoxedType(parameterType);
            }
            String nth = createShortOrdinalNumberString(n);
            b.addInput(t, nth + " argument", 
                "The " + nth + " method argument");
            n++;
        }
        
        // Add the return type as output
        Type returnType = method.getGenericReturnType();
        if (!Types.isVoid(returnType))
        {
            Type t = returnType;
            if (Types.isPrimitive(returnType))
            {
                // TODO Always boxing here - is this OK? 
                t = PrimitiveTypes.getBoxedType(returnType);
            }
            b.addOutput(t, "Result", "The result of the method call");
        }
        
        // Add the (non-primitive) parameters as outputs, to be available 
        // after the method call, for the case that the method changed
        // the parameters
        n = 1;
        for (Type parameterType : parameterTypes)
        {
            if (!Types.isPrimitive(parameterType))
            {
                String nth = createShortOrdinalNumberString(n);
                b.addOutput(parameterType, nth + " argument",
                    "The " + nth + " method argument after the call");
            }
            n++;
        }
        
        return b.build();
    }

    /**
     * Creates a short string representation for the given method,
     * mainly consisting of the (unqualified) name of the declaring
     * class and the name of the method
     * 
     * @param method The method
     * @return The short method name
     */
    private static String createShortMethodName(Method method)
    {
        String className = method.getDeclaringClass().getSimpleName();
        int dotIndex = className.lastIndexOf(".");
        String shortClassName =
            className.substring(dotIndex + 1, className.length());
        
        String parametersString = 
            createParameterTypesString(method.getParameterTypes());
        return shortClassName + "#" + method.getName() 
            + "(" + parametersString + ")";
    }
    
    /**
     * Create a {@link ModuleInfo} that describes a call to the
     * given constructor.
     * 
     * @param constructor The constructor
     * @return The {@link ModuleInfo}
     */
    public static ModuleInfo createForConstructor(Constructor<?> constructor)
    {
        // Initialize the ModuleInfoBuilder
        Class<?> c = constructor.getDeclaringClass();
        String constructorString = String.valueOf(constructor);
        constructorString = constructorString.replaceAll("public ", "");
        String description = "Creates a " + c.getSimpleName() 
            + " by calling " + constructorString;
        String parametersString = 
            createParameterTypesString(constructor.getParameterTypes());
        ModuleInfoBuilder b = create(
            "Create new " + c.getSimpleName() + "(" + parametersString + ")", 
            description);
        
        // Add the constructor parameters as input
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        int n = 1;
        for (Type parameterType : parameterTypes)
        {
            Type t = parameterType;
            if (Types.isPrimitive(parameterType))
            {
                // TODO Always boxing here - is this OK? What about ambiguities?
                t = PrimitiveTypes.getBoxedType(parameterType);
            }
            String nth = createShortOrdinalNumberString(n);
            b.addInput(t, nth + " argument",
                "The " + nth + " constructor argument");
            n++;
        }
        
        // Add the constructed type as an output
        Type constructedType = 
            Types.asParameterizedType(constructor.getDeclaringClass());
        b.addOutput(constructedType, c.getSimpleName(), 
            "The result of the constructor call");
        
        // Add the (non-primitive) parameters as outputs, to be available 
        // after the method call, for the unusual case that the constructor 
        // changed the parameters.
        n = 1;
        for (Type parameterType : parameterTypes)
        {
            if (!Types.isPrimitive(parameterType))
            {
                String nth = createShortOrdinalNumberString(n);
                b.addOutput(parameterType, nth + " argument",
                    "The " + nth + " constructor argument after the call");
            }
            n++;
        }
        
        return b.build();
    }
    
    /**
     * Creates a string containing the simple names of the given parameter
     * types, separated by a comma and a space
     * 
     * @param parameterTypes The parameter types
     * @return The string
     */
    private static String createParameterTypesString(Class<?>[] parameterTypes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<parameterTypes.length; i++)
        {
            if (i > 0)
            {
                sb.append(", ");
            }
            Class<?> parameterType = parameterTypes[i];
            sb.append(parameterType.getSimpleName());
        }
        return sb.toString();
    }
    
    
    /**
     * Return a string that is the short form of the ordinal number 
     * string for the given number. For example, <i>1st</i>, 
     * <i>42nd</i> or <i>126th</i>.   
     *  
     * @param n The number
     * @return The short ordinal number string
     */
    private static String createShortOrdinalNumberString(int n)
    {
        if ((n % 10 == 1) && (n != 11))
        {
            return n + "st";
        }
        if ((n % 10 == 2) && (n != 12))
        {
            return n + "nd";
        }
        if ((n % 10 == 3) && (n != 13))
        {
            return n + "rd";
        }
        return n + "th";
    }
    
    
    
    
    /**
     * Utility method to print a textual summary of the given 
     * {@link ModuleInfo}.<br>
     * <br>
     * <b>This method is intended for debugging and should not
     * be considered as part of the public API!</b>
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    public static void printModuleInfo(ModuleInfo moduleInfo)
    {
        System.out.println(createModuleInfoString(moduleInfo));
    }
    
    /**
     * Utility method to create a textual summary of the given 
     * {@link ModuleInfo}.<br> 
     * <br>
     * <b>This method is intended for debugging and should not
     * be considered as part of the public API!</b>
     * 
     * @param moduleInfo The {@link ModuleInfo}
     * @return The string
     */
    public static String createModuleInfoString(ModuleInfo moduleInfo)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Name                : " + moduleInfo.getName() + "\n");
        sb.append(
            "Description         : " + moduleInfo.getDescription() + "\n");
        
        if (!moduleInfo.getInputSlotInfos().isEmpty())
        {
            sb.append("Input slots:" + "\n");
            for (SlotInfo slotInfo : moduleInfo.getInputSlotInfos())
            {
                sb.append("    Type       : " + slotInfo.getType() + "\n");
                sb.append("    Name       : " + slotInfo.getName() + "\n");
                sb.append(
                    "    Description: " + slotInfo.getDescription() + "\n");
            }
        }
        if (!moduleInfo.getOutputSlotInfos().isEmpty())
        {
            sb.append("Output slots:" + "\n");
            for (SlotInfo slotInfo : moduleInfo.getOutputSlotInfos())
            {
                sb.append("    Type       : " + slotInfo.getType() + "\n");
                sb.append("    Name       : " + slotInfo.getName() + "\n");
                sb.append(
                    "    Description: " + slotInfo.getDescription() + "\n");
            }
        }
        return sb.toString();
    }
    
    
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private ModuleInfos()
    {
        // Private constructor to prevent instantiation
    }
    
    
    
    
    
    
    
    
}
