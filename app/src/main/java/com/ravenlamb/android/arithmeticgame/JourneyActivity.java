package com.ravenlamb.android.arithmeticgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DecimalFormat;

/**
 * This is the activity class for Journey mode of Arithmetic game
 * Each time an equation is correct, the selected cells are replaced,
 * Base on time, see strings.xml journey_help
 *
 * @author Karven Lam
 * @version 1.0 2014-12-14
 */
public class JourneyActivity extends ActionBarActivity
        implements BaseGridView.OnGridViewInteraction {

    public static final String TAG=JourneyActivity.class.getName();
    public static final String JOURNEY_PREFERENCES="JourneyPreferences";
    public static final String HIGH_SCORE ="highScore";
    public static final String HIGH_COUNT="highCount";
    public static final String HIGH_CHAIN ="highChain";
    public static final String HIGH_LARGEST ="highLargest";
    public static final float INITIAL_MOVES=10;
    public static final int animationRepeat=4;
    public static final DecimalFormat df=new DecimalFormat("0.00");


    // constants and variables for managing sounds
    private static final int SUCCESS_SOUND_ID = 0;
    private static final int FAILURE_SOUND_ID = 1;
    private SoundPool soundPool; // plays sound effects
    private SparseIntArray soundMap; // maps IDs to SoundPool

    AdView mAdView;

    JourneyGridView journeyGridView;

    TextView op1TextView;
    TextView op2TextView;
    TextView operatorTextView;
    TextView resultTextView;

    TextView moveTextView;
    TextView scoreTextView;
//    TextView countTextView;
    TextView chainTextView;
//    TextView largestTextView;

    double score=0;
    int count=0;
    int chain=0;
    int largest=0;
    float moves=INITIAL_MOVES;
    float movesFactor=.1f;

    SharedPreferences journeyPreferences;

    double journeyHighScore=0;
    int journeyHighCount=0;
    int journeyHighChain=0;
    int journeyHighLargest=0;

    boolean alreadyHighScore=false;
    boolean alreadyHighCount=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
        scoreTextView = (TextView) findViewById(R.id.score_textview);
//        countTextView = (TextView) findViewById(R.id.count_textview);
        chainTextView = (TextView) findViewById(R.id.chain_textview);
//        largestTextView =(TextView) findViewById(R.id.largest_textview);
        moveTextView = (TextView) findViewById(R.id.moves_textview);
        moveTextView.setText(df.format(moves));

        op1TextView = (TextView) findViewById(R.id.op1_textview);
        op2TextView= (TextView) findViewById(R.id.op2_textview);
        operatorTextView= (TextView) findViewById(R.id.operator_textview);
        resultTextView= (TextView) findViewById(R.id.result_textview);

        DisplayMetrics dm=getResources().getDisplayMetrics();
        int screenW=dm.widthPixels;
        int screenH=dm.heightPixels;
        int gridViewWidth=screenW-Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin))*2;
        if(gridViewWidth*2>screenH){
            gridViewWidth=screenH/2;
        }
        journeyGridView =(JourneyGridView)findViewById(R.id.journey_grid_view);
        ViewGroup.LayoutParams layoutParams= journeyGridView.getLayoutParams();
        layoutParams.width=gridViewWidth;
        layoutParams.height=gridViewWidth;
        journeyGridView.setLayoutParams(layoutParams);

//todo
        // initialize SoundPool to play the app's three sound effects
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

//        AudioAttributes.Builder ab=new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
//        ab.setUsage(AudioAttributes.USAGE_GAME);
//
//        SoundPool.Builder sb=new SoundPool.Builder();
//        sb.setMaxStreams(1);
//        sb.setAudioAttributes(ab.build());
//        soundPool=sb.build();

        // create Map of sounds and pre-load sounds
        soundMap = new SparseIntArray(3); // create new HashMap
        soundMap.put(SUCCESS_SOUND_ID,
                soundPool.load(this, R.raw.success, 1));
        soundMap.put(FAILURE_SOUND_ID,
                soundPool.load(this, R.raw.failure, 1));

        newGame();

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.journey_rule).setTitle("Journey");
        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    public void newGame(){
        score=0;
        count=0;
        chain=0;
        largest=0;
        moves=INITIAL_MOVES;

        journeyPreferences=getSharedPreferences(JOURNEY_PREFERENCES,0);
        journeyHighScore=Double.parseDouble(journeyPreferences.getString(HIGH_SCORE, "0"));
        journeyHighCount=journeyPreferences.getInt(HIGH_COUNT,0);
        journeyHighChain=journeyPreferences.getInt(HIGH_CHAIN,0);
        journeyHighLargest=journeyPreferences.getInt(HIGH_LARGEST,0);

        alreadyHighCount=false;
        alreadyHighScore=false;
        scoreTextView.setTypeface(null, Typeface.NORMAL);
        scoreTextView.setTextColor(getResources().getColor(R.color.default_text));
//        countTextView.setTypeface(null, Typeface.NORMAL);
        chainTextView.setTypeface(null, Typeface.NORMAL);
//        largestTextView.setTypeface(null, Typeface.NORMAL);
        moveTextView.setText(df.format(moves));

        op1TextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        op2TextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        resultTextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        operatorTextView.setText(BaseGameDriver.UNKNOWN_OPERATOR);
        scoreTextView.setText(String.valueOf((int)score));
//        countTextView.setText(String.valueOf(count));
        chainTextView.setText(String.valueOf(chain));
//        largestTextView.setText(String.valueOf(largest));

        journeyGridView.initDriver();//todo
        journeyGridView.invalidate();

    }

    public void onRestart(View view){
        newGame();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdView.destroy();
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
        if(id == R.id.action_quit){
            this.finish();
        }
        if (id == R.id.action_rules) {
            //todo show help dialog
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.journey_rule).setTitle("Journey Rules");
            builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
            return true;
        }
        if (id == R.id.action_help) {
            //todo show help dialog
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.journey_help).setTitle("Journey Help");
            builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(BaseGameDriver baseGameDriver) {
        JourneyGameDriver journeyGameDriver=(JourneyGameDriver)baseGameDriver;
        moves=moves-1;
        //todo shift operand animation
        //if result is not valid before, and not valid now, don't need shift animation
        boolean doShiftAnimation=true;
        String resultStrBefore=resultTextView.getText().toString();
        String resultStrAfter=journeyGameDriver.getResultNumber();
        if(resultStrBefore.equals(BaseGameDriver.UNKNOWN_VALUE) &&
                resultStrAfter.equals(BaseGameDriver.UNKNOWN_VALUE)){
            doShiftAnimation=false;
        }

//        int operator=baseGameDriver.computeStatus();
        int operator=baseGameDriver.getCurrStatus();
        op1TextView.setText(journeyGameDriver.getOp1Number());
        op2TextView.setText(journeyGameDriver.getOp2Number());
        if(operator==BaseGameDriver.OP_NEGATIVE_SUBTRACTION){
            resultTextView.setText("-"+ resultStrAfter);
        }else {
            resultTextView.setText(resultStrAfter);
        }
        //todo add animation and sound effect
        //todo add high score, chains, largest number
        if(BaseGameDriver.isValidOperator(operator)){
//            Log.d(TAG, "JourneyActivity onUpdate");
            operatorTextView.setText(journeyGameDriver.getOperator());


            chain++;
            chainTextView.setText(String.valueOf(chain));
            if(chain>journeyHighChain){
                SharedPreferences.Editor edit=journeyPreferences.edit();
                journeyHighChain=chain;
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
            double thisScore=journeyGameDriver.getScore()*chain;
            score+=thisScore;
            moves= (float) (moves+journeyGameDriver.getLogScore()*chain*movesFactor);
            moves= (moves>INITIAL_MOVES)?INITIAL_MOVES:moves;
            scoreTextView.setText(String.valueOf((int)Math.floor(score)));
//        scoreTextView.setText(String.valueOf(score));
            if(score>journeyHighScore){
                SharedPreferences.Editor edit=journeyPreferences.edit();
                journeyHighScore=score;
                edit.putString(HIGH_SCORE, String.valueOf(score));
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
            if(count>journeyHighCount){
                SharedPreferences.Editor edit=journeyPreferences.edit();
                journeyHighCount=count;
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

            int temp=journeyGameDriver.getLargest();
            if(temp>largest){
                largest=temp;
//                largestTextView.setText(String.valueOf(largest));
                if(largest>journeyHighLargest){
                    SharedPreferences.Editor edit=journeyPreferences.edit();
                    journeyHighLargest=largest;
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

            soundPool.play(soundMap.get(SUCCESS_SOUND_ID), 1, 1, 1, 0, 1f);
        }else if(operator==BaseGameDriver.OP_INVALID){
            operatorTextView.setText(BaseGameDriver.OPERATORS[BaseGameDriver.OP_INVALID ]);
            chain=0;

            soundPool.play(soundMap.get(FAILURE_SOUND_ID), 1, 1, 1, 0, 1f);
        }
        if(moves<1){
            journeyGridView.setGameOver();
        }
//        timeTextView.setText(String.valueOf((int) Math.floor(time)));
        moveTextView.setText(df.format(moves));
    }

    @Override
    public void onNewGame(BaseGameDriver baseGameDriver) {
        newGame();
    }

    @Override
    public void onGameQuit() {
        this.finish();
    }
}
