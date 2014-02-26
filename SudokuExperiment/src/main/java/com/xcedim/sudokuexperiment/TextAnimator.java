package com.xcedim.sudokuexperiment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by xcedim on 2/21/2014.
 */
public class TextAnimator extends BasicAnimator {
    private String _text = "";

    public TextAnimator(Color startColor, Color textColor, String text, int textSize)
    {
        super(startColor,textColor,textSize);
        _text = text;
    }

    @Override
    public void Draw(Canvas canvas) {
        canvas.drawRect(_x,_y,_x+_size,_y+_size,_solidPaint);
        if(_text != null)
        {
            Rect textBound = new Rect();
            _textPaint.getTextBounds(_text,0,1,textBound);
            int w =(int)_textPaint.measureText(_text);
            int textOffY = _size - (_size - textBound.height())/2;
            int textOffX = (_size - w)/2;
            canvas.drawText(_text, _x + textOffX, _y + textOffY, _textPaint);
        }
    }
}
