package de.javagl.flow.samples.gui01;

import java.util.List;

import javax.swing.SwingUtilities;

import de.javagl.category.Categories;
import de.javagl.category.CategoriesBuilder;
import de.javagl.category.Category;
import de.javagl.flow.Flow;
import de.javagl.flow.gui.FlowEditorApplication;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.creation.ModuleCreators;
import de.javagl.flow.plugin.ModuleCreatorServices;
import de.javagl.flow.samples.SampleFunctions;
import de.javagl.flow.samples.Utils;

/**
 * An example showing how to create an instance of the visual-interactive 
 * {@link Flow} editing application, with a certain set of 
 * {@link ModuleCreator} instances
 */
@SuppressWarnings("javadoc")
public class FlowGui_01_Basic
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
        
        categoriesBuilder.add(ModuleCreators.createForMethod("Create strings", 
            SampleFunctions.class, "createStrings"));
        categoriesBuilder.add(new SplitListModuleCreator());
        categoriesBuilder.add(ModuleCreators.createForMethod("Reverse strings", 
            SampleFunctions.class, "reverseStrings", List.class));
        categoriesBuilder.add(new MergeListsModuleCreator());
        categoriesBuilder.add(ModuleCreators.createForMethod("Print strings", 
            SampleFunctions.class, "printStrings", List.class));
        
        return categoriesBuilder.get();
    }
    
    public static void createAndShowGui()
    {
        new FlowEditorApplication(createModuleCreators());
    }

}
