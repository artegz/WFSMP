package edu.spbstu.wfsmp.sensor.command.parsers;

import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:52
 */
class CommandParserImpl<T> implements CommandParser<T> {

    @NotNull
    private final ParamParser<T> paramParser;

    protected CommandParserImpl(@NotNull ParamParser<T> paramParser) {
        this.paramParser = paramParser;
    }

    @NotNull
    @Override
    public ProtocolCommand parseCommand(@NotNull String commandStr) throws BadFormatException {
        // return new ProtocolCommand(parseCommandCode(commandStr), paramParser.parseCommandParam(commandStr.substring(getCommandCodeEnd())));
        throw new UnsupportedOperationException();
    }

    @NotNull
    public String parseCommandCode(@NotNull String commandStr) throws BadFormatException {
        return commandStr.substring(CommandParserFactory.PREFIX_POS, getCommandCodeEnd());
    }

    private int getCommandCodeEnd() {
        return CommandParserFactory.PREFIX_POS + CommandParserFactory.PREFIX_LENGTH + CommandParserFactory.COMMAND_CODE_LENGTH;
    }

}
