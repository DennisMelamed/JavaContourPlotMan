/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package charting;

import edu.mines.jtk.interp.SibsonInterpolator2;
import java.awt.Color;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author dennismelamed
 */
public class ContourPlot extends XYPlot
{

    NumberAxis legend;
    NumberAxis xAxis, yAxis;

    PaintScaleLegend scaleLegend;
    LookupPaintScale colorLookupScale;
    PaintScale grayLookupScale;
    XYBlockRenderer renderer;
    XYZDataset xyzset;

    /**
     *
     */
    public ContourPlot()
    {
        super();
    }

    /**
     *
     * @param data a 2d array of floats, data[0] is a list of x values, data[1] is a list of the corresponding y values, and data[2] is the z value of each point
     * @param accuracy number of boxes to be drawn between each unit tick mark (higher number means higher resolution contour plot)
     * @param color boolean defining if the chart should be in color or not
     * @param lowDomain lowest value in the domain of the dataset
     * @param highDomain highest value in the domain of the dataset
     * @param lowRange lowest value in the range of the dataset
     * @param highRange highest value in the range of the dataset
     * @param lowColor lowest value that should appear on the color-grayLookupScale (useful to focus in on small variations)
     * @param highColor highest color that should appear on the color-grayLookupScale (useful to focus in on small variations)
     * @param numberOfColors ONLY FOR COLOR PLOTS: number of colors to define in between the highColor and lowColors
     */
    public ContourPlot(float[][] data, double accuracy, boolean color, double lowDomain, double highDomain, double lowRange, double highRange, double lowColor, double highColor, int numberOfColors)
    {
        super();

        setBothAxis(lowDomain, highDomain, lowRange, highRange);

        setRenderer(accuracy);

        setPaintScaleLegend(color,lowColor,highColor, numberOfColors);

        setDataset(data, accuracy);

        setRenderer(renderer);

    }
    
    /**
     *
     * @param data data[0] is a list of x values, data[1] is a list of the corresponding y values, and data[2] is the z value of each point
     * @param accuracy number of boxes to be drawn between each unit tick mark (higher number means higher resolution contour plot)
     */
    public void setDataset(float[] [] data, double accuracy)
    {
        xyzset = new ContourPlotDataset(data, accuracy);
        setDataset(xyzset);
    }
    
    /**
     *
     * @param lowDomain lowest x value to display
     * @param highDomain highest x value to display
     * @param lowRange lowest y value to display
     * @param highRange highest y value to display
     */
    public void setBothAxis(double lowDomain, double highDomain, double lowRange, double highRange)
    {
        xAxis = new NumberAxis("X");
        yAxis = new NumberAxis("Y");
        xAxis.setRange(lowDomain, highDomain);
        yAxis.setRange(lowRange, highRange);
        this.setDomainAxis(xAxis);
        this.setRangeAxis(yAxis);
    }
    
    /**
     *
     * @param accuracy number of boxes to be drawn between each unit tick mark (higher number means higher resolution contour plot)
     */
    public void setRenderer(double accuracy)
    {
        renderer = new XYBlockRenderer();
        renderer.setBlockHeight(1/accuracy);
        renderer.setBlockWidth(1/accuracy);
    }
    
    /**
     *
     * @param color false=grayscale chart, true = color chart
     * @param lowColor lowest value that should appear on the color/gray LookupScale (useful to focus in on small variations)
     * @param highColor highest value that should appear on the color/gray LookupScale (useful to focus in on small variations)
     * @param numberOfColors
     */
    public void setPaintScaleLegend(boolean color, double lowColor, double highColor, int numberOfColors)
    {
        legend = new NumberAxis("color scale");
        legend.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        if (color)
        {
            colorLookupScale = new LookupPaintScale(lowColor, highColor, Color.lightGray);
            //make the appropriate range for the lookup table for the colored paintLookupScale
            //creates numberOfColors colors between the low and high colors the user sets
            for (double i = lowColor; i < highColor; i += ((highColor - lowColor) / numberOfColors))
            {
                float scaleColor = (float) (.7 - ((.7 * (i - lowColor)) / (highColor - lowColor)));
                colorLookupScale.add(i, Color.getHSBColor(scaleColor, (float) .9, (float) .9));
            }
            renderer.setPaintScale(colorLookupScale);
            scaleLegend = new PaintScaleLegend(colorLookupScale, legend);
        }
        else
        {
            grayLookupScale = new GrayPaintScale(lowColor, highColor);
            renderer.setPaintScale(grayLookupScale);
            scaleLegend = new PaintScaleLegend(grayLookupScale, legend);
        }
        scaleLegend.setPosition(RectangleEdge.RIGHT);
    }
    
    PaintScaleLegend getPaintScaleLegend()
    {
        return scaleLegend;
    }

    private static class ContourPlotDataset extends AbstractXYZDataset
    {

        float[][] data;
        double scaleFactorX;
        double scaleFactorY;
        double accuracy;

        SibsonInterpolator2 interp;

        double lowx, highx, lowy, highy, columnsize, rowsize;

        ContourPlotDataset(float[][] data, double accuracy)
        {

            this.data = data;
            
            this.accuracy = accuracy;

            lowx = minValue(data[0]);
            highx = maxValue(data[0]);
            lowy = minValue(data[1]);
            highy = maxValue(data[1]);
            columnsize = highy - lowy + (1 / accuracy);
            rowsize = highx - lowx + (1 / accuracy);

            lowx = Math.round(lowx * 10000d) / 10000d;
            highx = Math.round(highx * 10000d) / 10000d;
            lowy = Math.round(lowy * 10000d) / 10000d;
            highy = Math.round(highy * 10000d) / 10000d;
            columnsize = Math.round(columnsize * 10000d) / 10000d;

            rowsize = Math.round(rowsize * 10000d) / 10000d;

            this.interp = new SibsonInterpolator2(data[2], data[0], data[1]);

        }

        public int getSeriesCount()
        {
            return 1;
        }

        public Comparable getSeriesKey(int series)
        {
            return "series";
        }

        public int getItemCount(int series)
        {
            return (int) (columnsize * accuracy * rowsize * accuracy);
        }

        @Override
        public double getXValue(int series, int item)
        {
            return (item % (rowsize * accuracy) / accuracy) + lowx;
        }

        @Override
        public double getYValue(int series, int item)
        {
            return ((item / (int) (rowsize * accuracy)) / accuracy) + lowy;
        }

        @Override
        public double getZValue(int series, int item)
        {
            return interp.interpolate((float) getXValue(0, item), (float) getYValue(0, item));
        }

        public Number getX(int series, int item)
        {
            return getXValue(series, item);
        }

        public Number getY(int series, int item)
        {
            return getYValue(series, item);
        }

        public Number getZ(int series, int item)
        {
            return getZValue(series, item);
        }
    }

    /**
     *
     * @param vals array to find max of
     * @return the maximum value in the array
     */
    private static float maxValue(float[] vals)
    {
        float max = vals[0];
        for (int i = 0; i < vals.length; i++)
        {
            if (vals[i] > max)
            {
                max = vals[i];
            }
        }
        return max;
    }

    /**
     *
     * @param vals array to find min of 
     * @return the minimum value in the array
     */
    private static float minValue(float[] vals)
    {
        float min = vals[0];
        for (int i = 0; i < vals.length; i++)
        {
            if (vals[i] < min)
            {
                min = vals[i];
            }
        }
        return min;
    }
}
