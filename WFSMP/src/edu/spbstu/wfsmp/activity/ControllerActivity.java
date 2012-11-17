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
import edu.spbstu.wfsmp.sensor.SensorController;
import edu.spbstu.wfsmp.sensor.SensorException;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:04
 */
public class ControllerActivity extends Activity {

    public static final String TAG = ControllerActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init controller activity.");

        // prepare controller for selected device
        final Device device = (Device) ApplicationContext.getInstance().get(ApplicationProperties.CURRENT_DRIVER);
        assert (device != null);
        final SensorController sensorController = new SensorController(device);

        ApplicationContext.getInstance().set(ApplicationProperties.CURRENT_SENSOR, sensorController);

        // show view
        setContentView(R.layout.device_controller);

        // attach listeners
        findViewById(R.id.devInfoBtn).setOnClickListener(new ForwardListener(ShowInfoActivity.class, this));
        findViewById(R.id.disconnectBtn).setOnClickListener(new DisconnectListener(SelectDeviceActivity.class, this));
        findViewById(R.id.startMeasBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String distanceStr = ((EditText) findViewById(R.id.distanceInput)).getText().toString();
                final String depthStr = ((EditText) findViewById(R.id.depthInput)).getText().toString();

                showMessage("Starting measurement...");

                Integer distance = 0;
                Integer depth = 0;
                boolean valuesValid;

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
                    try {
                        sensorController.startMeasuring(new MeasurementParameters(distance, depth));
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
                try {
                    sensorController.stopMeasuring();
                } catch (SensorException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                showMessage("Measurement stopped.");
            }
        });
        findViewById(R.id.excelExportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("Export to excel isn't supported yet.");
            }
        });
        findViewById(R.id.viewResultsBtn).setOnClickListener(new ForwardListener(ViewResultsActivity.class, this));
        findViewById(R.id.programmSensorBtn).setOnClickListener(new ForwardListener(ProgrammingActivity.class, this));

        ApplicationContext.debug(getClass(), "Controller activity initialized.");
    }

    private void showMessage(final String message) {
        Log.i(TAG, message);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.controllerLoggingBox)).setText(message);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DisconnectListener.disconnect();
        super.onBackPressed();
    }

}
