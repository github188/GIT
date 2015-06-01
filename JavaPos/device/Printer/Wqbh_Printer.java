package device.Printer;

import java.util.Vector;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Interface.Interface_Printer;
import com.efuture.javaPos.Device.SerialPort.Javax_SerialConnection;
import com.efuture.javaPos.Device.SerialPort.Javax_SerialParameters;
import com.efuture.javaPos.Device.SerialPort.SerialConnectionException;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;


public class Wqbh_Printer implements Interface_Printer
{
    private Javax_SerialParameters para = null;
    private Javax_SerialConnection port = null;
    POSPrinter posPrinter = null;
    EventListener event = null;
    private int statusCode = 0;
    private String statusMsg = "";
    //private int row = 0;

    public void close()
    {
    	//
        if (port != null)
        {
            port.closeConnection();
        }

        //
        try
        {
            posPrinter.close();
            posPrinter.release();
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void cutPaper_Journal()
    {
        try
        {
            if (!checkStatus())
            {
                return;
            }
            
            for (int i = 0; i < 5; i++)
            {
                posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT,"\n");
            }
            
            posPrinter.cutPaper(90);
        }
        catch (JposException e)
        {
            new MessageBox(e.getMessage());
        }
        catch (Exception ex)
        {
            new MessageBox(ex.getMessage());
        }
    }

    public void cutPaper_Normal()
    {	
    	// 切纸
        port.sendChar((char) 0x1b);
        port.sendChar('d');
        port.sendChar((char) 0x01);
    }

    public void cutPaper_Slip()
    {
        cutPaper_Normal();
    }

    public boolean open()
    {
    	if (DeviceName.devicePrinter.length() <= 0) return false;
    	
    	// 串口
    	String[] conf = DeviceName.devicePrinter.split(";");
    	
        try
        {
            String[] arg = conf[0].split(",");
            para = new Javax_SerialParameters();

            if (arg.length >= 1)
            {
                para.setPortName(arg[0]);

                if (arg.length > 1)
                {
                    para.setBaudRate(arg[1]);
                    para.setParity(arg[2]);
                    para.setDatabits(arg[3]);
                    para.setStopbits(arg[4]);
                }
            }

            port = new Javax_SerialConnection(para);

            port.openConnection();
        }
        catch (SerialConnectionException ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开串口打印机异常:\n") + ex.getMessage());

            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            new MessageBox(Language.apply("打开串口打印机异常:\n") + ex.getMessage());

            return false;
        }

        // JPOS
        try
        {
        	if (conf.length <= 1 || conf[1].length() <= 0)
        	{
        		new MessageBox(Language.apply("未配置第二台打印机逻辑名\n请确定后重新配置后启动"));
        		return false;
        	}
        	
            posPrinter = new POSPrinter();

            event = new EventListener();

            posPrinter.open(conf[1].trim());

            posPrinter.setAsyncMode(true);
        }
        catch (JposException e)
        {
            e.printStackTrace();
            new MessageBox(Language.apply("打开JPOS打印机异常:\n") + e.getMessage());
            close();

            return false;
        }
        catch (Exception ex)
        {
            new MessageBox(ex.getMessage());
            close();

            return false;
        }

        return true;
    }

    public void printLine_Normal(String printStr)
    {
    	/**
    	try {
    		if (row > GlobalInfo.sysPara.printfreq)
    		{
    			Thread.sleep(GlobalInfo.sysPara.printdelay);
    			row = 0;
    		}
			row ++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
        port.sendString(printStr);
    }

    public void printLine_Journal(String printStr)
    {
        try
        {
            if (!checkStatus())
            {
                return;
            }

            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, printStr);
        }
        catch (JposException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            new MessageBox(ex.getMessage());
        }
    }

    private boolean checkStatus()
    {
        while (statusCode != 0)
        {
            if (new MessageBox(statusMsg + Language.apply("\n\n 1-重试 / 2-放弃当前打印")).verify() != GlobalVar.Key1)
            {
                return false;
            }
        }

        return true;
    }

    public void printLine_Slip(String printStr)
    {

        printLine_Normal(printStr);
    }

    public void setEnable(boolean enable)
    {
        try
        {
            if (enable)
            {
                if (!posPrinter.getClaimed())
                {
                    posPrinter.claim(1000);
                    posPrinter.setDeviceEnabled(true);
                    posPrinter.addStatusUpdateListener(event);
                }
            }
            else
            {
                if (posPrinter.getClaimed())
                {
                    posPrinter.removeStatusUpdateListener(event);
                    posPrinter.setDeviceEnabled(false);
                    posPrinter.release();
                }
            }
        }
        catch (JposException e)
        {
            new MessageBox(e.getMessage());
        }
        catch (Exception ex)
        {
            new MessageBox(ex.getMessage());
        }
    }

    public boolean passPage_Journal()
    {
    	return false;
    }

    public boolean passPage_Normal()
    {
        port.sendChar((char) 0x1D);
        port.sendChar((char) 0x0C);
        port.sendChar((char) 0x01);
        
        return true;
    }

    public boolean passPage_Slip()
    {
    	return false;
    }

    private class EventListener implements StatusUpdateListener
    {
        private int getSUEMessage(int code)
        {
            int value = 0;

            try
            {
                switch (code)
                {
                    case POSPrinterConst.PTR_SUE_COVER_OPEN: //打印机盖打开
                        value = POSPrinterConst.PTR_SUE_COVER_OPEN;
                        statusMsg = Language.apply("打印机盖打开");

                        break;

                    case POSPrinterConst.PTR_SUE_JRN_EMPTY: //日志打印机缺纸
                        value = POSPrinterConst.PTR_SUE_JRN_EMPTY;
                        statusMsg = Language.apply("日志打印机缺纸");

                        break;

                    case POSPrinterConst.PTR_SUE_JRN_NEAREMPTY: //日志打印机临近缺纸
                        value = POSPrinterConst.PTR_SUE_JRN_NEAREMPTY;
                        statusMsg = Language.apply("日志打印机临近缺纸");

                        break;

                    case POSPrinterConst.PTR_SUE_JRN_PAPEROK: //日志打印机纸张异常状态解除
                        value = POSPrinterConst.PTR_SUE_JRN_PAPEROK;
                        statusMsg = Language.apply("日志打印机纸张异常状态解除");

                        break;

                    case POSPrinterConst.PTR_SUE_REC_EMPTY: //票据打印机缺纸
                        value = POSPrinterConst.PTR_SUE_REC_EMPTY;
                        statusMsg = Language.apply("票据打印机缺纸");

                        break;

                    case POSPrinterConst.PTR_SUE_REC_NEAREMPTY: //票据打印机临近缺纸
                        value = POSPrinterConst.PTR_SUE_REC_NEAREMPTY;
                        statusMsg = Language.apply("票据打印机临近缺纸");

                        break;

                    case POSPrinterConst.PTR_SUE_REC_PAPEROK: //票据打印机纸张异常状态解除
                        value = POSPrinterConst.PTR_SUE_REC_PAPEROK;
                        statusMsg = Language.apply("票据打印机纸张异常状态解除");

                        break;

                    case POSPrinterConst.PTR_SUE_SLP_EMPTY: //平推打印机缺纸
                        value = POSPrinterConst.PTR_SUE_SLP_EMPTY;
                        statusMsg = Language.apply("平推打印机缺纸");

                        break;

                    case POSPrinterConst.PTR_SUE_SLP_NEAREMPTY: //平推打印机临时缺纸
                        value = POSPrinterConst.PTR_SUE_SLP_NEAREMPTY;
                        statusMsg = Language.apply("平推打印机临时缺纸");

                        break;

                    case POSPrinterConst.PTR_SUE_SLP_PAPEROK: //平推打印机纸张异常状态解除
                        value = POSPrinterConst.PTR_SUE_SLP_PAPEROK;
                        statusMsg = Language.apply("平推打印机纸张异常状态解除");

                        break;

                    case POSPrinterConst.PTR_SUE_IDLE: //打印机等待指令状态
                        value = POSPrinterConst.PTR_SUE_IDLE;
                        statusMsg = Language.apply("打印机等待指令状态");

                        break;

                    case POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_EMPTY: //日志打印机墨盒缺墨状态
                        value = POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_EMPTY;
                        statusMsg = Language.apply("日志打印机墨盒缺墨状态");

                        break;

                    case POSPrinterConst.PTR_SUE_JRN_HEAD_CLEANING: //日志打印机清洗打印磁头状态
                        value = POSPrinterConst.PTR_SUE_JRN_HEAD_CLEANING;
                        statusMsg = Language.apply("日志打印机清洗打印磁头状态");

                        break;

                    case POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_NEAREMPTY: //日志打印机临近缺默状态
                        value = POSPrinterConst.PTR_SUE_JRN_CARTRIDGE_NEAREMPTY;
                        statusMsg = Language.apply("日志打印机临近缺默状态");

                        break;

                    case POSPrinterConst.PTR_SUE_JRN_CARTDRIGE_OK: //日志打印机墨盒异常状态解除
                        value = POSPrinterConst.PTR_SUE_JRN_CARTDRIGE_OK;
                        statusMsg = Language.apply("日志打印机墨盒异常状态解除");

                        break;

                    case POSPrinterConst.PTR_SUE_REC_CARTRIDGE_EMPTY: //票据打印机墨盒缺墨状态
                        value = POSPrinterConst.PTR_SUE_REC_CARTRIDGE_EMPTY;
                        statusMsg = Language.apply("票据打印机墨盒缺墨状态");

                        break;

                    case POSPrinterConst.PTR_SUE_REC_HEAD_CLEANING: //票据打印机清洗打印磁头状态
                        value = POSPrinterConst.PTR_SUE_REC_HEAD_CLEANING;
                        statusMsg = Language.apply("票据打印机清洗打印磁头状态");

                        break;

                    case POSPrinterConst.PTR_SUE_REC_CARTRIDGE_NEAREMPTY: //票据打印机临近缺墨状态
                        value = POSPrinterConst.PTR_SUE_REC_CARTRIDGE_NEAREMPTY;
                        statusMsg = Language.apply("票据打印机临近缺墨状态");

                        break;

                    case POSPrinterConst.PTR_SUE_REC_CARTDRIGE_OK: //票据打印机墨盒异常状态解除
                        value = POSPrinterConst.PTR_SUE_REC_CARTDRIGE_OK;
                        statusMsg = Language.apply("票据打印机墨盒异常状态解除");

                        break;

                    case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_EMPTY: //平推打印机缺墨状态
                        value = POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_EMPTY;
                        statusMsg = Language.apply("平推打印机缺墨状态");

                        break;

                    case POSPrinterConst.PTR_SUE_SLP_HEAD_CLEANING: //平推打印机清洗打印磁头状态
                        value = POSPrinterConst.PTR_SUE_SLP_HEAD_CLEANING;
                        statusMsg = Language.apply("平推打印机清洗打印磁头状态");

                        break;

                    case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_NEAREMPTY: //平推打印机临近缺墨状态
                        value = POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_NEAREMPTY;
                        statusMsg = Language.apply("平推打印机临近缺墨状态");

                        break;

                    case POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_OK: //平推打印机墨盒异常状态解除
                        value = POSPrinterConst.PTR_SUE_SLP_CARTRIDGE_OK;
                        statusMsg = Language.apply("平推打印机墨盒异常状态解除");

                        break;

                    default:
                        value = 0; //当前打印机活动正常状态
                }

                return value;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                statusMsg = Language.apply("打印机未知异常");

                return -1;
            }
        }

        public void statusUpdateOccurred(StatusUpdateEvent sue)
        {
            try
            {
                statusCode = getSUEMessage(sue.getStatus());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

	public void enableRealPrintMode(boolean flag)
	{
	}

	public Vector getPara() 
	{
		return null;
	}

	public String getDiscription() 
	{
		return Language.apply("万千百货专用打印机(请联系开发人员进行配置)");
	}

	public void setEmptyMsg_Slip(String msg)
	{
	}
}
