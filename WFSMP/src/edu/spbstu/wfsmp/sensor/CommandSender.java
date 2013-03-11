package edu.spbstu.wfsmp.sensor;

import android.util.Log;
import edu.spbstu.wfsmp.ApplicationContext;
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
    
    public static final String TAG = CommandSender.class.getName();
    
    public static void sendNoResponseCommand(@NotNull ProtocolCommand request,
                                             @NotNull OutputStreamWriter outputStreamWriter) throws SensorException {
        ApplicationContext.debug(CommandSender.class, "Sending command.");
        sendCommand(request, outputStreamWriter);
        ApplicationContext.debug(CommandSender.class, "Command sent.");
    }

    public static void sendToBeConfirmedCommand(@NotNull ProtocolCommand request,
                                                @NotNull OutputStreamWriter outputStreamWriter,
                                                @NotNull InputStreamReader inputStreamReader)
            throws SensorException {
        sendToBeAnsweredCommand(request, outputStreamWriter, inputStreamReader, ProtocolCommandCodes.RESPONSE_OK);
    }

    @NotNull
    public static ProtocolCommand sendToBeAnsweredCommand(@NotNull ProtocolCommand request,
                                                          @NotNull OutputStreamWriter outputStreamWriter,
                                                          @NotNull InputStreamReader inputStreamReader,
                                                          @NotNull String expectedResponse)
            throws SensorException {
        final ProtocolCommand response;

        ApplicationContext.debug(CommandSender.class, "Sending command.");        
        sendCommand(request, outputStreamWriter);
        ApplicationContext.debug(CommandSender.class, "Command sent. Waiting for response...");

        try {
            response = receiveCommand(inputStreamReader);
            ApplicationContext.debug(CommandSender.class, "Validating received response against expected response.");
            if (! expectedResponse.equals(response.getCommandCode())) {
                // if it is not expected response - it must be NOK with error code
                if (! ProtocolCommandCodes.RESPONSE_NOK.equals(response.getCommandCode())) {
                    throw new AssertionError("Unexpected response received.");
                }

                // get error code (if exists)
                final String[] params = response.getParams();
                final String resultCode = params != null && params.length > 0 ? params[0] : null;

                // throw exception with error code
                throw new SensorException(resultCode != null ? Integer.valueOf(resultCode) : null);
            } else {
                ApplicationContext.debug(CommandSender.class, "Received command is valid.");
            }
        } catch (IOException e) {
            throw new SensorException(e);
        }

        return response;
    }

    @NotNull
    private static ProtocolCommand receiveCommand(@NotNull InputStreamReader inputStreamReader) throws IOException, SensorException {
        // read raw command from input stream
        final String receivedRawCommand = readRawCommand(inputStreamReader);

        ApplicationContext.debug(CommandSender.class, "Response command received.");
        ApplicationContext.debug(CommandSender.class, "Raw response command: " + receivedRawCommand);
        
        // parse command
        final ProtocolCommand receivedCommand = ProtocolCommand.parseCommand(receivedRawCommand);

        ApplicationContext.debug(CommandSender.class, "Parsed response command: " + receivedCommand.getCommandCode());
        
        return receivedCommand;
    }

    private static void sendCommand(ProtocolCommand request, OutputStreamWriter outputStreamWriter) throws SensorException {
        final String preparedCommand = request.prepareCommand();

        Log.d(TAG, "Sending command '" + request.getCommandCode() + "' with params '" + Arrays.asList(request.getParams()) + "'. Prepared: " + preparedCommand + ".");

        logBytes(preparedCommand);

        try {
            // send start request
            outputStreamWriter.append(preparedCommand);
            outputStreamWriter.flush();            
        } catch (IOException e) {
            throw new SensorException(e);
        }
    }

    @NotNull
    private static String readRawCommand(@NotNull InputStreamReader streamReader) throws IOException, SensorException {
        final StringBuilder sb = new StringBuilder();

        final char prefix = readChar(streamReader);

        if (prefix != ProtocolCommandCodes.RESPONSE_PREFIX) {
            throw new SensorException("Command has invalid prefix.");
        }

        // append prefix
        sb.append(prefix);

        // read until the command is complete
        while (! sb.toString().endsWith(ProtocolCommandCodes.COMMAND_POSTFIX)) {
            sb.append(readChar(streamReader));
        }

        final String completeCommand = sb.toString();

        // return command with catted ending chars
        return completeCommand.substring(0, completeCommand.length() - ProtocolCommandCodes.COMMAND_POSTFIX.length());
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
