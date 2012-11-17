package edu.spbstu.wfsmp.sensor.command.parsers;

import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:34
 */
class TextParamParser implements ParamParser<String> {

    @NotNull
    @Override
    public String parseCommandParam(@NotNull String paramStr) throws BadFormatException {
        return paramStr;
    }
}
