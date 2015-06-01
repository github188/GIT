package custom.localize.Bstd;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.ManipulateStr;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class Bstd_SaleBS1JfExchange extends Bstd_SaleBS0CmPop
{
	// 查找商品是否存在换购规则
	public void findJfExchangeGoods(int index)
	{
		if (hhflag == 'Y')
		{
			new MessageBox("换货状态不允许使用积分换购");
			return;
		}
		// 无会员卡不进行积分换购
		if (curCustomer == null)
		{
			new MessageBox("没有刷会员卡不允许积分换购");
			return;
		}

		// 无0509付款方式,不能进行积分换购
		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
		if (paymode == null)
		{
			new MessageBox("没有[0509]积分换购付款方式");
			return;
		}

		// 查找积分换购商品规则
		JfSaleRuleDef jfrd = new JfSaleRuleDef();
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.get(index);

		if (saleGoodsDef.mjzke > 0)
		{
			new MessageBox("该商品已享受过积分换购,不可多次换购");
			return;
		}

		if (!((Bstd_DataService) DataService.getDefault()).getJfExchangeGoods(jfrd, saleGoodsDef.code, curCustomer.code, curCustomer.type))
			return;

		if ((saleGoodsDef.hjje - saleGoodsDef.hjzk) <= jfrd.money * saleGoodsDef.sl)
		{
			new MessageBox("当前商品销售金额小于等于兑换金额\n不能进行换购");
			return;
		}

		if (curCustomer.valuememo < jfrd.jf)
		{
			if (GlobalInfo.sysPara.autojfexchange == 'Y')
				return;

			new MessageBox("当前会员卡的积分小于换购积分\n不能进行换购");
			return;
		}

		// 弹出提示框
		String message = "积分加上";
		MessageBox me = new MessageBox("您目前可用" + jfrd.jf * saleGoodsDef.sl + message + ManipulatePrecision.doubleToString(jfrd.money * saleGoodsDef.sl) + "元\n换购该商品\n是否要进行换购?", null, true);

		if (me.verify() != GlobalVar.Key1)
			return;

		if (curCustomer.valuememo - (jfrd.jf * saleGoodsDef.sl) < 0)
		{
			new MessageBox("商品数量过大,所需积分已超过会员当前总积分【" + curCustomer.valuememo + "】\n\n商品数量修改无效");
			return;
		}

		if (ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - jfrd.money * saleGoodsDef.sl, 2, 1) < 0)
		{
			new MessageBox("当前换购折扣过大\n不能进行换购");
			return;
		}

		clearZZK(saleGoodsDef);
		getZZK(saleGoodsDef);

		saleGoodsDef.mjzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - jfrd.money * saleGoodsDef.sl, 2, 1);
		saleGoodsDef.num4 = jfrd.jf * saleGoodsDef.sl;
		saleGoodsDef.yhdjbh = "w";
		curCustomer.valuememo = curCustomer.valuememo - jfrd.jf * saleGoodsDef.sl;

		getZZK(saleGoodsDef);
		calcHeadYsje();

		SaleGoodsDef sgd = (SaleGoodsDef) saleGoodsDef.clone();
		sgd.name = "(" + ManipulatePrecision.doubleToString(jfrd.money * saleGoodsDef.sl) + "元 + 积分" + saleGoodsDef.num4 + "换购)" + sgd.name;

		saleEvent.table.modifyRow(rowInfo(sgd), index);
		saleEvent.setTotalInfo();
		saleEvent.setCurGoodsBigInfo();
	}

	public boolean delJfExchangeByGoods(int index)
	{
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		if (info != null && info.char2 == 'Y')
		{
			int seq = -1;
			seq = Convert.toInt(info.str3.split(",")[0]);
			if (seq > -1)
			{
				// 检查付款方式里面对应的唯一键
				for (int i = 0; i < salePayment.size(); i++)
				{
					SalePayDef spd = (SalePayDef) salePayment.get(i);

					if (isJfExchangeSalePay(spd) && spd.num5 == seq)
					{
						if (!deleteSalePay(i))
						{
							new MessageBox("删除商品的积分换购付款失败!");

							return false;
						}

					}
				}
			}
		}

		return true;
	}

	public boolean r5SaleSaummary()
	{
		int i;
		SaleGoodsDef saleGoodsDef = null;
		SalePayDef salePayDef = null;
		int lastgoods = 0;
		double sswr_sysy = 0;
		double fk_sysy = 0;

		if (saleGoods == null || saleGoods.size() <= 0)
			return false;

		// 汇总商品明细
		for (i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			// 去掉营业员小计
			if (saleGoodsDef.flag == '0')
			{
				delSaleGoodsObject(i);
				i--;
				continue;
			}

			// 非削价商品且非批发或未定价商品:lsj = jg
			// 削价商品:lsj <> jg, lsj-jg=削价损失 -> thss
			// 批发销售:lsj <> jg, lsj-jg=批发损失 -> thss
			// 议价商品:lsj <> jg, lsj-jg=批发损失 -> thss
			if (saleGoodsDef.lsj <= 0 || (saleGoodsDef.flag != '3' && saleGoodsDef.flag != '6' && !SellType.ISBATCH(saletype)))
			{
				saleGoodsDef.lsj = saleGoodsDef.jg;
			}

			// 整理数据
			saleGoodsDef.rowno = i + 1;
			saleGoodsDef.fphm = saleHead.fphm;

			// 超市取单品折让/折扣授权是从saleGoodsDef.sqkh中取的
			/*
			 * if (saleGoodsDef.sqkh == null ||
			 * saleGoodsDef.sqkh.trim().length() <= 0) { saleGoodsDef.sqkh =
			 * cursqkh; saleGoodsDef.sqktype = cursqktype; saleGoodsDef.sqkzkfd
			 * = cursqkzkfd; }
			 */

			saleGoodsDef.hjzk = getZZK(saleGoodsDef);

			// 分摊损溢金额
			if (saleGoodsDef.flag != '1' && saleGoodsDef.type != '8')
			{
				saleGoodsDef.sswr_sysy = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / saleHead.ysje * saleHead.sswr_sysy);
				saleGoodsDef.fk_sysy = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk) / saleHead.ysje * saleHead.fk_sysy);

				sswr_sysy += saleGoodsDef.sswr_sysy;
				fk_sysy += saleGoodsDef.fk_sysy;
				lastgoods = i;
			}
		}

		// 损溢差额记入最后一个商品
		if (sswr_sysy != saleHead.sswr_sysy)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastgoods);
			saleGoodsDef.sswr_sysy += ManipulatePrecision.sub(saleHead.sswr_sysy, sswr_sysy);
		}
		if (fk_sysy != saleHead.fk_sysy)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(lastgoods);
			saleGoodsDef.fk_sysy += ManipulatePrecision.sub(saleHead.fk_sysy, fk_sysy);
		}

		// 汇总付款明细
		for (i = 0; i < salePayment.size(); i++)
		{
			salePayDef = (SalePayDef) salePayment.elementAt(i);

			// 整理数据
			salePayDef.rowno = i + 1;
			salePayDef.fphm = saleHead.fphm;

			// 检查卡号中是否存在非法字符
			if (salePayDef.payno != null && salePayDef.payno.trim().length() > 0)
			{
				salePayDef.payno = ManipulateStr.delSpecialChar(salePayDef.payno);
			}
		}

		// 整理数据
		saleHead.rqsj = ManipulateStr.interceptExceedStr(saleHead.rqsj, 20);
		saleHead.hykh = ManipulateStr.interceptExceedStr(saleHead.hykh, 20);
		saleHead.jfkh = ManipulateStr.interceptExceedStr(saleHead.jfkh, 20);
		saleHead.thsq = ManipulateStr.interceptExceedStr(saleHead.thsq, 20);
		saleHead.ghsq = ManipulateStr.interceptExceedStr(saleHead.ghsq, 20);
		saleHead.hysq = ManipulateStr.interceptExceedStr(saleHead.hysq, 20);
		saleHead.sqkh = ManipulateStr.interceptExceedStr(saleHead.sqkh, 20);
		saleHead.buyerinfo = ManipulateStr.interceptExceedStr(saleHead.buyerinfo, 20);
		saleHead.salefphm = ManipulateStr.interceptExceedStr(saleHead.salefphm, 20);

		saleHead.jdfhdd = ManipulateStr.interceptExceedStr(saleHead.jdfhdd, 20);

		// 计算本次积分
		// saleHead.bcjf = calcSaleBCJF();
		// saleHead.ljjf = calcSaleLJJF();

		// 记录商品付款分摊
		if (!paymentApportionSummary())
			return false;

		return true;

	}

	public boolean saleSummary()
	{
		if (!r5SaleSaummary())
			return false;

		// 记录促销明细到商品str3
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);
			if (goodsSpare == null || goodsSpare.size() <= i)
				continue;
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);
			if (spinfo == null)
				continue;

			// sg.str3 = 促销序号:促销金额
			// popzk = 促销序号,促销金额,促销备注
			sg.str3 = "";
			for (int j = 0; spinfo.popzk != null && j < spinfo.popzk.size(); j++)
			{
				String[] s = (String[]) spinfo.popzk.elementAt(j);
				sg.str3 += "," + s[0] + ":" + s[1];

				if (s.length >= 3 && s[2] != null && !s[2].equals(""))
				{
					sg.str3 += ":" + s[2];
				}
			}

			if (sg.str3.length() > 0)
				sg.str3 = sg.str3.substring(1);
		}

		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sg = (SaleGoodsDef) saleGoods.elementAt(i);

			if (goodsSpare == null || goodsSpare.size() <= i)
				continue;

			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(i);

			if (spinfo == null)
				continue;

			// 如果是一个积分换购的商品
			if (spinfo.char2 == 'Y')
			{
				int seq = -1;

				// 取付款序号
				seq = Convert.toInt(spinfo.str3.split(",")[0]);

				if (seq > -1)
				{
					// 检查付款方式里面对应的唯一键
					for (int j = 0; j < salePayment.size(); j++)
					{
						SalePayDef spd = (SalePayDef) salePayment.get(j);

						if (isJfExchangeSalePay(spd) && spd.num5 == seq)
						{
							spd.idno = spd.idno + "," + sg.rowno;

						}
					}
				}
			}
		}

		return true;
	}

	public boolean deleteSalePay(int index)
	{
		int salejf = 0;
		Payment p = (Payment) payAssistant.elementAt(index);

		if (isJfExchangeSalePay(p.salepay))
		{
			String salepaylist[] = p.salepay.idno.split(",");
			salejf = (int) Double.parseDouble(salepaylist[0]);
			curCustomer.valuememo = curCustomer.valuememo + salejf;

		}

		if (!super.deleteSalePay(index))
		{
			if (curCustomer != null)
			{
				curCustomer.valuememo = curCustomer.valuememo - salejf;
			}

			return false;
		}

		return true;
	}

	public boolean deleteAllSalePay()
	{
		Vector tempSalePayment = null;
		Vector tempPayAssistant = null;

		try
		{
			tempSalePayment = new Vector();
			tempPayAssistant = new Vector();

			// 先保存换购付款
			for (int i = 0; i < salePayment.size(); i++)
			{
				SalePayDef tempspay = (SalePayDef) salePayment.elementAt(i);
				Payment tempp = (Payment) payAssistant.elementAt(i);

				if (isJfExchangeSalePay(tempspay))
				{
					tempSalePayment.add(tempspay);
					tempPayAssistant.add(tempp);
				}
			}

			// 删除所有付款
			if (!super.deleteAllSalePay())
				return false;

			// 恢复换购的付款
			for (int i = 0; i < tempSalePayment.size(); i++)
			{
				salePayment.add(tempSalePayment.elementAt(i));
				payAssistant.add(tempPayAssistant.elementAt(i));

				SalePayDef tempspay = (SalePayDef) salePayment.elementAt(i);

				String salepaylist[] = tempspay.idno.split(",");
				int salejf = (int) Double.parseDouble(salepaylist[0]);
				curCustomer.valuememo = curCustomer.valuememo - salejf;

				// 重新检查是否有积分换购的商品，重新记录分摊
				for (int j = 0; j < saleGoods.size(); j++)
				{
					SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(j);
					if (info.char2 == 'Y')
					{
						int seqnum = Convert.toInt(info.str3.split(",")[0]);
						// 先保存换购付款
						for (int k = 0; k < salePayment.size(); k++)
						{
							if (tempspay.num5 == seqnum)
							{
								if (info.payft == null)
									info.payft = new Vector();
								String[] ft = new String[] { String.valueOf(tempspay.num5), tempspay.paycode, tempspay.payname, ManipulatePrecision.doubleToString(tempspay.je - tempspay.num1) };
								info.payft.add(ft);
								break;
							}
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
		finally
		{
			if (tempSalePayment != null)
			{
				tempSalePayment.clear();
				tempSalePayment = null;
			}

			if (tempPayAssistant != null)
			{
				tempPayAssistant.clear();
				tempPayAssistant = null;
			}
		}
	}

	public boolean delSaleGoodsObject(int index)
	{
		// 删除商品前先删除对应的积分换购付款
		if (!delJfExchangeByGoods(index))
			return false;

		// 删除商品
		if (!super.delSaleGoodsObject(index)) { return false; }

		return true;
	}

	public boolean isJfExchangeSalePay(SalePayDef spd)
	{
		if (spd.paycode.trim().equals("0509") && spd.memo.trim().equals("2"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void addMemoPayment()
	{
		super.addMemoPayment();

		// 在存在积分换购时，积分换购的付款方式放入memoPayment里， 如果salePayment大于0代表已经放入付款类表里，不重新放入
		if (isNewUseSpecifyTicketBack(false) && salePayment.size() <= 0)
		{
			for (int i = 0; i < memoPayment.size(); i++)
			{
				Payment pay = (Payment) memoPayment.elementAt(i);

				if (pay.salepay != null)
					addSalePayObject(pay.salepay, pay);
			}
		}
	}

	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods, Vector thsalePayment)
	{
		// 在断点保护的情况下，可能出现退货付款方式存入2次的情况，加以判断
		memoPayment.removeAllElements();

		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(i);

			// liwenjin Add 在退货时添加对应的分摊类
			// goodsSpare.add(new SpareInfoDef());

			// 小票头记录原单号
			saleHead.yfphm = String.valueOf(sgd.yfphm);
			saleHead.ysyjh = sgd.syjh;

			// 积分换购付款方式处理
			if (sgd.str2.indexOf("0509") >= 0)
			{
				int st = sgd.str2.lastIndexOf(",", sgd.str2.indexOf("0509"));
				int end = sgd.str2.indexOf(",", sgd.str2.indexOf("0509"));
				if (st <= 0)
					st = 0;
				if (end <= 0)
					end = sgd.str2.length();
				String line = sgd.str2.substring(st, end);

				if (line.charAt(0) == ',')
					line.substring(1);

				String rowno = line.substring(0, line.indexOf(":"));
				int rowno1 = -1;
				try
				{
					rowno1 = Integer.parseInt(rowno);
					rowno1--;
				}
				catch (Exception er)
				{
					er.printStackTrace();
				}

				if (rowno1 >= 0)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.elementAt(rowno1);
					if (spd1.paycode.equals("0509"))
					{
						PayModeDef pmd = DataService.getDefault().searchPayMode("0509");
						PaymentCustJfSale pay = new PaymentCustJfSale(pmd, this);

						if (sgd.sl < sgd.memonum1)
						{
							String salepaylist[] = spd1.idno.split(",");
							double jf = Double.parseDouble(salepaylist[0]);
							jf = ManipulatePrecision.doubleConvert(jf * (sgd.sl / sgd.memonum1));
							spd1.je = ManipulatePrecision.doubleConvert(spd1.je * (sgd.sl / sgd.memonum1));
							spd1.idno = jf + "," + spd1.idno.substring(spd1.idno.indexOf(",") + 1);

						}

						pay.salepay = spd1;
						memoPayment.add(pay);
					}
				}
			}

			// 将原付款分摊删除
			sgd.str2 = "";
		}
	}
}
