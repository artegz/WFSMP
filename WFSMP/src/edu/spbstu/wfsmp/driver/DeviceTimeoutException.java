package edu.spbstu.wfsmp.driver;

/**
 * User: Artegz
 * Date: 18.05.13
 * Time: 16:52
 */
public class DeviceTimeoutException extends DeviceException {

    public DeviceTimeoutException(Throwable throwable) {
        super(throwable);
    }

    public DeviceTimeoutException(String detailMessage) {
        super(detailMessage);
    }
}
