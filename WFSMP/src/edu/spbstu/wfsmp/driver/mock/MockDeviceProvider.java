package edu.spbstu.wfsmp.driver.mock;

import android.content.Context;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: artegz
 * Date: 28.10.12
 * Time: 18:51
 */
public class MockDeviceProvider implements DeviceProvider {

    private static final int NUM_DEVICES = 3;

    @NotNull
    private static DeviceProvider instance = new MockDeviceProvider();

    @NotNull
    private final Map<DeviceDescriptor, Device> devices = new HashMap<DeviceDescriptor, Device>();

    private MockDeviceProvider() {
        for (int i = 0; i < NUM_DEVICES; i++) {
            devices.put(new MockDeviceDescriptor(i), new MockDevice());
        }
    }

    @NotNull
    public static DeviceProvider getInstance() {
        return instance;
    }

    @Override
    public void reloadDeviceList(@NotNull Context parentContext) {
        // do nothing
    }

    @NotNull
    @Override
    public List<DeviceDescriptor> getConnectedDevices(Context parentContext) throws DeviceException {
        return new ArrayList<DeviceDescriptor>(devices.keySet());
    }

    @NotNull
    @Override
    public Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Context parentContext) throws DeviceException {
        return devices.get(descriptor);
    }

    @NotNull
    @Override
    public Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Map<String, Object> config, @NotNull Context parentContext) throws DeviceException {
        // config not supported
        return devices.get(descriptor);
    }

    @Override
    public void releaseDevice(@NotNull Device device) throws DeviceException {
        // do nothing
    }

    private static class MockDeviceDescriptor implements DeviceDescriptor {
        private final int index;

        private MockDeviceDescriptor(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String getDeviceIdentifier() {
            return String.valueOf(index);
        }
    }
}
