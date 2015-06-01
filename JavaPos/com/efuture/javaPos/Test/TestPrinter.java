package com.efuture.javaPos.Test;

import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Global.GlobalVar;

import device.Printer.JavaxPrintService_Printer;

public class TestPrinter
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO 自动生成方法存根

		GlobalVar.ConfigPath = "D:\\Code_Java\\tfsworkspace\\JavaPos\\javaPos.ConfigFile";
		DeviceName.devicePrinter = "\\EFUTURE-40\\HP LaserJet 1020,宋体,9,常规,0,0,纵向,0,0,0,0,,,Y,4";
		String str = "yebaokang\n#Qrcode:Wellcome to China.";
		JavaxPrintService_Printer printer = new JavaxPrintService_Printer();
		printer.open();
		printer.printLine_Normal(str);
		printer.cutPaper_Normal();
	}

}
