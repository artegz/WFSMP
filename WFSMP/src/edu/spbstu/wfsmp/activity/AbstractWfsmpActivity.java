package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import edu.spbstu.wfsmp.ApplicationContext;

/**
 * User: Artegz
 * Date: 02.05.13
 * Time: 19:05
 */
abstract class AbstractWfsmpActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    protected void showMessage(final String message) {
        showMessage(message, new Handler());
    }

    protected void showMessage(final String message, Handler handler) {
        Log.i(getClass().getName(), message);
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.statusRow)).setText(message);
            }
        });
    }

    protected void showMessage(final int textId) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.statusRow)).setText(textId);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_measurement:
                if (! (this instanceof MeasurementActivity)) {
                    forwardTo(MeasurementActivity.class);
                }
                break;
            case R.id.menu_preferences:
                forwardTo(PreferencesActivity.class);
                break;
            case R.id.menu_programming:
                forwardTo(ProgrammingActivity.class);
                break;
            case R.id.menu_info:
                forwardTo(ShowInfoActivity.class);
                break;
            case R.id.disconnect:
                ApplicationContext.unregisterDevice();
                forwardTo(SelectDeviceActivity.class);
                break;
            default:
                throw new UnsupportedOperationException("Selected menu item is not supported.");
        }


        return super.onOptionsItemSelected(item);
    }

    private void forwardTo(Class<? extends Activity> to) {
        final Intent intent = new Intent(this, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (! (this instanceof MeasurementActivity)) {
            forwardTo(MeasurementActivity.class);
        }
    }
}
