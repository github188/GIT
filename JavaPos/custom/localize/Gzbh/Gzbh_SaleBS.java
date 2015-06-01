package custom.localize.Gzbh;

import java.util.Vector;

import org.eclipse.swt.widgets.TableColumn;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.Struct.SuperMarketPopRuleDef;
import com.efuture.javaPos.UI.Design.SaleForm;

public class Gzbh_SaleBS extends SaleBS
{
	public String curdzqoperno = null;
	public String curdzqcanuse = null;

	Vector ruleReqList = null; // 超市规则促销条件列表
	Vector rulePopList = null; // 超市规则促销结果列表
	public double superMarketRuleyhje; // 超市规则促销优惠金额
	public double quantity; // 当前选正行的商品数量

	private Vector originalSaleGoods = null;
	private Vector originalGoodsAssistant = null;
	private Vector originalGoodsSpare = null;

	public void setUI(SaleForm saleform)
	{
		// 获取屏幕分辨率
		int screenWidth = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
		int screenHeight = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);

		// 如果分辨率为800*600 调整列宽
		if (screenWidth == 800 && screenHeight == 600)
		{
			PosTable table;
			TableColumn[] columns;

			table = saleform.table;
			columns = table.getColumns();
			//序列
			columns[0].setWidth(45);
			// 商品编号
			columns[1].setWidth(columns[1].getWidth() - 40);
			// 商品名称列
			columns[2].setWidth(columns[2].getWidth() + 100);
			// 数量列
			columns[4].setWidth(65);
			// 单价列
			columns[5].setWidth(85);
			// 应收金额列
			columns[7].setWidth(85);

			for (int i = 0; i < columns.length; i++)
			{
				System.out.println("第" + i + "列 " + columns[i].getText() + " 宽度" + columns[i].getWidth());
			}
		}
	}

	public int payButtonToPayModePosition(int key)
	{
		int k = -1;
		PayModeDef paymode = null;

		for (k = 0; k < GlobalInfo.payMode.size(); k++)
		{
			paymode = (PayModeDef) (GlobalInfo.payMode.elementAt(k));

			if (key == GlobalVar.PayCash && paymode.type == '1') break;
			if (key == GlobalVar.PayCheque && paymode.type == '2') break;
			if (key == GlobalVar.PayCredit && paymode.type == '3') break;
			if (key == GlobalVar.PayMzk && paymode.type == '4') break;
			if (key == GlobalVar.PayGift && paymode.type == '5') break;
			if (key == GlobalVar.PayTally && paymode.type == '6') break;
			if (key == GlobalVar.PayBank && paymode.isbank == 'Y') break;

			// 客户自定义键2 广百积分卡
			if (key == GlobalVar.CustomKey2 && paymode.code.equals("0002")) break;
			// 客户自定义键3 现金券			
			if (key == GlobalVar.CustomKey3 && paymode.code.equals("0003")) break;
			// 客户自定义键4 会员券
			if (key == GlobalVar.CustomKey4 && paymode.code.equals("0014")) break;
			// 客户自定义键5 外币卡
			if (key == GlobalVar.CustomKey5 && paymode.code.equals("0009")) break;
			// 客户自定义键6 本店促销券
			if (key == GlobalVar.CustomKey6 && paymode.code.equals("0017")) break;
			// 客户自定义键7 条码现金券
			if (key == GlobalVar.CustomKey7 && paymode.code.equals("0031")) break;
		}

		//
		if (k >= GlobalInfo.payMode.size())
		{
			return -1;
		}
		else
		{
			return k;
		}
	}

	private void printDzk(String msg, String payCode)
	{
		ProgressBox progress = null;
		String[] mzkinfo = null;
		boolean result = false;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在" + msg + "交易信息，请等待.....");
			Gzbh_NetService netservice = (Gzbh_NetService) NetService.getDefault();
			mzkinfo = new String[4];
			result = netservice.getDzkInfo(mzkinfo, payCode);
		}
		finally
		{
			if (progress != null) progress.close();
		}

		if (result && mzkinfo != null && mzkinfo.length > 0)
		{
			try
			{
				progress = new ProgressBox();
				progress.setText("正在打印" + msg + "交易信息，请等待.....");
				Gzbh_SaleBillMode saleBillMode = (Gzbh_SaleBillMode) SaleBillMode.getDefault();
				saleBillMode.printDzkInfo(mzkinfo, payCode);
			}
			finally
			{
				if (progress != null) progress.close();
			}
		}
	}

	public void execCustomKey1(boolean keydownonsale)
	{
		try
		{
			if (new MessageBox("您确定要打印积分卡消费信息吗", null, true).verify() == GlobalVar.Key1)
			{
				printDzk("广百消费卡", "0010");
				printDzk("条码现金券", "0031");
				/*
				 ProgressBox progress = null;
				 String[] mzkinfo = null;
				 boolean result = false;
				 try
				 {
				 progress = new ProgressBox();
				 progress.setText("正在查积分卡交易信息，请等待.....");
				 Gzbh_NetService netservice = (Gzbh_NetService) NetService.getDefault();
				 mzkinfo = new String[4];
				 result = netservice.getDzkInfo(mzkinfo, "0010");
				 }
				 finally
				 {
				 if (progress != null) progress.close();
				 }

				 if (result && mzkinfo != null && mzkinfo.length > 0)
				 {
				 try
				 {
				 progress = new ProgressBox();
				 progress.setText("正在打印分卡交易信息，请等待.....");
				 Gzbh_SaleBillMode saleBillMode = (Gzbh_SaleBillMode) SaleBillMode.getDefault();
				 saleBillMode.printDzkInfo(mzkinfo, "0010");
				 }
				 finally
				 {
				 if (progress != null) progress.close();
				 }
				 }
				 */
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
			return;
		}
	}

	//  客户自定义键2 广百积分卡
	public void execCustomKey2(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			sendQuickPayButton(GlobalVar.CustomKey2);
		}
		else
		{
			// 定位付款方式
			int last = payButtonToPayModePosition(GlobalVar.CustomKey2);
			if (last >= 0)
			{
				salePayEvent.gotoPayModeLocation(last);
			}
		}
	}

	// 客户自定义键3 现金券	
	public void execCustomKey3(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			sendQuickPayButton(GlobalVar.CustomKey3);
		}
		else
		{
			// 定位付款方式
			int last = payButtonToPayModePosition(GlobalVar.CustomKey3);
			if (last >= 0)
			{
				salePayEvent.gotoPayModeLocation(last);
			}
		}
	}

	// 客户自定义键4 会员券
	public void execCustomKey4(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			sendQuickPayButton(GlobalVar.CustomKey4);
		}
		else
		{
			// 定位付款方式
			int last = payButtonToPayModePosition(GlobalVar.CustomKey4);
			if (last >= 0)
			{
				salePayEvent.gotoPayModeLocation(last);
			}
		}
	}

	// 客户自定义键5 外币卡
	public void execCustomKey5(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			sendQuickPayButton(GlobalVar.CustomKey5);
		}
		else
		{
			// 定位付款方式
			int last = payButtonToPayModePosition(GlobalVar.CustomKey5);
			if (last >= 0)
			{
				salePayEvent.gotoPayModeLocation(last);
			}
		}
	}

	// 客户自定义键6 本店促销券
	public void execCustomKey6(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			sendQuickPayButton(GlobalVar.CustomKey6);
		}
		else
		{
			// 定位付款方式
			int last = payButtonToPayModePosition(GlobalVar.CustomKey6);
			if (last >= 0)
			{
				salePayEvent.gotoPayModeLocation(last);
			}
		}
	}

	// 客户自定义键7 条码现金券
	public void execCustomKey7(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			sendQuickPayButton(GlobalVar.CustomKey7);
		}
		else
		{
			// 定位付款方式
			int last = payButtonToPayModePosition(GlobalVar.CustomKey7);
			if (last >= 0)
			{
				salePayEvent.gotoPayModeLocation(last);
			}
		}
	}

	public boolean deleteGoods(int index)
	{
		if (new MessageBox("你确定要删除商品?", null, true).verify() != GlobalVar.Key1) { return false; }

		SaleGoodsDef oldsalegood = (SaleGoodsDef) saleGoods.get(index);
		String log = "删除单品，商品编码：" + oldsalegood.code + "，数量：" + oldsalegood.sl + "，金额：" + (oldsalegood.hjje - oldsalegood.hjzk);

		if (super.deleteGoods(index))
		{
			AccessDayDB.getDefault().writeWorkLog(log);
			return true;
		}

		return false;
	}

	// 无论是否找到商品，条码框清空
	public void enterInputCODE()
	{
		super.enterInputCODE();
		saleEvent.code.setText("");
	}

	public void calcGoodsVIPRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		if (saleGoodsDef.hjje == 0) return;

		// 会员VIP折扣模式,商品允许VIP折扣
		if (checkMemberSale() && (curCustomer != null) && (curCustomer.ishy == 'Y') && (goodsDef.isvipzk == 'Y'))
		{
			// 获取VIP折扣率定义
			CustomerVipZklDef zklDef = getGoodsVIPZKL(index);

			// 有VIP折扣率
			if (zklDef != null)
			{
				// 得到商品目前已打折比率
				double zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);

				if (zkl > 0 && zkl < 1) { return; }
				// 进行折上折
				if (zklDef.iszsz == 'Y')
				{
					double vipzkl = 1;

					if ((zkl >= zklDef.zklareadn) && (zkl <= zklDef.zklareaup)) // 折扣在区间内
					{
						vipzkl = zklDef.inareazkl;
					}
					else if (zkl > zklDef.zklareaup) // 折扣在区间上
					{
						vipzkl = zklDef.upareazkl;
					}
					else if (zkl < zklDef.zklareadn) // 折扣在区间下
					{
						vipzkl = zklDef.dnareazkl;
					}

					saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert((1 - vipzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
				}
				else
				{
					// 如果没有其他折扣，打会员折扣
					if (zkl == 1 || getZZK(saleGoodsDef) == 0)
					{
						zkl = ManipulatePrecision.doubleConvert((1 - zklDef.zkl) * saleGoodsDef.hjje, 2, 1);
						if (zkl > getZZK(saleGoodsDef))
						{
							saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert(zkl - getZZK(saleGoodsDef), 2, 1);
						}
					}
				}
			}
		}
	}

	// 单品手工折扣
	public boolean inputRebate(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		// 不能打折
		if (!checkGoodsRebate(goodsDef))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		// 如果已存在促销折扣，则不允许手工折
		if (goodsDef.poptype != '0' && !SellType.ISBATCH(saletype))
		{
			//			if ((goodsDef.isdzc == 'Y'&& goodsDef.poplsj > 0) || (goodsDef.poplsj > 0 && (saleGoodsDef.lsj > goodsDef.poplsj)))
			//			{
			new MessageBox("该商品已存在促销，不允许手工折扣");
			return false;
			//			}
		}

		// 如果已存在会员折扣，此时打手工折则清除会员折扣
		boolean custhyj = (checkMemberSale() && (curCustomer != null) && (curCustomer.ishy == 'Y'));
		if (custhyj && saleGoodsDef.hyzke > 0)
		{
			new MessageBox("该商品已存在会员折扣，手工打折将清除会员折扣");
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 清除会员折扣额
		saleGoodsDef.hyzke = 0;
		//清除临时折让额
		saleGoodsDef.lszre = 0;
		//清除临时总折扣额
		saleGoodsDef.lszzk = 0;
		//清除临时总折让额
		saleGoodsDef.lszzr = 0;
		// 存放临时折扣
		saleGoodsDef.num14 = 0;

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权单品折扣,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.dpzkl;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleGoodsDef.sqkh = cursqkh;
			saleGoodsDef.sqktype = cursqktype;
			saleGoodsDef.sqkzkfd = cursqkzkfd;
		}

		// 计算权限允许的最大折扣率
		double maxzkl = 0;
		if (grantflag)
		{
			//new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}

		// 以最大折扣率模拟计算折扣,检查打折以后商品的折扣合计是否超出权限允许的折扣率
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((1 - maxzkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		// 根据模拟计算得到当前最大打折比例
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

		// 输入折扣
		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open("请输入单品折扣百分比(%)" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折扣", "收银员对该商品的单品折扣权限为 "
				+ ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前最多在成交价基础上再打折 "
				+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%", buffer, lszkl, 100, true))
		{
			// 恢复数据
			saleGoods.setElementAt(oldGoodsDef, index);

			return false;
		}

		// 得到折扣率
		grantzkl = Double.parseDouble(buffer.toString());
		saleGoodsDef.lszke = 0;
		// 批发销售时，先记录下手工折扣，在计算过促销之后再打折
		if (SellType.ISBATCH(saletype))
		{
			saleGoodsDef.num14 = grantzkl;
		}
		else
		{
			// 计算最终折扣
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
			if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
			{
				saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
				saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
			}
			if (saleGoodsDef.lszke < 0) saleGoodsDef.lszke = 0;
			saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);
		}
		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();
		
		return true;
	}

	// 全单手工折扣
	public boolean inputAllRebate()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		StringBuffer strbuf = new StringBuffer();

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null) return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			grantgz = staff.grantgz;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleHead.sqkh = staff.gh;
			saleHead.sqktype = '1';
			saleHead.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权整单折扣,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}
		else
		{
			// 本次授权折扣
			grantzkl = curGrant.zpzkl;
			grantgz = curGrant.grantgz;
			grantflag = breachRebateGrant(curGrant);

			// 记录授权工号
			saleHead.sqkh = cursqkh;
			saleHead.sqktype = cursqktype;
			saleHead.sqkzkfd = cursqkzkfd;
		}

		// 总折扣计算模式为批量单品折扣模式
		if (GlobalInfo.sysPara.batchtotalrebate == 'Y')
		{
			// 输入折扣
			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)", "整单折扣", "收银员对所有商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true)
					+ "%", buffer, grantzkl * 100, 100, true)) { return false; }

			// 得到折扣率
			double zkl = Double.parseDouble(buffer.toString());

			// 循环为每个单品打折
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

				// 小记，削价 不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费,以旧换新 不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				/** 如果已存在促销折扣，则不允许手工折，如果已存在会员折扣，此时打手工折则清除会员折扣 -- start */
				// 如果已存在促销折扣，则不允许手工折
				if (goodsDef.poptype != '0' && !SellType.ISBATCH(saletype))
				{
					//					if ((goodsDef.isdzc == 'Y'&& goodsDef.poplsj > 0) || (goodsDef.poplsj > 0 && (saleGoodsDef.lsj > goodsDef.poplsj)))
					//					{
					strbuf.append("商品: " + goodsDef.barcode + " 已存在促销折扣,将不计算本次手工折扣\n");
					continue;
					//					}
				}

				// 如果已存在会员折扣，此时打手工折则清除会员折扣
				boolean custhyj = (checkMemberSale() && (curCustomer != null) && (curCustomer.ishy == 'Y'));
				if (custhyj && saleGoodsDef.hyzke > 0)
				{
					strbuf.append("商品: " + goodsDef.barcode + " 该商品已存在会员折扣，手工打折将清除会员折扣\n");
					saleGoodsDef.hyzke = 0;
				}

				if (saleGoodsDef.lszke > 0)
				{
					saleGoodsDef.lszke = 0;
				}

				if (saleGoodsDef.lszre > 0)
				{
					saleGoodsDef.lszre = 0;
				}

				if (saleGoodsDef.lszzr > 0)
				{
					saleGoodsDef.lszzr = 0;
				}

				/** 如果已存在促销折扣，则不允许手工折，如果已存在会员折扣，此时打手工折则清除会员折扣 -- end */

				// 计算权限允许的最大折扣额
				double maxzzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					//new MessageBox("允许突破最低折扣");
					maxzzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}

				// 计算最终折扣
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.num15 = 0;
				// 批发销售时，先记录下手工折扣，在计算过促销之后再打折
				if (SellType.ISBATCH(saletype))
				{
					saleGoodsDef.num15 = zkl;
				}
				else
				{
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
					if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1))
					{
						// 提示
						new MessageBox("[" + saleGoodsDef.code + "]" + saleGoodsDef.name + "\n\n最多能打折 "
								+ ManipulatePrecision.doubleToString(maxzzkl * 100) + "%");

						//
						saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1);
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
					}
					if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);
				}
				// 重算商品折扣合计
				getZZK(saleGoodsDef);
			}
		}
		else
		{
			// 计算整单最打可打折金额        
			double sumzzk = 0, sumlszzk = 0, lastzzk = 0;
			int lastzzkrow = -1;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

				// 小记、削价不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费、以旧换新不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 计算每个商品权限允许的最大折扣额
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);

				// 累计可折让金额
				sumzzk += maxzzk;
				sumlszzk += saleGoodsDef.lszzk;

				// 找可折让金额最大的商品
				if (maxzzk > lastzzk)
				{
					lastzzk = maxzzk;
					lastzzkrow = i;
				}
			}

			// 反算得到当前最大打折比例
			double lszkl = (sumzzk - saleHead.hjzke + sumlszzk) / (saleHead.hjzje - saleHead.hjzke + sumlszzk);
			lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

			// 输入折扣
			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折扣", "收银员对所有商品的总折扣权限为 "
					+ ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在交易额基础上再打折 "
					+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%", buffer, lszkl, 100, true)) { return false; }

			// 得到折扣金额,打折后按收银机定义四舍五入
			double zkl = Double.parseDouble(buffer.toString());
			double zzkje = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (saleHead.hjzje - saleHead.hjzke + sumlszzk), 2, 1);
			double tempysje = saleHead.hjzje - saleHead.hjzke + sumlszzk - zzkje;
			double tempyfje = getDetailOverFlow(tempysje);
			zzkje = ManipulatePrecision.sub(zzkje, ManipulatePrecision.sub(tempyfje, tempysje));

			// 把总折扣额分摊到每个商品
			double hjzzk = 0;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsDef = (GoodsDef) goodsAssistant.elementAt(i);

				// 小记、削价不处理
				if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3'))
				{
					continue;
				}

				// 服务费、以旧换新不处理
				if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8'))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 计算商品权限允许的最大折扣额
				double maxzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);

				// 取消其他手工折扣,计算最终折扣
				saleGoodsDef.sqkh = "";
				saleGoodsDef.sqktype = '\0';

				// 每个商品分摊的折让按金额占比计算
				if (i != lastzzkrow)
				{
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(maxzzk / sumzzk * zzkje, 2, 1);
					if (getZZK(saleGoodsDef) > maxzzk)
					{
						saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - maxzzk;
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
					}
					if (saleGoodsDef.lszzk < 0) saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);

					// 重算商品折扣合计
					getZZK(saleGoodsDef);

					// 计算已分摊的总折让
					hjzzk += saleGoodsDef.lszzk;
				}
			}

			// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzzkrow);
			saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(zzkje - hjzzk, 2, 1);
			getZZK(saleGoodsDef);
		}

		// 重算小票应收
		calcHeadYsje();

		if (strbuf.toString().trim().length() > 0)
		{
			new MessageBox(strbuf.toString().trim());
		}

		return true;
	}

	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] detail = new String[8];
		detail[1] = goodsDef.code;
		if (GlobalInfo.syjDef.issryyy == 'Y' && goodsDef.gz != null && !goodsDef.gz.equals("")) detail[2] = "[" + goodsDef.gz + "]" + goodsDef.name;
		else detail[2] = goodsDef.name;
		detail[3] = goodsDef.unit;
		detail[4] = ManipulatePrecision.doubleToString(goodsDef.sl, 4, 1, true);
		detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
		detail[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk)
				+ ((goodsDef.hjzk > 0) && (goodsDef.hjje - goodsDef.hjzk > 0) ? "("
						+ ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk) / goodsDef.hjje * 100, 0, 1) + "%)" : "");
		detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk, 2, 1);
		return detail;
	}

	public boolean reSetBigColor(int goodsIndex)
	{
		if (goodsIndex < 0) { return false; }

		if (goodsAssistant == null) { return false; }

		if (goodsAssistant.size() - 1 < goodsIndex) { return false; }

		GoodsDef goodsDef = (GoodsDef) goodsAssistant.get(goodsIndex);

		if (goodsDef == null) return false;

		// 特价商品
		if (goodsDef.popdjbh != null && goodsDef.popdjbh.trim() != "") { return true; }

		// 低毛利商品
		if (goodsDef.isvipzk == 'Y' && goodsDef.issqkzk != 'Y') { return true; }

		//不可打折的商品
		if (goodsDef.popdjbh != null && goodsDef.popdjbh.trim() != "" && goodsDef.isvipzk != 'Y' && goodsDef.issqkzk != 'Y') { return true; }

		return false;
	}

	// 批发销售时不确认商品价格
	public boolean isPriceConfirm(GoodsDef goodsDef)
	{
		if (((goodsDef.lsj <= 0) && (goodsDef.type != 'P') && (goodsDef.type != 'Z')) || SellType.ISBACK(saletype)) { return true; }
		return false;
	}

	// 批发销售时不输入商家分担
	public boolean inputGoodsAddInfo(GoodsDef goodsDef)
	{
		return true;
	}

	public void calcAllRebate(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		if (SellType.NOPOP(saletype)) return;

		// 预售定金不计算
		if (SellType.ISEARNEST(saletype) || SellType.ISPREPARETAKE(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) { return; }

		// 清除商品相应自动计算的折扣
		saleGoodsDef.hyzke = 0;
		saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.plzke = 0;
		saleGoodsDef.zszke = 0;

		// 计算商品促销折扣
		calcGoodsPOPRebate(index);

		// 计算会员VIP折上折
		if (saleGoodsDef.lszke == 0 || saleGoodsDef.lszke == 0)
		{
			calcGoodsVIPRebate(index);
		}
		// 
		saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);
		saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke, 2, 1);

		// 按价格精度计算折扣
		if (saleGoodsDef.yhzke > 0) saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
		if (saleGoodsDef.hyzke > 0) saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
	}

	//批发也取零售价
	public double setGoodsDefaultPrice(GoodsDef goodsDef)
	{
		return goodsDef.lsj;
	}

	public boolean checkGoodsRebate(GoodsDef goodsDef)
	{
		if (SellType.ISBATCH(saletype))
		{
			return true;
		}
		else
		{
			return super.checkGoodsRebate(goodsDef);
		}
	}

	public CustomerVipZklDef getGoodsVIPZKL(int index)
	{
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || (curCustomer == null)) { return null; }

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.BATCH_SALE)) { return null; }

		// 查询商品VIP折上折折扣率定义
		CustomerVipZklDef zklDef = new CustomerVipZklDef();

		if (DataService.getDefault().findVIPZKL(zklDef, curCustomer.code, curCustomer.type, goodsDef))
		{
			// 有柜组和商品的VIP折扣定义
			return zklDef;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			zklDef.iszsz = 'Y';
			zklDef.zkl = curCustomer.zkl;
			zklDef.zklareadn = 0;
			zklDef.zklareaup = 1;
			zklDef.inareazkl = zklDef.zkl;

			return zklDef;
		}
	}

	public boolean readHangGrant()
	{
		return true;
	}

	public boolean writeHangGrant()
	{
		return true;
	}

	public boolean verifyDzcmCheckbit(String dzcm)
	{
		int odd = 0; // 奇数
		int even = 0; // 偶数

		for (int i = 0; i < dzcm.length() - 1; i++)
		{
			if (i % 2 == 0)
			{
				odd = odd + Integer.parseInt(dzcm.substring(i, i + 1));
			}
			else
			{
				even = even + Integer.parseInt(dzcm.substring(i, i + 1));
			}
		}

		// 算法一（偶位数的和*3+奇位数的和）MOD10
		int a = odd + even * 3;

		// 算法二（奇位数的和*3+偶位数的和）MOD10
		int b = odd * 3 + even;

		int checkbit = Integer.parseInt(dzcm.substring(dzcm.length() - 1, dzcm.length()));

		if ((10 - (a % 10)) % 10 != checkbit && (10 - (b % 10)) % 10 != checkbit)
		{
			System.err.println("测试失败");
			return false;
		}
		else
		{
			System.out.println("测试成功");
			return true;
		}
	}

	public boolean isConfirmPrice(boolean isdzcm, double dzcprice, GoodsDef goodsDef)
	{
		if (isSpecifyBack() || isdzcm
				|| (!isSpecifyBack() && SellType.ISBACK(saletype) && GlobalInfo.sysPara.setPriceBackStatus == 'N' && goodsDef.lsj > 0)) { return false; }
		return true;
	}

	public boolean doSuperMarketCrmPop()
	{
		if (!SellType.ISSALE(saletype)) { return false; }

		if (SellType.NOPOP(saletype)) { return false; }

		if (SellType.ISEARNEST(saletype)) { return false; }

		if (SellType.ISPREPARETAKE(saletype)) { return false; }

		originalSaleGoods = cloneSaleGoodsVector(saleGoods);
		originalGoodsAssistant = cloneGoodsAssistantVector(goodsAssistant);
		originalGoodsSpare = cloneGoodsSpareVector(goodsSpare);
		
		// 初始化超市促销标志
		for (int i = 0; i < saleGoods.size(); i++)
		{
			((SaleGoodsDef) saleGoods.get(i)).isSMPopCalced = 'Y';
		}

		// 排序
		if (saleGoods.size() > 1)
		{
			SaleGoodsDef sgd = null;
			GoodsDef gd = null;
			SpareInfoDef sid = null;

			for (int i = 1; i < saleGoods.size(); i++)
			{
				for (int j = 0; j < saleGoods.size() - i; j++)
				{
					String code = ((SaleGoodsDef) saleGoods.get(j)).code;
					String code1 = ((SaleGoodsDef) saleGoods.get(j + 1)).code;

					if (code.equals(code1)) continue;

					if (code1.length() < code.length() || (code1.length() == code.length() && code1.compareTo(code) < 0))
					{
						sgd = (SaleGoodsDef) saleGoods.get(j);
						saleGoods.setElementAt(saleGoods.get(j + 1), j);
						saleGoods.setElementAt(sgd, j + 1);

						gd = (GoodsDef) goodsAssistant.get(j);
						goodsAssistant.setElementAt(goodsAssistant.get(j + 1), j);
						goodsAssistant.setElementAt(gd, j + 1);

						sid = (SpareInfoDef) goodsSpare.get(j);
						goodsSpare.setElementAt(goodsSpare.get(j + 1), j);
						goodsSpare.setElementAt(sid, j + 1);
					}
				}
			}
		}

		// 查找规则
		SuperMarketPopRuleDef ruleDef = null;
		Vector notRuleDjbh = new Vector();
		int calcCount = saleGoods.size();
		int k, j, l, m, n;
		double zje, je, t_zje;
		double or_yhsl = 0;//结果为OR关系的时候,存放第一个结果的数量
		long bs, minbs, t_minbs;

		String cardNo = "";
		if (curCustomer != null)
		{
			cardNo = curCustomer.code;
		}

		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;
		//SpareInfoDef spareInfoDef = null;
		for (int i = 0; i < calcCount + 1; i++)
		{
			// 查找单品优惠单号
			if (i != calcCount)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.get(i);
				goodsDef = (GoodsDef) goodsAssistant.get(i);
				//	spareInfoDef = (SpareInfoDef) goodsSpare.get(i);
				// 只有普通商品才能参与
				if (saleGoodsDef.flag != '2' && saleGoodsDef.flag != '4') continue;
				// 判断是否曾参与规则促销
				if (saleGoodsDef.isSMPopCalced == 'N') continue;
				// 标准百货crm促销，跳过
				//				if (goodsDef.str4.equals("Y")) continue;

				//已经查过的商品无需重复查规则促销
				for (k = 0; k < i; k++)
				{
					if (goodsDef.code.equals(((GoodsDef) goodsAssistant.get(k)).code) && goodsDef.gz.equals(((GoodsDef) goodsAssistant.get(k)).gz)
							&& goodsDef.uid.equals(((GoodsDef) goodsAssistant.get(k)).uid)) break;
				}
				if (k < i) continue;

			}
			// 超找整单超市促销单号
			else
			{
				goodsDef = new GoodsDef();
				goodsDef.code = "ALL";
				goodsDef.gz = ManipulatePrecision.doubleToString(saleHead.ysje);
				goodsDef.catid = "";
				goodsDef.ppcode = "";
				goodsDef.uid = "";
			}

			// 首先查找超市规则促销单号
			ruleDef = new SuperMarketPopRuleDef();
			if (!((Gzbh_DataService) DataService.getDefault()).findSuperMarketPopBillNo(ruleDef, goodsDef.code, goodsDef.gz, goodsDef.catid,
																						goodsDef.ppcode, goodsDef.uid, saleHead.rqsj, saleHead.rqsj,
																						cardNo))
			{
				continue;
			}

			//检查该单据是否已经运算过，如果已经运行过则无需重复运算
			for (k = 0; k < notRuleDjbh.size(); k++)
			{
				if (((String) notRuleDjbh.get(k)).equals(ruleDef.djbh)) break;
			}
			if (k < notRuleDjbh.size()) continue;

			// 查找超市促销规则明细
			ruleReqList = new Vector();
			rulePopList = new Vector();
			if (!((Gzbh_DataService) DataService.getDefault()).findSuperMarketPopRule(ruleReqList, rulePopList, ruleDef) || ruleReqList.size() == 0
					|| rulePopList.size() == 0)
			{
				continue;
			}

			// 初始化条件参数
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				// 标志为A表示在上一轮规则中被条件排除的商品，因此可以参与本轮规则促销
				sg.isPopExc = ' ';
				if (sg.isSMPopCalced == 'A') ((SaleGoodsDef) saleGoods.get(k)).isSMPopCalced = 'Y';
			}

			for (j = 0; j < ruleReqList.size(); j++)
			{
				for (k = 0; k < saleGoods.size(); k++)
				{
					//商品是否条件匹配
					if (isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
					{
						((SaleGoodsDef) saleGoods.get(k)).isPopExc = 'Y';//表示条件满足
					}
				}
			}

			//先将规则条件中要排除的商品排除掉
			for (j = 0; j < ruleReqList.size(); j++)
			{
				//presentsl为1表示该条件是排除的。
				if (((SuperMarketPopRuleDef) ruleReqList.get(j)).presentsl == 1)
				{
					for (k = 0; k < saleGoods.size(); k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//商品是否条件匹配
						if (sg.isPopExc == 'Y' && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
						{
							//将排除商品的标志置为N,表示不参与规则促销
							if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '0') //排除条件
							sg.isPopExc = ' ';
							else if (((SuperMarketPopRuleDef) ruleReqList.get(j)).istjn.charAt(0) == '1') //排除结果
							sg.isPopExc = 'N';
							else
							//条件和结果都排除
							{
								sg.isPopExc = ' ';
								sg.isSMPopCalced = 'A';
							}
						}
					}
					//如果是排除条件。那么无论结果是排除条件还是排除结果都不纳入条件计算 
					//例如,单据是一行类别的条件和一行排除结果的条件，如果此处不删除排除结果的那一行数据，
					//并且输入的商品中没有买这个排除结果的商品，会导致后面计算AND条件的时候算出倍数为0的情况。
					//如果是排除条件，则需将条件从条件列表中删除，这样是为了后面算多级
					ruleReqList.remove(j);
					j--;
				}
			}

			minbs = 0;
			zje = 0;

			for (n = 0; n < ((SuperMarketPopRuleDef) ruleReqList.get(0)).jc; n++)
			{
				t_minbs = 0;
				t_zje = 0;

				//得到当前级次
				getCurRuleJc(n + 1);

				//匹配规则条件中属于必须满足的条件
				for (l = 0, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					//条件为AND
					if (ruleReq.presentjs == 1)
					{
						l++;
						je = 0;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod((SuperMarketPopRuleDef) ruleReqList.get(j), k))
							{
								// 记录促销条件的种类
								sg.str7 = ruleReq.ppistr3;
								
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ruleReq.yhhyj == 0) je += sg.sl;
								else je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
								//								此处如果该变cominfo[k].infostr1[5] = 'A',在计算第2级别的时候,程序不能进入上面的IF条件进行统计，导致无法计算一级以上的级别
								//								避免后面的or判断时又找到该条件
							}
						}

						bs = 0;
						if (ManipulatePrecision.doubleCompare(je, ruleReq.yhlsj, 2) >= 0
								&& ManipulatePrecision.doubleCompare(ruleReq.yhlsj, 0, 2) >= 0)
						{
							bs = new Double(je / ruleReq.yhlsj).longValue();
						}
						if (l == 1) t_minbs = bs;
						else t_minbs = t_minbs > bs ? bs : t_minbs;

						t_zje += je;
					}
				}
				//有必须全满足的条件，并且未全满足时，则认为条件不满足
				if (l > 0 && t_minbs <= 0)
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
				//匹配规则条件中属于非必须满足的条件
				for (je = 0, m = -1, j = 0; j < ruleReqList.size(); j++)
				{
					SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(j);
					//条件为OR
					if (ruleReq.presentjs == 0)
					{
						m = j;
						for (k = 0; k < saleGoods.size(); k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否条件匹配
							if ((sg.isPopExc == 'Y' || sg.isPopExc == 'N') && isMatchCommod(ruleReq, k))
							{
								// 记录促销条件的种类
								sg.str7 = ruleReq.ppistr3;
								
								//yhhyj = 0，表示yhlsj中记录的是数量
								//yhhyj = 1，表示yhlsj中记录的是金额
								if (ManipulatePrecision.doubleCompare(ruleReq.yhhyj, 0, 2) == 0) je += sg.sl;
								else je += sg.hjje - sg.yhzke - sg.hyzke - sg.plzke;
							}
						}
					}
				}
				t_zje += je;

				//计算or条件的倍数

				if (m >= 0)
				{
					SuperMarketPopRuleDef ruleReqM = (SuperMarketPopRuleDef) ruleReqList.get(m);
					if (ManipulatePrecision.doubleCompare(je, ruleReqM.yhlsj, 2) >= 0 && ManipulatePrecision.doubleCompare(ruleReqM.yhlsj, 0, 2) > 0)
					;
					{
						bs = new Double(je / ruleReqM.yhlsj).longValue();
						if (l > 0) t_minbs = t_minbs > bs ? bs : t_minbs;
						else t_minbs = bs;
					}
				}
				if (t_minbs > 0)
				{
					minbs = t_minbs;
					zje = t_zje;
				}
				else
				{
					//还原上一级
					if (n > 0) getCurRuleJc(n);
					break;
				}
			}

			//有必须全满足的条件，并且未全满足时，则认为条件不满足
			if (minbs <= 0)
			{
				//记录下不匹配的单据号，以便后面的商品再找到该单据时不用再次进行匹配运算
				notRuleDjbh.add(ruleDef.djbh);
				continue;
			}
			else
			{
			}
			//ppistr6中的第1个字符为1时，表示1倍封顶
			if (((SuperMarketPopRuleDef) ruleReqList.get(0)).ppistr5.charAt(0) == '1') minbs = 1;

			//计算促销的结果
			for (j = 0; j < rulePopList.size(); j++)
			{
				SuperMarketPopRuleDef rulePop = (SuperMarketPopRuleDef) rulePopList.get(j);
				//商品优惠 商品优惠对应 一级商品 也对应多级的商品
				if (rulePop.yhdjlb == 'A' || rulePop.yhdjlb == 'E')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					if (rulePop.presentjs == 0)
					{
						if (j == 0) or_yhsl = yhsl;
						else yhsl = or_yhsl;
					}

					for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//商品是否结果匹配 排除结果
						if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							//商品拆分
							if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
							{
								yhsl -= sg.sl;
								or_yhsl -= sg.sl;
							}
							else
							{
								//拆分商品行
								splitSalecommod(k, yhsl);
								yhsl = 0;
								or_yhsl = 0;
							}
							double misszke = 0;
							//整单
							if (ruleDef.type == '8')
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
							}
							else
							{
								misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额
							}
							//取价方式判断
							if (rulePop.presentjg == 0)//取价方式 取价格
							{
								//如果是折上折，那么折后金额 = 一般优惠后的金额 * 现在的规则定价 /商品本身的价格
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke
												+ sg.rulezke, 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;
											sg.rulezke = 0;
											sg.rulezkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjdjbh = rulePop.djbh;
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke, 2, 0)
												- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，一般优惠清零
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = rulePop.djbh;
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 1)//取折扣率
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.mjzke;
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg)) * rulePop.yhlsj, 2, 0);
										sg.rulezke = calcComZkxe(k, sg.rulezke);
										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										//统计当前规则促销的折扣金额
										superMarketRuleyhje += sg.rulezke;
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = 0;
										sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;
											sg.yhdjbh = "";
											sg.ruledjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke - misszke;
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = 0;
										sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
												- ManipulatePrecision.doubleConvert((sg.hjje - getZZK(sg) + misszke) * rulePop.yhlsj, 2, 0);

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke - misszke;
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else if (rulePop.presentjg == 2)//取折扣额
							{
								if (rulePop.iszsz.charAt(0) == '1')
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;
										sg.mjzke = calcComZkxe(k, sg.mjzke);

										sg.mjdjbh = rulePop.djbh;
										sg.mjzkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.mjzke;//统计规则促销的折扣金额
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;
										sg.rulezke = calcComZkxe(k, sg.rulezke);

										sg.ruledjbh = rulePop.djbh;
										sg.rulezkfd = rulePop.zkfd;
										superMarketRuleyhje += sg.rulezke;//统计规则促销的折扣金额
									}
								}
								else
								{
									if (ruleDef.type == '8')
									{
										sg.mjzke = rulePop.yhlsj;

										if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.rulezke = 0;
											sg.rulezkfd = 0;

											sg.ruledjbh = "";
											sg.yhdjbh = "";
											sg.mjzke = calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.mjzke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.mjzke = 0;
										}
									}
									else
									{
										sg.rulezke = rulePop.yhlsj;

										if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
										{
											sg.yhzke = 0;
											sg.hyzke = 0;
											sg.plzke = 0;
											sg.spzkfd = 0;
											sg.yhzkfd = 0;//清除普通优惠和会员优惠
											sg.yhdjbh = "";
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.rulezke - misszke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.rulezke = 0;
										}
									}
								}
							}
							else
							//用于其它用途
							{
							}
							sg.isPopExc = 'Y';
							
							// 记录促销条件的种类
							sg.str7 = String.valueOf(rulePop.yhdjlb);							
						}
					}
				}
				//赠品
				if (rulePop.yhdjlb == 'B' || rulePop.yhdjlb == 'F')
				{
					//'4'表示买赠，该赠品是小票列表中的正常商品，要将其改成正赠品
					if (rulePop.ppistr6.length() > 0 && rulePop.ppistr6.charAt(0) == '4')
					{
						//赠品数量
						quantity = minbs * rulePop.yhlsj;

						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(quantity, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否结果匹配  排除结果
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, quantity, 4) <= 0)
								{
									quantity -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k, quantity);
									quantity = 0;
								}
								//将该商品改为赠品
								sg.flag = '1';
								sg.batch = rulePop.ppistr6;

								sg.xxtax = ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);
								sg.rulezke += sg.hjje - getZZK(sg) - ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);

								sg.ruledjbh = rulePop.djbh; //记录优惠单据编号
								sg.rulezkfd = rulePop.zkfd;

								//该商品的应收金额都记为优惠金额
								superMarketRuleyhje += sg.zszke;
							}
						}
					}
					else
					{
						GoodsDef gift = new GoodsDef();
						if (DataService.getDefault().getGoodsDef(gift, 1, rulePop.code, rulePop.gz, "", "", saletype) == -1) continue;
						//赠品数量
						quantity = minbs * rulePop.yhlsj;
						//赠品数量不能为零，因为数量为零会造成小票不能送网
						if (quantity <= 0) quantity = 1;
						SaleGoodsDef sgdef = goodsDef2SaleGoods(gift, "", quantity, 0, 0, false);
						sgdef.hjje = 0;
						sgdef.yhdjbh = rulePop.djbh;
						sgdef.flag = '1';
						sgdef.batch = rulePop.ppistr6;

						//需要加钱的赠品，钱由前台直接收取
						if (minbs * rulePop.yhhyj > 0)
						{
							sgdef.jg = ManipulatePrecision.doubleConvert((minbs * rulePop.yhhyj) / quantity, 2, 0);
							sgdef.hjje = minbs * rulePop.yhhyj;
						}
						sgdef.xxtax = ManipulatePrecision.doubleConvert(minbs * rulePop.yhhyj, 2, 0);
						saleGoods.add(sgdef);
					}
				}
				//任意几个定应收金额 只分多级
				if (rulePop.yhdjlb == 'X')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0
							&& ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();
						double t_zyhje = 0;

						t_zje = 0;
						t_zyhje = 0;

						//yhje应小于小票的应收金额，不然话，小票金额有成负数的可能。
						if (ManipulatePrecision.doubleCompare(yhje, saleHead.ysje, 2) < 0)
						{
							int t_maxjerow = -1;
							long t_yhsl = yhsl;
							//计算本次参与优惠商品的总金额
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (t_yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, t_yhsl, 4) <= 0)
										{
											t_yhsl -= (long) sg.sl;
										}
										else
										{
											//拆分商品行
											splitSalecommod(k, t_yhsl);
											t_yhsl = 0;
										}
										//如果是折上折
										if (rulePop.iszsz.charAt(0) == '1')
										{
											t_zje += sg.hjje - getZZK(sg);
										}
										else
										{
											if (ruleDef.type == '8')
											{
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
												t_zyhje += sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
											}
											else
											{
												t_zje += sg.hjje - getZZK(sg) + sg.yhzke + sg.plzke + sg.hyzke;
												t_zyhje += sg.yhzke + sg.hyzke + sg.plzke;
											}
										}
									}
								}
							}

							//计算出优惠金额
							yhje = t_zje - yhje;

							double t_je = 0;
							//将优惠金额按金额占比分摊到本次参与的商品上面
							for (l = 0; l < ruleReqList.size(); l++)
							{
								SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
								for (k = 0; k < saleGoods.size(); k++)
								{
									SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
									if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
									{
										//商品拆分
										if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
										{
											yhsl -= sg.sl;
										}
										else
										{
											//拆分商品行
											splitSalecommod(k, yhsl);
											yhsl = 0;
										}
										double misszke = 0;
										if (ruleDef.type == '8')
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke + sg.rulezke;
										}
										else
										{
											misszke = sg.yhzke + sg.yhzke + sg.plzke;
										}
										//根据折上折来判断，如果非折上折，取低价优先
										if (rulePop.iszsz.charAt(0) == '1')
										{
											if (ruleDef.type == '8')
											{
												sg.mjzke = 0;
												sg.mjzke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.mjzke);
												//记录折扣分担
												sg.mjzkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.mjdjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke;

											}
											else
											{
												sg.rulezke = 0;
												sg.rulezke = (sg.hjje - getZZK(sg)) / t_zje * yhje;
												calcComZkxe(k, sg.rulezke);
												//记录折扣分担
												sg.rulezkfd = rulePop.zkfd;
												//记录优惠单据编号
												sg.ruledjbh = rulePop.djbh;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke;
											}
										}
										else
										{
											if (yhje > t_zyhje)
											{
												if (ruleDef.type == '8')
												{
													sg.mjzke = 0;
													sg.mjzke = (sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;

													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd = 0;
													sg.yhzkfd = 0;
													sg.rulezke = 0;
													sg.rulezkfd = 0;
													sg.yhdjbh = "";
													sg.ruledjbh = "";
													calcComZkxe(k, sg.mjzke);
													//记录折扣分担
													sg.mjzkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.mjzke - misszke;

												}
												else
												{
													sg.rulezke = 0;
													sg.rulezke = (sg.hjje - getZZK(sg) + misszke) / t_zje * yhje;
													sg.yhzke = 0;
													sg.hyzke = 0;
													sg.plzke = 0;
													sg.spzkfd = 0;
													sg.yhzkfd = 0;
													sg.yhdjbh = "";
													calcComZkxe(k, sg.rulezke);
													//记录折扣分担
													sg.rulezkfd = rulePop.zkfd;
													//记录优惠单据编号
													sg.ruledjbh = rulePop.djbh;
													//统计当前规则促销的折扣金额
													superMarketRuleyhje += sg.rulezke - misszke;
												}
											}
											else
											{
												t_je = yhje;
											}
										}
										// 将参与优惠的商品打上标记
										sg.isPopExc = 'Y';
										if (ruleDef.type == '8')
										{
											t_je += sg.mjzke;
										}
										else
										{
											t_je += sg.rulezke;
										}

										//记下金额最大的行号
										if (t_maxjerow >= 0
												&& ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).hjje, 2) > 0
												|| t_maxjerow < 0) t_maxjerow = k;
									}
								}
								if (yhsl <= 0) break;
							}
							if (ManipulatePrecision.doubleCompare(Math.abs(yhje - t_je), 0, 2) > 0)
							{
								if (ruleDef.type == '8')
								{
									((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke += yhje - t_je;
									calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke);
								}
								else
								{
									((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke += yhje - t_je;
									calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke);
								}
								superMarketRuleyhje += yhje - t_je;
							}
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//满减促销  满减促销必须都按照分级的情况给出
				if (rulePop.yhdjlb == 'G')
				{
					double mjje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);
					double comzje = 0;
					double t_zyhje = 0;

					//减的金额必须>0
					if (mjje > 0)
					{
						//统计参与促销的商品的总优惠金额
						for (l = 0; l < ruleReqList.size(); l++)
						{
							//presentsl为1表示该条件是排除的。
							//if (rulepoplist[l].presentsl == 0) t_zyhje = 0; //这行代码是错的，会导致已经参与优惠(分期,规则非整单)的商品金额的优惠金额的统计
							SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A')/* && cominfo[k].infostr1[10] != 'Y'*/)
								{
									//是否折上折
									if (rulePop.iszsz.charAt(0) == '1')//是
									{
										comzje += sg.hjje;
										t_zyhje += sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
									}
									else
									//否
									{
										comzje += sg.hjje;

										if (ruleDef.type == '8')
										{
											t_zyhje += sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;
										}
										else
										{
											t_zyhje += sg.yhzke + sg.hyzke + sg.plzke;
										}
									}
								}
							}
						}

						int t_maxjerow = -1;
						double t_je = 0;

						//将减的金额分摊到商品明细上
						for (l = 0; l < ruleReqList.size(); l++)
						{
							SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A')/* && cominfo[k].infostr1[10] != 'Y'*/)
								{
									double yhzke = 0;
									double misszke = 0;

									if (ruleDef.type == '8') misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
									else misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额

									//是否折上折
									if (rulePop.iszsz.charAt(0) == '1')
									{
										yhzke = ManipulatePrecision.doubleConvert(sg.hjje / comzje * mjje, 2, 0);
										yhzke = getConvertPrice(yhzke, (GoodsDef) goodsAssistant.elementAt(k));

										if (ruleDef.type == '8')
										{
											sg.mjzke = 0;
											sg.mjzke = yhzke;
											sg.mjzke = calcComZkxe(k, sg.mjzke);

											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.mjzke;//统计规则促销的折扣金额	
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = yhzke;
											sg.rulezke = calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											superMarketRuleyhje += sg.rulezke;//统计规则促销的折扣金额	
										}
										t_je += yhzke;

										//										将参与优惠的商品打上标记
										sg.isSMPopCalced = 'Y';
									}
									else
									{
										//比较满减的金额是否比一般促销的金额更低
										if (ManipulatePrecision.doubleCompare(mjje, t_zyhje, 2) > 0)
										{
											yhzke = ManipulatePrecision.doubleConvert(sg.hjje / comzje * mjje, 2, 0);

											//如果是对应的整单满减
											if (ruleDef.type == '8')
											{
												sg.mjzke = 0;
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.yhzkfd = 0;
												sg.spzkfd = 0;
												sg.rulezke = 0;
												sg.rulezkfd = 0;
												sg.ruledjbh = "";
												sg.yhdjbh = "";
												sg.mjzke = yhzke;
												sg.mjzke = calcComZkxe(k, sg.mjzke);
												sg.mjdjbh = rulePop.djbh;
												sg.mjzkfd = rulePop.zkfd;
												superMarketRuleyhje += sg.mjzke - misszke;//统计规则促销的折扣金额	
											}
											else
											//对应商品满减
											{
												sg.rulezke = 0;
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.yhzkfd = 0;
												sg.spzkfd = 0;
												sg.yhdjbh = "";
												sg.rulezke = yhzke;
												sg.rulezke = calcComZkxe(k, sg.rulezke);
												sg.mjdjbh = rulePop.djbh;
												sg.mjzkfd = rulePop.zkfd;

												superMarketRuleyhje += sg.rulezke - misszke;//统计规则促销的折扣金额
											}
											t_je += yhzke;

											//参与满减促销的标志
											sg.isSMPopCalced = 'Y';
										}
										else
										{
											t_je = mjje;
											//参与满减促销的标志
											sg.isSMPopCalced = ' ';
											//当前规则有促销金额，则将有促销的商品排除掉
											if (ManipulatePrecision.doubleCompare(t_zyhje, 0, 2) > 0 && i < originalSaleGoods.size())
											{
												if (i >= 0) i--;
												if (ManipulatePrecision.doubleCompare(sg.yhzke + sg.hyzke + sg.plzke, 0, 2) > 0)
												{
													sg.isSMPopCalced = 'Y';
												}
											}
										}
									}
									//记下金额最大的行号
									if (t_maxjerow >= 0
											&& ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).hjje, 2) > 0
											|| t_maxjerow < 0) t_maxjerow = k;
								}
							}
						}
						//未分配完的金额分配到金额最大的商品上
						if (mjje != t_je)
						{
							if (ruleDef.type == '8')
							{
								((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke += mjje - t_je;
								calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).mjzke);
							}
							else
							{
								((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke += mjje - t_je;
								calcComZkxe(t_maxjerow, ((SaleGoodsDef) saleGoods.get(t_maxjerow)).rulezke);
							}
							superMarketRuleyhje += mjje - t_je;
						}
					}
					//满减只算一条结果
					break;
				}
				//数量促销 数量促销没有折上折(取的单价)，没有分级，取单价
				if (rulePop.yhdjlb == 'N')
				{
					//flag = 1 是全量优惠
					//flag = 2 是超量促销
					//flag = 3 是第n件促销
					//flag = 4 是整箱促销
					long flag = new Double(rulePop.yhhyj).longValue();
					//统计本单参与优惠的商品数量
					double kyhsl = 0;
					for (k = 0; k < calcCount; k++)
					{
						SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
						//条件排除的商品应该参与结果计算
						if (/*cominfo[k].infostr1[5] != ' ' && */isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
						{
							kyhsl += sg.sl;
							//如果前一个商品已经满足了折扣,
							//那么第二个商品必须标示为已经参与促销（此处为标示其已经促销的条件）。
						}
					}

					//优惠数量
					double yhsl = Double.parseDouble(rulePop.ppistr3);
					if (ManipulatePrecision.doubleCompare(yhsl, 0, 2) <= 0) yhsl = 1;//防止后面计算时除0错误

					//超量促销
					if (flag == 2) kyhsl -= yhsl;
					//第n件促销
					if (flag == 3) kyhsl = new Double(kyhsl / yhsl).longValue();
					//整箱促销
					if (flag == 4)
					{
						minbs = new Double(kyhsl / yhsl).longValue();
						long zyhsl = new Double(minbs * yhsl).longValue();//整箱总的优惠数量
						kyhsl = kyhsl > zyhsl ? zyhsl : kyhsl;
					}
					
					// 清除参与促销商品的手工折扣和会员折扣
					int popCount = new Double(kyhsl * yhsl).intValue();
					int splitCount = popCount;
					SaleGoodsDef popSg = null;
					for (int p = 0; p < saleGoods.size(); p++)
					{
						popSg = (SaleGoodsDef)saleGoods.get(p);
						if (isMatchCommod(rulePop, p) && !(popSg.isPopExc == 'N' || popSg.isSMPopCalced == 'A'))
						{
							if (popSg.sl <= splitCount)
							{
								splitCount -=  popSg.sl;
							}
							else
							{
								splitSalecommod(p, splitCount);
								splitCount = 0;
							}
							popSg.lszke = 0;
							popSg.lszzk = 0;
							popSg.lszkfd = 0;
							popSg.hyzke = 0;
							popSg.hyzkfd = 0;
							if (splitCount == 0) break;
						}
					}

					//开始计算优惠
					if (ManipulatePrecision.doubleCompare(kyhsl, 0, 2) > 0)
					{
						for (k = 0; k < calcCount; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							if (kyhsl > 0 && isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//优惠价低于零售价
								if (sg.lsj > rulePop.yhlsj && rulePop.yhlsj > 0)
								{
									if (kyhsl >= sg.sl)
									{
										kyhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k, kyhsl);
										kyhsl = -1;
									}
									sg.rulezke = 0;
									double misszke = sg.yhzke + sg.hyzke + sg.plzke;
									sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
											- ManipulatePrecision.doubleConvert(rulePop.yhlsj * sg.sl, 2, 0);

									//优惠价低于一般促销
									if (ManipulatePrecision.doubleCompare(sg.rulezke, misszke, 2) > 0)
									{
										sg.yhzke = 0;
										sg.hyzke = 0;
										sg.plzke = 0;
										sg.spzkfd = 0;
										sg.yhzkfd = 0;//清除普通优惠和会员优惠
										sg.yhdjbh = "";
										sg.rulezkfd = rulePop.zkfd;
										calcComZkxe(k, sg.rulezke);
										//记录优惠单据编号
										sg.ruledjbh = rulePop.djbh;
										superMarketRuleyhje += sg.rulezke - misszke;

										//参与满减促销的标志
										sg.isPopExc = 'Y';
									}
									else sg.rulezke = 0;
								}
							}
						}
					}
				}
				//任意几个定单价 只分多级
				if (rulePop.yhdjlb == 'Z')
				{
					if (ManipulatePrecision.doubleCompare(zje, ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 2) >= 0
							&& ManipulatePrecision.doubleCompare(((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj, 0, 2) > 0)
					{
						//参与优惠的商品明细数量
						long yhsl = new Double(minbs * ((SuperMarketPopRuleDef) ruleReqList.get(0)).yhlsj).longValue();

						for (l = 0; l < ruleReqList.size(); l++)
						{
							SuperMarketPopRuleDef ruleReq = (SuperMarketPopRuleDef) ruleReqList.get(l);
							for (k = 0; k < saleGoods.size(); k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								if (yhsl > 0 && isMatchCommod(ruleReq, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									//商品拆分
									if (ManipulatePrecision.doubleCompare(sg.sl, yhsl, 4) <= 0)
									{
										yhsl -= sg.sl;
									}
									else
									{
										//拆分商品行
										splitSalecommod(k, yhsl);

										yhsl = 0;
									}
									double misszke = 0;
									if (i == calcCount + 1) misszke = sg.yhzke + sg.hyzke + sg.plzke + sg.rulezke;//记录可能被清零的折扣额
									else misszke = sg.yhzke + sg.hyzke + sg.plzke;//记录可能被清零的折扣额

									if (rulePop.iszsz.charAt(0) == '1')
									{
										if (i == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.mjzke);
											sg.mjdjbh = rulePop.djbh;
											sg.mjzkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.mjzke;
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg), 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);
											calcComZkxe(k, sg.rulezke);
											sg.ruledjbh = rulePop.djbh;
											sg.rulezkfd = rulePop.zkfd;
											//统计当前规则促销的折扣金额
											superMarketRuleyhje += sg.rulezke;
										}
									}
									else
									{
										if (i == calcCount + 1)
										{
											sg.mjzke = 0;
											sg.mjzke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.mjzke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd = 0;
												sg.yhzkfd = 0;
												sg.rulezke = 0;
												sg.rulezkfd = 0;
												sg.ruledjbh = "";
												sg.yhdjbh = "";
												calcComZkxe(k, sg.mjzke);
												sg.mjdjbh = rulePop.djbh;
												sg.mjzkfd = rulePop.zkfd;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.mjzke - misszke;
											}
											else
											{
												sg.mjzke = 0;
											}
										}
										else
										{
											sg.rulezke = 0;
											sg.rulezke = ManipulatePrecision.doubleConvert(sg.hjje - getZZK(sg) + misszke, 2, 0)
													- ManipulatePrecision.doubleConvert(sg.sl * rulePop.yhlsj, 2, 0);

											if (sg.rulezke > misszke)//如果规则优惠大于一般优惠，规则优惠额记录规则优惠额跟一般优惠额的差额
											{
												sg.yhzke = 0;
												sg.hyzke = 0;
												sg.plzke = 0;
												sg.spzkfd = 0;
												sg.yhzkfd = 0;
												sg.yhdjbh = "";
												sg.ruledjbh = rulePop.djbh;
												calcComZkxe(k, sg.rulezke);
												sg.rulezkfd = rulePop.zkfd;
												//统计当前规则促销的折扣金额
												superMarketRuleyhje += sg.rulezke - misszke;
											}
											else
											{
												sg.rulezke = 0;
											}
										}
									}
									sg.isPopExc = 'Y';
								}
							}
							if (yhsl <= 0) break;
						}
					}
					//任意多级的，只判断一个结果
					break;
				}
				//对指定商品固定优惠金额
				if (rulePop.yhdjlb == 'V')
				{
					double yhsl = minbs * rulePop.yhhyj;//优惠数量
					double yhje = ManipulatePrecision.doubleConvert(minbs * rulePop.yhlsj, 2, 0);

					//结果为OR关系的时候 按照第一个结果的优惠数量来优惠剩余商品
					long and_flag = new Double(rulePop.presentjs).longValue();

					or_yhsl = yhsl;
					zje = 0;
					t_zje = 0;

					while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0 && j < rulePopList.size())
					{
						//统计参与优惠金额分配的商品总金额
						for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(or_yhsl, 0, 4) > 0; k++)
						{
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							//商品是否结果匹配
							if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
							{
								//商品拆分
								if (ManipulatePrecision.doubleCompare(sg.sl, or_yhsl, 4) <= 0)
								{
									or_yhsl -= sg.sl;
								}
								else
								{
									//拆分商品行
									splitSalecommod(k, or_yhsl);
									or_yhsl = 0;
								}
								zje += sg.hjje;
								t_zje += sg.hjje - getZZK(sg);
							}
						}
						if (and_flag == 1) break;
						j++;
					}
					if (ManipulatePrecision.doubleCompare(yhje, t_zje, 2) > 0) yhje = t_zje;

					//将优惠金额分担到商品明细上
					if (ManipulatePrecision.doubleCompare(zje, 0, 2) > 0 && ManipulatePrecision.doubleCompare(yhje, 0, 2) > 0
							&& ManipulatePrecision.doubleCompare(zje - yhje, 0, 2) >= 0)
					{
						int maxrow = -1;
						j = 0;
						t_zje = 0;
						while (and_flag == 1 || and_flag == 0 && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0 && j < rulePopList.size())
						{
							for (k = 0; k < saleGoods.size() && ManipulatePrecision.doubleCompare(yhsl, 0, 4) > 0; k++)
							{
								SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
								//商品是否结果匹配
								if (isMatchCommod(rulePop, k) && !(sg.isPopExc == 'N' || sg.isSMPopCalced == 'A'))
								{
									yhsl -= sg.sl;

									je = ManipulatePrecision.doubleConvert(sg.hjje / zje * yhje, 2, 0);
									if (ruleDef.type == '8')
									{
										sg.mjzke += je;
									}
									else
									{
										sg.rulezke += je;
									}
									//记录折扣分担
									sg.rulezkfd = rulePop.zkfd;
									//记录优惠单据编号
									sg.ruledjbh = rulePop.djbh;
									sg.isPopExc = 'Y';

									superMarketRuleyhje += je;
									t_zje += je;

									//记下金额最大的行号
									if (maxrow >= 0 && ManipulatePrecision.doubleCompare(sg.hjje, ((SaleGoodsDef) saleGoods.get(maxrow)).hjje, 2) > 0
											|| maxrow < 0) maxrow = k;
								}
							}
							if (and_flag == 1) break;
							j++;
						}
						//将未分配完的金额分配到金额最大的商品上
						if (ManipulatePrecision.doubleCompare(Math.abs(yhje - t_zje), 0, 2) > 0)
						{
							k = maxrow;
							SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
							if (ruleDef.type == '8')
							{
								sg.mjzke += yhje - t_zje;
							}
							else
							{
								sg.rulezke += yhje - t_zje;
							}
							superMarketRuleyhje += yhje - t_zje;
						}
					}
					//或条件时，已经优惠完了，所以要退出
					if (and_flag == 0) break;
				}
			}

			//将已参与规则促销的商品打上标志
			for (k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
				//商品是否条件匹配
				if (sg.isPopExc != ' ')
				{
					sg.isSMPopCalced = 'N';
					sg.isPopExc = ' ';
				}
			}
			//类别为8表示整单满减
			if (ruleDef.type == '8') break;
		}

		SaleGoodsDef sgd = null;
		for (k = 0; k < saleGoods.size(); k++)
		{
			sgd = (SaleGoodsDef) saleGoods.get(k);
			getZZK(sgd);
			// 批发销售最后计算手工折扣
			if (SellType.ISBATCH(saletype))
			{
				calcRebate(sgd, (GoodsDef)goodsAssistant.get(k));
			}
		}

		// 重算应收
		calcHeadYsje();

		// 刷新商品列表
		refreshSaleForm();
		return true;
	}
	
	// 批发销售最后计算手工折扣
	private void calcRebate(SaleGoodsDef sg, GoodsDef goods) 
	{
		if (sg.hjje - sg.hjzk > 0)
		{
			if (sg.num14 > 0)
			{
				sg.lszke = ManipulatePrecision.doubleConvert((100 - sg.num14) / 100 * (sg.hjje - getZZK(sg)), 2, 1);
				if (sg.lszke < 0) sg.lszke = 0;
				sg.lszke = getConvertPrice(sg.lszke, goods);
				getZZK(sg);
			}
			if (sg.num15 > 0)
			{
				sg.lszke = ManipulatePrecision.doubleConvert((100 - sg.num15) / 100 * (sg.hjje - getZZK(sg)), 2, 1);
				if (sg.lszzk < 0) sg.lszzk = 0;
				sg.lszzk = getConvertPrice(sg.lszzk, goods);
				getZZK(sg);
			}
		}
	}

	private void getCurRuleJc(int jc)
	{
		int i;
		String str1 = "";
		String str2 = "";
		// 获得条件在jc传入值的级别所对应的级别值
		for (i = 0; i < ruleReqList.size(); i++)
		{
			SuperMarketPopRuleDef reqDef = (SuperMarketPopRuleDef) ruleReqList.get(i);
			str1 = reqDef.ppistr1.split("\\|")[jc - 1];
			if (str1.length() > 0)
			{
				double a = Double.parseDouble(str1);
				if (a > 0) reqDef.yhlsj = a;
			}
		}
		for (i = 0; i < rulePopList.size(); i++)
		{
			SuperMarketPopRuleDef reqPop = (SuperMarketPopRuleDef) rulePopList.get(i);
			str1 = reqPop.ppistr1.split("\\|")[jc - 1];
			if (str1.length() > 0)
			{
				double a = Double.parseDouble(str1);
				if (a > 0) reqPop.yhlsj = a;
			}
			str2 = reqPop.ppistr2.split("\\|")[jc - 1];
			if (str2.length() > 0)
			{
				double b = Double.parseDouble(reqPop.ppistr2.split("\\|")[jc - 1]);
				if (b > 0) reqPop.yhhyj = b;
			}
		}
	}

	private boolean isMatchCommod(SuperMarketPopRuleDef ruleDef, int index)
	{
		SaleGoodsDef sg = ((SaleGoodsDef) saleGoods.get(index));
		GoodsDef goodsDef = ((GoodsDef) goodsAssistant.get(index));
		
		double tjzke = sg.lszke + sg.lszre + sg.lszzk + sg.lszzr + sg.yhzke + sg.rulezke;
		if ((ruleDef.yhdjlb == 'G' || (ruleDef.yhdjlb == '8' && ruleDef.ppistr3.equals("G"))) && tjzke > 0) return false;

		// 整单的规则,整单优先级最高 
		if (ruleDef.type == '8') return true;

		//只有正常的商品才参与规则促销
		if (sg.flag != '4' && sg.flag != '2') { return false; }

		//如果电子称商品不是排除条件
		if (ruleDef.presentsl != 1 || sg.flag != '2')
		{
			//如果电子称商品条件不是满减/满返，结果也不是满减/满返 
			if (!(ruleDef.yhdjlb == '8' && (ruleDef.ppistr3.length() > 0 && (ruleDef.ppistr3.charAt(0) == 'G' || ruleDef.ppistr3.charAt(0) == 'C')) || ruleDef.yhdjlb == 'G' || ruleDef.yhdjlb == 'C')
					&& sg.flag == '2') { return false; }
		}

		//条件为整单的时候如果是结果为非整单。此处就不判断商品是否参与了规则促销，
		//不然在结果匹配的时候会因为商品参与了非整单规则促销而无法参与整单的规则促销
		//在整单规则的时候初始化商品标识的时候使用
		if (((SuperMarketPopRuleDef) ruleReqList.get(0)).type != '8')
		{
			//不参与规则促销
			if (sg.isSMPopCalced != 'Y') { return false; }
		}

		switch (ruleDef.type)
		{
			case '1'://单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL"))) { return true; }
				break;
			case '2'://柜组
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				return true;
			case '3'://类别
				if (!ruleDef.code.equals(goodsDef.catid.substring(0, ruleDef.code.length()))) break;
				return true;
			case '4'://柜组品牌
				if (!ruleDef.code.equals(goodsDef.gz)) break;
				if (ruleDef.pp.equals(goodsDef.ppcode)) { return true; }
				break;
			case '5'://类别品牌
				if (!ruleDef.code.equals(goodsDef.catid.substring(0, ruleDef.code.length()))) break;
				if (ruleDef.pp.equals(goodsDef.ppcode)) { return true; }
				break;
			case '6'://品牌
				if (!ruleDef.code.equals(goodsDef.ppcode)) break;
				return true;
			case '7'://生鲜单品
				if (!ruleDef.code.equals(goodsDef.code)) break;
				if ((ruleDef.gz.equals(goodsDef.gz) || ruleDef.gz.equals("0")) && (ruleDef.spec.equals(goodsDef.uid) || ruleDef.spec.equals("AL"))) { return true; }
				break;
		}
		return false;
	}

	private boolean splitSalecommod(int n, double newsl)
	{
		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(n);
		GoodsDef goods = (GoodsDef) goodsAssistant.get(n);
		SpareInfoDef spare = (SpareInfoDef) goodsSpare.get(n);
		//		GoodsPopDef goodsPop = (GoodsPopDef) crmPop.get(n);

		if (saleGoods.size() <= 0 || n < 0 || n >= saleGoods.size()) return false;
		if (newsl >= sg.sl) return false;

		SaleGoodsDef newSg = (SaleGoodsDef) sg.clone();
		GoodsDef newGoods = (GoodsDef) goods.clone();
		SpareInfoDef newSpare = (SpareInfoDef) spare.clone();
		//		GoodsPopDef newGoodsPop = (GoodsPopDef) goodsPop.clone();

		double zje = sg.hjje;
		double rulezke = sg.rulezke;
		double mjzke = sg.mjzke;
		
		double lszke = sg.lszke;
		double lszzk = sg.lszzk;
		
		double hyzke = sg.hyzke;
		double yhzke = sg.yhzke;

		newSg.sl = sg.sl - newsl;
		sg.sl = newsl;

		//重算金额
		sg.hjje = ManipulatePrecision.doubleConvert(sg.sl * sg.jg, 2, 0);
		newSg.hjje = ManipulatePrecision.doubleConvert(newSg.sl * newSg.jg, 2, 0);

		//将拆分的商品的规则促销折扣金额进行分摊，此处必须分摊，不然会导致在计算整单的时候，出现成交金额为负数的情况
		sg.rulezke = (sg.hjje / zje) * rulezke;
		newSg.rulezke = (newSg.hjje / zje) * rulezke;
		sg.mjzke = (sg.hjje / zje) * mjzke;
		newSg.mjzke = (newSg.hjje / zje) * mjzke;
		
		sg.lszke = (sg.hjje / zje) * lszke;
		newSg.lszke = (newSg.hjje / zje) * lszke;
		sg.lszzk = (sg.hjje / zje) * lszzk;
		newSg.lszzk = (newSg.hjje / zje) * lszzk;
		sg.hyzke = (sg.hjje / zje) * hyzke;
		newSg.hyzke = (newSg.hjje / zje) * hyzke;
		sg.yhzke = (sg.hjje / zje) * yhzke;
		newSg.yhzke = (newSg.hjje / zje) * yhzke;

		getZZK(sg);
		getZZK(newSg);
		saleGoods.add(newSg);
		goodsAssistant.add(newGoods);
		goodsSpare.add(newSpare);
		
		// 刷新商品列表
		refreshSaleForm();
		return true;
	}

	private double calcComZkxe(int k, double zke)
	{
		if (k < 0 || k >= saleGoods.size()) return zke;

		SaleGoodsDef sg = (SaleGoodsDef) saleGoods.get(k);
		if (ManipulatePrecision.doubleCompare(sg.hjje - getZZK(sg), 0, 2) <= 0)
		{
			zke = 0;//引用数据类型,将实参清零
			zke = sg.hjje - getZZK(sg);
		}
		//计算价格精度
		zke = getConvertPrice(zke, (GoodsDef) goodsAssistant.elementAt(k));
		return zke;
	}

	public boolean paySellStart()
	{
		if (super.paySellStart())
		{
			if (SellType.ISSALE(saletype))
			{
				return doSuperMarketCrmPop();
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	public void paySellCancel()
	{
		if (originalSaleGoods != null)
		{
			saleGoods = cloneSaleGoodsVector(originalSaleGoods);
			goodsAssistant = cloneGoodsAssistantVector(originalGoodsAssistant);
			goodsSpare = cloneGoodsSpareVector(originalGoodsSpare);
		}
		super.paySellCancel();
		calcHeadYsje();
		// 刷新商品列表
		refreshSaleForm();
	}

	// 打印满赠和换购信息
	public void printSaleBill()
	{
		if (!SellType.ISEXERCISE(saletype))
		{
			Gzbh_NetService netService = new Gzbh_NetService();
			Vector giftList = new Vector();
			if (netService.getGift(giftList, saleHead))
			{
				saleHead.memo = "Y";
			}
			else
			{
				saleHead.memo = "N";
			}
			
			Vector hgList = new Vector();
			if (netService.getHGGift(hgList, saleHead))
			{
				saleHead.memo += "Y";
			}
			else
			{
				saleHead.memo += "N";
			}
			System.out.println(saleHead.memo);
		}
		super.printSaleBill();
	}
	
    public Vector cloneGoodsAssistantVector(Vector org)
    {
    	Vector v = new Vector();
    	
    	for (int i=0;i<org.size();i++)
    	{
    		v.add(((GoodsDef)org.elementAt(i)).clone());
    	}
    	
    	return v;
    }
    
    public Vector cloneGoodsSpareVector(Vector org)
    {
    	Vector v = new Vector();
    	
    	for (int i=0;i<org.size();i++)
    	{
    		v.add(((SpareInfoDef)org.elementAt(i)).clone());
    	}
    	
    	return v;
    }
}
