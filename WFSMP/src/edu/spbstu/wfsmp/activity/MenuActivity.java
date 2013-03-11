package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.activity.handlers.DisconnectListener;
import edu.spbstu.wfsmp.activity.handlers.ForwardListener;

/**
 * User: Artegz
 * Date: 08.03.13
 * Time: 22:21
 */
public class MenuActivity extends Activity {

    // todo asm: handle disconnect during workflow
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init menu activity.");

        setContentView(R.layout.menu);

        findViewById(R.id.doMeasurementBtn).setOnClickListener(new ForwardListener(MeasurementActivity.class, this));
        findViewById(R.id.devInfoBtn).setOnClickListener(new ForwardListener(ShowInfoActivity.class, this));
        findViewById(R.id.disconnectBtn).setOnClickListener(new DisconnectListener(SelectDeviceActivity.class, this));
        findViewById(R.id.excelExportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("Export to excel isn't supported yet.");
            }
        });
        findViewById(R.id.viewResultsBtn).setOnClickListener(new ForwardListener(ViewResultsActivity.class, this));
        findViewById(R.id.programmSensorBtn).setOnClickListener(new ForwardListener(ProgrammingActivity.class, this));
    }

    private void showMessage(final String message) {
        Log.i(getClass().getName(), message);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.statusRow)).setText(message);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DisconnectListener.disconnect();
        new ForwardListener(SelectDeviceActivity.class, this).onClick(null);
    }
}
