package com.ravenlamb.android.arithmeticgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends ActionBarActivity {

    TextView[] numTextViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numTextViews=new TextView[10];
        numTextViews[0]=(TextView)findViewById(R.id.textView0);
        numTextViews[1]=(TextView)findViewById(R.id.textView1);
        numTextViews[2]=(TextView)findViewById(R.id.textView2);
        numTextViews[3]=(TextView)findViewById(R.id.textView3);
        numTextViews[4]=(TextView)findViewById(R.id.textView4);
        numTextViews[5]=(TextView)findViewById(R.id.textView5);
        numTextViews[6]=(TextView)findViewById(R.id.textView6);
        numTextViews[7]=(TextView)findViewById(R.id.textView7);
        numTextViews[8]=(TextView)findViewById(R.id.textView8);
        numTextViews[9]=(TextView)findViewById(R.id.textView9);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int screenW=dm.widthPixels;
        int screenH=dm.heightPixels;
        int screenMin=(screenW>screenH)?screenH:screenW;

        for(int i=0;i<10;i++) {
            float size = (float) ((Math.random() * .3 + .4) * screenMin);
            int x = (int) ((Math.random()*.4+0.1) * (double) screenW );
            int y = (int) ((Math.random()*.7-0.0) * (double) screenH );
            float r = (float) Math.random() * 360;

            numTextViews[i].setTextSize(size);
            numTextViews[i].setTranslationX(x);
            numTextViews[i].setTranslationY(y);
            numTextViews[i].setRotation(r);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void onZenStart(View view)
    {
        Intent intent=new Intent(this, ZenActivity.class);
        startActivity(intent);
    }

    public void onJourneyStart(View view){
        Intent intent=new Intent(this, JourneyActivity.class);
        startActivity(intent);
    }

    public void onAdventureStart(View view){
        Intent intent=new Intent(this, AdventureActivity.class);
        startActivity(intent);
    }

    public void onChainStart(View view){
        Intent intent=new Intent(this, ChainActivity.class);
        startActivity(intent);
    }

    public void onHighScoreStart(View view){
        Intent intent=new Intent(this, HighScoreActivity.class);
        startActivity(intent);
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
        if(id == R.id.action_quit){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
