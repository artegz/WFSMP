package edu.spbstu.wfsmp.sensor;

import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceInputStream;
import edu.spbstu.wfsmp.driver.DeviceOutputStream;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommandCodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:46
 */
public class SensorController {

    public static final String TAG = "SensorController";

    @NotNull
    private final OutputStreamWriter outputStreamWriter;

    @NotNull
    private final InputStreamReader inputStreamReader;

    public SensorController(@NotNull Device device) {
        try {
            this.outputStreamWriter = new OutputStreamWriter(
                    new BufferedOutputStream(new DeviceOutputStream(device)), ProtocolCommandCodes.PROTOCOL_COMMAND_ENCODING);
            this.inputStreamReader = new InputStreamReader(
                    new BufferedInputStream(new DeviceInputStream(device)), ProtocolCommandCodes.PROTOCOL_COMMAND_ENCODING);
        } catch (UnsupportedEncodingException e) {
            // unexpected case
            throw new AssertionError(e);
        }
    }

    @NotNull
    public String getSerialNumber() throws SensorException {
        final ProtocolCommand response = CommandSender.execRetCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_SERIAL_NUMBER),
                outputStreamWriter, inputStreamReader, ProtocolCommandCodes.RESPONSE_SERIAL_NUMBER);

        return (String) response.getParams()[0];
    }

    @NotNull
    public List<Measurement> getAllMeasurements() {
        // todo
        return null;
    }

    @Nullable
    public Measurement getMeasurement(@NotNull MeasurementSelector selector) {
        // todo
        return null;
    }

    /*@NotNull
    public List<Measurement> getMeasurements(@NotNull MeasurementSelector selector) {
        return null;
    }*/

    public void startMeasuring(@NotNull MeasurementParameters parameters) throws SensorException {
        final ProtocolCommand request = new ProtocolCommand(ProtocolCommandCodes.REQUEST_START,
                String.format("%04d", parameters.getDistance()), String.format("%04d", parameters.getDepth()));
        CommandSender.execSimpleCommand(request, outputStreamWriter, inputStreamReader);
    }

    public void stopMeasuring() throws SensorException {
        CommandSender.execSimpleCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_STOP), outputStreamWriter, inputStreamReader);
    }

    public void eraseAllMeasurements() throws SensorException {
        // CommandSender.execSimpleCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_ERASE_RESULT_ALL), outputStreamWriter, inputStreamReader);
    }

    public void applySensorProperties(@NotNull SensorProperties properties) throws SensorException {
        // CommandSender.execSimpleCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_WRITE_DEVICE_NUMBER, properties.getSerialNumber()), outputStreamWriter, inputStreamReader);
    }

    public void applyLinearTable(@NotNull LinearTable table) throws SensorException {
        // CommandSender.execSimpleCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_WRITE_LINEAR_TABLE, formatLinearTable(table)), outputStreamWriter, inputStreamReader);
    }

    private static String formatLinearTable(@NotNull LinearTable table) {
        // todo asm: format table into command parameter
        return "stub";
    }

}
