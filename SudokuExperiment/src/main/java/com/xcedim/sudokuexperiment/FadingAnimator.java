package com.xcedim.sudokuexperiment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by xcedim on 2/1/2014.
 */
public class FadingAnimator extends BasicAnimator {
    private Color _startColor;
    private Color _endColor;
    private Color _currentColor;
    private int _span;
    private Color _dColor;
    private Paint _overPaint;

    private int _animCounter;


    public FadingAnimator(Color startColor, Color endColor, int span, Color textColor, int textSize)
    {
        super(startColor,textColor,textSize);
        _startColor = startColor;
        _currentColor = _startColor;
        _endColor = endColor;
        _span = span;

        _dColor = _endColor.Substract(_startColor);
        double size = _dColor.Size();
        double sizeMultipe = (size / _span);
        _dColor = _dColor.Divide(size).Multiple(sizeMultipe);

        _overPaint = new Paint();
        _overPaint.setColor(_currentColor.Value());

        _animCounter = 0;
    }

    @Override
    public void Clock()
    {
        if(_animCounter < _span)
        {
            _currentColor = _currentColor.Add(_dColor);
            _overPaint.setColor(_currentColor.Value());
            _animCounter++;
        }
    }

    @Override
    public void Draw(Canvas canvas) {
        //canvas.drawRect(_x,_y,_x+_size,_y+_size,_solidPaint);
        canvas.drawRect(_x,_y,_x+_size,_y+_size,_overPaint);
        double radius = ((double)(_size/2)/_span);
        //canvas.drawCircle(_x+_size/2,_y+_size/2,(_size/2) - (int)(radius*_animCounter),_overPaint);
        if(_cell != null && _cell.Number()!=0)
        {
            Rect textBound = new Rect();
            _textPaint.getTextBounds(Integer.toString(_cell.Number()),0,1,textBound);
            int textOffY = _size - (_size - textBound.height())/2;
            int textOffX = (_size - textBound.width())/2;
            canvas.drawText(Integer.toString(_cell.Number()), _x + textOffX, _y + textOffY, _textPaint);
        }
    }

    @Override
    public boolean isFinished() {
        return (_animCounter == _span);
    }
}
