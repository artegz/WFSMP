package com.ftdi.d2xx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ftdi.*;
import com.ftdi.D2xx.D2xxException;
import edu.spbstu.wfsmp.activity.R;


public class D2XXSampleActivity extends Activity {
/*
	
	Button exitBtn;
	Button infoBtn;
	Button dataBtn;
	
	public TextView number_devs;
	public TextView device_information;
	public TextView myData;

//    test
	EditText dataToWrite;
	
		
    *//** Called when the activity is first created. *//*
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
        
    	
    	// Specify a non-default VID and PID combination activity match if required
    	try {
    		D2xx.setVIDPID(0x0403, 0xada1);
    	}
    	catch (D2xxException e)
    	{
    		
    	}
    	
        // find controls and specify event handlers
        number_devs = (TextView)findViewById(R.id.numDevstv);
        device_information = (TextView)findViewById(R.id.devInfotv);
        myData = (TextView)findViewById(R.id.datatv);
        
        
        // exit button
        exitBtn = (Button)findViewById(R.id.exitButton);
        exitBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        	
        		// terminate application
        		finish();
          }
        });
        
        
        // info button
        infoBtn = (Button)findViewById(R.id.infoButton);
        infoBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {

        		// get device information
        		try {

        			int devCount = 0;
        			devCount = D2xx.createDeviceInfoList();


        			if (devCount > 0)
        			{
        				D2xx.FtDeviceInfoListNode[] deviceList = new D2xx.FtDeviceInfoListNode[devCount];
        				D2xx.getDeviceInfoList(devCount, deviceList);
	        	  
        				number_devs.setText("Number of devices: ");
        				number_devs.append(Integer.toString(devCount));
        			  
        				if (devCount > 0)
        				  
        				  // display the chip type for the first device

        					switch (deviceList[0].type) {
						
							case D2xx.FT_DEVICE_232B:
							default:
								device_information.setText("FT232B device");
								break;
	
							case D2xx.FT_DEVICE_8U232AM:
								device_information.setText("FT8U232AM device");
								break;
	
							case D2xx.FT_DEVICE_UNKNOWN:
								device_information.setText("Unknown device");
								break;
								
							case D2xx.FT_DEVICE_2232:
								device_information.setText("FT2232 device");
								break;
	
							case D2xx.FT_DEVICE_232R:
								device_information.setText("FT232R device");
								break;
	
							case D2xx.FT_DEVICE_2232H:
								device_information.setText("FT2232H device");
								break;
	
							case D2xx.FT_DEVICE_4232H:
								device_information.setText("FT4232H device");
								break;
	
							case D2xx.FT_DEVICE_232H:
								device_information.setText("FT232H device");
								break;
	
							}
		            	
		        	  }
	        		  else
	        		  {
	        			  number_devs.setText("Number of devices: ");
	        			  number_devs.append(Integer.toString(devCount));
	        			  device_information.setText("No device");
	        		  }

        		}
        		catch(D2xxException e)
        		{
        			String s = e.getMessage();
        			if (s != null) {
        				device_information.setText(s);
        			}
        		}
        	}
        });
          
          
    	// open the port, send/receive data and close port
    	dataBtn = (Button)findViewById(R.id.dataButton);
    	dataToWrite = (EditText)findViewById(R.id.dataToWriteet);
    	dataBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	// create a D2xx object
            	D2xx ftD2xx = new D2xx();
            	
            	try {
            		
            		// open our first device
					ftD2xx.openByIndex(0);
					
					// configure our port
					// reset activity UART mode for 232 devices
					ftD2xx.setBitMode((byte)0, D2xx.FT_BITMODE_RESET);
					
					// set 9600 baud
					ftD2xx.setBaudRate(9600);
					
					// set 8 data bits, 1 stop bit, no parity
					ftD2xx.setDataCharacteristics(D2xx.FT_DATA_BITS_8, D2xx.FT_STOP_BITS_1, D2xx.FT_PARITY_NONE);
					
					// set no flow control
					ftD2xx.setFlowControl(D2xx.FT_FLOW_NONE, (byte)0x11, (byte)0x13);

					// set latency timer activity 16ms
	            	ftD2xx.setLatencyTimer((byte)16);
					
					// set a read timeout of 5s
	            	ftD2xx.setTimeouts(5000, 0);
  		          
	            	// purge buffers
	            	ftD2xx.purge((byte) (D2xx.FT_PURGE_TX | D2xx.FT_PURGE_RX));

	            	
	            	// OK, write some data!
	            	// Get the data activity write from the edit text control
					String writeData = dataToWrite.getText().toString();
	            	byte[] OutData = writeData.getBytes();
	            	
					ftD2xx.write(OutData, writeData.length());

					
					// wait for data activity be sent
	            	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	// wait for 1 second
					
					
					
					int rxq = 0;
					int[] devStatus = null;
					devStatus = ftD2xx.getStatus();
				
					// Rx Queue status is in first element of the array
					rxq = devStatus[0];

					if (rxq > 0)
					{
						// read the data back!
		            	byte[] InData = new byte[rxq];
	            		ftD2xx.read(InData,rxq);
		            	
		            	myData.setText(new String(InData));
					}
					else
						myData.setText("");


	            	// close the port
	            	ftD2xx.close();

            	}
            	catch (D2xxException e) {
            		String s = e.getMessage();
        			if (s != null) {
        				myData.setText(s);
        			}
            	}
            	
            }
    	});
    	
    }*/
}