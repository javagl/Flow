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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import de.javagl.flow.link.Link;

/**
 * Implementation of an UndoableEdit that describes the 
 * deletion of a {@link Link}
 */
final class LinkDeletionEdit 
    extends AbstractUndoableEdit 
    implements UndoableEdit
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -2573088626025341419L;

    /**
     * The {@link FlowEditor} that created this edit
     */
    private final FlowEditor flowEditor;
    
    /**
     * The {@link Link} that was deleted
     */
    private final Link link;
    
    /**
     * Creates a new deletion edit
     * 
     * @param flowEditor The {@link FlowEditor} 
     * that created this edit
     * @param link The {@link Link} that was deleted
     */
    LinkDeletionEdit(
        FlowEditor flowEditor, 
        Link link)
    {
        this.flowEditor = flowEditor;
        this.link = link;
    }

    @Override
    public void undo() throws CannotUndoException 
    {
        super.undo();
        flowEditor.addLinkInternal(link);
    }

    @Override
    public void redo() throws CannotRedoException 
    {
        super.redo();
        flowEditor.removeLinkInternal(link);
    }
    
    @Override
    public String getPresentationName() 
    {
        return "delete link";
    }    
    
    @Override
    public String toString()
    {
        return "LinkDeletionEdit[" + "link=" + link + "]";
    }

}
