package com.xcedim.sudokuexperiment;

import android.graphics.Canvas;

/**
 * Created by xcedim on 1/31/2014.
 */
public interface ICellAnimator {
    void Clock();
    void Draw(Canvas canvas);
    boolean isFinished();
    void SetLocation(int x, int y);
    void SetSize(int size);
    void SetGameCell(GameCell cell);
}
