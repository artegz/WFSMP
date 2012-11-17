package com.watermeas.datasource;

/**
 * User: artegz
 * Date: 05.10.12
 * Time: 20:03
 */
public interface DataSource {

    void startRecording();

    void stopRecording();

    MeasureResult getResult(int number);

    void cleanup();


}
