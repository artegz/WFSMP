package edu.spbstu.wfsmp.sensor;

import android.text.format.DateFormat;
import com.ftdi.j2xx.FT_Device;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.driver.Device;
import edu.spbstu.wfsmp.driver.DeviceException;
import edu.spbstu.wfsmp.driver.DeviceUtils;
import edu.spbstu.wfsmp.driver.j2xx.D2xxFTDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:46
 */
public class DeviceControllerImpl implements DeviceController {

    private static final Semaphore semaphore = new Semaphore(1);

    protected static final int YEAR_BASE = 2000;
    
    public static final int LINEAR_TABLE_70_MM_START = 0;
    public static final int LINEAR_TABLE_70_MM_END = 23;

    public static final int LINEAR_TABLE_120_MM_START = 24;
    public static final int LINEAR_TABLE_120_MM_END = 47;
    public static final int LINEAR_TABLE_NUM_POINTS = 6;
    public static final int SERAIL_NUMBER_START = 56;
    public static final int SERIAL_NUMBER_END = 57;
    public static final int INF_STRING_START = 60;
    public static final int INF_STRING_END = 76;

    @NotNull
    private final Device device;

    public DeviceControllerImpl(@NotNull Device device) {
        this.device = device;
    }

    @Override
    public void clearDb() throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                final String response = sendRequest(ProtocolCodes.REQUEST_CLEAR_DB, ProtocolCodes.RESPONSE_CLEAR_DB);
                ProtocolUtils.validate(response);
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
                final String depth = String.format("%02d", (int) parameters.getDepth());
                final String preparedRequest = ProtocolCodes.REQUEST_SAVE + distance + depth;

                final String response = sendRequest(preparedRequest, ProtocolCodes.RESPONSE_SAVE);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);

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
                final String response = sendRequest(ProtocolCodes.REQUEST_INF_STRING, ProtocolCodes.RESPONSE_INF_STRING);
                ProtocolUtils.validate(response);

                // receive version from response
                final String[] params = ProtocolUtils.getParameters(response, null);

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
    public String getSoftwareVersion() throws SensorException {
        return doSyncOperation(new Operation<String>() {
            @Override
            public String doOperation() throws SensorException {
                final String result;

                // prepare request command
                final String response = sendRequest(ProtocolCodes.REQUEST_SERIAL_NUMBER, ProtocolCodes.RESPONSE_SERIAL_NUMBER);
                ProtocolUtils.validate(response);

                // receive version from response
                final String[] params = ProtocolUtils.getParameters(response, null);

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
        final Date realDateOut = getRealDateOut();
        final Date realTimeOut = getRealTimeOut();
        final Status status = getStatusOut();
        final Integer direction = getDirection();

        return new MeasurementResult(velocityOut, frequencyOut, 0, turnNumberOut, measureTimeOut, 0, realTimeOut, realDateOut, status, direction);
    }

    @Override
    @NotNull
    public List<MeasurementResult> getDataBaseOut() throws SensorException {
        final int dbSize = getDbSize();

        return doSyncOperation(new Operation<List<MeasurementResult>>() {

            @Override
            public List<MeasurementResult> doOperation() throws SensorException {
                final List<MeasurementResult> measResults = new ArrayList<MeasurementResult>(dbSize);

                if (dbSize > 0) {
                    final String response = sendRequest(ProtocolCodes.REQUEST_DATA_BASE_OUT, ProtocolCodes.RESPONSE_DATA_BASE_OUT);
                    ProtocolUtils.validate(response);
                    final String[] formattedMeasResults = ProtocolUtils.getParameters(response, " ");

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

                        final Integer direction = 0; // todo asm: not supported by hardware yet

                        final GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, minute, second);

                        final Status statusOut = new Status(status);

                        measResults.add(new MeasurementResult(velocity, frequency, distance, numTurns, measTime, depth, calendar.getTime(), calendar.getTime(), statusOut, direction));
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
                final String response = sendRequest(ProtocolCodes.REQUEST_START, ProtocolCodes.RESPONSE_START);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);

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
                final String response = sendRequest(ProtocolCodes.REQUEST_STOP, ProtocolCodes.RESPONSE_STOP);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);

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

    @Override
    public int getDbSize() throws SensorException {
        return doSyncOperation(new Operation<Integer>() {
            @Override
            public Integer doOperation() throws SensorException {
                final String response = sendRequest(ProtocolCodes.REQUEST_DB_SIZE, ProtocolCodes.RESPONSE_DB_SIZE);
                ProtocolUtils.validate(response);
                final String[] parameters = ProtocolUtils.getParameters(response, null);

                final int dbSize;

                if (parameters != null && parameters.length == 1) {
                    final String sizeParam = parameters[0];
                    dbSize = Integer.parseInt(sizeParam);
                } else {
                    throw new SensorException("Bad format.");
                }

                return dbSize;
            }
        });
    }

    @Override
    public void turnOn() throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                if (device instanceof D2xxFTDevice) {
                    final FT_Device ftDevice = ((D2xxFTDevice) device).getNativeDriver();

                    // to turn off device - set 1 on sbus3 out for 2 seconds
                    ftDevice.setBitMode((byte) 0x88, (byte) 0x20);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ftDevice.setBitMode((byte) 0x80, (byte) 0x20);
                        }
                    }, 2100);
                }

                return null;
            }
        });
    }

    @Override
    public void turnOff() throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                return doSyncOperation(new Operation<Void>() {
                    @Override
                    public Void doOperation() throws SensorException {
                        final String response = sendRequest(ProtocolCodes.REQUEST_TURN_OFF, null);

                        if (response != null) {
                            // response not expected for current command, it may come only if is '?' response, lets check it
                            ProtocolUtils.validate(response);
                        }

                        return null;
                    }
                });
            }
        });
    }

    @Override
    public void setDisplayMode(@NotNull IndicationMode indicationMode) throws SensorException {
        final byte value;

        // 0-time;1-turns;2-freq;3-vel
        switch (indicationMode) {
            case velocity:
                value = 3;
                break;
            case turnNum:
                value = 1;
                break;
            case measTime:
                value = 0;
                break;
            case frequency:
                value = 2;
                break;
            default:
                throw new AssertionError("Unknown indication mode.");
        }

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String preparedCommand = ProtocolCodes.REQUEST_CHANGE_DISP_MODE + value;
                final String response = sendRequest(preparedCommand, ProtocolCodes.RESPONSE_CHANGE_DISP_MODE);
                ProtocolUtils.validate(response);
                // do not check params, if validation not failed - indication mode has been successfully applied
                return null;
            }
        });
    }

    @Override
    public void setWhirligigType(WhirligigType whirligigType) throws SensorException {
        final byte value;

        //  0-1:20;1-1:1;2-70:3-120
        switch (whirligigType) {
            case type_1_20:
                value = 0;
                break;
            case type_1_1:
                value = 1;
                break;
            case type_d_70mm:
                value = 2;
                break;
            case type_d_120mm:
                value = 3;
                break;
            default:
                throw new AssertionError("Unknown WhirligigType.");
        }

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String preparedCommand = ProtocolCodes.REQUEST_CHANGE_WH_TYPE + value;
                final String response = sendRequest(preparedCommand, ProtocolCodes.RESPONSE_CHANGE_WH_TYPE);
                ProtocolUtils.validate(response);
                // do not check params, if validation not failed - WhirligigType has been successfully applied
                return null;
            }
        });
    }

    @Override
    public void setSound(boolean enabled) throws SensorException {
        final byte value = enabled ? (byte) 1 : 0;

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String preparedCommand = ProtocolCodes.REQUEST_CHANGE_SOUND + value;
                final String response = sendRequest(preparedCommand, ProtocolCodes.RESPONSE_CHANGE_SOUND);
                ProtocolUtils.validate(response);
                // do not check params, if validation not failed - sound mode has been successfully applied
                return null;
            }
        });
    }

    @Override
    public void setSensEnable(boolean enabled) throws SensorException {
        final byte value = enabled ? (byte) 1 : 0;

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String preparedCommand = ProtocolCodes.REQUEST_CHANGE_CONTACT_CONTROL + value;
                final String response = sendRequest(preparedCommand, ProtocolCodes.RESPONSE_CHANGE_CONTACT_CONTROL);
                ProtocolUtils.validate(response);
                // do not check params, if validation not failed - sound mode has been successfully applied
                return null;
            }
        });
    }

    @Override
    public void setTime(@NotNull final Date time) throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String preparedRequest = ProtocolCodes.REQUEST_SET_TIME + DateFormat.format("hhmmss", time);
                final String response = sendRequest(preparedRequest, ProtocolCodes.RESPONSE_SET_TIME);
                ProtocolUtils.validate(response);
                // do not check params, if validation not failed - changes should be applied
                return null;
            }
        });
    }

    @Override
    public void setDate(@NotNull final Date date) throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String preparedRequest = ProtocolCodes.REQUEST_SET_DATE + DateFormat.format("ddMMyy", date);
                final String response = sendRequest(preparedRequest, ProtocolCodes.RESPONSE_SET_DATE);
                ProtocolUtils.validate(response);
                // do not check params, if validation not failed - changes should be applied
                return null;
            }
        });
    }

    @Override
    public LinearTable readLinearTable() throws SensorException {
        // todo asm: test it
        final LinearTable linearTable = new LinearTable();
        final byte[] linTable70mm = new byte[LINEAR_TABLE_70_MM_END - LINEAR_TABLE_70_MM_START + 1];
        final byte[] linTable120mm = new byte[LINEAR_TABLE_120_MM_END - LINEAR_TABLE_120_MM_START + 1];

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                readEepromBytes(linTable70mm, LINEAR_TABLE_70_MM_START, LINEAR_TABLE_70_MM_END);
                readEepromBytes(linTable120mm, LINEAR_TABLE_120_MM_START, LINEAR_TABLE_120_MM_END);
                
                for (int i = 0; i < LINEAR_TABLE_NUM_POINTS; i++) {
                    final byte linTable70mmVal1Hi = linTable70mm[4 * i];    // todo asm: correct it
                    final byte linTable70mmVal1Low = linTable70mm[4 * i + 1];
                    final byte linTable70mmVal2Hi = linTable70mm[4 * i + 2];
                    final byte linTable70mmVal2Low = linTable70mm[4 * i + 3];

                    final byte linTable120mmVal1Hi = linTable120mm[4 * i];
                    final byte linTable120mmVal1Low = linTable120mm[4 * i + 1];
                    final byte linTable120mmVal2Hi = linTable120mm[4 * i + 2];
                    final byte linTable120mmVal2Low = linTable120mm[4 * i + 3];
                    
                    linearTable.addPoint120mm(new LinearTable.Point(
                            ((linTable120mmVal1Hi << 8) + linTable120mmVal1Low),
                            ((linTable120mmVal2Hi << 8) + linTable120mmVal2Low)
                    ));

                    linearTable.addPoint70mm(new LinearTable.Point(
                            ((linTable70mmVal1Hi << 8) + linTable70mmVal1Low),
                            ((linTable70mmVal2Hi << 8) + linTable70mmVal2Low)
                    ));
                }

                return null;
            }
        });

        return linearTable;
    }

    @Override
    public void writeLinearTable(LinearTable linearTable) throws SensorException {
        // todo asm: test it
        final byte[] linTable70mm = new byte[LINEAR_TABLE_70_MM_END - LINEAR_TABLE_70_MM_START + 1];
        final byte[] linTable120mm = new byte[LINEAR_TABLE_120_MM_END - LINEAR_TABLE_120_MM_START + 1];

        for (int i = 0; i < LINEAR_TABLE_NUM_POINTS; i++) {
            final LinearTable.Point point = linearTable.getPoints120mm().get(i);
            
            linTable120mm[4 * i] = (byte) (point.getFrequency() >> 8);
            linTable120mm[4 * i + 1] = (byte) (point.getFrequency());
            linTable120mm[4 * i + 2] = (byte) (point.getVelocity() >> 8);
            linTable120mm[4 * i + 3] = (byte) (point.getVelocity());
        }

        for (int i = 0; i < LINEAR_TABLE_NUM_POINTS; i++) {
            final LinearTable.Point point = linearTable.getPoints70mm().get(i);

            linTable70mm[4 * i] = (byte) (point.getFrequency() >> 8);
            linTable70mm[4 * i + 1] = (byte) (point.getFrequency());
            linTable70mm[4 * i + 2] = (byte) (point.getVelocity() >> 8);
            linTable70mm[4 * i + 3] = (byte) (point.getVelocity());
        }

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                writeEepromData(linTable120mm, LINEAR_TABLE_120_MM_START, LINEAR_TABLE_120_MM_END);
                writeEepromData(linTable70mm, LINEAR_TABLE_70_MM_START, LINEAR_TABLE_70_MM_END);

                return null;
            }
        });
    }



    @Override
    public void writeSerialNumber(String serialNumber) throws SensorException {
        // todo asm: test it
        final byte[] bytes = new byte[2];
        /*try {
            bytes = serialNumber.getBytes(ProtocolUtils.PROTOCOL_COMMAND_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }*/

        final int sNumber = Integer.valueOf(serialNumber);

        bytes[0] = (byte) (sNumber >> 8); // hi
        bytes[1] = (byte) (sNumber); // low

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                writeEepromData(bytes, SERAIL_NUMBER_START, SERIAL_NUMBER_END);
                return null;
            }
        });
    }

    @Override
    public void writeSoftwareVersion(String softwareVersion) throws SensorException {
        // todo asm: test it
        final byte[] bytes;
        try {
            bytes = softwareVersion.getBytes(ProtocolUtils.PROTOCOL_COMMAND_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }

        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // todo asm: if inf string is shorter than required - fill with spaces
                final int endAddr = Math.min(INF_STRING_START + bytes.length, INF_STRING_END);
                writeEepromData(bytes, INF_STRING_START, endAddr);
                return null;
            }
        });
    }

    public Integer getDirection() throws SensorException {
        return doSyncOperation(new Operation<Integer>() {
            @Override
            public Integer doOperation() throws SensorException {
                return 0; // todo asm: not supported by hardware yet
            }
        });
    }

    @NotNull
    public Integer getFrequencyOut() throws SensorException {
        return doSyncOperation(new Operation<Integer>() {
            @Override
            public Integer doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCodes.REQUEST_FREQUENCY_OUT, ProtocolCodes.RESPONSE_FREQUENCY_OUT);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);
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
                final String response = sendRequest(ProtocolCodes.REQUEST_VELOCITY_OUT, ProtocolCodes.RESPONSE_VELOCITY_OUT);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);
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
                final String response = sendRequest(ProtocolCodes.REQUEST_TURN_NUMBER_OUT, ProtocolCodes.RESPONSE_TURN_NUMBER_OUT);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);
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
                final String response = sendRequest(ProtocolCodes.REQUEST_MEASURE_TIME_OUT, ProtocolCodes.RESPONSE_MEASURE_TIME_OUT);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);
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
                final String response = sendRequest(ProtocolCodes.REQUEST_STATUS_OUT, ProtocolCodes.RESPONSE_STATUS_OUT);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);
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
    public Date getRealTimeOut() throws SensorException {
        return doSyncOperation(new Operation<Date>() {
            @Override
            public Date doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCodes.REQUEST_REAL_TIME_OUT, ProtocolCodes.RESPONSE_REAL_TIME_OUT);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);
                Date result;

                if (params != null && params.length > 0) {
                    final String param = params[0];
                    final Integer hour = Integer.valueOf(param.substring(0, 2));
                    final Integer minute = Integer.valueOf(param.substring(2, 4));
                    final Integer second = Integer.valueOf(param.substring(4, 6));

                    final GregorianCalendar calendar = new GregorianCalendar(0, 0, 0, hour, minute, second);

                    result = calendar.getTime();
                } else {
                    throw new SensorException("Bad format.");
                }

                return result;
            }
        });
    }

    @NotNull
    public Date getRealDateOut() throws SensorException {
        return doSyncOperation(new Operation<Date>() {
            @Override
            public Date doOperation() throws SensorException {
                // send prepared command
                final String response = sendRequest(ProtocolCodes.REQUEST_REAL_DATE_OUT, ProtocolCodes.RESPONSE_REAL_DATE_OUT);
                ProtocolUtils.validate(response);
                final String[] params = ProtocolUtils.getParameters(response, null);
                Date result;

                if (params != null && params.length > 0) {
                    final String param = params[0];
                    final Integer day = Integer.valueOf(param.substring(0, 2));
                    final Integer month = Integer.valueOf(param.substring(2, 4));
                    final Integer year = YEAR_BASE + Integer.valueOf(param.substring(4, 6));

                    final GregorianCalendar calendar = new GregorianCalendar(year, month, day);

                    result = calendar.getTime();
                } else {
                    throw new SensorException("Bad format.");
                }

                return result;
            }
        });
    }

    @Nullable
    protected String sendRequest(@NotNull String request, @Nullable String expectedResponse) throws SensorException {
        final String response;
        final boolean responseExpected = expectedResponse != null;

        try {
            // send request and receive response
            response = DeviceUtils.send(ProtocolUtils.prepareCommand(request), device, responseExpected);

            if (responseExpected) {
                if (!response.startsWith(expectedResponse)) {
                    throw new SensorException("Unexpected response.");
                }

                ProtocolUtils.validate(response);
            }
        } catch (DeviceException e) {
            throw new SensorException(e.getMessage(), e);
        }

        return response;
    }

    private void readEepromBytes(byte[] linTable70mm, int startAddress, int endAddress) throws SensorException {
        int index = 0;
        for (int i = startAddress; i <= endAddress; i++, index++) {
            final String hexAddress = String.format("%02x", i).toUpperCase();
            final String preparedRequest = ProtocolCodes.REQUEST_READ_EEPROM_BYTE + hexAddress;
            final String response = sendRequest(preparedRequest, ProtocolCodes.RESPONSE_READ_EEPROM_BYTE);
            ProtocolUtils.validate(response);

            final String[] parameters = ProtocolUtils.getParameters(response, null);

            if (parameters != null && parameters.length == 1) {
                // first 2 chars - address, skip them
                linTable70mm[index] = ((byte) Integer.parseInt(parameters[0].substring(2), 16));
            } else {
                throw new SensorException("Bad format.");
            }
        }
    }

    private void writeEepromData(byte[] bytes, int start, int end) throws SensorException {
        for (int i = start; i <= end; i++) {
            final String address = String.format("%02x", i);
            final String data = String.format("%02x", bytes[i]);
            final String preparedRequest = ProtocolCodes.REQUEST_WRITE_EEPROM_BYTE + address + data;
            final String response = sendRequest(preparedRequest, ProtocolCodes.RESPONSE_WRITE_EEPROM_BYTE);
            ProtocolUtils.validate(response);
            // validation succeeded - ok
        }
    }

    protected  <T> T doSyncOperation(@NotNull Operation<? extends T> op) throws SensorException {
        final T result;

        try {
            if (semaphore.tryAcquire(10, TimeUnit.SECONDS)) {
                try {
                    result = op.doOperation();
                } catch (SensorException e) {
                    try {
                        device.purgeRx();
                        device.purgeTx();
                    } catch (IOException e1) {
                        ApplicationContext.handleException(getClass(), e1);
                    }
                    throw e;
                }
            } else {
                throw new SensorException("Device blocked.");
            }
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        } finally {
            semaphore.release();
        }

        return result;
    }

    protected static interface Operation<T> {
        T doOperation() throws SensorException;
    }

}
