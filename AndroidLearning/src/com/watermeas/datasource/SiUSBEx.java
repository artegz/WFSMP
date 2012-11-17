package com.watermeas.datasource;

/**
 * User: artegz
 * Date: 10.10.12
 * Time: 0:00
 */
class SiUSBEx {

    public static native int getNumDevices(int outNumDevices);

    public static native int getProductString(int inDeviceNum, String outDeviceString, int inFlags);

    public static native int fillBuffer(int inPtrHandle, int inTimeout);

    public static native int getBuffer(int intPtrHandle, byte[] outBuffer, int inBytesToGet);

    public static native int open(int inDevNumber, int outPtrHandle);

    public static native int close(int inPtrHandle);

    public static native int read(int inPtrHandle, byte[] outBuffer, int inBytesToRead, int outBytesReturned);

    public static native int write(int inPtrHandle, byte[] inBuffer, int inBytesToWrite, int outBytesWritten);

    public static native int resetDevice(int inPtrHandle);

    public static native int deviceIoControl();
}
