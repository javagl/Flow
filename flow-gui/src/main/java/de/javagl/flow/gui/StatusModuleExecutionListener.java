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
package de.javagl.flow.gui;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.javagl.flow.module.ModuleExecutionEvent;
import de.javagl.flow.module.ModuleExecutionListener;

/**
 * Implementation of a {@link ModuleExecutionListener} that writes
 * messages about the execution status into a {@link ModuleComponent}
 */
final class StatusModuleExecutionListener implements ModuleExecutionListener
{
    /**
     * The {@link ModuleComponent}
     */
    private ModuleComponent moduleComponent;
    
    /**
     * Creates a new listener that will write status messages
     * into the given {@link ModuleComponent}
     * 
     * @param moduleComponent The {@link ModuleComponent}
     */
    StatusModuleExecutionListener(ModuleComponent moduleComponent)
    {
        this.moduleComponent = moduleComponent;
    }

    @Override
    public void beforeExecution(ModuleExecutionEvent moduleExecutionEvent)
    {
        moduleComponent.setMessage("Waiting for input...", null);
    }
    
    @Override
    public void beforeProcessing(ModuleExecutionEvent moduleExecutionEvent)
    {
        moduleComponent.setMessage("Processing...", null);
    }
    
    @Override
    public void progressChanged(ModuleExecutionEvent moduleExecutionEvent)
    {
        moduleComponent.setMessage(moduleExecutionEvent.getMessage(), null);
        moduleComponent.setProgress(moduleExecutionEvent.getProgress());
    }
    
    @Override
    public void afterProcessing(ModuleExecutionEvent moduleExecutionEvent)
    {
        moduleComponent.setMessage("Writing output...", null);
        moduleComponent.setProgress(0);
    }
    
    @Override
    public void afterExecution(ModuleExecutionEvent moduleExecutionEvent)
    {
        Throwable t = moduleExecutionEvent.getThrowable();
        if (t == null)
        {
            moduleComponent.setMessage("Done", null);
        }
        else
        {
            String detailedMessage = createDetailedMessageHTML(t);
            moduleComponent.setMessage(
                "Error: " + t.getMessage(), detailedMessage);
        }
        moduleComponent.setProgress(0);
    }

    /**
     * Creates an HTML string containing the stack trace of the
     * given throwable, or <code>null</code> if the given 
     * throwable is <code>null</code>
     *   
     * @param t The throwable
     * @return The message string
     */
    private static String createDetailedMessageHTML(Throwable t)
    {
        if (t == null)
        {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        String s = sw.toString();
        s = s.replaceAll("\\\n", "<br>&nbsp;&nbsp;&nbsp;&nbsp;");
        String detailedMessage = "<html>" + s + "</html>";
        return detailedMessage;
    }

}