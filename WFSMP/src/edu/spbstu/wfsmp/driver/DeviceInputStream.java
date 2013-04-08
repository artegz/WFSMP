package edu.spbstu.wfsmp.driver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 23:37
 */
public class DeviceInputStream extends InputStream {

    public static final int READ_BLOCK_TIMEOUT = 25;

    @NotNull
    private final Device device;

    public DeviceInputStream(@NotNull Device device) {
        this.device = device;
    }

    @Override
    public int available() throws IOException {
        return device.getQueueStatus();
    }

    @Override
    public void close() throws IOException {
        device.purgeRx();
    }

    @Override
    public synchronized int read(byte[] buffer, int offset, int length) throws IOException {
        int queueStatus = device.getQueueStatus();

        try {
            while (queueStatus < 1) {
                // wait
                wait(READ_BLOCK_TIMEOUT);
                // check if new data appears
                queueStatus = device.getQueueStatus();
            }
        } catch (InterruptedException e) {
            throw new InterruptedIOException(e.getMessage());
        }

        final int bytesToRead = Math.min(length, queueStatus);
        final byte[] tmpBuffer = new byte[bytesToRead];

        device.read(tmpBuffer, bytesToRead);

        // fill received buffer
        System.arraycopy(tmpBuffer, 0, buffer, offset, bytesToRead);

        return bytesToRead;
    }

    @Override
    public long skip(long byteCount) throws IOException {
        if (byteCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Not allowed activity skip more then " + Integer.MAX_VALUE + " at once.");
        }

        if (byteCount < 0) {
            return 0;
        }

        final int bufferSize = (int) byteCount;
        final byte[] tmpBuffer = new byte[bufferSize];

        // activity skip bytes just read them into temporary buffer
        return read(tmpBuffer, 0, bufferSize);
    }

    @Override
    public int read() throws IOException {
        final byte[] buffer = new byte[1];
        final int bytesRead = read(buffer, 0, 1);

        if (bytesRead < 1) {
            throw new AssertionError("Expected at least 1 byte to be read.");
        }

        return buffer[0];
    }
}
