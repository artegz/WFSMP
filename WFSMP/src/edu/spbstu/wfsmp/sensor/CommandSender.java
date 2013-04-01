package edu.spbstu.wfsmp.sensor;

import android.util.Log;
import edu.spbstu.wfsmp.ApplicationContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:05
 */
class CommandSender {
    
    public static final String TAG = CommandSender.class.getName();

    @NotNull
    public static String send(@NotNull String request,
                              @NotNull OutputStreamWriter outputStreamWriter,
                              @NotNull InputStreamReader inputStreamReader)
            throws SensorException, IOException {
        return send0(outputStreamWriter, inputStreamReader, request);
    }

    private static String send0(OutputStreamWriter outputStreamWriter, InputStreamReader inputStreamReader, String preparedCommand) throws SensorException, IOException {
        ApplicationContext.debug(CommandSender.class, "Sending command '" + preparedCommand + "'.");

        // send command
        sendCommand(outputStreamWriter, preparedCommand);

        ApplicationContext.debug(CommandSender.class, "Command sent. Waiting for response...");

        // receive response
        final String response = receiveCommand0(inputStreamReader);

        ApplicationContext.debug(CommandSender.class, "Response command received: " + response);

        return response;
    }

    @NotNull
    private static String receiveCommand0(@NotNull InputStreamReader inputStreamReader) throws IOException, SensorException {
        // read raw command from input stream
        final String receivedRawCommand = readRawCommand(inputStreamReader);

        return receivedRawCommand;
    }

    private static void sendCommand(OutputStreamWriter outputStreamWriter, String command) throws SensorException {

        logBytes(command);

        try {
            // send start request
            outputStreamWriter.append(command);
            outputStreamWriter.flush();            
        } catch (IOException e) {
            throw new SensorException(e);
        }
    }

    @NotNull
    private static String readRawCommand(@NotNull InputStreamReader streamReader) throws IOException, SensorException {
        final StringBuilder sb = new StringBuilder();

        final char prefix = readChar(streamReader);

        if (prefix != ProtocolCommand.RESPONSE_PREFIX) {
            throw new SensorException("Command has invalid prefix.");
        }

        // append prefix
        sb.append(prefix);

        // read until the command is complete
        while (! sb.toString().endsWith(ProtocolCommand.COMMAND_POSTFIX)) {
            sb.append(readChar(streamReader));
        }

        final String completeCommand = sb.toString();

        // return command with catted ending chars
        return completeCommand.substring(0, completeCommand.length() - ProtocolCommand.COMMAND_POSTFIX.length());
    }

    private static char readChar(@NotNull InputStreamReader streamReader) throws IOException {
        int b = streamReader.read();
        assert (b != -1) : "Unexpected end of stream has been reached.";
        if (b < 0) {
            throw new AssertionError("Unexpected end of stream has been reached.");
        }
        Log.d(TAG, "Char received: " + b);
        return (char) b;
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
}
