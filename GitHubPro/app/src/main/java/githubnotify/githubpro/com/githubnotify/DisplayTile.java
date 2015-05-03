package githubnotify.githubpro.com.githubnotify;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;


public class DisplayTile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_slide_in_top, 0);
        setContentView(R.layout.activity_display_tile);
        TextView tile = (TextView) this.findViewById(R.id.tile);
        tile.setText(getIntent().getStringExtra("tile")); // We get the number of the tile to display.
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev) {
        finish();
        DisplayTile.this.overridePendingTransition(0,R.anim.abc_slide_out_bottom);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        DisplayTile.this.overridePendingTransition(0,R.anim.abc_slide_out_bottom);
        return true;
    }
}
