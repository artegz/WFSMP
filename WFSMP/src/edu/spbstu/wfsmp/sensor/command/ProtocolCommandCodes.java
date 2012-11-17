package edu.spbstu.wfsmp.sensor.command;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 22:21
 */
public interface ProtocolCommandCodes {

    String REQUEST_SERIAL_NUMBER        = "#S";
    String RESPONSE_SERIAL_NUMBER       = "*S";


    String REQUEST_EEPROM_DATA_OUT      = "#R";
    String REQUEST_FREQUENCY_OUT        = "#f";
    String REQUEST_VELOCITY_OUT         = "#v";
    String REQUEST_TURN_NUMBER_OUT      = "#n";
    String REQUEST_MEASURE_TIME_OUT     = "#t";
    String REQUEST_STATUS_OUT           = "#s";
    String REQUEST_REAL_TIME_OUT        = "#T";
    String REQUEST_REAL_DATE_OUT        = "#D";
    String REQUEST_DATA_BASE_OUT        = "#B";
    String REQUEST_NUM_RECORDS_OUT      = "#N";
    String REQUEST_WRITE_RECORD         = "#w";
    String REQUEST_CLEAR                = "#c";
    String REQUEST_SET_MODE             = "#m";
    String REQUEST_SET_DISPLAY          = "#d";
    String REQUEST_START                = "#b";
    String REQUEST_STOP                 = "#b";
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

    char COMMAND_POSTFIX            = '\n';
    // char COMMAND_PARAM_SEPARATOR    = ';';

    // request commands
    String REQUEST_READ_RESULT_SIZE     = "req1";
    String REQUEST_READ_RESULT_SINGLE   = "req2";
    String REQUEST_READ_RESULT_ALL      = "req3";
    String REQUEST_READ_LINEAR_TABLE    = "req4";
    String REQUEST_READ_DEVICE_NUMBER   = "req5";
    // Soft version
    String REQUEST_MEASURING_START      = "req6";
    String REQUEST_MEASURING_STOP       = "req7";
    String REQUEST_WRITE_LINEAR_TABLE   = "req8";
    String REQUEST_WRITE_DEVICE_NUMBER  = "req9";    
    // String REQUEST_ERASE_RESULT_SINGLE  = "req10";
    String REQUEST_ERASE_RESULT_ALL     = "req11";

    // response commands
    String RESPONSE_OK              = "req1";
    String RESPONSE_NOK             = "req2";
    String RESPONSE_RESULT_SIZE     = "req3";
    String RESPONSE_RESULT_SINGLE   = "req4";
    String RESPONSE_RESULT_ALL      = "req5";
    String RESPONSE_LINEAR_TABLE    = "req6";
    String RESPONSE_DEVICE_NUMBER   = "req7";
}
