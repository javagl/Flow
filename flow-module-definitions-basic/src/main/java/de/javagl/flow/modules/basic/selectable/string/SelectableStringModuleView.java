/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.string;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.view.ModuleView;

/**
 * Implementation of a {@link ModuleView} for a {@link SelectableStringModule}
 */
public final class SelectableStringModuleView 
    extends JPanel implements ModuleView
{
    /**
     * The {@link Module}
     */
    private SelectableStringModule module;
    
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
     * Whether an update is currently in progress
     */
    private boolean updating = false;

    /**
     * Default constructor
     */
    public SelectableStringModuleView()
    {
        setLayout(new GridLayout(0,2));
        add(new JLabel("String:"));
        textField = new JTextField(10);
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
        add(textField);
        
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
        this.module = (SelectableStringModule) module;
        if (this.module != null)
        {
            StringBean bean = this.module.getConfiguration();
            bean.addPropertyChangeListener(propertyChangeListener);
            textField.setEnabled(true);
            textField.setText(bean.getValue());
        }
    }
}