package edu.spbstu.wfsmp.sensor;

import org.jetbrains.annotations.NotNull;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 0:53
 */
public class LinearTable {

    @NotNull
    private short[][] linearTable;

    @NotNull
    private String tableId;

    public static LinearTable createLinearTable(@NotNull short[] frequencyValues,
                                                @NotNull short[] speedValues,
                                                @NotNull String tableId) {
        // preconditions
        if (frequencyValues.length < 2 && frequencyValues.length != speedValues.length) {
            throw new IllegalArgumentException("Number of frequency values and speed values should be same and must be greeter then 2.");
        }

        final int numValues = frequencyValues.length;
        final short[][] table = new short[2][numValues];

        System.arraycopy(frequencyValues, 0, table[0], 0, numValues);
        System.arraycopy(speedValues, 0, table[1], 0, numValues);

        return new LinearTable(table, tableId);
    }

    private LinearTable(@NotNull short[][] linearTable, @NotNull String tableId) {
        this.linearTable = linearTable;
    }

    @NotNull
    public short[][] getLinearTable() {
        return linearTable;
    }

    @NotNull
    public String getTableId() {
        return tableId;
    }
}
