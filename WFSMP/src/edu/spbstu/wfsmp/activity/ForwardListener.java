package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    //private final Context baseContext;
    private final Activity from;

    public ForwardListener(@NotNull Class<? extends Activity> to, @NotNull Activity from) {
        this.to = to;
        this.from = from;
        //this.baseContext = this.from.getBaseContext();
    }

    @Override
    public void onClick(View view) {
        // ApplicationContext.debug(getClass(), "Dev info button clicked.");
        forwardTo();
    }

    public void forwardTo() {
        forwardActivity();
    }

    private void forwardActivity() {
        Log.i(ForwardListener.class.getName(), "Starting " + to.getName() + " activity.");
        final Intent intent = new Intent(from.getBaseContext(), to);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        from.startActivity(intent);
    }

}
