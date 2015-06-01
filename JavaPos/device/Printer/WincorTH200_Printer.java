package device.Printer;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.Language;

import device.DeviceInfo;


public class WincorTH200_Printer extends Serial_Printer 
{
	
	public String Qrcode = "#Qrcode:" ;
	//Wincor TH200i热敏打印机 支持的二维码打印
	//二维码大小：1D 6F 00 05 00 02 
	//二维码打印：1D 6B 0B 51 41 2C 30 31 32 33 34 35 36 37 38 39 00  (固定格式 1D 6B 0B 51 41 2C ,00为结束标志)
	public void print_Qrcode(String printStr)
	{
		//设置二维码大小		
		port.sendChar((char)0x1D);
		port.sendChar((char)0x6F);
		port.sendChar((char)0x00);
		port.sendChar((char)0x05);
		port.sendChar((char)0x00);
		port.sendChar((char)0x02);
			
		//打印二维码
		port.sendChar((char)0x1D);
		port.sendChar((char)0x6B);
		port.sendChar((char)0x0B);
		port.sendChar((char)0x51);
		port.sendChar((char)0x41);
		port.sendChar((char)0x2C);
		
		
		byte[] printStrByte=printStr.getBytes();
		String hex=printHexString(printStrByte);
		char[] c = DeviceInfo.convertCmdStringToCmdChar(hex);
		for (int i=0;i<c.length;i++)
		{
			port.sendChar(c[i]);
		}
		port.sendChar((char)0x00);
		port.sendChar((char)0x0A);
				
	}
	
	//把字符串转换成16进制形式的串：如 0x1b
	public static String printHexString(byte[] b)
	{
		StringBuffer returnValue = new StringBuffer();
		for (int i = 0; i < b.length; i++) 
		{
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			if(i<b.length-1){
				returnValue.append(hex.toUpperCase()+"&");
			}else{
				returnValue.append(hex.toUpperCase());
			}
		}

		return returnValue.toString();
	}
	
	public void cutPaper_Normal() 
	{
    	for (int i = 0;i < cutLine;i++)
    	{
    		printLine_Normal("\n");
    	}
    	port.sendChar((char)0x1b);
    	port.sendChar((char)0x69);
	}
	
	public String getDiscription()
	{
		return Language.apply("WincorTH200款机的串口打印机");
	}
	

   
	public void printLine_Normal(String printStr)
    {
        boolean done = false;

        if (printStr.indexOf("Big&") == 0)
        {
            done = true;
            setBigChar(true);
        }

        if (done)
        {
            if (printStr.length() > 4)
            {
                printStr = printStr.substring(4);
            }
            else
            {
                printStr = "\n";
            }
        }
        
        if(printStr.indexOf(Qrcode) >= 0)
        {
        	print_Qrcode(printStr.substring(Qrcode.length())); //打印二维码
        }
        else
        {
        	 super.printLine_Normal(printStr);
        }
       

        if (done)
        {
            setBigChar(false);
        }
    }

    public void setBigChar(boolean status)
    {
        if (status)
        {
            //char[] con = { 0x1b, 0x21,0x10};
            //port.sendString(String.valueOf(con));
            char[] con1 = { 0x1b, 0x21,0x20};
            port.sendString(String.valueOf(con1));
        }
        else
        {	
            char[] con = { 0x1b, 0x21,0x00};
            port.sendString(String.valueOf(con));
            //char[] con1 = { 0x1b, 'S'};
            //port.sendString(String.valueOf(con));
        }
    }
}
