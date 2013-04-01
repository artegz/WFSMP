package edu.spbstu.wfsmp.sensor;

import edu.spbstu.wfsmp.ApplicationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:49
 */
public class ProtocolCommand {

    public static final int COMMAND_PREFIX_LENGTH = 2;
    // command format
    public static final char REQUEST_PREFIX             = '#';
    public static final char RESPONSE_PREFIX            = '*';
    public static final String COMMAND_POSTFIX            = "\r\n";
    // encoding
    public static final String PROTOCOL_COMMAND_ENCODING = "UTF-8";

    @NotNull
    private String commandCode;

    @Nullable
    private String[] params;

    public ProtocolCommand(@NotNull String commandCode, @Nullable String... params) {
        this.commandCode = commandCode;
        this.params = params;
    }

    @NotNull
    public static ProtocolCommand parseCommand(@NotNull String receivedCommand) throws SensorException {
        validate(receivedCommand);

        // todo test
        final String commandCode = getCommandCode(receivedCommand);

        if (ProtocolCommandCodes.RESPONSE_DATA_BASE_OUT.equals(commandCode)) {
            final String[] parameters = getParameters(receivedCommand, " ");

            return new ProtocolCommand(receivedCommand.substring(0, 2), parameters);
        } else if (ProtocolCommandCodes.RESPONSE_SERIAL_NUMBER.equals(commandCode)) {
            return new ProtocolCommand(receivedCommand.substring(0, 2), receivedCommand.substring(2, receivedCommand.length()));
        } else if (ProtocolCommandCodes.RESPONSE_NOK.equals(commandCode)) {
            return new ProtocolCommand(receivedCommand.substring(0, 2));
        } else {
            throw new AssertionError("Command code '" + commandCode + "' is not supported.");
        }
    }

    public static void validate(String response) throws SensorException {
        ApplicationContext.debug(ProtocolCommand.class, "Validating received response against expected response.");

        final String commandCode = getCommandCode(response);

        if (ProtocolCommandCodes.ERROR.equals(commandCode)) {
            throw new SensorException("Unrecognized command.");
        } else {
            ApplicationContext.debug(ProtocolCommand.class, "Received command is valid.");
        }
    }

    public static String[] getParameters(String receivedCommand, String delimiter) {
        final String parametersString = receivedCommand.substring(2, receivedCommand.length());
        return delimiter != null ? parametersString.split(delimiter) : new String[] {parametersString};
    }

    public static String getCommandCode(@NotNull String command) {
        return command.substring(0, ProtocolCommand.COMMAND_PREFIX_LENGTH);
    }

    @NotNull
    public String getCommandCode() {
        return commandCode;
    }

    @Nullable
    public String[] getParams() {
        return params;
    }

    @NotNull
    public String prepareCommand() {
        return prepareCommand(commandCode, params);
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
