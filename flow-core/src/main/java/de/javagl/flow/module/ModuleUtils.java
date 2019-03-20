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
package de.javagl.flow.module;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;


/**
 * Utility methods related to {@link Module} objects. For example, for 
 * computing the successors or predecessors of a {@link Module}, or the 
 * {@link Link} objects that are attached to a {@link Module}.<br>
 * <br>
 * Note: The methods in this class might be moved to the {@link Modules}
 * class in the future, and this class may be omitted.
 */
public class ModuleUtils
{
    /**
     * Return an unmodifiable set containing the direct successors of the given
     * {@link Module}. That is, a set that contains all {@link Module} objects
     * that are at the end of a {@link Link} that is attached to one
     * {@link OutputSlot} of the given {@link Module}
     * 
     * @param module The {@link Module}
     * @return The set of successors
     */
    public static Set<Module> computeSuccessors(Module module)
    {
        Set<Module> successors = new LinkedHashSet<Module>();
        for (OutputSlot outputSlot : module.getOutputSlots())
        {
            for (Link link : outputSlot.getOutputLinks())
            {
                if (link.getSourceSlot().getModule().equals(module))
                {
                    successors.add(link.getTargetSlot().getModule());
                }
            }
        }
        return Collections.unmodifiableSet(successors);
    }
    
    /**
     * Return an unmodifiable set containing the direct successors of the given
     * {@link Module} objects. That is, a set that contains all {@link Module} 
     * objects that are at the end of a {@link Link} that is attached to one
     * {@link OutputSlot} of one of the given {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     * @return The set of successors
     */
    public static Set<Module> computeSuccessors(
        Iterable<? extends Module> modules)
    {
        Set<Module> successors = new LinkedHashSet<Module>();
        for (Module module : modules)
        {
            successors.addAll(computeSuccessors(module));
        }
        return Collections.unmodifiableSet(successors);
    }
    
    /**
     * Return an unmodifiable set containing all (direct and indirect) 
     * successors of the given {@link Module}. That is, a set that contains 
     * all {@link Module} objects that are at the end of a {@link Link} 
     * that is attached to one {@link OutputSlot} of the given {@link Module}, 
     * and all successors of these modules.
     * 
     * @param module The {@link Module}
     * @return The set of all successors
     */
    public static Set<Module> computeAllSuccessors(Module module)
    {
        Set<Module> allSuccessors = new LinkedHashSet<Module>();
        computeAllSuccessors(Collections.singleton(module), allSuccessors);
        return allSuccessors;
    }
    
    /**
     * Recursively compute all successors of the given collection of 
     * {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     * @param result The collection that will store the result
     */
    private static void computeAllSuccessors(
        Iterable<? extends Module> modules, Collection<Module> result)
    {
        for (Module module : modules)
        {
            Set<Module> successors = computeSuccessors(module);
            result.addAll(successors);
            computeAllSuccessors(successors, result);
        }
    }
    

    /**
     * Return an unmodifiable set containing the direct predecessors of the 
     * given {@link Module}. That is, a set that contains all {@link Module} 
     * objects that are at the start of a {@link Link} that is attached to one
     * {@link InputSlot} of the given {@link Module}
     * 
     * @param module The {@link Module}
     * @return The set of predecessors
     */
    public static Set<Module> computePredecessors(Module module)
    {
        Set<Module> predecessors = new LinkedHashSet<Module>();
        for (InputSlot inputSlot : module.getInputSlots())
        {
            Link link = inputSlot.getInputLink();
            if (link != null && link.getTargetSlot().getModule().equals(module))
            {
                predecessors.add(link.getSourceSlot().getModule());
            }
        }
        return Collections.unmodifiableSet(predecessors);
    }

    /**
     * Return an unmodifiable set containing the direct predecessors of the 
     * given {@link Module} objects. That is, a set that contains all 
     * {@link Module} objects that are at the start of a {@link Link} that 
     * is attached to one {@link InputSlot} of one of the given {@link Module} 
     * objects
     * 
     * @param modules The {@link Module}
     * @return The set of predecessors
     */
    public static Set<Module> computePredecessors(
        Iterable<? extends Module> modules)
    {
        Set<Module> predecessors = new LinkedHashSet<Module>();
        for (Module module : modules)
        {
            predecessors.addAll(computePredecessors(module));
        }
        return Collections.unmodifiableSet(predecessors);
    }

    /**
     * Return an unmodifiable set containing all (direct and indirect) 
     * predecessors of the given {@link Module}. That is, a set that contains 
     * all {@link Module} objects that are at the start of a {@link Link} 
     * that is attached to one {@link InputSlot} of the given {@link Module}, 
     * and all predecessors of these modules.
     * 
     * @param module The {@link Module}
     * @return The set of all successors
     */
    public static Set<Module> computeAllPredecessors(Module module)
    {
        Set<Module> allPredecessors = new LinkedHashSet<Module>();
        computeAllPredecessors(Collections.singleton(module), allPredecessors);
        return allPredecessors;
    }
    
    /**
     * Recursively compute all predecessors of the given collection of 
     * {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     * @param result The collection that will store the result
     */
    private static void computeAllPredecessors(
        Iterable<? extends Module> modules, Collection<Module> result)
    {
        for (Module module : modules)
        {
            Set<Module> predecessors = computePredecessors(module);
            result.addAll(predecessors);
            computeAllPredecessors(predecessors, result);
        }
    }
    
    /**
     * Returns an unmodifiable set containing all {@link Link} objects that
     * are connected to the given {@link Module}
     * 
     * @param module The {@link Module}
     * @return The set of {@link Link} objects
     */
    public static Set<Link> computeLinks(Module module)
    {
        return ModuleUtils.computeLinks(Arrays.asList(module));
    }

    /**
     * Returns an unmodifiable set containing all {@link Link} objects that
     * are connected to any the given {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     * @return The set of {@link Link} objects
     */
    public static Set<Link> computeLinks(Iterable<? extends Module> modules)
    {
        Set<Link> links = new LinkedHashSet<Link>();
        links.addAll(computeIncomingLinks(modules));
        links.addAll(computeOutgoingLinks(modules));
        return Collections.unmodifiableSet(links);
    }

    /**
     * Returns an unmodifiable set containing all {@link Link} objects that
     * are connected to any of the {@link InputSlot} objects of the given 
     * {@link Module}
     * 
     * @param module The {@link Module}
     * @return The set of {@link Link} objects
     */
    public static Set<Link> computeIncomingLinks(Module module)
    {
        Set<Link> links = new LinkedHashSet<Link>();
        for (InputSlot inputSlot : module.getInputSlots())
        {
            Link inputLink = inputSlot.getInputLink();
            if (inputLink != null)
            {
                links.add(inputLink);
            }
        }
        return Collections.unmodifiableSet(links);
    }

    /**
     * Returns an unmodifiable set containing all {@link Link} objects that
     * are connected to any of the {@link InputSlot} objects of the given 
     * {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     * @return The set of {@link Link} objects
     */
    public static Set<Link> computeIncomingLinks(
        Iterable<? extends Module> modules)
    {
        Set<Link> links = new LinkedHashSet<Link>();
        for (Module module : modules)
        {
            links.addAll(computeIncomingLinks(module));
        }
        return Collections.unmodifiableSet(links);
    }

    /**
     * Returns an unmodifiable set containing all {@link Link} objects that
     * are connected to any of the {@link OutputSlot} objects of the given 
     * {@link Module}
     * 
     * @param module The {@link Module}
     * @return The set of {@link Link} objects
     */
    public static Set<Link> computeOutgoingLinks(Module module)
    {
        Set<Link> links = new LinkedHashSet<Link>();
        for (OutputSlot outputSlot : module.getOutputSlots())
        {
            links.addAll(outputSlot.getOutputLinks());
        }
        return Collections.unmodifiableSet(links);
    }

    /**
     * Returns an unmodifiable set containing all {@link Link} objects that
     * are connected to any of the {@link OutputSlot} objects of the given 
     * {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     * @return The set of {@link Link} objects
     */
    public static Set<Link> computeOutgoingLinks(
        Iterable<? extends Module> modules)
    {
        Set<Link> links = new LinkedHashSet<Link>();
        for (Module module : modules)
        {
            links.addAll(computeOutgoingLinks(module));
        }
        return Collections.unmodifiableSet(links);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private ModuleUtils()
    {
        // Private constructor to prevent instantiation
    }
    
}
