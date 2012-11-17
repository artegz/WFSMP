package edu.spbstu.wfsmp.driver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 23:37
 */
public class DeviceInputStream extends InputStream {

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
    public int read(byte[] buffer, int offset, int length) throws IOException {
        final byte[] tmpBuffer = new byte[length];

        // todo asm: blocking may be required
        device.read(tmpBuffer, length);

        // fill received buffer
        System.arraycopy(tmpBuffer, 0, buffer, offset, length);

        return length;
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
        return (bytesRead > 0) ? buffer[0] : bytesRead;
    }
}
