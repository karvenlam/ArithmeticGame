package com.ravenlamb.android.arithmeticgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * TODO: document your custom view class.
 * Todo need to make this abstract
 */
public abstract class BaseGridView extends View {
    public final static float STROKE_WIDTH=3f;

    protected int gridSize=6;

    protected int gridWidth;
    protected int gridHeight;

    protected OnGridViewInteraction onGridViewInteraction;
//    private ZenGameDriver zenDriver;//todo need to make this BaseGameDriver
    protected BaseGameDriver baseGameDriver;

    Paint numberPaint;
    int[] colorArray;
    Paint op1Paint;
    Paint op2Paint;
    Paint resultPaint;
    Paint currentPaint;

    public BaseGridView(Context context) {
        super(context);
        init(context);
    }

    public BaseGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){

        Resources res=getResources();

        onGridViewInteraction=(OnGridViewInteraction) context;

        numberPaint=new Paint();
        numberPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        numberPaint.setColor(Color.BLACK);
        numberPaint.setTextAlign(Paint.Align.CENTER);
//        numberPaint.setTypeface();

        colorArray=new int[10];
        colorArray[0]=res.getColor(R.color.num_color0);
        colorArray[1]=res.getColor(R.color.num_color1);
        colorArray[2]=res.getColor(R.color.num_color2);
        colorArray[3]=res.getColor(R.color.num_color3);
        colorArray[4]=res.getColor(R.color.num_color4);
        colorArray[5]=res.getColor(R.color.num_color5);
        colorArray[6]=res.getColor(R.color.num_color6);
        colorArray[7]=res.getColor(R.color.num_color7);
        colorArray[8]=res.getColor(R.color.num_color8);
        colorArray[9]=res.getColor(R.color.num_color9);

        op1Paint=new Paint();
        op1Paint.setColor(res.getColor(R.color.op1_color));
        op1Paint.setStyle(Paint.Style.STROKE);
        op1Paint.setStrokeWidth(STROKE_WIDTH);

        op2Paint=new Paint();
        op2Paint.setColor(res.getColor(R.color.op2_color));
        op2Paint.setStyle(Paint.Style.STROKE);
        op2Paint.setStrokeWidth(STROKE_WIDTH);

        resultPaint=new Paint();
        resultPaint.setColor(res.getColor(R.color.result_color));
        resultPaint.setStyle(Paint.Style.STROKE);
        resultPaint.setStrokeWidth(STROKE_WIDTH);

        currentPaint=new Paint();
        currentPaint.setColor(Color.YELLOW);//todo need to match  resultColor
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(STROKE_WIDTH);
        initDriver();

    }

    //todo initialize baseDriver
    public abstract void initDriver();


    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(Color.WHITE);
////
//        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        circlePaint.setColor(Color.BLUE);
//        circlePaint.setTextAlign(Paint.Align.CENTER);
//
//        int radius;
//        Configuration config = getResources().getConfiguration();
//        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            radius = canvas.getHeight() / 3;
//        } else {
//            radius = canvas.getWidth() / 3;
//        }
//
//        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,
//                radius, circlePaint);

//        cirlePaint.setAlpha();
//
//        Paint gridPaint=new Paint();


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
                int numToDraw=baseGameDriver.getCellNum(i, j);
                numberPaint.setColor(colorArray[numToDraw]);
                canvas.drawText(String.valueOf(numToDraw),x,y,numberPaint);
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
            if(baseGameDriver.endingCoord(x,y)){
//                onGridViewInteraction.onUpdate(baseGameDriver);
            }
            //need to update operand even if result is invalid, set resultTextView to ???
            onGridViewInteraction.onUpdate(baseGameDriver);
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


    public interface OnGridViewInteraction{
        public void onDebug(BaseGameDriver baseGameDriver);
        public void onUpdate(BaseGameDriver baseGameDriver);
    }
}
