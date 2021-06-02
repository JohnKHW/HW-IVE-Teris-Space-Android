package ivegsd.johnwkh.assignment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class GameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Disable screen rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //setContentView(new GameView(this));
        setContentView(new MainGamePanel(this));
    }
}
