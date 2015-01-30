package com.ravenlamb.android.arithmeticgame;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


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

        return super.onOptionsItemSelected(item);
    }
}
