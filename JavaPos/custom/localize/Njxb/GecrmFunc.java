package custom.localize.Njxb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class GecrmFunc
{
	private String methordName = "";
	private SaleHeadDef saleHead = null;
	private Vector salePay = null;

	private final static String GECRM_BEGIN = "GECRMSERVERBEGIN";
	private final static String GECRM_END = "GECRMSERVEREND";

	public final static String CHECKOLD = "1903";
	public final static String CHECKNEW = "1904";
	public final static String SALE = "2101";
	public final static String BACK = "2171";
	public final static String RUSH = "1001";

	private final static String GEMKT = "0119"; //正式0119，测试0120

	public char isEsc = 'N';

	public GecrmFunc()
	{
	}

	public GecrmFunc(String methordName, SaleHeadDef saleHead, Vector salePay)
	{
		this.methordName = methordName;
		this.saleHead = saleHead;
		this.salePay = salePay;
	}

	private boolean writeRequest()
	{
		// 删除之前生成的请求和应答文件
		if (!deleteFile()) return false;

		try
		{
			if (!createXMLFile()) { return false; }
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(methordName + " 写入请求文件时发生异常" + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean readResult(GecrmCard card)
	{
		// 读取应答
		BufferedReader br = null;
		try
		{
			if (!PathFile.fileExist("Gecrm\\result.txt") || ((br = CommonMethod.readFileGBK("Gecrm\\result.txt")) == null))
			{
				new MessageBox(methordName + " 找不到应答文件");

				return false;
			}

			// 读取应答数据
			String line = br.readLine();
			if (line != null)
			{
				String[] ret = line.split(",");
				if (ret.length < 2)
				{
					new MessageBox(methordName + " 返回数据格式不正确");
					return false;
				}

				// 调用函数返回成功
				if (ret[0].equals("0"))
				{
					if (!readXMLFile(ret[1], card)) { return false; }
				}
				else
				{
					if (ret[1].trim().equals("ESC"))
					{
						isEsc = 'Y';
						return false;
					}
					else
					{
						String error = "";
						error = ret[1] + "\n 金鹰卡接口调用失败";
						new MessageBox(error);
						return false;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(methordName + " 读取应答数据时发生异常" + e.getMessage());
			return false;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
					if (!deleteFile()) return false;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private boolean execGecrm()
	{
		// 调用接口
		try
		{
			CommonMethod.waitForExec("Gecrm\\JavaPosGecrm.exe GECRM");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox(methordName + " 调用接口时发生异常" + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean doGecrm(GecrmCard card)
	{
		if (!sendRush()) { return false; }
		if (!writeRushFile()) { return false; }
		if (writeRequest() && execGecrm() && readResult(card))
		{
			return true;
		}
		else return false;
	}

	public boolean doRushGecrm()
	{
		if (writeRequest() && execGecrm() && readResult(null))
		{
			deleteRushFile();
			return true;
		}
		else
		{
			return false;
		}
	}

	private boolean deleteFile()
	{
		if (PathFile.fileExist("Gecrm\\request.txt"))
		{
			PathFile.copyPath("Gecrm\\request.txt", "Gecrm\\LastRequest.txt");
			PathFile.deletePath("Gecrm\\request.txt");

			if (PathFile.fileExist("Gecrm\\request.txt"))
			{
				new MessageBox(methordName + " 交易请求文件request.TXT无法删除,请重试");
				return false;
			}
		}

		if (PathFile.fileExist("Gecrm\\result.txt"))
		{
			PathFile.copyPath("Gecrm\\result.txt", "Gecrm\\LastResult.txt");
			PathFile.deletePath("Gecrm\\result.txt");

			if (PathFile.fileExist("Gecrm\\result.txt"))
			{
				new MessageBox(methordName + " 交易请求文件result.txt无法删除,请重试");
				return false;
			}
		}
		return true;
	}

	private boolean createXMLFile()
	{
		/** 建立document对象 */
		Document document = DocumentHelper.createDocument();

		Element rootElement = document.addElement("gecrm-server");
		rootElement.addAttribute("handler_type", this.methordName);

		if (CHECKOLD.equals(this.methordName) || CHECKNEW.equals(this.methordName))
		{
			rootElement.addElement("card_no");
			rootElement.addElement("card_type_no");
			Element mktElement = rootElement.addElement("company_no");
			mktElement.setText(GEMKT);
			rootElement.addElement("make_sum");
			rootElement.addElement("balance");
			rootElement.addElement("cvn_en");
			rootElement.addElement("valid_date");
			rootElement.addElement("error_msg");
		}
		else if (SALE.equals(this.methordName) || BACK.equals(this.methordName))
		{
			Element fphmElement = rootElement.addElement("exchange_no");
			fphmElement.setText(String.valueOf(this.saleHead.fphm));

			Element syjhElement = rootElement.addElement("pos_no");
			syjhElement.setText(this.saleHead.syjh);

			Element mktElement = rootElement.addElement("company_no");
			mktElement.setText(GEMKT);

			Element syyhElement = rootElement.addElement("cashier_no");
			syyhElement.setText(this.saleHead.syyh);

			Element timeElement = rootElement.addElement("sale_time");
			timeElement.setText(ManipulateDateTime.getCurrentDateTime().replace("/", "").replace(":", "").replace(" ", ""));

			Element scrdElement = rootElement.addElement("scrd_accounts");
			Element accElement = null;
			Element scrd_noElement = null;
			Element cons_sumElement = null;
			SalePayDef sp = null;
			for (int i = 0; i < this.salePay.size(); i++)
			{
				sp = (SalePayDef) this.salePay.get(i);
				if (sp.paycode.equals("0401") || sp.paycode.equals("0402"))
				{
					accElement = scrdElement.addElement("account");
					scrd_noElement = accElement.addElement("scrd_no");
					scrd_noElement.setText(sp.payno);
					cons_sumElement = accElement.addElement("cons_sum");
					cons_sumElement.setText(String.valueOf(sp.je));
				}
			}
		}
		else if (RUSH.equals(this.methordName))
		{
			Element fphmElement = rootElement.addElement("exchange_no");
			fphmElement.setText(String.valueOf(this.saleHead.fphm));

			Element djlbElement = rootElement.addElement("exchange_type");
			String type = "1";
			if (SellType.ISBACK(this.saleHead.djlb))
			{
				type = "2";
			}
			djlbElement.setText(type);

			Element syjhElement = rootElement.addElement("pos_no");
			syjhElement.setText(this.saleHead.syjh);

			Element mktElement = rootElement.addElement("company_no");
			mktElement.setText(GEMKT);

			Element syyhElement = rootElement.addElement("cashier_no");
			syyhElement.setText(this.saleHead.syyh);

			Element timeElement = rootElement.addElement("sale_time");
			timeElement.setText(ManipulateDateTime.getCurrentDateTime().replace("/", "").replace(":", "").replace(" ", ""));

			Element scrdElement = rootElement.addElement("scrd_accounts");
			Element accElement = null;
			Element scrd_noElement = null;
			Element cons_sumElement = null;
			SalePayDef sp = null;
			for (int i = 0; i < this.salePay.size(); i++)
			{
				sp = (SalePayDef) this.salePay.get(i);
				accElement = scrdElement.addElement("account");
				scrd_noElement = accElement.addElement("scrd_no");
				scrd_noElement.setText(sp.payno);
				cons_sumElement = accElement.addElement("cons_sum");
				cons_sumElement.setText(String.valueOf(sp.ybje));
			}
		}
		XMLWriter writer = null;
		try
		{
			writer = new XMLWriter(new FileWriter(new File("Gecrm\\request.txt")));
			String req = document.asXML();
			req = this.methordName + GECRM_BEGIN + req + GECRM_END;
			String len = getXmlBytes(req);
			writer.write(len + this.methordName + GECRM_BEGIN);
			writer.write(document);
			writer.write(GECRM_END);
			writer.close();
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("创建金鹰卡请求文件异常");
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private boolean readXMLFile(String xmlStr, GecrmCard card)
	{
		String finalStr = xmlStr.substring(xmlStr.indexOf(GECRM_BEGIN) + 16, xmlStr.indexOf(GECRM_END));
		//	 字符串转XML
		try
		{
			Document document = DocumentHelper.parseText(finalStr);
			// 积分卡校验
			if (this.methordName.equals(CHECKOLD) || this.methordName.equals(CHECKNEW))
			{
				List errorList = document.selectNodes("gecrm-server/error_msg");
				Element errE = (Element) errorList.get(0);
				String errMsg = errE.getStringValue();
				if (errMsg.trim().length() > 0)
				{
					new MessageBox(errMsg);
					return false;
				}
				// 取得卡号
				List list = document.selectNodes("gecrm-server/card_no");
				Element e = (Element) list.get(0);
				card.card_no = e.getStringValue();
				// 取得余额
				list = document.selectNodes("gecrm-server/balance");
				e = (Element) list.get(0);
				card.balance = Double.parseDouble(e.getStringValue());
				// 取得有效期
				list = document.selectNodes("gecrm-server/valid_date");
				e = (Element) list.get(0);
				card.valid_date = e.getStringValue();
				return true;
			}
			// 消费交易
			else if (this.methordName.equals(SALE) || this.methordName.equals(BACK) || this.methordName.equals(RUSH))
			{
				Element root = document.getRootElement();
				Attribute a = root.attribute("success");
				if ("Y".equals(a.getValue())) // 记账成功
				{
					return true;
				}
				else
				// 记账失败
				{
					List list = document.selectNodes("gecrm-server/error_msg");
					Element e = (Element) list.get(0);
					if (e.getStringValue().trim().length() > 0)
					{
						new MessageBox(e.getStringValue());
					}
					return false;
				}
			}
			else
			{
				new MessageBox("不存在的接口函数：" + methordName);
				return false;
			}
		}
		catch (Exception e)
		{
			new MessageBox("解析金鹰卡应答文件异常");
			e.printStackTrace();
			return false;
		}
	}

	private String getXmlBytes(String xmlStr)
	{
		long len;
		len = xmlStr.getBytes().length + 8;
		String strLen = String.valueOf(len);
		if (strLen.length() < 8)
		{
			strLen = Convert.increaseCharForward(strLen, '0', 8);
		}
		return strLen;
	}

	private boolean writeRushFile()
	{
		if (!this.methordName.equals(SALE) && !this.methordName.equals(BACK)) { return true; }

		FileOutputStream f = null;
		try
		{
			// 写入冲正文件
			f = new FileOutputStream(getGeRushFile());

			ObjectOutputStream s = new ObjectOutputStream(f);

			// 写入交易类型
			writeGecrmCzToStream(s);

			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("创建金鹰冲正文件失败");
			return false;
		}
		finally
		{
			try
			{
				if (f != null) f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean deleteRushFile()
	{
		if (PathFile.fileExist(getGeRushFile()))
		{
			PathFile.deletePath(getGeRushFile());
			if (PathFile.fileExist(getGeRushFile()))
			{
				new MessageBox("金鹰卡冲正文件无法删除,请重试");
				return false;
			}
		}
		return true;
	}

	public boolean sendRush()
	{
		ProgressBox pb = null;
		FileInputStream in = null;
		ObjectInputStream si = null;
		boolean ok = false;

		try
		{
			File file = new File(getGeRushFile());

			if (file.exists())
			{
				SaleHeadDef saleHead1;
				Vector salePay1;
				file = new File(getGeRushFile());

				// 显示冲正进度提示
				if (pb == null)
				{
					pb = new ProgressBox();
					pb.setText("正在发送付款冲正数据,请等待......");
				}
				in = new FileInputStream(getGeRushFile());
				si = new ObjectInputStream(in);
				saleHead1 = (SaleHeadDef) si.readObject();
				salePay1 = (Vector) si.readObject();
				// 修改冲正时间为当前时间
				saleHead1.rqsj = ManipulateDateTime.getCurrentDateTime().replace("/", "").replace(":", "").replace(" ", "");

				// 关闭文件
				in.close();
				in = null;
				si.close();
				si = null;
				GecrmFunc func = new GecrmFunc(RUSH, saleHead1, salePay1);
				if (!func.doRushGecrm()) { return false; }
			}
			ok = true;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (in != null) in.close();
				if (si != null) si.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (pb != null) pb.close();
			if (!ok)
			{
				new MessageBox("有冲正数据未发送,不能进行卡交易!");
			}
		}
	}

	public boolean hasRush()
	{
		if (PathFile.fileExist(getGeRushFile())) { return true; }
		return false;
	}

	private void writeGecrmCzToStream(ObjectOutputStream s) throws Exception
	{
		s.writeObject(saleHead);
		s.writeObject(salePay);
	}

	public String getGeRushFile()
	{
		return "Gecrm\\Cz\\GeRush.cz";
	}
}
