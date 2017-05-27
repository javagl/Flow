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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import de.javagl.flow.Flow;
import de.javagl.flow.link.Link;
import de.javagl.flow.workspace.FlowWorkspace;
import de.javagl.selection.SelectionModel;

/**
 * The panel that is in layer -1 of the desktop pane of a 
 * {@link FlowEditorComponent}. It is responsible for painting the {@link Link}
 * representations.
 */
final class LinksPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 3539981747036379983L;

    /**
     * The color for selected links
     */
    private static final Color SELECTION_COLOR = Color.RED;

    /**
     * The color for default links 
     */
    private static final Color DEFAULT_COLOR = Color.BLUE;

    /**
     * The {@link LinkShapeProvider}
     */
    private final LinkShapeProvider linkShapeProvider;
    
    /**
     * The current line, i.e. the line representing a 
     * {@link Link} that is currently being created
     */
    private Line2D currentLine = null;
    
    /**
     * The color of the current line (e.g. green if the
     * corresponding link can be created, or red if it
     * can not be created)
     */
    private Color currentLineColor = null;

    /**
     * The currently highlighted link (i.e. the link
     * that is currently under the mouse cursor)
     */
    private Link highlightedLink = null;
    
    /**
     * The {@link FlowWorkspace} which contains the
     * selection state of the {@link Link} objects
     */
    private FlowWorkspace flowWorkspace;
    
    /**
     * Creates a new links panel that paints {@link Link} objects
     * with shapes that are provided by the given
     * {@link LinkShapeProvider}
     * 
     * @param linkShapeProvider The {@link LinkShapeProvider}
     */
    LinksPanel(LinkShapeProvider linkShapeProvider)
    {
        this.linkShapeProvider = linkShapeProvider;
        setOpaque(false);
    }
    
    /**
     * Set the {@link FlowWorkspace} that provides information
     * about {@link Link} objects and their selection state
     * 
     * @param flowWorkspace The {@link FlowWorkspace}
     */
    void setFlowWorkspace(FlowWorkspace flowWorkspace)
    {
        this.flowWorkspace = flowWorkspace;
    }
    
    
    /**
     * Set the current line and its color, i.e. the line representing a
     * {@link Link} that is about to be created. 
     * 
     * @param currentLine The current line. May be <code>null</code>
     * @param color The color of the current line
     */
    void setCurrentLine(Line2D currentLine, Color color)
    {
        this.currentLine = currentLine;
        this.currentLineColor = color;
        repaint();
    }
    
    /**
     * Set the {@link Link} that is currently highlighted
     * due to the mouse hovering over it
     * 
     * @param link The {@link Link}
     */
    void setHighlightedLink(Link link)
    {
        this.highlightedLink = link;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics gr)
    {
        super.paintComponent(gr);
        
        Graphics2D g = (Graphics2D)gr.create();
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);

        paintNormalLinks(g);
        paintHighlightedLink(g);
        paintLinkContents(g);
        paintCurrentLine(g);
    }


    /**
     * Paint all basic links into the given graphics
     * 
     * @param g The graphics
     */
    private void paintNormalLinks(Graphics2D g)
    {
        Flow flow = flowWorkspace.getFlow();
        for (Link link : flow.getLinks())
        {
            paintLink(g, link, 3.0f);
        }
    }
    
    /**
     * Paint the currently highlighted link into the given graphics
     * 
     * @param g The graphics
     */
    private void paintHighlightedLink(Graphics2D g)
    {
        if (highlightedLink != null)
        {
            paintLink(g, highlightedLink, 6.0f);
        }
    }
    
    /**
     * Paint the given link into the given graphics
     * 
     * @param g The graphics
     * @param link The {@link Link}
     * @param width The width of the link line
     */
    private void paintLink(Graphics2D g, Link link, float width)
    {
        Shape shape = linkShapeProvider.shapeForLink(link);
        SelectionModel<Link> linkSelectionModel = 
            flowWorkspace.getLinkSelectionModel();
        if (linkSelectionModel.isSelected(link))
        {
            g.setColor(SELECTION_COLOR);
            g.setStroke(new BasicStroke(width + 3.0f));
            g.draw(shape);
        }
        boolean linkIsValid = true;
        //linkIsValid = Links.isValid(link); // Optionally check this each time
        if (linkIsValid)
        {
            g.setColor(DEFAULT_COLOR);
            g.setStroke(new BasicStroke(width));
        }
        else
        {
            BasicStroke dashedStroke =
                new BasicStroke(width,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    5.0f, new float[] { 5.0f }, 0.0f);                
            g.setColor(Color.ORANGE);
            g.setStroke(dashedStroke);
        }
        g.draw(shape);
    }

    /**
     * Paint all links contents indicators into the given graphics
     * 
     * @param g The graphics
     */
    private void paintLinkContents(Graphics2D g)
    {
        Flow flow = flowWorkspace.getFlow();
        for (Link link : flow.getLinks())
        {
            int contentsSize = link.getContents().size();
            if (contentsSize != 0)
            {
                Shape line = linkShapeProvider.shapeForLink(link);
                Rectangle bounds = line.getBounds();
                int cx = (int) bounds.getCenterX();
                int cy = (int) bounds.getCenterY();
                g.setColor(Color.BLUE);
                g.fillOval(cx - 10, cy - 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf(contentsSize), cx - 8, cy + 5);
            }
        }
    }
    
    /**
     * Paint the current line into the given graphics
     * 
     * @param g The graphics
     */
    private void paintCurrentLine(Graphics2D g)
    {
        if (currentLine != null)
        {
            g.setStroke(new BasicStroke(6));
            g.setColor(currentLineColor);
            g.draw(currentLine);
        }
    }

    
    
    
}