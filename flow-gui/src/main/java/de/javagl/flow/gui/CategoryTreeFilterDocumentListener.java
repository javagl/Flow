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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import de.javagl.common.ui.tree.filtered.TreeModelFilters;

/**
 * Implementation of a DocumentListener that uses the text of the document
 * for creating a filter for a {@link CategoryTree} 
 */
final class CategoryTreeFilterDocumentListener implements DocumentListener
{
    /**
     * The {@link CategoryTree} to which the filter is applied
     */
    private final CategoryTree<?> categoryTree;
    
    /**
     * Creates a new instance for the given {@link CategoryTree}
     * 
     * @param categoryTree The {@link CategoryTree}
     */
    CategoryTreeFilterDocumentListener(CategoryTree<?> categoryTree)
    {
        this.categoryTree = categoryTree; 
    }
    
    @Override
    public void removeUpdate(DocumentEvent e)
    {
        update(e);
    }
    
    @Override
    public void insertUpdate(DocumentEvent e)
    {
        update(e);
    }
    
    @Override
    public void changedUpdate(DocumentEvent e)
    {
        update(e);
    }
    
    /**
     * Performs the update of the filter of the {@link CategoryTree}
     * based on the contents of the document that the given event
     * originated from
     * 
     * @param e The document event
     */
    private void update(DocumentEvent e)
    {
        Document document = e.getDocument();
        String s = null;
        try
        {
            s = document.getText(0, document.getLength());
        }
        catch (BadLocationException canNotHappen)
        {
            // Can not happen
        }
        if (s == null || s.trim().length() == 0)
        {
            categoryTree.setFilter(null);
        }
        else
        {
            categoryTree.setFilter(
                TreeModelFilters.containsStringIgnoreCase(s));
        }
    }
    
}