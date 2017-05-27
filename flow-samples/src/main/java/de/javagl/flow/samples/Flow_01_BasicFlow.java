package de.javagl.flow.samples;

import java.awt.List;

import de.javagl.flow.Flows;
import de.javagl.flow.MutableFlow;
import de.javagl.flow.execution.FlowExecutor;
import de.javagl.flow.execution.FlowExecutors;
import de.javagl.flow.link.Links;
import de.javagl.flow.module.Module;
import de.javagl.flow.module.Modules;

/**
 * An example for using the flow library to build a basic flow, consisting
 * of an input module that provides a list of strings, a processing module
 * that reverses all strings in a list and returns them as a new list,
 * and a module that prints a list of strings to the console.<br>
 * <br>
 * (All the modules in this example are created from method references.
 * Other examples will show how to create other types of (custom) modules) 
 */
@SuppressWarnings("javadoc")
public class Flow_01_BasicFlow
{
    public static void main(String[] args)
    {
        
        // Create one Module for each method 
        Module input = Modules.createForSupplier("Create strings", 
            "", SampleFunctions::createStrings, List.class);
        
        Module process = Modules.createForFunction("Reverse strings", 
            "", SampleFunctions::reverseStrings, List.class, List.class);

        Module output = Modules.createForConsumer("Print strings", 
            "", SampleFunctions::printStrings, List.class);
        
        // Add the modules to a flow
        MutableFlow flow = Flows.create();
        flow.addModule(input);
        flow.addModule(process);
        flow.addModule(output);
        
        // Add connections between the modules
        flow.addLink(Links.create(input, 0, process, 0));
        flow.addLink(Links.create(process, 0, output, 0));
        
        // Execute the flow
        FlowExecutor flowExecutor = FlowExecutors.create();
        flowExecutor.execute(flow);
    }
    
}
