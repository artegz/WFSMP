package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

/**
* Created with IntelliJ IDEA.
* User: Artegz
* Date: 01.04.13
* Time: 18:29
* To change this template use File | Settings | File Templates.
*/
public final class Status {

    @NotNull
    private BitSet bitSet;

    public Status(byte b) {
        this.bitSet = DeviceControllerImpl.toBitSet(b);
    }

    public boolean isSensEnable() {
        return bitSet.get(7);
    }

    public boolean isBipTurnEnable() {
        return bitSet.get(6);
    }

    public boolean isStarted() {
        return bitSet.get(5);
    }

    public boolean isNewDataExists() {
        return bitSet.get(4);
    }

    public IndicationMode getIndicationMode() {
        if (bitSet.get(3) && bitSet.get(2)) {
            // 1 1
            return IndicationMode.measTime;
        } else if (bitSet.get(3)) {
            // 1 0
            return IndicationMode.frequency;
        } else if (bitSet.get(2)) {
            // 0 1
            return IndicationMode.turnNum;
        } else {
            // 0 0
            return IndicationMode.velocity;
        }
    }

    public WhirligigType getWhirligigType() {
        if (bitSet.get(1) && bitSet.get(0)) {
            return WhirligigType.type_d_120mm;
        } else if (bitSet.get(0)) {
            return WhirligigType.type_d_70mm;
        } else if (bitSet.get(1)) {
            return WhirligigType.type_1_1;
        } else {
            return WhirligigType.type_1_20;
        }
    }

    public static enum IndicationMode {
        measTime,
        turnNum,
        frequency,
        velocity
    }

    public static enum WhirligigType {
        type_1_20,
        type_1_1,
        type_d_70mm,
        type_d_120mm
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int i = bitSet.size(); i >= 0; i--) {
             sb.append(bitSet.get(i) ? "1" : "0");
        }

        return sb.toString();
    }
}
