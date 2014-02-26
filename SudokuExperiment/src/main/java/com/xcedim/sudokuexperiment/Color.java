package com.xcedim.sudokuexperiment;

import android.content.Context;

/**
 * Created by xcedim on 2/7/2014.
 */
public class Color {
    protected double _alpha;
    protected double _red;
    protected double _green;
    protected double _blue;


    public Color()
    {
        _alpha = 255;
        _red = 0;
        _green = 0;
        _blue = 0;
    }

    public Color(double red, double green, double blue)
    {
        _alpha = 255;
        _red = red;
        _green = green;
        _blue = blue;
    }

    public Color(double alpha, double red, double green, double blue)
    {
        _alpha = alpha;
        _red = red;
        _green = green;
        _blue = blue;
    }

    public Color(int color)
    {
        _alpha = (color & 0xFF000000) >> 24;
        _red = (color   & 0x00FF0000) >> 16;
        _green = (color & 0x0000FF00) >> 8;
        _blue = (color  & 0x000000FF);
    }

    public double Alpha()
    {
        return _alpha;
    }

    public double Red()
    {
        return _red;
    }

    public double Green()
    {
        return _green;
    }

    public double Blue()
    {
        return _blue;
    }

    public int Value()
    {
        return ((int)_alpha<<24) + ((int)_red<<16) + ((int)_green<<8) + ((int)_blue);
    }

    public double Size()
    {
        return Math.sqrt(Math.pow(_red,2) + Math.pow(_green,2) + Math.pow(_blue,2));
    }

    public Color Substract(Color c2)
    {
        Color newColor = new Color(_alpha-c2._alpha,_red - c2._red,_green-c2._green,_blue-c2._blue);
        return newColor;
    }

    public Color Add(Color c2)
    {
        Color newColor = new Color(_alpha+c2._alpha,_red + c2._red,_green+c2._green,_blue+c2._blue);
        return newColor;
    }

    public Color Divide(Color c2)
    {
        Color newColor = new Color(_alpha/c2._alpha,_red / c2._red,_green/c2._green,_blue/c2._blue);
        return newColor;
    }

    public Color Divide(double dividance)
    {
        Color newColor = new Color(_alpha/dividance,_red / dividance,_green/dividance,_blue/dividance);
        return newColor;
    }

    public Color Multiple(double value)
    {
        Color newColor = new Color(_alpha*value,_red * value,_green*value,_blue*value);
        return newColor;
    }

    public static Color Red = new Color(255,255,0,0);
    public static Color Green = new Color(255,0,255,0);
    public static Color Blue = new Color(255,0,0,255);
}
