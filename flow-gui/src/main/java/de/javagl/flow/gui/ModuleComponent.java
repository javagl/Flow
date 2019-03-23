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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.view.ModuleView;

/**
 * A component representing a {@link Module} inside a 
 * {@link FlowEditorComponent}.
 */
final class ModuleComponent extends JInternalFrame
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(ModuleComponent.class.getName());
    
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 5043631196674437988L;

    /**
     * The size of the border. That is, the size of the (colored)
     * border that will be added to the actual border, in order
     * to indicate the selection state of the {@link Module}
     */
    private static final int BORDER_SIZE = 2;
    
    /**
     * The {@link Module} represented by this component
     */
    private Module module;

    /**
     * The {@link ModuleComponentInputPanel}, containing the input slots
     */
    private ModuleComponentInputPanel moduleComponentInputPanel;

    /**
     * The {@link ModuleComponentOutputPanel}, containing the output slots
     */
    private ModuleComponentOutputPanel moduleComponentOutputPanel;

    /**
     * The panel containing the panels with the 
     * {@link #moduleComponentInputPanel}
     * and {@link #moduleComponentOutputPanel}
     */
    private JPanel slotsPanel;
    
    /**
     * The bar containing the status message
     */
    private final JProgressBar statusBar;
    
    /**
     * The (optional) {@link ModuleView} that shows a UI component for
     * editing the {@link Module#getConfiguration() configuration} of the 
     * module
     */
    private final ModuleView configurationView;

    /**
     * The (optional) {@link ModuleView} that shows a visualization that
     * is associated with the module
     */
    private final ModuleView visualizationView;

    /**
     * The content pane
     */
    private final JPanel contentPane;
    
    /**
     * The color of the border for the content pane
     */
    private Color borderColor;

    /**
     * A (usually semi-transparent) shading color. If this is
     * non-<code>null</code>, the panel will be painted 
     * semi-transparently, and the shade color will be painted 
     * over the module
     */
    private Color shadeColor;
    
    /**
     * The "default" minimum size (also depending on the
     * name of the module, which is shown in the title)
     */
    private Dimension internalMinimumSize = new Dimension();
    
    /**
     * A listener that listens for changes of the "preferredSize"
     * property of a JComponent, and sets the size of this 
     * ModuleComponent to its preferred size. This is used to
     * adapt the size of this ModuleComponent when the preferred
     * size of any {@link ModuleView} component changes.
     * 
     * NOTE: If this listener is omitted in the future, also see
     * notes in LayoutUpdateListener
     */
    private final PropertyChangeListener preferredSizeListener = 
        new PropertyChangeListener()
    {
        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (evt.getPropertyName().equals("preferredSize"))
            {
                setSize(getPreferredSize());
            }
        }
        
        @Override
        public String toString() 
        {
            return "preferredSizeListener in " + ModuleComponent.this;
        }
    };

    /**
     * Creates a new component that represents a {@link Module}.<br>
     * <br>
     * If the given configuration view is not <code>null</code>, then its 
     * {@link ModuleView#getComponent() component} will be added to this 
     * component.<br>
     * <br> 
     * If the given visualization view is not <code>null</code>, then its 
     * {@link ModuleView#getComponent() component} will be added to this 
     * component.
     * 
     * @param configurationView The {@link ModuleView} that contains the
     * UI components for editing the 
     * {@link Module#getConfiguration() configuration} of
     * the {@link Module}. May be <code>null</code>
     * @param visualizationView The {@link ModuleView} that contains the
     * visualization that is associated with the {@link Module}.
     * May be <code>null</code>
     */
    ModuleComponent( 
        ModuleView configurationView,
        ModuleView visualizationView)
    {
        super("(none)", false, false, false, false);
        
        JComponent titleBar = getTitleBar();
        Font titleFont = titleBar.getFont();
        titleBar.setFont(titleFont.deriveFont(10.0f));
        
        this.configurationView = configurationView;
        this.visualizationView = visualizationView;
        
        this.contentPane = new JPanel();
        setContentPane(contentPane);
        
        setBorderColor(Color.WHITE);
        getContentPane().setLayout(new BorderLayout());
        
        // Create the center panel, containing the IO-panel with
        // the input/output slots, and the visualization+configuration views
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        // Create the slots panel, containing the input and output slots
        slotsPanel = new JPanel();
        slotsPanel.setLayout(new GridBagLayout());
        slotsPanel.setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        
        centerPanel.add(slotsPanel, BorderLayout.NORTH);
        
        if (configurationView != null || visualizationView != null)
        {
            // Create the panel that contains the visualization
            // and configuration views
            JPanel viewsPanel = new JPanel(new BorderLayout());

            JComponent configurationComponent = 
                getModuleViewComponent(configurationView);
            if (configurationComponent != null)
            {
                viewsPanel.add(configurationComponent, BorderLayout.NORTH);
                configurationComponent.addPropertyChangeListener(
                    preferredSizeListener);
            }
            
            JComponent visualizationComponent = 
                getModuleViewComponent(visualizationView);
            if (visualizationComponent != null)
            {
                viewsPanel.add(visualizationComponent, BorderLayout.CENTER);
                visualizationComponent.addPropertyChangeListener(
                    preferredSizeListener);
            }
            
            centerPanel.add(viewsPanel, BorderLayout.CENTER);
        }
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        
        statusBar = new JProgressBar();
        Font font = statusBar.getFont();
        float newSize = font.getSize() * 0.75f;
        statusBar.setFont(font.deriveFont(newSize));
        statusBar.setStringPainted(true);
        statusBar.setString(" ");
        statusBar.setOpaque(true);
        statusBar.setBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        
        setVisible(true);
        setResizable(visualizationView != null);
        setMaximizable(visualizationView != null);
        
        // Uninstall the popup menu that may have been added by the Nimbus L&F.
        // A popup menu that may contain the action to delete the module 
        // component is handled via the ModuleComponentPopupMenus class
        setComponentPopupMenu(null);
    }
    
    /**
     * Obtains the component from the given {@link ModuleView}.
     * If the given {@link ModuleView} is <code>null</code>, then
     * <code>null</code> is returned. If the component of the
     * {@link ModuleView} is not a JComponent, a warning is 
     * printed and <code>null</code> is returned.
     * 
     * @param moduleView The {@link ModuleView}
     * @return The component of the {@link ModuleView}
     */
    private JComponent getModuleViewComponent(ModuleView moduleView)
    {
        if (moduleView != null)
        {
            Object component = moduleView.getComponent();
            if (component instanceof JComponent)
            {
                JComponent jComponent = (JComponent)component;
                return jComponent;
            }
            logger.warning("ModuleView component is no " 
                + "JComponent but " + component.getClass());
        }
        return null;
    }
    
    
    /**
     * Set the {@link Module} that should be represented by this
     * {@link ModuleComponent}.
     * 
     * @param newModule The {@link Module} to show in this component
     */
    void setModule(Module newModule)
    {
        module = newModule;

        if (configurationView != null)
        {
            configurationView.setModule(newModule);
        }
        if (visualizationView != null)
        {
            visualizationView.setModule(newModule);
        }
        
        slotsPanel.removeAll();
        if (module == null)
        {
            setTitle("(none)");
        }
        else
        {
            setTitle(String.valueOf(module));
            
            if (!module.getModuleInfo().getInputSlotInfos().isEmpty())
            {
                moduleComponentInputPanel = 
                    new ModuleComponentInputPanel(module);

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.BOTH;
                constraints.insets = new Insets(0, 0, 0, 0);
                constraints.weightx = 0.5;
                constraints.gridx = 0;
                slotsPanel.add(moduleComponentInputPanel, constraints);
            }
            
            if (!module.getModuleInfo().getOutputSlotInfos().isEmpty())
            {
                moduleComponentOutputPanel = 
                    new ModuleComponentOutputPanel(module);
                
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.fill = GridBagConstraints.BOTH;
                constraints.insets = new Insets(0, 0, 0, 0);
                constraints.weightx = 0.5;
                constraints.gridx = 1;
                slotsPanel.add(moduleComponentOutputPanel, constraints);
            }
            internalMinimumSize = computeInternalMinimumSize();
            updateSlotInfoLabels();
        }
    }
    
    /**
     * Update the info labels that show the name and type of the 
     * {@link InputSlot} objects and {@link OutputSlot} objects 
     */
    private void updateSlotInfoLabels()
    {
        if (moduleComponentInputPanel != null)
        {
            moduleComponentInputPanel.updateSlotInfoLabels(module);
        }
        if (moduleComponentOutputPanel != null)
        {
            moduleComponentOutputPanel.updateSlotInfoLabels(module);
        }
        //setSize(getPreferredSize());
    }
    
    
    
    
    /**
     * Compute the minimum size of this component. This will also
     * depend on the length of the name that is shown in the
     * title
     * 
     * @return The internal minimum size
     */
    private Dimension computeInternalMinimumSize()
    {
        InternalFrameUI currentUI = getUI();
        if (currentUI instanceof BasicInternalFrameUI)
        {
            // Some magic values taken from BasicInternalFrameTitlePane
            int width = 22;
            width += 64;

            if (isClosable())
            {
                width += 19;
            }
            if (isMaximizable())
            {
                width += 19;
            }
            if (isIconifiable())
            {
                width += 19;
            }
            BasicInternalFrameUI basicUI = (BasicInternalFrameUI) currentUI;
            JComponent northPane = basicUI.getNorthPane();
            Font font = northPane.getFont();
            FontMetrics fontMetrics = northPane.getFontMetrics(font);
            width += fontMetrics.stringWidth(getTitle());

            Dimension minimumSize = super.getMinimumSize();
            minimumSize.width = width;
            return minimumSize;
        }
        // This should never happen for the built-in LaFs
        logger.warning("UI of ModuleComponent is no BasicInternalFrameUI");
        return new Dimension(100,50);
    }
    
    /**
     * Returns the title bar of this frame.
     * 
     * @return The title bar of this frame (or this frame itself,
     * if there is no title bar that can be obtained)
     */
    JComponent getTitleBar()
    {
        InternalFrameUI currentUI = getUI();
        if (currentUI instanceof BasicInternalFrameUI)
        {
            BasicInternalFrameUI basicUI = (BasicInternalFrameUI) currentUI;
            JComponent northPane = basicUI.getNorthPane();
            return northPane;
        }
        return this;
    }
    
    
    @Override
    public Dimension getMinimumSize()
    {
        if (super.isMinimumSizeSet())
        {
            return super.getMinimumSize();
        }
        Dimension dim = super.getMinimumSize();
        dim.width = Math.max(dim.width, internalMinimumSize.width);
        dim.height= Math.max(dim.height, internalMinimumSize.height);
        dim.width += BORDER_SIZE * 2;
        dim.height += BORDER_SIZE * 2;
        return dim;
    }
    
    
    @Override
    public Dimension getPreferredSize()
    {
        if (super.isPreferredSizeSet())
        {
            return super.getPreferredSize();
        }
        Dimension dim = super.getPreferredSize();
        dim.width = Math.max(dim.width, internalMinimumSize.width);
        dim.height= Math.max(dim.height, internalMinimumSize.height);
        dim.width += BORDER_SIZE * 2;
        dim.height += BORDER_SIZE * 2;
        return dim;
    }
    
    
    
    /**
     * Returns the {@link Module} that is represented by this component
     * 
     * @return The {@link Module} that is represented by this component
     */
    Module getModule()
    {
        return module;
    }
    
    @Override
    public void paint(Graphics gr)
    {
        Graphics2D g = (Graphics2D)gr;
        if (shadeColor == null)
        {
            super.paint(g);
        }
        else 
        {
            Composite composite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.25f);
            g.setComposite(composite);
            super.paint(g);
            g.setColor(shadeColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    /**
     * Set the string that should be shown in the status bar.
     * This method may be called from any thread.
     *  
     * @param message The message string. 
     * @param detailedMessage The detailed message string. This may be
     * a longer message that will be shown in a tooltip of the status
     * bar. May be <code>null</code> if there is no detail message.
     */
    void setMessage(final String message, final String detailedMessage)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            doSetMessage(message, detailedMessage);
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    doSetMessage(message, detailedMessage);
                }
            });
        }
    }

    /**
     * Internal method to set the status string
     * 
     * @param message The message string
     * @param detailedMessage The detailed message string. This may be
     * a longer message that will be shown in a tooltip of the status
     * bar. May be <code>null</code> if there is no detail message.
     */
    private void doSetMessage(String message, String detailedMessage)
    {
        if (message == null)
        {
            statusBar.setString(" ");
        }
        else
        {
            statusBar.setString(message);
        }
        statusBar.setToolTipText(detailedMessage);
    }
    
    
    /**
     * Set the progress (as a value in [0,1]) that should be shown in the 
     * status bar. If the given value is smaller than 0, then the progress
     * bar will be set to indeterminate.
     * This method may be called from any thread. 
     *  
     * @param progress The progress 
     */
    void setProgress(double progress)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            doSetProgress(progress);
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    doSetProgress(progress);
                }
            });
        }
    }

    /**
     * Internal method to set the progress
     * 
     * @param progress The progress
     */
    private void doSetProgress(double progress)
    {
        if (progress < 0)
        {
            statusBar.setIndeterminate(true);
        }
        else
        {
            statusBar.setIndeterminate(false);
            int value = (int)(progress*100);
            value = Math.min(value, 100);
            statusBar.setValue(value);
        }
    }
    

    /**
     * Set the border color. This method may be called from
     * any thread.
     * 
     * @param color The border color.
     */
    void setBorderColor(Color color)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            doSetBorderColor(color);
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    doSetBorderColor(color);
                }
            });
        }
    }
    
    /**
     * Internal method to set the border color for the content pane
     * 
     * @param color The border color
     */
    private void doSetBorderColor(Color color)
    {
        this.borderColor = color;
        this.contentPane.setBorder(
            BorderFactory.createLineBorder(borderColor, BORDER_SIZE));
        repaint();
    }
    
    /**
     * Set the shading color of this panel. This will usually be 
     * a semi-transparent color that will be painted over the 
     * whole panel. If the given color is <code>null</code>, then
     * no shading will be applied
     *  
     * @param color The color
     */
    void setShadeColor(Color color)
    {
        this.shadeColor = color;
        repaint();
    }
    
    
    /**
     * Highlight the representation of the specified {@link InputSlot}
     * by setting its background color to the given color. If the given
     * index is negative, then ALL input slots will receive
     * the given background.
     * 
     * @param index The index of the {@link InputSlot}, or a value &lt;0 to
     * set all backgrounds
     * @param color The background color
     */
    void highlightInputSlot(int index, Color color)
    {
        if (moduleComponentInputPanel != null)
        {
            moduleComponentInputPanel.highlightInputSlot(index, color);
        }
    }

    
    /**
     * Highlight the representation of the specified {@link OutputSlot}
     * by setting its background color to the given color. If the given
     * index is negative, then ALL output slots will receive
     * the given background.
     * 
     * @param index The index of the {@link OutputSlot}, or a value &lt;0 to
     * set all backgrounds
     * @param color The background color
     */
    void highlightOutputSlot(int index, Color color)
    {
        if (moduleComponentOutputPanel != null)
        {
            moduleComponentOutputPanel.highlightOutputSlot(index, color);
        }
    }

    
    /**
     * Returns the bounds of the {@link InputSlot} with the given index,
     * relative to the given target component.
     * 
     * @param slotIndex The slot index
     * @param targetComponent The target component
     * @return The bounding rectangle of the {@link InputSlot} with the
     * given index, relative to the target component
     */
    Rectangle2D getInputSlotBounds(int slotIndex, Component targetComponent)
    {
        JComponent component = 
            moduleComponentInputPanel.getInputSlotLabel(slotIndex);
        Rectangle r = 
            SwingUtilities.convertRectangle(
                component.getParent(), component.getBounds(), targetComponent);
        return r;
    }
    
    /**
     * Returns the bounds of the {@link OutputSlot} with the given index,
     * relative to the given target component.
     * 
     * @param slotIndex The slot index
     * @param targetComponent The target component
     * @return The bounding rectangle of the {@link OutputSlot} with the
     * given index, relative to the target component
     */
    Rectangle2D getOutputSlotBounds(int slotIndex, Component targetComponent)
    {
        JComponent component = 
            moduleComponentOutputPanel.getOutputSlotLabel(slotIndex);
        Rectangle r = 
            SwingUtilities.convertRectangle(
                component.getParent(), component.getBounds(), targetComponent);
        return r;
    }
    
    
    /**
     * Returns the absolute center position of the representation of
     * the given {@link OutputSlot} of this {@link ModuleComponent}.
     * That is, the center position of the representation of the given
     * {@link OutputSlot} referring to the parent of this 
     * {@link ModuleComponent}.
     * 
     * @param slot The {@link OutputSlot}
     * @return The center position of the slot. 
     */
    Point2D centerOfOutput(OutputSlot slot)
    {
        int index = slot.getIndex();
        Rectangle2D r = getOutputSlotBounds(index, getParent());
        Point2D result = new Point2D.Double(r.getCenterX(), r.getCenterY());
        return result;
    }
    
    /**
     * Returns the absolute center position of the representation of
     * the given {@link InputSlot} of this {@link ModuleComponent}.      
     * That is, the center position of the representation of the given
     * {@link InputSlot} referring to the parent of this 
     * {@link ModuleComponent}.
     * 
     * @param slot The {@link InputSlot}
     * @return The center position of the slot. 
     */
    Point2D centerOfInput(InputSlot slot)
    {
        int index = slot.getIndex();
        Rectangle2D r = getInputSlotBounds(index, getParent());
        Point2D result = new Point2D.Double(r.getCenterX(), r.getCenterY());
        return result;
    }
    
    
    @Override
    public String toString()
    {
        return "ModuleComponent[module=" + module + "]";
    }
    
}
