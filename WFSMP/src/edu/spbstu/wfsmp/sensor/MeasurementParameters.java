package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:51
 */
public class MeasurementParameters {

    private int distance;

    private int depth;

    public MeasurementParameters(int distance, int depth) {
        this.distance = distance;
        this.depth = depth;
    }

    public int getDistance() {
        return distance;
    }

    public int getDepth() {
        return depth;
    }
}
