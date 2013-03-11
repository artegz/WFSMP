package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Artegz
 * Date: 16.12.12
 * Time: 20:40
 */
public interface ISensorController {

    @NotNull
    String getSerialNumber() throws SensorException;

    @NotNull
    List<MeasurementResult> getAllMeasurements() throws SensorException;

    /*@Nullable
    Measurement getMeasurement(@NotNull MeasurementSelector selector);*/

    void startMeasuring(@NotNull MeasurementParameters parameters) throws SensorException;

    void stopMeasuring() throws SensorException;

    void eraseAllMeasurements() throws SensorException;

    void applySensorProperties(@NotNull SensorProperties properties) throws SensorException;

    void applyLinearTable(@NotNull LinearTable table) throws SensorException;
}
