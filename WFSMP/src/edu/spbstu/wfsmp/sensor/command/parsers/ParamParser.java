package edu.spbstu.wfsmp.sensor.command.parsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 21:08
 */
public interface ParamParser<T> {

    @Nullable
    T parseCommandParam(@NotNull final String paramStr) throws BadFormatException;
}