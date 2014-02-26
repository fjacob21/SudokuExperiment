package com.xcedim.sudokuexperiment;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.graphics.Canvas;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by xcedim on 11/17/2013.
 */
public class SudokuShelf {//extends BackupAgentHelper {
    private Context _context=null;
    private SudokuGame _game = null;

    public SudokuShelf(Context context)
    {
        _context = context;
        Restore();
    }

    public SudokuGame CurrentGame()
    {
        if(_game == null)
        {
           //Try loading from storage
            _game = Restore();
            if(_game == null)
            {
                _game = new SudokuGame();
            }
        }
        return _game;
    }

    public void Create(SudokuGame.GameLevel level)
    {
        _game = new SudokuGame(level);
    }

    public void Store()
    {
        Store(_game);
    }

    public void Store(SudokuGame game)
    {
        try
        {
            FileOutputStream fos = _context.openFileOutput("CurrentGame", Context.MODE_PRIVATE);
            fos.write(_game.Serialize());

            fos.close();
        }
        catch(Exception e)
        {
            _game = new SudokuGame();
        }
    }

    public SudokuGame Restore()
    {
        try
        {
            FileInputStream fis = _context.openFileInput("CurrentGame");
            byte[] data = new byte[333];
            fis.read(data);
            _game = new SudokuGame();
            _game.Deserialize(data);

            fis.close();
        }
        catch(Exception e)
        {

        }
        return _game;
    }

    /*@Override
    public void onCreate() {
        _singleton = this;
        FileBackupHelper helper = new FileBackupHelper(this, HIGH_SCORES_FILENAME);
        addHelper(FILES_BACKUP_KEY, helper);
    }*/

    ///Singleton implementation////////////////////////////////////
    private static SudokuShelf _singleton = null;

    public static SudokuShelf Create(Context context)
    {
        _singleton = new SudokuShelf(context);
        return Singleton();
    }

    public static SudokuShelf Singleton()
    {
        return _singleton;
    }
}
