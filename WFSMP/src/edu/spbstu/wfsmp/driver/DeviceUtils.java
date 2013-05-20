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

    public static final int READ_BYTE_PAUSE_MS = 25;
    public static final int READ_BYTE_TIMEOUT_MS = 500;

    @Nullable
    public static String send(@NotNull String requestString, @NotNull Device device, boolean responseExpected)
            throws DeviceException {
        try {
            return send0(requestString, device, responseExpected);
        } catch (IOException e) {
            throw new DeviceException(e);
        }
    }

    @Nullable
    private static String send0(@NotNull String commandString, @NotNull Device device, boolean responseExpected) throws IOException, DeviceException {
        ApplicationContext.debug(DeviceUtils.class, "Sending command '" + commandString + "'.");

        // send command
        sendCommand(commandString, device);

        ApplicationContext.debug(DeviceUtils.class, "Command sent. Waiting for response...");

        final String response;

        if (responseExpected) {
            // receive response
            response = receiveCommand0(device, ProtocolUtils.RESPONSE_PREFIX, ProtocolUtils.COMMAND_POSTFIX);

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
                response = receiveCommand0(device, ProtocolUtils.RESPONSE_PREFIX, ProtocolUtils.COMMAND_POSTFIX);
            } else {
                response = null;
            }
        }

        return response;
    }

    @NotNull
    private static String receiveCommand0(@NotNull Device device, char startToken, String endToken) throws IOException, DeviceException {
        // read raw command from input stream
        //noinspection UnnecessaryLocalVariable
        final String receivedRawCommand = readRawCommand(device, startToken, endToken);
        return receivedRawCommand;
    }

    private static void sendCommand(@NotNull String command, @NotNull Device device) throws DeviceException {

        logBytes(command);

        try {
            // send request
            final byte[] bytes = command.getBytes(ProtocolUtils.PROTOCOL_COMMAND_ENCODING);
            device.write(bytes, bytes.length);

            /*outputStreamWriter.append(command);
            outputStreamWriter.flush();*/
        } catch (IOException e) {
            throw new DeviceException(e);
        }
    }

    // todo: make startToken - String
    @NotNull
    private static String readRawCommand(@NotNull Device device, char startToken, @NotNull String endToken) throws IOException, DeviceException {
        final StringBuilder sb = new StringBuilder();

        final char prefix;

        try {
            prefix = readChar(device);
        } catch (DeviceTimeoutException e) {
            throw new DeviceException("Device not available, it's probably turned off.", e);
        }

        if (prefix != startToken) {
            ApplicationContext.error(DeviceUtils.class, "Invalid command start. Expected: '" + ProtocolUtils.RESPONSE_PREFIX + "'. Received: '" + prefix + "'.");
            throw new DeviceException("Invalid command start. Expected: '" + startToken + "'. Received: '" + prefix + "'." );
        }

        // append prefix
        sb.append(prefix);

        // read until the command is complete
        try {
            while (! sb.toString().endsWith(endToken)) {
                sb.append(readChar(device));
            }
        } catch (DeviceTimeoutException e) {
            throw new DeviceException("Response has not been completely received. Probably device has been turned off or responce is invalid.");
        }

        final String completeCommand = sb.toString();

        // return command with catted ending chars
        return completeCommand.substring(0, completeCommand.length() - ProtocolUtils.COMMAND_POSTFIX.length());
    }

    private static char readChar(@NotNull Device device) throws IOException, DeviceException {
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

    private static byte readByte(Device device, boolean waitData) throws IOException, DeviceException {
        final byte[] buffer = new byte[1];
        final int bytesRead = readBytes(buffer, 0, 1, device, waitData);

        if (bytesRead < 1) {
            throw new AssertionError("Expected at least 1 byte to be read.");
        }

        return buffer[0];
    }

    private static int readBytes(byte[] buffer, int offset, int length, Device device, boolean waitData) throws IOException, DeviceException {
        int queueStatus = device.getQueueStatus();

        try {
            final long startTime = System.currentTimeMillis();
            while (queueStatus < 1 && waitData) {
                // check if global timeout was expired
                final long currentTime = System.currentTimeMillis();
                if (currentTime - startTime > READ_BYTE_TIMEOUT_MS) {
                    throw new DeviceTimeoutException("The device is not responding.");
                }

                // wait
                Thread.sleep(READ_BYTE_PAUSE_MS);
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
