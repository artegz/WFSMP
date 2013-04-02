package edu.spbstu.wfsmp.sensor;

import android.text.format.DateFormat;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceInputStream;
import edu.spbstu.wfsmp.driver.DeviceOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:46
 */
public class DeviceControllerImpl implements DeviceController {

    private static final int YEAR_BASE = 2000;

    @NotNull
    private final OutputStreamWriter outputStreamWriter;

    @NotNull
    private final InputStreamReader inputStreamReader;

    public DeviceControllerImpl(@NotNull Device device) {
        try {
            this.outputStreamWriter = new OutputStreamWriter(
                    new BufferedOutputStream(new DeviceOutputStream(device)), ProtocolCommand.PROTOCOL_COMMAND_ENCODING);
            this.inputStreamReader = new InputStreamReader(
                    new BufferedInputStream(new DeviceInputStream(device)), ProtocolCommand.PROTOCOL_COMMAND_ENCODING);
        } catch (UnsupportedEncodingException e) {
            // unexpected case
            throw new AssertionError(e);
        }
    }

    @Override
    @NotNull
    public String getSerialNumber() throws SensorException {
        final String result;

        // prepare request command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_SERIAL_NUMBER, ProtocolCommandCodes.RESPONSE_SERIAL_NUMBER);

        // receive version from response
        final String[] params = ProtocolCommand.getParameters(response, null);

        if (params == null) {
            throw new SensorException("Bad response.");
        }

        result = params[0];

        return result;
    }

    @Override
    @NotNull
    public MeasurementResult getCurrentMeasurement() throws SensorException {
        final Integer frequencyOut = getFrequencyOut();
        final Integer velocityOut = getVelocityOut();
        final Integer turnNumberOut = getTurnNumberOut();
        final Integer measureTimeOut = getMeasureTimeOut();
        final String realDateOut = getRealDateOut();
        final String realTimeOut = getRealTimeOut();
        final Status status = getStatusOut();

        // todo asm: prepare MeasurementResult
        final int distance = 0;
        final int depth = 0;

        return new MeasurementResult(distance, depth, velocityOut, frequencyOut, turnNumberOut, measureTimeOut, realTimeOut, realDateOut, status);
    }

    @Override
    @NotNull
    public List<MeasurementResult> getDataBaseOut() throws SensorException {
        final List<MeasurementResult> measResults;

        // todo test
        return Arrays.asList(new MeasurementResult(1, 2, 3, 4, 5, 6, "12:30", "01.01.2013", new Status((byte)127)),
                new MeasurementResult(1, 2, 3, 4, 5, 6, "12:30", "01.01.2013", new Status((byte)127)),
                new MeasurementResult(1, 2, 3, 4, 5, 6, "12:30", "01.01.2013", new Status((byte)127)));

        /*final String response = sendRequest(ProtocolCommandCodes.REQUEST_DATA_BASE_OUT, ProtocolCommandCodes.RESPONSE_DATA_BASE_OUT);

        ProtocolCommand.validate(response);

        final String[] formattedMeasResults = ProtocolCommand.getParameters(response, " ");

        measResults = new ArrayList<MeasurementResult>(formattedMeasResults.length);

        for (String formattedResult : formattedMeasResults) {
            final Byte status = Byte.valueOf(formattedResult.substring(0, 2));
            final Integer distance = Integer.valueOf(formattedResult.substring(2, 6));
            final Integer depth = Integer.valueOf(formattedResult.substring(6, 8));
            final Integer speed = Integer.valueOf(formattedResult.substring(8, 12));
            final Integer frequency = Integer.valueOf(formattedResult.substring(12, 16));
            final Integer num = Integer.valueOf(formattedResult.substring(16, 20));
            final Integer time = Integer.valueOf(formattedResult.substring(20, 24));
            final Integer year = YEAR_BASE + Integer.valueOf(formattedResult.substring(24, 26));
            final Integer month = Integer.valueOf(formattedResult.substring(26, 28));
            final Integer day = Integer.valueOf(formattedResult.substring(28, 30));
            final Integer hour = Integer.valueOf(formattedResult.substring(30, 32));
            final Integer minute = Integer.valueOf(formattedResult.substring(32, 34));
            final Integer second = Integer.valueOf(formattedResult.substring(34, 36));

            final GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute, second);

            measResults.add(new MeasurementResult(distance, depth, speed, frequency, num, time,
                    DateFormat.format("dd.MM.yyyy", calendar).toString(), DateFormat.format("hh:mm:ss", calendar).toString(), new Status(status)));
        }

        return measResults;*/
    }

    @Override
    public void start(@NotNull MeasurementParameters parameters) throws SensorException {
        // todo setting distance and depth is not supported yet

        // prepare protocol command to be sent
        /*final ProtocolCommand request = new ProtocolCommand(
                ProtocolCommandCodes.REQUEST_START,              // command id
                String.format("%04d", parameters.getDistance()), // formatted 1st parameter
                String.format("%04d", parameters.getDepth())     // formatted 2nd parameter
        );*/

        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_START, ProtocolCommandCodes.RESPONSE_START);
        final String[]  params = ProtocolCommand.getParameters(response, "");

        if (params != null && params.length > 0) {
            if (Integer.valueOf(params[0]) != 0) {
                throw new SensorException("Sensor was already started. Now it is stopped.");
            } else {
                // OK
            }
        } else {
            throw new SensorException("Bad format.");
        }
    }

    @Override
    public void stop() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_STOP, ProtocolCommandCodes.RESPONSE_STOP);
        final String[]  params = ProtocolCommand.getParameters(response, "");

        if (params != null && params.length > 0) {
            if (Integer.valueOf(params[0]) != 1) {
                throw new SensorException("Sensor wasn't started before. Now it is started.");
            } else {
                // OK
            }
        } else {
            throw new SensorException("Bad format.");
        }
    }

    @NotNull
    public Integer getFrequencyOut() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_FREQUENCY_OUT, ProtocolCommandCodes.RESPONSE_FREQUENCY_OUT);
        final String[]  params = ProtocolCommand.getParameters(response, "");
        Integer result;

        if (params != null && params.length > 0) {
            result = Integer.valueOf(params[0]);
        } else {
            throw new SensorException("Bad format.");
        }

        return result;
    }

    @NotNull
    public Integer getVelocityOut() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_VELOCITY_OUT, ProtocolCommandCodes.RESPONSE_VELOCITY_OUT);
        final String[]  params = ProtocolCommand.getParameters(response, "");
        Integer result;

        if (params != null && params.length > 0) {
            result = Integer.valueOf(params[0]);
        } else {
            throw new SensorException("Bad format.");
        }

        return result;
    }

    @NotNull
    public Integer getTurnNumberOut() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_TURN_NUMBER_OUT, ProtocolCommandCodes.RESPONSE_TURN_NUMBER_OUT);
        final String[]  params = ProtocolCommand.getParameters(response, "");
        Integer result;

        if (params != null && params.length > 0) {
            result = Integer.valueOf(params[0]);
        } else {
            throw new SensorException("Bad format.");
        }

        return result;
    }

    @NotNull
    public Integer getMeasureTimeOut() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_MEASURE_TIME_OUT, ProtocolCommandCodes.RESPONSE_MEASURE_TIME_OUT);
        final String[]  params = ProtocolCommand.getParameters(response, "");
        Integer result;

        if (params != null && params.length > 0) {
            result = Integer.valueOf(params[0]);
        } else {
            throw new SensorException("Bad format.");
        }

        return result;
    }

    @Override
    @NotNull
    public Status getStatusOut() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_STATUS_OUT, ProtocolCommandCodes.RESPONSE_STATUS_OUT);
        final String[]  params = ProtocolCommand.getParameters(response, "");
        Byte result;

        if (params != null && params.length > 0) {
            result = Byte.valueOf(params[0]);
        } else {
            throw new SensorException("Bad format.");
        }

        return new Status(result);
    }

    @NotNull
    public String getRealTimeOut() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_REAL_TIME_OUT, ProtocolCommandCodes.RESPONSE_REAL_TIME_OUT);
        final String[]  params = ProtocolCommand.getParameters(response, "");
        String result;

        if (params != null && params.length > 0) {
            final String param = params[0];
            final Integer day = Integer.valueOf(param.substring(0, 2));
            final Integer month = Integer.valueOf(param.substring(2, 4));
            final Integer year = YEAR_BASE + Integer.valueOf(param.substring(4, 6));

            final GregorianCalendar calendar = new GregorianCalendar(year, month, day);

            result = DateFormat.format("dd.MM.yyyy", calendar).toString();
        } else {
            throw new SensorException("Bad format.");
        }

        return result;
    }

    @NotNull
    public String getRealDateOut() throws SensorException {
        // send prepared command
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_REAL_DATE_OUT, ProtocolCommandCodes.RESPONSE_REAL_DATE_OUT);
        final String[]  params = ProtocolCommand.getParameters(response, "");
        String result;

        if (params != null && params.length > 0) {
            final String param = params[0];
            final Integer hour = Integer.valueOf(param.substring(0, 2));
            final Integer minute = Integer.valueOf(param.substring(2, 4));
            final Integer second = YEAR_BASE + Integer.valueOf(param.substring(4, 6));

            final GregorianCalendar calendar = new GregorianCalendar(0, 0, 0, hour, minute, second);

            result = DateFormat.format("hh:mm:ss", calendar).toString();
        } else {
            throw new SensorException("Bad format.");
        }

        return result;
    }

    private String sendRequest(String request, String expectedResponse, Object... params) throws SensorException {
        final String response;
        try {
            // send request and receive response
            response = CommandSender.send(ProtocolCommand.prepareCommand(request, params), outputStreamWriter, inputStreamReader);

            if (! response.startsWith(expectedResponse)) {
                throw new SensorException("Unexpected response.");
            }

            ProtocolCommand.validate(response);
        } catch (IOException e) {
            throw new SensorException(e);
        }

        return response;
    }

    @NotNull
    public static BitSet toBitSet(byte b) {
        final BitSet bitSet = new BitSet(8);

        for (int i = 0; i < 8; i++) {
            bitSet.set(i, (b & (1 << i)) != 0);
        }

        return bitSet;
    }

}
