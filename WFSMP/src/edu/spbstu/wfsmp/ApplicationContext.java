package edu.spbstu.wfsmp;

import android.util.Log;
import edu.spbstu.wfsmp.driver.DeviceManager;
import edu.spbstu.wfsmp.driver.d2xx.D2xxDeviceManager;
import edu.spbstu.wfsmp.driver.mock.MockDeviceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 17:47
 */
public class ApplicationContext {

    private final boolean mockDevice = false;

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

    public DeviceManager getDeviceManager() {
        if (mockDevice) {
            return MockDeviceManager.getInstance();
        } else {
            return D2xxDeviceManager.getInstance();
        }
    }

    public static void handleException(Class clazz, Throwable e) {
        Log.e(clazz.getName(), e.getMessage(), e);
    }

    public static void debug(Class<?> clazz, String msg) {
        Log.d(clazz.getName(), msg);
    }

}
