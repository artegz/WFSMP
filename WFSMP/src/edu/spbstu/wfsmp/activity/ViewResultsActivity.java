package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.activity.handlers.ForwardListener;
import edu.spbstu.wfsmp.sensor.MeasurementResult;
import edu.spbstu.wfsmp.sensor.SensorException;
import edu.spbstu.wfsmp.sensor.Status;

import java.util.List;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:05
 */
public class ViewResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_results);

        final TextView textView = (TextView) findViewById(R.id.statusRow);
        textView.setText("Loading results...");

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<MeasurementResult> allMeasurements = ApplicationContext.getInstance().getDeviceController().getDataBaseOut();

                    ApplicationContext.debug(getClass(), "Device DB loaded. Total " + allMeasurements.size() + " entries.");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final TableLayout table = (TableLayout) findViewById(R.id.resultsTable);
                            final View headerRow = findViewById(R.id.resultsHeaderRow);

                            table.removeAllViewsInLayout();

                            table.addView(headerRow);

                            int i = 0;

                            for (MeasurementResult measurementResult : allMeasurements) {
                                final TableRow newDataRow = new TableRow(table.getContext());

                                newDataRow.addView(createCell(i++, newDataRow.getContext()));

                                newDataRow.addView(createCell(measurementResult.getDistance(), newDataRow.getContext()));
                                newDataRow.addView(createCell(measurementResult.getDepth(), newDataRow.getContext()));
                                newDataRow.addView(createCell(measurementResult.getVelocity(), newDataRow.getContext()));
                                newDataRow.addView(createCell(measurementResult.getFrequency(), newDataRow.getContext()));
                                newDataRow.addView(createCell(measurementResult.getTurns(), newDataRow.getContext()));
                                newDataRow.addView(createCell(measurementResult.getMeasTime(), newDataRow.getContext()));
                                final Status status = measurementResult.getStatus();
                                if (status != null) {
                                    newDataRow.addView(createCell(status.getWhirligigType(), newDataRow.getContext()));
                                } else {
                                    newDataRow.addView(createCell("-", newDataRow.getContext()));
                                }
                                newDataRow.addView(createCell(measurementResult.getDate() + " " + measurementResult.getTime(), newDataRow.getContext()));

                                table.addView(newDataRow);
                            }
                        }
                    });
                } catch (SensorException e) {
                    final String message = e.getMessage();

                    ApplicationContext.handleException(getClass(), e);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final TextView textView = (TextView) findViewById(R.id.statusRow);
                            textView.setText(message);
                        }
                    });
                }
            }
        }).start();


        /*final ListView view = (ListView) findViewById(R.id.resultsTable);

        // set adapter which provide device list
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                results);

        view.setAdapter(arrayAdapter);*/
    }

    private TextView createCell(Object value, Context context) {
        final TextView cell = new TextView(context);
        cell.setPadding(5, 5, 5, 5);
        cell.setText(String.valueOf(value));
        return cell;
    }

    @Override
    public void onBackPressed() {
        new ForwardListener(MenuActivity.class, this).onClick(null);
    }
}
