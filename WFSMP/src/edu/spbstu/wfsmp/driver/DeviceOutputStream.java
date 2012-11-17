package edu.spbstu.wfsmp.driver;

import com.ftdi.D2xx;
import edu.spbstu.wfsmp.driver.Device;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:36
 */
public class DeviceOutputStream extends OutputStream {

    @NotNull
    private final Device device;

    public DeviceOutputStream(@NotNull Device device) {
        this.device = device;
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        final byte[] tmpBuffer = new byte[count];

        System.arraycopy(buffer, offset, tmpBuffer, 0, count);
        device.write(tmpBuffer, count);
    }

    @Override
    public void flush() throws IOException {
        device.purgeTx();
    }

    @Override
    public void close() throws IOException {
        flush();
    }

    @Override
    public void write(int i) throws IOException {
        write(new byte[] { (byte) i }, 0, 1);
    }

}
