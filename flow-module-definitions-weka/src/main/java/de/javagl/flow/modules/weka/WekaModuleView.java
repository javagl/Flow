/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.GridLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.view.ModuleView;
import weka.gui.GenericObjectEditor.GOEPanel;

/**
 * Implementation of a {@link ModuleView} for a Weka object. 
 */
final class WekaModuleView 
    extends JPanel implements ModuleView
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(WekaModuleView.class.getName());
    
    /**
     * The class type
     */
    private final Class<?> classType;
    
    /**
     * The {@link CustomGenericObjectEditor}
     */
    private final CustomGenericObjectEditor customGenericObjectEditor;
    
    /**
     * The GOEPanel from the Weka GenericObjectEditor
     */
    private final GOEPanel goePanel; 
    
    /**
     * The {@link Module}
     */
    private Module module;
    
    /**
     * The property change listener that will be attached to the
     * {@link WekaModule#getConfiguration() WekaModule configuration},
     * to update the options of the Weka Object when the configuration
     * changes
     */
    private final PropertyChangeListener propertyChangeListener;
    
    /**
     * Default constructor
     * 
     * @param classType The classType
     */
    WekaModuleView(Class<?> classType)
    {
        this.classType = classType;
        setLayout(new GridLayout(1,1));
        
        customGenericObjectEditor = 
            new CustomGenericObjectEditor(null, false);
        customGenericObjectEditor.setClassType(classType);
        Component customEditor = customGenericObjectEditor.getCustomEditor();
        this.goePanel = (GOEPanel)customEditor;
        
        JButton configureButton = new JButton("Configure...");
        configureButton.addActionListener(e -> configure());
        add(configureButton);
        
        
        this.propertyChangeListener = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                updateWekaObjectOptionsFromConfiguration();
            }
        };
        customGenericObjectEditor.addPropertyChangeListener(e -> 
        {
            updateConfigurationFromWekaObjectOptions();
        });
    }
    
    /**
     * Called when the "Configure..." button is pressed: Opens a dialog
     * showing the configuration options for the current object
     */
    private void configure()
    {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = 
            new JDialog(window, "Configure", ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setLayout(new BorderLayout());
        
        dialog.getContentPane().add(goePanel, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Update the {@link WekaModule#getConfiguration() WekaModule configuration}
     * from the Weka Object, when there was a change in the properties dialog.
     */
    private void updateConfigurationFromWekaObjectOptions()
    {
        logger.fine("Update configuration from Weka object options");
        if (this.module != null)
        {
            WekaModule wekaModule = (WekaModule)module;
            StringBean configuration = wekaModule.getConfiguration();
            Object wekaObject = wekaModule.getWekaObject();
            String options = WekaOptions.tryGetOptions(wekaObject);
            configuration.setValue(options);
        }
    }
    
    /**
     * Update the Weka Object options, when there was a change in the
     * {@link WekaModule#getConfiguration() WekaModule configuration}
     */
    private void updateWekaObjectOptionsFromConfiguration()
    {
        logger.fine("Update Weka object options from configuration");
        if (this.module != null)
        {
            WekaModule wekaModule = (WekaModule)module;
            StringBean configuration = wekaModule.getConfiguration();
            Object wekaObject = wekaModule.getWekaObject();
            WekaOptions.trySetOptions(wekaObject, configuration.getValue());
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
        if (this.module != null)
        {
            WekaModule wekaModule = (WekaModule)this.module;
            StringBean configuration = wekaModule.getConfiguration();
            configuration.removePropertyChangeListener(propertyChangeListener);
        }
        this.module = module;
        if (this.module != null)
        {
            updateWekaObjectOptionsFromConfiguration();
            
            WekaModule wekaModule = (WekaModule)module;
            Object wekaObject = wekaModule.getWekaObject();
            customGenericObjectEditor.setClassType(classType);
            customGenericObjectEditor.setValue(wekaObject);
            
            StringBean configuration = wekaModule.getConfiguration();
            configuration.addPropertyChangeListener(propertyChangeListener);
            
        }
        else
        {
            customGenericObjectEditor.setClassType(Object.class);
        }
    }
}