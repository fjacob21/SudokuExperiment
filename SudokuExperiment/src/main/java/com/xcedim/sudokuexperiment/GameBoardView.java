package com.xcedim.sudokuexperiment;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by xcedim on 11/30/2013.
 */
public class GameBoardView extends SurfaceView implements SurfaceHolder.Callback{
    class GameThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private Paint mBackPaint;
        private Paint mSelectBackPaint;
        private Paint mSuccessBackPaint;
        private Paint mIndirectSelectBackPaint;
        private Paint mLinePaint;
        private Paint mLargeLinePaint;
        private Paint mInitialtextPaint;
        private Paint mTextPaint;
        private Paint mSuccessTextPaint;
        //private Paint mChoicePaint;
        private Drawable mUndoLogo;
        private Drawable mRedoLogo;

        /** Message handler used by thread to interact with TextView */
        //private Handler mHandler;

        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = true;

        private final Object mRunLock = new Object();

        private int mGridSize;
        private int mCellSize;
        private int mItemSize;
        private int mLargeLineSize;
        private int mLineSize;
        private int mTextSize;
        private int mSuccessTextSize;

        private int mGridX;
        private int mGridY;

        private int mNumberX;
        private int mNumberY;

        private int mChoiceX;
        private int mChoiceY;

        private int mOrientation;

        private long mLastTime;

        private boolean mSuccess = false;

        //private int _color = -1;
        //private boolean _choiceMode = false;

        private int _selectedNumber=-1;
        private int _selectedCell=-1;

        private ArrayList<CellPainter> mGameCells;
        private ArrayList<CellPainter> mNumberCells;
        private ArrayList<CellPainter> mChoiceCells;

        public GameThread(SurfaceHolder surfaceHolder, Context context,
                           Handler handler) {

            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            //mHandler = handler;
            mContext = context;

        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        long now = System.currentTimeMillis();

                        // Do nothing if mLastTime is in the future.
                        // This allows the game-start to delay the start of the physics
                        // by 100ms or whatever.
                        if (mLastTime > now) return;

                        double elapsed = (now - mLastTime);
                        if(elapsed >= 50)
                        {
                            AnimationClock();
                            mLastTime = now;
                        }

                        if (mRun) doPaint(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        private void doInit()
        {
            //Calculate the grid dimension relative to the screen size
            //border:90% line:2% zoneline:4%
            Canvas c = mSurfaceHolder.lockCanvas(null);

            mOrientation = mContext.getResources().getConfiguration().orientation;
            double width = c.getWidth();
            double height = c.getHeight();
            double shortSide = width;
            if(mOrientation== Configuration.ORIENTATION_LANDSCAPE)
            {
                shortSide = height;
            }

            mGridSize = (int)(shortSide * 0.98);
            mLineSize = (int)(shortSide * 0.005);
            mLargeLineSize = (int)(shortSide * 0.01);
            mCellSize = (mGridSize - (4 * mLargeLineSize) - (6*mLineSize)) / 9;
            //Correct gridsize
            mGridSize = (4 * mLargeLineSize) + (6*mLineSize) + (9*mCellSize);

            mItemSize = (mGridSize / 9);
            mTextSize = (int)(mCellSize * 0.9);
            mSuccessTextSize = (int)(mCellSize * 1.2);

            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                mGridX = (int)(width - mGridSize - (mItemSize*4))/2;
                mGridY = (int)(height - mGridSize)/2;
                mNumberX = mGridX  + mGridSize + 10;
                mNumberY = mGridY;
                mChoiceX = mNumberX + mItemSize + 10;
                mChoiceY = mNumberY;
            }
            else
            {
                mGridX = (int)(width - mGridSize)/2;
                mGridY = (int)(height - mGridSize - (mItemSize*4))/2;
                mNumberX = mGridX;
                mNumberY = mGridY + mGridSize + 10;
                mChoiceX = mNumberX;
                mChoiceY = mNumberY + mItemSize + 10;
            }

            mBackPaint = new Paint();
            mBackPaint.setARGB(255,200,200,200);

            mSelectBackPaint = new Paint();
            mSelectBackPaint.setARGB(255,255,10,10);

            mSuccessBackPaint = new Paint();
            mSuccessBackPaint.setARGB(200,0,0,0);

            mIndirectSelectBackPaint = new Paint();
            mIndirectSelectBackPaint.setARGB(255,230,160,160);

            mLinePaint = new Paint();
            mLinePaint.setARGB(255,0,0,0);
            mLinePaint.setStrokeWidth(mLineSize);

            mLargeLinePaint = new Paint();
            mLargeLinePaint.setARGB(255,0,0,0);
            mLargeLinePaint.setStrokeWidth(mLargeLineSize);

            mInitialtextPaint = new Paint();
            mInitialtextPaint.setARGB(255,0,0,255);
            mInitialtextPaint.setTextSize(mTextSize);

            mTextPaint = new Paint();
            mTextPaint.setARGB(255,0,0,0);
            mTextPaint.setTextSize(mTextSize);

            mSuccessTextPaint = new Paint();
            mSuccessTextPaint.setARGB(255,255,0,0);
            mSuccessTextPaint.setTextSize(mSuccessTextSize);

            mGameCells = new ArrayList<CellPainter>();
            mNumberCells = new ArrayList<CellPainter>();
            mChoiceCells = new ArrayList<CellPainter>();

            CalculateGameCellLocation(c.getWidth(),c.getHeight());
            CalculateNumberSelectLocation(c.getWidth(),c.getHeight());
            CalculateChoicesLocation(c.getWidth(), c.getHeight());

            mSurfaceHolder.unlockCanvasAndPost(c);

            Resources res = mContext.getResources();
            // cache handles to our key sprites & other drawables
            mUndoLogo = mContext.getResources().getDrawable(R.drawable.undo2);
            mRedoLogo = mContext.getResources().getDrawable(R.drawable.redo2);

        }

        private void CalculateGameCellLocation(int cw, int ch)
        {
            int offX;
            int offY = mGridY;

            for(int i=0;i<9;i++)
            {
                offX = mGridX;
                if(i%3==0)
                {
                    offY += mLargeLineSize;
                }
                else
                {
                    offY += mLineSize;
                }

                for(int j=0;j<9;j++)
                {
                    if(j%3==0)
                    {
                        offX += mLargeLineSize;
                    }
                    else
                    {
                        offX += mLineSize;
                    }

                    CellPainter newCell;
                    if(SudokuShelf.Singleton().CurrentGame().Get(j,i).IsInitial())
                    {
                        newCell = new CellPainter(offX,offY,mCellSize,SudokuShelf.Singleton().CurrentGame().Get(j,i),new BasicAnimator(new Color(255,200,200,200),new Color(255,0,0,255),mTextSize));
                    }
                    else
                    {
                        newCell = new CellPainter(offX,offY,mCellSize,SudokuShelf.Singleton().CurrentGame().Get(j,i),new BasicAnimator(new Color(255,200,200,200),new Color(255,0,0,0),mTextSize));
                    }

                    mGameCells.add(newCell);
                    offX += mCellSize;
                }
                offY += mCellSize;
            }
        }

        private void CalculateNumberSelectLocation(int cw, int ch)
        {
            int offX = mNumberX;
            int offY = mNumberY;
            int size = mItemSize - (2*mLineSize);

            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                offX += mLineSize;
                offY += (mGridSize - (9*size) - (8*mLineSize)) /2;
            }
            else
            {
                offX += (mGridSize - (9*size) - (8*mLineSize)) /2;
                offY += mLineSize;
            }

            for(int i=0;i<9;i++)
            {

                GameCell cell = new GameCell(false);
                cell.Number(i+1);
                CellPainter newCell = new CellPainter(offX,offY,size,cell,new BasicAnimator(new Color(0xFFC8C8C8),new Color(255,0,0,255),mTextSize));

                mNumberCells.add(newCell);
                if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
                    offY += size + mLineSize;
                else
                    offX += size + mLineSize;
            }
        }

        private void CalculateChoicesLocation(int cw, int ch)
        {
            int offX = mChoiceX;
            int offY = mChoiceY;
            int size = mItemSize - (2*mLineSize);

            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                offX += mLineSize;
                offY += (mGridSize - (9*size) - (8*mLineSize)) /2;
            }
            else
            {
                offX += (mGridSize - (9*size) - (8*mLineSize)) /2;
                offY += mLineSize;
            }

            //Add the Red button
            GameCell cell = new GameCell(false);
            CellPainter newCell = new CellPainter(offX,offY,size,cell,new BasicAnimator(new Color(255,255,0,0),new Color(255,0,0,255),mTextSize));

            mChoiceCells.add(newCell);
            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
                offY += size + mLineSize;
            else
                offX += size + mLineSize;

            //Add the Blue button
            cell = new GameCell(false);
            newCell = new CellPainter(offX,offY,size,cell,new BasicAnimator(new Color(255,0,0,255),new Color(255,0,0,255),mTextSize));

            mChoiceCells.add(newCell);
            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
                offY += size + mLineSize;
            else
                offX += size + mLineSize;

            //Add the green button
            cell = new GameCell(false);
            newCell = new CellPainter(offX,offY,size,cell,new BasicAnimator(new Color(255,0,255,0),new Color(255,0,0,255),mTextSize));

            mChoiceCells.add(newCell);
            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
                offY += size + mLineSize;
            else
                offX += size + mLineSize;

            //Add the choices button
            cell = new GameCell(false);
            newCell = new CellPainter(offX,offY,size,cell,new TextAnimator(new Color(255,200,200,200),new Color(255,0,0,255),"123",mTextSize/3));

            mChoiceCells.add(newCell);
            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
                offY += size + mLineSize;
            else
                offX += size + mLineSize;

        } 
        private void doPaint(Canvas canvas)
        {
            int offX = mGridX;
            int offY = mGridY;
            int size = mItemSize*3;

            canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),mBackPaint);

            //Draw calculated cells
            canvas.drawRect(offX,offY,offX+mGridSize,offY+mGridSize,mLinePaint);
            for(int i=0;i<mGameCells.size();i++)
            {
                CellPainter cell = mGameCells.get(i);
                cell.Draw(canvas);
            }

            //Draw calculated number
            offX = mNumberX;
            offY = mNumberY;
            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                canvas.drawRect(offX,offY,offX+mItemSize,offY+mGridSize,mLinePaint);
            }
            else
            {
                canvas.drawRect(offX,offY,offX+mGridSize,offY+mItemSize,mLinePaint);
            }

            for(int i=0;i<mNumberCells.size();i++)
            {
                CellPainter cell = mNumberCells.get(i);
                cell.Draw(canvas);
            }

            //Draw calculated choice
            offX = mChoiceX;
            offY = mChoiceY;
            if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                canvas.drawRect(offX,offY,offX+mItemSize,offY+mGridSize,mLinePaint);
            }
            else
            {
                canvas.drawRect(offX,offY,offX+mGridSize,offY+mItemSize,mLinePaint);
            }

            for(int i=0;i<mChoiceCells.size();i++)
            {
                CellPainter cell = mChoiceCells.get(i);
                cell.Draw(canvas);
            }

            /*
            mUndoLogo.setBounds(offX,offY,offX + 64,offY + 64);
            mUndoLogo.draw(canvas);

            offX = canvas.getWidth() - mUndoLogo.getMinimumWidth();
            mRedoLogo.setBounds(offX,offY,offX + mUndoLogo.getMinimumWidth(),offY + mUndoLogo.getMinimumHeight());
            mRedoLogo.draw(canvas);*/

            if(mSuccess)
            {
                canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),mSuccessBackPaint);
                String suc = "!!!! Perfect !!!!";
                Rect textBound = new Rect();
                mSuccessTextPaint.getTextBounds(suc,0,1,textBound);
                int ch = canvas.getHeight();
                int th = textBound.height();
                int cw = canvas.getWidth();
                int tw = (int)mSuccessTextPaint.measureText(suc);

                if(mOrientation == Configuration.ORIENTATION_LANDSCAPE)
                {

                    offX = (cw-tw)/2;
                    offY = (ch-th)/2;
                }
                else
                {
                    offX = (cw-tw)/2;
                    offY += mItemSize + 20;
                }
                int textOffY =  textBound.height();
                canvas.drawText(suc, offX, offY + textOffY, mSuccessTextPaint);
            }
        }

        private void AnimationClock()
        {
            for(int i=0;i<mGameCells.size();i++)
            {
                CellPainter cell = mGameCells.get(i);
                cell.Clock();
            }

            for(int i=0;i<mNumberCells.size();i++)
            {
                CellPainter cell = mNumberCells.get(i);
                cell.Clock();
            }
        }

        private void SetLineSelectAnimation(int index, boolean clear)
        {
            int line = index / 9;
            for(int i=0;i<9;i++)
            {
                if(!clear)
                {
                    mGameCells.get((line * 9)+i).AddAnimator(new BasicAnimator(new Color(255,230,160,160),new Color(255,0,0,255),mTextSize));
                }
                else
                {
                    mGameCells.get((line * 9)+i).ClearAnimator();
                }
            }
        }

        private void SetColumnSelectAnimation(int index, boolean clear)
        {
            int column = index % 9;
            for(int i=0;i<9;i++)
            {
                if(!clear)
                {
                    mGameCells.get((i * 9)+column).AddAnimator(new BasicAnimator(new Color(255,230,160,160),new Color(255,0,0,255),mTextSize));
                }
                else
                {
                    mGameCells.get((i * 9)+column).ClearAnimator();
                }
            }
        }

        private void SetZoneSelectAnimation(int index, boolean clear)
        {
            int x = index % 9;
            int y = index / 9;
            int zx = x/3;
            int zy = y/3;
            int zone = zx+(zy*3);
            int sx = zx*3;
            int sy = zy*3;

            for(int i=0;i<9;i++)
            {
                int dx = i % 3;
                int dy = i / 3;
                if(!clear)
                {
                    mGameCells.get(((sy+dy)*9)+sx+dx).AddAnimator(new BasicAnimator(new Color(255, 230, 160, 160), new Color(255, 0, 0, 255), mTextSize));
                }
                else
                {
                    mGameCells.get(((sy+dy)*9)+sx+dx).ClearAnimator();
                }
            }
        }

        private boolean SetColor(int color)
        {
            if(color != SudokuShelf.Singleton().CurrentGame().Color())
            {
                SudokuShelf.Singleton().CurrentGame().Color(color);
                return true;
            }
            else
            {
                SudokuShelf.Singleton().CurrentGame().Color(-1);
                return false;
            }
        }

        private boolean SetNumber(int index, int value)
        {
            boolean result = SudokuShelf.Singleton().CurrentGame().Set(index,value);
            mSuccess = SudokuShelf.Singleton().CurrentGame().Validate();
            return result;
        }

        private boolean SetChoiceMode()
        {
            SudokuShelf.Singleton().CurrentGame().ChoiceMode(!SudokuShelf.Singleton().CurrentGame().ChoiceMode());
            SudokuShelf.Singleton().CurrentGame().Color(-1);
            return SudokuShelf.Singleton().CurrentGame().ChoiceMode();
        }

        private void ClearCellAnims()
        {
            for(int i=0;i<81;i++)
            {
                mGameCells.get(i).ClearAnimator();
            }
        }

        private void SetTouchNumber(int newNumber)
        {
            if(_selectedNumber >= 0)
            {
                //Remove past selection
                mNumberCells.get(_selectedNumber).ClearAnimator();
            }

            //Set the new selection
            _selectedNumber = newNumber;
            if(_selectedNumber >= 0)
            {
                mNumberCells.get(_selectedNumber).AddAnimator(new BasicAnimator(new Color(255,200,0,0),new Color(255,0,0,255),mTextSize));

                if(_selectedCell != -1)
                {
                    if(!SudokuShelf.Singleton().CurrentGame().Get(_selectedCell).IsInitial() && SudokuShelf.Singleton().CurrentGame().Get(_selectedCell).Number()==_selectedNumber+1)
                    {
                        //Select the same number, we remove the selection
                        SetNumber(_selectedCell, 0);
                        mNumberCells.get(_selectedNumber).ClearAnimator();
                        _selectedNumber = -1;
                    }
                    else
                    {
                        //Set the new value
                        if(!SetNumber(_selectedCell, _selectedNumber + 1))
                        {
                            //There is an error so we want to deselect the bad one
                            mNumberCells.get(_selectedNumber).ClearAnimator();
                            mNumberCells.get(_selectedNumber).AddAnimator(new FadingAnimator(new Color(255,200,0,0),new Color(255,200,200,200),10,new Color(255,0,0,255),mTextSize));
                            _selectedNumber = SudokuShelf.Singleton().CurrentGame().Get(_selectedCell).Number()-1;
                            if(_selectedNumber>=0)
                            {
                                mNumberCells.get(_selectedNumber).ClearAnimator();
                                mNumberCells.get(_selectedNumber).AddAnimator(new FadingAnimator(new Color(255,200,200,200),new Color(255,200,0,0),10,new Color(255,0,0,255),mTextSize));
                                mNumberCells.get(_selectedNumber).AddAnimator(new BasicAnimator(new Color(255,200,0,0),new Color(255,0,0,255),mTextSize));
                            }
                        }
                    }
                }
                else
                {
                    //No cell selected, we highlight all the same number
                    ClearCellAnims();
                    SudokuGame game = SudokuShelf.Singleton().CurrentGame();
                    for(int i=0;i<81;i++)
                    {
                        if(game.Get(i).Number() == _selectedNumber+1)
                        {
                            mGameCells.get(i).AddAnimator(new FadingAnimator(new Color(255, 255, 0, 0), new Color(255, 200, 200, 200), 100, new Color(255, 0, 0, 255), mTextSize));
                        }
                    }
                }
            }
        }

        private void SetTouchCell(int newCell)
        {
            int oldCell = _selectedCell;

            ClearCellAnims();

            SetSelectedCell(newCell);

            if(oldCell == newCell)
            {
                SetSelectedCell(-1);
                SetSelectedNumber(-1);
            }
            else
            {
                if(SudokuShelf.Singleton().CurrentGame().Get(_selectedCell).Number() == 0 && _selectedNumber != -1)
                {
                    if(!SetNumber(_selectedCell, _selectedNumber + 1))
                    {
                        SetSelectedNumber(-1);
                    }
                }

                if(SudokuShelf.Singleton().CurrentGame().Get(_selectedCell).Number() != 0)
                {
                    SetSelectedNumber(SudokuShelf.Singleton().CurrentGame().Get(_selectedCell).Number() - 1);
                }
            }
        }

        private void SetTouchColor(int color)
        {
            for(int i=0;i<mChoiceCells.size();i++)
            {
                mChoiceCells.get(i).ClearAnimator();
            }

            switch(color)
            {
                case 0:
                    if(SetColor(color))
                        mChoiceCells.get(color).AddAnimator(new BasicAnimator(new Color(255,200,0,0),new Color(255,0,0,255),mTextSize));
                    else
                        mChoiceCells.get(color).ClearAnimator();
                    break;
                case 1:
                    if(SetColor(color))
                        mChoiceCells.get(color).AddAnimator(new BasicAnimator(new Color(255,0,0,200),new Color(255,0,0,255),mTextSize));
                    else
                        mChoiceCells.get(color).ClearAnimator();
                    break;
                case 2:
                    if(SetColor(color))
                        mChoiceCells.get(color).AddAnimator(new BasicAnimator(new Color(255,0,200,0),new Color(255,0,0,255),mTextSize));
                    else
                        mChoiceCells.get(color).ClearAnimator();
                    break;
                case 3:
                    if(SetChoiceMode())
                        mChoiceCells.get(color).AddAnimator(new TextAnimator(new Color(255,0,200,0),new Color(255,0,0,255),"123",mTextSize/3));
                    else
                        mChoiceCells.get(color).ClearAnimator();
                    break;
            }

        }

        private void SetSelectedCell(int newCell)
        {
            if(_selectedCell >= 0)
            {
                //Remove past selection
                SetLineSelectAnimation(_selectedCell,true);
                SetColumnSelectAnimation(_selectedCell,true);
                SetZoneSelectAnimation(_selectedCell,true);
                mGameCells.get(_selectedCell).ClearAnimator();

            }

            _selectedCell = newCell;
            if(_selectedCell >= 0)
            {
                SetLineSelectAnimation(_selectedCell,false);
                SetColumnSelectAnimation(_selectedCell,false);
                SetZoneSelectAnimation(_selectedCell,false);
                mGameCells.get(_selectedCell).ClearAnimator();
                mGameCells.get(_selectedCell).AddAnimator(new BasicAnimator(new Color(255,200,0,0),new Color(255,0,0,255),mTextSize));
            }
        }

        private void SetSelectedNumber(int newNumber)
        {
            if(_selectedNumber >= 0)
            {
                //Remove past selection
                mNumberCells.get(_selectedNumber).ClearAnimator();
            }

            //Set the new selection
            _selectedNumber = newNumber;
            if(_selectedNumber >= 0)
            {
                mNumberCells.get(_selectedNumber).AddAnimator(new BasicAnimator(new Color(255,200,0,0),new Color(255,0,0,255),mTextSize));
            }
        }
          
        public void TouchEvent(int x, int y)
        {
            for(int i=0;i<mGameCells.size();i++)
            {
                if(mGameCells.get(i).isInside(x,y))
                {
                    SetTouchCell(i);
                }
            }

            for(int i=0;i<mNumberCells.size();i++)
            {
                if(mNumberCells.get(i).isInside(x,y))
                {
                    SetTouchNumber(i);
                }
            }

            for(int i=0;i<mChoiceCells.size();i++)
            {
                if(mChoiceCells.get(i).isInside(x,y))
                {
                    SetTouchColor(i);
                }
            }
        }
        public void setRunning(boolean b) {
            // Do not allow mRun to be modified while any canvas operations
            // are potentially in-flight. See doDraw().
            synchronized (mRunLock) {
                mRun = b;
            }
        }
    }


    /** The thread that actually draws the animation */
    private GameThread thread;
    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new GameThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                //mStatusText.setVisibility(m.getData().getInt("viz"));
                //mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events


    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            int x = (int)event.getX();
            int y = (int)event.getY();
            thread.TouchEvent(x, y);
            return true;
        }
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //thread.setRunning(true);
        thread.doInit();
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

}
