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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

/**
 * A class that shows the JTree of a {@link CategoryTree}, and contains
 * a text field that allows filtering the tree.
 */
final class FilterableCategoryTreePanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 1498221699951378914L;

    /**
     * Creates a new panel for the given {@link CategoryTree}
     * 
     * @param categoryTree The {@link CategoryTree}
     */
    FilterableCategoryTreePanel(CategoryTree<?> categoryTree)
    {
        super(new BorderLayout());

        JPanel filterPanel = createFilterPanel(categoryTree);
        add(filterPanel, BorderLayout.NORTH);

        JTree tree = categoryTree.getTree();
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the panel that contains the filter-text field
     * 
     * @param categoryTree The {@link CategoryTree}
     * @return The filter panel
     */
    private JPanel createFilterPanel(CategoryTree<?> categoryTree)
    {
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel("Filter: "), BorderLayout.WEST);
        
        final JTextField filterTextField = new JTextField();
        filterTextField.getDocument().addDocumentListener(
            new CategoryTreeFilterDocumentListener(categoryTree));
        filterPanel.add(filterTextField, BorderLayout.CENTER);
        
        JButton filterClearButton = new JButton(" x ");
        filterClearButton.setMargin(new Insets(0,0,0,0));
        filterClearButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                filterTextField.setText("");
            }
        });
        filterPanel.add(filterClearButton, BorderLayout.EAST);
        return filterPanel;
    }
    
    
}
