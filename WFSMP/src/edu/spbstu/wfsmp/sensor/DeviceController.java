package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Artegz
 * Date: 16.12.12
 * Time: 20:40
 */
public interface DeviceController {

    @NotNull
    String getSerialNumber() throws SensorException;

    @NotNull
    List<MeasurementResult> getDataBaseOut() throws SensorException;

    void start(@NotNull MeasurementParameters parameters) throws SensorException;

    void stop() throws SensorException;

    @NotNull
    MeasurementResult getCurrentMeasurement() throws SensorException;

    @NotNull
    Status getStatusOut() throws SensorException;
}
