package custom.localize.Hmsl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.GlobalParaDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SyjMainDef;

public class Hmsl_CyDateService
{
	
	String serverIp; // 通讯IP
	int serverPort; // 通讯端口
	String encoding = "GBK"; //xml字符集类型

	//交易类型
//	  appSys_Login = '0000'; //登陆
//	  appSys_Exit = '0001'; //退出
//	  appPos_CashCardBalance = '0105'; //查询储值卡余额
//	  appPos_PrepareTransCashCard = '0106'; //储值卡消费
//	  appPos_GetCashCarXFJL = '0111';       //获取储值卡交易信息以供冲正
//	  appPos_CancelTransCashCard = '0112'; //储值卡消费冲正
	
	String session_id; // 会话ID，每次交易获取一个 session_id
	
	private static Hmsl_CyDateService  dataService = null;
	
	public static Hmsl_CyDateService getDefault()
	{
		if (dataService == null)
		{
			dataService = new Hmsl_CyDateService();
		}
		
		return dataService;
	}
	
	public Hmsl_CyDateService()
	{
		if (GlobalInfo.sysPara.cyCrmUrl == null || GlobalInfo.sysPara.cyCrmUrl.trim().equals("") || GlobalInfo.sysPara.cyCrmUrl.trim().indexOf(":") == -1)
		{
			new MessageBox("请设置正确的用于登录Crm的连接地址");
			return;
		}

		String[] urlAry = GlobalInfo.sysPara.cyCrmUrl.split(":");
		if (urlAry != null && urlAry.length > 0)
			serverIp = urlAry[0];
		if (urlAry != null && urlAry.length > 1)
			serverPort = Convert.toInt(urlAry[1]);
	}
	
	// 登陆,并记录 session_id，每次消费，查询使用一个 session_id
	public boolean hasLogin()
	{		
		try
		{
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("bfcrm_req");
			root.addAttribute("app", "0000");
			Element store = root.addElement("storeID");
			store.addText(GlobalInfo.sysPara.mktcode);
			Element mach = root.addElement("machine");
			mach.addText(GlobalInfo.syjDef.syjh);
			
			Document rsDoc = xmlSocketService(null, doc, true);			
			if (rsDoc == null) return false;
			
			Element rsRoot = rsDoc.getRootElement();
			
			//如果返回的根节点不为  bfcrm_resp，则返回结果有问题
			if (!rsRoot.getName().equals("bfcrm_resp"))
				return false;
			
			//得到根节点的属性，并判断属性值  success="Y"，则返回正确
			Attribute att = rsRoot.attribute("success");
			if ( !(att != null && att.getValue().trim().equals("Y")) )
			{
				Element failType = rsRoot.element("fail_type");
				Element message = rsRoot.element("message");
				new MessageBox("登录卡服务器失败!!! \n\n错误类型:" + failType.getTextTrim() + " " + message.getTextTrim());			
				return false;
			}
			
			Element rsSession = (Element) rsRoot.element("session_id");
			session_id = rsSession.getTextTrim();			
//			System.out.println("Id:" + session_id);
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	////退出
	public boolean hasLogout()
	{
		
		try
		{
			// session_id 为空，说明没有得到　session_id
			if (session_id == null)
				return true;
			
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("bfcrm_req");
			root.addAttribute("app", "0001");
			root.addAttribute("session_id", session_id);
			
			Document rsDoc = xmlSocketService(null, doc, false);
			
			session_id = null;//退出后将 session_id 置空
			
			if (rsDoc == null) return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	// 储值卡余额查询
	public boolean queryMzkInfo(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		try
		{
			//1.查询前先登录，获得 session_id 
			if (!hasLogin())
			{
//				new MessageBox("登录卡服务器失败!!!");
				return false;
			}
			
			//2.查询
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("bfcrm_req");
			root.addAttribute("app", "0105");
			root.addAttribute("session_id", session_id);
			
			// 卡号和磁道号区分，长度大于 8位的
			String track2 = "";
			String code = "";
			if(mzkreq.track2.length() > 8)
			{
				track2 = mzkreq.track2;
			}
			else
			{
				code = mzkreq.track2;
			}		
			Element track = root.addElement("track_data");
			track.addText(track2);
			Element member = root.addElement("member_code");
			member.addText(code);
			
			Document rsDoc = xmlSocketService(null, doc, true);			
			if (rsDoc == null) return false;
			
			Element rsRoot = rsDoc.getRootElement();
			
			//如果返回的根节点不为  bfcrm_resp，则返回结果有问题
			if (!rsRoot.getName().trim().equals("bfcrm_resp"))
				return false;
			
			//得到根节点的属性，并判断属性值  success="Y"，则返回正确
			Attribute att = rsRoot.attribute("success");
			if ( !(att != null && att.getValue().trim().equals("Y")) )
			{
				Element failType = rsRoot.element("fail_type");
				Element message = rsRoot.element("message");
				new MessageBox("储值卡查询失败!!! \n\n错误类型:" + failType.getTextTrim() + " " + message.getTextTrim());			

				return false;
			}
			
			Element rsMember = (Element) rsRoot.element("member_code");
			mzkret.cardno = rsMember.getTextTrim();
			Element rsDate = (Element) rsRoot.element("date_valid");
			mzkret.str1 = rsDate.getTextTrim();
			Element rsBalance = rsRoot.element("balance");
			mzkret.ye = Convert.toDouble(rsBalance.getTextTrim());
			mzkreq.num1 = Convert.toDouble(rsBalance.getTextTrim()); // 冲正用，保存卡余额
			Element rsBottom = rsRoot.element("bottom");
			mzkret.value1 = Convert.toDouble(rsBottom.getTextTrim());
			
			return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			//3.退出
			if (hasLogout());
		}
		
		return false;
	}
	
	//储值卡消费
	public boolean mzkSale(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		try
		{
			//1.查询前先登录，获得 session_id 
			if (!hasLogin())
			{
//				new MessageBox("登录卡服务器失败!!!");
				return false;
			}
			
			//2.储值卡消费
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("bfcrm_req");
			root.addAttribute("app", "0106");
			root.addAttribute("session_id", session_id);
			Element billid = root.addElement("billid");
			billid.addText( String.valueOf(mzkreq.seqno) );
			
			// 卡号和磁道号区分，长度大于 8位的
			String track2 = "";
			String code = "";
			if(mzkreq.track2.length() > 8)
			{
				track2 = mzkreq.track2;
			}
			else
			{
				code = mzkreq.track2;
			}		
			Element track = root.addElement("track_data");
			track.addText(track2);
			Element member = root.addElement("member_code");
			member.addText(code);
			Element number = root.addElement("number_xfje");
			number.addText(String.valueOf(mzkreq.je));
			
			Document rsDoc = xmlSocketService(null, doc, true);			
			if (rsDoc == null) return false;
			
			Element rsRoot = rsDoc.getRootElement();
			
			//如果返回的根节点不为  bfcrm_resp，则返回结果有问题
			if (!rsRoot.getName().trim().equals("bfcrm_resp"))
				return false;
			
			//得到根节点的属性，并判断属性值  success="Y"，则返回正确
			Attribute att = rsRoot.attribute("success");
			if ( !(att != null && att.getValue().trim().equals("Y")) )
			{				
				Element failType = rsRoot.element("fail_type");
				Element message = rsRoot.element("message");
				new MessageBox("储值卡交易失败!!! \n\n错误类型:" + failType.getTextTrim() + " " + message.getTextTrim());			
				
				// 交易失败后，记录交易编号，冲正需要。
				Element rsJybh = (Element) rsRoot.element("jybh");
				mzkret.str2 = rsJybh.getTextTrim(); //  储值卡交易时产生的交易编号
				mzkreq.str2 = rsJybh.getTextTrim(); //  冲正查询时，传入参数
				
				Element rsMember = (Element) rsRoot.element("member_code");
				mzkret.cardno = rsMember.getTextTrim();
				mzkreq.str3 = rsMember.getTextTrim(); // 冲正用，保存卡号
				return false;
			}
			
			Element rsMember = (Element) rsRoot.element("member_code");
			mzkret.cardno = rsMember.getTextTrim();
			mzkreq.str3 = rsMember.getTextTrim(); // 冲正用，保存卡号
			Element rsDate = (Element) rsRoot.element("date_valid");
			mzkret.str1 = rsDate.getTextTrim();
			Element rsBalance = (Element) rsRoot.element("balance");		
			mzkret.ye = Convert.toDouble(rsBalance.getTextTrim());
//			mzkreq.num1 = Convert.toDouble(rsBalance.getTextTrim()); // 冲正用，保存卡余额
			Element rsBottom = (Element) rsRoot.element("bottom");
			mzkret.value1 = Convert.toDouble(rsBottom.getTextTrim());
			Element rsJybh = (Element) rsRoot.element("jybh");
			mzkret.str2 = rsJybh.getTextTrim(); //  储值卡交易时产生的交易编号
			mzkreq.str2 = rsJybh.getTextTrim(); //  冲正查询时，传入参数
			return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			//3.退出
			if (hasLogout());
		}
		return false;
	}
	
	// 冲正逻辑处理
	public boolean flushes(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		//beforeSaleBalance 记录消费前余额，查询时会将保存消费前余额字段重新赋值
		double bSBalance =  mzkreq.num1; 
		
		if ( !Hmsl_CyDateService.getDefault().queryMzkInfo(mzkreq, mzkret))
		{
			new MessageBox("查询卡余额失败，不能冲正!!!");
			return false;
		}
		// 消费失败后，卡余额没有减少，则不需要冲正
		if (bSBalance == mzkret.ye )
		{
			return true;
		}
		else if (bSBalance < mzkret.ye )
		{
			new MessageBox("长益储值卡查询后余额比消费前还多，卡余额异常。请联系信息部处理!!!");
			return false;
		}
		
		if (mzkreq.str2 == null || mzkreq.str2.equals(""))
		{
			String log = "消费交易失败，卡余额已扣除，\n但无交易编号,无法冲正。请联系信息部!!!\n\n"
					+ "卡号:" + mzkreq.track2 + " 消费金额:" + mzkreq.je;
			new MessageBox(log);
			
			AccessDayDB.getDefault().writeWorkLog("长益储值卡冲正失败。错误原因：无交易编号。 门店号" + mzkreq.mktcode + " 收银机号:" + mzkreq.syjh + " 小票号:" + mzkreq.fphm
			+ " 存值卡流水号:" + mzkreq.seqno + " 卡号:" + mzkreq.track2 + "消费金额:" + mzkreq.je);
			
			return false;
		}
		mzkreq.num1 = bSBalance;
		
		if (!getCashCarXFJL(mzkreq, mzkret))
			return false;
				
		if (CancelTransCashCard(mzkreq, mzkret))
		{
			return true;
		}
		
		return false;
	}
	
	//获取储值卡交易信息,并效验，以供冲正
	public boolean getCashCarXFJL(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		String log = null;
		try
		{	
			//1.查询前先登录，获得 session_id 
			if (!hasLogin())
			{
//				new MessageBox("登录卡服务器失败!!!");
				return false;
			}
			
			//2.查询冲正信息
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("bfcrm_req");
			root.addAttribute("app", "0111");
			root.addAttribute("session_id", session_id);
			Element jybh = root.addElement("id");
			jybh.addText( mzkreq.str2);
			
			// 卡号和磁道号区分，长度大于 8位的
			String track2 = "";
			String code = "";
			if(mzkreq.track2.length() > 8)
			{
				track2 = mzkreq.track2;
			}
			else
			{
				code = mzkreq.track2;
			}		
			Element track = root.addElement("track_data");
			track.addText(track2);
			Element member = root.addElement("member_code");
			member.addText(code);
			
			Document rsDoc = xmlSocketService(null, doc, true);			
			if (rsDoc == null) return false;
			
			Element rsRoot = rsDoc.getRootElement();
			
			//如果返回的根节点不为  bfcrm_resp，则返回结果有问题
			if (!rsRoot.getName().trim().equals("bfcrm_resp"))
				return false;
			
			//得到根节点的属性，并判断属性值  success="Y"，则返回正确
			Attribute att = rsRoot.attribute("success");
			if ( !(att != null && att.getValue().trim().equals("Y")) )
				return false;
			
			Element rsMember = (Element) rsRoot.element("member_code");
			if (!mzkreq.str3.equals(rsMember.getTextTrim()) )
			{
				log = "冲正卡号不正确，原交易卡号:" + mzkreq.str3 + ",冲正返回卡号:" + rsMember.getTextTrim();
				new MessageBox(log);
				return false;
			}
			
			Element rsSktjlbh = (Element) rsRoot.element("sktjlbh");
			if ( mzkreq.seqno != Convert.toDouble(rsSktjlbh.getTextTrim()) )
			{
				log = "冲正小票流水号不正确，原交小票流水号:" + mzkreq.seqno + ",冲正返回小票流水号:" + rsSktjlbh.getTextTrim();
				new MessageBox(log);
				return false;
			}
			
			Element rsXfje = (Element) rsRoot.element("xfje");
			if (!(mzkreq.je == Convert.toDouble(rsXfje.getTextTrim())) )
			{
				log = "冲正小票交易金额不正确，原小票交易金额:" + mzkreq.je + ",冲正查询返回交易金额:" + rsXfje.getTextTrim() ;
				new MessageBox(log);
				return false;
			}
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			//3.退出
			if (hasLogout());
			
			if (log != null)
			{
				AccessDayDB.getDefault().writeWorkLog("长益储值卡冲正失败。错误原因：冲正信息有问题。 " 
			     + log + " 门店号" + mzkreq.mktcode + " 收银机号:" + mzkreq.syjh 
			     + " 小票号:" + mzkreq.fphm+ " 存值卡流水号:" + mzkreq.seqno + " 卡号:" + mzkreq.str3 + "消费金额:" + mzkreq.je);				
			}
		}

		return false;
	}
	
	//储值卡消费冲正
	public boolean CancelTransCashCard(MzkRequestDef mzkreq, MzkResultDef mzkret)
	{
		try
		{	
			//2.查询
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("bfcrm_req");
			root.addAttribute("app", "0112");
			root.addAttribute("session_id", session_id);
			Element jybh = root.addElement("jybh");
			jybh.addText(mzkreq.str2);
			
			// 卡号和磁道号区分，长度大于 8位的
			String track2 = "";
			String code = "";
			if(mzkreq.track2.length() > 8)
			{
				track2 = mzkreq.track2;
			}
			else
			{
				code = mzkreq.track2;
			}		
			Element track = root.addElement("track_data");
			track.addText(track2);
			Element member = root.addElement("member_code");
			member.addText(code);
			Element number = root.addElement("number_xfje");
			track.addText(String.valueOf(mzkreq.je) );
			
			Document rsDoc = xmlSocketService(null, doc, true);			
			if (rsDoc == null) return false;
			
			Element rsRoot = rsDoc.getRootElement();
			
			//如果返回的根节点不为  bfcrm_resp，则返回结果有问题
			if (!rsRoot.getName().trim().equals("bfcrm_resp"))
				return false;
			
			//得到根节点的属性，并判断属性值  success="Y"，则返回正确
			Attribute att = rsRoot.attribute("success");
			if ( !(att != null && att.getValue().trim().equals("Y")) )
				return false;
			
			Element rsBalance = (Element) rsRoot.element("balance");
			double rsYe = Convert.toDouble(rsBalance.getTextTrim());
			String info = "";
			if (rsYe != mzkreq.num1)
			{
				new MessageBox("冲正出现异常。长益储值卡显示冲正成功，但卡余额没有冲回去。\n\n请联系信息部处理!!!");
				info = "长益储值卡冲正失败。异常错误。";
				AccessDayDB.getDefault().writeWorkLog(info + " 门店号" + mzkreq.mktcode + " 收银机号:" + mzkreq.syjh 
				                 				     + " 小票号:" + mzkreq.fphm+ " 存值卡流水号:" + mzkreq.seqno + " 交易编号" + mzkreq.str2 + " 卡号:" + mzkreq.str3 + " 消费金额:" + mzkreq.je + "冲正后卡余额:" + rsYe);				

				return false;
			}
			
			Element rsJybh = (Element) rsRoot.element("jybh");
			mzkret.str3 = rsJybh.getTextTrim(); // 储值卡冲正时产生的交易编号
			
			info = "长益储值卡冲正成功。";
			AccessDayDB.getDefault().writeWorkLog(info + " 门店号" + mzkreq.mktcode + " 收银机号:" + mzkreq.syjh 
				     + " 小票号:" + mzkreq.fphm+ " 存值卡流水号:" + mzkreq.seqno + " 交易编号" + mzkreq.str2 + " 卡号:" + mzkreq.str3 + " 消费金额:" + mzkreq.je + "冲正后卡余额:" + rsYe);				

			
			return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}
	
	
	private Document xmlSocketService(Socket outSocket, Document doc, boolean isRet)
	{
		Socket socket = null;
		Document rsDoc = null;
		try
		{
			if (outSocket == null)
				socket = new Socket(serverIp, serverPort);
			else
				socket = outSocket;

			socket.setSoTimeout(ConfigClass.ReceiveTimeout);
			
			//设置XML文件的字符集类型
			OutputFormat format = new OutputFormat();
			format.setEncoding(encoding);
			
			// 记录发送的信息的		
			StringWriter strWriter = new StringWriter();
			XMLWriter logRq = new XMLWriter(strWriter, format);
			logRq.write(doc);
			logRq.flush();
			logRq.close();			
			String str = strWriter.toString();
			strWriter.flush();
			strWriter.close();

			
			StringBuilder sb = new StringBuilder();
			sb.append("BFCRMXML08");
			sb.append(String.format("%08d", str.length()));
			sb.append(str);
			sb.append("12345678");
			String xmlReq = sb.toString();
			System.out.println("Request:\n" + xmlReq);
			
			socket.getOutputStream().write(xmlReq.getBytes("GBK"));
			socket.getOutputStream().flush();
								
			if (!isRet) return null;
			
			long interval = 0; // 时间间隔
			int tmpData = 0; // 读取的数据
			int i = 0; // 缓存记数器

			byte[] readBuffer = new byte[1024];
			long starttime = System.currentTimeMillis();

			do
			{
				tmpData = socket.getInputStream().read();

				if (tmpData == -1)
					break;

				readBuffer[i++] = (byte) tmpData;
				interval = System.currentTimeMillis() - starttime;

				if (interval > ConfigClass.ReceiveTimeout)
				{
					new MessageBox("读取Crm返回数据超时");
					return null;
				}

				if (i > 1024)
				{
					new MessageBox("缓冲区溢出");
					return null;
				}
			} while (tmpData != -1);
			
			// 记录返回的信息
			String result = new String(readBuffer, 0, i, "GBK");
			System.out.println("Result:" + result);
			
			//截取返回信息中的xml内容， xml 内容以 '<'开始，'>'结尾
			int start = result.indexOf('<');
			int end = result.lastIndexOf('>') + 1;
			String xml = result.substring(start, end );
			System.out.println("strat:" + start + " end:" + end + " xml:\n" + xml);
			
			// 将 XML内容转换成  org.dom4j.Document 对象内容，后续操作
			StringReader strRead = new StringReader(xml);			
			SAXReader reader = new SAXReader();
			rsDoc = reader.read(strRead);
			
			return rsDoc;
		}
		catch(SocketTimeoutException ste)
		{
			ste.printStackTrace();
			
			new MessageBox("Pos 与 长益储值卡服务器 通讯超时...");
		}
		catch(DocumentException de)
		{
			de.printStackTrace();
			new MessageBox("读取的返回信息不符合通讯规范 ( xml文件格式)...");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("Pos 与 长益储值卡服务器 通讯出现未知异常：" + e.getMessage());
		}
		finally
		{
			try
			{
				if (socket != null && outSocket == null)
					socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				socket = null;
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) throws Exception
	{
		// 132.147.43.5 
//		Socket socket  = new Socket("132.147.43.202", 6000);		
		
		getDefault().serverIp ="132.147.43.202";
		getDefault().serverPort =6000;		
		getDefault().encoding ="GBK";		
		ConfigClass.ReceiveTimeout = 30000;		
		GlobalInfo.sysPara = new GlobalParaDef();
		GlobalInfo.syjDef = new SyjMainDef();
		GlobalInfo.sysPara.mktcode ="201";
		GlobalInfo.syjDef.syjh = "7777";
		
		getDefault().hasLogin();
		
	}
}
