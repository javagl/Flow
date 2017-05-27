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
package de.javagl.flow.workspace;

import de.javagl.flow.Flow;
import de.javagl.flow.MutableFlow;
import de.javagl.selection.SelectionModel;

/**
 * Methods to create {@link FlowWorkspace} instances.
 */
public class FlowWorkspaces
{
    /**
     * Create a new, empty {@link MutableFlowWorkspace}
     * 
     * @return A new, empty {@link MutableFlowWorkspace}
     */
    public static MutableFlowWorkspace create()
    {
        return new DefaultFlowWorkspace();
    }

    /**
     * Create a new {@link FlowWorkspace} with the given {@link Flow}
     * and {@link FlowLayout} (and empty {@link SelectionModel} objects).
     * 
     * @param flow The {@link Flow} of the {@link FlowWorkspace}
     * @param flowLayout The {@link FlowLayout} of the {@link FlowWorkspace}
     * @return The new {@link FlowWorkspace}
     */
    public static FlowWorkspace create(Flow flow, FlowLayout flowLayout)
    {
        return new ImmutableFlowWorkspace(flow, flowLayout);
    }
    
    /**
     * Create a new {@link MutableFlowWorkspace} with the given {@link Flow}
     * and {@link FlowLayout} (and empty {@link SelectionModel} objects).
     * 
     * @param flow The {@link Flow} of the {@link FlowWorkspace}
     * @param flowLayout The {@link FlowLayout} of the {@link FlowWorkspace}
     * @return The new {@link FlowWorkspace}
     */
    public static MutableFlowWorkspace create(
        MutableFlow flow, MutableFlowLayout flowLayout)
    {
        return new DefaultFlowWorkspace(flow, flowLayout);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private FlowWorkspaces()
    {
        // Private constructor to prevent instantiation
    }
}
