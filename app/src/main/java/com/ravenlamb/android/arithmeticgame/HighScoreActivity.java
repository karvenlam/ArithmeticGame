package com.ravenlamb.android.arithmeticgame;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ravenlamb.android.arithmeticgame.R;

import java.util.ArrayList;

public class HighScoreActivity extends ActionBarActivity {

    public static final String CHAIN_PREFERENCES=ChainActivity.CHAIN_PREFERENCES;
    public static final String ADVENTURE_PREFERENCES =AdventureActivity.ADVENTURE_PREFERENCES;
    public static final String JOURNEY_PREFERENCES=JourneyActivity.JOURNEY_PREFERENCES;
    public static final String ZEN_PREFERENCES=ZenActivity.ZEN_PREFERENCES;
    public static final String HIGH_SCORE ="highScore";
    public static final String HIGH_CHAIN ="highChain";
    public static final String HIGH_LARGEST ="highLargest";

    View[] animateViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);


        SharedPreferences chainPreferences=getSharedPreferences(CHAIN_PREFERENCES,0);
        double chainHighScore=Double.parseDouble(chainPreferences.getString(HIGH_SCORE, "0"));
        TextView chainScore=(TextView)findViewById(R.id.chain_score);
        chainScore.setText(String.valueOf((int)Math.floor(chainHighScore)));
        int chainHighChain=chainPreferences.getInt(HIGH_CHAIN,0);
        TextView chainChain=(TextView)findViewById(R.id.chain_chain);
        chainChain.setText(String.valueOf(chainHighChain));
        int chainHighLargest=chainPreferences.getInt(HIGH_LARGEST,0);
        TextView chainLargest=(TextView)findViewById(R.id.chain_largest);
        chainLargest.setText(String.valueOf(chainHighLargest));


        SharedPreferences adventurePreferences=getSharedPreferences(ADVENTURE_PREFERENCES,0);
        double adventureHighScore=Double.parseDouble(adventurePreferences.getString(HIGH_SCORE, "0"));
        TextView adventureScore=(TextView)findViewById(R.id.adventure_score);
        adventureScore.setText(String.valueOf((int)Math.floor(adventureHighScore)));
        int adventureHighChain=adventurePreferences.getInt(HIGH_CHAIN,0);
        TextView adventureChain=(TextView)findViewById(R.id.adventure_chain);
        adventureChain.setText(String.valueOf(adventureHighChain));
        int adventureHighLargest=adventurePreferences.getInt(HIGH_LARGEST,0);
        TextView adventureLargest=(TextView)findViewById(R.id.adventure_largest);
        adventureLargest.setText(String.valueOf(adventureHighLargest));


        //count
        SharedPreferences journeyPreferences=getSharedPreferences(JOURNEY_PREFERENCES,0);
        double journeyHighScore=Double.parseDouble(journeyPreferences.getString(HIGH_SCORE, "0"));
        TextView journeyScore=(TextView)findViewById(R.id.journey_score);
        journeyScore.setText(String.valueOf((int)Math.floor(journeyHighScore)));
        int journeyHighChain=journeyPreferences.getInt(HIGH_CHAIN,0);
        TextView journeyChain=(TextView)findViewById(R.id.journey_chain);
        journeyChain.setText(String.valueOf(journeyHighChain));
        int journeyHighLargest=journeyPreferences.getInt(HIGH_LARGEST,0);
        TextView journeyLargest=(TextView)findViewById(R.id.journey_largest);
        journeyLargest.setText(String.valueOf(journeyHighLargest));


        SharedPreferences zenPreferences=getSharedPreferences(ZEN_PREFERENCES,0);
        double zenHighScore=Double.parseDouble(zenPreferences.getString(HIGH_SCORE, "0"));
        TextView zenScore=(TextView)findViewById(R.id.zen_score);
        zenScore.setText(String.valueOf((int)Math.floor(zenHighScore)));
        int zenHighChain=zenPreferences.getInt(HIGH_CHAIN,0);
        TextView zenChain=(TextView)findViewById(R.id.zen_chain);
        zenChain.setText(String.valueOf(zenHighChain));
        int zenHighLargest=zenPreferences.getInt(HIGH_LARGEST,0);
        TextView zenLargest=(TextView)findViewById(R.id.zen_largest);
        zenLargest.setText(String.valueOf(zenHighLargest));

        ArrayList<View> viewArrayList=new ArrayList<View>();
        viewArrayList.add(findViewById(R.id.chainTitleTextView));
        viewArrayList.add(findViewById(R.id.chainScoreLinear));
        viewArrayList.add(findViewById(R.id.chainChainLinear));
        viewArrayList.add(findViewById(R.id.chainLargestLinear));

        viewArrayList.add(findViewById(R.id.adventureTitleTextView));
        viewArrayList.add(findViewById(R.id.adventureScoreLinear));
        viewArrayList.add(findViewById(R.id.adventureChainLinear));
        viewArrayList.add(findViewById(R.id.adventureLargestLinear));

        viewArrayList.add(findViewById(R.id.journeyTitleTextView));
        viewArrayList.add(findViewById(R.id.journeyScoreLinear));
        viewArrayList.add(findViewById(R.id.journeyChainLinear));
        viewArrayList.add(findViewById(R.id.journeyLargestLinear));

        viewArrayList.add(findViewById(R.id.zenTitleTextView));
        viewArrayList.add(findViewById(R.id.zenScoreLinear));
        viewArrayList.add(findViewById(R.id.zenChainLinear));
        viewArrayList.add(findViewById(R.id.zenLargestLinear));


        animateViews=new View[viewArrayList.size()];
        for(int i=0;i<animateViews.length;i++){
            animateViews[i]=viewArrayList.get(i);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_rules) {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.main_rule).setTitle("Game Rules");
            AlertDialog dialog=builder.create();
            dialog.show();
            return true;
        }
        if(id == R.id.action_quit){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int screenW=dm.widthPixels;
        int offset=180;
        for(int i=0;i<animateViews.length;i++){
            TranslateAnimation translateAnimation=new TranslateAnimation(screenW,0,0,0);
            translateAnimation.setDuration(500);
            translateAnimation.setInterpolator(new OvershootInterpolator(.5f));
            translateAnimation.setStartOffset(i * offset);
            animateViews[i].startAnimation(translateAnimation);
        }

    }
}
