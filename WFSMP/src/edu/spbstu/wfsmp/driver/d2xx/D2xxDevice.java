package edu.spbstu.wfsmp.driver.d2xx;

import com.ftdi.D2xx;
import edu.spbstu.wfsmp.driver.Device;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 18:39
 */
class D2xxDevice implements Device {

    @NotNull
    private final D2xx driver;

    D2xxDevice(@NotNull D2xx driver) {
        this.driver = driver;
    }

    @NotNull
    public D2xx getDriver() {
        return driver;
    }

    @Override
    public void purgeRx() throws IOException {
        driver.purge(D2xx.FT_PURGE_RX);
    }

    @Override
    public void purgeTx() throws IOException {
        driver.purge(D2xx.FT_PURGE_TX);
    }

    @Override
    public void close() throws IOException {
        driver.close();
    }

    @Override
    public int read(byte[] data, int bytesToRead) throws IOException {
        return driver.read(data, bytesToRead);
    }

    @Override
    public int write(byte[] data, int bytesToWrite) throws IOException {
        return driver.write(data, bytesToWrite);
    }

    @Override
    public int getQueueStatus() throws IOException {
        return driver.getQueueStatus();
    }

    @Override
    public void resetDevice() throws IOException {
        driver.resetDevice();
    }

}
