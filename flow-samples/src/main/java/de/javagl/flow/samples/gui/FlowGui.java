package de.javagl.flow.samples.gui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.javagl.flow.samples.Utils;
import de.javagl.flow.samples.gui01.FlowGui_01_Basic;
import de.javagl.flow.samples.gui02.FlowGui_02_CustomVisualization;

@SuppressWarnings("javadoc")
public class FlowGui
{
    public static void main(String[] args)
    {
        Utils.defaultInitialization();
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }

    private static void createAndShowGui()
    {
        JFrame f = new JFrame("Demos");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        p.add(createButton("01_Basic", 
            FlowGui_01_Basic::createAndShowGui));
        p.add(createButton("02_CustomVisualization", 
            FlowGui_02_CustomVisualization::createAndShowGui));
        
        f.getContentPane().add(p);
        
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    private static JButton createButton(String name, Runnable runnable)
    {
        JButton button = new JButton(name);
        button.addActionListener(e -> runnable.run());
        return button;
    }
}
