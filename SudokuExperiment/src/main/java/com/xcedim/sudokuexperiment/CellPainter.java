package com.xcedim.sudokuexperiment;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.LinkedList;

/**
 * Created by xcedim on 1/31/2014.
 */
public class CellPainter {

    private int _x;
    private int _y;
    private int _size;
    private GameCell _cell;
    private Rect _region;
    private ICellAnimator _defaultAnimator;
    private ICellAnimator _currentAnimator;
    private LinkedList<ICellAnimator> _animators;

    public CellPainter(int x, int y, int size, GameCell cell, ICellAnimator defaultAnimator)
    {
        _x = x;
        _y = y;
        _size = size;
        _cell = cell;
        _region = new Rect(_x,_y,_x+_size,_y+_size);
        _defaultAnimator = defaultAnimator;
        _defaultAnimator.SetLocation(_x,_y);
        _defaultAnimator.SetSize(_size);
        _defaultAnimator.SetGameCell(_cell);
        _currentAnimator = _defaultAnimator;
        _animators = new LinkedList<ICellAnimator>();
    }

    public boolean isInside(int x, int y)
    {
        return _region.contains(x,y);
    }

    public void Clock()
    {
        _currentAnimator.Clock();
        if(_currentAnimator.isFinished())
        {
            if(_animators.isEmpty())
            {
                _currentAnimator = _defaultAnimator;
            }
            else
            {
                _currentAnimator = _animators.removeLast();
            }
        }
    }

    public void Draw(Canvas canvas)
    {
        _currentAnimator.Draw(canvas);
    }

    public void AddAnimator(ICellAnimator animator)
    {
        animator.SetGameCell(_cell);
        animator.SetLocation(_x,_y);
        animator.SetSize(_size);
        if(_animators.isEmpty())
        {
            if(_currentAnimator == _defaultAnimator)
            {
                _currentAnimator = animator;
            }
            else
            {
                _animators.addFirst(animator);
            }
        }
        else
        {
            _animators.addFirst(animator);
        }
    }

    public void ClearAnimator()
    {
        _currentAnimator = _defaultAnimator;
        _animators.clear();
    }
}
