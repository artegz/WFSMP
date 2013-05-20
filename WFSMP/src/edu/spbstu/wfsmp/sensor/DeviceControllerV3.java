package edu.spbstu.wfsmp.sensor;

import edu.spbstu.wfsmp.driver.Device;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

/**
 * User: Artegz
 * Date: 18.05.13
 * Time: 17:36
 */
public class DeviceControllerV3 implements DeviceControllerExt {

    private boolean measurementInProgress = false;

    private DeviceController baseController;

    public DeviceControllerV3(DeviceController baseController) {
        this.baseController = baseController;
    }

    public boolean isMeasurementInProgress() {
        return measurementInProgress;
    }

    private void checkMeasurementInProgress() throws SensorException {
        if (measurementInProgress) {
            throw new SensorException("Measurement in progress, operation can not be performed.");
        }
    }

    @Override
    public boolean measurementFinished() {
        return false;  // todo...
    }

    @Override
    @NotNull
    public String getSerialNumber() throws SensorException {
        return baseController.getSerialNumber();
    }

    @Override
    @NotNull
    public String getSoftwareVersion() throws SensorException {
        return baseController.getSoftwareVersion();
    }

    @Override
    @NotNull
    public List<MeasurementResult> getDataBaseOut() throws SensorException {
        return baseController.getDataBaseOut();
    }

    @Override
    public void start() throws SensorException {
        baseController.start();
    }

    @Override
    public void stop() throws SensorException {
        baseController.stop();
    }

    @Override
    @NotNull
    public MeasurementResult getCurrentMeasurement() throws SensorException {
        return baseController.getCurrentMeasurement();
    }

    @Override
    @NotNull
    public Status getStatusOut() throws SensorException {
        return baseController.getStatusOut();
    }

    @Override
    public void save(@NotNull MeasurementParameters parameters) throws SensorException {
        baseController.save(parameters);
    }

    @Override
    public void clearDb() throws SensorException {
        baseController.clearDb();
    }

    @Override
    public int getDbSize() throws SensorException {
        return baseController.getDbSize();
    }

    @Override
    public void turnOn() throws SensorException {
        baseController.turnOn();
    }

    @Override
    public void turnOff() throws SensorException {
        baseController.turnOff();
    }

    @Override
    public void setDisplayMode(@NotNull IndicationMode indicationMode) throws SensorException {
        baseController.setDisplayMode(indicationMode);
    }

    @Override
    public void setWhirligigType(WhirligigType whirligigType) throws SensorException {
        baseController.setWhirligigType(whirligigType);
    }

    @Override
    public void setSound(boolean enabled) throws SensorException {
        baseController.setSound(enabled);
    }

    @Override
    public void setSensEnable(boolean enabled) throws SensorException {
        baseController.setSensEnable(enabled);
    }

    @Override
    public void setTime(@NotNull Date time) throws SensorException {
        baseController.setTime(time);
    }

    @Override
    public void setDate(@NotNull Date date) throws SensorException {
        baseController.setDate(date);
    }

    @Override
    public LinearTable readLinearTable() throws SensorException {
        return baseController.readLinearTable();
    }

    @Override
    public void writeLinearTable(LinearTable linearTable) throws SensorException {
        baseController.writeLinearTable(linearTable);
    }

    @Override
    public void writeSerialNumber(String serialNumber) throws SensorException {
        baseController.writeSerialNumber(serialNumber);
    }

    @Override
    public void writeSoftwareVersion(String softwareVersion) throws SensorException {
        baseController.writeSoftwareVersion(softwareVersion);
    }
}
