package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 16:06
 */
public class SensorException extends Exception {

    private Integer resultCode;

    public SensorException(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public SensorException(Throwable throwable) {
        super(throwable);
    }


    public SensorException(String detailMessage) {
        super(detailMessage);
    }

    public Integer getResultCode() {
        return resultCode;
    }
}
