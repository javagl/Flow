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

package de.javagl.flow.module.view;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.creation.ModuleCreator;

/**
 * Interface for classes that serve as the view for a {@link Module}.
 * Instances of classes implementing this interface provide a GUI
 * component that belongs to a certain {@link Module}.
 */
public interface ModuleView
{
    /**
     * Returns the GUI component of this view
     * 
     * @return The GUI component of this view
     */
    Object getComponent();
    
    /**
     * Set the {@link Module} that serves as the model for this view.
     * Implementations of this method may assume the {@link Module}
     * to be of a certain type. Particularly, the {@link Module} 
     * will usually have to be of the same type as the {@link Module}
     * that is created by the {@link ModuleCreator} that also created
     * this {@link ModuleView}. The caller is responsible to make
     * sure that this method is only called with a {@link Module}
     * whose type matches the type expected by this view.
     * 
     * @param newModule The {@link Module} that serves as the model for this 
     * view
     */
    void setModule(Module newModule);
}
