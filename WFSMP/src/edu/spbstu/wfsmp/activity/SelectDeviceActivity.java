package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.ApplicationProperties;
import edu.spbstu.wfsmp.activity.handlers.DisconnectListener;
import edu.spbstu.wfsmp.activity.handlers.ForwardListener;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceProvider;
import edu.spbstu.wfsmp.sensor.DeviceController;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:04
 */
public class SelectDeviceActivity extends Activity {

    @NotNull
    private static final String ACTION_USB_PERMISSION = "edu.spbstu.wfsmp.USB_PERMISSION";

    @NotNull
    private final List<String> deviceIdentifiers = new ArrayList<String>();

    // key: device identifier, value: device descriptor
    @NotNull
    private final Map<String, DeviceDescriptor> deviceDescriptors = new HashMap<String, DeviceDescriptor>();

    @NotNull
    private ArrayAdapter<String> devicesViewAdapter;

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

        // register usb broad cast receivers to handle usb device events
        registerUsbBroadcastReceivers();

        // fill devices list with already connected devices
        initDevicesList();

        ApplicationContext.debug(getClass(), "Select device activity successfully initialized.");
    }

    private void initDevicesList() {
        ApplicationContext.debug(getClass(), "Initialising devices list.");
        final UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        final HashMap<String, UsbDevice> connectedDevices = manager.getDeviceList();

        // get all connected devices and check permissions for them; if granted => appropriate broadcast receiver should be invoked
        for (UsbDevice usbDevice : connectedDevices.values()) {
            requestDevicePermission(this, usbDevice);
        }
    }

    private void registerUsbBroadcastReceivers() {
        ApplicationContext.debug(getClass(), "Registering usb broadcast receiver.");

        // register usb device attached event receiver
        registerReceiver(new UsbDeviceAttachedBroadcastReceiver(), new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));

        // register usb device detached event receiver
        registerReceiver(new UsbDeviceDetachedBroadcastReceiver(), new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        // register usb device permission received receiver
        registerReceiver(new UsbDevicePermissionGrantedBroadcastReceiver(), new IntentFilter(ACTION_USB_PERMISSION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void reloadActivityDeviceList(Context context, DeviceProvider deviceProvider) throws DeviceException {
        ApplicationContext.debug(getClass(), "Access to USB device was granted.");

        // reload list of connected devices
        ApplicationContext.debug(getClass(), "Reloading list of connected devices.");
        deviceProvider.reloadDeviceList(context);

        // receive list of connected devices
        final List<DeviceDescriptor> connectedDevices = deviceProvider.getConnectedDevices(context);

        // update activity's device descriptors according to reloaded list
        reloadDeviceDescriptors(connectedDevices);

        // update device list on GUI
        ApplicationContext.debug(getClass(), "Updating list of connected devices on GUI.");
        reloadDeviceListInGui();
    }

    private boolean isUsbPermissionGranted(Intent intent) {
        boolean permissionGranted;

        synchronized (this) {
            final UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                if(device != null){
                    permissionGranted = true;
                } else {
                    throw new AssertionError("Unexpected case.");
                }
            } else {
                permissionGranted = false;
            }
        }

        return permissionGranted;
    }

    private void reloadDeviceListInGui() {
        deviceIdentifiers.clear();
        deviceIdentifiers.addAll(deviceDescriptors.keySet());

        final Handler uiHandler = new Handler();

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

    private void requestDevicePermission(Context context, UsbDevice device) {
        ApplicationContext.debug(getClass(), "Requesting appropriate permissions.");
        final UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        final PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        // request permission to use usb device
        manager.requestPermission(device, permissionIntent);
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

                // create controller for connected device
                final DeviceController deviceController = new DeviceController(device);

                // register controller in application context
                ApplicationContext.getInstance().set(ApplicationProperties.CONNECTED_DEVICE, device);
                ApplicationContext.getInstance().set(ApplicationProperties.DEVICE_CONTROLLER, deviceController);

                ApplicationContext.debug(getClass(), "Device connected.");
                ApplicationContext.debug(getClass(), "Forwarding activity controller activity.");
                startActivity(new Intent(SelectDeviceActivity.this.getBaseContext(), MenuActivity.class));
            } catch (DeviceException e) {
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }

    private class UsbDeviceAttachedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                ApplicationContext.debug(getClass(), "Device attached.");

                final UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                requestDevicePermission(context, device);
            } else {
                ApplicationContext.debug(getClass(), "Unexpected action received: " + action);
                throw new AssertionError("Unexpected action received: " + action);
            }
        }
    }

    private class UsbDeviceDetachedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                try {
                    reloadActivityDeviceList(context, ApplicationContext.getInstance().getDeviceManager());

                    DisconnectListener.disconnect();
                    new ForwardListener(SelectDeviceActivity.class, SelectDeviceActivity.this).onClick(null);
                } catch (DeviceException e) {
                    ApplicationContext.handleException(getClass(), e);
                }
            } else {
                ApplicationContext.debug(getClass(), "Unexpected action received: " + action);
                throw new AssertionError("Unexpected action received: " + action);
            }
        }
    }

    private class UsbDevicePermissionGrantedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                // check permission and reload list
                try {
                    boolean permissionGranted = isUsbPermissionGranted(intent);

                    if (permissionGranted) {
                        reloadActivityDeviceList(context, ApplicationContext.getInstance().getDeviceManager());
                    } else {
                        ApplicationContext.debug(SelectDeviceActivity.this.getClass(), "Access to USB device was denied.");
                    }
                } catch (DeviceException e) {
                    ApplicationContext.handleException(getClass(), e);
                }
            } else {
                ApplicationContext.debug(getClass(), "Unexpected action received: " + action);
                throw new AssertionError("Unexpected action received: " + action);
            }
        }
    }
}
