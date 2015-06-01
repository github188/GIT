package com.efuture.javaPos.Device;

import java.io.BufferedReader;
import java.io.File;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalVar;


public class DeviceName
{
    public static String deviceKeyBoard = "defaultPOSKeyboard";
    public static String deviceCashBox = "defaultCashBox";
    public static String devicePrinter = "defaultPOSPrinter";
    public static String deviceMSR = "defaultMSR";
    public static String deviceLineDisplay = "defaultLineDisplay";
    public static String deviceScanner = "COM1";
    public static String deviceICCard = ".\\org.javapos.lib\\,0,57600";
    public static String deviceElectronicScale = "";
    public static String deviceEvaluation = "";
    public static String deviceBankTracker= "";

    public DeviceName()
    {
        readDeviceName();
        KeyFilter();
    }
    
    public void KeyFilter()
    {
    	if (DeviceName.deviceKeyBoard.indexOf("(") >= 0)
    	{
    		String line = DeviceName.deviceKeyBoard.substring(DeviceName.deviceKeyBoard.indexOf("(")+1,DeviceName.deviceKeyBoard.indexOf(")"));
    		
    		String[] key = line.split(",");
    		
    		int num[] = new int[key.length];
    		
    		for (int i = 0 ; i <  key.length; i ++)
    		{
    			if (key[i] == null || key[i].length() <= 0) continue;
    			
    			int num1 = Convert.toInt(key[i]);
    			if (num1 == 0)
    			{
    				num[i] = (int)key[i].charAt(0);
    			}
    			else if (num1 > 0)
    			{
    				num[i] = num1;
    			}
    		}
    		
    		NewKeyListener.keyFilter = num;
    	}
    }

    public void readDeviceName()
    {
        BufferedReader br;

        try
        {
            String file = GlobalVar.ConfigPath + "/DeviceName.ini";

            if (!new File(file).exists())
            {
                return;
            }

            br = CommonMethod.readFile(file);

            if (br == null)
            {
                return;
            }

            String line;
            String[] sp;

            while ((line = br.readLine()) != null)
            {
                if ((line == null) || (line.length() <= 0))
                {
                    continue;
                }

                sp = line.trim().split("=");
/*
                if (sp.length < 2)
                {
                    continue;
                }
*/
                if (sp[0].trim().compareToIgnoreCase("Printer") == 0)
                {
                    DeviceName.devicePrinter = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("MSR") == 0)
                {
                    DeviceName.deviceMSR = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("KeyBoard") == 0)
                {
                    DeviceName.deviceKeyBoard = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("CashBox") == 0)
                {
                    DeviceName.deviceCashBox = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("LineDisplay") == 0)
                {
                    DeviceName.deviceLineDisplay = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("Scanner") == 0)
                {
                    DeviceName.deviceScanner = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("ICCard") == 0)
                {
                    DeviceName.deviceICCard = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("ElectronicScale") == 0)
                {
                	DeviceName.deviceElectronicScale = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("Evaluation") == 0){
                	DeviceName.deviceEvaluation = (sp.length>=2?sp[1].trim():"");
                }
                else if (sp[0].trim().compareToIgnoreCase("BankTracker") == 0){
                	DeviceName.deviceBankTracker = (sp.length>=2?sp[1].trim():"");
                }
            }

            br.close();
        }
        catch (Exception e)
        {
        	PosLog.getLog(getClass()).debug(e);
        }
    }
}
