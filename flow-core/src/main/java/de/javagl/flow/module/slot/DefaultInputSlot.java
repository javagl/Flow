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
import java.util.Objects;

import de.javagl.flow.Flow;
import de.javagl.flow.TypeContext;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * Default implementation of an {@link InputSlot}
 */
final class DefaultInputSlot extends AbstractSlot implements InputSlot
{
    /**
     * The default type that is expected by this {@link InputSlot}
     */
    private final Type defaultExpectedType;
    
    /**
     * The {@link Link} that is currently set as the inputLink
     */
    private Link inputLink;

    /**
     * Create a new input slot for the given {@link Module} with the
     * given index, and the given type as the default value to be 
     * returned as the {@link #getExpectedType()}. This type should
     * be the same as the one defined by {@link SlotInfo#getType()}
     * for this {@link Slot}.
     * 
     * @param module The {@link Module} this slot belongs to
     * @param index The index of this slot in the {@link Module}
     * @param formalType The {@link Slot#getFormalType() formal type}
     */
    DefaultInputSlot(Module module, int index, Type formalType)
    {
        super(module, index, 
            module.getModuleInfo().getInputSlotInfos().get(index), formalType);
        this.defaultExpectedType = formalType;
    }
    
    @Override
    public Type getExpectedType()
    {
        Type expectedType = defaultExpectedType;
        Flow flow = getModule().getFlow();
        if (flow != null)
        {
            TypeContext typeContext = flow.getTypeContext();
            expectedType = typeContext.resolveType(defaultExpectedType);
        }
        return expectedType;
    }
    
    @Override
    public Type getActualType()
    {
        if (inputLink != null)
        {
            OutputSlot sourceSlot = inputLink.getSourceSlot();
            Type actualType = sourceSlot.getActualType();
            return actualType;
        }
        return getExpectedType();
    }

    @Override
    public Link setInputLink(Link newInput)
    {
        Link oldInput = this.inputLink;
        this.inputLink = newInput;
        if (oldInput != null)
        {
            fireLinkRemoved(oldInput);
        }
        if (newInput != null)
        {
            fireLinkAdded(newInput);
        }
        return oldInput;
    }
    

    @Override
    public Link getInputLink()
    {
        return inputLink;
    }

    @Override
    public String toString()
    {
        return "DefaultInputSlot["
            + "module=" + getModule() + ","
            + "index=" + getIndex() + "]";
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIndex();
        result = prime * result + Objects.hashCode(getModule());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof InputSlot))
            return false;
        InputSlot other = (InputSlot) obj;
        if (getIndex() != other.getIndex())
            return false;
        if (!Objects.equals(getModule(), other.getModule()))
            return false;
        return true;
    }
    
}
