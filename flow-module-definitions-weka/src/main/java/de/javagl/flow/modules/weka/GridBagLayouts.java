/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Utility methods related to containers with a <code>GridBagLayout</code> 
 */
class GridBagLayouts
{
    // Taken with minor adjustments from CommonUI project
    
    /**
     * Add the given components as a new row in the given container, 
     * which must have a <code>GridBagLayout</code>.
     * 
     * @param container The container
     * @param row The row
     * @param weightY The weight in y-direction
     * @param extraSpaceColumn The index of the column that should receive
     * any extra space
     * @param components The components to add
     * @throws IllegalArgumentException If the given container does 
     * not have a <code>GridBagLayout</code>.
     */
    public static void addRow(
        Container container, int row, double weightY, int extraSpaceColumn, 
        Component ... components)
    {
        if (!(container.getLayout() instanceof GridBagLayout))
        {
            throw new IllegalArgumentException(
                "Container does not have a " +
                "GridBagLayout: "+container.getLayout());
        }
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.weighty = weightY;
        constraints.weightx = 1.0;
        constraints.gridy = row;
        for (int i=0; i<components.length; i++)
        {
            Component component = components[i];
            constraints.gridx = i;
            if (i != extraSpaceColumn)
            {
                constraints.weightx = 0.0;
            }
            else
            {
                constraints.weightx = 1.0;
            }
            container.add(component, constraints);
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    private GridBagLayouts()
    {
        // Private constructor to prevent instantiation
    }
}
