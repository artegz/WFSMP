package edu.spbstu.wfsmp.driver;

import android.content.Context;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * User: artegz
 * Date: 28.10.12
 * Time: 18:50
 */
public interface DeviceProvider {

    void reloadDeviceList(@NotNull Context parentContext) throws DeviceException;

    @NotNull
    List<DeviceDescriptor> getConnectedDevices(Context parentContext) throws DeviceException;

    @NotNull
    Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Context parentContext) throws DeviceException;

    @NotNull
    Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Map<String, Object> config, @NotNull Context parentContext) throws DeviceException;

    void releaseDevice(@NotNull Device device) throws DeviceException;
}
