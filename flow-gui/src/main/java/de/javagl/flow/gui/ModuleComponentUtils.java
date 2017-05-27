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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.Slot;
import de.javagl.flow.module.slot.SlotInfo;
import de.javagl.types.Types;

/**
 * Utility methods related to {@link ModuleComponent} instances
 */
class ModuleComponentUtils
{
    /**
     * Create the HTML string for the given {@link Slot}
     * 
     * @param slotInfo The {@link SlotInfo} for the {@link Slot}
     * @param slot The {@link Slot}
     * @return The HTML string for the {@link Slot}
     */
    static String createSlotString(SlotInfo slotInfo, Slot slot)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(slotInfo.getName());
        sb.append("<br>");
        sb.append("<font size=-2>");
        sb.append(GuiUtils.escapeTypeName(
            Types.stringFor(slot.getActualType())));
        sb.append("</font>");
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Set the given text for the given label. If the preferred size
     * of the label afterwards is larger than the size of its parent,
     * then set the given text as the tooltip as well (otherwise,
     * the tooltip is set to <code>null</code>)
     * 
     * @param label The label
     * @param text The text
     */
    static void setTextAndTooltip(JLabel label, String text)
    {
        label.setText(text);
        
        Dimension preferred = label.getPreferredSize();
        Dimension parent = label.getParent().getSize();
        if (preferred.width > parent.width || preferred.height > parent.height)
        {
            label.setToolTipText(text);
        }
        else
        {
            label.setToolTipText(null);
        }
    }
    
    /**
     * Returns the {@link OutputSlot} under the mouse position as of the
     * given mouse event, or <code>null</code> if there is no such slot
     * 
     * @param e The mouse event
     * @param moduleComponents The {@link ModuleComponent} objects to examine
     * @return The {@link OutputSlot} under the mouse, or <code>null</code>
     */
    static OutputSlot computeStartSlot(MouseEvent e,
        Iterable<? extends ModuleComponent> moduleComponents)
    {
        for (ModuleComponent moduleComponent : moduleComponents)
        {
            Rectangle moduleComponentBounds = moduleComponent.getBounds();
            if (!moduleComponentBounds.contains(e.getPoint()))
            {
                continue;
            }
            Module module = moduleComponent.getModule();
            int numSlots = module.getOutputSlots().size();
            for (int i = 0; i < numSlots; i++)
            {
                Rectangle2D b = 
                    moduleComponent.getOutputSlotBounds(i, e.getComponent());
                if (b.contains(e.getPoint()))
                {
                    return module.getOutputSlots().get(i);
                }
            }
        }
        return null;
    }

    /**
     * Returns the {@link InputSlot} under the mouse position as of the
     * given mouse event, or <code>null</code> if there is no such slot
     * 
     * @param e The mouse event
     * @param moduleComponents The {@link ModuleComponent} objects to examine
     * @return The {@link InputSlot} under the mouse, or <code>null</code>
     */
    static InputSlot computeEndSlot(MouseEvent e,
        Iterable<? extends ModuleComponent> moduleComponents)
    {
        for (ModuleComponent moduleComponent : moduleComponents)
        {
            Rectangle moduleComponentBounds = moduleComponent.getBounds();
            if (!moduleComponentBounds.contains(e.getPoint()))
            {
                continue;
            }
            Module module = moduleComponent.getModule();
            int numSlots = module.getInputSlots().size();
            for (int i = 0; i < numSlots; i++)
            {
                Rectangle2D b = 
                    moduleComponent.getInputSlotBounds(i, e.getComponent());
                if (b.contains(e.getPoint()))
                {
                    return module.getInputSlots().get(i);
                }
            }
        }
        return null;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private ModuleComponentUtils()
    {
        // Private constructor to prevent instantiation
    }
    
}
