package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.activity.handlers.ForwardListener;
import edu.spbstu.wfsmp.sensor.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:04
 */
public class MeasurementActivity extends Activity {

    public static final String TAG = MeasurementActivity.class.getName();
    private final static int STATE_CHECK_PERIOD = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init controller activity.");

        // show view
        setContentView(R.layout.measurement);

        // make clickable start button only
        findViewById(R.id.startMeasBtn).setClickable(true);
        findViewById(R.id.stopMeasBtn).setClickable(false);

        // attach listeners
        findViewById(R.id.startMeasBtn).setOnClickListener(new OnMeasurementStartListener());
        findViewById(R.id.startMeasBtn).setOnClickListener(new OnMeasurementStopListener());

        // schedule update status rows
        scheduleUpdateStatusRows();

        ApplicationContext.debug(getClass(), "Controller activity initialized.");
    }

    private void scheduleUpdateStatusRows() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final Button startMeasBtn = (Button) findViewById(R.id.startMeasBtn);
        final Button stopMeasBtn = (Button) findViewById(R.id.stopMeasBtn);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ApplicationContext.debug(getClass(), "Updating status rows.");
                try {
                    final Status status = ApplicationContext.getInstance().getDeviceController().getStatusOut();

                    ApplicationContext.debug(getClass(), "Status bits: " + status.toString());

                    ApplicationContext.debug(getClass(), "Loading last measurement status.");

                    // fill status rows
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                fillStatus(ApplicationContext.getInstance().getDeviceController().getCurrentMeasurement());
                                fillTextView((TextView) findViewById(R.id.statusValue), status.isStarted() ? "started" : "stopped");
                            } catch (SensorException e) {
                                ApplicationContext.handleException(getClass(), e);
                                throw new AssertionError(e);
                            }
                        }
                    });

                    if (status.isStarted()) {
                        // started - block start button
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startMeasBtn.setClickable(false);
                                stopMeasBtn.setClickable(true);
                            }
                        });
                    } else {
                        // stopped - block stop button
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startMeasBtn.setClickable(true);
                                stopMeasBtn.setClickable(false);
                            }
                        });
                    }

                    ApplicationContext.debug(getClass(), "Last measurement values applied to view.");
                } catch (SensorException e) {
                    ApplicationContext.handleException(getClass(), e);
                    throw new AssertionError(e);
                }
            }
        }, 0, STATE_CHECK_PERIOD);
    }

    private void fillStatus(MeasurementResult measurement) {
        fillTextView((TextView) findViewById(R.id.distanceValue), measurement.getDistance());
        fillTextView((TextView) findViewById(R.id.depthValue), measurement.getDepth());
        fillTextView((TextView) findViewById(R.id.frequencyValue), measurement.getFrequency());
        fillTextView((TextView) findViewById(R.id.velocityValue), measurement.getVelocity());
        fillTextView((TextView) findViewById(R.id.dateValue), measurement.getDate() + " " + measurement.getTime());
        fillTextView((TextView) findViewById(R.id.timeValue), measurement.getMeasTime());
        fillTextView((TextView) findViewById(R.id.numTurnsValue), measurement.getTurns());
    }

    private <T> void fillTextView(TextView distanceValueView, T value) {
        if (value != null) {
            distanceValueView.setText(String.valueOf(value));
        } else {
            distanceValueView.setText("-");
        }
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

    private class OnMeasurementStartListener implements View.OnClickListener {

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
                    ApplicationContext.getInstance().getDeviceController().start(new MeasurementParameters(distance, depth));
                    showMessage("Measurement started.");
                } catch (SensorException e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                final Button startMeasBtn = (Button) findViewById(R.id.startMeasBtn);
                final Button stopMeasBtn = (Button) findViewById(R.id.stopMeasBtn);

                startMeasBtn.setClickable(false);
                stopMeasBtn.setClickable(true);
            }
        }
    }

    private class OnMeasurementStopListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            showMessage("Stopping measurement...");

            // init measurement stop
            try {
                ApplicationContext.getInstance().getDeviceController().stop();
            } catch (SensorException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            showMessage("Measurement stopped.");

            final Button startMeasBtn = (Button) findViewById(R.id.startMeasBtn);
            final Button stopMeasBtn = (Button) findViewById(R.id.stopMeasBtn);

            startMeasBtn.setClickable(true);
            stopMeasBtn.setClickable(false);
        }
    }

}
