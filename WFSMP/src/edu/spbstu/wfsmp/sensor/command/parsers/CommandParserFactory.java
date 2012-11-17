package edu.spbstu.wfsmp.sensor.command.parsers;

import org.jetbrains.annotations.NotNull;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:20
 */
public class CommandParserFactory {

    public static final int PREFIX_POS = 0;
    public static final int PREFIX_LENGTH = 1;
    public static final int COMMAND_POS = 1;
    public static final int COMMAND_CODE_LENGTH = 1;

    public static String findCommandCode(@NotNull String commandStr) {
        return commandStr.substring(COMMAND_POS, PREFIX_POS + COMMAND_CODE_LENGTH + PREFIX_LENGTH);
    }
    
    @NotNull
    public CommandParser createParser(@NotNull String commandCode) {
        
        return null;
    }
}
