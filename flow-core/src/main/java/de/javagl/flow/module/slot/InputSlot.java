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

import de.javagl.flow.Flow;
import de.javagl.flow.TypeContext;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;


/**
 * Interface for the {@link Slot} of a {@link Module} that receives
 * the input for the {@link Module}.
 */
public interface InputSlot extends Slot
{
    /**
     * Returns the type that is expected by this slot. This is the type
     * that is assumed for all objects that are obtained from the
     * {@link Link#provide()} method of a {@link Link} that is attached
     * to this slot. <br>
     * <br>
     * In simple cases, this will be the same as the 
     * {@link SlotInfo#getType() SlotInfo type} of the {@link #getSlotInfo()},
     * but it may also be a parameterized type where the type variables have
     * been resolved based on the {@link TypeContext} of the {@link Flow}
     * that the {@link #getModule() module} belongs to.  
     *  
     * @return The type that is expected by this slot
     */
    Type getExpectedType();
    
    /**
     * Set the {@link Link} that provides the input for this slot.
     * 
     * @param newInputLink The input {@link Link}
     * @return The old input {@link Link}. May be <code>null</code>
     * if no link was previously assigned to this slot. 
     */
    Link setInputLink(Link newInputLink);
    
    /**
     * Returns the input {@link Link} of this slot. 
     * 
     * @return The input {@link Link}. May be <code>null</code>
     * if no input was assigned to this slot. 
     */
    Link getInputLink();
}
