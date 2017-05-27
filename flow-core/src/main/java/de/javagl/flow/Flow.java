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

import java.util.Set;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;

/**
 * Interface for a graph consisting of {@link Module} objects and 
 * {@link Link} objects. This graph describes the connectivity among modules, 
 * and thus the logical (although not necessarily the technical) execution 
 * flow. Instances of classes implementing this interface may be created with
 * the {@link Flows} class.
 */
public interface Flow
{
    /**
     * Returns an unmodifiable view on the set of {@link Module} objects 
     * 
     * @return The {@link Module} objects
     */
    Set<Module> getModules();

    /**
     * Returns an unmodifiable view on the set of {@link Link} objects 
     * 
     * @return The {@link Link} objects
     */
    Set<Link> getLinks();
    
    /**
     * Add the given {@link FlowListener} to be informed about
     * changes in this flow
     * 
     * @param flowListener The {@link FlowListener} to add
     */
    void addFlowListener(FlowListener flowListener);

    /**
     * Remove the given {@link FlowListener}
     * 
     * @param flowListener The {@link FlowListener} to remove
     */
    void removeFlowListener(FlowListener flowListener);

    /**
     * TODO TypeContext is experimental. Do not use.
     * 
     * @return Nothing relevant for you.
     */
    TypeContext getTypeContext();
}

