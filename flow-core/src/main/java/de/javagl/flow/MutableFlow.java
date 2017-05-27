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
package de.javagl.flow;

import de.javagl.flow.link.Link;
import de.javagl.flow.link.Links;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Interface for a mutable {@link Flow}. This is a {@link Flow} where 
 * {@link Module} objects and {@link Link} objects may be added or removed.
 */
public interface MutableFlow extends Flow
{
    /**
     * Add the given {@link Module} to this {@link Flow}. Afterwards,
     * a call to {@link Module#getFlow()} will return this {@link Flow}.
     * 
     * @param module The {@link Module} to add
     * @return Whether this {@link Flow} changed through this action
     */
    boolean addModule(Module module);

    /**
     * Remove the given {@link Module} from this {@link Flow}. Afterwards,
     * a call to {@link Module#getFlow()} will return <code>null</code>.
     * 
     * @param module The {@link Module} to remove
     * @return Whether this {@link Flow} changed through this action
     */
    boolean removeModule(Module module);
    
    /**
     * Add the given {@link Link} to this {@link Flow}. This will
     * cause the {@link Link} to actually be connected to its source 
     * {@link OutputSlot} and its target {@link InputSlot}. <br>
     * <br>
     * This method will not perform any sanity checks. 
     * The {@link Links#isValid(TypeContext, Link)} method may be used to check 
     * whether a {@link Link} is valid <i>at a specific point in time</i>:
     * A formerly invalid {@link Link} might become valid when the 
     * {@link OutputSlot#getActualType() actual type} of an {@link OutputSlot}
     * that it is connected to changes. Similarly, a formerly valid 
     * {@link Link} might become invalid when the actual type changes.<br>
     * <br>
     * Having an invalid {@link Link} inside a {@link Flow} may cause 
     * undefined behavior when the {@link Flow} is executed. Invalid 
     * {@link Link} configurations should only occur temporarily at 
     * construction time.  
     * 
     * @param link The {@link Link} to add
     * @return Whether this {@link Flow} changed through this action
     */
    boolean addLink(Link link);
    
    /**
     * Remove the given {@link Link} from this {@link Flow}.
     * This will cause the given {@link Link} to be disconnected from the 
     * {@link OutputSlot} of its source {@link Module} and the
     * {@link InputSlot} of its target {@link Module}.
     * 
     * @param link The {@link Link} to remove
     * @return Whether this {@link Flow} changed through this action
     */
    boolean removeLink(Link link);
}