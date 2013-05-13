package edu.spbstu.wfsmp.activity;

import edu.spbstu.wfsmp.ApplicationContext;

/**
 * User: Artegz
 * Date: 03.05.13
 * Time: 19:31
 */
public class ActivityUtils {

    private ActivityUtils() {
        throw new AssertionError("Not instantiable.");
    }

    public static void disconnect() {
        ApplicationContext.unregisterDevice();
    }
}
