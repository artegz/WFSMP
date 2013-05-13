package edu.spbstu.wfsmp.sensor;

/**
 * User: artegz
 * Date: 13.10.12
 * Time: 22:21
 */
public interface ProtocolCodes {

    // implemented commands
    String REQUEST_SERIAL_NUMBER        = "#S";
    String RESPONSE_SERIAL_NUMBER       = "*S";
    String REQUEST_DATA_BASE_OUT        = "#B";
    String RESPONSE_DATA_BASE_OUT       = "*B";
    String REQUEST_START                = "#b";
    String RESPONSE_START               = "*b";
    String REQUEST_STOP                 = "#b";
    String RESPONSE_STOP                = "*b";
    String RESPONSE_NOK                 = "*?";

    String REQUEST_FREQUENCY_OUT        = "#f";
    String RESPONSE_FREQUENCY_OUT       = "*f";
    String REQUEST_VELOCITY_OUT         = "#v";
    String RESPONSE_VELOCITY_OUT        = "*v";
    String REQUEST_TURN_NUMBER_OUT      = "#n";
    String RESPONSE_TURN_NUMBER_OUT     = "*n";
    String REQUEST_MEASURE_TIME_OUT     = "#t";
    String RESPONSE_MEASURE_TIME_OUT    = "*t";
    String REQUEST_STATUS_OUT           = "#s";
    String RESPONSE_STATUS_OUT          = "*s";
    String REQUEST_REAL_TIME_OUT        = "#T";
    String RESPONSE_REAL_TIME_OUT       = "*T";
    String REQUEST_REAL_DATE_OUT        = "#D";
    String RESPONSE_REAL_DATE_OUT       = "*D";

    String REQUEST_SAVE                 = "#w";
    String RESPONSE_SAVE                = "*w";

    String REQUEST_DB_SIZE              = "#N";
    String RESPONSE_DB_SIZE             = "*N";

    String REQUEST_CLEAR_DB             = "#c";
    String RESPONSE_CLEAR_DB            = "*c";

    String REQUEST_INF_STRING           = "#H";
    String RESPONSE_INF_STRING          = "*H";

    // set whirwind type - "#md/r/n"
    // set time - "#Thhmmss/r/n"
    // set date - "#DddMMyy/r/n"
    // set display "#dm/r/n"

    String REQUEST_TURN_OFF             = "#e";

    String REQUEST_CHANGE_SOUND         = "#z"; // "#zS/r/n", S=0,1
    String RESPONSE_CHANGE_SOUND        = "*z"; // "#zS/r/n", S=0,1

    String REQUEST_CHANGE_CONTACT_CONTROL   = "#k";
    String RESPONSE_CHANGE_CONTACT_CONTROL  = "*k";

    String REQUEST_WRITE_EEPROM_BYTE    = "#P"; // "#Paadd/r/n" - "*Paadd/r/n"
    String RESPONSE_WRITE_EEPROM_BYTE   = "*P"; // "#Paadd/r/n" - "*Paadd/r/n"

    String REQUEST_READ_EEPROM_BYTE     = "#R";  // "#Raa/r/n" - "*Raadd/r/n "
    String RESPONSE_READ_EEPROM_BYTE    = "*R";  // "#Raa/r/n" - "*Raadd/r/n "

    String REQUEST_CHANGE_WH_TYPE       = "#m"; // "#mS/r/n", 0-1:20;1-1:1;2-70:3-120
    String RESPONSE_CHANGE_WH_TYPE      = "*m"; // "#mS/r/n", 0-1:20;1-1:1;2-70:3-120

    String REQUEST_CHANGE_DISP_MODE     = "#d"; // "#dS/r/n", 0-time;1-turns;2-freq;3-vel
    String RESPONSE_CHANGE_DISP_MODE    = "*d"; // "#dS/r/n", 0-time;1-turns;2-freq;3-vel

    String REQUEST_SET_DATE             = "#D"; // #DddMMyy/r/n
    String RESPONSE_SET_DATE            = "*D"; // #DddMMyy/r/n

    String REQUEST_SET_TIME             = "#T"; // #Thhmmss/r/n
    String RESPONSE_SET_TIME            = "*T"; // #Thhmmss/r/n

    String REQUEST_U_BATTERY            = "#U";

    String ERROR                        = "*?\r\n";
}
