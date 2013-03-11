package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:47
 */

import edu.spbstu.wfsmp.sensor.command.ComplexParameter;
import edu.spbstu.wfsmp.sensor.command.ComplexParameterPart;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Java representation for single measurement result.
 */
@ComplexParameter
public class MeasurementResult {

    @ComplexParameterPart(order = 0, numSymbols = 4)
    private int measNo;

    @ComplexParameterPart(order = 1, numSymbols = 4)
    private int estimatedSteed;

    @ComplexParameterPart(order = 2, numSymbols = 4)
    private int measuredFrequency;

    // in protocol consist from two 4char words - date and time
    @NotNull
    @ComplexParameterPart(order = 3, numSymbols = 4)
    private Date timestamp;

    @ComplexParameterPart(order = 4, numSymbols = 4)
    private int distance;

    private int turns;

    private int time;

    private int type;

    @ComplexParameterPart(order = 5, numSymbols = 4)
    private int depth;

    public MeasurementResult(int measNo,
                             int distance,
                             int depth,
                             int estimatedSteed,
                             int measuredFrequency,
                             int turns,
                             int time,
                             int type,
                             @NotNull Date timestamp) {
        this.measNo = measNo;
        this.estimatedSteed = estimatedSteed;
        this.measuredFrequency = measuredFrequency;
        this.timestamp = timestamp;
        this.distance = distance;
        this.turns = turns;
        this.time = time;
        this.type = type;
        this.depth = depth;
    }

    public int getMeasNo() {
        return measNo;
    }

    public int getEstimatedSteed() {
        return estimatedSteed;
    }

    public int getMeasuredFrequency() {
        return measuredFrequency;
    }

    @NotNull
    public Date getTimestamp() {
        return timestamp;
    }

    public int getDistance() {
        return distance;
    }

    public int getDepth() {
        return depth;
    }

    public int getTurns() {
        return turns;
    }

    public int getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasurementResult that = (MeasurementResult) o;

        if (depth != that.depth) return false;
        if (distance != that.distance) return false;
        if (estimatedSteed != that.estimatedSteed) return false;
        if (measuredFrequency != that.measuredFrequency) return false;
        if (measNo != that.measNo) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = measNo;
        result = 31 * result + estimatedSteed;
        result = 31 * result + measuredFrequency;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + distance;
        result = 31 * result + depth;
        return result;
    }
}
