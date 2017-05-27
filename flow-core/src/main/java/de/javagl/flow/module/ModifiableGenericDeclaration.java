package de.javagl.flow.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of a GenericDeclaration where the type parameters may be
 * modified. Instances of this class will be used for instantiations
 * of TypeVariables in {@link TypeVariableInstantiator}
 */
final class ModifiableGenericDeclaration 
    implements GenericDeclaration
{
    /**
     * The instance ID counter
     */
    private static AtomicInteger idCounter = new AtomicInteger();

    /**
     * The instance ID 
     */
    private final int id;
    
    /**
     * The type parameters of this generic declaration
     */
    private final List<TypeVariable<?>> typeParameters;
    
    /**
     * Creates a new instance 
     * 
     * @param numTypeParameters The number of type parameters,
     * initially all <code>null</code> 
     */
    ModifiableGenericDeclaration(int numTypeParameters)
    {
        id = idCounter.getAndIncrement();
        typeParameters  = new ArrayList<TypeVariable<?>>(
            Collections.<TypeVariable<?>>nCopies(numTypeParameters, null)); 
    }
    
    /**
     * Set the given type parameter in this generic declaration
     *
     * @param index The index
     * @param typeParameter The type parameter to set
     */
    void setTypeParameter(int index, TypeVariable<?> typeParameter)
    {
        typeParameters.set(index, typeParameter);
    }

    @Override
    public TypeVariable<?>[] getTypeParameters()
    {
        return typeParameters.toArray(new TypeVariable<?>[0]);
    }
    
    /**
     * Returns the ID for this instance
     * 
     * @return The ID for this instance
     */
    int getId()
    {
        return id;
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
    {
        return null;
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return new Annotation[0];
    }

    @Override
    public Annotation[] getDeclaredAnnotations()
    {
        return new Annotation[0];
    }
    
    @Override
    public String toString()
    {
        return "ID"+id;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        for (TypeVariable<?> typeParameter : typeParameters)
        {
            if (typeParameter != null)
            {
                String name = typeParameter.getName();
                if (name != null)
                {
                    result = prime * result + name.hashCode();
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (object == null)
            return false;
        return false;
    }
}