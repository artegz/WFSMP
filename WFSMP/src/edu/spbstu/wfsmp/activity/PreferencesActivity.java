package edu.spbstu.wfsmp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.sensor.IndicationMode;
import edu.spbstu.wfsmp.sensor.SensorException;
import edu.spbstu.wfsmp.sensor.Status;
import edu.spbstu.wfsmp.sensor.WhirligigType;

import java.util.Date;

/**
 * User: Artegz
 * Date: 02.05.13
 * Time: 21:23
 */
public class PreferencesActivity extends AbstractWfsmpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preferences);

        findViewById(R.id.prefApplyBtn).setOnClickListener(new OnApplyListener());
        findViewById(R.id.prefReadBtn).setOnClickListener(new OnReadListener());
        findViewById(R.id.syncDevTimeBtn).setOnClickListener(new OnSynchronizeTimeListener());
    }

    private int detectRadioId(WhirligigType whirligigType) {
        int a;

        switch (whirligigType) {
            case type_1_1:
                a = R.id.psvMode11;
                break;
            case type_1_20:
                a = R.id.psvMode1_20;
                break;
            case type_d_120mm:
                a = R.id.psvMode120;
                break;
            case type_d_70mm:
                a = R.id.psvMode70;
                break;
            default:
                throw new AssertionError("Not supported whirligig type.");
        }
        return a;
    }

    private int detectRadioId(IndicationMode indicationMode) {
        int displayModeId;

        switch (indicationMode) {
            case frequency:
                displayModeId = R.id.displayModeFreq;
                break;
            case measTime:
                displayModeId = R.id.displayModeTime;
                break;
            case turnNum:
                displayModeId = R.id.displayModeNumTurns;
                break;
            case velocity:
                displayModeId = R.id.displayModeVel;
                break;
            default:
                throw new AssertionError("Not supported indication type.");
        }
        return displayModeId;
    }

    private class OnApplyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final int checkedSensEnableRadioId = ((RadioGroup) findViewById(R.id.ccGrp)).getCheckedRadioButtonId();
            final int checkedSoundEnableRadioId = ((RadioGroup) findViewById(R.id.soundGrp)).getCheckedRadioButtonId();
            final int checkedDisplayModeRadioId = ((RadioGroup) findViewById(R.id.dispModeGrp)).getCheckedRadioButtonId();
            final int checkedWhirligigRadioId = ((RadioGroup) findViewById(R.id.psvWhTypeGrp)).getCheckedRadioButtonId();

            boolean error = false;

            // set sense enable
            if (checkedSensEnableRadioId >= 0) {
                boolean senseEnable = checkedSensEnableRadioId == R.id.addContrOn;
                try {
                    ApplicationContext.getInstance().getDeviceController().setSensEnable(senseEnable);
                } catch (SensorException e) {
                    error = true;
                    showMessage("Set sens enable failed.");
                    ApplicationContext.handleException(getClass(), e);
                }
            }

            // set sound
            if (checkedSoundEnableRadioId >= 0) {
                boolean soundOn = checkedSoundEnableRadioId == R.id.soundOn;
                try {
                    ApplicationContext.getInstance().getDeviceController().setSound(soundOn);
                } catch (SensorException e) {
                    error = true;
                    showMessage("Set sound failed.");
                    ApplicationContext.handleException(getClass(), e);
                }
            }

            // set indication mode
            if (checkedDisplayModeRadioId >= 0) {
                final IndicationMode indicationMode;

                switch (checkedDisplayModeRadioId) {
                    case R.id.displayModeFreq:
                        indicationMode = IndicationMode.frequency;
                        break;
                    case R.id.displayModeTime:
                        indicationMode = IndicationMode.measTime;
                        break;
                    case R.id.displayModeNumTurns:
                        indicationMode = IndicationMode.turnNum;
                        break;
                    case R.id.displayModeVel:
                        indicationMode = IndicationMode.velocity;
                        break;
                    default:
                        throw new AssertionError("Not supported indication type.");
                }

                try {
                    ApplicationContext.getInstance().getDeviceController().setDisplayMode(indicationMode);
                } catch (SensorException e) {
                    error = true;
                    showMessage("Set indication mode failed.");
                    ApplicationContext.handleException(getClass(), e);
                }
            }

            // set indication mode
            if (checkedWhirligigRadioId >= 0) {
                final WhirligigType whirligigType;

                switch (checkedWhirligigRadioId) {
                    case R.id.psvMode11:
                        whirligigType = WhirligigType.type_1_1;
                        break;
                    case R.id.psvMode1_20:
                        whirligigType = WhirligigType.type_1_20;
                        break;
                    case R.id.psvMode120:
                        whirligigType = WhirligigType.type_d_120mm;
                        break;
                    case R.id.psvMode70:
                        whirligigType = WhirligigType.type_d_70mm;
                        break;
                    default:
                        throw new AssertionError("Not supported whirligig type.");
                }

                try {
                    error = true;
                    ApplicationContext.getInstance().getDeviceController().setWhirligigType(whirligigType);
                } catch (SensorException e) {
                    showMessage("Set whirligig type failed.");
                    ApplicationContext.handleException(getClass(), e);
                }
            }

            if (!error) {
                showMessage("Settings successfully applied.");
            }

        }
    }

    private class OnReadListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                final Status statusOut = ApplicationContext.getInstance().getDeviceController().getStatusOut();

                final WhirligigType whirligigType = statusOut.getWhirligigType();
                final IndicationMode indicationMode = statusOut.getIndicationMode();
                final boolean bipTurnEnable = statusOut.isBipTurnEnable();
                final boolean sensEnable = statusOut.isSensEnable();

                ((RadioGroup) findViewById(R.id.ccGrp)).check(sensEnable ? R.id.addContrOn : R.id.addContrOff);
                ((RadioGroup) findViewById(R.id.soundGrp)).check(bipTurnEnable ? R.id.soundOn : R.id.sounfOff);
                ((RadioGroup) findViewById(R.id.dispModeGrp)).check(detectRadioId(indicationMode));
                ((RadioGroup) findViewById(R.id.psvWhTypeGrp)).check(detectRadioId(whirligigType));

                showMessage("Settings successfully read.");
            } catch (SensorException e) {
                ApplicationContext.handleException(getClass(), e);
                showMessage("Error occurred.");
            }
        }
    }

    private class OnSynchronizeTimeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Date currentDate = new Date();

            try {
                ApplicationContext.getInstance().getDeviceController().setDate(currentDate);
                ApplicationContext.getInstance().getDeviceController().setTime(currentDate);

                showMessage("Date synchronized.");
            } catch (SensorException e) {
                showMessage("Setting date failed.");
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }
}
