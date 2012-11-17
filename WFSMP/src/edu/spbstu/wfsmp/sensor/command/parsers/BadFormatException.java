package edu.spbstu.wfsmp.sensor.command.parsers;

/**
 * User: artegz
 * Date: 04.11.12
 * Time: 20:25
 */
public class BadFormatException extends Exception {

    public BadFormatException() {
    }

    public BadFormatException(String detailMessage) {
        super(detailMessage);
    }

    public BadFormatException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BadFormatException(Throwable throwable) {
        super(throwable);
    }
}
