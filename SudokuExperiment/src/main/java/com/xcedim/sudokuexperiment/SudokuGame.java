package com.xcedim.sudokuexperiment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by xcedim on 11/17/2013.
 */
public class SudokuGame {

    private static final int INITIAL_FLAG = 0x8000000;
    private static final int SIZE = 9;
    private static final int TOTAL_SIZE = 81;

    public enum GameLevel
    {
        Easy(50),
        Medium(40),
        Hard(30);

        private final int _value;
        GameLevel(int value)
        {
            _value = value;
        }

        public final int Value(){return _value;}
    };

    private GameCell[] _game = new GameCell[TOTAL_SIZE];
    private int[] _gameValueTemp = new int[TOTAL_SIZE];
    private int _color = -1;
    private boolean _choiceMode = false;

    public SudokuGame()
    {

        SudokuMasterMind.CreateSudoku(_gameValueTemp,GameLevel.Easy.Value());
        for(int i=0;i<TOTAL_SIZE;i++)
        {
            if(_gameValueTemp[i] != 0)
            {
                _game[i] = new GameCell(true);
                _game[i].Number(_gameValueTemp[i]);
            }
            else
            {
                _game[i] = new GameCell(false);
            }
        }
    }

    public SudokuGame(GameLevel level)
    {
        SudokuMasterMind.CreateSudoku(_gameValueTemp, level.Value());
        for(int i=0;i<TOTAL_SIZE;i++)
        {
            if(_gameValueTemp[i] != 0)
            {
                _game[i] = new GameCell(true);
                _game[i].Number(_gameValueTemp[i]);
            }
            else
            {
                _game[i] = new GameCell(false);
            }
        }
    }

    public SudokuGame(int[] initialData)
    {
        _gameValueTemp = initialData.clone();
        for(int i=0;i<TOTAL_SIZE;i++)
        {
            if(_gameValueTemp[i] != 0)
            {
                _game[i] = new GameCell(true);
                _game[i].Number(_gameValueTemp[i]);
            }
            else
            {
                _game[i] = new GameCell(false);
            }
        }
    }

    public GameCell Get(int zone, int zx, int zy)
    {
        int x = ((zone % 3)*3)+zx;
        int y = ((zone /3)*3)+zy;
        return Get(y*SIZE+x);
    }

    public GameCell Get(int x, int y)
    {
        return Get(y*SIZE+x);
    }

    public GameCell Get(int index)
    {
        return _game[index];
    }

    public boolean Set(int zone, int zx, int zy, int value)
    {
        int x = ((zone % 3)*3)+zx;
        int y = ((zone /3)*3)+zy;
        return Set(y*SIZE+x,value);
    }

    public boolean Set(int x, int y, int value)
    {
        return Set(y*SIZE+x,value);
    }

    public boolean Set(int index, int value)
    {
        if(!_game[index].IsInitial())
        {
            if(SudokuMasterMind.isValid(GetRawData(), index,value) || value == 0)
            {
                if(!_choiceMode)
                {
                    _game[index].Color(_color);
                    _game[index].Number(value);
                }
                else
                {
                    _game[index].Choice(value,true);
                }
                return true;
            }
        }
        return false;
    }

    public boolean IsInitial(int zone, int zx, int zy)
    {
        int x = ((zone % 3)*3)+zx;
        int y = ((zone /3)*3)+zy;
        return IsInitial(y*SIZE+x);
    }

    public boolean IsInitial(int x, int y)
    {
        return IsInitial(y*SIZE+x);
    }

    public boolean IsInitial(int index)
    {
        return _game[index].IsInitial();
    }

    public boolean Validate()
    {
        return SudokuMasterMind.Validate(GetValidData());
    }

    public int Color()
    {
        return _color;
    }

    public void Color(int color)
    {
        _color = color;
        if(_color == -1)
        {
            for(int i=0;i<TOTAL_SIZE;i++)
            {
                _game[i].Color(-1);
            }
        }
        else
        {
            _choiceMode = false;
            for(int i=0;i<TOTAL_SIZE;i++)
            {
                _game[i].ResetChoice();
            }
        }
    }

    public boolean ChoiceMode()
    {
        return _choiceMode;
    }

    public void ChoiceMode(boolean choiceMode)
    {
        _choiceMode = choiceMode;
        _color = -1;
    }

    public int[] GetRawData()
    {
        for(int i=0;i<TOTAL_SIZE;i++)
        {
            _gameValueTemp[i] = _game[i].Number();
        }
        return _gameValueTemp;
    }

    public int[] GetValidData()
    {
        for(int i=0;i<TOTAL_SIZE;i++)
        {
            if(_game[i].Color() != -1 &&  !_game[i].isChoiced())
            {
                _gameValueTemp[i] = _game[i].Number();
            }
            else
            {
                _gameValueTemp[i] = 0;
            }
        }
        return _gameValueTemp;
    }

    public byte[] Serialize()
    {
        byte[] rest=null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            //oos.reset();
            for(int i=0;i<TOTAL_SIZE;i++)
            {
                oos.writeInt(_game[i].Number());
                //oos.flush();
            }
            oos.flush();
            rest = baos.toByteArray();


            baos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return rest;
    }

    public void Deserialize(byte[] serializedData)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);

        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            //_game = new int[TOTAL_SIZE];

            for(int i=0;i<TOTAL_SIZE;i++)
            {
                _game[i].Number(ois.readInt());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SudokuGame Create()
    {
        return new SudokuGame();
    }
}
