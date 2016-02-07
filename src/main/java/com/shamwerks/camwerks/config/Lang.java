package com.shamwerks.camwerks.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

public class Lang {

	private static Lang instance = new Lang();

	private HashMap<String,String> langMap = new HashMap<String,String>();
	
	public static String getText(LangEntry key){
		return instance.langMap.get(key.getValue());
	}
	
	public Lang(){
		Locale currentLocale = Locale.FRENCH;
		
		
		/*
		ResourceBundle labels = ResourceBundle.getBundle("config/Language", currentLocale);
		Enumeration<String> bundleKeys = labels.getKeys();
		
		while (bundleKeys.hasMoreElements()) {
		    String key = (String)bundleKeys.nextElement();
		    String value = labels.getString(key);
		    //System.out.println("key = " + key + ", " + "value = " + value);
		    langMap.put(key, value);
		}
		*/
		
		Properties props = new Properties();
		try {
		    props.load( new FileInputStream( "config/Language_" + currentLocale + ".properties") );
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Object key : props.keySet() ) {
		    //String key = (String)bundleKeys.nextElement();
		    String value = props.getProperty(key.toString());
		    //System.out.println("key = " + key + ", " + "value = " + value);
		    langMap.put(key.toString(), value);
		}

	}
}
