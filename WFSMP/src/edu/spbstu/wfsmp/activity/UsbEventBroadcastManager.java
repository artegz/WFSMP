package edu.spbstu.wfsmp.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.driver.DeviceException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * User: Artegz
 * Date: 18.05.13
 * Time: 15:05
 */
public class UsbEventBroadcastManager {

    private static UsbEventBroadcastManager instance;

    @NotNull
    private static final String ACTION_USB_PERMISSION = "edu.spbstu.wfsmp.USB_PERMISSION";

    private boolean initialized = false;

    private UsbEventBroadcastManager() {
    }

    public static UsbEventBroadcastManager getInstance() {
        if (instance == null) {
            synchronized (UsbEventBroadcastManager.class) {
                if (instance == null) {
                    instance = new UsbEventBroadcastManager();
                }
            }
        }

        return instance;
    }

    public synchronized void init(@NotNull Context context) throws DeviceException {
        if (!initialized) {
            // register usb broadcast receivers to handle usb device events
            registerUsbBroadcastReceivers(context);

            // request permission for already connected devices
            requestPermissionsForConnectedDevices(context);

            // reload list of connected devices
            reloadDeviceList(context);

            initialized = true;
        } else {
            // already initialized
        }
    }

    private static void reloadDeviceList(Context context) throws DeviceException {
        ApplicationContext.debug(SelectDeviceActivity.class, "Reloading list of connected devices.");
        ApplicationContext.getInstance().getDeviceManager().reloadDeviceList(context);
    }

    private void requestPermissionsForConnectedDevices(@NotNull Context context) {
        ApplicationContext.debug(getClass(), "Initialising devices list.");
        final UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        final Map<String, UsbDevice> connectedDevices = manager.getDeviceList();

        // get all connected devices and check permissions for them; if granted => appropriate broadcast receiver should be invoked
        for (UsbDevice usbDevice : connectedDevices.values()) {
            requestDevicePermission(context, usbDevice);
        }
    }

    private void registerUsbBroadcastReceivers(@NotNull Context context) {
        ApplicationContext.debug(getClass(), "Registering usb broadcast receiver.");

        // register usb device attached event receiver
        context.registerReceiver(new UsbDeviceAttachedBroadcastReceiver(), new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));

        // register usb device detached event receiver
        context.registerReceiver(new UsbDeviceDetachedBroadcastReceiver(), new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        // register usb device permission received receiver
        context.registerReceiver(new UsbDevicePermissionGrantedBroadcastReceiver(), new IntentFilter(ACTION_USB_PERMISSION));
    }

    private static boolean isUsbPermissionGranted(Intent intent) {
        boolean permissionGranted;

        synchronized (UsbEventBroadcastManager.class) {
            final UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                if (device != null) {
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

    private void requestDevicePermission(Context context, UsbDevice device) {
        ApplicationContext.debug(getClass(), "Requesting appropriate permissions.");
        final UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        final PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        // request permission to use usb device
        manager.requestPermission(device, permissionIntent);
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

    private static class UsbDeviceDetachedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                ActivityUtils.disconnect();
                ActivityUtils.forwardToActivity(SelectDeviceActivity.class, context);

                try {
                    reloadDeviceList(context);
                } catch (DeviceException e) {
                    ApplicationContext.handleException(getClass(), e);
                }
            } else {
                ApplicationContext.debug(getClass(), "Unexpected action received: " + action);
                throw new AssertionError("Unexpected action received: " + action);
            }
        }
    }

    private static class UsbDevicePermissionGrantedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                // check permission and reload list
                try {
                    boolean permissionGranted = isUsbPermissionGranted(intent);

                    if (permissionGranted) {
                        reloadDeviceList(context);
                    } else {
                        ApplicationContext.debug(getClass(), "Access to USB device was denied.");
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
