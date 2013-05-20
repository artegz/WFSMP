package edu.spbstu.wfsmp.driver;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 23:48
 */
public class DeviceException extends Exception {

    public DeviceException(Throwable throwable) {
        super(throwable);
    }

    public DeviceException(String detailMessage) {
        super(detailMessage);
    }

    public DeviceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
