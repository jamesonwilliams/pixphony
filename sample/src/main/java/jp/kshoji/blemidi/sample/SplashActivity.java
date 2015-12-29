package jp.kshoji.blemidi.sample;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/*
 * See https://stackoverflow.com/questions/5486789/how-do-i-make-a-splash-screen
 */
public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);

        /* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, CentralActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}

