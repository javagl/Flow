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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleUtils;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;


/**
 * Default implementation of a {@link MutableFlow}
 */
final class DefaultFlow implements MutableFlow
{
    /**
     * The {@link Module} objects in this flow
     */
    private final Set<Module> modules;
    
    /**
     * The {@link Link} objects in this flow
     */
    private final Set<Link> links;
    
    /**
     * The {@link TypeContext} associated with this flow
     */
    private final TypeContext typeContext;
    
    /**
     * The {@link FlowListener} instances that want to be informed
     * about changes in this flow
     */
    private final List<FlowListener> flowListeners;
    
    /**
     * Creates a new, empty flow
     */
    DefaultFlow()
    {
        modules = new LinkedHashSet<Module>();
        links = new LinkedHashSet<Link>();
        typeContext = new DummyTypeContext();
        flowListeners = new CopyOnWriteArrayList<FlowListener>();
    }
    
    @Override
    public Set<Module> getModules()
    {
        return Collections.unmodifiableSet(modules);
    }
    
    @Override
    public Set<Link> getLinks()
    {
        return Collections.unmodifiableSet(links);
    }
    
    @Override
    public TypeContext getTypeContext()
    {
        return typeContext;
    }

    @Override
    public boolean addModule(Module module)
    {
        boolean changed = modules.add(module);
        if (changed)
        {
            module.setFlow(this);
            fireModuleAdded(module);
        }
        return changed;
    }

    @Override
    public boolean removeModule(Module module)
    {
        Set<Link> links = ModuleUtils.computeLinks(module);
        for (Link link : links)
        {
            removeLink(link);
        }
        boolean changed = modules.remove(module);
        if (changed)
        {
            module.setFlow(null);
            fireModuleRemoved(module);
        }
        return changed;
    }

    @Override
    public boolean addLink(Link link)
    {
        boolean changed = links.add(link);
        if (changed)
        {
            OutputSlot sourceSlot = link.getSourceSlot();
            sourceSlot.addOutputLink(link);
            InputSlot targetSlot = link.getTargetSlot();
            targetSlot.setInputLink(link);
            fireLinkAdded(link);
        }
        return changed;
    }
    
    @Override
    public boolean removeLink(Link link)
    {
        boolean changed = links.remove(link);
        if (changed)
        {
            OutputSlot sourceSlot = link.getSourceSlot();
            sourceSlot.removeOutputLink(link);
            InputSlot targetSlot = link.getTargetSlot();
            targetSlot.setInputLink(null);
            fireLinkRemoved(link);
        }
        return changed;
    }

    
    @Override
    public void addFlowListener(FlowListener flowListener)
    {
        flowListeners.add(flowListener);
    }

    @Override
    public void removeFlowListener(FlowListener flowListener)
    {
        flowListeners.remove(flowListener);
    }


    /**
     * Notify each registered {@link FlowListener} that the given
     * {@link Module} was added.
     * 
     * @param module The {@link Module} that was added
     */
    protected final void fireModuleAdded(Module module)
    {
        FlowEvent flowEvent = new FlowEvent(this, module);
        for (FlowListener flowListener : flowListeners)
        {
            flowListener.moduleAdded(flowEvent);
        }
    }
    
    /**
     * Notify each registered {@link FlowListener} that the given
     * {@link Module} was removed.
     * 
     * @param module The {@link Module} that was removed
     */
    protected final void fireModuleRemoved(Module module)
    {
        FlowEvent flowEvent = new FlowEvent(this, module);
        for (FlowListener flowListener : flowListeners)
        {
            flowListener.moduleRemoved(flowEvent);
        }
    }
    
    /**
     * Notify each registered {@link FlowListener} that the given
     * {@link Link} was added.
     * 
     * @param link The {@link Link} that was added
     */
    protected final void fireLinkAdded(Link link)
    {
        FlowEvent flowEvent = new FlowEvent(this, link);
        for (FlowListener flowListener : flowListeners)
        {
            flowListener.linkAdded(flowEvent);
        }
    }
    
    /**
     * Notify each registered {@link FlowListener} that the given
     * {@link Link} was removed.
     * 
     * @param link The {@link Link} that was removed
     */
    protected final void fireLinkRemoved(Link link)
    {
        FlowEvent flowEvent = new FlowEvent(this, link);
        for (FlowListener flowListener : flowListeners)
        {
            flowListener.linkRemoved(flowEvent);
        }
    }
}