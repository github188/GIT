package bankpay.Bank;

import java.io.BufferedReader;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.SalePayDef;

//永泰泰豆消费
public class Ytcz_PaymentBankFunc extends PaymentBankFunc {
	String path = "C:\\Member";

	private SaleBS saleBS = null;

	private SalePayDef salepay = null;

	public String[] getFuncItem() {
		String[] func = new String[4];
		func[0] = "[" + PaymentBank.XYKXF + "]" + "储值消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "储值撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "储值退货";
		func[3] = "[" + PaymentBank.XYKCD + "]" + "储值退货撤销";
		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr) {
		switch (type) {
		case PaymentBank.XYKXF: // 储值消费
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = "请刷卡";
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKCX:// 储值撤销
			grpLabelStr[0] = "原流水号";
			grpLabelStr[1] = "原批次号";
			grpLabelStr[2] = "原交易日";
			grpLabelStr[3] = "请刷卡";
			grpLabelStr[4] = "原小票号";

			break;
		case PaymentBank.XYKTH:// 储值退货
			grpLabelStr[0] = "原流水号";
			grpLabelStr[1] = "原批次号";
			grpLabelStr[2] = "原交易日";
			grpLabelStr[3] = "请刷卡";
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKCD: // 储值退货撤销
			grpLabelStr[0] = "原流水号";
			grpLabelStr[1] = "原批次号";
			grpLabelStr[2] = "原交易日";
			grpLabelStr[3] = "请刷卡";
			grpLabelStr[4] = "原小票号";
			break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr) {
		switch (type) {
		case PaymentBank.XYKXF: // 储值支付
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKCX: // 储值撤销
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKTH: // 储值退货
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKCD: // 储值撤销
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		}

		return true;
	}

	public boolean checkBankOperType(int operType, SaleBS saleBS,
			PaymentBank payObj) {
		boolean ok = true;
		if (saleBS != null) {
			if (
			// 销售交易或者扣回时,只允许选择0(消费)
			// 退货交易且非扣回时,只允许选择1(撤销),2(退货)
			((SellType.ISSALE(saleBS.saletype) || saleBS.isRefundStatus())
					&& operType != PaymentBank.XYKXF && operType != PaymentBank.XYKCX)
					|| ((SellType.ISBACK(saleBS.saletype) && !saleBS
							.isRefundStatus())
							&& operType != PaymentBank.XYKCD && operType != PaymentBank.XYKTH)) {
				ok = false;
			}
		} else {
			if (
			// 删除付款时只允许选择1(撤销),2(退货)
			// 交易红冲时只允许选择1(撤销),2(退货)
			// 后台退货时只允许选择2(退货)
			// 非小票交易不允许选择0(消费)
			(payObj != null && operType != PaymentBank.XYKCX && operType != PaymentBank.XYKCD)
					|| (payObj != null && SellType.ISHC(payObj.salehead.djlb)
							&& operType != PaymentBank.XYKCX && operType != PaymentBank.XYKCD)
					|| (payObj != null && SellType.ISBACK(payObj.salehead.djlb) && operType != PaymentBank.XYKTH && operType != PaymentBank.XYKCD)
					|| (operType == PaymentBank.XYKXF && !salebyself)) {
				ok = false;
			}
		}
		if (!ok) {
			new MessageBox("不允许进行该银联操作,请重新选择");
			return false;
		}
		return true;
	}

	public boolean XYKExecute(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		String request[] = null;
		String bonusAlterReturn = "";
		try {
			if (!(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX
					|| type == PaymentBank.XYKTH || type == PaymentBank.XYKCD)) {
				new MessageBox("大会员接口不支持此交易类型！！！");
				return false;
			}
			if (track2 != null) {
				if (!findMemberDHYCard(track2, memo)) {
					new MessageBox("查找大会员卡失败，无法进行消费！");
					return false;
				}
			}
			PosLog.getLog(this.getClass().getSimpleName()).info(
					"XYKExecute start(syjh=" + bld.syjh + ",syyh=" + bld.syyh
							+ ")");

			// 调用接口模块
			request = XYKWriteRequest(type, money, track1, track2, track3,
					oldseqno, oldauthno, olddate, memo);
			PosLog.getLog(this.getClass().getSimpleName()).info(
					"request=[" + request[1] + "]");

			// 调用接口模块
			if (RdPlugins.getDefault().getPlugins1().exec(
					Convert.toInt(request[0]), request[1])) {
				bonusAlterReturn = (String) RdPlugins.getDefault()
						.getPlugins1().getObject();
			}
			// 读取应答数据
			if (!XYKReadResult(bonusAlterReturn)) {
				return false;
			}

			// 检查交易是否成功
			XYKCheckRetCode();

			// 打印签购单
			if (XYKNeedPrintDoc(type)) {
				Printer.getDefault().close();//释放打印机
				Printer.getDefault().open();//重新连接打印机
				Printer.getDefault().setEnable(true);
				XYKPrintDoc(type);
			}
			return true;
		} catch (Exception ex) {
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			XYKSetError("XX", "大会员异常XX:" + ex.getMessage());
			new MessageBox("调用大会员处理模块异常!\n\n" + ex.getMessage(), null, false);

			return false;
		}
	}

	public boolean findMemberDHYCard(String track2, Vector memo) {
		/**
		 * 1.调用dll获取大会员详细信息 2.将大会员详细信息上传到百货后台 3.生成CustomerDef对象返回
		 */
		if (track2.length() > 16) {
			String[] s = track2.split("=");
			track2 = s[0];
		}
		//if (memo.size() >=2 ) saleBS = (SaleBS)memo.elementAt(2);
		String fphm = "0";
		String syjh = GlobalInfo.syjDef.syjh;
		String mktcode = GlobalInfo.sysPara.mktcode;
		String syyh = GlobalInfo.posLogin.gh;
		String memberInfoReturn = "";
		PosLog.getLog(getClass()).info(
				"收银机号:" + syjh + " 收银员号：" + syyh + " 轨道号:" + track2.trim());

		if (RdPlugins.getDefault().getPlugins1().exec(2,
				mktcode + "," + syyh + "," + track2.trim() + "," + fphm)) {
			memberInfoReturn = (String) RdPlugins.getDefault().getPlugins1()
					.getObject();
			PosLog.getLog(getClass()).info(
					"小票号:" + fphm + " " + memberInfoReturn);
			if (memberInfoReturn.substring(0, 2).equals("00"))
				return true;
		}
		return false;

	}

	public boolean XYKReadResult(String result) {

		try {
			PosLog.getLog(this.getClass().getSimpleName()).info(
					"result=[" + result + "]");

			if (result == null || result.length() <= 0) {
				return false;
			}
			if (result.substring(0, 2).equals("00")) {
				bld.retcode = result.substring(0, 2);//应答代码
				bld.trace = Convert.toLong(result.substring(2, 14));//主机流水号
				bld.authno = result.substring(14, 20);//批次号
				bld.retmsg = "交易成功";
			}
			int type = Integer.parseInt(bld.type.trim());
			{
				if (!result.substring(0, 2).equals("00")) {// 交易失败

					switch (type) {
					case PaymentBank.XYKXF: // 泰豆消费
						if(result.length()==222)
						bld.retmsg = result.substring(123, 222).trim(); // 错误说明
						bld.memo1 = "储值消费失败";
						break;
					case PaymentBank.XYKCX: // 泰豆撤销
						if(result.length()==255)
						bld.retmsg = result.substring(68, 255).trim(); // 错误说明
						bld.memo1 = "储值撤销失败";
						break;
					case PaymentBank.XYKTH: // 泰豆退货
						if(result.length()==213)
						bld.retmsg = result.substring(125, 213).trim(); // 错误说明
						bld.memo1 = "储值退货失败";
						break;
					case PaymentBank.XYKCD: // 退货撤销
						if(result.length()==255)
						bld.retmsg = result.substring(68, 255).trim(); // 错误说明
						bld.memo1 = "储值退货撤销失败";
						break;
					}
					return false;
				} else {
					switch (type) {
					case PaymentBank.XYKXF: // 储值消费
						bld.cardno = result.substring(20, 39).trim();
						bld.kye = Convert.toDouble(result.substring(51, 63));
						//bld.je=Convert.toDouble(result.substring(87, 99));
						bld.memo1 = "储值消费成功";
						break;
					case PaymentBank.XYKCX: // 储值撤销
						bld.kye = Convert.toDouble(result.substring(32, 44));
						//bld.je=Convert.toDouble(result.substring(44, 56));
						bld.memo1 = "储值撤销成功";
						break;
					case PaymentBank.XYKTH: // 储值退货
						bld.cardno = result.substring(20, 39).trim();
						bld.kye = Convert.toDouble(result.substring(51, 63));
						//bld.je=Convert.toDouble(result.substring(87, 99));
						bld.memo1 = "储值退货成功";
						break;
					case PaymentBank.XYKCD: // 储值退货撤销
						bld.kye = Convert.toDouble(result.substring(32, 44));
						//bld.je=Convert.toDouble(result.substring(44, 56));
						bld.memo1 = "储值退货撤销成功";
						break;
					}
				}
				if (type == PaymentBank.XYKXF || type == PaymentBank.XYKTH) {
					double je = Convert.toDouble(result.substring(87, 99));
					if (ManipulatePrecision.doubleConvert(bld.je) != je) {
						String strPay = "消费";
						if (type == PaymentBank.XYKCX) {
							strPay = "撤消";
						} else if (type == PaymentBank.XYKTH) {
							strPay = "退货";
						}
						new MessageBox("收银系统发起的" + strPay + "金额（"
								+ ManipulatePrecision.doubleConvert(bld.je)
								+ "）与银行接口返回的金额（" + je + "）不匹配");
						bld.je = je; // 交易金额
					}

				}
			}
			if (TransComplete(String.valueOf(bld.trace)))
			return true;
			else{
				new MessageBox("确认交易失败，请后台确认！");
				return true;
			}
				
		} catch (Exception ex) {
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			XYKSetError("XX", "解析应答XX:" + ex.getMessage());
			new MessageBox("解析大会员应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();

			return false;
		} finally {
		}
	}

	public boolean WriteRequestLog(int type, double money, String oldseqno,
			String oldauthno, String olddate) {
		try {
			bld = new BankLogDef();

			Object obj = GlobalInfo.dayDB
					.selectOneData("select max(rowcode) from BANKLOG");

			if (obj == null) {
				bld.rowcode = 1;
			} else {
				bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
			}

			bld.rqsj = ManipulateDateTime.getCurrentDateTime();
			bld.syjh = GlobalInfo.syjDef.syjh;
			bld.fphm = GlobalInfo.syjStatus.fphm;
			bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh
					: "");
			bld.type = String.valueOf(type);
			if (type == PaymentBank.XYKCX || type == PaymentBank.XYKCD)
				bld.je = salepay.ybje;
			else
				bld.je = Convert.toDouble(money);
			if (type != PaymentBank.XYKTH)
				bld.oldrq = olddate;
			bld.typename = getChangeType(getFuncItem(), bld.type);
			bld.classname = (bankcfgname != null ? bankcfgname : this
					.getClass().getName().substring(
							this.getClass().getName().lastIndexOf(".") + 1));

			if ((oldseqno != null) && !oldseqno.trim().equals("")) {
				bld.oldtrace = Long.parseLong(oldseqno);
			} else {
				bld.oldtrace = 0;
			}

			bld.cardno = "";//会员卡号
			bld.trace = 0;
			bld.authno = "";
			bld.bankinfo = "";
			bld.crc = "";
			bld.retcode = "";
			bld.retmsg = "";
			bld.retbz = 'N';
			bld.net_bz = 'N';
			bld.allotje = 0;
			bld.memo = getMemo(type, money, oldseqno, oldauthno, olddate);
			bld.memo1 = "";
			bld.memo2 = "";
			bld.tempstr = "";
			bld.tempstr1 = "";

			//
			if (!AccessDayDB.getDefault().writeBankLog(bld)) {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
			bld = null;

			return false;
		}

		return true;
	}

	public boolean XYKNeedPrintDoc(int type) {
		if (!checkBankSucceed()) {
			return false;
		}
		if (type == PaymentBank.XYKXF || type == PaymentBank.XYKCX
				|| type == PaymentBank.XYKTH || type == PaymentBank.XYKCD) {
			return true;
		} else
			return false;
	}

	public void XYKPrintDoc(int type) {
		ProgressBox pb = null;
		if (GlobalInfo.sysPara.bankprint < 1)
			return;
		String printName = "C:\\Member\\MemberReceipt.TXT";
		try {

			if (!PathFile.fileExist(printName)) {
				new MessageBox("永泰大会员凭条不存在，无法打印!", null, false);
				return;
			}
			pb = new ProgressBox();
			pb.setText("正在打印永泰大会员凭条,请等待...");

			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++) {
				XYKPrintDoc_Start();

				BufferedReader br = null;

				try {
					br = CommonMethod.readFileGBK(printName);

					if (br == null) {
						new MessageBox("打开" + printName + "打印文件失败!");

						return;
					}

					String line = null;

					while ((line = br.readLine()) != null) {
						if (line == null || line.length() <= 0)
							continue;
						if (line.indexOf("CUTPAPER") != -1) {
							XYKPrintDoc_End();
							//new MessageBox("请撕下永泰大会员凭条" );
							continue;
						}

						XYKPrintDoc_Print(line);
					}
				} catch (Exception ex) {
					new MessageBox(ex.getMessage());
				} finally {
					if (br != null) {
						br.close();
					}
				}

				XYKPrintDoc_End();
			}

		} catch (Exception ex) {
			new MessageBox("打印永泰大会员凭条发生异常\n\n" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			if (pb != null) {
				pb.close();
				pb = null;
			}
		}

	}

	public String[] XYKWriteRequest(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		String line[] = { "", "" };
		String type1 = ""; // 交易类型
		try {
			if (oldseqno.length() > 16) {
				String[] s = oldseqno.split("=");
				oldseqno = s[0];
			}
			String yfphm = "0";
			String crashNum = "0";
			if (saleBS != null) {
				yfphm = String.valueOf(saleBS.saleHead.yfphm);
				crashNum = String.valueOf(saleBS.saleHead.ysje);
			}
			String syjh = GlobalInfo.syjDef.syjh;
			String mktcode = GlobalInfo.sysPara.mktcode;
			String fphm = String.valueOf(GlobalInfo.syjStatus.fphm);
			String syyh = GlobalInfo.posLogin.gh;
			String bonusAlterRequest = "";
			PosLog.getLog(getClass()).info(
					"收银机号:" + syjh + " 收银员号：" + syyh + " 轨道号:" + track2.trim());
			switch (type) {
			case PaymentBank.XYKXF: // 泰豆消费
				/**销售小票&&交易类型01  = 减积分
				 销售小票大会员积分消费付款   Of_MemberSales() 函数
				 * char       TenantID[15]          //* 商户代码
				 char       CasherID[20]          //* 收银员号
				 char       Track[40]             //* 磁道信息
				 char       ReceiptNumber[12]     // 收银流水号
				 char       IsPayment[1]          //* 是否支付 1 支付 2 积分
				 char       TotalAmount[12]       //* 小票总金额
				 char       Amount[12]            //* 有效消费金额
				 char       Bonus[12]             //* 业态积分
				 char       PayType[2]           //* 支付类型 0 全部 1 积分 2券3 积分+券 4 储值 5 积分+储值 6 券+储值
				 char       ResultInfo[255]       //* 回应信息
				 **/
				type1 = "3";
				bonusAlterRequest = mktcode + "," + syyh + "," + track2 + ","
						+ fphm + ",1," + crashNum + "," + money + ",0,4";
				break;
			case PaymentBank.XYKCX: // 泰豆撤销
				/**（交易类型03 && 销售小票）||交易类型02  =  加积分
				 //销售小票大会员积分消费撤销 或者 消费冲正Of_UnMemberSales() 函数
				 * char       TenantID[15]           //* 商户代码
				 char       CasherID[20]           //* 收银员号
				 char       Track[40]              //* 磁道信息
				 char       ReceiptNumber[12]      // 本次收银流水号
				 char       UnTraceNumber[12]      //* 原主机流水号
				 char       UnBatchNumber[6]       //* 原批次号
				 char       UnDate[4]              //* 原交易日期
				 char       UnReceiptNumber[12]    // 原收银流水号
				 char       ResultInfo[255]        //* 回应信息
				 */
				type1 = "4";
				bonusAlterRequest = mktcode + "," + syyh + "," + track2 + ","
						+ fphm + "," + oldseqno + "," + oldauthno + ","
						+ olddate + "," + Convert.toInt(money);
				break;
			case PaymentBank.XYKTH: // 隔日退货
				/**交易类型03 && 退货小票 = 加积分
				 //退货交易大会员积分退款  Of_MemberReturn()
				 * char        TenantID[15]           //* 商户代码
				 char        CasherID[20]           //* 收银员号
				 char        Track[40]              //* 磁道信息
				 char        ReceiptNumber[12]      // 本次收银流水号
				 char        UnTraceNumber[12]      //* 原主机流水号
				 char        UnBatchNumber[6]       //* 原批次号
				 char        UnDate[4]              //* 原交易日期
				 char        UnReceiptNumber[12]    // 原收银流水号
				 char        IsPayment[1]          //* 退货方式 ：1 支付退货 2 积分退货
				 char        TotalAmount[12]        //* 退货小票总金额
				 char        ReturnAmount[12]       //* 退货金额
				 char        ReturnBonus[12]        //* 退业态积分
				 char        ResultInfo[255]        //* 回应信息
				 */
				type1 = "5";
				bonusAlterRequest = mktcode + "," + syyh + "," + track2 + ","
						+ fphm + "," + oldseqno + "," + oldauthno + ","
						+ olddate + "," + yfphm + ",1," + crashNum + ","
						+ money + ",0";
				break;
			case PaymentBank.XYKCD: // 退货撤销
				/**交易类型04 || (退货小票&&交易类型01) = 减积分
				 //退货冲正  或者  退货小票大会员积分退款撤销  Of_UnMemberReturn ()
				 * char       TenantID[15]           //* 商户代码
				 char       CasherID[20]           //* 收银员号
				 char       Track[40]              //* 磁道信息
				 char       ReceiptNumber[12]      // 本次收银流水号
				 char       UnTraceNumber[12]      //* 原主机流水号
				 char       UnBatchNumber[6]       //* 原批次号
				 char       UnDate[4]              //* 原交易日期
				 char       UnReceiptNumber[12]    // 原收银流水号
				 char       ResultInfo[255]        //* 回应信息
				 **/
				type1 = "6";
				bonusAlterRequest = mktcode + "," + syyh + "," + track2 + ","
						+ fphm + "," + oldseqno + "," + oldauthno + ","
						+ olddate + "," + Convert.toInt(money);
				break;
			}

			try {
				line[0] = type1;
				line[1] = bonusAlterRequest;
			} finally {

			}
			return line;
		} catch (Exception ex) {
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("生成请求数据异常!\n\n" + ex.getMessage(), null, false);
			return null;
		}
	}

	public boolean checkDate(Text date) {
		String d = date.getText();
		if (d.length() != 4) {
			new MessageBox("日期格式错误\n日期格式《MMDD》");
			return false;
		}

		return true;
	}

	public boolean callBankFunc(int type, double money, String track1,
			String track2, String track3, String oldseqno, String oldauthno,
			String olddate, Vector memo) {
		boolean doClosePrint = false;

		try {
			// 银联接口需要自己进行打印则释放打印机
			if (GlobalInfo.sysPara.issetprinter == 'Y'
					&& GlobalInfo.syjDef.isprint == 'Y'
					&& Printer.getDefault() != null
					&& Printer.getDefault().getStatus()) {
				Printer.getDefault().close();
				doClosePrint = true;
			}

			// 规范数据
			if (track1 == null)
				track1 = "";
			if (track2 == null)
				track2 = "";
			if (track3 == null)
				track3 = "";
			if (oldseqno == null)
				oldseqno = "";
			if (oldauthno == null)
				oldauthno = "";
			if (olddate == null)
				olddate = "";
			if (memo.size() >= 2) {
				saleBS = (SaleBS) memo.elementAt(2);
				salepay = (SalePayDef) memo.elementAt(3);
			}
			// 写入请求数据日志
			if (!this
					.WriteRequestLog(type, money, oldseqno, oldauthno, olddate)) {
				return false;
			}

			// 调用金卡模块处理
			this.XYKExecute(type, money, track1, track2, track3, oldseqno,
					oldauthno, olddate, memo);

			// 写入应答数据日志
			this.WriteResultLog();

			// 写入单独进行的银联消费数据
			this.WriteSelfSaleData(memo);

			// 将交易日志发送网上
			this.BankLogSend();

			// 判断交易是否成功
			return checkBankSucceed();
		} catch (Exception ex) {
			new MessageBox("执行第三方支付接口异常!\n\n" + ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {
			// 银联接口执行完重新连接打印机
			if (GlobalInfo.sysPara.issetprinter == 'Y'
					&& GlobalInfo.syjDef.isprint == 'Y'
					&& Printer.getDefault() != null
					&& !Printer.getDefault().getStatus() && doClosePrint) {
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}
		}
	}
	
	public boolean TransComplete(String TraceNumber){
		/**Of_TransComplete()
		 * char        TenantID[15]           //* 商户代码
           char        CasherID[20]           //* 收银员号
           char        TraceNumber[12]        //* 主机流水号
		 */
		String mktcode =GlobalInfo.sysPara.mktcode;
		String syyh = GlobalInfo.posLogin.gh;
		String bonusAlterRequest=mktcode+","+syyh+","+TraceNumber;
		if (RdPlugins.getDefault().getPlugins1().exec(7, bonusAlterRequest)) {
			String bonusAlterReturn = (String) RdPlugins.getDefault()
					.getPlugins1().getObject();
			if (bonusAlterReturn.substring(0, 2).equals("00")) {
				return true;
			}
		}
		return false;
		
	}

}
