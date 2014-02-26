package com.xcedim.sudokuexperiment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xcedim.sudokuexperiment.R;

/**
 * Created by xcedim on 11/15/2013.
 */
public class level_selection extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_selection);
    }

    public void StartGameClick(View view){
        if(view == (View) findViewById(R.id.btEasy))
        {
            SudokuShelf.Singleton().Create(SudokuGame.GameLevel.Easy);
        }
        else if(view == (View) findViewById(R.id.btNormal))
        {
            SudokuShelf.Singleton().Create(SudokuGame.GameLevel.Medium);
        }
        else if(view == (View) findViewById(R.id.btExpert))
        {
            SudokuShelf.Singleton().Create(SudokuGame.GameLevel.Hard);
        }
        Intent intent = new Intent(this, game_display_gx.class);
        startActivity(intent);
    }
}