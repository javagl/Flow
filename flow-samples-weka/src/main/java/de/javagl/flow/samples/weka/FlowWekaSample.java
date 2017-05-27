package de.javagl.flow.samples.weka;

import javax.swing.SwingUtilities;

import de.javagl.category.Categories;
import de.javagl.category.CategoriesBuilder;
import de.javagl.category.Category;
import de.javagl.flow.Flow;
import de.javagl.flow.gui.FlowEditorApplication;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.plugin.ModuleCreatorServices;

/**
 * An example showing how to create an instance of the visual-interactive 
 * {@link Flow} editing application, with a certain set of 
 * {@link ModuleCreator} instances that perform Weka operations.
 */
@SuppressWarnings("javadoc")
public class FlowWekaSample
{
    public static void main(String[] args)
    {
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
