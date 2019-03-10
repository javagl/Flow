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

import java.util.Objects;

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Default implementation of a {@link Link}.
 */
final class DefaultLink extends AbstractLink implements Link
{
    /**
     * The current contents of this link
     */
    private Object contents;

    /**
     * Creates a new link between the given source- and target slot
     * 
     * @param sourceSlot The {@link OutputSlot} that is the source of this link
     * @param targetSlot The {@link InputSlot} that is the target of this link
     * @throws NullPointerException If any argument is <code>null</code>
     */
    DefaultLink(
        OutputSlot sourceSlot,
        InputSlot targetSlot)
    {
        super(sourceSlot, targetSlot);
    }
    
    @Override
    public void accept(Object object)
    {
        this.contents = object;
        fireObjectAccepted(object);
    }

    @Override
    public Object provide()
    {
        fireObjectProvided(contents);
        return contents;
    }

    @Override
    public String toString()
    {
        return "Link[" + getSourceSlot() + "-" + getTargetSlot() + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(getSourceSlot());
        result = prime * result + Objects.hashCode(getTargetSlot());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Link))
            return false;
        Link other = (Link) obj;
        if (!Objects.equals(getSourceSlot(), other.getSourceSlot()))
            return false;
        if (!Objects.equals(getTargetSlot(), other.getTargetSlot()))
            return false;
        return true;
    }

}