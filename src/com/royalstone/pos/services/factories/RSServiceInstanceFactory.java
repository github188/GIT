package com.royalstone.pos.services.factories;

import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;

import com.royalstone.pos.services.RSBaseServiceAdapter;

/**
 * 
 * @author worldheart
 */

public class RSServiceInstanceFactory implements JposServiceInstanceFactory {

	public RSServiceInstanceFactory() {
	}

	public JposServiceInstance createInstance(
		String logicalName,
		JposEntry entry)
		throws JposException {

		JposServiceInstance instance = null;

		if (!entry.hasPropertyWithName("serviceClass"))
			throw new JposException(
				104,
				"The JposEntry does not contain the 'serviceClass' property");

		String serviceClass = (String) entry.getPropertyValue("serviceClass");

		try {
			Class clazz = Class.forName(serviceClass);
			instance = (JposServiceInstance) clazz.newInstance();
			((RSBaseServiceAdapter) instance).init(entry);
			return instance;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new JposException(
				104,
				"Could not create the service instance with logicalName="
					+ logicalName
					+ ", deviceServiceClassName="
					+ serviceClass,
				ex);
		}

	}

}