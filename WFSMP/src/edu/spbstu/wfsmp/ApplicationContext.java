package edu.spbstu.wfsmp;

import android.util.Log;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceProvider;
import edu.spbstu.wfsmp.driver.j2xx.D2xxDeviceProvider;
import edu.spbstu.wfsmp.sensor.DeviceController;
import edu.spbstu.wfsmp.sensor.DeviceControllerImpl;
import edu.spbstu.wfsmp.sensor.SensorException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 17:47
 */
public class ApplicationContext {

    @NotNull
    private static final ApplicationContext singleton = new ApplicationContext();

    @NotNull
    private final Map<String, Object> properties = new HashMap<String, Object>();

    private ApplicationContext() {}

    @NotNull
    public static ApplicationContext getInstance() {
        return singleton;
    }

    public static void registerDevice(@NotNull Device device) {
        // create controller for connected device
        final DeviceController deviceController = new DeviceControllerImpl(device);

        // register controller in application context
        getInstance().set(ApplicationProperties.CONNECTED_DEVICE, device);
        getInstance().set(ApplicationProperties.DEVICE_CONTROLLER, deviceController);
    }

    public static void unregisterDevice() {
        final Device device = (Device) getInstance().get(ApplicationProperties.CONNECTED_DEVICE);

        try {
            if (device != null) {
                device.close();
            }
        } catch (IOException e) {
            Log.e(ApplicationContext.class.getName(), e.getMessage(), e);
        }

        getInstance().remove(ApplicationProperties.DEVICE_CONTROLLER);
        getInstance().remove(ApplicationProperties.CONNECTED_DEVICE);
    }

    @Nullable
    public Object get(@NotNull String key) {
        return properties.get(key);
    }

    public void set(@NotNull String key, @Nullable Object value) {
        properties.put(key, value);
    }

    public void remove(@NotNull String key) {
        properties.remove(key);
    }

    public boolean contains(@NotNull String key) {
        return properties.containsKey(key);
    }

    public void clear() {
        properties.clear();
    }

    public DeviceProvider getDeviceManager() {
        return D2xxDeviceProvider.getInstance();
    }

    @NotNull
    public DeviceController getDeviceController() throws SensorException {
        final DeviceController dc = (DeviceController) ApplicationContext.getInstance().get(ApplicationProperties.DEVICE_CONTROLLER);

        if (dc == null) {
            throw new SensorException("Device controller not initialized.");
        }

        return dc;
    }

    public boolean isConnected() {
        return contains(ApplicationProperties.DEVICE_CONTROLLER);
    }

    public static void handleException(Class clazz, Throwable e) {
        Log.e(clazz.getName(), e.getMessage(), e);
    }

    public static void debug(Class<?> clazz, String msg) {
        Log.i(clazz.getName(), msg);
    }

    public static void warn(Class<?> clazz, String msg) {
        Log.i(clazz.getName(), msg);
    }

    public static void error(Class<?> clazz, String msg) {
        Log.e(clazz.getName(), msg);
    }

}
