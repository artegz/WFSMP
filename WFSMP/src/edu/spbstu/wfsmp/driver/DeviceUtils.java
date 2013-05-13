package edu.spbstu.wfsmp.driver;

import android.util.Log;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.sensor.ProtocolUtils;
import edu.spbstu.wfsmp.sensor.SensorException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:05
 */
public class DeviceUtils {
    
    public static final String TAG = DeviceUtils.class.getName();
    public static final int READ_BLOCK_TIMEOUT = 25;

    @Nullable
    public static String send(@NotNull String requestString, @NotNull Device device, boolean responseExpected)
            throws SensorException, IOException {
        return send0(requestString, device, responseExpected);
    }

    @Nullable
    private static String send0(@NotNull String commandString, @NotNull Device device, boolean responseExpected) throws SensorException, IOException {
        ApplicationContext.debug(DeviceUtils.class, "Sending command '" + commandString + "'.");

        // send command
        sendCommand(commandString, device);

        ApplicationContext.debug(DeviceUtils.class, "Command sent. Waiting for response...");

        final String response;

        if (responseExpected) {
            // receive response
            response = receiveCommand0(device);

            ApplicationContext.debug(DeviceUtils.class, "Response command received: " + response);
        } else {
            // wait 100ms to allow device to process request
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }

            if (isResponseAvailable(device)) {
                // response received, lets read it and return
                response = receiveCommand0(device);
            } else {
                response = null;
            }
        }

        return response;
    }

    @NotNull
    private static String receiveCommand0(@NotNull Device device) throws IOException, SensorException {
        // read raw command from input stream
        //noinspection UnnecessaryLocalVariable
        final String receivedRawCommand = readRawCommand(device);
        return receivedRawCommand;
    }

    private static void sendCommand(@NotNull String command, @NotNull Device device) throws SensorException {

        logBytes(command);

        try {
            // send request
            final byte[] bytes = command.getBytes(ProtocolUtils.PROTOCOL_COMMAND_ENCODING);
            device.write(bytes, bytes.length);

            /*outputStreamWriter.append(command);
            outputStreamWriter.flush();*/
        } catch (IOException e) {
            throw new SensorException(e);
        }
    }

    @NotNull
    private static String readRawCommand(@NotNull Device device) throws IOException, SensorException {
        final StringBuilder sb = new StringBuilder();

        final char prefix = readChar(device);

        if (prefix != ProtocolUtils.RESPONSE_PREFIX) {
            ApplicationContext.error(DeviceUtils.class, "Command has invalid prefix. Expected: '" + ProtocolUtils.RESPONSE_PREFIX + "'. Received: '" + prefix + "'.");
            throw new SensorException("Command has invalid prefix. Expected: '" + ProtocolUtils.RESPONSE_PREFIX + "'. Received: '" + prefix + "'." );
        }

        // append prefix
        sb.append(prefix);

        // read until the command is complete
        while (! sb.toString().endsWith(ProtocolUtils.COMMAND_POSTFIX)) {
            sb.append(readChar(device));
        }

        final String completeCommand = sb.toString();

        // return command with catted ending chars
        return completeCommand.substring(0, completeCommand.length() - ProtocolUtils.COMMAND_POSTFIX.length());
    }

    private static char readChar(@NotNull Device device) throws IOException {
        byte b = readByte(device, true);
        /*int b = streamReader.read();
        assert (b != -1) : "Unexpected end of stream has been reached.";
        if (b < 0) {
            throw new AssertionError("Unexpected end of stream has been reached.");
        }*/
        Log.d(TAG, "Char received: " + (char) b + " (" + b + ")" );
        return (char) b;
    }

    private static boolean isResponseAvailable(@NotNull Device device) throws IOException {
        return device.getQueueStatus() > 0;
    }

    private static void logBytes(@NotNull String str) {
        try {
            final byte[] bytes = str.getBytes("UTF-8");
            final StringBuilder sb = new StringBuilder();

            for (byte aByte : bytes) {
                sb.append(Integer.toHexString(aByte)).append(" ");
            }

            Log.d(TAG, "Sending bytes: " + sb.toString() + ".");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static byte readByte(Device device, boolean waitData) throws IOException {
        final byte[] buffer = new byte[1];
        final int bytesRead = readBytes(buffer, 0, 1, device, waitData);

        if (bytesRead < 1) {
            throw new AssertionError("Expected at least 1 byte to be read.");
        }

        return buffer[0];
    }

    public static int readBytes(byte[] buffer, int offset, int length, Device device, boolean waitData) throws IOException {
        int queueStatus = device.getQueueStatus();

        try {
            while (queueStatus < 1 && waitData) {
                // wait
                Thread.sleep(READ_BLOCK_TIMEOUT);
                // check if new data appears
                queueStatus = device.getQueueStatus();
            }
        } catch (InterruptedException e) {
            throw new InterruptedIOException(e.getMessage());
        }

        final int bytesToRead = Math.min(length, queueStatus);
        final byte[] tmpBuffer = new byte[bytesToRead];

        device.read(tmpBuffer, bytesToRead);

        // fill received buffer
        System.arraycopy(tmpBuffer, 0, buffer, offset, bytesToRead);

        return bytesToRead;
    }
}
