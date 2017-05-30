package de.javagl.flow.modules.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.javagl.category.CategoriesBuilder;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.reflection.Classes;
import weka.core.Utils;

/**
 * Utility to create the {@link ModuleCreator} instances for Weka
 */
class WekaCategoriesGenerator
{
    /**
     * Generate the categories for the given Weka class (interface) in the 
     * given {@link CategoriesBuilder}. This will use some internal Weka magic
     * to obtain the classes that implement the interface, and create the
     * {@link ModuleCreator} instances (in a subcategory that depends on
     * the package name), by passing their class names to the given 
     * constructor function.
     * 
     * @param classType The Weka class type
     * @param categoriesBuilder The {@link CategoriesBuilder}
     * @param constructor The constructor to call for each class name
     */
    static void generateCategories(
        Class<?> classType, 
        CategoriesBuilder<ModuleCreator> categoriesBuilder,
        Function<String, ? extends ModuleCreator> constructor)
    {
        List<String> classNames = collectClassNames(classType);
        for (String className : classNames)
        {
            CategoriesBuilder<ModuleCreator> subCategoriesBuilder = 
                getSubCategoriesBuilderForClassName(
                    categoriesBuilder, className);
            subCategoriesBuilder.add(constructor.apply(className));
        }
    }
    
    /**
     * Collect all Weka class names for classes that implement the given
     * interface
     * 
     * @param classType The interface
     * @return The class names
     */
    private static List<String> collectClassNames(Class<?> classType)
    {
        CustomGenericObjectEditor customGenericObjectEditor =
            new CustomGenericObjectEditor(null, false);
        customGenericObjectEditor.setClassType(classType);
        List<String> classNames = new ArrayList<String>();
        JTree tree = customGenericObjectEditor.createTree();
        TreeModel treeModel = tree.getModel();
        Object root = treeModel.getRoot();
        traverse(treeModel, new TreePath(root), treePath -> 
        {
            Object node = treePath.getLastPathComponent();
            if (treeModel.isLeaf(node))
            {
                String className = 
                    customGenericObjectEditor.getClassnameFromPath(treePath);
                classNames.add(className);
            }
        });
        return classNames;
    }
    
    /**
     * Creates a {@link CategoriesBuilder} from the given one, that describes
     * a sub-category based on the given fully-qualified class name. 
     * (If the class name starts with <code>weka.</code>, this part will
     * be omitted).
     *  
     * @param <T> The element type
     * @param categoriesBuilder The {@link CategoriesBuilder}
     * @param className The class name
     * @return The sub-{@link CategoriesBuilder}
     */
    private static <T> CategoriesBuilder<T> getSubCategoriesBuilderForClassName(
        CategoriesBuilder<T> categoriesBuilder, String className)
    {
        String[] tokens = className.split("\\.");
        int startIndex = 0;
        if (tokens.length > 0 && tokens[0].equals("weka"))
        {
            startIndex = 1;
        }
        CategoriesBuilder<T> current = categoriesBuilder;
        for (int i = startIndex; i < tokens.length - 1; i++)
        {
            String name = capitalizeFirstLetter(tokens[i]);
            current = current.get(name);
        }
        return current;
    }
    
    /**
     * Returns the description for the specified Weka class
     * 
     * @param className The fully qualified Weka class name
     * @return The description
     */
    static String getDescriptionHtml(String className)
    {
        Object instance = Classes.newInstanceOptional(className);
        if (instance == null)
        {
            return "(Could not instantiate " + className + ")";
        }
        String globalInfo = Utils.getGlobalInfo(instance, true);
        if (globalInfo == null)
        {
            return "(Could not obtain info for " + className + ")";
        }
        return stripEnclosingHtmlTags(globalInfo.trim());
    }
    
    /**
     * If the given string starts with <code>&lt;html&gt;</code> and ends 
     * with <code>&lt;/html&gt;</code>, then these enclosing tags are
     * removed. Otherwise, the string itself is returned.
     * 
     * @param s The string
     * @return The string without enclosing HTML tags
     */
    private static String stripEnclosingHtmlTags(String s)
    {
        if (s == null)
        {
            return null;
        }
        if (s.startsWith("<html>") && s.endsWith("</html>"))
        {
            return s.substring(6, s.length() - 7);
        }
        return s;
    }
    
    /**
     * Returns the unqualified name of the class with the given 
     * (fully-qualified) name. That is, this method returns the 
     * part of the given string behind the last <code>.</code> dot.
     * 
     * @param className The class name
     * @return The unqualified class name
     */
    static String getUnqualifiedClassName(String className)
    {
        if (className == null)
        {
            return className;
        }
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex == -1)
        {
            return className;
        }
        return className.substring(lastDotIndex + 1);
    }
    
    /**
     * Capitalize the first letter of the given string. If the string
     * is <code>null</code> or empty, it will be returned.
     * 
     * @param s The string
     * @return The string with a capital first letter
     */
    private static String capitalizeFirstLetter(String s)
    {
        if (s == null || s.isEmpty())
        {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    /**
     * Traverse the given tree model, based on the given path, calling
     * the given callback with each resulting path
     * 
     * @param treeModel The tree model
     * @param currentPath The current path
     * @param treePathCallback The callback
     */
    private static void traverse(TreeModel treeModel, TreePath currentPath, 
        Consumer<? super TreePath> treePathCallback)
    {
        treePathCallback.accept(currentPath);
        Object node = currentPath.getLastPathComponent();
        int childCount = treeModel.getChildCount(node);
        for (int i=0; i<childCount; i++)
        {
            Object child = treeModel.getChild(node, i);
            TreePath nextPath = currentPath.pathByAddingChild(child);
            traverse(treeModel, nextPath, treePathCallback);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private WekaCategoriesGenerator()
    {
        // Private constructor to prevent instantiation
    }
}
