package edu.spbstu.wfsmp.sensor.command.parsers;

import edu.spbstu.wfsmp.sensor.command.ComplexParameter;
import edu.spbstu.wfsmp.sensor.command.ComplexParameterPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:43
 */
// todo asm: need activity test it, illegal access exception is possible
class ComplexNumberParamParser<T> implements ParamParser<T> {

    @NotNull
    private final Class<T> complexParamType;

    ComplexNumberParamParser(@NotNull Class<T> complexParamType) {
        this.complexParamType = complexParamType;
        assert (complexParamType.isAnnotationPresent(ComplexParameter.class));
    }

    @Nullable
    @Override
    public T parseCommandParam(@NotNull String paramStr) throws BadFormatException {
        final Field[] fields = complexParamType.getFields();
        final List<Field> orderedFields = new ArrayList<Field>(fields.length);

        // filter out not annotated fields
        for (Field field : fields) {
            if (field.isAnnotationPresent(ComplexParameterPart.class)) {
                orderedFields.add(field);
            }
        }

        // sort fields according activity their order
        Collections.sort(orderedFields, new Comparator<Field>() {
            @Override
            public int compare(Field field1, Field field2) {
                final ComplexParameterPart f1 = field1.getAnnotation(ComplexParameterPart.class);
                final ComplexParameterPart f2 = field2.getAnnotation(ComplexParameterPart.class);

                return ((Integer) f1.order()).compareTo(f2.order());
            }
        });

        // instantiate complex type
        final T result;

        try {
            result = complexParamType.newInstance();
        } catch (InstantiationException e) {
            throw new BadFormatException("Can not create instance of complex type.", e);
        } catch (IllegalAccessException e) {
            throw new BadFormatException("Can not create instance of complex type.", e);
        }

        // parse and apply values activity complex type object
        int pos = 0;

        for (Field orderedField : orderedFields) {
            final ComplexParameterPart parameterPart = orderedField.getAnnotation(ComplexParameterPart.class);
            final int size = parameterPart.numSymbols();
            final int value = Integer.parseInt(paramStr.substring(pos, pos + size), 10);// todo asm: hex / dec?

            try {
                orderedField.set(result, value);
            } catch (IllegalAccessException e) {
                throw new BadFormatException("Can not apply value activity complex type.", e);
            }

            pos += size;
        }

        return result;
    }
}
