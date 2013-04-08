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
    private final static int STATE_CHECK_PERIOD = 2000;
    private Timer refreshTimer;
    private boolean refreshScheduled = false;

    // int d = 0; // todo remove

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init controller activity.");

        // show view
        setContentView(R.layout.measurement);

        // attach listeners
        findViewById(R.id.startMeasBtn).setOnClickListener(new OnMeasurementStartListener());
        findViewById(R.id.stopMeasBtn).setOnClickListener(new OnMeasurementStopListener());
        findViewById(R.id.saveToDbBtn).setOnClickListener(new OnSaveListener());
        
        // set default values
        ((EditText) findViewById(R.id.distanceInput)).setText("0");
        ((EditText) findViewById(R.id.depthInput)).setText("0");

        ApplicationContext.debug(getClass(), "Controller activity initialized.");

        final CompassRoseView compassRoseView = (CompassRoseView) findViewById(R.id.compassRoseView);
        final Handler handler = new Handler();


        // test compass
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        compassRoseView.setDirection(d++);
                    }
                });
            }
        }, 0, 1000);*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        // check if it's already scheduled
        if (refreshScheduled) {
            refreshTimer.cancel();
            refreshScheduled = false;
            refreshTimer = null;

            ApplicationContext.debug(getClass(), "Measurement status refresh timer stopped.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ApplicationContext.debug(getClass(), "Starting measurement status refresh timer.");

        // check if it's already scheduled
        if (refreshScheduled && refreshTimer == null) {
            throw new AssertionError("Task already scheduled.");
        }

        // init timer
        this.refreshTimer = new Timer();

        // schedule update status rows
        scheduleUpdateStatusRows();
        refreshScheduled = true;

        ApplicationContext.debug(getClass(), "Measurement status refresh timer started.");
    }

    private void scheduleUpdateStatusRows() {
        final Handler handler = new Handler();

        refreshTimer.schedule(new TimerTask() {
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

                    ApplicationContext.debug(getClass(), "Last measurement values applied to view.");
                } catch (SensorException e) {
                    ApplicationContext.handleException(getClass(), e);
                    //throw new AssertionError(e);
                }
            }
        }, 0, STATE_CHECK_PERIOD);
    }

    private void fillStatus(MeasurementResult measurement) {
        fillTextView((TextView) findViewById(R.id.frequencyValue), measurement.getFrequency());
        fillTextView((TextView) findViewById(R.id.velocityValue), measurement.getVelocity());
        fillTextView((TextView) findViewById(R.id.dateValue), measurement.getRealDate() + " " + measurement.getRealTime());
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
            // check if not started
            final Status statusOut;

            try {
                statusOut = ApplicationContext.getInstance().getDeviceController().getStatusOut();

                if (statusOut.isStarted()) {
                    showMessage("Already started.");
                    return;
                }
            } catch (SensorException e) {
                ApplicationContext.handleException(getClass(), e);
                showMessage("Error occurred.");
            }

            // init measurement start
            try {
                ApplicationContext.getInstance().getDeviceController().start();
                showMessage("Measurement started.");
            } catch (SensorException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private class OnMeasurementStopListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // check if not started
            final Status statusOut;

            try {
                statusOut = ApplicationContext.getInstance().getDeviceController().getStatusOut();

                if (!statusOut.isStarted()) {
                    showMessage("Already stopped.");
                    return;
                }
            } catch (SensorException e) {
                ApplicationContext.handleException(getClass(), e);
                showMessage("Error occurred.");
            }

            // init measurement stop
            try {
                ApplicationContext.getInstance().getDeviceController().stop();
            } catch (SensorException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            showMessage("Measurement stopped.");
        }
    }

    private class OnSaveListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // read input values
            final String distanceStr = ((EditText) findViewById(R.id.distanceInput)).getText().toString();
            final String depthStr = ((EditText) findViewById(R.id.depthInput)).getText().toString();

            Integer distance = 0;
            Integer depth = 0;
            boolean valuesValid;

            // validate input values
            try {
                distance = Integer.valueOf(distanceStr);
                depth = Integer.valueOf(depthStr);
                valuesValid = true;
                showMessage("Save measurement result with depth = " + depth + " and distance = " + distance + ".");
            } catch (NumberFormatException e) {
                valuesValid = false;
                showMessage("Invalid depth or distance.");
            }

            if (valuesValid) {
                // init measurement start
                try {
                    ApplicationContext.getInstance().getDeviceController().save(new MeasurementParameters(distance, depth));
                    showMessage("Measurement result saved.");
                } catch (SensorException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }
}
