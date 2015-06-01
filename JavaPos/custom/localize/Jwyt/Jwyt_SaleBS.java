package custom.localize.Jwyt;

import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCustJfSale;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiInputForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_NetService;

public class Jwyt_SaleBS extends Jwyt_SaleBS0CRMPop
{
	public static String isAutoMSR = "";
	public static int interval = 50;
	public static boolean initFlag = false;
	public static boolean isOpenSelectForm = false;
	public static boolean hykOprType = false;

	public void initSellData()
	{
		super.initSellData();

		// mars.removeAllElements();
		// mars.clear();

		Jwyt_MarsModule.getDefault().clear();
		if (crmPop == null)
			crmPop = new Vector();
		else
			crmPop.removeAllElements();
	}

	// 根据手机号身份证号进行会员授权
	public void execCustomKey0(boolean keydownonsale)
	{
		NewKeyListener.sendKey(GlobalVar.MemberGrant);
		isOpenSelectForm = true;
		hykOprType = true;
	}

	// 根据手机号身份证号进行会员信息查询
	public void execCustomKey1(boolean keydownonsale)
	{
		NewKeyListener.sendKey(GlobalVar.HykInfo);
		isOpenSelectForm = true;
		hykOprType = false;
	}

	// 呼出枪扫二维码
	public void execCustomKey3(boolean keydownonsale)
	{
		// 扫码结果格式 type:text
		// 结果分为两部分，类型、内容。以第一个冒号间隔开。
		// Tcode码类型:TCode
		// 二维码类型: QRCode
		// 条形码类型:ISBN-10 EAN-13

		if (!checkValidate(true))
			return;

		if (keydownonsale && !ewmBusiness())
			return;

	}

	protected boolean ewmBusiness()
	{
		if (Jwyt_MarsModule.getDefault().getMarSaleRet() == null)
			return false;

		// 处理礼券
		if (Jwyt_MarsModule.getDefault().getMarSaleRet().isGiftCoupon())
			return doGiftCode();
		// 处理抵扣
		else if (Jwyt_MarsModule.getDefault().getMarSaleRet().isDeductCoupon())
			return doDeductCode();
		// 处理折扣
		else if (Jwyt_MarsModule.getDefault().getMarSaleRet().isRebateCoupon())
			return doRebateCode();
		else if (Jwyt_MarsModule.getDefault().getMarSaleRet().isMzkCash())
		{
			Jwyt_MarsModule.getDefault().getMarSaleRet().showCashMsg();
			Jwyt_MarsModule.getDefault().clear();
			new MessageBox("该券为储值卡代金券,请在支付时出示!");
		}
		new MessageBox("未找到与该券类型匹配的数据!");
		return true;
	}

	// 手工输入短信辅助码
	public void execCustomKey4(boolean keydownonsale)
	{

		if (!checkValidate(false))
			return;

		if (keydownonsale && !ewmBusiness())
			return;
	}

	public void execCustomKey5(boolean keydownonsale)
	{
		Jwyt_MarsModule.getDefault().getDataFromVideo(this);
	}

	private boolean checkValidate(boolean flag)
	{
		// 通过视频设备得到码串
		if (flag)
		{
			String data = Jwyt_MarsModule.getDefault().getDataFromVideo(null);

			if (data == null || data.trim().equals(""))
			{
				new MessageBox("未读取到数据");
				return false;
			}

			if (data.indexOf(":") < 0)
			{
				new MessageBox("读取到数据不合法");
				return false;
			}

			String[] code = data.split(":");

			if (code[0].equalsIgnoreCase("TCode"))
			{
				if (!Jwyt_MarsModule.getDefault().sendValidateCode(code[1], "", false))
					return false;
			}
			else if (code[0].equalsIgnoreCase("QRCode"))
			{
				if (!Jwyt_MarsModule.getDefault().sendValidateCode("", code[1], false))
					return false;
			}
			else if (code[0].equalsIgnoreCase("ISBN-10") || code[0].equalsIgnoreCase("EAN-13"))
			{
				saleEvent.saleform.code.setText(code[1]);
				saleEvent.saleform.code.setFocus();
				enterInput();
				// if (!Jwyt_MarsModule.getDefault().sendValidateCode("",
				// code[1], false)) return false;
			}
		}
		else
		{
			StringBuffer assistCode = new StringBuffer();
			// 输入顾客卡号
			TextBox txt = new TextBox();
			if (!txt.open("请输入短信辅助码", "辅助码", "请输入短信辅助码", assistCode, 0, 0, false, TextBox.AllInput))
				return false;

			if (!Jwyt_MarsModule.getDefault().sendValidateCode("", assistCode.toString(), false))
				return false;
		}

		// String curDatetime = ManipulateDateTime.getCurrentDateTimeBySign();

		/*
		 * if (new ManipulateDateTime().getDisDateTime(curDatetime,
		 * Jwyt_MarsModule.getDefault().getMarSaleRet().effectiveDate) < 0) {
		 * new MessageBox("还未到用码日期!"); return false; }
		 * 
		 * if (new ManipulateDateTime().getDisDateTime(curDatetime,
		 * Jwyt_MarsModule.getDefault().getMarSaleRet().expireDate) > 0) { new
		 * MessageBox("码已过期!"); return false; }
		 */

		return true;
	}

	protected boolean doRebateCode()
	{
		ProgressBox pb = null;
		try
		{
			// 退货状态下不进行查找
			if (!SellType.ISSALE(this.saletype))
				return true;

			if (saleGoods.size() == 0)
			{
				new MessageBox("请在录完商品且即将付款前使用该促销券");
				return false;
			}

			Jwyt_MarsModule.getDefault().getMarSaleRet().showRebateMsg();

			pb = new ProgressBox();
			pb.setText("正在处理商品折扣券,请稍候......");

			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				GoodsDef goods = (GoodsDef) this.goodsAssistant.elementAt(i);

				// 已经计算过会员促销则跳过,不考虑折上折
				if (sgd.hyzke > 0)
					continue;

				// 存在抵扣的跳过
				if (sgd.str4 != null && sgd.str4.trim().length() > 0 && sgd.str4.trim().startsWith("Deduct"))
					continue;

				pb.setText("正在查找" + sgd.code + "短信码商品促销,请稍候......");
				// 查找二维码商品的促销结果集
				Vector popvec = ((Bstd_NetService) NetService.getDefault()).findCMPOPGoods(saleHead.rqsj, goods, Jwyt_MarsModule.getDefault().getMarSaleRet().marketingid, "", CmdDef.WYT_FINDEWMCMPOP);

				if (popvec != null)
				{
					// 清空折扣
					clearGoodsAllRebate(i);
					goodsCmPop.setElementAt(popvec, i);
					filterCMPOPInfo(popvec);
					calcGoodsPOPRebate(i);
					// 记录下数据，发送给第三方记帐
					sgd.memo = Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode + "," + Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid;
					// calcGoodsYsje(i);
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
			Jwyt_MarsModule.getDefault().clear();
			if (pb != null)
				pb.close();
			pb = null;
		}
	}

	// 礼品
	protected boolean doGiftCode()
	{
		ProgressBox pb = null;

		try
		{
			if (saleGoods.size() > 0)
			{
				new MessageBox("此单存在商品,请完成交易或清除后再使用礼品券!");
				return false;
			}

			Jwyt_MarsModule.getDefault().getMarSaleRet().showGiftMsg();

			pb = new ProgressBox();
			pb.setText("正在查找礼品,请稍候......");

			Vector gift = new Vector();
			if (!((Jwyt_NetService) NetService.getDefault()).findEWMGift(Jwyt_MarsModule.getDefault().getMarSaleRet().marketingid, gift))
			{
				new MessageBox("查找礼品失败!");
				return false;
			}

			String retindex = new Jwyt_GiftForm().open(gift);

			if (retindex == null)
				return false;

			if (retindex.trim().equals(""))
			{
				new MessageBox("处理礼品失败!");
				return false;
			}

			String[] intIndex = retindex.split(",");
			if (intIndex == null)
				return false;

			Vector newGiftVec = new Vector();
			for (int i = 0; i < intIndex.length; i++)
			{
				int index = Convert.toInt(intIndex[i]);
				if (index < 0 || index > gift.size())
					return false;

				if (gift.get(index) == null)
					continue;

				newGiftVec.add(gift.get(index));
			}

			if (newGiftVec == null || newGiftVec.size() == 0)
				return false;

			Jwyt_EwmGiftDef.EwmGiftHead giftHead = new Jwyt_EwmGiftDef.EwmGiftHead();
			giftHead.transseq = Jwyt_MarsModule.getDefault().getMarSaleRet().transseq;
			giftHead.couponname = Jwyt_MarsModule.getDefault().getMarSaleRet().couponname;
			giftHead.couponid = Jwyt_MarsModule.getDefault().getMarSaleRet().couponid;
			giftHead.markingid = Jwyt_MarsModule.getDefault().getMarSaleRet().marketingid;

			Vector giftDetail = new Vector();
			Vector giftSaleGoods = new Vector();

			for (int j = 0; j < newGiftVec.size(); j++)
			{
				String[] item = (String[]) newGiftVec.get(j);
				SaleGoodsDef sgd = new SaleGoodsDef();

				Jwyt_EwmGiftDef.EwmGiftDetail ewm = new Jwyt_EwmGiftDef.EwmGiftDetail();

				ewm.serial = (j + 1) + "";
				ewm.goodscode = item[0];
				ewm.goodsnum = 1 + "";
				ewm.qty = item[2];
				ewm.price = item[3];
				ewm.cost = item[4];
				giftDetail.add(ewm);

				sgd.code = sgd.barcode = ewm.goodscode;
				sgd.name = item[1];
				sgd.rowno = j + 1;
				sgd.sl = Convert.toDouble(item[2]);
				giftSaleGoods.add(sgd);
			}

			if (!Jwyt_MarsModule.getDefault().sendUseCode(Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode, -1))
			{
				new MessageBox("礼品券消费失败!");
				return false;
			}

			pb.setText("正在生成礼品单据并审核,请稍候......");
			Vector retVec = new Vector();

			if (!((Jwyt_NetService) NetService.getDefault()).sendEWMGift(giftHead, giftDetail, retVec))
			{
				new MessageBox("礼品单据审核失败!");

				// 充回去
				Jwyt_MarsModule.getDefault().sendReturnMarCoupon(Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode, Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid, -1);

				return false;
			}

			pb.setText("正在发送短信码日志,请稍候......");
			((Jwyt_NetService) NetService.getDefault()).sendEWMWorkLog((String) retVec.get(0), Jwyt_MarsModule.getDefault().getMarSaleRet());

			pb.setText("正在打印礼品单,请稍候......");
			Jwyt_GiftPrintMode.getDefault().setTemplateObject(saleHead, giftSaleGoods, new Vector());
			Jwyt_GiftPrintMode.getDefault().printBill();
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (pb != null)
				pb.close();
			pb = null;

			Jwyt_MarsModule.getDefault().clear();
		}
	}

	protected boolean createDeductPay(SaleGoodsDef sgd)
	{
		if (sgd.str4 == null || sgd.str4.equals("") || sgd.str4.indexOf(":") < 0)
			return false;

		String[] payitem = sgd.str4.split(":");
		if (payitem == null || payitem.length < 4)
			return false;

		double allowje = Convert.toDouble(payitem[2]);
		double money = Convert.toDouble(payitem[3]);

		PayModeDef pmd = (PayModeDef) DataService.getDefault().searchPayMode("0707");

		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.get(i);
			if (spd.paycode.equals(pmd.code) && spd.payno.equals(payitem[1]) && spd.je == money)
				return false;
		}

		if (pmd == null)
		{
			new MessageBox("未定义抵扣券付款对象!");
			return false;
		}

		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, this);
		pay.allowpayje = allowje;

		if (pay.createSalePayObject(String.valueOf(money)))
		{
			pay.salepay.payno = payitem[1];// Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode;
			pay.salepay.idno = payitem[4];// Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid;
			pay.salepay.batch = payitem[5];// Jwyt_MarsModule.getDefault().getMarSaleRet().transseq;
			pay.salepay.memo = payitem[6];// Jwyt_MarsModule.getDefault().getMarSaleRet().couponid
											// +
											// Jwyt_MarsModule.getDefault().getMarSaleRet().couponname
											// + "," +
											// Jwyt_MarsModule.getDefault().getMarSaleRet().coupontype;
			pay.salepay.kye = Convert.toDouble(payitem[7]);// Convert.toDouble(Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney);
			pay.salepay.str1 = payitem[8];// Jwyt_MarsModule.getDefault().getMarSaleRet().marketingid;
			pay.salepay.str5 = sgd.code;
			// sgd.str4 = "Deduct:" +
			// Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode + ":" +
			// allowje + ":" + money + ":";
			// sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid
			// + ":";
			// sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().transseq
			// + ":";
			// sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().couponid
			// + Jwyt_MarsModule.getDefault().getMarSaleRet().couponname + "," +
			// Jwyt_MarsModule.getDefault().getMarSaleRet().coupontype + ":";
			// sgd.str4 +=
			// Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney + ":";
			// sgd.str4 +=
			// Jwyt_MarsModule.getDefault().getMarSaleRet().marketingid;

			addSalePayObject(pay.salepay, pay);

			calcPayBalance();

			return true;
		}

		return false;
	}

	// 抵扣
	protected boolean doDeductCode()
	{
		// 一张券只能用于一个商品
		ProgressBox pb = null;
		try
		{
			Jwyt_MarsModule.getDefault().getMarSaleRet().showCashMsg();

			pb = new ProgressBox();
			pb.setText("正在处理商品抵扣券,请稍候......");

			for (int i = 0; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				// 若存在会员折扣，不能用抵扣券
				if (sgd.hyzke > 0)
					continue;

				// 如果已经抵扣,则不往下继续了
				if ((sgd.str4 != null && sgd.str4.trim().length() > 0 && sgd.str4.trim().startsWith("Deduct")))
					continue;

				// 同一码不允许多次消费
				if (sgd.str6 != null && sgd.str6.equals(Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode))
				{
					new MessageBox("短信码:" + Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode + "已在商品" + sgd.code + "上使用!");
					break;
				}

				Vector money = new Vector();
				// 查找抵扣二维码
				if (!((Jwyt_NetService) NetService.getDefault()).getEWMDeductRule(sgd.code, Jwyt_MarsModule.getDefault().getMarSaleRet().marketingid, money))
					continue;

				if (money == null || money.size() == 0)
					continue;

				String[] deductRule = (String[]) money.get(0);
				if (deductRule == null || deductRule.length < 2)
					continue;

				double dkje = Convert.toDouble(deductRule[0]);
				double limitsl = Convert.toDouble(deductRule[1]);

				// 如果合计金额小于最低的抵扣金额，也不抵扣
				if (sgd.sl < limitsl)
					continue;

				double allowje = Convert.toDouble(Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney);
				dkje = Math.min(allowje, dkje);

				if (dkje == 0 || dkje == sgd.hjje)
				{
					new MessageBox("定义的券抵扣金额等于商品合计金额,不允许抵扣!");
					continue;
				}

				sgd.str6 = Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode;

				sgd.str4 = "Deduct:" + Jwyt_MarsModule.getDefault().getMarSaleRet().assistcode + ":" + allowje + ":" + dkje + ":";
				sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().tcodeid + ":";
				sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().transseq + ":";
				sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().couponid + "," + Jwyt_MarsModule.getDefault().getMarSaleRet().couponname + "," + Jwyt_MarsModule.getDefault().getMarSaleRet().coupontype + ":";
				sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().balancemoney + ":";
				sgd.str4 += Jwyt_MarsModule.getDefault().getMarSaleRet().marketingid;

				// 清空所有折扣
				clearGoodsAllRebate(i);
				goodsCmPop.setElementAt(null, i);

				Jwyt_MarsModule.getDefault().clear();
				break;

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
			if (pb != null)
				pb.close();
			pb = null;

			Jwyt_MarsModule.getDefault().clear();

			// 刷新一下界面显示
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
			saleEvent.setCurGoodsBigInfo();
		}
	}

	public boolean memberGrant()
	{
		hykOprType = true;
		return super.memberGrant();
	}

	public void initSetYYYGZ(String type, boolean iscsinput)
	{
		// 是否输入营业员,Y-输入营业员/N-超市不输入营业员/B-百货不输入营业员/A-可输可不输,不输入时为超市,输入时为营业员
		if (SellType.ISCHECKINPUT(type))
		{
			saleEvent.yyyh.setText("盘点");
			saleEvent.gz.setText("");
			saleEvent.saleform.setFocus(saleEvent.code);
		}
		else
		{
			if (GlobalInfo.posLogin.gh == null && GlobalInfo.posLogin.gh.length() == 0)
				GlobalInfo.posLogin.gh = "";

			if (GlobalInfo.sysPara.mktname == null && GlobalInfo.sysPara.mktname.length() == 0)
				GlobalInfo.sysPara.mktname = "";

			if (GlobalInfo.syjDef.issryyy == 'N')
			{
				saleEvent.yyyh.setText(GlobalInfo.posLogin.gh);
				saleEvent.gz.setText(GlobalInfo.sysPara.mktname);
				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else
			{
				GlobalInfo.syjDef.issryyy = 'N';
				saleEvent.yyyh.setText(GlobalInfo.posLogin.gh);
				saleEvent.gz.setText(GlobalInfo.sysPara.mktname);
				saleEvent.saleform.setFocus(saleEvent.code);
			}
		}
	}

	public boolean findGoods(String code, String yyyh, String gz)
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
				new MessageBox("电子秤码校验位错误", null, false);

				return false;
			}

			if (dzcmjg <= 0 && dzcmsl <= 0)
			{
				new MessageBox("该电子秤格式条码无效", null, false);

				return false;
			}
		}
		else
		{
			comcode = barcode;
		}

		// 查找详细商品资料,可支持数量转换
		StringBuffer slbuf = new StringBuffer("1");
		GoodsDef goodsDef = findGoodsInfo(comcode, yyyh, gz, dzcmscsj, isdzcm, slbuf);
		if (goodsDef == null)
			return false;
		quantity *= Convert.toDouble(slbuf.toString());

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
			StringBuffer inputSL = new StringBuffer();
			StringBuffer inputJE = new StringBuffer();
			double sl = 0.0, je = 0.0;

			while (true)
			{
				inputSL.delete(0, inputSL.length());
				inputJE.delete(0, inputJE.length());

				inputSL.append(0.0);
				inputJE.append(0.0);

				if (new MutiInputForm().open("请输入价格或数量(两者不能同时输入)", "商品数量", inputSL, "商品金额", inputJE, TextBox.DoubleInput))
				{
					sl = Double.parseDouble(inputSL.toString());
					je = Double.parseDouble(inputJE.toString());

					if (sl == 0 && je == 0)
						new MessageBox("价格或数量不能同时为 0,请重新输入");
					else if (sl != 0 && je != 0)
						new MessageBox("不能同时输入价格和数量,请重新输入");
					else
						break;
				}
				else
					return false;
			}

			if (je != 0)
				dzcmjg = ManipulatePrecision.doubleConvert(getConvertPrice(je, goodsDef), 2, 1);
			else if (sl != 0)
				dzcmsl = Double.parseDouble(inputSL.toString());
			isdzcm = true;
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

						if (SellType.ISBACK(saletype))
						{
							dzcmjgzk = allprice - ManipulatePrecision.doubleConvert(quantity * goodsDef.pfj);
						}
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
					String[] title = { "商品编码", "数量", "单价", "合计折扣", "应付金额" };
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

					cho = new MutiSelectForm().open("请选择退货商品信息", title, width, choice);
				}
				thSaleGoods = (SaleGoodsDef) back.elementAt(cho);

				if (thSaleGoods.sl < quantity)
				{
					new MessageBox("该商品退货数量大于原销售数量\n\n不能退货");
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
				if (!isonlinegdjging)
				{
					StringBuffer pricestr = new StringBuffer();
					do
					{
						pricestr.delete(0, pricestr.length());
						pricestr.append(price);

						double min = 0.01;
						if (goodsDef.type == 'Z')
						{
							min = 0;
						}

						boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "价格", "价格", "", pricestr, min, getMaxSaleGoodsMoney(), true);
						if (!done)
						{
							return false;
						}
						else
						{
							price = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

							// 检查价格
							if (price <= 0 && goodsDef.type != 'Z')
							{
								new MessageBox("该商品价格必须大于0");
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
					} while (true);
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

	public void customerIsZk(CustomerDef cust)
	{
		// 具有折扣功能
		if (cust.iszk == 'Y')
		{
			// 记录到小票
			saleHead.hysq = cust.code;

			// 设置当前授权卡为顾客卡
			cursqkh = cust.code;
			cursqktype = '2';
			cursqkzkfd = 1;

			// 授权
			// String msg = "";
			if (cust.func == null || cust.func.length() <= 0)
				cust.func = "A";
			if (cust.func.charAt(0) != 'Y' && cust.func.charAt(0) != 'N')
			{
				curGrant.zpzkl = cust.zkl;
				curGrant.dpzkl = cust.zkl;
				// msg = "顾客卡授权打折\n\n总品及单品折扣:" +
				// ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}
			if (cust.func.charAt(0) == 'Y')
			{
				curGrant.zpzkl = cust.zkl;
				// msg = "顾客卡授权打折\n\n总品折扣:" +
				// ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}
			if (cust.func.charAt(0) == 'N')
			{
				curGrant.dpzkl = cust.zkl;
				// msg = "顾客卡授权打折\n\n单品折扣:" +
				// ManipulatePrecision.doubleToString(cust.zkl * 100) + "%";
			}

			// 提示
			// new MessageBox(msg);
		}
	}

	public boolean paySellStart()
	{
		if (!super.paySellStart())
			return false;

		boolean isvipzk = false;
		// 如果为VIP折扣区间的打折方式，在满减前计算
		if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y' && GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
		{
			// vipzk2表示按下付款键时才计算VIP折扣
			for (int k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(k);
				SaleGoodsDef clone = (SaleGoodsDef) sgd.clone();

				clearGoodsAllRebate(k);

				getVIPZK(k, vipzk2);

				if (sgd.hyzke > 0)
				{
					goodsCmPop.setElementAt(null, k);
					isvipzk = true;
				}

				if (!isvipzk)
					saleGoods.setElementAt(clone, k);
			}

			// 重算小票应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();

			// 显示会员卡折扣总金额
			if (saleHead.hyzke > 0)
				new MessageBox("会员折扣总金额 ：" + saleHead.hyzke);
		}

		if (!isvipzk)
		{
			// 创建抵扣付款方式
			for (int k = 0; k < saleGoods.size(); k++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(k);
				createDeductPay(sgd);
			}
		}
		return true;
	}

	public boolean inputQuantity(int index, double quantity)
	{
		if (SellType.isJS(saletype)) { return false; }

		SaleGoodsDef oldGoodsDef = null;
		SpareInfoDef oldSpare = null;
		double newsl = -1;
		boolean flag = false;
		// 如果输入了
		if (quantity >= 0)
		{
			flag = true;
			newsl = quantity;
		}

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 非称重商品，当库存小于1时，不让修改数量
		/*
		 * if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' &&
		 * goodsDef.isxh != 'Y')) { new MessageBox("该商品库存小于1,不允许修改数量"); return
		 * false; }
		 */

		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8))
			return false;
		// 判断是否允许修改数量
		if (!allowInputQuantity(index))
			return false;

		// 输入数量
		StringBuffer buffer = new StringBuffer();
		do
		{
			if (!flag)
			{
				buffer.delete(0, buffer.length());
				buffer.append(ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1, true));

				// 检查是否从电子称里获取重量金额
				boolean input = true;

				if (input)
				{
					if (SellType.ISCOUPON(saletype) || (saleEvent.yyyh.getText().trim().equals("超市") && GlobalInfo.sysPara.goodsAmountInteger == 'Y') && goodsDef.isdzc != 'Y')
					{
						if (!new TextBox().open("请输入该商品数量", "数量", "", buffer, 1, getMaxSaleGoodsQuantity(), true, TextBox.IntegerInput, -1)) { return false; }
					}
					else
					{
						if (!new TextBox().open("请输入该商品数量", "数量", "", buffer, 0.0001, getMaxSaleGoodsQuantity(), true)) { return false; }
					}
					newsl = Double.parseDouble(buffer.toString());
				}
				newsl = ManipulatePrecision.doubleConvert(newsl, 4, 1);
				flag = true;
			}

			// 非电子秤商品如果数量是小数，则不让修改
			if (goodsDef.isdzc != 'Y')
			{
				String strJe = ManipulatePrecision.doubleToString(newsl, 2, 1);
				int pos = strJe.indexOf(".");

				if (strJe.indexOf(".") != -1 && Double.parseDouble(strJe.substring(pos + 1)) > 0)
				{
					new MessageBox("该商品数量不允许输入小数,请重新输入");
					return false;
				}
			}

			// 检查销红
			if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					if (GlobalInfo.sysPara.xhisshowsl == 'Y')
						new MessageBox("销售数量已大于该商品库存【" + goodsDef.kcsl + "】\n\n不能销售");
					else
						new MessageBox("该商品库存不足,不能销售");

					if (flag)
						return false;
					continue;
				}
			}

			// 指定小票退货
			if (isSpecifyBack(saleGoodsDef))
			{
				// 统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl, goodsDef.bzhl)) + ManipulatePrecision.mul(newsl, goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					new MessageBox("退货数量已大于该商品原销售数量\n\n不能退货");
					if (flag)
						return false;
					continue;
				}
			}

			// 跳出循环
			break;
		} while (true);

		if (newsl < 0)
			return false;

		// 无权限
		if ((newsl < saleGoodsDef.sl) && (curGrant.privqx != 'Y') && (curGrant.privqx != 'Q'))
		{
			//
			OperUserDef staff = inputQuantityGrant(index);
			if (staff == null)
				return false;

			// 记录日志
			String log = "授权修改数量,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",数量:" + newsl + ",授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		if (info != null)
			oldSpare = (SpareInfoDef) info.clone();

		// 重算商品应收
		double oldsl = saleGoodsDef.sl;
		saleGoodsDef.sl = newsl;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		double lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre / oldsl * newsl);
		double lszzk = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke / oldsl * newsl);
		clearGoodsGrantRebate(index);
		saleGoodsDef.lszre = lszre;
		saleGoodsDef.lszke = lszzk;

		getZZK(saleGoodsDef);
		calcGoodsYsje(index);

		// 重算小票应收
		calcHeadYsje();

		// 数量过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox("商品数量过大,导致销售金额达到上限\n\n商品数量修改无效");

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 退货金额过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox("商品数量过大,导致退货金额超过限额\n\n商品数量修改无效");

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "A";
			}
			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}

		return true;
	}

	public boolean checkPaymodeValid(PayModeDef mode, String money)
	{
		try
		{
			// 前提条件:本单存在折扣
			if (SellType.ISSALE(saletype) && super.saleHead.hjzke > 0)
			{
				// 禁用的付款编码
				if (GlobalInfo.sysPara.disablepaycodewhenrebate != null && GlobalInfo.sysPara.disablepaycodewhenrebate.length() > 0)
				{
					String[] tmpPaycode = GlobalInfo.sysPara.disablepaycodewhenrebate.split(",");
					if (tmpPaycode != null && tmpPaycode.length > 0)
					{
						for (int i = 0; i < tmpPaycode.length; i++)
							if (mode.code.equals(tmpPaycode[i].trim()))
							{
								new MessageBox("该笔交易存在折扣,不支持【" + mode.name + "】付款");
								return false;
							}
					}
				}
			}

			// 退货时判断付款方式
			if (isSpecifyTicketBack() && GlobalInfo.sysPara.isctrlthpay == 'Y')
			{
				if (backPayment != null && backPayment.size() > 0 && mode.ismj == 'Y' && !money.equals(""))
				{
					double zlje = 0.0;
					double yfje = 0.0;
					SalePayDef tmpPay = null;

					// 先将找零金额过滤出来
					for (int j = 0; j < backPayment.size(); j++)
					{
						tmpPay = (SalePayDef) backPayment.get(j);
						yfje = ManipulatePrecision.doubleConvert(yfje + tmpPay.je, 2, 1);

						if (tmpPay.flag == '1')
							continue;
						if (tmpPay.flag == '2')
							zlje = tmpPay.je;
					}

					for (int i = 0; i < backPayment.size(); i++)
					{
						tmpPay = (SalePayDef) backPayment.get(i);

						// 编码相等
						if (mode.code.equals(tmpPay.paycode))
						{
							// 非现金且金额必须相等
							if (mode.type != '1')
							{
								if (ManipulatePrecision.doubleConvert(Double.parseDouble(money)) == ManipulatePrecision.doubleConvert(tmpPay.je))
									return super.checkPaymodeValid(mode, money);
								else
								{
									new MessageBox("请核对当前[付款方式/金额]是否与原始小票付款一致");
									return false;
								}
							}
							// 金额不等，再减去找零部分，看金额是否相等
							else
							{
								if (ManipulatePrecision.doubleConvert(Double.parseDouble(money)) == ManipulatePrecision.doubleConvert(tmpPay.je))
								{
									return super.checkPaymodeValid(mode, money);
								}
								else if (ManipulatePrecision.doubleConvert(Double.parseDouble(money)) < ManipulatePrecision.doubleConvert(tmpPay.je))
								{
									if (ManipulatePrecision.doubleConvert(Double.parseDouble(money)) == ManipulatePrecision.doubleConvert(tmpPay.je - zlje))
										return super.checkPaymodeValid(mode, money);
								}
								else
								{
									new MessageBox("请核对当前[付款方式/金额]是否与原始小票付款一致");
									return false;
								}
							}
						}
					}

					new MessageBox("请核对当前[付款方式/金额]是否与原始小票付款一致");
					return false;
				}
			}

			return super.checkPaymodeValid(mode, money);
		}
		catch (Exception ex)
		{
			new MessageBox(ex.getMessage());
			return false;
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

				// 检查此小票是否已经退货过，给出提示
				if (thsaleHead.str1.trim().length() > 0)
				{

					pb.close();
					pb = null;

					new MessageBox(thsaleHead.str1);
					// super.backToSaleStatus();

					// 初始化交易
					// initOneSale(this.saletype);
					return false;
				}

				pb.close();
				pb = null;

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
							row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
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

				// 选择要退货的商品
				int cho = new MutiSelectForm().open("【提示】用户对该窗口所做修改均为无效，请回车选中后按确认键保存退出", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);

				if (cho < 0 && isbackticket)
					return true;

				else if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}
				/*
				 * if (cho==-2 || (cho == -1 && choice.size()==0)) { thSyjh =
				 * null; thFphm = 0; return false; } else if (cho == -2 &&
				 * isbackticket) // 如果cho小于0且已经选择过退货小票 return true;
				 */

				// 将退货授权保存下来
				String thsq = saleHead.thsq;

				// 清除已有商品明细,重新初始化交易变量
				initSellData();

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					/*
					 * if (!row[6].trim().equals("Y")) continue;
					 */

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					// 数量输入无效，始终以后台找下来的数量为准
					double thsl = sgd.sl;// ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]),
					// 4, 1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					sgd.num2 = sgd.yrowno;

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

	public static void autoMSR()
	{
		try
		{
			if (!initFlag)
			{
				if (ConfigClass.CustomItem1 != null && ConfigClass.CustomItem1.length() > 0 && ConfigClass.CustomItem1.indexOf(",") > 0)
				{
					String[] para = ConfigClass.CustomItem1.split(",");
					if (para != null && para.length > 0)
						isAutoMSR = para[0].trim();
					if (para != null && para.length > 1)
						interval = Integer.parseInt(para[1].trim());
					initFlag = true;
				}
			}

			if (isAutoMSR.equals("Y") && initFlag)
			{
				Display.getDefault().syncExec(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(interval);
							NewKeyListener.sendKey(GlobalVar.ICInput);
						}
						catch (Exception ex)
						{

						}
					}
				});
			}
		}
		catch (Exception ex)
		{
			initFlag = false;
			ex.printStackTrace();
		}
	}

	public boolean checkFinalStatus()
	{
		try
		{
			String tmpMemo = ",";
			for (int a = 0; a < saleGoods.size(); a++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(a);

				if (sgd.memo == null || sgd.memo.trim().equals(""))
					continue;

				// 无促销单号，则未享受促销
				if ((sgd.yhdjbh == null || sgd.yhdjbh.equals("")) && (sgd.zsdjbh == null || sgd.zsdjbh.equals("")) && (sgd.str3 == null || sgd.str3.equals("")))
					continue;

				if (sgd.memo.indexOf(",") < 0)
					continue;

				String[] item = sgd.memo.split(",");

				// 已经发送过了用码，则不再发送
				if (tmpMemo.indexOf("," + item[0] + ",") >= 0)
					continue;

				if (Jwyt_MarsModule.getDefault().sendUseCode(item[0], -1))
				{
					// 发送完就清空，避免多次发送
					sgd.memo = "";
					((Jwyt_NetService) NetService.getDefault()).sendEWMWorkLog(Jwyt_MarsModule.getDefault().getMarSaleRet());
				}
				else
				{
					new MessageBox("短信促销码:" + item[0] + "用码失败!");
					return false;
				}

				tmpMemo = tmpMemo + item[0] + ",";
				Jwyt_MarsModule.getDefault().clear();
			}

			for (int j = 0; j < salePayment.size(); j++)
			{
				SalePayDef spd = (SalePayDef) salePayment.get(j);

				if (spd.str2 == null || !spd.str2.equals("EWM"))
					continue;

				if (SellType.ISBACK(saletype))
				{
					if (spd.str5 == null || !spd.str5.equals("Y"))
					{
						if (Jwyt_MarsModule.getDefault().sendReturnMarCoupon(spd.payno, spd.idno, spd.je))
						{
							spd.str5 = "Y"; // 标记已成功记录第三方记账OK
							((Jwyt_NetService) NetService.getDefault()).sendEWMWorkLog(Jwyt_MarsModule.getDefault().getMarSaleRet());
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					if (Jwyt_MarsModule.getDefault().sendUseCode(spd.payno, spd.je))
					{
						spd.str5 = "Y";
						((Jwyt_NetService) NetService.getDefault()).sendEWMWorkLog(Jwyt_MarsModule.getDefault().getMarSaleRet());
					}
					else
					{
						return false;
					}
				}

				Jwyt_MarsModule.getDefault().clear();
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
			Jwyt_MarsModule.getDefault().clear();
		}
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

		if (!((Jwyt_DataService) DataService.getDefault()).getJfExchangeGoods(jfrd, saleGoodsDef.code, saleGoodsDef.gz, curCustomer.code, curCustomer.type))
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

	public void paySellCancel()
	{
		// 判断是否需要删除VIP折扣
		if (checkMemberSale() && curCustomer != null && curCustomer.iszk == 'Y' && GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
		{
			for (int i = 0; i < saleGoods.size(); i++)
			{
				// 不为VIP折扣的商品，不删除VIP折扣
				GoodsDef gd1 = (GoodsDef) goodsAssistant.get(i);
				if (gd1.isvipzk == 'N')
					continue;

				SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				if (goodsSpare.size() <= i)
					continue;
				SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(i);
				if (info.char1 == 'Y')
				{
					continue;
				}
				saleGoodsDef.hyzke = 0;
				getZZK(saleGoodsDef);
			}

			// 重算小票应收
			calcHeadYsje();

			// 刷新商品列表
			saleEvent.updateTable(getSaleGoodsDisplay());
			saleEvent.setTotalInfo();
		}

		super.paySellCancel();
	}

}
