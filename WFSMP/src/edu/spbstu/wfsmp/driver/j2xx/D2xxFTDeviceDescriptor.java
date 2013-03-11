package edu.spbstu.wfsmp.driver.j2xx;

import com.ftdi.j2xx.D2xxManager;
import edu.spbstu.wfsmp.driver.DeviceDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * User: artegz
 * Date: 20.10.12
 * Time: 14:26
 */
class D2xxFTDeviceDescriptor implements DeviceDescriptor {

    @NotNull
    private final D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode;

    D2xxFTDeviceDescriptor(@NotNull D2xxManager.FtDeviceInfoListNode ftDeviceInfoListNode) {
        this.ftDeviceInfoListNode = ftDeviceInfoListNode;
    }

    public int getIndex() {
        return ftDeviceInfoListNode.id;
    }

    @Override
    public String getDeviceIdentifier() {
        return D2xxDeviceProvider.getDeviceTypeName(ftDeviceInfoListNode);
    }
}
