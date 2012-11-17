package edu.spbstu.wfsmp.sensor.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:49
 */
public class ProtocolCommand {

    @NotNull
    private String commandCode;

    @Nullable
    private Object[] params;

    public ProtocolCommand(@NotNull String commandCode, @Nullable Object... params) {
        this.commandCode = commandCode;
        this.params = params;
    }

    @NotNull
    public static ProtocolCommand parseCommand(@NotNull String receivedCommand) {


        // todo
        return new ProtocolCommand(receivedCommand.substring(0, 2), "00010002");
    }

    @NotNull
    public String getCommandCode() {
        return commandCode;
    }

    @Nullable
    public Object[] getParams() {
        return params;
    }

    @NotNull
    public String prepareCommand() {
        final StringBuilder sb = new StringBuilder();

        sb.append(commandCode);
        for (Object param : params) {
            // todo asM: decimal or hex?
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
