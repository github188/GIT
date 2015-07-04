/*
 * 创建日期 2005-8-13
 */
package com.royalstone.pos.services;

import jpos.*;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;

/**
 * @author liangxinbiao
 */

public class Java2DPrinter
	extends RSBaseServiceAdapter
	implements jpos.services.POSPrinterService14, JposServiceInstance {

	private StringBuffer content = new StringBuffer();

	private PrinterJob job;
	private PageFormat pageFormat;
	private ThePrinter thePrinter;

	private double width = 3;
	private double height = 10;
	private double heightDelta = 0;
	private int margin = 25;
	private String fontName = "仿宋_GB2312";
	private int fontSize = 6;

	private int lines = 0;
	private Font font;

	public Java2DPrinter() {
		super("Java2DPrinter");
	}

	public void init(JposEntry entry) {

		String strParam = (String) entry.getPropertyValue("driverName");

		if (strParam != null) {

			String params[] = strParam.split(";");

			for (int i = 0; i < params.length; i++) {

				switch (i) {

					case 0 :

						try {
							width = Double.parseDouble(params[i]);
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
						}

						break;
					case 1 :

						try {
							height = Double.parseDouble(params[i]);
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
						}

						break;

					case 2 :

						try {
							heightDelta = Double.parseDouble(params[i]);
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
						}

						break;

					case 3 :

						try {
							margin = Integer.parseInt(params[i]);
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
						}

						break;
					case 4 :

						fontName = params[i];

						break;
					case 5 :

						try {
							fontSize = Integer.parseInt(params[i]);
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
						}

						break;

				}

			}

		}

		font = new Font(fontName, Font.PLAIN, fontSize);

	}

	public int getCapCharacterSet() {
		return 0;
	}

	public boolean getCapConcurrentJrnRec() {
		return true;
	}

	public boolean getCapConcurrentJrnSlp() {
		return true;
	}

	public boolean getCapConcurrentRecSlp() {
		return true;
	}

	public boolean getCapCoverSensor() {
		return true;
	}

	public boolean getCapJrn2Color() {
		return true;
	}

	public boolean getCapJrnBold() {
		return true;
	}

	public boolean getCapJrnDhigh() {
		return true;
	}

	public boolean getCapJrnDwide() {
		return true;
	}

	public boolean getCapJrnDwideDhigh() {
		return true;
	}

	public boolean getCapJrnEmptySensor() {
		return true;
	}

	public boolean getCapJrnItalic() {
		return true;
	}

	public boolean getCapJrnNearEndSensor() {
		return true;
	}

	public boolean getCapJrnPresent() {
		return true;
	}

	public boolean getCapJrnUnderline() {
		return true;
	}

	public boolean getCapRec2Color() {
		return true;
	}

	public boolean getCapRecBarCode() {
		return true;
	}

	public boolean getCapRecBitmap() {
		return true;
	}

	public boolean getCapRecBold() {
		return true;
	}

	public boolean getCapRecDhigh() {
		return true;
	}

	public boolean getCapRecDwide() {
		return true;
	}

	public boolean getCapRecDwideDhigh() {
		return true;
	}

	public boolean getCapRecEmptySensor() {
		return true;
	}

	public boolean getCapRecItalic() {
		return true;
	}

	public boolean getCapRecLeft90() {
		return true;
	}

	public boolean getCapRecNearEndSensor() {
		return true;
	}

	public boolean getCapRecPapercut() {
		return true;
	}

	public boolean getCapRecPresent() {
		return true;
	}

	public boolean getCapRecRight90() {
		return true;
	}

	public boolean getCapRecRotate180() {
		return true;
	}

	public boolean getCapRecStamp() {
		return true;
	}

	public boolean getCapRecUnderline() {
		return true;
	}

	public boolean getCapSlp2Color() {
		return true;
	}

	public boolean getCapSlpBarCode() {
		return true;
	}

	public boolean getCapSlpBitmap() {
		return true;
	}

	public boolean getCapSlpBold() {
		return true;
	}

	public boolean getCapSlpDhigh() {
		return true;
	}

	public boolean getCapSlpDwide() {
		return true;
	}

	public boolean getCapSlpDwideDhigh() {
		return true;
	}

	public boolean getCapSlpEmptySensor() {
		return true;
	}

	public boolean getCapSlpFullslip() {
		return true;
	}

	public boolean getCapSlpItalic() {
		return true;
	}

	public boolean getCapSlpLeft90() {
		return true;
	}

	public boolean getCapSlpNearEndSensor() {
		return true;
	}

	public boolean getCapSlpPresent() {
		return true;
	}

	public boolean getCapSlpRight90() {
		return true;
	}

	public boolean getCapSlpRotate180() {
		return true;
	}

	public boolean getCapSlpUnderline() {
		return true;
	}

	public boolean getCapTransaction() {
		return true;
	}

	// Properties
	public boolean getAsyncMode() {
		return true;
	}

	public void setAsyncMode(boolean asyncMode) {
	}

	public int getCharacterSet() {
		return 0;
	}

	public void setCharacterSet(int characterSet) {
	}

	public String getCharacterSetList() {
		return new String("-default-");
	}

	public boolean getCoverOpen() {
		return true;
	}

	public int getErrorLevel() {
		return 0;
	}

	public int getErrorStation() {
		return 0;
	}

	public String getErrorString() {
		return new String("-default-");
	}

	public boolean getFlagWhenIdle() {
		return true;
	}

	public void setFlagWhenIdle(boolean flagWhenIdle) {
	}

	public String getFontTypefaceList() {
		return new String("-default-");
	}

	public boolean getJrnEmpty() {
		return true;
	}

	public boolean getJrnLetterQuality() {
		return true;
	}

	public void setJrnLetterQuality(boolean jrnLetterQuality) {
	}

	public int getJrnLineChars() {
		return 0;
	}

	public void setJrnLineChars(int jrnLineChars) {
	}

	public String getJrnLineCharsList() {
		return new String("-default-");
	}

	public int getJrnLineHeight() {
		return 0;
	}

	public void setJrnLineHeight(int jrnLineHeight) {
	}

	public int getJrnLineSpacing() {
		return 0;
	}

	public void setJrnLineSpacing(int jrnLineSpacing) {
	}

	public int getJrnLineWidth() {
		return 0;
	}

	public boolean getJrnNearEnd() {
		return true;
	}

	public int getMapMode() {
		return 0;
	}

	public void setMapMode(int mapMode) {
	}

	public int getOutputID() {
		return 0;
	}

	public String getRecBarCodeRotationList() {
		return new String("-default-");
	}

	public boolean getRecEmpty() {
		return true;
	}

	public boolean getRecLetterQuality() {
		return true;
	}

	public void setRecLetterQuality(boolean recLetterQuality) {
	}

	public int getRecLineChars() {
		return 0;
	}

	public void setRecLineChars(int recLineChars) {
	}

	public String getRecLineCharsList() {
		return new String("-default-");
	}

	public int getRecLineHeight() {
		return 0;
	}

	public void setRecLineHeight(int recLineHeight) {
	}

	public int getRecLineSpacing() {
		return 0;
	}

	public void setRecLineSpacing(int recLineSpacing) {
	}

	public int getRecLinesToPaperCut() {
		return 0;
	}

	public int getRecLineWidth() {
		return 0;
	}

	public boolean getRecNearEnd() {
		return true;
	}

	public int getRecSidewaysMaxChars() {
		return 0;
	}

	public int getRecSidewaysMaxLines() {
		return 0;
	}

	public int getRotateSpecial() {
		return 0;
	}

	public void setRotateSpecial(int rotateSpecial) {
	}

	public String getSlpBarCodeRotationList() {
		return new String("-default-");
	}

	public boolean getSlpEmpty() {
		return true;
	}

	public boolean getSlpLetterQuality() {
		return true;
	}

	public void setSlpLetterQuality(boolean recLetterQuality) {
	}

	public int getSlpLineChars() {
		return 0;
	}

	public void setSlpLineChars(int recLineChars) {
	}

	public String getSlpLineCharsList() {
		return new String("-default-");
	}

	public int getSlpLineHeight() {
		return 0;
	}

	public void setSlpLineHeight(int recLineHeight) {
	}

	public int getSlpLinesNearEndToEnd() {
		return 0;
	}

	public int getSlpLineSpacing() {
		return 0;
	}

	public void setSlpLineSpacing(int recLineSpacing) {
	}

	public int getSlpLineWidth() {
		return 0;
	}

	public int getSlpMaxLines() {
		return 0;
	}

	public boolean getSlpNearEnd() {
		return true;
	}

	public int getSlpSidewaysMaxChars() {
		return 0;
	}

	public int getSlpSidewaysMaxLines() {
		return 0;
	}

	// Methods
	public void beginInsertion(int timeout) {
	}

	public void beginRemoval(int timeout) {
	}

	public void clearOutput() {
	}

	public void cutPaper(int percentage) {

		if (content.capacity() > 0) {

			try {

				thePrinter = new ThePrinter();

				thePrinter.setPrintContent(content);

				PrinterJob job = PrinterJob.getPrinterJob();

				PageFormat pageFormat = job.defaultPage();

				Paper paper = pageFormat.getPaper();

				paper.setSize(
					width * ThePrinter.INCH,
					height * lines + heightDelta);
					
				paper.setImageableArea(
					margin,
					0,
					paper.getWidth() - 2 * margin,
					paper.getHeight());

				pageFormat.setPaper(paper);

				thePrinter.setFont(font);

				job.setPrintable(thePrinter, pageFormat);

				job.print();

			} catch (PrinterException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			content.delete(0, content.capacity());
			lines = 0;

		}
	}

	public void endInsertion() {
	}

	public void endRemoval() {
	}

	public void printBarCode(
		int station,
		String data,
		int symbology,
		int height,
		int width,
		int alignment,
		int textPosition) {
	}

	public void printBitmap(
		int station,
		String fileName,
		int width,
		int alignment) {
	}

	public void printImmediate(int station, String data) {
	}

	public void printNormal(int station, String data) {
		content.append(data);
		lines++;
	}

	public void printTwoNormal(int stations, String data1, String data2) {
	}

	public void rotatePrint(int station, int rotation) {
	}

	public void setBitmap(
		int bitmapNumber,
		int station,
		String fileName,
		int width,
		int alignment) {
	}

	public void setLogo(int location, String data) {
	}

	public void transactionPrint(int station, int control) {
	}

	public void validateData(int station, String data) {
	}

	// 1.3
	// Capabilities

	public int getCapPowerReporting() {
		return 0;
	}

	// Properties
	public int getPowerNotify() {
		return 0;
	}

	public void setPowerNotify(int powerNotify) {
	}

	public int getPowerState() {
		return 0;
	}

	// Nothing new added for release 1.4

	/* 
	 * @see jpos.loader.JposServiceInstance#deleteInstance()
	 */
	public void deleteInstance() throws JposException {
	}

	public void close() throws jpos.JposException {
		super.close();
	}

	private class ThePrinter implements Printable {

		public static final double INCH = 72;

		private StringBuffer content;
		private Font font;

		public void setFont(Font font) {
			this.font = font;
		}

		public void setPrintContent(StringBuffer content) {
			this.content = content;
		}

		public int print(Graphics g, PageFormat pageFormat, int page)
			throws PrinterException {

			Graphics2D g2d = (Graphics2D) g;

			if (page == 0) {

				g2d.setColor(Color.black);

				g2d.setFont(font);

				FontMetrics fontMetrics = g2d.getFontMetrics();

				g2d.translate(
					pageFormat.getImageableX(),
					pageFormat.getImageableY());

				if (content != null) {

					BufferedReader br =
						new BufferedReader(
							new StringReader(content.toString()));

					String s = null;

					try {
						int i = 1;
						while ((s = br.readLine()) != null) {

							g2d.drawString(
								s,
								(int) 0,
								i * (int) fontMetrics.getHeight());

							i++;

						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

				return (PAGE_EXISTS);
			}

			return NO_SUCH_PAGE;
		}

	}

}
