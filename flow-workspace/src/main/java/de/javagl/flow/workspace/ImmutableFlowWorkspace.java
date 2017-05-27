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
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;
import de.javagl.selection.SelectionModel;
import de.javagl.selection.SelectionModels;

/**
 * Implementation of an immutable {@link FlowWorkspace}.
 */
final class ImmutableFlowWorkspace implements FlowWorkspace
{
    /**
     * The {@link Flow} of this workspace
     */
    private final Flow flow;
    
    /**
     * The {@link FlowLayout} for the {@link Flow} of this workspace
     */
    private final FlowLayout flowLayout;
    
    /**
     * The {@link SelectionModel} for the {@link Module} objects
     */
    private final SelectionModel<Module> moduleSelectionModel;
    
    /**
     * The {@link SelectionModel} for the {@link Link} objects
     */
    private final SelectionModel<Link> linkSelectionModel;
    
    /**
     * Create a new workspace combining the given {@link Flow} and
     * {@link FlowLayout}
     * 
     * @param flow The {@link Flow}
     * @param flowLayout The {@link FlowLayout} for the {@link Flow}
     */
    ImmutableFlowWorkspace(Flow flow, FlowLayout flowLayout)
    {
        this.flow = flow;
        this.flowLayout = flowLayout;
        this.moduleSelectionModel = SelectionModels.create();
        this.linkSelectionModel = SelectionModels.create();
    }

    @Override
    public Flow getFlow()
    {
        return flow;
    }

    @Override
    public FlowLayout getFlowLayout()
    {
        return flowLayout;
    }

    @Override
    public SelectionModel<Module> getModuleSelectionModel()
    {
        return moduleSelectionModel;
    }

    @Override
    public SelectionModel<Link> getLinkSelectionModel()
    {
        return linkSelectionModel;
    }
}
