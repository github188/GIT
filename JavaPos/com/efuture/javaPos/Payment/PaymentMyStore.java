package com.efuture.javaPos.Payment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.HttpService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

//insert into SYSPARA (code, name, paravalue, status, memo)
//values ('17', '微店密钥', '', 'Y', '微店密钥');

//insert into SYSPARA (code, name, paravalue, status, memo)
//values ('18', '微店商户调用标识符', '', 'Y', '销方识别号');

//insert into SYSPARA (code, name, paravalue, status, memo)
//values ('19', '微店http地址', '', 'Y', '微店http地址');

//将O5参数根据需要设置，O5所表含义如下：
//是否联网实时计算返券(N-不实时计算/Y-实时计算电子券/A-实时计算并打印纸券),O5
//是否联网实时计算微店返券(N-不实时计算/A-实时计算并打印),O5

public class PaymentMyStore extends Payment
{
	public static final String INPUTSALEGOODS = "inputsalegoods";
	public static final String INPUTPAY = "inputpay";
	public static final String COMPLETESALE = "completesale";
	public static final String CHECKCOUPON = "checkcoupon";
	public static final String CANCELSALE = "cancelsale";
	public static final String CANCELCOUPON = "cancelcoupon";
	public static final String RETCOUPON = "retcoupon";

	public PaymentMyStore()
	{

	}

	public PaymentMyStore(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public PaymentMyStore(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			if (getMystoreCouponCount() >= GlobalInfo.sysPara.mystoreusestimes)
			{
				new MessageBox("一单中只能使用" + GlobalInfo.sysPara.mystoreusestimes + "张微店券!");
				return null;
			}

			// 先判断付款列表中是否存在微店券，若存在直接取之前的订单号
			String mainid = getMystoreCouponMainid(saleBS.salePayment);

			if (mainid == null)
			{
				mainid = String.valueOf(ManipulateDateTime.getMillisByDatetime(new ManipulateDateTime().getDateByEmpty(), new ManipulateDateTime().getTimeByEmpty()));

				// mainid = GlobalInfo.sysPara.mktcode +
				// GlobalInfo.syjStatus.syjh + GlobalInfo.syjStatus.fphm +
				// ManipulateDateTime.getCurrentDateTimeByEmpty();

				// 预上传商品明细
				if (!preSendTicketCoupon(mainid, saleBS.saleHead, saleBS.saleGoods))
				{
					new MessageBox("预上传商品明细失败,无法计算当前可用券!");
					return null;
				}
			}

			if (SellType.ISBACK(saleBS.saletype))
			{
				if (!backCoupon(mainid))
					return null;

				return salepay;
			}

			if (!checkCoupon(mainid))
				return null;

			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public String getMystoreCouponMainid(Vector salepay)
	{
		if (salepay == null)
			return null;

		for (int i = 0; i < salepay.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salepay.get(i);

			if (CreatePayment.getDefault().isPaymentMystore(spd))
				return spd.idno;
		}
		return null;
	}

	private int getMystoreCouponCount()
	{
		int count = 0;

		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);
			if (CreatePayment.getDefault().isPaymentMystore(spd))
				count++;
		}

		return count;
	}

	public boolean isExistSameCoupon(String coupon)
	{
		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) saleBS.salePayment.get(i);

			if (spd.payno.equals(coupon))
				return true;
		}
		return false;
	}

	protected boolean createGiftPay(String code, String mainid, String coupon)
	{
		try
		{
			salepay = new SalePayDef();
			salepay.syjh = saleBS.saleHead.syjh;
			salepay.fphm = saleBS.saleHead.fphm;
			salepay.paycode = paymode.code;
			salepay.payname = paymode.name;
			salepay.flag = '1';
			salepay.ybje = 0;
			salepay.hl = paymode.hl;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);
			salepay.batch = "";
			salepay.kye = 0;
			salepay.idno = mainid;
			salepay.payno = coupon;
			salepay.memo = code;
			salepay.str1 = "gift";
			salepay.str2 = "";
			salepay.str3 = "";
			salepay.str4 = "";
			salepay.str5 = "";
			salepay.num1 = 0;
			salepay.num2 = 0;
			salepay.num3 = 0;
			salepay.num4 = 0;
			salepay.num5 = 0;

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("生成交易付款对象出现异常\n\n") + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	public boolean createSalePay(String mainid, String coupon, String money)
	{
		this.allowpayje = saleBS.calcPayBalance();

		// 必须一次性用完
		if (super.createSalePay(String.valueOf(money)))
		{
			salepay.idno = mainid;
			salepay.payno = coupon;
			return true;
		}

		return false;
	}

	public boolean cancelPay()
	{
		if (!cancelCoupon(salepay.idno, salepay.payno))
			return false;

		// 删除赠品
		if (salepay.str1.equals("gift"))
			deleteGift(salepay.memo);

		cancelSale(null);
		return true;
	}

	protected String getTicketDetailJson(String mainid, SaleHeadDef salehead, Vector salegoods)
	{
		try
		{
			JSONObject data = new JSONObject();
			data.put("mainid", mainid);

			JSONArray item = new JSONArray();
			for (int i = 0; i < salegoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) salegoods.get(i);
				JSONObject goods = new JSONObject();
				goods.put("itemid", salehead.fphm + sgd.code + sgd.rowno);
				goods.put("goodsno", sgd.code);
				goods.put("barcode", sgd.barcode);
				goods.put("goodsname", sgd.name);
				goods.put("qty", String.valueOf(sgd.sl));
				goods.put("price", String.valueOf(sgd.lsj));
				goods.put("salevalue", ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk, 2, 1));
				goods.put("goodstype", "");
				goods.put("deptno", sgd.catid);
				goods.put("promtype", "");
				goods.put("promprice", ManipulatePrecision.doubleToString((sgd.hjje - sgd.hjzk) / sgd.sl, 2, 1));
				goods.put("discvalue", String.valueOf(sgd.hjzk));
				goods.put("placeno", sgd.gz);
				item.add(goods);
			}

			data.put("item", item);
			System.out.println(data.toString());

			return data.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected String getTicketPayJson(String mainid, Vector salepay)
	{
		try
		{
			JSONObject data = new JSONObject();
			data.put("mainid", mainid);

			JSONArray item = new JSONArray();
			for (int i = 0; i < salepay.size(); i++)
			{
				SalePayDef spd = (SalePayDef) salepay.get(i);
				JSONObject pay = new JSONObject();
				pay.put("itemid", GlobalInfo.syjStatus.fphm + spd.paycode + spd.rowno);
				pay.put("paytype", spd.paycode);
				pay.put("payvalue", String.valueOf(spd.je));
				pay.put("aboutno", spd.payno);
				pay.put("couponshop", spd.str1);
				pay.put("coupontype", spd.str2);

				item.add(pay);
			}

			data.put("item", item);

			System.out.println(data.toString());
			return data.toString();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected String getTickeConfirmJson(String mainid, String yyyh, String hyk)
	{
		try
		{
			JSONObject data = new JSONObject();
			data.put("mainid", mainid);

			data.put("shopid", GlobalInfo.sysPara.mktcode);
			data.put("posno", GlobalInfo.syjStatus.syjh);
			data.put("flowno", String.valueOf(GlobalInfo.syjStatus.fphm - 1));
			data.put("cardno", hyk == null ? "" : hyk);
			data.put("cashier", GlobalInfo.syjStatus.syyh);
			data.put("assistant", yyyh);
			data.put("saledate", ManipulateDateTime.getCurrentDateTimeBySign());

			System.out.println(data.toString());
			return data.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected String getBackCouponJson(String oldmainid, String couponno, String money)
	{
		try
		{
			JSONObject data = new JSONObject();
			data.put("mainid", GlobalInfo.sysPara.mktcode + GlobalInfo.syjStatus.syjh + GlobalInfo.syjStatus.fphm + ManipulateDateTime.getCurrentDateTimeByEmpty());
			data.put("shopid", GlobalInfo.sysPara.mktcode);
			data.put("oldmainid", oldmainid);
			data.put("couponno", couponno);
			data.put("couponvalue", money);

			data.put("data", data);

			return data.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected String getCancelSaleJson(String mainid)
	{
		try
		{
			JSONObject data = new JSONObject();
			data.put("mainid", mainid);
			data.put("shopid", GlobalInfo.sysPara.mktcode);
			data.put("cardno", saleBS.saleHead.hykh == null ? "" : saleBS.saleHead.hykh);

			System.out.println(data.toString());
			return data.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	protected String getCheckOrCancelCouponJson(String mainid, String couponno)
	{
		try
		{
			JSONObject data = new JSONObject();
			data.put("mainid", mainid);
			data.put("shopid", GlobalInfo.sysPara.mktcode);
			data.put("shopname", GlobalInfo.sysPara.mktname);
			data.put("posno", GlobalInfo.syjStatus.syjh);
			data.put("flowno", String.valueOf(GlobalInfo.syjStatus.fphm));
			data.put("cardno", saleBS.saleHead.hykh == null ? "" : saleBS.saleHead.hykh);
			// data.put("cardno", "88888888");
			data.put("saledate", ManipulateDateTime.getCurrentDateTime());
			data.put("couponno", couponno);

			System.out.println(data.toString());
			return data.toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected String[] parseRetJson(String json, boolean flag)
	{
		String[] retval = new String[2];

		if (ConfigClass.DebugMode)
			new MessageBox("ret:" + json);

		System.out.println(json);

		try
		{
			JSONObject retJson = JSONObject.fromObject(json);
			if (flag)
			{
				retval[0] = retJson.getString("code");
				retval[1] = retJson.getString("msg");
			}
			else
			{
				retval[0] = retJson.getString("type");
				retval[1] = retJson.getString("value");
			}

			return retval;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("解析返回数据异常" + ex.getMessage());
			return null;
		}
	}

	protected void showRetJson(String json)
	{

		Vector coupons = null;
		try
		{
			JSONObject retJson = JSONObject.fromObject(json);
			JSONArray ary = retJson.getJSONArray("msg");

			Iterator iterator = ary.iterator();
			int count = 1;
			while (iterator.hasNext())
			{
				JSONObject item = (JSONObject) iterator.next();
				String[] retval = new String[7];

				retval[0] = item.getString("title");
				retval[1] = item.getString("cardno");
				retval[2] = item.getString("couponno");
				retval[3] = item.getString("starttime");
				retval[4] = item.getString("endtime");
				retval[5] = item.getString("content");
				retval[6] = item.getString("systemid");

				StringBuffer info = new StringBuffer();
				info.append("返券   " + String.valueOf(count++) + "\n");
				info.append("卡    号:" + Convert.appendStringSize("", retval[1], 1, 16, 16, 0) + "\n");
				info.append("券    号:" + Convert.appendStringSize("", retval[2], 1, 16, 16, 0) + "\n");
				info.append("起始时间:" + Convert.appendStringSize("", retval[3].substring(0, 10), 1, 16, 16, 0) + "\n");
				info.append("截止时间:" + Convert.appendStringSize("", retval[4].substring(0, 10), 1, 16, 16, 0) + "\n");

				new MessageBox(info.toString());

				if (coupons == null)
					coupons = new Vector();

				coupons.add(retval);
			}

			SaleBillMode.getDefault().setMystoreCoupon(coupons);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("解析返回数据异常" + ex.getMessage());
		}
	}

	protected boolean findCouponGift(String code)
	{
		StringBuffer slbuf = new StringBuffer("1");

		try
		{
			GoodsDef goodsDef = saleBS.findGoodsInfo(code, saleBS.saleEvent.yyyh.getText(), saleBS.saleEvent.gz.getText(), "", false, slbuf);
			if (goodsDef == null)
				return false;

			if (!saleBS.checkFindGoodsAllowSale(goodsDef, 1, false, 0.0, 0.0))
				return false;

			if (!saleBS.allowFinishFindGoods(goodsDef, 1, 0.0))
				return false;

			SaleGoodsDef sgd = saleBS.goodsDef2SaleGoods(goodsDef, saleBS.saleEvent.yyyh.getText(), 1, 0, 0, false);
			sgd.str10 = "gift";
			saleBS.saleGoods.add(sgd);
			saleBS.goodsAssistant.add(goodsDef);
			saleBS.goodsSpare.add(saleBS.getGoodsSpareInfo(goodsDef, sgd));

			saleBS.refreshSaleForm();

			new MessageBox("请查看赠品:[" + goodsDef.code + "]" + goodsDef.name);
			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("处理赠品发生异常!");
			return false;
		}
	}

	// 预上传小票明细
	public boolean preSendTicketCoupon(String mainid, SaleHeadDef salehead, Vector salegoods)
	{
		ProgressBox box = new ProgressBox();
		box.setText("正在预上传小票明细,请稍等...");
		try
		{
			HttpService http = new HttpService();
			ArrayList param = new ArrayList();
			param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
			param.add(http.obtainParameter("sign", ""));
			param.add(http.obtainParameter("data", getTicketDetailJson(mainid, salehead, salegoods)));

			String ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + INPUTSALEGOODS, param, GlobalInfo.sysPara.timeoutpresendcoupon);

			if (ret == null)
				return false;

			String[] retval = parseRetJson(ret, true);

			if (retval == null)
				return false;

			if (!retval[0].equals("0"))
			{
				new MessageBox(retval[1]);
				return false;
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
			if (box != null)
				box.close();
			box = null;
		}
	}

	public boolean backCoupon(String mainid)
	{
		TextBox txt = new TextBox();
		StringBuffer buf = new StringBuffer();
		String coupon = null;
		String oldtrans = null;
		String money = null;

		try
		{
			if (!txt.open("请扫描或输入二维券码", "券号", "手机券码", buf, 0, 0, false, TextBox.AllInput))
				return false;

			coupon = buf.toString().trim();

			if (coupon.equals(""))
				return false;

			buf.delete(0, buf.toString().length());
			if (!txt.open("请输入原销售订单号", "订单号", "退货订单号", buf, 0, 0, false, TextBox.AllInput))
				return false;

			oldtrans = buf.toString().trim();

			if (oldtrans.equals(""))
				return false;

			buf.delete(0, buf.toString().length());
			if (!txt.open("请输入原销售订单消费金额", "退货金额", "退货金额", buf, 0, 0, false, TextBox.DoubleInput))
				return false;

			money = buf.toString().trim();
			if (money.equals(""))
				return false;

			HttpService http = new HttpService();
			ArrayList param = new ArrayList();
			param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
			param.add(http.obtainParameter("sign", ""));
			param.add(http.obtainParameter("data", getCheckOrCancelCouponJson(mainid, coupon)));

			String ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + RETCOUPON, param, GlobalInfo.sysPara.timeoutpresendcoupon);

			if (ret == null)
				return false;

			String[] retval = parseRetJson(ret, true);

			if (retval == null)
				return false;

			if (!retval[0].equals("0"))
			{
				new MessageBox(retval[1]);
				return false;
			}

			if (!createSalePay(mainid, coupon, money))
				return false;

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean cancelCoupon(String mainid, String coupon)
	{
		try
		{
			HttpService http = new HttpService();
			ArrayList param = new ArrayList();
			param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
			param.add(http.obtainParameter("sign", ""));
			param.add(http.obtainParameter("data", getCheckOrCancelCouponJson(mainid, coupon)));

			String ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + CANCELCOUPON, param, GlobalInfo.sysPara.timeoutpresendcoupon);

			if (ret == null)
				return false;

			String[] retval = parseRetJson(ret, true);

			if (retval == null)
				return false;

			if (!retval[0].equals("0"))
			{
				new MessageBox(retval[1]);
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private void deleteGift(String code)
	{
		// 从后往前找
		for (int i = saleBS.saleGoods.size() - 1; i >= 0; i--)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.get(i);
			if ((sgd.code.equals(code) || sgd.barcode.equals(code)) && sgd.str10.equals("gift") && sgd.jg == 0)
			{
				SaleGoodsDef cloneGoods = (SaleGoodsDef) sgd.clone();
				saleBS.delSaleGoodsObject(i);
				saleBS.getDeleteGoodsDisplay(i, cloneGoods);
				break;
			}
		}
	}

	// 用券
	public boolean checkCoupon(String mainid)
	{
		TextBox txt = new TextBox();
		StringBuffer buf = new StringBuffer();
		String coupon = null;
		try
		{
			if (!txt.open("请扫描手机上的二维券码", "券号", "请扫描手机券码", buf, 0, 0, false, TextBox.AllInput))
				return false;

			coupon = buf.toString().trim();
			if (coupon.equals(""))
				return false;

			/*
			 * if (isExistSameCoupon(coupon)) { new MessageBox("不允许相同券号重复付款!");
			 * return false; }
			 */
			HttpService http = new HttpService();
			ArrayList param = new ArrayList();
			param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
			param.add(http.obtainParameter("sign", ""));
			param.add(http.obtainParameter("data", getCheckOrCancelCouponJson(mainid, coupon)));

			String ret = null;

			try
			{
				ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + CHECKCOUPON, param, GlobalInfo.sysPara.timeoutpresendcoupon);
			}
			catch (Exception ex)
			{
				cancelSale(mainid);
				return false;
			}

			if (ret == null)
				return false;

			String[] retval = parseRetJson(ret, true);

			if (retval == null)
				return false;

			if (!retval[0].equals("0"))
			{
				cancelSale(mainid);
				new MessageBox(retval[1]);
				return false;
			}

			retval = parseRetJson(retval[1], false);

			if (retval == null)
				return false;

			// 2为赠品券
			if (retval[0].equals("2"))
			{
				// 如果查找商品失败，则取消验证的券
				if (!findCouponGift(retval[1]))
				{
					if (!cancelCoupon(mainid, coupon))
					{
						new MessageBox("取消赠品券失败,请联系券发行商!");
						return false;
					}

					// 如果本单没有微店券，则取消整单
					if (getMystoreCouponCount() == 0)
						cancelSale(mainid);

					return false;
				}

				// 如果商品查找成功，则创建付款方式
				if (!createGiftPay(retval[1], mainid, coupon))
				{
					if (!cancelCoupon(mainid, coupon))
					{
						new MessageBox("取消赠品券失败,请联系券发行商!");
						return false;
					}

					// 同时删除赠品
					deleteGift(retval[1]);

					// 如果本单没有微店券，则取消整单
					if (getMystoreCouponCount() == 0)
						cancelSale(mainid);

					return false;

				}
				return true;
			}

			// 非2均为金额券
			if (!createSalePay(mainid, coupon, retval[1]))
			{
				if (!cancelCoupon(mainid, coupon)) // 创建付款失败，则取消券验证
				{
					new MessageBox("取消券侍款失败,请联系券发行商!");
					return false;
				}

				// 如果本单没有微店券，则取消整单
				if (getMystoreCouponCount() == 0)
					cancelSale(mainid);

				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public void cancelSale(String mainid)
	{
		try
		{
			// 此句表示付款列表中已有券付款，此时不能发送整单取消
			if (mainid != null && getMystoreCouponCount() > 0)
				return;

			// 用于控制整单取消
			if (getMystoreCouponCount() > 1)
				return;

			HttpService http = new HttpService();
			ArrayList param = new ArrayList();
			param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
			param.add(http.obtainParameter("sign", ""));
			param.add(http.obtainParameter("data", this.getCancelSaleJson(mainid == null ? getMystoreCouponMainid(saleBS.salePayment) : mainid)));

			String ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + CANCELSALE, param, GlobalInfo.sysPara.timeoutpresendcoupon);

			if (ret == null)
				return;

			String[] retval = parseRetJson(ret, true);

			if (retval == null)
				return;

			if (!retval[0].equals("0"))
			{
				new MessageBox(retval[1]);
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 获取mystore优惠券
	public boolean getMyStoreCoupon(SaleHeadDef salehead, Vector salegoods, Vector salepay)
	{
		String[] retval = null;
		String ret = null;

		HttpService http = new HttpService();
		ArrayList param = new ArrayList();
		int timeout = GlobalInfo.sysPara.timeouthavecoupon; // 默认是存在有券消费的

		try
		{
			String mainid = getMystoreCouponMainid(salepay);

			if (mainid == null)
			{
				if (GlobalInfo.sysPara.issendmystorecouponwithhyk == 'N')
				{
					if (salehead.hykh == null || salehead.hykh.trim().equals(""))
						return true;
				}

				mainid = String.valueOf(ManipulateDateTime.getMillisByDatetime(new ManipulateDateTime().getDateByEmpty(), new ManipulateDateTime().getTimeByEmpty()));

				param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
				param.add(http.obtainParameter("sign", ""));
				param.add(http.obtainParameter("data", getTicketDetailJson(mainid, salehead, salegoods)));

				timeout = GlobalInfo.sysPara.timeoutnocoupon;
				ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + INPUTSALEGOODS, param, timeout);

				if (ret == null)
					return false;

				retval = parseRetJson(ret, true);

				if (retval == null)
					return false;

				if (!retval[0].equals("0"))
				{
					new MessageBox(retval[1]);
					return false;
				}
			}

			param = new ArrayList();
			param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
			param.add(http.obtainParameter("sign", ""));
			param.add(http.obtainParameter("data", getTicketPayJson(mainid, salepay)));
			ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + INPUTPAY, param, timeout);

			if (ret == null)
				return false;

			retval = parseRetJson(ret, true);

			if (retval == null)
				return false;

			if (!retval[0].equals("0"))
			{
				new MessageBox(retval[1]);
				return false;
			}

			param = new ArrayList();
			param.add(http.obtainParameter("operaterid", GlobalInfo.sysPara.mystorecallid));
			param.add(http.obtainParameter("sign", ""));
			param.add(http.obtainParameter("data", getTickeConfirmJson(mainid, ((SaleGoodsDef) salegoods.get(0)).yyyh, salehead.hykh)));
			ret = http.httpPost(GlobalInfo.sysPara.mystoreurl + COMPLETESALE, param, timeout);

			if (ret == null)
			{
				new MessageBox("微店券接口调用失败,未获取到返回值!");
				return false;
			}

			retval = parseRetJson(ret, true);

			if (retval == null)
				return false;

			if (retval[0].equals("1") && retval[1].indexOf("couponno") > 0)
			{
				showRetJson(ret);
				return true;
			}

			if (!retval[0].equals("0"))
			{
				new MessageBox(retval[1]);
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
}
