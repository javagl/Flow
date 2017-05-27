/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.z;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.view.ModuleView;

/**
 * Implementation of a {@link ModuleView} for a {@link SelectableBooleanModule}
 */
public final class SelectableBooleanModuleView 
    extends JPanel implements ModuleView
{
    /**
     * The {@link Module}
     */
    private SelectableBooleanModule module;
    
    /**
     * The actual GUI component
     */
    private final JCheckBox checkBox;
    
    /**
     * The property change listener that will be attached to the 
     * configuration of the module, and update the view accordingly.
     */
    private final PropertyChangeListener propertyChangeListener;
    
    /**
     * Default constructor
     */
    public SelectableBooleanModuleView()
    {
        setLayout(new GridLayout(0,2));
        add(new JLabel("Boolean:"));
        checkBox = new JCheckBox();
        checkBox.addActionListener(e -> 
        {
            if (module != null)
            {
                module.getConfiguration().setValue(checkBox.isSelected());
            }
        });
        add(checkBox);
        
        propertyChangeListener = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                Boolean newValue = (Boolean) event.getNewValue();
                checkBox.setSelected(newValue);
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
        checkBox.setEnabled(false);
        if (this.module != null)
        {
            BooleanBean bean = this.module.getConfiguration();
            bean.removePropertyChangeListener(propertyChangeListener);
        }
        this.module = (SelectableBooleanModule) module;
        if (this.module != null)
        {
            BooleanBean bean = this.module.getConfiguration();
            bean.addPropertyChangeListener(propertyChangeListener);
            boolean value = bean.getValue() == Boolean.TRUE;
            checkBox.setEnabled(true);
            checkBox.setSelected(value);
        }
    }
}