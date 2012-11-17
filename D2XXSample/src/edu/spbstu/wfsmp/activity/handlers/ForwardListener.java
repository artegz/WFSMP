package edu.spbstu.wfsmp.activity.handlers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.activity.ShowInfoActivity;
import org.jetbrains.annotations.NotNull;

/**
* User: artegz
* Date: 05.11.12
* Time: 16:45
*/
public class ForwardListener implements View.OnClickListener {

    @NotNull
    private final Class<? extends Activity> to;

    @NotNull
    private final Activity from;

    public ForwardListener(@NotNull Class<? extends Activity> to, @NotNull Activity from) {
        this.to = to;
        this.from = from;
    }

    @Override
    public void onClick(View view) {
        // ApplicationContext.debug(getClass(), "Dev info button clicked.");
        Log.i(ForwardListener.class.getName(), "Starting " + to.getName() + " activity.");
        from.startActivity(new Intent(from.getBaseContext(), to));
    }
}
