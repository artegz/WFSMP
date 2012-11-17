package edu.spbstu.wfsmp.sensor;

import android.util.Log;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommandCodes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:05
 */
class CommandSender {
    
    public static final String TAG = "CommandSender";

    // todo asm: make not static
    /**
     * Execute simple command (result is either OK or NOK).
     *
     * @param request request command
     * @param outputStreamWriter
     * @param inputStreamReader
     * @throws edu.spbstu.wfsmp.sensor.SensorException thrown in case if execution failed
     */
    public static void execSimpleCommand(@NotNull ProtocolCommand request,
                                         @NotNull OutputStreamWriter outputStreamWriter,
                                         @NotNull InputStreamReader inputStreamReader)
            throws SensorException {
        execRetCommand(request, outputStreamWriter, inputStreamReader, ProtocolCommandCodes.RESPONSE_OK);
    }

    @NotNull
    public static ProtocolCommand execRetCommand(@NotNull ProtocolCommand request,
                                                 @NotNull OutputStreamWriter outputStreamWriter,
                                                 @NotNull InputStreamReader inputStreamReader,
                                                 @NotNull String expectedResponse)
            throws SensorException {
        final ProtocolCommand response;
        final String preparedCommand = request.prepareCommand();

        Log.d(TAG, "Sending command '" + request.getCommandCode() + "' with params '" + Arrays.asList(request.getParams()) + "'. Prepared: " + preparedCommand + ".");

        logBytes(preparedCommand);

        try {
            // send start request
            outputStreamWriter.append(preparedCommand);
            outputStreamWriter.flush();

            Log.d(TAG, "Command sent. Waiting for response...");

            // await OK response
            final String receivedCommand = readCommand(inputStreamReader);

            Log.d(TAG, "Response received: " + receivedCommand);

            // todo ...
            // response = ProtocolCommand.parseCommand(receivedCommand);
            response = new ProtocolCommand(receivedCommand.substring(0, 2));

            if (! expectedResponse.equals(response.getCommandCode())) {
                // result for start request command must be either OK or NOK
                assert (ProtocolCommandCodes.RESPONSE_NOK.equals(response.getCommandCode())) : "Unexpected response received.";

                final Object[] params = response.getParams();
                final Integer resultCode = params != null ? (Integer) params[0] : null;

                assert (resultCode != null) : "NOK response must contains error code.";

                throw new SensorException(resultCode);
            }
        } catch (IOException e) {
            throw new SensorException(e);
        }

        return response;
    }
    
    @NotNull
    private static String readCommand(@NotNull InputStreamReader streamReader) throws IOException {
        final StringBuilder sb = new StringBuilder();

        Character c = null;
        final char prefix = readChar(streamReader);
        assert (prefix == ProtocolCommandCodes.RESPONSE_PREFIX) : "Command has invalid prefix.";

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
        Log.d(TAG, "Char received: " + b);
        return (char) b;
    }

    private static void logBytes(String str) {
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
}
