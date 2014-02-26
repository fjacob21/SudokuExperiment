package com.xcedim.sudokuexperiment;

/**
 * Created by xcedim on 2/15/2014.
 */
public class GameCell {
    private boolean _isInitial;
    private int _number;
    private boolean[] _choiceNumbers = new boolean[9];
    private int _color;

    public GameCell(boolean isInitial)
    {
        _isInitial = isInitial;
        _number = 0;
        ResetChoice();
        _color = -1;
    }

    public boolean IsInitial()
    {
        return _isInitial;
    }

    public int Number()
    {
        return _number;
    }

    public void Number(int value)
    {
        _number = value;
        ResetChoice();
        //_color = -1;
    }

    public int Color()
    {
        return _color;
    }

    public void Color(int value)
    {
        _color = value;
        //_number = 0;
        ResetChoice();
    }

    public void Choice(int choice, boolean value)
    {
        _choiceNumbers[choice] = value;
        _number = 0;
        _color = -1;
    }

    public boolean Choice(int choice)
    {
        return _choiceNumbers[choice];
    }

    public boolean isChoiced()
    {
        boolean choiced = false;
        for(int i=0;i<9;i++)
        {
            if(_choiceNumbers[i])choiced = true;
        }

        return choiced;
    }

    public void ResetChoice()
    {
        for(int i=0;i<9;i++)
        {
            _choiceNumbers[i] = false;
        }
    }
}
