/*
 * Filename: RSJposServiceInstanceFactory.java
 * Created on 2004-1-18
 * @author wangcr
 */
package com.royalstone.pos.services.factories;

import java.lang.reflect.Constructor;

import com.royalstone.pos.services.OSServiceConfiguration;

import jpos.*;
import jpos.config.*;
import jpos.loader.*;


public class RSJposServiceInstanceFactory implements JposServiceInstanceFactory{
	private static JposServiceManager jposServiceManager = null;

	public JposServiceInstance createInstance(String logicalName, JposEntry jposentry) throws JposException {		
		
		if(!jposentry.hasPropertyWithName("serviceClass"))
			throw new JposException(104, "The JposEntry does not contain the 'serviceClass' property");
		if(jposServiceManager == null)
		{
			jposServiceManager = JposServiceLoader.getManager();
			OSServiceConfiguration.setEntryRegistry(jposServiceManager.getEntryRegistry());
		}
		JposServiceInstance jposserviceinstance = null;
		String s1 = "";
		try
		{
			s1 = (String)jposentry.getPropertyValue("serviceClass");
			Class class1 = Class.forName(s1);
			Class aclass[] = new Class[1];
			aclass[0] = Class.forName( "java.lang.String" );
			Constructor constructor = class1.getConstructor(aclass);
			Object aobject[] = new Object[1];
			aobject[0] = s1;
			jposserviceinstance = (JposServiceInstance)constructor.newInstance(aobject);
		}
		catch(Exception exception)
		{
			String s2 = "Could not create the service instance with logicalName=" + logicalName + ", deviceServiceClassName=" + s1;
		
			throw new JposException(104, s2, exception);
		}
		return jposserviceinstance;
    
	}

}
