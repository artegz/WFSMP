package edu.spbstu.wfsmp.sensor;

import android.text.format.DateFormat;
import edu.spbstu.wfsmp.ApplicationContext;
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

    private static final Object monitor = new Object();

    private static final int YEAR_BASE = 2000;

    @NotNull
    private final OutputStreamWriter outputStreamWriter;

    @NotNull
    private final InputStreamReader inputStreamReader;

    @NotNull
    private final Device device;

    public DeviceControllerImpl(@NotNull Device device) {
        this.device = device;
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
    public void clearDb() throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_CLEAR_DB, ProtocolCommandCodes.RESPONSE_CLEAR_DB);
                ProtocolCommand.validate(response);
                return null;
            }
        });
    }

    @Override
    public void save(@NotNull final MeasurementParameters parameters) throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String distance = String.format("%03d", parameters.getDistance());
                final String depth = String.format("%02d", parameters.getDepth());
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_SAVE + distance + depth, ProtocolCommandCodes.RESPONSE_SAVE);
                final String[] params = ProtocolCommand.getParameters(response, null);

                if (params != null && params.length > 0) {
                    if (!params.equals(distance + depth)) {
                        throw new SensorException("Unexpected response.");
                    } else {
                        // ok
                    }
                } else {
                    throw new SensorException("Bad format.");
                }

                return null;
            }
        });
    }

    @Override
    @NotNull
    public String getSerialNumber() throws SensorException {
        return doSyncOperation(new Operation<String>() {
            @Override
            public String doOperation() throws SensorException {
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
        });
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

        return new MeasurementResult(velocityOut, frequencyOut, 0, turnNumberOut, measureTimeOut, 0, realTimeOut, realDateOut, status);
    }

    @Override
    @NotNull
    public List<MeasurementResult> getDataBaseOut() throws SensorException {
        return doSyncOperation(new Operation<List<MeasurementResult>>() {

            @Override
            public List<MeasurementResult> doOperation() throws SensorException {
                final int dbSize = getDbSize();
                final List<MeasurementResult> measResults = new ArrayList<MeasurementResult>(dbSize);

                if (dbSize > 0) {

                    // todo test
        /*return Arrays.asList(new MeasurementResult(1, 2, 3, 4, 5, 6, "12:30", "01.01.2013", new Status((byte)127)),
                new MeasurementResult(1, 2, 3, 4, 5, 6, "12:30", "01.01.2013", new Status((byte)127)),
                new MeasurementResult(1, 2, 3, 4, 5, 6, "12:30", "01.01.2013", new Status((byte)127)));*/

                    final String response = sendRequest(ProtocolCommandCodes.REQUEST_DATA_BASE_OUT, ProtocolCommandCodes.RESPONSE_DATA_BASE_OUT);

                    ProtocolCommand.validate(response);

                    final String[] formattedMeasResults = ProtocolCommand.getParameters(response, " ");

                    for (String formattedResult : formattedMeasResults) {
                        final Byte status = Byte.valueOf(formattedResult.substring(0, 2));
                        final Integer distance = Integer.valueOf(formattedResult.substring(2, 6));
                        final Integer depth = Integer.valueOf(formattedResult.substring(6, 8));
                        final Integer velocity = Integer.valueOf(formattedResult.substring(8, 12));
                        final Integer frequency = Integer.valueOf(formattedResult.substring(12, 16));
                        final Integer numTurns = Integer.valueOf(formattedResult.substring(16, 20));
                        final Integer measTime = Integer.valueOf(formattedResult.substring(20, 24));
                        final Integer year = YEAR_BASE + Integer.valueOf(formattedResult.substring(24, 26));
                        final Integer month = Integer.valueOf(formattedResult.substring(26, 28));
                        final Integer day = Integer.valueOf(formattedResult.substring(28, 30));
                        final Integer hour = Integer.valueOf(formattedResult.substring(30, 32));
                        final Integer minute = Integer.valueOf(formattedResult.substring(32, 34));
                        final Integer second = Integer.valueOf(formattedResult.substring(34, 36));

                        final GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute, second);

                        final String realTimeOut = DateFormat.format("dd.MM.yyyy", calendar).toString();
                        final String realDateOut = DateFormat.format("hh:mm:ss", calendar).toString();
                        final Status statusOut = new Status(status);

                        measResults.add(new MeasurementResult(velocity, frequency, distance, numTurns, measTime, depth, realTimeOut, realDateOut, statusOut));
                    }
                }

                return measResults;
            }
        });
    }

    @Override
    public void start() throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_START, ProtocolCommandCodes.RESPONSE_START);
                final String[] params = ProtocolCommand.getParameters(response, null);

                if (params != null && params.length > 0) {
                    if (Integer.valueOf(params[0]) != 0) {
                        throw new SensorException("Sensor was already started. Now it is stopped.");
                    } else {
                        // OK
                    }
                } else {
                    throw new SensorException("Bad format.");
                }

                return null;
            }
        });
    }

    @Override
    public void stop() throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_STOP, ProtocolCommandCodes.RESPONSE_STOP);
                final String[] params = ProtocolCommand.getParameters(response, null);

                if (params != null && params.length > 0) {
                    if (Integer.valueOf(params[0]) != 1) {
                        throw new SensorException("Sensor wasn't started before. Now it is started.");
                    } else {
                        // OK
                    }
                } else {
                    throw new SensorException("Bad format.");
                }

                return null;
            }
        });
    }

    @NotNull
    public Integer getFrequencyOut() throws SensorException {
        return doSyncOperation(new Operation<Integer>() {
            @Override
            public Integer doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_FREQUENCY_OUT, ProtocolCommandCodes.RESPONSE_FREQUENCY_OUT);
                final String[] params = ProtocolCommand.getParameters(response, null);
                Integer result;

                if (params != null && params.length > 0) {
                    result = Integer.valueOf(params[0]);
                } else {
                    throw new SensorException("Bad format.");
                }

                return result;
            }
        });
    }

    @NotNull
    public Integer getVelocityOut() throws SensorException {
        return doSyncOperation(new Operation<Integer>() {
            @Override
            public Integer doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_VELOCITY_OUT, ProtocolCommandCodes.RESPONSE_VELOCITY_OUT);
                final String[] params = ProtocolCommand.getParameters(response, null);
                Integer result;

                if (params != null && params.length > 0) {
                    result = Integer.valueOf(params[0]);
                } else {
                    throw new SensorException("Bad format.");
                }

                return result;
            }
        });
    }

    @NotNull
    public Integer getTurnNumberOut() throws SensorException {
        return doSyncOperation(new Operation<Integer>() {
            @Override
            public Integer doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_TURN_NUMBER_OUT, ProtocolCommandCodes.RESPONSE_TURN_NUMBER_OUT);
                final String[] params = ProtocolCommand.getParameters(response, null);
                Integer result;

                if (params != null && params.length > 0) {
                    result = Integer.valueOf(params[0]);
                } else {
                    throw new SensorException("Bad format.");
                }

                return result;
            }
        });
    }

    @NotNull
    public Integer getMeasureTimeOut() throws SensorException {
        return doSyncOperation(new Operation<Integer>() {
            @Override
            public Integer doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_MEASURE_TIME_OUT, ProtocolCommandCodes.RESPONSE_MEASURE_TIME_OUT);
                final String[] params = ProtocolCommand.getParameters(response, null);
                Integer result;

                if (params != null && params.length > 0) {
                    result = Integer.valueOf(params[0]);
                } else {
                    throw new SensorException("Bad format.");
                }

                return result;
            }
        });
    }

    @Override
    @NotNull
    public Status getStatusOut() throws SensorException {
        return doSyncOperation(new Operation<Status>() {
            @Override
            public Status doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_STATUS_OUT, ProtocolCommandCodes.RESPONSE_STATUS_OUT);
                final String[] params = ProtocolCommand.getParameters(response, null);
                Byte result;

                if (params != null && params.length > 0) {
                    ApplicationContext.debug(getClass(), "Status:" + params[0]);
                    result = Byte.valueOf(params[0], 16);
                } else {
                    throw new SensorException("Bad format.");
                }

                return new Status(result);
            }
        });
    }

    @NotNull
    public String getRealTimeOut() throws SensorException {
        return doSyncOperation(new Operation<String>() {
            @Override
            public String doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_REAL_TIME_OUT, ProtocolCommandCodes.RESPONSE_REAL_TIME_OUT);
                final String[] params = ProtocolCommand.getParameters(response, null);
                String result;

                if (params != null && params.length > 0) {
                    final String param = params[0];
                    final Integer hour = Integer.valueOf(param.substring(0, 2));
                    final Integer minute = Integer.valueOf(param.substring(2, 4));
                    final Integer second = Integer.valueOf(param.substring(4, 6));

                    final GregorianCalendar calendar = new GregorianCalendar(0, 0, 0, hour, minute, second);

                    result = DateFormat.format("hh:mm:ss", calendar).toString();
                } else {
                    throw new SensorException("Bad format.");
                }

                return result;
            }
        });
    }

    @NotNull
    public String getRealDateOut() throws SensorException {
        return doSyncOperation(new Operation<String>() {
            @Override
            public String doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCommandCodes.REQUEST_REAL_DATE_OUT, ProtocolCommandCodes.RESPONSE_REAL_DATE_OUT);
                final String[] params = ProtocolCommand.getParameters(response, null);
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
        });
    }

    private String sendRequest(String request, String expectedResponse) throws SensorException {
        final String response;
        try {
            // send request and receive response
            response = CommandSender.send(ProtocolCommand.prepareCommand(request), outputStreamWriter, inputStreamReader);

            if (!response.startsWith(expectedResponse)) {
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

    private void purgeBuffers() throws SensorException {
        try {
            this.device.purgeRx();
            this.device.purgeTx();
        } catch (IOException e) {
            throw new SensorException(e);
        }
    }

    private int getDbSize() throws SensorException {
        final String response = sendRequest(ProtocolCommandCodes.REQUEST_DB_SIZE, ProtocolCommandCodes.RESPONSE_DB_SIZE);
        ProtocolCommand.validate(response);
        final String[] parameters = ProtocolCommand.getParameters(response, null);
        final int dbSize;

        if (parameters != null && parameters.length == 1) {
            final String sizeParam = parameters[0];
            dbSize = Integer.parseInt(sizeParam);
        } else {
            throw new SensorException("Bad format.");
        }

        return dbSize;
    }

    private <T> T doSyncOperation(@NotNull Operation<? extends T> op) throws SensorException {
        final T result;

        synchronized (monitor) {
            //purgeBuffers();
            result = op.doOperation();
        }

        return result;
    }

    private static interface Operation<T> {
        T doOperation() throws SensorException;
    }

}
