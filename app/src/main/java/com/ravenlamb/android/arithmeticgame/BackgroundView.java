package com.ravenlamb.android.arithmeticgame;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import com.ravenlamb.android.arithmeticgame.R;

/**
 * TODO: document your custom view class.
 */
public class BackgroundView extends View {

    Paint numberPaint;
    int[] colorArray;


    public BackgroundView(Context context) {
        super(context);
        init(null, 0);
    }

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BackgroundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        Resources res=getResources();

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


        numberPaint=new Paint();
        numberPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        numberPaint.setColor(Color.BLACK);
        numberPaint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        DisplayMetrics dm=getResources().getDisplayMetrics();
//        int screenW=dm.widthPixels;
//        int screenH=dm.heightPixels;
        int screenW=canvas.getWidth();
        int screenH=canvas.getHeight();
        int screenMin=(screenW>screenH)?screenH:screenW;



        for(int i=0;i<10;i++) {
            float size = (float) ((Math.random() * .1 + .3) * screenMin);
//            int x = (int) ((Math.random()*.6+0.1) * (double) screenW );
//            int y = (int) ((Math.random()*.1+0.8) * (double) screenH );
            int x = (int)(i*0.1*screenW);
            int y = (int)(0.8*screenH);
            float r = (float) Math.random() * 360;
            r=30;

            numberPaint.setTextSize(size);
            numberPaint.setColor(colorArray[i]);
            canvas.save();
//            canvas.rotate(r, screenW/2, screenH/2);
            canvas.rotate(r, 0, 0);
            int x1=(int)(x*Math.cos(r)+y*Math.sin(r));
            int y1=(int)(-x*Math.sin(r)+y*Math.cos(r));
//            canvas.drawText(String.valueOf(i),screenW/2,screenH/4*3,numberPaint);
            canvas.drawText(String.valueOf(i),x1,y1,numberPaint);
            canvas.restore();

        }


//
//        int x = 75;
//        int y = 185;
//        Paint paint=new Paint();
//        paint.setColor(Color.GRAY);
//        paint.setTextSize(25);
//        String rotatedtext = "Rotated helloandroid :)";
//
//        //Draw bounding rect before rotating text:
//
//        Rect rect = new Rect();
//        paint.getTextBounds(rotatedtext, 0, rotatedtext.length(), rect);
//        canvas.translate(x, y);
//        paint.setStyle(Paint.Style.FILL);
//
//        canvas.drawText(rotatedtext , 0, 0, paint);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(rect, paint);
//
//        canvas.translate(-x, -y);
//
//
//        paint.setColor(Color.RED);
//        canvas.rotate(-45, x + rect.exactCenterX(),y + rect.exactCenterY());
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawText(rotatedtext, x, y, paint);
//
//        for(int i=0;i<10;i++){
//            TranslateAnimation translateAnimation=new TranslateAnimation(0,0,-screenH,0);
//            translateAnimation.setDuration(800);
//            translateAnimation.setInterpolator(new OvershootInterpolator(.5f));
//            translateAnimation.setStartOffset((int) (Math.random() * 2500));
//            numTextViews[i].startAnimation(translateAnimation);
//
//        }
    }

}
