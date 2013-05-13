package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Artegz
 * Date: 03.05.13
 * Time: 21:41
 */
public class LinearTable {

    @NotNull
    private List<Point> points70mm = new ArrayList<Point>(6);

    @NotNull
    private List<Point> points120mm = new ArrayList<Point>(6);

    @NotNull
    public List<Point> getPoints70mm() {
        return points70mm;
    }

    @NotNull
    public List<Point> getPoints120mm() {
        return points120mm;
    }

    public void addPoint70mm(@NotNull Point point) {
        points70mm.add(point);
    }

    public void addPoint120mm(@NotNull Point point) {
        points120mm.add(point);
    }

    public static class Point {

        private int frequency;

        private int velocity;

        public Point(int frequency, int velocity) {
            this.frequency = frequency;
            this.velocity = velocity;
        }

        public int getVelocity() {
            return velocity;
        }

        public int getFrequency() {
            return frequency;
        }
    }

}
