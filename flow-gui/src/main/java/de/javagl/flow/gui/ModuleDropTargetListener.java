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
package de.javagl.flow.gui;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.workspace.GeomUtils;

/**
 * Implementation of a DropTargetListener that will handle dropping a 
 * {@link Module} on a {@link FlowEditorComponent}, by calling 
 * {@link FlowEditor#addModule(Module, Rectangle2D)}
 */
final class ModuleDropTargetListener implements DropTargetListener
{
    /**
     * The {@link FlowEditorComponent} to which the {@link ModuleCreator}
     * will be dropped
     */
    private final FlowEditorComponent flowEditorComponent;

    /**
     * Creates a drop target listener for the given 
     * {@link FlowEditorComponent}
     * 
     * @param flowEditorComponent The {@link FlowEditorComponent}
     */
    ModuleDropTargetListener(
        FlowEditorComponent flowEditorComponent)
    {
        this.flowEditorComponent = flowEditorComponent;
        
        @SuppressWarnings("unused")
        DropTarget dropTarget = new DropTarget(
            flowEditorComponent, this);
    }

    @Override
    public void drop(DropTargetDropEvent e)
    {
        Transferable transferable = e.getTransferable();

        if (transferable.isDataFlavorSupported(
            TransferableModule.MODULE_FLAVOR))
        {
            try
            {
                Module module = (Module)transferable.getTransferData(
                    TransferableModule.MODULE_FLAVOR);
                Point location = e.getLocation();
                performDrop(module, location);
                e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            }
            catch (UnsupportedFlavorException ex)
            {
                e.rejectDrop();
            }
            catch (IOException ex)
            {
                e.rejectDrop();
            }
        }
        else
        {
            e.rejectDrop();
        }
        e.getDropTargetContext().dropComplete(true);
    }

    /**
     * Performs the actual drop operation of the given {@link Module}
     * for the given location
     * 
     * @param module The {@link Module}
     * @param location The drop location
     */
    private void performDrop(Module module, Point location)
    {
        //System.out.println("Drop at "+location);

        flowEditorComponent.getDragPanel().setDropPreviewFor(null, null);
        
        // TODO This ModuleComponent is solely created for computing the bounds 
        // that the module should have. Is there any better way to do this?
        // The bounds are required for the layout, and when the modules are
        // read from a file they are already given. But in order to compute
        // them from a drop-point, a ModuleComponent HAS to be created, 
        // otherwise the action of adding a Module via the editor can not
        // be performed in the abstract (and not-GUI-related) way.
        ModuleComponent boundsModuleComponent =
            flowEditorComponent.createModuleComponent(module);
        Point centerPosition = new Point(
            location.x - flowEditorComponent.getWidth() / 2,
            location.y - flowEditorComponent.getHeight() / 2);
        Rectangle2D defaultBounds = boundsModuleComponent.getBounds();
        Rectangle2D bounds =
            GeomUtils.moveTo(defaultBounds, centerPosition);
        boundsModuleComponent.setModule(null); // Detach listeners
        
        FlowEditor flowEditor = flowEditorComponent.getFlowEditor();
        flowEditor.addModule(module, bounds);
    }

    
    
    @Override
    public void dragEnter(DropTargetDragEvent e)
    {
        // Not used
    }

    @Override
    public void dragOver(DropTargetDragEvent e)
    {
        try
        {
            Transferable transferable = e.getTransferable();
            Module module = (Module)transferable.getTransferData(
                TransferableModule.MODULE_FLAVOR);
            flowEditorComponent.getDragPanel().setDropPreviewFor(
                module, e.getLocation());
        } 
        catch (UnsupportedFlavorException ex)
        {
            // Ignored
        } 
        catch (IOException ex)
        {
            // Ignored
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent e)
    {
        // Not used
    }

    @Override
    public void dragExit(DropTargetEvent e)
    {
        flowEditorComponent.getDragPanel().setDropPreviewFor(null, null);
    }
}
