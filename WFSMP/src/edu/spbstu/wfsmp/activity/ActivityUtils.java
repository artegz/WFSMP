package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import edu.spbstu.wfsmp.ApplicationContext;
import org.jetbrains.annotations.NotNull;

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

    public static void forwardToActivity(@NotNull Class<? extends Activity> to, @NotNull Context context) {
        Log.i(ForwardListener.class.getName(), "Starting " + to.getName() + " activity.");
        final Intent intent = new Intent(context, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent, new Bundle());
    }
}
