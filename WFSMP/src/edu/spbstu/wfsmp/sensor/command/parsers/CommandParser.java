package edu.spbstu.wfsmp.sensor.command.parsers;

import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:20
 */
public interface CommandParser<T> {

    @NotNull
    ProtocolCommand parseCommand(@NotNull String commandStr) throws BadFormatException;

}
