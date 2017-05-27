package de.javagl.flow.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.SlotInfo;

/**
 * A panel containing the representations of the {@link OutputSlot} objects
 * of a {@link Module}, used inside a {@link ModuleComponent}
 */
final class ModuleComponentOutputPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 2206183202108424451L;

    /**
     * The labels representing the {@link OutputSlot} objects of the module
     */
    private List<JLabel> outputSlotLabels = Collections.emptyList();

    /**
     * The labels containing information about the {@link OutputSlot} objects 
     * of the module
     */
    private List<JLabel> outputInfoLabels = Collections.emptyList();

    /**
     * Create a new panel for the given {@link Module}
     * 
     * @param module The {@link Module}
     */
    ModuleComponentOutputPanel(Module module)
    {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));

        outputSlotLabels = new ArrayList<JLabel>();
        outputInfoLabels = new ArrayList<JLabel>();
        ModuleInfo moduleInfo = module.getModuleInfo();
        int numOutputSlots = moduleInfo.getOutputSlotInfos().size();
        for (int i=0; i<numOutputSlots; i++)
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(0, 0, 0, 0);
            constraints.weighty = 1.0;
            constraints.gridy = i;

            // The outputInfoLabel will contain the
            // slot name and type information
            JLabel outputInfoLabel = new JLabel();
            outputInfoLabel.setOpaque(true);
            outputInfoLabel.setBackground(Color.WHITE);
            outputInfoLabel.setBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2));
            outputInfoLabels.add(outputInfoLabel);

            constraints.gridx = 0;
            constraints.weightx = 1.0;
            JPanel container = new JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
            container.setBackground(Color.WHITE);
            container.add(outputInfoLabel);
            add(container, constraints);

            // The outputSlotLabel will contain the
            // arrow > and allow connecting links
            JLabel outputSlotLabel = new JLabel(" > ");
            outputSlotLabel.setOpaque(true);
            outputSlotLabel.setBackground(Color.WHITE);
            outputSlotLabel.setBorder(                    
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 2, 2, 2), 
                    BorderFactory.createMatteBorder(
                        1, 0, 1, 1, Color.BLACK)));
            outputSlotLabels.add(outputSlotLabel);

            constraints.gridx = 1;
            constraints.weightx = 0.0;
            add(outputSlotLabel, constraints);
        }
    }
    
    /**
     * Update the info labels that show the name and type of the 
     * {@link InputSlot} objects and {@link OutputSlot} objects
     * 
     * @param module The {@link Module}
     */
    void updateSlotInfoLabels(Module module)
    {
        ModuleInfo moduleInfo = module.getModuleInfo();
        
        List<SlotInfo> outputSlotInfos = moduleInfo.getOutputSlotInfos();
        List<OutputSlot> outputSlots = module.getOutputSlots();
        for (int i=0; i<outputSlotInfos.size(); i++)
        {
            OutputSlot slot = outputSlots.get(i);
            SlotInfo slotInfo = outputSlotInfos.get(i);
            String string = 
                ModuleComponentUtils.createSlotString(slotInfo, slot);
            JLabel label = outputInfoLabels.get(i);
            ModuleComponentUtils.setTextAndTooltip(label, string);
            
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
        if (index < 0)
        {
            for (int i=0; i<outputSlotLabels.size(); i++)
            {
                JComponent component = outputSlotLabels.get(i);
                component.setBackground(color);
            }
        }
        else
        {
            JComponent component = outputSlotLabels.get(index);
            component.setBackground(color);
        }
    }
    
    /**
     * Returns the component representing the {@link OutputSlot}
     * with the given index
     * 
     * @param index The index
     * @return The component representing the {@link OutputSlot}
     * with the given index
     */
    JComponent getOutputSlotLabel(int index)
    {
        return outputSlotLabels.get(index);
    }
    
}