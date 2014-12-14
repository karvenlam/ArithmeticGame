package com.ravenlamb.android.arithmeticgame;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class JourneyActivity extends ActionBarActivity
        implements BaseGridView.OnGridViewInteraction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
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
        if (id == R.id.action_help) {
            //todo show help dialog
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.journey_help).setTitle("Journey Help");
            AlertDialog dialog=builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate(BaseGameDriver baseGameDriver) {
        //todo
    }

    @Override
    public void onDebug(BaseGameDriver baseGameDriver) {

    }
}
