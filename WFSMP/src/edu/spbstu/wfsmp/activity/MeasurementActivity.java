package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.ApplicationProperties;
import edu.spbstu.wfsmp.activity.handlers.DisconnectListener;
import edu.spbstu.wfsmp.activity.handlers.ForwardListener;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.sensor.MeasurementParameters;
import edu.spbstu.wfsmp.sensor.DeviceController;
import edu.spbstu.wfsmp.sensor.SensorException;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:04
 */
public class MeasurementActivity extends Activity {

    public static final String TAG = MeasurementActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init controller activity.");

        final DeviceController deviceController = (DeviceController) ApplicationContext.getInstance().get(ApplicationProperties.DEVICE_CONTROLLER);

        // show view
        setContentView(R.layout.measurement);

        // attach listeners
        findViewById(R.id.startMeasBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // read input values
                final String distanceStr = ((EditText) findViewById(R.id.distanceInput)).getText().toString();
                final String depthStr = ((EditText) findViewById(R.id.depthInput)).getText().toString();

                showMessage("Starting measurement...");

                Integer distance = 0;
                Integer depth = 0;
                boolean valuesValid;

                // validate input values
                try {
                    distance = Integer.valueOf(distanceStr);
                    depth = Integer.valueOf(depthStr);
                    valuesValid = true;
                    showMessage("Starting measurement with depth = " + depth + " and distance = " + distance + ".");
                } catch (NumberFormatException e) {
                    valuesValid = false;
                    showMessage("Invalid depth or distance.");
                }

                if (valuesValid) {
                    // init measurement start
                    try {
                        deviceController.startMeasuring(new MeasurementParameters(distance, depth));
                        showMessage("Measurement started.");
                    } catch (SensorException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        });
        findViewById(R.id.stopMeasBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("Stopping measurement...");

                // init measurement stop
                try {
                    deviceController.stopMeasuring();
                } catch (SensorException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                showMessage("Measurement stopped.");
            }
        });

        ApplicationContext.debug(getClass(), "Controller activity initialized.");
    }

    private void showMessage(final String message) {
        Log.i(TAG, message);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.statusRow)).setText(message);
            }
        });
    }

    @Override
    public void onBackPressed() {
        new ForwardListener(MenuActivity.class, this).onClick(null);
    }

}
