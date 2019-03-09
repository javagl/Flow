/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.file;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.modules.basic.selectable.string.StringBean;

/**
 * Implementation of a {@link ModuleView} for a {@link SelectableFileModule}
 */
public final class SelectableFileModuleView 
    extends JPanel implements ModuleView
{
    /**
     * The {@link Module}
     */
    private SelectableFileModule module;
    
    /**
     * The actual GUI component
     */
    private final JTextField textField;
    
    /**
     * The property change listener that will be attached to the 
     * configuration of the module, and update the view accordingly.
     */
    private final PropertyChangeListener propertyChangeListener;
    
    /**
     * The file chooser that will be used for selecting the file
     */
    private final JFileChooser fileChooser;
    
    /**
     * Whether an update is currently in progress
     */
    private boolean updating = false;

    /**
     * Default constructor
     */
    public SelectableFileModuleView()
    {
        setLayout(new BorderLayout());
        add(new JLabel("File:"), BorderLayout.WEST);
        textField = new JTextField(40);
        textField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                update();
            }
            
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                update();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
                update();
            }
            
            private void update()
            {
                if (module != null)
                {
                    updating = true;
                    module.getConfiguration().setValue(textField.getText());
                    updating = false;
                }
           }
        });
        add(textField, BorderLayout.CENTER);
        
        JButton selectButton = new JButton("Select...");
        selectButton.addActionListener(e -> 
        {
            selectFile();
        });
        add(selectButton, BorderLayout.EAST);
        
        fileChooser = new JFileChooser();
        
        propertyChangeListener = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                if (!updating)
                {
                    String newValue = (String) event.getNewValue();
                    textField.setText(newValue);
                }
            }
        };
    }

    /**
     * Called when the button for selecting a file is clicked
     */
    private void selectFile()
    {
        int returnState = fileChooser.showOpenDialog(this);
        if (returnState == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            if (file != null)
            {
                textField.setText(file.toString());
            }
        }
    }

    @Override
    public Object getComponent()
    {
        return this;
    }

    @Override
    public void setModule(Module module)
    {
        textField.setEnabled(false);
        if (this.module != null)
        {
            StringBean bean = this.module.getConfiguration();
            bean.removePropertyChangeListener(propertyChangeListener);
        }
        this.module = (SelectableFileModule) module;
        if (this.module != null)
        {
            StringBean bean = this.module.getConfiguration();
            bean.addPropertyChangeListener(propertyChangeListener);
            textField.setEnabled(true);
            textField.setText(bean.getValue());
            
            String fileName = bean.getValue();
            if (fileName == null)
            {
                fileName = "";
            }
            File file = new File(fileName);
            fileChooser.setSelectedFile(file);
        }
    }
}