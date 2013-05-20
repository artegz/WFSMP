package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:51
 */
public class MeasurementParameters {

    private int distance;

    private double depth;

    public MeasurementParameters(int distance, int depth) {
        this.distance = distance;
        this.depth = depth;
    }

    public int getDistance() {
        return distance;
    }

    public double getDepth() {
        return depth;
    }
}
