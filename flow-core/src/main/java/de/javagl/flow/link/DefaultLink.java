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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Default implementation of a {@link Link}.
 * 
 * TODO Also see notes in {@link Link}
 */
final class DefaultLink extends AbstractLink implements Link
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(DefaultLink.class.getName());

    /**
     * The log level that will be used for the output
     */
    private static final Level level = Level.FINE;

    /**
     * An object representing a 'null' entry in the queue. Blocking
     * queues can not accept 'null' as their input, so this object
     * is used as a placeholder for 'null'. 
     */
    private static final Object NULL_OBJECT = new Object();
    
    /**
     * The queue that internally stores the value that is
     * passes through this link.
     */
    private final BlockingQueue<Object> queue;
    
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
        this.queue = new ArrayBlockingQueue<Object>(1);
    }

    @Override
    public void accept(Object object)
    {
        try
        {
            logger.log(level, this + " try to put");
            if (object == null)
            {
                queue.put(NULL_OBJECT);
            }
            else
            {
                queue.put(object);
            }
            logger.log(level, this + " try to put DONE");
            fireObjectAccepted(object);
        }
        catch (InterruptedException e)
        {
            logger.log(level, 
                this + " was interrupted while trying to accept");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Object provide()
    {
        try
        {
            logger.log(level, this + " try to take");
            Object result = queue.take();
            if (result == NULL_OBJECT)
            {
                result = null;
            }
            logger.log(level, this + " try to take DONE");
            fireObjectProvided(result);
            return result;
        }
        catch (InterruptedException e)
        {
            logger.log(level,
                this + " was interrupted while trying to provide");
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public List<Object> getContents()
    {
        Object contentsArray[] = queue.toArray();
        for (int i = 0; i < contentsArray.length; i++)
        {
            if (contentsArray[i] == NULL_OBJECT)
            {
                contentsArray[i] = null;
            }
        }
        return Collections.unmodifiableList(Arrays.asList(contentsArray));
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