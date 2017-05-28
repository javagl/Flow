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

import java.beans.PropertyChangeEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import de.javagl.common.beans.BeanUtils;


/**
 * Implementation of an UndoableEdit that corresponds to a 
 * PropertyChangeEvent
 */
final class PropertyChangeEdit 
    extends AbstractUndoableEdit 
    implements UndoableEdit
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 1333674224011018707L;
    
    /**
     * The flag that will be set while performing an undo or redo
     */
    private final AtomicBoolean undoRedoFlag;
    
    /**
     * A copy of the event that caused this edit
     */
    private final PropertyChangeEvent propertyChangeEvent;
    
    /**
     * Creates a new property change edit
     * 
     * @param undoRedoFlag The flag that will be set while performing
     * an undo or redo
     * @param propertyChangeEvent The property change event. A copy of this
     * event will be stored. 
     */
    PropertyChangeEdit(AtomicBoolean undoRedoFlag,
        PropertyChangeEvent propertyChangeEvent)
    {
        this.undoRedoFlag = undoRedoFlag;
        this.propertyChangeEvent = new PropertyChangeEvent(
            propertyChangeEvent.getSource(),
            propertyChangeEvent.getPropertyName(),
            propertyChangeEvent.getOldValue(),
            propertyChangeEvent.getNewValue());
    }

    @Override
    public void undo() throws CannotUndoException 
    {
        super.undo();
        undoRedoFlag.set(true);
        BeanUtils.invokeWriteMethodOptional(
            propertyChangeEvent.getSource(),
            propertyChangeEvent.getPropertyName(),
            propertyChangeEvent.getOldValue());
        undoRedoFlag.set(false);
    }

    @Override
    public void redo() throws CannotRedoException 
    {
        super.redo();
        undoRedoFlag.set(true);
        BeanUtils.invokeWriteMethodOptional(
            propertyChangeEvent.getSource(),
            propertyChangeEvent.getPropertyName(),
            propertyChangeEvent.getNewValue());
        undoRedoFlag.set(false);
    }
    
    @Override
    public String getPresentationName() 
    {
        String oldValueString = limitLength(
            String.valueOf(propertyChangeEvent.getOldValue()), 10);
        String newValueString = limitLength( 
            String.valueOf(propertyChangeEvent.getNewValue()), 10);
        return "Change " 
            + propertyChangeEvent.getPropertyName() + " in "
            + propertyChangeEvent.getSource() + " from "
            + oldValueString + " to "
            + newValueString;
    }
    
    /**
     * Limit the length of a string to the given maximum, appending "..."
     * if necessary, to indicate that it was truncated.
     * 
     * @param s The string
     * @param maxLength The maximum length
     * @return The limited string
     */
    private static String limitLength(String s, int maxLength)
    {
        if (s == null || s.length() <= maxLength)
        {
            return s;
        }
        return s.substring(0, maxLength) + "...";
    }
    
    @Override
    public String toString()
    {
        return "PropertyChangeEdit[" + propertyChangeEvent + "]";
    }

}
