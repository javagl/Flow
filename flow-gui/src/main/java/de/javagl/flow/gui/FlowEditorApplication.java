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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.w3c.dom.Node;

import de.javagl.category.Category;
import de.javagl.flow.Flow;
import de.javagl.flow.gui.editor.FlowEditor;
import de.javagl.flow.gui.editor.FlowEditorContext;
import de.javagl.flow.gui.editor.FlowEditorUndoManager;
import de.javagl.flow.io.xml.XmlException;
import de.javagl.flow.io.xml.XmlFlowWorkspace;
import de.javagl.flow.io.xml.XmlUtils;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.repository.Repository;
import de.javagl.flow.workspace.FlowWorkspace;
import de.javagl.flow.workspace.FlowWorkspaces;
import de.javagl.flow.workspace.MutableFlowWorkspace;

/**
 * The main class for the flow editor application. Hence the name...
 * It maintains the main frame of the application and the menu 
 * actions. The menu actions allow loading/saving a {@link FlowWorkspace}
 * and forwarding it to a {@link FlowEditor} which is shown in a
 * {@link FlowEditorApplicationPanel}. This class also establishes 
 * the connection between the {@link FlowEditor} and the 
 * {@link FlowEditorUndoManager} that manages the undo/redo for 
 * the {@link FlowEditor}.  
 */
public final class FlowEditorApplication
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(FlowEditorApplication.class.getName());
    
    
    /**
     * The Action for creating a new flow
     * 
     * @see #newFile()
     */
    private final Action newAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -5125243029591873126L;

        // Initialization
        {
            putValue(NAME, "New");
            putValue(SHORT_DESCRIPTION, "Create a new flow");
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            newFile();
        }
    };

    
    /**
     * The Action for opening a flow file
     */
    private final Action openAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -5125243029591873126L;

        // Initialization
        {
            putValue(NAME, "Open...");
            putValue(SHORT_DESCRIPTION, "Open a flow file");
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            openFile();
        }
    };

    /**
     * The Action for saving a flow file under a user-selected name
     */
    private final Action saveAsAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = 2304472601624430742L;

        // Initialization
        {
            putValue(NAME, "Save as...");
            putValue(SHORT_DESCRIPTION, "Save current flow to a file");
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            saveAsFile();
        }
    };
    
    /**
     * The Action to exit the application 
     */
    private final Action exitAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -8426096621732848545L;

        // Initialization
        {
            putValue(NAME, "Exit");
            putValue(SHORT_DESCRIPTION, "Exit the application");
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            exit();
        }
    };

    /**
     * The main frame of the application
     */
    private final JFrame frame;

    /**
     * The FileChooser for opening flow files
     */
    private final JFileChooser openFileChooser;
    
    /**
     * The FileChooser for saving flow files
     */
    private final JFileChooser saveAsFileChooser;

    /**
     * The {@link FlowEditor}
     */
    private FlowEditor flowEditor;

    /**
     * The main panel of the application, namely the GUI component for
     * controlling the {@link FlowEditor}
     */
    private final FlowEditorApplicationPanel flowEditorApplicationPanel;
    
    /**
     * An UndoableEditListener that is attached to the {@link FlowEditor}
     * and manages the undo/redo for this application
     */
    private final FlowEditorUndoManager flowEditorUndoManager; 

    /**
     * An UndoableEditListener that maintains the {@link #currentStateUnsaved} 
     * flag based on UndoableEdits that happened in the {@link FlowEditor}
     */
    private final UndoableEditListener savedStateUndoableEditListener = 
        new UndoableEditListener()
    {
        @Override
        public void undoableEditHappened(UndoableEditEvent e)
        {
            currentStateUnsaved = true;
        }
    };
    
    /**
     * Whether the current flow has been modified and not saved yet
     */
    private boolean currentStateUnsaved = false;
    
    /** 
     * The {@link FlowEditorContext} in which this application operates
     */
    private final FlowEditorContext flowEditorContext; 
    
    /**
     * Creates the application
     * 
     * @param mainModuleCreatorCategory The {@link Category} containing all 
     * available {@link ModuleCreator} instances.  
     */
    public FlowEditorApplication(
        Category<ModuleCreator> mainModuleCreatorCategory)
    {
        flowEditorContext = new FlowEditorContext(mainModuleCreatorCategory);
        flowEditorUndoManager = new FlowEditorUndoManager();
        
        // Create the file choosers
        openFileChooser = createOpenFileChooser();
        saveAsFileChooser = createSaveAsFileChooser();
        
        // Create the main frame
        frame = new JFrame("Flow");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                exit();
            }
        });
        frame.getContentPane().setLayout(new GridLayout(1,1));

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = createFileMenu();
        menuBar.add(fileMenu);
        JMenu editMenu = createEditMenu();
        menuBar.add(editMenu);
        frame.setJMenuBar(menuBar);        
        
        // Create the main application panel and initialize the FlowEditor        
        flowEditorApplicationPanel = 
            new FlowEditorApplicationPanel(flowEditorContext);
        initEditor(FlowWorkspaces.create());
        frame.getContentPane().add(flowEditorApplicationPanel);
        
        // Set up the hotkeys for undo/redo
        ActionMap actionMap = flowEditorApplicationPanel.getActionMap();
        InputMap inputMap = flowEditorApplicationPanel.getInputMap(
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(flowEditorUndoManager.getUndoKeyStroke(), "undo");
        actionMap.put("undo", flowEditorUndoManager.getUndoAction());
        inputMap.put(flowEditorUndoManager.getRedoKeyStroke(), "redo");
        actionMap.put("redo", flowEditorUndoManager.getRedoAction());
        
        // Set the initial size of the frame
        GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        Rectangle bounds = gc.getBounds();
        
        bounds.height -= 300; // XXX Arbitrarily decreasing window height
        
        frame.setBounds(bounds);
        
        frame.setVisible(true);
    }
    
    /**
     * Create and return a file chooser for opening an XML file
     *  
     * @return The file chooser
     */
    private JFileChooser createOpenFileChooser()
    {
        JFileChooser openFileChooser = new JFileChooser(".");
        openFileChooser.setFileFilter(
            new FileNameExtensionFilter("XML Files", "xml"));
        return openFileChooser;
    }
    
    /**
     * Create and return a file chooser for saving an XML file
     *  
     * @return The file chooser
     */
    private JFileChooser createSaveAsFileChooser()
    {
        JFileChooser saveAsFileChooser = new JFileChooser(".");
        saveAsFileChooser.setFileFilter(
            new FileNameExtensionFilter("XML Files", "xml"));
        return saveAsFileChooser;
    }
    
    /**
     * Create the 'File' menu
     * 
     * @return The menu
     */
    private JMenu createFileMenu()
    {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(new JMenuItem(newAction));
        fileMenu.add(new JMenuItem(openAction));
        fileMenu.add(new JMenuItem(saveAsAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(exitAction));
        
        return fileMenu;
    }
    
    /**
     * Create the 'Edit' menu
     * 
     * @return The menu
     */
    private JMenu createEditMenu()
    {
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        editMenu.add(new JMenuItem(flowEditorUndoManager.getUndoAction()));
        editMenu.add(new JMenuItem(flowEditorUndoManager.getRedoAction()));
        
        return editMenu;
    }
    
    
    
    /**
     * Starts the creation of a new {@link Flow} by calling 
     * {@link #initEditor(MutableFlowWorkspace)} with a new
     * {@link FlowWorkspace}.
     * If there are unsaved changes, the user is asked for confirmation. 
     */
    private void newFile()
    {
        if (currentStateUnsaved)
        {
            int confirmState = 
                JOptionPane.showConfirmDialog(frame, 
                    "Current flow was modified, do you want " +
                    "to save before creating a new one?", 
                    "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirmState == JOptionPane.YES_OPTION)
            {
                boolean saved = saveAsFile();
                if (!saved)
                {
                    return;
                }
            }
            else if (confirmState == JOptionPane.CANCEL_OPTION)
            {
                return;
            }
        }
        initEditor(FlowWorkspaces.create());
    }
    
    
    /**
     * Shows a file chooser for selecting a file that will be passed
     * to {@link #openXmlFile(File)}.
     * If there are unsaved changes, the user is asked for confirmation. 
     */
    private void openFile()
    {
        if (currentStateUnsaved)
        {
            int confirmState = 
                JOptionPane.showConfirmDialog(frame, 
                    "The current flow was modified, do you want " +
                    "to save it before opening a new file?", 
                    "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirmState == JOptionPane.YES_OPTION)
            {
                boolean saved = saveAsFile();
                if (!saved)
                {
                    return;
                }
            }
            else if (confirmState == JOptionPane.CANCEL_OPTION)
            {
                return;
            }
        }
        
        int returnState = openFileChooser.showOpenDialog(frame);
        if (returnState == JFileChooser.APPROVE_OPTION) 
        {
            File file = openFileChooser.getSelectedFile();
            saveAsFileChooser.setSelectedFile(file);
            openXmlFile(file);
        }        
    }
    
    /**
     * Read a {@link FlowWorkspace} from the given file, and 
     * pass it to {@link #initEditor(MutableFlowWorkspace)}.
     * 
     * @param file The file to open
     */
    private void openXmlFile(File file)
    {
        MutableFlowWorkspace flowWorkspace = null;

        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
            Node node = XmlUtils.read(inputStream);
            
            Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository = 
                flowEditorContext.getModuleCreatorRepository();
            
            flowWorkspace = XmlFlowWorkspace.parse(
                node, moduleCreatorRepository);
        }
        catch (FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(frame,
                "File not found: " + file, 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } 
        catch (XmlException e)
        {
            JOptionPane.showMessageDialog(frame,
                "Could not read file " + file + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            
            // TODO Show error message in dialog
            e.printStackTrace();
            
            return;
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    logger.warning(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        initEditor(flowWorkspace);
    }

    
    /**
     * Initialize a {@link FlowEditor} for the given workspace, and
     * show it in the 
     * {@link FlowEditorApplicationPanel#setFlowEditor(FlowEditor)
     * FlowEditorApplicationPanel}
     * 
     * @param flowWorkspace The {@link FlowWorkspace} that should be
     * edited with the {@link FlowEditor}
     */
    private void initEditor(MutableFlowWorkspace flowWorkspace)
    {
        if (flowEditor != null)
        {
            flowEditor.removeUndoableEditListener(flowEditorUndoManager);
            flowEditor.removeUndoableEditListener(
                savedStateUndoableEditListener);
        }
        flowEditor = new FlowEditor(flowWorkspace);
        flowEditor.addUndoableEditListener(flowEditorUndoManager);
        flowEditor.addUndoableEditListener(savedStateUndoableEditListener);
        flowEditorApplicationPanel.setFlowEditor(flowEditor);

        currentStateUnsaved = false;
        flowEditorUndoManager.reset();
    }
    
    
    /**
     * Shows a file chooser for selecting a file that will be passed
     * to {@link #saveAsXmlFile(File)}
     * If the file already exists, the user is asked for confirmation. 
     * 
     * @return Whether the save operation completed without errors
     */
    private boolean saveAsFile()
    {
        int returnState = saveAsFileChooser.showSaveDialog(frame);
        if (returnState == JFileChooser.APPROVE_OPTION) 
        {
            File file = saveAsFileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xml"))
            {
                file = new File(file.getName() + ".xml");
            }
            if (file.exists())
            {
                int confirmState = 
                    JOptionPane.showConfirmDialog(frame, 
                        "The file already exists, do you want to overwrite it?", 
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirmState != JOptionPane.YES_OPTION)
                {
                    return saveAsFile();
                }
            }
            boolean saved = saveAsXmlFile(file);
            currentStateUnsaved = !saved;
            return saved;
        }       
        return false;
    }
    
    /**
     * Save the current {@link FlowWorkspace} in the given file
     * 
     * @param file The file
     * @return Whether the save operation completed without errors
     */
    private boolean saveAsXmlFile(File file)
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(file);

            // TODO Should the selection state also be written to XML?
            FlowWorkspace flowWorkspace = flowEditor.getFlowWorkspace();
            
            Repository<ModuleInfo, ModuleCreator> moduleCreatorRepository = 
                flowEditorContext.getModuleCreatorRepository();
            
            Node node = XmlFlowWorkspace.createNode(
                flowWorkspace, moduleCreatorRepository);
            XmlUtils.write(node, outputStream);
            return true;
        }
        catch (FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(frame,
                "File not found: " + file,
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        catch (XmlException e)
        {
            JOptionPane.showMessageDialog(frame,
                "Could not write XML: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        finally
        {
            if (outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (IOException e)
                {
                    logger.warning(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    /**
     * Exits the application by disposing the main frame. 
     * If there are unsaved changes, the user is asked for confirmation. 
     */
    private void exit()
    {
        if (currentStateUnsaved)
        {
            int confirmState = 
                JOptionPane.showConfirmDialog(frame, 
                    "The current flow was modified, do you want " +
                    "to save it before exiting?", 
                    "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirmState == JOptionPane.YES_OPTION)
            {
                boolean saved = saveAsFile();
                if (!saved)
                {
                    return;
                }
            }
            else if (confirmState == JOptionPane.CANCEL_OPTION)
            {
                return;
            }
        }
        frame.setVisible(false);
        frame.dispose();
    }
    
}