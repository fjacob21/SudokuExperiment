package com.xcedim.sudokuexperiment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;

import com.xcedim.sudokuexperiment.R;

/**
 * Created by xcedim on 11/16/2013.
 */
public class GameDisplay extends Activity {

    //private int[] play = new int[81];
    private EditText[] cells = new EditText[81];

    public void Solve(View v)
    {
        int[] play = SudokuShelf.Singleton().CurrentGame().GetRawData();
        if(SudokuMasterMind.solveSudoku(play))
        {
            for(int i=0;i<81;i++)
                cells[i].setText(Integer.toString(play[i]));
        }
        else
        {
            Log.v("Sudoku", "POO SUCCESS:");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_display);


        GridLayout grid = (GridLayout) findViewById(R.id.GameGrid);
        //SurfaceView view = (SurfaceView)findViewById(R.id.surfaceView);
        //Canvas cv = new Canvas();
        //    cv.drawLine(0,0,10,10, new Paint());
        //view.draw(cv);

        /*for(int i=0;i<81;i++)
        {
            play[i]=-1;
        }*/
        //SudokuMasterMind.CreateSudoku(play,17);

        for (int i=0; i < 9; i++)
        {
            for(int j=0;j<9;j++)
            {
                EditText cell = new EditText(this);
                cell.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(1);
                cell.setFilters(filterArray);
                cell.setId(j * 9 + i);
                cells[j*9+i]=cell;
                cell.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(!b)
                        {
                            EditText cell = (EditText)view;
                            Editable text = cell.getText();
                            if(!text.toString().isEmpty())
                            {
                                String t = text.toString();
                                int id = cell.getId();
                                int value = Integer.valueOf(t);
                                SudokuShelf.Singleton().CurrentGame().Set(id, value);
                                /*boolean valid = SudokuMasterMind.Validate(play,id-(id/9)*9,(id/9));
                                if(!valid)
                                {
                                    SudokuShelf.Singleton().CurrentGame().Set(cell.getId(), 0);
                                    cell.setText("");
                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(300);
                                }*/
                            }
                        }
                    }
                });


                if(SudokuShelf.Singleton().CurrentGame().Get((j*9)+i).Number() != 0)
                    cell.setText(Integer.toString(SudokuShelf.Singleton().CurrentGame().Get((j*9)+i).Number()));



                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                // left, right, left, right - creates new default rows
                lp.columnSpec = GridLayout.spec(i);
                lp.rowSpec  = GridLayout.spec(j);

                // this would put every view in the same row => all Views are on the very top
                // lp.rowSpec = GridLayout.spec(0);

                cell.setLayoutParams(lp);

                grid.addView(cell);
            }
    }

    }
}