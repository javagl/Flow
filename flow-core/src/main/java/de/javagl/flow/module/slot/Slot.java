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

import de.javagl.flow.module.Module;


/**
 * Basic interface for a slot that describes one input or output of 
 * a {@link Module}. Concrete implementations will implement either
 * the {@link InputSlot} or the {@link OutputSlot} interface.
 */
public interface Slot
{
    /**
     * Returns the {@link Module} that this slot belongs to
     * 
     * @return The {@link Module} that this slot belongs to
     */
    Module getModule();
    
    /**
     * Returns the index that this slot has in its {@link Module}.
     * 
     * @return The index of this slot
     */
    int getIndex();
    
    /**
     * Returns the {@link SlotInfo} that corresponds to this slot
     * 
     * @return The {@link SlotInfo} for this slot
     */
    SlotInfo getSlotInfo();
    
    /**
     * Returns the formal type of this slot. This is the same as the
     * type returned by {@link SlotInfo#getType()}, except that the
     * type variables are specific for this slot in this module. 
     * 
     * @return The formal type of this slot
     */
    Type getFormalType();

    /**
     * Returns the actual type of this slot. <br>
     * <br>
     * For an {@link OutputSlot}, this is the type of the object that it 
     * actually provides.<br> 
     * <br>
     * For an {@link InputSlot}, this is the type of the object that it is 
     * actually fed with (and consequently, the 
     * {@link InputSlot#getExpectedType() expected type} must always be 
     * a assignable from this type).
     * 
     * @return The actual type of this slot
     */
    Type getActualType();
    
    /**
     * Add the given {@link SlotListener} to be informed about 
     * changes of this slot.
     * 
     * @param slotListener The listener to add
     */
    void addSlotListener(SlotListener slotListener);

    /**
     * Remove the given {@link SlotListener}.
     * 
     * @param slotListener The listener to remove
     */
    void removeSlotListener(SlotListener slotListener);
}
