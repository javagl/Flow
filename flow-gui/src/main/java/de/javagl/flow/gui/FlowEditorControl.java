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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import de.javagl.common.ui.MoreSwingUtilities;
import de.javagl.flow.TypeContext;
import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.link.Link;
import de.javagl.flow.link.Links;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.SlotUtils;
import de.javagl.types.Types;

/**
 * The control functionality for a {@link FlowEditor}. An instance of 
 * this class will be attached as a mouse- and mouse motion listener to the 
 * main desktopPane of a {@link FlowEditorComponent}, <b>and</b> to the
 * {@link ModuleComponent} objects in the {@link FlowEditorComponent}. All 
 * mouse events will first be converted to be relative to the 
 * {@link FlowEditorComponent}. Then they will be processed 
 * accordingly, and possibly cause editing operations in the 
 * {@link FlowEditor}. 
 */
final class FlowEditorControl 
    extends MouseInputAdapter 
    implements MouseListener, MouseMotionListener
{
    /**
     * The color for a {@link Link} that is about to be created
     */
    private static final Color CREATING_LINK_COLOR = Color.GRAY;
    
    /**
     * The color for a valid {@link Link} that is about to be created
     */
    private static final Color VALID_LINK_COLOR = new Color(0,128,0);
    
    /**
     * The color for an invalid {@link Link} that is about to be created
     */
    private static final Color INVALID_LINK_COLOR = new Color(255,0,0);
    
    /**
     * The {@link FlowEditorComponent} to which this control is attached
     */
    private final FlowEditorComponent flowEditorComponent;
    
    /**
     * The {@link FlowEditor} that performs the editing operations
     */
    private FlowEditor flowEditor;
    
    /**
     * The mouse position after the previous mouse event
     */
    private final Point previousPosition = new Point();
    
    /**
     * Whether the current mouse drags should affect the viewport
     */
    private boolean draggingViewport = false;

    /**
     * The position where a mouse drag started
     */
    private Point dragStartPosition = null;
    
    /**
     * The set of {@link ModuleComponent} objects that are currently dragged. 
     */
    private Set<ModuleComponent> draggedModuleComponents = null;

    /**
     * The locations of the {@link ModuleComponent} objects that are currently 
     * dragged 
     */
    private Map<ModuleComponent, Point> draggedModuleComponentLocations = null;
    
    /**
     * The {@link ModuleComponent} that the mouse was pressed on in order
     * to start dragging. This component will be dragged directly, by the
     * means of the JInternalFrame and JDesktopPane mechanisms. All other
     * {@link #draggedModuleComponents} will be moved accordingly by the
     * {@link #dragMovementListener} 
     */
    private ModuleComponent directlyDraggedModuleComponent;
    
    /**
     * The ComponentListener that will be attached to the 
     * {@link #directlyDraggedModuleComponent} while dragging. It will apply 
     * the movement of the {@link #directlyDraggedModuleComponent} to all
     * other {@link #draggedModuleComponents}
     */
    private ComponentListener dragMovementListener = new ComponentAdapter()
    {
        @Override
        public void componentMoved(ComponentEvent e) 
        {
            Point oldLocation = 
                draggedModuleComponentLocations.get(
                    directlyDraggedModuleComponent);
            Point newLocation = 
                directlyDraggedModuleComponent.getLocation();
            int dx = newLocation.x - oldLocation.x;
            int dy = newLocation.y - oldLocation.y;
            
            for (ModuleComponent moduleComponent : draggedModuleComponents)
            {
                if (moduleComponent != directlyDraggedModuleComponent)
                {
                    Point location = 
                        draggedModuleComponentLocations.get(moduleComponent);
                    location.x += dx;
                    location.y += dy;
                    moduleComponent.setLocation(location);
                }
            }
            draggedModuleComponentLocations.put(
                directlyDraggedModuleComponent, newLocation);
        }
    };
    
    
    /**
     * The position where the creation of a selection rectangle started
     */
    private Point selectionStartPosition = null;
    
    /**
     * The current selection rectangle
     */
    private Rectangle2D selectionRectangle = null;

    /**
     * The set of {@link OutputSlot} objects that are currently marked 
     * as being "valid" as the start point of a {@link Link} that
     * is to be created
     */
    private Set<OutputSlot> validStartSlots = null;

    /**
     * The set of {@link InputSlot} objects that are currently marked 
     * as being "valid" as the end point of a {@link Link} that
     * is to be created
     */
    private Set<InputSlot> validEndSlots = null;
    
    /**
     * The {@link OutputSlot} for a {@link Link} to be created. 
     */
    private OutputSlot startSlot;

    /**
     * The {@link InputSlot} for a {@link Link} to be created. 
     */
    private InputSlot endSlot;

    /**
     * Whether the user is currently (manually) resizing a 
     * {@link ModuleComponent}. In this case, no auto-scrolling
     * should happen.
     */
    private boolean resizingModuleComponent = false;
    
    /**
     * A timer that will move the {@link FlowEditorComponent}
     * when the mouse is dragged towards the border of its visible
     * area
     */
    private final Timer autoscrollTimer = new Timer(50, new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (directlyDraggedModuleComponent != null)
            {
                return;
            }
            if (resizingModuleComponent)
            {
                return;
            }
            Point delta = 
                flowEditorComponent.autoscroll(previousPosition);
            previousPosition.x -= delta.x;
            previousPosition.y -= delta.y;
        }
    });
    
    /**
     * The handler for right-clicks on {@link ModuleComponent} title bars,
     * to show a popup menu
     */
    private final ModuleComponentPopupMenuHandler popupMenuHandler;
    
    /**
     * Creates a new control that operates on the given panel
     * 
     * @param flowEditorComponent The {@link FlowEditorComponent}
     * @param popupMenuHandler The {@link ModuleComponentPopupMenuHandler}
     */
    FlowEditorControl(FlowEditorComponent flowEditorComponent,
        ModuleComponentPopupMenuHandler popupMenuHandler)
    {
        this.flowEditorComponent = flowEditorComponent;
        this.popupMenuHandler = popupMenuHandler;
        autoscrollTimer.stop();
    }
    
    /**
     * Set the {@link FlowEditor} that performs the editing operations
     * 
     * @param flowEditor The {@link FlowEditor}
     */
    void setFlowEditor(FlowEditor flowEditor)
    {
        this.flowEditor = flowEditor;
    }
    
    /**
     * Set whether the user is currently (manually) resizing a 
     * {@link ModuleComponent}. In this case, not auto-scrolling
     * should happen.
     * 
     * @param resizingModuleComponent The new state
     */
    public void setResizingModuleComponent(boolean resizingModuleComponent)
    {
        this.resizingModuleComponent = resizingModuleComponent;
    }
    
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        //System.out.println("mousePressed "+e);
        
        e = MoreSwingUtilities.convertMouseEvent(
            e.getComponent(), e, flowEditorComponent);
        
        ModuleComponent pressedOnModuleComponent = 
            flowEditorComponent.getModuleComponentAt(e.getPoint());

        if (e.getButton() == MouseEvent.BUTTON1)
        {
            if (!flowEditorComponent.hasFocus() &&
                pressedOnModuleComponent == null)
            {
                // TODO Check what the focussing issue was about
                //System.out.println("Only focussing");
                flowEditorComponent.requestFocusInWindow();
                previousPosition.setLocation(e.getPoint());
                return;
            }
            boolean startModuleDragging = tryStartModuleDragging(e);
            if (!startModuleDragging)
            {
                boolean startLinkRemoval = tryStartLinkRemoval(e);
                if (!startLinkRemoval)
                {
                    boolean startLinkCreation = tryStartLinkCreation(e);
                    if (!startLinkCreation)
                    {
                        if (pressedOnModuleComponent == null)
                        {
                            tryStartRectangleSelection(e);
                        }
                    }
                }
            }
        }
        else if (e.getButton() == MouseEvent.BUTTON3)
        {
            if (pressedOnModuleComponent == null)
            {
                Cursor cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                DragPanel dragPanel = flowEditorComponent.getDragPanel();
                dragPanel.setCursor(cursor);
                draggingViewport = true;            
            }
        }
        previousPosition.setLocation(e.getPoint());
    }
    
    


    /**
     * Check whether the mouse was pressed on an {@link OutputSlot} or
     * an {@link InputSlot}, and prepare the creation of a {@link Link}
     * in this case
     *  
     * @param e The mouse event
     * @return Whether the creation of a {@link Link} started
     */
    private boolean tryStartLinkCreation(MouseEvent e)
    {
        ModuleComponent moduleComponent = 
            flowEditorComponent.getModuleComponentAt(e.getPoint());
        
        if (moduleComponent == null)
        {
            return false;
        }
        startSlot = ModuleComponentUtils.computeStartSlot(e, 
            Arrays.asList(moduleComponent));
        
        //System.out.println("start is "+startSlot);
        
        if (startSlot != null)
        {
            validEndSlots = SlotUtils.computeCompatibleEndSlots(startSlot,
                flowEditor.getFlow().getModules());
            //System.out.println("Valid targets for "+startSlot.getActualType()+" are "+validEndSlots);
            flowEditorComponent.highlightInputSlots(
                startSlot, validEndSlots);
            return true;
        }
        
        endSlot = ModuleComponentUtils.computeEndSlot(e,
            flowEditorComponent.getModuleComponents());
        //System.out.println("Pressed, end is "+endSlot);
        if (endSlot != null && endSlot.getInputLink() == null)
        {
            validStartSlots = SlotUtils.computeCompatibleStartSlots(endSlot,
                flowEditor.getFlow().getModules());
            //System.out.println("Valid sources for "+endSlot.getActualType()+" are "+validStartSlots);
            flowEditorComponent.highlightOutputSlots(
                endSlot, validStartSlots);
            return true;
        }
        
        return false;
    }

    /**
     * Check whether the mouse was pressed on the {@link InputSlot} of
     * a {@link Module}. If it was pressed on the {@link InputSlot} of
     * a {@link Module}, and this {@link InputSlot} has an attached
     * {@link Link}, then this {@link Link} will be removed, and it will
     * be pretended that the process of creating a {@link Link} is
     * still ongoing. This allows the user to "pull out" the {@link Link}
     * from an {@link InputSlot}
     * 
     * @param e The mouse event
     * @return Whether the link removal has started
     */
    private boolean tryStartLinkRemoval(MouseEvent e)
    {
        ModuleComponent moduleComponent = 
            flowEditorComponent.getModuleComponentAt(e.getPoint());
        
        if (moduleComponent == null)
        {
            return false;
        }
        InputSlot endSlotForRemoval = ModuleComponentUtils.computeEndSlot(e,
            flowEditorComponent.getModuleComponents());
        
        //System.out.println("Pressed, removing link to "+endSlotForRemoval);
        
        if (endSlotForRemoval != null && 
            endSlotForRemoval.getInputLink() != null)
        {
            Link link = endSlotForRemoval.getInputLink();
            flowEditor.removeLink(link);
            startSlot = link.getSourceSlot();
            validEndSlots = SlotUtils.computeCompatibleEndSlots(startSlot,
                flowEditor.getFlow().getModules());
            //System.out.println("Valid targets for "+startSlot.getActualType()+" are "+validEndSlots);
            flowEditorComponent.highlightInputSlots(
                startSlot, validEndSlots);
            updateCurrentLineFromStartSlot(e);
            return true;
        }
        
        return false;
    }
    
    
    /**
     * Checks whether the mouse was pressed on the title bar of a 
     * {@link ModuleComponent}, and prepare dragging the {@link Module} objects 
     * in this case.
     * 
     * @param e The mouse event
     * @return Whether dragging of {@link Module} objects has started
     */
    private boolean tryStartModuleDragging(MouseEvent e)
    {
        // Only proceed when the mouse was pressed on a ModuleComponent
        ModuleComponent pressedOnModuleComponent = 
            flowEditorComponent.getModuleComponentAt(e.getPoint());
        if (pressedOnModuleComponent == null)
        {
            return false;
        }
        
        // Only proceed if the mouse was pressed on the title
        // bar of a ModuleComponent
        if (!isOnTitleBar(e, pressedOnModuleComponent))
        {
            return false;
        }

        dragStartPosition = e.getPoint();
        draggedModuleComponents = new LinkedHashSet<ModuleComponent>();
        draggedModuleComponentLocations = 
            new LinkedHashMap<ModuleComponent, Point>();

        Module module = pressedOnModuleComponent.getModule();
        if (flowEditor.isModuleSelected(module))
        {
            // If the mouse was pressed on a selected module,
            // then ALL selected modules will be dragged
            for (Module m : flowEditor.getSelectedModules())
            {
                ModuleComponent moduleComponent = 
                    flowEditorComponent.getModuleComponent(m); 
                draggedModuleComponents.add(moduleComponent);
                draggedModuleComponentLocations.put(moduleComponent,
                    moduleComponent.getLocation());
            }
        }
        else
        {
            // If the mouse was pressed on an UNselected module,
            // then only this module will be dragged
            draggedModuleComponents.add(pressedOnModuleComponent);
            draggedModuleComponentLocations.put(pressedOnModuleComponent,
                pressedOnModuleComponent.getLocation());
        }
        directlyDraggedModuleComponent = pressedOnModuleComponent;
        directlyDraggedModuleComponent.addComponentListener(
            dragMovementListener);
        return true;
    }

    /**
     * Returns whether the given event appeared on the title bar of the
     * given {@link ModuleComponent}
     * 
     * @param e The event
     * @param moduleComponent The {@link ModuleComponent}
     * @return Whether the event was on the title bar
     */
    private boolean isOnTitleBar(MouseEvent e, ModuleComponent moduleComponent)
    {
        Point globalPoint = e.getPoint();
        Point localPoint = SwingUtilities.convertPoint(
            e.getComponent(), globalPoint, moduleComponent);
        JComponent titleBar = moduleComponent.getTitleBar();
        
        //System.out.println("Pressed on "+e.getComponent());
        //System.out.println("Local point "+localPoint);
        //System.out.println("Title bar "+titleBar.getBounds());
        
        return titleBar.getBounds().contains(localPoint);
    }

    /**
     * Attempts to start the creation of a selection rectangle. This will
     * be the case if the mouse was NOT pressed on a {@link Link}.
     * (Thus, this method assumes that it was already ensured that the
     * mouse was not pressed on a {@link ModuleComponent})
     * 
     * @param e The mouse event
     * @return Whether the creation of a selection rectangle started
     */
    private boolean tryStartRectangleSelection(MouseEvent e)
    {
        Link pressedOnLink = 
            flowEditorComponent.getLinkAt(e.getPoint());
        if (pressedOnLink != null)
        {
            return false;
        }
        DragPanel dragPanel = flowEditorComponent.getDragPanel();            
        selectionStartPosition = SwingUtilities.convertPoint(
            e.getComponent(), e.getPoint(), dragPanel);
        selectionRectangle = 
            new Rectangle2D.Double(
                selectionStartPosition.getX(), 
                selectionStartPosition.getY(), 1, 1);
        dragPanel.setSelectionRectangle(selectionRectangle);
        return true;
    }
    
    
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        e = MoreSwingUtilities.convertMouseEvent(
            e.getComponent(), e, flowEditorComponent);

        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
        {
            if (draggedModuleComponents != null)
            {
                flowEditorComponent.repaint();
            }
            else if (startSlot != null)
            {
                updateCurrentLineFromStartSlot(e);
            }
            else if (endSlot != null)
            {
                updateCurrentLineFromEndSlot(e);
            }
            else if (selectionRectangle != null)
            {
                updateSelectionRectangleTo(e);
            }
        }
        else if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0)
        {
            if (draggingViewport)
            {
                Rectangle bounds = flowEditorComponent.getBounds();
                int dx = e.getX() - previousPosition.x;
                int dy = e.getY() - previousPosition.y;
                bounds.x += dx;
                bounds.y += dy;
                flowEditorComponent.setBounds(bounds);
                
                // Return here. The previousPosition will stay the
                // same while dragging the viewport!
                return;
            }
        }

        previousPosition.setLocation(e.getPoint());
        if (!autoscrollTimer.isRunning())
        {
            autoscrollTimer.start();   
        }
    }
    
    /**
     * Update the current selectionRectangle while the mouse is
     * dragged, according to the selectionStartPosition and the 
     * given event
     *  
     * @param e The mouse event
     */
    private void updateSelectionRectangleTo(MouseEvent e)
    {
        DragPanel dragPanel = flowEditorComponent.getDragPanel();            
        Point point = SwingUtilities.convertPoint(e.getComponent(), 
            e.getPoint(), dragPanel);
        double xMin = Math.min(point.getX(), selectionStartPosition.getX()); 
        double yMin = Math.min(point.getY(), selectionStartPosition.getY()); 
        double xMax = Math.max(point.getX(), selectionStartPosition.getX()); 
        double yMax = Math.max(point.getY(), selectionStartPosition.getY()); 
        selectionRectangle = new Rectangle2D.Double(
            xMin, yMin, xMax-xMin, yMax-yMin);
        dragPanel.setSelectionRectangle(selectionRectangle);
    }

    /**
     * Update the line that indicates a {@link Link} that is currently
     * being created while the mouse is dragged.  This method will update 
     * the line according to the startSlot where the {@link Link} creation 
     * started, and the current mouse position
     * 
     * @param e The mouse event
     */
    private void updateCurrentLineFromStartSlot(MouseEvent e)
    {
        LinkShapeProvider linkShapeProvider =
            flowEditorComponent.getLinkShapeProvider();

        Color currentLineColor = CREATING_LINK_COLOR;
        Point2D currentLineStart = 
            linkShapeProvider.centerOfOutput(startSlot);
        Point2D currentLineEnd = e.getPoint();

        InputSlot potentialEndSlot = ModuleComponentUtils.computeEndSlot(
            e, flowEditorComponent.getModuleComponents());
        if (potentialEndSlot != null)
        {
            if (validEndSlots.contains(potentialEndSlot))
            {
                currentLineEnd = 
                    linkShapeProvider.centerOfInput(
                        potentialEndSlot);
                currentLineColor = VALID_LINK_COLOR;
            }
            else
            {
                TypeContext typeContext = 
                    flowEditor.getFlow().getTypeContext();
                boolean debugPrint = false;
                debugPrint = true;
                if (debugPrint)
                {
                    Type actualType = startSlot.getActualType();
                    Type expectedType = potentialEndSlot.getExpectedType();
                    System.out.println("Not assignable:");
                    System.out.println("    Type "+Types.debugStringFor(expectedType));
                    System.out.println("    from "+Types.debugStringFor(actualType));
                    System.out.println("Result : " + 
                    typeContext.isAssignable(actualType, expectedType));
                }
                    
                currentLineColor = INVALID_LINK_COLOR;
            }
        }
        //System.out.println("line start "+currentLineStart);
        //System.out.println("line end   "+currentLineEnd);

        LinksPanel linksPanel = flowEditorComponent.getLinksPanel();
        linksPanel.setCurrentLine(
            new Line2D.Double(currentLineStart, currentLineEnd), 
            currentLineColor);
    }


    /**
     * Update the line that indicates a {@link Link} that is currently
     * being created while the mouse is dragged. This method will update 
     * the line according to the endSlot where the {@link Link} creation 
     * started, and the current mouse position
     * 
     * @param e The mouse event
     */
    private void updateCurrentLineFromEndSlot(MouseEvent e)
    {
        LinkShapeProvider linkShapeProvider =
            flowEditorComponent.getLinkShapeProvider();

        Color currentLineColor = CREATING_LINK_COLOR;
        Point2D currentLineStart = e.getPoint();
        Point2D currentLineEnd = 
            linkShapeProvider.centerOfInput(endSlot);

        OutputSlot potentialStartSlot = ModuleComponentUtils.computeStartSlot(
            e, flowEditorComponent.getModuleComponents());
        if (potentialStartSlot != null)
        {
            if (validStartSlots.contains(potentialStartSlot))
            {
                currentLineStart = 
                    linkShapeProvider.centerOfOutput(
                        potentialStartSlot);
                currentLineColor = VALID_LINK_COLOR;
            }
            else
            {
                currentLineColor = INVALID_LINK_COLOR;
            }
        }
        //System.out.println("line start "+currentLineStart);
        //System.out.println("line end   "+currentLineEnd);

        LinksPanel linksPanel = flowEditorComponent.getLinksPanel();
        linksPanel.setCurrentLine(
            new Line2D.Double(currentLineStart, currentLineEnd), 
            currentLineColor);
    }
    

    @Override
    public void mouseMoved(MouseEvent e)
    {
        updateCursor(e);

        e = MoreSwingUtilities.convertMouseEvent(
            e.getComponent(), e, flowEditorComponent);

        LinksPanel linksPanel = flowEditorComponent.getLinksPanel();
        Link link = flowEditorComponent.getLinkAt(e.getPoint());
        linksPanel.setHighlightedLink(link);
        previousPosition.setLocation(e.getPoint());
    }
    
    /**
     * Update the cursor according to the given mouse event
     * 
     * @param originalMouseEvent The mouse event
     */
    private void updateCursor(MouseEvent originalMouseEvent)
    {
        DragPanel dragPanel = flowEditorComponent.getDragPanel();            
        Component source = originalMouseEvent.getComponent();
        dragPanel.setCursor(source.getCursor());
    }
    

    @Override
    public void mouseClicked(MouseEvent e)
    {
        //System.out.println("mouseClicked "+e);
        
        e = MoreSwingUtilities.convertMouseEvent(
            e.getComponent(), e, flowEditorComponent);
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            FlowEditorSelectionUtils.updateSelectionForClickAt(
                flowEditorComponent, flowEditor, e);
        }
        else if (e.getButton() == MouseEvent.BUTTON3)
        {
            ModuleComponent clickedModuleComponent = 
                flowEditorComponent.getModuleComponentAt(e.getPoint());
            if (clickedModuleComponent != null)
            {
                if (isOnTitleBar(e, clickedModuleComponent))
                {
                    Point location = SwingUtilities.convertPoint(
                        e.getComponent(), e.getPoint(), clickedModuleComponent);
                    popupMenuHandler.handleModuleComponentPopupMenu(
                        clickedModuleComponent, location);
                }
            }
        }
        previousPosition.setLocation(e.getPoint());
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        //System.out.println("mouseReleased on "+e.getSource());
        
        e = MoreSwingUtilities.convertMouseEvent(
            e.getComponent(), e, flowEditorComponent);

        if (startSlot != null || endSlot != null)
        {
            tryLinkCreationOnRelease(e);
        }
        else if (selectionRectangle != null)
        {
            updateSelectionFromRectangleFromRelease(e);
        }
        else if (draggedModuleComponents != null)
        {
            Point delta = new Point();
            delta.x = e.getX() - dragStartPosition.x;
            delta.y = e.getY() - dragStartPosition.y;
            
            //System.out.println("DragStart "+dragStartPosition);
            //System.out.println("to        "+e.getPoint());
            //System.out.println("delta     "+delta);
            
            if (delta.x != 0 || delta.y != 0)
            {
                Set<Module> movedModules = new LinkedHashSet<Module>();
                for (ModuleComponent moduleComponent : draggedModuleComponents)
                {
                    movedModules.add(moduleComponent.getModule());
                }
                flowEditor.moveModules(movedModules, delta);
            }
        }

        cleanupForMouseRelease();
        previousPosition.setLocation(e.getPoint());
    }
    
    /**
     * If either the startSlot or the endSlot are non-null, then this
     * method will check whether releasing the mouse as in the given
     * event will cause the creation of a new {@link Link}, and create
     * the {@link Link} if it describes a valid connection.
     * 
     * @param e The mouse event
     */
    private void tryLinkCreationOnRelease(MouseEvent e)
    {
        if (startSlot != null)
        {
            InputSlot potentialEndSlot = ModuleComponentUtils.computeEndSlot(
                e, flowEditorComponent.getModuleComponents());
            if (potentialEndSlot != null)
            {
                if (validEndSlots.contains(potentialEndSlot))
                {
                    endSlot = potentialEndSlot;
                }
            }
        }
        else if (endSlot != null)
        {
            OutputSlot potentialStartSlot = 
                ModuleComponentUtils.computeStartSlot(
                    e, flowEditorComponent.getModuleComponents());
            if (potentialStartSlot != null)
            {
                if (validStartSlots.contains(potentialStartSlot))
                {
                    startSlot = potentialStartSlot;
                }
            }
        }
            
        if (startSlot != null && endSlot != null)
        {
            boolean printLinkCreationInfo = false;
            //printLinkCreationInfo = true;
            if (printLinkCreationInfo)
            {
                printLinkCreationInfo();
            }
            
            Link link = Links.create(startSlot, endSlot);
            flowEditor.addLink(link);
        }
    }
    
    /**
     * Print debug information about the link that is about
     * to be created according to the current (non-null)
     * startSlot and endSlot
     */
    private void printLinkCreationInfo()
    {
        Module startModule = startSlot.getModule();
        int startIndex = startSlot.getIndex();

        Module endModule = endSlot.getModule();
        int endIndex = endSlot.getIndex();

        System.out.println("Create link between");
        System.out.println("    " + startModule);
        System.out.println("    " + startSlot);
        System.out.println("and " + endModule);
        System.out.println("    " + endSlot);
        Link link = Links.create(
            startModule, startIndex, endModule, endIndex);
        TypeContext typeContext = flowEditor.getFlow().getTypeContext();
        System.out.println("Is valid? " + Links.isValid(typeContext, link));
    }
    

    
    /**
     * Update the selection of {@link Link} objects and {@link Module} objects
     * according to a mouse release as of the given event, assuming
     * that this mouse release finished the selection using a 
     * selectionRectangle
     * 
     * @param e The mouse event
     */
    private void updateSelectionFromRectangleFromRelease(MouseEvent e)
    {
        Rectangle r = new Rectangle(
            (int)selectionRectangle.getX(), 
            (int)selectionRectangle.getY(),
            (int)selectionRectangle.getWidth(),
            (int)selectionRectangle.getHeight());
        DragPanel dragPanel = flowEditorComponent.getDragPanel();            
        Rectangle converted = SwingUtilities.convertRectangle(
            dragPanel, r, flowEditorComponent);
        
        FlowEditorSelectionUtils.updateSelectionFromRectangle(
            flowEditorComponent, flowEditor, converted, e);
    }

    
    
    /**
     * Cleanup the state describing all possible actions when
     * the mouse was released
     */
    private void cleanupForMouseRelease()
    {
        draggingViewport = false;
        autoscrollTimer.stop();
        
        if (directlyDraggedModuleComponent != null)
        {
            directlyDraggedModuleComponent.removeComponentListener(
                dragMovementListener);
        }
        directlyDraggedModuleComponent = null;
        draggedModuleComponents = null;
        dragStartPosition = null;
        LinksPanel linksPanel = flowEditorComponent.getLinksPanel();
        linksPanel.setCurrentLine(null, null);

        selectionRectangle = null;
        selectionStartPosition = null;
        DragPanel dragPanel = flowEditorComponent.getDragPanel();            
        dragPanel.setSelectionRectangle(null);

        startSlot = null;
        endSlot = null;

        validEndSlots = null;
        flowEditorComponent.highlightInputSlots(null, null);
        
        validStartSlots = null;
        flowEditorComponent.highlightOutputSlots(null, null);
        
        dragPanel.setCursor(null);
        
    }


    @Override
    public void mouseEntered(MouseEvent e)
    {
        updateCursor(e);

        e = MoreSwingUtilities.convertMouseEvent(
            e.getComponent(), e, flowEditorComponent);
        previousPosition.setLocation(e.getPoint());
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        updateCursor(e);
        
        e = MoreSwingUtilities.convertMouseEvent(
            e.getComponent(), e, flowEditorComponent);
        previousPosition.setLocation(e.getPoint());
    }
    
}



