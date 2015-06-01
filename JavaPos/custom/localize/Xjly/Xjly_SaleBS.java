package custom.localize.Xjly;

import java.util.Vector;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.DeviceName;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_DataService;

public class Xjly_SaleBS extends Xjly_SaleBS0CRMPop
{
	public char checkType = 'X'; // 盘点单类型
	boolean amountPromotion = false;

	public String getSyyInfoLabel()
	{
		return GlobalInfo.posLogin.name;
	}

	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		String comcode = "";
		String barcode = "";
		boolean isdzcm;
		double dzcmjg = 0;
		double dzcmsl = 0;
		String dzcmscsj = "";
		double quantity = 1;
		double price = 0;

		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);
		barcode = s[1];

		// 解析电子秤码
		String[] codeInfo = new String[4];
		isdzcm = analyzeBarcode(barcode, codeInfo);

		if (isdzcm)
		{
			comcode = codeInfo[0];
			dzcmjg = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[1]), 2, 1);
			dzcmsl = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[2]), 4, 1);
			dzcmscsj = codeInfo[3];

			// 验证电子秤校验位
			if (!verifyDzcmCheckbit(barcode))
			{
				new MessageBox(Language.apply("电子秤码校验位错误"), null, false);

				return false;
			}

			if (dzcmjg <= 0 && dzcmsl <= 0)
			{
				new MessageBox(Language.apply("该电子秤格式条码无效"), null, false);

				return false;
			}
		}
		else
		{
			comcode = barcode;
		}

		// 查找详细商品资料,可支持数量转换
		StringBuffer slbuf = new StringBuffer(ManipulatePrecision.doubleToString(quantity));

		GoodsDef goodsDef = findGoodsInfo(comcode, yyyh, gz, dzcmscsj, isdzcm, slbuf);
		if (!checkServiceCode(goodsDef))
			return false;

		quantity = Convert.toDouble(slbuf.toString());

		// 获得最小批量数量
		quantity = getMinPlsl(quantity, goodsDef);

		// 电子秤商品记录原始电子秤码
		goodsDef.inputbarcode = barcode;
		if (isdzcm)
			goodsDef.barcode = convertDzcmBarcode(goodsDef, barcode, isdzcm);

		// 设置商品缺省售价
		price = setGoodsDefaultPrice(goodsDef);

		// 电子秤码没有通过条码解析销售，补入商品价格或数量
		if (goodsDef.isdzc == 'Y' && !isdzcm)
		{
			// 输入价格模式
			if (GlobalInfo.sysPara.dzccodesale == 'Y')
			{
				isdzcm = true;

				StringBuffer pricestr = new StringBuffer();
				do
				{
					pricestr.delete(0, pricestr.length());
					pricestr.append(price);

					boolean done = new TextBox().open(Language.apply("请输入商品[") + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + Language.apply("价格"), Language.apply("价格"), "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmjg = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

						if (dzcmjg <= 0)
						{
							new MessageBox(Language.apply("该商品价格必须大于0"));
						}
						else
						{
							break;
						}
					}
				}
				while (true);
			}

			// 输入数量模式
			if (GlobalInfo.sysPara.dzccodesale == 'A')
			{
				isdzcm = true;

				StringBuffer slstr = new StringBuffer();
				do
				{
					slstr.delete(0, slstr.length());
					slstr.append(0.000);

					boolean done = new TextBox().open(Language.apply("请输入商品[") + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + Language.apply("数量"), Language.apply("数量"), "", slstr, 0.01, getMaxSaleGoodsQuantity(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmsl = Double.parseDouble(slstr.toString());

						if (dzcmsl <= 0)
						{
							new MessageBox(Language.apply("该商品数量必须大于0"));
						}
						else
						{
							break;
						}
					}
				}
				while (true);
			}
		}

		// 电子秤条码的数量价格处理
		int dzcprice = 0;
		double allprice = 0;

		if (isdzcm)
		{
			dzcmjgzk = 0;

			if ((dzcmsl > 0) && (dzcmjg <= 0)) // 只有数量
			{
				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				price = ManipulatePrecision.doubleConvert(goodsDef.lsj, 2, 1);
				allprice = quantity * price;
				dzcprice = 1;

				// 电子秤打印的合计一般都是从第三位截断再四舍五入
				allprice = ManipulatePrecision.doubleConvert(allprice, 3, 0);
				allprice = ManipulatePrecision.doubleConvert(allprice, 2, 1);

				// 按价格精度进行计算,差额记折扣
				double jg = getConvertPrice(allprice, goodsDef);
				if (ManipulatePrecision.doubleCompare(allprice, jg, 2) != 0)
				{
					dzcmjgzk = ManipulatePrecision.sub(allprice, jg);
				}
			}
			else if ((dzcmsl <= 0) && (dzcmjg > 0)) // 只有金额
			{

				if (goodsDef.lsj <= 0) // 不定价商品
				{
					quantity = 1;
					price = dzcmjg;
					allprice = price;
					dzcprice = 1;
				}
				else
				// 定价商品,反算数量
				{
					// pfj存放电子秤实际秤上的价格(可能是促销价),如果和商品主档价格不一致,说明有促销,
					// 用秤的价格反算出数量然后再正常计算促销
					if (GlobalInfo.sysPara.isCalcAsPfj == 'Y' && (goodsDef.pfj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj, goodsDef.pfj, 2) != 0))
					{
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.pfj), 4, 1);
						price = goodsDef.lsj;
						allprice = ManipulatePrecision.doubleConvert(quantity * price);
						dzcprice = 2;
						/*
						 * if (SellType.ISBACK(saletype)) { dzcmjgzk = allprice
						 * - ManipulatePrecision.doubleConvert(quantity *
						 * goodsDef.pfj); }
						 */
						dzcmjgzk = allprice - ManipulatePrecision.doubleConvert(quantity * goodsDef.pfj);
					}
					else
					{
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.lsj), 4, 1);
						price = goodsDef.lsj;
						allprice = dzcmjg;
						dzcprice = 2;
					}
				}
			}
			else if ((dzcmsl > 0) && (dzcmjg > 0)) // 即有数量又有价格
			{

				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				// 如果定价商品单价*数量的成交金额已经与秤的成交价四舍五入精度后一致,则无需重算商品单价
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				allprice = dzcmjg;
				if (goodsDef.lsj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, ManipulatePrecision.getDoubleScale(allprice)) == 0)
				{
					// 电子秤的成交价可能到角,秤的成交价和数量*单价到分的成交价之间的四舍五入差额记折扣
					if (ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, 2) != 0)
					{
						allprice = ManipulatePrecision.doubleConvert(goodsDef.lsj * quantity, 2, 1);
						dzcmjgzk = ManipulatePrecision.sub(allprice, dzcmjg);
						dzcmjgzk = ManipulatePrecision.doubleConvert(dzcmjgzk, 2, 1);
					}
				}
				else
				{
					goodsDef.lsj = goodsDef.hyj = goodsDef.pfj = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
				}
				price = goodsDef.lsj;
				//
			}
		}

		// 检查找到的商品是否允许销售
		if (!checkFindGoodsAllowSale(goodsDef, quantity, isdzcm, dzcmsl, dzcmjg))
			return false;

		// 未定价商品或退货或批发要求输入售价
		if (isPriceConfirm(goodsDef))
		{
			// 指定小票退货,查询退货原始交易信息
			if (isSpecifyBack())
			{
				Vector back = new Vector();

				if (!DataService.getDefault().getBackGoodsDetail(back, thSyjh, String.valueOf(thFphm), goodsDef.code, goodsDef.gz, goodsDef.uid)) { return false; }

				int cho = 0;
				if (back.size() > 1)
				{
					Vector choice = new Vector();
					String[] title = { Language.apply("商品编码"), Language.apply("数量"), Language.apply("单价"), Language.apply("合计折扣"), Language.apply("应付金额") };
					int[] width = { 100, 100, 100, 100, 100 };
					String[] row = null;
					for (int j = 0; j < back.size(); j++)
					{
						thSaleGoods = (SaleGoodsDef) back.elementAt(j);
						row = new String[5];
						row[0] = thSaleGoods.code;
						row[1] = ManipulatePrecision.doubleToString(thSaleGoods.sl, 4, 1, true);
						row[2] = ManipulatePrecision.doubleToString(thSaleGoods.lsj, 2, 1);
						row[3] = ManipulatePrecision.doubleToString(thSaleGoods.hjzk, 2, 1);
						row[4] = ManipulatePrecision.doubleToString(thSaleGoods.hjje - thSaleGoods.hjzk, 2, 1);
						choice.add(row);
					}

					cho = new MutiSelectForm().open(Language.apply("请选择退货商品信息"), title, width, choice);
				}
				thSaleGoods = (SaleGoodsDef) back.elementAt(cho);

				if (thSaleGoods.sl < quantity)
				{
					new MessageBox(Language.apply("该商品退货数量大于原销售数量\n\n不能退货"));
					thSaleGoods = null;
					return false;
				}
			}

			// 如果是指定小票退货，不进行价格确认
			// 如果是电子秤商品且价格确定，不进行价格确认
			if (!isConfirmPrice(isdzcm, dzcprice, goodsDef))
			{
			}
			else
			{
				if (!isonlinegdjging && memo == null)
				{
					StringBuffer pricestr = new StringBuffer();
					do
					{
						pricestr.delete(0, pricestr.length());
						// pricestr.append(price);
						pricestr.append(goodsDef.lsj);

						String strprice = setGoodsLSJ(goodsDef, pricestr);
						if (strprice == null) { return false; }
						price = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(strprice), goodsDef), 2, 1);

						if (GlobalInfo.sysPara.isGoodsMoney0 != 'Y' && price <= 0)
						{
							// 检查价格
							if (price <= 0 && goodsDef.type != 'Z')
							{
								new MessageBox(Language.apply("该商品价格必须大于0"));
							}
						}
						else
						{
							// 电子秤商品重新计算
							if (isdzcm && (dzcprice > 0))
							{
								if (dzcprice == 1)
								{
									allprice = quantity * price;
								}
								else
								{
									quantity = ManipulatePrecision.doubleConvert(dzcmjg / price, 4, 1);
								}
							}

							// 是否允许在商品退货时,商品是否在下限和上限的价格之内
							if (!isAllowedBackPriceLimit(goodsDef, price))
								continue;

							break;
						}

					}
					while (true);
				}
			}
		}

		// 如果是联网挂单状态，则不输入商品附加信息
		if (!isonlinegdjging && !inputGoodsAddInfo(goodsDef))
			return false;

		// 检查找到的商品最后是否OK
		if (!allowFinishFindGoods(goodsDef, quantity, price))
			return false;

		// 增加商品到商品明细中
		if (!addSaleGoods(goodsDef, yyyh, quantity, price, allprice, isdzcm))
			return false;

		return true;

	}

	protected boolean checkServiceCode(GoodsDef goods)
	{
		if (goods == null)
			return false;

		goods.num5 = goods.num2;
		if (goodsAssistant.size() == 0)
			return true;

		GoodsDef gds = (GoodsDef) goodsAssistant.get(goodsAssistant.size() - 1);

		if (gds.num5 != goods.num5)
		{
			new MessageBox("请检查该商品是否服务码\n一单中不允许同时出现多种类型商品");
			return false;
		}
		return true;
	}

	public void backToSaleStatus()
	{
		// 家电下乡返款交易后，切换到普通销售状态
		if (SellType.ISJDXXFK(saletype) || SellType.ISJDXXFKTH(saletype))
		{
			saletype = SellType.RETAIL_SALE;
		}
		else
		{
			super.backToSaleStatus();
		}
	}

	public boolean isGoodsAllowCheck(GoodsDef goodsDef)
	{
		if (SellType.ISCHECKINPUT(saletype))
		{
			// checkType 等于 D 代表当前盘点状态是大码商品盘点
			if (checkType == 'D' && goodsDef.lsj > 0)
			{
				new MessageBox("商品[" + goodsDef.code + "]不是大码商品，不允许在当前盘点类型下盘点");
				return false;
			}
			else if (checkType == 'X' && goodsDef.lsj <= 0)
			{
				new MessageBox("商品[" + goodsDef.code + "]是大码商品，不允许在当前盘点类型下盘点");
				return false;
			}
		}
		return super.isGoodsAllowCheck(goodsDef);
	}

	public void custMethod()
	{
		if (!SellType.ISSALE(saletype))
			return;

		SaleGoodsDef saleGoodsDef = null;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			if (saleGoodsDef.jg < GlobalInfo.sysPara.isSalePrecision)
				continue;

			String hyjd = ((SpareInfoDef) goodsSpare.elementAt(i)).memo;
			if (hyjd == null || hyjd.length() < 1)
				continue;

			int dec;
			if ("0.1".equals(hyjd))
				dec = 1;
			else if ("1".equals(hyjd))
				dec = 0;
			else if ("10".equals(hyjd))
				dec = -1;
			else
				continue;

			double ysje = 0;
			double hyjdzk = 0;

			ysje = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk, dec, 0);
			hyjdzk = ManipulatePrecision.sub(saleGoodsDef.hjje - saleGoodsDef.hjzk, ysje);
			saleGoodsDef.hyzke += hyjdzk;
			getZZK(saleGoodsDef);
		}
		// 重算小票头
		calcHeadYsje();

		saleEvent.updateTable(getSaleGoodsDisplay());
		// 显示合计
		saleEvent.setTotalInfo();
		// 显示商品大字信息
		saleEvent.setCurGoodsBigInfo();
	}

	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null)
			return;

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
		if (((Bcrm_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid, saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
			goodsDef.num4 = popDef.num2;
			((SpareInfoDef) goodsSpare.elementAt(index)).memo = popDef.memo;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
			goodsDef.num4 = 1;
		}
	}

	public boolean memberGrant()
	{
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode() && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
			return false;
		}

		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许重新刷卡");
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null)
			return false;

		if (bs.selectedRule != null)
		{
			if ("手机号".equals(bs.selectedRule.desc))
				saleHead.num2 = 1;
		}

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;

			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}

	public void enterInput()
	{
		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack())
		{
			saleEvent.saleform.getFocus().selectAll();
			return;
		}

		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许修改商品状态");
			return;
		}

		// 扫瞄后回车前刷新界面显示
		Display.getCurrent().update();

		// 营业员输入
		if (saleEvent.saleform.getFocus().equals(saleEvent.yyyh))
		{
			enterInputYYY();
			String s[] = GlobalInfo.syjDef.priv.split(",");
			if (s.length > 1)
			{
				if (!(saleEvent.saleform.getFocus().equals(saleEvent.yyyh)) && s[1].substring(0, 1).equals("Y"))
				{
					if (!DeviceName.deviceEvaluation.equals(""))
					{
						device.Evaluation.PJ06.getDefault().Welcome();
					}
					else
					{
						new MessageBox("评价器设备未配置！");
					}
				}
			}

			return;
		}

		// 柜组输入
		if (saleEvent.saleform.getFocus().equals(saleEvent.gz))
		{
			enterInputGZ();

			return;
		}

		// 条码输入
		if (saleEvent.saleform.getFocus().equals(saleEvent.code))
		{
			enterInputCODE();

			return;
		}
	}

	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox("小票附加信息输入失败,不能完成交易!");
				return false;
			}

			//
			setSaleFinishHint(status, "正在汇总交易数据,请等待.....");
			if (!saleSummary())
			{
				new MessageBox("交易数据汇总失败!");

				return false;
			}

			//
			setSaleFinishHint(status, "正在校验数据平衡,请等待.....");
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox("交易数据校验错误!");

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, "正在输入客户信息,请等待......");
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, "正在打开钱箱,请等待.....");
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, "正在记账付款数据,请等待.....");
				if (!saleCollectAccountPay())
				{
					new MessageBox("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!");

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}
				String s[] = GlobalInfo.syjDef.priv.split(",");
				if (s.length > 1)
				{
					if (s[1].substring(0, 1).equals("Y"))
					{
						setSaleFinishHint(status, "正在评价中,请等待.....");
						if (!DeviceName.deviceEvaluation.equals(""))
						{
							saleHead.str2 = device.Evaluation.PJ06.getDefault().getData();
						}
					}
				}

				setSaleFinishHint(status, "正在写入交易数据,请等待......");
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox("交易数据写盘失败!");
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, "正在发送冲正数据,请等待.....");
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, "正在清除断点保护数据,请等待......");
				clearBrokenData();

				//
				setSaleFinishHint(status, "正在清除付款冲正数据,请等待......");
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!");
				}

				// 上传当前小票
				setSaleFinishHint(status, "正在上传交易小票数据,请等待......");
				boolean bsend = GlobalInfo.isOnline;
				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}

				// 发送当前收银状态
				setSaleFinishHint(status, "正在上传收银机交易汇总,请等待......");
				DataService.getDefault().sendSyjStatus();

				// 打印小票
				setSaleFinishHint(status, "正在打印交易小票,请等待......");
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, "正在打印交易小票,请等待......");
					printSaleBill();
				}

				// 标记本次交易已完成
				saleFinish = true;
			}

			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}

			// 清除本次交易数据
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, "正在等待关闭钱箱,请等待......");
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, "本笔交易结束,开始新交易");

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox("完成交易时发生异常:\n\n" + ex.getMessage());

			return saleFinish;
		}
	}

	public void initSellData()
	{
		super.initSellData();
		// 收银机组
		String s[] = GlobalInfo.syjDef.priv.split(",");
		saleHead.str1 = s[0];
	}

	public boolean calcAmountRebate()
	{
		boolean isjjcx = false;
		saleHead.num5 = 0;
		// 价随量变的促销
		Vector group = new Vector();
		// 先进行分组
		for (int i = 0; i < goodsSpare.size(); i++)
		{
			SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(i);
			if (sid.Zklist == null || sid.Zklist.length() <= 0 || sid.Zklist.equals("0"))
			{
				continue;
			}
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			GoodsPopDef goodsPop = (GoodsPopDef) crmPop.elementAt(i);
			saleGoodsDef.num8 = Convert.toDouble(sid.zkfd);
			saleGoodsDef.yhdjbh = sid.pmbillno;
			// saleGoodsDef.yhzke = 0;

			// 存在数量促销就不做组合促销
			if (sid.str1 != null)
			{
				if (sid.str1.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(sid.str1.trim());
					for (int z = 2; z < buff.length(); z++)
					{
						buff.setCharAt(z, '0');
					}
					sid.str1 = buff.toString();
				}
				else
				{
					sid.str1 = "0000";
				}
			}

			// str3重新赋值，去掉返券
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
				saleGoodsDef.str3 = sid.str1 + String.valueOf(Convert.increaseInt(goodsPop.yhspace, 5).substring(4)) + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));
			else
				saleGoodsDef.str3 = sid.str1 + saleGoodsDef.str3.substring(saleGoodsDef.str3.indexOf(";"));

			int n = 0;
			for (; n < group.size(); n++)
			{
				AmountRebateDef ar = (AmountRebateDef) group.elementAt(n);
				String condition = null;
				if (sid.Zklist.indexOf("1:") == 0)
					condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z */
				else
					condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z */
				if (ar.addrule.equals(sid.addrule) && ar.Zklist.equals(condition) && ar.bz.equals(sid.bz))
				{
					boolean cond = true;
					if (ar.addrule.length() > 0 && ar.addrule.charAt(0) == 'Y')
					{
						cond = cond && ar.gys.equals(goodsDef.str1);
					}

					if (ar.addrule.length() > 1 && ar.addrule.charAt(1) == 'Y')
					{
						cond = cond && ar.gz.equals(goodsDef.gz);
					}

					if (ar.addrule.length() > 2 && ar.addrule.charAt(2) == 'Y')
					{
						cond = cond && ar.pp.equals(goodsDef.ppcode);
					}

					if (ar.addrule.length() > 3 && ar.addrule.charAt(3) == 'Y')
					{
						cond = cond && ar.catid.equals(goodsDef.catid);
					}

					if (ar.addrule.length() > 4 && ar.addrule.charAt(4) == 'Y')
					{
						cond = cond && ar.code.equals(goodsDef.code);
					}

					if (cond)
					{
						ar.zsl = ManipulatePrecision.doubleConvert(ar.zsl + saleGoodsDef.sl);
						ar.list.add(String.valueOf(i));
						break;
					}
				}
			}

			if (n >= group.size())
			{
				AmountRebateDef ar = new AmountRebateDef();
				ar.pmbillno = sid.pmbillno;/* 促销单号 */
				ar.addrule = sid.addrule;/* 累计规则 'YYYYY' */
				if (sid.Zklist.indexOf("1:") == 0)
					ar.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z */
				else
					ar.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z */
				ar.pmrule = sid.pmrule;/* 价随量变规则 */
				ar.etzkmode2 = sid.etzkmode2;/* 1-阶梯折扣 2-统一打折 */
				ar.seq = sid.seq; /* 规则序号 */
				ar.zkfd = Convert.toDouble(sid.zkfd);
				ar.bz = sid.bz;
				ar.maxnum = sid.maxnum;
				ar.maxzke = sid.maxzke;

				ar.zsl = ManipulatePrecision.doubleConvert(saleGoodsDef.sl);
				ar.list.add(String.valueOf(i));

				ar.gys = goodsDef.str1;
				ar.gz = goodsDef.gz;
				ar.pp = goodsDef.ppcode;
				ar.catid = goodsDef.catid;
				ar.code = goodsDef.code;

				group.add(ar);
			}
		}

		if (group.size() <= 0)
			return true;
		// 检查促销生效
		for (int i = 0; i < group.size(); i++)
		{
			AmountRebateDef ar = (AmountRebateDef) group.elementAt(i);

			String zklist = ar.Zklist;
			String[] zk = zklist.split("\\|");
			int n = zk.length - 1;
			for (; n >= 0; n--)
			{
				String rule = zk[n];
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));
				if (ar.zsl < sl)
				{
					continue;
				}
				else
				{
					ar.sl_cond = sl;
					ar.zkl_result = zkl;
					break;
				}
			}

			if (n < 0)
			{
				group.removeElementAt(i);
				i--;
			}
		}

		boolean done = false;
		// 开始计算促销
		for (int i = 0; i < group.size(); i++)
		{
			AmountRebateDef ar = (AmountRebateDef) group.elementAt(i);

			if (ar.maxzke > 0 && ar.curyhzke >= ar.maxzke)
			{
				continue;
			}

			String zkcheck1 = ar.Zklist;
			String[] zkcheck2 = zkcheck1.split("\\|");
			if (zkcheck2[0] != null)
			{
				double sl1 = Convert.toDouble(zkcheck2[0].substring(0, zkcheck2[0].indexOf(":")));
				if (sl1 > 1)
				{
					ar.Zklist = "1:1|" + ar.Zklist;
				}
			}

			for (int x = 0; x < ar.list.size(); x++)
			{
				int index = Convert.toInt(ar.list.elementAt(x));
				// SpareInfoDef sid = (SpareInfoDef)
				// goodsSpare.elementAt(index);
				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				double yhzke = 0;
				// 不循环且为阶梯折扣时
				if ((Convert.toInt(ar.etzkmode2) == 1 || Convert.toInt(ar.etzkmode2) == 3) && ar.bz.trim().equals("N"))
				{
					yhzke = caculateSL(ar, saleGoodsDef, saleGoodsDef.sl, ar.Zklist.split("\\|"), 0, Convert.toInt(ar.etzkmode2));
				}
				else if ((Convert.toInt(ar.etzkmode2) == 1 || Convert.toInt(ar.etzkmode2) == 3) && ar.bz.trim().equals("Y"))
				{
					yhzke = caculateSL1(ar, saleGoodsDef, saleGoodsDef.sl, ar.Zklist.split("\\|"), 0, Convert.toInt(ar.etzkmode2));
				}
				else if (Convert.toInt(ar.etzkmode2) == 2 && ar.bz.trim().equals("Y"))
				{
					if (ar.cursl + saleGoodsDef.sl > ar.maxnum)
					{
						double num1 = ManipulatePrecision.doubleConvert(ar.maxnum - ar.cursl);
						yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getLszzk(saleGoodsDef)) / saleGoodsDef.sl) * (num1) * (1 - ar.zkl_result));
						ar.cursl = ar.maxnum;
					}
					else
					{
						yhzke = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getLszzk(saleGoodsDef)) * (1 - ar.zkl_result));
						ar.cursl = ManipulatePrecision.doubleConvert(ar.cursl + saleGoodsDef.sl);
					}
				}
				else if (Convert.toInt(ar.etzkmode2) == 4)
				{
					if (ar.cursl + saleGoodsDef.sl > ar.maxnum)
					{
						double num1 = ManipulatePrecision.doubleConvert(ar.maxnum - ar.cursl);
						yhzke = ManipulatePrecision.doubleConvert(num1 * ar.zkl_result);
						ar.cursl = ar.maxnum;
					}
					else
					{
						yhzke = ManipulatePrecision.doubleConvert(ar.zkl_result * saleGoodsDef.sl);
						ar.cursl = ManipulatePrecision.doubleConvert(ar.cursl + saleGoodsDef.sl);
					}
				}
				else
				{
					yhzke = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getLszzk(saleGoodsDef)) * (1 - ar.zkl_result));
				}

				if (yhzke >= 0)
				{
					if (ar.maxzke > 0)
					{
						if (ar.curyhzke + yhzke > ar.maxzke)
						{
							yhzke = ManipulatePrecision.doubleConvert(ar.maxzke - ar.curyhzke);
						}
						ar.curyhzke = ManipulatePrecision.doubleConvert(ar.curyhzke + yhzke);

						if (ar.maxzke > 0 && ar.maxzke < yhzke)
						{
							yhzke = ar.maxzke;
						}
					}

					if (saleGoodsDef.hjje - getLszzk(saleGoodsDef) - yhzke <= 0)
					{
						new MessageBox("数量促销活动设置错误");
						return false;
					}

					if (saleGoodsDef.yhzke > 0)
					{
						isjjcx = true;
					}
					saleGoodsDef.yhzke = 0;
					saleGoodsDef.hyzke = 0;
					saleGoodsDef.zszke = 0;
					saleGoodsDef.num7 = yhzke;
					saleGoodsDef.num7 = getConvertRebate(index, saleGoodsDef.num7);
					// saleGoodsDef.num8 = ar.zkfd;
					getZZK(saleGoodsDef);
					// saleGoodsDef.yhdjbh = ar.pmbillno;
					done = true;
				}
			}

		}

		if (done)
		{
			saleHead.num5 = 1;
			// 重算应收
			calcHeadYsje();

			// 刷新商品列表
			refreshSaleForm();
		}

		if (isjjcx)
		{
			new MessageBox("本笔交易有部分商品参与价随量变促销活动\n活动期内，此类商品将不接受定期降价促销折扣");
		}
		return true;
	}

	public double caculateSL(AmountRebateDef ar, SaleGoodsDef sgd, double use_sl, String[] zk, int index, int mode)
	{
		if (index >= zk.length)
			return 0;

		if (index == 0)
		{
			int n = 0;
			for (; n < zk.length; n++)
			{
				String rule = zk[n];
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double sl1 = -1;
				if ((n + 1) < zk.length)
				{
					sl1 = Convert.toDouble(zk[n + 1].substring(0, zk[n + 1].indexOf(":")));
				}

				if (sl1 < 0)
				{

					double use_sl1 = ManipulatePrecision.doubleConvert(ar.cursl + use_sl - sl);
					if (use_sl1 <= 0)
					{
						continue;
					}
					use_sl = use_sl1;
					index = n;
					break;
				}
				else if (ar.cursl > sl && ar.cursl >= sl1)
				{

					continue;
				}
				else if (ar.cursl >= sl && ar.cursl < sl1)
				{
					if (sl1 - ar.cursl <= use_sl)
						index = n + 1;
					else
						index = n;
					break;
				}
				else if (ManipulatePrecision.doubleConvert(ar.cursl + use_sl) >= sl1 && ManipulatePrecision.doubleConvert(ar.cursl + use_sl) > sl)
				{
					index = n;
					break;

				}
				else if (ManipulatePrecision.doubleConvert(ar.cursl + use_sl) >= sl)
				{
					index = n;
					break;
				}
			}

			if (n >= zk.length)
			{
				ar.cursl = ManipulatePrecision.doubleConvert(ar.cursl + use_sl);
				return 0;
			}
		}

		String rule = zk[index];

		double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
		double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));

		// boolean done_b = false;
		if ((index + 1) < zk.length)
		{
			// double sl1 = Convert.toDouble(zk[index + 1].substring(0, zk[index
			// + 1].indexOf(":")));
			// if (ar.cursl >= sl1) done_b = true;
		}

		// 证明有下一及
		if ((index + 1) < zk.length)
		{
			String rule1 = zk[index + 1];
			double sl1 = Convert.toDouble(rule1.substring(0, rule1.indexOf(":")));

			// 本级需要计算的数量
			double x_sl = (ar.cursl + use_sl - sl1);
			if (x_sl < 0)
			{
				ar.cursl = ManipulatePrecision.doubleConvert(ar.cursl + use_sl);
				double zkje = 0;
				if (mode == 1)
					zkje = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert((sgd.hjje - getLszzk(sgd)) / sgd.sl) * (use_sl) * (1 - zkl));
				if (mode == 3)
				{
					zkje = ManipulatePrecision.doubleConvert(use_sl * zkl);
				}
				return zkje;
			}
			else
			{
				x_sl = ManipulatePrecision.doubleConvert(sl1 - sl);
			}
			double zkje = 0;
			if (mode == 1)
				zkje = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert((sgd.hjje - getLszzk(sgd)) / sgd.sl) * x_sl * (1 - zkl));
			if (mode == 3)
			{
				zkje = ManipulatePrecision.doubleConvert(x_sl * zkl);
			}

			// 当前已经计算的数量
			ar.cursl = ManipulatePrecision.doubleConvert(ar.cursl + x_sl);
			// 当前未计算的数量
			double y_sl = ManipulatePrecision.doubleConvert(use_sl - x_sl);
			return zkje + caculateSL(ar, sgd, y_sl, zk, (index + 1), mode);
		}
		else
		// 没有下一集，用本级计算
		{
			double zkje = 0;
			if (mode == 1)
				zkje = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert((sgd.hjje - getLszzk(sgd)) / sgd.sl) * use_sl * (1 - zkl));
			if (mode == 3)
			{
				zkje = ManipulatePrecision.doubleConvert(use_sl * zkl);
			}
			return zkje;
		}
	}

	// 阶梯且循环
	public double caculateSL1(AmountRebateDef ar, SaleGoodsDef sgd, double use_sl, String[] zk, int index, int mode)
	{
		double sumzk = 0;
		for (int i = 0; i < use_sl; i++)
		{
			ar.cursl++;

			int n = zk.length - 1;
			for (; n >= 0; n--)
			{
				String rule = zk[n];
				// new MessageBox(rule);
				double sl = Convert.toDouble(rule.substring(0, rule.indexOf(":")));
				double zkl = Convert.toDouble(rule.substring(rule.indexOf(":") + 1));

				// 代表为最后一级，需要循环
				if (ar.cursl >= sl && n == (zk.length - 1))
				{
					ar.cursl = 0;
					if (mode == 1)
						sumzk = ManipulatePrecision.doubleConvert(sumzk + ManipulatePrecision.doubleConvert((sgd.hjje - getLszzk(sgd)) / sgd.sl) * (1 - zkl));
					if (mode == 3)
						sumzk = ManipulatePrecision.doubleConvert(sumzk + zkl);
					break;
				}

				if (ar.cursl >= sl)
				{
					if (mode == 1)
						sumzk = ManipulatePrecision.doubleConvert(sumzk + ManipulatePrecision.doubleConvert((sgd.hjje - getLszzk(sgd)) / sgd.sl) * (1 - zkl));
					if (mode == 3)
						sumzk = ManipulatePrecision.doubleConvert(sumzk + zkl);
					break;
				}

			}
		}

		return sumzk;
	}

	class AmountRebateDef
	{
		public String pmbillno;/* 促销单号 */
		public String addrule;/* 累计规则 'YYYYY' */
		public String Zklist;/* 折扣列表 1:X|2:Y|3:Z */
		public String pmrule;/* 价随量变规则 */
		public String etzkmode2;/* 1-阶梯折扣 2-统一打折 */
		public String seq; /* 规则序号 */
		public double zkfd;
		public String bz;/* 阶梯折扣时 Y/N是否循环 统一折扣时 Y/N是否启用上限数量 */
		public double maxnum; /* 上限数量 */
		public double maxzke; /* 上限上限折扣额 */

		public double zsl;

		public double sl_cond; // 满足数量条件
		public double zkl_result;// 折扣率
		public double cursl; // 计算阶梯折扣时，记录当前数量
		public double curyhzke; // 计算阶梯折扣时，记录当前优惠折扣额

		// 供应商 柜组 品牌 类别 商品
		public String gys;
		public String gz;
		public String pp;
		public String catid;
		public String code;

		Vector list = new Vector();
	};

	public boolean paySellPop()
	{
		if (calcAmountRebate())
		{
			amountPromotion = true;
			return super.paySellPop();
		}
		else
		{
			return false;
		}
	}

	public void paySellCancel()
	{
		super.paySellCancel();

		if (amountPromotion)
		{
			amountPromotion = false;
			calcHeadYsje();

			// 刷新商品列表
			refreshSaleForm();
		}
	}

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		super.findGoodsCRMPop(sg, goods, info);

		String cardno = null;
		String cardtype = null;
		String isfjk = "";
		String grouplist = "";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2)
				isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}

		if (!GlobalInfo.isOnline) { return; }
		// 查询商品价随量变信息
		((Xjly_NetService) NetService.getDefault()).findBatchRule(info, sg.code, sg.gz, sg.uid, goods.str1, sg.catid, sg.ppcode, saleHead.rqsj, cardno, cardtype, isfjk, grouplist, saletype, GlobalInfo.localHttp);
		if (info.Zklist != null && info.Zklist.trim().length() > 1)
		{
			sg.name = "B" + sg.name;
		}
	}

	// 获取退货小票信息
	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			// 如果是新指定小票进入
			if (saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText("开始查找退货小票操作.....");
				if (!DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
				{
					pb.close();
					pb = null;

					thSyjh = null;
					thFphm = 0;

					return false;
				}

				pb.close();
				pb = null;
				// 检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				// Y为已在后台退回礼品 津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					new MessageBox("此小票有满赠礼品，请先到后台退回礼品再办理退货！");
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + "\n是否继续退货？", null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox("原小票是[" + SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) + "]交易\n\n与当前退货交易类型不匹配");

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
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A"))
							sgd.inputbarcode = sgd.barcode;
						row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
							sgd.inputbarcode = sgd.code;
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
					// 选择要退货的商品
					cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open("开单营业员号：", "", "请输入有效开单营业员号", backYyyh, 0);
					// 查找营业员
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
				if (cho < 0 && isbackticket)
					return true;
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

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y"))
						continue;

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
						sgd.hyzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl), thsl), 2, 1); // 会员折扣额(来自会员优惠)
						sgd.yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl), thsl), 2, 1); // 优惠折扣额(来自营销优惠)
						sgd.lszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl), thsl), 2, 1); // 零时折扣额(来自手工打折)
						sgd.lszre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl), thsl), 2, 1); // 零时折让额(来自手工打折)
						sgd.lszzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl), thsl), 2, 1); // 零时总品折扣
						sgd.lszzr = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl), thsl), 2, 1); // 零时总品折让
						sgd.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl), thsl), 2, 1); // 批量折扣
						sgd.zszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl), thsl), 2, 1); // 赠送折扣
						sgd.cjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl), thsl), 2, 1); // 厂家折扣
						sgd.ltzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl), thsl), 2, 1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl), thsl), 2, 1);
						sgd.qtzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl), thsl), 2, 1);
						sgd.qtzre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl), thsl), 2, 1);
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

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				// 数量促销小票
				if (thsaleHead.num5 == 1)
				{
					if (saleGoods.size() != thsaleGoods.size() || saleHead.ysje != thsaleHead.ysje)
					{
						new MessageBox("原交易小票存在数量促销，必须整单退货");
						initOneSale(this.saletype);
						return false;
					}
				}

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

				return isbackticket;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
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

	public double getZZK(SaleGoodsDef saleGoodsDef)
	{
		saleGoodsDef.hjzk = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke + saleGoodsDef.yhzke + saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr + saleGoodsDef.plzke + saleGoodsDef.zszke + saleGoodsDef.cjzke + saleGoodsDef.ltzke + saleGoodsDef.hyzklje + saleGoodsDef.qtzke + saleGoodsDef.qtzre + saleGoodsDef.rulezke + saleGoodsDef.mjzke + saleGoodsDef.num7, 2, 1);

		return saleGoodsDef.hjzk;
	}

	public void calcHeadYsje()
	{
		SaleGoodsDef saleGoodsDef = null;
		int sign = 1;

		saleHead.hjzje = 0;
		saleHead.hjzsl = 0;
		saleHead.hjzke = 0;
		saleHead.hyzke = 0;
		saleHead.yhzke = 0;
		saleHead.lszke = 0;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			if (!statusCond(saleGoodsDef))
			{
				continue;
			}

			// 合计商品件数(电子秤商品总是按1件记数)
			int spjs = (int) saleGoodsDef.sl;
			if (saleGoodsDef.flag == '2')
				spjs = 1;
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
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.num7 * sign), 2, 1); // 超市规则促销折扣（整单折扣）

			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszke * sign), 2, 1); // 零时折扣额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszre * sign), 2, 1); // 零时折让额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzk * sign), 2, 1); // 零时总品折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzr * sign), 2, 1); // 零时总品折让
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.plzke * sign), 2, 1); // 批量折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.cjzke * sign), 2, 1); // 厂家折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.ltzke * sign), 2, 1); // 零头折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzke * sign), 2, 1); // 其他折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzre * sign), 2, 1); // 其他折扣
		}

		saleHead.ysje = ManipulatePrecision.doubleConvert(saleHead.hjzje - saleHead.hjzke, 2, 1);

		// 计算应付
		calcHeadYfje();
	}

	private double getLszzk(SaleGoodsDef sgd)
	{
		return sgd.lszke + sgd.lszre + sgd.lszzk + sgd.lszzr;
	}
}
