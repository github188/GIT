package com.royalstone.pos.services.factories;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.royalstone.pos.services.JournalPrinter;

import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;

/**
 * 
 * @author Igor Semenko
 *
 */
public class JournalPrinterServiceInstanceFactory
	implements JposServiceInstanceFactory {
		
	Logger logger = Logger.getLogger (JournalPrinterServiceInstanceFactory.class.getName());
		
	public JournalPrinterServiceInstanceFactory (){}
	
	/**
	 * 
	 * @see jpos.loader.JposServiceInstanceFactory#createInstance(java.lang.String, jpos.config.JposEntry)
	 */
	public JposServiceInstance createInstance(String logicalName,	JposEntry entry)
			throws JposException {

		Properties properties = getVendorProperties(entry);
				
		return new JournalPrinter (logicalName, properties);
	}

	protected Properties getVendorProperties(JposEntry entry) {
		Properties properties = new Properties();
		if ( entry != null){
			for (Enumeration keys = entry.getPropertyNames(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				if ( key.startsWith("vendor.")){
					String val = (String)entry.getPropertyValue(key);
					key = key.substring(7);
					properties.setProperty (key,val);
				}
			}
		}
		return properties;
	}

}
