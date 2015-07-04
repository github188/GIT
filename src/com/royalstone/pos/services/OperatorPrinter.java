
package com.royalstone.pos.services;

import java.util.Properties;

import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;

/**
 * @author liangxinbiao
 */

public class OperatorPrinter extends BaseServiceAdapter implements jpos.services.POSPrinterService14, JposServiceInstance{

	/**
	 * @param device_name
	 */
	public OperatorPrinter(String device_name,Properties properties) {
		super(device_name);
	}
	
	
	public void init(JposEntry entry) {
		//driverName = (String) entry.getPropertyValue("driverName");
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService13#getCapPowerReporting()
	 */
	public int getCapPowerReporting() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService13#getPowerNotify()
	 */
	public int getPowerNotify() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService13#setPowerNotify(int)
	 */
	public void setPowerNotify(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService13#getPowerState()
	 */
	public int getPowerState() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapCharacterSet()
	 */
	public int getCapCharacterSet() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapConcurrentJrnRec()
	 */
	public boolean getCapConcurrentJrnRec() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapConcurrentJrnSlp()
	 */
	public boolean getCapConcurrentJrnSlp() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapConcurrentRecSlp()
	 */
	public boolean getCapConcurrentRecSlp() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapCoverSensor()
	 */
	public boolean getCapCoverSensor() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrn2Color()
	 */
	public boolean getCapJrn2Color() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnBold()
	 */
	public boolean getCapJrnBold() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnDhigh()
	 */
	public boolean getCapJrnDhigh() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnDwide()
	 */
	public boolean getCapJrnDwide() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnDwideDhigh()
	 */
	public boolean getCapJrnDwideDhigh() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnEmptySensor()
	 */
	public boolean getCapJrnEmptySensor() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnItalic()
	 */
	public boolean getCapJrnItalic() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnNearEndSensor()
	 */
	public boolean getCapJrnNearEndSensor() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnPresent()
	 */
	public boolean getCapJrnPresent() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapJrnUnderline()
	 */
	public boolean getCapJrnUnderline() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRec2Color()
	 */
	public boolean getCapRec2Color() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecBarCode()
	 */
	public boolean getCapRecBarCode() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecBitmap()
	 */
	public boolean getCapRecBitmap() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecBold()
	 */
	public boolean getCapRecBold() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecDhigh()
	 */
	public boolean getCapRecDhigh() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecDwide()
	 */
	public boolean getCapRecDwide() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecDwideDhigh()
	 */
	public boolean getCapRecDwideDhigh() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecEmptySensor()
	 */
	public boolean getCapRecEmptySensor() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecItalic()
	 */
	public boolean getCapRecItalic() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecLeft90()
	 */
	public boolean getCapRecLeft90() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecNearEndSensor()
	 */
	public boolean getCapRecNearEndSensor() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecPapercut()
	 */
	public boolean getCapRecPapercut() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecPresent()
	 */
	public boolean getCapRecPresent() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecRight90()
	 */
	public boolean getCapRecRight90() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecRotate180()
	 */
	public boolean getCapRecRotate180() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecStamp()
	 */
	public boolean getCapRecStamp() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapRecUnderline()
	 */
	public boolean getCapRecUnderline() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlp2Color()
	 */
	public boolean getCapSlp2Color() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpBarCode()
	 */
	public boolean getCapSlpBarCode() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpBitmap()
	 */
	public boolean getCapSlpBitmap() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpBold()
	 */
	public boolean getCapSlpBold() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpDhigh()
	 */
	public boolean getCapSlpDhigh() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpDwide()
	 */
	public boolean getCapSlpDwide() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpDwideDhigh()
	 */
	public boolean getCapSlpDwideDhigh() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpEmptySensor()
	 */
	public boolean getCapSlpEmptySensor() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpFullslip()
	 */
	public boolean getCapSlpFullslip() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpItalic()
	 */
	public boolean getCapSlpItalic() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpLeft90()
	 */
	public boolean getCapSlpLeft90() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpNearEndSensor()
	 */
	public boolean getCapSlpNearEndSensor() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpPresent()
	 */
	public boolean getCapSlpPresent() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpRight90()
	 */
	public boolean getCapSlpRight90() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpRotate180()
	 */
	public boolean getCapSlpRotate180() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapSlpUnderline()
	 */
	public boolean getCapSlpUnderline() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCapTransaction()
	 */
	public boolean getCapTransaction() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getAsyncMode()
	 */
	public boolean getAsyncMode() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setAsyncMode(boolean)
	 */
	public void setAsyncMode(boolean arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCharacterSet()
	 */
	public int getCharacterSet() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setCharacterSet(int)
	 */
	public void setCharacterSet(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCharacterSetList()
	 */
	public String getCharacterSetList() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getCoverOpen()
	 */
	public boolean getCoverOpen() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getErrorLevel()
	 */
	public int getErrorLevel() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getErrorStation()
	 */
	public int getErrorStation() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getErrorString()
	 */
	public String getErrorString() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getFlagWhenIdle()
	 */
	public boolean getFlagWhenIdle() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setFlagWhenIdle(boolean)
	 */
	public void setFlagWhenIdle(boolean arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getFontTypefaceList()
	 */
	public String getFontTypefaceList() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnEmpty()
	 */
	public boolean getJrnEmpty() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnLetterQuality()
	 */
	public boolean getJrnLetterQuality() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setJrnLetterQuality(boolean)
	 */
	public void setJrnLetterQuality(boolean arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnLineChars()
	 */
	public int getJrnLineChars() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setJrnLineChars(int)
	 */
	public void setJrnLineChars(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnLineCharsList()
	 */
	public String getJrnLineCharsList() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnLineHeight()
	 */
	public int getJrnLineHeight() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setJrnLineHeight(int)
	 */
	public void setJrnLineHeight(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnLineSpacing()
	 */
	public int getJrnLineSpacing() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setJrnLineSpacing(int)
	 */
	public void setJrnLineSpacing(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnLineWidth()
	 */
	public int getJrnLineWidth() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getJrnNearEnd()
	 */
	public boolean getJrnNearEnd() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getMapMode()
	 */
	public int getMapMode() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setMapMode(int)
	 */
	public void setMapMode(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getOutputID()
	 */
	public int getOutputID() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecBarCodeRotationList()
	 */
	public String getRecBarCodeRotationList() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecEmpty()
	 */
	public boolean getRecEmpty() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecLetterQuality()
	 */
	public boolean getRecLetterQuality() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setRecLetterQuality(boolean)
	 */
	public void setRecLetterQuality(boolean arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecLineChars()
	 */
	public int getRecLineChars() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setRecLineChars(int)
	 */
	public void setRecLineChars(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecLineCharsList()
	 */
	public String getRecLineCharsList() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecLineHeight()
	 */
	public int getRecLineHeight() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setRecLineHeight(int)
	 */
	public void setRecLineHeight(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecLineSpacing()
	 */
	public int getRecLineSpacing() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setRecLineSpacing(int)
	 */
	public void setRecLineSpacing(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecLinesToPaperCut()
	 */
	public int getRecLinesToPaperCut() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecLineWidth()
	 */
	public int getRecLineWidth() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecNearEnd()
	 */
	public boolean getRecNearEnd() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecSidewaysMaxChars()
	 */
	public int getRecSidewaysMaxChars() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRecSidewaysMaxLines()
	 */
	public int getRecSidewaysMaxLines() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getRotateSpecial()
	 */
	public int getRotateSpecial() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setRotateSpecial(int)
	 */
	public void setRotateSpecial(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpBarCodeRotationList()
	 */
	public String getSlpBarCodeRotationList() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpEmpty()
	 */
	public boolean getSlpEmpty() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpLetterQuality()
	 */
	public boolean getSlpLetterQuality() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setSlpLetterQuality(boolean)
	 */
	public void setSlpLetterQuality(boolean arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpLineChars()
	 */
	public int getSlpLineChars() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setSlpLineChars(int)
	 */
	public void setSlpLineChars(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpLineCharsList()
	 */
	public String getSlpLineCharsList() throws JposException {
		// TODO 自动生成方法存根
		return null;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpLineHeight()
	 */
	public int getSlpLineHeight() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setSlpLineHeight(int)
	 */
	public void setSlpLineHeight(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpLinesNearEndToEnd()
	 */
	public int getSlpLinesNearEndToEnd() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpLineSpacing()
	 */
	public int getSlpLineSpacing() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setSlpLineSpacing(int)
	 */
	public void setSlpLineSpacing(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpLineWidth()
	 */
	public int getSlpLineWidth() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpMaxLines()
	 */
	public int getSlpMaxLines() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpNearEnd()
	 */
	public boolean getSlpNearEnd() throws JposException {
		// TODO 自动生成方法存根
		return false;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpSidewaysMaxChars()
	 */
	public int getSlpSidewaysMaxChars() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#getSlpSidewaysMaxLines()
	 */
	public int getSlpSidewaysMaxLines() throws JposException {
		// TODO 自动生成方法存根
		return 0;
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#beginInsertion(int)
	 */
	public void beginInsertion(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#beginRemoval(int)
	 */
	public void beginRemoval(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#clearOutput()
	 */
	public void clearOutput() throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#cutPaper(int)
	 */
	public void cutPaper(int arg0) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#endInsertion()
	 */
	public void endInsertion() throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#endRemoval()
	 */
	public void endRemoval() throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#printBarCode(int, java.lang.String, int, int, int, int, int)
	 */
	public void printBarCode(int arg0, String arg1, int arg2, int arg3, int arg4, int arg5, int arg6) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#printBitmap(int, java.lang.String, int, int)
	 */
	public void printBitmap(int arg0, String arg1, int arg2, int arg3) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#printImmediate(int, java.lang.String)
	 */
	public void printImmediate(int arg0, String arg1) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#printNormal(int, java.lang.String)
	 */
	public void printNormal(int arg0, String arg1) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#printTwoNormal(int, java.lang.String, java.lang.String)
	 */
	public void printTwoNormal(int arg0, String arg1, String arg2) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#rotatePrint(int, int)
	 */
	public void rotatePrint(int arg0, int arg1) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setBitmap(int, int, java.lang.String, int, int)
	 */
	public void setBitmap(int arg0, int arg1, String arg2, int arg3, int arg4) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#setLogo(int, java.lang.String)
	 */
	public void setLogo(int arg0, String arg1) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#transactionPrint(int, int)
	 */
	public void transactionPrint(int arg0, int arg1) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.services.POSPrinterService12#validateData(int, java.lang.String)
	 */
	public void validateData(int arg0, String arg1) throws JposException {
		// TODO 自动生成方法存根
		
	}

	/* （非 Javadoc）
	 * @see jpos.loader.JposServiceInstance#deleteInstance()
	 */
	public void deleteInstance() throws JposException {
		// TODO 自动生成方法存根
		
	}

}
