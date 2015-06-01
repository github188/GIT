package custom.localize.Zmsy;

import java.text.DecimalFormat;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Zmjc.Zmjc_SaleBillMode;

public class Zmsy_SaleBillMode extends Zmjc_SaleBillMode
{
	//引用ZMJC的打印项
	protected final static int GWK_NATIONALITY = 217; //gwk_国籍 CustInfo_SCNATIONALITY
	protected final static int GWK_LJHB = 218; //gwk_航班号 CustInfo_SCNUMBER
	protected final static int GWK_PASSPORT = 219; //gwk_证件号：如身份证号、护照号等 CustInfo_SCPASSPORTNO
	protected final static int GWK_NAME = 220; //gwk_姓名 CustInfo_SCNAME
	protected final static int GWK_ZJLB = 222; //gwk_证件类别 CustInfo_SCOTHERNO
	
	//ZMSY新增打印项
	protected final static int GWK_GKLB = 250; //gwk_顾客类别
	protected final static int GWK_LJ_DATE = 251; //离境日期，格式： 月-日
	protected final static int GWK_LJ_DATETIME = 252; //离岛/离境时间，格式：2013-08-17 07:45
	protected final static int GWK_LJ_THDD = 253; //提货地点，即离境地点
	protected final static int GWK_LJ_BIRTHDAY = 254; //出生日期
	protected final static int GWK_LJ_MOBILE = 255; //手机号码
	
	protected final static int GWK_HBSEQ = 256; //航班序号,即分货号
	protected final static int GWK_SJSEQ = 257; //税金序号,即税单号
	
	protected final static int GOODS_MSYE = 258; //使用免税余额(元)
	protected final static int GOODS_WSJE = 259; //计税价格(元)
	protected final static int GOODS_BSSL = 260; //税率
	protected final static int GOODS_BSJE = 261; //税额(元)
	
	protected final static int HJ_HJJE = 262; //合    计 -->改为应收金额 update add 2014.01.18 for sanya lhf
	protected final static int HJ_ZBSJE = 263; //总补税金额 -->改为 机场提货_总税金 或即购即提_税金担保金  update add 2014.01.18 for sanya lhf
	protected final static int HJ_HSJE = 264; //含税金额 -->改为免税品金额(即商品成交价之和,不含税金) update add 2014.01.18 for sanya lhf
	protected final static int HJ_SCZK = 265; //特别优惠
	
	protected final static int HJ_PAYINFO = 266; //付款明细
	protected final static int HJ_YFPHM = 267; //退货原小票号
	protected final static int HJ_PAGEINFO = 268; //分页信息，格式：当前页：1/1
	protected final static int GWK_LJ_THDD_JC = 269;//离岛/离境地点:打印简称
	protected final static int GWK_LJ_DATE_JC = 270;//离境日期，格式：如是日期为今天则打印D，若是明天则打印T，否则什么都不打印
	protected final static int PAY_PAYINFO = 271;//付款明细：一行打印三个付款方式（付款方式名称+付款金额）
	
	protected final static int DETAIL_JGJT_TITLE1 = 272;//即购即提_明细head托运title1 add by 2014.01.12
	protected final static int DETAIL_JGJT_TITLE2 = 273;//即购即提_明细head托运title2 add by 2014.01.12
	protected final static int GOODS_JGJT_DETAIL = 274;//即购即提_托运detail add by 2014.01.12
	protected final static int HJ_JGJT_ZJSJ = 275;//即购即提_暂缴税金 add by 2014.01.12 或机场提货_总税金 BY 2014.1.18
	protected final static int HEAD_JGJT_TITLE1 = 276;//即购即提_tilte1 add by 2014.01.17
	protected final static int HEAD_JGJT_TITLE2 = 277;//即购即提_title2 add by 2014.01.17
	
	private SaleGoodsDef sg;
	private DecimalFormat df;
	private final static double perPageNum_goods = 8;//免税（分页打印）每页打印商品行数
	
	private final static double sxje = 8000;//免税上限额度,等同于GwkDef.sxje
		
	private double totalPageNum = 1;//总页数
	private double currPageIndex = 1;//当前第几页
	private String payPrintInfo = "";//付款信息（一行）
	
	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case GWK_NATIONALITY:
					line = getGwkItem(1);
					break;
					
				case GWK_LJHB:
					line = getGwkItem(7);
					break;
					
				case GWK_PASSPORT:
					line = getGwkItem(3);
					break;
					
				case GWK_NAME:
					line = getGwkItem(0);
					break;
					
				case GWK_ZJLB:
					line = getGwkItem(2);
					break;
					
				case GWK_GKLB:
					line = getGwkItem(4);
					break;
					
				case GWK_LJ_DATE:
					String itme5 = getGwkItem(5);
					line = "";
					if (itme5!=null && itme5.trim().length()>0) line = ManipulateDateTime.getMonthDay(itme5);
					break;
					
				case GWK_LJ_DATETIME:
					line = "";
					String[] arr = getGwkItem(5).split(" ");
					if (arr.length > 0) line = arr[0];
					if (arr.length > 1) line = line + " " + ManipulateDateTime.getFormatTime(arr[1]);
					
					break;
					
				case GWK_LJ_THDD:
					line = getGwkItem(6);
					break;
					
				case GWK_LJ_BIRTHDAY:
					line = getGwkItem(8);
					break;
					
				case GWK_LJ_MOBILE:
					line = getGwkItem(9);
					break;
					
				case GWK_HBSEQ:
					df = new DecimalFormat();
					df.applyPattern("000");					
					line = df.format(salehead.num8);
					break;
					
				case GWK_SJSEQ:
					df = new DecimalFormat();
					df.applyPattern("000");		
					line = df.format(salehead.num7);
					line = String.valueOf(Convert.toLong(String.valueOf(salehead.num7)));
					break;
					
				case GOODS_MSYE://使用免税余额(元)
					if (isJGJT())
					{
						//line = getJGJTItem(GOODS_MSYE, index);
						
						//打商品的成交价即可 by 2014.1.18
						line = ManipulatePrecision.doubleToString((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk) * SellType.SELLSIGN(salehead.djlb));
					}
					else
					{
						//处理原则：当是"离境"的时候，这里打空；否则当com.SJJE >= 8000时打印“免税税额”【sg.num11】，否则打印com.SJJE（ZJE - ZZK）
						if(getGwkItem(4).equalsIgnoreCase("离境"))
						{
							line = "";
						}
						else
						{
							sg = (SaleGoodsDef) salegoods.elementAt(index);
							if (ManipulatePrecision.doubleConvert(sg.hjje-sg.hjzk)>sxje)//>8000原来为>=8000,老系统也是这样.但yans说要改为>8000 by 2013.9.13
							{
								line = ManipulatePrecision.doubleToString(formatSign(sg.num11));
							}
							else
							{
								line = ManipulatePrecision.doubleToString(formatSign(ManipulatePrecision.doubleConvert(sg.hjje-sg.hjzk)));
							}
							
						}
						
					}
					break;
					
				case GOODS_WSJE://计税价格(元)
					if (isJGJT())
					{
						//即购即提时,不打印
						line = "";//getJGJTItem(GOODS_WSJE, index);
					}
					else
					{
						//处理原则：IIf(com.Value3 = 0, "", FormatSign(udtType, com.Value3)，注：com.Value3=sg.num7（完税价）
						sg = (SaleGoodsDef) salegoods.elementAt(index);
						if (sg.num7==0)
						{
							line = "";
						}
						else
						{
							line = ManipulatePrecision.doubleToString(formatSign(sg.num7));//精确到2位
						}
					}
					
					break;
					
				case GOODS_BSSL://税率
					if (isJGJT())
					{
						line = "";//getJGJTItem(GOODS_BSSL, index); 暂不用打印
					}
					else
					{
						//处理原则：IIf(com.Value4 = 0, "", Format(com.Value4 * 100, "0") & "%")，注：com.Value4=sg.num3
						sg = (SaleGoodsDef) salegoods.elementAt(index);
						if (sg.num3==0)
						{
							line = "";
						}
						else
						{
							df = new DecimalFormat();
							df.applyPattern("0");
							line = df.format(sg.num3 * 100) + "%";						
						}	
						
					}				
					
					break;
					
				case GOODS_BSJE://税额(元)
					if (isJGJT())
					{
						line = getJGJTItem(GOODS_BSJE, index);
					}
					else
					{
						/*处理原则：//com.Value4=sg.num3, com.Value5=sg.num4,com.Value3=sg.num7
					     If com.Value4 <> 0 AndAlso com.Value5 = 0 Then
					        '燕双提:
					        '当 税率<>0 and 税金=0 时,就打免征,并且税额(即税金)要重新计算,即税额=计税价格*税率,并保留两位小数
					         '否则什么也不动
					          Dim tmpBSJE As Double = FormatSign(udtType, com.Value3 * com.Value4)
					          PrintTemplate.SetValue("BSJE", Format(tmpBSJE, "0.00") & "(免征)") '补税金额 wangyong add by 2012.10.25
					     Else
					          PrintTemplate.SetValue("BSJE", IIf(com.Value5 = 0, "", FormatSign(udtType, com.Value5))) '补税金额 wangyong add by 2010.7.28
					     End If*/
						sg = (SaleGoodsDef) salegoods.elementAt(index);
						if (sg.num3!=0 && sg.num4==0)
						{
							double tmpBSJE = ManipulatePrecision.doubleConvert(sg.num7*sg.num3);//精确到2位
							line = ManipulatePrecision.doubleToString(formatSign(tmpBSJE)) + "(免征)";												
						}
						else
						{
							if (sg.num4==0)
							{
								line = "";
							}
							else
							{
								line = ManipulatePrecision.doubleToString(formatSign(sg.num4));
							}
						}
						
					}
					
					break;
					
				case HJ_HJJE:
					//合计(只是商品金额,不含有顾客补税金额)
					//FormatSign(udtType, sell.YSJE - (sell.SPValue - sell.AQJE - sell.BQJE))，
					//即：FormatSign(udtType, saleHead.sjfk - (saleHead.num2 - saleHead.num3 - saleHead.num6))
					// old bak 2014.1.18
					//line = ManipulatePrecision.doubleToString(formatSign(salehead.ysje - (salehead.num2 - salehead.num3 - salehead.num6)));//.sjfk 应该是应收金额,而不是实际金额 by 2013.9.22
					
					//new 总计=应收
					line = ManipulatePrecision.doubleToString(formatSign(salehead.ysje));
					break;
					
				case HJ_ZBSJE://总税金或税金担保金 by 2014.1.18
					if(isJGJT())
					{//即购即提
						line = ManipulatePrecision.doubleToString(formatSign(salehead.num9));
						line = Convert.increaseCharForward(line, 14);
						line= Convert.increaseChar("税款担保金:", 28-4) + line ;
					}
					else
					{//机场提货

						//总补税金额(若不存在"免征"(只打印免征部分),则打印空白 for 三亚王珍 by 2013.9.13)					
						boolean isOK = false;
						for (int i=0; i<salegoods.size(); i++)
						{
							sg = (SaleGoodsDef) salegoods.elementAt(0);
							if (sg.num3!=0 && sg.num4==0)
							{
								double tmpBSJE = ManipulatePrecision.doubleConvert(sg.num7*sg.num3);//精确到2位
								if(tmpBSJE>0)
								{
									//line = ManipulatePrecision.doubleToString(formatSign(tmpBSJE)) + "(免征)";
									line = ManipulatePrecision.doubleToString(formatSign(tmpBSJE));
									line = Convert.increaseCharForward(line, 14+3) + "(免征)";
									isOK = true;
									break;//只存在一件补税,所以找到后则退出
								}
								
							}
							/*else
							{
								if (sg.num4==0)
								{
									line = "";
								}
								else
								{
									line = ManipulatePrecision.doubleToString(formatSign(sg.num4));
								}
							}*/

						}
						
						if (!isOK)
						{
							//若不存在"免征"(只打印免征部分),则打印空白 for 三亚王珍 by 2013.9.13
							//line = ""; 
							
							//若不存在"免征",则打印总补税额
							
							//FormatSign(udtType, sell.SPValue - sell.AQJE - sell.BQJE),
							//即：FormatSign(udtType, saleHead.num2 - saleHead.num3 - saleHead.num6)
							double hj_zbsje = ManipulatePrecision.doubleConvert(salehead.num2 - salehead.num3 - salehead.num6);
							if (hj_zbsje<=0)
							{
								line = Convert.increaseCharForward("0.00", 14+3);
							}
							else
							{
								line = ManipulatePrecision.doubleToString(formatSign(hj_zbsje));
								line = Convert.increaseCharForward(line, 14+3);
							}
						}

						//line = ManipulatePrecision.doubleToString(formatSign(salehead.num9));
						//line = Convert.increaseCharForward(line, 14);
						line= Convert.increaseChar("      税款:", 28-4) + line ;
						
					}
					break;
					
				case HJ_HSJE:
					//含税金额(商品总成交金额+顾客补税金额+店内承担税额+厂家承担税额)
					//FormatSign(udtType, sell.YSJE + sell.AQJE + sell.BQJE)，
					//即：FormatSign(udtType, saleHead.sjfk + saleHead.num3 + saleHead.num6)//.sjfk
					//old bak 2014.01.17
					//line = ManipulatePrecision.doubleToString(formatSign(salehead.ysje + salehead.num3 + salehead.num6));//sjfk=小票应收金额+实际补税金额,实际补税金额=顾客补税金额-店内承担税额-厂家承担税额
					
					//new by 2014.1.18 免税品金额(即商品成交价之和,不含税金)
					line = ManipulatePrecision.doubleToString(formatSign(salehead.ysje - (salehead.num2 - salehead.num3 - salehead.num6) - salehead.num9));
					/*//new 2014.01.17
					double tmpJE = formatSign(salehead.ysje + salehead.num3 + salehead.num6);
					if(isJGJT())
					{//当时即购即提时,把"含税金额"改为"免税品金额",其它不变
						line = ManipulatePrecision.doubleToString(tmpJE);
						line = Convert.increaseCharForward(line, 14);
						line= Convert.increaseChar("免税品金额:", 28-4) + line ;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(tmpJE);
						line = Convert.increaseCharForward(line, 14);
						line= Convert.increaseChar("含税金额:", 28) + line ;
						
					}*/
					break;
					
				case HJ_SCZK://特别优惠(店内承担税额+厂家承担税额)
					//FormatSign(udtType, -1 * (sell.AQJE + sell.BQJE))，
					//即：FormatSign(udtType, -1 * (saleHead.num3 + saleHead.num6))
					line = ManipulatePrecision.doubleToString(formatSign(-1 * (salehead.num3 + salehead.num6)));//店内承担税额+厂家承担税额
					break;
					
				case HEAD_JGJT_TITLE1:
					if(isJGJT()) 
						line="即购即提";
					else
						line="";						

					break;
					
				case HEAD_JGJT_TITLE2:
					if(isJGJT()) 
						line="免税品核对单";
					else
						line="  提货单";	//wangyong add by 2014.3.18 for xiahuan					

					break;
					
				case DETAIL_JGJT_TITLE1://托运title1
					if(isJGJT()) 
						line="  编码          品名及规格     数量   单价(元)   金额(元)   使用免税                  税款担保金  是否";
					else
						line="  编码          品名及规格     数量   单价(元)   金额(元)   使用免税   计税价格   税率      税额";
						
					break;
					
				case DETAIL_JGJT_TITLE2://托运title2
					if(isJGJT()) 
						//line="                                (件)                        余额(元)                    (元)      托运";
					    line="                               (件)                         余额(元)                  (元）     托运";
					else
						line="                               (件)                         余额(元)    (元)               (元)";						

					break;
					
				case GOODS_JGJT_DETAIL://托运detail
					if(isJGJT()) 
						line="□";
					else
						line="";
					
					break;
					
				case HJ_JGJT_ZJSJ://暂缴税金或总税金
					/*if(isJGJT())
					{
						line = ManipulatePrecision.doubleToString(formatSign(salehead.num9));
						line = Convert.increaseCharForward(line, 14);
						line= Convert.increaseChar("税款担保金:", 28-4) + line ;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(formatSign(salehead.num9));
						line = Convert.increaseCharForward(line, 14);
						line= Convert.increaseChar("     税款:", 28-4) + line ;
					}*/
					break;
					
				case HJ_PAYINFO://付款明细
					//处理原则：一行打印三个付款方式（付款方式名称+付款金额）
					line = payPrintInfo;
					payPrintInfo="";
					break;
					
				case HJ_YFPHM://退货原小票号
					line = "";
					if (Convert.toLong(salehead.yfphm)>0) line = String.valueOf(Convert.toLong(salehead.yfphm));
					break;
					 
				case HJ_PAGEINFO://分页信息
					line = "当前页：" + String.valueOf(Convert.toLong(String.valueOf(currPageIndex))) + "/" + String.valueOf(Convert.toLong(String.valueOf(totalPageNum)));
					break;
					
				case GWK_LJ_THDD_JC://离岛/离境地点:打印简称
					line = getGwkItem(10);
					if (line==null) line="";
					break;
					
				case GWK_LJ_DATE_JC://离境日期，格式：如是日期为今天则打印D，若是明天则打印T，否则什么都不打印										
					line = getLjDate_JC(getGwkItem(5));
					break;
					
				case PAY_PAYINFO://
					line = this.payPrintInfo;
					break;
					
				case SBM_rq: // 交易日期
					line = "";
					if (salehead.rqsj.split(" ").length>0) line = salehead.rqsj.split(" ")[0].replace("/", "-");
					break;
				case SBM_zl: // 找零金额
					line = ManipulatePrecision.doubleToString(formatSign(salehead.zl));

					break;
			
				case SBM_cjdj: // 成交单价
					
					line = ManipulatePrecision.doubleToString(formatSign(ManipulatePrecision.div((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk), ((SaleGoodsDef) salegoods.elementAt(index)).sl)));

					break;
					
				case SBM_fphm: // 小票号码
					//line = Convert.increaseLong(salehead.fphm, 8);
					line = String.valueOf(salehead.fphm);

					break;
					
				default:
					return super.getItemDataString(item, index);

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
	
	/**
	 * 是否即购即提
	 * @return
	 */
	public boolean isJGJT()
	{
		if (salehead!=null && salehead.num9>0)
		{
			return true;
		}
		return false;
	}
	//获取即购即提信息
	public String getJGJTItem(int printItem, int goodsIndex)
	{
		String result = "";
		try
		{
			if(printItem<0 || goodsIndex<0) return "";
			
			SaleGoodsDef sg = (SaleGoodsDef) salegoods.elementAt(goodsIndex);
			if (sg==null) return "";
			String[] strArr = sg.str6.split("\\|");//(暂缴税金|暂缴税率|完税（价）金额|使用免税额度)
			if (printItem==GOODS_MSYE)
			{
				//使用免税余额(元)
				if(strArr.length>=4)
				{
					result = ManipulatePrecision.doubleToString(formatSign(Convert.toDouble(strArr[3])));
				}
			}
			else if(printItem==GOODS_WSJE)
			{
				//计税价格(元)
				if(strArr.length>=3)
				{
					result = ManipulatePrecision.doubleToString(formatSign(Convert.toDouble(strArr[2])));
				}
			}
			else if(printItem==GOODS_BSSL)
			{
				//税率
				if(strArr.length>=2)
				{
					df = new DecimalFormat();
					df.applyPattern("0");
					result = df.format(Convert.toDouble(strArr[1]) * 100) + "%";
				}
			}
			else if(printItem==GOODS_BSJE)
			{
				//税额(元)
				if(strArr.length>=1)
				{
					result = ManipulatePrecision.doubleToString(formatSign(Convert.toDouble(strArr[0])));
				}
			}
			else
			{
				
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return result;
	}
	
	public String getGwkItem(int index)
	{

		/*
		 saleHead.str9:
		 String[] memo = new String[11];
		 memo[0]=gwk.name;//0姓 名
		 memo[1]="";//1国 籍(格式：中国)
		 memo[2]=gwk.zjlb;//2证件类别
		 memo[3]=gwk.passport;//3证件号码
		 memo[4]="";//4顾客类别（格式：离岛）
		 memo[5]=gwk.ljrq + " " + gwk.ljsj;//5离境日期（格式：2013-08-15 10:47:27）
		 memo[6]="";//6提货地点（格式：三亚国内出发厅）
		 memo[7]=gwk.ljhb;//7离境航班
		 memo[8]=gwk.birth;//8出生日期（格式：1957-2-27）
		 memo[9]=gwk.mobile;//9手机号码
		 memo[10]="";//10 提货地点_简称(ThJC)
	*/	
		
		String result = "";
		try
		{
			if (salehead==null || salehead.str9==null || salehead.str9.length()<=0 || index<0)
			{
				
			}
			else
			{
				String[] arr = salehead.str9.split("\\|");
				if (index < arr.length)
				{
					result = arr[index];
				}
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return result;
	}
		
	private String getLjDate_JC(String strDate)
	{
		try
		{
			if (strDate==null || strDate.trim().length()<=0) return "";
			String[] arr = strDate.split(" ");
			if(arr.length<1) return "";
			if (arr[0].equalsIgnoreCase(new ManipulateDateTime().getDateBySign()))
			{
				//若是今天，则打印D
				return "D";
			}
			String strDateT = new ManipulateDateTime().skipDate(new ManipulateDateTime().getDateBySign(), 1).replace("/", "-");
			if (arr[0].equalsIgnoreCase(strDateT))
			{
				//若是明天则打印T
				return "T";
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		
		//否则什么都不打印
		return "";
		
	}
	
	public void printBill()
	{
		//super.printBill();
		if (salehead.str8!=null && salehead.str8.charAt(0)=='Y')
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("printBill() 免税机打印开始");
			//免税机打印
			printBill_FreeTax();
		}
		else
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("printBill() 有税机打印开始,打印份数=[" + GlobalInfo.sysPara.salebillnum + "].");
			//有税机打印
			if (GlobalInfo.sysPara.salebillnum<=0) GlobalInfo.sysPara.salebillnum = 1;
			for (int salebillnum = 0; salebillnum < GlobalInfo.sysPara.salebillnum; salebillnum++)
			{
				super.printBill();
			}
			
		}
		PosLog.getLog(this.getClass().getSimpleName()).info("printBill() 打印结束");
	}
	
	public void printBill_FreeTax()
	{
		try
		{
			totalPageNum = Math.ceil(salegoods.size()/perPageNum_goods);
			
			if (totalPageNum<=0)
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("printBill_FreeTax()错误：salegoods_size=[" + salegoods.size() + "],perPageNum=[" + perPageNum_goods + "],totalPageNum=[" + totalPageNum + "].");
				return;
			}
			
			for (int i = 1; i <=  Convert.toInt(String.valueOf(totalPageNum)); i++)
			{
				currPageIndex = i;

				// 设置打印方式
				printSetPage();

				// 打印头部区域
				printHeader();

				// 打印明细区域
				printDetail_FreeTax(i);

				// 打印汇总区域
				printTotal();

				// 打印付款区域
				printPay_FreeTax();

				printPayZL_FreeTax();

				// 打印尾部区域
				printBottom();

				// 切纸
				printCutPaper();
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(ex);
		}
	}
	
	//免税找零
	public void printPayZL_FreeTax()
	{		
		super.printPayZL();		 
	}
	
	//有税找零
	public void printPayZL()
	{
		//当没有找零时,则不打印 for 小贺 by 2013.10.8
		if(salehead.zl<=0) return;
		
		super.printPayZL();	
	}
	
	public void printDetail_FreeTax(int currPage)
	{
		// 设置打印区域
		setPrintArea("Detail");

		// 循环打印商品明细
		for (int i = 0; i < salegoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) salegoods.elementAt(i);

			if (!checkPrintDetail(i, salegoods.size(), currPage)) continue;
			
			// 赠品商品不打印
			if (sgd.flag == '1')
			{
				continue;
			}

			printVector(getCollectDataString(Detail, i, Width));
		}
	}
	
	private double formatSign(double je)
	{
		return je * SellType.SELLSIGN(salehead.djlb);
	}
	
	//此函数内的付款要重新整理，按相关规则打印
	public void printPay_FreeTax()
	{
		// 设置打印区域
		setPrintArea("Pay");
		
		int ipay=1;
		String payinfo = "";
		// 循环打印付款明细
		for (int k = 0; k < salepay.size(); k++)
		{
			SalePayDef spd = (SalePayDef) salepay.elementAt(k);

			// 找零付款不打印
			if (spd.flag == '2')
			{
				continue;
			}
			payinfo = payinfo + (spd.payname + ":         ").substring(0,10) + (ManipulatePrecision.doubleToString(formatSign(spd.ybje)) + "             ").substring(0,12);
			if (ipay==3 || k>=salepay.size()-1)
			{
				payPrintInfo = payinfo;
				printVector(getCollectDataString(Pay, k, Width));
				payPrintInfo="";
				payinfo="";
				ipay=0;				
			}
			
			ipay++;
			
			//printVector(getCollectDataString(Pay, i, Width));
		}
		if (payinfo.length()>0)
		{
			payPrintInfo = payinfo;
			printVector(getCollectDataString(Pay, -1, Width));
			payPrintInfo="";
			payinfo="";
		}
	}

	
	private boolean checkPrintDetail(int goodsIndex, int goodsSize, int currPage)
	{
		//3
		//1:1,2,3
		//2:4,5,6
		//3:7,8,9
		// goodsIndex>=startIndex and goodsIndex<=endIndex
		//startIndex X (currPage-1)*pageCount
		//           1 (1-1)*3=0
		//			 2 (2-1)*3=3
		//endIndex   Y currPage*pageCount-1
		//			 1 1*3-1=2
		//			 2 2*3-1=5
		int startIndex = (currPage-1) * Convert.toInt(String.valueOf(perPageNum_goods));
		int endIndex = currPage*Convert.toInt(String.valueOf(perPageNum_goods)) - 1;
		if (goodsIndex>=startIndex && goodsIndex<=endIndex) return true;
		return false;
	}
}
