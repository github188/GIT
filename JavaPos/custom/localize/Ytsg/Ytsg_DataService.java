package custom.localize.Ytsg;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_DataService;

public class Ytsg_DataService extends Cmls_DataService {
    protected BankLogDef bld = null;
	public int sendSaleWebService(SaleHeadDef saleHead, Vector saleGoods,
			Vector salePayment) {
		if (saleHead.hykh != null && !saleHead.hykh.trim().equals(""))
			sendDHYJF(saleHead, saleGoods, salePayment);
		return 0;
	}
	
	public boolean BankLogSend()
	{
		ProgressBox pb = null;

		try
		{
			pb = new ProgressBox();

			pb.setText("正在发送第三方支付交易日志,请等待...");

			//
			if (NetService.getDefault().sendBankLog(bld))
			{
				//
				bld.net_bz = 'Y';

				//
				if (!AccessDayDB.getDefault().updateBankLog(bld)) { return false; }
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
			{
				pb.close();
			}
		}
	}

	private boolean sendDHYJF(SaleHeadDef saleHead, Vector saleGoods,
			Vector salePayment) {
		if (GlobalInfo.sysPara.mktcode.indexOf(",") >= 0)
			GlobalInfo.sysPara.mktcode = GlobalInfo.sysPara.mktcode.split(",")[1];
		/**
		 * char TenantID[15] //* 商户代码 char CasherID[20] //* 收银员号 char Track[40]
		 * //* 磁道信息 char ReceiptNumber[12] // 收银流水号 char IsPayment[1] //* 是否支付 1
		 * 支付 2 积分 char TotalAmount[12] //* 小票总金额 char Amount[12] //* 有效消费金额
		 * char Bonus[12] //* 业态积分 char PayType[2] //* 支付类型 0 全部 1 积分 2券3 积分+券 4
		 * 储值 5 积分+储值 6 券+储值 char ResultInfo[255] //* 回应信息
		 */
		// 门店号+收银机号+小票号+行号（+1，消费为0）
		String mktcode = GlobalInfo.sysPara.mktcode;
		String syjh = GlobalInfo.syjDef.syjh;
		String syyh = GlobalInfo.posLogin.gh;
		String track = saleHead.str6;
		String fphm = String.valueOf(saleHead.fphm);
		String cashAmount = "";
		String bonusAlterReturn = "";
		cashAmount = String.valueOf(calcPayFPMoney(saleHead, saleGoods,
				salePayment));
		PosLog.getLog(getClass()).info(
				"收银机号:" + syjh + " 收银员号：" + syyh + " 轨道号:" + track + " 小票号:"
						+ fphm + " TotalAmount:" + saleHead.ysje + " Amount:"
						+ cashAmount + " Bonus:" + saleHead.bcjf);

		if (SellType.ISSALE(saleHead.djlb)) {// 销售 of_MemberSale()
			/**
			 * char       TenantID[15]          //* 商户代码
               char       CasherID[20]          //* 收银员号
               char       Track[128]             //* 磁道信息
               char       ReceiptNumber[12]     // 收银流水号
               char       IsPayment[1]          //* 是否支付 1 支付 2 积分
               char       TotalAmount[12]       //* 小票总金额
               char       Amount[12]            //* 有效消费金额
               char       Bonus[12]             //* 业态积分
               char       PayType[2]           //* 支付类型 0 全部 1 积分 2券3 积分+券 4 储值 5 积分+储值 6 券+储值
			 */
			if (Convert.toDouble(cashAmount)==0) return true;
			if (RdPlugins.getDefault().getPlugins1().exec(
					3,
					mktcode + "," + syyh + "," + track + "," + fphm + ",2,"
							+ saleHead.ysje + "," + cashAmount + "," + "0,0")) {
				bonusAlterReturn = (String) RdPlugins.getDefault()
						.getPlugins1().getObject();
				PosLog.getLog(getClass()).info(
						"小票号:" + saleHead.fphm + " " + bonusAlterReturn);
				try
				{
					bld = new BankLogDef();

					Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");

					if (obj == null)
					{
						bld.rowcode = 1;
					}
					else
					{
						bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
					}

					bld.rqsj = ManipulateDateTime.getCurrentDateTime();
					bld.syjh = GlobalInfo.syjDef.syjh;
					bld.fphm = GlobalInfo.syjStatus.fphm;
					bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh : "");
					bld.type = "7";
					bld.je = Convert.toDouble(cashAmount);
					bld.oldrq = saleHead.rqsj;
					bld.typename ="积分";
				    bld.oldtrace = 0;
					bld.cardno =saleHead.hykh;//会员卡号
					if (bonusAlterReturn!=null&&bonusAlterReturn.substring(2, 14)!="")
						  bld.trace = Convert.toLong(bonusAlterReturn.substring(2, 14));
					else bld.trace=0;
					bld.authno = "";
					bld.bankinfo = "";
					bld.crc = "";
					if (bonusAlterReturn!=null&&bonusAlterReturn!="")
						bld.retcode = bonusAlterReturn.substring(0, 2);
					else bld.retcode="";
					bld.retmsg = "";
					bld.retbz = 'N';
					bld.net_bz = 'N';
					bld.allotje = 0;
					bld.memo = "积分";
					bld.memo1 = "";
					bld.memo2 = "";
					bld.tempstr = "";
					bld.tempstr1 = "";
					if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
					BankLogSend();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();

					new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
					bld = null;

					return false;
				}
				if (bonusAlterReturn != null
						&& bonusAlterReturn.trim().length() > 0
						&& bonusAlterReturn.substring(0, 2).equals("00")) {
					if (TransComplete(bonusAlterReturn.substring(2, 14))) {// 会员交易达成
						new MessageBox("大会员积分余额:    "
								+ bonusAlterReturn.substring(39, 51).trim()
								+ "\n大会员可用积分:    "
								+ bonusAlterReturn.substring(51, 63).trim());
						saleHead.num1 = Convert.toDouble(bonusAlterReturn.substring(75, 87));//本次积分
						saleHead.num2 = Convert.toDouble(bonusAlterReturn.substring(51, 63));//累计积分
		                saleHead.str3 = bonusAlterReturn.substring(2, 14);//主机流水号
		                saleHead.str4 = bonusAlterReturn.substring(14,20);//批次号
					}
				} else {
					PosLog.getLog(getClass()).info(
							"小票号:" + saleHead.fphm + " " + bonusAlterReturn);
					new MessageBox("发送大会员积分失败!");
					return false;
				}

			}
		} else if (SellType.ISBACK(saleHead.djlb)) {// 退货
			/**
			 * 交易类型03 && 退货小票 = 加积分 //退货交易大会员积分退款 Of_MemberReturn() char
			 * TenantID[15] //* 商户代码 char CasherID[20] //* 收银员号 char Track[40]
			 * //* 磁道信息 char ReceiptNumber[12] // 本次收银流水号 char UnTraceNumber[12]
			 * //* 原主机流水号 char UnBatchNumber[6] //* 原批次号 char UnDate[4] //*
			 * 原交易日期 char UnReceiptNumber[12] // 原收银流水号 char IsPayment[1] //*
			 * 退货方式 ：1 支付退货 2 积分退货 char TotalAmount[12] //* 退货小票总金额 char
			 * ReturnAmount[12] //* 退货金额 char ReturnBonus[12] //* 退业态积分 char
			 * ResultInfo[255] //* 回应信息
			 */
			if (Convert.toDouble(cashAmount)==0) return true;
			if (saleHead.str3!=""&& saleHead.num1==1)
			  if (!TransComplete(saleHead.str3)) {// 会员交易达成
				return false;
			  }
			BankLogSend();
		} else if (saleHead.djlb.equals(SellType.RETAIL_SALE_HC)) {// 红冲销售
																	// Of_UnMemberSales()
			StringBuffer UnTraceNumber = new StringBuffer();
			TextBox txt = new TextBox();
			if (!txt.open("请输入原主机流水号", "原主机流水号", "请将在键盘输入原主机流水号",
					UnTraceNumber, 0, 0, false, TextBox.IntegerInput))
				return false;
			String UnTraceNum = UnTraceNumber.toString();

			StringBuffer UnBatchNumber = new StringBuffer();
			if (!txt.open("请输入原批次号", "原批次号", "请将在键盘输入原批次号", UnBatchNumber, 0,
					0, false, TextBox.IntegerInput))
				return false;
			String UnBatchNum = UnBatchNumber.toString();

			StringBuffer Unfphm = new StringBuffer();
			if (!txt.open("请输入原收银流水号", "原收银流水号", "请将在键盘输入原收银流水号", Unfphm, 0, 0,
					false, TextBox.IntegerInput))
				return false;
			String Yfphm = Unfphm.toString();

			StringBuffer Unjyrq = new StringBuffer();
			if (!txt.open("请输入原交易时间", "原交易时间", "请将在键盘输入原交易时间(MMDD)", Unjyrq, 0,
					0, false, TextBox.IntegerInput))
				return false;
			String Yjyrq = Unjyrq.toString();
			String bonusAlterRequest = GlobalInfo.sysPara.mktcode + "," + syyh
					+ "," + track + "," + fphm + "," + UnTraceNum + ","
					+ UnBatchNum + "," + Yjyrq + "," + Yfphm;
			if (RdPlugins.getDefault().getPlugins1().exec(4, bonusAlterRequest)) {
				bonusAlterReturn = (String) RdPlugins.getDefault()
						.getPlugins1().getObject();
				
				if (bonusAlterReturn != null
						&& bonusAlterReturn.trim().length() > 0) {
					/**
					 * char ResultCode[2] // 应答代码 char TraceNumber[12] // 主机流水号
					 * char BatchNumber[6] // 批次号 char Bonus[12] // 当前积分余额 char
					 * ValidBonus[12] // 当前可用积分 char UseBonus[12] // 本次使用积分 char
					 * GiveBonus[12] // 本次积分增加
					 */
					PosLog.getLog(getClass()).info(
							"小票号:" + fphm + " " + bonusAlterReturn);
					try
					{
						bld = new BankLogDef();

						Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");

						if (obj == null)
						{
							bld.rowcode = 1;
						}
						else
						{
							bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
						}

						bld.rqsj = ManipulateDateTime.getCurrentDateTime();
						bld.syjh = GlobalInfo.syjDef.syjh;
						bld.fphm = GlobalInfo.syjStatus.fphm;
						bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh : "");
						bld.type = "8";
						if(bonusAlterReturn!=null&&bonusAlterReturn.substring(56, 68)!="")
						bld.je = Convert.toDouble(bonusAlterReturn.substring(56, 68));
						else bld.je=0;
						bld.oldrq = saleHead.rqsj;
						bld.typename ="积分撤销";
					    bld.oldtrace = 0;
						bld.cardno =track;//会员卡号
						if (bonusAlterReturn!=null&&bonusAlterReturn.substring(2, 14)!="")
							  bld.trace = Convert.toLong(bonusAlterReturn.substring(2, 14));
						else bld.trace=0;
						bld.authno = "";
						bld.bankinfo = "";
						bld.crc = "";
						if (bonusAlterReturn!=null&&bonusAlterReturn!="")
							bld.retcode = bonusAlterReturn.substring(0, 2);
						else bld.retcode="";
						bld.retmsg = "";
						bld.retbz = 'N';
						bld.net_bz = 'N';
						bld.allotje = 0;
						bld.memo = "红冲销售积分";
						bld.memo1 = "";
						bld.memo2 = "";
						bld.tempstr = "";
						bld.tempstr1 = "";
						if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
						BankLogSend();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();

						new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
						bld = null;

						return false;
					}
					if (bonusAlterReturn.substring(0, 2).equals("00")) {
						if (TransComplete(bonusAlterReturn.substring(2, 14))) {// 会员交易达成
							saleHead.num1 = Convert.toDouble(bonusAlterReturn.substring(44, 56))*(-1);//本次积分
							saleHead.num2 = Convert.toDouble(bonusAlterReturn.substring(32, 44));//累计积分
			                saleHead.str3 = bonusAlterReturn.substring(2, 14);//主机流水号
			                saleHead.str4 = bonusAlterReturn.substring(14,20);//批次号
						}
					} else {
						new MessageBox("大会员积分红冲销售接口返回数据失败："
								+ bonusAlterReturn.substring(0, 2));
					}
					return false;
				}
			}
		} else if (saleHead.djlb.equals(SellType.RETAIL_BACK_HC)) {// 红冲退货
																	// Of_UnMemberReturn()
			/**
			 * 交易类型04 || (退货小票&&交易类型01) = 减积分 //退货冲正 或者 退货小票大会员积分退款撤销
			 * Of_UnMemberReturn () char TenantID[15] //* 商户代码 char CasherID[20]
			 * //* 收银员号 char Track[40] //* 磁道信息 char ReceiptNumber[12] //
			 * 本次收银流水号 char UnTraceNumber[12] //* 原主机流水号 char UnBatchNumber[6]
			 * //* 原批次号 char UnDate[4] //* 原交易日期 char UnReceiptNumber[12] //
			 * 原收银流水号 char ResultInfo[255] //* 回应信息
			 */
			StringBuffer UnTraceNumber = new StringBuffer();
			TextBox txt = new TextBox();
			if (!txt.open("请输入原主机流水号", "原主机流水号", "请将在键盘输入原主机流水号",
					UnTraceNumber, 0, 0, false, TextBox.IntegerInput))
				return false;
			String UnTraceNum = UnTraceNumber.toString();

			StringBuffer UnBatchNumber = new StringBuffer();
			if (!txt.open("请输入原批次号", "原批次号", "请将在键盘输入原批次号", UnBatchNumber, 0,
					0, false, TextBox.IntegerInput))
				return false;
			String UnBatchNum = UnBatchNumber.toString();

			StringBuffer Unfphm = new StringBuffer();
			if (!txt.open("请输入原收银流水号", "原收银流水号", "请将在键盘输入原收银流水号", Unfphm, 0, 0,
					false, TextBox.IntegerInput))
				return false;
			String Yfphm = Unfphm.toString();

			StringBuffer Unjyrq = new StringBuffer();
			if (!txt.open("请输入原交易时间", "原交易时间", "请将在键盘输入原交易时间(MMDD)", Unjyrq, 0,
					0, false, TextBox.IntegerInput))
				return false;
			String Yjyrq = Unjyrq.toString();
			cashAmount = String.valueOf(Math.abs(saleHead.ysje));
			String bonusAlterRequest = GlobalInfo.sysPara.mktcode + "," + syyh
					+ "," + track + "," + fphm + "," + UnTraceNum + ","
					+ UnBatchNum + "," + Yjyrq + "," + Yfphm;
			if (RdPlugins.getDefault().getPlugins1().exec(6, bonusAlterRequest)) {
				bonusAlterReturn = (String) RdPlugins.getDefault()
						.getPlugins1().getObject();
				/**
				 * char ResultCode[2] // 应答代码 char TraceNumber[12] // 主机流水号 char
				 * BatchNumber[6] // 批次号 char Bonus[12] // 当前积分余额 char
				 * ValidBonus[12] // 当前可用积分 char ReBonus[12] // 积分退还 char
				 * ReGiveBonus[12] // 积分赠送退还
				 */
				PosLog.getLog(getClass()).info(
						"小票号:" + fphm + " " + bonusAlterReturn);
				try
				{
					bld = new BankLogDef();

					Object obj = GlobalInfo.dayDB.selectOneData("select max(rowcode) from BANKLOG");

					if (obj == null)
					{
						bld.rowcode = 1;
					}
					else
					{
						bld.rowcode = Integer.parseInt(String.valueOf(obj)) + 1;
					}

					bld.rqsj = ManipulateDateTime.getCurrentDateTime();
					bld.syjh = GlobalInfo.syjDef.syjh;
					bld.fphm = GlobalInfo.syjStatus.fphm;
					bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh : "");
					bld.type = "10";
					if(bonusAlterReturn!=null&&bonusAlterReturn.substring(56, 68)!="")
						bld.je = Convert.toDouble(bonusAlterReturn.substring(56, 68));
					else bld.je=0;
					bld.oldrq = saleHead.rqsj;
					bld.typename ="积分退货撤销";
				    bld.oldtrace = 0;
					bld.cardno =track;//会员卡号
					if (bonusAlterReturn!=null&&bonusAlterReturn.substring(2, 14)!="")
					  bld.trace = Convert.toLong(bonusAlterReturn.substring(2, 14));
					else bld.trace=0;
					bld.authno = "";
					bld.bankinfo = "";
					bld.crc = "";
					if (bonusAlterReturn!=null&&bonusAlterReturn!="")
					bld.retcode = bonusAlterReturn.substring(0, 2);
					else bld.retcode="";
					bld.retmsg = "";
					bld.retbz = 'N';
					bld.net_bz = 'N';
					bld.allotje = 0;
					bld.memo = "红冲退货退积分";
					bld.memo1 = "";
					bld.memo2 = "";
					bld.tempstr = "";
					bld.tempstr1 = "";
					if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
					BankLogSend();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();

					new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
					bld = null;

					return false;
				}
				if (bonusAlterReturn.substring(0, 2).equals("00")) {
					if (TransComplete(bonusAlterReturn.substring(2, 14))) {// 会员交易达成
						saleHead.num1 = Convert.toDouble(bonusAlterReturn.substring(44, 56));//本次积分
						saleHead.num2 = Convert.toDouble(bonusAlterReturn.substring(32, 44));//累计积分
		                saleHead.str3 = bonusAlterReturn.substring(2, 14);//主机流水号
		                saleHead.str4 = bonusAlterReturn.substring(14,20);//批次号
					}
				} else {
					new MessageBox("大会员积分红冲退货接口返回数据失败："
							+ bonusAlterReturn.substring(0, 2));
				}
				return false;
			}
		}
		if (!GlobalInfo.dayDB.executeSql("update salehead set str3 = '"+saleHead.str3+"',str4='"+saleHead.str4+"',num1="+saleHead.num1
				+",num2="+saleHead.num2+" where syjh = '" + ConfigClass.CashRegisterCode + "' and  fphm = " + saleHead.fphm))
        {
            new MessageBox(Language.apply("更新大会员返回数据错误"));
        }
		return true;
	}

	protected double calcPayFPMoney(SaleHeadDef salehead, Vector saleGoods,
			Vector salepay) {
		double je = salehead.sjfk - salehead.zl;

		String payex = "," + GlobalInfo.sysPara.fpjepayex + ",";
		for (int i = 0; i < salepay.size(); i++) {
			SalePayDef sp = (SalePayDef) salepay.elementAt(i);
			if (sp.flag == '1' && payex.indexOf("," + sp.paycode + ",") >= 0) {
				je -= sp.je;

				for (int j = 0; j < salepay.size(); j++) {
					SalePayDef sp1 = (SalePayDef) salepay.elementAt(j);
					if (sp1.flag == '2' && sp1.paycode.equals(sp.paycode)) {
						je += sp1.je;
					}
				}
			}
		}
		return je;
	}

	public boolean TransComplete(String TraceNumber) {
		/**
		 * Of_TransComplete() char TenantID[15] //* 商户代码 char CasherID[20] //*
		 * 收银员号 char TraceNumber[12] //* 主机流水号
		 */
		String mktcode = GlobalInfo.sysPara.mktcode;
		String syyh = GlobalInfo.posLogin.gh;
		String bonusAlterRequest = mktcode + "," + syyh + "," + TraceNumber;
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
