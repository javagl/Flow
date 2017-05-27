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

package de.javagl.flow.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import de.javagl.flow.module.creation.ModuleCreator;

/**
 * This class is responsible for resolving services that may create 
 * {@link ModuleCreator} instances. It will resolve services that implement 
 * the {@link ModuleCreatorSource} interface, and offer these services to 
 * callers.
 */
class ModuleCreatorService
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(ModuleCreatorService.class.getName());
    
    /**
     * The ServiceLoader for {@link ModuleCreatorSource} instances
     */
    private static final ServiceLoader<ModuleCreatorSource> 
        moduleCreatorSources = 
            ServiceLoader.load(ModuleCreatorSource.class);
    
    
    /**
     * Return an unmodifiable list containing all 
     * {@link ModuleCreatorSource} instances that can be found on 
     * the class path.
     * 
     * @return The list of {@link ModuleCreatorSource} instances
     */
    public static List<ModuleCreatorSource> createModuleCreatorSources() 
    {
        List<ModuleCreatorSource> sources = 
            new ArrayList<ModuleCreatorSource>();
        for (ModuleCreatorSource source : moduleCreatorSources) 
        {
            logger.info("Found ModuleCreatorSource: " + source);
            sources.add(source);
        }
        return Collections.unmodifiableList(sources);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private ModuleCreatorService()
    {
        // Private constructor to prevent instantiation
    }
}
