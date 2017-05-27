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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * Abstract base implementation of a {@link Slot}
 */
abstract class AbstractSlot implements Slot
{
    /**
     * The {@link Module} that this slot belongs to
     */
    private final Module module;
    
    /**
     * The index of this slot in its {@link Module}
     */
    private final int index;
    
    /**
     * The {@link SlotInfo} that corresponds to this slot
     */
    private final SlotInfo slotInfo;
    
    /**
     * The {@link #getFormalType() formal type} of this slot
     */
    private final Type formalType;
    
    /**
     * The list of {@link SlotListener} instances that want to be
     * informed about changes in this slot.
     */
    private final List<SlotListener> slotListeners;
    
    /**
     * Creates a new slot for the given {@link Module},
     * with the given index.
     * 
     * @param module The {@link Module}
     * @param index The index of this slot in the module
     * @param slotInfo The {@link SlotInfo} for this slot
     * @param formalType The formal type of this slot.
     */
    protected AbstractSlot(Module module, int index, 
        SlotInfo slotInfo, Type formalType)
    {
        this.module = Objects.requireNonNull(
            module, "The module may not be null");
        this.index = index;
        this.slotInfo = Objects.requireNonNull(
            slotInfo, "The slotInfo may not be null");
        this.formalType = Objects.requireNonNull(
            formalType, "The formalType may not be null");
        this.slotListeners = new CopyOnWriteArrayList<SlotListener>();
    }
    
    @Override
    public Type getFormalType()
    {
        return formalType;
    }

    @Override
    public final Module getModule()
    {
        return module;
    }

    @Override
    public final int getIndex()
    {
        return index;
    }
    
    @Override
    public SlotInfo getSlotInfo()
    {
        return slotInfo;
    }

    @Override
    public final void addSlotListener(SlotListener slotListener)
    {
        slotListeners.add(slotListener);
    }

    @Override
    public final void removeSlotListener(SlotListener slotListener)
    {
        slotListeners.remove(slotListener);
    }
    
    /**
     * Notify all registered {@link SlotListener} instances that the
     * given {@link Link} was added to this slot
     * 
     * @param link The {@link Link} that was added
     */
    protected final void fireLinkAdded(Link link)
    {
        if (!slotListeners.isEmpty())
        {
            SlotEvent slotEvent = new SlotEvent(this, link);
            for (SlotListener slotListener : slotListeners)
            {
                slotListener.linkAdded(slotEvent);
            }
        }
    }

    /**
     * Notify all registered {@link SlotListener} instances that the
     * given {@link Link} was removed from this slot
     * 
     * @param link The {@link Link} that was removed
     */
    protected final void fireLinkRemoved(Link link)
    {
        if (!slotListeners.isEmpty())
        {
            SlotEvent slotEvent = new SlotEvent(this, link);
            for (SlotListener slotListener : slotListeners)
            {
                slotListener.linkRemoved(slotEvent);
            }
        }
    }
    
    
}
