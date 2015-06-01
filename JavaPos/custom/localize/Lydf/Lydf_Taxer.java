package custom.localize.Lydf;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Lydf_Taxer
{
	public static Lydf_Taxer taxer = new Lydf_Taxer();

	public static Lydf_Taxer getDefault()
	{
		return taxer;
	}

	// 开票 cmd =1
	public String createMakeInvoiceBody(String cmdcode, SaleHeadDef saleHead, Vector saleGoods, Vector salePay)
	{
		String cmd = "<Cmd>" + cmdcode + "</Cmd>";
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("GBK");

		try
		{
			Element InvElement = document.addElement("Inv");
			Element HElement = InvElement.addElement("H");

			//M:改动1
//			if (GlobalInfo.sysPara.isMultiMkt == 'N')
//				HElement.addElement("BH").setText(saleHead.syjh);
//			else
//				HElement.addElement("BH").setText("");
			
			HElement.addElement("BH").setText("");
			HElement.addElement("LS").setText(String.valueOf(saleHead.fphm));
			HElement.addElement("MC").setText("个人");
			HElement.addElement("GH").setText(""); //M:改动2
//			HElement.addElement("GH");   //M:原来
			HElement.addElement("SN").setText(saleHead.syyh);
			HElement.addElement("XM").setText(GlobalInfo.sysPara.taxcompanyname);
			HElement.addElement("XH").setText(GlobalInfo.sysPara.taxcompanyid);

			Element LXElement = HElement.addElement("LX");
			if (SellType.ISCARD(saleHead.djlb))
				LXElement.setText("1");
			else
				LXElement.setText("0");

			// 隔月红冲税票
			if (SellType.ISHC(saleHead.djlb) && saleHead.salefphm.length() > 20)
			{
				Element CXXXElement = HElement.addElement("CXXX");
				Element CXLXElement = CXXXElement.addElement("CXLX");
				Element YS_PHElement = CXXXElement.addElement("YS_PH");
				Element YS_LSElement = CXXXElement.addElement("YS_LS");

				CXLXElement.setText("1");
				YS_PHElement.setText(saleHead.salefphm);
				YS_LSElement.setText(saleHead.yfphm);
			}

			Element CElement = InvElement.addElement("C");
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				Element CMXElement = CElement.addElement("MX");

				Element MCElement = CMXElement.addElement("MC");
				MCElement.addCDATA(sgd.name);

				//Element CGElement = 
				CMXElement.addElement("GG").setText("");
				// CGElement.addCDATA("");

				Element DWElement = CMXElement.addElement("DW");
				DWElement.addCDATA(sgd.unit);

				CMXElement.addElement("SL").setText(String.valueOf(sgd.sl));
				CMXElement.addElement("DJ").setText(String.valueOf(sgd.jg));
				CMXElement.addElement("JE").setText(String.valueOf(sgd.hjje));
				CMXElement.addElement("SLV").setText("");  //M:改动
				CMXElement.addElement("SE").setText("");   //M:改动
//				CMXElement.addElement("SLV");   //M:原来
//				CMXElement.addElement("SE");
				CMXElement.addElement("TM").setText(sgd.barcode);
			}

			Element JSXXElement = InvElement.addElement("JSXX");
			Element XJElement = JSXXElement.addElement("XJ");
			Element GWKElement = JSXXElement.addElement("GWK");
			Element QUANElement = JSXXElement.addElement("QUAN");

			double zl = Lydf_Util.getChangeMone(salePay);

			for (int j = 0; j < salePay.size(); j++)
			{
				SalePayDef pay = (SalePayDef) salePay.get(j);
				// 有扣回，则不发送
				if (pay.flag == '3' || pay.flag == '2')
					continue;

				PayModeDef paymode = DataService.getDefault().searchPayMode(pay.paycode);

				String limitpay = "," + GlobalInfo.sysPara.taxlimitpay + ",";
				if (limitpay.indexOf("," + pay.paycode + ",") > -1)
					continue;

				// 1-人民币类,3-信用卡类,4-面值卡类,5-礼券类,6-赊销类,7-其它
				if (paymode.type == '1' || paymode.type == '3' || paymode.type == '6' || pay.paycode.equals("0202"))
				{
					if (XJElement == null)
						XJElement = JSXXElement.addElement("XJ");
					Element XJMXElement = XJElement.addElement("MX");

					XJMXElement.addElement("LX").setText("0");
					// 从现金中扣除找零
					if (paymode.type == '1' && zl > 0)
					{
						XJMXElement.addElement("JE").setText(String.valueOf(ManipulatePrecision.doubleConvert(pay.je - zl, 2, 1)));
						zl = 0.0;
					}
					else
						XJMXElement.addElement("JE").setText(String.valueOf(pay.je));

					XJMXElement.addElement("BZ");

				}
				else if (paymode.type == '4' || paymode.type == '2')
				{
					if (GWKElement == null)
						GWKElement = JSXXElement.addElement("GWK");
					Element GWKMXElement = GWKElement.addElement("MX");
					if (pay.equals("0201"))
						GWKMXElement.addElement("KLX").setText("1");
					else
						GWKMXElement.addElement("KLX").setText("0");

					if (pay.equals("0402"))
						GWKMXElement.addElement("KSX").setText("1");
					else
						GWKMXElement.addElement("KSX").setText("0");
					GWKMXElement.addElement("KH").setText(pay.payno);
					GWKMXElement.addElement("KJE").setText(String.valueOf(pay.je));
					GWKMXElement.addElement("BZ");

				}
				else if (paymode.type == '5')
				{
					if (QUANElement == null)
						QUANElement = JSXXElement.addElement("QUAN");
					Element QUANMXElement = QUANElement.addElement("MX");
					QUANMXElement.addElement("LX").setText("0");
					QUANMXElement.addElement("JE").setText(String.valueOf(pay.je));
					QUANMXElement.addElement("BZ").setText(pay.payname);
				}
			}

			String xml = cmd + document.asXML().substring(document.asXML().indexOf("?>") + 3);
			System.out.println("开票:" + xml);
			return xml;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	// 查询款机最后一笔 cmd =2
	public String createQueryLastInvoiceBody(String cmdcode, String syjh)
	{
		String cmd = "<Cmd>" + cmdcode + "</Cmd>";
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element InvElement = document.addElement("Inv");
			Element BHElement = InvElement.addElement("BH");
			BHElement.setText(syjh);

			String xml = cmd + document.asXML().substring(document.asXML().indexOf("?>") + 3);
			System.out.println("查询最近一笔:" + xml);
			return xml;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	// 作废 cmd = 3
	public String createInvalidInvoiceBody(String cmdcode, String invno)
	{
		String cmd = "<Cmd>" + cmdcode + "</Cmd>";
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element PHElement = document.addElement("PH");
			PHElement.setText(invno);

			String xml = cmd + document.asXML().substring(document.asXML().indexOf("?>") + 3);
			System.out.println("作废: " + xml);
			return xml;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	// 查询发票明细 cmd = 4
	public String createQueryInvoiceBody(String cmdcode, String invno)
	{
		String cmd = "<Cmd>" + cmdcode + "</Cmd>";
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element PHElement = document.addElement("PH");
			PHElement.setText(invno);

			String xml = cmd + document.asXML().substring(document.asXML().indexOf("?>") + 3);
			System.out.println("查询明细:" + xml);
			return xml;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String call(String inStr)
	{
		PrintWriter pw = null;
		BufferedReader br = null;
		String line = null;
		try
		{
			// 先删除上次交易数据文件
			if (PathFile.fileExist("c:\\javapos\\taxRequest.txt"))
			{
				PathFile.deletePath("c:\\javapos\\taxRequest.txt");

				if (PathFile.fileExist("c:\\javapos\\taxRequest.txt"))
				{
					new MessageBox("交易请求文件taxRequest.txt无法删除");
					return null;
				}
			}

			if (PathFile.fileExist("c:\\javapos\\taxResponse.txt"))
			{
				PathFile.deletePath("c:\\javapos\\taxResponse.txt");

				if (PathFile.fileExist("c:\\javapos\\taxResponse.txt"))
				{
					new MessageBox("交易请求文件taxResponse.txt无法删除");
					return null;
				}
			}

			try
			{
				pw = CommonMethod.writeFile("c:\\javapos\\taxRequest.txt");
				if (pw != null)
				{
					pw.println(inStr);
					pw.flush();
				}
			}
			finally
			{
				if (pw != null)
					pw.close();

				pw = null;
			}

			if (PathFile.fileExist("c:\\javapos\\postaxer.exe"))
			{
				CommonMethod.waitForExec("c:\\javapos\\postaxer.exe TAX");
			}
			else
			{
				new MessageBox("找不到税控模块 postaxer.exe");
				return null;
			}

			if (!PathFile.fileExist("c:\\javapos\\taxResponse.txt") || ((br = CommonMethod.readFileGBK("c:\\javapos\\taxResponse.txt")) == null))
			{
				new MessageBox("读取税控应答数据失败!");
				return null;
			}

			try
			{
				line = br.readLine();

				if (line == null || line.length() <= 0)
					return null;

				System.out.println("返回:" + line);

				String[] retStr = line.split(",");

				if (retStr == null || retStr.length < 1)
				{
					new MessageBox("获取税控数据失败");
					return null;
				}

				if (retStr[0].equals("0"))
				{
					if (retStr.length > 1)
						return retStr[1];
				}
				{
					new MessageBox("税控开票失败[" + retStr[0] + "]");
				}

				return null;
			}
			finally
			{
				if (br != null)
					br.close();
				br = null;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public boolean execute(int cmdcode, SaleHeadDef saleHead, Vector saleGoods, Vector salePay, Lydf_TaxInfo taxinfo)
	{
		String request = null;
		String response = null;

		try
		{
			if (cmdcode == 1)
			{
				request = createMakeInvoiceBody(String.valueOf(cmdcode), saleHead, saleGoods, salePay);
			}
			else if (cmdcode == 2)
			{
				request = createQueryLastInvoiceBody(String.valueOf(cmdcode), saleHead.syjh);
			}
			else if (cmdcode == 3)
			{
				request = createInvalidInvoiceBody(String.valueOf(cmdcode), saleHead.salefphm);
			}
			else if (cmdcode == 4)
			{
				StringBuffer buffer = new StringBuffer();
				if (!new TextBox().open("请输入所要查询的电子票号", "发票查询", "注意:电子票号长度约30位", buffer, 0, 0, false))
					return false;

				request = createQueryInvoiceBody(String.valueOf(cmdcode), buffer.toString());
			}
			else
			{
				new MessageBox("不被支持的业务类型");
				return false;
			}

			response = call(request);

			if (response == null)
				return false;

			if (parseResultXml(response, taxinfo))
				return true;

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean parseResultXml(String xml, Lydf_TaxInfo taxinfo)
	{
		String retXml = null;
		String memoXml = null;
		Document retDoc = null;
		Document bodyDoc = null;
		Document memoDoc = null;

		String retStartFlag = "<Ret>";
		String retEndFlag = "</Ret>";

		try
		{
			taxinfo.Ret = "-1";

			if (xml == null || xml.equals(""))
				return false;

			if (!xml.startsWith(retStartFlag) && xml.indexOf(retEndFlag) == -1)
				return false;

			retXml = xml.substring(0, xml.indexOf(retEndFlag) + retEndFlag.length());

			retDoc = DocumentHelper.parseText(retXml);
			Element retRoot = retDoc.getRootElement();
			taxinfo.Ret = retRoot.getTextTrim();

			if (!taxinfo.Ret.equals("0"))
				return false;

			xml = xml.substring(retXml.length());
			if (xml.equals(""))
				return true;

			if (!xml.startsWith("<Inv>"))
				return false;

			if (xml.indexOf("<LSH>") > -1)
			{
				memoXml = xml.substring(xml.indexOf("<LSH>"));
				xml = xml.substring(0, xml.indexOf("<LSH>"));
			}

			if (memoXml != null)
			{
				memoDoc = DocumentHelper.parseText(memoXml);
				Element memoRoot = memoDoc.getRootElement();
				taxinfo.LSH = memoRoot.getTextTrim();
			}

			bodyDoc = DocumentHelper.parseText(xml);
			Element root = bodyDoc.getRootElement();
			Iterator iter = root.elementIterator();

			while (iter.hasNext())
			{
				Element e = (Element) iter.next();
				if (e.getName().equals("RQ"))
					taxinfo.RQ = e.getTextTrim();
				else if (e.getName().equals("JE"))
					taxinfo.JE = e.getTextTrim();
				else if (e.getName().equals("SE"))
					taxinfo.SE = e.getTextTrim();
				else if (e.getName().equals("PH"))
					taxinfo.PH = e.getTextTrim();
				else if (e.getName().equals("SK"))
					taxinfo.SK = e.getTextTrim();
				else if (e.getName().equals("BZ"))
					taxinfo.BZ = e.getTextTrim();
			}

			return true;

			//
			// <Ret>0</Ret> //返回码 0: 开票成功, 　　// X: 错误码 　　
			// 　<Inv>
			// <RQ>20101128</RQ> //开票日期 　　
			// <JE>116.40</JE> //合计金额（含税）
			// <SE>0.63</SE> //合计税额 　　
			// <PH>140301710927388010010000004312</PH> //电子票号 　　
			// <SK>12395 84053 04230 85432</SK> //税控码
			// <BZ>对应正数发票的电子票号:
			// ******************************开票日期:xxxx-mm-dd开票金额
			// :xxxxx.xx交易流水号:xxxxxxxx </BZ> //备注
			// 　</Inv>
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
