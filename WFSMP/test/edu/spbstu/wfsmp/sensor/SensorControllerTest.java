package edu.spbstu.wfsmp.sensor;

import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.mock.MockDevice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 18:25
 */
public class SensorControllerTest {

    private SensorController sensorController;
    private Device mockDevice;

    @Before
    public void setUp() throws Exception {
        mockDevice = new MockDevice();
        sensorController = new SensorController(mockDevice);
    }

    @After
    public void tearDown() throws Exception {


    }

    public void testGetAllMeasurements() throws Exception {

    }

    public void testGetMeasurement() throws Exception {

    }

    @Test
    public void testStartMeasuring() throws Exception {
        sensorController.startMeasuring(new MeasurementParameters(100, 100));
    }

    public void testStopMeasuring() throws Exception {

    }

    public void testEraseAllMeasurements() throws Exception {

    }

    public void testEraseMeasurement() throws Exception {

    }

    public void testApplySensorProperties() throws Exception {

    }

    public void testApplyLinearTable() throws Exception {

    }
}
