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

import java.util.List;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Interface for a link between two {@link Module} instances. More 
 * specifically, the link will be created between one {@link OutputSlot} of 
 * a module and one {@link InputSlot} of another module. Instances may be 
 * created using the {@link Links} class.<br> 
 * <br>
 */
public interface Link
{
    // Note: The exact behavior of a link may have to be specified in more detail. 
    // For example, the {@link DefaultLink} implementation internally uses a 
    // one-element, blocking queue. Other implementations may be possible. 
    // Also see notes in {@link DefaultLink}.
    
    /**
     * Accept the given input object, which was produced by the 
     * module that the {@link #getSourceSlot()} belongs to, so 
     * that it may be {@link #provide() provided} to another
     * module.
     * 
     * @param object The object to accept.
     */
    void accept(Object object);
    
    /**
     * Provide the object that was previously {@link #accept(Object) accepted},
     * as an input for the module that the {@link #getTargetSlot()} belongs to.
     * 
     * @return The object 
     */
    Object provide();
    
    /**
     * Returns the source slot of this link, namely the {@link OutputSlot}
     * of the source module.
     * 
     * @return The source slot of this link
     */
    OutputSlot getSourceSlot();
    
    /**
     * Returns the target slot of this link, namely the {@link InputSlot}
     * of the target module.
     * 
     * @return The target slot of this link
     */
    InputSlot getTargetSlot();
 
    /**
     * Add the given {@link LinkListener} to this link, to be informed 
     * when objects are {@link Link#accept(Object) accepted} or 
     * {@link Link#provide() provided}
     * 
     * @param linkListener The {@link LinkListener} to add
     */
    void addLinkListener(LinkListener linkListener);

    /**
     * Remove the given {@link LinkListener} from this link
     * 
     * @param linkListener The {@link LinkListener} to remove
     */
    void removeLinkListener(LinkListener linkListener);
    
    
    /**
     * TODO Experimental. Do not use.
     * @return Do not use
     */
    List<Object> getContents();
}