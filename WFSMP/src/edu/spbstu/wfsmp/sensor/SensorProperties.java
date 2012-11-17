package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:54
 */
public class SensorProperties {

    @NotNull
    private String deviceNumber;

    public SensorProperties(@NotNull String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    @NotNull
    public String getDeviceNumber() {
        return deviceNumber;
    }
}
