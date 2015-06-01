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

public class Hfhf_VipScore
{
	private Vector scoreDetails;
	private ExchangebleScore exgScore;
	private ExchangebleScoreResult exgScoreRet;

	public String getScoreDetailRequestXml(String cardno, String fromDate, String toDate)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("FromDate").setText(fromDate);
			inputParameterElement.addElement("ToDate").setText(toDate);

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

	public String getExchangebleScoreBalanceRequestXml(String cardno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);

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

	public String getLockExchangeScoreRequestXml(String djlb, String cardno, double money, double point)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("SaleDate").setText(ManipulateDateTime.getCurrentDateTimeBySign());
			inputParameterElement.addElement("Amount").setText(String.valueOf(money));
			inputParameterElement.addElement("Points").setText(String.valueOf(point));
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);
			inputParameterElement.addElement("BillType").setText("Cash");
			inputParameterElement.addElement("SrcId").setText(String.valueOf(GlobalInfo.syjStatus.fphm));

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

	public String getConfirmOrCancelExchangeScoreRequestXml(String djlb, String cardno,double money, double point, String referno)
	{
		try
		{
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("GBK");

			Element inputParameterElement = document.addElement("InputParameter");
			inputParameterElement.addElement("CardNo").setText(cardno);
			inputParameterElement.addElement("SaleDate").setText(ManipulateDateTime.getCurrentDateTimeBySign());
			inputParameterElement.addElement("Amount").setText(String.valueOf(money));
			inputParameterElement.addElement("Points").setText(String.valueOf(point));
			inputParameterElement.addElement("StoreNo").setText(GlobalInfo.sysPara.mktcode);
			inputParameterElement.addElement("PosNo").setText(GlobalInfo.syjStatus.syjh);
			inputParameterElement.addElement("Employee").setText(GlobalInfo.syjStatus.syyh);
			inputParameterElement.addElement("BillType").setText("invoice");//SellType.getDefault().typeExchange(djlb, 'N', null));
			inputParameterElement.addElement("SrcId").setText(String.valueOf(GlobalInfo.syjStatus.fphm));
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

	public boolean parseScoreDetail(String xml)
	{
		try
		{
			if (scoreDetails != null)
			{
				scoreDetails.removeAllElements();
				scoreDetails.clear();
			}

			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			System.out.println(root.getName());

			Iterator iter = root.elementIterator();

			while (iter.hasNext())
			{
				Element e = (Element) iter.next();
				if (e.getName().equals("ScoreDetails"))
				{
					Iterator elemIter = e.elementIterator();
					System.out.println(e.getName());
					scoreDetails = new Vector();

					while (elemIter.hasNext())
					{
						Element sub = (Element) elemIter.next();
						System.out.println(sub.getName());

						if (sub.getName().equals("ScoreDetail"))
						{
							Iterator end = sub.elementIterator();
							ScoreDetail itemMoney = new ScoreDetail();

							while (end.hasNext())
							{
								Element item = (Element) end.next();
								System.out.println(item.getName());

								if (item.getName().equals("Cardno"))
									itemMoney.Cardno = item.getTextTrim();
								if (item.getName().equals("Points"))
									itemMoney.Points = Convert.toDouble(item.getTextTrim());
								if (item.getName().equals("Remarks"))
									itemMoney.Remarks = item.getTextTrim();
								if (item.getName().equals("StoreNo"))
									itemMoney.StoreNo = item.getTextTrim();
								if (item.getName().equals("BillType"))
									itemMoney.BillType = item.getTextTrim();
								if (item.getName().equals("SrcId"))
									itemMoney.SrcId = item.getTextTrim();
								if (item.getName().equals("SaleDate"))
									itemMoney.SaleDate = item.getTextTrim();
								if (item.getName().equals("MerchantNo"))
									itemMoney.MerchantNo = item.getTextTrim();
								if (item.getName().equals("TerminalId"))
									itemMoney.TerminalId = item.getTextTrim();
							}

							scoreDetails.add(itemMoney);
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

	public boolean parseExchangebleScoreBalance(String xml)
	{
		try
		{
			exgScore = new ExchangebleScore();

			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			Iterator iter = root.elementIterator();

			while (iter.hasNext())
			{
				Element e = (Element) iter.next();
				if (e.getName().equals("Points"))
					exgScore.Points = Convert.toDouble(e.getTextTrim());
				if (e.getName().equals("BasicPoints"))
					exgScore.BasePoint = Convert.toDouble(e.getTextTrim());
				if (e.getName().equals("BaseCash"))
					exgScore.BaseCash = Convert.toDouble(e.getTextTrim());
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public boolean parseLockExchangeScore(String xml)
	{
		try
		{
			Document retDoc = DocumentHelper.parseText(xml);
			Element root = retDoc.getRootElement();
			System.out.println(root.getName());

			Iterator iter = root.elementIterator();
			exgScoreRet = new ExchangebleScoreResult();

			while (iter.hasNext())
			{
				Element item = (Element) iter.next();

				if (item.getName().equals("Balance"))
					exgScoreRet.Balance = Convert.toDouble(item.getTextTrim());
				if (item.getName().equals("ReferNo"))
					exgScoreRet.ReferNo = item.getTextTrim();
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public ExchangebleScoreResult getExchangebleScoreResult()
	{
		return this.exgScoreRet;
	}

	public Vector getScoreDetail()
	{
		return this.scoreDetails;
	}

	public ExchangebleScore getExchangebleScore()
	{
		return this.exgScore;
	}

	class ExchangebleScore
	{
		public double Points;
		public double BasePoint;
		public double BaseCash;
	}

	class ExchangebleScoreResult
	{
		public double Balance;
		public String ReferNo;
	}

	class ScoreDetail
	{
		public String Cardno;
		public double Points;
		public String Remarks;
		public String StoreNo;
		public String BillType;
		public String SrcId;
		public String SaleDate;
		public String MerchantNo;
		public String TerminalId;

	}
}
