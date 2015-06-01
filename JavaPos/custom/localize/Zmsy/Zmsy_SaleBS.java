package custom.localize.Zmsy;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentChange;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleCustDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;
import com.efuture.javaPos.UI.Design.SaleShowAccountForm;

public class Zmsy_SaleBS extends Zmsy_SaleBSGwk
{

	/*
	 * 移至ZMJC WANGYONG BY 2013.11.14
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] rowinfos = super.rowInfo(goodsDef);
		if (!newrowInfo) return rowinfos;

		int rowindex = saleGoods.indexOf(goodsDef);
		rowinfos[0] = String.valueOf(rowindex + 1);
		int index = 0;
		boolean default1 = true;

		// 检查有没有匹配的类型
		String[] info = new String[saleEvent.table.getColumnCount()];

		for (int i = 0; i < Math.min(rowinfos.length, info.length); i++)
		{
			info[i] = rowinfos[i];
		}

		for (int i = 0; i < tab.size(); i++)
		{
			boolean fit = false;
			String[] lines = (String[]) tab.elementAt(i);
			if (lines[2].split(",").length <= 1)
			{
				if ((SellType.ISCHECKINPUT(this.saletype) && lines[2].equals("CHECK_INPUT"))) fit = true;
				else if (lines[2].equals(this.saletype)) fit = true;
			}
			else
			{
				String[] types = lines[2].split(",");
				for (int j = 0; j < types.length; j++)
				{
					if ((SellType.ISCHECKINPUT(this.saletype) && types[j].equals("CHECK_INPUT")))
					{
						fit = true;
						break;
					}
					else if (types[j].equals(this.saletype))
					{
						fit = true;
						break;
					}
				}
			}

			if (fit)
			{
				if (lines[1].split(",").length > 1)
				{
					default1 = false;

					if (lines[1].split(",")[1].equals("cjdj"))
					{
						// 成交单价
						info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(
																									ManipulatePrecision.sub(goodsDef.hjje,
																															goodsDef.hjzk),
																									goodsDef.sl), 2, 1);
					}
					else if (lines[1].split(",")[1].equals("ysje"))
					{
						// 应收金额
						info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), 2, 1);
					}
					else
					{
						info[index] = String.valueOf(PrintTemplate.findObjectValue(this, lines[1].split(",")[1], rowindex));
					}

				}
				index++;
			}
		}

		if (default1)
		{
			// 首先填入默认值
			index = 0;
			for (int i = 0; i < tab.size(); i++)
			{
				String[] lines = (String[]) tab.elementAt(i);
				if (lines[2].equals("Default"))
				{
					if (lines[1].split(",").length > 1)
					{

						if (lines[1].split(",")[1].equals("barcode"))
						{
							info[index] = (goodsDef.barcode == null ? "" : goodsDef.barcode.trim());
						}
						else if (lines[1].split(",")[1].equals("code"))
						{
							info[index] = (goodsDef.code == null ? "" : goodsDef.code.trim());
						}
						else if (lines[1].split(",")[1].equals("hh"))
						{
							info[index] = (goodsDef.str12 == null ? "" : goodsDef.str12.trim());
							;
						}
						else if (lines[1].split(",")[1].equals("name"))
						{
							info[index] = (goodsDef.name == null ? "" : goodsDef.name.trim());
							;
							;
						}
						else if (lines[1].split(",")[1].equals("unit"))
						{
							info[index] = (goodsDef.unit == null ? "" : goodsDef.unit.trim());
							;
							;
							;
						}
						else if (lines[1].split(",")[1].equals("sl"))
						{
							info[index] = ManipulatePrecision.doubleToString(goodsDef.sl, 2, 1);
							;
						}
						else if (lines[1].split(",")[1].equals("cjdj"))
						{
							// 成交单价
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(ManipulatePrecision.sub(goodsDef.hjje,
																																goodsDef.hjzk),
																										goodsDef.sl), 2, 1);
						}
						else if (lines[1].split(",")[1].equals("sj"))
						{
							// 售价(折前单价)
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.div(goodsDef.hjje, goodsDef.sl), 2, 1);
						}
						else if (lines[1].split(",")[1].equals("zkje"))
						{
							// 折扣金额，百分比显示折扣
							//info[index] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
							info[index] = ManipulatePrecision.doubleToString(goodsDef.hjzk);//宽度有限，所以暂不显示百分比
						}
						else if (lines[1].split(",")[1].equals("ysje"))
						{
							// 应收金额
							info[index] = ManipulatePrecision.doubleToString(ManipulatePrecision.sub(goodsDef.hjje, goodsDef.hjzk), 2, 1);
						}
						else
						{
							info[index] = String.valueOf(PrintTemplate.findObjectValue(this, lines[1].split(",")[1], rowindex));
						}

					}

					index++;
				}
			}
		}

		return info;
	}*/

	public boolean allowStartFindGoods()
	{
		//录入商品前,检查免税款机是否已经刷购物卡; （退货时，按付款键时会自动去查找）
		if (SellType.ISSALE(saletype) && checkFreeTaxSYJ() && !checkGwkInput())
		{
			//未刷购物卡
			new MessageBox("市内店【免税收银机】必须先刷［购物卡］才能购物!");
			return false;
		}
		return super.allowStartFindGoods();
	}

	public void calcAllRebate(int index)
	{
		super.calcAllRebate(index);

		//计算购物卡折扣率
		calcGoodsPOPRebate_Ex(index);
	}

	//计算购物卡折扣率，取分期促销按低价优先原则
	public void calcGoodsPOPRebate_Ex(int index)
	{
		try
		{
			//super.calcGoodsPOPRebate(index);

			//与分期促销按低价优先原则获取
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
			if (saleGoodsDef.yhzke >= 0 && gwk != null && gwk.zkl > 0 && gwk.zkl < 1)
			{
				boolean isGwkZkl = false;
				if (gwk.str2 != null && gwk.str2.equalsIgnoreCase("Y"))
				{
					//若为Y,则允许购物卡折扣(不管商品单价是否超8K)
					isGwkZkl = true;
					PosLog.getLog(this.getClass().getSimpleName()).info("calcGoodsPOPRebate_Ex() [" + String.valueOf(gwk.str2) + "].");
				}
				else
				{
					if ((gwk.ispdxe == null || gwk.ispdxe.equalsIgnoreCase("N") == false) && saleGoodsDef.jg > gwk.sxje)
					{//离境顾客(ispdxe=N)不判断是否超8K
						//当商品单价超8K时,不允许购物卡折扣
						isGwkZkl = false;
						PosLog.getLog(this.getClass().getSimpleName()).info(
																			"calcGoodsPOPRebate_Ex() 该商品单价超过限额,不允许进行购物卡折扣 je=[" + saleGoodsDef.jg
																					+ "],sxje=[" + gwk.sxje + "].");
					}
					else
					{
						isGwkZkl = true;
					}
				}
				if (isGwkZkl)
				{

					if (!isUseGwkZkl)
					{
						PosLog.getLog(this.getClass().getSimpleName()).info(
																			"calcGoodsPOPRebate_Ex() 之前收银员选择[不使用卡折扣],所以不进行购物卡折扣, gwk.zkl=[" + gwk.zkl
																					+ "].");
						return;//若收银员选择不使用购物卡折扣,则不允许进行购物卡折扣
					}

					//当存在分期促销且存在购物卡折扣率时，则与购物卡折扣率进行比较
					double tmpzke = 0;
					tmpzke = saleGoodsDef.hjje * (1 - gwk.zkl);
					tmpzke = ManipulatePrecision.doubleConvert(tmpzke, 2, 1);
					if (tmpzke > saleGoodsDef.yhzke)
					{//当购物卡的折扣率大时，则取购物卡的折扣率
						PosLog.getLog(this.getClass().getSimpleName()).info(
																			"calcGoodsPOPRebate_Ex() 取购物卡折扣率gwk.zkl=[" + gwk.zkl + "],gwk_zke=["
																					+ tmpzke + "],yhzke=[" + saleGoodsDef.yhzke + "],hjje=["
																					+ saleGoodsDef.hjje + "],code=[" + saleGoodsDef.code + "].");
						saleGoodsDef.yhzke = tmpzke;
						saleGoodsDef.yhzkfd = 0;
						saleGoodsDef.yhdjbh = "";//gwk.zjlb + gwk.passport; 当是购物卡折扣率时,记为空

						//					 
						saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);

						// 按价格精度计算折扣
						saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
					}
					else
					{
						PosLog.getLog(this.getClass().getSimpleName()).info(
																			"calcGoodsPOPRebate_Ex() 普通促销额大于购物卡折扣率额,所以不取购物卡折扣率gwk.zkl=[" + gwk.zkl
																					+ "],gwk_zke=[" + tmpzke + "],yhzke=[" + saleGoodsDef.yhzke
																					+ "],hjje=[" + saleGoodsDef.hjje + "],code=[" + saleGoodsDef.code
																					+ "].");

					}
				}

			}

		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}

	/**
	 * ZJSY:检查免税四限
	 */
	public boolean checkCustPay()
	{
		try
		{
			//练习模式不判断
			if (SellType.ISEXERCISE(saletype)) return true;

			//按付款键后,检查免税款机是否已经刷购物卡
			if (checkFreeTaxSYJ() && !checkGwkInput())
			{
				new MessageBox("市内店【免税收银机】必须先刷［购物卡］才能购物!");
				return false;
			}

			return checkGwkLimit();
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}

		return true;
	}

	public void paySellCancel()
	{
		try
		{
			super.paySellCancel();

			//清除税额等信息
			clearLimitJE();
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}

	}

	/**
	 * 检查四限
	 * @return true:检查通过 ， false:检查不通过（不允许进入付款界面）
	 */
	private boolean checkGwkLimit()
	{
		try
		{

			if (checkFreeTaxSYJ() && gwk.ispdxe != null && gwk.ispdxe.equalsIgnoreCase("Y"))
			{//市内店且为免税款机、且要判断限额时，则进行以下操作

				//saleHead.str6 = gwk.zjlb + gwk.passport;//购物卡号（证件类型+证件号）
				//saleHead.str7 = gwk.sjcd;//税金承担(1为中免承担,2为顾客承担)
				//PosLog.getLog(this.getClass().getSimpleName()).info("gwk.passport=[" + gwk.passport + "],saleHead.str6=[" + saleHead.str6 + "]");

				if (SellType.ISSALE(saletype))
				{//当是销售类型时

					//清除限额
					clearLimitJE();
					
					//检查是否满足即购即提条件
					if(!checkJGJTPay()) return false;

					//检查限额
					if (!checkGoodsLimitJE()) return false;

					//本地检查超额限商品的量(判断是否超额)
					if (!checkMoreGoodsSL())
					{
						new MessageBox("当前购买的超限商品总数已经超过购物卡的限量!\n卡限购数量:" + ManipulatePrecision.doubleConvert(gwk.bsjs) + "\n超限额标准:"
								+ ManipulatePrecision.doubleConvert(gwk.sxje));
						return false;
					}

					//联网检查非超额商品的限量(判断是否超量)
					if (!checkLessGoodsSL(calcStr('2'))) return false;

					//发送商品组合串(计算补税税额)
					//if (!sendLimitJEStr(calcStr())) return false;//old BAK 2014.01.11
					double dblMSED =getMSDE();
					String str = calcStr('1', dblMSED);
					if (!sendLimitJEStr(str)) return false;
					
					//即购即提
					str = calcStr('3', dblMSED);
					return sendLimitJEStr_ZJSJ(str);

				}
				else
				{//退货不控制限额

					double dblBSJE = 0;
					double dblBSJE_ZJSJ = 0;
					SaleGoodsDef sg;
					for (int i = 0; i < saleGoods.size(); i++)
					{
						sg = (SaleGoodsDef) saleGoods.elementAt(i);
						if (sg == null) continue;
						//税金
						dblBSJE += ManipulatePrecision.doubleConvert(sg.num4 - sg.num8 - sg.num10);
						
						//即购即提_暂缴税金 add by 2014.01.11
						dblBSJE_ZJSJ += ManipulatePrecision.doubleConvert(sg.num12);
						
					}					

					dispPay();

					String msg ="";

					if (dblBSJE_ZJSJ>0) //add by 2014.01.11
					{
						//this.showMsg("原小票的【暂缴税金】为：" + ManipulatePrecision.doubleConvert(dblBSJE) + "\n此费用需退还给顾客.");
						 msg = msg + "原小票的【税款担保金】为：" + ManipulatePrecision.doubleConvert(dblBSJE_ZJSJ) + "元\n";
						 if (saleHead.str8==null || saleHead.str8.length()<=0) saleHead.str8=" ";
						 saleHead.str8 = saleHead.str8.charAt(0) + "Y";
					}	
					else if (dblBSJE > 0 && gwk.sjcd.equalsIgnoreCase("2"))//old bak 2014.01.11
					{
						//this.showMsg("原小票的补税金额为：" + ManipulatePrecision.doubleConvert(dblBSJE) + "\n此费用需退还给顾客.");
						msg = "原小票的【补税金额】为：" + ManipulatePrecision.doubleConvert(dblBSJE) + "\n";
					}		

					if (msg.length()>0) 
					{
						msg = msg + "\n此费用需退还给顾客.";
						this.showMsg(msg);
					}
				}
			}

		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}

	//刷新界面信息
	private void dispPay()
	{
		try
		{

			// 重算小票应收  
			calcHeadYsje();

			//显示汇总
			saleEvent.setTotalInfo(getSellPayMoneyLabel(), getTotalPayMoneyLabel(), getTotalMoneyLabel(), getTotalQuantityLabel(),
									getTotalRebateLabel());

			/*SaleGoodsDef sg;
			 for (int i = 0;i < saleGoods.size();i++)
			 {
			 sg = (SaleGoodsDef) saleGoods.elementAt(i);			
			 if (sg==null) continue;
			 getZZK(sg);
			 calcGoodsYsje(i);
			 }
			 
			 // 重算小票应收  
			 calcHeadYsje();
			 
			 //--------------------行邮税承担-----------------------
			 //顾客承担时,要将补税金额加入付款总金额内
			 //顾客承担 = 税金 - 店内承担税额 - 厂商承担税额 // Sale.YSJE += (.Value5 - .Value2 - .Value1)
			 if (gwk.sjcd.equals("2"))
			 {
			 double je_gkcd = 0;//顾客承担的税金
			 for (int i = 0;i < saleGoods.size();i++)
			 {
			 sg = (SaleGoodsDef) saleGoods.elementAt(i);			
			 if (sg==null) continue;
			 
			 je_gkcd += sg.num4 - sg.num8 - sg.num10;//顾客承担 = 税金 - 店内承担税额 - 厂商承担税额
			 }
			 je_gkcd = ManipulatePrecision.doubleConvert(je_gkcd);
			 if (je_gkcd!=0)
			 {
			 saleyfje += je_gkcd;
			 
			 //显示汇总
			 //saleEvent.setTotalInfo();					
			 saleEvent.setTotalInfo(getSellPayMoneyLabel(), getTotalPayMoneyLabel(), getTotalMoneyLabel(), getTotalQuantityLabel(), getTotalRebateLabel());
			 //saleEvent.setTotalInfo(getSellPayMoneyLabel(), getTotalPayMoneyLabel(), ManipulatePrecision.doubleToString(saleyfje), getTotalQuantityLabel(), getTotalRebateLabel());
			 //saleHead.ysje
			 }
			 }*/

		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}

	public void calcHeadYsje()
	{
		SaleGoodsDef saleGoodsDef = null;
		int sign = 1;
		double je_gkcd = 0;//顾客承担金额

		saleHead.hjzje = 0;
		saleHead.hjzsl = 0;
		saleHead.hjzke = 0;
		saleHead.hyzke = 0;
		saleHead.yhzke = 0;
		saleHead.lszke = 0;

		saleHead.num2 = 0;
		saleHead.num3 = 0;
		saleHead.num6 = 0;
		
		saleHead.num9 = 0;//即购即提_暂缴税金

		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			if (!statusCond(saleGoodsDef))
			{
				continue;
			}

			// 合计商品件数(电子秤商品总是按1件记数)
			int spjs = (int) saleGoodsDef.sl;
			if (saleGoodsDef.flag == '2') spjs = 1;
			saleHead.hjzsl += spjs;

			// 以旧换新商品,合计要减
			if (saleGoodsDef.type == '8')
			{
				sign = -1;
			}
			else
			{
				sign = 1;
			}

			// 计算小票头汇总
			saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.hjzje + (saleGoodsDef.hjje * sign), 2, 1); // 合计总金额
			saleHead.hjzke = ManipulatePrecision.doubleConvert(saleHead.hjzke + (saleGoodsDef.hjzk * sign), 2, 1); // 合计折扣额

			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzke * sign), 2, 1); // 会员折扣额(来自会员优惠)	
			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzklje * sign), 2, 1); // 会员折扣率金额(来自会员优惠)	

			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.yhzke * sign), 2, 1); // 优惠折扣额(来自营销优惠)	
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.zszke * sign), 2, 1); // 赠送折扣	
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.rulezke * sign), 2, 1); // 超市规则促销折扣（非整单折扣）
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.mjzke * sign), 2, 1); // 超市规则促销折扣（整单折扣）

			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszke * sign), 2, 1); // 零时折扣额(来自手工打折)	
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszre * sign), 2, 1); // 零时折让额(来自手工打折)	
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzk * sign), 2, 1); // 零时总品折扣					
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzr * sign), 2, 1); // 零时总品折让					
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.plzke * sign), 2, 1); // 批量折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.cjzke * sign), 2, 1); // 厂家折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.ltzke * sign), 2, 1); // 零头折扣	
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzke * sign), 2, 1); // 其他折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzre * sign), 2, 1); // 其他折扣

			//三亚_税金金额
			saleHead.num2 = ManipulatePrecision.doubleConvert(saleHead.num2 + (saleGoodsDef.num4 * sign), 2, 1);
			//行邮税店承担
			saleHead.num3 = ManipulatePrecision.doubleConvert(saleHead.num3 + (saleGoodsDef.num8 * sign), 2, 1);
			//行邮税厂家承担
			saleHead.num6 = ManipulatePrecision.doubleConvert(saleHead.num6 + (saleGoodsDef.num10 * sign), 2, 1);
			
			//暂缴税金合计 ADD BY 2014.01.11
			saleHead.num9 = ManipulatePrecision.doubleConvert(saleHead.num9 + (saleGoodsDef.num12 * sign), 2, 1);
			
			PosLog.getLog(this.getClass().getSimpleName()).info("calcHeadYsje code=[" + saleGoodsDef.code + "],num4=[" + String.valueOf(saleGoodsDef.num4) + "],num8=[" + String.valueOf(saleGoodsDef.num8) + "],num12=[" + String.valueOf(saleGoodsDef.num12) + "].");
		}

		//顾客承担 = 税金 - 店内承担税额 - 厂商承担税额 
		if (saleHead.str7 != null && saleHead.str7.equalsIgnoreCase("2"))
		{
			//顾客承担时,要将补税金额加入付款总金额内
			je_gkcd = (saleHead.num2 - saleHead.num3 - saleHead.num6);
			PosLog.getLog(this.getClass().getSimpleName()).info("calcHeadYsje 0je_gkcd=[" + String.valueOf(je_gkcd) + "]");
		}
		
		//即购即提_暂缴税金
		je_gkcd = je_gkcd + saleHead.num9; 
		PosLog.getLog(this.getClass().getSimpleName()).info("calcHeadYsje je_gkcd=[" + String.valueOf(je_gkcd) + "]");
		
		saleHead.ysje = ManipulatePrecision.doubleConvert(saleHead.hjzje - saleHead.hjzke + je_gkcd, 2, 1);

		// 计算应付
		calcHeadYfje();
	}

	public boolean exitPaySell()
	{
		boolean blnRet = false;
		try
		{
			blnRet = super.exitPaySell();
			if (blnRet && SellType.ISBACK(saletype))
			{
				//清除退货的税额
				double je_gkcd = 0;//顾客承担金额				

				//顾客承担 = 税金 - 店内承担税额 - 厂商承担税额 
				if (saleHead.str7 != null && saleHead.str7.equalsIgnoreCase("2"))
				{
					//顾客承担时,要将补税金额加入付款总金额内
					je_gkcd = (saleHead.num2 - saleHead.num3 - saleHead.num6);
					saleHead.num2 = 0;
					saleHead.num3 = 0;
					saleHead.num6 = 0;					
				}
				je_gkcd = je_gkcd + saleHead.num9;
				saleHead.num9 = 0;//即购即提_暂缴税金
				saleHead.str8 = saleHead.str8.charAt(0) + "N";
				saleHead.ysje = ManipulatePrecision.doubleConvert(saleHead.hjzje - saleHead.hjzke - je_gkcd, 2, 1);

				// 计算应付
				calcHeadYfje();
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
		return blnRet;
	}

	//清除限额
	private void clearLimitJE()
	{
		if (SellType.ISBACK(saletype)) return;//退货时不清空,否则有问题

		SaleGoodsDef sg;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) saleGoods.elementAt(i);
			if (sg == null) continue;

			sg.str10 = "";//行邮税单据编号
			sg.str11 = "";//税号
			sg.num3 = 0;//补税税率
			sg.num4 = 0;//补税税额
			sg.num7 = 0;//完税金额
			sg.num8 = 0;//行邮税店承担
			sg.num10 = 0;//行邮税厂家承担
			sg.num11 = 0;//免税额度
			
			sg.num12 = 0;//即购即提_暂缴税金 add by 2014.01.11
			sg.str6 = "";//即购即提字段 add by 2014.01.11
		}

		//saleHead.str6="";//购物卡号（证件类型+证件号）
		//saleHead.str7="";//税金承担(1为中免承担,2为顾客承担)
		saleHead.num2 = 0;//税金金额
		saleHead.num3 = 0;//行邮税店承担金额
		saleHead.num6 = 0;//行邮税厂家承担金额	
		
		saleHead.num9 = 0;//暂缴税金合计 add by 2014.01.11
		//saleHead.str8 = saleHead.str8.charAt(0) + "N";//是否免税款机+是否即购即提 add by 2014.01.11
	}

	//检查限额
	private boolean checkGoodsLimitJE()
	{
		try
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(0);
			if (gwk.xe <= 0 && saleGoods.size() > 0)
			{
				double dblGoodsJG = (sg.hjje - sg.hjzk) / sg.sl;
				if (dblGoodsJG > gwk.sxje && saleGoods.size() <= 1 && sg.sl <= gwk.bsjs)
				{
					//当卡余额为0、且商品的成交单价大于8K、且商品数量<=可补税件数时,则允许购买此商品
					return true;
				}
				else
				{
					PosLog.getLog(this.getClass().getSimpleName()).info(
																		"checkGoodsLimitJE() 当前商品的总成交价=[" + dblGoodsJG + "],卡余额=[" + gwk.xe
																				+ "],限额=[" + gwk.sxje + "],限购数量=[" + gwk.bsjs + "],商品行数=["
																				+ saleGoods.size() + "],第一行商品的数量=[" + sg.sl + "].");

					//此提示新加 2013.9.19 start by xhq,xh
					if (dblGoodsJG <= gwk.sxje)
					{
						new MessageBox("不能购物：当卡余额为0时，商品单价必须大于 " + gwk.sxje + " 元");
						return false;
					}
					if (sg.sl > gwk.bsjs)
					{
						String str = String.valueOf(gwk.bsjs);
						if (gwk.bsjs <= 0)
						{
							new MessageBox("不能购物：因为卡余额为0，且当前可补税商品的件数为 0 件");
						}
						else
						{
							new MessageBox("不能购物：当卡余额为0时，当前补税商品的件数不能超过 " + str + " 件");
						}

						return false;
					}
					//此提示新加 2013.9.19 end

					new MessageBox("不能购物：因为卡余额为0，且未找到符合条件的商品!");
					return false;
				}
			}

			//单价小于等于面额的商品成交金额合计,若超过了卡余额,则不允许通过
			if (getAllGoodsJG() > gwk.xe)
			{
				//当商品总成交价大于卡余额时，只能购买一个并且是第一次用卡（余额未用）
				if (saleGoods.size() <= 1)
				{
					if (ManipulatePrecision.doubleConvert(sg.sl) > 1
							|| ManipulatePrecision.doubleConvert(gwk.xe) != ManipulatePrecision.doubleConvert(gwk.sxje))
					{
						this.showMsg("不能购物,购买的商品已经超过购物卡限额!\n卡余额:" + ManipulatePrecision.doubleConvert(gwk.xe) + "\n已超额:"
								+ ManipulatePrecision.doubleConvert(getAllGoodsJG() - gwk.xe));
						return false;
					}
					return true;
				}
				else
				{
					//多条商品且超额时,不允许购物
					showMsg("不能购物,购买的商品已经超过购物卡限额! \n卡余额:" + ManipulatePrecision.doubleConvert(gwk.xe) + "\n已超额:"
							+ ManipulatePrecision.doubleConvert(getAllGoodsJG() - gwk.xe));
					return false;
				}
			}
			else
			{
				return true;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			showMsg("检查限额时发生异常：" + ex.getMessage());
			return false;
		}
	}

	/**
	 * 获取录入的非超限商品的成交价
	 * @return
	 */
	private double getAllGoodsJG()
	{
		double zje = 0;
		try
		{
			SaleGoodsDef sg;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg == null) continue;

				if (ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / sg.sl) <= gwk.sxje)
				{
					zje += ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return zje;
	}

	//本地检查超额限商品的量
	private boolean checkMoreGoodsSL()
	{
		double zsl = 0;
		try
		{
			SaleGoodsDef sg;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg == null) continue;

				if (ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / sg.sl) > gwk.sxje)
				{
					zsl += sg.sl;
				}
			}

			if (zsl > gwk.bsjs) { return false; }
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
		}
		return true;
	}

	//联网检查非超额商品的限量
	private boolean checkLessGoodsSL(String strList)
	{
		//代码待加...
		StringBuffer sbList = new StringBuffer();
		if (!dataservice.checkLessGoodsSL(strList, gwk.zjlb + gwk.passport, sbList))
		{
			new MessageBox("检查商品限量失败！");
			return false;
		}
		return true;
	}

	
	/**
	 * 拼装发送的限额字符串[1-超额商品,2-非超额商品]
	 * @return
	 */
	private String calcStr()
	{		
		//'1.获取当前所有(免税)商品的成交价
		//'2.当应收金额大于免税税额时,则提示输入税额,其中税额应该减去单价格小于税额部分 (结果要>=0)
		double msed_max = 0;//当前可使用的免税税金
		double msed = 0;//当前要使用的免税税金
		double ysje = 0;//所有商品的成交价
		double je = 0;//所有单价小于 可用免税税额 的商品成交价
		SaleGoodsDef sg;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) saleGoods.elementAt(i);
			if (sg == null) continue;

			ysje = ysje + ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
			if (ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / sg.sl) <= gwk.xe)
			{
				je = je + ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
			}

		}
		if (1 == 2)//参数待加
		{
			//参数控制"是否弹免税额度的提示框",且"商品成交金额与卡当前可用免税额度,两者取低" for yans by 2013.1.30 春节之后用
			if (ysje > gwk.xe)
			{
				msed = gwk.xe;
			}
		}
		else
		{
			if (ysje > gwk.xe)
			{
				//当应收金额大于免税税额时,则提示输入税额,其中税额应该减去单价格小于税额部分(当减到0或负数时,则不提示输入额度了)
				msed_max = ManipulatePrecision.doubleConvert(gwk.xe - je);
			}
			else
			{
				msed_max = 0;
			}

			if (msed_max <= 0)
			{
				//当<=0时,无须提示输入可用免税额度了
				msed = 0;
			}
			else
			{
				msed_max = ManipulatePrecision.doubleConvert(msed_max);
				if (2 == 2)//wangyong add by 2013.10.15 启用提示是否使用额度 for lihuifeng yans chenyihuan
				{
					int retMessage = new MessageBox("可用免税额度：" + msed_max + "\n\n是否使用？", null, true).verify();

					if (retMessage == GlobalVar.Key1)
					{//当选择可用时
						msed = msed_max;
					}
					else
					{
						msed = 0;
					}
				}
				else
				{
					//不弹出窗口，直接默认窗口里选"是",且税金与按了“是”按钮的功能一样 for yans by wangyong 2013.2.25
					msed = msed_max;
				}
			}
		}

		return calcStr('1', msed);
	}
	
	private double getMSDE()
	{
		double msed = 0;//当前要使用的免税税金
		try
		{

			//'1.获取当前所有(免税)商品的成交价
			//'2.当应收金额大于免税税额时,则提示输入税额,其中税额应该减去单价格小于税额部分 (结果要>=0)
			double msed_max = 0;//当前可使用的免税税金
			//double msed = 0;//当前要使用的免税税金
			double ysje = 0;//所有商品的成交价
			double je = 0;//所有单价小于 可用免税税额 的商品成交价
			SaleGoodsDef sg;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg == null) continue;

				ysje = ysje + ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
				if (ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / sg.sl) <= gwk.xe)
				{
					je = je + ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk);
				}

			}
			if (1 == 2)//参数待加
			{
				//参数控制"是否弹免税额度的提示框",且"商品成交金额与卡当前可用免税额度,两者取低" for yans by 2013.1.30 春节之后用
				if (ysje > gwk.xe)
				{
					msed = gwk.xe;
				}
			}
			else
			{
				if (ysje > gwk.xe)
				{
					//当应收金额大于免税税额时,则提示输入税额,其中税额应该减去单价格小于税额部分(当减到0或负数时,则不提示输入额度了)
					msed_max = ManipulatePrecision.doubleConvert(gwk.xe - je);
				}
				else
				{
					msed_max = 0;
				}

				if (msed_max <= 0)
				{
					//当<=0时,无须提示输入可用免税额度了
					msed = 0;
				}
				else
				{
					msed_max = ManipulatePrecision.doubleConvert(msed_max);
					if (2 == 2)//wangyong add by 2013.10.15 启用提示是否使用额度 for lihuifeng yans chenyihuan
					{
						int retMessage = new MessageBox("可用免税额度：" + msed_max + "\n\n是否使用？", null, true).verify();

						if (retMessage == GlobalVar.Key1)
						{//当选择可用时
							msed = msed_max;
						}
						else
						{
							msed = 0;
						}
					}
					else
					{
						//不弹出窗口，直接默认窗口里选"是",且税金与按了“是”按钮的功能一样 for yans by wangyong 2013.2.25
						msed = msed_max;
					}
				}
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return msed;
	}

	/**
	 * 拼装发送的限额字符串
	 * @param type [type=1-超额商品,2-非超额商品]
	 * @return
	 */
	private String calcStr(char type)
	{
		return calcStr(type, 0);
	}

	/**
	 * 拼装发送的限额字符串
	 * @param type [type=1-超额商品,2-非超额商品, 3即购即提商品ALL]
	 * @param msed 免税额度(当type=1时使用)
	 * @return
	 */
	private String calcStr(char type, double msed)
	{
		String strRet = "";
		try
		{
			SaleGoodsDef sg;
			int flag = 0;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg == null) continue;

				if (type == '1')
				{
					//行号,商品代码,商品条码,数量,成交价,免税额度,重量|..
					if (ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / sg.sl) > gwk.sxje)
					{
						if (flag > 0) strRet = strRet + "|";
						strRet = strRet + String.valueOf(i) + "," + sg.code + "," + sg.barcode + "," + String.valueOf(sg.sl) + ","
								+ String.valueOf(ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk)) + "," + String.valueOf(msed)
								+  "," + String.valueOf(sg.num13);
						flag++;
					}
				}
				else if (type == '3')
				{//即购即提商品
					if (flag > 0) strRet = strRet + "|";
					strRet = strRet + String.valueOf(i) + "," + sg.code + "," + sg.barcode + "," + String.valueOf(sg.sl) + ","
							+ String.valueOf(ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk)) + "," + String.valueOf(msed)
							+  "," + String.valueOf(sg.num13);
					flag++;
				}
				else
				{
					//行号,商品代码,商品条码,数量,成交价,重量|..
					if (ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk) / sg.sl) <= gwk.sxje)
					{
						if (flag > 0) strRet = strRet + "|";
						strRet = strRet + String.valueOf(i) + "," + sg.code + "," + sg.barcode + "," + String.valueOf(sg.sl) + ","
								+ String.valueOf(ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk))
								+  "," + String.valueOf(sg.num13);
						flag++;
					}
				}

			}

			PosLog.getLog(this.getClass().getSimpleName()).info("calcStr(" + type + ") strRet=[" + strRet + "].");
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return strRet;
	}

	//发送商品组合串
	private boolean sendLimitJEStr(String str)
	{
		//代码待加...
		if (!GlobalInfo.isOnline)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr网络连接失败");
			this.showMsg("网络连接失败,无法获取补税金额信息！");
			return false;
		}
		if (str == null || str.trim().length() <= 0)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr拼装限额字符串失败：str=[" + String.valueOf(str) + "]");
			return true;
		}

		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText("正在计算补税金额,请等待...");
			StringBuffer sbList = new StringBuffer();
			if (!dataservice.sendLimitJEStr(str, gwk.zjlb + gwk.passport, sbList))
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr() 获取补税金额失败.");
				showMsg("获取补税金额失败！");
				return false;
			}

			double dblBSJE = 0;
			double dblDncdje = 0;
			double dblCscdje = 0;
			String[] strRetShuiJin = sbList.toString().split("\\|");
			for (int i = 0; i < strRetShuiJin.length; i++)
			{
				String[] strRetList = strRetShuiJin[i].split(",");
				if (strRetList.length >= 10)
				{
					dblBSJE += Convert.toDouble(strRetList[4]);
					dblDncdje += Convert.toDouble(strRetList[6]);
					dblCscdje += Convert.toDouble(strRetList[7]);
					setGoodsBSJE(Convert.toInt(strRetList[0]), strRetList[1], strRetList[2], Convert.toDouble(strRetList[3]),
									Convert.toDouble(strRetList[4]), Convert.toDouble(strRetList[5]), Convert.toDouble(strRetList[6]),
									Convert.toDouble(strRetList[7]), strRetList[8], strRetList[9], Convert.toDouble(strRetList[10]));
				}
				else
				{
					PosLog.getLog(this.getClass().getSimpleName()).info(
																		"sendLimitJEStr：字符串不合法strRetShuiJin[ " + String.valueOf(i) + "]=["
																				+ strRetShuiJin[i] + "]");
				}
			}

			if (dblBSJE > 0)
			{
				dispPay();// 刷新界面====================待加=================================
				showMsg("税金总共：" + ManipulatePrecision.doubleConvert(dblBSJE) + "元\n" + "顾客应交："
						+ ManipulatePrecision.doubleConvert(dblBSJE - dblDncdje - dblCscdje) + "元\n" + "店内承担："
						+ ManipulatePrecision.doubleConvert(dblDncdje + dblCscdje) + "元");
			}

			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
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
	
	//检查是否满足即购即提条件
	private boolean checkJGJTPay()
	{
		try
		{
			if (saleHead.str8!=null && saleHead.str8.length()>=2 && saleHead.str8.charAt(1)=='Y')
			{
				//即购即提
				//判断是否即购即提:成交单价超8K的,不允许即购即提
				SaleGoodsDef sg;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					sg = (SaleGoodsDef) saleGoods.elementAt(i);
					if (sg == null) continue;
					
					double dblGoodsJG = (sg.hjje - sg.hjzk) / sg.sl;
					if (gwk !=null && dblGoodsJG > gwk.sxje)//8K
					{
						PosLog.getLog(this.getClass().getSimpleName()).info("商品成交单价超过8K[" + gwk.sxje + "],所以不进行暂缴税金:code=" + sg.code + ",barcode=" + sg.barcode);
						showMsg("不能即购即提：\n商品[" + sg.code + "][" + sg.name + "]成交单价超过" + gwk.sxje); 
						return false;
					}
								
				}
			}
			
			return true;
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			showMsg("操作失败：检查能否即购即提时异常\n" + ex.getMessage()); 
			return false;
		}
		
	}
	
	//计算税款担保金
	private boolean sendLimitJEStr_ZJSJ(String str)
	{
		if (saleHead.str8!=null && saleHead.str8.length()>=2 && saleHead.str8.charAt(1)=='Y')
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("当前是即购即提模式,开始获取税款担保金信息...");
		}
		else
		{
			//非即购即提时,直接返回,不用计算
			return true;
		}
		/*saleHead.str8 = saleHead.str8.charAt(0) + "N";
		
		//判断是否即购即提:成交单价超8K的,不允许即购即提
		SaleGoodsDef sg;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			sg = (SaleGoodsDef) saleGoods.elementAt(i);
			if (sg == null) continue;
			
			double dblGoodsJG = (sg.hjje - sg.hjzk) / sg.sl;
			if (gwk !=null && dblGoodsJG > gwk.sxje)//8K
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("商品成交单价超过8K,所以不进行暂缴税金:code=" + sg.code + ",barcode=" + sg.barcode);
				return true;
			}
						
		}
		
		//提示是否选择即购即提
		if (new MessageBox("是否即购即提？", null, true).verify() != GlobalVar.Key1)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("未选择即购即提");
			saleHead.str8 = saleHead.str8.charAt(0) + "N";
			return true;
		}*/
		
		if (!GlobalInfo.isOnline)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr_ZJSJ网络连接失败");
			this.showMsg("网络连接失败,无法获取税款担保金信息！");
			return false;
		}
		if (str == null || str.trim().length() <= 0)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr_ZJSJ拼装限额字符串失败：str=[" + String.valueOf(str) + "]");
			return true;
		}

		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText("正在计算税款担保金,请等待...");
			StringBuffer sbList = new StringBuffer();///* 行号,商品代码,商品条码,税率,税金,完税价(*数量),店内承担税额,厂商承担税额,单据号,税号,免税额度,   (此字段以后，为老葛新加) 完税价（不含 *数量）,关税税率Tg,增值税税率Tz,消费税率Tx,HS编码

			if (!dataservice.sendLimitJEStr_ZJSJ(str, gwk.zjlb + gwk.passport, sbList))
			{
				PosLog.getLog(this.getClass().getSimpleName()).info("sendLimitJEStr_ZJSJ() 获取税款担保金失败.");
				showMsg("获取税款担保金失败！");
				return false;
			}

			double dblBSJE = 0;
			//double dblDncdje = 0;
			//double dblCscdje = 0;
			String[] strRetShuiJin = sbList.toString().split("\\|");
			for (int i = 0; i < strRetShuiJin.length; i++)
			{
				String[] strRetList = strRetShuiJin[i].split(",");
				if (strRetList.length >= 15)
				{
					dblBSJE += Convert.toDouble(strRetList[4]);//税金
					//dblDncdje += Convert.toDouble(strRetList[6]);
					//dblCscdje += Convert.toDouble(strRetList[7]);
					setGoodsBSJE_ZJSJ(Convert.toInt(strRetList[0]), strRetList[1], strRetList[2], Convert.toDouble(strRetList[3]),
									Convert.toDouble(strRetList[4]), Convert.toDouble(strRetList[5]), Convert.toDouble(strRetList[6]),
									Convert.toDouble(strRetList[7]), strRetList[8], strRetList[9], Convert.toDouble(strRetList[10]),
									strRetList[11], strRetList[12], strRetList[13], strRetList[14], strRetList[15]);
				}
				else
				{
					PosLog.getLog(this.getClass().getSimpleName()).info(
																		"sendLimitJEStr_ZJSJ：字符串不合法strRetShuiJin[ " + String.valueOf(i) + "]=["
																				+ strRetShuiJin[i] + "]");
				}
			}

			//saleHead.str8 = saleHead.str8.charAt(0) + "Y";
			
			if (dblBSJE > 0)
			{
				dispPay();
				/*showMsg("税金总共：" + ManipulatePrecision.doubleConvert(dblBSJE) + "元\n" + "顾客应交："
						+ ManipulatePrecision.doubleConvert(dblBSJE - dblDncdje - dblCscdje) + "元\n" + "店内承担："
						+ ManipulatePrecision.doubleConvert(dblDncdje + dblCscdje) + "元");*/
				showMsg("顾客选择【即购即提】，需缴税款担保金：" + ManipulatePrecision.doubleConvert(dblBSJE) + "元");
			}

			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
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

	private void showMsg(String msg)
	{
		new MessageBox(msg);
	}

	private void setGoodsBSJE(int curNo, String code, String barcode, double bssl, double bsje, double wsje, double dncdje, double cscdje, String taxdjbh, String catcode, double msed)
	{
		try
		{
			//行邮税店承担,行邮税厂家承担,行邮税单据编号 wangyong add by 2012.3.20
			//免税额度 wangyong add by 2012.9.23
			SaleGoodsDef sg;
			boolean isFind = false;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg == null) continue;

				if (i == curNo && sg.code.equalsIgnoreCase(code) && sg.barcode.equalsIgnoreCase(barcode.trim()))
				{
					sg.num7 = wsje;//完税价
					sg.num3 = bssl;//税率
					sg.num4 = bsje;//(总)税金

					sg.num8 = dncdje;//行邮税店承担
					sg.num10 = cscdje;//行邮税厂家承担
					sg.str10 = taxdjbh;//行邮税单据编号
					sg.str11 = catcode;//税号 wangyong add by 2012.4.16
					sg.num11 = msed;//免税额度 wangyong add by 2012.9.23

					/* '---------注意start
					 '顾客承担 = 税金 - 店内承担税额 - 厂商承担税额
					 '最后的小票应付金额 = 成交价 + 顾客承担
					 '---------注意end*/

					isFind = true;
					PosLog.getLog(this.getClass().getSimpleName()).info(
																		"setGoodsBSJE:设置成功 curNo=[" + String.valueOf(curNo) + "], code=[" + code
																				+ "], barcode=[" + barcode + "],完税价num7=[" + String.valueOf(sg.num7)
																				+ "],税率num3=[" + String.valueOf(sg.num3) + "],税金num4=["
																				+ String.valueOf(sg.num4) + "],行邮税店承担num8=["
																				+ String.valueOf(sg.num8) + "],行邮税厂家承担num10=["
																				+ String.valueOf(sg.num10) + "],行邮税单据编号str10=[" + sg.str10
																				+ "],税号str11=[" + sg.str11 + "],免税额度num11=["
																				+ String.valueOf(sg.num11) + "].");
					break;

				}//end if

			}//end for

			if (isFind == false)
			{
				PosLog.getLog(this.getClass().getSimpleName()).info(
																	"setGoodsBSJE：设置补税金额失败，未找到符合条件的商品 curNo=[" + String.valueOf(curNo) + "], code=["
																			+ code + "], barcode=[" + barcode + "],bsje=["
																			+ String.valueOf(ManipulatePrecision.doubleConvert(bsje)) + "].");
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(ex);
		}
	}
	
	//存储暂缴税金 ADD BY 2014.01.11
	//行号,商品代码,商品条码,税率,税金,完税价（*数量）,店内承担税额,厂商承担税额,单据号,税号,免税额度,   (此字段以后，为老葛新加) 完税价（不含 *数量）,关税税率Tg,增值税税率Tz,消费税率Tx
	private void setGoodsBSJE_ZJSJ(int curNo, String code, String barcode, double bssl, double bsje, double wsje, double dncdje, double cscdje, String taxdjbh, String catcode, double msed
									, String wsje_D, String Tg, String Tz, String Tx, String Hs)
	{
		try
		{
			//行邮税店承担,行邮税厂家承担,行邮税单据编号 wangyong add by 2012.3.20
			//免税额度 wangyong add by 2012.9.23
			SaleGoodsDef sg;
			boolean isFind = false;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				sg = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sg == null) continue;

				if (i == curNo && sg.code.equalsIgnoreCase(code) && sg.barcode.equalsIgnoreCase(barcode.trim()))
				{
					sg.num7 = 0;//完税价 wsje
					sg.num3 = 0;//税率 bssl
					sg.num4 = 0;//(总)税金 bsje

					sg.num8 = 0;//行邮税店承担 dncdje
					sg.num10 = 0;//行邮税厂家承担 cscdje
					sg.str10 = "";//行邮税单据编号 taxdjbh
					sg.str11 = "";//税号 catcode 
					sg.num11 = 0;//免税额度 msed 
					

					sg.num12 = bsje;//即购即提_补税税额(总税金) 对应num4
					sg.str6 = bsje + "|" + bssl + "|" + wsje + "|" + msed + "|" + wsje_D + "|" + Tg + "|" + Tz + "|" + Tx + "|" + Hs;//即购即提字段值(代缴税金|代缴税率|完税（价）金额|使用免税额度)|完税价（不含 *数量）,关税税率Tg,增值税税率Tz,消费税率Tx,HS编码

					/* '---------注意start
					 '顾客承担 = 税金 - 店内承担税额 - 厂商承担税额
					 '最后的小票应付金额 = 成交价 + 顾客承担
					 '---------注意end*/

					isFind = true;
					PosLog.getLog(this.getClass().getSimpleName()).info(
																		"setGoodsBSJE_ZJSJ:设置成功 *curNo=[" + String.valueOf(curNo) + "], *code=[" + code
																				+ "], *barcode=[" + barcode + "],*完税价wsje=[" + String.valueOf(wsje)
																				+ "],*str6=[" + sg.str6 + "],*税金bsje=["
																				+ String.valueOf(bsje) + "],行邮税店承担dncdje=["
																				+ String.valueOf(dncdje) + "],行邮税厂家承担cscdje=["
																				+ String.valueOf(cscdje) + "],行邮税单据编号taxdjbh=[" + taxdjbh
																				+ "],税号catcode=[" + catcode + "],*免税额度msed=["
																				+ String.valueOf(msed) + "],完税价（不含 *数量）wsje_D=[" + wsje_D + "]"
																				+ ",关税税率Tg=[" + Tg + "],增值税税率Tz=[" + Tz + "],消费税率Tx=[" + Tx + "],编码Hs=[" + Hs + "],JGJT_JE=[" + String.valueOf(sg.num12) + "].");
					break;

				}//end if

			}//end for

			if (isFind == false)
			{
				PosLog.getLog(this.getClass().getSimpleName()).info(
																	"setGoodsBSJE_ZJSJ：设置补税金额失败，未找到符合条件的商品 curNo=[" + String.valueOf(curNo) + "], code=["
																			+ code + "], barcode=[" + barcode + "],bsje=["
																			+ String.valueOf(ManipulatePrecision.doubleConvert(bsje)) + "].");
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(ex);
		}
	}

	//小票汇总
	public boolean saleSummary()
	{
		////行邮税 待加
		return super.saleSummary();
	}

	public boolean allowFinishFindGoods(GoodsDef goodsDef, double quantity, double price)
	{
		try
		{
			if (super.allowFinishFindGoods(goodsDef, quantity, price))
			{/*
			 //市内店,是否控制限额,免税品款机,退货不控制限额
			 if (!SellType.ISSALE(saletype) 
			 || !checkFreeTaxSYJ() 
			 || (gwk!=null && gwk.ispdxe!=null && !gwk.ispdxe.trim().equalsIgnoreCase("Y"))) return true;

			 if (getAllGoodsJG() > gwk.xe)
			 {
			 if (this.saleGoods.size() <= 1 && quantity <= 1
			 && ManipulatePrecision.doubleConvert(gwk.xe) == ManipulatePrecision.doubleConvert(gwk.sxje))
			 {
			 //第一个商品且数量为1时，不提示
			 }
			 else
			 {
			 //仅仅给出提示
			 this.showMsg("提示:当前购买的商品总额已经超过购物卡限额!" + "\n卡余额:" + ManipulatePrecision.doubleConvert(gwk.xe) + "\n已超额:"
			 + ManipulatePrecision.doubleConvert(getAllGoodsJG() - gwk.xe));
			 }
			 }
			 else
			 {
			 if (!checkMoreGoodsSL())
			 {
			 //仅仅给出提示
			 this.showMsg("提示:当前购买的超限商品总数已经超过购物卡的限量!" + "\n卡限购数量:" + ManipulatePrecision.doubleConvert(gwk.bsjs) + "\n超限额标准:"
			 + ManipulatePrecision.doubleConvert(gwk.sxje));
			 }
			 }*/

				return true;

			}
			return false;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(ex);
			this.showMsg("检查商品时发生异常：" + ex.getMessage());
			return false;
		}
	}

	//销售打印小票
	public void printSaleBill()
	{
		if (checkSale(saleHead.syjh, saleHead.fphm))
		{
			super.printSaleBill();

			//录入提货单号
			inputTHD(saleHead.syjh, saleHead.fphm, saleHead.djlb);
		}
		else
		{
			this.showMsg("当前小票交易失败或不存在,无法打印!");
		}

	}

	/**
	 * (销售后)打印前,先判断当前小票是否存在,以免删除的小票也被打印了
	 * @param syjh
	 * @param fphm
	 * @return
	 */
	public boolean checkSale(String syjh, long fphm)
	{
		try
		{
			Zmsy_AccessDayDB day = (Zmsy_AccessDayDB) AccessDayDB.getDefault();
			return day.checkSale(syjh, fphm);
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(ex);
			return false;
		}
	}

	//重印小票
	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, SaleCustDef saleCust, boolean isRed)
	{
		super.printSaleTicket(vsalehead, vsalegoods, vsalepay, saleCust, isRed);

		//录入提货单号
		inputTHD(vsalehead.syjh, vsalehead.fphm, vsalehead.djlb);
	}

	//按付款键时，查找原小票购物卡信息
	public boolean getBackSaleCustomerInfo()
	{
		return true;
	}

	public boolean getBackSaleGwkInfo(GwkDef gwkTH)
	{

		//按原单退货时，获取原小票的购物卡	
		if (SellType.ISBACK(saletype))
		{
			if (thSyjh != null && thSyjh.trim().length() > 0 && thFphm > 0)
			{
				//获取原小票的购物卡信息
				boolean blnRet = netservice.findGwkInfo_TH(thSyjh, thFphm, gwkTH);
				if (!blnRet)
				{
					//当免税机型时，若获取原购物卡信息失败时，则不允许通过
					if (checkFreeTaxSYJ())
					{
						PosLog.getLog(this.getClass().getSimpleName()).info(
																			"getBackSaleCustomerInfo(thSyjh=[" + thSyjh + "],thFphm=["
																					+ String.valueOf(thFphm) + "])免税款机[" + saleHead.syjh
																					+ "]，获取原小票的购物卡信息失败");
						showMsg("获取原小票的购物卡信息失败！");
						return blnRet;
					}
					else
					{
						PosLog.getLog(this.getClass().getSimpleName()).info(
																			"getBackSaleCustomerInfo(thSyjh=[" + thSyjh + "],thFphm=["
																					+ String.valueOf(thFphm) + "])有税款机[" + saleHead.syjh
																					+ "]，获取原小票的购物卡信息失败");
					}

				}
				else
				{
					//记录原购物卡信息（小票打印用）

				}
			}

		}
		return true;
	}

	public void setBackSaleGwkInfo(GwkDef gwk, GwkDef gwkTH)
	{
		try
		{
			if (gwk == null || gwkTH == null) return;

			gwk.zkl = gwkTH.zkl;
			gwk.name = gwkTH.name;
			gwk.nation = gwkTH.nation;
			gwk.passport = gwkTH.passport;
			gwk.ljhb = gwkTH.ljhb;
			gwk.ljrq = gwkTH.ljrq;
			gwk.ljsj = gwkTH.ljsj;
			gwk.thdd = gwkTH.thdd;
			if (gwkTH.code != null && gwkTH.code.length() > 0)
			{
				gwk.zjlb = String.valueOf(gwkTH.code.charAt(0));
				//gwk.code = gwkTH.code.substring(1);
			}
			if (gwk.zjlb == null) gwk.zjlb = "";

			gwk.sjcd = gwkTH.sjcd;
			gwk.ispdxe = gwkTH.ispdxe;

			saleHead.str6 = gwk.zjlb + gwk.passport;//购物卡号（证件类型+证件号）
			saleHead.str7 = gwk.sjcd;//税金承担(1为中免承担,2为顾客承担)

			recordGwkInfo();
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).info(ex);
		}
	}

	//	 获取退货小票信息
	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if (GlobalInfo.sysPara.inputydoc == 'D')
			{
				//只记录原单小票号和款机号,但不按原单找商品				
				return false;
			}

			// 如果是新指定小票进入
			if (saletype.equals(SellType.JDXX_BACK)
					|| ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods
																																							.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText("开始查找退货小票操作.....");
				GwkDef gwkTH = null;
				boolean blnResult = DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment);
				if (blnResult)
				{
					//有税款机与免税机不能互退
					if (thsaleHead.str8 == null || thsaleHead.str8.trim() == "") thsaleHead.str8 = "NN";
					if (saleHead.str8.charAt(0)!=thsaleHead.str8.charAt(0))//!saleHead.str8.equalsIgnoreCase(thsaleHead.str8)
					{
						String msg = "";
						if (saleHead.str8.equalsIgnoreCase("Y")) msg = "[免税款机]不能退[有税款机]的小票！";
						else msg = "[有税款机]不能退[免税款机]的小票！";
						new MessageBox(msg);
						blnResult = false;
					}
					else
					{
						if (thsaleHead.str6 != null && thsaleHead.str6.trim().length() > 0)//原小票的购物卡号
						{
							//原小票已刷购物卡
							//查找原小票的购物卡信息
							gwkTH = new GwkDef();
							blnResult = getBackSaleGwkInfo(gwkTH);
						}
					}

				}
				if (!blnResult)
				{
					pb.close();
					pb = null;

					thSyjh = null;
					thFphm = 0;

					return false;
				}

				pb.close();
				pb = null;
				//检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				//Y为已在后台退回礼品   津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					thSyjh = null;
					thFphm = 0;
					new MessageBox("此小票有满赠礼品，请先到后台退回礼品再办理退货！");
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					//if (new MessageBox(thsaleHead.str1 + "\n是否继续退货？", null, true).verify() != GlobalVar.Key1) { return false; }
					if (new MessageBox(thsaleHead.str1 + "\n是否继续退货？", null, true).verify() != GlobalVar.Key1)
					{
						thSyjh = null;
						thFphm = 0;
						//new MessageBox("操作失败：" + thsaleHead.str1);
						return false;
					}

				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox("原小票是[" + SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead)
								+ "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { "序", "商品编码", "商品名称", "原数量", "原折扣", "原成交价", "退货", "退货数量" };
				int[] width = { 30, 100, 170, 80, 80, 100, 60, 100, 55 };
				String[] row = null;
				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					row = new String[8];
					row[0] = String.valueOf(sgd.rowno);

					if (sgd.inputbarcode.equals(""))
					{
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A")) sgd.inputbarcode = sgd.barcode;
						row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B")) sgd.inputbarcode = sgd.code;
						row[1] = sgd.code;
					}
					else
					{
						row[1] = sgd.inputbarcode;
					}

					row[2] = sgd.name;
					row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
					row[4] = ManipulatePrecision.doubleToString(sgd.hjzk);
					row[5] = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk);
					row[6] = "";
					row[7] = "";
					if (checkFreeTaxSYJ())
					{
						row[6] = "Y";
						row[7] = row[3];
					}
					choice.add(row);
				}

				String[] title1 = { "序", "付款名称", "账号", "付款金额" };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				int cho = -1;
				if (EBill.getDefault().isEnable() && EBill.getDefault().isBack())
				{
					cho = EBill.getDefault().getChoice(choice);
				}
				else
				{
					if (checkFreeTaxSYJ())
					{
						//免税款机，默认全选，且不允许修改
						cho = 0;
					}
					else
					{
						// 选择要退货的商品
						cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true,
														true, 7, true, 750, 130, title1, width1, content2, 0);
					}
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open("开单营业员号：", "", "请输入有效开单营业员号", backYyyh, 0);
					//						 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox("该工号不是营业员!", null, false);
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}

				}

				// 如果cho小于0且已经选择过退货小票
				if (cho < 0 && isbackticket) return true;
				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 清除已有商品明细,重新初始化交易变量

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();
				if (gwkTH != null) setBackSaleGwkInfo(this.gwk, gwkTH);

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y")) continue;

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					sgd.str4 = backYyyh.toString();
					sgd.ysl = sgd.sl;

					// 重算商品行折扣
					if (ManipulatePrecision.doubleCompare(sgd.sl, thsl, 4) > 0)
					{
						sgd.hjje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hjje, sgd.sl), thsl), 2, 1); // 合计金额
						sgd.hyzke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl), thsl), 2,
																		1); // 会员折扣额(来自会员优惠)
						sgd.yhzke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl), thsl), 2,
																		1); // 优惠折扣额(来自营销优惠)
						sgd.lszke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl), thsl), 2,
																		1); // 零时折扣额(来自手工打折)
						sgd.lszre = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl), thsl), 2,
																		1); // 零时折让额(来自手工打折)
						sgd.lszzk = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl), thsl), 2,
																		1); // 零时总品折扣
						sgd.lszzr = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl), thsl), 2,
																		1); // 零时总品折让
						sgd.plzke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl), thsl), 2,
																		1); // 批量折扣
						sgd.zszke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl), thsl), 2,
																		1); // 赠送折扣
						sgd.cjzke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl), thsl), 2,
																		1); // 厂家折扣
						sgd.ltzke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl), thsl), 2,
																		1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl), thsl),
																		2, 1);
						sgd.qtzke = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl), thsl), 2,
																		1);
						sgd.qtzre = ManipulatePrecision
														.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl), thsl), 2,
																		1);
						sgd.hjzk = getZZK(sgd);
						sgd.sl = thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
				}

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';

					/*
					 * 业务过程只支持磁道查询,不支持卡号查询,因此无法检查原交易会员卡是否有效 if
					 * (!DataService.getDefault().getCustomer(curCustomer,
					 * thsaleHead.hykh)) { curCustomer.code = thsaleHead.hykh;
					 * curCustomer.name = "无效卡"; curCustomer.ishy = 'Y';
					 * 
					 * new MessageBox("原交易的会员卡可能已失效!\n请重新刷卡后进行退货"); }
					 */
				}

				// 设置原小票头信息
				saleHead.hykh = thsaleHead.hykh;
				saleHead.hytype = thsaleHead.hytype;
				saleHead.jfkh = thsaleHead.jfkh;

				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.salefphm = thsaleHead.salefphm;
				saleHead.str8 = thsaleHead.str8;

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				// 为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox("超出退货的最大限额，不能退货");

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
						}
					}
				}

				backPayment.removeAllElements();
				backPayment.addAll(thsalePayment);

				// 刷新界面显示
				saleEvent.clearTableItem();
				saleEvent.updateSaleGUI();

				if(isbackticket && saleHead.str8!=null && saleHead.str8.charAt(0)=='Y')
				{
					//当时即购即提时,则给出提示
					if ((saleHead.str8 + "N").charAt(1)=='Y')
					{
						new MessageBox("提示:当前退货的小票为【即购即提】");
					}					
				}
				
				return isbackticket;
			}

			return false;
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}
	}

	public boolean refreshSaleData()
	{
		boolean blnRet = false;
		try
		{
			blnRet = super.refreshSaleData();
			PosLog.getLog(this.getClass().getSimpleName()).info("refreshSaleData ");
			if (saleHead != null)
			{
				if (saleHead.str8!=null && saleHead.str8.length()>0)
				{
					//若已经存在，则不再赋值
					PosLog.getLog(this.getClass().getSimpleName()).info("refreshSaleData saleHead.str8=[" + saleHead.str8 + "]");
				}
				else
				{
					if (checkFreeTaxSYJ())
					{
						saleHead.str8 = "YN";//是否免税款机
					}
					else
					{
						saleHead.str8 = "NN";
					}
				}
				

				if (saleHead.zmsy_gwk != null)
				{
					gwk = (GwkDef) saleHead.zmsy_gwk;
					gwk.syjh = GlobalInfo.syjDef.syjh;
					gwk.syyh = GlobalInfo.posLogin.gh;
					gwk.fphm = saleHead.fphm;

					saleHead.str6 = gwk.zjlb + gwk.passport;//购物卡号（证件类型+证件号）
					saleHead.str7 = gwk.sjcd;//税金承担(1为中免承担,2为顾客承担)					
					if (gwk.str4.equalsIgnoreCase("Y"))
					{
						isUseGwkZkl = true;//使用卡折扣率
					}
					else
					{
						isUseGwkZkl = false;//不使用卡折扣率(可能使用手工券)
					}
				}
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return blnRet;
	}

	public Vector getPayModeBySuper(String sjcode, StringBuffer index, String code)
	{
		Vector child = new Vector();
		String[] temp = null;
		PayModeDef mode = null;
		int k = -1;
		for (int i = 0; i < GlobalInfo.payMode.size(); i++)
		{
			mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

			//当使用卡折扣率时,则不显示手工券付款方式
			if (isUseGwkZkl && GlobalInfo.sysPara.sgQuan_paycode.length() > 0 && mode.code.trim().length() > 0
					&& ("," + GlobalInfo.sysPara.sgQuan_paycode + ",").indexOf("," + mode.code.trim() + ",") >= 0)
			{
				continue;
			}

			if ((mode.sjcode.trim().equals(sjcode.trim()) || (sjcode.equals("0") && mode.sjcode.trim().equals(mode.code))) && getPayModeByNeed(mode))
			{
				k++;

				// 标记code付款方式在vector中的位置
				if (index != null && code != null && mode.code.compareTo(code) == 0)
				{
					index.append(String.valueOf(k));
				}

				//
				if (GlobalInfo.sysPara.salepayDisplayRate == 'Y')
				{
					temp = new String[3];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					temp[2] = ManipulatePrecision.doubleToString(mode.hl, 4, 1, false);
				}
				else
				{
					temp = new String[2];
					temp[0] = mode.code.trim();
					temp[1] = mode.name;
					if (mode.hl != 1) temp[1] = temp[1] + "<" + ManipulatePrecision.doubleToString(mode.hl, 4, 1, false) + ">";
				}
				child.add(temp);
			}
		}

		return child;
	}

	public boolean addSaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		boolean blnRet = super.addSaleGoods(goodsDef, yyyh, quantity, price, allprice, dzcm);
		if (blnRet)
		{
			//市内店,是否控制限额,免税品款机,退货不控制限额
			if (!SellType.ISSALE(saletype) || !checkFreeTaxSYJ() || (gwk != null && gwk.ispdxe != null && !gwk.ispdxe.trim().equalsIgnoreCase("Y"))) return true;

			boolean isShowMsg = false;
			if (getAllGoodsJG() > gwk.xe)
			{//当单价<=8K的所有商品成交价之合,大于卡余额时,则如下判断:
				if (this.saleGoods.size() <= 1 && quantity <= 1
						&& ManipulatePrecision.doubleConvert(gwk.xe) == ManipulatePrecision.doubleConvert(gwk.sxje))
				{
					//第一个商品且数量为1时，不提示
				}
				else
				{
					//仅仅给出提示
					isShowMsg=true;
					this.showMsg("提示:当前购买的商品总额已经超过购物卡限额!" + "\n卡余额:" + ManipulatePrecision.doubleConvert(gwk.xe) + "\n已超额:"
							+ ManipulatePrecision.doubleConvert(getAllGoodsJG() - gwk.xe));
				}
			}
			else
			{
				if (!checkMoreGoodsSL())
				{//单价>8K的所有商品数量之合,大于"可补税件数"时,则如下判断:				
					//仅仅给出提示
					isShowMsg=true;
					this.showMsg("提示:当前购买的超限商品总数已经超过购物卡的限量!" + "\n卡限购数量:" + ManipulatePrecision.doubleConvert(gwk.bsjs) + "\n超限额标准:"
							+ ManipulatePrecision.doubleConvert(gwk.sxje));
				}
			}
			if (!isShowMsg)
			{
				//当没有提示时,则检查即购即提
				checkJGJTPay();
			}
		}
		return blnRet;
	}

	// 新CRM满减促销, (免税品折前单价大于8000的,不参与满减 wangyong by 2013.9.25)
	public boolean doCrmPop()
	{
		boolean haveCrmPop = false;

		//清空，放满减描述
		saleHead.str2 = "";

		// 默认总是不进行分摊付款的
		apportionPay = false;

		// 先总是无满减规则方式的付款
		isPreparePay = payNormal;

		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) return false;

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		// 先进行直接打折
		int i = 0;
		double hjzszk = 0;
		for (i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(i);
			double zkl = ((GoodsDef) goodsAssistant.elementAt(i)).maxzkl;

			// 不计算换购商品
			if (((SpareInfoDef) goodsSpare.elementAt(i)).char2 == 'Y') continue;

			if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);

			if (mjrule.charAt(0) == '1')
			{
				double sj = saleGoodsDef.hjje - getZZK(saleGoodsDef);
				double dz = ManipulatePrecision.mul(sj, goodsPop1.poplsjzkl);

				double minje = saleGoodsDef.hjje * zkl;

				if (dz < minje)
				{
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, minje);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				}
				else
				{
					saleGoodsDef.zszke = ManipulatePrecision.sub(sj, dz);
					saleGoodsDef.zszkfd = goodsPop1.poplsjzkfd;
					saleGoodsDef.zsdjbh = goodsPop1.djbh;
				}
				// 计算价格精度
				if (saleGoodsDef.zszke > 0) saleGoodsDef.zszke = getConvertRebate(i, saleGoodsDef.zszke);

				getZZK(saleGoodsDef);
				hjzszk += saleGoodsDef.zszke;

				haveCrmPop = true;
			}
		}

		if (hjzszk > 0)
		{
			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

			new MessageBox("有商品参加活动促销，总共可打折 " + ManipulatePrecision.doubleToString(hjzszk));
		}

		// 在VIP促销需要除券计算模式下，计算VIP前先提示输入券付款
		boolean vippaycw = false;
		if (GlobalInfo.sysPara.vipPayExcp == 'Y' && checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y'
				&& GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
		{
			// 提示先输入券付款
			if (new MessageBox("券付款不参与VIP折扣,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
			{
				// 开始预付除外付款方式
				isPreparePay = payPopPrepare;

				// 打开付款窗口
				new SalePayForm().open(saleEvent.saleBS);

				// 付款完成，开始新交易
				if (this.saleFinish)
				{
					sellFinishComplete();

					// 预先付款就已足够,不再继续后续付款
					doRulePopExit = true;
					return false; // 表示没有满减促销,取消付款时无需恢复
				}
			}

			// 进入实付剩余付款方式,只允许非券付款方式进行付款
			isPreparePay = payPopOther;

			// 标记已输入除外付款，后面满减时不再输入除外付款
			vippaycw = true;
		}

		// 如果为VIP折扣区间的打折方式，在满减前计算    	
		if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y' && GlobalInfo.sysPara.vipPromotionCrm != null
				&& GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
		{
			// vipzk2表示按下付款键时才计算VIP折扣
			for (int k = 0; k < saleGoods.size(); k++)
			{
				getVIPZK(k, vipzk2);
			}

			// 重算小票应收  
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

			//显示会员卡折扣总金额
			if (saleHead.hyzke > 0) new MessageBox("会员折扣总金额 ：" + saleHead.hyzke);
		}

		// 检查促销折扣控制 如果低于折扣率,不进行满减,返券,返礼促销
		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(j);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(j);
			double zkl = 0;
			if (saleGoodsDef.hjje != 0)
			{
				zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);
			}

			if (zkl < goodsPop1.pophyjzkfd || 
					((checkFreeTaxSYJ() && gwk != null && saleGoodsDef.jg > gwk.sxje) && (gwk.ispdxe == null || gwk.ispdxe.equalsIgnoreCase("N") == false)))//wangyong by 2013.9.25 折前单价大于8000的免税品不参与满减;  离境顾客(ispdxe=N)除外(不判断8000) FOR yans 2013.11.25
			{

				if (mjrule.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(mjrule);
					for (int z = 2; z < buff.length(); z++)
					{
						buff.setCharAt(z, '0');
					}
					mjrule = buff.toString();
				}
				else
				{
					mjrule = mjrule.charAt(0) + "000";
				}
				((SpareInfoDef) goodsSpare.elementAt(j)).str1 = mjrule;

				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') saleGoodsDef.str3 = mjrule
						+ String.valueOf(Convert.increaseInt(goodsPop1.yhspace, 5).substring(4))
						+ saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
				else saleGoodsDef.str3 = mjrule + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
			}
		}

		// 检查是否需要分摊
		for (int j = 0; j < saleGoods.size(); j++)
		{
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(j)).str1;
			if (mjrule.charAt(0) == '9' && mjrule.length() > 3 && mjrule.charAt(2) == '1') apportionPay = true;
			if (mjrule.charAt(1) == '1') apportionPay = true;
			if (apportionPay)
			{
				break;
			}
		}

		// 再查找是否存在满减或减现
		int j = 0;
		Vector set = new Vector();
		CalcRulePopDef calPop = null;

		// 先按商品分组促销规则
		for (i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			GoodsDef goodsDef = ((GoodsDef) goodsAssistant.elementAt(i));
			GoodsPopDef goodsPop = (GoodsPopDef) crmPop.elementAt(i);
			String mjrule = ((SpareInfoDef) goodsSpare.elementAt(i)).str1;
			String ruleCode = goodsDef.specinfo;

			if (mjrule.charAt(0) == '9') mjrule = mjrule.substring(1);
			// 选择了不参与减现继续下一个商品
			if (mjrule.equals("N") || (mjrule.charAt(1) != '1'))
			{
				continue;
			}

			// 查找是否相同促销规则
			for (j = 0; j < set.size(); j++)
			{
				calPop = (CalcRulePopDef) set.elementAt(j);

				int oldIndex = Integer.parseInt((String) calPop.row_set.elementAt(0));
				SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(oldIndex);
				GoodsDef goodsDef1 = ((GoodsDef) goodsAssistant.elementAt(oldIndex));
				GoodsPopDef goodsPop1 = (GoodsPopDef) crmPop.elementAt(oldIndex);
				String mjrule1 = ((SpareInfoDef) goodsSpare.elementAt(oldIndex)).str1;

				if (mjrule1.charAt(0) == '9') mjrule1 = mjrule1.substring(1);
				// 判断是否为同规则促销
				if (isSamePop(saleGoodsDef, goodsDef, goodsPop, mjrule, saleGoodsDef1, goodsDef1, goodsPop1, mjrule1))
				{
					calPop.row_set.add(String.valueOf(i));

					break;
				}
			}

			if (j >= set.size())
			{
				calPop = new CalcRulePopDef();
				calPop.code = saleGoodsDef.code;
				calPop.gz = saleGoodsDef.gz;
				calPop.uid = saleGoodsDef.uid;
				calPop.rulecode = ruleCode;
				calPop.catid = saleGoodsDef.catid;
				calPop.ppcode = saleGoodsDef.ppcode;
				calPop.popDef = goodsPop;
				calPop.row_set = new Vector();
				calPop.row_set.add(String.valueOf(i));
				set.add(calPop);
			}
		}

		// 无规则促销
		if (set.size() <= 0) { return haveCrmPop; }

		// 满减前先对所有商品进行舍分处理
		this.calcSellPayMoney(true);

		// 引用促销规则集合，用于付款分摊时进行判断，只有一个规则自动平摊到每个商品
		rulePopSet = set;

		// 检查是否要除券
		boolean havepaycw = false;
		for (i = 0; i < set.size(); i++)
		{
			calPop = (CalcRulePopDef) set.elementAt(i);

			if (calPop.popDef.catid.equals("Y"))
			{
				havepaycw = true;
				break;
			}
		}

		// 前面已经进行了VIP除外付款输入,不再输入除外付款
		if (vippaycw) havepaycw = false;

		// 循环两次
		// 第一次先检查是否有满足条件的规则,如果没有则直接返回
		// 第二次检查除券外是否还有满足条件的规则,如果不需要除券,则只用循环一次
		int nwhile = 1;
		do
		{
			// 开始计算商品分组参与计算的合计金额
			for (i = 0; i < set.size(); i++)
			{
				// 如果是能进入第二次循环,说明有交易金额是满足促销条件的规则促销
				// 如果需要扣除券付款,先输入券付款方式
				if ((nwhile >= 2) && havepaycw)
				{
					// 提示先输入券付款
					if (GlobalInfo.sysPara.mjPaymentRule.trim().length() > 0
							&& new MessageBox("本笔交易有活动促销,请先输入券付款金额\n\n如果顾客没有券付款,请直接按‘退出’键").verify() != GlobalVar.Exit)
					{
						// 开始预付除外付款方式
						isPreparePay = payPopPrepare;

						// 打开付款窗口
						new SalePayForm().open(saleEvent.saleBS);

						// 付款完成，开始新交易
						if (this.saleFinish)
						{
							sellFinishComplete();

							// 预先付款就已足够,不再继续后续付款
							doRulePopExit = true;
							return false;
						}
					}

					// 进入实付剩余付款方式,只允许非券付款方式进行付款
					isPreparePay = payPopOther;

					// 券除外付款只输入一次
					havepaycw = false;
				}

				// 计算同规则商品参与促销的合计
				calPop = (CalcRulePopDef) set.elementAt(i);
				double sphj = 0;
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					sphj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef));
				}

				// 如果只有一组促销规则,计算前存在的付款方式都算需要除外的付款
				// 如果有多个组促销规则,除外金额为该商品已分摊的付款金额
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					if (spinfo.payft == null) continue;
					for (int n = 0; n < spinfo.payft.size(); n++)
					{
						String[] s = (String[]) spinfo.payft.elementAt(n);

						if (!calPop.popDef.catid.equals("Y"))
						{
							String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");
							int x = 0;
							for (x = 0; x < pay.length; x++)
							{
								if (s[1].equals(pay[x].trim()))
								{
									break;
								}
							}

							if (x >= pay.length) sphj -= Convert.toDouble(s[3]);
						}
						else
						{
							sphj -= Convert.toDouble(s[3]);
						}
					}
				}

				if (sphj <= 0)
				{
					set.remove(i);
					i--;
					continue;
				}

				// 满减限额
				double limitje = 0;
				if (calPop.popDef.sl <= 0) limitje = 99999999;
				else limitje = calPop.popDef.sl;

				// 检查是否满足条件
				if (calPop.popDef.gz.equals("1")) // 按金额满减
				{
					double mjje = 0;
					calPop.popje = sphj;

					int num = 0;

					// 已参与满减的金额
					double yfmj = 0;

					if (GlobalInfo.sysPara.mjtype == 'Y')
					{
						//检查是否存在促销条件,现在全部的条件都在此地设定 用分号分隔 
						if ((calPop.popDef.str3 != null) && (calPop.popDef.str3.trim().length() > 0))
						{
							String[] row = calPop.popDef.str3.split(";");

							for (int c = row.length - 1; c >= 0; c--)
							{
								if ((row[c] == null) || (row[c].split(",").length != 4))
								{
									continue;
								}

								double a = Convert.toDouble(row[c].split(",")[0]); //参加下限
								double b = Convert.toDouble(row[c].split(",")[1]); //参加上限
								double t = Convert.toDouble(row[c].split(",")[2]); //满减条件
								double je = Convert.toDouble(row[c].split(",")[3]); //满减金额

								if ((je == 0) || (b == 0))
								{
									continue;
								}

								if ((ManipulatePrecision.doubleConvert(sphj - yfmj) >= a) && (ManipulatePrecision.doubleConvert(sphj - yfmj) <= b))
								{
									// 如果满减条件为0，直接取定义的满减金额
									if (t == 0)
									{
										if (je < limitje)
										{
											mjje = je;
										}
										else
										{
											mjje = limitje;
										}

										break;
									}

									//浮点运算1       = 0.999999,需要进位到两位小数再取整
									//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
									num = ManipulatePrecision.integerDiv(sphj - yfmj, t);

									double bcje = 0;
									if (num > 0)
									{
										bcje = num * je;
									}

									if (bcje > limitje)
									{
										bcje = limitje;
									}

									mjje += bcje;
									yfmj = ManipulatePrecision.doubleConvert(num * t + yfmj);
									if (mjje >= limitje || (GlobalInfo.sysPara.mjloop == 'N' && yfmj > 0))
									{
										break;
									}
									else
									{
										continue;
									}
								}
								else
								{
									continue;
								}
							}
						}
					}
					else
					{
						if (calPop.popDef.poplsj > 0)
						{
							//浮点运算1       = 0.999999,需要进位到两位小数再取整
							//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
							num = ManipulatePrecision.integerDiv(sphj - yfmj, calPop.popDef.poplsj);
						}

						// 满足促销条件，不超过满减限额，不超过参与打折的金额
						double bcje = num * calPop.popDef.pophyj;
						if (bcje + mjje > limitje) bcje = limitje - mjje;
						if (bcje > 0 && (bcje + mjje <= calPop.popje))
						{
							mjje += bcje;
							yfmj += num * calPop.popDef.poplsj;
						}

						// 检查是否存在附加促销条件
						// 允许递归计算满减
						if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
						{

						}
						else if (calPop.popDef.str3 != null && calPop.popDef.str3.trim().length() > 0)
						{
							String[] row = calPop.popDef.str3.split(";");

							for (int c = 0; c < row.length; c++)
							{
								if (row[c] == null || row[c].split(",").length != 2) continue;

								double a = Convert.toDouble(row[c].split(",")[0]); //满减条件
								double b = Convert.toDouble(row[c].split(",")[1]); //满减金额

								if (a == 0 || b == 0) continue;

								//浮点运算1       = 0.999999,需要进位到两位小数再取整
								//浮点运算299/300 = 0.996666,进位取整=1,还需再乘分母用金额比较，如果大倍数要减1
								num = ManipulatePrecision.integerDiv(sphj - yfmj, a);

								// 满足促销条件，不超过满减限额，不超过参与打折的金额
								bcje = num * b;
								if (bcje + mjje > limitje) bcje = limitje - mjje;
								if (bcje > 0 && (bcje + mjje <= calPop.popje))
								{
									mjje += bcje;
									yfmj += num * a;
								}

								if (yfmj > 0 && GlobalInfo.sysPara.mjloop == 'N')
								{
									break;
								}
							}
						}
					}

					if (mjje > 0)
					{
						calPop.mult_Amount = mjje;
					}
					else
					{
						set.remove(i);
						i--;
					}
				}
				else if (calPop.popDef.gz.equals("2")) // 按百分比减现
				{
					// 无效的减现比例
					if ((calPop.popDef.poplsjzkl <= 0) || (calPop.popDef.poplsjzkl >= 1) || (sphj * calPop.popDef.poplsjzkl > limitje))
					{
						set.remove(i);
						i--;
					}
					else
					{
						calPop.popje = sphj;
					}
				}
				else
				{
					set.remove(i);
					i--;
				}
			}

			// 无有效的、满足条件的规则促销
			if (set.size() <= 0) { return haveCrmPop; }

			// 循环计数,如果不需要除券,则不用进行第二次循环
			nwhile++;
			if (!havepaycw) nwhile++;
		} while (nwhile <= 2);

		String[] pay = GlobalInfo.sysPara.mjPaymentRule.split(",");

		boolean exsit = false;
		for (int jj = 0; jj < salePayment.size(); jj++)
		{
			SalePayDef spay = (SalePayDef) salePayment.elementAt(jj);
			for (int ii = 0; ii < pay.length; ii++)
			{

				if (spay.paycode.equals(pay[ii].trim()))
				{
					exsit = true;
					break;
				}
			}

			if (exsit) break;
		}

		// 满减和收券选其一时
		if (!(GlobalInfo.sysPara.ismj == 'Y' && exsit))
		{
			// str2记录规则串描述供小票打印
			saleHead.str2 = "";

			// 分摊满减折扣金额
			for (i = 0; i < set.size(); i++)
			{
				calPop = (CalcRulePopDef) set.elementAt(i);
				double je = 0;
				double hj = 0;

				// 按金额满减
				if (calPop.popDef.gz.equals("1"))
				{
					je = calPop.mult_Amount;
					je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++)
					{
						line1 += "," + String.valueOf(Convert.toInt((String) calPop.row_set.elementAt(x)) + 1);
					}

					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

					// 提示满减规则
					new MessageBox("参加活动的金额为 " + ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n减现 "
							+ ManipulatePrecision.doubleToString(je) + " 元");
				}

				// 按百分比减现
				if (calPop.popDef.gz.equals("2"))
				{
					je = calPop.popje * calPop.popDef.poplsjzkl;
					je = getDetailOverFlow(je, GlobalInfo.syjDef.sswrfs);
					String line1 = "";
					for (int x = 0; x < calPop.row_set.size(); x++)
					{
						line1 += "," + (String) calPop.row_set.elementAt(x);
					}

					line1 = line1.substring(1);

					saleHead.str2 += calPop.popDef.kssj + "\n" + "满减：" + Convert.increaseChar(String.valueOf(je), 8) + "(" + line1 + ")\n";

					// 提示满减规则
					new MessageBox("现有促销减现 " + ManipulatePrecision.doubleToString(calPop.popDef.poplsjzkl * 100) + "%\n\n你目前可参加活动的金额为 "
							+ ManipulatePrecision.doubleToString(calPop.popje) + " 元\n\n你目前可以减现 " + ManipulatePrecision.doubleToString(je) + " 元");
				}

				// 记录规则促销单据信息
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					hj += ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo));
					saleGoodsDef.zsdjbh = calPop.popDef.djbh;
					saleGoodsDef.zszkfd = popDef.poplsjzkfd;
				}

				// 分摊满减折扣到各商品
				double yfd = 0;
				//					int row = -1;
				//					double lje = -1;
				for (j = 0; j < calPop.row_set.size(); j++)
				{
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));
					GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(Integer.parseInt((String) calPop.row_set.elementAt(j)));

					// 把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度
					double lszszk = 0;
					if (popDef.str5 == null || popDef.str5.trim().length() <= 0) popDef.str5 = "A";//若为空,则默认为A(满减) wangyong add by 2013.5.12

					if (popDef.str5.equals("B"))
					{
						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
						}
						else
						{
							lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
						}

						if (lszszk <= 0) continue;

						if (!createMDPayment(lszszk))
						{
							havePaymode = true;
							return false;
						}

						// liwj add
						SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
						String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(lszszk) };
						if (spinfo.payft == null) spinfo.payft = new Vector();
						spinfo.payft.add(s);
					}
					else if (popDef.str5.equals("A"))
					{
						//	把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度

						if (j == (calPop.row_set.size() - 1))
						{
							lszszk = ManipulatePrecision.doubleConvert(je - yfd, 2, 1);
							saleGoodsDef.zszke = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke + lszszk, 2, 1);
						}
						else
						{
							lszszk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef) - getftje(spinfo)) / hj * je, 2, 1);
							double oldzszke = saleGoodsDef.zszke;
							saleGoodsDef.zszke += lszszk;
							saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke);
							saleGoodsDef.zszke = getConvertRebate(Integer.parseInt((String) calPop.row_set.elementAt(j)), saleGoodsDef.zszke,
																	getGoodsApportionPrecision());
							lszszk = ManipulatePrecision.doubleConvert(saleGoodsDef.zszke - oldzszke, 2, 1);
						}
						getZZK(saleGoodsDef);
					}
					// 计算已分摊的金额
					yfd += lszszk;
				}
			}

			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			haveCrmPop = true;
		}
		return haveCrmPop;

	}
	
	public boolean payComplete()
	{
		// 检查付款是否足够
		if (!comfirmPay() || calcPayBalance() > 0 || (saleHead.sjfk <= 0 && GlobalInfo.sysPara.issaleby0 != 'Y'))
		{
			new MessageBox("付款金额不足!");
			return false;
		}

		// 付款完成处理
		if (!payCompleteDoneEvent())
			return false;

		// 找零处理
		PaymentChange pc = calcSaleChange();
		if (pc == null)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			return false;
		}

		//检查券付款金额是否超额 ADD BY 2014.01.17
		if(!checkPay()) return false;
		
		// 付款确认
		new SaleShowAccountForm().open(saleEvent.saleBS);

		// 恢复状态，允许再次触发最后交易完成方法
		waitlab = false;

		// 交易未成功
		if (!saleFinish)
		{
			// 付款完成放弃
			payCompleteCancelEvent();

			// 清除找零
			pc.clearChange();
		}

		return saleFinish;
	}

	private boolean checkPay()
	{
		try
		{
			if (saleHead.num9 <= 0) return true;
			
			PayModeDef mode;
			double jje=0;//券付款金额
			for (int jj = 0; jj < salePayment.size(); jj++)
			{
				SalePayDef spay = (SalePayDef) salePayment.elementAt(jj);
				if (spay==null) continue;
				mode = DataService.getDefault().searchPayMode(spay.paycode);
				if(mode.type=='5')
				{
					jje += ManipulatePrecision.doubleConvert(spay.ybje * spay.hl, 2,1);
				}
			}
			if (ManipulatePrecision.doubleConvert(saleHead.ysje-jje,2,1) < saleHead.num9)
			{
				//不允许付款通过
				PosLog.getLog(this.getClass().getSimpleName()).info("税款担保金[" + ManipulatePrecision.doubleToString(saleHead.num9) + "]不能用【券】付款,请减少【券】付款金额!");
				new MessageBox("税款担保金[" + ManipulatePrecision.doubleToString(saleHead.num9) + "]不能用【券】付款:\n请减少【券】付款金额!");
				return false;
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return true;
	}
}
