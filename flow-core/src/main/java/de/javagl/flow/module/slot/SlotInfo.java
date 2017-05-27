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
package de.javagl.flow.module.slot;

import java.lang.reflect.Type;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;


/**
 * Description of one {@link Slot} of a {@link Module}. Instances of
 * classes implementing this interface are contained in a 
 * {@link ModuleInfo}, namely the {@link ModuleInfo#getInputSlotInfos()}
 * and {@link ModuleInfo#getOutputSlotInfos()} 
 */
public interface SlotInfo
{
    /**
     * Returns the type of the {@link Slot}
     * 
     * @return The type of the {@link Slot}
     */
    Type getType();
    
    /**
     * Returns the name of the {@link Slot}. This should be a short,
     * human-readable name that could be used as a title or label 
     * in a user interface.
     * 
     * @return The name of the {@link Slot}
     */
    String getName();
    
    /**
     * Returns a textual, human-readable description of the {@link Slot}
     * 
     * @return A description of the {@link Slot}
     */
    String getDescription();
}
