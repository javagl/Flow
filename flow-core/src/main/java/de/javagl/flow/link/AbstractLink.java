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
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Abstract base implementation of a {@link Link}. It only manages the 
 * {@link LinkListener} instances.
 */
abstract class AbstractLink implements Link
{
    /**
     * The source slot of this link, namely the {@link OutputSlot}
     * of the source module.
     */
    private final OutputSlot sourceSlot;
    
    /**
     * The target slot of this link, namely the {@link InputSlot} 
     * of the target module.
     */
    private final InputSlot targetSlot;
    
    /**
     * The list of {@link LinkListener} instances that want to be 
     * informed about changes in this link.
     */
    private final List<LinkListener> linkListeners;

    /**
     * Creates a new link between the given source- and target slot
     * 
     * @param sourceSlot The {@link OutputSlot} that is the source of this link
     * @param targetSlot The {@link InputSlot} that is the target of this link
     * @throws NullPointerException If any argument is <code>null</code>
     */
    protected AbstractLink(
        OutputSlot sourceSlot,
        InputSlot targetSlot)
    {
        this.sourceSlot = Objects.requireNonNull(
            sourceSlot, "The sourceSlot may not be null");
        this.targetSlot = Objects.requireNonNull(
            targetSlot, "The targetSlot may not be null"); 
        this.linkListeners = new CopyOnWriteArrayList<LinkListener>();
    }

    @Override
    public final OutputSlot getSourceSlot()
    {
        return sourceSlot;
    }

    @Override
    public final InputSlot getTargetSlot()
    {
        return targetSlot;
    }

    @Override
    public final void addLinkListener(LinkListener linkListener)
    {
        linkListeners.add(linkListener);
    }

    @Override
    public final void removeLinkListener(LinkListener linkListener)
    {
        linkListeners.remove(linkListener);
    }

    /**
     * Inform all registered {@link LinkListener} instances that the given 
     * object was {@link #accept(Object) accepted} by this link.
     * 
     * @param object The object that was {@link #accept(Object) accepted}
     */
    protected final void fireObjectAccepted(Object object)
    {
        if (!linkListeners.isEmpty())
        {
            LinkEvent linkEvent = new LinkEvent(this, object);
            for (LinkListener linkListener : linkListeners)
            {
                linkListener.objectAccepted(linkEvent);
            }
        }
    }

    /**
     * Inform all registered {@link LinkListener} instances that the given 
     * object was {@link #provide() provided} by this link
     * 
     * @param object The object that was {@link #provide() provided}
     */
    protected final void fireObjectProvided(Object object)
    {
        if (!linkListeners.isEmpty())
        {
            LinkEvent linkEvent = new LinkEvent(this, object);
            for (LinkListener linkListener : linkListeners)
            {
                linkListener.objectProvided(linkEvent);
            }
        }
    }

}