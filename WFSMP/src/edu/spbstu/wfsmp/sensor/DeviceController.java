package edu.spbstu.wfsmp.sensor;

import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceInputStream;
import edu.spbstu.wfsmp.driver.DeviceOutputStream;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommand;
import edu.spbstu.wfsmp.sensor.command.ProtocolCommandCodes;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:46
 */
public class DeviceController implements ISensorController {

    public static final String TAG = "DeviceController";

    @NotNull
    private final OutputStreamWriter outputStreamWriter;

    @NotNull
    private final InputStreamReader inputStreamReader;

    public DeviceController(@NotNull Device device) {
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

    @Override
    @NotNull
    public String getSerialNumber() throws SensorException {
        // prepare request command
        final ProtocolCommand request = new ProtocolCommand(ProtocolCommandCodes.REQUEST_SERIAL_NUMBER);

        // send request and receive response
        final ProtocolCommand response = CommandSender.sendToBeAnsweredCommand(request,
                outputStreamWriter, inputStreamReader, ProtocolCommandCodes.RESPONSE_SERIAL_NUMBER);

        // receive version from response
        final String[] params = response.getParams();

        if (params == null) {
            throw new SensorException("Bad response.");
        }

        return params[0];
    }

    @Override
    @NotNull
    public List<MeasurementResult> getAllMeasurements() throws SensorException {

        /*return Arrays.asList(new MeasurementResult(1, 2, 3, 4, 5, 6, 7, 8, new Date()),
                new MeasurementResult(1, 2, 3, 4, 5, 6, 7, 8, new Date()),
                new MeasurementResult(1, 2, 3, 4, 5, 6, 7, 8, new Date()));*/

        final ProtocolCommand request = new ProtocolCommand(ProtocolCommandCodes.REQUEST_DATA_BASE_OUT);
        final ProtocolCommand response = CommandSender.sendToBeAnsweredCommand(request, outputStreamWriter, inputStreamReader, ProtocolCommandCodes.RESPONSE_DATA_BASE_OUT);

        // log received result
        ApplicationContext.debug(getClass(), "Response '" + response.getCommandCode() + "' received with following parameters: ");
        for (String param : response.getParams()) {
            ApplicationContext.debug(getClass(), "\t" + param);
        }

        // todo test
        // format of each string is: [4sim - v][4sim - l][4sim - u][4sim - t][3sim - distance][2sim - depth][4sim - speed][6sim - date][6sim - time]
        final String[] formattedMeasResults = response.getParams();

        final List<MeasurementResult> measResults = new ArrayList<MeasurementResult>(formattedMeasResults.length);

        for (String formattedResult : formattedMeasResults) {
            try {
                final Integer measNo = Integer.getInteger(formattedResult.substring(0, 4));
                final Integer distance = Integer.getInteger(formattedResult.substring(4, 8));
                final Integer depth = Integer.getInteger(formattedResult.substring(8, 12));
                final Integer estimatedSteed = Integer.getInteger(formattedResult.substring(12, 16));
                final Integer measuredFrequency = Integer.getInteger(formattedResult.substring(16, 19));
                final Integer turns = Integer.getInteger(formattedResult.substring(19, 21));
                final Integer time = Integer.getInteger(formattedResult.substring(21, 25));
                final Integer type = Integer.getInteger(formattedResult.substring(25, 31));
                final Date timestamp = DateFormat.getDateInstance().parse(formattedResult.substring(31, 36));

                measResults.add(new MeasurementResult(measNo, distance, depth, estimatedSteed, measuredFrequency, turns, time, type, timestamp));
            } catch (ParseException e) {
                throw new AssertionError("Bad format.");
            }
        }

        return measResults;
    }

    /*@Override
    @Nullable
    public Measurement getMeasurement(@NotNull MeasurementSelector selector) {
        // todo implement
        return null;
    }
*/
    @Override
    public void startMeasuring(@NotNull MeasurementParameters parameters) throws SensorException {
        // prepare protocol command to be sent
        final ProtocolCommand request = new ProtocolCommand(
                ProtocolCommandCodes.REQUEST_START,              // command id
                String.format("%04d", parameters.getDistance()), // formatted 1st parameter
                String.format("%04d", parameters.getDepth())     // formatted 2nd parameter
        );

        // send prepared command (response is not expected)
        CommandSender.sendNoResponseCommand(request, outputStreamWriter);
    }

    @Override
    public void stopMeasuring() throws SensorException {
        // prepare protocol command to be sent
        final ProtocolCommand request = new ProtocolCommand(ProtocolCommandCodes.REQUEST_STOP);

        // send prepared command (response is not expected)
        CommandSender.sendNoResponseCommand(request, outputStreamWriter);
    }

    @Override
    public void eraseAllMeasurements() throws SensorException {
        // todo implement
        throw new SensorException("Feature is not implemented yet.");
        // CommandSender.sendToBeConfirmedCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_ERASE_RESULT_ALL), outputStreamWriter, inputStreamReader);
    }

    @Override
    public void applySensorProperties(@NotNull SensorProperties properties) throws SensorException {
        // todo implement
        throw new SensorException("Feature is not implemented yet.");
        // CommandSender.execSimpleCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_WRITE_DEVICE_NUMBER, properties.getSerialNumber()), outputStreamWriter, inputStreamReader);
    }

    @Override
    public void applyLinearTable(@NotNull LinearTable table) throws SensorException {
        // todo implement
        throw new SensorException("Feature is not implemented yet.");
        // CommandSender.sendToBeConfirmedCommand(new ProtocolCommand(ProtocolCommandCodes.REQUEST_WRITE_LINEAR_TABLE, formatLinearTable(table)), outputStreamWriter, inputStreamReader);
    }

    private static String formatLinearTable(@NotNull LinearTable table) {
        // todo asm: format table into command parameter
        return "stub";
    }

}
