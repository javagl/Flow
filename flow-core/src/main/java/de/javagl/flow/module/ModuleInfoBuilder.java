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
import java.util.List;
import java.util.Objects;

import de.javagl.flow.module.slot.SlotInfo;
import de.javagl.flow.module.slot.SlotInfos;

/**
 * A builder for {@link ModuleInfo} instances.
 */
public final class ModuleInfoBuilder
{
    /**
     * The name of the {@link Module}
     */
    private final String name;
    
    /**
     * The description of the {@link Module}
     */
    private final String description;
    
    /**
     * The input {@link SlotInfo} objects
     */
    private final List<SlotInfo> inputSlotInfos;
    
    /**
     * The output {@link SlotInfo} objects
     */
    private final List<SlotInfo> outputSlotInfos;
    
    /**
     * Create a builder for a {@link ModuleInfo} about a
     * {@link Module} with the given name and description.
     *  
     * @param name The name of the {@link Module}
     * @param description The description of the {@link Module}
     */
    ModuleInfoBuilder(String name, String description)
    {
        this.name = Objects.requireNonNull(name, "The name may not be null");
        this.description = Objects.requireNonNull(
            description, "The description may not be null");
        this.inputSlotInfos = new ArrayList<SlotInfo>();
        this.outputSlotInfos = new ArrayList<SlotInfo>();
    }

    /**
     * Add the specified input {@link SlotInfo} to the {@link ModuleInfo}
     * that is currently being built.
     * 
     * @param type The type of the slot to add.
     * @param name The name of the slot to add.
     * @param description The description for the slot.
     * @return This builder
     */
    public ModuleInfoBuilder addInput(Type type, 
        String name, String description)
    {
        inputSlotInfos.add(
            SlotInfos.create(type, name, description));
        return this;
    }

    /**
     * Add the specified output {@link SlotInfo} to the {@link ModuleInfo}
     * that is currently being built.
     * 
     * @param type The type of the slot to add.
     * @param name The name of the slot to add.
     * @param description The description for the slot.
     * @return This builder
     */
    public ModuleInfoBuilder addOutput(Type type, 
        String name, String description)
    {
        outputSlotInfos.add(
            SlotInfos.create(type, name, description));
        return this;
    }
    
    /**
     * Build the {@link ModuleInfo} that has been described so far.
     * 
     * @return The new {@link ModuleInfo}
     */
    public ModuleInfo build()
    {
        return new DefaultModuleInfo(
            name, description, 
            inputSlotInfos, 
            outputSlotInfos);
    }
    
    
}