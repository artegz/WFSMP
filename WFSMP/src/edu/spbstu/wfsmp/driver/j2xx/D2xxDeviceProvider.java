package edu.spbstu.wfsmp.driver.j2xx;

import android.content.Context;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 22:24
 */
public class D2xxDeviceProvider implements DeviceProvider {

    @NotNull
    private static final Map<String, Object> defaultConfiguration = new HashMap<String, Object>();

    // fill default config
    static {
        // configure our port
        // reset to UART mode for 232 devices
        defaultConfiguration.put(D2xxDeviceParam.BIT_MODE_MASK, (byte) 0);
        defaultConfiguration.put(D2xxDeviceParam.BIT_MODE_MODE, D2xxManager.FT_BITMODE_RESET);

        // set 9600 baud
        defaultConfiguration.put(D2xxDeviceParam.BAUD_RATE, 9600);

        // set 8 data bits, 1 stop bit, no parity
        defaultConfiguration.put(D2xxDeviceParam.DATA_DATA_BITS, D2xxManager.FT_DATA_BITS_8);
        defaultConfiguration.put(D2xxDeviceParam.DATA_PARITY_BITS, D2xxManager.FT_PARITY_NONE);
        defaultConfiguration.put(D2xxDeviceParam.DATA_STOP_BITS, D2xxManager.FT_STOP_BITS_1);

        // set no flow control
        defaultConfiguration.put(D2xxDeviceParam.FLOW_CONTROL, D2xxManager.FT_FLOW_NONE);
        defaultConfiguration.put(D2xxDeviceParam.FLOW_XON, (byte)0x11);
        defaultConfiguration.put(D2xxDeviceParam.FLOW_XOFF, (byte)0x13);

        // set latency timer to 16ms
        defaultConfiguration.put(D2xxDeviceParam.LATENCY_TIMER, (byte)16);

        // set a read timeout of 5s
        /*defaultConfiguration.put(D2xxParameter.Param.READ_TIMEOUT, 50);
        defaultConfiguration.put(D2xxParameter.Param.WRITE_TIMEOUT, 0);*/
    }

    // it need to be a singleton to take complete control over communication between API user and d2xx driver
    private static D2xxDeviceProvider singleton;

    @NotNull
    private static List<D2xxFTDeviceDescriptor> j2xxConnectedDevices = new ArrayList<D2xxFTDeviceDescriptor>();

    // make not instantiable to implement singleton pattern
    private D2xxDeviceProvider() {
        ApplicationContext.debug(getClass(), "D2xxDeviceProvider initialised.");
    }

    @NotNull
    public static D2xxDeviceProvider getInstance() {
        if (singleton == null) {
            singleton = new D2xxDeviceProvider();
        }

        return singleton;
    }

    @Override
    public void reloadDeviceList(@NotNull Context parentContext) throws DeviceException {
        try {
            // cleanup previously detected devices from list
            j2xxConnectedDevices.clear();

            // get d2xx native manager
            ApplicationContext.debug(getClass(), "Getting device manager.");
            final D2xxManager d2xxManager = D2xxManager.getInstance(parentContext);

            // prepare list of devices in driver
            ApplicationContext.debug(getClass(), "Create device info list.");
            final int devCount = d2xxManager.createDeviceInfoList(parentContext);
            ApplicationContext.debug(getClass(), String.valueOf(devCount) + " devices found.");

            // get connected devices from driver
            ApplicationContext.debug(getClass(), "Fill devices list.");
            final D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
            d2xxManager.getDeviceInfoList(devCount, deviceList);

            ApplicationContext.debug(getClass(), "Devices found: " + Arrays.asList(deviceList).toString());

            // reload list
            for (D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode : deviceList) {
                j2xxConnectedDevices.add(new D2xxFTDeviceDescriptor(ftDeviceInfoListNode));
            }
            ApplicationContext.debug(getClass(), "List of connected devices completely reloaded.");
        } catch (D2xxManager.D2xxException e) {
            throw new DeviceException(e);
        }
    }

    @Override
    @NotNull
    public List<DeviceDescriptor> getConnectedDevices(Context parentContext) throws DeviceException {
        return new ArrayList<DeviceDescriptor>(j2xxConnectedDevices);
    }

    /**
     * Establish connection with specified device.
     * @param descriptor device descriptor
     * @return connected device
     * @throws edu.spbstu.wfsmp.driver.DeviceException will be thrown in case if another device already connected
     */
    @Override
    @NotNull
    public Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Context context) throws DeviceException {
        return openDevice(descriptor, new HashMap<String, Object>(), context);
    }

    @Override
    @NotNull
    public Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Map<String, Object> config, @NotNull Context context) throws DeviceException {
        if (! (descriptor instanceof D2xxFTDeviceDescriptor)) {
            throw new IllegalArgumentException("Invalid descriptor.");
        }

        D2xxFTDevice j2xxDevice;

        ApplicationContext.debug(getClass(), "Opening device.");
        try {
            // create a D2xx object
            final D2xxManager d2xxManager = D2xxManager.getInstance(context);

            // open device using descriptor
            final FT_Device device = d2xxManager.openByIndex(context, descriptor.getIndex());
            ApplicationContext.debug(getClass(), "Device opened.");

            // configure device
            ApplicationContext.debug(getClass(), "Configuring device.");
            configure(device, mergeConfig(config, defaultConfiguration));

            // purge buffers
            device.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
            ApplicationContext.debug(getClass(), "Configuring configured.");

            // wrap
            j2xxDevice = new D2xxFTDevice(device);
        } catch (D2xxManager.D2xxException e) {
            throw new DeviceException(e);
        }

        return j2xxDevice;
    }

    @NotNull
    private static Map<String, Object> mergeConfig(@NotNull Map<String, Object> appliedConfig, @NotNull Map<String, Object> baseConfig) {
        final HashMap<String, Object> mergedConfig = new HashMap<String, Object>(baseConfig);

        for (Map.Entry<String, Object> entry : appliedConfig.entrySet()) {
            mergedConfig.put(entry.getKey(), entry.getValue());
        }

        return mergedConfig;
    }

    private void configure(@NotNull FT_Device ftDevice, @NotNull Map<String, Object> configuration) throws DeviceException {
        final Byte bitMode = (Byte) configuration.get(D2xxDeviceParam.BIT_MODE_MODE);
        final Integer baudRate = (Integer) configuration.get(D2xxDeviceParam.BAUD_RATE);
        final Byte dataBits = (Byte) configuration.get(D2xxDeviceParam.DATA_DATA_BITS);
        final Byte stopBits = (Byte) configuration.get(D2xxDeviceParam.DATA_STOP_BITS);
        final Byte parity = (Byte) configuration.get(D2xxDeviceParam.DATA_PARITY_BITS);
        final Short flowControl = (Short) configuration.get(D2xxDeviceParam.FLOW_CONTROL);
        final Byte xon = (Byte) configuration.get(D2xxDeviceParam.FLOW_XON);
        final Byte xoff = (Byte) configuration.get(D2xxDeviceParam.FLOW_XOFF);
        final Byte latency = (Byte) configuration.get(D2xxDeviceParam.LATENCY_TIMER);
        final Integer readTimeout = (Integer) configuration.get(D2xxDeviceParam.READ_TIMEOUT);
        final Integer writeTimeout = (Integer) configuration.get(D2xxDeviceParam.WRITE_TIMEOUT);

        if (bitMode != null) {
            ftDevice.setBitMode((Byte) configuration.get(D2xxDeviceParam.BIT_MODE_MASK), bitMode);
        }
        if (baudRate != null) {
            ftDevice.setBaudRate(baudRate);
        }
        if (dataBits != null && stopBits != null && parity != null) {
            ftDevice.setDataCharacteristics(dataBits, stopBits, parity);
        }
        if (flowControl != null && xon != null && xoff != null) {
            ftDevice.setFlowControl(flowControl, xon, xoff);
        }
        if (latency != null) {
            ftDevice.setLatencyTimer(latency);
        }
        if (readTimeout != null && writeTimeout != null) {
            throw new UnsupportedOperationException("Setting timeouts is not supported.");
        }
    }

    @Override
    public void releaseDevice(@NotNull Device device) throws DeviceException {
        if (! (device instanceof D2xxFTDevice)) {
            throw new IllegalArgumentException("Unknown device.");
        }

        try {
            device.close();
        } catch (IOException e) {
            throw new DeviceException(e);
        }
    }

    static String getDeviceTypeName(@NotNull D2xxManager.FtDeviceInfoListNode node) {
        String result;

        switch (node.type) {
            case D2xxManager.FT_DEVICE_8U232AM:
                result = "FT8U232AM device";
                break;

            case D2xxManager.FT_DEVICE_UNKNOWN:
                result = "Unknown device";
                break;

            case D2xxManager.FT_DEVICE_2232:
                result = "FT2232 device";
                break;

            case D2xxManager.FT_DEVICE_232R:
                result = "FT232R device";
                break;

            case D2xxManager.FT_DEVICE_2232H:
                result = "FT2232H device";
                break;

            case D2xxManager.FT_DEVICE_4232H:
                result = "FT4232H device";
                break;

            case D2xxManager.FT_DEVICE_232H:
                result = "FT232H device";
                break;

            case D2xxManager.FT_DEVICE_232B:
            default:
                result = "FT232B device";
                break;

        }

        return result;
    }

}
