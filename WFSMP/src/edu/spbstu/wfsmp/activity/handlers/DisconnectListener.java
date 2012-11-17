package edu.spbstu.wfsmp.activity.handlers;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.ApplicationProperties;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.sensor.SensorController;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * User: artegz
 * Date: 05.11.12
 * Time: 16:44
 */
public class DisconnectListener extends ForwardListener {

    private static final String TAG = DisconnectListener.class.getName();

    public DisconnectListener(@NotNull Class<? extends Activity> to, @NotNull Activity from) {
        super(to, from);
    }

    @Override
    public void onClick(View view) {
        disconnect();
        super.onClick(view);
    }

    public static void disconnect() {
        final Device device = (Device) ApplicationContext.getInstance().get(ApplicationProperties.CURRENT_DRIVER);

        try {
            if (device != null) {
                device.close();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        ApplicationContext.getInstance().remove(ApplicationProperties.CURRENT_SENSOR);
        ApplicationContext.getInstance().remove(ApplicationProperties.CURRENT_DRIVER);
    }
}
