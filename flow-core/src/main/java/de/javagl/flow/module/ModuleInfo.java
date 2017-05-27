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

import java.util.List;

import de.javagl.flow.module.slot.InputSlot;
import de.javagl.flow.module.slot.OutputSlot;
import de.javagl.flow.module.slot.SlotInfo;

/**
 * Interface summarizing the unmodifiable information about a {@link Module}. 
 * Instances of this class describe a type of a {@link Module}, and its 
 * {@link InputSlot} and {@link OutputSlot} information. Each {@link Module} 
 * has an associated {@link ModuleInfo}. 
 */
public interface ModuleInfo
{
    /**
     * Returns the name of the {@link Module}. This should be a short,
     * human-readable name that could be used as a title or label 
     * in a user interface.
     * 
     * @return The name of the {@link Module}
     */
    String getName();
    
    /**
     * Returns a textual, human-readable description of the {@link Module}
     * 
     * @return A description of the {@link Module}
     */
    String getDescription();

    /**
     * Returns an unmodifiable list containing the {@link SlotInfo} objects
     * for the {@link InputSlot} objects of the {@link Module}
     * 
     * @return The input {@link SlotInfo} objects
     */
    List<SlotInfo> getInputSlotInfos();

    /**
     * Returns an unmodifiable list containing the {@link SlotInfo} objects
     * for the {@link OutputSlot} objects of the {@link Module}
     * 
     * @return The output {@link SlotInfo} objects
     */
    List<SlotInfo> getOutputSlotInfos();

}
