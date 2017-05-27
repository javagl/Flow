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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.javagl.types.Types;

/**
 * A class for instantiating type variables in a certain context.<br> 
 * <br>
 * A TypeVariable belongs to a GenericDeclaration. Two TypeVariables
 * with equal names and equal GenericDeclarations are considered
 * as being equal by default. But when there are two modules that expect,
 * for example, a <code>List&lt;T&gt;</code> as their input, the TypeVariable
 * <code>T</code> has to be different for both modules, so that
 * different types may be assigned to these TypeVariables. <br>
 * <br>
 * This class allows {@link #register(Type) registering} multiple  
 * types that involve TypeVariables and belong to one context 
 * (i.e. one Module). It then makes sure that the involved 
 * TypeVariables are specific and unique for the context.<br>
 * <br> 
 * For example, when registering types
 * <code><pre>
 * Map&lt;A, B&gt; 
 * Map&lt;B, C&gt; 
 * Map&lt;A, C&gt;
 * </pre></code>
 * where <code>A</code>, <code>B</code> and <code>C</code> have the 
 * same GenericDeclaration, then this class may be used to 
 * {@link #instantiate(Type) instantiate} the types and their 
 * TypeVariables. For a type <code>Map&lt;A,B&gt;</code>, this class 
 * will return a type <code>Map&lt;A<sub>0</sub>,B<sub>0</sub>&gt;</code>, 
 * where the TypeVariables <code>A</code> and <code>B</code> have been 
 * replaced by new, unique instances, <code>A<sub>0</sub></code> and 
 * <code>B<sub>0</sub></code>, with a common (new) GenericDeclaration. 
 * For a type <code>Map&lt;B,C&gt;</code>, it will return a type 
 * <code>Map&lt;B<sub>0</sub>,C<sub>0</sub>&gt;</code> where 
 * <code>B<sub>0</sub></code> is the same TypeVariable as in the first 
 * result.  
 */
class TypeVariableInstantiator
{
    /**
     * The map from type variables to their unique instances
     */
    private final Map<Type, Type> typeVariableToInstantiatedTypeVariable;
    
    /**
     * The map from generic declarations to the instances that 
     * have been created for the new type variable instances.
     */
    private final Map<GenericDeclaration, 
        ModifiableGenericDeclaration> genericDeclarationMap;
    
    /**
     * Default constructor
     */
    TypeVariableInstantiator()
    {
        typeVariableToInstantiatedTypeVariable = 
            new LinkedHashMap<Type, Type>();
        genericDeclarationMap = 
            new LinkedHashMap<GenericDeclaration, 
                ModifiableGenericDeclaration>();
    }
    
    /**
     * Register the given type in this instantiator. The type variables
     * that are contained in the given type will be mapped to new instances
     * of type variables with new generic declarations. A call to 
     * {@link #instantiate(Type)} with the same type will then return
     * a new type where the type variables have been replaced with these
     * instances. 
     * 
     * @param type The type to register.
     */
    void register(Type type)
    {
        if (type instanceof Class)
        {
            // Nothing to do here
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            for (Type typeArgument : typeArguments)
            {
                register(typeArgument);
            }
        }
        else if (type instanceof WildcardType)
        {
            WildcardType wildcardType = (WildcardType)type;
            Type lowerBounds[] = wildcardType.getLowerBounds();
            for (Type lowerBound : lowerBounds)
            {
                register(lowerBound);
            }
            Type upperBounds[] = wildcardType.getUpperBounds();
            for (Type upperBound : upperBounds)
            {
                register(upperBound);
            }
        }
        else if (type instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable = (TypeVariable<?>)type;
            registerTypeVariable(typeVariable);
        }
        else if (type instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            Type componentType = genericArrayType.getGenericComponentType();
            register(componentType);
        }
        else
        {
            throw new IllegalArgumentException(
                "Unknown type: " + type.getClass());
        }
    }
    
    /**
     * Register the given type variable by creating its GenericDeclaration
     * and its corresponding instantiation into the 
     * {@link #genericDeclarationMap} and the
     * {@link #typeVariableToInstantiatedTypeVariable} map. This method is 
     * called for each type variable that is encountered during the 
     * recursion through {@link #register(Type)}.
     * 
     * @param typeVariable The type variable
     */
    private void registerTypeVariable(TypeVariable<?> typeVariable)
    {
        String name = typeVariable.getName();
        Type bounds[] = typeVariable.getBounds();
        GenericDeclaration genericDeclaration = 
            typeVariable.getGenericDeclaration();
        TypeVariable<?>[] typeParameters = 
            genericDeclaration.getTypeParameters();

        // Check whether an instantiation for the GenericDeclaration
        // of the TypeVariable already exists, and create one if not
        ModifiableGenericDeclaration genericDeclarationInstance =
            genericDeclarationMap.get(genericDeclaration);
        if (genericDeclarationInstance == null)
        {
            genericDeclarationInstance = 
                new ModifiableGenericDeclaration(
                    typeParameters.length);
            genericDeclarationMap.put(
                genericDeclaration, genericDeclarationInstance);
        }
        int id = genericDeclarationInstance.getId();
        
        // Create the instantiation of the TypeVariable
        ModifiableTypeVariable<?> typeVariableInstance = 
            new ModifiableTypeVariable<GenericDeclaration>(
                genericDeclarationInstance, name+id, bounds);
        
        // Put the instantiated TypeVariable into the genericDeclaration
        List<TypeVariable<?>> typeParameterList = 
            Arrays.asList(genericDeclaration.getTypeParameters());
        int index = typeParameterList.indexOf(typeVariable);
        genericDeclarationInstance.setTypeParameter(
            index, typeVariableInstance);
        
        // Finish the registration of the TypeVariable instantiation
        typeVariableToInstantiatedTypeVariable.put(
            typeVariable, typeVariableInstance);
        
        // The TypeVariable instantiation has been registered.
        // Now instantiate all bounds of the type variable, 
        // so that any occurrence of the original TypeVariable
        // is replaced with the proper instantiation.
        for (int i = 0; i < bounds.length; i++)
        {
            typeVariableInstance.setBound(i, instantiate(bounds[i]));
        }
    }

    /**
     * Returns a new type that is structurally equal to the given type,
     * but where the type variables have been replaced with the unique 
     * instances of type variables that have been created when 
     * {@link #register(Type) registering} a type that contained the
     * same type variables.
     *  
     * @param type The type to instantiate
     * @return The type where all type variables are instantiated
     * @throws IllegalArgumentException If the given type contains 
     * a type variable that was not treated during a preceding call
     * to {@link #register(Type)}
     */
    Type instantiate(Type type)
    {
        if (type instanceof Class)
        {
            return type;
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            return Types.createParameterizedType(
                parameterizedType.getRawType(),
                parameterizedType.getOwnerType(),
                instantiateAll(typeArguments));
        }
        else if (type instanceof WildcardType)
        {
            WildcardType wildcardType = (WildcardType)type;
            Type lowerBounds[] = wildcardType.getLowerBounds();
            Type upperBounds[] = wildcardType.getUpperBounds();
            return Types.createWildcardType(
                instantiateAll(lowerBounds),
                instantiateAll(upperBounds));
        }
        else if (type instanceof TypeVariable<?>)
        {
            Type instantiatedType = 
                typeVariableToInstantiatedTypeVariable.get(type);
            if (instantiatedType != null)
            {
                return instantiatedType;
            }
            throw new IllegalArgumentException(
                "Type variable " + type + " was not registered");
        }
        else if (type instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            Type componentType = genericArrayType.getGenericComponentType();
            return Types.createGenericArrayType(
                instantiate(componentType));
        }
        else
        {
            throw new IllegalArgumentException(
                "Unknown type: " + type.getClass());
        }
    }

    /**
     * Calls {@link #instantiate(Type)} on all types and returns the
     * results as an array
     * 
     * @param types The input types
     * @return The instantiated types
     */
    private Type[] instantiateAll(Type types[])
    {
        Type instantiatedTypes[] = new Type[types.length];
        for (int i = 0; i < types.length; i++)
        {
            instantiatedTypes[i] = instantiate(types[i]);
        }
        return instantiatedTypes;
    }    
}
