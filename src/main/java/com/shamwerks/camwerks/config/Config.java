package com.shamwerks.camwerks.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    public static final String CONFIG_FILE = "config/config.properties";
    public static final String VERSION = "0.1";
    
    private String language;
    private int nbSteps;
    private int nbStepsIncrement;
    
    private String comPorts[];
    
    public Config(){
    	 System.out.println("Working Directory = " + System.getProperty("user.dir"));
		//loading the config.properties file
		Properties props = new Properties();
		try {
			//System.out.println("stream=" + this.getClass().getResourceAsStream(CONFIG_FILE));
			//props.load(  this.getClass().getResourceAsStream(CONFIG_FILE ) ); 
		    props.load( new FileInputStream( CONFIG_FILE ) );
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("config="+props);
		language = props.getProperty("language");
		nbSteps = Integer.parseInt(props.getProperty("nbSteps"));
		nbStepsIncrement = Integer.parseInt(props.getProperty("nbStepsIncrement"));
		
		comPorts = props.getProperty("comPorts").split(",");
    }//end constructor

	public String getLanguage() {
		return language;
	}

	public int getNbSteps() {
		return nbSteps;
	}

	public int getNbStepsIncrement() {
		return nbStepsIncrement;
	}

	public String[] getComPorts() {
		return comPorts;
	}

}
