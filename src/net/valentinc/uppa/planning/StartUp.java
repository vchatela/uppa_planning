package net.valentinc.uppa.planning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by valentinc on 21/09/2015.
 * StartUp activity
 */
public class StartUp extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_startup);
        // wait 1 sec
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
