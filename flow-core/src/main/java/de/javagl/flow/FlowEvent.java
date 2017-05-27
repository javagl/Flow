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

import java.util.EventObject;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * An event that passed to a {@link FlowListener} to indicate
 * a change in a {@link Flow}
 */
public final class FlowEvent extends EventObject
{
    /**
     * The {@link Flow} from which this event originates
     */
    private final Flow flow;
    
    /**
     * The optional {@link Module} that was added or removed
     */
    private final Module module;
    
    /**
     * The optional {@link Link} that was added or removed
     */
    private final Link link;
    
    /**
     * Creates a new event with the given parameters
     * 
     * @param flow The {@link Flow} from which this event originates
     * @param module The {@link Module} that was added or removed
     */
    FlowEvent(Flow flow, Module module)
    {
        this(flow, module, null);
    }
    
    /**
     * Creates a new event with the given parameters
     * 
     * @param flow The {@link Flow} from which this event originates
     * @param link The {@link Link} that was added or removed
     */
    FlowEvent(Flow flow, Link link)
    {
        this(flow, null, link);
    }
    
    /**
     * Creates a new event with the given parameters
     * 
     * @param flow The {@link Flow} from which this event originates
     * @param module The optional {@link Module} that was added or removed
     * @param link The optional {@link Link} that was added or removed
     */
    private FlowEvent(Flow flow, Module module, Link link)
    {
        super(flow);
        this.flow = flow;
        this.module = module;
        this.link = link;
    }

    /**
     * Returns the {@link Flow} from which this event originates
     * 
     * @return The {@link Flow} from which this event originates
     */
    public Flow getFlow()
    {
        return flow;
    }

    /**
     * Returns the optional {@link Module} that was added or removed
     * 
     * @return The optional {@link Module} that was added or removed
     */
    public Module getModule()
    {
        return module;
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
}
