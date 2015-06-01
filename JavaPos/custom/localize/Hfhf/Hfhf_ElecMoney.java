package custom.localize.Hfhf;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.javaPos.Global.GlobalInfo;

public class Hfhf_ElecMoney
{
	private Vector elecMoneyList;
	private LockPayResult lockPayResult;

	public String getRegTerminalRequestXml()
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("Terminal").setText(GlobalInfo.sysPara.mktcode + GlobalInfo.syjStatus.syjh);
			inputParameterElement.addElement("DeviceSerial").setText("");

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter content = new StringWriter();

			XMLWriter xmlWriter = new XMLWriter(content, format);
			xmlWriter.write(document);
			xmlWriter.close();

			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	public String getQueryRequestXml(String cardno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter content = new StringWriter();

			XMLWriter xmlWriter = new XMLWriter(content, format);
			xmlWriter.write(document);
			xmlWriter.close();

			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getCancelPayRequestXml(String djlb, String cardno, String acctID, double money, String referno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("Amount").setText(String.valueOf(money));
			inputParameterElement.addElement("BillType").setText("invoice");// SellType.getDefault().typeExchange(djlb,
																			// 'N',
																			// null));
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);
			inputParameterElement.addElement("SaleDate").setText(ManipulateDateTime.getCurrentDateTimeBySign());
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("EmoneyId ").setText(acctID);
			inputParameterElement.addElement("Remarks ").setText("");
			inputParameterElement.addElement("SrcId ").setText(String.valueOf(GlobalInfo.syjStatus.fphm));
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);
			inputParameterElement.addElement("ReferNo").setText(referno);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter content = new StringWriter();

			XMLWriter xmlWriter = new XMLWriter(content, format);
			xmlWriter.write(document);
			xmlWriter.close();

			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	public String getLockPayRequestXml(String djlb, String cardno, String acctID, double money)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("Amount").setText(String.valueOf(money));
			inputParameterElement.addElement("BillType").setText("invoice");// SellType.getDefault().typeExchange(djlb,
																			// 'N',
																			// null));
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);
			inputParameterElement.addElement("SaleDate").setText(ManipulateDateTime.getCurrentDateTimeBySign());
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);
			inputParameterElement.addElement("EmoneyId ").setText(acctID);
			inputParameterElement.addElement("Remarks ").setText("");
			inputParameterElement.addElement("SrcId ").setText(String.valueOf(GlobalInfo.syjStatus.fphm));
			// inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter content = new StringWriter();

			XMLWriter xmlWriter = new XMLWriter(content, format);
			xmlWriter.write(document);
			xmlWriter.close();

			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getConfirmPayRequestXml(String referno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("ReferNo").setText(referno);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			StringWriter content = new StringWriter();

			XMLWriter xmlWriter = new XMLWriter(content, format);
			xmlWriter.write(document);
			xmlWriter.close();

			return content.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getRechangeRequestXml()
	{
		return null;
	}
	
	public static void main(String[] args)
	{
		String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "<OutputParameter>" + "<EmoneyDetails>" + "<EmoneyDetail>" + "<EmoneyId>电子币账号</EmoneyId>" + "<Balance>余额</Balance>" + "<UseableAmount>电子币可用金额</UseableAmount>" + "<IsCash>是否为现金币</IsCash>" + "<Descrip>说明</Descrip>" + "<EffectiveDate>生效日期</EffectiveDate>" + "<InvalidDate>失效日期</InvalidDate>" + "<SaleAmount>消费满金额</SaleAmount>" + "<PayAmount>可以使用电子币金额</PayAmount>" + "</EmoneyDetail>" +

		"<EmoneyDetail>" + "<EmoneyId>电子币账号</EmoneyId>" + "<Balance>余额</Balance>" + "<UseableAmount>电子币可用金额</UseableAmount>" + "<IsCash>是否为现金币</IsCash>" + "<Descrip>说明</Descrip>" + "<EffectiveDate>生效日期</EffectiveDate>" + "<InvalidDate>失效日期</InvalidDate>" + "<SaleAmount>消费满金额</SaleAmount>" + "<PayAmount>可以使用电子币金额</PayAmount>" + "</EmoneyDetail>" +

		"</EmoneyDetails>" + "</OutputParameter>";
		Hfhf_ElecMoney test = new Hfhf_ElecMoney();
		test.parseQuery(xml);
	}

	public boolean parseQuery(String xml)
	{
		try
		{
			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			System.out.println(root.getName());

			Iterator iter = root.elementIterator();

			while (iter.hasNext())
			{
				Element e = (Element) iter.next();
				if (e.getName().equals("EmoneyDetails"))
				{
					Iterator elemIter = e.elementIterator();
					System.out.println(e.getName());
					elecMoneyList = new Vector();

					while (elemIter.hasNext())
					{
						Element sub = (Element) elemIter.next();
						System.out.println(sub.getName());

						if (sub.getName().equals("EmoneyDetail"))
						{
							Iterator end = sub.elementIterator();
							ElecMoney itemMoney = new ElecMoney();

							while (end.hasNext())
							{
								Element item = (Element) end.next();
								System.out.println(item.getName());

								if (item.getName().equals("EmoneyId"))
									itemMoney.EmoneyId = item.getTextTrim();
								if (item.getName().equals("Balance"))
									itemMoney.Balance = Convert.toDouble(item.getTextTrim());
								if (item.getName().equals("UseableAmount"))
									itemMoney.UseableAmount = Convert.toDouble(item.getTextTrim());
								if (item.getName().equals("EmoneymetaId"))
									itemMoney.EmoneymetaId = item.getTextTrim();
								if (item.getName().equals("Descrip"))
									itemMoney.Descrip = item.getTextTrim();
								if (item.getName().equals("EffectiveDate"))
									itemMoney.EffectiveDate = item.getTextTrim();
								if (item.getName().equals("InvalidDate"))
									itemMoney.InvalidDate = item.getTextTrim();
								if (item.getName().equals("SaleAmount"))
									itemMoney.SaleAmount =0.01;//Convert.toDouble(item.getTextTrim()) == 0 ? 1 : Convert.toDouble(item.getTextTrim());
								if (item.getName().equals("PayAmount"))
									itemMoney.PayAmount =0.01;// Convert.toDouble(item.getTextTrim()) == 0 ? 1 : Convert.toDouble(item.getTextTrim());
								if (item.getName().equals("LimitDesks"))
									itemMoney.LimitDesks = item.getTextTrim();
							}
							elecMoneyList.add(itemMoney);
						}
					}

				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean parseLockPay(String xml)
	{
		try
		{
			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();

			Iterator iter = root.elementIterator();
			lockPayResult = new LockPayResult();

			while (iter.hasNext())
			{
				Element item = (Element) iter.next();

				if (item.getName().equals("Balance"))
					lockPayResult.Balance = Convert.toDouble(item.getTextTrim());
				if (item.getName().equals("ReferNo"))
					lockPayResult.ReferNo = item.getTextTrim();
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public LockPayResult getLockPayResult()
	{
		/*
		 * lockPayResult= new LockPayResult(); lockPayResult.Balance = 10;
		 * lockPayResult.ReferNo = "10001";
		 */

		return this.lockPayResult;
	}

	public Vector getElecMoneyList()
	{
		/*
		 * if(elecMoneyList!=null) { elecMoneyList.removeAllElements();
		 * elecMoneyList.clear(); }
		 * 
		 * for (int i=0; i<5;i++) { ElecMoney item = new ElecMoney();
		 * item.Balance = 200; item.EmoneyId = "100000"+i; item.UseableAmount =
		 * 150; item.SaleAmount= i*10; item.PayAmount = i+2;
		 * 
		 * if(elecMoneyList==null) elecMoneyList = new Vector();
		 * 
		 * elecMoneyList.add(item); }
		 */

		return this.elecMoneyList;
	}

	public void clearElecMoneyList()
	{
		if (elecMoneyList == null)
			elecMoneyList = new Vector();

		elecMoneyList.removeAllElements();
		elecMoneyList.clear();
	}

	public void clearResult()
	{
		lockPayResult = null;
	}

	public LockPayResult getLockResult()
	{
		return lockPayResult;
	}

	class LockPayResult
	{
		public double Balance;
		public String ReferNo;
	}

	class ElecMoney
	{
		public String EmoneyId;
		public double Balance;
		public double UseableAmount;
		public String EmoneymetaId;
		public String Descrip;
		public String EffectiveDate;
		public String InvalidDate;
		public double SaleAmount;
		public double PayAmount;
		public String LimitDesks;

		public String haspay;
		public boolean ispay;

	}
}
