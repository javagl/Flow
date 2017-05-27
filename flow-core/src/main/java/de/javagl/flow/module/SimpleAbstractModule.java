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

package de.javagl.flow.module;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.SlotInfo;
import de.javagl.flow.module.slot.Slots;

/**
 * Abstract base implementation of a simple {@link Module}. This class
 * extends {@link AbstractModule} by providing a list of 
 * {@link InputSlot} objects and {@link OutputSlot} objects. These are created 
 * based on the {@link ModuleInfo} that is given in the constructor.
 */
public abstract class SimpleAbstractModule 
    extends AbstractModule implements Module
{
    /**
     * The {@link InputSlot} objects of this module
     */
    private final List<InputSlot> inputSlots;

    /**
     * The {@link OutputSlot} objects of this module
     */
    private final List<OutputSlot> outputSlots;

    /**
     * Creates a new module with the given {@link ModuleInfo}
     * 
     * @param moduleInfo The {@link ModuleInfo}. May not be <code>null</code>.
     */
    protected SimpleAbstractModule(ModuleInfo moduleInfo)
    {
        super(moduleInfo);
        
        this.inputSlots = new ArrayList<InputSlot>();
        this.outputSlots = new ArrayList<OutputSlot>();
        
        TypeVariableInstantiator typeVariableInstantiator =
            createTypeVariableInstantiator();
        
        createInputSlots(typeVariableInstantiator);
        createOutputSlots(typeVariableInstantiator);
    }
    
    /**
     * Creates the {@link TypeVariableInstantiator} for this module.
     * For multiple, equal type variables that occur in the inputs
     * or outputs of this module, it will create unique instances
     * of type variables.
     *
     * @return The {@link TypeVariableInstantiator}
     */
    private TypeVariableInstantiator createTypeVariableInstantiator()
    {
        TypeVariableInstantiator typeVariableInstantiator =
            new TypeVariableInstantiator();
        List<SlotInfo> inputSlotInfos = getModuleInfo().getInputSlotInfos();
        for (int i=0; i<inputSlotInfos.size(); i++)
        {
            SlotInfo slotInfo = inputSlotInfos.get(i);
            typeVariableInstantiator.register(slotInfo.getType());
        }
        List<SlotInfo> outputSlotInfos = getModuleInfo().getOutputSlotInfos();
        for (int i=0; i<outputSlotInfos.size(); i++)
        {
            SlotInfo slotInfo = outputSlotInfos.get(i);
            typeVariableInstantiator.register(slotInfo.getType());
        }
        return typeVariableInstantiator;
    }
    
    /**
     * Create all input slots of this module
     * 
     * @param typeVariableInstantiator The {@link TypeVariableInstantiator}
     */
    private void createInputSlots(
        TypeVariableInstantiator typeVariableInstantiator)
    {
        List<SlotInfo> inputSlotInfos = getModuleInfo().getInputSlotInfos();
        for (int i=0; i<inputSlotInfos.size(); i++)
        {
            SlotInfo slotInfo = inputSlotInfos.get(i);
            Type slotInfoType = slotInfo.getType();
            Type formalType = 
                typeVariableInstantiator.instantiate(slotInfoType);
            InputSlot inputSlot = createInputSlot(i, formalType);
            inputSlots.add(inputSlot);
        }
    }
    
    /**
     * Create the {@link InputSlot} with the given index and the given
     * formal type of this {@link Module}. The default implementation
     * creates a default {@link InputSlot}. The method may be overridden
     * by subclasses to create specialized {@link InputSlot} objects. 
     * 
     * @param index The index of the {@link InputSlot} to create
     * @param formalType The formal type of the {@link InputSlot}, as
     * defined in the corresponding {@link SlotInfo}
     * @return The new {@link InputSlot}
     */
    protected InputSlot createInputSlot(int index, Type formalType)
    {
        return Slots.createInputSlot(this, index, formalType);
    }
    
    /**
     * Create all output slots of this module
     * 
     * @param typeVariableInstantiator The {@link TypeVariableInstantiator}
     */
    private void createOutputSlots(
        TypeVariableInstantiator typeVariableInstantiator)
    {
        List<SlotInfo> outputSlotInfos = getModuleInfo().getOutputSlotInfos();
        for (int i=0; i<outputSlotInfos.size(); i++)
        {
            SlotInfo slotInfo = outputSlotInfos.get(i);
            Type slotInfoType = slotInfo.getType();
            Type formalType = 
                typeVariableInstantiator.instantiate(slotInfoType);
            OutputSlot outputSlot = createOutputSlot(i, formalType);
            outputSlots.add(outputSlot);
        }
    }
    
    /**
     * Create the {@link OutputSlot} with the given index and the given
     * formal type of this {@link Module}. The default implementation
     * creates a default {@link OutputSlot}. The method may be overridden
     * by subclasses to create specialized {@link OutputSlot} objects. 
     * 
     * @param index The index of the {@link OutputSlot} to create
     * @param formalType The formal type of the {@link OutputSlot}, as
     * defined in the corresponding {@link SlotInfo}
     * @return The new {@link OutputSlot}
     */
    protected OutputSlot createOutputSlot(int index, Type formalType)
    {
        return Slots.createOutputSlot(this, index, formalType);
    }
    
    @Override
    public final List<InputSlot> getInputSlots()
    {
        return Collections.unmodifiableList(inputSlots);
    }
    
    @Override
    public final List<OutputSlot> getOutputSlots()
    {
        return Collections.unmodifiableList(outputSlots);
    }
    
}