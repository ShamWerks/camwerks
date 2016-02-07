package com.shamwerks.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.shamwerks.camwerks.CamWerks;


public class Arduino implements SerialPortEventListener {
    SerialPort serialPort = null;
    
    private boolean connected = false;
    
    private boolean dataReceived = false;
    private String  data ;
    
    private enum ArduinoCommand {
    						PING, 
    						STEP, 
    						MEASURE,
    						SET_DIRECTION
    					} 

    private String appName;
    private BufferedReader input;
    private OutputStream output;
    
    final Lock lock = new ReentrantLock();
    final Condition measureReceived  = lock.newCondition(); 
    
    private static final int TIME_OUT  = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port

    public boolean initialize() {
        try {
            CommPortIdentifier portId = null;
            Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
            
            // Enumerate system ports and try connecting to Arduino over each
            //
            System.out.println( "Trying:");
            while (portId == null && portEnum.hasMoreElements()) {
                // Iterate through your host computer's serial port IDs
                //
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                System.out.println( "   port" + currPortId.getName() );
                
                for (String portName : CamWerks.getInstance().getConfig().getComPorts() ) {
                    if ( currPortId.getName().equals(portName) 
                      || currPortId.getName().startsWith(portName)) {
                        // Try to connect to the Arduino on this port / Open serial port
                        serialPort = (SerialPort)currPortId.open(appName, TIME_OUT);
                        portId = currPortId;
                        System.out.println( "Connected on port" + currPortId.getName() );
                        break;
                    }
                }//end for
            }
        
            if (portId == null || serialPort == null) {
                System.out.println("Oops... Could not connect to Arduino");
                return false;
            }
        
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
            
            try {
                 serialPort.enableReceiveTimeout(1000);
                 serialPort.enableReceiveThreshold(0);
            }
            catch (UnsupportedCommOperationException e) {
            	System.out.println("serial port enable err : " + e);
            }
            
            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            // Give the Arduino some time
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            
            return true;
        }
        catch ( Exception e ) { 
            e.printStackTrace();
        }
        return false;
    }
    
    
    public double getMeasure() throws ArduinoException{
    	String arduinoReturn = sendCommand(ArduinoCommand.MEASURE.toString());
    	if(arduinoReturn==null){
    		System.out.println("ERROR!! arduinoReturn==null!!");
        	return 0.0;
    	}
    		
    	return Double.parseDouble( arduinoReturn );
    }
    
    public void doStep() throws ArduinoException{
    	String arduinoReturn = sendCommand(ArduinoCommand.STEP.toString());
    	if (!arduinoReturn.equals("STEP_ACK")) throw new ArduinoException("Incorrect Return Value!");
    }
    
    public void setDirection(int dir) throws ArduinoException{
    	String arduinoReturn = sendCommand(  ArduinoCommand.SET_DIRECTION + " " + dir  );
    	if (!arduinoReturn.equals("DIR_ACK")) throw new ArduinoException("Incorrect Return Value!");
    }
    
    public boolean ping() throws ArduinoException{
    	String arduinoReturn = sendCommand(ArduinoCommand.PING.toString());
    	if (arduinoReturn.equals("PING_ACK")) return true;
    	else return false;
    }
    
    
    private String sendCommand(String command)  throws ArduinoException {
        try {
            //System.out.println("Sending comand: " + command );
        	String out = command + "\r\n";
            
        	data = null;
        	
            // open the streams and send the "y" character
            output = serialPort.getOutputStream();
            output.write( out.getBytes() );
            int i=0;
            while (!dataReceived){
            	i++;
            	if (i>=100){
            		 throw new ArduinoException("Time-Out!");
            	}
            	Thread.sleep(10);
            }
            dataReceived=false;
        } 
        catch (Exception e) {
        	System.err.println("sendCommand : " + e.toString());
        }
        return data;
    }

    //
    // This should be called when you stop using the port
    //
    public synchronized void close() {
        if ( serialPort != null ) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    //
    // Handle serial port event
    //
    public void serialEvent(SerialPortEvent oEvent) {
        //System.out.println("Event received: " + oEvent.toString());
        try {
            switch (oEvent.getEventType() ) {
                case SerialPortEvent.DATA_AVAILABLE: 
                    if ( input == null ) {
                        input = new BufferedReader(  new InputStreamReader(  serialPort.getInputStream()  )  );
                    }
                    data = input.readLine();
                    //System.out.println("RECEIVED FROM ARDUINO : " + data);
                    dataReceived = true;
                    break;
                default:
                    break;
            }
        } 
        catch (Exception e) {
            System.err.println("serialEvent : " + e.toString());
        }
    }

    public Arduino() {
        appName = getClass().getName();
    }
    
    
    public void disconnect()
    {
        //close the serial port
        try
        {
            //writeData(0, 0);

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);
            //window.keybindingController.toggleControls();

            //logText = "Disconnected.";
        }
        catch (Exception e)
        {
            System.out.println("Failed to close " + serialPort.getName() + "(" + e.toString() + ")");
        }
    }    

    
    public void setConnected(boolean connected){
    	this.connected = connected;
    }
    
    public boolean isConnected(){
    	return connected;
    }
    
    public static void main(String[] args)  {
    	Arduino arduinoConnect = new Arduino();
        if ( arduinoConnect.initialize() ) {
            arduinoConnect.close();
        }

        // Wait 5 seconds then shutdown
        try { Thread.sleep(2000); } catch (InterruptedException ie) {}
        System.out.println("I'm out of here.");
    }
    
}



