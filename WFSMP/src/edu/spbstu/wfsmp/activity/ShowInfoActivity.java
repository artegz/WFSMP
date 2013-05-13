package edu.spbstu.wfsmp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.sensor.SensorException;

/**
 * User: artegz
 * Date: 28.10.12
 * Time: 19:14
 */
public class ShowInfoActivity extends AbstractWfsmpActivity {

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
                    final String softwareVersion = ApplicationContext.getInstance().getDeviceController().getSoftwareVersion();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final TextView textView1 = (TextView) findViewById(R.id.serialNumberView);
                            textView1.setText(serialNumber);

                            final TextView textView2 = (TextView) findViewById(R.id.softwareVersionView);
                            textView2.setText(softwareVersion);
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

}
