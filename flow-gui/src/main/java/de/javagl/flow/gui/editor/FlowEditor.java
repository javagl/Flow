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
package de.javagl.flow.gui.editor;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import de.javagl.common.beans.PropertyChangeUtils;
import de.javagl.flow.Flow;
import de.javagl.flow.MutableFlow;
import de.javagl.flow.link.Link;
import de.javagl.flow.link.Links;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleUtils;
import de.javagl.flow.workspace.FlowLayout;
import de.javagl.flow.workspace.FlowWorkspace;
import de.javagl.flow.workspace.GeomUtils;
import de.javagl.flow.workspace.MutableFlowLayout;
import de.javagl.flow.workspace.MutableFlowWorkspace;
import de.javagl.selection.SelectionModel;

/**
 * An editor for a {@link Flow}. The editable state of the {@link Flow} is
 * summarized in a {@link MutableFlowWorkspace}. This class offers methods
 * to manipulate the state of the {@link MutableFlowWorkspace} and generate
 * UndoableEdits for these editing operations. 
 */
public final class FlowEditor
{
    /**
     * The {@link FlowWorkspace} that is edited
     */
    private final MutableFlowWorkspace flowWorkspace;
    
    /**
     * The list of UndoableEditListeners. At least one of these
     * listeners will usually delegate the UndoableEdits to the 
     * UndoManager
     */
    private final List<UndoableEditListener> undoableEditListeners;
    
    /**
     * A PropertyChangeListener that will be added to the 
     * {@link Module#getConfiguration() configuration} of
     * each {@link Module}, and forward property changes in
     * form of UndoableEdits to the UndoableEditListeners
     */
    private final PropertyChangeListener 
        moduleConfigurationPropertyChangeListener = new PropertyChangeListener()
    {
        /**
         * A flag storing whether an undo/redo is currently performed.
         * This flag will be set to <code>true</code> while the 
         * {@link PropertyChangeEdit} is performing its undo/redo.
         * This PropertyChangeListener will ignore all events that
         * happen while this flag is set. 
         */
        private final AtomicBoolean undoRedoFlag = new AtomicBoolean();
        
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent)
        {
            if (!undoRedoFlag.get())
            {
                UndoableEdit edit = new PropertyChangeEdit(
                    undoRedoFlag, propertyChangeEvent);
                notifyUndoableEdit(edit);
            }
        }
    };

    /**
     * Creates an editor for the given {@link MutableFlowWorkspace}
     * 
     * @param flowWorkspace The {@link MutableFlowWorkspace}
     */
    public FlowEditor(MutableFlowWorkspace flowWorkspace)
    {
        this.flowWorkspace = flowWorkspace;
        
        this.undoableEditListeners = 
            new CopyOnWriteArrayList<UndoableEditListener>();     

        Flow flow = flowWorkspace.getFlow();
        FlowLayout flowLayout = flowWorkspace.getFlowLayout();
        for (Module module : flow.getModules())
        {
            Rectangle2D bounds = 
                flowLayout.getBounds(module);
            addModuleInternal(module, bounds);
        }
    }
    
    /**
     * Returns the {@link FlowWorkspace} that describes the editable
     * state of this editor
     * 
     * @return The {@link FlowWorkspace}
     */
    public FlowWorkspace getFlowWorkspace()
    {
        return flowWorkspace;
    }
    
    /**
     * Returns the {@link Flow} that is edited 
     * 
     * @return The edited {@link Flow}
     */
    public Flow getFlow()
    {
        return flowWorkspace.getFlow();
    }
    
    /**
     * Returns the {@link FlowLayout} that is edited 
     * 
     * @return The edited {@link FlowLayout}
     */
    private FlowLayout getFlowLayout()
    {
        return flowWorkspace.getFlowLayout();
    }
    
    
    /**
     * Returns the {@link SelectionModel} that stores which {@link Module} 
     * objects currently selected in this editor.
     * 
     * @return The {@link SelectionModel} of the {@link Module} objects
     */
    public SelectionModel<Module> getModuleSelectionModel()
    {
        return flowWorkspace.getModuleSelectionModel();
    }

    /**
     * Returns the {@link SelectionModel} that stores which {@link Link} 
     * objects currently selected in this editor.
     * 
     * @return The {@link SelectionModel} of the {@link Link} objects
     */
    private SelectionModel<Link> getLinkSelectionModel()
    {
        return flowWorkspace.getLinkSelectionModel();
    }

    /**
     * Returns whether the given {@link Module} is currently selected
     * 
     * @param module The {@link Module}
     * @return Whether the given {@link Module} is currently selected
     */
    public boolean isModuleSelected(Module module)
    {
        return getModuleSelectionModel().isSelected(module);
    }
    
    /**
     * Returns a new, unmodifiable set of {@link Module} objects 
     * that are currently selected.
     * 
     * @return The currently selected {@link Module} objects
     */
    public Set<Module> getSelectedModules()
    {
        return Collections.unmodifiableSet(new LinkedHashSet<Module>(
            getModuleSelectionModel().getSelection()));
    }

    
    /**
     * Returns whether the given {@link Link} is currently selected
     * 
     * @param link The {@link Link}
     * @return Whether the given {@link Link} is currently selected
     */
    boolean isLinkSelected(Link link)
    {
        return getLinkSelectionModel().isSelected(link);
    }
    
    /**
     * Returns a new, unmodifiable set of {@link Link} objects 
     * that are currently selected.
     * 
     * @return The currently selected {@link Link} objects
     */
    public Set<Link> getSelectedLinks()
    {
        return Collections.unmodifiableSet(new LinkedHashSet<Link>(
            getLinkSelectionModel().getSelection()));
    }
    
    /**
     * Returns the current bounds of the given {@link Module},
     * in world coordinates, or <code>null</code> if the given 
     * {@link Module} is not contained in the {@link Flow}
     * of this editor. 
     * 
     * @param module The {@link Module}
     * @return The bounds
     */
    public Rectangle2D getModuleBounds(Module module)
    {
        return getFlowLayout().getBounds(module);
    }
    

    /**
     * Add the {@link #moduleConfigurationPropertyChangeListener} to 
     * the configuration of the given {@link Module}.
     * 
     * @param module The {@link Module} 
     */
    private void addModuleConfigurationListener(Module module)
    {
        Object configuration = module.getConfiguration();
        if (configuration != null)
        {
            PropertyChangeUtils.tryAddPropertyChangeListenerUnchecked(
                configuration, moduleConfigurationPropertyChangeListener);
        }
    }
    
    
    /**
     * Remove the {@link #moduleConfigurationPropertyChangeListener} from 
     * the configuration of the given {@link Module}.
     * 
     * @param module The {@link Module} 
     */
    private void removeModuleConfigurationListener(Module module)
    {
        Object configuration = module.getConfiguration();
        if (configuration != null)
        {
            PropertyChangeUtils.tryRemovePropertyChangeListenerUnchecked(
                configuration, moduleConfigurationPropertyChangeListener);
        }
    }
    
    
    
    //=========================================================================
    // The following methods are the main editing commands. When they are
    // called, they will cause an UndoableEdit to be created and passed to
    // the UndoableEditListeners.
    // Below there are '...Internal' counterparts of these methods. These
    // 'Internal' methods are actually performing the work and creating
    // the UndoableEdits, but not passing them to the UndoableEditListeners.
    // Thus, when the UndoableEdits should be undone, they will call the
    // 'Internal' methods (avoiding to create new UndoableEdits, that are
    // just the opposite of the ones undone)
    
    /**
     * Change the selection of {@link Module} objects and {@link Link} objects
     * 
     * @param modulesAdded The {@link Module} objects added to the selection
     * @param modulesRemoved The {@link Module} objects removed 
     * from the selection
     * @param linksAdded The {@link Link} objects added to the selection
     * @param linksRemoved The {@link Link} objects removed 
     * from the selection
     */
    public void changeSelection(
        Set<Module> modulesAdded, Set<Module> modulesRemoved,
        Set<Link> linksAdded, Set<Link> linksRemoved)
    {
        CompoundEdit edit = new CompoundEdit();
        
        UndoableEdit edit0 = changeModuleSelectionInternal(
            modulesAdded, modulesRemoved);
        UndoableEdit edit1 = changeLinkSelectionInternal(
            linksAdded, linksRemoved);
        
        if (edit0 != null)
        {
            edit.addEdit(edit0);
        }
        if (edit1 != null)
        {
            edit.addEdit(edit1);
        }
        if (edit0 != null || edit1 != null)
        {
            edit.end();
            notifyUndoableEdit(edit);
        }
    }
     
    /**
     * Change the bounds of the {@link Module} objects according to the given 
     * map
     * 
     * @param newBounds The new bounds
     */
    public void changeModuleLayout(Map<Module, Rectangle2D> newBounds)
    {
        UndoableEdit edit = changeModuleLayoutInternal(newBounds);
        if (edit != null)
        {
            notifyUndoableEdit(edit);
        }
    }

    /**
     * Change the bounds of the {@link Module} objects according to the given 
     * map, <b>without</b> causing an undoable edit. 
     * 
     * @param newBounds The new bounds
     */
    public void changeModuleLayoutAutomatically(
        Map<Module, Rectangle2D> newBounds)
    {
        changeModuleLayoutInternal(newBounds);
    }

    /**
     * Move the given {@link Module} objects by the given delta
     * 
     * @param modules The {@link Module} objects
     * @param delta The movement
     */
    public void moveModules(Set<Module> modules, Point2D delta)
    {
        Map<Module, Rectangle2D> newBounds = 
            new LinkedHashMap<Module, Rectangle2D>();
        for (Module module : modules)
        {
            Rectangle2D bounds = getModuleBounds(module);
            
            //System.out.println("FlowEditor#moveModules:");
            //System.out.println("    Bounds of "+module);
            //System.out.println("         from "+bounds);

            bounds.setRect(
                bounds.getX() + delta.getX(),
                bounds.getY() + delta.getY(),
                bounds.getWidth(),
                bounds.getHeight());
            
            //System.out.println("           to "+bounds);
            
            newBounds.put(module, bounds);
        }
        changeModuleLayout(newBounds);
    }
    

    /**
     * Add the given {@link ModuleInfo} with the given bounds
     * 
     * @param module The {@link Module}
     * @param bounds The bounds
     */
    public void addModule(Module module, Rectangle2D bounds)
    {
        UndoableEdit edit = addModuleInternal(module, bounds);
        notifyUndoableEdit(edit);
    }
    
    /**
     * Add the given {@link Link} to the currently edited 
     * {@link MutableFlow}.
     * 
     * @param link The {@link Link} to add
     */
    public void addLink(Link link)
    {
        UndoableEdit edit = addLinkInternal(link);
        notifyUndoableEdit(edit);
    }
    
    /**
     * Remove the given {@link Link} from the currently edited 
     * {@link MutableFlow}.
     * 
     * @param link The {@link Link} to remove
     */
    public void removeLink(Link link)
    {
        UndoableEdit edit = removeLinkInternal(link);
        notifyUndoableEdit(edit);
    }

    /**
     * Delete all selected {@link Module} objects and {@link Link} objects
     */
    public void deleteSelected()
    {
        delete(getSelectedModules(), getSelectedLinks());
    }
    
    /**
     * Delete the given {@link Module} objects and {@link Link} objects, and
     * all {@link Link} objects that are connected to one of the 
     * deleted {@link Module} objects
     * 
     * @param deletedModules The {@link Module} objects to delete
     * @param deletedLinks The {@link Link} objects to delete
     */
    public void delete(Set<Module> deletedModules, Set<Link> deletedLinks)
    {
        // Compute the outgoing links that have NOT been deleted explicitly,
        // but will be deleted anyhow, because they belong to 
        // modules that are deleted 
        Set<Link> actuallyDeletedOutgoingLinks = new LinkedHashSet<Link>();
        actuallyDeletedOutgoingLinks.addAll(
            ModuleUtils.computeOutgoingLinks(deletedModules));
        actuallyDeletedOutgoingLinks.removeAll(deletedLinks);
        
        // Compute the incoming links that have NOT been deleted explicitly,
        // but will be deleted anyhow, because they belong to 
        // modules that are deleted 
        Set<Link> actuallyDeletedIncomingLinks = new LinkedHashSet<Link>();
        actuallyDeletedIncomingLinks.addAll(
            ModuleUtils.computeIncomingLinks(deletedModules));
        actuallyDeletedIncomingLinks.removeAll(deletedLinks);
        
        CompoundEdit edit = new CompoundEdit();
        for (Link link : actuallyDeletedOutgoingLinks)
        {
            edit.addEdit(removeLinkInternal(link));
        }
        for (Link link : actuallyDeletedIncomingLinks)
        {
            edit.addEdit(removeLinkInternal(link));
        }
        for (Link link : deletedLinks)
        {
            edit.addEdit(removeLinkInternal(link));
        }
        for (Module module : deletedModules)
        {
            edit.addEdit(removeModuleInternal(module));
        }
        edit.end();
        notifyUndoableEdit(edit);
    }
    
    
    
    
    //=========================================================================
    // The following methods are the internal handling of the main editing 
    // commands. When they are called, they will create and return an
    // UndoableEdit. The caller may either ignore these edits (in case they
    // are called due to an undo/redo itself) or pass them on to the 
    // UndoableEditListeners.
    
    /**
     * Add the given {@link Module} with the given bounds to the
     * currently edited {@link MutableFlow}.
     * 
     * @param module The {@link Module} to add
     * @param bounds The bounds where to add the module
     * @return The UndoableEdit describing the modification
     */
    UndoableEdit addModuleInternal(Module module, Rectangle2D bounds)
    {
        MutableFlow flow = flowWorkspace.getFlow();
        MutableFlowLayout flowLayout = flowWorkspace.getFlowLayout();
        flow.addModule(module);
        flowLayout.setBounds(module, bounds);
        
        addModuleConfigurationListener(module);
        
        UndoableEdit edit = 
            new ModuleAddEdit(this, module, GeomUtils.copy(bounds));
        return edit;
    }
    
    
    
    
    /**
     * Remove the given {@link Module} from the currently edited 
     * {@link MutableFlow}.
     * 
     * @param module The {@link Module} to remove
     * @return The UndoableEdit describing the modification
     */
    UndoableEdit removeModuleInternal(Module module)
    {
        CompoundEdit edit = new CompoundEdit();

        UndoableEdit linkUnselectionEdit = changeLinkSelectionInternal(
            Collections.emptySet(), ModuleUtils.computeLinks(module));
        if (linkUnselectionEdit != null)
        {
            edit.addEdit(linkUnselectionEdit);
        }
        
        UndoableEdit moduleUnselectionEdit = changeModuleSelectionInternal(
            Collections.emptySet(), Collections.singleton(module));            
        if (moduleUnselectionEdit != null)
        {
            edit.addEdit(moduleUnselectionEdit);
        }
        
        MutableFlow flow = flowWorkspace.getFlow();
        MutableFlowLayout flowLayout = flowWorkspace.getFlowLayout();
        flow.removeModule(module);
        Rectangle2D bounds = flowLayout.setBounds(module, null);

        removeModuleConfigurationListener(module);
        
        UndoableEdit deletionEdit = 
            new ModuleDeletionEdit(this, module, bounds);
        edit.addEdit(deletionEdit);
        edit.end();
        
        return edit;
    }

    /**
     * Add the given {@link Link} to the currently edited 
     * {@link MutableFlow}.
     * 
     * @param link The {@link Link} to add
     * @return The UndoableEdit describing the modification
     */
    UndoableEdit addLinkInternal(Link link)
    {
        MutableFlow flow = flowWorkspace.getFlow();
        flow.addLink(link);
        UndoableEdit edit = new LinkAddEdit(this, link);
        return edit;
    }

    /**
     * Remove the given {@link Link} from the currently edited 
     * {@link MutableFlow}.
     *  
     * @param link The {@link Link} to remove
     * @return The UndoableEdit describing the modification
     */
    UndoableEdit removeLinkInternal(Link link)
    {
        CompoundEdit edit = new CompoundEdit();
        
        UndoableEdit unselectionEdit = changeLinkSelectionInternal(
            Collections.emptySet(), Collections.singleton(link));
        if (unselectionEdit != null)
        {
            edit.addEdit(unselectionEdit);
        }
        
        MutableFlow flow = flowWorkspace.getFlow();
        flow.removeLink(link);
        
        UndoableEdit deletionEdit = new LinkDeletionEdit(this, link);
        edit.addEdit(deletionEdit);
        edit.end();
        
        return edit;
    }
    
    /**
     * Sets the bounds of the {@link Module} objects according to the given map  
     * 
     * @param newWorldBounds The new bounds, in world coordinates
     * @return The UndoableEdit describing the modification, or 
     * <code>null</code> if the given map is is empty
     * or no bounds have changed
     */
    UndoableEdit changeModuleLayoutInternal(
        Map<Module, Rectangle2D> newWorldBounds)
    {
        if (newWorldBounds.isEmpty())
        {
            return null;
        }
        boolean changed = false;
        MutableFlowLayout flowLayout = flowWorkspace.getFlowLayout();
        Map<Module, Rectangle2D> oldWorldBounds = 
            new LinkedHashMap<Module, Rectangle2D>();
        for (Module module : newWorldBounds.keySet())
        {
            Rectangle2D oldWorldBound = flowLayout.getBounds(module);
            oldWorldBounds.put(module, oldWorldBound);
            Rectangle2D newWorldBound = newWorldBounds.get(module);
            if (!oldWorldBound.equals(newWorldBound))
            {
                changed = true;
                flowLayout.setBounds(module, newWorldBound);
            }
        }
        if (!changed)
        {
            return null;
        }
        UndoableEdit edit = new ModuleLayoutEdit(
            this, oldWorldBounds, newWorldBounds);
        return edit;
        
    }
    
    
    
    /**
     * Change the set of currently selected {@link Module} objects according
     * to the given sets that should be added to or removed from the
     * selection
     * 
     * @param modulesAdded The {@link Module} objects to add to the selection
     * @param modulesRemoved The {@link Module} objects to remove 
     * from the selection
     * @return The UndoableEdit describing the modification, or 
     * <code>null</code> if the intended operation did not change the
     * current selection
     */
    UndoableEdit changeModuleSelectionInternal(
        Set<Module> modulesAdded, Set<Module> modulesRemoved)
    {
        boolean changed = false;
        SelectionModel<Module> moduleSelectionModel =
            flowWorkspace.getModuleSelectionModel();
        Set<Module> selection = moduleSelectionModel.getSelection();
        changed |= !selection.containsAll(modulesAdded);
        changed |= !containsNone(selection, modulesRemoved);
        if (changed)
        {
            moduleSelectionModel.addToSelection(modulesAdded);
            moduleSelectionModel.removeFromSelection(modulesRemoved);
            UndoableEdit edit = new ModuleSelectionChangeEdit(this, 
                modulesAdded, modulesRemoved);
            return edit;
        }
        return null;
    }
    
    /**
     * Returns whether the given set contains none of the objects contained
     * in the other set
     * 
     * @param containing The (possibly) containing set
     * @param other The other set
     * @return Whether the given set contains none of the object from the other
     */
    private static boolean containsNone(Set<?> containing, Set<?> other)
    {
        for (Object object : other)
        {
            if (containing.contains(object))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Change the set of currently selected {@link Links} objects according
     * to the given sets that should be added to or removed from the
     * selection
     * 
     * @param linksAdded The {@link Link} objects to add to the selection
     * @param linksRemoved The {@link Link} objects to remove 
     * from the selection
     * @return The UndoableEdit describing the modification, or 
     * <code>null</code> if the intended operation did not change the
     * current selection
     */
    UndoableEdit changeLinkSelectionInternal(
        Set<Link> linksAdded, Set<Link> linksRemoved)
    {
        boolean changed = false;
        SelectionModel<Link> linkSelectionModel =
            flowWorkspace.getLinkSelectionModel();
        Set<Link> selection = linkSelectionModel.getSelection();
        changed |= !selection.containsAll(linksAdded);
        changed |= !containsNone(selection, linksRemoved);
        if (changed)
        {
            linkSelectionModel.addToSelection(linksAdded);
            linkSelectionModel.removeFromSelection(linksRemoved);
            UndoableEdit edit = new LinkSelectionChangeEdit(this, 
                linksAdded, linksRemoved);
            return edit;
        }
        return null;
    }
    
    
    /**
     * Add the given UndoableEditListener to be informed about 
     * UndoableEdits that happen in this editor
     * 
     * @param undoableEditListener The listener to add
     */
    public void addUndoableEditListener(
        UndoableEditListener undoableEditListener)
    {
        undoableEditListeners.add(undoableEditListener);
    }
    
    /**
     * Remove the given UndoableEditListener 
     * 
     * @param undoableEditListener The listener to remove
     */
    public void removeUndoableEditListener(
        UndoableEditListener undoableEditListener)
    {
        undoableEditListeners.add(undoableEditListener);
    }
    
    /**
     * Notify all registered UndoableEditListeners that the given
     * UndoableEdit happened
     * 
     * @param edit The UndoableEdit that happened
     */
    private void notifyUndoableEdit(UndoableEdit edit)
    {
        UndoableEditEvent event = new UndoableEditEvent(this, edit);
        for (UndoableEditListener undoableEditListener : undoableEditListeners)
        {
            undoableEditListener.undoableEditHappened(event);
        }
    }
    
    
    
}
