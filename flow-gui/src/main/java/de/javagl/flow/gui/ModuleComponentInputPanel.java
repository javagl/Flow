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
 * A panel containing the representations of the {@link InputSlot} objects
 * of a {@link Module}, used inside a {@link ModuleComponent}
 */
final class ModuleComponentInputPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 5938674940538607154L;

    /**
     * The labels representing the {@link InputSlot} objects of the module
     */
    private List<JLabel> inputSlotLabels = Collections.emptyList();

    /**
     * The labels containing information about the {@link InputSlot} objects 
     * of the module
     */
    private List<JLabel> inputInfoLabels = Collections.emptyList();
    
    /**
     * Create a new panel for the given {@link Module}
     * 
     * @param module The {@link Module}
     */
    ModuleComponentInputPanel(Module module)
    {
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        
        inputSlotLabels = new ArrayList<JLabel>();
        inputInfoLabels = new ArrayList<JLabel>();
        ModuleInfo moduleInfo = module.getModuleInfo();
        int numInputSlots = moduleInfo.getInputSlotInfos().size();
        for (int i=0; i<numInputSlots; i++)
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(0, 0, 0, 0);
            constraints.weighty = 1.0;
            constraints.gridy = i;

            // The inputSlotLabel will contain the
            // arrow > and allow connecting links
            JLabel inputSlotLabel = new JLabel(" > ");
            inputSlotLabel.setOpaque(true);
            inputSlotLabel.setBackground(Color.WHITE);
            inputSlotLabel.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 2, 2, 2), 
                    BorderFactory.createMatteBorder(
                        1, 0, 1, 1, Color.BLACK)));
            inputSlotLabels.add(inputSlotLabel);

            constraints.gridx = 0;
            constraints.weightx = 0.0;
            add(inputSlotLabel, constraints);

            // The inputInfoLabel will contain the
            // slot name and type information
            JLabel inputInfoLabel = new JLabel();
            inputInfoLabel.setOpaque(true);
            inputInfoLabel.setBackground(Color.WHITE);
            inputInfoLabel.setBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2));
            inputInfoLabels.add(inputInfoLabel);

            constraints.gridx = 1;
            constraints.weightx = 1.0;
            JPanel container = new JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
            container.setBackground(Color.WHITE);
            container.add(inputInfoLabel);
            add(container, constraints);
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
        
        List<SlotInfo> inputSlotInfos = moduleInfo.getInputSlotInfos();
        List<InputSlot> inputSlots = module.getInputSlots();
        for (int i=0; i<inputSlotInfos.size(); i++)
        {
            InputSlot slot = inputSlots.get(i);
            SlotInfo slotInfo = inputSlotInfos.get(i);
            String string = 
                ModuleComponentUtils.createSlotString(slotInfo, slot);
            JLabel label = inputInfoLabels.get(i);
            ModuleComponentUtils.setTextAndTooltip(label, string);
        }
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
        if (index < 0)
        {
            for (int i=0; i<inputSlotLabels.size(); i++)
            {
                JComponent component = inputSlotLabels.get(i);
                component.setBackground(color);
            }
        }
        else
        {
            JComponent component = inputSlotLabels.get(index);
            component.setBackground(color);
        }
    }

    /**
     * Returns the component representing the {@link InputSlot}
     * with the given index
     * 
     * @param index The index
     * @return The component representing the {@link InputSlot}
     * with the given index
     */
    JComponent getInputSlotLabel(int index)
    {
        return inputSlotLabels.get(index);
    }
    
}