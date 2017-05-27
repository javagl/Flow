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

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import de.javagl.flow.link.Link;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;

/**
 * Default implementation of a {@link LinkShapeProvider}
 */
final class DefaultLinkShapeProvider implements LinkShapeProvider
{
    /**
     * The {@link ModuleComponentOwner} that provides the 
     * {@link ModuleComponent} instances that are used for
     * computing the center positions of slots
     */
    private final ModuleComponentOwner moduleComponentOwner;
    
    /**
     * Creates a new instance
     * 
     * @param moduleComponentOwner The {@link ModuleComponentOwner}
     */
    DefaultLinkShapeProvider(ModuleComponentOwner moduleComponentOwner)
    {
        this.moduleComponentOwner = Objects.requireNonNull(
            moduleComponentOwner, "The moduleComponentOwner may not be null");
    }
    
    @Override
    public Shape shapeForLink(Link link)
    {
        Point2D start = centerOfOutput(link.getSourceSlot());
        Point2D end = centerOfInput(link.getTargetSlot());
        
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        
        // If the delta is negative, move the control points slightly
        // adding some curve to the link shape, to keep the link visible
        double cdy = 0; 
        double cdx = 0;
        cdx = 0.5 * dx + 0.25 * Math.abs(dy);
        if (dx < 50)
        {
            cdx -= (dx - 50);
        }
        cdx = Math.min(200, cdx);
        
        Path2D path = new Path2D.Double();
        path.moveTo(start.getX(), start.getY());
        path.curveTo(
            start.getX() + cdx, start.getY() + cdy,
            end.getX() - cdx, end.getY() - cdy,
            end.getX(), end.getY());
        return path;
    }

    @Override
    public Point2D centerOfOutput(OutputSlot slot)
    {
        ModuleComponent moduleComponent = 
            moduleComponentOwner.getModuleComponent(slot.getModule());
        return moduleComponent.centerOfOutput(slot);
    }

    @Override
    public Point2D centerOfInput(InputSlot slot)
    {
        ModuleComponent moduleComponent = 
            moduleComponentOwner.getModuleComponent(slot.getModule());
        return moduleComponent.centerOfInput(slot);
    }
}
