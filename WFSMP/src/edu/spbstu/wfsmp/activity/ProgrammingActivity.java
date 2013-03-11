package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.os.Bundle;
import edu.spbstu.wfsmp.activity.handlers.ForwardListener;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:05
 */
public class ProgrammingActivity extends Activity {

    // todo implement me

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.programming);
    }

    @Override
    public void onBackPressed() {
        new ForwardListener(MenuActivity.class, this).onClick(null);
    }
}
