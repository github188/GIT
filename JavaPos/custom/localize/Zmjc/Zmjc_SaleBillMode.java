package custom.localize.Zmjc;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;

public class Zmjc_SaleBillMode extends Bcrm_SaleBillMode
{

	public SaleCustDef saleCust = null; //小票顾客信息
	public Vector PayZL = null;

	protected final static int MDZBZ = 210; //门店主币种

	protected final static int DTJYLSH = 211; //当天交易流水号

	//protected final static int CustInfo = 212;		//顾客信息

	protected final static int PAYZL_NAME = 213; //找零币种名称
	protected final static int PAYZL_JE = 214; //找零币种金额
	protected final static int PAYBZL_NAME = 215; //补币种名称
	protected final static int PAYBZL_JE = 216; //补币种金额

	protected final static int CustInfo_SCNATIONALITY = 217; //顾客信息_国籍
	protected final static int CustInfo_SCNUMBER = 218; //顾客信息_航班号
	protected final static int CustInfo_SCPASSPORTNO = 219; //顾客信息_护照号
	protected final static int CustInfo_SCNAME = 220; //顾客信息_姓名
	protected final static int CustInfo_SCID = 221; //身份证号
	protected final static int CustInfo_SCOTHERNO = 222; //其它证件号
	protected final static int CustInfo_SCSEX = 223; //性别
	//protected final static int CustInfo_SCMEMO = 224; //
	protected final static int CustInfo_SCMEMO1 = 225; //顾客信息_memo1//出生年份(后两位)
	protected final static int CustInfo_SCMEMO2 = 226; //顾客信息_memo2//VIP卡号
	protected final static int CustInfo_SCMEMO3 = 227; //顾客信息_memo3
	protected final static int CustInfo_SCMEMO4 = 228; //顾客信息_memo4
	protected final static int CustInfo_SCMEMO5 = 229; //顾客信息_memo5
	protected final static int CustInfo_SCMEMO6 = 230; //顾客信息_memo6
	protected final static int CustInfo_SCMEMO7 = 231; //顾客信息_memo7
	protected final static int CustInfo_SCMEMO8 = 232; //顾客信息_memo8
	protected final static int CustInfo_SCMEMO9 = 233; //顾客信息_memo9
	protected final static int CustInfo_SCMEMO10 = 234; //顾客信息_memo10
	protected final static int CustInfo_SCMEMO11 = 235; //顾客信息_memo11
	protected final static int CustInfo_SCMEMO12 = 236; //顾客信息_memo12
	protected final static int CustInfo_SCMEMO13 = 237; //顾客信息_memo13
	protected final static int CustInfo_SCMEMO14 = 238; //顾客信息_memo14
	protected final static int CustInfo_SCMEMO15 = 239; //顾客信息_memo15
	protected final static int CustInfo_SCMEMO16 = 240; //顾客信息_memo16
	protected final static int CustInfo_SCMEMO17 = 241; //顾客信息_memo17
	protected final static int CustInfo_SCMEMO18 = 242; //顾客信息_memo18
	protected final static int CustInfo_SCMEMO19 = 243; //顾客信息_memo19
	protected final static int CustInfo_SCMEMO20 = 244; //顾客信息_memo20
	
	protected final static int MZ_LP_INFO = 245; //满赠礼品打印项  WANGYONG ADD BY 2014.5.28
	protected final static int ZCD_INFO	=246;//暂存单打印项

	//		protected final static int SBM_custItem = 201; // 客户化的打印项从201开始编号,自己控制

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p, SaleCustDef cust)
	{
		super.setTemplateObject(h, s, p);
		saleCust = cust;
	}

	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case MDZBZ:
					line = GlobalInfo.sysPara.mktZWB;//"RMB";
					break;

				case DTJYLSH:
					line = String.valueOf(Convert.toLong(String.valueOf(salehead.num5)));// String.valueOf(((SaleHeadDef)super.salehead).fphm);
					break;

				case CustInfo_SCNATIONALITY:
					line = "";
					if(saleCust!=null)
					{
						String scNationality = saleCust.custItem(CustInfoDef.CUST_SCNATIONALITY).value;

						if (scNationality == null) scNationality = "";

						line = scNationality;
					}
					

					break;

				case CustInfo_SCNUMBER:
					line = "";
					if(saleCust!=null)
					{
						String scNumber = saleCust.custItem(CustInfoDef.CUST_SCNUMBER).value;

						if (scNumber == null) scNumber = "";

						line = scNumber;
					}
					

					break;

				case CustInfo_SCPASSPORTNO:
					line = "";
					if(saleCust!=null)
					{
						String scPassPortNO = saleCust.custItem(CustInfoDef.CUST_SCPASSPORTNO).value;

						if (scPassPortNO == null) scPassPortNO = "";

						line = scPassPortNO;
					}
					

					break;

				case CustInfo_SCNAME:
					line = "";

					if(saleCust!=null)
					{
						String scName = saleCust.custItem(CustInfoDef.CUST_SCNAME).value;

						if (scName == null) scName = "";

						line = scName;
					}
					

					break;

				case CustInfo_SCID:
					line = "";

					if(saleCust!=null)
					{
						String scID = saleCust.custItem(CustInfoDef.CUST_SCID).value;

						if (scID == null) scID = "";

						line = scID;
					}
					

					break;

				case CustInfo_SCOTHERNO:
					line = "";

					if(saleCust!=null)
					{
						String scOtherNO = saleCust.custItem(CustInfoDef.CUST_SCOTHERNO).value;

						if (scOtherNO == null) scOtherNO = "";

						line = scOtherNO;
					}
					

					break;

				case CustInfo_SCSEX:
					line = "";

					if(saleCust!=null)
					{
						String scSex = saleCust.custItem(CustInfoDef.CUST_SCSEX).value;

						if (scSex == null) scSex = "";

						line = scSex;
					}
					

					break;

				/*case CustInfo_SCMEMO:

					String scMemo = saleCust.custItem(CustInfoDef.CUST_SCMEMO).value;

					if (scMemo == null) scMemo = "";

					line = scMemo;

					break;*/

				case PAYZL_NAME:
					String zlName = "";
					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef spd = (SalePayDef) salepay.elementAt(i);

						// 付款不打印
						if (spd.flag == '1')
						{
							continue;
						}
						if (spd.payname.indexOf(Language.apply("补")) == 0)
						{
							//补找零

						}
						else
						{
							//找零
							zlName = spd.payname;
						}
					}
					line = zlName;
					break;

				case PAYBZL_NAME:
					String brmbName = "";
					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef spd = (SalePayDef) salepay.elementAt(i);

						// 付款不打印
						if (spd.flag == '1')
						{
							continue;
						}
						if (spd.payname.indexOf(Language.apply("补")) == 0)
						{
							//补找零
							brmbName = spd.payname;
						}
						else
						{
							//找零
						}
					}

					line = brmbName;
					break;

				case PAYZL_JE:
					double zlJE = 0;
					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef spd = (SalePayDef) salepay.elementAt(i);

						// 付款不打印
						if (spd.flag == '1')
						{
							continue;
						}
						if (spd.payname.indexOf(Language.apply("补")) == 0)
						{
							//补找零

						}
						else
						{
							//找零
							zlJE = spd.ybje * SellType.SELLSIGN(salehead.djlb);
						}
					}

					line = ManipulatePrecision.doubleToString(zlJE);
					break;

				case PAYBZL_JE:
					double zlBJE = 0;
					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef spd = (SalePayDef) salepay.elementAt(i);

						// 付款不打印
						if (spd.flag == '1')
						{
							continue;
						}
						if (spd.payname.indexOf(Language.apply("补")) == 0)
						{
							//补找零
							zlBJE = spd.ybje * SellType.SELLSIGN(salehead.djlb);
						}
						else
						{
							//找零
						}
					}

					line = ManipulatePrecision.doubleToString(zlBJE);
					break;
					

				case SBM_yyyh: // 营业员号
					if(index<0 || index>salegoods.size()-1)
					{
						line = String.valueOf(((SaleGoodsDef) salegoods.elementAt(0)).yyyh);//index
					}
					else
					{
						line = String.valueOf(((SaleGoodsDef) salegoods.elementAt(index)).yyyh);
					}
					

					break;
					
				case MZ_LP_INFO://礼品信息
					if(salehead.str10 != null && salehead.str10.length()>0)
					{
						for( String zp:salehead.str10.split("\n"))
						{
							Printer.getDefault().printLine_Normal(zp);
						}
						
						line = null;//salehead.str10;
					}
					break;
					
				case ZCD_INFO://暂存单
					if(salehead.str2 != null && salehead.str2.length()>0)
					{
						String[] zcd=salehead.str2.split("\\|");//暂存单打印信息（回程航班编码|回程航班号|回程日期|回程时间|联系方式）
						if(zcd.length>=5)
						{
							Printer.getDefault().printLine_Normal("----------寄存----------");
							Printer.getDefault().printLine_Normal("回程时间：" + zcd[2].trim() + " " + zcd[3].trim());
							Printer.getDefault().printLine_Normal("回程航班：" + zcd[1].trim());
							Printer.getDefault().printLine_Normal("联系方式：" + zcd[4].trim());
							Printer.getDefault().printLine_Normal("寄存人签名：");
							Printer.getDefault().printLine_Normal("");
						}						
						line = null;
					}
					break;

				default:
					int printItem = Integer.parseInt(item.code);
					if (printItem >= CustInfo_SCMEMO1 && printItem <= CustInfo_SCMEMO20)
					{
						line = getCustMemoInfo(printItem);
						break;

					}
					else
					{
						return super.getItemDataString(item, index);
					}

			}
		}

		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text !=
		// null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);
			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}

	//获取顾客memo1-20信息
	public String getCustMemoInfo(int printItem)
	{
		String strValue = "";
		try
		{
			String scMemo1 = saleCust.custItem(CustInfoDef.CUST_SCMEMO).value;

			if (scMemo1 != null && scMemo1.length() > 0)
			{
				String[] arr = scMemo1.split("@");

				int index = -1;
				switch (printItem)
				{
					case CustInfo_SCMEMO1:
						index = 0;
						break;

					case CustInfo_SCMEMO2:
						index = 1;
						break;

					case CustInfo_SCMEMO3:
						index = 2;
						break;

					case CustInfo_SCMEMO4:
						index = 3;
						break;

					case CustInfo_SCMEMO5:
						index = 4;
						break;

					case CustInfo_SCMEMO6:
						index = 5;
						break;

					case CustInfo_SCMEMO7:
						index = 6;
						break;

					case CustInfo_SCMEMO8:
						index = 7;
						break;

					case CustInfo_SCMEMO9:
						index = 8;
						break;

					case CustInfo_SCMEMO10:
						index = 9;
						break;

					case CustInfo_SCMEMO11:
						index = 10;
						break;

					case CustInfo_SCMEMO12:
						index = 11;
						break;

					case CustInfo_SCMEMO13:
						index = 12;
						break;

					case CustInfo_SCMEMO14:
						index = 13;
						break;

					case CustInfo_SCMEMO15:
						index = 14;
						break;

					case CustInfo_SCMEMO16:
						index = 15;
						break;

					case CustInfo_SCMEMO17:
						index = 16;
						break;

					case CustInfo_SCMEMO18:
						index = 17;
						break;

					case CustInfo_SCMEMO19:
						index = 18;
						break;

					case CustInfo_SCMEMO20:
						index = 19;
						break;

					default:
						index = -1;
						break;
				}

				if (index >= 0 && index < arr.length)
				{
					strValue = arr[index].trim();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return strValue;
	}

	public void printBill()
	{
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

		printPayZL();

		// 打印尾部区域
		printBottom();

		// 切纸
		printCutPaper();
	}

	public void printPayZL()
	{
		// 设置打印区域
		setPrintArea("PayZL");

		/*// 循环打印找零明细
		 for (int i = 0; i < salepay.size(); i++)
		 {
		 SalePayDef spd = (SalePayDef) salepay.elementAt(i);

		 // 付款不打印
		 if (spd.flag == '1')
		 {
		 continue;
		 }

		 printVector(getCollectDataString(PayZL, i, Width));
		 }*/

		printVector(getCollectDataString(PayZL, -1, Width));
	}

	public boolean ReadTemplateFile(String name)
	{
		InitTemplate();

		Title = new String[] { "General", "Header", "Detail", "Total", "Pay", "PayZL", "Bottom", "Memo" };

		return super.ReadTemplateFile(Title, name);
	}

	protected void InitTemplate()
	{
		super.InitTemplate();
		PayZL = new Vector();
	}

	protected boolean addTemplateeItem(PrintTemplateItem item, String curLoc)
	{
		if (!super.addTemplateeItem(item, curLoc))
		{
			if (curLoc.equalsIgnoreCase("PayZL"))
			{
				PayZL.add(item);

				return true;
			}
		}
		return false;
	}

	public void printDetail()
	{		
		if (1==2)//是否启用新的营业员打印模式
		{
			super.printDetail();
		}
		else
		{
			//营业员打印模式
			printDetail_YYY();
		}
	}
	
	//打印营业员合计
	public void printDetail_YYY()
	{
		int i = 0;
		int j = 0;
		Vector set = null;
		CalcRulePopDef calPop = null;
		SaleGoodsDef sgd = null;
		//String line;

		// 先把商品进行分组
		set = new Vector();

		for (i = 0; i < salegoods.size(); i++)
		{
			sgd = (SaleGoodsDef) salegoods.elementAt(i);

			// 按营业员柜组分组
			for (j = 0; j < set.size(); j++)
			{
				calPop = (CalcRulePopDef) set.elementAt(j);

				if (calPop.code.equals(sgd.yyyh))
				{
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			//若没有找到,则添加一条新的
			if (j >= set.size())
			{
				calPop = new CalcRulePopDef();
				calPop.code = sgd.yyyh;
				//calPop.gz = sgd.gz;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));//商品行号
				set.add(calPop);
			}
		}
				

		// 设置打印区域
		setPrintArea("Detail");
		
		// 按分组进行分单打印(循环打印商品明细)
		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			/*if (new MessageBox("请将营业员(" + calPop.code + ")的销售单放入打印机\n\n按‘回车’键后开始打印\n按‘退出’键则跳过打印").verify() == GlobalVar.Exit)
			{
				continue;
			}

			Printer.getDefault().startPrint_Slip();

			Printer.getDefault().printLine_Slip("时间:" + salehead.rqsj + " NO." + salehead.syjh + "-" + salehead.fphm);
			Printer.getDefault().printLine_Slip("收银员:" + salehead.syyh + "          " + SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));

			if (salehead.printnum > 0)
			{
				Printer.getDefault().printLine_Slip("---------------重打印-----------------");
			}
			else
			{
				Printer.getDefault().printLine_Slip("--------------------------------------");
			}
*/
			
			double hjje = 0;
			double hjzk = 0;

			//打印营业员头
			printLine(Language.apply("营业员:") + calPop.code + "\n");//打印:营业员: 9653

			int goodsIndex = -1;//当前商品行号
			for (j = 0; j < calPop.row_set.size(); j++)
			{
				goodsIndex = Integer.parseInt((String) calPop.row_set.elementAt(j));
				sgd = (SaleGoodsDef) salegoods.elementAt(goodsIndex);
/*
				// 赠品商品不打印
				if (sgd.flag == '1')
				{
					continue;
				}*/
				
				/*line = Convert.appendStringSize("", sgd.barcode, 0, 10, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true), 12, 4, Width, 1);
				line = Convert.appendStringSize(line, " x ", 16, 3, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.jg), 19, 8, Width);
				line = Convert.appendStringSize(line, ManipulatePrecision.doubleToString(sgd.hjje), 28, 9, Width, 1);

				Printer.getDefault().printLine_Slip(line);
				Printer.getDefault().printLine_Slip(sgd.name);*/

				hjje += sgd.hjje;
				hjzk += sgd.hjzk;
				//sgd.rowno = j;//以新序号为准,否则乱了
				printVector(getCollectDataString(Detail, goodsIndex, Width));
			}
			
			//打印营业员尾
			printLine(Language.apply("小计: ") + ManipulatePrecision.doubleToString(hjje * SellType.SELLSIGN(salehead.djlb)) + "\n");//打印:小计:          10.00

			/*Printer.getDefault().printLine_Slip("--------------------------------------");
			Printer.getDefault().printLine_Slip("营业员:" + Convert.appendStringSize("", calPop.code, 0, 10, 10) + "        柜组:" + Convert.appendStringSize("", calPop.gz, 0, 10, 10));
			Printer.getDefault().printLine_Slip("总小计:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjje), 0, 10, 10) + "        折扣:" + Convert.appendStringSize("", ManipulatePrecision.doubleToString(hjzk), 0, 10, 10));

			Printer.getDefault().cutPaper_Slip();*/
		}
				
	}
}
