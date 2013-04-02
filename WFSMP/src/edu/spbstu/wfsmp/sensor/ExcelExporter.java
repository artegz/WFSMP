package edu.spbstu.wfsmp.sensor;

import android.os.Environment;
import android.util.Log;
import edu.spbstu.wfsmp.ApplicationContext;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * User: Artegz
 * Date: 01.04.13
 * Time: 19:34
 */
public class ExcelExporter {

    public void doExportAll() throws SensorException {
        final List<MeasurementResult> measurementResults = ApplicationContext.getInstance().getDeviceController().getDataBaseOut();
        doExport(measurementResults);
    }

    public void doExport(@NotNull List<MeasurementResult> measurementResults) {
        final Date currentDate = new Date();
        final String filename = String.valueOf(currentDate.getTime()) + ".xls";

        try {
            final WritableWorkbook workbook = createWorkbook(filename);
            final WritableSheet sheet = createSheet(workbook, "Results", 1);

            // print headers
            writeCell(1, 1, "#", true, sheet);
            writeCell(2, 1, "Distance", true, sheet);
            writeCell(3, 1, "Depth", true, sheet);
            writeCell(4, 1, "Velocity", true, sheet);
            writeCell(5, 1, "Frequency", true, sheet);
            writeCell(6, 1, "Turns", true, sheet);
            writeCell(7, 1, "Measurement time", true, sheet);
            writeCell(8, 1, "Whirligig type", true, sheet);
            writeCell(9, 1, "Date", true, sheet);

            for (int i = 0; i < measurementResults.size(); i++) {
                final MeasurementResult measurementResult = measurementResults.get(i);

                writeCell(1, 2 + i, String.valueOf(i), false, sheet);
                writeCell(2, 2 + i, String.valueOf(measurementResult.getDistance()), false, sheet);
                writeCell(3, 2 + i, String.valueOf(measurementResult.getDepth()), false, sheet);
                writeCell(4, 2 + i, String.valueOf(measurementResult.getVelocity()), false, sheet);
                writeCell(5, 2 + i, String.valueOf(measurementResult.getFrequency()), false, sheet);
                writeCell(6, 2 + i, String.valueOf(measurementResult.getTurns()), false, sheet);
                writeCell(7, 2 + i, String.valueOf(measurementResult.getMeasTime()), false, sheet);
                final Status status = measurementResult.getStatus();
                if (status != null) {
                    writeCell(8, 2 + i, String.valueOf(status.getWhirligigType()), false, sheet);
                } else {
                    writeCell(8, 2 + i, "-", false, sheet);
                }
                writeCell(9, 2 + i, String.valueOf(measurementResult.getDate() + " " + measurementResult.getTime()), false, sheet);
            }

            workbook.write();
            workbook.close();

        } catch (WriteException e) {
            ApplicationContext.handleException(getClass(), e);
            throw new AssertionError(e);
        } catch (IOException e) {
            ApplicationContext.handleException(getClass(), e);
            throw new AssertionError(e);
        }
    }


    /**
     *
     * @param fileName - the name to give the new workbook file
     * @return - a new WritableWorkbook with the given fileName
     */
    private WritableWorkbook createWorkbook(String fileName) throws IOException {
        //exports must use a temp file while writing to avoid memory hogging
        final WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setUseTemporaryFileDuringWrite(true);

        if (! android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            throw new AssertionError("SDCard is not accessible.");
        }

        //get the sdcard's directory
        final File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        //create a standard java.io.File object for the Workbook to use
        final File wbfile = new File(sdCard, fileName);

        if (! wbfile.exists()) {
            final boolean newFile = wbfile.createNewFile();

            if (! newFile) {
                throw new AssertionError("Unable to create file.");
            }
        }

        WritableWorkbook wb;

        try{
            //create a new WritableWorkbook using the java.io.File and
            //WorkbookSettings from above
            wb = Workbook.createWorkbook(wbfile, wbSettings);
        } catch(IOException ex) {
            ApplicationContext.handleException(getClass(), ex);
            throw new AssertionError(ex);
        }

        return wb;
    }

    /**
     *
     * @param wb - WritableWorkbook to create new sheet in
     * @param sheetName - name to be given to new sheet
     * @param sheetIndex - position in sheet tabs at bottom of workbook
     * @return - a new WritableSheet in given WritableWorkbook
     */
    private WritableSheet createSheet(WritableWorkbook wb,
                                     String sheetName,
                                     int sheetIndex){
        //create a new WritableSheet and return it
        return wb.createSheet(sheetName, sheetIndex);
    }

    /**
     *
     * @param columnPosition - column to place new cell in
     * @param rowPosition - row to place new cell in
     * @param contents - string value to place in cell
     * @param headerCell - whether to give this cell special formatting
     * @param sheet - WritableSheet to place cell in
     * @throws RowsExceededException - thrown if adding cell exceeds .xls row limit
     * @throws WriteException - Idunno, might be thrown
     */
    private void writeCell(int columnPosition, int rowPosition, String contents, boolean headerCell, WritableSheet sheet) throws RowsExceededException, WriteException {
        //create a new cell with contents at position
        final Label newCell = new Label(columnPosition,rowPosition,contents);

        if (headerCell){
            //give header cells size 10 Arial bolded
            final WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            final WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

            //center align the cells' contents
            headerFormat.setAlignment(Alignment.CENTRE);
            newCell.setCellFormat(headerFormat);
        }

        sheet.addCell(newCell);
    }

}
