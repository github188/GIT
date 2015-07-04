/*
 * Filename: OSServiceConfiguration.java
 * Created on 2004-1-28
 * @author wangcr
 */
package com.royalstone.pos.services;

import jpos.config.JposEntry;
import jpos.config.JposEntryRegistry;

public class OSServiceConfiguration {

	public OSServiceConfiguration(String s) throws Exception {
	    keyName = null;
	    jposEntry = null;
	
	    if (theEntryRegistry == null) {
	      throw new Exception("OSServiceConfiguration not initialized");
		}
	
		if (s == null) {
		  throw new Exception("OSServiceConfiguration key '" + s + "' illegal.");
		}
		
		jposEntry = theEntryRegistry.getJposEntry(s);
		if (jposEntry == null) {
		  throw new Exception("OSServiceConfiguration key '" + s + "' not found.");
		}
	    else {
	      keyName = s;
	      return;
	    }
	  }
	
	  public String getKeyName() {
	    return keyName;
	  }
	
	  public String getValue(String s) {
	
	    String s1 = (String) jposEntry.getPropertyValue(s);
	    if (s1 != null) {
	      s1 = s1.trim();
	      if (s1.startsWith("\"") && s1.endsWith("\"") && s1.length() >= 2) {
	        s1 = s1.substring(1, s1.length() - 1);
	      }
	    }
	 
	    return s1;
	  }
	
	  public static void setEntryRegistry(JposEntryRegistry jposentryregistry) {
	    theEntryRegistry = jposentryregistry;
	  }
	
	  private static JposEntryRegistry theEntryRegistry = null;	
	  private String keyName;
	  private JposEntry jposEntry;

 
}
