package custom.localize.Jnyz;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;

public class Jnyz_PaymentCoupon extends PaymentCoupon {
		
	public boolean findFjk(String track1, String track2, String track3)
	{
		if ((track1.trim().length() <= 0) && (track2.trim().length() <= 0) && (track3.trim().length() <= 0)) { return false; }

		// 解析磁道
		String[] s = parseFjkTrack(track1, track2, track3);

		if (s == null) { return false; }

		track1 = s[0];
		track2 = s[1];
		track3 = s[2];
		
		String code="";
		if(paymode != null){
			code =paymode.code;
		}
		if(GlobalInfo.sysPara.msrspeed == 100){
			if(!code.equals("0502")){
				//	根据不同键盘驱动解析磁道
//				KeyBoard key = new KeyBoard(ConfigClass.KeyBoard1);
//				if(key.isValid()){
//					String keyName = key.keyboard.getDiscription();
//					if(keyName.indexOf("Wincor键盘") != -1){
//						track2 = track2.substring(7);
//					}
//					//截取磁道号前面的符号
//					if(track2.indexOf(";")==0){
//						track2 = track2.substring(1);
//					}
//				}
				
				if(ConfigClass.KeyBoard1.trim().equals("device.KeyBoard.Wincor_KeyBoard")){
						track2 = track2.substring(7);
					}
					//截取磁道号前面的符号
					if(track2.indexOf(";")==0){
						track2 = track2.substring(1);
					}
					
				}
			}
		
		if(saleBS != null && code.equals("0502"))//0502纸劵付款
		{
			//退货非扣回允许输入 0000 ，退货扣回不允许输入 0000 
			//非扣回
			if(((SellType.BATCH_BACK.equals(saleBS.saletype) || SellType.RETAIL_BACK.equals(saleBS.saletype)) && !saleBS.isRefundStatus()) && !track2.equals("0000"))
			{
				new MessageBox("退货交易请输入 0000 "); 
				return false;
			}
			//扣回
			else if(((SellType.BATCH_BACK.equals(saleBS.saletype) || SellType.RETAIL_BACK.equals(saleBS.saletype)) && saleBS.isRefundStatus()) && track2.equals("0000"))
			{
				return false;
			}
		}
			

		
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
//				if (!txt.open("请刷原小票里的会员卡或顾客打折卡", "会员号", "请将会员卡或顾客打折卡从刷卡槽刷入", cardno, 0, 0, false, getAccountInputMode())) { return false; }
				if (!txt.open("请输入原打印券号", "打印券号", "", cardno, 0, 0, false, getAccountInputMode())) { return false; }
				String tr = txt.Track2;

				String[] retinfo = NetService.getDefault().findoldqpaydet(salehead.ysyjh, salehead.yfphm, paymode.code, tr, "", "", "", "", "", GlobalInfo.localHttp);
				if (retinfo == null) { return false; }
				yyje = Convert.toDouble(retinfo[1]);
				sjje = Convert.toDouble(retinfo[0]);
			}

			// 传入原收银机号和原小票号
//			mzkreq.track3 = saleBS.saleHead.ysyjh + "," + saleBS.saleHead.yfphm;
		}
		
//		new MessageBox(mzkreq.track2);

		// 发送查询交易
		boolean done = sendMzkSale(mzkreq, mzkret);

		return done;
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

			PaymentCoupon cpf = new PaymentCoupon(paymode, saleBS);

			cpf.paymode = (PayModeDef) this.paymode.clone();
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;
			cpf.couponList = this.couponList;

			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型
			String[] rows = (String[]) couponList.elementAt(index);

			if (Convert.toInt(rows[5]) > 0)
			{
				cpf.CouponType = Convert.toInt(rows[5]);
			}

			cpf.mzkreq.memo = rows[0];
			cpf.mzkret.ye = Convert.toDouble(rows[2]);

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
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) && !deletePayment(index, cpf))
			{
				(GlobalInfo.sysPara.isBackPaymentCover == 'N')
				new MessageBox("删除原付款方式失败！");

				return false;
			}
			*/

			if (this.allowpayje >= 0 && money > this.allowpayje)
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
					buf.append(ManipulatePrecision.doubleToString((yyje/cpf.paymode.hl)));
					String tjje = ManipulatePrecision.doubleToString(money+(yyje/cpf.paymode.hl));
					new MessageBox("原券溢余:"+buf.toString()+"元\n退券面额:"+tjje+"元");
//					TextBox txt = new TextBox();
//					txt.open("请输入此券益余金额", "益余金额", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大益余金额为:" + ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);

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
				paymentApportion(cpf.salepay, cpf, false);

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
		}

		return false;
	}
	
//	 设置金额
	public boolean setYeShow(Table table)
	{
		// 设置余额列表
		table.removeAll();

		// 显示券类型
		for (int i = 0; i < couponList.size(); i++)
		{
			String[] str = new String[3];
			TableItem item = null;
			String[] row = (String[]) couponList.elementAt(i);

			str[0] = row[1];
			str[1] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
			if(paymode.code.equals("0502")){
				str[2] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
			}else{
				str[2] = "0.00";
			}
			item = new TableItem(table, SWT.NONE);
			item.setText(str);
		}
		return true;
	}
	
	protected String getDisplayAccountInfo()
	{
		if(paymode.code.equals("0502"))
		return "请 输 卡";
		else
		return "请 刷 卡";
	}
}
