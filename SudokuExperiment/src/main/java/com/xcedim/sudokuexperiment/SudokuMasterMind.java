package com.xcedim.sudokuexperiment;

import android.util.Log;

import java.util.Random;

/**
 * Created by xcedim on 11/17/2013.
 */
public class SudokuMasterMind {

    public static boolean CreateSudoku(int[] play, int level)
    {
        Random rnd = new Random();
        //rnd.setSeed(System.currentTimeMillis() % 1000);
        //Build a complete board
         while(true)
         {
             int count=0;
             for(int i=0;i<81;i++)
             {
                 if(play[i]!=0)
                     count++;
             }

             if(count >= 81)//level)
                 break;

             CreateSudoku(play, 81,0,count);

         }
        //Remove values until we respect the requested level
        int count=0;
        while(true)
        {
            int nextRemoveIdx = (Math.abs(rnd.nextInt(81)));

            if(play[nextRemoveIdx] != 0)
            {
                count++;
                play[nextRemoveIdx] = 0;
            }

            if(count >= (81-level))
                break;
        }

        return true;// (Math.abs(rnd.nextInt()) % 81));
    }

    public static boolean CreateSudoku(int[] play, int level, int position, int count)
    {
        Random rnd = new Random();
        //rnd.setSeed(System.currentTimeMillis()%1000);

        //We walked all the position with success
        if(level == 0x0 || position==81 || count == level)
        {
            return true;
        }

        int caspos = (Math.abs(rnd.nextInt(81)));
        //if not empty walk next
        if(caspos > level || play[position] != 0)
        {
            return CreateSudoku(play, level, position+1,count);//(Math.abs(rnd.nextInt()) % 81));
        }
        else
        {
            //Log.v("Sudoku", "CASPOS:" + caspos);
            //walk all possible values
            int value = (Math.abs(rnd.nextInt(9)));

            int[] values= new int[9];
            for(int i=0;i<9;i++)
                values[i]=0;
            boolean exit=false;

            //for(int i=0;i<9;i++)
            while(!exit)
            {
                values[value]++;
                if(values[value]==1)
                {



                    //if(Validate(play, position - ((position/9)*9), position/9))
                    if(isValid(play,position,value+1))
                    {
                        play[position]=value+1;
                        //int nextposition = (Math.abs(rnd.nextInt()) % 81);

                        //while(play[nextposition] != -1)
                        //{
                        //    nextposition = (Math.abs(rnd.nextInt()) % 81);
                        //}
                        count++;
                        if(CreateSudoku(play, level,position+1,count))//(Math.abs(rnd.nextInt()) % 81)))
                        {
                            return true;
                        }
                    }

                    exit=true;
                    for(int i=0;i<9;i++)
                    {
                        if(values[i]==0)
                            exit=false;
                    }
                }
                value = (Math.abs(rnd.nextInt()%9));
            }
        }
        play[position]=0;
        return false;
    }

    public static boolean solveSudokuRand(int[] play)
    {
        Random rnd = new Random();
        int[] originalPlay = play.clone();

        while(!Validate(play))
        {
            play = originalPlay.clone();
            for(int i=0;i<81;i++)
            {
               if(play[i] == 0)
               {
                    play[i] = rnd.nextInt(9)+1;
               }
            }
        }

        return true;
    }

    public static boolean solveSudoku(int[] play)
    {
        //Walk all position to solve remaining place
        return solveSudoku(play,0);
    }

    protected static boolean solveSudoku(int[] play,int position)
    {


        //find next position
        while(position<81 && play[position]!=0)
            position++;

        //We walked all the position with success
        if(position==9*9)
        {
            Log.v("Sudoku", "SUCCESS:" + position);
            return true;
        }
        //if not empty walk next
        /*if(play[position] != -1)
        {
            return solveSudoku(play,position+1);
        }
        else
        {*/
            //walk all possible values
            //int[] values = GetAvailableValues(play,position - ((position/9)*9), position/9);

            for(int i=0;i<9;i++)
            {
                //play[position]=i+1;//values[i];

                //if(Validate(play, position - ((position/9)*9), position/9))
                if(isValid(play,position,i+1))
                {
                    //Log.v("Sudoku", "POS:" + position);
                    play[position]=i+1;//values[i];
                    if(solveSudoku(play,position+1))
                    {
                        return true;
                    }
                    play[position]=0;
                }
            }
        //}

        return false;
    }

    /*public static boolean Validate(int[] play)
    {
        for(int x=0;x<9;x++)
        {
            for(int y=0;y<9;y++)
            {
                if(!ValidateFull(play, x, y))
                    return false;
            }
        }
        return true;
    }*/
    public static boolean isValid(int[] play, int position, int val)
    {
        int x = position - (position/9)*9;
        int y = (position/9);
        int zone = GetZone(x,y);

        return isValid(play,x,y,val);
    }

    public static boolean isValid(int[] play, int x, int y, int val)
    {
        int zone = GetZone(x,y);

        for(int i=0;i<9;i++)
        {
            if(play[GetIndex(x,i)] == val || play[GetIndex(i,y)] == val || GetZonePositionValue(play,zone,i) == val)
                return false;
        }
        return true;
    }

    public static boolean Validate(int[] play, int x, int y)
    {
        return ValidateRow(play, y) && ValidateColumn(play, x) && ValidateZone(play, GetZone(x,y));
    }

    public static boolean Validate(int[] play)
    {
        for(int i=0;i<9;i++)
        {
            int totalR = 0;
            int totalC = 0;
            int totalZ = 0;

            for(int j=0;j<9;j++)
            {
                totalR += play[GetIndex(i,j)];
                totalC += play[GetIndex(j,i)];
                totalZ += GetZonePositionValue(play,i,j);
            }

            if(totalC != 45 || totalR != 45 || totalZ != 45)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean ValidateRow(int[] play,int row)
    {
        return ValidateRow(play,row,false);
    }

    public static boolean ValidateRowFull(int[] play,int row)
    {
        int total = 0;
        for(int i=0;i<9;i++)
        {
            total += play[GetIndex(row,i)];
        }

        return (total == 45);
    }

    public static boolean ValidateRow(int[] play,int row,boolean full)
    {
        int[] val = new int[9];

        for(int i=0;i<9;i++)
        {
            if(play[(row*9)+i]!=0)
            {
                val[play[(row*9)+i]-1]++;
            }
        }

        for(int i=0;i<9;i++)
        {
            if(val[i]!=1 && !(!full && val[i]==0))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean ValidateColumn(int[] play,int column)
    {
        return ValidateColumn(play,column,false);
    }

    public static boolean ValidateColumn(int[] play,int column,boolean full)
    {
        int[] val=new int[9];

        for(int i=0;i<9;i++)
        {
            if(play[(i*9)+column]!=0)
            {
                val[play[(i*9)+column]-1]++;
            }
        }

        for(int i=0;i<9;i++)
        {
            if(val[i]!=1 && !(!full && val[i]==0))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean ValidateZone(int[] play,int zone)
    {
        return ValidateZone(play,zone,false);
    }

    public static boolean ValidateZone(int[] play,int zone,boolean full)
    {
        int[] val=new int[9];

        for(int i=0;i<9;i++)
        {
            if(GetZonePositionValue(play, zone, i)!=0)
            {
                val[GetZonePositionValue(play, zone, i)-1]++;
            }
        }

        for(int i=0;i<9;i++)
        {
            if(val[i]!=1 && !(!full && val[i]==0))
            {
                return false;
            }
        }

        return true;
    }

    public static void SetZonePositionValue(int[] play,int zone,int position,int value)
    {
        int zy = zone/3;
        int zx = zone - (zy*3);

        int zpy = (zy*3) + (position/3);
        int zpx = (zx*3) + position-((position/3)*3);

        int index = (zpy*9)+zpx;

        play[index]=value;
    }

    public static int GetZonePositionValue(int[] play,int zone,int position)
    {
        int zy = zone/3;
        int zx = zone - (zy*3);

        int zpy = (zy*3) + (position/3);
        int zpx = (zx*3) + position-((position/3)*3);

        int index = (zpy*9)+zpx;

        return play[index];
    }

    public static int GetZone(int x,int y)
    {
        int zx = x/3;
        int zy = y/3;
        return zx+(zy*3);
    }

    public static int GetZonePosition(int x,int y)
    {
        int zx = x/3;
        int zy = y/3;

        int zpx = x-(zx*3);
        int zpy = y-(zy*3);

        return zpx+(zpy*3);
    }

    public static int[] GetAvailableValues(int[] play, int x, int y)
    {
        //int[] rowValues = new int[9];
        //int[] columnValues = new int[9];
        //int[] zoneValues = new int[9];
        int[] values = new int[9];
        int zone = GetZone(x,y);

        int[] counts = new int[9];
        int count=0;

        for(int i=0;i<9;i++)
        {
            int rvalue = play[GetIndex(i,y)];
            int cvalue = play[GetIndex(x,i)];
            int zvalue = GetZonePositionValue(play,zone,i);

            if(rvalue > 0)
            {
                counts[rvalue-1]++;
            }
            if(cvalue > 0)
            {
                counts[cvalue-1]++;
            }
            if(zvalue > 0)
            {
                counts[zvalue-1]++;
            }
        }

        for(int i=0;i<9;i++)
        {
            if(counts[i]==0)
                count++;
        }

        int[] availables = new int[count];
        int index = 0;
        for(int i=0;i<9;i++)
        {
            if(counts[i]==0)
                availables[index++] = i+1;
        }

        return availables;
    }

    public static int[] GetAvailableValuesInRow(int[] play, int x, int y)
    {
        int[] counts = new int[9];
        int count=0;

        for(int i=0;i<9;i++)
        {
            int value = play[GetIndex(i,y)];
            if(value > 0)
            {
                counts[value]++;
                count++;
            }
        }

        int[] availables = new int[9-count];
        int index = 0;

        for(int i=0;i<9;i++)
        {
            if(counts[i]==0)
                availables[index++] = i+1;
        }

        return availables;
    }

    public static int[] GetAvailableValuesInColumn(int[] play, int x, int y)
    {
        int[] counts = new int[9];
        int count=0;

        for(int i=0;i<9;i++)
        {
            int value = play[GetIndex(x,i)];
            if(value > 0)
            {
                counts[value]++;
                count++;
            }
        }

        int[] availables = new int[9-count];
        int index = 0;

        for(int i=0;i<9;i++)
        {
            if(counts[i]==0)
                availables[index++] = i+1;
        }

        return availables;
    }

    public static int[] GetAvailableValuesInZone(int[] play, int x, int y)
    {
        int[] counts = new int[9];
        int count=0;
        int zone = GetZone(x,y);

        for(int i=0;i<9;i++)
        {
            int value = GetZonePositionValue(play,zone,i);
            if(value > 0)
            {
                counts[value]++;
                count++;
            }
        }

        int[] availables = new int[9-count];
        int index = 0;

        for(int i=0;i<9;i++)
        {
            if(counts[i]==0)
                availables[index++] = i+1;
        }

        return availables;
    }

    private static int GetIndex(int x, int y)
    {
        return (y*9)+x;
    }
}
