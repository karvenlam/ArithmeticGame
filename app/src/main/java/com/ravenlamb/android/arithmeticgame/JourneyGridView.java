package com.ravenlamb.android.arithmeticgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by kl on 12/12/2014.
 * at least one number is greater than 10
 * for valid equation, animate operand cells and replace
 */
public class JourneyGridView extends BaseGridView {

    private int animateStage=0;
    Paint numberAnimatePaint;
    boolean[][] shouldAnimate;
    boolean gameOver=false;//todo

    public JourneyGridView(Context context) {
        super(context);
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

        baseGameDriver=new JourneyGameDriver(gridSize,gridSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        gridWidth=canvas.getWidth()/gridSize;
        gridHeight=canvas.getHeight()/gridSize;
        int circleRadius=(gridHeight>gridWidth)? gridWidth/2:gridHeight/2;
        numberPaint.setTextSize(gridHeight / 2);

        for(int i=0;i<gridSize;i++){
            for(int j=0;j<gridSize;j++){
                float x=i*gridWidth+gridWidth/2;
                float y=j*gridHeight+gridHeight/2 - ((numberPaint.descent() + numberPaint.ascent()) / 2);
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
                canvas.drawText(String.valueOf(baseGameDriver.getCellNum(i, j)),x,y,numberPaint);
            }
        }
        //canvas.drawText("CENTER",canvas.getWidth()/2,canvas.getHeight()/2,numberPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

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
            if(baseGameDriver.getCellNum(x,y)==0){
                onGridViewInteraction.onUpdate(baseGameDriver);
            }
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
                if(operator==BaseGameDriver.OP_ADDITION ||
                        operator==BaseGameDriver.OP_SUBTRACTION ||
                        operator==BaseGameDriver.OP_MULTIPLICATION ||
                        operator==BaseGameDriver.OP_DIVISION ){

                    //todo animate cell replacement and call JourneyGameDriver to replace number
                    //replace number
                    //value animate the alpha or textSize, Property Animation
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
}
