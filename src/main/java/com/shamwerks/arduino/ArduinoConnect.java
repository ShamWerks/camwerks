package com.shamwerks.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.shamwerks.camwerks.CamWerks;


public class ArduinoConnect implements SerialPortEventListener {
    SerialPort serialPort = null;
    
    public enum ArduinoCommand {
    						PING, 
    						//START_CYCLE, 
    						DO_STEP, 
    						DO_MEASURE, 
    						SET_NB_STEPS, 
    						SET_DIRECTION, 
    						SET_NB_CYCLES
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
    
    public void sendCommand(String command) {
        try {
            System.out.println("Sending comand: " + command );
        	String out = command + "\r\n";
            
            // open the streams and send the "y" character
            output = serialPort.getOutputStream();
            output.write( out.getBytes() );
        } 
        catch (Exception e) {
            System.err.println(e.toString());
            System.exit(0);
        }
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
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        //System.out.println("Event received: " + oEvent.toString());
        try {
            switch (oEvent.getEventType() ) {
                case SerialPortEvent.DATA_AVAILABLE: 
                    if ( input == null ) {
                        input = new BufferedReader(  new InputStreamReader(  serialPort.getInputStream()  )  );
                    }
                    String inputLine = input.readLine();
                     
                    if(inputLine.startsWith("MEASURE ")){
                    	String data[] = inputLine.split(" ");
                    	double measure = Double.parseDouble(data[1]);
                    	System.out.println("Measure received : measure = " + measure);
                    }
                    
                    break;

                default:
                    break;
            }
        } 
        catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public ArduinoConnect() {
        appName = getClass().getName();
    }
    
    public static void main(String[] args)  {
    	ArduinoConnect arduinoConnect = new ArduinoConnect();
        if ( arduinoConnect.initialize() ) {
        	
            arduinoConnect.close();
        }

        // Wait 5 seconds then shutdown
        try { Thread.sleep(2000); } catch (InterruptedException ie) {}
        System.out.println("I'm out of here.");
    }
    
}



