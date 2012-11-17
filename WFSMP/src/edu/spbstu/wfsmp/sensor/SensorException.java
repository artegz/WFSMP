package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 16:06
 */
public class SensorException extends Exception {

    private int resultCode;

    public SensorException(int resultCode) {
        this.resultCode = resultCode;
    }

    public SensorException(Throwable throwable) {
        super(throwable);
    }

    public int getResultCode() {
        return resultCode;
    }
}
