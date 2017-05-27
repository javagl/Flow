/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.javagl.flow.module.creation.AbstractModuleView;
import de.javagl.flow.module.view.ModuleView;

/**
 * Implementation of a {@link ModuleView} for a {@link GuiConsoleModule} 
 */
public final class GuiConsoleModuleView 
    extends AbstractModuleView implements ModuleView
{
    /**
     * The actual GUI component
     */
    private final JTextArea textArea;
    
    /**
     * Default constructor
     */
    public GuiConsoleModuleView()
    {
        textArea = new JTextArea(5, 20);
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        textArea.setFont(font);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        getComponent().setLayout(new GridLayout(1,1));
        getComponent().add(scrollPane);
    }

    @Override
    protected void updateModuleView()
    {
        GuiConsoleModule module = (GuiConsoleModule)getModule();
        if (module != null)
        {
            textArea.setText(String.valueOf(module.getObject()));
        }
    }

}