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
package de.javagl.flow.module.view;

import de.javagl.flow.module.Module;

/**
 * A class offering predefined {@link ModuleViewType} instances, and methods to
 * create new ones.
 */
public final class ModuleViewTypes
{
    /**
     * The {@link ModuleViewType} identifying a {@link ModuleView} that shows
     * configuration settings for a {@link Module}
     */
    public static final ModuleViewType CONFIGURATION_VIEW = 
        create("Configuration");

    /**
     * The {@link ModuleViewType} identifying a {@link ModuleView} that shows
     * a visualization for a {@link Module}
     */
    public static final ModuleViewType VISUALIZATION_VIEW = 
        create("Visualization");

    /**
     * Create a new {@link ModuleViewType} with the given name
     * 
     * @param name The name for the {@link ModuleViewType}
     * @return The {@link ModuleViewType}
     */
    public static ModuleViewType create(String name)
    {
        return new ModuleViewType(name);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private ModuleViewTypes()
    {
        // Private constructor to prevent instantiation
    }


}
