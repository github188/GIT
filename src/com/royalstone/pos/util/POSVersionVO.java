package com.royalstone.pos.util;

import java.io.Serializable;

/**
 * 封装了代表POS程序三个模块的版本号
 * @deprecated
 * @author liangxinbiao
 */
public class POSVersionVO implements Serializable {
	
	private String posVersion;
	private String loaderVersion;
	private String driverVersion;
	
	

	/**
	 * @return 驱动程序Driver.zip的版本号
	 */
	public String getDriverVersion() {
		return driverVersion;
	}

	/**
	 * @return loader.jar的版本号
	 */
	public String getLoaderVersion() {
		return loaderVersion;
	}

	/**
	 * @return pos.jar的版本号
	 */
	public String getPosVersion() {
		return posVersion;
	}

	/**
	 * @param string 驱动程序Driver.zip的版本号
	 */
	public void setDriverVersion(String string) {
		driverVersion = string;
	}

	/**
	 * @param string loader.jar的版本号
	 */
	public void setLoaderVersion(String string) {
		loaderVersion = string;
	}

	/**
	 * @param string pos.jar的版本号
	 */
	public void setPosVersion(String string) {
		posVersion = string;
	}

}
