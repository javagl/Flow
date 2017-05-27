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

import java.util.List;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * Interface for a {@link Slot} of a {@link Module} that provides the 
 * output of the {@link Module}.
 */
public interface OutputSlot extends Slot
{
    /**
     * Add the given output {@link Link} to this slot. When the {@link Module}
     * that this slot belongs to has been executed, its output will be passed
     * to each output {@link Link} by calling the {@link Link#accept(Object)}
     * method.
     * 
     * @param outputLink The output {@link Link} to add
     */
    void addOutputLink(Link outputLink);

    /**
     * Remove the given output {@link Link} from this slot. 
     * 
     * @param outputLink The output {@link Link} to remove
     */
    void removeOutputLink(Link outputLink);
    
    /**
     * Returns an unmodifiable view on the list of output {@link Link}
     * instances that have been attached to this slot.
     * 
     * @return The list of output {@link Link} instances
     */
    List<Link> getOutputLinks();
}
