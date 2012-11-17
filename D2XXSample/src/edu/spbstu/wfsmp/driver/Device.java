package edu.spbstu.wfsmp.driver;

import java.io.IOException;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 18:39
 */
public interface Device {

    void close() throws IOException;

    int read(byte[] data, int bytesToRead) throws IOException;

    int write(byte[] data, int bytesToWrite) throws IOException;

    int getQueueStatus() throws IOException;

    void purgeRx() throws IOException;

    void purgeTx() throws IOException;

    void resetDevice() throws IOException;
}
