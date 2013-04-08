package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:47
 */

/**
 * Java representation for single measurement result.
 */
public class MeasurementResult {

    private Integer velocity;

    private Integer frequency;

    private Integer distance;

    private Integer turns;

    private Integer measTime;

    private Integer depth;

    private String realTime;

    private String realDate;

    private Status status;

    public MeasurementResult(Integer velocity,
                             Integer frequency,
                             Integer distance,
                             Integer turns,
                             Integer measTime,
                             Integer depth,
                             String realTime,
                             String realDate,
                             Status status) {
        this.velocity = velocity;
        this.frequency = frequency;
        this.distance = distance;
        this.turns = turns;
        this.measTime = measTime;
        this.depth = depth;
        this.realTime = realTime;
        this.realDate = realDate;
        this.status = status;
    }

    public Integer getVelocity() {
        return velocity;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getTurns() {
        return turns;
    }

    public Integer getMeasTime() {
        return measTime;
    }

    public Integer getDepth() {
        return depth;
    }

    public String getRealTime() {
        return realTime;
    }

    public String getRealDate() {
        return realDate;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasurementResult that = (MeasurementResult) o;

        if (realDate != null ? !realDate.equals(that.realDate) : that.realDate != null) return false;
        if (depth != null ? !depth.equals(that.depth) : that.depth != null) return false;
        if (distance != null ? !distance.equals(that.distance) : that.distance != null) return false;
        if (frequency != null ? !frequency.equals(that.frequency) : that.frequency != null) return false;
        if (measTime != null ? !measTime.equals(that.measTime) : that.measTime != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (realTime != null ? !realTime.equals(that.realTime) : that.realTime != null) return false;
        if (turns != null ? !turns.equals(that.turns) : that.turns != null) return false;
        if (velocity != null ? !velocity.equals(that.velocity) : that.velocity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = velocity != null ? velocity.hashCode() : 0;
        result = 31 * result + (frequency != null ? frequency.hashCode() : 0);
        result = 31 * result + (distance != null ? distance.hashCode() : 0);
        result = 31 * result + (turns != null ? turns.hashCode() : 0);
        result = 31 * result + (measTime != null ? measTime.hashCode() : 0);
        result = 31 * result + (depth != null ? depth.hashCode() : 0);
        result = 31 * result + (realTime != null ? realTime.hashCode() : 0);
        result = 31 * result + (realDate != null ? realDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
