package custom.localize.Hrsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Hrsl_PaymentCoupon extends PaymentCoupon
{
	Vector copyCouponList = new Vector();

	public Hrsl_PaymentCoupon()
	{
	}

	public Hrsl_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Hrsl_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && (GlobalInfo.sysPara.thmzk != 'Y'))
			{
				new MessageBox("退货时不能使用" + paymode.name, null, false);

				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz()) { return null; }

			// 打开明细输入窗口
			new Hrsl_PaymentCouponForm().open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex);
		}

		return null;
	}

	// 查找返券卡基本信息
	public boolean findFjkInfo(String track1, String track2, String track3, ArrayList fjklist)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		//
		setRequestDataByFind(track1, track2, track3);

		return Hrsl_ServiceCrmModule.getDefault().queryCouponInfo(mzkreq, mzkret);
	}

	public void specialDeal(Hrsl_PaymentCouponEvent event)
	{
		if (saleBS.curCustomer == null)
			return;
		
		if (saleBS.curCustomer.valstr10 != null && saleBS.curCustomer.valstr10.length() == 11)
		{
			event.txtAccount.setFocus();
			event.txtAccount.setText(saleBS.curCustomer.valstr10);
			// event.txtAccount.selectAll();
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					try
					{
						Thread.sleep(50);
						NewKeyListener.sendKey(GlobalVar.Enter);
					}
					catch (Exception ex)
					{

					}
				}
			});

		}
	}

	protected String getDisplayAccountInfo()
	{
		return "请 刷 卡";
	}

	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if (sjje >= 0)
			req.track2 = "0000";
		return DataService.getDefault().sendFjkSale(req, ret);
	}

	public boolean findFjk(String track1, String track2, String track3)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		track2 = track2.replace(",", "");
		track2 = track2.replace("?", "");
		track2 = track2.replace("+", "=");

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置查询条件
		setRequestDataByFind(track1, track2, track3);

		// 查询时memo存放活动规则
		mzkreq.memo = fjkrulecode;

		if (mzkreq.invdjlb != null && SellType.ISBACK(mzkreq.invdjlb) && !saleBS.isRefundStatus())
		{
			if (GlobalInfo.sysPara.oldqpaydet != 'N' && track2.equals("0000"))
			{
				StringBuffer cardno = new StringBuffer();
				TextBox txt = new TextBox();
				if (!txt.open("请刷原小票里的会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, getAccountInputMode())) { return false; }
				String tr = txt.Track2;

				String[] retinfo = NetService.getDefault().findoldqpaydet(salehead.ysyjh, salehead.yfphm, paymode.code, tr, "", "", "", "", "", GlobalInfo.localHttp);
				if (retinfo == null) { return false; }
				yyje = Convert.toDouble(retinfo[1]);
				sjje = Convert.toDouble(retinfo[0]);
			}

			// 传入原收银机号和原小票号
			mzkreq.track3 = saleBS.saleHead.ysyjh + "," + saleBS.saleHead.yfphm;
		}

		// 发送查询交易
		boolean done = Hrsl_ServiceCrmModule.getDefault().queryCouponInfo(mzkreq, mzkret);

		return done;
	}

	// 设置金额
	public boolean setYeShow(Table table)
	{
		// 设置余额列表
		table.removeAll();
		Vector copyList = (Vector) couponList.clone();
		// 将相同的券id合并
		for (int i = 0; i < copyList.size(); i++)
		{
			String[] row = (String[]) copyList.elementAt(i);
			for (int j = i + 1; j < copyList.size();)
			{

				String[] row1 = (String[]) copyList.elementAt(j);
				if (row[0].equals(row1[0]))
				{
					row[2] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]) + Convert.toDouble(row1[2]));
					copyList.remove(j);
					continue;
				}
				else
				{
					j = j + 1;
				}
			}
			copyCouponList.add(row);
			String[] str = new String[5];
			TableItem item = null;
			str[0] = row[1];
			// str[1] = row[5];
			// str[1] = "2013-11-02";
			str[1] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
			str[2] = "0.00";
			item = new TableItem(table, SWT.NONE);
			item.setText(str);
		}

		/*
		 * // 显示券类型
		 * for (int i = 0; i < couponList.size(); i++)
		 * {
		 * String[] str = new String[5];
		 * TableItem item = null;
		 * String[] row = (String[]) couponList.elementAt(i);
		 * // 券id,名称,余额,汇率,日期
		 * str[0] = row[1];
		 * //str[1] = row[5];
		 * //str[1] = "2013-11-02";
		 * str[1] =
		 * ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
		 * str[2] = "0.00";
		 * item = new TableItem(table, SWT.NONE);
		 * item.setText(str);
		 * }
		 */
		return true;
	}

	// 查询可付金额
	public String getValidJe(int index)
	{
		String[] rows = (String[]) copyCouponList.elementAt(index);
		String line = "";
		// 退货时不记算最大能退金额
		if (SellType.ISSALE(salehead.djlb))
		{
			allowpayje = Convert.toDouble(rows[2]);
			double hl = Convert.toDouble(rows[3]);
			// 总是进位到分,确保hl*原币一定是大于应收
			allowpayje = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(allowpayje, hl) + 0.009, 2, 0);
			double showprice = saleBS.getDetailOverFlow(allowpayje);
			line = "可收" + rows[1] + "金额为： " + ManipulatePrecision.doubleToString(allowpayje);
			line += "\n汇率为 " + ManipulatePrecision.doubleToString(hl);
			allowpayje = Math.max(allowpayje, showprice);
		}
		else
		{
			double hl = 1;
			line = "汇率为 " + ManipulatePrecision.doubleToString(hl);

			if (GlobalInfo.sysPara.fjkkhhl != null && GlobalInfo.sysPara.fjkkhhl.length() > 0 && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };
				int i = 0;
				for (i = 0; i < lines.length; i++)
				{
					String l = lines[i];
					if (l.indexOf(",") > 0)
					{
						String cid = l.substring(0, l.indexOf(","));
						if (cid.equals(rows[0]))
						{
							hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));
							if (hl != 1)
								line = "注意:此项请输入要扣回的积分数\n";
							line += "汇率为 " + ManipulatePrecision.doubleToString(hl);
							break;
						}
					}
				}
			}
			else
			{
				hl = Convert.toDouble(rows[3]);

				line = "汇率为 " + rows[3];
			}

			allowpayje = getBackCouponJe(paymode.code, mzkret.cardno, rows[0], String.valueOf(hl), Convert.toInt(rows[4]));

			if (sjje > 0)
			{
				allowpayje = Math.min(ManipulatePrecision.doubleConvert(sjje / Convert.toDouble(rows[3])), allowpayje);
			}

			line += "\n剩余未付金额为:" + allowpayje;
		}

		if (SellType.ISCOUPON(saleBS.saletype) && SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
		{

			for (int i = 0; i < saleBS.refundlist.size(); i++)
			{
				String[] row = (String[]) saleBS.refundlist.elementAt(i);
				if (row[0].equals(rows[0]))
				{
					line += "\n此券需扣回金额为 " + row[1] + " :" + row[2] + "\n";
					break;
				}
			}
		}
		return line;
	}

	public boolean CreateNewjPayment(int index, double money, StringBuffer bufferStr)
	{
		try
		{
			if (money <= 0)
			{
				new MessageBox("付款金额必须大于0");

				return false;
			}

			Hrsl_PaymentCoupon cpf = new Hrsl_PaymentCoupon(paymode, saleBS);

			cpf.paymode = (PayModeDef) this.paymode.clone();
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;
			cpf.couponList = this.couponList;

			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型
			String[] rows = (String[]) copyCouponList.elementAt(index);

			if (Convert.toInt(rows[5]) > 0)
			{
				cpf.CouponType = Convert.toInt(rows[5]);
			}

			cpf.mzkreq.memo = rows[0];
			cpf.mzkret.ye = Convert.toDouble(rows[2]);

			if (rows[0].equals("100"))
			{
				// double a =
				// Double.parseDouble(GlobalInfo.sysPara.CuseJFSaleRule.split(",")[0]);
				// // 积分 2000
				double b = Double.parseDouble(GlobalInfo.sysPara.CuseJFSaleRule.split(",")[1]); // 金额
				                                                                                // 20
				if (money < b || money % b != 0)
				{
					new MessageBox("付款金额必须大于" + b + "且为" + b + "倍数!");
					return false;
				}
			}

			if ((GlobalInfo.sysPara.fjkkhhl != null) && (GlobalInfo.sysPara.fjkkhhl.length() > 0) && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };

				if (lines != null)
				{
					int i = 0;

					for (i = 0; i < lines.length; i++)
					{
						String l = lines[i];

						if (l.indexOf(",") > 0)
						{
							String cid = l.substring(0, l.indexOf(","));

							if (cid.equals(rows[0]))
							{
								cpf.paymode.hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));

								break;
							}
						}
					}

					if (i >= lines.length)
					{
						cpf.paymode.hl = Convert.toDouble(rows[3]);
					}
				}
			}
			else
			{
				cpf.paymode.hl = Convert.toDouble(rows[3]);
			}
			cpf.allowpayje = this.allowpayje;

			// 查询并删除原付款
			// 如果是退货且非扣回时，不删除原付款方式

			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) || GlobalInfo.sysPara.isBackPaymentCover == 'Y')
			{
				if (!deletePayment(index, cpf))
				{
					new MessageBox("删除原付款方式失败！");

					return false;
				}

			}

			/*
			 * if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus())
			 * && !deletePayment(index, cpf))
			 * {
			 * (GlobalInfo.sysPara.isBackPaymentCover == 'N')
			 * new MessageBox("删除原付款方式失败！");
			 * 
			 * return false;
			 * }
			 */

			if (this.allowpayje >= 0 && money > this.allowpayje && paymode.isyy != 'Y')
			{
				new MessageBox("该付款方式最多允许付款 " + ManipulatePrecision.doubleToString(allowpayje) + " 元");

				return false;
			}

			double yy = 0;
			if (yyje > 0 && sjje > 0)
			{
				double min = Math.min(ManipulatePrecision.doubleConvert(sjje / cpf.paymode.hl), cpf.allowpayje);
				if (sjje > 0 && money > min)
				{
					new MessageBox("最大可退金额为: " + min);
					return false;
				}

				if (GlobalInfo.sysPara.oldqpaydet == 'A')
				{
					StringBuffer buf = new StringBuffer();
					buf.append(ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)));
					TextBox txt = new TextBox();
					txt.open("请输入券面值", "券面值", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大券面值为:" + ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert(money + (yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					double yfk = money;
					money = Convert.toDouble(buf.toString());
					if (money > yfk)
						yy = ManipulatePrecision.doubleConvert(money - yfk);
				}
				else
				{
					StringBuffer buf = new StringBuffer();
					// buf.append(ManipulatePrecision.doubleToString((yyje/cpf.paymode.hl)));
					TextBox txt = new TextBox();
					txt.open("请输入此券益余金额", "益余金额", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大益余金额为:" + ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);

					if (Convert.toDouble(buf.toString()) > 0)
						yy = Convert.toDouble(buf.toString());
				}
			}
			// 创建付款对象
			if (cpf.createSalePay(String.valueOf(money + yy)))
			{
				// 设置付款方式名称
				cpf.salepay.payname = rows[1];
				if (yy > 0)
					cpf.salepay.num1 = ManipulatePrecision.doubleConvert(yy * cpf.salepay.hl);

				// 增加已付款
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					cpf.salepay.payname += "扣回";
					saleBS.addSaleRefundObject(cpf.salepay, cpf);

				}
				else
				{
					saleBS.addSalePayObject(cpf.salepay, cpf);
				}

				alreadyAddSalePay = true;

				// 记录当前付款方式
				rows[4] = String.valueOf(cpf.salepay.num5);

				addMessage(cpf, bufferStr);

				// 开始分摊到各个商品
				// paymentApportion(cpf.salepay, cpf, false);

				if (GlobalInfo.sysPara.oldqpaydet != 'N' && sjje > 0 && yyje > 0)
				{
					isCloseShell = true;
				}

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex);
		}

		return false;
	}

	public boolean mzkAccount(boolean isAccount, Vector payinfo)
	{
		BankLogDef bld = null;
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				// 销售
				if (SellType.ISSALE((salehead.djlb)))
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货，加
				// 保存交易数据进行交易
				if (!setRequestDataByAccount())
				{
					if (paynoMsrflag)
					{
						salepay.payno = "";
						continue;
					}
					return false;
				}

				// 先获取交易ID，方便写冲正文件
				if (Hrsl_ServiceCrmModule.getDefault().couponPrepareSale(mzkreq, payinfo) == null) {

				return false; }

				// 将交易ID写到冲正文件
				if (!writeMzkCz())
				{
					if (paynoMsrflag)
					{
						salepay.payno = "";
						continue;
					}
					// 写冲正文失败，则取消当笔交易
					Hrsl_ServiceCrmModule.getDefault().mzkCancelSale(mzkreq);

					return false;
				}

				// 记录面值卡交易日志
				bld = mzkAccountLog(false, null, mzkreq, mzkret);

				// 发送交易请求
				if (!Hrsl_ServiceCrmModule.getDefault().couponConfirmSale(mzkreq))
				{
					if (paynoMsrflag)
					{
						salepay.payno = "";
						continue;
					}
					Hrsl_ServiceCrmModule.getDefault().mzkCancelSale(mzkreq);
					return false;
				}
				/*
				 * // 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
				 * if (mzkreq.type == "01")
				 * salepay.kye -= mzkreq.je;
				 * else
				 * salepay.kye += mzkreq.je;
				 */
				// new MessageBox("salepay.kye="+salepay.kye);
				// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态

			}
			else
			{

				if (SellType.ISSALE((salehead.djlb)))
					mzkreq.type = "01"; // 消费,加
				else
					mzkreq.type = "03"; // 退货，减

				// 保存交易数据进行交易
				if (!setRequestDataByAccount())
					return false;

				if (!writeMzkCz())
					return false;

				bld = mzkAccountLog(false, null, mzkreq, mzkret);

				if (!Hrsl_ServiceCrmModule.getDefault().mzkCancelSale(mzkreq))
					return false;
				/*
				 * if (mzkreq.type == "01")
				 * salepay.kye += mzkreq.je;
				 * else
				 * salepay.kye -= mzkreq.je;
				 */
			}

			saveAccountMzkResultToSalePay();
			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		}
		while (true);
	}

	public boolean mzkAccount1(boolean isAccount, Vector payinfo)
	{
		BankLogDef bld = null;
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				// 销售
				if (SellType.ISSALE((salehead.djlb)))
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货，加
				// 保存交易数据进行交易
				if (!setRequestDataByAccount())
				{
					if (paynoMsrflag)
					{
						salepay.payno = "";
						continue;
					}
					return false;
				}

				// 将交易ID写到冲正文件
				/*
				 * if (!writeMzkCz())
				 * {
				 * if (paynoMsrflag)
				 * {
				 * salepay.payno = "";
				 * continue;
				 * }
				 * 
				 * return false;
				 * }
				 */
				// 记录面值卡交易日志
				bld = mzkAccountLog(false, null, mzkreq, mzkret);

			}
			else
			{

				if (SellType.ISSALE((salehead.djlb)))
					mzkreq.type = "01"; // 消费,加
				else
					mzkreq.type = "03"; // 退货，减

				// 保存交易数据进行交易
				if (!setRequestDataByAccount())
					return false;

				// if (!writeMzkCz())
				// return false;

				bld = mzkAccountLog(false, null, mzkreq, mzkret);

			}

			saveAccountMzkResultToSalePay();
			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		}
		while (true);
	}

	// 分摊在BS里计算
	public boolean isApportionInBS()
	{
		return true;
	}

	// 分摊是否在基类里分摊---防止多重重载的情况下不能调用基类
	public boolean isBaseApportion()
	{
		if (!isApportionInBS())
			return false;

		return false;
	}

	protected boolean setRequestDataByAccount()
	{
		if (super.setRequestDataByAccount())
		{
			// 交易日期
			mzkreq.str2 = ManipulateDateTime.getCurrentDateBySign();
			return true;
		}
		return false;
	}

	public boolean initList()
	{
		try
		{
			couponList.clear();
			couponList.removeAllElements();

			if ((mzkret.memo != null) && (mzkret.memo.trim().length() > 0))
			{
				String[] row = mzkret.memo.split("\\|");

				if (row.length <= 0) { return false; }

				for (int i = 0; i < row.length; i++)
				{
					String[] line = row[i].split(",");

					if (line.length != 5)
					{
						continue;
					}

					// if (sjje >0)
					// {
					// String[] lines = { line[0], line[1],
					// ManipulatePrecision.doubleToString(sjje/Convert.toDouble(line[3])),
					// line[3], "-1", line[4] };
					// couponList.add(lines);
					// }
					// else
					// {
					String[] lines = { line[0], line[1], line[2], line[3], "-1", line[4] };
					couponList.add(lines);
					// }

				}

				return true;
			}
			else
			{
				new MessageBox("当前没有有效券余额\n或\n此券已经消费或者过期");
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}

	public boolean deletePayment(int index, PaymentCoupon pcp1)
	{
		String[] rows = (String[]) copyCouponList.elementAt(index);

		int oldpayindex = -1;

		// 查询并删除原付款
		if (Convert.toInt(rows[4]) == -1)
		{
			oldpayindex = getpaymentIndex(paymode.code, mzkret.cardno, rows[0]);
		}
		else
		{
			oldpayindex = Convert.toInt(rows[4]);
		}

		// 没有相同的付款方式
		if (oldpayindex == -1) { return true; }

		int index1 = -1;
		Vector vi = saleBS.salePayment;

		if (saleBS.isRefundStatus())
		{
			vi = saleBS.refundPayment;
		}

		for (int i = 0; i < vi.size(); i++)
		{
			SalePayDef spd = (SalePayDef) vi.elementAt(i);

			if (spd.num5 == oldpayindex)
			{
				index1 = i;

				break;
			}
		}

		if (index1 != -1)
		{
			if (saleBS.isRefundStatus())
			{
				saleBS.delSaleRefundObject(index1);
			}
			else
			{
				saleBS.delSalePayObject(index1);
			}
		}

		alreadyAddSalePay = true;

		return true;
	}

	public boolean collectAccountPay(Vector payinfo)
	{
		// 如果不是即时记账,则集中记账
		if (GlobalInfo.sysPara.cardrealpay != 'Y' || salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 付款记账
			if (mzkAccount(true, payinfo))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// 已记账,直接返回
			return true;
		}
	}

	public boolean collectAccountPay1(Vector payinfo)
	{
		// 如果不是即时记账,则集中记账
		if (GlobalInfo.sysPara.cardrealpay != 'Y' || salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 付款记账
			if (mzkAccount1(true, payinfo))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// 已记账,直接返回
			return true;
		}
	}

	public boolean writeMzkCz()
	{
		FileOutputStream f = null;

		try
		{
			String name = GetMzkCzFile();

			f = new FileOutputStream(name);
			ObjectOutputStream s = new ObjectOutputStream(f);
			s.writeObject(mzkreq);
			s.flush();
			s.close();
			f.close();
			s = null;
			f = null;

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean collectAccountPay()
	{
		// 如果不是即时记账,则集中记账
		if (GlobalInfo.sysPara.cardrealpay != 'Y' || salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 付款记账
			if (mzkAccount(true))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// 已记账,直接返回
			return true;
		}
	}

	// 发送冲正
	public boolean sendAccountCz()
	{
		FileInputStream f = null;
		boolean ok = false;
		ProgressBox pb = null;

		try
		{
			File file = new File(ConfigClass.LocalDBPath);
			File[] filename = file.listFiles();

			for (int i = 0; i < filename.length; i++)
			{
				if (isCzFile(filename[i].getName()))
				{
					// 读取文件
					String name = filename[i].getAbsolutePath();

					// 检查文件是否为未删除文件
					File a = new File(ConfigClass.LocalDBPath + "/DEL_" + filename[i].getName());
					if (a.exists())
					{
						// 删除冲正文件
						deleteMzkCz(name);
						a.delete();
						continue;
					}

					// 显示冲正进度提示
					if (pb == null)
					{
						pb = new ProgressBox();
						pb.setText("正在发送付款冲正数据,请等待......");
					}

					f = new FileInputStream(name);
					ObjectInputStream s = new ObjectInputStream(f);

					// 读取冲正数据
					MzkRequestDef req = (MzkRequestDef) s.readObject();

					// 关闭文件
					s.close();
					s = null;
					f.close();
					f = null;

					// 发送冲正交易
					if (!Hrsl_ServiceCrmModule.getDefault().mzkCancelSale(req))
						return false;
				}
			}

			ok = true;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
		finally
		{
			try
			{
				if (f != null)
					f.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (pb != null)
				pb.close();
			if (!ok)
			{
				new MessageBox("有冲正数据未发送,不能进行卡交易!");
			}
		}
	}
}
