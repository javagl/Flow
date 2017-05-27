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

import java.util.EventListener;

import de.javagl.flow.link.Link;


/**
 * Interface for classes that want to be informed about changes of
 * a {@link Slot}.
 */
public interface SlotListener extends EventListener
{
    /**
     * Will be called when a {@link Link} was added to a {@link Slot}.<br>
     * <br> 
     * For an {@link OutputSlot}, this method will be called to indicate
     * that a link was added using the {@link OutputSlot#addOutputLink(Link)}
     * method.<br>
     * <br>
     * For an {@link InputSlot}, this method will be called to indicate 
     * that a link was set using the {@link InputSlot#setInputLink(Link)} 
     * method. 
     * 
     * @param slotEvent The {@link SlotEvent}
     */
    void linkAdded(SlotEvent slotEvent);
    
    /**
     * Will be called when a {@link Link} was removed from a {@link Slot}.<br>
     * <br> 
     * For an {@link OutputSlot}, this method will be called to indicate 
     * that a link was removed by calling 
     * {@link OutputSlot#removeOutputLink(Link)}.<br>
     * <br>
     * For an {@link InputSlot}, this method will be called to indicate 
     * that a link was removed by calling {@link InputSlot#setInputLink(Link)} 
     * with a new link
     * 
     * @param slotEvent The {@link SlotEvent}
     */
    void linkRemoved(SlotEvent slotEvent);
}
