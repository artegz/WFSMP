package edu.spbstu.wfsmp.sensor.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:49
 */
public class ProtocolCommand {

    private static final int COMMAND_PREFIX_LENGTH = 2;

    @NotNull
    private String commandCode;

    @Nullable
    private String[] params;

    public ProtocolCommand(@NotNull String commandCode, @Nullable String... params) {
        this.commandCode = commandCode;
        this.params = params;
    }

    @NotNull
    public static ProtocolCommand parseCommand(@NotNull String receivedCommand) {
        // todo test
        final String commandCode = receivedCommand.substring(0, COMMAND_PREFIX_LENGTH);

        if (ProtocolCommandCodes.RESPONSE_DATA_BASE_OUT.equals(commandCode)) {
            final String parametersString = receivedCommand.substring(2, receivedCommand.length());
            final String[] parameters = parametersString.split(" ");

            return new ProtocolCommand(receivedCommand.substring(0, 2), parameters);
        } else if (ProtocolCommandCodes.RESPONSE_SERIAL_NUMBER.equals(commandCode)) {
            return new ProtocolCommand(receivedCommand.substring(0, 2), receivedCommand.substring(2, receivedCommand.length()));
        } else if (ProtocolCommandCodes.RESPONSE_NOK.equals(commandCode)) {
            return new ProtocolCommand(receivedCommand.substring(0, 2));
        } else {
            throw new AssertionError("Command code '" + commandCode + "' is not supported.");
        }
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
        sb.append(ProtocolCommandCodes.COMMAND_POSTFIX);

        return sb.toString();
    }

}
