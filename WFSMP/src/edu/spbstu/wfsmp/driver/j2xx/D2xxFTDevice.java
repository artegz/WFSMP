package edu.spbstu.wfsmp.driver.j2xx;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import edu.spbstu.wfsmp.driver.Device;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 18:39
 */
public class D2xxFTDevice implements Device {

    @NotNull
    private final FT_Device driver;

    private boolean opened = true;

    D2xxFTDevice(@NotNull FT_Device driver) {
        this.driver = driver;
    }

    @Override
    public void purgeRx() throws IOException {
        checkOpened();
        driver.purge(D2xxManager.FT_PURGE_RX);
    }

    @Override
    public void purgeTx() throws IOException {
        checkOpened();
        driver.purge(D2xxManager.FT_PURGE_TX);
    }

    @Override
    public void close() throws IOException {
        driver.close();
        opened = false;
    }

    @Override
    public int read(byte[] data, int bytesToRead) throws IOException {
        checkOpened();
        return driver.read(data, bytesToRead);
    }

    @Override
    public int write(byte[] data, int bytesToWrite) throws IOException {
        checkOpened();
        return driver.write(data, bytesToWrite);
    }

    @Override
    public int getQueueStatus() throws IOException {
        checkOpened();
        return driver.getQueueStatus();
    }

    @Override
    public void resetDevice() throws IOException {
        checkOpened();
        driver.resetDevice();
    }

    private void checkOpened() throws IOException {
        if (!opened) {
            throw new IOException("Device is not available.");
        }
    }

    @NotNull
    public FT_Device getNativeDriver() {
        return driver;
    }
}
