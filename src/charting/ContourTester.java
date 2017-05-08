/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package charting;

import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author dennismelamed
 */
public class ContourTester extends ApplicationFrame
{
    public ContourTester(String title)
    {
        super(title);
        
        float [] [] data = new float[3][5];
        data[0][0] = 0;
        data[0][1] = 2;
        data[0][2] = 4;
        data[0][3] = 6;
        data[0][4] = 20;
        data[1][0] = 1;
        data[1][1] = 3;
        data[1][2] = 5;
        data[1][3] = 7;
        data[1][4] = 9;
        data[2][0] = 2;
        data[2][1] = 1;
        data[2][2] = 3;
        data[2][3] = 6;
        data[2][4] = 2;
        
        
        
        
        
        
        ContourPlot plot = new ContourPlot(data,10,true, 0, 20,1,9,1,6, 10000);
        JFreeChart chart = new JFreeChart("ContourPlot", plot);
      //  chart.removeLegend();
        
        JPanel chartPanel = new ChartPanel(chart);
        
        
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
        
        chart.addSubtitle(plot.getPaintScaleLegend());
    }
    
    public static void main(String [] args)
    {
        ContourTester demo = new ContourTester(
                "ContourPlot.java");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
    
}
