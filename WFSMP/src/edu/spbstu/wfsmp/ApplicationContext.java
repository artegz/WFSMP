package edu.spbstu.wfsmp;

import android.util.Log;
import edu.spbstu.wfsmp.driver.DeviceProvider;
import edu.spbstu.wfsmp.driver.j2xx.D2xxDeviceProvider;
import edu.spbstu.wfsmp.driver.mock.MockDeviceProvider;
import edu.spbstu.wfsmp.sensor.DeviceController;
import edu.spbstu.wfsmp.sensor.DeviceControllerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 17:47
 */
public class ApplicationContext {

    public final boolean mockDevice = false;

    @NotNull
    private static final ApplicationContext singleton = new ApplicationContext();

    @NotNull
    private final Map<String, Object> properties = new HashMap<String, Object>();

    private ApplicationContext() {}

    @NotNull
    public static ApplicationContext getInstance() {
        return singleton;
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
        if (mockDevice) {
            return MockDeviceProvider.getInstance();
        } else {
            // return D2xxDeviceManager.getInstance();
            return D2xxDeviceProvider.getInstance();
        }
    }

    @NotNull
    public DeviceController getDeviceController() {
        final DeviceController dc = (DeviceController) ApplicationContext.getInstance().get(ApplicationProperties.DEVICE_CONTROLLER);

        if (dc == null) {
            throw new IllegalStateException("Device controller not initialized.");
        }

        return dc;
    }

    public static void handleException(Class clazz, Throwable e) {
        Log.e(clazz.getName(), e.getMessage(), e);
    }

    public static void debug(Class<?> clazz, String msg) {
        Log.i(clazz.getName(), msg);
    }

    public static void error(Class<?> clazz, String msg) {
        Log.e(clazz.getName(), msg);
    }

}
