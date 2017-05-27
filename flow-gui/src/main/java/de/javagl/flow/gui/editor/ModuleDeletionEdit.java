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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import de.javagl.flow.module.Module;

/**
 * Implementation of an UndoableEdit that describes the 
 * deletion of a {@link Module}
 */
final class ModuleDeletionEdit 
    extends AbstractUndoableEdit 
    implements UndoableEdit
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -2053950184248625462L;
    
    /**
     * The {@link FlowEditor} that created this edit
     */
    private final FlowEditor flowEditor;
    
    /**
     * The {@link Module} that was deleted
     */
    private final Module module;
    
    /**
     * The bounds of the {@link Module} that was deleted
     */
    private final Rectangle2D bounds;
    
    /**
     * Creates a new deletion edit. Will store a reference to
     * the given bounds.
     * 
     * @param flowEditor The {@link FlowEditor} 
     * that created this edit 
     * @param module The {@link Module} that was deleted
     * @param bounds The bounds of the {@link Module} that was deleted
     */
    ModuleDeletionEdit(FlowEditor flowEditor,
        Module module, Rectangle2D bounds)
    {
        this.flowEditor = flowEditor;
        this.module = module;
        this.bounds = bounds;
    }

    @Override
    public void undo() throws CannotUndoException 
    {
        super.undo();
        flowEditor.addModuleInternal(module, bounds);
    }

    @Override
    public void redo() throws CannotRedoException 
    {
        super.redo();
        flowEditor.removeModuleInternal(module);
    }
    
    @Override
    public String getPresentationName() 
    {
        return "delete modules";
    }    
    
    @Override
    public String toString()
    {
        return "ModuleDeletionEdit[" 
            + "module=" + module + "," 
            + "bounds=" + bounds + "]";
    }
}
