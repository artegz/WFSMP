package edu.spbstu.wfsmp.sensor.command.parsers;

import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:31
 */
class NoParamsParser implements ParamParser<Void> {

    @Nullable
    @Override
    public Void parseCommandParam(@NotNull String paramStr) throws BadFormatException {
        assert (paramStr.isEmpty());
        return null;
    }

}
