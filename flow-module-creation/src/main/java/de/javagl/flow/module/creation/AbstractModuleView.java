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
package de.javagl.flow.module.creation;

import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleExecutionAdapter;
import de.javagl.flow.module.ModuleExecutionEvent;
import de.javagl.flow.module.ModuleExecutionListener;
import de.javagl.flow.module.view.ModuleView;

/**
 * Abstract base implementation of a {@link ModuleView}. It offers
 * a Swing Component and a simple {@link #updateModuleView() update} 
 * mechanism that is triggered when a {@link Module} finished
 * processing. 
 */
public abstract class AbstractModuleView implements ModuleView
{
    /**
     * The {@link Module} that is shown in this view
     */
    private Module module;
    
    /**
     * The main {@link #getComponent() component} of this view
     */
    private final Container component;
    
    /**
     * The {@link ModuleExecutionListener} that will call
     * {@link #updateModuleView()} when the module was 
     * executed.
     */
    private final ModuleExecutionListener moduleExecutionListener;

    /**
     * Default constructor
     */
    protected AbstractModuleView()
    {
        moduleExecutionListener = new ModuleExecutionAdapter()
        {
            @Override
            public void afterProcessing(
                ModuleExecutionEvent moduleExecutionEvent)
            {
                invokeUpdateModuleViewOnEDT();
            }
        };
        component = new JPanel();
    }
    
    /**
     * Invoke the {@link #updateModuleView()} method on the Swing Event
     * Dispatch Thread
     */
    private final void invokeUpdateModuleViewOnEDT()
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            updateModuleView();
        }
        else
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    updateModuleView();
                }
            });
        }
    }
    
    /**
     * This method will be called when the {@link Module} finished
     * {@link ModuleExecutionListener#afterProcessing(ModuleExecutionEvent)
     * processing}, or when a new {@link Module} was assigned to this 
     * view.<br> 
     * <br>
     * <b>Note that this new {@link Module} may also be <code>null</code>!</b>
     * <br>
     * <br>
     * This method will be called on the Swing Event Dispatch Thread.
     * It may be used to update the state of this view based on the 
     * {@link Module}. An example implementation might look as follows:
     * <pre><code>
     * protected void updateModuleView() {
     *     Module module = getModule();
     *     if (module == null) {
     *         myComponent.clear();
     *         return;
     *     }
     *     myComponent.updateVisualizationFor(module);
     * }
     * </code></pre>
     */
    protected abstract void updateModuleView();

    /**
     * Returns the {@link Module} that is currently shown in this view,
     * or <code>null</code> when there is no {@link Module}.
     * 
     * @return The current {@link Module} of this view. 
     */
    protected final Module getModule()
    {
        return module;
    }
    
    @Override
    public final Container getComponent()
    {
        return component;
    }

    @Override
    public final void setModule(Module newModule)
    {
        if (module != null)
        {
            module.removeModuleExecutionListener(moduleExecutionListener);
        }
        module = newModule;
        if (module != null)
        {
            module.addModuleExecutionListener(moduleExecutionListener);
        }
        updateModuleView();
    }
}