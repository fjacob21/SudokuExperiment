package com.xcedim.sudokuexperiment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by xcedim on 1/31/2014.
 */
public class BasicAnimator implements ICellAnimator {
    protected int _x;
    protected int _y;
    protected int _size;
    protected GameCell _cell;

    protected Paint _solidPaint;
    protected Paint _textPaint;

    public BasicAnimator(Color backColor, Color textColor, int textSize)
    {
        _solidPaint = new Paint();
        _solidPaint.setColor(backColor.Value());
        _textPaint = new Paint();
        _textPaint.setColor(textColor.Value());
        _textPaint.setTextSize(textSize);
        _x = _y = _size = 0;
    }

    @Override
    public void Clock() {

    }

    @Override
    public void Draw(Canvas canvas) {
        DrawUnderlay(canvas);
        canvas.drawRect(_x,_y,_x+_size,_y+_size,_solidPaint);
        DrawText(canvas);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void SetLocation(int x, int y) {
        _x = x;
        _y = y;
    }

    @Override
    public void SetSize(int size) {
        _size = size;
    }

    @Override
    public void SetGameCell(GameCell cell)
    {
        _cell = cell;
    }

    protected void DrawUnderlay(Canvas canvas)
    {
        if(_cell.Color() != -1)
        {
            _solidPaint.setAlpha(128);
            Paint underlayPaint = new Paint();
            switch(_cell.Color())
            {
                case 0:
                    underlayPaint.setColor(Color.Red.Value());
                    break;
                case 1:
                    underlayPaint.setColor(Color.Blue.Value());
                    break;
                case 2:
                    underlayPaint.setColor(Color.Green.Value());
                    break;
            }
            canvas.drawRect(_x,_y,_x+_size,_y+_size,underlayPaint);
        }
        else
        {
            _solidPaint.setAlpha(255);
        }


    }

    protected void DrawText(Canvas canvas)
    {
        if(_cell!= null && _cell.Number() != 0)
        {
            Rect textBound = new Rect();
            _textPaint.getTextBounds(Integer.toString(_cell.Number()),0,1,textBound);
            int textOffY = _size - (_size - textBound.height())/2;
            int textOffX = (_size - textBound.width())/2;
            canvas.drawText(Integer.toString(_cell.Number()), _x + textOffX, _y + textOffY, _textPaint);
        }
    }
}
