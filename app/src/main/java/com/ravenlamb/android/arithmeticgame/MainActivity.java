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
import android.view.animation.TranslateAnimation;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        //todo start number animation
        super.onResume();
    }

    @Override
    protected void onPause() {
        //todo stop number animation
        super.onPause();
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

    @Override
    protected void onStart() {
        super.onStart();
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int screenW=dm.widthPixels;
        int screenH=dm.heightPixels;

        //todo combine visibility, alphaanimation and translateanimation
        TextView textView0=(TextView)findViewById(R.id.textView0);
        int x=(int)(Math.random()*(double)screenW);
        int y=(int)(Math.random()*(double)screenH);

        TranslateAnimation position=new TranslateAnimation(x,x,y,y);
        position.setDuration(5000);
        position.setRepeatCount(Animation.INFINITE);
        textView0.startAnimation(position);

    }
}
