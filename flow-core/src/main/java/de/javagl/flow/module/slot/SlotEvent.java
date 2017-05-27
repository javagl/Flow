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

import java.util.EventObject;

import de.javagl.flow.link.Link;


/**
 * An event passed to a {@link SlotListener} to indicate a change
 * in a {@link Slot}
 */
public final class SlotEvent extends EventObject
{
    /**
     * The {@link Slot} from which this event originates
     */
    private final Slot slot;
    
    /**
     * The optional {@link Link} that was added or removed
     */
    private final Link link;
    
    /**
     * Creates a new event with the given parameters
     * 
     * @param slot The {@link Slot} from which this event originates
     * @param link The optional {@link Link} that was added or removed
     */
    SlotEvent(Slot slot, Link link)
    {
        super(slot);
        this.slot = slot;
        this.link = link;
    }


    /**
     * Returns the {@link Slot} from which this event originates
     * 
     * @return The {@link Slot} from which this event originates
     */
    public Slot getSlot()
    {
        return slot;
    }

    /**
     * Returns the optional {@link Link} that was added or removed
     * 
     * @return The optional {@link Link} that was added or removed
     */
    public Link getLink()
    {
        return link;
    }

    @Override
    public String toString()
    {
        return "SlotEvent[" 
            + "slot=" + slot + "," 
            + "link=" + link + "," + "]";
    }
    
}
