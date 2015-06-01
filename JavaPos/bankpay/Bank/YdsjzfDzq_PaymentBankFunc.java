package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import bankpay.Bank.YdsjzfDzq1_PaymentBankFunc.xmlMobile;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class YdsjzfDzq_PaymentBankFunc extends PaymentBankFunc
{

	Socket s = null;
	OutputStream ps = null;
	InputStream br = null;
	boolean connect = false;
	public String ip = null;
	public int port = 0;

	public static String serverUrl = "";
	public String merId = "";
	public static String pwd = "";
	public static String orderIdOnly = "";
	public static int typeN;
	public static String oldseq = "";

	//	每个交易都包含的字段

	public String[] getFuncItem()
	{
		String[] func = new String[4];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKYE + "]" + "交易查询";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		if (!getServerInfo()) return false;
		typeN = type;
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = "手 机 号";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "金    额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "交易流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "手 机 号";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "金    额";

				break;

			case PaymentBank.XYKTH: //隔日退货   
				grpLabelStr[0] = "交易流水号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "手 机 号";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "金    额";

				break;

			case PaymentBank.XYKYE: //隔日退货   
				grpLabelStr[0] = "交易流水";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "手 机 号";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易查询";

				break;

		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKTH: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键进行交易查询";

				break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
			
			xmlMobile dd = new xmlMobile();
			ManipulateDateTime mdt = new ManipulateDateTime();
			//dd.setStoreId(Convert.increaseCharForward(GlobalInfo.sysPara.mktcode, '0', 8)) ; //门店编号
			dd.setStoreId(Convert.increaseCharForward(GlobalInfo.sysPara.mktcode, '0', 8)) ; //门店编号
			dd.setPosId(Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 8)); //终端号
			
			dd.setReqDate(mdt.getDateByEmpty()); //请求日期
			dd.setReqTime(mdt.getTimeByEmpty()); //请求时间
			dd.setCalling( olddate.trim());
			if(!(type==PaymentBank.XYKCX ||type==PaymentBank.XYKTH)){
				dd.setOrderId(orderIdOnly);
			}
			
			dd.setCheckCode(pwd);
			dd.setMerId(merId);
			dd.setAmt(jestr);
			
			bld.cardno = dd.getCalling();
			bld.oldrq = "";
			String xmlStr = "";
			//xmlStr = "<?xml version='1.0' encoding='GBK' ?> "+"<xmlMobile>"+xmlStr.substring(5,xmlStr.length()-6)+"</xmlMobile>";

			switch (type)
			{

				case PaymentBank.XYKXF:
					dd.setFunCode("SC1020");
					xmlStr = dd.toXml();
					break;
				case PaymentBank.XYKCX:
					dd.setFunCode("SC1030");
					dd.setTranSeq(oldseqno);
					dd.setOrderId(oldseqno);
					oldseq = oldseqno;
					xmlStr = dd.toXml();
					break;
				case PaymentBank.XYKTH:
					dd.setFunCode("SC1040") ;
					dd.setTranSeq(oldseqno);
					dd.setOrderId(oldseqno);
					oldseq = oldseqno;
					xmlStr = dd.toXml();
					break;
				case PaymentBank.XYKYE:
					dd.setFunCode("SC1060");
					
					dd.setOrderId(oldseqno);
						
					dd.setTranSeq(oldseqno);
					xmlStr = dd.toXml();
					break;
				default:
					return false;
			}

			String result = httpPost(serverUrl, xmlStr);
			//String result = "<?xml version='1.0' encoding='gb2312' ?><xmlMobile><colDate>20130806</colDate><memo>撤销成功</memo><merId>888000174200001</merId><calling>13514978522</calling><reqDate>20130806</reqDate><amt>100</amt><retCode>0001</retCode><funCode>SC1060</funCode><reqTime>160036</reqTime><posId>00000916</posId><storeId>00000201</storeId><tranSeq>90621758056780294823</tranSeq><redAmt>100</redAmt><orderId>2013080615563513514978522729</orderId></xmlMobile>";
			xmlMobile ddr = new xmlMobile();
			switch (type)
			{

				case PaymentBank.XYKXF:
					ddr = dd.fromXml(result);
					break;
				case PaymentBank.XYKCX:
					ddr = dd.fromXml(result);
					break;
				case PaymentBank.XYKTH:
					ddr = dd.fromXml(result);
					break;
				case PaymentBank.XYKYE:
					ddr = dd.fromXml(result);
					break;
				default:
					return false;
			}
			
			if (ddr != null)
			{   
				if(type == PaymentBank.XYKYE){
					if (!ddr.retCode.equals("0000")&&!ddr.retCode.equals("0001")&&!ddr.retCode.equals("0002"))
					{
						bld.retcode = ddr.retCode;
						bld.retmsg = ddr.memo;
						new MessageBox("获取交易信息失败：" + ddr.memo);
						return false;
					}
				}else{
					if (!ddr.retCode.equals("0000"))
					{
						bld.retcode = ddr.retCode;
						bld.retmsg = ddr.memo;
						new MessageBox("获取交易信息失败：" + ddr.memo);
						return false;
					}
				}
				

			}else{
				return false;
			}

			// 读取应答数据
			if (!XYKReadResult(ddr)) { return false; }
			
			// 检查交易是否成功
			XYKCheckRetCode(type);

			// 打印签购单

			XYKPrintDoc(type,ddr);

			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}finally{
			oldseq = "";
			pwd = "";
			orderIdOnly = "";
		}
	}

	private boolean XYKReadResult(xmlMobile ddr)
	{
		bld.retcode = ddr.retCode.trim();
		
		if(ddr.tranSeq!=null && ddr.memo!=null)bld.retmsg = ddr.tranSeq.trim() + " " + ddr.memo.trim();
		if(ddr.amt!=null)bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(ddr.amt.trim()) / 100, 2, 1);
		if(ddr.calling!=null)bld.cardno = ddr.calling.trim();
		if(ddr.colDate!=null)bld.memo = ddr.colDate.trim();
		
		if (ddr.funCode!=null&&ddr.funCode.trim().equals("SC1060"))
		{
			new MessageBox("返回信息：" + bld.retmsg);
		}
		return true;
	}

	public boolean XYKCheckRetCode(int type)
	{
		if(type == PaymentBank.XYKYE){
			if (!bld.retcode.equals("0000")&&!bld.retcode.equals("0001")&&!bld.retcode.equals("0002"))
			{
				bld.retbz = 'N';
				
				return false;
			}else{

				bld.retbz = 'Y';
				
				return true;
			}
		}else{

			if (bld.retcode.trim().equals("0000"))
			{
				bld.retbz = 'Y';
				
				return true;
			}
			else
			{
				bld.retbz = 'N';
				
				return false;
			}
		}
		
	}

	

	
	public static class xmlMobile
	{
		private String funCode; //交易功能码
		private String reqDate; //请求日期
		private String reqTime; //请求时间
		private String merId; //商户编号
		private String storeId; //门店编号
		//String storeId = Convert.increaseCharForward("201", '0', 8); //门店编号
		private String posId; //终端号
		//String storeId = "";
		//String posId = "";
		private String calling; //手机号码
		private String retCode; //返回代码
		private String memo; //返回消息
		private String checkCode; //验证码
		private String amt; //金额
		private String orderId; //订单编号
		private String tranSeq; //交易流水号
		private String colDate; //对账日期
		private String sign;
		private String redAmt;
		public String getRedAmt()
		{
			return redAmt;
		}
		public void setRedAmt(String redAmt)
		{
			this.redAmt = redAmt;
		}
		public String getAmt()
		{
			return amt;
		}
		public void setAmt(String amt)
		{
			this.amt = amt;
		}
		public String getCalling()
		{
			return calling;
		}
		public void setCalling(String calling)
		{
			this.calling = calling;
		}
		public String getCheckCode()
		{
			return checkCode;
		}
		public void setCheckCode(String checkCode)
		{
			this.checkCode = checkCode;
		}
		public String getColDate()
		{
			return colDate;
		}
		public void setColDate(String colDate)
		{
			this.colDate = colDate;
		}
		public String getFunCode()
		{
			return funCode;
		}
		public void setFunCode(String funCode)
		{
			this.funCode = funCode;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public String getMerId()
		{
			return merId;
		}
		public void setMerId(String merId)
		{
			this.merId = merId;
		}
		public String getOrderId()
		{
			return orderId;
		}
		public void setOrderId(String orderId)
		{
			this.orderId = orderId;
		}
		public String getPosId()
		{
			return posId;
		}
		public void setPosId(String posId)
		{
			this.posId = posId;
		}
		public String getReqDate()
		{
			return reqDate;
		}
		public void setReqDate(String reqDate)
		{
			this.reqDate = reqDate;
		}
		public String getReqTime()
		{
			return reqTime;
		}
		public void setReqTime(String reqTime)
		{
			this.reqTime = reqTime;
		}
		public String getRetCode()
		{
			return retCode;
		}
		public void setRetCode(String retCode)
		{
			this.retCode = retCode;
		}
		public String getSign()
		{
			return sign;
		}
		public void setSign(String sign)
		{
			this.sign = sign;
		}
		public String getStoreId()
		{
			return storeId;
		}
		public void setStoreId(String storeId)
		{
			this.storeId = storeId;
		}
		public String getTranSeq()
		{
			return tranSeq;
		}
		public void setTranSeq(String tranSeq)
		{
			this.tranSeq = tranSeq;
		}
		public xmlMobile fromXml(String retXml)
		{
			if(retXml == null)
				return null;
			System.out.println(retXml);
			
			try
			{
				XStream stream = new XStream(new DomDriver());
				stream.alias("xmlMobile", xmlMobile.class);

				xmlMobile result = (xmlMobile) stream.fromXML(retXml);
				
				return result;

			} catch (Exception ex)
			{
				ex.printStackTrace();
				new MessageBox(""+ex);
				System.out.println(ex);
				return null;
			}
		}

		private String toXml()
		{
			try
			{
				XStream xstream = new XStream(new XppDriver(
						new XmlFriendlyReplacer("__", "_")));
				xstream.alias("xmlMobile", xmlMobile.class);
				
				String xmlString = xstream.toXML(this);
				xmlString = "<?xml version='1.0' encoding='GBK'?>\n"+xmlString;
				
				System.out.println(xmlString);
				PosLog.getLog(getClass()).info(xmlString);
				return xmlString;
			} catch (Exception ex)
			{
				return null;
			}
			
		}
		
	}

	public boolean checkDate(Text date)
	{
		if (date.getText().trim().length() != 11)
		{
			new MessageBox("请输入11位手机号码！");
			return false;
		}
		if (typeN != PaymentBank.XYKXF && typeN != PaymentBank.XYKCX && typeN != PaymentBank.XYKTH) return true;

		xmlMobile dd = new xmlMobile();
		ManipulateDateTime mdt = new ManipulateDateTime();
		
		dd.setStoreId(Convert.increaseCharForward(GlobalInfo.sysPara.mktcode, '0', 8)) ; //门店编号
		dd.setPosId(Convert.increaseCharForward(GlobalInfo.syjDef.syjh, '0', 8)); //终端号
		dd.setReqDate(mdt.getDateByEmpty()); //请求日期
		dd.setReqTime(mdt.getTimeByEmpty()); //请求时间
		
		//Transition.ItemDetail(dd, dd.ref, new String[][] { new String[] { "jygs", GlobalInfo.sysPara.jygs } });
		dd.setFunCode("SC1010");
		dd.setCalling(date.getText().trim());
		dd.setMerId(merId);
		dd.setAmt("0");
		if(oldseq.length()>0){
			dd.setOrderId(oldseq);
		}else{
			String crc = dd.getReqDate()+ dd.getReqTime()+ dd.getCalling()+ XYKGetCRC();
			orderIdOnly = crc;
			dd.setOrderId(crc);
		}
		
		//String xmlStr = getRequestInfo(dd, "refYZ");
		String xmlStr = dd.toXml();
		//xmlStr = "<?xml version='1.0' encoding='GBK' ?> "+"<xmlMobile>"+xmlStr.substring(5,xmlStr.length()-6)+"</xmlMobile>";

		String result = httpPost(serverUrl, xmlStr);
		if(result==null) {
			return false;
		}
		xmlMobile ddr = dd.fromXml(result);
		if (ddr != null)
		{
			if (!ddr.retCode.equals("0000"))
			{
				new MessageBox("获取验证码交易失败：" + ddr.memo);
				return false;
			}
			/*
			if (!ddr.orderId.equals(crc))
			{
				new MessageBox("订单编号返回数据与请求数据不一致！");
				return false;
			}
			*/
			if (dd.amt != null || !dd.amt.trim().equals(""))
			{
				ManipulatePrecision.doubleConvert(Double.parseDouble(dd.amt.trim()) / 100, 2, 1);
			}

		}
		while (true)
		{
			StringBuffer passwd = new StringBuffer();
			TextBox txt = new TextBox();
			if (!txt.open("请输入验证码", "", "需要先输入验证码以后进行操作", passwd, 0, 0, false, TextBox.AllInput)) { return false; }
			if (passwd.toString().trim().length() < 1)
			{
				new MessageBox("验证码输入为空,请重新输入！");
			}
			else
			{
				pwd = passwd.toString();
				break;
			}
		}

		return true;
	}


	public String httpPost(String str, String postdata)
	{
		URL url;
		try
		{
			url = new URL(str);
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length", String.valueOf(postdata.length()));
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Language", "zh-cn");
			conn.setRequestProperty("HOST", "114.251.148.201:29198");
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			PosLog.getLog(getClass()).info("请求：" + postdata);
			writer.write(postdata);
			writer.flush();
			writer.close();

			InputStreamReader reder = new InputStreamReader(conn.getInputStream(), "GBK");

			BufferedReader breader = new BufferedReader(reder);

			String content = null;
			
			String output = "";
			while ((content = breader.readLine()) != null)
			{
				output = output + content.trim();

			}
			PosLog.getLog(getClass()).info("应答:" + output);
			return output;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			
			System.out.println(e);
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			new MessageBox("连接移动服务器URL异常！");
			System.out.println(e);
			return null;
		}
	}

	private boolean getServerInfo()
	{
		BufferedReader br = null;

		// 读取YDSJDZQ.ini文件
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "\\YDSJDZQ.ini");
		if (br == null)
		{
			new MessageBox("没有找到YDSJDZQ.ini文件");
			return false;
		}
		String sinfo = null;
		try
		{
			while ((sinfo = br.readLine()) != null)
			{
				String s[] = sinfo.split("=");
				if (s.length > 1)
				{
					if (s[0].trim().equals("ServerUrl"))
					{
						serverUrl = s[1].trim();
					}
					if (s[0].trim().equals("MerId"))
					{
						merId = s[1].trim();
					}
				}
			}
			if (serverUrl == null || merId == null || serverUrl.equals("") || merId.equals(""))
			{
				new MessageBox("YDSJDZQ.ini中的内容未填写完整");
				return false;
			}
		}
		catch (IOException e)
		{
			PosLog.getLog("getServerInfo").error(e);
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if (br != null) br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.out.println(e);
			}
		}
		return true;

	}

	public boolean WriteRequestLog(int type, double money, String oldseqno, String oldauthno, String olddate)
	{
		try
		{
			bld = new BankLogDef();

			Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");

			if (obj == null)
			{
				bld.rowcode = 1;
			}
			else
			{
				bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
			}

			bld.rqsj = ManipulateDateTime.getCurrentDateTime();
			bld.syjh = GlobalInfo.syjDef.syjh;
			bld.fphm = GlobalInfo.syjStatus.fphm;
			bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh : "");
			bld.type = String.valueOf(type);
			bld.je = money;
			//bld.oldrq = olddate;
			bld.typename = getChangeType(getFuncItem(), bld.type);
			bld.classname = (bankcfgname != null ? bankcfgname : this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1));

			if ((oldseqno != null) && !oldseqno.trim().equals(""))
			{
				bld.oldtrace = 0;
			}
			else
			{
				bld.oldtrace = 0;
			}

			bld.cardno = "";
			bld.trace = 0;
			bld.authno = "";
			bld.bankinfo = "";
			bld.crc = "";
			bld.retcode = "";
			bld.retmsg = "";
			bld.retbz = 'N';
			bld.net_bz = 'N';
			bld.allotje = 0;
			bld.memo = getMemo(type, money, oldseqno, oldauthno, olddate);
			bld.memo1 = "";
			bld.memo2 = "";
			bld.tempstr = "";
			bld.tempstr1 = "";

			//
			if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
			bld = null;

			return false;
		}

		return true;
	}

	public void XYKPrintDoc(int type, xmlMobile ddr)
	{
		if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || type == PaymentBank.XYKTH)
		{
			if(GlobalInfo.sysPara.bankprint<1) return;
			ProgressBox pb = null;
			
			try
			{
				if(type == PaymentBank.XYKXF){
					
				}
				pb = new ProgressBox();
				pb.setText("正在打印银联签购单,请等待...");
				String line = "";
				line = line + Convert.appendStringSize("", "移动电子券" , 0, 38, 38)+"\n";
				line = line + Convert.appendStringSize("", "商户编号:"+ddr.merId , 0, 38, 38)+"\n";
				line = line + Convert.appendStringSize("", "门店编号:"+ddr.storeId , 0, 38, 38)+"\n";
				line = line + Convert.appendStringSize("", "终 端 号:"+ddr.posId , 0, 38, 38)+"\n";
				line = line + Convert.appendStringSize("", "收银员号:"+GlobalInfo.syjStatus.syyh , 0, 38, 38)+"\n";
				if(type == PaymentBank.XYKXF){
					line = line + Convert.appendStringSize("", "交易类型:电子券支付"+ddr.funCode , 0, 38, 38)+"\n";
				}
				if(type == PaymentBank.XYKCX){
					line = line + Convert.appendStringSize("", "交易类型:电子券撤销"+ddr.funCode , 0, 38, 38)+"\n";
					line = line + Convert.appendStringSize("", "原交易流水号:"+ oldseq, 0, 38, 38)+"\n";
					
				}
				if(type == PaymentBank.XYKTH){
					line = line + Convert.appendStringSize("", "交易类型:电子券退货"+ddr.funCode , 0, 38, 38)+"\n";
					line = line + Convert.appendStringSize("", "原交易流水号:"+ oldseq , 0, 38, 38)+"\n";
					
				}
				line = line + Convert.appendStringSize("", "手 机 号:"+ddr.calling , 0, 38, 38)+"\n";
				//line = line + Convert.appendStringSize("", "验 证 码:"+pwd , 0, 38, 38)+"\n";
				if(ddr.tranSeq!=null && !ddr.tranSeq.equals("")){
					line = line + Convert.appendStringSize("", "订单编号:"+ddr.orderId, 0, 38, 38)+"\n";
				}
				line = line + Convert.appendStringSize("", "交易流水号:"+ddr.tranSeq , 0, 38, 38)+"\n";
				line = line + Convert.appendStringSize("", "消费金额:"+ManipulatePrecision.doubleConvert(Double.parseDouble(ddr.amt.trim()) / 100, 2, 1) , 0, 38, 38)+"\n";
				line = line + Convert.appendStringSize("", "对账日期:"+ddr.colDate , 0, 38, 38)+"\n";
				
				
				for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
				{
					
					Printer.getDefault().startPrint_Normal();
					
					Printer.getDefault().printLine_Normal(line);
					// 切纸
					Printer.getDefault().cutPaper_Normal();
					System.out.print(line);
				}
			}
			catch (Exception ex)
			{
				new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
				ex.printStackTrace();
			}
			finally
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}
			}
		}
	}
	
	public boolean checkSeqno(Text seq)
	{
		if(seq.getText().trim().length()>0){
			oldseq = seq.getText().trim();
		}
		return true;
	}
}
