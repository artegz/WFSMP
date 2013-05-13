package edu.spbstu.wfsmp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.ExcelExporter;
import edu.spbstu.wfsmp.sensor.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:04
 */
public class Measurement2Activity extends AbstractWfsmpActivity {

    public static final String TAG = Measurement2Activity.class.getName();
    private final static int STATE_CHECK_PERIOD = 2000;
    private Timer refreshTimer;
    private boolean refreshScheduled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init controller activity.");

        // show view
        setContentView(R.layout.measurement2);

        // create handler to handle GUI events from OnClick listeners
        final Handler handler = new Handler();

        // attach listeners

        // measurement operation
        findViewById(R.id.startMeasBtn).setOnClickListener(new OnMeasurementStartListener());
        findViewById(R.id.stopMeasBtn).setOnClickListener(new OnMeasurementStopListener());
        findViewById(R.id.saveToDbBtn).setOnClickListener(new OnSaveListener());

        // DB operations
        findViewById(R.id.exportDbBtn).setOnClickListener(new ExportResultsListener(handler));
        findViewById(R.id.showDbBtn).setOnClickListener(new ForwardListener(ViewResultsActivity.class, this));
        findViewById(R.id.clearDb).setOnClickListener(new OnClearDbListener());

        // device operations
        findViewById(R.id.devTurnOn).setOnClickListener(new OnTurnOnListener());
        findViewById(R.id.devTurnOff).setOnClickListener(new OnTurnOffListener());

        ApplicationContext.debug(getClass(), "Controller activity initialized.");
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
                    // todo asm: if something else failed - device controller may be unregistered
                    final Status status = ApplicationContext.getInstance().getDeviceController().getStatusOut();

                    ApplicationContext.debug(getClass(), "Status bits: " + status.toString());

                    ApplicationContext.debug(getClass(), "Loading last measurement status.");

                    // fill status rows
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // fill current measurement values
                                fillStatus(ApplicationContext.getInstance().getDeviceController().getCurrentMeasurement());
                                fillTextView((TextView) findViewById(R.id.statusValue), status.isStarted() ? "started" : "stopped");

                                // fill db size data
                                final int dbSize = ApplicationContext.getInstance().getDeviceController().getDbSize();
                                ((TextView) findViewById(R.id.numRecordsValue)).setText(String.valueOf(dbSize));
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
        fillTextView((TextView) findViewById(R.id.devTimeValue), DateFormat.format("hh:mm:ss", measurement.getRealTime()));
        fillTextView((TextView) findViewById(R.id.devDateValue), DateFormat.format("dd.MM.yyyy", measurement.getRealDate()));

        fillTextView((TextView) findViewById(R.id.frequencyValue), measurement.getFrequency());
        fillTextView((TextView) findViewById(R.id.velocityValue), measurement.getVelocity());
        fillTextView((TextView) findViewById(R.id.timeValue), measurement.getMeasTime());
        fillTextView((TextView) findViewById(R.id.numTurnsValue), measurement.getTurns());

        fillTextView((TextView) findViewById(R.id.dirValue), measurement.getDirection());

        ((CompassRoseView) findViewById(R.id.compassRoseView)).setDirection(measurement.getDirection());

    }

    private <T> void fillTextView(TextView valueView, T value) {
        if (value != null) {
            valueView.setText(String.valueOf(value));
        } else {
            valueView.setText("-");
        }
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

    private class ExportResultsListener implements View.OnClickListener {
        private final Handler handler;

        public ExportResultsListener(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onClick(View v) {
            try {
                final String filename = new ExcelExporter().doExportAll();
                final TextView statusRow = (TextView) findViewById(R.id.statusRow);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        statusRow.setText("Device database successfully exported into file '" + filename + "'.");
                    }
                });
            } catch (SensorException e) {
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }

    private class OnClearDbListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                ApplicationContext.getInstance().getDeviceController().clearDb();
                showMessage("DB has been cleared..");
            } catch (SensorException e) {
                showMessage("Clear DB failed.");
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }

    private class OnTurnOnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                ApplicationContext.getInstance().getDeviceController().turnOn();
            } catch (SensorException e) {
                showMessage("Turning on failed.");
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }

    private class OnTurnOffListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                ApplicationContext.getInstance().getDeviceController().turnOff();
            } catch (SensorException e) {
                showMessage("Turning off failed.");
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }
}
