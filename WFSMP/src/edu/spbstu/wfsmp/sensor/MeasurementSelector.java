package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:49
 */

/**
 * Required activity select only specified measurements from DB. Now - filter by id only applied.
 */
public class MeasurementSelector {

    @NotNull
    private Integer measurementId;

    public MeasurementSelector(@NotNull Integer measurementId) {
        this.measurementId = measurementId;
    }

    @NotNull
    public Integer getMeasurementId() {
        return measurementId;
    }
}
