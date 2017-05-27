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
package de.javagl.flow.gui.compatibility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.javagl.category.Categories;
import de.javagl.category.Category;
import de.javagl.category.MutableCategory;
import de.javagl.flow.TypeContext;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfoUtils;
import de.javagl.flow.module.creation.MethodModuleCreators;
import de.javagl.flow.module.creation.ModuleCreator;
import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.Slot;
import de.javagl.flow.repository.Repository;

/**
 * A model providing information about {@link ModuleCreator} objects that
 * can create compatible source- and target {@link Module} objects for a 
 * given set of {@link Module} objects. <br>
 * <br>
 */
public final class ModuleCompatibilityModel
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(ModuleCompatibilityModel.class.getName());
    
    /**
     * The log level for some debug output
     */
    private static final Level COMPATIBLE_MODULES_LOG_LEVEL = Level.INFO;

    /**
     * The current {@link TypeContext} on which the assignability computations
     * take place
     */
    private TypeContext typeContext;
    
    /**
     * The set of all {@link ModuleCreator} instances 
     */
    private final Set<ModuleCreator> moduleCreators;
    
    /**
     * The {@link Category} containing the {@link ModuleCreator} objects for
     * compatible source {@link Module} objects
     */
    private final MutableCategory<ModuleCreator> sourcesCategory;

    /**
     * The {@link Category} containing the {@link ModuleCreator} objects for
     * compatible target {@link Module} objects
     */
    private final MutableCategory<ModuleCreator> targetsCategory;

    /**
     * The {@link Category} containing the {@link ModuleCreator} objects for
     * {@link Module} objects that invoke methods on the outputs of other
     * {@link Module} objects
     */
    private final MutableCategory<ModuleCreator> targetMethodsCategory;

    /**
     * Creates a new compatibility model that will obtain the candidates
     * for compatible {@link ModuleCreator} objects from the given
     * collection.
     * 
     * @param moduleCreatorRepository The {@link Repository} of 
     * {@link ModuleCreator} instances
     * @param moduleCreators The set of all {@link ModuleCreator}
     */
    public ModuleCompatibilityModel(
        Collection<? extends ModuleCreator> moduleCreators)
    {
        this.moduleCreators = 
            new LinkedHashSet<ModuleCreator>(moduleCreators);
        
        this.sourcesCategory = Categories.create("Sources");
        this.targetsCategory = Categories.create("Targets");
        this.targetMethodsCategory = Categories.create("Methods");
    }

    /**
     * Set the {@link Module} objects for which this model should provide
     * compatibility information
     * 
     * @param typeContext The {@link TypeContext}
     * @param modules The relevant {@link Module} objects
     */
    public void setModules(
        TypeContext typeContext, 
        Iterable<? extends Module> modules)
    {
        this.typeContext = Objects.requireNonNull(
            typeContext, "The typeContext may not be null");
        
        updateSourcesCategory(modules);
        updateTargetsCategory(modules);
        updateTargetMethodsCategory(modules);
    }

    
    /**
     * Returns the {@link Category} containing {@link ModuleCreator} objects
     * for compatible source {@link Module} objects
     * 
     * @return The {@link Category} for sources 
     */
    public Category<ModuleCreator> getSourcesCategory()
    {
        return sourcesCategory;
    }
    
    /**
     * Returns the {@link Category} containing {@link ModuleCreator} objects
     * for compatible target {@link Module} objects
     * 
     * @return The {@link Category} for targets 
     */
    public Category<ModuleCreator> getTargetsCategory()
    {
        return targetsCategory;
    }
    
    /**
     * Returns the {@link Category} containing {@link ModuleCreator} objects
     * for compatible method {@link Module} objects
     * 
     * @return The {@link Category} for method invokers 
     */
    public Category<ModuleCreator> getTargetMethodsCategory()
    {
        return targetMethodsCategory;
    }
    

    /**
     * Update the {@link #sourcesCategory} for the given {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     */
    private void updateSourcesCategory(
        Iterable<? extends Module> modules)
    {
        sourcesCategory.removeAllChildren();
        sourcesCategory.removeAllElements();
        
        for (Module module : modules)
        {
            updateSourcesCategory(module);
        }
    }

    /**
     * Update the {@link #sourcesCategory} for the given {@link Module} object
     * 
     * @param module The {@link Module} object
     */
    private void updateSourcesCategory(Module module)
    {
        List<InputSlot> inputSlots = module.getInputSlots();
        for (int i = 0; i < inputSlots.size(); i++)
        {
            InputSlot inputSlot = inputSlots.get(i);
            if (inputSlot.getInputLink() != null)
            {
                continue;
            }
            Type inputType = inputSlot.getExpectedType();
            Predicate<ModuleCreator> predicate = 
                hasOutputTypeAssignableTo(inputType);
            Set<ModuleCreator> compatibleModuleCreators = 
                ModuleCompatibilityModel.filter(
                    moduleCreators, predicate);

            //ModuleCompatibilityModel.logCompatibility(
            //    "Compatible source modules for " + inputType, 
            //    compatibleModuleCreators);

            MutableCategory<ModuleCreator> subCategory = 
                sourcesCategory.addChild("for slot " + i);
            subCategory.addElements(compatibleModuleCreators);
        }
    }
    
    

    /**
     * Update the {@link #targetsCategory} for the given {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     */
    private void updateTargetsCategory(
        Iterable<? extends Module> modules)
    {
        targetsCategory.removeAllChildren();
        targetsCategory.removeAllElements();

        List<Type> selectedOutputTypes = 
            computeActualOutputTypes(modules);
        
        if (selectedOutputTypes.isEmpty())
        {
            MutableCategory<ModuleCreator> subCategory = 
                targetsCategory.addChild("for none");
            Predicate<ModuleCreator> predicate =
                ModuleCompatibilityModel.hasNoInputs();
            Set<ModuleCreator> compatibleModuleCreators = 
                filter(moduleCreators, predicate);
            subCategory.addElements(compatibleModuleCreators);
        }
        else
        {
            addTargetsForAllSubsets(selectedOutputTypes);
            addPartiallySatisfiedTargets(selectedOutputTypes);
        }
    }


    
    /**
     * Compute the {@link ModuleCreator} objects that can create 
     * {@link Module} objects that can accept subsets of the selected 
     * output types, and add them to the {@link #targetsCategory}.
     * 
     * @param selectedOutputTypes The selected output types
     */
    private void addTargetsForAllSubsets(
        List<Type> selectedOutputTypes)
    {
        List<List<Type>> sortedPowerSet = 
            Combinatorics.computeSortedPowerSet(selectedOutputTypes);

        for (int i = sortedPowerSet.size() - 1; i > 0; i--)
        {
            List<Type> subsetOfSelectedOutputTypes = sortedPowerSet.get(i);
            
            Predicate<ModuleCreator> predicate = 
                hasInputTypesAssignableFromAll(subsetOfSelectedOutputTypes);
            Set<ModuleCreator> compatibleModuleCreators = 
                filter(moduleCreators, predicate);
            
            Set<ModuleCreator> alreadyDisplayedModuleCreators = 
                Categories.getAllElements(targetsCategory);
            compatibleModuleCreators.removeAll(alreadyDisplayedModuleCreators);
            
            if (!compatibleModuleCreators.isEmpty())
            {
                MutableCategory<ModuleCreator> subCategory = 
                    targetsCategory.addChild(
                        "for " + subsetOfSelectedOutputTypes.size());
                subCategory.addElements(compatibleModuleCreators);
            }
        }
    }
    

    /**
     * Compute the {@link ModuleCreator} objects that can create {@link Module} 
     * objects that can accept any of the selected output types (but may still 
     * require other inputs), and add them to the {@link #targetsCategory}.
     * 
     * @param selectedOutputTypes The selected input types
     */
    private void addPartiallySatisfiedTargets(
        List<? extends Type> selectedOutputTypes)
    {
        Predicate<ModuleCreator> predicate = 
            hasInputTypeAssignableFromAny(selectedOutputTypes);
        Set<ModuleCreator> compatibleModuleCreators = 
            filter(moduleCreators, predicate);
        
        Set<ModuleCreator> alreadyDisplayedModuleCreators = 
            Categories.getAllElements(targetsCategory);
        compatibleModuleCreators.removeAll(alreadyDisplayedModuleCreators);
        
        if (!compatibleModuleCreators.isEmpty())
        {
            MutableCategory<ModuleCreator> subCategory = 
                targetsCategory.addChild("partial");
            subCategory.addElements(compatibleModuleCreators);
        }
    }

    /**
     * Update the {@link #targetMethodsCategory} with the {@link ModuleCreator} 
     * objects that can create compatible method invoking {@link Module} 
     * objects for the outputs of the given set of {@link Module} objects.<br>
     * <br>
     * Note that these {@link ModuleCreator} objects are NOT added to the
     * {@link #moduleCreatorRepository} in this method.
     *  
     * @param modules The relevant {@link Module} objects
     */
    private void updateTargetMethodsCategory(Iterable<? extends Module> modules)
    {
        targetMethodsCategory.removeAllChildren();
        targetMethodsCategory.removeAllElements();

        // Collect the output types of the selected modules
        // and add one category for each output type to the
        // methodsCategory
        Set<Type> selectedOutputTypes = 
            new LinkedHashSet<Type>(
                computeActualOutputTypes(modules));
        for (Type outputType : selectedOutputTypes)
        {
            MutableCategory<ModuleCreator> typeCategory = 
                targetMethodsCategory.addChild(String.valueOf(outputType));
            MethodModuleCreators.addInstanceMethodModuleCreators(
                typeCategory, outputType);
        }
        Categories.removeEmptyCategories(targetMethodsCategory);
    }
    

    
    
    /**
     * Create a predicate that says whether a {@link ModuleCreator} creates
     * {@link Module} objects that do not need any inputs
     * 
     * @return The predicate
     */
    private static Predicate<ModuleCreator> hasNoInputs()
    {
        return moduleCreator -> 
        {
            ModuleInfo moduleInfo = moduleCreator.getModuleInfo();
            return ModuleInfoUtils.hasNoInputs(moduleInfo);
        };
    }
    
    /**
     * Create a predicate that says whether a {@link ModuleCreator} creates
     * {@link Module} objects where any output type is assignable to the
     * given type
     * 
     * @param type The type to assign from
     * @return The predicate
     */
    Predicate<ModuleCreator> hasOutputTypeAssignableTo(Type type)
    {
        return moduleCreator -> 
        {
            ModuleInfo moduleInfo = moduleCreator.getModuleInfo();
            return ModuleInfoUtils.hasOutputTypeAssignableTo(
                typeContext, moduleInfo, type);
        };
    }
    
    /**
     * Create a predicate that says whether a {@link ModuleCreator} creates
     * {@link Module} objects where any input type is assignable from any
     * of the given types
     * 
     * @param types The types to assign from
     * @return The predicate
     */
    private Predicate<ModuleCreator> hasInputTypeAssignableFromAny(
        List<? extends Type> types)
    {
        return moduleCreator -> 
        {
            ModuleInfo moduleInfo = moduleCreator.getModuleInfo();
            return ModuleInfoUtils.hasInputTypeAssignableFromAny(
                typeContext, moduleInfo, types);
        };
    }

    /**
     * Create a predicate that says whether a {@link ModuleCreator} creates
     * {@link Module} objects where the input types are assignable from
     * the given types (in any order) 
     * 
     * @param types The types to assign from
     * @return The predicate
     */
    private Predicate<ModuleCreator> hasInputTypesAssignableFromAll(
        List<? extends Type> types)
    {
        return moduleCreator -> 
        {
            ModuleInfo moduleInfo = moduleCreator.getModuleInfo();
            return ModuleInfoUtils.hasInputTypesAssignableFromAll(
                typeContext, moduleInfo, types);
        };
    }
    
    /**
     * Returns a new set by filtering the given {@link ModuleCreator} 
     * collection with the given predicate
     * 
     * @param moduleCreators The {@link ModuleCreator} collection
     * @param predicate The predicate
     * @return The filtered set
     */
    static Set<ModuleCreator> filter(
        Collection<? extends ModuleCreator> moduleCreators,
        Predicate<? super ModuleCreator> predicate)
    {
        return moduleCreators.stream()
            .filter(predicate)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    
    /**
     * Returns a list containing all {@link Slot#getActualType() actual types}
     * of the {@link OutputSlot} objects of the given {@link Module} objects
     * 
     * @param modules The {@link Module} objects
     * @return The output types of the given {@link Module} objects
     */
    private static List<Type> computeActualOutputTypes(
        Iterable<? extends Module> modules)
    {
        List<Type> outputTypes = new ArrayList<Type>();
        for (Module module : modules)
        {
            outputTypes.addAll(computeActualTypes(module.getOutputSlots()));
        }
        return outputTypes;
    }
    
    /**
     * Returns a list containing the {@link Slot#getActualType() actual types}
     * of the given {@link Slot} objects.
     * 
     * @param slots The {@link Slot} objects
     * @return The list of types
     */
    private static List<Type> computeActualTypes(Iterable<? extends Slot> slots)
    {
        List<Type> result = new ArrayList<Type>();
        for (Slot slot : slots)
        {
            result.add(slot.getActualType());
        }
        return result;
    }
    
    /**
     * Print a logging message containing the {@link ModuleInfo} strings
     * of the given {@link ModuleCreator} objects
     * 
     * @param message An additional message
     * @param moduleCreators The {@link ModuleCreator} objects
     */
    static void logCompatibility(String message, 
        Iterable<? extends ModuleCreator> moduleCreators)
    {
        if (logger.isLoggable(COMPATIBLE_MODULES_LOG_LEVEL))
        {
            Level level = COMPATIBLE_MODULES_LOG_LEVEL;
            logger.log(level, message);
            for (ModuleCreator moduleCreator : moduleCreators)
            {
                logger.log(level, "    " + moduleCreator.getModuleInfo());
            }
        }
    }
    
    
}
