/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.reflection.Classes;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor.GOEPanel;

/**
 * Implementation of a {@link ModuleView} for a {@link WekaTypeBean}. It
 * offers text components to edit the {@link WekaTypeBean#getClassName()
 * class name} and {@link WekaTypeBean#getOptions() options}, but also
 * uses a Weka GenericObjectEditor to expose the class name selection
 * and option editing capabilities in the view.
 */
final class WekaTypeModuleView 
    extends JPanel implements ModuleView
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(WekaTypeModuleView.class.getName());
    
    /**
     * The class type
     */
    private final Class<?> classType;
    
    /**
     * The {@link CustomGenericObjectEditor}
     */
    private final CustomGenericObjectEditor customGenericObjectEditor; 
    
    /**
     * The {@link Module}
     */
    private Module module;
    
    /**
     * The GUI component for the class name
     */
    private final JTextField classNameTextField;

    /**
     * The GUI component for the options
     */
    private final JTextArea optionsTextArea;
    
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
     * 
     * @param classType The classType
     */
    public WekaTypeModuleView(Class<?> classType)
    {
        this.classType = classType;
        setLayout(new GridBagLayout());
        
        int row = 0;

        // First row: The class name and the class name selection dialog button
        classNameTextField = new JTextField(40);
        
        JButton selectClassNameButton = new JButton("Select...");
        selectClassNameButton.setMargin(new Insets(0,0,0,0));
        selectClassNameButton.addActionListener(e -> selectClassName());

        GridBagLayouts.addRow(this, row++, 0.0, 1,  
            new JLabel("Class Name: "), 
            classNameTextField, 
            wrapFlow(selectClassNameButton));
        
        // Second row: The options and the option editing dialog button
        optionsTextArea = new JTextArea(10, 40);
        JButton editOptionsButton = new JButton("Edit...");
        editOptionsButton.setMargin(new Insets(0, 0, 0, 0));
        editOptionsButton.addActionListener(e -> editOptions());

        JScrollPane optionsTextAreaScrollPane = 
            new JScrollPane(optionsTextArea);
        optionsTextAreaScrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        optionsTextAreaScrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagLayouts.addRow(this, row++, 1.0, 1, 
            new JLabel("Options: "), 
            optionsTextAreaScrollPane, 
            wrapFlow(editOptionsButton));

        DocumentListener documentListener = new DocumentListener()
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
                updateModelFromView();
           }
        };
        classNameTextField.getDocument().addDocumentListener(documentListener);
        optionsTextArea.getDocument().addDocumentListener(documentListener);
        
        propertyChangeListener = new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent event)
            {
                updateViewFromModel();
            }
        };
        
        this.customGenericObjectEditor = new CustomGenericObjectEditor(
            className -> classNameTextField.setText(className), false);
    }
    
    /**
     * Wrap the given component into a panel with flow layout
     * 
     * @param c The component
     * @return The panel
     */
    private static JComponent wrapFlow(JComponent c)
    {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.add(c);
        return p;
    }

    
    /**
     * Update the text components due to changes in the bean
     */
    private void updateViewFromModel()
    {
        if (updating)
        {
            return;
        }
        updating = true;
        
        classNameTextField.setText("");
        optionsTextArea.setText("");
        
        if (module == null)
        {
            updating = false;
            return;
        }
        
        WekaTypeBean bean = (WekaTypeBean) module.getConfiguration();
        classNameTextField.setText(bean.getClassName());
        
        String options = bean.getOptions();
        optionsTextArea.setText(WekaOptions.tryFormatOptions(options));
        
        updating = false;
    }
    
    /**
     * Update the bean due to changes in the text components
     */
    private void updateModelFromView()
    {
        if (updating)
        {
            return;
        }
        if (module == null)
        {
            return;
        }
        updating = true;
        
        WekaTypeBean bean = (WekaTypeBean) module.getConfiguration();
        
        String className = classNameTextField.getText();
        bean.setClassName(className);
        
        Object instance = 
            Classes.newInstanceOptional(className);
        if (instance == null)
        {
            logger.warning("No valid class name: " + className);
            classNameTextField.setForeground(Color.RED);
        }
        else
        {
            customGenericObjectEditor.classSelected(className);
            classNameTextField.setForeground(Color.BLACK);
        }
        
        bean.setOptions(optionsTextArea.getText());
        
        updating = false;
    }
    
    /**
     * Show a dialog containing the (Weka) tree to select a class name
     */
    private void selectClassName()
    {
        customGenericObjectEditor.setClassType(classType);
        
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = 
            new JDialog(window, "Select", ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setLayout(new BorderLayout());
        
        JTree tree = customGenericObjectEditor.createTree();
        customGenericObjectEditor.setDefaultValue();
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(600, 300));
        dialog.getContentPane().add(treeScrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        okButton.addActionListener(event -> dialog.dispose());
        controlPanel.add(okButton);
        dialog.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    
    /**
     * Show the (Weka) dialog to edit the options of the current object
     */
    private void editOptions()
    {
        Window window = SwingUtilities.getWindowAncestor(this);
        
        JDialog dialog = 
            new JDialog(window, "Edit", ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(
            customGenericObjectEditor.getCustomEditor());
        
        // Hook into the "OK"-Button to transfer the edited options
        // into the WekaTypeBean
        GOEPanel goePanel = 
            (GOEPanel) customGenericObjectEditor.getCustomEditor();
        goePanel.addOkListener(e -> {
            Object wekaObject = customGenericObjectEditor.getValue();
            String options = "";            
            if (wekaObject instanceof OptionHandler) 
            {
                OptionHandler optionHandler = (OptionHandler)wekaObject;
                String[] optionsArray = optionHandler.getOptions();
                options = Utils.joinOptions(optionsArray);
            }
            WekaTypeBean bean = (WekaTypeBean) this.module.getConfiguration();
            bean.setOptions(options);
        });

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    @Override
    public Object getComponent()
    {
        return this;
    }

    @Override
    public void setModule(Module module)
    {
        classNameTextField.setEnabled(false);
        optionsTextArea.setEnabled(false);
        if (this.module != null)
        {
            WekaTypeBean bean = (WekaTypeBean) this.module.getConfiguration();
            bean.removePropertyChangeListener(propertyChangeListener);
        }
        this.module = module;
        if (this.module != null)
        {
            WekaTypeBean bean = (WekaTypeBean) this.module.getConfiguration();
            bean.addPropertyChangeListener(propertyChangeListener);
            classNameTextField.setEnabled(true);
            optionsTextArea.setEnabled(true);
        }
        updateViewFromModel();
    }
}