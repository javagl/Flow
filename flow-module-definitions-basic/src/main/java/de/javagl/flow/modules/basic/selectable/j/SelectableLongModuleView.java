/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.j;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.view.ModuleView;

/**
 * Implementation of a {@link ModuleView} for a {@link SelectableLongModule}
 */
public final class SelectableLongModuleView 
    extends JPanel implements ModuleView
{
    /**
     * The {@link Module}
     */
    private SelectableLongModule module;
    
    /**
     * The actual GUI component
     */
    private final JSpinner spinner;
    
    /**
     * The property change listener that will be attached to the 
     * configuration of the module, and update the view accordingly.
     */
    private final PropertyChangeListener propertyChangeListener;
    
    /**
     * Default constructor
     */
    public SelectableLongModuleView()
    {
        setLayout(new BorderLayout());
        add(new JLabel("Long:"), BorderLayout.WEST);

        spinner = new JSpinner(new SpinnerNumberModel(
            0, -Long.MAX_VALUE, Long.MAX_VALUE, 1));
        DefaultEditor editor = (DefaultEditor) spinner.getEditor();
        JFormattedTextField textField = editor.getTextField();
        textField.setColumns(10);
        
        spinner.addChangeListener(e -> 
        {
            if (module != null)
            {
                Object object = spinner.getValue();
                Number number = (Number)object;
                module.getConfiguration().setValue(number.longValue());
            }
        });
        
        add(spinner, BorderLayout.CENTER);
        
        propertyChangeListener = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                Number newValue = (Number) event.getNewValue();
                spinner.setValue(newValue.longValue());
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
        spinner.setEnabled(false);
        if (this.module != null)
        {
            LongBean bean = this.module.getConfiguration();
            bean.removePropertyChangeListener(propertyChangeListener);
        }
        this.module = (SelectableLongModule) module;
        if (this.module != null)
        {
            LongBean bean = this.module.getConfiguration();
            bean.addPropertyChangeListener(propertyChangeListener);
            spinner.setEnabled(true);
            spinner.setValue(bean.getValue());
        }
    }
}