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

import java.awt.datatransfer.Transferable;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.creation.ModuleCreator;

/**
 * A TransferHandler for {@link Module} objects. An instance of this
 * class is assigned to a JTree that allows dragging {@link Module} 
 * objects for drag-and-drop operations. The tree contains 
 * {@link ModuleCreator} instances, which will create the {@link Module}
 * instances. These {@link Module} instances are wrapped into Transferable 
 * instances by this class. The Transferable is later consumed by 
 * a {@link ModuleDropTargetListener}.
 */
final class ModuleTransferHandler extends TransferHandler
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(ModuleTransferHandler.class.getName());
    
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -6404485349852032912L;

    @Override
    public Transferable createTransferable(JComponent component)
    {
        if (!(component instanceof JTree))
        {
            logger.severe("Source component is no JTree!");
            return null;
        }

        JTree tree = (JTree)component;
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null)
        {
            return null;
        }
        
        TreeNode lastNode = (TreeNode)selectionPath.getLastPathComponent();
        if (!(lastNode instanceof DefaultMutableTreeNode))
        {
            logger.severe("Source node is no DefaultMutableTreeNode!");
            return null;
        }

        DefaultMutableTreeNode n = (DefaultMutableTreeNode)lastNode;
        Object userObject = n.getUserObject();
        if (userObject instanceof ModuleCreator)
        {
            ModuleCreator moduleCreator = (ModuleCreator)userObject;
            Module module = moduleCreator.createModule();
            return new TransferableModule(module);
        }
        else
        {
            // Not severe. Just pressed an inner node.
            //logger.severe("User object is no ModuleCreator!");
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return COPY;
    }
    
}    


