package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.ApplicationProperties;
import edu.spbstu.wfsmp.sensor.SensorController;
import edu.spbstu.wfsmp.sensor.SensorException;

/**
 * User: artegz
 * Date: 28.10.12
 * Time: 19:14
 */
public class ShowInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationContext.debug(getClass(), "Init show info activity.");

        setContentView(R.layout.info);

        final View devInfoView = findViewById(R.id.devInfoView);

        ApplicationContext.debug(getClass(), "Show info activity initialized.");
    }

    private String prepareDevInfo() {
        final SensorController sensor = (SensorController) ApplicationContext.getInstance().get(ApplicationProperties.CURRENT_SENSOR);
        final StringBuilder sb = new StringBuilder();
        assert (sensor != null);

        try {
            final String deviceNumber = sensor.getSerialNumber();
        } catch (SensorException e) {
            sb.append("Error. See log for details.");
            ApplicationContext.handleException(getClass(), e);
        }

        return sb.toString();
    }


}
