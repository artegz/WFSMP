package edu.spbstu.wfsmp;

import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

/**
 * User: Artegz
 * Date: 03.05.13
 * Time: 19:19
 */
public class WfsmpUtils {

    private WfsmpUtils() {
        throw new AssertionError("Not instantiable.");
    }

    @NotNull
    public static BitSet toBitSet(byte b) {
        final BitSet bitSet = new BitSet(8);

        for (int i = 0; i < 8; i++) {
            bitSet.set(i, (b & (1 << i)) != 0);
        }

        return bitSet;
    }
}
