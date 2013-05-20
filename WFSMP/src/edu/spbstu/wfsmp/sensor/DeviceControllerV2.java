package edu.spbstu.wfsmp.sensor;

import edu.spbstu.wfsmp.driver.Device;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * User: Artegz
 * Date: 18.05.13
 * Time: 17:44
 */
public class DeviceControllerV2 extends DeviceControllerImpl {

    public DeviceControllerV2(@NotNull Device device) {
        super(device);
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
                        final Integer depth = Integer.valueOf(formattedResult.substring(6, 10));
                        final Integer velocity = Integer.valueOf(formattedResult.substring(10, 14));
                        final Integer frequency = Integer.valueOf(formattedResult.substring(14, 18));
                        final Integer numTurns = Integer.valueOf(formattedResult.substring(18, 22));
                        final Integer measTime = Integer.valueOf(formattedResult.substring(22, 26));
                        final Integer year = YEAR_BASE + Integer.valueOf(formattedResult.substring(26, 28));
                        final Integer month = Integer.valueOf(formattedResult.substring(28, 30));
                        final Integer day = Integer.valueOf(formattedResult.substring(30, 32));
                        final Integer hour = Integer.valueOf(formattedResult.substring(32, 34));
                        final Integer minute = Integer.valueOf(formattedResult.substring(34, 36));
                        final Integer second = Integer.valueOf(formattedResult.substring(36, 38));

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
    public void save(@NotNull final MeasurementParameters parameters) throws SensorException {
        doSyncOperation(new Operation<Void>() {
            @Override
            public Void doOperation() throws SensorException {
                // send prepared command
                final String distance = String.format("%03d", parameters.getDistance());
                final String depth = String.format("%04d", (int) parameters.getDepth() * 10);
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
}
