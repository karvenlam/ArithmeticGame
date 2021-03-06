package com.ravenlamb.android.arithmeticgame;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kl on 12/12/2014.
 * at least one number is greater than 10
 * for valid equation, animate operand cells and replace
 */
public class JourneyGridView extends BaseGridView {

    public static final String TAG=JourneyGridView.class.getName();

    Paint numberAnimatePaint;
    boolean[][] shouldAnimate;
    float animateTextSize;
    float numberTextSize;
    boolean gameOver=false;
    boolean gameOverDialogShown=false;
    String gameOverMessage="JOURNEY ENDS";

    public JourneyGridView(Context context) {
        super(context);
    }

    public JourneyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public JourneyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initDriver() {
        shouldAnimate=new boolean[gridSize][gridSize];
        for(int i=0;i<gridSize;i++){
            for(int j=0;j<gridSize;j++){
                shouldAnimate[i][j]=false;
            }
        }
        numberAnimatePaint=new Paint();
        numberAnimatePaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        numberAnimatePaint.setColor(Color.BLACK);
        numberAnimatePaint.setTextAlign(Paint.Align.CENTER);
        animateTextSize=numberPaint.getTextSize();
        numberTextSize=animateTextSize;

        baseGameDriver=new JourneyGameDriver(gridSize,gridSize);

        gameOver=false;
        gameOverDialogShown=false;
        JourneyGridView.this.setEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        gridWidth=canvas.getWidth()/gridSize;
        gridHeight=canvas.getHeight()/gridSize;
        int circleRadius=(gridHeight>gridWidth)? gridWidth/2:gridHeight/2;
        numberTextSize=gridHeight/2;
        numberPaint.setTextSize(gridHeight / 2);

        for(int i=0;i<gridSize;i++){
            for(int j=0;j<gridSize;j++){
                float x=i*gridWidth+gridWidth/2;
                float circleY=j*gridHeight+gridHeight/2;
                switch (baseGameDriver.getCellStatus(i,j)){
                    case BaseGameDriver.CELL_OPERAND1:
                        canvas.drawCircle(x,circleY,circleRadius,op1Paint);
                        break;
                    case BaseGameDriver.CELL_OPERAND2:
                        canvas.drawCircle(x,circleY,circleRadius,op2Paint);
                        break;
                    case BaseGameDriver.CELL_RESULT:
                        canvas.drawCircle(x,circleY,circleRadius,resultPaint);
                        break;
                    case BaseGameDriver.CELL_CURR_COORD:
                        canvas.drawCircle(x,circleY,circleRadius,currentPaint);
                        break;
                }

                //todo draw number in different colors, change font
                //todo if shouldAnimate, use the numberAnimatePaint
                int numToDraw=baseGameDriver.getCellNum(i, j);
                if(shouldAnimate[i][j]){
                    numberAnimatePaint.setTextSize(animateTextSize);
                    numberAnimatePaint.setColor(colorArray[numToDraw]);
                    float numY=j*gridHeight+gridHeight/2 - ((numberAnimatePaint.descent() + numberAnimatePaint.ascent()) / 2);
                    canvas.drawText(String.valueOf(numToDraw),x,numY,numberAnimatePaint);
                }else{
                    float numY=j*gridHeight+gridHeight/2 - ((numberPaint.descent() + numberPaint.ascent()) / 2);
                    numberPaint.setColor(colorArray[numToDraw]);
                    canvas.drawText(String.valueOf(numToDraw),x,numY,numberPaint);
                }

            }
        }
//        Log.w(TAG,"onDraw");
        if(animateTextSize<numberTextSize){
            invalidate();
        }
        if(gameOver){
            numberPaint.setColor(Color.BLACK);
            canvas.drawText(gameOverMessage,canvas.getWidth()/2,canvas.getHeight()/2,numberPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(gameOver){
            if(!gameOverDialogShown) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) onGridViewInteraction);
                builder.setTitle(gameOverMessage);
                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gameOverDialogShown=false;
                    }
                });
                builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onGridViewInteraction.onNewGame(baseGameDriver);
                    }
                });
                builder.setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onGridViewInteraction.onGameQuit();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                gameOverDialogShown=true;
            }
            return true;
        }
        // get the event type and the ID of the pointer that caused the event
        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer (i.e., finger)
        //todo
        int x=Math.round(event.getX(actionIndex)/gridWidth-0.5f);
        int y=Math.round(event.getY(actionIndex)/gridHeight-0.5f);
        if(x>=gridSize){
            x=gridSize-1;
        }
        if(x<0){
            x=0;
        }
        if(y>=gridSize){
            y=gridSize-1;
        }
        if(y<0){
            y=0;
        }

        // determine whether touch started, ended or is moving
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN)
        {
//            Toast.makeText(this.getContext(), "onTouch down: "+x+","+y, Toast.LENGTH_LONG).show();
            baseGameDriver.startingCoord(x, y);
//            if(baseGameDriver.getCellNum(x,y)==0){
//                onGridViewInteraction.onUpdate(baseGameDriver);
//            }
//            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
//                    event.getPointerId(actionIndex));
        }
        else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP)
        {
//            Toast.makeText(this.getContext(), "onTouch up: "+x+","+y, Toast.LENGTH_LONG).show();
//            this.setEnabled(false);//todo might not need this
            if(baseGameDriver.endingCoord(x,y)){

                int operator=baseGameDriver.getCurrStatus();
                if(BaseGameDriver.isValidOperator(operator)){

                    //todo animate cell replacement and call JourneyGameDriver to replace number
                    //replace number
                    //value animate the alpha or textSize, Property Animation
                    JourneyGameDriver journeyGameDriver = (JourneyGameDriver) baseGameDriver;
                    BaseGameDriver.Coord[] coords= journeyGameDriver.replaceOperandCells();
                    for(int i=0;i<gridSize;i++){
                        for(int j=0;j<gridSize;j++){
                            shouldAnimate[i][j]=false;
                        }
                    }
                    for (BaseGameDriver.Coord coord : coords) {
                        shouldAnimate[coord.x][coord.y] = true;
                    }
//                    ObjectAnimator objectAnimator=new ObjectAnimator();
//                    objectAnimator.setInterpolator(new OvershootInterpolator());
//                    objectAnimator.setObjectValues(this);
//                    objectAnimator.setPropertyName("animateTextSize");
//                    objectAnimator.setFloatValues(numberTextSize/2,numberTextSize);
//                    objectAnimator.setDuration(500).start();
                    ObjectAnimator.ofFloat(JourneyGridView.this, "animateTextSize",numberTextSize/2,numberTextSize).setDuration(500).start();

                }
            }
            onGridViewInteraction.onUpdate(baseGameDriver);//need to update operand even if equation invalid
//            touchEnded(event.getPointerId(actionIndex));
        }
        else
        {
//            Toast.makeText(this.getContext(), "onTouch move: "+x+","+y, Toast.LENGTH_LONG).show();
            baseGameDriver.hoveringCoord(x, y);
//            touchMoved(event);
        }

        invalidate(); // redraw
        return  true;
    }

    //for ObjectAnimator
    public void setAnimateTextSize(float ts){
//        Log.w(TAG,"setAnimateTextSize: "+ts+","+animateTextSize);
        animateTextSize=ts;
    }

    public void setGameOver(){
        gameOver=true;
        invalidate();
    }


    //FOR CHAIN MODE
    public void setChainGameOver(){
        gameOver=true;
        gameOverMessage="CHAIN BROKEN";
        invalidate();
    }

    //FOR CHAIN MODE
    public void shuffleChainGrid(){
        //todo shuffle chain grid
        baseGameDriver.initGrid();
        for(int i=0;i<gridSize;i++){
            for(int j=0;j<gridSize;j++){
                shouldAnimate[i][j]=false;
            }
        }
        ObjectAnimator.ofFloat(JourneyGridView.this, "animateTextSize",numberTextSize/2,numberTextSize).setDuration(500).start();
        invalidate();
    }

}
