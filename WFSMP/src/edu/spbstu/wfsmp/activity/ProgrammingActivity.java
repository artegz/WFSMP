package edu.spbstu.wfsmp.activity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import edu.spbstu.wfsmp.ApplicationContext;
import edu.spbstu.wfsmp.sensor.LinearTable;
import edu.spbstu.wfsmp.sensor.SensorException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:05
 */
public class ProgrammingActivity extends AbstractWfsmpActivity {

    public static final int[] FREQ_120_MM_FIELDS = new int[]{R.id.freq120mm1, R.id.freq120mm2, R.id.freq120mm3, R.id.freq120mm4, R.id.freq120mm5, R.id.freq120mm6};
    public static final int[] VEL_120_MM_FIELDS = new int[]{R.id.vel120mm1, R.id.vel120mm2, R.id.vel120mm3, R.id.vel120mm4, R.id.vel120mm5, R.id.vel120mm6};
    public static final int[] FREQ_70_MM_FIELDS = new int[]{R.id.freq70mm1, R.id.freq70mm2, R.id.freq70mm3, R.id.freq70mm4, R.id.freq70mm5, R.id.freq70mm6};
    public static final int[] VEL_70_MM_FIELDS = new int[]{R.id.vel70mm1, R.id.vel70mm2, R.id.vel70mm3, R.id.vel70mm4, R.id.vel70mm5, R.id.vel70mm6};

    public static final int NUM_POINTS_PER_WHIRLIGIG = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.programming);

        findViewById(R.id.linTableReadBtn).setOnClickListener(new OnReadLinearTableListener());
        findViewById(R.id.libTableWriteBtn).setOnClickListener(new OnApplyLinearTableListener());
        findViewById(R.id.progDevInfoReadBtn).setOnClickListener(new OnReadSystemInfoListener());
        findViewById(R.id.progDevInfoWriteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText serialNumberField = (EditText) findViewById(R.id.progSerialNumberValue);
                final EditText softwareVersionField = (EditText) findViewById(R.id.progSoftVersion);
                boolean error = false;

                if (serialNumberField.getText().length() <= 0 || softwareVersionField.getText().length() <= 0) {
                    showMessage("Failed. Both serial number and software version must be specified before apply.");
                }

                try {
                    Integer.valueOf(serialNumberField.getText().toString());
                } catch (NumberFormatException e) {
                    showMessage("Invalid serial number.");
                    error = true;
                }

                if (!error) {
                    final String serialNumber = serialNumberField.getText().toString();
                    final String softwareVersion = softwareVersionField.getText().toString();

                    try {
                        ApplicationContext.getInstance().getDeviceController().writeSerialNumber(serialNumber);
                        ApplicationContext.getInstance().getDeviceController().writeSoftwareVersion(softwareVersion);

                        showMessage("Values have been successfully applied.");
                    } catch (SensorException e) {
                        showMessage("Failed.");
                        ApplicationContext.handleException(getClass(), e);
                    }
                }
            }
        });
    }

    private void fillLinearTableFields(@NotNull List<LinearTable.Point> points120mm,
                                       int[] freqFields, int[] velFields, int numPointsPerWhirligig) {
        if (points120mm.size() != numPointsPerWhirligig) {
            ApplicationContext.warn(getClass(), points120mm.size() + " points read. Expected: " + numPointsPerWhirligig);
        }

        for (int i = 0; i < numPointsPerWhirligig; i++) {
            final LinearTable.Point point = points120mm.get(i);

            ((EditText) findViewById(freqFields[i])).setText(String.valueOf(point.getFrequency()));
            ((EditText) findViewById(velFields[i])).setText(String.valueOf(point.getVelocity()));
        }
    }

    private void fillTableWith120mmPoints(@NotNull LinearTable linearTable) throws SensorException {
        final List<LinearTable.Point> points = preparePoints(
                FREQ_120_MM_FIELDS, VEL_120_MM_FIELDS, NUM_POINTS_PER_WHIRLIGIG
        );
        for (LinearTable.Point point : points) {
            linearTable.addPoint120mm(point);
        }
    }
    
    private void fillTableWith70mmPoints(@NotNull LinearTable linearTable) throws SensorException {
        final List<LinearTable.Point> points = preparePoints(
                FREQ_70_MM_FIELDS, VEL_70_MM_FIELDS, NUM_POINTS_PER_WHIRLIGIG
        );
        for (LinearTable.Point point : points) {
            linearTable.addPoint70mm(point);
        }
    }

    @NotNull
    private List<LinearTable.Point> preparePoints(int[] freqFields, int[] speedFields, int count) throws SensorException {
        final List<LinearTable.Point> result = new ArrayList<LinearTable.Point>();

        for (int i = 0; i < count; i++) {
            result.add(new LinearTable.Point(
                    getNumericValueFromTextField(freqFields[i]),
                    getNumericValueFromTextField(speedFields[i])
            ));
        }

        return result;
    }

    private int getNumericValueFromTextField(int fieldId) throws SensorException {
        final Editable fieldText = ((EditText) findViewById(fieldId)).getText();
        
        if (fieldText.length() <= 0) {
            throw new SensorException("Missing value.");
        }
        
        final String fieldNativeText = fieldText.toString();
        final String numericValue = fieldNativeText.trim();

        final Integer result;
        
        try {
            result = Integer.valueOf(numericValue);
        } catch (NumberFormatException e) {
            throw new SensorException(e);
        }
        
        return result;
    }

    private class OnApplyLinearTableListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final LinearTable linearTable = new LinearTable();
            boolean error = false;

            try {
                fillTableWith120mmPoints(linearTable);
                fillTableWith70mmPoints(linearTable);
            } catch (SensorException e) {
                // may be thrown if input data is invalid
                showMessage("Operation failed. Input linear table invalid and can not be applied.");
                error = true;
            }
            
            if (!error) {
                try {
                    ApplicationContext.getInstance().getDeviceController().writeLinearTable(linearTable);
                    showMessage("New linear table has been successfully applied.");
                } catch (SensorException e) {
                    showMessage("Failed.");
                    ApplicationContext.handleException(getClass(), e);
                }
            }
        }
    }

    private class OnReadLinearTableListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                final LinearTable linearTable = ApplicationContext.getInstance().getDeviceController().readLinearTable();

                final List<LinearTable.Point> points120mm = linearTable.getPoints120mm();
                final List<LinearTable.Point> points70mm = linearTable.getPoints70mm();

                fillLinearTableFields(points120mm, FREQ_120_MM_FIELDS, VEL_120_MM_FIELDS, NUM_POINTS_PER_WHIRLIGIG);
                fillLinearTableFields(points70mm, FREQ_70_MM_FIELDS, VEL_70_MM_FIELDS, NUM_POINTS_PER_WHIRLIGIG);

                showMessage("Linear table has been successfully readen.");
            } catch (SensorException e) {
                showMessage("Failed.");
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }

    private class OnReadSystemInfoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                final String serialNumber = ApplicationContext.getInstance().getDeviceController().getSerialNumber();
                final String softwareVersion = ApplicationContext.getInstance().getDeviceController().getSoftwareVersion();

                ((EditText) findViewById(R.id.progSerialNumberValue)).setText(serialNumber);
                ((EditText) findViewById(R.id.progSoftVersion)).setText(softwareVersion);

                showMessage("Ok.");
            } catch (SensorException e) {
                showMessage("Failed.");
                ApplicationContext.handleException(getClass(), e);
            }
        }
    }
}
