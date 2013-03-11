package edu.spbstu.wfsmp.sensor.command;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 22:21
 */
public interface ProtocolCommandCodes {

    String REQUEST_SERIAL_NUMBER        = "#S";
    String RESPONSE_SERIAL_NUMBER       = "*S";

    String REQUEST_DATA_BASE_OUT        = "#B";
    String RESPONSE_DATA_BASE_OUT       = "*B";

    String REQUEST_START                = "#b";
    String REQUEST_STOP                 = "#b";

    String RESPONSE_NOK                 = "*?";





    String REQUEST_EEPROM_DATA_OUT      = "#R";
    String REQUEST_FREQUENCY_OUT        = "#f";
    String REQUEST_VELOCITY_OUT         = "#v";
    String REQUEST_TURN_NUMBER_OUT      = "#n";
    String REQUEST_MEASURE_TIME_OUT     = "#t";
    String REQUEST_STATUS_OUT           = "#s";
    String REQUEST_REAL_TIME_OUT        = "#T";
    String REQUEST_REAL_DATE_OUT        = "#D";

    String REQUEST_NUM_RECORDS_OUT      = "#N";
    String REQUEST_WRITE_RECORD         = "#w";
    String REQUEST_CLEAR                = "#c";
    String REQUEST_SET_MODE             = "#m";
    String REQUEST_SET_DISPLAY          = "#d";

    String REQUEST_SOUND_SET            = "#z"; // 1|0
    String REQUEST_GROUND_SET           = "#k"; // 1|0
    String REQUEST_SOFT_RESET           = "#r";
    String REQUEST_WRITE_BYTE           = "#P";
    String REQUEST_SLEEP                = "#e";
    String REQUEST_PROMT_OUTPUT         = "#H";
    String REQUEST_NUMBER_VERSIE_OUTPUT = "#V";

    // encoding
    String PROTOCOL_COMMAND_ENCODING = "UTF-8";

    // command format
    char REQUEST_PREFIX             = '#';
    char RESPONSE_PREFIX            = '*';

    String COMMAND_POSTFIX            = "\r\n";

    String REQUEST_WRITE_LINEAR_TABLE   = "req8";

    String REQUEST_ERASE_RESULT_ALL     = "req11";

    // response commands
    String RESPONSE_OK              = "";

    String NO_RESPONSE              = "NO_RESPONSE_FAKE_COMMAND";
}
