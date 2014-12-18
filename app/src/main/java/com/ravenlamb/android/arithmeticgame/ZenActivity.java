package com.ravenlamb.android.arithmeticgame;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;


public class ZenActivity extends ActionBarActivity
    implements BaseGridView.OnGridViewInteraction{

    public static final String TAG=BaseGameDriver.class.getName();
    public static final String ZEN_PREFERENCES="ZenPreferences";
    public static final String HIGH_SCORE ="highScore";
    public static final String HIGH_COUNT="highCount";
    public static final String HIGH_CHAIN ="highChain";
    public static final String HIGH_LARGEST ="highLargest";

    ZenGridView zenGridView;

    TextView op1TextView;
    TextView op2TextView;
    TextView operatorTextView;
    TextView resultTextView;
    TextView historyTextView;
//    TextView debugTextView;

    TextView scoreTextView;
    TextView countTextView;
    TextView chainTextView;
    TextView largestTextView;

    double score=0;
    int count=0;
    int chain=0;
    int largest=0;

    SharedPreferences zenPreferences;

    double zenHighScore=0;
    int zenHighCount=0;
    int zenHighChain=0;
    int zenHighLargest=0;

    boolean alreadyHighScore=false;
    boolean alreadyHighCount=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zen);
//        if (savedInstanceState == null) {
////            getSupportFragmentManager().beginTransaction()
////                    .add(R.id.container, new PlaceholderFragment())
////                    .commit();
//            FrameLayout fragment_container= (FrameLayout) findViewById(R.id.fragment_container);
//            ViewGroup.LayoutParams layoutParam= fragment_container.getLayoutParams();
//            int screenwidth=getResources().getDisplayMetrics().widthPixels;
//            Toast.makeText(this, "ZenActivity "+screenwidth, Toast.LENGTH_LONG).show();
//            layoutParam.width=screenwidth;
//            layoutParam.height=screenwidth;
////            ViewGroup.LayoutParams layoutParam=new FrameLayout.LayoutParams(screenwidth,screenwidth);
//            fragment_container.setLayoutParams(layoutParam);
//            ZenGridFragment zenGridFragment=new ZenGridFragment();
//            int commit = getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, zenGridFragment)
//                    .commit();
//        }


        //todo need to initialize textview values
        scoreTextView = (TextView) findViewById(R.id.score_textview);
        countTextView = (TextView) findViewById(R.id.count_textview);
        chainTextView = (TextView) findViewById(R.id.chain_textview);
        largestTextView =(TextView) findViewById(R.id.largest_textview);


        op1TextView = (TextView) findViewById(R.id.op1_textview);
        op2TextView= (TextView) findViewById(R.id.op2_textview);
        operatorTextView= (TextView) findViewById(R.id.operator_textview);
        resultTextView= (TextView) findViewById(R.id.result_textview);
        historyTextView = (TextView) findViewById(R.id.history_textview);
//        debugTextView = (TextView) findViewById(R.id.debug_textview);

        zenPreferences=getSharedPreferences(ZEN_PREFERENCES,0);
        zenHighScore=zenPreferences.getFloat(HIGH_SCORE, 0);
        zenHighCount=zenPreferences.getInt(HIGH_COUNT,0);
        zenHighChain=zenPreferences.getInt(HIGH_CHAIN,0);
        zenHighLargest=zenPreferences.getInt(HIGH_LARGEST,0);


        DisplayMetrics dm=getResources().getDisplayMetrics();
        int screenW=dm.widthPixels;
        int screenH=dm.heightPixels;
        int gridViewWidth=screenW-Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin))*2;
        if(gridViewWidth*2>screenH){
            gridViewWidth=screenH/2;
        }
        zenGridView =(ZenGridView)findViewById(R.id.zen_grid_view);
        ViewGroup.LayoutParams layoutParams= zenGridView.getLayoutParams();
        layoutParams.width=gridViewWidth;
        layoutParams.height=gridViewWidth;
        zenGridView.setLayoutParams(layoutParams);


    }


    public void newGame(){
        score=0;
        count=0;
        chain=0;
        largest=0;

        alreadyHighCount=false;
        alreadyHighScore=false;
        scoreTextView.setTypeface(null, Typeface.NORMAL);
        countTextView.setTypeface(null, Typeface.NORMAL);
        chainTextView.setTypeface(null, Typeface.NORMAL);
        largestTextView.setTypeface(null, Typeface.NORMAL);

        op1TextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        op2TextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        resultTextView.setText(BaseGameDriver.UNKNOWN_VALUE);
        operatorTextView.setText(BaseGameDriver.UNKNOWN_OPERATOR);
        scoreTextView.setText(String.valueOf((int)score));
        countTextView.setText(String.valueOf(count));
        chainTextView.setText(String.valueOf(chain));
        largestTextView.setText(String.valueOf(largest));

        zenGridView.initDriver();
        zenGridView.invalidate();

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
            //todo mute, unmute
            return true;
        }
        if (id == R.id.action_help) {
            //todo show help dialog
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.zen_help).setTitle("Zen Help");
            AlertDialog dialog=builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(BaseGameDriver baseGameDriver) {
        ZenGameDriver zenGameDriver=(ZenGameDriver)baseGameDriver;

        //todo shift operand animation
        //if result is not valid before, and not valid now, don't need shift animation
        boolean doShiftAnimation=true;
        String resultStrBefore=resultTextView.getText().toString();
        String resultStrAfter=zenGameDriver.getResultNumber();
        if(resultStrBefore.equals(BaseGameDriver.UNKNOWN_VALUE) &&
                resultStrAfter.equals(BaseGameDriver.UNKNOWN_VALUE)){
            doShiftAnimation=false;
        }


        op1TextView.setText(zenGameDriver.getOp1Number());
        op2TextView.setText(zenGameDriver.getOp2Number());
        resultTextView.setText(resultStrAfter);
//        int operator=baseGameDriver.computeStatus();
        int operator=baseGameDriver.getCurrStatus();
        //todo add animation and sound effect
        //todo add high score, chains, largest number
        if(operator==BaseGameDriver.OP_ADDITION ||
                operator==BaseGameDriver.OP_SUBTRACTION ||
                operator==BaseGameDriver.OP_MULTIPLICATION ||
                operator==BaseGameDriver.OP_DIVISION ){
            Log.d(TAG,"ZenActivity onUpdate" );
            operatorTextView.setText(zenGameDriver.getOperator());
//            String currHistory=historyTextView.getText().toString();
//            historyTextView.setText(baseGameDriver.getCurrEquation() +"\n" + currHistory);
            historyTextView.setText(zenGameDriver.history.toString());


            chain++;
            chainTextView.setText(String.valueOf(chain));
            if(chain>zenHighChain){
                SharedPreferences.Editor edit=zenPreferences.edit();
                zenHighChain=chain;
                edit.putInt(HIGH_CHAIN,chain);
                //todo new high chain animation, need to change to translate bounce
                chainTextView.setTypeface(null, Typeface.BOLD);
                ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
                scaleAnimation.setDuration(500);
                scaleAnimation.setInterpolator(new BounceInterpolator());
                chainTextView.startAnimation(scaleAnimation);
            }else{
                chainTextView.setTypeface(null, Typeface.NORMAL);
            }


            //todo need to compare to preference high score and change scoreTextView
            score+=zenGameDriver.getScore()*chain;
            scoreTextView.setText(String.valueOf((int)Math.floor(score)));
//        scoreTextView.setText(String.valueOf(score));
            if(score>zenHighScore){
                SharedPreferences.Editor edit=zenPreferences.edit();
                zenHighScore=score;
                edit.putFloat(HIGH_SCORE,(float)score);

                //todo new high score animation, scale score textview
                if(!alreadyHighScore) {
                    alreadyHighScore = true;
                    scoreTextView.setTypeface(null, Typeface.BOLD);
                    ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
                    scaleAnimation.setDuration(500);
                    scaleAnimation.setInterpolator(new BounceInterpolator());
                    scoreTextView.startAnimation(scaleAnimation);
                }
            }

            count++;
            countTextView.setText(String.valueOf(count));
            if(count>zenHighCount){
                SharedPreferences.Editor edit=zenPreferences.edit();
                zenHighCount=count;
                edit.putInt(HIGH_COUNT,count);

                //todo new high count animation
                if(!alreadyHighCount){
                    alreadyHighCount=true;
                    countTextView.setTypeface(null, Typeface.BOLD);
                    ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
                    scaleAnimation.setDuration(500);
                    scaleAnimation.setInterpolator(new BounceInterpolator());
                    countTextView.startAnimation(scaleAnimation);
                }
            }

            int temp=zenGameDriver.getLargest();
            if(temp>largest){
                largest=temp;
                largestTextView.setText(String.valueOf(largest));
                if(largest>zenHighLargest){
                    SharedPreferences.Editor edit=zenPreferences.edit();
                    zenHighLargest=largest;
                    edit.putInt(HIGH_LARGEST,largest);
                    //todo new high largest animation
                    largestTextView.setTypeface(null, Typeface.BOLD);
                    ScaleAnimation scaleAnimation=new ScaleAnimation(1f,2f,1f,2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f);
                    scaleAnimation.setDuration(500);
                    scaleAnimation.setInterpolator(new BounceInterpolator());
                    largestTextView.startAnimation(scaleAnimation);
                }else{
                    largestTextView.setTypeface(null, Typeface.NORMAL);
                }
            }

        }else if(operator==BaseGameDriver.OP_INVALID){
            operatorTextView.setText(BaseGameDriver.OPERATORS[BaseGameDriver.OP_INVALID ]);
            chain=0;
        }

//        historyTextView.setText(baseGameDriver.toString());
    }


    @Override
    public void onDebug(BaseGameDriver baseGameDriver) {
        //historyTextView.setText(baseGameDriver.toString());
    }
}
