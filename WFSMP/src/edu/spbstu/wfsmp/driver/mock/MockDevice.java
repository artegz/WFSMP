package edu.spbstu.wfsmp.driver.mock;

import android.util.Log;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommandCodes;
import edu.spbstu.wfsmp.sensor.command.ProtocolResultCodes;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 18:30
 */
public class MockDevice implements Device {

    public static final String TAG = MockDevice.class.getName();

    @NotNull
    private DeviceProcessor deviceProcessor;

    public MockDevice() {
        this.deviceProcessor = new DeviceProcessor();
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Operation isn't supported yet.");
    }

    @Override
    public int read(byte[] data, int bytesToRead) throws IOException {
        return deviceProcessor.read(data, bytesToRead);
    }

    @Override
    public int write(byte[] data, int bytesToWrite) throws IOException {
        return deviceProcessor.write(data, bytesToWrite);
    }

    @Override
    public int getQueueStatus() throws IOException {
        return deviceProcessor.querySize();
    }

    @Override
    public void purgeRx() throws IOException {
        // do nothing, write performed immediately
    }

    @Override
    public void purgeTx() throws IOException {
        // do nothing, read performed immediately
    }

    @Override
    public void resetDevice() throws IOException {
        throw new UnsupportedOperationException("Operation not supported.");
    }

    private static class DeviceProcessor {

        @NotNull
        private Queue<String> outputBuffer = new LinkedList<String>();

        public int querySize() {
            // as stream's available() method doesn't offer return real stream size
            // - lets just return size of first available command
            return outputBuffer.size() > 0 ? outputBuffer.peek().length() : 0;
        }

        public int write(byte[] data, int bytesToWrite) throws IOException {
            final InputStreamReader inputStreamReader = new InputStreamReader(
                    new ByteArrayInputStream(data), ProtocolCommandCodes.PROTOCOL_COMMAND_ENCODING);

            assert (outputBuffer.size() == 0) : "Previous response wasn't received.";

            final String request = readCommand(inputStreamReader);
            Log.i(TAG, "Command '" + request + "' received.");
            outputBuffer.add(processRequest(request));

            return bytesToWrite;
        }

        public int read(byte[] data, int bytesToRead) throws IOException {
            // todo asm bloking may be required
            final String response = outputBuffer.poll();
            assert (response != null) : "Response is null.";
            final byte[] responseBytes = response.getBytes(ProtocolCommandCodes.PROTOCOL_COMMAND_ENCODING);

            System.arraycopy(responseBytes, 0, data, 0, responseBytes.length);

            return responseBytes.length;
        }

        @NotNull
        public String processRequest(@NotNull String request) {
            final ProtocolCommand command = ProtocolCommand.parseCommand(request);
            final String commandId = command.getCommandCode();
            final ProtocolCommand result;

            if (commandId.equals(ProtocolCommandCodes.REQUEST_START)) {
                final Object[] commandParams = command.getParams();

                if (commandParams != null && commandParams.length == 2) {
                    result = new ProtocolCommand(ProtocolCommandCodes.RESPONSE_OK);
                } else {
                    result = new ProtocolCommand(ProtocolCommandCodes.RESPONSE_NOK, ProtocolResultCodes.WRONG_PARAMS_NUMBER);
                }
            } else if (commandId.endsWith(ProtocolCommandCodes.REQUEST_STOP)) {
                result = new ProtocolCommand(ProtocolCommandCodes.RESPONSE_OK);
            } else {
                throw new UnsupportedOperationException("Command '" + commandId + "' isn't supported yet.");
            }

            return result.prepareCommand();
        }

    }

    // todo asm duplicates code in Command sender
    @NotNull
    private static String readCommand(@NotNull InputStreamReader streamReader) throws IOException {
        final StringBuilder sb = new StringBuilder();

        Character c = null;
        final char prefix = readChar(streamReader);
        assert (prefix == ProtocolCommandCodes.RESPONSE_PREFIX) : "Command has invalid prefix.";
        sb.append(prefix);

        do {
            if (c != null) {
                sb.append(c);
            }
            c = readChar(streamReader);
        } while (c != ProtocolCommandCodes.COMMAND_POSTFIX);

        return sb.toString();
    }

    private static char readChar(InputStreamReader streamReader) throws IOException {
        int b = streamReader.read();
        assert (b != -1) : "Unexpected end of stream has been reached.";
        if (b < 0) {
            throw new AssertionError("Unexpected end of stream has been reached.");
        }
        Log.d(TAG, String.format("Char received: %x - %d - %c", b, b, (char) b));
        return (char) b;
    }

}
