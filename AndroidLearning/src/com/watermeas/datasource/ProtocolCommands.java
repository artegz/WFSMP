package com.watermeas.datasource;

/**
 * User: artegz
 * Date: 05.10.12
 * Time: 19:59
 */
public interface ProtocolCommands {
    String PREFIX = "#";
    String POSTFIX = "\r\n";

    String COMMAND_GET_SIZE = "g";
    String COMMAND_READ_RECORD = "r";
    // ...
}
