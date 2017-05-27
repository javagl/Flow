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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.SlotInfo;

/**
 * Default implementation of a {@link ModuleInfo}
 */
final class DefaultModuleInfo implements ModuleInfo
{
    /**
     * The name of the {@link Module}
     */
    private final String name;
    
    /**
     * A short description of the {@link Module}
     */
    private final String description;

    /**
     * The list of {@link SlotInfo} instances describing the
     * {@link InputSlot} instances of the {@link Module}
     */
    private final List<SlotInfo> inputSlotInfos;

    /**
     * The list of {@link SlotInfo} instances describing the
     * {@link OutputSlot} instances of the {@link Module}
     */
    private final List<SlotInfo> outputSlotInfos;
    
    /**
     * Creates a new module info for a {@link Module} with the
     * name, description and {@link SlotInfo} instances.
     * 
     * @param name The name 
     * @param description The description
     * @param inputSlotInfos The input {@link SlotInfo} instances
     * @param outputSlotInfos The output {@link SlotInfo} instances
     */
    DefaultModuleInfo(
        String name,
        String description,
        List<SlotInfo> inputSlotInfos, 
        List<SlotInfo> outputSlotInfos)
    {
        this.name = name;
        this.description = description;
        this.inputSlotInfos = Collections.unmodifiableList(
            new ArrayList<SlotInfo>(inputSlotInfos));
        this.outputSlotInfos = Collections.unmodifiableList(
            new ArrayList<SlotInfo>(outputSlotInfos));
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public List<SlotInfo> getInputSlotInfos()
    {
        return inputSlotInfos;
    }

    @Override
    public List<SlotInfo> getOutputSlotInfos()
    {
        return outputSlotInfos;
    }
    
    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(getName());
        result = prime * result + Objects.hashCode(getDescription());
        result = prime * result + Objects.hashCode(getInputSlotInfos());
        result = prime * result + Objects.hashCode(getOutputSlotInfos());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ModuleInfo))
            return false;
        ModuleInfo other = (ModuleInfo) obj;
        if (!Objects.equals(getName(), other.getName()))
            return false;
        if (!Objects.equals(getDescription(), other.getDescription()))
            return false;
        if (!Objects.equals(getInputSlotInfos(), other.getInputSlotInfos()))
            return false;
        if (!Objects.equals(getOutputSlotInfos(), other.getOutputSlotInfos()))
            return false;
        return true;
    }


}