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

import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import de.javagl.flow.module.Module;

/**
 * Implementation of an UndoableEdit that describes
 * the change of the layout of a set of {@link Module} objects
 */
final class ModuleLayoutEdit 
    extends AbstractUndoableEdit 
    implements UndoableEdit
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 5813944840624871379L;

    /**
     * The {@link FlowEditor} that created this edit
     */
    private final FlowEditor flowEditor;
    
    /**
     * The old bounds, in world coordinates
     */
    private final Map<Module, Rectangle2D> oldWorldBounds;

    /**
     * The new bounds, in world coordinates
     */
    private final Map<Module, Rectangle2D> newWorldBounds;
    
    /**
     * Creates a new movement edit. Will store references
     * to the given collections!
     * 
     * @param flowEditor The {@link FlowEditor} 
     * that created this edit
     * @param oldWorldBounds The old bounds, in world coordinates
     * @param newWorldBounds The new bounds, in world coordinates
     */
    ModuleLayoutEdit(FlowEditor flowEditor, 
        Map<Module, Rectangle2D> oldWorldBounds,
        Map<Module, Rectangle2D> newWorldBounds)
    {
        this.flowEditor = flowEditor;
        this.oldWorldBounds = oldWorldBounds;
        this.newWorldBounds = newWorldBounds;
    }

    @Override
    public void undo() throws CannotUndoException 
    {
        super.undo();
        flowEditor.changeModuleLayoutInternal(oldWorldBounds);
    }

    @Override
    public void redo() throws CannotRedoException 
    {
        super.redo();
        flowEditor.changeModuleLayoutInternal(newWorldBounds);
    }
    
    @Override
    public String getPresentationName() 
    {
        return "move modules";
    }    
    
    @Override
    public String toString()
    {
        return "ModuleLayoutEdit[" 
            + "oldWorldBounds=" + oldWorldBounds + ","
            + "newWorldBounds=" + newWorldBounds + "]";
    }
    
}
