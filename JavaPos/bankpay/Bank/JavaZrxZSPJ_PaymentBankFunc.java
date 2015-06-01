package bankpay.Bank;

import java.util.Vector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import pos.trans.base.impl.DrmServiceImpl;

public class JavaZrxZSPJ_PaymentBankFunc extends PaymentBankFunc
{
	// 知而行当前业务状态
	public static int PAY_INIT = 1;

	public static int SENDGOODS = 2;

	public static int PAY_DONE = 3;

	// 当前状态
	public static int CurStatus = PAY_INIT;

	// 是否启用知而行
	public static boolean ISUSED = false;

	// 付款编码
	public static String PAYCODE = "";

	// 连网状态标志
	public static boolean ISNETCONN = true;

	protected static JavaZrxZSPJ_PaymentBankFunc selfRef = null;

	protected static DrmServiceImpl dsi = null;

	public JavaZrxZSPJ_PaymentBankFunc()
	{
		super();
		selfRef = this;
	}

	public String[] getFuncItem()
	{
		String[] func = new String[3];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "退货";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "优惠券号";
				grpLabelStr[4] = "执行操作";

				break;
			case PaymentBank.XYKCX: // 消费撤销
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "执行操作";

				break;
			case PaymentBank.XYKTH: // 隔日退货
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = "优惠券号";
				grpLabelStr[4] = "执行操作";

				break;

		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		// 0-4对应FORM中的5个输入框
		// null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: // 消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "回车消费";

				break;
			case PaymentBank.XYKCX: // 消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "回车撤销";

				break;
			case PaymentBank.XYKTH: // 退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "回车退货";

				break;
		}
		return true;
	}

	public static JavaZrxZSPJ_PaymentBankFunc getZrxZSPJ()
	{
		if (selfRef == null)
			selfRef = new JavaZrxZSPJ_PaymentBankFunc();

		return selfRef;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		SaleBS saleBS = null;
		SalePayDef tmpPay = null;

		try
		{
			if (memo == null || memo.elementAt(2) == null)
			{
				errmsg = "知而行优惠券交易失败";
				return false;
			}

			saleBS = (SaleBS) memo.elementAt(2);

			switch (type)
			{
				// 消费
				case PaymentBank.XYKXF:
					tmpPay = (SalePayDef) memo.elementAt(3);
					if (!sendValidateCoupon(saleBS.saleHead, tmpPay, track2))
						return false;

					break;

				// 撤销
				case PaymentBank.XYKCX:
					tmpPay = (SalePayDef) memo.elementAt(3);
					if (!sendCancelCoupon(saleBS.saleHead, tmpPay, tmpPay.payno))
						return false;

					break;

				// 退货
				case PaymentBank.XYKTH:
					tmpPay = (SalePayDef) memo.elementAt(3);

					if (!this.RetCoupon(saleBS.saleHead, tmpPay, track2))
						return false;

					break;

				default:
					errmsg = "知而行接口不支持该交易";
					new MessageBox(errmsg);

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

	public boolean checkBankOperType(int operType, SaleBS saleBS, PaymentBank payObj)
	{
		boolean ok = true;
		if (saleBS != null)
		{
			// 零售状态且类型不为消费时（不考虑扣回）
			if ((SellType.ISSALE(saleBS.saletype) && operType != PaymentBank.XYKXF && operType != PaymentBank.XYKCX) || (SellType.ISBACK(saleBS.saletype) && operType != PaymentBank.XYKTH))
			{
				ok = false;
			}
		}
		else
		{
			// 撤销
			if ((SellType.ISSALE(payObj.salehead.djlb) && payObj != null && operType != PaymentBank.XYKCX) || (SellType.ISBACK(payObj.salehead.djlb) && payObj != null && operType != PaymentBank.XYKTH))
			{
				ok = false;
			}
		}
		if (!ok)
		{
			new MessageBox("不允许该操作,请重新选择");
			return false;
		}

		return true;
	}

	public static void sendFlow(SaleGoodsDef salegoods, boolean flag, String saletype)
	{
		if (!getDrmServiceImpl())
			return;

		// 不管成功与否，只发送一次
		if (SellType.ISSALE(saletype))
		{
			if (flag)
				dsi.sendFlow(salegoods.syjh, String.valueOf(salegoods.fphm), convertBarcode(salegoods.barcode), salegoods.sl, salegoods.lsj, ManipulatePrecision.doubleConvert(salegoods.hjje - salegoods.hjzk, 2, 1), "RMB", "自营", salegoods.code, salegoods.catid);
			else
				dsi.sendFlow(salegoods.syjh, String.valueOf(salegoods.fphm), convertBarcode(salegoods.barcode), salegoods.sl * -1, salegoods.lsj, (ManipulatePrecision.doubleConvert(salegoods.hjje - salegoods.hjzk, 2, 1)) * -1, "RMB", "自营", salegoods.code, salegoods.catid);
		}

		if (SellType.ISBACK(saletype))
			dsi.sendFlow(salegoods.syjh, String.valueOf(salegoods.fphm), convertBarcode(salegoods.barcode), salegoods.sl * -1, salegoods.lsj, (ManipulatePrecision.doubleConvert(salegoods.hjje - salegoods.hjzk, 2, 1)) * -1, "RMB", "自营", salegoods.code, salegoods.catid);

		CurStatus = SENDGOODS;
	}

	public static boolean sendCancel(String syjh, long fphm)
	{
		ISNETCONN = true;

		if (!getDrmServiceImpl())
			return false;
		if (dsi.sendCancel(syjh, String.valueOf(fphm)) == 1)
			return true;
		return false;
	}

	public boolean sendCancelCoupon(SaleHeadDef saleHead, SalePayDef pay, String couponcode)
	{
		if (!ISUSED)
		{
			errmsg = "知而行未启用或不被支持的操作";
			return false;
		}

		try
		{
			if (!getDrmServiceImpl())
				return false;

			if (dsi.sendCancelCoupon(saleHead.syjh, String.valueOf(saleHead.fphm), couponcode) == 0)
			{
				bld.retbz = 'N';
				bld.retcode = "0";
				bld.retmsg = dsi.getMessage(); // 此处容易产生异常
				bld.cardno = couponcode;

				return false;
			}

			if (dsi.sendCancelCoupon.getIType() != 1)
			{
				bld.retbz = 'N';
				bld.retcode = String.valueOf(dsi.sendCancelCoupon.getIType());
				bld.retmsg = dsi.sendCancelCoupon.getCError();
				bld.cardno = couponcode;
				errmsg = bld.retmsg;

				return false;
			}
			else
			{
				bld.retbz = 'Y';
				bld.retcode = String.valueOf(dsi.sendCancelCoupon.getIType());
				bld.retmsg = "取消成功";
				bld.cardno = couponcode;
				bld.je = dsi.sendCancelCoupon.getCCouponMoney();

				// 优惠券项目号,优惠券模板号
				bld.memo = dsi.sendCancelCoupon.getCProjectID() + "," + dsi.sendCancelCoupon.getCTemplateID() + dsi.sendCancelCoupon.getCTicketDescript();
				pay.memo = dsi.sendCancelCoupon.getCProjectID() + "," + dsi.sendCancelCoupon.getCTemplateID() + dsi.sendCancelCoupon.getCTicketDescript();

				errmsg = bld.retmsg;

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			bld.retbz = 'N';
			bld.retcode = "0";
			bld.retmsg = "知而行产生异常";
			bld.cardno = couponcode;

			return false;
		}
	}

	protected boolean sendValidateCoupon(SaleHeadDef saleHead, SalePayDef pay, String couponcode)
	{
		if (!ISUSED)
		{
			errmsg = "知而行未启用或不被支持的操作";
			return false;
		}

		int retInt = -1;

		try
		{
			if (!getDrmServiceImpl())
				return false;

			if (dsi.sendValidateCoupon(saleHead.syjh, String.valueOf(saleHead.fphm), couponcode, saleHead.hykh) != 1)
			{
				bld.retbz = 'N';
				bld.retcode = "0";
				bld.retmsg = dsi.getMessage(); // 此处有可能抛出异常
				bld.cardno = couponcode;

				return false;
			}

			retInt = dsi.sendValidateCoupon.getIType();

			if (retInt == 1)
			{
				bld.retbz = 'Y';
				bld.retcode = String.valueOf(retInt);
				bld.retmsg = dsi.sendValidateCoupon.getCError();
				bld.cardno = couponcode;
				bld.je = dsi.sendValidateCoupon.getCCouponMoney();

				// 优惠券项目号,优惠券模板号,优惠券描述
				bld.memo = dsi.sendValidateCoupon.getCProjectID() + "," + dsi.sendValidateCoupon.getCTemplateID() + dsi.sendValidateCoupon.getCTicketDescript();
				pay.memo = dsi.sendValidateCoupon.getCProjectID() + "," + dsi.sendValidateCoupon.getCTemplateID() + dsi.sendValidateCoupon.getCTicketDescript();

				return true;
			}
			else if (retInt == 8)
			{
				bld.retbz = 'Y';
				bld.retcode = String.valueOf(retInt);
				bld.retmsg = dsi.sendValidateCoupon.getCError();
				bld.cardno = couponcode + "(知而行会员)";
				bld.je = 0;

				// 优惠券项目号,优惠券模板号,优惠券描述
				bld.memo = dsi.sendValidateCoupon.getCProjectID() + "," + dsi.sendValidateCoupon.getCTemplateID() + dsi.sendValidateCoupon.getCTicketDescript();
				pay.memo = dsi.sendValidateCoupon.getCProjectID() + "," + dsi.sendValidateCoupon.getCTemplateID() + dsi.sendValidateCoupon.getCTicketDescript();
				return true;
			}
			else
			{
				bld.retbz = 'N';
				bld.retcode = String.valueOf(retInt);
				bld.cardno = couponcode;
				bld.je = 0;
				bld.retmsg = dsi.sendValidateCoupon.getCError();
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			bld.retbz = 'N';
			bld.retcode = "0";
			bld.retmsg = "知而行发生异常";
			bld.cardno = couponcode;

			return false;
		}
	}

	private boolean RetCoupon(SaleHeadDef saleHead, SalePayDef pay, String couponcode)
	{
		if (!ISUSED)
		{
			errmsg = "知而行未启用或不被支持的操作";
			return false;
		}

		try
		{
			if (!getDrmServiceImpl())
				return false;

			if (dsi.retCoupon(saleHead.syjh, String.valueOf(saleHead.fphm), couponcode) == 0)
			{
				bld.retcode = "0";
				bld.retmsg = dsi.getMessage(); // 此处容易产生异常
				bld.cardno = couponcode;
				bld.retbz = 'N';
				errmsg = bld.retmsg;

				return false;
			}

			if (dsi.retCoupon.getIType() != 1)
			{
				bld.retcode = String.valueOf(dsi.retCoupon.getIType());
				bld.retmsg = dsi.retCoupon.getCError();
				bld.cardno = couponcode;
				bld.retbz = 'N';

				return false;
			}
			else
			{
				bld.retcode = String.valueOf(dsi.retCoupon.getIType());
				bld.retmsg = dsi.retCoupon.getCError();
				bld.cardno = couponcode;
				bld.je = dsi.retCoupon.getCCouponMoney();
				bld.retbz = 'Y';

				pay.memo = dsi.retCoupon.getCProjectID() + "," + dsi.retCoupon.getCTemplateID();
				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			bld.retbz = 'N';
			bld.retcode = "0";
			bld.retmsg = dsi.getMessage();
			bld.cardno = couponcode;

			return false;
		}
	}

	public static boolean sendFinish(SaleHeadDef saleHead)
	{
		int i = 0;
		Vector retCouponInfo = null;
		try
		{
			if (!getDrmServiceImpl())
				return false;

			// saleHead.hykh="2011051311111";
			// 本单已放发知而行优惠券
			if (dsi.sendFinish(saleHead.syjh, String.valueOf(saleHead.fphm), saleHead.hykh) == 0)
			{
				ISNETCONN = false; // 标记为断网
				return false;
			}
			else
			{
				ISNETCONN = true; // 标记为联网

				if (dsi.sendFinish.getIType() > 0)
				{
					switch (dsi.sendFinish.getIType())
					{
						case 1:
						case 2:
						case 3:
							// 记录返回的券信息
							saleHead.str2 = dsi.sendFinish.getCMemo();

							if (retCouponInfo == null)
								retCouponInfo = new Vector();

							String[] retStrInfo = dsi.sendFinish.getCMemo().split("\\|");

							while (retStrInfo.length - i >= 4)
							{
								String[] tmpAry = new String[4];
								tmpAry[0] = retStrInfo[i]; // 项目号
								tmpAry[1] = retStrInfo[i + 1]; // 模板号
								tmpAry[2] = retStrInfo[i + 2]; // 类型
								tmpAry[3] = retStrInfo[i + 3]; // 券号

								retCouponInfo.add(tmpAry);
								i += 4;
							}

							saveCouponBill(saleHead, retCouponInfo);
							break;

						case 4:
							break;
					}
				}
				CurStatus = PAY_DONE;
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	protected static boolean saveCouponBill(SaleHeadDef head, Vector content)
	{
		PrintWriter pw = null;
		String prnFile = "c:\\javapos\\zrxprn_" + String.valueOf(head.fphm) + ".zrx";

		try
		{
			File dir = new File("C:\\JAVAPOS\\");
			String[] list = dir.list(new ZrxFileFilter());
			for (int i = 0; i < list.length; i++)
				PathFile.deletePath("c:\\javapos\\" + list[i]);

			StringBuffer sb = new StringBuffer();

			sb.append("知而行优惠券" + String.valueOf(content.size()) + "张,明细为:\r\n");

			for (int i = 0; i < content.size(); i++)
			{
				String[] tmp = (String[]) content.elementAt(i);
				sb.append(tmp[0] + "   " + tmp[1] + "   " + tmp[2] + "   " + tmp[3] + "    \r\n");
			}
			sb.append("知而行小票已打印");
			sb.toString();

			pw = CommonMethod.writeFile(prnFile);

			if (pw != null)
			{
				pw.print(sb);
				pw.flush();
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
			if (pw != null)
				pw.close();
		}
	}

	// 根据配置文件判断是否启用知而行
	public static void loadConfig()
	{
		try
		{
			String config[] = ConfigClass.Bankfunc.split("\\|");
			for (int j = 0; j < config.length; j++)
			{
				String[] s = config[j].split(",");

				// 配置一个付款对象时
				if (s.length == 1 && (s[0].trim().equals("bankpay.Bank.JavaZrxZSPJ_PaymentBankFunc") || s[0].trim().equals("JavaZrxZSPJ_PaymentBankFunc")))
					ISUSED = true;

				// 配置多个付款对象时
				if (s.length > 1)
				{
					if (s[0].trim().equals("bankpay.Bank.JavaZrxZSPJ_PaymentBankFunc") ||  s[0].trim().equals("JavaZrxZSPJ_PaymentBankFunc"))
					{
						PAYCODE = s[1].trim();
						ISUSED = true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static boolean sendPayInfo(SaleHeadDef head, Vector pay)
	{
		int payType = 18; //默认为其他 

		try
		{
			if (pay == null)
				return false;

			if (!getDrmServiceImpl())
				return false;

			for (int i = 0; i < pay.size(); i++)
			{
				SalePayDef tmpPay = (SalePayDef) pay.elementAt(i);

				// 通过付款名称获取付款编码
				if (PAYCODE.equals(""))
				{
					if (tmpPay.payname.indexOf("知而行") != -1)
						PAYCODE = tmpPay.paycode;
				}

				// 扣回不处理
				if (tmpPay.flag == '3') // || tmpPay.flag == '3')
					continue;

				PayModeDef pm = DataService.getDefault().searchPayMode(tmpPay.paycode);

				switch (pm.type)
				{
					case '1': // 人民币类,
					case '2':// 支票类
						payType = 11;
						break;
					case '3': // 信用卡类
						payType = 12;
						break;
					case '4': // 面值卡类
						payType = 13;
						break;
					case '5': // 礼券类
						payType = 16;
						break;
					case '7': // 其它
					case '8':
					case '9':
						payType = 18;
						break;
				}

				if (tmpPay.paycode.equals(PAYCODE))
					payType = 17;

				if (payType == 11)
				{
					// 找零发送负数
					if (tmpPay.flag == '2')
						dsi.sendPayInfo(head.syjh, String.valueOf(head.fphm), payType, String.valueOf(head.fphm), -1 * tmpPay.je);
					else
						dsi.sendPayInfo(head.syjh, String.valueOf(head.fphm), payType, String.valueOf(head.fphm), tmpPay.je);
				}
				else
				{
					dsi.sendPayInfo(head.syjh, String.valueOf(head.fphm), payType, tmpPay.payno, tmpPay.je);
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args)
	{
		String s = "2121212345633337";
		String s1 = convertBarcode(s);
		System.out.println(s1);
	}

	/*
	 * 条码转换算法： EAN-13码的检查码的算法与UPC-A码相同， 例如假设一EAN-13码各码代号如下： N1 N2 N3 N4 N5 N6 N7
	 * N8 N9 N10 N11 N12 C 检查码之计算步骤如下： C1 = N1+ N3+N5+N7+N9+N11 C2 =
	 * (N2+N4+N6+N8+N10+N12)× 3 CC = (C1+C2) 取个位数 C (检查码) = 10 - CC (若值为10，则取0)
	 */
	private static String convertBarcode(String code)
	{
		String retCode = "";
		char[] chrAry = null;
		int odd = 0, even = 0, tmp = 0;
		try
		{
			if (code.length() == 13) //13位码直接返回
				return code;
			else if (code.length()==12) //处理12位码
			{
				chrAry = code.toCharArray();

				for (int i = 0; i < chrAry.length; i++)
				{
					if (i % 2 == 0)
						odd += Integer.parseInt(String.valueOf(chrAry[i]));
					else
						even += Integer.parseInt(String.valueOf(chrAry[i]));
				}

				even *= 3;
				tmp = 10 - (odd + even) % 10;

				if (tmp == 10)
					retCode = code + "0";
				else
					retCode = code + String.valueOf(tmp);

				return retCode;
			}
			else if (code.length() > 13) //处理大于13位的生鲜码
				return code = code.substring(1, 7);
			else									//小于12位的直接返回不处理
				return code;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return code;
		}
	}

	/*
	 * protected void rePrintCoupon(String syjh, long fphm) { try {
	 * getDrmServiceImpl();
	 * 
	 * if (dsi == null) return; // 返回为1表示成功 if (dsi.rePrintCoupon(syjh,
	 * String.valueOf(fphm)) != 1) { new MessageBox("知而行优惠券重打印失败"); } else {
	 * switch (dsi.rePrintCoupon.getIType()) { case 1: case 3: case 4: new
	 * MessageBox(dsi.rePrintCoupon.getCError()); break; } } return; } catch
	 * (Exception ex) { ex.printStackTrace(); return; } }
	 */

	protected static boolean getDrmServiceImpl()
	{
		try
		{
			if (dsi == null)
				dsi = new DrmServiceImpl();
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			dsi = null;
			return false;
		}
	}

	public void printZrxSaleTicket(SaleHeadDef saleHead)
	{
		ProgressBox pb = null;
		BufferedReader br = null;

		try
		{
			String printName = "c:\\javapos\\zrxprn_" + String.valueOf(saleHead.fphm) + ".zrx";

			if (PathFile.fileExist(printName))
			{
				if (saleHead.printnum > 0)
				{
					MessageBox me = new MessageBox("该小票存在知而行优惠券,确认打印?", null, true);
					if (me.verify() != GlobalVar.Key1)
						return;
				}
			}
			else
			{
				return;
			}

			pb = new ProgressBox();
			pb.setText("正在打印知而行优惠券,请等待...");

			br = CommonMethod.readFileGBK(printName);

			if (br == null)
			{
				new MessageBox("打开" + printName + "打印文件失败!");
				return;
			}

			String line = null;

			XYKPrintDoc_Start();

			while ((line = br.readLine()) != null)
				XYKPrintDoc_Print(line);

			// XYKPrintDoc_End();
			return;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (Exception ex)
				{
					br = null;
				}
			}
			if (pb != null)
				pb.close();
		}
	}

	static class ZrxFileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			return isZrx(name);
		}

		public boolean isZrx(String name)
		{
			if (name.toLowerCase().endsWith(".zrx"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
