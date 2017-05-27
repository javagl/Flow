package de.javagl.flow.samples;

import java.util.List;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;
import de.javagl.flow.module.ModuleInfos;
import de.javagl.flow.module.SimpleAbstractModule;

/**
 * Methods to create Module instances for the samples
 */
public class SampleModules
{
    /**
     * Create the module information for the module that is created in
     * {@link #createSplitListModule()}
     * 
     * @return The module info
     */
    public static ModuleInfo createSplitListModuleInfo()
    {
        // The ModuleInfo will determine the interface of the module, 
        // namely the inputs and outputs that it has.
        ModuleInfo moduleInfo = ModuleInfos.create("Split list", "")
            .addInput(List.class, "The list", "")
            .addOutput(List.class, "The first half of the list", "")
            .addOutput(List.class, "The second half of the list", "")
            .build();
        return moduleInfo;
    }
    
    /**
     * An example showing how to create a custom module, by extending the
     * SimpleAbstractModule class and implementing the process() method.
     * 
     * @return The module
     */
    public static Module createSplitListModule()
    {
        // Create the module by extending the SimpleAbstractModule class
        return new SimpleAbstractModule(createSplitListModuleInfo())
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void process()
            {
                // The first input (the list) is obtained here 
                Object input = obtainInput(0);
                List<Object> list = (List<Object>) input;
                
                System.out.println("Splitting " + input);
                List<List<Object>> lists = SampleFunctions.splitList(list);
                
                // The two parts of the list are forwarded to the outputs
                forwardOutput(0, lists.get(0)); 
                forwardOutput(1, lists.get(1));
            }
        };
    }

    /**
     * Create the module information for the module that is created in
     * {@link #createMergeListsModule()}
     * 
     * @return The module info
     */
    public static ModuleInfo createMergeListsModuleInfo()
    {
        // The ModuleInfo will determine the interface of the module, 
        // namely the inputs and outputs that it has.
        ModuleInfo moduleInfo = ModuleInfos.create("Merge lists", "")
            .addInput(List.class, "The first list", "")
            .addInput(List.class, "The second list", "")
            .addOutput(List.class, "The merged list", "")
            .build();
        return moduleInfo;
    }
    
    /**
     * An example showing how to create a custom module, by extending the
     * SimpleAbstractModule class and implementing the 
     * processCore(Object[], Object[]) method.
     * 
     * @return The module
     */
    public static Module createMergeListsModule()
    {
        return new SimpleAbstractModule(createMergeListsModuleInfo())
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void processCore(Object inputs[], Object outputs[])
            {
                // The inputs contain the first and the second list
                List<Object> input0 = (List<Object>) inputs[0];
                List<Object> input1 = (List<Object>) inputs[1];
                
                System.out.println("Merging " + input0 + " and " + input1);
                
                // Merge the lists 
                List<Object> output = 
                    SampleFunctions.mergeLists(input0, input1);
                
                // Set the output to be the new list
                outputs[0] = output;
            }
        };
    }
 
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SampleModules()
    {
        // Private constructor to prevent instantiation
    }
    
}
