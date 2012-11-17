package edu.spbstu.wfsmp.sensor.command;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 21:44
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface ComplexParameter {
    // Class<? extends ProtocolCommandMapper> mapper();
}
