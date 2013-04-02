package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.ApplicationProperties;
import edu.spbstu.wfsmp.activity.handlers.ForwardListener;
import edu.spbstu.wfsmp.sensor.DeviceController;
import edu.spbstu.wfsmp.sensor.DeviceControllerImpl;
import edu.spbstu.wfsmp.sensor.SensorException;

/**
 * User: artegz
 * Date: 28.10.12
 * Time: 19:14
 */
public class ShowInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init show info activity.");

        setContentView(R.layout.show_info);

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String serialNumber = ApplicationContext.getInstance().getDeviceController().getSerialNumber();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final TextView textView = (TextView) findViewById(R.id.serialNumberView);
                            textView.setText(serialNumber);
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

        ApplicationContext.debug(getClass(), "Show info activity initialized.");
    }

    @Override
    public void onBackPressed() {
        new ForwardListener(MenuActivity.class, this).onClick(null);
    }

}
