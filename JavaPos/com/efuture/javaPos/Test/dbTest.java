package com.efuture.javaPos.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import org.eclipse.swt.internal.win32.OS;


import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Global.GlobalInfo;

import device.ICCard.IC_Cpu_cczz;
import device.Printer.HisensePT180_Printer;
import device.Printer.IBM4610Serial_Printer;
import device.Printer.Parallel_Printer;
import device.Printer.SundarPT2800_Printer;
import device.Scanner.Serial_Scanner;



//import com.efuture.commonKit.ManipulatePrecision;


//本地数据库测试使用
public class dbTest {
	//public b a= null;
	protected Vector v = null;
	int  startid = 0;
	int  endid = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自动生成方法存根
		new dbTest();
		
	}
	
	public dbTest()
	{
		String na = "http://172.17.8.155:8080/PosPosCenter/PopPosCenterServer";
		Http a = new Http(na);
		a.init();
		
		for (int i = 0 ; i < 1000 ; i++)
		{
		String line = " 202&@&2222&@&999&@&01&@& &@& #@#<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><table><row>"+"<str>test"+i+"</str>"+"</row></table></root>";
		a.setRequestString(line);
		
		System.out.println(a.execute());
		}
		//DeviceName.deviceICCard = "100,115200,654321,0,32,c:\\cpu\\,D5ULM";
		//IC_Cpu_cczz ic = new IC_Cpu_cczz();
		//ic.open();
		//System.out.println(ic.findCard());
		
		
		/*DeviceName.deviceScanner = "COM10,9600,N,8,1";
		Serial_Scanner ss = new Serial_Scanner();
		ss.open();
		System.out.println("开始");
		ss.close();*/
		//OS.FindWindowW(arg0, arg1)
		
		// TODO Auto-generated method stub
		//double a = 16;
		//a -= 36 - 20;
		//a = ManipulatePrecision.doubleConvert(a, 2, 1);
		
		//System.out.println(ManipulatePrecision.doubleConvert(a));
		//String ax = "{A";
		//a.printLine_Normal(ax);
		
		//char[] con1 = {(int)123,(int)65,(int)29,(int)107,(int)73,(int)12};
		//char[] con1 = {(int)29,(int)107,(int)2};
		//String s = "73";
		
		//a.sendString(String.valueOf(con1));
		//a.port.sendChar((char)0x1B);
		//a.port.sendChar((char)0x40);
		//a.setEnable(true);

		//		char[] con = {(char)0x1d,(char)0x6b,'2'};
		
//		a.port.sendString(String.valueOf(con)+ac+String.valueOf((char)0x00));
//		a.port.sendString(String.valueOf(con1)+ String.valueOf(s) + ac);
		
		//a.port.sendChar((char)0x1D);
		//a.port.sendChar((char)0x6B);
		//a.port.sendChar('2');
		
		//a.printLine_Normal(ac);
		//a.printLine_Normal(String.valueOf((char)0x00));
		//a.printLine_Normal(String.valueOf(con1) + ac + (char)0x00);
		

		//System.out.println("11111111111");
		//System.exit(0);
		//System.out.println(ManipulatePrecision.EncodeString("BjcsfGZ_PaymentBankFunc", "0000"));
		/**
		String line ="2222&@&5075&@&48&@&01&@& &@& #@#<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><table  Name=\"com.efuture.javaPos.Struct.BankLogDef\"   itemCount=\" 1\" ><row><net_bz>N</net_bz><rowcode>12</rowcode><rqsj>2011/11/22 17:41:34</rqsj><syjh>5075</syjh><fphm>100157</fphm><syyh>9999</syyh><type>3</type><je>0.0</je><oldrq> </oldrq><oldtrace>0</oldtrace><typename>交易签到</typename><classname>PaymentBankFunc</classname><cardno> </cardno><trace>815055</trace><bankinfo>0000测试银行</bankinfo><crc> </crc><retcode>00</retcode><retmsg>模拟第三方支付交易成功!</retmsg><retbz>Y</retbz><allotje>0.0</allotje><memo>100</memo><kye>0.0</kye><authno> </authno></row></table></root>";
		Http h = new Http();

		System.out.println("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?".split(",").length);
		System.out.println("rowcode,rqsj,syjh,fphm,syyh,type,je,oldrq,oldtrace,cardno,trace,bankinfo,crc,retcode,retmsg,retbz,classname".split(",").length);
		System.out.println("i,s,s,l,s,s,f,s,l,s,l,s,s,s,s,c,s".split(",").length);
		System.out.println("rowcode,rqsj,syjh,fphm,syyh,type,je,oldrq,oldtrace,cardno,trace,bankinfo,crc,retcode,retmsg,retbz,classname".split(",").length);
		System.out.println("rowcode,rqsj,syjh,fphm,syyh,type,je,oldrq,oldtrace,cardno,trace,bankinfo,crc,retcode,retmsg,retbz,classname".split(",").length);

		h = new Http("172.22.0.211", 8080, "/PosServerPosJB/PosServer");
            h.init();
            h.setConncetTimeout(10000); //连接超时
            h.setReadTimeout(30000); //处理超时
		h.setRequestString(line);
		System.out.println(h.execute());*/
            /**
		new MessageBox("222222222");
		String saleNo = "17351090";
		String posNo = "0501";
		String cashierNumber = "00115851";
		boolean successBit = false ; 
		String receiveMoneyType = "01|262.0"; 
		String receiveMoneyTime = "2011-6-26 18:02:22";
		String memo = "";
		new MessageBox("3333333333333333");
		IFuturnService service = new FuturnServiceImpl();
		InfoEntity info = service.receiveMoneyCallPdaMw(saleNo, posNo, cashierNumber, successBit, receiveMoneyType, receiveMoneyTime, memo);
		System.out.println("111111111111111111111");
		System.out.println(info.getCode());
		System.out.println(info.getCodeInfo());*/
	}
	
	public  final int daysBetween(Date early, Date late) {
        java.util.Calendar calst = java.util.Calendar.getInstance();
        java.util.Calendar caled = java.util.Calendar.getInstance();
        calst.setTime(early);
        caled.setTime(late);
        //设置时间为0时
        calst.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calst.set(java.util.Calendar.MINUTE, 0);
        calst.set(java.util.Calendar.SECOND, 0);
        caled.set(java.util.Calendar.HOUR_OF_DAY, 0);
        caled.set(java.util.Calendar.MINUTE, 0);
        caled.set(java.util.Calendar.SECOND, 0);

        //得到两个日期相差的天数
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst
                .getTime().getTime() / 1000)) / 3600 / 24;
        return days;
    }

	
	public void testsocket()
	{
		DecimalFormat df = new DecimalFormat( "0000" );
		String s = "1000005099990000000019    1.00    1.00    0.0001    0.00    0.00000130030000307388888888    1.00";
		String s1= "1000005099990000000137    1.00  399.00    0.0000    0.00    0.00000130030000307388888888    1.00";

		System.out.println(s1.length());
		s = df.format( s1.length() ) + s1;
		Socket socket;
		try
		{
			socket = new Socket( "192.168.64.212",7139 );

		System.out.println( socket.isConnected() );
		OutputStream os = new DataOutputStream( socket.getOutputStream() );
		InputStream is = new DataInputStream( socket.getInputStream() );
		os.write( s.getBytes() );
		System.out.println( "over ~!!!" );
		
		byte[] head = new byte[4];
		is.read( head, 0, 4 );
		System.out.println( "head ::: '" + new String( head ) + "'" );
		byte[] pcode = new byte[ 2 ];
		is.read( pcode, 0, pcode.length );
		System.out.println( "pcode ::: '" + new String( pcode ) + "'" );
		byte[] rcode = new byte[ 2 ];
		is.read( rcode, 0, rcode.length );
		System.out.println( "rcode ::: '" + new String( rcode ) + "'" );
		byte[] traceid = new byte[ 10 ];
		is.read( traceid, 0, traceid.length );
		System.out.println( "traceid ::: '" + new String( traceid ) + "'" );
		byte[] posid = new byte[ 4 ];
		is.read( posid, 0, posid.length );
		System.out.println( "posid ::: '" + new String( posid ) + "'" );
		byte[] cashid = new byte[ 4 ];
		is.read( cashid, 0, cashid.length );
		System.out.println( "cashid ::: '" + new String( cashid ) + "'" );
		byte[] seqid = new byte[ 10 ];
		is.read( seqid, 0, seqid.length );
		System.out.println( "seqid ::: '" + new String( seqid ) + "'" );
		byte[] settdate = new byte[ 10 ];
		is.read( settdate, 0, settdate.length );
		System.out.println( "settdate ::: '" + new String( settdate ) + "'" );
		byte[] transtime = new byte[ 19 ];
		is.read( transtime, 0, transtime.length );
		System.out.println( "transtime ::: '" + new String( transtime ) + "'" );
		byte[] money = new byte[ 8 ];
		is.read( money, 0, money.length );
		System.out.println( "money ::: '" + new String( money ) + "'" );
		byte[] tmoney = new byte[ 8 ];
		is.read( tmoney, 0, tmoney.length );
		System.out.println( "tmoney ::: '" + new String( tmoney ) + "'" );
		byte[] pscore = new byte[ 6 ];
		is.read( pscore, 0, pscore.length );
		System.out.println( "pscore ::: '" + new String( pscore ) + "'" );
		byte[] cscore = new byte[ 6 ];
		is.read( cscore, 0, cscore.length );
		System.out.println( "cscore ::: '" + new String( cscore ) + "'" );
		byte[] lscore = new byte[ 6 ];
		is.read( lscore, 0, lscore.length );
		System.out.println( "lscore ::: '" + new String( lscore ) + "'" );
		byte[] charge = new byte[ 8 ];
		is.read( charge, 0, charge.length );
		System.out.println( "charge ::: '" + new String( charge ) + "'" );
		byte[] distype = new byte[ 2 ];
		is.read( distype, 0, distype.length );
		System.out.println( "distype ::: '" + new String( distype ) + "'" );
		byte[] dispmoney = new byte[ 8 ];
		is.read( dispmoney, 0, dispmoney.length );
		System.out.println( "dispmoney ::: '" + new String( dispmoney ) + "'" );
		byte[] disinmoney = new byte[ 8 ];
		is.read( disinmoney, 0, disinmoney.length );
		System.out.println( "disinmoney ::: '" + new String( disinmoney ) + "'" );
		byte[] disusemoney = new byte[ 8 ];
		is.read( disusemoney, 0, disusemoney.length );
		System.out.println( "disusemoney ::: '" + new String( disusemoney ) + "'" );
		byte[] disbalance = new byte[ 8 ];
		is.read( disbalance, 0, disbalance.length );
		System.out.println( "disbalance ::: '" + new String( disbalance ) + "'" );
		byte[] disvaliddate = new byte[ 10 ];
		is.read( disvaliddate, 0, disvaliddate.length );
		System.out.println( "disvaliddate ::: '" + new String( disvaliddate ) + "'" );
		byte[] xxmoney = new byte[ 8 ];
		is.read( xxmoney, 0, xxmoney.length );
		System.out.println( "xxmoney ::: '" + new String( xxmoney ) + "'" );
		byte[] cardid = new byte[ 16 ];
		is.read( cardid, 0, cardid.length );
		System.out.println( "cardid ::: '" + new String( cardid ) + "'" );
		byte[] pmoney = new byte[ 8 ];
		is.read( pmoney, 0, pmoney.length );
		System.out.println( "pmoney ::: '" + new String( pmoney ) + "'" );
		byte[] cmoney = new byte[ 8 ];
		is.read( cmoney, 0, cmoney.length );
		System.out.println( "cmoney ::: '" + new String( cmoney ) + "'" );
		byte[] lmoney = new byte[ 8 ];
		is.read( lmoney, 0, lmoney.length );
		System.out.println( "lmoney ::: '" + new String( lmoney ) + "'" );
		byte[] validdate = new byte[ 10 ];
		is.read( validdate, 0, validdate.length );
		System.out.println( "validdate ::: '" + new String( validdate ) + "'" );
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double getformatPara(String fmt,String value)
	{
		if (fmt.trim().charAt(0) == '%')
		{
			String len = fmt.trim().substring(1,fmt.trim().indexOf("."));
			if (Convert.toInt(len) >= value.length()) value = Convert.increaseCharForward(value, '0', Convert.toInt(len));
			else value = value.substring(0,Convert.toInt(len));
			String point = fmt.trim().substring(fmt.trim().indexOf(".")+1);
			int point1 = Convert.toInt(point);
			String newvalue = value.substring(0,(value.length() - point1))+"."+value.substring((value.length() - point1));
			return Convert.toDouble(newvalue);
		}
		
		return Convert.toDouble(value);
	}
	
	public String formatParam(String fmt,Object val)
	{
		try
		{
			String fmtval = "";
			if (fmt == null || fmt.trim().length() <= 0) fmt = "%s";
			if (fmt.indexOf('%') < 0) fmt = "%"+fmt;
			int len = fmt.length();
			int i = fmt.indexOf('%') + 1;
			int j = i;
			for (;i<len;i++) if ((fmt.charAt(i) >= 'a' && fmt.charAt(i) <= 'z') || (fmt.charAt(i) >= 'A' && fmt.charAt(i) <= 'Z')) break;
			int fmtlen = Convert.toInt(fmt.substring(j,i));
			int fmtdec = (int)Math.abs(ManipulatePrecision.doubleConvert(Convert.toDouble(fmt.substring(j,i)) - Convert.toInt(fmt.substring(j,i))) * 10);
			if (fmt.charAt(i) == 'd') 
			{
				int value = Convert.toInt(val);
				if (fmtdec > 0)
				{
					// %.2d表示以无小数点2位小数表示
					int dec10 = 1;
					for (int n=0;n<fmtdec;n++) dec10 *= 10;
					value = (int)ManipulatePrecision.doubleConvert(Convert.toDouble(val)*dec10);
					if (fmt.indexOf('.') > -1) fmt = fmt.substring(0,j) + (fmtlen>0?fmt.substring(j,fmt.indexOf('.')):"") + fmt.substring(i);
					else fmt = fmt.substring(0,j) + (fmtlen>0?String.valueOf(fmtlen):"") + fmt.substring(i);
				}
				fmtval = String.format(fmt, new Object[]{new Integer(value)});
			}
			else if (fmt.charAt(i) == 'f') fmtval = String.format(fmt, new Object[]{new Double(Convert.toDouble(val))});
			else fmtval = String.format(fmt,new Object[]{String.valueOf(val)});
			
			// 超出长度部分进行截取
			if (fmtlen != 0 && fmtval.length() > Math.abs(fmtlen))
			{
				if (fmtlen >= 0) fmtval = fmtval.substring(fmtval.length() - fmtlen);
				else fmtval = fmtval.substring(0,Math.abs(fmtlen));
			}
			
			return fmtval;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return String.valueOf(val);
		}
	}
	
	public String getObjectValue(String objectExpress,int index)
	{
		return "1";
	}
	
	public String getExpressValue(String express,int index)
	{
		String strTemp = null;
		while (express.indexOf(":")!=-1)
		{ 
			strTemp=express.substring(express.indexOf(":"),express.length()); 
			if (strTemp.indexOf(" ")!=-1) strTemp = strTemp.substring(0, strTemp.indexOf(" "));

			String expObj = getObjectValue(strTemp,index);
			if (expObj == null ) return null;
			express=express.replace(strTemp,expObj); 
		} 
		return ExpressionDeal.SpiltExpression(express);
	}
	
	public void abc(Double a)
	{
		 
	}
	
	public boolean bb()
	{
		System.out.println("11111");
		return true;
	}
}
	

	



