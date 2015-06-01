package custom.localize.Wqbh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;

public class Wqbh_SaleBillMode extends Bcrm_SaleBillMode
{
	public static DQTaxDef dqt = null;

	public void printBottom()
	{
		// 设置打印区域
		setPrintArea("Bottom");
		// 税控信息
		printFaxInfo();
		if (!GlobalInfo.sysPara.mktcode.equals("01,6401"))
		{
			// 电子券返券提示
			printMSInfoTicket();
			// 打印重打印标志
			printRePrintMark();
		}
		printVector(getCollectDataString(Bottom, -1, Width));
	}

	// 打印赠券
	public void printSaleTicketMSInfo()
	{
		if (this.zq == null || this.zq.size() <= 0) { return; }

		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.zq = null;
			this.gift = null;
			return;
		}

		for (int i = 0; i < this.zq.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
			if (!def.type.trim().equals("99") && !def.type.trim().equals("4"))
			{

				if (GiftBillMode.getDefault().checkTemplateFile())
				{
					GiftBillMode.getDefault().setTemplateObject(salehead, def);
					GiftBillMode.getDefault().PrintGiftBill();
					Printer.getDefault().cutPaper_Journal();
					continue;
				}

				Printer.getDefault().printLine_Journal("收银机号：" + salehead.syjh + "  小票号：" + Convert.increaseLong(salehead.fphm, 8));
				if (SellType.ISCOUPON(salehead.djlb)) Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "买券交易", 1, 37, 38, 2));

				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				手 工 券", 1, 37, 38));
				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().printLine_Journal("== 券  号  : " + def.code);
				Printer.getDefault().printLine_Journal("== 券信息  : " + def.info);
				Printer.getDefault().printLine_Journal("== 券总额  : " + def.je);
				if (salehead.printnum > 0)
				{
					Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				重 打 印", 1, 37, 38));
				}
				Printer.getDefault().printLine_Journal("== 券有效期: " + def.memo);
				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().cutPaper_Journal();
			}
		}
	}

	public void printAppend()
	{
		if (!CommonMethod.isFileExist(GlobalVar.ConfigPath + "//SaleAppendMode.ini")) return;
		ReadTemplateFile(GlobalVar.ConfigPath + "//SaleAppendMode.ini");

		// 打印头部区域
		printHeader();

		// 打印明细区域
		printDetail();

		// 打印汇总区域
		printTotal();

		// 打印付款区域
		printPay();

		// 打印尾部区域
		printBottom();
	}

	protected void printSellBill()
	{
		// GlobalInfo.sysPara.fdprintyyy = (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市")))
				&& (GlobalInfo.sysPara.fdprintyyy == 'A')) { return; }
		try
		{
			if (!SellType.ISEXERCISE(salehead.djlb) && printnum < 1 && salehead.printnum < 1 && !getFaxInfo()) new MessageBox("获取税控信息失败！");

			// 设置打印方式
			printSetPage();

			// 打印头部区域
			printHeader();

			// 打印明细区域
			printDetail();

			// 打印汇总区域
			printTotal();

			// 打印付款区域
			printPay();

			// 打印尾部区域
			printBottom();

			printAppend();

			// 打印赠品联
			printGift();

			// 打印税控信息
			//			printFaxInfo();
			// 切纸
			printCutPaper();

			// 还原默认的打印模板
			ReadTemplateFile(GlobalVar.ConfigPath + "//SalePrintMode.ini");
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

	}

	public boolean getFaxInfo()
	{
		// 银川万千专用
		if (GlobalInfo.sysPara.mktcode.equals("01,6401"))
		{
			// 银川万千专用
			if (!GlobalInfo.sysPara.mktcode.equals("01,6401")) return true;
			String line = null;
			String headLine = null;
			String goodsLine = null;

			// 清除请求文件
			if (PathFile.fileExist("C:\\JavaPos\\taxRequest.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\taxRequest.txt");

				if (PathFile.fileExist("C:\\JavaPos\\taxRequest.txt"))
				{
					new MessageBox("交易请求文件request.txt无法删除,请重试");
					return false;
				}
			}

			// 清除应答文件
			if (PathFile.fileExist("C:\\JavaPos\\taxResponse.txt"))
			{
				PathFile.deletePath("C:\\JavaPos\\taxResponse.txt");

				if (PathFile.fileExist("C:\\JavaPos\\taxResponse.txt"))
				{
					new MessageBox("交易请求文件request.txt无法删除,请重试");
					return false;
				}
			}

			try
			{
				// 小票头
				headLine = ItemDetail(new String[] { salehead.syjh, String.valueOf(salehead.fphm), "个人", GlobalInfo.posLogin.name },
										new String[] { "BH", "LS", "MC", "SN" });
				headLine = closeHead(headLine);

				// 小票明细
				SaleGoodsDef sg = null;
				String lineDetail = null;

				double kpje = salehead.sjfk - salehead.zl - calcPayFPMoney();

				String str = "";
				if (kpje > 0)
				{
					str = "<MX><MC>非开票金额</MC><GG></GG><DW>个</DW><SL></SL><JE>" + String.valueOf(kpje * SellType.SELLSIGN(salehead.djlb) * -1)
							+ "</JE><DJ>" + String.valueOf(kpje * SellType.SELLSIGN(salehead.djlb) * -1) + "</DJ></MX>";
				}

				for (int i = 0; i < salegoods.size(); i++)
				{
					sg = (SaleGoodsDef) salegoods.get(i);
					lineDetail = "<MX>"
							+ ItemDetail(new String[] {
														sg.name,
														"",
														sg.unit,
														String.valueOf(sg.sl),
														String.valueOf(sg.hjje * SellType.SELLSIGN(salehead.djlb) - sg.hjzk
																* SellType.SELLSIGN(salehead.djlb)),
														String.valueOf(sg.lsj) }, new String[] { "MC", "GG", "DW", "SL", "JE", "DJ" }) + "</MX>";
					goodsLine += lineDetail;
				}
				goodsLine = closeDetail(goodsLine + str);
				line = completeSaleFax(headLine + goodsLine);

				// 输出到文本
				PrintWriter pw = null;
				try
				{
					pw = CommonMethod.writeFile("C:\\JavaPos\\taxRequest.txt");

					if (pw != null)
					{
						pw.print(line);
						pw.flush();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
				finally
				{
					if (pw != null)
					{
						pw.close();
					}
				}

				// 调用税控接口
				CommonMethod.waitForExec("C:\\JavaPos\\javapostax.exe YCWQ", "javapostax.exe");

				// 读取应答文件
				Vector v = new Vector();
				if (readResult(v))
				{
					salehead.str3 = (String) v.get(0) + "," + (String) v.get(1) + "," + (String) v.get(2) + "," + (String) v.get(3);
					// 写入本地库
					if (!AccessDayDB.getDefault().updateSaleHeadStr(salehead.fphm, "str3", salehead.str3))
					{
						new MessageBox("税控信息存盘失败");
						return false;
					}
				}
				else return false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		
		
//		 银川西夏店专用
//		if (GlobalInfo.sysPara.mktcode.equals("01,6402"))
//		{
//			//银川西夏店专用
//			if (!GlobalInfo.sysPara.mktcode.equals("01,6402")) return true;
//			String line = null;
//			String headLine = null;
//			String goodsLine = null;
//
//			// 清除请求文件
//			if (PathFile.fileExist("C:\\JavaPos\\taxRequest.txt"))
//			{
//				PathFile.deletePath("C:\\JavaPos\\taxRequest.txt");
//
//				if (PathFile.fileExist("C:\\JavaPos\\taxRequest.txt"))
//				{
//					new MessageBox("交易请求文件request.txt无法删除,请重试");
//					return false;
//				}
//			}
//
//			// 清除应答文件
//			if (PathFile.fileExist("C:\\JavaPos\\taxResponse.txt"))
//			{
//				PathFile.deletePath("C:\\JavaPos\\taxResponse.txt");
//
//				if (PathFile.fileExist("C:\\JavaPos\\taxResponse.txt"))
//				{
//					new MessageBox("交易请求文件request.txt无法删除,请重试");
//					return false;
//				}
//			}
//
//			try
//			{
//				// 小票头
//				headLine = ItemDetail(new String[] { salehead.syjh,
//				                                     String.valueOf(salehead.fphm), 
//				                                     "个人", 
//				                                     "",
//				                                     GlobalInfo.posLogin.name ,
//				                                     "",
//				                                     "",
//				                                     "0" },
//										new String[] { "BH", "LS", "MC", "GH", "SN", "XM", "XH","LX" });
//				if (SellType.ISHC(salehead.djlb) && salehead.salefphm.length() > 20)
//				{
//					headLine = headLine + "<CXXX><CXLX>1</CXLX><YS_PH>" + salehead.salefphm + "</YS_PH><YS_LS>" + salehead.yfphm + "</YS_LS></CXXX>";
//				}
//				
//				headLine = closeHead(headLine);
//
//				// 小票明细
//				SaleGoodsDef sg = null;
//				String lineDetail = null;
//				
//
//				double kpje = salehead.sjfk - salehead.zl - calcPayFPMoney();
//
//				String str = "";
//				if (kpje > 0)
//				{
//					str = "<MX><MC>非开票金额</MC><GG></GG><DW>个人</DW><SL></SL><DJ>" + String.valueOf(kpje * SellType.SELLSIGN(salehead.djlb) * -1) + "</DJ><JE>" + String.valueOf(kpje * SellType.SELLSIGN(salehead.djlb) * -1)
//							+ "</JE><SLV></SLV><SE></SE><TM></TM></MX>";
//				}
//
//				for (int i = 0; i < salegoods.size(); i++)
//				{
//					sg = (SaleGoodsDef) salegoods.get(i);
//					lineDetail = "<MX>"
//							+ ItemDetail(new String[] {
//														sg.name,
//														"",
//														sg.unit,
//														String.valueOf(sg.sl),
//														String.valueOf(sg.hjje * SellType.SELLSIGN(salehead.djlb) - sg.hjzk
//																* SellType.SELLSIGN(salehead.djlb)),
//														String.valueOf(sg.lsj),
//														"",
//														"",
//														""}, new String[] { "MC", "GG", "DW", "SL", "DJ", "JE","SLV" ,"SE" ,"TM" }) + "</MX>";
//					goodsLine += lineDetail;
//				}
//				goodsLine = closeDetail(goodsLine + str);
//				
//				String xianjin = null;
//				String card = null;
//				String quan = null;
//				
//				double zl = Lydf_Util.getChangeMone(salepay);
//
//				for (int j = 0; j < salepay.size(); j++)
//				{
//					SalePayDef pay = (SalePayDef) salepay.get(j);
//					// 有扣回，则不发送
//					if (pay.flag == '3' || pay.flag == '2')
//						continue;
//
//					PayModeDef paymode = DataService.getDefault().searchPayMode(pay.paycode);
//
//					String limitpay = "," + GlobalInfo.sysPara.taxlimitpay + ",";
//					if (limitpay.indexOf("," + pay.paycode + ",") > -1)
//						continue;
//
//					// 付款类型,1-人民币类,2-支票类,3-信用卡类,4-面值卡类,5-礼券类,6-赊销类,7-其它
//					if (paymode.type == '1' || paymode.type == '3' || paymode.type == '2' || paymode.type == '7')
//					{
//						if (paymode.type == '1' && zl > 0)
//						{
//							xianjin = "<XJ><MX><LX>0</LX><JE>"+String.valueOf(ManipulatePrecision.doubleConvert(pay.je - zl, 2, 1))+"</JE></MX></XJ>";
//						}
//						else
//						{
//							xianjin = "<XJ><MX><LX>0</LX><JE>"+String.valueOf(pay.je)+"</JE></MX></XJ>";
//						}
//					}
//					else if (paymode.type == '4' || paymode.type == '2')
//					{
//						if (pay.equals("0201"))
//						{
//							card = "<GWK><MX><KLX>1</KLX><KSX>1</KSX><KH>"+pay.payno+"</KH><KJE>"+String.valueOf(pay.je)+"</KJE></MX></GWK>";
//						}
//						else if (pay.equals("0402"))
//						{
//							card = "<GWK><MX><KLX>0</KLX><KSX>0</KSX><KH>"+pay.payno+"</KH><KJE>"+String.valueOf(pay.je)+"</KJE></MX></GWK>";
//						}
//					}
//					else if (paymode.type == '5')
//					{
//						quan = "<QUAN><MX><LX>0</LX><JE>" + String.valueOf(pay.je) + "</JE></MX></QUAN>";
//					}
//				}
//
//				goodsLine += addJSXX(xianjin + card + quan);
//							
//				line = completeSaleFax(headLine + goodsLine);
//				
//				String xml = line;
//				AccessDayDB.getDefault().writeWorkLog(line);
//				System.out.println("开票:" + xml);
//
//				// 输出到文本
//				PrintWriter pw = null;
//				try
//				{
//					pw = CommonMethod.writeFile("C:\\JavaPos\\taxRequest.txt");
//
//					if (pw != null)
//					{
//						pw.print(line);
//						pw.flush();
//					}
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//					return false;
//				}
//				finally
//				{
//					if (pw != null)
//					{
//						pw.close();
//					}
//				}
//
//				// 调用税控接口
//				CommonMethod.waitForExec("C:\\JavaPos\\javapostax.exe YCWQ", "javapostax.exe");
//
//				// 读取应答文件
//				Vector v = new Vector();
//				if (readResult(v))
//				{
//					salehead.str3 = (String) v.get(0) + "," + (String) v.get(1) + "," + (String) v.get(2) + "," + (String) v.get(3) + "," + (String) v.get(4) + "," + (String) v.get(5);
//					// 写入本地库
//					if (!AccessDayDB.getDefault().updateSaleHeadStr(salehead.fphm, "str3", salehead.str3))
//					{
//						new MessageBox("税控信息存盘失败");
//						return false;
//					}
//				}
//				else return false;
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				return false;
//			}
//			return true;
//		}
		
		

		//大庆万千 2303
		if (GlobalInfo.sysPara.mktcode.equals("01,2303"))
		{
			/*原
			 * if(!getDQTaxInfo()) {return false;}
			 * 
			 * 
			 */

			if (!getDQTaxInfo(salehead, salegoods, salepay, true))
			{
				//如果不成功，则标记税控上传标志为-1
				AccessDayDB.getDefault().updateSaleHeadStr(salehead.fphm, "num1", "-1");
				return false;
			}
			else
			{
				//如果成功，标记税控上传标志为1，并且检查上一次小票税控上传情况，若上一次不成功，则重新传上一次。
				if(!AccessDayDB.getDefault().updateSaleHeadStr(salehead.fphm, "num1", "1")){
					AccessDayDB.getDefault().updateSaleHeadStr(salehead.fphm, "num1", "1");
				}
				AccessDayDB.getDefault().writeWorkLog("上传税控小票"+salehead.fphm+"成功");
				//发送上一笔税控信息
				sendLastSaleFax(salehead.fphm-1,salehead.syjh);

			}
		}

		return true;

	}

	public boolean sendLastSaleFax(long fphm, String syjh)
	{
		//	查询上一笔小票信息
		SaleHeadDef shead = Wqbh_AccessDayDB.getSaleHead(syjh, fphm - 1);
		//若上一笔小票为上传税控，则上传小票税控信息
		if (shead.num1==-1&&!SellType.ISBACK(shead.djlb))
		{
			Vector sdetail = Wqbh_AccessDayDB.getSaleDetail(String.valueOf(shead.fphm));
			Vector spay = Wqbh_AccessDayDB.getPayDetail(String.valueOf(shead.fphm));
			if (sdetail != null && spay != null)
			{
				if (!getDQTaxInfo(shead, sdetail, spay, false))
				{
					//如果不成功，则标记税控上传标志为-1
					AccessDayDB.getDefault().updateSaleHeadStr(shead.fphm, "num1", "-1");
					return false;
				}
				else
				{
					AccessDayDB.getDefault().updateSaleHeadStr(shead.fphm, "num1", "1");
					AccessDayDB.getDefault().writeWorkLog("上传税控小票"+shead.fphm+"成功");
				}
			}
		}
		return true;
	}

	public boolean getDQTaxInfo(SaleHeadDef salehead, Vector salegoods, Vector salepay, boolean flag)
	{
		DQTaxDef df = new DQTaxDef();
		String line = null;
		//		小票头
		ManipulateDateTime md = new ManipulateDateTime();
		String payerName = ""; //付款单位（如果是退票，则为原发票的发票号码）
		String checkerName = GlobalInfo.posLogin.name + ","; //收款员名称 
		String infoID = md.getDateByEmpty() + salehead.syjh + salehead.fphm; //开票ID 号（发票唯一标识，20位数字，建议由开票日期，时间等信息组成） 
		infoID = Convert.increaseCharForward(infoID, '0', 20) + ",";
		String taxSum = String.valueOf((long) ManipulatePrecision.doubleConvert(salehead.ysje * 100, 2, 1)) + ","; //计税总金额（此发票计税部分的金和，无论正票、退票，此金额均为正单位：分） 
		String invoiceType = "1, "; //开票类型（1：正票  2：退票） 
		String reserve = "|"; //保留 

		//		 清除请求文件
		if (PathFile.fileExist("C:\\JavaPos\\taxRequest.txt"))
		{
			PathFile.deletePath("C:\\JavaPos\\taxRequest.txt");

			if (PathFile.fileExist("C:\\JavaPos\\taxRequest.txt"))
			{
				new MessageBox("交易请求文件taxRequest.txt无法删除,请重试");
				AccessDayDB.getDefault().writeWorkLog("交易请求文件taxRequest.txt无法删除");
				df.infoid = infoID;
				df.cgbz = false;
				if (flag) dqt = df;
				return false;
			}
		}

		// 清除应答文件
		if (PathFile.fileExist("C:\\JavaPos\\taxResponse.txt"))
		{
			PathFile.deletePath("C:\\JavaPos\\taxResponse.txt");

			if (PathFile.fileExist("C:\\JavaPos\\taxResponse.txt"))
			{
				new MessageBox("交易请求文件taxResponse.txt无法删除,请重试");
				AccessDayDB.getDefault().writeWorkLog("交易请求文件taxResponse.txt无法删除");
				df.infoid = infoID;
				df.cgbz = false;
				if (flag) dqt = df;
				return false;
			}
		}

		try
		{
			if (SellType.ISBACK(salehead.djlb))
			{
				StringBuffer buffer = new StringBuffer();
				while (true)
				{
					TextBox txt = new TextBox();
					String fpInfo = "";
					if(salehead.ysyjh!=null&&salehead.ysyjh.length()>0 && salehead.yfphm!=null&&salehead.yfphm.length()>0){
						fpInfo = "原收银机号:"+salehead.ysyjh+" 原小票号:"+salehead.yfphm;
					}
					if ((!txt.open("请刷输入原发票的发票号码", "发票号码", fpInfo, buffer, 0, 0, false, TextBox.AllInput)))
					{
						if (new MessageBox("你确定不要输入原发票的发票号码吗?", null, true).verify() == GlobalVar.Key1)
						{
							df.infoid = infoID;
							df.cgbz = false;
							if (flag) dqt = df;
							AccessDayDB.getDefault().writeWorkLog("手工退出输入原发票号码框 " + infoID);
							return false;
						}
						else
						{
							continue;
						}

					}
					break;
				}
				payerName = buffer.toString() + ",";
			}
			else
			{
				payerName = " ,";
			}

			if (SellType.ISBACK(salehead.djlb)) invoiceType = "2, ";
			line = payerName + checkerName + infoID + taxSum + invoiceType + reserve;

			//商品明细  
			//商品名称
			//数量(保留小数点后两位的数字，再乘以100） 
			//单价(单位：分) 
			//总额(单位：分) 
			//保留 
			String goods = "";
			for (int i = 0; i < salegoods.size(); i++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) salegoods.get(i);
				double sum = sg.hjje - sg.hjzk;
				goods = goods + "#" + sg.name + "," + String.valueOf((long) ManipulatePrecision.doubleConvert(sg.sl * 100, 2, 1)) + ","
						+ String.valueOf((long) ManipulatePrecision.doubleConvert(sg.lsj * 100, 2, 1)) + ","
						+ String.valueOf((long) ManipulatePrecision.doubleConvert(sum * 100, 2, 1)) + ", ";
			}
			line = line + goods.substring(1) + "|";

			//付款明细
			//款项名称（如：人民币、银行卡、会员卡等）
			//款项总额（单位：分） 
			//保留 
			String pay = "";
			for (int i = 0; i < salepay.size(); i++)
			{
				SalePayDef sp = (SalePayDef) salepay.get(i);
				pay = pay + "#" + sp.payname + "," + String.valueOf((long) ManipulatePrecision.doubleConvert(sp.je * 100, 2, 1)) + ", ";
			}
			line = line + pay.substring(1);

			// 输出到文本
			PrintWriter pw = null;
			try
			{
				pw = CommonMethod.writeFile("C:\\JavaPos\\taxRequest.txt");

				if (pw != null)
				{
					pw.print(line);
					pw.flush();
				}
			}
			catch (Exception e)
			{
				new MessageBox("生成税控请求文件异常！");
				e.printStackTrace();
				df.infoid = infoID;
				df.cgbz = false;
				if (flag) dqt = df;
				AccessDayDB.getDefault().writeWorkLog("生成税控请求文件异常 " + infoID);
				return false;
			}
			finally
			{
				if (pw != null)
				{
					pw.close();
				}
			}
			//获取税控服务器信息  server[0]服务器地址 server[1]端口
			String[] server = getTaxServerInfo();

			if (server == null)
			{
				df.cgbz = false;
				if (flag) dqt = df;
				return false;
			}

			if (server[0] == null || server[1] == null || server[0].trim().equals("") || server[1].trim().equals(""))
			{
				new MessageBox("获取税控服务器ip或端口失败！");
				AccessDayDB.getDefault().writeWorkLog("获取税控服务器ip或端口失败 " + infoID);
				df.infoid = infoID;
				df.cgbz = false;
				if (flag) dqt = df;
				return false;
			}
			//命令行参数如下：服务器地址，端口号，开票机器编号，收款方式的种类数，商品数量
			String syjh = ",";
			String paynum = salepay.size() + ",";
			String goodsnum = String.valueOf(salegoods.size());

			//			 调用税控接口
			if (!PathFile.fileExist("C:\\JavaPos\\JavaPosTax.exe"))
			{
				new MessageBox("找不到JavaPosTax.exe文件!", null, false);
				df.infoid = infoID;
				df.cgbz = false;
				if (flag) dqt = df;
				AccessDayDB.getDefault().writeWorkLog("找不到JavaPosTax.exe文件 " + infoID);
				return false;
			}
			CommonMethod.waitForExec("C:\\JavaPos\\JavaPosTax.exe" + " " + server[0] + server[1] + syjh + paynum + goodsnum);

			// 读取应答文件
			BufferedReader br = null;
			if (!PathFile.fileExist("C:\\JavaPos\\taxResponse.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\taxResponse.txt")) == null))
			{
				new MessageBox("找不到税控应答文件taxResponse.txt", null, false);
				df.infoid = infoID;
				df.cgbz = false;
				if (flag) dqt = df;
				AccessDayDB.getDefault().writeWorkLog("找不到税控应答文件taxResponse.txt " + infoID);
				return false;
			}
			// 读取请求数据
			String result = br.readLine();
			if (result != null || !result.trim().equals(""))
			{
				String[] v = result.split(",");
				//机器编号  发票代码  税控码  发票号  发票日期  发票时间  开票类型  计税总金额  开票ID 号  保留 
				if (v.length > 2)
				{
					df.mkt = GlobalInfo.sysPara.mktcode;
					df.syjh = salehead.syjh;
					df.fphm = salehead.fphm;
					df.machineid = v[1];
					df.invoicecode = v[2];
					df.fiscalcode = v[3];
					df.invoiceserialnumber = Integer.parseInt(v[4]);
					df.invoicedate = Integer.parseInt(v[5]);
					df.invoicetime = Integer.parseInt(v[6]);
					df.invoicetype = Integer.parseInt(v[7]);
					df.totalsum = ManipulatePrecision.doubleConvert(Double.parseDouble(v[8]), 2, 1);
					df.infoid = v[9];
					df.reserve = "";
					df.cgbz = true;
					//String[] taxref = new String[] {"mkt","syjh","fphm","machineid","invoiceCode","fiscalCode","invoiceSerialNumber","invoiceDate","invoiceTime","invoiceType","totalSum","infoID","reserve","NET_BZ","CGBZ"};
					Wqbh_NetService wns = new Wqbh_NetService();
					if (wns.sendTaxLog(df.ref, df)) df.net_bz = 'Y';

					// 写入本地库
					if (!Wqbh_AccessDayDB.writeTaxLog(df.ref, df))
					{
						new MessageBox("税控信息存盘失败");
						AccessDayDB.getDefault().writeWorkLog("税控信息存盘失败");
					}
				}
				else
				{
					df.infoid = infoID;
					df.cgbz = false;
					new MessageBox(v[0] + ":" + v[1]);
					if (flag) dqt = df;
				}
			}
			else
			{
				new MessageBox("应答文件taxResponse.txt内容为空,获取失败!");
				AccessDayDB.getDefault().writeWorkLog("taxResponse.txt内容为空 " + infoID);
				df.infoid = infoID;
				df.cgbz = false;
				if (flag) dqt = df;
				return false;
			}
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			new MessageBox("调用javapostax.exe异常!");
			AccessDayDB.getDefault().writeWorkLog("调用javapostax.exe异常");
			df.infoid = infoID;
			df.cgbz = false;
			if (flag) dqt = df;
			return false;
		}
		if (flag) dqt = df;
		return true;

	}

	public String[] getTaxServerInfo()
	{
		String[] res = new String[2];
		//		读取税控服务器ip和端口

		if (!(new File(GlobalVar.ConfigPath + "//TaxServerInfo.ini").exists()))
		{
			new MessageBox("找不到税控服务器配置文件TaxServerInfo.ini");
			AccessDayDB.getDefault().writeWorkLog("找不到税控服务器配置文件TaxServerInfo.ini");
			return null;
		}

		BufferedReader br1;
		br1 = CommonMethod.readFile(GlobalVar.ConfigPath + "//TaxServerInfo.ini");
		if (br1 == null)
		{
			new MessageBox("税控服务器配置文件TaxServerInfo.ini内容为空！");
			AccessDayDB.getDefault().writeWorkLog("税控服务器配置文件TaxServerInfo.ini内容为空");
			return null;
		}
		String linets;
		String[] sp;
		try
		{
			while ((linets = br1.readLine()) != null)
			{
				if ((linets == null) || (linets.length() <= 0))
				{
					continue;
				}
				String[] lines = linets.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2) continue;
				if (sp[0].trim().equals("TaxServerIP") && (!sp[1].trim().equals(""))) res[0] = sp[1].trim() + ",";
				if (sp[0].trim().equals("TaxServerPort") && (!sp[1].trim().equals(""))) res[1] = sp[1].trim() + ",";
			}
		}
		catch (IOException e)
		{
			new MessageBox("读取税控服务器配置文件TaxServerInfo.ini异常！");
			e.printStackTrace();
			AccessDayDB.getDefault().writeWorkLog("读取税控服务器配置文件TaxServerInfo.ini异常");
			return null;
		}
		finally
		{
			if (br1 != null)
			{
				try
				{
					br1.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();

				}
			}
		}
		return res;

	}

	public class DQTaxDef
	{
		//门店号，收银机号，小票号，机器编号，发票代码，税控码，发票好，发票日期，发票时间，开票类型，计税总金额，开票ID号，保留   
		String[] ref = new String[] {
										"mkt",
										"syjh",
										"fphm",
										"machineid",
										"invoicecode",
										"fiscalcode",
										"invoiceserialnumber",
										"invoicedate",
										"invoicetime",
										"invoicetype",
										"totalsum",
										"infoid",
										"reserve" };

		public String mkt;
		public String syjh;
		public long fphm;
		public String machineid; //机器编号 
		public String invoicecode; //发票代码 
		public String fiscalcode; //税控码 
		public int invoiceserialnumber; //发票号 
		public int invoicedate; //发票日期 
		public int invoicetime; //发票时间 
		public int invoicetype; //开票类型 
		public double totalsum; //计税总金额 
		public String infoid; //开票ID 号 
		public String reserve; //保留 
		public char net_bz = 'N';
		public boolean cgbz;

	}

	// 读取应答
	public boolean readResult(Vector v)
	{
		if (GlobalInfo.sysPara.mktcode.equals("01,6402"))
		{
			BufferedReader br = null;
			try
			{
				if (!PathFile.fileExist("C:\\JavaPos\\taxResponse.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\taxResponse.txt")) == null))
				{
					new MessageBox("读取税控应答数据失败!", null, false);
					return false;
				}

				// 读取请求数据
				String line = br.readLine();
				// 加入根节点
				line = addRoot(line);
				String[] arg = new String[] { "RQ", "JE", "SE", "PH", "SK" ,"BZ" };
				if (!parseMeth(line, arg, v) || v.size() != arg.length) return false;
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			BufferedReader br = null;
			try
			{
				if (!PathFile.fileExist("C:\\JavaPos\\taxResponse.txt") || ((br = CommonMethod.readFileGBK("C:\\JavaPos\\taxResponse.txt")) == null))
				{
					new MessageBox("读取税控应答数据失败!", null, false);
					return false;
				}

				// 读取请求数据
				String line = br.readLine();
				// 加入根节点
				line = addRoot(line);
				String[] arg = new String[] { "JE", "RQ", "PH", "SK" };
				if (!parseMeth(line, arg, v) || v.size() != arg.length) return false;
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}

	}

	// 生成请求xml串
	public String ItemDetail(String[] values, String[] arg)
	{
		if (values.length != arg.length) { return null; }

		StringBuffer sbXML = new StringBuffer();

		for (int i = 0; i < arg.length; i++)
		{
			try
			{
				sbXML.append("<" + arg[i] + ">");
				if (values[i] == null) sbXML.append("");
				else sbXML.append(values[i]);
				sbXML.append("</" + arg[i] + ">");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		return sbXML.toString();
	}

	// 解析应答xml
	public boolean parseMeth(String line, String[] arg, Vector v)
	{
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;

		dbf = DocumentBuilderFactory.newInstance();
		try
		{
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(line)));

			NodeList nList = doc.getElementsByTagName("Root");
			NodeList nlRet = ((Element) nList.item(0)).getElementsByTagName("Ret");
			String ret = ((Element) nlRet.item(0)).getFirstChild().getNodeValue();
			if (ret == null || !("0").equals(ret))
			{
				new MessageBox("税控接口返回失败 Ret = " + ret);
				return false;
			}

			NodeList nlInv = ((Element) nList.item(0)).getElementsByTagName("Inv");

			for (int i = 0; i < arg.length; i++)
			{
				NodeList nl = ((Element) nlInv.item(0)).getElementsByTagName(arg[i]);
				v.add(((Element) nl.item(0)).getFirstChild().getNodeValue());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String closeHead(String line)
	{
		return "<H>" + line + "</H>";
	}

	public String closeDetail(String line)
	{
		return "<C>" + line + "</C>";
	}

	public String completeSaleFax(String line)
	{
		return "<Cmd>1</Cmd><Inv>" + line + "</Inv>";
	}

	public String addRoot(String line)
	{
		return "<Root>" + line + "</Root>";
	}
	
	public String addJSXX(String line)
	{
		return "<JSXX>" + line + "</JSXX>";
	}

	// 打印税控信息
	public void printFaxInfo()
	{
		// 银川万千专用
		if (GlobalInfo.sysPara.mktcode.equals("01,6401"))
		{
			if (salehead.str3.length() > 0)
			{
				// 合计金额,开票日期,电子票号,税控码
				String[] fax = salehead.str3.split(",");
				if (fax.length > 3) Printer.getDefault().printLine_Normal("税控:" + fax[3] + " " + "计税额:" + fax[0] + "\n");
			}
		}
	}

	public void printHeader()
	{
		// 设置打印区域
		setPrintArea("Header");
		// 银川万千专用
		if (GlobalInfo.sysPara.mktcode.equals("01,6401"))
		{
			if (salehead.str3.length() > 0)
			{
				String[] fax = salehead.str3.split(",");
				if (fax.length > 2) Printer.getDefault().printLine_Normal(Convert.increaseCharForward(fax[2], fax[2].length() + 10) + "\n");
			}
		}
		//		 大庆万千专用 2303
		if (GlobalInfo.sysPara.mktcode.equals("01,2303"))
		{
			if (!SellType.ISEXERCISE(salehead.djlb) && (printnum > 0 || salehead.printnum > 0))
			{
				DQTaxDef dfd = Wqbh_AccessDayDB.getTaxLog(salehead.syjh, salehead.fphm);
				if (dfd == null)
				{
					String id = Convert.increaseCharForward(salehead.rqsj.substring(0, 4) + salehead.rqsj.substring(5, 7)
							+ salehead.rqsj.substring(8, 10) + salehead.fphm, '0', 20);

					Printer.getDefault().printLine_Normal("开票ID号:" + id + "\n");
					Printer.getDefault().printLine_Normal("未获取税控信息，凭此票可另行换开发票" + "\n\n");

				}
				else
				{
					Printer.getDefault().printLine_Normal("机器编号:" + dfd.machineid + "\n");
					Printer.getDefault().printLine_Normal("发票代码:" + dfd.invoicecode + "\n");
					Printer.getDefault().printLine_Normal("税 控 码:" + dfd.fiscalcode + "\n");
					Printer.getDefault().printLine_Normal("发 票 号:" + dfd.invoiceserialnumber + "\n");
					Printer.getDefault().printLine_Normal("发票日期:" + dfd.invoicedate + "  发票时间:" + dfd.invoicetime + "\n");
					Printer.getDefault().printLine_Normal(
															"开票类型:" + dfd.invoicetype + "         计税总金额:"
																	+ ManipulatePrecision.doubleConvert(dfd.totalsum / 100, 2, 1) + "\n");
					Printer.getDefault().printLine_Normal("开票ID号:" + dfd.infoid + "\n\n");

				}
			}
			else
			{
				if (dqt.cgbz == true)
				{
					Printer.getDefault().printLine_Normal("机器编号:" + dqt.machineid + "\n");
					Printer.getDefault().printLine_Normal("发票代码:" + dqt.invoicecode + "\n");
					Printer.getDefault().printLine_Normal("税 控 码:" + dqt.fiscalcode + "\n");
					Printer.getDefault().printLine_Normal("发 票 号:" + dqt.invoiceserialnumber + "\n");
					Printer.getDefault().printLine_Normal("发票日期:" + dqt.invoicedate + "  发票时间:" + dqt.invoicetime + "\n");
					Printer.getDefault().printLine_Normal(
															"开票类型:" + dqt.invoicetype + "         计税总金额:"
																	+ ManipulatePrecision.doubleConvert(dqt.totalsum / 100, 2, 1) + "\n");
					Printer.getDefault().printLine_Normal("开票ID号:" + dqt.infoid + "\n\n");

				}
				else if (dqt.cgbz == false)
				{
					Printer.getDefault().printLine_Normal("开票ID号:" + dqt.infoid.substring(0, dqt.infoid.length() - 1) + "\n");
					Printer.getDefault().printLine_Normal("未获取税控信息，凭此票可另行换开发票" + "\n\n");
				}
				dqt = null;
			}

		}
		// 打印
		printVector(getCollectDataString(Header, -1, Width));
	}

	public void printMSInfoTicket()
	{
		if (zq != null)
		{
			StringBuffer line = new StringBuffer();
			double je = 0;
			for (int i = 0; i < this.zq.size(); i++)
			{
				GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
				if (!def.type.equals("99") && !def.type.equals("98"))
				{
					line.append(" " + def.info + "\n");
					line.append(" 有效期:" + def.memo + "\n");
					je += def.je;
				}
			}

			if (je > 0)
			{
				//				Printer.getDefault().printLine_Normal(" 本次小票有返券，返券金额为:" + String.valueOf(je));
				Printer.getDefault().printLine_Normal(line.toString());
			}
		}
	}

	public void printRePrintMark()
	{
		if (salehead.printnum > 0)
		{
			printLine("-----------重打印---------");
		}
	}
	
	protected String extendCase(PrintTemplateItem item, int index) 
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			case SBM_bcjf:
				if (salehead.bcjf == 0)
				{
					line = null;
				}
				else
				{
					if(salehead.str6!=null && !salehead.str6.trim().equals("")){
						line = ManipulatePrecision.doubleToString(ManipulatePrecision.doubleConvert(salehead.bcjf,1,0));
					}else{
						line = ManipulatePrecision.doubleToString(salehead.bcjf);
					}
				}

				break;
		}
		return line;
	}
}
