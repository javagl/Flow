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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.javagl.flow.Flow;
import de.javagl.flow.TypeContext;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * Default implementation of an {@link OutputSlot}
 */
final class DefaultOutputSlot extends AbstractSlot implements OutputSlot
{
    /**
     * The default actual type for this {@link OutputSlot}
     */
    private final Type defaultActualType;

    /**
     * The list of output {@link Link} objects that have been 
     * attached to this slot.
     */
    private List<Link> outputLinks;
    
    /**
     * Creates a new output slot for the given {@link Module}, with the
     * given index and default actual type. This type should
     * be the same as the one defined by {@link SlotInfo#getType()}
     * for this {@link Slot}.
     * 
     * @param module The {@link Module}
     * @param index The index of this slot in the {@link Module} 
     * @param formalType The {@link Slot#getFormalType() formal type}
     */
    DefaultOutputSlot(Module module, int index, Type formalType)
    {
        super(module, index, 
            module.getModuleInfo().getOutputSlotInfos().get(index),
            formalType);
        this.defaultActualType = formalType;
        this.outputLinks = new ArrayList<Link>();
    }
    
    @Override
    public Type getActualType()
    {
        Type actualType = defaultActualType;
        Flow flow = getModule().getFlow();
        if (flow != null)
        {
            TypeContext typeContext = flow.getTypeContext();
            actualType = typeContext.resolveType(defaultActualType);
        }
        return actualType;
    }
    
    @Override
    public final void addOutputLink(Link output)
    {
        boolean changed = outputLinks.add(output);
        if (changed)
        {
            fireLinkAdded(output);
        }
    }

    @Override
    public final void removeOutputLink(Link output)
    {
        boolean changed = outputLinks.remove(output);
        if (changed)
        {
            fireLinkRemoved(output);
        }
    }

    @Override
    public final List<Link> getOutputLinks()
    {
        return Collections.unmodifiableList(outputLinks);
    }
    
    @Override
    public String toString()
    {
        return "DefaultOutputSlot["
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
        if (!(obj instanceof OutputSlot))
            return false;
        OutputSlot other = (OutputSlot) obj;
        if (getIndex() != other.getIndex())
            return false;
        if (Objects.equals(getModule(),  other.getModule()))
            return false;
        return true;
    }


}
