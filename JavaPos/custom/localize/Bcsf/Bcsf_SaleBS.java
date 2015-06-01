package custom.localize.Bcsf;

import java.util.Vector;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Payment.PaymentCustLczc;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_DataService;
import custom.localize.Bstd.Bstd_SaleBS;

public class Bcsf_SaleBS extends Bstd_SaleBS
{
	public void enterInput()
	{
		if (SellType.ISCARD(saletype))
			sellSingleCard();
		else
			super.enterInput();
	}

	public void backSell()
	{
		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡交易不允许退货!");
			return;
		}
		super.backSell();
	}

	public void paySellByZero()
	{
		if (SellType.ISSTAMP(saletype) && GlobalInfo.sysPara.issaleby0 == 'Y' && calcHeadYfje() <= 0)
		{
			for (int i = 0; i < GlobalInfo.payMode.size(); i++)
			{
				PayModeDef pmd = (PayModeDef) GlobalInfo.payMode.get(i);

				if (pmd.ismj == 'Y' && pmd.type == '1' && pmd.iszl == 'Y' && pmd.code.equals("0108"))
				{
					// 创建一个付款方式对象
					Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, saleEvent.saleBS);

					if (pay == null)
						continue;

					// inputPay这个方法根据不同的付款方式进行重写
					SalePayDef sp = pay.inputPay("1");

					payAccount(pay, sp);

					sp.je = 0;
					sp.ybje = 0;
					calcPayBalance();
					break;
				}
			}
		}
		else
		{
			super.paySellByZero();
		}
	}

	public void paySell()
	{
		char tmppara = GlobalInfo.sysPara.issaleby0;

		try
		{
			if (SellType.ISSTAMP(saletype))
				GlobalInfo.sysPara.issaleby0 = 'Y';

			super.paySell();
		}
		finally
		{
			GlobalInfo.sysPara.issaleby0 = tmppara;
		}
	}

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡交易不允许添加商品!");
			return false;
		}

		return super.findGoods(code, yyyh, gz, memo);
	}

	public boolean checkFindGoodsAllowSale(GoodsDef goodsDef, double quantity, boolean isdzcm, double dzcmsl, double dzcmjg)
	{
		if (SellType.ISSALE(saletype) && !SellType.ISSTAMP(saletype))
		{
			if (goodsDef.type == 'S')
			{
				new MessageBox("该商品为印花换购商品,零售状态下不允许售卖!");
				return false;
			}
		}
		return super.checkFindGoodsAllowSale(goodsDef, quantity, isdzcm, dzcmsl, dzcmjg);
	}

	public boolean allowEditGoods()
	{
		if (SellType.ISCARD(saletype))
		{
			new MessageBox("售卡交易不允许修改!");
			return false;
		}

		if (SellType.ISSTAMP(saletype))
		{
			new MessageBox("印花换购不允许修改!");
			return false;
		}
		return super.allowEditGoods();
	}

	protected void initBusiness()
	{
		if (SellType.ISCARD(saletype))
		{
			sellSingleCard();
		}

		super.initBusiness();
	}

	public void addCmpopSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if (SellType.ISSTAMP(saletype))
		{
			String[] row = new String[2];
			if (!((Bcsf_NetService) NetService.getDefault()).findStampGoods(goods, row))
				return;

			if (row.length == 2)
			{

				sg.num1 = Convert.toDouble(row[0]); // 记录换购价
				sg.num2 = Convert.toDouble(row[1]); // 记录换购的印花数

				sg.jg = ManipulatePrecision.doubleConvert(sg.num1, 2, 1);
				sg.hjje = ManipulatePrecision.doubleConvert(sg.jg * sg.sl, 2, 1);

				sg.name += "(" + sg.num1 + "RMB+" + sg.num2 + "印花)";
			}
		}

		super.addCmpopSaleGoodsObject(sg, goods, info);

	}

	public void findGoodsRuleFromCRM(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		((Bcsf_NetService) NetService.getDefault()).findGoodsCouponRule(goods, goods.code, this.saleHead.rqsj, NetService.getDefault().getMemCardHttp(11));
	}

	public void findGoodsCMPOPInfo(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		// 卖卡及印花交易不查找
		if (!SellType.ISSALE(saletype) || SellType.ISCARD(saletype))
			return;

		// 券连网使用，查找网上失败后不查询本地
		findGoodsRuleFromCRM(sg, goods, info);

		if (SellType.ISSTAMP(saletype))
			return;

		// 查找商品的促销结果集
		Vector popvec = ((Bstd_DataService) DataService.getDefault()).findCMPOPGoods(saleHead.rqsj, goods, curCustomer != null ? curCustomer.code : "", curCustomer != null ? curCustomer.type : "");
		goodsCmPop.add(popvec);

		filterCMPOPInfo(popvec);
	}

	public void calcGoodsVIPRebate(int index)
	{
		//卖卡及印花交易不计算会员
		if (SellType.ISCARD(saletype) || SellType.ISCARD(saletype))
			return;

		super.calcGoodsVIPRebate(index);
	}

	protected void sellSingleCard()
	{
		StringBuffer cardno = new StringBuffer();
		TextBox cardTracker = new TextBox();

		try
		{
			do
			{
				if (!cardTracker.open("请刷卡", "售卡交易", "按【退出键】取消/结束刷卡", cardno, 0, 0, false, TextBox.AllInput))
					break;

				if (saleGoods.size() >= 20)
				{
					new MessageBox("一笔只能售20张,请重新操作!");
					return;
				}

				String track = cardno.toString();
				if (track == null || track.equals(""))
					continue;

				cardno.delete(0, cardno.toString().length());
				Vector retVec = new Vector();
				if (((Bcsf_NetService) NetService.getDefault()).saleMzk("01", track, retVec))
				{
					String[] info = (String[]) retVec.get(0);
					if (info == null || info.length < 1 || info[0] == null)
						continue;

					StringBuffer passwd = new StringBuffer();
					TextBox txt = new TextBox();

					if (!txt.open("请输入验证码", "PASSWORD", "请输入验证码", passwd, 0, 0, false, TextBox.AllInput))
						continue;

					if (!passwd.toString().equals(info[4]))
					{
						new MessageBox("验证码输入错误!");
						continue;
					}

					GoodsDef goodsDef = new GoodsDef();

					if (info.length < 1 || info[0] == null)
						continue;

					boolean issamegoods = false;
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
						if (sgd.name.startsWith(info[0]))
						{
							new MessageBox("卡/券号:" + info[0] + "已在列表中存在\n相同卡号不允许重复售卖");
							issamegoods = true;
							break;
						}
					}

					if (issamegoods)
						continue;

					goodsDef.barcode = goodsDef.code = "0000";

					goodsDef.gz = "00";
					goodsDef.uid = "00";
					goodsDef.name = info[0] + "|工本费" + info[3] + "元+面值" + ManipulatePrecision.doubleConvert(Convert.toDouble(info[2])) + "元";
					goodsDef.lsj = ManipulatePrecision.doubleConvert(Convert.toDouble(info[2]) + Convert.toDouble(info[3]));
					goodsDef.unit = "张";
					goodsDef.issqkzk = 'N';
					goodsDef.isvipzk = 'N';

					SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, saleEvent.yyyh.getText(), 1, goodsDef.lsj, 0, false);
					saleGoodsDef.num3 = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * Convert.toDouble(info[3])); // 记录工本费
					addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));
					getZZK(saleGoodsDef);

					calcHeadYsje();

					refreshSaleForm();
					saleEvent.updateSaleGUI();
				}

			} while (true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
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

			if (("," + GlobalInfo.sysPara.visiblepaycode + ",").indexOf("," + mode.code + ",") > -1)
				continue;

			if (SellType.ISCARD(saletype))
				if (("," + GlobalInfo.sysPara.limitpaytype + ",").indexOf("," + mode.type + ",") == -1)
					continue;

			// 印花换购下只允许使用现金类型方式
			if (SellType.ISSTAMP(saletype))
			{
				if (!mode.code.equals("0108"))
					continue;
			}
			else
			{
				if (mode.code.equals("0108"))
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
					if (mode.hl != 1)
						temp[1] = temp[1] + "<" + ManipulatePrecision.doubleToString(mode.hl, 4, 1, false) + ">";
				}
				child.add(temp);
			}

		}

		return child;

	}

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

		if (!((Bstd_DataService) DataService.getDefault()).getJfExchangeGoods(jfrd, saleGoodsDef.code, curCustomer.code, curCustomer.type))
			return;
		// 加入日志

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

		double maxsl = -1;
		// 判断换购数量
		if (String.valueOf(jfrd.char1).length() > 0 && jfrd.char1 == 'Y')
		{
			double sum = 0;
			// 按商品行号查找对应的积分换购付款
			for (int i = 0; i < saleGoods.size(); i++)
			{
				SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
				SaleGoodsDef salegoods = (SaleGoodsDef) saleGoods.elementAt(i);
				if (i == index)
					continue;

				if (info.char2 == 'Y' && salegoods.code.equals(saleGoodsDef.code))
				{
					sum += salegoods.sl;
				}
			}

			maxsl = ManipulatePrecision.doubleConvert(jfrd.num1 - sum);

			if (ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl) > jfrd.num1)
			{
				new MessageBox("包含此商品后，" + jfrd.str1 + " 此规则已换购数量" + ManipulatePrecision.doubleConvert(sum + saleGoodsDef.sl) + "\n" + saleGoodsDef.code + " 超出最大可换购数量【" + jfrd.num1 + "】");

				return;
			}
		}

		// 提示是否进行换购
		String message = null;
		if (jfrd.char2 == 'Y')
		{
			message = "积分兑换";

			MessageBox me = new MessageBox("您目前可用" + jfrd.jf + message + ManipulatePrecision.doubleToString(jfrd.money) + "元\n换购该商品\n是否要进行换购?", null, true);

			if (me.verify() != GlobalVar.Key1) { return; }

		}
		else
		{
			// 弹出提示框
			message = "积分加上";

			MessageBox me = new MessageBox("您目前可用" + jfrd.jf + message + ManipulatePrecision.doubleToString(jfrd.money) + "元\n换购该商品\n是否要进行换购?", null, true);

			if (me.verify() != GlobalVar.Key1) { return; }

			StringBuffer buffer = new StringBuffer();
			double max = ManipulatePrecision.doubleConvert((int) (curCustomer.valuememo / jfrd.jf));

			// 如果存在限量
			if (maxsl > 0)
				max = Math.min(max, maxsl);

			buffer.append(max);

			do
			{
				if (new TextBox().open("请输入要兑换的数量", "数量", "目前最大可兑换的数量为" + ManipulatePrecision.doubleToString(max), buffer, 1, max, true, TextBox.IntegerInput, -1))
				{
					double inputsl = Convert.toDouble(buffer.toString());
					if (!inputQuantity(index, inputsl))
					{
						continue;
					}

				}
				else
				{
					return;
				}
				break;
			} while (true);

		}

		// 先删除换购付款
		delJfExchangeByGoods(index);

		double jf = 0;
		int salejf = 0;
		double sjje = 0;

		SaleGoodsDef sgd = (SaleGoodsDef) saleGoodsDef.clone();
		salejf = (int) ManipulatePrecision.mul(jfrd.jf, sgd.sl);

		if (jfrd.char2 == 'Y')
		{
			// 输入积分
			StringBuffer buffer = new StringBuffer();
			buffer.append(curCustomer.valuememo);

			do
			{
				if (new TextBox().open("请输入要使用的积分", "积分", "目前可用" + jfrd.jf + message + ManipulatePrecision.doubleToString(jfrd.money) + "元", buffer, 1, curCustomer.valuememo, true, TextBox.IntegerInput, -1))
				{
					if (Double.parseDouble(buffer.toString()) < salejf)
					{
						new MessageBox("输入积分" + Integer.parseInt(buffer.toString()) + "小于换购积分" + salejf + "\n不能进行换购");
						continue;
					}

					salejf = (int) Double.parseDouble(buffer.toString());

					double tempsajf = ManipulatePrecision.div((sgd.hjje - sgd.hjzk), jfrd.money) * jfrd.jf;

					tempsajf = tempsajf - (int) tempsajf % jfrd.jf;

					if (salejf > tempsajf)
						salejf = (int) tempsajf;

					jf = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(salejf, jfrd.jf) * jfrd.money);

					curCustomer.valuememo = curCustomer.valuememo - salejf;

					break;
				}
				else
				{
					return;
				}

			} while (true);
		}
		else
		{
			if (curCustomer.valuememo < salejf)
			{
				salejf = (int) (curCustomer.valuememo - (salejf - curCustomer.valuememo));
				sjje = ManipulatePrecision.mul(ManipulatePrecision.integerDiv(salejf, jfrd.jf), jfrd.money);
				double tempsl = sgd.sl - ManipulatePrecision.integerDiv(salejf, jfrd.jf);
				sjje = (sgd.hjje - sgd.hjzk) - ManipulatePrecision.mul(ManipulatePrecision.div((sgd.hjje - sgd.hjzk), sgd.sl), tempsl) + sjje;
			}
			else
			{
				sjje = ManipulatePrecision.mul(jfrd.money, sgd.sl);
			}

			jf = getDetailOverFlow(ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) - sjje));
			curCustomer.valuememo = curCustomer.valuememo - salejf;
		}

		// 生成积分换购付款方式
		PaymentCustJfSale pay = new PaymentCustJfSale(paymode, this);

		// 创建积分换购对象
		if (pay != null && pay.createJfExchangeSalePay(jf, salejf, jfrd, index))
		{
			// 转换名称用于显示
			if (jfrd.char2 == 'Y')
			{
				sgd.name = "(积分" + salejf + "+" + ManipulatePrecision.doubleToString(getDetailOverFlow(ManipulatePrecision.doubleConvert((sgd.hjje - sgd.hjzk) - ManipulatePrecision.doubleConvert(ManipulatePrecision.div(salejf, jfrd.jf) * jfrd.money)))) + "元换购);" + sgd.name;
			}
			else
			{
				sgd.name = "(积分" + salejf + "+" + ManipulatePrecision.doubleToString(sjje) + "元换购);" + sgd.name;
			}

			// 在付款对象记录商品信息(要扣的积分,XX积分,兑单个商品XX金额，换购规则单号,商品编码，商品数量,商品行号)
			pay.salepay.idno += "," + jfrd.str1 + "," + sgd.code + "," + sgd.sl;

			// 增加已付款
			addSalePayObject(pay.salepay, pay);

			SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

			// 积分换购商品标志
			info.char2 = 'Y';
			info.str3 = String.valueOf(pay.salepay.num5) + "," + jfrd.str1;
			// 记录积分扣回的分摊
			if (info.payft == null)
				info.payft = new Vector();
			String[] ft = new String[] { String.valueOf(pay.salepay.num5), pay.salepay.paycode, pay.salepay.payname, String.valueOf(jf) };
			info.payft.add(ft);

			// 计算剩余付款
			calcPayBalance();

			saleEvent.table.modifyRow(rowInfo(sgd), index);

			saleEvent.setTotalInfo();
			saleEvent.setCurGoodsBigInfo();

		}
		else
		{
			new MessageBox("积分换购付款对象创建失败\n请删除商品后重新试一次!");

		}

		sgd = null;
	}

	public boolean paySellStart()
	{
		if (super.paySellStart())
		{
			if (curCustomer != null && curCustomer.valuememo >= GlobalInfo.sysPara.scoreAmountLimit)
				new MessageBox("当前会员积分:" + this.curCustomer.valuememo + "\n可使用积分消费付款");

			return true;
		}

		return false;
	}

	public String getVipInfoLabel()
	{
		if (curCustomer == null)
			return "";
		else
		{
			if (curCustomer.code.length() >= 10)
				return "[" + curCustomer.code.substring(10) + "]" + curCustomer.name;

			return super.getVipInfoLabel();
		}
	}

	// 输入金额
	public boolean inputPrice(int index)
	{
		if (SellType.isJS(saletype)) { return false; }

		SaleGoodsDef oldGoodsDef = null;
		double newjg;
		String grantgh = null;

		// 检查是否允许输入价格
		if (!allowInputPrice(index)) { return false; }

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8))
			return false;

		GoodsDef goodsDef = null;
		if (goodsAssistant != null && goodsAssistant.size() > index)
			goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 定价商品授权改价
		if (SellType.ISSALE(saletype) && !SellType.ISBATCH(saletype) && (goodsDef == null || goodsDef.lsj > 0) && curGrant.privgj != 'Y')
		{
			OperUserDef staff = inputPriceGrant(index);
			if (staff == null)
				return false;

			// 记录授权号
			grantgh = staff.gh;
		}

		// 输入价格
		StringBuffer buffer = new StringBuffer();
		newjg = saleGoodsDef.jg;
		do
		{
			buffer.delete(0, buffer.length());
			buffer.append(newjg);

			if (!new TextBox().open("请输入该商品价格", "价格", "", buffer, 0.01, getMaxSaleGoodsMoney(), true)) { return false; }

			newjg = Convert.toDouble(buffer.toString());
			// 输入价格按价格金额截取
			if (goodsDef != null)
				newjg = getConvertPrice(Double.parseDouble(buffer.toString()), goodsDef);
			newjg = ManipulatePrecision.doubleConvert(newjg, 2, 1);

			// 检查价格(P:配件;Z:赠品)
			if (goodsDef != null && goodsDef.type != 'P' && goodsDef.type != 'Z' && newjg <= 0)
			{
				new MessageBox("该商品价格必须大于0");
				continue;
			}

			// 最低限价
			if (goodsDef != null && newjg < goodsDef.maxzke)
			{
				new MessageBox("该项商品价格不能小于最低限价" + ManipulatePrecision.doubleToString(goodsDef.maxzke));
				continue;
			}

			// 最低限价 num4
			if (goodsDef != null && goodsDef.num4 > 0 && newjg < goodsDef.num4)
			{
				new MessageBox("该项商品价格不能小于最低限价" + ManipulatePrecision.doubleToString(goodsDef.num4));
				continue;
			}

			// 最高限价 num5
			if (goodsDef != null && goodsDef.num5 > 0 && newjg > goodsDef.num5)
			{
				new MessageBox("该项商品价格不能高于最高限价" + ManipulatePrecision.doubleToString(goodsDef.num5));
				continue;
			}

			// 是否允许在商品退货时,商品是否在下限和上限的价格之内
			if (!isAllowedBackPriceLimit(goodsDef, newjg))
				continue;

			// 跳出循环
			break;
		} while (true);

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 重算商品应收
		if (goodsDef != null && goodsDef.lsj > 0)
			saleGoodsDef.flag = '6'; // 标记该商品被议价
		saleGoodsDef.jg = newjg;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		clearGoodsGrantRebate(index);

		if (goodsDef != null)
			calcGoodsYsje(index);

		// 重算小票应收
		calcHeadYsje();

		// 价格过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox("商品价格过大,导致销售金额达到上限\n\n商品价格修改无效");

			// 恢复价格
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 价格过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox("商品价格过大,导致退货金额超过限额\n\n商品价格修改无效");

			// 恢复数量
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.str8 = "A";
				saleGoodsDef.name += "[修改]";
			}

			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}

		// 记录授权日志
		if (grantgh != null)
		{
			String log = "授权修改价格,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",原价:" + oldGoodsDef.jg + ",新价格:" + saleGoodsDef.jg + ",授权:" + grantgh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		return true;
	}

	public boolean TgGrant()
	{
		// Y - 挂单解挂 B - 解挂
		if (curGrant.priv.length() > 7 && curGrant.priv.charAt(7) != 'Y')
		{
			OperUserDef staff = DataService.getDefault().personGrant("收银团购授权");

			if (staff != null)
			{
				if (staff.priv.length() > 7 && staff.priv.charAt(7) != 'Y')
				{
					new MessageBox("当前工号没有团购权限!");
					return false;
				}

				String log = "授权团购,授权工号:" + staff.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	public boolean doLcZc(Label txt_zl, Group grp_zl_sy)
	{
		// 1.强制转存情况下如果会员不具有转存的功能，则直接返回
		if (GlobalInfo.sysPara.isAutoLczc == 'Y')
		{
			// 未刷会员
			if ((curCustomer == null))
				return false;

			// 或者会员不具备零钞转存的功能均直接返回
			if (curCustomer.func == null || curCustomer.func.length() == 0 || curCustomer.func.charAt(0) != 'Y')
				return false;
		}

		double zlmoney = 0;
		boolean showtips = !(GlobalInfo.sysPara.isAutoLczc == 'Y'); // 是否强制存入零钞

		// 销售交易才能转存
		if (!SellType.ISSALE(saletype))
		{
			if (showtips)
				new MessageBox("必须是销售模式才能进行零钞转存的功能!");
			return false;
		}

		if (GlobalInfo.sysPara.lczcmaxmoney <= 0)
		{
			if (showtips)
				new MessageBox("系统参数定义最大零钞转存金额小于等于0\n\n无法进行零钞转存的功能!");
			return false;
		}

		// 计算实际可找零金额
		double zl = 0;
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);

			// 计算找零合计
			if (sp.flag == '2')
			{
				zl = ManipulatePrecision.add(zl, sp.je);
			}

			// 计算已有的转存金额，将转存金额补回到找零合计,得到未进行转存前真实的找零
			if (CreatePayment.getDefault().isPaymentLczc(sp) || CreatePayment.getDefault().isPaymentMobileCharge(sp))
			{
				zlmoney = ManipulatePrecision.add(zlmoney, sp.je * -1);
			}
		}
		zl = ManipulatePrecision.doubleConvert(ManipulatePrecision.add(zl, zlmoney));

		if (zl <= 0)
		{
			if (showtips)
				new MessageBox("当前无找零金额\n\n无法进行零钞转存的功能!");
			return false;
		}

		// 找零金额
		if (GlobalInfo.sysPara.isAutoLczc == 'Y' && zl > 0)
		{
			if (new MessageBox("是否进行零钞转存?", null, true).verify() == GlobalVar.Key2)
				return false;
		}

		// 找零充值方式
		Vector vec = new Vector();

		// 可以使用移动找零充值
		if (GlobalInfo.useMobileCharge)
		{
			if (GlobalInfo.sysPara.isAutoLczc == 'N' || GlobalInfo.sysPara.isAutoLczc == 'M')
			{
				vec.add(new String[] { "移动找零充值", "将找零充值到移动手机卡上", "MOB" });
			}
		}

		// 可以使用会员零钞转存
		if ((curCustomer != null && curCustomer.func != null && curCustomer.func.length() > 0 && curCustomer.func.charAt(0) == 'Y') && (GlobalInfo.sysPara.isAutoLczc == 'N' || GlobalInfo.sysPara.isAutoLczc == 'Y' || GlobalInfo.sysPara.isAutoLczc == 'H'))
		{
			vec.add(new String[] { "会员零钞转存", "将找零存入会员卡的零钞账户", "HY" });
		}

		// 选择零钞转存方式
		String lczcmode = null, lczcdesc = null;
		if (vec.size() <= 0)
		{
			if (showtips)
			{
				if (GlobalInfo.useMobileCharge)
					new MessageBox("没有定义移动付款方式\n\n无法使用移动找零充值功能");
				else
					new MessageBox("没有刷会员卡 或 会员没有定义零钞转存功能\n\n无法使用会员零钞转存功能!");
			}
			return false;
		}
		else if (vec.size() == 1)
		{
			lczcdesc = ((String[]) vec.elementAt(0))[0];
			lczcmode = ((String[]) vec.elementAt(0))[2];
		}
		else
		{
			String[] title = { "找零转存方式", "描述" };
			int[] width = { 200, 350 };
			int choice = new MutiSelectForm().open("请选择找零转存方式", title, width, vec);
			if (choice < 0)
				return false;
			lczcdesc = ((String[]) vec.elementAt(choice))[0];
			lczcmode = ((String[]) vec.elementAt(choice))[2];
		}

		// 强制零钞转存则自动存入参数定义的最大金额,否则提示输入找零金额
		if (GlobalInfo.sysPara.isAutoLczc == 'Y')
		{
			// 存入低于参数的找零金额的零头部分,参数表示的意义是最小的可找零面额
			double maxlczc = ManipulatePrecision.doubleConvert(zl % GlobalInfo.sysPara.lczcmaxmoney);

			// 不能超过会员卡允许的每次存入金额
			if ("HY".equals(lczcmode) && curCustomer.value4 > 0 && maxlczc > curCustomer.value4) { return false; }

			zlmoney = maxlczc;
		}
		else
		{
			double maxlczc = zl;

			if ("HY".equals(lczcmode))
			{
				// value2表示会员卡零钞账户的余额上限,value1表示会员卡零钞账户的当前余额,value4表示会员卡零钞账户每次存入上限
				if (curCustomer.value2 != 0)
					maxlczc = Math.min(maxlczc, ManipulatePrecision.doubleConvert(curCustomer.value2 - curCustomer.value1));
				if (curCustomer.value4 > 0)
					maxlczc = Math.min(maxlczc, curCustomer.value4);
			}

			// 输入转存金额
			StringBuffer buffer = new StringBuffer();
			buffer.append(ManipulatePrecision.doubleToString(zl));
			String line = "本笔应找零金额为 " + ManipulatePrecision.doubleToString(zl, 2, 1) + " 元\n" + "本次最多允许进行 " + ManipulatePrecision.doubleToString(maxlczc, 2, 1) + " 元的" + lczcdesc;
			if (!new TextBox().open("请输入您要进行" + lczcdesc + "的金额", "金额", line, buffer, 0.01, maxlczc, true)) { return false; }
			zlmoney = Double.parseDouble(buffer.toString());
			if (zlmoney > GlobalInfo.sysPara.lczcmaxmoney)
			{
				new MessageBox("输入的转存充值金额大于系统定义的 " + ManipulatePrecision.doubleToString(GlobalInfo.sysPara.lczcmaxmoney) + " 元\n无法进行零钞转存的功能!");
				return false;
			}
			if ("HY".equals(lczcmode) && (curCustomer.value2 != 0 && (zlmoney + curCustomer.value1) > curCustomer.value2))
			{
				new MessageBox("该会员账户的零钞余额已经到达最大的上限金额\n无法进行零钞转存的功能!");
				return false;
			}
		}

		// 先删除已存在的零钞转存
		deleteLcZc();

		// 再增加新的转存金额付款
		if ("HY".equals(lczcmode))
		{
			PaymentCustLczc pay = CreatePayment.getDefault().getPaymentLczc(saleEvent.saleBS);
			if (pay == null || !pay.createLczcSalePay(zlmoney))
			{
				new MessageBox("没有零钞转存付款方式 或 零钞转存对象创建失败\n\n无法进行零钞转存的功能!");
				return false;
			}
			addSalePayObject(pay.salepay, pay);
		}
		if ("MOB".equals(lczcmode))
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(saleEvent.saleBS);
			if (pay == null || !pay.createChgChargeSalePay(zlmoney))
			{
				new MessageBox("没有找零充值付款方式 或 找零充值对象创建失败\n\n无法进行找零充值的功能!");
				return false;
			}
			addSalePayObject(pay.salepay, pay);
		}

		// 重新计算应收应付
		calcPayBalance();

		// 重新计算找零
		calcSaleChange();

		// 刷新找零窗口显示
		grp_zl_sy.setText(getChangeTitleLabel());
		grp_zl_sy.setText(grp_zl_sy.getText() + "/零钞转存(" + ManipulatePrecision.doubleToString(zlmoney) + ")");
		txt_zl.setText(ManipulatePrecision.doubleToString(saleHead.zl));

		return true;
	}

	public boolean saleCollectAccountPay()
	{
		if (!SellType.ISCARD(saletype))
			return super.saleCollectAccountPay();
		return true;
	}

	public boolean checkFinalStatus()
	{
		if (!SellType.ISCARD(saletype))
			return super.checkFinalStatus();

		String tracks = "";
		double cardfee = 0.0;
		ProgressBox box = null;

		try
		{
			box = new ProgressBox();
			box.setText("系统正在执行卡激活,请稍等...");

			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				if (sgd.name.indexOf("|") > -1)
				{
					tracks = tracks + sgd.name.substring(0, sgd.name.indexOf("|")) + ",";
					cardfee = cardfee + sgd.num3; // 记录工本费
				}
			}

			tracks = tracks.substring(0, tracks.length() - 1);
			cardfee = ManipulatePrecision.doubleConvert(cardfee);

			if (tracks.equals(""))
				return super.checkFinalStatus();

			double cash = 0, bank = 0, change = 0;
			String cashpaycode="",payinfo = "";
			
			for (int j = 0; j < salePayment.size(); j++)
			{
				SalePayDef spd = (SalePayDef) salePayment.get(j);
				if (spd.flag == '2')
				{
					change = spd.je;
					continue;
				}

				PayModeDef pmd = DataService.getDefault().searchPayMode(spd.paycode);
				if (pmd.type == '1')
				{
					cash += spd.je;
					cashpaycode = spd.paycode;
					continue;
				}
				
				if (pmd.type == '3')
					bank += spd.je;
				
				payinfo += spd.paycode+"|"+spd.je +",";
			}

			cash = cash - change;
			
			if (cash <= 0)
				cash = 0;
			
			payinfo = cashpaycode+"|"+cash+","+payinfo;
			
			if (!((Bcsf_NetService) NetService.getDefault()).saleMzk("02", tracks, ManipulatePrecision.doubleConvert(cash, 2, 1), 
					ManipulatePrecision.doubleConvert(bank, 2, 1), cardfee, null,payinfo))
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (box != null)
				box.close();
			box = null;
		}
	}

	public void execCustomKey0(boolean keydownonsale)
	{
		if (keydownonsale)
		{
			if (SellType.ISSALE(saletype) && TgGrant())
			{
				String msg = saleHead.str2.trim().equals("Y") ? "1-团购" : "2-非团购";
				int ret = new MessageBox("当前选择\n" + msg + "\n\n请选择销售类型\n 1-团购  2-非团购  任意键-取消", null, false).verify();
				if (ret == GlobalVar.Key1)
				{
					saleHead.str2 = "Y";
				}
				else if (ret == GlobalVar.Key2)
				{
					saleHead.str2 = "N";
				}
				else
				{
					return;
				}
			}
		}
	}

	// 输入折扣
	public boolean inputRebate(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		if (goodsDef.ischgjg == 'N')
		{
			new MessageBox("该商品不允许议价!");

			return false;
		}

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null)
				return false;

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
			// new MessageBox("允许突破最低折扣");
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
		if (saleGoodsDef.lszke < 0)
			saleGoodsDef.lszke = 0;

		// 根据模拟计算得到当前最大打折比例
		double lszkl = saleGoodsDef.lszke / (saleGoodsDef.hjje - getZZK(saleGoodsDef) + saleGoodsDef.lszke);
		lszkl = ManipulatePrecision.doubleConvert((1 - lszkl) * 100, 2, 1);

		// 输入折扣
		String maxzklmsg = "收银员正在对该商品进行打折";

		if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
		{
			maxzklmsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前最多在成交价基础上再打折 " + ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
		}

		StringBuffer buffer = new StringBuffer();
		if (!new TextBox().open("请输入单品折扣百分比(%)" + (grantflag == true ? "(允许突破商品最低折扣限制)" : ""), "单品折扣", maxzklmsg, buffer, lszkl, 100, true))
		{
			// 恢复数据
			saleGoods.setElementAt(oldGoodsDef, index);

			return false;
		}

		// 得到折扣率
		grantzkl = Double.parseDouble(buffer.toString());

		// 计算最终折扣
		saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = ManipulatePrecision.doubleConvert((100 - grantzkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
		if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1))
		{
			saleGoodsDef.lszke -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzkl)), 2, 1);
			saleGoodsDef.lszke = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke, 2, 1);
		}
		if (saleGoodsDef.lszke < 0)
			saleGoodsDef.lszke = 0;
		saleGoodsDef.lszke = getConvertRebate(index, saleGoodsDef.lszke);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		// 最低限价 num4
		double newjg = ManipulatePrecision.div(ManipulatePrecision.sub(saleGoodsDef.hjje, saleGoodsDef.hjzk), saleGoodsDef.sl);
		if (goodsDef != null && goodsDef.num4 > 0 && newjg < goodsDef.num4)
		{
			new MessageBox("该项商品价格不能小于最低限价" + ManipulatePrecision.doubleToString(goodsDef.num4));

			saleGoods.setElementAt(oldGoodsDef, index);

			// 重算商品折扣合计
			getZZK(saleGoodsDef);

			// 重算小票应收
			calcHeadYsje();

			return false;
		}

		// 最高限价 num5
		if (goodsDef != null && goodsDef.num5 > 0 && newjg > goodsDef.num5)
		{
			new MessageBox("该项商品价格不能高于最高限价" + ManipulatePrecision.doubleToString(goodsDef.num5));

			saleGoods.setElementAt(oldGoodsDef, index);

			// 重算商品折扣合计
			getZZK(saleGoodsDef);

			// 重算小票应收
			calcHeadYsje();

			return false;
		}

		return true;
	}

	// 输入折让金额
	public boolean inputRebatePrice(int index)
	{
		double grantzkl = 0;
		boolean grantflag = false;

		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
			return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 小计、削价不处理
		if ((saleGoodsDef.flag == '0') || (saleGoodsDef.flag == '3')) { return false; }

		// 服务费、以旧换新不处理
		if ((saleGoodsDef.type == '7') || (saleGoodsDef.type == '8')) { return false; }

		if (goodsDef.ischgjg == 'N')
		{
			new MessageBox("该商品不允许议价!");

			return false;
		}

		// 不能打折
		if (!checkGoodsRebate(goodsDef, info))
		{
			new MessageBox("该商品不允许打折!");

			return false;
		}

		// 备份数据
		SaleGoodsDef oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 授权
		if ((curGrant.dpzkl * 100) >= 100 || !checkGoodsGrantRange(goodsDef, curGrant.grantgz))
		{
			OperUserDef staff = inputRebateGrant(index);
			if (staff == null)
				return false;

			// 本次授权折扣
			grantzkl = staff.dpzkl;
			grantflag = breachRebateGrant(staff);

			// 记录授权工号
			saleGoodsDef.sqkh = staff.gh;
			saleGoodsDef.sqktype = '1';
			saleGoodsDef.sqkzkfd = staff.privje1;

			// 记录日志
			String log = "授权单品折让,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",折扣权限:" + grantzkl * 100 + "%,授权:" + staff.gh;
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

		// 计算权限允许的最大折扣额
		double maxzkl = 0;
		if (grantflag)
		{
			// new MessageBox("允许突破最低折扣");
			// 允许突破最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, 0);
		}
		else
		{
			// 不允许最低折扣
			maxzkl = getMaxRebateGrant(grantzkl, goodsDef);
		}
		double maxzre = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
		// goodsDef.maxzke为最低限价
		if ((goodsDef.maxzke * saleGoodsDef.sl) <= saleGoodsDef.hjje && saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl) < maxzre)
		{
			maxzre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - (goodsDef.maxzke * saleGoodsDef.sl), 2, 1);
		}

		// 输入折让
		String maxzremsg = "收银员对该商品进行折让";

		StringBuffer buffer = new StringBuffer();
		if (GlobalInfo.sysPara.rebatepriacemode == 'Y')
		{
			// 计算最大折让到金额
			double lszre = saleGoodsDef.hjje - maxzre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多只能够折让到 " + ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open("请输入单品折让后的成交价" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, lszre, saleGoodsDef.hjje, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			// 得到折让额
			lszre = Double.parseDouble(buffer.toString());

			// 清除所有手工折扣,按输入的成交价计算最终折让
			saleGoodsDef.lszke = 0;
			saleGoodsDef.lszre = 0;
			saleGoodsDef.lszzk = 0;
			saleGoodsDef.lszzr = 0;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - lszre, 2, 1);
		}
		else
		{
			// 计算最大可折让金额
			double lszre = maxzre - getZZK(saleGoodsDef) + saleGoodsDef.lszre;
			lszre = ManipulatePrecision.doubleConvert(lszre, 2, 1);
			if (lszre < 0)
				lszre = 0;

			if (GlobalInfo.sysPara.CloseShowZkCompetence == 'N')
			{
				maxzremsg = "收银员对该商品的单品折扣权限为 " + ManipulatePrecision.doubleToString(maxzkl * 100, 2, 1, true) + "%\n你目前对该商品最多还可以再折让 " + ManipulatePrecision.doubleToString(lszre, 2, 1, true) + " 元";
			}

			if (!new TextBox().open("请输入单品要折让的金额" + (grantflag == true ? "(允许突破最低折扣)" : ""), "单品折让", maxzremsg, buffer, 0, lszre, true))
			{
				// 恢复数据
				saleGoods.setElementAt(oldGoodsDef, index);

				return false;
			}

			// 得到折让额
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(Double.parseDouble(buffer.toString()), 2, 1);
		}

		if (getZZK(saleGoodsDef) > maxzre)
		{
			saleGoodsDef.lszre -= getZZK(saleGoodsDef) - maxzre;
			saleGoodsDef.lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre, 2, 1);
		}

		if (saleGoodsDef.lszre < 0)
			saleGoodsDef.lszre = 0;
		saleGoodsDef.lszre = getConvertRebate(index, saleGoodsDef.lszre);

		// 重算商品折扣合计
		getZZK(saleGoodsDef);

		// 重算小票应收
		calcHeadYsje();

		// 最低限价 num4
		double newjg = ManipulatePrecision.div(ManipulatePrecision.sub(saleGoodsDef.hjje, saleGoodsDef.hjzk), saleGoodsDef.sl);
		if (goodsDef != null && goodsDef.num4 > 0 && newjg < goodsDef.num4)
		{
			new MessageBox("该项商品价格不能小于最低限价" + ManipulatePrecision.doubleToString(goodsDef.num4));

			saleGoods.setElementAt(oldGoodsDef, index);

			// 重算商品折扣合计
			getZZK(saleGoodsDef);

			// 重算小票应收
			calcHeadYsje();

			return false;
		}

		// 最高限价 num5
		if (goodsDef != null && goodsDef.num5 > 0 && newjg > goodsDef.num5)
		{
			new MessageBox("该项商品价格不能高于最高限价" + ManipulatePrecision.doubleToString(goodsDef.num5));

			saleGoods.setElementAt(oldGoodsDef, index);

			// 重算商品折扣合计
			getZZK(saleGoodsDef);

			// 重算小票应收
			calcHeadYsje();

			return false;
		}

		return true;
	}

	// 输入总折扣
	public boolean inputAllRebate()
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
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%";
			}

			// 输入折扣
			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)", "整单折扣", maxzzklmsg, buffer, grantzkl * 100, 100, true)) { return false; }

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
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
				if (getZZK(saleGoodsDef) > ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1))
				{
					// 提示
					new MessageBox("[" + saleGoodsDef.code + "]" + saleGoodsDef.name + "\n\n最多能打折 " + ManipulatePrecision.doubleToString(maxzzkl * 100) + "%");

					//
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - ManipulatePrecision.doubleConvert((saleGoodsDef.hjje * (1 - maxzzkl)), 2, 1);
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
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
				sumzzk += ManipulatePrecision.doubleConvert((1 - grantzkl) * saleGoodsDef.hjje, 2, 1);
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
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);
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
				maxzzklmsg = "收银员对权限范围内商品的总折扣权限为 " + ManipulatePrecision.doubleToString(grantzkl * 100, 2, 1, true) + "%\n你目前最多在权限内交易额基础上再打折 " + ManipulatePrecision.doubleToString(lszkl, 2, 1, true) + "%";
			}

			StringBuffer buffer = new StringBuffer();
			if (!new TextBox().open("请输入整单折扣百分比(%)" + (grantflag == true ? "(允许突破最低折扣)" : ""), "整单折扣", maxzzklmsg, buffer, lszkl, 100, true)) { return false; }

			// 得到折扣金额,打折后按收银机定义四舍五入
			double zkl = Double.parseDouble(buffer.toString());
			double zzkje = ManipulatePrecision.doubleConvert((100 - zkl) / 100 * (hjcjj + sumlszzk), 2, 1);
			double tempysje = (saleHead.hjzje - saleHead.hjzke + sumlszzk) - zzkje;
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
				double maxzzk = ManipulatePrecision.doubleConvert((1 - maxzkl) * saleGoodsDef.hjje, 2, 1);

				// 取消其他手工折扣,计算最终折扣
				saleGoodsDef.sqkh = "";
				saleGoodsDef.sqktype = '\0';

				// 每个商品分摊的折让按金额占比计算
				if (i != lastzzkrow)
				{
					if (GlobalInfo.sysPara.batchtotalrebate == 'N')
					{
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.hjzk + saleGoodsDef.lszzk) / (hjcjj + sumlszzk) * zzkje, 2, 1);
					}
					else
					{
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(maxzzk / sumzzk * zzkje, 2, 1);
					}
					if (getZZK(saleGoodsDef) > maxzzk)
					{
						saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - maxzzk;
						saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
					}
					if (saleGoodsDef.lszzk < 0)
						saleGoodsDef.lszzk = 0;
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk);
					saleGoodsDef.lszzk = getConvertRebate(i, saleGoodsDef.lszzk, getGoodsApportionPrecision());

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
				saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(zzkje - hjzzk, 2, 1);
				if (getZZK(saleGoodsDef) > lastzzk)
				{
					saleGoodsDef.lszzk -= getZZK(saleGoodsDef) - lastzzk;
					saleGoodsDef.lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszzk, 2, 1);
				}
				if (saleGoodsDef.lszzk < 0)
					saleGoodsDef.lszzk = 0;
				getZZK(saleGoodsDef);
			}
		}

		// 重算小票应收
		calcHeadYsje();

		return true;
	}

	public void execCustomKey1(boolean keydownonsale)
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return;

		saleHead.str3 = "A," + String.valueOf(saleHead.hjzje);
		new MessageBox("性别:男青\n年龄:30岁以下\n销售金额:" + String.valueOf(saleHead.hjzje));
	}

	public void execCustomKey2(boolean keydownonsale)
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return;

		saleHead.str3 = "B," + String.valueOf(saleHead.hjzje);
		new MessageBox("性别:男中\n年龄:30-50岁\n销售金额:" + String.valueOf(saleHead.hjzje));
	}

	public void execCustomKey3(boolean keydownonsale)
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return;

		saleHead.str3 = "C," + String.valueOf(saleHead.hjzje);
		new MessageBox("性别:男老\n年龄:50岁以上\n销售金额:" + String.valueOf(saleHead.hjzje));
	}

	public void execCustomKey4(boolean keydownonsale)
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return;

		saleHead.str3 = "D," + String.valueOf(saleHead.hjzje);
		new MessageBox("性别:女青\n年龄:30岁以下\n销售金额:" + String.valueOf(saleHead.hjzje));
	}

	public void execCustomKey5(boolean keydownonsale)
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return;

		saleHead.str3 = "E," + String.valueOf(saleHead.hjzje);
		new MessageBox("性别:女中\n年龄:30-50岁\n销售金额:" + String.valueOf(saleHead.hjzje));
	}

	public void execCustomKey6(boolean keydownonsale)
	{
		if (saleGoods == null || saleGoods.size() < 1)
			return;

		saleHead.str3 = "F," + String.valueOf(saleHead.hjzje);
		new MessageBox("性别:女老\n年龄:50岁以上\n销售金额:" + String.valueOf(saleHead.hjzje));
	}

	public void payInput()
	{
		if (GlobalInfo.sysPara.isxxcj == 'Y' && (saleHead.str3.trim().equals("")))
		{
			new MessageBox("请先采集顾客信息");
		}
		else
		{
			super.payInput();
		}
	}
}
