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
public class Measurement {

    @ComplexParameterPart(order = 0, numSymbols = 4)
    private int measurementId;

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

    @ComplexParameterPart(order = 5, numSymbols = 4)
    private int depth;

    public Measurement() {
    }

    public Measurement(int measurementId,
                       int estimatedSteed,
                       int measuredFrequency,
                       @NotNull Date timestamp,
                       int distance,
                       int depth) {
        this.measurementId = measurementId;
        this.estimatedSteed = estimatedSteed;
        this.measuredFrequency = measuredFrequency;
        this.timestamp = timestamp;
        this.distance = distance;
        this.depth = depth;
    }

    public int getMeasurementId() {
        return measurementId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        if (depth != that.depth) return false;
        if (distance != that.distance) return false;
        if (estimatedSteed != that.estimatedSteed) return false;
        if (measuredFrequency != that.measuredFrequency) return false;
        if (measurementId != that.measurementId) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = measurementId;
        result = 31 * result + estimatedSteed;
        result = 31 * result + measuredFrequency;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + distance;
        result = 31 * result + depth;
        return result;
    }
}
