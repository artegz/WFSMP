package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

/**
 * User: Artegz
 * Date: 16.12.12
 * Time: 20:40
 */
public interface DeviceController {

    @NotNull
    String getSerialNumber() throws SensorException;

    @NotNull
    String getSoftwareVersion() throws SensorException;

    @NotNull
    List<MeasurementResult> getDataBaseOut() throws SensorException;

    void start() throws SensorException;

    void stop() throws SensorException;

    @NotNull
    MeasurementResult getCurrentMeasurement() throws SensorException;

    @NotNull
    Status getStatusOut() throws SensorException;

    void save(@NotNull MeasurementParameters parameters) throws SensorException;

    void clearDb() throws SensorException;

    int getDbSize() throws SensorException;

    void turnOn() throws SensorException;   // todo asm: not tested

    void turnOff() throws SensorException;  // todo asm: not tested

    // preferences

    void setDisplayMode(@NotNull IndicationMode indicationMode) throws SensorException;  // todo asm: not tested

    void setWhirligigType(WhirligigType whirligigType) throws SensorException;  // todo asm: not tested

    void setSound(boolean enabled) throws SensorException;  // todo asm: not tested

    void setSensEnable(boolean enabled) throws SensorException;  // todo asm: not tested

    void setTime(@NotNull Date time) throws SensorException; // todo asm: not tested

    void setDate(@NotNull Date date) throws SensorException; // todo asm: not tested

    LinearTable readLinearTable() throws SensorException; // todo asm: not tested

    void writeLinearTable(LinearTable linearTable) throws SensorException; // todo asm: not tested

    void writeSerialNumber(String serialNumber) throws SensorException; // todo asm: not tested

    void writeSoftwareVersion(String softwareVersion) throws SensorException; // todo asm: not tested

}
