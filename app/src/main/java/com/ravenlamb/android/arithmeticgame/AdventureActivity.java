package com.ravenlamb.android.arithmeticgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This is the activity class for adventure mode of Arithmetic game
 * Each time an equation is correct, the selected cells are replaced,
 * Base on time, see strings.xml adventure_help
 *
 * @author Karven Lam
 * @version 1.0 2014-12-14
 */
public class AdventureActivity extends ActionBarActivity
        implements BaseGridView.OnGridViewInteraction {

    public static final String TAG=AdventureActivity.class.getName();
    public static final String ADVENTURE_PREFERENCES ="AdventurePreferences";
    public static final String HIGH_SCORE ="highScore";
    public static final String HIGH_COUNT="highCount";
    public static final String HIGH_CHAIN ="highChain";
    public static final String HIGH_LARGEST ="highLargest";
    public static final float INITIAL_TIME=30;
    public static final int animationRepeat=4;

    AdventureGridView adventureGridView;

    TextView op1TextView;
    TextView op2TextView;
    TextView operatorTextView;
    TextView resultTextView;

    TextView timeTextView;
    TextView scoreTextView;
//    TextView countTextView;
    TextView chainTextView;
//    TextView largestTextView;

    double score=0;
    int count=0;
    int chain=0;
    int largest=0;
    float time =INITIAL_TIME;
    float timeFactor =.2f;

    SharedPreferences adventurePreferences;

    double adventureHighScore=0;
    int adventureHighCount=0;
    int adventureHighChain=0;
    int adventureHighLargest=0;

    boolean alreadyHighScore=false;
    boolean alreadyHighCount=false;

    boolean gameStarted=false;


    final Handler mHandler=new Handler();

    final Runnable mUpdateTimeTextView = new Runnable() {
        public void run() {
            updateTimeTextView();
        }
    };

    public void updateTimeTextView(){

//        timeTextView.setText(String.valueOf((int) Math.floor(time)));
        timeTextView.setText(String.valueOf(time));
        if(time <0){
            adventureGridView.setGameOver();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure);
        scoreTextView = (TextView) findViewById(R.id.score_textview);
//        countTextView = (TextView) findViewById(R.id.count_textview);
        chainTextView = (TextView) findViewById(R.id.chain_textview);
//        largestTextView =(TextView) findViewById(R.id.largest_textview);
        timeTextView = (TextView) findViewById(R.id.time_textview);
        timeTextView.setText(String.valueOf((int) Math.floor(time)));

        op1TextView = (TextView) findViewById(R.id.op1_textview);
        op2TextView= (TextView) findViewById(R.id.op2_textview);
        operatorTextView= (TextView) findViewById(R.id.operator_textview);
        resultTextView= (TextView) findViewById(R.id.result_textview);

        adventurePreferences=getSharedPreferences(ADVENTURE_PREFERENCES,0);
        adventureHighScore=adventurePreferences.getFloat(HIGH_SCORE, 0);
        adventureHighCount=adventurePreferences.getInt(HIGH_COUNT,0);
        adventureHighChain=adventurePreferences.getInt(HIGH_CHAIN,0);
        adventureHighLargest=adventurePreferences.getInt(HIGH_LARGEST,0);


        DisplayMetrics dm=getResources().getDisplayMetrics();
        int screenW=dm.widthPixels;
        int screenH=dm.heightPixels;
        int gridViewWidth=screenW-Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin))*2;
        if(gridViewWidth*2>screenH){
            gridViewWidth=screenH/2;
        }
        adventureGridView =(AdventureGridView)findViewById(R.id.adventure_grid_view);
        ViewGroup.LayoutParams layoutParams= adventureGridView.getLayoutParams();
        layoutParams.width=gridViewWidth;
        layoutParams.height=gridViewWidth;
        adventureGridView.setLayoutParams(layoutParams);
        newGame();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.adventure_rule).setTitle("Adventure");
        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }


    public void newGame(){
        score=0;
        count=0;
        chain=0;
        largest=0;
        time =INITIAL_TIME;

        alreadyHighCount=false;
        alreadyHighScore=false;
        scoreTextView.setTypeface(null, Typeface.NORMAL);
        scoreTextView.setTextColor(getResources().getColor(R.color.default_text));
//        countTextView.setTypeface(null, Typeface.NORMAL);
        chainTextView.setTypeface(null, Typeface.NORMAL);
//        largestTextView.setTypeface(null, Typeface.NORMAL);

        op1TextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        op2TextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        resultTextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        operatorTextView.setText(BaseGameDriver.UNKNOWN_OPERATOR);
        scoreTextView.setText(String.valueOf((int)score));
//        countTextView.setText(String.valueOf(count));
        chainTextView.setText(String.valueOf(chain));
//        largestTextView.setText(String.valueOf(largest));

        adventureGridView.initDriver();//todo
        adventureGridView.invalidate();

        gameStarted=false;

//
//        timeTextView.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while(time>0){
//
//                        Thread.sleep(1000);
//                        time=time-1f;
//                        timeTextView.setText(String.valueOf((int) Math.floor(time)));
//                    }
//
//                } catch (InterruptedException e) {
//                    Log.d(TAG,"Inside post Runnable catch");
//                }
//            }
//        });
    }

    public void onRestart(View view){
        newGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_new_game) {
            newGame();
            return true;
        }
        if (id == R.id.action_help) {
            //todo show help dialog
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.adventure_help).setTitle("Adventure Help");
            AlertDialog dialog=builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(BaseGameDriver baseGameDriver) {

        if(!gameStarted){
            gameStarted=true;
            new Thread(){
                public void run(){

                    try {
                        while(time>0){

                            Thread.sleep(1000);
                            time=time-1f;
                            mHandler.post(mUpdateTimeTextView);
        //                            timeTextView.setText(String.valueOf((int) Math.floor(time)));
                        }

                    } catch (InterruptedException e) {
                        Log.d(TAG,"Inside post Runnable catch");
                    }
                }

            }.start();
        }

        AdventureGameDriver adventureGameDriver=(AdventureGameDriver)baseGameDriver;
        //todo shift operand animation
        //if result is not valid before, and not valid now, don't need shift animation
        boolean doShiftAnimation=true;
        String resultStrBefore=resultTextView.getText().toString();
        String resultStrAfter=adventureGameDriver.getResultNumber();
        if(resultStrBefore.equals(BaseGameDriver.UNKNOWN_VALUE) &&
                resultStrAfter.equals(BaseGameDriver.UNKNOWN_VALUE)){
            doShiftAnimation=false;
        }

//        int operator=baseGameDriver.computeStatus();
        int operator=baseGameDriver.getCurrStatus();
        op1TextView.setText(adventureGameDriver.getOp1Number());
        op2TextView.setText(adventureGameDriver.getOp2Number());
        if(operator==BaseGameDriver.OP_NEGATIVE_SUBTRACTION){
            resultTextView.setText("-"+ resultStrAfter);
        }else {
            resultTextView.setText(resultStrAfter);
        }
        //todo add animation and sound effect
        //todo add high score, chains, largest number
        if(BaseGameDriver.isValidOperator(operator)){
            Log.d(TAG, "ZenActivity onUpdate");
            operatorTextView.setText(adventureGameDriver.getOperator());


            chain++;
            chainTextView.setText(String.valueOf(chain));
            if(chain>adventureHighChain){
                SharedPreferences.Editor edit=adventurePreferences.edit();
                adventureHighChain=chain;
                edit.putInt(HIGH_CHAIN,chain);
                edit.commit();
                //todo new high chain animation
                chainTextView.setTypeface(null, Typeface.BOLD);
//                ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
//                scaleAnimation.setDuration(500);
//                scaleAnimation.setRepeatCount(animationRepeat);
//                scaleAnimation.setInterpolator(new CycleInterpolator(.5f));
//                chainTextView.startAnimation(scaleAnimation);
            }else{
                chainTextView.setTypeface(null, Typeface.NORMAL);
            }


            //todo need to compare to preference high score and change scoreTextView
            double thisScore=adventureGameDriver.getScore()*chain;
            score+=thisScore;
            time = (float) (time +adventureGameDriver.getLogScore()*chain* timeFactor);
            time = (time >INITIAL_TIME)?INITIAL_TIME: time;
            scoreTextView.setText(String.valueOf((int)Math.floor(score)));
//        scoreTextView.setText(String.valueOf(score));
            if(score>adventureHighScore){
                SharedPreferences.Editor edit=adventurePreferences.edit();
                adventureHighScore=score;
                edit.putFloat(HIGH_SCORE,(float)score);
                edit.commit();

                //todo new high score animation, scale score textview
                if(!alreadyHighScore) {
                    alreadyHighScore = true;
                    scoreTextView.setTypeface(null, Typeface.BOLD);
                    scoreTextView.setTextColor(getResources().getColor(R.color.default_new_record));
//                    ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
//                    scaleAnimation.setDuration(500);
//                    scaleAnimation.setRepeatCount(animationRepeat);
//                    scaleAnimation.setInterpolator(new CycleInterpolator(.5f));
//                    scoreTextView.startAnimation(scaleAnimation);
                }
            }

            count++;
//            countTextView.setText(String.valueOf(count));
            if(count>adventureHighCount){
                SharedPreferences.Editor edit=adventurePreferences.edit();
                adventureHighCount=count;
                edit.putInt(HIGH_COUNT, count);
                edit.commit();

                //todo new high count animation
                if(!alreadyHighCount){
                    alreadyHighCount=true;
//                    countTextView.setTypeface(null, Typeface.BOLD);
//                    ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
//                    scaleAnimation.setDuration(500);
//                    scaleAnimation.setRepeatCount(animationRepeat);
//                    scaleAnimation.setInterpolator(new CycleInterpolator(.5f));
//                    countTextView.startAnimation(scaleAnimation);
                }
            }

            int temp=adventureGameDriver.getLargest();
            if(temp>largest){
                largest=temp;
//                largestTextView.setText(String.valueOf(largest));
                if(largest>adventureHighLargest){
                    SharedPreferences.Editor edit=adventurePreferences.edit();
                    adventureHighLargest=largest;
                    edit.putInt(HIGH_LARGEST,largest);
                    edit.commit();
                    //todo new high largest animation
//                    largestTextView.setTypeface(null, Typeface.BOLD);
//                    ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
//                    scaleAnimation.setDuration(500);
//                    scaleAnimation.setRepeatCount(animationRepeat);
//                    scaleAnimation.setInterpolator(new CycleInterpolator(.5f));
//                    largestTextView.startAnimation(scaleAnimation);
                }else{
//                    largestTextView.setTypeface(null, Typeface.NORMAL);
                }
            }

        }else if(operator==BaseGameDriver.OP_INVALID){
            operatorTextView.setText(BaseGameDriver.OPERATORS[BaseGameDriver.OP_INVALID ]);
            chain=0;
        }
//        timeTextView.setText(String.valueOf((int) Math.floor(time)));
        timeTextView.setText(String.valueOf(time));
    }

    @Override
    public void onDebug(BaseGameDriver baseGameDriver) {

    }
}
