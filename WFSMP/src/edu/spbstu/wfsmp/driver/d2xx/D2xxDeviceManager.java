package edu.spbstu.wfsmp.driver.d2xx;

import android.util.Log;
import com.ftdi.D2xx;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 22:24
 */
public class D2xxDeviceManager implements DeviceManager {

    @NotNull
    private static final Logger logger = Logger.getAnonymousLogger();

    @NotNull
    private static final Map<String, Object> defaultConfiguration = new HashMap<String, Object>();

    static {
        // configure our port
        // reset activity UART mode for 232 devices
        defaultConfiguration.put(D2xxParameter.Param.BIT_MODE_MASK, 0);
        defaultConfiguration.put(D2xxParameter.Param.BIT_MODE_MODE, D2xx.FT_BITMODE_RESET);

        // set 9600 baud
        defaultConfiguration.put(D2xxParameter.Param.BAUD_RATE, 9600);

        // set 8 data bits, 1 stop bit, no parity
        defaultConfiguration.put(D2xxParameter.Param.DATA_DATA_BITS, D2xx.FT_DATA_BITS_8);
        defaultConfiguration.put(D2xxParameter.Param.DATA_PARITY_BITS, D2xx.FT_PARITY_NONE);
        defaultConfiguration.put(D2xxParameter.Param.DATA_STOP_BITS, D2xx.FT_STOP_BITS_1);

        // set no flow control
        defaultConfiguration.put(D2xxParameter.Param.FLOW_CONTROL, D2xx.FT_FLOW_NONE);
        defaultConfiguration.put(D2xxParameter.Param.FLOW_XON, (byte)0x11);
        defaultConfiguration.put(D2xxParameter.Param.FLOW_XOFF, (byte)0x13);

        // set latency timer activity 16ms
        defaultConfiguration.put(D2xxParameter.Param.LATENCY_TIMER, 16);

        // set a read timeout of 5s
        defaultConfiguration.put(D2xxParameter.Param.READ_TIMEOUT, 50);
        defaultConfiguration.put(D2xxParameter.Param.WRITE_TIMEOUT, 0);
    }

    // it need activity be a singleton activity take complete control over communication between API user and d2xx driver
    private static D2xxDeviceManager singleton;

    // make not instantiable activity implement singleton pattern
    private D2xxDeviceManager() {

        // todo asm: is it required
        // Specify a non-default VID and PID combination activity match if required
        /*try {
            setCustomVIDPID(0x0403, 0xada1);
        } catch (DeviceException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }*/
    }

    @NotNull
    public static D2xxDeviceManager getInstance() {
        if (singleton == null) {
            singleton = new D2xxDeviceManager();
        }

        return singleton;
    }

    @Override
    @NotNull
    public List<DeviceDescriptor> getConnectedDevices() throws DeviceException {
        try {
            Log.i(getClass().getName(), "Getting connected devices.");
            final int devCount = D2xx.createDeviceInfoList();

            Log.i(getClass().getName(), String.valueOf(devCount) + " devices found.");

            D2xx.FtDeviceInfoListNode[] deviceList = new D2xx.FtDeviceInfoListNode[devCount];
            D2xx.getDeviceInfoList(devCount, deviceList);
            Log.i(getClass().getName(), "Devices: " + Arrays.asList(deviceList).toString());
        } catch (D2xx.D2xxException e) {
            throw new DeviceException(e);
        }

        /*

            	try {
	            	// OK, write some data!
	            	// Get the data activity write from the edit text control
					String writeData = dataToWrite.getText().toString();
	            	byte[] OutData = writeData.getBytes();

					ftD2xx.write(OutData, writeData.length());


					// wait for data activity be sent
	            	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	// wait for 1 second



					int rxq = 0;
					int[] devStatus = null;
					devStatus = ftD2xx.getStatus();

					// Rx Queue status is in first element of the array
					rxq = devStatus[0];

					if (rxq > 0)
					{
						// read the data back!
		            	byte[] InData = new byte[rxq];
	            		ftD2xx.read(InData,rxq);

		            	myData.setText(new String(InData));
					}
					else
						myData.setText("");


	            	// close the port
	            	ftD2xx.close();

            	}
            	catch (D2xxException e) {
            		String s = e.getMessage();
        			if (s != null) {
        				myData.setText(s);
        			}
            	}
         */

        return Collections.emptyList();
        // todo
    }

    /**
     * Establish connection with specified device.
     * @param descriptor device descriptor
     * @return connected device
     * @throws edu.spbstu.wfsmp.driver.DeviceException will be thrown in case if another device already connected
     */
    @Override
    @NotNull
    public Device openDevice(@NotNull DeviceDescriptor descriptor) throws DeviceException {
        return openDevice(descriptor, new HashMap<String, Object>());
    }

    @Override
    @NotNull
    public Device openDevice(@NotNull DeviceDescriptor descriptor, @NotNull Map<String, Object> config) throws DeviceException {
        if (! (descriptor instanceof D2xxDeviceDescriptor)) {
            throw new IllegalArgumentException("Wrong descriptor.");
        }

        // create a D2xx object
        final D2xx ftD2xx = new D2xx();

        try {
            // open our first device
            ftD2xx.openByIndex(descriptor.getIndex());

            configure(ftD2xx, getAppliedConfig(config, defaultConfiguration));

            // purge buffers
            ftD2xx.purge((byte) (D2xx.FT_PURGE_TX | D2xx.FT_PURGE_RX));
        } catch (D2xx.D2xxException e) {
            throw new DeviceException(e);
        }

        return new D2xxDevice(ftD2xx);
    }

    @NotNull
    private Map<String, Object> getAppliedConfig(@NotNull Map<String, Object> appliedConfig, @NotNull Map<String, Object> baseConfig) {
        final HashMap<String, Object> mergedConfig = new HashMap<String, Object>(baseConfig);

        for (Map.Entry<String, Object> entry : appliedConfig.entrySet()) {
            mergedConfig.put(entry.getKey(), entry.getValue());
        }

        return mergedConfig;
    }


    private void configure(@NotNull D2xx ftD2xx, @NotNull Map<String, Object> configuration) throws DeviceException {
        try {
            ftD2xx.setBitMode((Byte) configuration.get(D2xxParameter.Param.BIT_MODE_MASK), (Byte) configuration.get(D2xxParameter.Param.BIT_MODE_MODE));
            ftD2xx.setBaudRate((Integer) configuration.get(D2xxParameter.Param.BAUD_RATE));
            ftD2xx.setDataCharacteristics((Byte) configuration.get(D2xxParameter.Param.DATA_DATA_BITS),
                    (Byte) configuration.get(D2xxParameter.Param.DATA_STOP_BITS), (Byte) configuration.get(D2xxParameter.Param.DATA_PARITY_BITS));
            ftD2xx.setFlowControl((Short) configuration.get(D2xxParameter.Param.FLOW_CONTROL),
                    (Byte) configuration.get(D2xxParameter.Param.FLOW_XON), (Byte) configuration.get(D2xxParameter.Param.FLOW_XOFF));
            ftD2xx.setLatencyTimer((Byte) configuration.get(D2xxParameter.Param.LATENCY_TIMER));
            ftD2xx.setTimeouts((Integer) configuration.get(D2xxParameter.Param.READ_TIMEOUT), (Integer) configuration.get(D2xxParameter.Param.READ_TIMEOUT));
        } catch (D2xx.D2xxException e) {
            throw new DeviceException(e);
        }
    }

    @Override
    public void releaseDevice(@NotNull Device device) throws DeviceException {
        if (! (device instanceof D2xxDevice)) {
            throw new IllegalArgumentException("Unknown device.");
        }

        try {
            device.close();
        } catch (IOException e) {
            throw new DeviceException(e);
        }
    }

    public void setCustomVIDPID(int vid, int pid) throws DeviceException {
        try {
            D2xx.setVIDPID(vid, pid);
        } catch (D2xx.D2xxException e) {
            throw new DeviceException(e);
        }
    }

    private static String getDeviceTypeName(D2xx.FtDeviceInfoListNode node) {
        final String result;

        switch (node.type) {
            case D2xx.FT_DEVICE_8U232AM:
                result = "FT8U232AM device";
                break;

            case D2xx.FT_DEVICE_UNKNOWN:
                result = "Unknown device";
                break;

            case D2xx.FT_DEVICE_2232:
                result = "FT2232 device";
                break;

            case D2xx.FT_DEVICE_232R:
                result = "FT232R device";
                break;

            case D2xx.FT_DEVICE_2232H:
                result = "FT2232H device";
                break;

            case D2xx.FT_DEVICE_4232H:
                result = "FT4232H device";
                break;

            case D2xx.FT_DEVICE_232H:
                result = "FT232H device";
                break;

            case D2xx.FT_DEVICE_232B:
            default:
                result = "FT232B device";
                break;

        }

        return result;
    }

}
