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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Implementation of a TypeVariable where the bounds can be modified.
 * Used only by the {@link TypeVariableInstantiator}.
 *
 * @param <D> The GenericDeclaration type
 */
final class ModifiableTypeVariable<D extends GenericDeclaration> 
    implements TypeVariable<D>
{
    /**
     * The generic declaration
     */
    private final D genericDeclaration;

    /**
     * The name
     */
    private final String name;
    
    /**
     * The bounds
     */
    private final Type bounds[];
    
    /**
     * Creates a new type variable
     * 
     * @param genericDeclaration The generic declaration
     * @param name The name
     * @param bounds The bounds. If this is an empty array, then
     * <code>Object.class</code> will be used as the (implicit)
     * bounds.
     */
    ModifiableTypeVariable(D genericDeclaration, String name, Type ... bounds)
    {
        this.genericDeclaration = genericDeclaration;
        this.name = name;
        if (bounds.length == 0)
        {
            this.bounds = new Type[]{ Object.class };
        }
        else
        {
            this.bounds = bounds.clone();
        }
    }
    
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Type[] getBounds()
    {
        return bounds.clone();
    }
    
    /**
     * Set the bound with the given index
     * 
     * @param index The index
     * @param bound The bound
     */
    void setBound(int index, Type bound)
    {
        bounds[index] = bound;
    }

    @Override
    public D getGenericDeclaration()
    {
        return genericDeclaration;
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
    public AnnotatedType[] getAnnotatedBounds()
    {
        return new AnnotatedType[0];
    }
    
    @Override
    public String toString()
    {
        return name; //+" [GD:"+System.identityHashCode(genericDeclaration)+"]";
    }

    @Override
    public int hashCode()
    {
        return genericDeclaration.hashCode() ^ name.hashCode();        
    }


    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof TypeVariable<?>))
            return false;
        TypeVariable<?> other = (TypeVariable<?>) object;
        if (genericDeclaration == null)
        {
            if (other.getGenericDeclaration() != null)
                return false;
        } else if (!genericDeclaration.equals(other.getGenericDeclaration()))
            return false;
        if (name == null)
        {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        return true;
    }


}
