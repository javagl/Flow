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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import de.javagl.flow.gui.FlowEditorApplication;

/**
 * An UndoableEditListener that is attached to a {@link FlowEditor} and
 * manages the undo/redo for the {@link FlowEditorApplication}
 */
public final class FlowEditorUndoManager implements UndoableEditListener
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(FlowEditorUndoManager.class.getName());

    /**
     * The UndoManager that manages the UndoableEdits
     */
    private final UndoManager undoManager = new UndoManager()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -3185648953915105803L;
        
        /*
         * Implementation note: These methods are overridden solely for logging
         */
        
        @Override
        public synchronized void undo() throws CannotUndoException 
        {
            UndoableEdit edit = super.editToBeUndone();
            logger.info("Undo " + edit);
            super.undo();
            printEdits();
        }
        
        @Override
        public synchronized void redo() throws CannotRedoException 
        {
            UndoableEdit edit = super.editToBeRedone();
            logger.info("Redo " + edit);
            super.redo();
            printEdits();
        }
        
        @Override
        public void undoableEditHappened(UndoableEditEvent e) 
        {
            logger.info("UndoableEditEvent " + e);
            super.undoableEditHappened(e);
            printEdits();
        }
        
        /**
         * Print all edits that are currently contained in this UndoManager
         */
        private void printEdits()
        {
            boolean doPrint = false;
            //doPrint = true;
            if (doPrint)
            {
                logger.info("Edits now:");
                for (Object object : edits)
                {
                    logger.info("    " + object);
                }
            }
        }
        
    };
    
    /**
     * The Action that operates on the UndoManager (together with
     * the RedoAction) and allows undoing the previous operation
     */
    private final class UndoAction extends AbstractAction
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = 8350732328472038217L;

        /**
         * Creates the UndoAction
         */
        private UndoAction()
        {
            putValue(NAME, undoManager.getUndoPresentationName());
            putValue(SHORT_DESCRIPTION, "Undo the previous edit");
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
            putValue(ACCELERATOR_KEY, undoKeyStroke);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (undoManager.canUndo())
            {
                undoManager.undo();
            }
            updateUndoState();
            redoAction.updateRedoState();            
        }

        /**
         * Update this action depending on the availability
         * of an undo and the name of the undo
         */
        private void updateUndoState() 
        {
            setEnabled(undoManager.canUndo());
            putValue(Action.NAME, undoManager.getUndoPresentationName());
        }        
    }

    /**
     * The Action that operates on the UndoManager (together with the
     * UndoAction) and allows redoing the previously undone operation
     */
    private final class RedoAction extends AbstractAction
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = 8134150418397144411L;

        /**
         * Creates the RedoAction
         */
        private RedoAction()
        {
            putValue(NAME, undoManager.getRedoPresentationName());
            putValue(SHORT_DESCRIPTION, "Redo the previous edit");
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
            putValue(ACCELERATOR_KEY, redoKeyStroke);
            setEnabled(false);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (undoManager.canRedo())
            {
                undoManager.redo();
            }
            updateRedoState();
            undoAction.updateUndoState();
        }
        
        /**
         * Update this action depending on the availability
         * of a redo and the name of the redo
         */
        private void updateRedoState() 
        {
            setEnabled(undoManager.canRedo());
            putValue(Action.NAME, undoManager.getRedoPresentationName());
        }        
    }
    
    /**
     * The Keystroke triggering the {@link UndoAction}
     */
    private final KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(
        KeyEvent.VK_Z, InputEvent.CTRL_MASK);
    
    /**
     * The Keystroke triggering the {@link RedoAction}
     */
    private final KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(
        KeyEvent.VK_Y, InputEvent.CTRL_MASK);
    
    /**
     * The {@link UndoAction}
     */
    private final UndoAction undoAction = new UndoAction();

    /**
     * The {@link RedoAction}
     */
    private final RedoAction redoAction = new RedoAction();
    


    @Override
    public void undoableEditHappened(UndoableEditEvent e) 
    {
        undoManager.addEdit(e.getEdit());
        undoAction.updateUndoState();
        redoAction.updateRedoState();
    }

    /**
     * Returns the KeyStroke that triggers the Undo action
     * 
     * @return The KeyStroke that triggers the Undo action
     */
    public KeyStroke getUndoKeyStroke()
    {
        return undoKeyStroke;
    }

    /**
     * Returns the KeyStroke that triggers the Redo action
     * 
     * @return The KeyStroke that triggers the Redo action
     */
    public KeyStroke getRedoKeyStroke()
    {
        return redoKeyStroke;
    }

    /**
     * Returns the Undo action
     * 
     * @return The Undo action
     */
    public Action getUndoAction()
    {
        return undoAction;
    }

    /**
     * Returns the Undo action
     * 
     * @return The Undo action
     */
    public Action getRedoAction()
    {
        return redoAction;
    }

    /**
     * Discard all edits and update the states of the undo- and redo actions
     */
    public void reset()
    {
        undoManager.discardAllEdits();
        undoAction.updateUndoState();
        redoAction.updateRedoState();
    }

}
