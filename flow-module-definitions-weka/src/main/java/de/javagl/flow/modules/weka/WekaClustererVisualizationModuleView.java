package de.javagl.flow.modules.weka;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.GridLayout;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;

import de.javagl.flow.module.creation.AbstractModuleView;
import de.javagl.flow.module.view.ModuleView;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

/**
 * A {@link ModuleView} for a {@link WekaClustererVisualizationModule}
 */
final class WekaClustererVisualizationModuleView 
    extends AbstractModuleView implements ModuleView
{
    // TODO The dialog is never disposed. This could probably be solved with 
    // a ComponentListener, that is informed when the component of this 
    // view is detached from its parent
    
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(WekaClustererVisualizationModuleView.class.getName());
    
    /**
     * The Weka VisualizePanel
     */
    private final VisualizePanel visualizePanel;
    
    /**
     * The dialog
     */
    private final JDialog dialog;
    
    /**
     * Default constructor
     */
    public WekaClustererVisualizationModuleView()
    {
        this.visualizePanel = new VisualizePanel();
        getComponent().setLayout(new GridLayout(1,1));
        
        dialog = new JDialog(null, "Visualization", ModalityType.MODELESS);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(visualizePanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        
        JButton showButton = new JButton("Show...");
        showButton.addActionListener(e -> 
        {
            dialog.setVisible(true);
        });
        getComponent().add(showButton);
        
    }
    
    @Override
    protected void updateModuleView()
    {
        visualizePanel.setName("None");
        visualizePanel.removeAllPlots();
        
        WekaClustererVisualizationModule module = 
            (WekaClustererVisualizationModule) getModule();
        if (module != null)
        {
            Instances clusteredInstances = module.getClusteredInstances();
            if (clusteredInstances != null)
            {
                PlotData2D plotData = new PlotData2D(clusteredInstances);
                
                String plotName = clusteredInstances.relationName();
                if (module.getClusterer() != null)
                {
                    plotName = module.getClusterer().getClass().getSimpleName();
                }
                plotData.setPlotName(plotName);
                dialog.setTitle(plotName);
        
                visualizePanel.setName(clusteredInstances.relationName());
                try
                {
                    
                    visualizePanel.addPlot(plotData);
                } 
                catch (Exception e)
                {
                    logger.severe(e.getMessage());
                }
            }
        }
    }

}
