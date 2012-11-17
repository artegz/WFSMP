package edu.spbstu.wfsmp.sensor.command.parsers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:45
 */
class ParamArrayParser<T> implements ParamParser<Collection<T>> {

    @NotNull
    private final ParamParser<T> nestedParser;

    @NotNull
    private final String elementSeparator;

    ParamArrayParser(@NotNull ParamParser<T> nestedParser, @NotNull String elementSeparator) {
        this.nestedParser = nestedParser;
        this.elementSeparator = elementSeparator;
    }

    @NotNull
    @Override
    public Collection<T> parseCommandParam(@NotNull String commandStr) throws BadFormatException {
        final List<String> nestedParamStrs = Arrays.asList(commandStr.split(Pattern.quote(elementSeparator)));
        final List<T> result = new ArrayList<T>(nestedParamStrs.size());

        for (String nestedParamStr : nestedParamStrs) {
            result.add(nestedParser.parseCommandParam(nestedParamStr));
        }

        return result;
    }

}
