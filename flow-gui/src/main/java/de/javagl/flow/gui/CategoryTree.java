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

import java.awt.Component;
import java.awt.Font;
import java.util.function.Function;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.javagl.category.Category;
import de.javagl.category.CategoryEvent;
import de.javagl.category.CategoryListener;
import de.javagl.common.ui.JTrees;
import de.javagl.common.ui.tree.filtered.FilteredTree;
import de.javagl.common.ui.tree.filtered.TreeModelFilter;

/**
 * A class summarizing a (possibly filtered) JTree and its TreeModel
 * corresponding to a {@link Category}. This tree serves as a view
 * for the {@link Category}. That means that it will add a 
 * {@link CategoryListener} to its {@link Category}, so that changes 
 * in the {@link Category} will trigger an update in this tree.
 *  
 * @param <T> The type of the elements in the {@link Category} 
 */
final class CategoryTree<T>
{
    /**
     * Default implementation of a function that just
     * returns <code>String.valueOf(object)</code>.
     */
    private static final Function<Object, String> DEFAULT_TO_STRING_FUNCTION =
        object -> String.valueOf(object);
    
    /**
     * Returns a default implementation of a function that 
     * just returns <code>String.valueOf(object)</code>.
     * 
     * @return The default string function
     */
    private static Function<Object, String> defaultToStringFunction()
    {
        return DEFAULT_TO_STRING_FUNCTION;
    }
    
    /**
     * The default font that should be used in all trees
     */
    private static final Font DEFAULT_TREE_FONT = 
        new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    /**
     * The root category of this tree
     */
    private Category<T> rootCategory;
    
    /**
     * The tree 
     */
    private JTree tree;
    
    /**
     * The tree model
     */
    private DefaultTreeModel unfilteredTreeModel;
    
    /**
     * The {@link FilteredTree}   
     */
    private FilteredTree filteredTree;
    
    /**
     * The listener for the root {@link Category} that will cause a 
     * rebuild of the tree model when something in the root 
     * {@link Category} changes
     */
    private final CategoryListener<T> categoryListener = 
        new CategoryListener<T>()
    {
        @Override
        public void elementsAdded(CategoryEvent<T> event)
        {
            setCategory(rootCategory);
        }

        @Override
        public void elementsRemoved(CategoryEvent<T> event)
        {
            setCategory(rootCategory);
        }

        @Override
        public void childAdded(CategoryEvent<T> event)
        {
            setCategory(rootCategory);
        }

        @Override
        public void childRemoved(CategoryEvent<T> event)
        {
            setCategory(rootCategory);
        }
    };
    
    
    /**
     * Creates a tree for the given {@link Category}. It the given
     * category is <code>null</code>, then an empty tree will be
     * created.
     * 
     * @param category The root {@link Category}
     */
    CategoryTree(Category<T> category)
    {
        this(category, defaultToStringFunction());
    }
    
    /**
     * Creates a tree for the given {@link Category}. It the given
     * category is <code>null</code>, then an empty tree will be
     * created.
     * 
     * @param category The root {@link Category}
     * @param toStringFunction The function for creating the string 
     * representation of the objects
     */
    CategoryTree(Category<T> category, 
        Function<Object, String> toStringFunction)
    {
        unfilteredTreeModel = 
            new DefaultTreeModel(new DefaultMutableTreeNode());
        filteredTree = FilteredTree.create(unfilteredTreeModel);
        tree = filteredTree.getTree();
        tree.setCellRenderer(new DefaultTreeCellRenderer()
        {
            /**
             * Serial UID 
             */
            private static final long serialVersionUID = -5095969870680886825L;

            @Override
            public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean selected, 
                boolean expanded, boolean leaf, int row,
                boolean hasFocus)
            {
                super.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);
                setText(toStringFunction.apply(value));
                return this;
            }
        });
        tree.setFont(DEFAULT_TREE_FONT);
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCategory(category);
    }
    
    
    /**
     * Set the root {@link Category} that should be shown in this tree. 
     * 
     * @param category The root {@link Category}
     */
    void setCategory(Category<T> category)
    {
        if (this.rootCategory != null)
        {
            this.rootCategory.removeCategoryListener(categoryListener);
        }
        this.rootCategory = category;
        unfilteredTreeModel = new DefaultTreeModel(createNode(category));
        filteredTree.setInputModel(unfilteredTreeModel);
        if (this.rootCategory != null)
        {
            this.rootCategory.addCategoryListener(categoryListener);
        }
    }
    
    
    /**
     * Returns the tree
     * 
     * @return The tree
     */
    JTree getTree()
    {
        return tree;
    }
    
    /**
     * Set the {@link TreeModelFilter} to use
     * 
     * @param filter The {@link TreeModelFilter} to apply. If this is 
     * <code>null</code>, then the unfiltered tree will be shown.
     */
    void setFilter(TreeModelFilter filter)
    {
        filteredTree.setFilter(filter);
    }
    
    
    /**
     * Expand all nodes up to the given depth
     * 
     * @param depth The depth
     */
    void expandAll(int depth)
    {
        expandAll(tree, (TreeNode) tree.getModel().getRoot(), depth);
    }
    
    /**
     * Expand all nodes for the given depth in the given tree
     * 
     * @param tree The tree
     * @param node The current node
     * @param depth The depth
     */
    private static void expandAll(JTree tree, TreeNode node, int depth)
    {
        if (depth == 0)
        {
            
            TreePath path = JTrees.createTreePathToRoot(tree.getModel(), node);
            tree.expandPath(path);
        }
        else
        {
            int n = tree.getModel().getChildCount(node);
            for (int i=0; i<n; i++)
            {
                Object child = tree.getModel().getChild(node, i);
                expandAll(tree, (TreeNode) child, depth-1);
            }
        }
    }

    /**
     * Create a tree node representing the given {@link Category}
     * hierarchy. If the given {@link Category} is <code>null</code>,
     * then an empty tree node will be returned.
     * 
     * @param category The node of the {@link Category} hierarchy
     * @return The new node
     */
    private static MutableTreeNode createNode(Category<?> category)
    {
        if (category == null)
        {
            return new DefaultMutableTreeNode();
        }
        
        DefaultMutableTreeNode rootNode =
            new DefaultMutableTreeNode(category);
        
        for (Object element : category.getElements())
        {
            MutableTreeNode elementNode = 
                new DefaultMutableTreeNode(element);
            rootNode.add(elementNode);
        }

        for (Category<?> child : category.getChildren())
        {
            MutableTreeNode childNode = createNode(child);
            rootNode.add(childNode);
        }
        
        return rootNode;
    }

    /**
     * Debugging method
     * @param treeModel treeModel
     */
    @SuppressWarnings("unused")
    private static void print(TreeModel treeModel)
    {
        print(treeModel, treeModel.getRoot(), "");
    }
    
    /**
     * Debugging method
     * @param treeModel treeModel
     * @param node node
     * @param indent indent
     */
    private static void print(TreeModel treeModel, Object node, String indent)
    {
        System.out.println(indent+node);
        int n = treeModel.getChildCount(node);
        for (int i=0; i<n; i++)
        {
            Object child = treeModel.getChild(node, i);
            print(treeModel, child, indent+"    ");
        }
    }
    
}
