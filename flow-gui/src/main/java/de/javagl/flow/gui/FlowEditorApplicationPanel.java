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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import de.javagl.category.Category;
import de.javagl.flow.Flow;
import de.javagl.flow.TypeContext;
import de.javagl.flow.gui.compatibility.ModuleCompatibilityModel;
import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.gui.editor.FlowEditorContext;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.repository.Repository;
import de.javagl.selection.SelectionEvent;
import de.javagl.selection.SelectionListener;

/**
 * The main component that is shown in a {@link FlowEditorApplication}.
 * This panel contains all GUI components for controlling a {@link FlowEditor}.
 * The main component is a {@link FlowEditorComponent} which shows the 
 * flow graph. The other components are responsible for showing the 
 * trees that allow drag-and-drop of {@link Module} objects into the main 
 * editor component, as well as miscellaneous information- and control
 * components. 
 */
final class FlowEditorApplicationPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -7720238732271054479L;

    /**
     * The {@link FlowEditor} that is controlled with this panel
     */
    private FlowEditor flowEditor;
    
    /**
     * The {@link FlowEditorComponent} that shows the {@link FlowEditor}.
     * This component only shows the "flow graph"
     */
    private FlowEditorComponent flowEditorComponent;
    
    /**
     * The {@link FlowEditorViewport} that serves as a viewport for the
     * the (virtually infinitely large) {@link FlowEditorComponent}
     */
    private FlowEditorViewport flowEditorViewport;

    /**
     * The {@link FlowOverview} that shows an overview of the whole
     * contents of the {@link FlowEditorComponent} and allows 
     * panning and scrolling the {@link FlowEditorComponent} inside
     * the {@link FlowEditorViewport}
     */
    private FlowOverview flowOverview;
    
    /**
     * The {@link FlowExecutorControl} (preliminary) 
     */
    private FlowExecutorControl flowExecutorControl;
    
    /**
     * The {@link FlowEditorContext} containing the {@link Repository}
     * of available {@link ModuleCreator} objects
     */
    private final FlowEditorContext flowEditorContext;

    /**
     * The {@link CategoryTree} containing the whole {@link ModuleCreator} 
     * {@link Category} hierarchy
     */
    private CategoryTree<ModuleCreator> mainModuleCreatorTree;
    
    /**
     * The component containing the {@link #mainModuleCreatorTree}
     */
    private JComponent mainModuleCreatorTreePanel;

    /**
     * The {@link CategoryTree} containing the {@link ModuleCreator} 
     * {@link Category} hierarchy for the built-in {@link ModuleCreator}
     * types
     */
    private CategoryTree<ModuleCreator> builtInModuleCreatorTree;
    
    /**
     * The component containing the {@link #builtInModuleCreatorTree}
     */
    private JComponent builtInModuleCreatorTreePanel;
    
    /**
     * The {@link CategoryTree} containing the {@link ModuleCreator} objects 
     * that can create {@link Module} objects that can provide inputs for the 
     * currently selected {@link Module} objects. 
     * 
     * This is the view for the 
     * {@link ModuleCompatibilityModel#getSourcesCategory()} of the
     * {@link FlowEditorContext#getMainModuleCompatibilityModel()}
     */
    private CategoryTree<ModuleCreator> sourcesTree;
    
    /**
     * The component containing the {@link #sourcesTree}
     */
    private JComponent sourcesTreePanel;
    
    /**
     * The {@link CategoryTree} containing the {@link ModuleCreator} objects 
     * that can create {@link Module} objects that can provide inputs for the 
     * currently selected {@link Module} objects.
     * 
     * This is the view for the 
     * {@link ModuleCompatibilityModel#getSourcesCategory()} of the
     * {@link FlowEditorContext#getBuiltInModuleCompatibilityModel()}
     */
    private CategoryTree<ModuleCreator> builtInSourcesTree;
    
    /**
     * The component containing the {@link #builtInSourcesTree}
     */
    private JComponent builtInSourcesTreePanel;

    /**
     * The {@link CategoryTree} containing the {@link ModuleCreator} objects 
     * that can create {@link Module} objects that can consume outputs of the 
     * currently selected {@link Module} objects. 
     * 
     * This is the view for the 
     * {@link ModuleCompatibilityModel#getTargetsCategory()} of the
     * {@link FlowEditorContext#getMainModuleCompatibilityModel()}
     */
    private CategoryTree<ModuleCreator> targetsTree;
    
    /**
     * The component containing the {@link #targetsTree}
     */
    private JComponent targetsTreePanel;
    
    /**
     * The {@link CategoryTree} containing the {@link ModuleCreator} objects 
     * that can create {@link Module} objects that can consume outputs of the 
     * currently selected {@link Module} objects.
     * 
     * This is the view for the 
     * {@link ModuleCompatibilityModel#getTargetsCategory()} of the
     * {@link FlowEditorContext#getBuiltInModuleCompatibilityModel()}
     */
    private CategoryTree<ModuleCreator> builtInTargetsTree;

    /**
     * The component containing the {@link #builtInTargetsTree}
     */
    private JComponent builtInTargetsTreePanel;
    
    /**
     * The {@link CategoryTree} containing the {@link ModuleCreator} objects 
     * that can create {@link Module} objects that can consume outputs of the 
     * currently selected {@link Module} objects by invoking a method on them. 
     * This is the view for the 
     * {@link ModuleCompatibilityModel#getTargetMethodsCategory()} of the
     * {@link FlowEditorContext#getMainModuleCompatibilityModel()}
     */
    private CategoryTree<ModuleCreator> targetMethodsTree;

    /**
     * The component containing the {@link #targetMethodsTree}
     */
    private JComponent targetMethodsTreePanel;
    
    /**
     * The {@link ModuleDescriptionPanel} that shows some information
     * about the currently selected {@link Module} or {@link ModuleInfo}
     */
    private ModuleDescriptionPanel selectedModuleDescriptionPanel;

    /**
     * A {@link RepaintingListener} that will be attached to 
     * several components and trigger a repaint of the 
     * {@link FlowOverview}
     */
    private RepaintingListener overviewRepaintingListener;

    /**
     * A {@link SelectionListener} that will be notified when the
     * set of selected {@link Module} objects inside the {@link FlowEditor}
     * changes, and call {@link #moduleSelectionChanged()} 
     */
    private final SelectionListener<Module> moduleSelectionListener = 
        new SelectionListener<Module>()
    {
        @Override
        public void selectionChanged(SelectionEvent<Module> selectionEvent)
        {
            FlowEditorApplicationPanel.this.moduleSelectionChanged();
        }
    };

    
    /**
     * A TreeSelectionListener that will be attached to all trees, and
     * update the {@link #selectedModuleDescriptionPanel} with information
     * about the {@link ModuleInfo} of the {@link ModuleCreator} that 
     * was selected in the tree 
     */
    private final TreeSelectionListener 
        commonModuleCreatorTreeSelectionListener = new TreeSelectionListener()
    {
        @Override
        public void valueChanged(TreeSelectionEvent e)
        {
            TreePath path = e.getNewLeadSelectionPath();
            if (path == null)
            {
                selectedModuleDescriptionPanel.setModuleInfo(null);
                return;
            }
            Object leaf = path.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)leaf;
            Object userObject = node.getUserObject();
            if (userObject != null && userObject instanceof ModuleCreator)
            {
                ModuleCreator moduleCreator = (ModuleCreator)userObject;
                ModuleInfo moduleInfo = moduleCreator.getModuleInfo();
                //System.out.println("Selected module: "+moduleInfo);
                selectedModuleDescriptionPanel.setModuleInfo(moduleInfo);
            }
            else
            {
                selectedModuleDescriptionPanel.setModuleInfo(null);
            }
        }
    };
    
    /**
     * Creates a new FlowEditorApplicationPanel. It will contain the 
     * {@link #createMainPanel() main panel} and
     * {@link #createInfoPanel() info panel}<br>
     * 
     * @param flowEditorContext The {@link FlowEditorContext} in which 
     * this application operates 
     */
    FlowEditorApplicationPanel(FlowEditorContext flowEditorContext)
    {
        this.flowEditorContext = flowEditorContext;
        this.flowExecutorControl = new FlowExecutorControl();
        
        createTrees();
        
        setLayout(new GridLayout(1,1));
        JSplitPane splitPaneB = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        JPanel mainPanel = createMainPanel();
        splitPaneB.setTopComponent(mainPanel);

        JPanel infoPanel = createInfoPanel();
        splitPaneB.setBottomComponent(infoPanel);
        splitPaneB.setResizeWeight(1.0);

        GuiUtils.setDividerLocation(splitPaneB, 0.7);
        add(splitPaneB);
        
        Category<ModuleCreator> rootModuleCreatorCategory =
            flowEditorContext.getRootModuleCreatorCategory();
        mainModuleCreatorTree.setCategory(rootModuleCreatorCategory);
        mainModuleCreatorTree.expandAll(1);

        Category<ModuleCreator> builtInModuleCreatorCategory =
            flowEditorContext.getBuiltInModuleCreatorCategory();
        builtInModuleCreatorTree.setCategory(builtInModuleCreatorCategory);
        mainModuleCreatorTree.expandAll(1);
        
        
    }

    /**
     * Create the main panel. It will contain the 
     * {@link #createMainTreesPanel()} and the   
     * {@link #createMainEditingAreaPanel()}
     * 
     * @return The main panel
     */
    private JPanel createMainPanel()
    {
        JPanel mainPanel = new JPanel(new GridLayout(1,1));
        
        JSplitPane treeAndEditingAreaSplitPane = 
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JComponent mainTreesPanel = createMainTreesPanel();
        treeAndEditingAreaSplitPane.setLeftComponent(mainTreesPanel);
        
        JPanel mainEditingAreaPanel = createMainEditingAreaPanel();
        treeAndEditingAreaSplitPane.setRightComponent(mainEditingAreaPanel);
        
        GuiUtils.setDividerLocation(treeAndEditingAreaSplitPane, 0.15);
        mainPanel.add(treeAndEditingAreaSplitPane);
        
        return mainPanel;
    }
    
    /**
     * Create the tabbed pane that shows the {@link #mainModuleCreatorTree}
     * and the {@link #builtInModuleCreatorTree}
     * 
     * @return The component
     */
    private JComponent createMainTreesPanel()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Modules", mainModuleCreatorTreePanel);
        tabbedPane.addTab("Built-in", builtInModuleCreatorTreePanel);
        return tabbedPane;
    }
    
    /**
     * Create the tabbed pane that shows the {@link #sourcesTreePanel}
     * and the {@link #builtInSourcesTreePanel}
     * 
     * @return The component
     */
    private JComponent createSourcesTreesPanel()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Modules", sourcesTreePanel);
        tabbedPane.addTab("Built-in", builtInSourcesTreePanel);
        return tabbedPane;
    }

    /**
     * Create the tabbed pane that shows the {@link #sourcesTreePanel}
     * and the {@link #builtInSourcesTreePanel}
     * 
     * @return The component
     */
    private JComponent createTargetsTreesPanel()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Modules", targetsTreePanel);
        tabbedPane.addTab("Built-in", builtInTargetsTreePanel);
        tabbedPane.addTab("Methods", targetMethodsTreePanel);
        return tabbedPane;
    }
    
    

    /**
     * Create the main editing area panel. It will contain the 
     * {@link #flowEditorComponent} inside the {@link #flowEditorViewport},
     * and the trees for the compatible modules at the left and right side
     * 
     * @return The main editing area panel
     */
    private JPanel createMainEditingAreaPanel()
    {
        JPanel mainEditingAreaPanel = new JPanel(new GridLayout(1,1));

        JSplitPane splitPaneL = 
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane splitPaneR = 
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneL.setResizeWeight(0.2);
        splitPaneR.setResizeWeight(0.8);
        splitPaneL.setLeftComponent(createSourcesTreesPanel());
        splitPaneL.setRightComponent(splitPaneR);
        mainEditingAreaPanel.add(splitPaneL);

        createFlowEditorViewport();
        splitPaneR.setLeftComponent(flowEditorViewport);

        splitPaneR.setRightComponent(createTargetsTreesPanel());
        
        GuiUtils.setDividerLocation(splitPaneL, 0.12);
        GuiUtils.setDividerLocation(splitPaneR, 0.7);
        
        return mainEditingAreaPanel;
    }

    /**
     * Create the {@link #flowEditorViewport} and the contained
     * {@link #flowEditorComponent}
     */
    private void createFlowEditorViewport()
    {
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository =
            flowEditorContext.getModuleCreatorRepository();
        flowEditorComponent = new FlowEditorComponent(moduleCreatorRepository);
        flowEditorViewport = new FlowEditorViewport(flowEditorComponent);
        
        @SuppressWarnings("unused")
        ModuleDropTargetListener moduleDropTargetListener =
            new ModuleDropTargetListener(flowEditorComponent);
    }

    /**
     * Create all trees that are shown in this panel
     */
    private void createTrees()
    {
        mainModuleCreatorTree = new CategoryTree<ModuleCreator>(null);
        initTree(mainModuleCreatorTree);
        mainModuleCreatorTreePanel =  
            new FilterableCategoryTreePanel(mainModuleCreatorTree); 

        builtInModuleCreatorTree = new CategoryTree<ModuleCreator>(null);
        initTree(builtInModuleCreatorTree);
        builtInModuleCreatorTreePanel =  
            new FilterableCategoryTreePanel(builtInModuleCreatorTree); 
        
        
        // Main
        ModuleCompatibilityModel mainModuleCompatibilityModel = 
            flowEditorContext.getMainModuleCompatibilityModel();
        
        // Main sources
        Category<ModuleCreator> sourcesCategory =
            mainModuleCompatibilityModel.getSourcesCategory();

        sourcesTree = createCategoryTree(sourcesCategory);
        sourcesTreePanel = 
            new FilterableCategoryTreePanel(sourcesTree);
        
        // Main targets
        Category<ModuleCreator> targetsCategory =
            mainModuleCompatibilityModel.getTargetsCategory();
        
        targetsTree = createCategoryTree(targetsCategory);
        targetsTreePanel = 
            new FilterableCategoryTreePanel(targetsTree);

        // Main target methods
        Category<ModuleCreator> targetMethodsCategory =
            mainModuleCompatibilityModel.getTargetMethodsCategory();
        
        targetMethodsTree = createCategoryTree(targetMethodsCategory);
        targetMethodsTreePanel = 
            new FilterableCategoryTreePanel(targetMethodsTree);


        
        // Built-in
        ModuleCompatibilityModel builtInModuleCompatibilityModel = 
            flowEditorContext.getBuiltInModuleCompatibilityModel();
        
        // Built-in sources
        Category<ModuleCreator> builtInSourcesCategory =
            builtInModuleCompatibilityModel.getSourcesCategory();

        builtInSourcesTree = createCategoryTree(builtInSourcesCategory);
        builtInSourcesTreePanel = 
            new FilterableCategoryTreePanel(builtInSourcesTree);
        
        // Built-in targets
        Category<ModuleCreator> builtInTargetsCategory =
            builtInModuleCompatibilityModel.getTargetsCategory();
        
        builtInTargetsTree = createCategoryTree(builtInTargetsCategory);
        builtInTargetsTreePanel = 
            new FilterableCategoryTreePanel(builtInTargetsTree);
        
        
        
    }
    
    /**
     * Create a new {@link CategoryTree} for the given {@link Category},
     * and initialize it by passing it to {@link #initTree(CategoryTree)}
     * 
     * @param category The {@link Category}
     * @return The {@link CategoryTree}
     */
    private CategoryTree<ModuleCreator> createCategoryTree(
        Category<ModuleCreator> category)
    {
        CategoryTree<ModuleCreator> categoryTree = 
            new CategoryTree<ModuleCreator>(category);
        initTree(categoryTree);
        return categoryTree;
    }
    
    /**
     * Initialize the given {@link CategoryTree}, by enabling drag-and-drop
     * and attaching the {@link #commonModuleCreatorTreeSelectionListener}
     * 
     * @param categoryTree The {@link CategoryTree}
     */
    private void initTree(CategoryTree<ModuleCreator> categoryTree)
    {
        JTree tree = categoryTree.getTree();
        Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository =
            flowEditorContext.getModuleCreatorRepository();
        tree.setTransferHandler(
            new ModuleTransferHandler(moduleCreatorRepository));
        tree.setDragEnabled(true);
        tree.addTreeSelectionListener(
            commonModuleCreatorTreeSelectionListener);
    }



    /**
     * Create the info panel. It will contain the 
     * {@link #selectedModuleDescriptionPanel} and the {@link #flowOverview}
     * 
     * @return The info panel
     */
    private JPanel createInfoPanel()
    {
        JPanel infoPanel = new JPanel(new BorderLayout());

        // Create the info panel for the selected module
        selectedModuleDescriptionPanel = new ModuleDescriptionPanel();
        JScrollPane scrollPane =
            new JScrollPane(selectedModuleDescriptionPanel);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create the FlowOverview
        flowOverview = new FlowOverview(flowEditorViewport);
        flowOverview.setPreferredSize(new Dimension(300,300));
        FlowOverviewControl flowOverviewControl = 
            new FlowOverviewControl(flowOverview, flowEditorViewport);
        flowOverview.addMouseListener(flowOverviewControl);
        flowOverview.addMouseMotionListener(flowOverviewControl);
        
        // Create a mouse input listener that will repaint the overview 
        // due to mouse events, and assign it to the flowEditorComponent
        overviewRepaintingListener = new RepaintingListener(flowOverview);
        flowEditorComponent.setRepaintingListener(overviewRepaintingListener); 
        
        // Add the overview to the infoPanel
        JPanel panel = new JPanel(new GridLayout(1,1));
        panel.setBorder(BorderFactory.createTitledBorder("Overview"));
        panel.add(flowOverview);
        infoPanel.add(panel, BorderLayout.EAST);

        return infoPanel;
    }
    
    

    /**
     * Set the {@link FlowEditor} that is controlled with this panel
     * 
     * @param newFlowEditor The new {@link FlowEditor}
     */
    void setFlowEditor(FlowEditor newFlowEditor)
    {
        if (flowEditor != null)
        {
            flowEditor.getModuleSelectionModel().removeSelectionListener(
                moduleSelectionListener);
        }
        
        this.flowEditor = newFlowEditor;
        this.flowEditorComponent.setFlowEditor(flowEditor);
        this.flowExecutorControl.setFlowWorkspace(flowEditor.getFlowWorkspace());
        
        if (flowEditor != null)
        {
            flowEditor.getModuleSelectionModel().addSelectionListener(
                moduleSelectionListener);
        }
    }
    
    /**
     * This method will be called by the {@link #moduleSelectionListener}
     * when the selection of {@link Module} objects in the current 
     * {@link FlowEditor} changes. It will update the 
     * {@link #selectedModuleDescriptionPanel} and the 
     * {@link ModuleCompatibilityModel} in the {@link #flowEditorContext} 
     */
    private void moduleSelectionChanged()
    {
        Collection<Module> selectedModules = flowEditor.getSelectedModules();
        //System.out.println("Selected now "+selectedModules);
        
        if (selectedModules.size() == 1)
        {
            Module selectedModule = selectedModules.iterator().next();
            selectedModuleDescriptionPanel.setModule(selectedModule);
        }
        else
        {
            selectedModuleDescriptionPanel.setModule(null);
        }
      

        Flow flow = flowEditor.getFlow();
        TypeContext typeContext = flow.getTypeContext();
        ModuleCompatibilityModel mainModuleCompatibilityModel = 
            flowEditorContext.getMainModuleCompatibilityModel();
        mainModuleCompatibilityModel.setModules(
            typeContext, selectedModules);
        ModuleCompatibilityModel builtInModuleCompatibilityModel = 
            flowEditorContext.getBuiltInModuleCompatibilityModel();
        builtInModuleCompatibilityModel.setModules(
            typeContext, selectedModules);
        
        sourcesTree.expandAll(1);
        targetsTree.expandAll(1);
        targetMethodsTree.expandAll(1);
        builtInSourcesTree.expandAll(1);
        builtInTargetsTree.expandAll(1);
    }
    
}

