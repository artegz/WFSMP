package edu.spbstu.wfsmp.driver;

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
public interface DeviceManager {

    @NotNull
    List<DeviceDescriptor> getConnectedDevices() throws DeviceException;

    @NotNull
    Device openDevice(@NotNull DeviceDescriptor descriptor) throws DeviceException;

    @NotNull
    Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Map<String, Object> config) throws DeviceException;

    void releaseDevice(@NotNull Device device) throws DeviceException;
}
