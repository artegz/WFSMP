package edu.spbstu.wfsmp.driver.mock;

import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceManager;
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
public class MockDeviceManager implements DeviceManager {

    private static final int NUM_DEVICES = 3;

    @NotNull
    private static DeviceManager instance = new MockDeviceManager();

    @NotNull
    private final Map<DeviceDescriptor, Device> devices = new HashMap<DeviceDescriptor, Device>();

    private MockDeviceManager() {
        for (int i = 0; i < NUM_DEVICES; i++) {
            devices.put(new MockDeviceDescriptor(i), new MockDevice());
        }
    }

    @NotNull
    public static DeviceManager getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public List<DeviceDescriptor> getConnectedDevices() throws DeviceException {
        return new ArrayList<DeviceDescriptor>(devices.keySet());
    }

    @NotNull
    @Override
    public Device openDevice(@NotNull DeviceDescriptor descriptor) throws DeviceException {
        return devices.get(descriptor);
    }

    @NotNull
    @Override
    public Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Map<String, Object> config) throws DeviceException {
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
    }
}
