package com.agilegithub.main;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Louis on 03/05/2015.
 */
public class PlanningPokerView implements IFragmentView{
    public static int COL = 3;
    private LinearLayout grid;
    private Activity activity;
    private int[] numbers = {1,2,3,5,8,13,21,34,55,89};

    public PlanningPokerView(Activity activity){
        grid = new LinearLayout(activity);
        grid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout line = new LinearLayout(activity);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        grid.addView(line);
        Button digit;
        for(int i=0; i<numbers.length; i++) {
            digit = new Button(activity);
            digit.setText(""+numbers[i]);
            digit.setTextSize(40);
            digit.setGravity(Gravity.CENTER);
            digit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            digit.setOnClickListener(new tileOnClickListener());
            line.addView(digit);
            if((i+1)%COL==0) {
                line = new LinearLayout(activity);
                line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                grid.addView(line);
            }
        }
    }

    public View getView(){
        return grid;
    }

    @Override
    public void updateSyncData() {

    }

    private class tileOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            System.out.println("Press: "+b.getText());
            Intent display = new Intent(MainActivity.activity, DisplayTile.class);
            display.putExtra("tile", ""+b.getText());
            MainActivity.activity.startActivity(display);
        }
    }
}
