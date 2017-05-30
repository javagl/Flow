/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.awt.Component;
import java.awt.Container;
import java.util.function.Consumer;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import weka.gui.GenericObjectEditor;

/**
 * A wrapper around the Weka GenericObjectEditor, exposing some of its
 * functionality to use it in the module view.<br>
 * <br>
 * Note: This is not very elegant. The GenericObjectEditor offers some
 * convenient functionality, and one could probably avoid this class
 * by analyzing the details of the GenericObjectEditor class, and
 * re-implementing the required functionality manually. 
 * But I have better things to do. This is not public, anyhow.
 */
class CustomGenericObjectEditor extends GenericObjectEditor
{
    /**
     * A consumer that will be called with every class name that is selected
     */
    private final Consumer<String> classNameConsumer;
    
    /**
     * Whether the OK-button should be removed
     */
    private final boolean removeOkButton;
    
    /**
     * Creates a new instance
     * 
     * @param classNameConsumer An optional consumer for the class names
     * that are selected in the tree that is created with {@link #createTree()}
     * @param removeOkButton Whether the OK-button should be removed
     */
    CustomGenericObjectEditor(
        Consumer<String> classNameConsumer, boolean removeOkButton)
    {
        this.classNameConsumer = classNameConsumer;
        this.removeOkButton = removeOkButton;
    }
    
    @Override
    public java.awt.Component getCustomEditor() 
    {
        // Hackily remove some unwanted buttons
        class CustomGOEPanel extends GOEPanel
        {
            void removeButtons()
            {
                Component[] components = getComponents();
                Container buttonPanel = 
                    (Container) components[components.length - 1];
                buttonPanel.remove(m_SaveBut);
                buttonPanel.remove(m_OpenBut);
                buttonPanel.remove(m_cancelBut);
                if (removeOkButton)
                {
                    buttonPanel.remove(m_okBut);
                }
            }
        }

        if (m_EditorComponent == null) {
            CustomGOEPanel result = new CustomGOEPanel();
            result.removeButtons();
            m_EditorComponent = result;
        }
        return m_EditorComponent;
    }
    
    
    /**
     * Create the tree for selecting the class names
     * 
     * @return The tree
     */
    JTree createTree()
    {
        JTree tree = createTree(m_ObjectNames);
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                GOETreeNode node =
                    (GOETreeNode) tree.getLastSelectedPathComponent();
                if (node == null)
                {
                    return;
                }
                if (node.isLeaf())
                {
                    String className =
                        getClassnameFromPath(tree.getSelectionPath());
                    if (classNameConsumer != null)
                    {
                        classNameConsumer.accept(className);
                    }
                    classSelected(className);
                }
            }
        });
        return tree;
    }
    
    // Public to be called also when the text in the text field is edited
    @Override
    public void classSelected(String className)
    {
        super.classSelected(className);
    }
    
    // Public to be called when building the category tree
    @Override
    public String getClassnameFromPath(TreePath path)
    {
        return super.getClassnameFromPath(path);
    }
}
