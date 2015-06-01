package custom.localize.Zspj;

import org.eclipse.swt.SWT;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.swtdesigner.SWTResourceManager;
import bankpay.Bank.JavaZrxZSPJ_PaymentBankFunc;


public class Zspj_SaleBS1Zrx extends Zspj_SaleBS0CmPop
{
	private boolean startBroken = false;

	private boolean sendFlag = false;

	private boolean isBatchUpdate = false;

	// 发送商品单个流水
	public void doSaleGoodsDisplayEvent(SaleGoodsDef oldGoods, int index)
	{
		
		if (SellType.ISCHECKINPUT(saletype))
			checkIndex = index;
		
		// 未启用则直接返回
		if (!JavaZrxZSPJ_PaymentBankFunc.ISUSED)
			return;

		if (isBatchUpdate)
			return;

		SaleGoodsDef zrxSendGoods = null;

		// index行的商品被修改
		if (oldGoods != null && index >= 0)
		{
			zrxSendGoods = (SaleGoodsDef) saleGoods.elementAt(index);

			JavaZrxZSPJ_PaymentBankFunc.sendFlow(oldGoods, false, saletype);

			JavaZrxZSPJ_PaymentBankFunc.sendFlow(zrxSendGoods, true, saletype);
		}

		// index行的商品被删除
		if (oldGoods != null && index < 0)
			JavaZrxZSPJ_PaymentBankFunc.sendFlow(oldGoods, false, saletype);

		// case 3: // index行的商品被新增
		if (oldGoods == null && index >= 0)
		{
			if (startBroken)
			{
				if (!sendFlag)
				{
					zrxSendGoods = (SaleGoodsDef) saleGoods.elementAt(index);
					JavaZrxZSPJ_PaymentBankFunc.sendFlow(zrxSendGoods, true,
							saleHead.djlb);

					if (index == saleGoods.size() - 1)
						sendFlag = true;
				}

				// 断点数据已发完
				if (sendFlag)
					startBroken = false;
			}
			else
			{
				zrxSendGoods = (SaleGoodsDef) saleGoods.elementAt(index);
				JavaZrxZSPJ_PaymentBankFunc.sendFlow(zrxSendGoods, true,
						saletype);
			}
		}
	}

	// 发送商品批量流水
	public void doSaleGoodsDisplayFinishedEvent()
	{
		if (!JavaZrxZSPJ_PaymentBankFunc.ISUSED)
			return;

		if (isBatchUpdate)
		{
			// 整单取消
			JavaZrxZSPJ_PaymentBankFunc.sendCancel(
					ConfigClass.CashRegisterCode, GlobalInfo.syjStatus.fphm);

			// 逐条发送流水
			for (int i = 0; i < saleGoods.size(); i++)
				JavaZrxZSPJ_PaymentBankFunc.sendFlow((SaleGoodsDef) saleGoods
						.elementAt(i), true, saletype);
		}
		isBatchUpdate = false;
	}

	public boolean isRetailSale()
	{
		// 知而行未启用直接反回
		if (!JavaZrxZSPJ_PaymentBankFunc.ISUSED)
			return false;

		// 若启用后则对知而行当前交易状态进行判断(仅适用于零售销售状态)
		// 不论知而行是否启用，只要在非零售状态下均视为未启用
		if (!SellType.RETAIL_SALE.equals(saletype)
				&& !SellType.RETAIL_BACK.equals(saletype))
		{
			JavaZrxZSPJ_PaymentBankFunc.ISUSED = false;
			return false;
		}

		return true;
	}

	// 初始化交易
	public void initNewSale()
	{
		super.initNewSale();

		// if (JavaZrxZSPJ_PaymentBankFunc.PAYCODE.equals(""))
		JavaZrxZSPJ_PaymentBankFunc.loadConfig();

		// 检查交易状态
		isRetailSale();

		// 知而行未启用，则下面的操作将不执行
		if (JavaZrxZSPJ_PaymentBankFunc.ISUSED)
		{
			// 用状态标志知而行交易状态
			if (JavaZrxZSPJ_PaymentBankFunc.CurStatus != JavaZrxZSPJ_PaymentBankFunc.PAY_DONE)
				JavaZrxZSPJ_PaymentBankFunc
						.sendCancel(ConfigClass.CashRegisterCode,
								GlobalInfo.syjStatus.fphm);

			isBatchUpdate = false;

			// 系统启动（包括断点启动）时
			if (JavaZrxZSPJ_PaymentBankFunc.CurStatus == JavaZrxZSPJ_PaymentBankFunc.PAY_INIT)
			{
				if (checkBrokenData() != null)
					startBroken = true;
			}
		}
	}

	// 在界面显示知而行网络连接状态
	public void setInfoGUI()
	{
		// 知而行启用状态下才提示断网
		if (JavaZrxZSPJ_PaymentBankFunc.ISUSED)
		{ // 系统启动均视知而行网络状态为联网状态，等完成一次交易后再判断最终的网络状态
			if (!JavaZrxZSPJ_PaymentBankFunc.ISNETCONN)
			{
				this.saleEvent.yfje.setFont(SWTResourceManager.getFont("宋体",
						20, SWT.BOLD));
				this.saleEvent.yfje.setText("知而行断网");
			}
		}
	}

	// 处理断点启动
	public void doBrokenData()
	{
		// 处理在非零售销售状态下的断点启动,发送一次cancel
		if (!isRetailSale())
			JavaZrxZSPJ_PaymentBankFunc.sendCancel(
					ConfigClass.CashRegisterCode, GlobalInfo.syjStatus.fphm);

		SalePayDef salePayDef = null;
		try
		{
			if (salePayment.size() < 1)
				return;

			for (int i = salePayment.size(); i > 0; i--)
			{
				salePayDef = (SalePayDef) salePayment.elementAt(i - 1);
				// 如果配置文件中只定义了一种付款对象，作如下处理
				if (JavaZrxZSPJ_PaymentBankFunc.ISUSED
						&& JavaZrxZSPJ_PaymentBankFunc.PAYCODE.equals(""))
				{
					if (salePayDef.payname.indexOf("知而行") != -1)
						JavaZrxZSPJ_PaymentBankFunc.PAYCODE = salePayDef.paycode;
				}
				if (salePayDef.paycode
						.equals(JavaZrxZSPJ_PaymentBankFunc.PAYCODE))
					salePayment.removeElementAt(i - 1);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 会员更新
	public void customerIsHy(CustomerDef cust)
	{
		super.customerIsHy(cust);

		isBatchUpdate = true;
	}

	// 整单折扣
	public boolean inputAllRebate()
	{
		/*
		 * if (super.inputAllRebate()) { isBatchUpdate = true; return true; }
		 * return false;
		 */

		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			grantgz = staff.grantgz;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleHead.sqkh = staff.gh;
			saleHead.sqktype = '1';
			saleHead.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权整单折扣,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl
					* 100 + "%,授权:" + staff.gh;
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

		// 计算商品能否打折
		boolean rebate = false;
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

			rebate = true;
			break;
		}

		if (!rebate)
		{
			new MessageBox("整单没有可打折的商品，不能手工折扣");
			return false;
		}

		String maxzzklmsg = "该收银员正在进行整单打折";

		// 总折扣计算模式为批量单品折扣模式
		if (GlobalInfo.sysPara.batchtotalrebate == 'Y')
		{
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 "
						+ ManipulatePrecision.doubleToString(grantzkl * 100, 2,
								1, true)
						+ "%\n你目前最多在权限内交易额基础上再打折 "
						+ ManipulatePrecision.doubleToString(grantzkl * 100, 2,
								1, true) + "%";
			}

			// 输入折扣
			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)", "整单折扣", maxzzklmsg,
					buffer, grantzkl * 100, 100, true)) { return false; }

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

				// 计算权限允许的最大折扣额
				double maxzzkl = 0;
				if (grantflag)
				{
					// 允许突破最低折扣
					maxzzkl = getMaxRebateGrant(grantzkl, 0);
				}
				else
				{
					// 不允许最低折扣
					maxzzkl = getMaxRebateGrant(grantzkl, goodsDef);
				}

				// 计算最终折扣
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(
						(100 - zkl) / 100
								* (saleGoodsDef.hjje - getZZK(saleGoodsDef)),
						2, 1);
				if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert(
						(saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1))
				{
					// 提示
					new MessageBox("[" + saleGoodsDef.code + "]"
							+ saleGoodsDef.name + "\n\n最多能打折 "
							+ ManipulatePrecision.doubleToString(maxzzkl * 100)
							+ "%");

					//
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef)
							- ManipulatePrecision.doubleConvert(
									(saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1);
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(
							saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0)
					saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);

				// 重算商品折扣合计
				getZZK(saleGoodsDef);
			}
		}
		else
		{
			// 计算整单最打可打折金额
			double sumzzk = 0, sumlszzk = 0, lastzzk = 0, hjcjj = 0, hjzke = 0;
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

				// 不在授权范围,只要是权限范围内的商品不管商品本身能不能打折，都参与总折计算，然后分摊时不分摊
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 累计可折扣金额
				sumzzk += ManipulatePrecision.doubleConvert((1 - grantzkl)
						* saleGoodsDef.hjje, 2, 1);
				sumlszzk += saleGoodsDef.lszzk;
				hjcjj += saleGoodsDef.hjje - saleGoodsDef.hjzk;
				hjzke += saleGoodsDef.hjzk;

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
				{
					continue;
				}

				// 计算商品权限允许的最大折扣额,找可折让金额最大的商品
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
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl)
						* saleGoodsDef.hjje, 2, 1);
				if (maxzzk > lastzzk)
				{
					lastzzk = maxzzk;
					lastzzkrow = i;
				}
			}

			// 反算得到当前最大打折比例
			double lszkl = (sumzzk - hjzke + sumlszzk) / (hjcjj + sumlszzk);
			if (lszkl < 0)
				lszkl = 0;
			lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

			// 输入折扣
			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 "
						+ ManipulatePrecision.doubleToString(grantzkl * 100, 2,
								1, true) + "%\n你目前最多在权限内交易额基础上再打折 "
						+ ManipulatePrecision.doubleToString(lszkl, 2, 1, true)
						+ "%\n提示:【最终折扣金额受单品最低折扣率限制】";
			}

			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)"
					+ (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折扣",
					maxzzklmsg, buffer, lszkl, 100, true)) { return false; }

			// 得到折扣金额,打折后按收银机定义四舍五入
			double zkl = Double.parseDouble(buffer.toString());
			double zzkje = ManipulatePrecision.doubleConvert((100 - zkl) / 100
					* (hjcjj + sumlszzk), 2, 1);
			double tempysje = (saleHead.hjzje - saleHead.hjzke + sumlszzk)
					- zzkje;
			double tempyfje = getDetailOverFlow(tempysje);
			zzkje = ManipulatePrecision.sub(zzkje, ManipulatePrecision.sub(
					tempyfje, tempysje));

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

				// 不在授权范围
				if (!checkGoodsGrantRange(goodsDef, grantgz))
				{
					continue;
				}

				// 不能打折
				if (!checkGoodsRebate(goodsDef))
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
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl)
						* saleGoodsDef.hjje, 2, 1);

				// 取消其他手工折扣,计算最终折扣
				saleGoodsDef.sqkh = "";
				saleGoodsDef.sqktype = '\0';

				// 每个商品分摊的折让按金额占比计算
				if (i != lastzzkrow)
				{
					if (GlobalInfo.sysPara.batchtotalrebate == 'N')
					{
						saleGoodsDef.lszzk = ManipulatePrecision
								.doubleConvert(
										(saleGoodsDef.hjje - saleGoodsDef.hjzk + saleGoodsDef.lszzk)
												/ (hjcjj + sumlszzk) * zzkje,
										2, 1);
					}
					else
					{
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(
								maxzzk / sumzzk * zzkje, 2, 1);
					}
					if (getZZK(saleGoodsDef) > maxzzk)
					{
						saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - maxzzk;
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(
								saleGoodsDef.lszzk, 2, 1);
					}
					if (saleGoodsDef.lszzk < 0)
						saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);
					saleGoodsDef.lszzk = getConvertRebate(i,
							saleGoodsDef.lszzk, getGoodsApportionPrecision());

					// 重算商品折扣合计
					getZZK(saleGoodsDef);

					// 计算已分摊的总折让
					hjzzk += saleGoodsDef.lszzk;
				}
			}

			// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
			if (lastzzkrow >= 0)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzzkrow);
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(zzkje
						- hjzzk, 2, 1);
				if (getZZK(saleGoodsDef) > lastzzk)
				{
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - lastzzk;
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(
							saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0)
					saleGoodsDef.lszzk = 0;
				getZZK(saleGoodsDef);
			}
		}

		// 重算小票应收
		calcHeadYsje();

		isBatchUpdate = true;

		return true;
	}

	// 整单折让
	public boolean inputAllRebatePrice()
	{
		if (saleGoods.size() <= 0) { return false; }

		double grantzkl = 0;
		String grantgz = null;
		boolean grantflag = false;
		SaleGoodsDef saleGoodsDef = null;
		GoodsDef goodsDef = null;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		// 授权
		if ((curGrant.zpzkl * 100) >= 100)
		{
			OperUserDef staff = inputAllRebateGrant();
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.zpzkl;
			grantgz = staff.grantgz;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleHead.sqkh = staff.gh;
			saleHead.sqktype = '1';
			saleHead.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权整单折让,小票号:" + saleHead.fphm + ",折扣权限:" + grantzkl
					* 100 + "%,授权:" + staff.gh;
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

		// 计算整单最打可打折金额
		double sumzzr = 0, summxzzr = 0, sumlszzr = 0, lastzre = 0, hjzke = 0;
		int lastzrerow = -1;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);

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

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			// 累计可折让金额
			summxzzr += ManipulatePrecision.doubleConvert((1 - grantzkl)
					* saleGoodsDef.hjje, 2, 1);
			sumlszzr += saleGoodsDef.lszzr;
			hjzke += saleGoodsDef.hjzk;

			// 不能打折
			if (!checkGoodsRebate(goodsDef, info))
			{
				continue;
			}

			// 计算每个商品权限允许的最大折扣额,找可折让金额最大的商品
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
			double maxzzr = 0;
			if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
			{
				maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl)
						* (saleGoodsDef.hjje - (saleGoodsDef.hjzk
								- saleGoodsDef.lszke - saleGoodsDef.lszre
								- saleGoodsDef.lszzk - saleGoodsDef.lszzr)), 2,
						1);
			}
			else
			{
				maxzzr = ManipulatePrecision
						.doubleConvert(
								(1 - maxzkl)
										* (saleGoodsDef.hjje - (saleGoodsDef.hjzk - saleGoodsDef.lszzr)),
								2, 1);
			}
			sumzzr += maxzzr;
			if (maxzzr > lastzre)
			{
				lastzre = maxzzr;
				lastzrerow = i;
			}
		}

		if (summxzzr <= 0)
		{
			new MessageBox("整单没有可打折的商品，不能手工折扣");
			return false;
		}

		// 输入折让
		double zzrje = 0;

		String maxzzrmsg = "该收银员正在进行整单折让";

		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			// double lszzr = saleHead.hjzje - summxzzr;
			double lszzr = saleHead.hjzje - sumzzr; // 总金额-最大折让金额=可接受的最低价格
			lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = "收银员对权限内商品的总折扣权限为 "
						+ ManipulatePrecision.doubleToString(grantzkl * 100, 2,
								1, true) + "%\n商品折让权限范围为【"
						+ ManipulatePrecision.doubleToString(lszzr, 2, 1, true)
						+ " --- " + saleHead.hjzje + "】元";
				// maxzzrmsg = "\n收银员折让权限范围为【" +
				// ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + " ---
				// "+saleHead.hjzje +"】元";
			}

			if (!new TextBox().open("请输入整单折让后的成交价"
					+ (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折让",
					maxzzrmsg, buffer, lszzr, saleHead.hjzje, true)) { return false; }

			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;
				getZZK(saleGoodsDef);
			}
			calcHeadYsje();

			zzrje = saleHead.hjzje - saleHead.hjzke
					- Double.parseDouble(buffer.toString());
			zzrje = ManipulatePrecision.doubleConvert(zzrje, 2, 1);
		}
		else
		{
			// 计算最大可折让金额
			double lszzr = sumzzr - hjzke + sumlszzr;
			lszzr = ManipulatePrecision.doubleConvert(lszzr, 2, 1);
			if (lszzr < 0)
				lszzr = 0;

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzzrmsg = "收银员对权限范围内商品的总折扣权限为 "
						+ ManipulatePrecision.doubleToString(grantzkl * 100, 2,
								1, true) + "%\n商品折让权限范围为【0 --- "
						+ ManipulatePrecision.doubleToString(lszzr, 2, 1, true)
						+ "】元";
				// maxzzrmsg = "\n收银员折让权限范围为【0 --- " +
				// ManipulatePrecision.doubleToString(lszzr, 2, 1, true) + "】元";
			}

			if (!new TextBox().open("请输入整单要折让的金额"
					+ (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折让",
					maxzzrmsg, buffer, 0, lszzr, true)) { return false; }
			zzrje = Double.parseDouble(buffer.toString());
			zzrje = ManipulatePrecision.doubleConvert(zzrje, 2, 1);
		}

		// 把总折让额分摊到每个商品
		double hjzzr = 0;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);

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

			// 不在授权范围
			if (!checkGoodsGrantRange(goodsDef, grantgz))
			{
				continue;
			}

			// 不能打折
			if (!checkGoodsRebate(goodsDef, info))
			{
				continue;
			}

			// 取消其他手工折扣,计算最终折扣
			saleGoodsDef.sqkh = "";
			saleGoodsDef.sqktype = '\0';

			// 每个商品分摊的折让按金额占比计算
			if (i != lastzrerow)
			{
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
				saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(
						(1 - maxzkl) * (saleGoodsDef.hjje - saleGoodsDef.hjzk)
								/ sumzzr * zzrje, 2, 1);
				double maxzzr = ManipulatePrecision.doubleConvert((1 - maxzkl)
						* saleGoodsDef.hjje, 2, 1);
				if (getZZK(saleGoodsDef) > maxzzr)
				{
					saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - maxzzr;
					saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(
							saleGoodsDef.lszzr, 2, 1);
				}
				if (saleGoodsDef.lszzr < 0)
					saleGoodsDef.lszzr = 0;
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr);
				saleGoodsDef.lszzr = getConvertRebate(i, saleGoodsDef.lszzr,
						getGoodsApportionPrecision());

				// 重算商品折扣合计
				getZZK(saleGoodsDef);

				// 计算已分摊的总折让
				hjzzr += saleGoodsDef.lszzr;
			}
		}

		// 可折让金额最大商品的折扣用减法直接等于剩余的分摊,最后计算
		if (lastzrerow >= 0)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastzrerow);
			saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(zzrje
					- hjzzr, 2, 1);
			if (getZZK(saleGoodsDef) > lastzre)
			{
				saleGoodsDef.lszzr -= getZZK(saleGoodsDef) - lastzre;
				saleGoodsDef.lszzr = ManipulatePrecision.doubleConvert(
						saleGoodsDef.lszzr, 2, 1);
			}
			if (saleGoodsDef.lszzr < 0)
				saleGoodsDef.lszzr = 0;
			getZZK(saleGoodsDef);
		}

		// 重算小票应收
		calcHeadYsje();

		isBatchUpdate = true;

		return true;
		/*
		 * if (super.inputAllRebatePrice()) { isBatchUpdate = true; return true; }
		 * return false;
		 */
	}

	// 退货业务中不让删除已付款
	public boolean isDeletePay(int index)
	{
		if (!JavaZrxZSPJ_PaymentBankFunc.ISUSED)
			return super.isDeletePay(index);

		Payment p = null;
		if (index >= 0)
			p = (Payment) payAssistant.elementAt(index);

		if (SellType.ISBACK(saleHead.djlb)
				&& p.salepay.paycode
						.equals(JavaZrxZSPJ_PaymentBankFunc.PAYCODE))
		{
			new MessageBox("该付款方式无法删除");
			return false;
		}
		else
			return super.isDeletePay(index);
	}

	// 发送交易完成信息
	public boolean checkFinalStatus()
	{
		if (JavaZrxZSPJ_PaymentBankFunc.ISUSED)
		{
			JavaZrxZSPJ_PaymentBankFunc.sendPayInfo(saleHead, salePayment);
			JavaZrxZSPJ_PaymentBankFunc.sendFinish(saleHead);
		}
		return super.checkFinalStatus();
	}

}
