package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:04
 */
public class SelectDeviceActivity extends Activity {

    @NotNull
    private final List<String> deviceIdentifiers = new ArrayList<String>();

    // key: device identifier, value: device descriptor
    @NotNull
    private final Map<String, DeviceDescriptor> deviceDescriptors = new HashMap<String, DeviceDescriptor>();

    @NotNull
    private ArrayAdapter<String> devicesViewAdapter;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Initializing select device activity.");

        // ************ Prepare view ****************

        setContentView(R.layout.select_device);

        final ListView view = (ListView) findViewById(R.id.selectDeviceList);

        // set adapter which provide device list
        devicesViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceIdentifiers);

        view.setAdapter(devicesViewAdapter);

        // register listener for device selection
        view.setOnItemClickListener(new DeviceSelectedListener());

        // ************ Additional initialization *****************

        // initialize usb manager
        try {
            UsbEventBroadcastManager.getInstance().init(this.getApplication().getApplicationContext());
        } catch (DeviceException e) {
            ApplicationContext.handleException(getClass(), e);
        }

        ApplicationContext.debug(getClass(), "UsbEventBroadcastManager successfully initialized.");

        // initialize timer refreshing GUI devises list
        final Handler uiHandler = new Handler();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    reloadActivityDeviceList(SelectDeviceActivity.this, ApplicationContext.getInstance().getDeviceManager(), uiHandler);
                } catch (DeviceException e) {
                    ApplicationContext.handleException(getClass(), e);
                }
            }
        }, 0, 1000);
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
        ApplicationContext.debug(getClass(), "Select device activity paused.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationContext.debug(getClass(), "Select device activity resumed.");
    }

    @NotNull
    private DeviceProvider getDeviceManager() {
        return ApplicationContext.getInstance().getDeviceManager();
    }

    private void reloadActivityDeviceList(Context context, DeviceProvider deviceProvider, Handler uiHandler) throws DeviceException {
        // receive list of connected devices
        final List<DeviceDescriptor> connectedDevices = deviceProvider.getConnectedDevices(context);

        // update activity's device descriptors according to reloaded list
        reloadDeviceDescriptors(connectedDevices);

        // update device list on GUI
        reloadDeviceListInGui(uiHandler);
    }

    private void reloadDeviceListInGui(Handler uiHandler) {
        deviceIdentifiers.clear();
        deviceIdentifiers.addAll(deviceDescriptors.keySet());

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                devicesViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void reloadDeviceDescriptors(List<DeviceDescriptor> connectedDevices) throws DeviceException {
        deviceDescriptors.clear();
        for (DeviceDescriptor connectedDevice : connectedDevices) {
            deviceDescriptors.put(connectedDevice.getDeviceIdentifier(), connectedDevice);
        }
    }

    private class DeviceSelectedListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final String item = (String) adapterView.getItemAtPosition(i);

            ApplicationContext.debug(getClass(), "Device '" + item + "' selected.");

            final DeviceProvider driverManager = getDeviceManager();
            final DeviceDescriptor deviceDescriptor = deviceDescriptors.get(item);

            try {
                final Device device = driverManager.openDevice(deviceDescriptor, SelectDeviceActivity.this);

                ApplicationContext.registerDevice(device);

                ApplicationContext.debug(getClass(), "Device connected.");
                ApplicationContext.debug(getClass(), "Forwarding activity controller activity.");
                startActivity(new Intent(SelectDeviceActivity.this.getBaseContext(), MeasurementActivity.class));
            } catch (DeviceException e) {
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }
}
