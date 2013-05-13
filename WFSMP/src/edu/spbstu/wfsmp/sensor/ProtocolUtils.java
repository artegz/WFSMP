package edu.spbstu.wfsmp.sensor;

import edu.spbstu.wfsmp.ApplicationContext;
import org.jetbrains.annotations.NotNull;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:49
 */
public class ProtocolUtils {

    public static final int COMMAND_PREFIX_LENGTH = 2;

    // command format
    public static final char REQUEST_PREFIX             = '#';
    public static final char RESPONSE_PREFIX            = '*';
    public static final String COMMAND_POSTFIX            = "\r\n";

    // encoding
    public static final String PROTOCOL_COMMAND_ENCODING = "UTF-8";

    public static void validate(String response) throws SensorException {
        ApplicationContext.debug(ProtocolUtils.class, "Validating received response against expected response.");

        final String commandCode = getCommandCode(response);

        if (ProtocolCodes.ERROR.equals(commandCode)) {
            throw new SensorException("Unrecognized command.");
        } else {
            ApplicationContext.debug(ProtocolUtils.class, "Received command is valid.");
        }
    }

    public static String[] getParameters(String receivedCommand, String delimiter) {
        final String parametersString = receivedCommand.substring(2, receivedCommand.length());
        return delimiter != null ? parametersString.split(delimiter) : new String[] {parametersString};
    }

    public static String getCommandCode(@NotNull String command) {
        return command.substring(0, ProtocolUtils.COMMAND_PREFIX_LENGTH);
    }

    public static String prepareCommand(String commandCode, Object... params) {
        final StringBuilder sb = new StringBuilder();

        sb.append(commandCode);
        for (Object param : params) {
            if (param instanceof Boolean) {
                sb.append(((Boolean) param) ? "1" : "0");
            } else if (param instanceof Integer) {
                sb.append(String.format("%04d", ((Integer) param)));
            } else if (param instanceof Short) {
                sb.append(String.format("%04d", ((Short) param)));
            } else if (param instanceof Byte) {
                sb.append(String.format("%02d", ((Byte) param)));
            } else if (param instanceof String) {
                sb.append(param);
            } else {
                throw new AssertionError("Unknown param type.");
            }
        }
        sb.append(COMMAND_POSTFIX);

        return sb.toString();
    }

}
