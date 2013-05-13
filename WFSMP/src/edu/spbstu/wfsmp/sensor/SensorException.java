package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 16:06
 */
public class SensorException extends Exception {

    public SensorException(Throwable throwable) {
        super(throwable);
    }


    public SensorException(String detailMessage) {
        super(detailMessage);
    }

}
