package edu.spbstu.wfsmp.driver.j2xx;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 23:24
 */
public interface D2xxParameter {

    //todo
    public static final String BAUD_RATE = "BAUD_RATE";

    public static final String DATA_BITS = "BAUD_RATE";
    public static final String STOP_BITS = "BAUD_RATE";
    public static final String PARITY = "BAUD_RATE";

    public static final String FLOW_CONTROL = "BAUD_RATE";
    public static final String XON = "BAUD_RATE";
    public static final String XOFF = "BAUD_RATE";

    public static final String READ_TIMEOUTS = "BAUD_RATE";
    public static final String WRITE_TIMEOUTS = "BAUD_RATE";

    interface Param {
        String BIT_MODE_MASK = "BitModeMask";
        String BIT_MODE_MODE = "BitMode";
        String BAUD_RATE = "BaudRate";
        String DATA_DATA_BITS = "DataBits";
        String DATA_STOP_BITS = "DataStopBits";
        String DATA_PARITY_BITS = "DataParityBits";
        String FLOW_CONTROL = "FlowControl";
        String FLOW_XON = "FlowXon";
        String FLOW_XOFF = "FlowXoff";
        String LATENCY_TIMER = "LatencyTimer";
        String READ_TIMEOUT = "ReadTimeout";
        String WRITE_TIMEOUT = "WriteTimeout";
    }


}
