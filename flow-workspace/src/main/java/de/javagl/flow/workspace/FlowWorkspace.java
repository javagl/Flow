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

/**
 * An interface summarizing the state of a {@link Flow} that is shown 
 * in a GUI component. It summarizes the {@link Flow} itself, its 
 * {@link FlowLayout}, a {@link SelectionModel} for the {@link Module} objects
 * a {@link SelectionModel} for the {@link Link} objects. 
 *  
 */
public interface FlowWorkspace
{
    /**
     * Returns the {@link Flow} of this workspace
     * 
     * @return The {@link Flow} of this workspace
     */
    Flow getFlow();

    /**
     * Returns the {@link FlowLayout} of the {@link Flow} in this workspace
     * 
     * @return The {@link FlowLayout} of the {@link Flow} in this workspace
     */
    FlowLayout getFlowLayout();
    
    /**
     * Returns the {@link SelectionModel} that contains information about
     * the selected {@link Module} objects in this workspace
     * 
     * @return The {@link SelectionModel} for the {@link Module} objects
     */
    SelectionModel<Module> getModuleSelectionModel();
    
    /**
     * Returns the {@link SelectionModel} that contains information about
     * the selected {@link Link} objects in this workspace
     * 
     * @return The {@link SelectionModel} for the {@link Link} objects
     */
    SelectionModel<Link> getLinkSelectionModel();
}
