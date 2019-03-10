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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import de.javagl.common.ui.MoreSwingUtilities;
import de.javagl.flow.Flow;
import de.javagl.flow.FlowEvent;
import de.javagl.flow.FlowListener;
import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.link.Link;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleExecutionListener;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.Slot;
import de.javagl.flow.module.view.ModuleView;
import de.javagl.flow.module.view.ModuleViewTypes;
import de.javagl.flow.repository.Repository;
import de.javagl.flow.workspace.FlowLayout;
import de.javagl.flow.workspace.FlowLayoutEvent;
import de.javagl.flow.workspace.FlowLayoutListener;
import de.javagl.flow.workspace.FlowWorkspace;
import de.javagl.selection.SelectionEvent;
import de.javagl.selection.SelectionListener;
import de.javagl.selection.SelectionModel;

/**
 * The component showing a {@link FlowEditor}. This component is usually 
 * embedded in a {@link FlowEditorViewport}, and virtually infinitely large. 
 * Whenever modifications are performed in the {@link FlowEditor} (like 
 * adding or selecting a {@link Module} or a {@link Link}) this component 
 * will be updated accordingly.
 */
final class FlowEditorComponent extends JPanel implements ModuleComponentOwner
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(FlowEditorComponent.class.getName());
    
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 2414688711161481446L;

    /**
     * The color for highlighting {@link InputSlot} objects and 
     * {@link OutputSlot} objects in a {@link ModuleComponent} 
     */
    private static final Color HIGHLIGHT_COLOR = new Color(190, 255, 190);

    /**
     * The color for the slot where a {@link Link} creation started
     */
    private static final Color ORIGIN_SLOT_COLOR = new Color(190, 190, 190);
    
    /**
     * The (semi-transparent) color that will be used for
     * shading a {@link ModuleComponent} that does not contain
     * any highlighted slots
     */
    private static final Color SHADE_COLOR = new Color(255,255,255,190);
    
    /**
     * The border color that a {@link ModuleComponent} should have
     * when its {@link Module} is selected
     */
    private static final Color SELECTED_MODULE_BORDER_COLOR = Color.RED;
    
    /**
     * The border color that a {@link ModuleComponent} should have
     * when its {@link Module} is not selected
     */
    private static final Color DEFAULT_BORDER_COLOR = Color.WHITE;
    
    /**
     * The border size for auto-scrolling. When the mouse is dragged
     * into this border during {@link Link} creation, this component
     * will scroll inside its {@link FlowEditorViewport} accordingly
     */
    private static final int AUTOSCROLL_BORDER_SIZE = 25;
    
    /**
     * The desktop pane contained in this panel. Layer 0 contains
     * the {@link ModuleComponent} objects for the {@link Module} objects 
     * of the {@link Flow}. The JLayeredPane.DRAG_LAYER contains
     * the {@link DragPanel}. Layer -1 contains the {@link LinksPanel}.
     */
    private final JDesktopPane desktopPane;

    /**
     * The {@link DragPanel} in the JLayeredPane.DRAG_LAYER of the
     * desktop pane, containing the selection rectangle and the
     * drop preview.
     */
    private final DragPanel dragPanel;
    
    /**
     * The {@link LinksPanel} in layer -1 of the desktop pane,
     * painting the links. 
     */
    private final LinksPanel linksPanel;
    
    /**
     * The {@link LinkShapeProvider} that provides the shapes
     * for the {@link Link} objects between the {@link ModuleComponent}
     * objects
     */
    private final LinkShapeProvider linkShapeProvider;
    
    /**
     * A map from each {@link Module} to its {@link ModuleComponent}
     */
    private final Map<Module, ModuleComponent> moduleToModuleComponent;
    
    /**
     * A map from each {@link Module} to a {@link ModuleExecutionListener}
     * that updates the status description in the {@link ModuleComponent} 
     * according to the execution status of the {@link Module}
     */
    private final Map<Module, ModuleExecutionListener> 
        statusModuleExecutionListeners;
    
    /**
     * The {@link FlowEditor} which is shown in this panel
     */
    private FlowEditor flowEditor;
    
    /**
     * The {@link Repository} of {@link ModuleCreator} objects. When a 
     * {@link Module} is added, then this repository will be queried
     * for the matching {@link ModuleCreator}, which in turn will be
     * used to create the {@link ModuleView} instances that go into
     * the {@link ModuleComponent}.
     */
    private final Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository;
    
    /**
     * The controller for the {@link FlowEditor} that is
     * driven through this panel  
     */
    private final FlowEditorControl flowEditorControl; 
    
    /**
     * The key stroke for the 'delete' key
     */
    private final KeyStroke deleteKeyStroke = 
        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true);    
    
    
    /**
     * The action to delete all selected elements
     */
    private final Action deleteSelectedAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -4749576494668345243L;

        @Override
        public void actionPerformed(ActionEvent e)
        {
            flowEditor.deleteSelected();
            FlowEditorComponent.this.requestFocusInWindow();
        }
    }; 
    
    
    /**
     * The {@link SelectionListener} that will be attached to the
     * {@link FlowWorkspace#getModuleSelectionModel() module selection model}
     * of the {@link FlowWorkspace}. For all selected {@link Module} objects,
     * the corresponding {@link ModuleComponent} will be highlighted. 
     */
    private final SelectionListener<Module> moduleSelectionListener = 
        new SelectionListener<Module>()
    {
        @Override
        public void selectionChanged(SelectionEvent<Module> selectionEvent)
        {
            for (Module module : selectionEvent.getAddedElements())
            {
                ModuleComponent moduleComponent = 
                    getModuleComponent(module);
                moduleComponent.setBorderColor(
                    SELECTED_MODULE_BORDER_COLOR);
            }
            for (Module module : selectionEvent.getRemovedElements())
            {
                ModuleComponent moduleComponent = 
                    getModuleComponent(module);
                moduleComponent.setBorderColor(
                    DEFAULT_BORDER_COLOR);
            }
            repaint();
        }
    };
    
    /**
     * The {@link SelectionListener} that will be attached to the
     * {@link FlowWorkspace#getLinkSelectionModel() link selection model}
     * of the {@link FlowWorkspace}. Changes in the selection will trigger
     * a repaint, so that the {@link LinksPanel} will paint the new
     * selection state of the {@link Link} objects.  
     */
    private final SelectionListener<Link> linkSelectionListener = 
        new SelectionListener<Link>()
    {
        @Override
        public void selectionChanged(SelectionEvent<Link> selectionEvent)
        {
            repaint();
        }
    };
        
    /**
     * The {@link FlowListener} that will be attached
     * to the {@link Flow} of the {@link FlowEditor}, and 
     * update the view state in this panel depending on the changes 
     * (e.g. added links or removed modules) in the {@link Flow}.
     */
    private final FlowListener flowListener = 
        new FlowListener()
    {
        @Override
        public void moduleAdded(FlowEvent flowEvent)
        {
            ModuleComponent moduleComponent = createModuleComponent(
                flowEvent.getModule());
            Dimension d = moduleComponent.getPreferredSize();
            Rectangle2D worldBounds = new Rectangle(0,0,d.width,d.height);
            addModuleComponent(moduleComponent, worldBounds);
        }

        @Override
        public void moduleRemoved(FlowEvent flowEvent)
        {
            removeModuleComponent(flowEvent.getModule());
        }

        @Override
        public void linkAdded(FlowEvent flowEvent)
        {
            linksPanel.setHighlightedLink(null);
            repaint();
        }

        @Override
        public void linkRemoved(FlowEvent flowEvent)
        {
            linksPanel.setHighlightedLink(null);
            repaint();
        }
    };
    
    
    /**
     * The {@link FlowLayoutListener} that will be attached to the
     * {@link FlowLayout} of the {@link FlowWorkspace}. Changes in
     * the layout will cause the {@link ModuleComponent} bounds
     * to be updated accordingly.  
     */
    private final FlowLayoutListener flowLayoutListener = 
        new FlowLayoutListener()
    {
        @Override
        public void flowLayoutChanged(
            FlowLayoutEvent flowLayoutEvent)
        {
            Module module = flowLayoutEvent.getModule();
            ModuleComponent moduleComponent = getModuleComponent(module);

            Rectangle2D worldBounds = 
                flowLayoutEvent.getNewBounds();
            if (worldBounds != null)
            {
                Rectangle r = computeComponentBounds(worldBounds);

                //System.out.println("Bounds of  "+module);
                //System.out.println("   old     "+oldWorldBounds.get(module));
                //System.out.println("   new     "+newWorldBounds.get(module));
                //System.out.println(" local old "+moduleComponent.getBounds());
                //System.out.println(" local new "+r);

                moduleComponent.setBounds(r);
                repaint();
            }
        }
    };
    
    /**
     * The listener that will be attached to each {@link ModuleComponent},
     * and cause a repaint of the overview when the {@link ModuleComponent}
     * is moved, or when the mouse is moved
     */
    private RepaintingListener repaintingListener;
    
    /**
     * This class is a ComponentListener that may be attached to each 
     * {@link ModuleComponent} and cause an update of the layout whenever 
     * a {@link ModuleComponent} is resized automatically. Note that this 
     * is ONLY intended for automatic resizes, which may happen when the
     * contents of a component changes, and its size is adapted 
     * automatically to this change. So these size changes do NOT cause
     * an UndoableEdit. This listener will be disabled during the 
     * manual resizing operations. The layout update for manual resizing
     * operations (which should cause an UndoableEdit) are handled in the 
     * DesktopManager. See 
     * {@link FlowEditorComponent#createDesktopManager(DesktopManager)}   
     * 
     * NOTE: It is not clear whether ModuleComponents should resize 
     * automatically. Formerly, they have been resizing when the
     * text in the "slot info labels" changed. Now, they do not 
     * resize due to such a change. But they still resize when the
     * preferred size of one of their ModuleViews changes. If this
     * functionality is also removed, this class may become obsolete.
     */
    private class LayoutUpdateListener extends ComponentAdapter
    {
        /**
         * Whether there is currently a manual resizing in progress
         */
        private boolean manualResizing = false;

        /**
         * Set whether there is currently a manual resizing in progress
         * 
         * @param manualResizing The flag
         */
        private void setManualResizing(boolean manualResizing)
        {
            this.manualResizing = manualResizing;
        }

        @Override
        public void componentResized(ComponentEvent e)
        {
            if (!manualResizing)
            {
                updateLayout(e);
            }
        }

        /**
         * Update the layout according to the given event
         * 
         * @param e The event
         */
        private void updateLayout(ComponentEvent e)
        {
            Component component = e.getComponent();
            if (component instanceof ModuleComponent)
            {
                ModuleComponent moduleComponent = (ModuleComponent)component;
                Rectangle componentBounds = moduleComponent.getBounds();
                Rectangle2D worldBounds = computeWorldBounds(componentBounds);
                Module module = moduleComponent.getModule();
                flowEditor.changeModuleLayoutAutomatically(
                    Collections.singletonMap(module, worldBounds));
            }
        }

    }
    
    /**
     * The {@link LayoutUpdateListener}
     */
    private final LayoutUpdateListener layoutUpdateListener = 
        new LayoutUpdateListener();
    

    /**
     * Creates a new {@link FlowEditorComponent}
     * 
     * @param moduleCreatorRepository The {@link Repository} of 
     * {@link ModuleCreator} objects
     */
    public FlowEditorComponent(
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository)
    {
        setFocusable(true);
        setLayout(new GridLayout(1,1));
        
        this.moduleCreatorRepository = moduleCreatorRepository;
        
        this.moduleToModuleComponent = 
            new LinkedHashMap<Module, ModuleComponent>();
        this.statusModuleExecutionListeners = 
            new LinkedHashMap<Module, ModuleExecutionListener>();
        
        this.linkShapeProvider = new DefaultLinkShapeProvider(this);
        
        desktopPane = createDesktopPane();
        
        dragPanel = new DragPanel(moduleCreatorRepository);
        desktopPane.add(dragPanel, JLayeredPane.DRAG_LAYER);
        
        // Create the panel that will paint the links
        linksPanel = new LinksPanel(linkShapeProvider);
        desktopPane.add(linksPanel, Integer.valueOf(-1));
        
        // Add the main control to this panel, which will capture
        // all mouse events and call the appropriate editing 
        // functions in the FlowEditor, causing UndoableEdit
        // instances to be created
        flowEditorControl = 
            new FlowEditorControl(this);
        desktopPane.addMouseListener(flowEditorControl);
        desktopPane.addMouseMotionListener(flowEditorControl);

        getInputMap(JComponent.WHEN_FOCUSED).put(
            deleteKeyStroke, "deleteSelected");
        getActionMap().put("deleteSelected", deleteSelectedAction);
        
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new PropertyChangeListener()
//        {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt)
//            {
//                if (evt.getPropertyName().equals("focusOwner"))
//                {
//                    System.out.println("Old : "+evt.getOldValue());
//                    System.out.println("New : "+evt.getNewValue());
//                }
//            }
//        });
        
        add(desktopPane);
    }
    
    
    
    /**
     * Create the main desktop pane that will contain the {@link LinksPanel}
     * and {@link DragPanel}, as well as the {@link ModuleComponent} objects. 
     * The DesktopManager of this DesktopPane will be the one created with
     * {@link #createDesktopManager(DesktopManager)}.
     *  
     * @return The main desktop pane
     */
    private JDesktopPane createDesktopPane()
    {
        final JDesktopPane desktopPane = new JDesktopPane()
        {
            /**
             * Serial UID
             */
            private static final long serialVersionUID = 6377021225140551978L;
            
            /**
             * The DesktopManager that is created with
             * {@link FlowEditorComponent#createDesktopManager(
             * DesktopManager)}
             */
            private DelegatingDesktopManager delegatingDesktopManager = null;

            // TODO Actually, this should be overridden like this,
            // but then resizing also causes an (empty) movement
            // Check what's wrong there...
//            @Override
//            public void setDesktopManager(DesktopManager d) 
//            {
//                if (delegatingDesktopManager == null)
//                {
//                    delegatingDesktopManager = createDesktopManager(d);
//                    super.setDesktopManager(delegatingDesktopManager);
//                }
//                else
//                {
//                    delegatingDesktopManager.setDelegate(d);
//                }
//            }
            
            @Override
            public DesktopManager getDesktopManager() 
            {
                final DesktopManager desktopManager = super.getDesktopManager();
                if (desktopManager == null)
                {
                    return null;
                }
                if (delegatingDesktopManager == null)
                {
                    delegatingDesktopManager = 
                        createDesktopManager(desktopManager);
                }
                return delegatingDesktopManager;
            }
            
            @Override
            protected void paintComponent(Graphics gr)
            {
                super.paintComponent(gr);
                Graphics2D g = (Graphics2D)gr;
                if (FlowEditorComponent.this.hasFocus())
                {
                    Rectangle r = computeVisibleRectangle();
                    g.setColor(Color.GRAY);
                    g.drawRect(r.x + 1, r.y + 1, r.width - 3, r.height - 3);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(r.x + 2, r.y + 2, r.width - 5, r.height - 5);
                }
            }
            
            
            @Override
            public String toString() 
            {
                return "FlowEditorComponent$JDestopPane"+
                    Integer.toHexString(System.identityHashCode(this));
            }
        };
        desktopPane.setOpaque(true);
        desktopPane.setBackground(Color.WHITE);
        desktopPane.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                int w = desktopPane.getWidth();
                int h = desktopPane.getHeight();
                linksPanel.setBounds(0, 0, w, h);
                dragPanel.setBounds(0, 0, w, h);
            }
        });
        addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                desktopPane.repaint();
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
                desktopPane.repaint();
            }
        });
        
        return desktopPane;
    }
    
    
    /**
     * Create a special DesktopManager for the desktopPane in this component.
     * This special DesktopManager will delegate all calls to another
     * DesktopManager, except for the one to maximize a frame. This call
     * will cause the frame to be made as large as the parent of this
     * component. The parent of this component is usually a 
     * {@link FlowEditorViewport}, so this will make the frame
     * as large as the part of this component that is currently visible
     * in the viewport.<br>
     * <br> 
     * Additionally, the returned DesktopManager will keep track of 
     * resizing operations, and convert them into (undoable) layout 
     * changes through the {@link FlowEditor} 
     * 
     * @param desktopManager The delegate DesktopManager
     * @return The new DesktopManager
     */
    private DelegatingDesktopManager createDesktopManager(
        DesktopManager desktopManager)
    {
        return new DelegatingDesktopManager(desktopManager)
        {
            @Override
            public void beginResizingFrame(JComponent f, int direction)
            {
                layoutUpdateListener.setManualResizing(true);
                flowEditorControl.setResizingModuleComponent(true);
                super.beginResizingFrame(f, direction);
            }
            
            @Override
            public void endResizingFrame(JComponent f)
            {
                if (f instanceof ModuleComponent)
                {
                    Rectangle2D newBounds = f.getBounds();
                    Rectangle2D newWorldBounds =
                        computeWorldBounds(newBounds);
                    ModuleComponent moduleComponent = (ModuleComponent)f;
                    Module module = moduleComponent.getModule();
                    flowEditor.changeModuleLayout(
                        Collections.singletonMap(module, newWorldBounds));
                }
                super.endResizingFrame(f);
                flowEditorControl.setResizingModuleComponent(false);
                layoutUpdateListener.setManualResizing(false);
            }
            
           @Override
            public void maximizeFrame(JInternalFrame f)
            {
                if (f.isIcon())
                {
                    try
                    {
                        f.setIcon(false);
                    }
                    catch (PropertyVetoException e2)
                    {
                        // Ignore
                    }
                }
                else
                {
                    f.setNormalBounds(f.getBounds());
                    
                    Component parent = 
                        FlowEditorComponent.this.getParent();
                    Rectangle parentRectangle = 
                        new Rectangle(new Point(0, 0), parent.getSize());
                    Rectangle bounds = SwingUtilities.convertRectangle(
                        parent, parentRectangle, desktopPane);
                    
                    //System.out.println("From rect "+parentRectangle);
                    //System.out.println("to bounds "+bounds);
                    setBoundsForFrame(f, bounds.x, bounds.y, 
                        bounds.width, bounds.height);
                }

                try
                {
                    f.setSelected(true);
                }
                catch (PropertyVetoException e2)
                {
                    // Ignore
                }
            }
        };
        
    }

    /**
     * If there is a frame selected and maximized in the desktopPane
     * of this component, its bounds will be set to the 
     * {@link #computeVisibleRectangle() visible rectangle}.
     * Otherwise, nothing will be done. This method is called whenever 
     * the {@link FlowEditorViewport} (which contains this component)
     * is resized.
     */
    void updateMaximizedModuleComponentBounds()
    {
        JInternalFrame f = desktopPane.getSelectedFrame();
        if (f == null)
        {
            return;
        }
        if (!f.isMaximum())
        {
            return;
        }
        Rectangle bounds = computeVisibleRectangle();
        desktopPane.getDesktopManager().setBoundsForFrame(f, 
            bounds.x, bounds.y, bounds.width, bounds.height);
    }
    
    /**
     * Compute the rectangle that is currently visible of this
     * component referring to its parent
     * 
     * @return The visible rectangle
     */
    private Rectangle computeVisibleRectangle()
    {
        Container parent = getParent();
        Rectangle parentBounds = 
            new Rectangle(0, 0, parent.getWidth(), parent.getHeight());
        Rectangle visibleRectangle = 
            SwingUtilities.convertRectangle(
                parent, parentBounds, this);
        return visibleRectangle;
    }

    
    /**
     * Return the {@link LinksPanel} 
     * 
     * @return The {@link LinksPanel}
     */
    LinksPanel getLinksPanel()
    {
        return linksPanel;
    }
    
    /**
     * Return the {@link DragPanel}
     *  
     * @return The {@link DragPanel}
     */
    DragPanel getDragPanel()
    {
        return dragPanel;
    }
    

    
    /**
     * Set the listener that will be attached to each {@link ModuleComponent},
     * and cause a repaint of the {@link FlowOverview} when the
     * {@link ModuleComponent} is moved, or when the mouse is moved
     * 
     * @param newRepaintingListener The listener
     */
    void setRepaintingListener(RepaintingListener newRepaintingListener)
    {
        if (this.repaintingListener != null)
        {
            desktopPane.removeMouseListener(this.repaintingListener);
            desktopPane.removeMouseWheelListener(this.repaintingListener);
            desktopPane.removeMouseMotionListener(this.repaintingListener);
        }
        this.repaintingListener = newRepaintingListener;
        if (this.repaintingListener != null)
        {
            desktopPane.addMouseListener(this.repaintingListener);
            desktopPane.addMouseWheelListener(this.repaintingListener);
            desktopPane.addMouseMotionListener(this.repaintingListener);
        }
    }
    
    
    /**
     * Set the given {@link FlowEditor} to be shown in this
     * component
     * 
     * @param newFlowEditor The new {@link FlowEditor}
     */
    void setFlowEditor(FlowEditor newFlowEditor)
    {
        if (flowEditor != null)
        {
            detachListeners(flowEditor.getFlowWorkspace());
            Flow flow = flowEditor.getFlow();
            for (Module module : flow.getModules())
            {
                removeModuleComponent(module);
            }
        }
        this.flowEditor = newFlowEditor;
        flowEditorControl.setFlowEditor(flowEditor);
        linksPanel.setFlowWorkspace(flowEditor.getFlowWorkspace());
        if (flowEditor != null)
        {
            attachListeners(flowEditor.getFlowWorkspace());
            Flow flow = flowEditor.getFlow();
            for (Module module : flow.getModules())
            {
                ModuleComponent moduleComponent = 
                    createModuleComponent(module);
                Rectangle2D worldBounds = 
                    flowEditor.getModuleBounds(module);
                addModuleComponent(moduleComponent, worldBounds);
            }
        }
    }
    
    /**
     * Detach all listeners that are stored in this class from the
     * elements of the given {@link FlowWorkspace}
     * 
     * @param flowWorkspace The {@link FlowWorkspace}
     */
    private void detachListeners(FlowWorkspace flowWorkspace)
    {
        Flow flow = flowWorkspace.getFlow(); 
        flow.removeFlowListener(flowListener);
        
        FlowLayout flowLayout = flowWorkspace.getFlowLayout();
        flowLayout.removeFlowLayoutListener(flowLayoutListener);
        
        SelectionModel<Module> moduleSelectionModel =
            flowWorkspace.getModuleSelectionModel();
        moduleSelectionModel.removeSelectionListener(moduleSelectionListener);
        
        SelectionModel<Link> linkSelectionModel = 
            flowWorkspace.getLinkSelectionModel();
        linkSelectionModel.removeSelectionListener(linkSelectionListener);
    }

    /**
     * Attach all listeners that are stored in this class to the
     * elements of the given {@link FlowWorkspace}
     * 
     * @param flowWorkspace The {@link FlowWorkspace}
     */
    private void attachListeners(FlowWorkspace flowWorkspace)
    {
        Flow flow = flowWorkspace.getFlow(); 
        flow.addFlowListener(flowListener);
        
        FlowLayout flowLayout = flowWorkspace.getFlowLayout();
        flowLayout.addFlowLayoutListener(flowLayoutListener);
        
        SelectionModel<Module> moduleSelectionModel =
            flowWorkspace.getModuleSelectionModel();
        moduleSelectionModel.addSelectionListener(moduleSelectionListener);
        
        SelectionModel<Link> linkSelectionModel = 
            flowWorkspace.getLinkSelectionModel();
        linkSelectionModel.addSelectionListener(linkSelectionListener);
    }
    
    
    /**
     * Return the {@link FlowEditor} that is controlled via this panel
     * 
     * @return The {@link FlowEditor}
     */
    FlowEditor getFlowEditor()
    {
        return flowEditor;
    }
    
    
    /**
     * Create the {@link ModuleComponent} for the given {@link Module}.
     * The resulting component will have a location of (0,0), and its
     * preferred size.<br>
     * <br>
     * This will only create the {@link ModuleComponent}. In order to
     * add it to this component, 
     * {@link #addModuleComponent(ModuleComponent, Rectangle2D)}
     * has to be called. 
     * 
     * @param module The {@link Module}
     * @return The {@link ModuleComponent}
     */
    ModuleComponent createModuleComponent(Module module)
    {
        ModuleCreator moduleCreator = 
            moduleCreatorRepository.get(module.getModuleInfo());

        // TODO Currently, only the two default module view types 
        // are supported here
        ModuleView configurationView = null; 
        ModuleView visualizationView = null;
        if (moduleCreator == null)
        {
            logger.severe("No ModuleCreator found in repository "
                + "for the following ModuleInfo:");
            logger.severe(ModuleInfos.createModuleInfoString(
                module.getModuleInfo()));
        }
        else
        {
            configurationView = moduleCreator.createModuleView(
                ModuleViewTypes.CONFIGURATION_VIEW); 
            visualizationView = moduleCreator.createModuleView(
                ModuleViewTypes.VISUALIZATION_VIEW); 
        }
        
        ModuleComponent moduleComponent = 
            new ModuleComponent(configurationView, visualizationView);
        moduleComponent.setModule(module);
        Dimension size = moduleComponent.getPreferredSize();
        moduleComponent.setSize(size);
        return moduleComponent;
    }
    
    
    /**
     * Called by the {@link #flowListener} when a {@link Module}
     * was added to the {@link Flow} via the {@link FlowEditor}. 
     * It will add the give module component to the desktop pane
     * and set up the necessary listener connections.
     * 
     * @param moduleComponent The {@link ModuleComponent} that was added
     * @param worldBounds The bounds of the module, in world coordinates
     */
    private void addModuleComponent(ModuleComponent moduleComponent, 
        Rectangle2D worldBounds)
    {
        Module module = moduleComponent.getModule();
        
        Rectangle r = computeComponentBounds(worldBounds);
        moduleComponent.setBounds(r);

        moduleToModuleComponent.put(module, moduleComponent);
        desktopPane.add(moduleComponent);
        moduleComponent.toFront();

        moduleComponent.addComponentListener(layoutUpdateListener);
        if (repaintingListener != null)
        {
            moduleComponent.addComponentListener(repaintingListener);
            moduleComponent.addMouseListener(repaintingListener);
            moduleComponent.addMouseMotionListener(repaintingListener);
        }
        moduleComponent.addMouseListener(flowEditorControl);
        moduleComponent.addMouseMotionListener(flowEditorControl);
        
        InternalFrameUI currentUI = moduleComponent.getUI();
        if (currentUI instanceof BasicInternalFrameUI)
        {
            BasicInternalFrameUI basicUI = (BasicInternalFrameUI) currentUI;
            JComponent northPane = basicUI.getNorthPane();
            northPane.addMouseListener(flowEditorControl);
            northPane.addMouseMotionListener(flowEditorControl);
        }
        else
        {
            // This should never happen for the built-in LaFs
            logger.warning("UI of ModuleComponent is no BasicInternalFrameUI");
        }
        
        ModuleExecutionListener statusListener = 
            new StatusModuleExecutionListener(moduleComponent);
        statusModuleExecutionListeners.put(module, statusListener);
        module.addModuleExecutionListener(statusListener);
        
        repaint();
    }
    
    
    /**
     * Called by the {@link #flowListener} when a {@link Module}
     * was removed from the {@link Flow} via the {@link FlowEditor}. 
     * This will remove the corresponding {@link ModuleComponent}.
     * 
     * @param module The {@link Module} that was removed
     */
    private void removeModuleComponent(Module module)
    {
        ModuleComponent moduleComponent = moduleToModuleComponent.get(module);
        desktopPane.remove(moduleComponent);
        moduleToModuleComponent.remove(module);
        
        moduleComponent.removeComponentListener(layoutUpdateListener);
        if (repaintingListener != null)
        {
            moduleComponent.removeComponentListener(repaintingListener);
            moduleComponent.removeMouseListener(repaintingListener);
            moduleComponent.removeMouseMotionListener(repaintingListener);
        }
        moduleComponent.removeMouseListener(flowEditorControl);
        moduleComponent.removeMouseMotionListener(flowEditorControl);
        
        InternalFrameUI currentUI = moduleComponent.getUI();
        if (currentUI instanceof BasicInternalFrameUI)
        {
            BasicInternalFrameUI basicUI = (BasicInternalFrameUI) currentUI;
            JComponent northPane = basicUI.getNorthPane();
            northPane.removeMouseListener(flowEditorControl);
            northPane.removeMouseMotionListener(flowEditorControl);
        }
        else
        {
            // This should never happen for the built-in LaFs
            logger.warning("UI of ModuleComponent is no BasicInternalFrameUI");
        }

        ModuleExecutionListener statusListener = 
            statusModuleExecutionListeners.remove(module);
        module.removeModuleExecutionListener(statusListener);
        
        repaint();
    }
    
    
    
    
    /**
     * Convert the given bounds from world coordinates into the local 
     * coordinates of this (usually infinitely large) component. 
     * The world position (0,0) will correspond to the center of this 
     * component.
     * 
     * @param worldBounds The bounds, in world coordinates
     * @return The bounds in coordinates local to the
     * center of this component
     */
    private Rectangle computeComponentBounds(
        Rectangle2D worldBounds)
    {
        Rectangle r = new Rectangle(
            (int)worldBounds.getX() + getWidth() / 2,
            (int)worldBounds.getY() + getHeight() / 2,
            (int)worldBounds.getWidth(),
            (int)worldBounds.getHeight());
        return r;
    }

    /**
     * Convert the given bounds from local coordinates of this (usually 
     * virtually infinitely large) component into world coordinates. 
     * The world position (0,0) will correspond to the center of this 
     * component.
     * 
     * @param componentBounds The bounds, in component coordinates
     * @return The bounds in world coordinates
     */
    private Rectangle computeWorldBounds(
        Rectangle2D componentBounds)
    {
        Rectangle r = new Rectangle(
            (int)componentBounds.getX() - getWidth() / 2,
            (int)componentBounds.getY() - getHeight() / 2,
            (int)componentBounds.getWidth(),
            (int)componentBounds.getHeight());
        return r;
    }
    
    
    /**
     * Returns the {@link ModuleComponent} with the highest layer at the
     * given position.
     * 
     * @param p The position
     * @return The {@link ModuleComponent} with the highest layer at the
     * given position, or <code>null</code> if there is no
     * {@link ModuleComponent}
     */
    ModuleComponent getModuleComponentAt(Point2D p)
    {
        Point2D converted = 
            MoreSwingUtilities.convertPoint(this, p, desktopPane);
        int maxLayer = Integer.MIN_VALUE;
        ModuleComponent result = null;
        for (Component component : desktopPane.getComponentsInLayer(0))
        {
            if (component instanceof ModuleComponent)
            {
                ModuleComponent moduleComponent = (ModuleComponent)component;
                if (moduleComponent.getBounds().contains(converted))
                {
                    if (moduleComponent.getLayer() > maxLayer)
                    {
                        maxLayer = moduleComponent.getLayer();
                        result = moduleComponent;
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Returns the first {@link Link} that is close to the given
     * position, within a small threshold. 
     *  
     * @param p The position
     * @return The first {@link Link} that is found
     */
    Link getLinkAt(Point2D p)
    {
        final int threshold = 10;
        double minDistanceSquared = threshold;
        Link closest = null;
        
        for (Link link : flowEditor.getFlow().getLinks())
        {
            Shape shape = linkShapeProvider.shapeForLink(link);
            Rectangle bounds = shape.getBounds();
            bounds.x -= threshold;
            bounds.y -= threshold;
            bounds.width += threshold * 2;
            bounds.height += threshold * 2;

            if (bounds.contains(p))
            {
                List<Line2D> lines = Shapes.computeLineSegments(shape, 0.5);
                for (int i=0; i<lines.size(); i++)
                {
                    Line2D line = lines.get(i);
                    double distanceSquared = line.ptSegDistSq(p);
                    if (distanceSquared < minDistanceSquared)
                    {
                        minDistanceSquared = distanceSquared;
                        closest = link;
                    }
                }
            }
        }
        return closest;
    }
    
    /**
     * Returns a map from each {@link Link} to the {@link Shape}
     * representing the {@link Link}.
     * 
     * @return The map from {@link Link} objects to Shapes.
     */
    Map<Link, Shape> getLinkShapes()
    {
        Map<Link, Shape> map = new LinkedHashMap<Link, Shape>();
        for (Link link : flowEditor.getFlow().getLinks())
        {
            Shape shape = linkShapeProvider.shapeForLink(link);
            map.put(link, shape);
        }
        return map;
    }
    
    
    /**
     * Return an unmodifiable list containing all {@link ModuleComponent} 
     * objects that are currently shown in this panel
     * 
     * @return The list of {@link ModuleComponent} objects
     */
    List<ModuleComponent> getModuleComponents()
    {
        return Collections.unmodifiableList(
            new ArrayList<ModuleComponent>(
                moduleToModuleComponent.values()));
    }
    
    @Override
    public ModuleComponent getModuleComponent(Module module)
    {
        return moduleToModuleComponent.get(module);
    }
    
    /**
     * Compute the rectangle describing the bounds of all
     * {@link ModuleComponent} objects 
     * 
     * @return The bounds of all {@link ModuleComponent} objects
     */
    Rectangle computeModuleComponentBounds()
    {
        List<ModuleComponent> moduleComponents = getModuleComponents();
        Rectangle totalBounds = new Rectangle();
        if (!moduleComponents.isEmpty())
        {
            totalBounds = moduleComponents.get(0).getBounds();
            for (int i = 1; i < moduleComponents.size(); i++)
            {
                Rectangle b = moduleComponents.get(i).getBounds();
                SwingUtilities.computeUnion(
                    b.x, b.y, b.width, b.height, totalBounds);
            }
        }
        else
        {
            totalBounds = new Rectangle(0, 0, getWidth(), getHeight());
        }
        return totalBounds;
    }
    
    /**
     * Returns the {@link LinkShapeProvider} for this component
     * 
     * @return The {@link LinkShapeProvider}
     */
    LinkShapeProvider getLinkShapeProvider()
    {
        return linkShapeProvider;
    }
    
    
    /**
     * Highlight the given {@link InputSlot} objects. If the given slots is 
     * <code>null</code>, then the highlighting will be reset, 
     * and everything will be displayed normally. Otherwise, the
     * {@link ModuleComponent} objects that do NOT contain any of the given 
     * slots will be shaded. The ones that contain one of the given slots 
     * will be shown normally, with the respective slot being highlighted.
     *  
     * @param startSlot The start slot whose module which will not be shaded
     * @param slots The highlighted slots
     */
    void highlightInputSlots(Slot startSlot, 
        Iterable<? extends InputSlot> slots)
    {
        if (slots == null)
        {
            for (ModuleComponent moduleComponent : getModuleComponents())
            {
                moduleComponent.setShadeColor(null);
                Color labelBackgroundColor = 
                    UIManager.getColor("Label.background");
                moduleComponent.highlightInputSlot(-1, labelBackgroundColor);
            }
        }
        else
        {
            for (ModuleComponent moduleComponent : getModuleComponents())
            {
                moduleComponent.setShadeColor(SHADE_COLOR);
            }
            if (startSlot != null)
            {
                Module startModule = startSlot.getModule();
                ModuleComponent startModuleComponent = 
                    getModuleComponent(startModule);
                startModuleComponent.setShadeColor(null);
                startModuleComponent.highlightOutputSlot(
                    startSlot.getIndex(), ORIGIN_SLOT_COLOR);
            }
            for (Slot slot : slots)
            {
                Module module = slot.getModule();
                ModuleComponent moduleComponent = getModuleComponent(module);
                moduleComponent.setShadeColor(null);
                int index = slot.getIndex();
                moduleComponent.highlightInputSlot(index, HIGHLIGHT_COLOR);
            }
        }
    }
    
    /**
     * Highlight the given {@link OutputSlot} objects. If the given slots is 
     * <code>null</code>, then the highlighting will be reset, 
     * and everything will be displayed normally. Otherwise, the
     * {@link ModuleComponent} objects that do NOT contain any of the given 
     * slots will be shaded. The ones that contain one of the given slots 
     * will be shown normally, with the respective slot being highlighted.
     *  
     * @param startSlot The start slot whose module which will not be shaded
     * @param slots The highlighted slots
     */
    void highlightOutputSlots(Slot startSlot, 
        Iterable<? extends OutputSlot> slots)
    {
        if (slots == null)
        {
            for (ModuleComponent moduleComponent : getModuleComponents())
            {
                moduleComponent.setShadeColor(null);
                Color labelBackgroundColor = 
                    UIManager.getColor("Label.background");
                moduleComponent.highlightOutputSlot(-1, labelBackgroundColor);
            }
        }
        else
        {
            for (ModuleComponent moduleComponent : getModuleComponents())
            {
                moduleComponent.setShadeColor(SHADE_COLOR);
            }
            if (startSlot != null)
            {
                Module startModule = startSlot.getModule();
                ModuleComponent startModuleComponent = 
                    getModuleComponent(startModule);
                startModuleComponent.setShadeColor(null);
                startModuleComponent.highlightInputSlot(
                    startSlot.getIndex(), ORIGIN_SLOT_COLOR);
            }
            for (Slot slot : slots)
            {
                Module module = slot.getModule();
                ModuleComponent moduleComponent = getModuleComponent(module);
                moduleComponent.setShadeColor(null);
                int index = slot.getIndex();
                moduleComponent.highlightOutputSlot(index, HIGHLIGHT_COLOR);
            }
        }
    }

    /**
     * Returns the insets for the auto-scrolling
     * 
     * @return The auto-scrolling insets
     */
    private Insets getAutoscrollInsets()
    {
        return new Insets(
            AUTOSCROLL_BORDER_SIZE,
            AUTOSCROLL_BORDER_SIZE,
            AUTOSCROLL_BORDER_SIZE,
            AUTOSCROLL_BORDER_SIZE);
    }

    /**
     * Auto-scroll for the given cursor location.
     * 
     * @param cursorLocation The cursor location
     * @return The scrolling that was performed
     */
    Point autoscroll(Point cursorLocation)
    {
        Component parent = getParent();
        Rectangle parentBounds = 
            new Rectangle(0,0,parent.getWidth(),parent.getHeight());
        Rectangle visibleBounds = 
            SwingUtilities.convertRectangle(
                parent, parentBounds, this);
        
        Insets insets = getAutoscrollInsets();
        Point min = new Point(
            visibleBounds.x + insets.left,
            visibleBounds.y + insets.top);
        Point max = new Point(
            visibleBounds.x + visibleBounds.width - insets.right,
            visibleBounds.y + visibleBounds.height - insets.bottom);
        
        int dx = 0;
        int dy = 0;
        if(cursorLocation.x < min.x)
        {
            dx = min.x - cursorLocation.x;
        }
        else if(cursorLocation.x > max.x)
        {
            dx = max.x - cursorLocation.x;
        }
        if(cursorLocation.y < min.y)
        {
            dy = min.y - cursorLocation.y;
        }
        else if(cursorLocation.y > max.y)
        {
            dy = max.y - cursorLocation.y;
        }

        Rectangle newBounds = getBounds();
        newBounds.x += dx;
        newBounds.y += dy;
        setBounds(newBounds);
        validate();
        return new Point(dx, dy);
    }

}
