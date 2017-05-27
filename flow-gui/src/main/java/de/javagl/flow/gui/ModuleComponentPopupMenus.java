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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.module.Module;

/**
 * Methods for creating popup menus for {@link ModuleComponent} instances
 */
class ModuleComponentPopupMenus
{
    /**
     * Creates and shows a popup menu for the given {@link ModuleComponent}
     * 
     * @param flowEditor The {@link FlowEditor} that will perform the actions
     * @param moduleComponent The {@link ModuleComponent}
     * @param location The location of the popup menu on the invoker
     */
    static void showMenuFor(
        FlowEditor flowEditor,
        ModuleComponent moduleComponent,
        Point location)
    {
        JPopupMenu menu = new JPopupMenu();
        Module module = moduleComponent.getModule();
        menu.add(createDeleteModuleAction(flowEditor, module));
        menu.show(moduleComponent, location.x, location.y);
    }

    
    /**
     * Create an action to delete the given {@link Module} in the given
     * {@link FlowEditor}
     * 
     * @param flowEditor The {@link FlowEditor}
     * @param module The {@link Module}
     * @return The action
     */
    private static Action createDeleteModuleAction(
        FlowEditor flowEditor, Module module)
    {
        Action deleteModuleAction = new AbstractAction()
        {
            /**
             * Serial UID
             */
            private static final long serialVersionUID = -8474211805730850772L;

            // Initialization
            {
                putValue(NAME, "Delete " + module);
                putValue(SHORT_DESCRIPTION, "Delete the module");
            }
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                flowEditor.delete(
                    Collections.singleton(module), 
                    Collections.emptySet());
            }
        };
        return deleteModuleAction;
    }


    /**
     * Private constructor to prevent instantiation
     */
    private ModuleComponentPopupMenus()
    {
        // Private constructor to prevent instantiation
    }
}
