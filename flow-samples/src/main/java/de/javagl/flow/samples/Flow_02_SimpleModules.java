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
 * An example showing how to create a flow with simple custom modules. 
 * Particularly, with modules that have multiple inputs and outputs.
 * The {@link SampleModules#createSplitListModule()} and 
 * {@link SampleModules#createMergeListsModule()}
 * methods show basic approaches for implementing simple custom modules.
 */
@SuppressWarnings("javadoc")
public class Flow_02_SimpleModules
{
    public static void main(String[] args)
    {
        MutableFlow flow = Flows.create();
        
        Module input = Modules.createForSupplier("Create strings", 
            "", SampleFunctions::createStrings, List.class);
        
        Module split = SampleModules.createSplitListModule();
        
        Module process0 = Modules.createForFunction( "Reverse strings", 
            "", SampleFunctions::reverseStrings, 
            List.class, List.class);
        Module process1 = Modules.createForFunction("Reverse strings", 
            "", SampleFunctions::reverseStrings, List.class, List.class);

        Module merge = SampleModules.createMergeListsModule();
        
        Module output = Modules.createForConsumer("Print strings", 
            "", SampleFunctions::printStrings, List.class);
        
        flow.addModule(input);
        flow.addModule(split);
        flow.addModule(process0);
        flow.addModule(process1);
        flow.addModule(merge);
        flow.addModule(output);
        
        flow.addLink(Links.create(input, 0, split, 0));
        flow.addLink(Links.create(split, 0, process0, 0));
        flow.addLink(Links.create(split, 1, process1, 0));
        flow.addLink(Links.create(process0, 0, merge, 0));
        flow.addLink(Links.create(process1, 0, merge, 1));
        flow.addLink(Links.create(merge, 0, output, 0));
        
        // The flow now looks like this:
        // 
        //   -------      -------      ----------      -------       -------- 
        //  |       |    |       |--->| process0 |--->|       |     |        |
        //  |       |    |       |     ----------     |       |     |        |
        //  | input |--->| split |                    | merge |---> | output |
        //  |       |    |       |     ----------     |       |     |        |
        //  |       |    |       |--->| process1 |--->|       |     |        |
        //   -------      -------      ----------      -------       -------- 
        
        
        FlowExecutor flowExecutor = FlowExecutors.create();
        flowExecutor.execute(flow);
    }
}
