package edu.spbstu.wfsmp.sensor.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 21:47
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface ComplexParameterPart {

    int order();

    int numSymbols();
}
