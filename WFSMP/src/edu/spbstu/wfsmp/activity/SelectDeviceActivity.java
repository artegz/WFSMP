package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.ApplicationProperties;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceManager;
import edu.spbstu.wfsmp.driver.d2xx.D2xxDeviceManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:04
 */
public class SelectDeviceActivity extends Activity {

    private Timer timer;

    @NotNull
    private final List<String> devices = new ArrayList<String>();

    @NotNull
    private final Map<String, DeviceDescriptor> deviceDescriptions = new HashMap<String, DeviceDescriptor>();

    private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Initializing select device activity.");

        setContentView(R.layout.select_device);

        final ListView view = (ListView) findViewById(R.id.selectDeviceList);

        // set adapter which provide device list
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                R.layout.select_device,
                R.id.text_row,
                devices);

        view.setAdapter(arrayAdapter);

        // register listener for device selection
        view.setOnItemClickListener(new ConnectListener());

        // set timer activity refresh list
        timer = new Timer();

        final Handler uiHandler = new Handler();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (! paused) {
                    updateData();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }, 0, 5000); // start immediately, refresh every 5 seconds

        ApplicationContext.debug(getClass(), "Select device activity successfully initialized.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        ApplicationContext.debug(getClass(), "Select device activity destroyed.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused = false;
    }

    private void updateData() {
        ApplicationContext.debug(getClass(), "Device list reloaded.");
        devices.clear();
        deviceDescriptions.clear();

        try {
            final List<DeviceDescriptor> connectedDevices = getDeviceManager().getConnectedDevices();

            for (DeviceDescriptor connectedDevice : connectedDevices) {
                deviceDescriptions.put("index: " + connectedDevice.getIndex(), connectedDevice);
            }
        } catch (DeviceException e) {
            ApplicationContext.handleException(getClass(), e);
        }

        devices.addAll(deviceDescriptions.keySet());
    }

    @NotNull
    private DeviceManager getDeviceManager() {
        return ApplicationContext.getInstance().getDeviceManager();
    }

    class ConnectListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final String item = (String) adapterView.getItemAtPosition(i);

            ApplicationContext.debug(getClass(), "Device '" + item + "' selected.");

            final DeviceManager driverManager = getDeviceManager();
            final DeviceDescriptor deviceDescriptor = deviceDescriptions.get(item);

            try {
                final Device driver = driverManager.openDevice(deviceDescriptor);

                ApplicationContext.debug(getClass(), "Device connected.");
                ApplicationContext.getInstance().set(ApplicationProperties.CURRENT_DRIVER, driver);
                ApplicationContext.debug(getClass(), "Forwarding activity controller activity.");
                startActivity(new Intent(SelectDeviceActivity.this.getBaseContext(), ControllerActivity.class));
            } catch (DeviceException e) {
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }

}
