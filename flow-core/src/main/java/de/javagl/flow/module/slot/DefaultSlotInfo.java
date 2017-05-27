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
import java.util.Objects;


/**
 * Default implementation of a {@link SlotInfo}
 */
class DefaultSlotInfo implements SlotInfo
{
    /**
     * The type of the {@link Slot}
     */
    private final Type type;
    
    /**
     * The name of the {@link Slot}
     */
    private final String name;
    
    /**
     * The description of the {@link Slot}
     */
    private final String description;

    /**
     * Creates a new slot info with the given type, name and description
     * 
     * @param type The type
     * @param name The name
     * @param description The description 
     * @throws NullPointerException If any argument is <code>null</code>
     */
    DefaultSlotInfo(Type type, String name, String description)
    {
        this.type = Objects.requireNonNull(type, "The type may not be null");
        this.name = Objects.requireNonNull(name, "The name may not be null");
        this.description = Objects.requireNonNull(
            description, "The description may not be null");
    }
    
    @Override
    public Type getType()
    {
        return type;
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
    public String toString()
    {
        return "DefaultSlotInfo[" +
            "type=" + type + "," +
            "description=" + description + "]";
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hashCode(description);
        result = prime * result + Objects.hashCode(type);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SlotInfo))
            return false;
        SlotInfo other = (SlotInfo) obj;
        if (!Objects.equals(description, other.getDescription()))
            return false;
        if (!Objects.equals(type, other.getType()))
            return false;
        return true;
    }

}
