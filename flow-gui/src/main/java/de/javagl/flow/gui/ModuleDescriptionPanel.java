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

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import de.javagl.flow.module.Module;
import de.javagl.flow.module.ModuleInfo;

/**
 * A panel showing a textual description of a {@link Module} or
 * a {@link ModuleInfo}
 */
final class ModuleDescriptionPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 8614168970643719914L;
    
    /**
     * The text component containing the HTML description
     */
    private JTextComponent textComponent;

    /**
     * Creates a new, empty info panel
     */
    ModuleDescriptionPanel()
    {
        setLayout(new GridLayout(1,1));
        textComponent = GuiUtils.createDefaultEditor();
        add(textComponent);
    }
    
    /**
     * Set the {@link Module} that should be summarized in
     * this component
     * 
     * @param module The {@link Module}
     */
    void setModule(final Module module)
    {
        textComponent.setText(GuiUtils.moduleString(module));
    }

    /**
     * Set the {@link ModuleInfo} that should be summarized in
     * this component
     * 
     * @param moduleInfo The {@link ModuleInfo}
     */
    void setModuleInfo(final ModuleInfo moduleInfo)
    {
        textComponent.setText(GuiUtils.moduleInfoString(moduleInfo));
    }
}