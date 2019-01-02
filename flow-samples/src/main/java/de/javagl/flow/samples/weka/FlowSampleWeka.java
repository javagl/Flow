package de.javagl.flow.samples.weka;

import javax.swing.SwingUtilities;

import de.javagl.category.Categories;
import de.javagl.category.CategoriesBuilder;
import de.javagl.category.Category;
import de.javagl.flow.Flow;
import de.javagl.flow.gui.FlowEditorApplication;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.plugin.ModuleCreatorServices;
import de.javagl.flow.samples.Utils;

/**
 * An example showing how to create an instance of the visual-interactive 
 * {@link Flow} editing application with Weka modules
 */
@SuppressWarnings("javadoc")
public class FlowSampleWeka
{
    public static void main(String[] args)
    {
        Utils.defaultInitialization();
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }
    
    private static Category<ModuleCreator> createModuleCreators()
    {
        CategoriesBuilder<ModuleCreator> categoriesBuilder =
            Categories.createBuilder("All modules");
        ModuleCreatorServices.initialize(categoriesBuilder);
        return categoriesBuilder.get();
    }
    
    public static void createAndShowGui()
    {
        new FlowEditorApplication(createModuleCreators());
    }

}
