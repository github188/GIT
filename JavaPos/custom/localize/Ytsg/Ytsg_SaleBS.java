package custom.localize.Ytsg;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Device.RdPlugins;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Ytsg_SaleBS extends Cmls_SaleBS {
    protected BankLogDef bld = null;
	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods,
			Vector thsalePayment) {
		super.takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);
		saleHead.hykh = thsaleHead.hykh;
		saleHead.bcjf = thsaleHead.bcjf;
	}
	public boolean memberGrant() {
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode()
				&& !isNewUseSpecifyTicketBack(false)) {
			new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
			return false;
		}

		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();

		// 读取会员卡
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;

		// 解析出磁道和选择的类型
		String[] s = track2.split(",");
		track2 = s[0];

		// 查找会员卡
		 String memberInfoReturn=findMemberDHYCard(track2);
		 CustomerDef cust=new CustomerDef();
			 if (memberInfoReturn != null
						&& memberInfoReturn.trim().length() > 0) {
					if (memberInfoReturn.substring(0, 2).equals("00")) {
						byte[] mir = memberInfoReturn.getBytes();
						cust.code = new String(subBytes(mir, 2, 19)).trim();
						cust.valuememo = Double.parseDouble(new String(subBytes(mir, 33, 12)).trim());
						cust.type = new String(subBytes(mir, 92, 1))
								.trim();
						cust.name = new String(subBytes(mir, 213, 32)).trim();
						cust.status = "Y";
						cust.maxdate="0";
					} else {
						new MessageBox("该顾客卡已失效!");
						return false;
					} 
		 }
		saleHead.str6 = track2;
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false)) {
			if (cust.status == null || cust.status.trim().length() <= 0
					|| cust.status.charAt(0) != 'Y') {
				new MessageBox("该顾客卡已失效!");
				return false;
			}
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			return true;
		} else {
			if (memberGrantFinish(cust)) {
				return true;
			}
			// 记录会员卡
			return false;
		}
	}
	
	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		for (int i = begin; i < begin + count; i++)
			bs[i - begin] = src[i];
		return bs;
	}
	
	public String findMemberDHYCard(String track2) {
		/**
		 * 1.调用dll获取大会员详细信息 2.将大会员详细信息上传到百货后台 3.生成CustomerDef对象返回
		 */
		String syjh = GlobalInfo.syjDef.syjh;
		String mktcode = GlobalInfo.sysPara.mktcode;
		String syyh = GlobalInfo.posLogin.gh;
		String memberInfoReturn = "";
		PosLog.getLog(getClass()).info(
				"收银机号:" + syjh + " 收银员号：" + syyh + " 轨道号:" + track2.trim());

		if (RdPlugins.getDefault().getPlugins1().exec(2,
				mktcode + "," + syyh + "," + track2.trim()+",0")) {
			memberInfoReturn = (String) RdPlugins.getDefault().getPlugins1()
					.getObject();
			return memberInfoReturn;
		}
		return null;

	}
	
	public boolean doRefundEvent() {
		if (!SellType.ISBACK(saletype))
			return true;

		if (GlobalInfo.sysPara.refundByPos == 'N')
			return true;

		if (!GlobalInfo.isOnline) {
			if (isNewUseSpecifyTicketBack()) {
				new MessageBox(Language.apply("必须在联网状态下检查退货扣回！"));
				return false;
			} else {
				return true;
			}
		}

		double jfkhje = 0;
		if (saleHead.hykh.length()>0&&saleHead.bcjf==1) {
			boolean b = false;
			while ((saleHead.str6 == null || saleHead.str6.trim().equals(""))) {
				// 读取会员卡
				HykInfoQueryBS bs = CustomLocalize.getDefault()
						.createHykInfoQueryBS();
				saleHead.str6 = bs.readMemberCard();
				if (saleHead.str6 == null || saleHead.str6.trim().equals("")) {
					if (new MessageBox("本笔小票有大会员刷卡,必须刷大会员卡进行退货!\n是否继续刷大会员？",
							null, true).verify() != GlobalVar.Key2) {
						continue;
					} else {
						PosLog.getLog(getClass()).info(
								"小票号:" + saleHead.fphm + " 收银员选择不刷大会员卡");
						saleHead.str6 = "";
						b = true;
						break;
					}
				}
			}
			if (b)
				return false;
		}

		// 清除扣回付款集合
		if (refundPayment == null)
			refundPayment = new Vector();
		else
			refundPayment.clear();
		if (refundAssistant == null)
			refundAssistant = new Vector();
		else
			refundAssistant.clear();

		// 获取需要扣回的金额 
		ProgressBox pb = new ProgressBox();
		try {
			// 调用后台过程返回需要扣回的金额
			pb.setText(Language.apply("正在获取退货小票的扣回金额......"));
			if ((saleHead.hykh.length()>0)&&(saleHead.bcjf==1)&&((saleHead.str3 =="")||(saleHead.str3 ==null))) {
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
				String mktcode =GlobalInfo.sysPara.mktcode;
				String syyh = GlobalInfo.posLogin.gh;
				String track="";
				if (!(saleHead.str6 == null) && (!saleHead.str6.trim().equals("")))
				   track =saleHead.str6;
				else track=saleHead.hykh;//原卡号
				String fphm = String.valueOf(saleHead.fphm);
				String Amount = String.valueOf(Math.abs(saleHead.ysje));
				StringBuffer UnTraceNumber = new StringBuffer();
				TextBox txt = new TextBox();
				if (!txt.open("请输入原主机流水号", "原主机流水号", "请在键盘输入原主机流水号",
						UnTraceNumber, 0, 0, false, TextBox.IntegerInput))
					return false;
				String UnTraceNum = UnTraceNumber.toString();

				StringBuffer UnBatchNumber = new StringBuffer();
				if (!txt.open("请输入原批次号", "原批次号", "请在键盘输入原批次号", UnBatchNumber, 0,
						0, false, TextBox.IntegerInput))
					return false;
				String UnBatchNum = UnBatchNumber.toString();

				StringBuffer Unfphm = new StringBuffer();
				if (!txt.open("请输入原收银流水号", "原收银流水号", "请在键盘输入原收银流水号", Unfphm, 0, 0,
						false, TextBox.IntegerInput))
					return false;
				String Yfphm = Unfphm.toString();

				StringBuffer Unjyrq = new StringBuffer();
				if (!txt.open("请输入原交易日期", "原交易日期", "请在键盘输入原交易日期(MMDD)", Unjyrq, 0, 0,
					false, TextBox.IntegerInput))
					return false;
				String Yjyrq = Unjyrq.toString();
				System.out.println("1");
				StringBuffer Unyxje = new StringBuffer();
				if (!txt.open("请输入有效消费金额", "有效消费金额", "请在键盘输入有效消费金额", Unyxje, 0, 0,
					false, TextBox.DoubleInput))
					return false;
				String Yxje = Unyxje.toString();

				String bonusAlterRequest = mktcode + "," + syyh
						+ "," + track + "," + fphm + "," + UnTraceNum + ","
						+ UnBatchNum + "," + Yjyrq + "," + Yfphm + ",2,"
						+ Amount +  ","+Yxje+",0";
				if (RdPlugins.getDefault().getPlugins1().exec(5, bonusAlterRequest)) {
					String bonusAlterReturn = (String) RdPlugins.getDefault()
							.getPlugins1().getObject();
					if (bonusAlterReturn != null
							&& bonusAlterReturn.trim().length() > 0) {
						/** char       ResultCode[2]        // 应答代码
	                        char       TraceNumber[12]      // 主机流水号
	                        char       BatchNumber[6]       // 批次号
	                        char       CardNumber[19]       // 卡号
	                        char       Bonus[12]            // 当前积分余额
	                        char       ValidBonus[12]       // 当前可用积分
	                        char       ReBonus[12]          // 积分退还
	                        char       ReGiveBonus[12]      // 积分赠送退还
	                        char       PayAmount[12]        // 本交易已支付金额
	                        char       BalanceAmount[12]    // 本交易未付余额
	                        char       BonusAmount[12]      // 本次可退回积分金额
	                        char       ReturnCash[12]      // 退货补现金额
						 **/
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
							bld.type = "9";
							bld.je = Convert.toDouble(Yxje);
							bld.oldrq = saleHead.rqsj;
							bld.typename ="积分退货";
						    bld.oldtrace = 0;
							bld.cardno =track;//会员卡号
							bld.trace = Convert.toLong(bonusAlterReturn.substring(2, 14));
							bld.authno = "";
							bld.bankinfo = "";
							bld.crc = "";
							bld.retcode = bonusAlterReturn.substring(0, 2);
							bld.retmsg = "";
							bld.retbz = 'N';
							bld.net_bz = 'N';
							bld.allotje = 0;
							bld.memo = "";
							bld.memo1 = "";
							bld.memo2 = "";
							bld.tempstr = "";
							bld.tempstr1 = "";
							if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
						}
						catch (Exception ex)
						{
							ex.printStackTrace();

							new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
							bld = null;

							return false;
						}
						if (bonusAlterReturn.substring(0, 2).equals("00")) {
//							if (TransComplete(bonusAlterReturn.substring(2, 14))){//会员交易达成
						    jfkhje=Convert.toDouble(bonusAlterReturn.substring(123, 135).trim());//退货补现金额
						    saleHead.str3=bonusAlterReturn.substring(2, 14);//主机流水号   用于后面确认退货交易
							saleHead.str4=bonusAlterReturn.substring(14, 20);//批次号
							saleHead.num1 = Convert.toDouble(bonusAlterReturn.substring(75, 87))*(-1);//本次积分
							saleHead.num2 = Convert.toDouble(bonusAlterReturn.substring(51, 63));//累计积分
//							}
						} else if (bonusAlterReturn.substring(0, 2)
								.equals("FE")) {
							new MessageBox(
									"泰豆余额不足，请持小票至服务台购买泰豆再来退货！");
							Ytsg_MenuFuncBS wm = new Ytsg_MenuFuncBS();
							wm.PrintDHYDoc();
							return false;
						}else {
							new MessageBox("大会员积分退货接口返回数据失败："
									+ bonusAlterReturn.substring(0, 2));
							return false;
						}
					}
				} else {
					new MessageBox("未找到匹配交易类型，无法发送积分调整数据!");
					return false;
				}

				// 关闭提示
				if (pb != null) {
					pb.close();
					pb = null;
				}
				}

				// 无扣回金额,不用输入
				refundTotal = jfkhje;

				// 员工缴费和结算单如果存在扣回，不允许通过
				if ((SellType.isJF(saletype) || SellType.isJS(saletype))
						&& Math.abs(refundTotal) > 0) {
					new MessageBox(Language.apply("员工缴费 或 结算单 不允许存在扣回\n"));
					return false;
				}

				//liwj test
				/*refundTotal = 1;*/
				if (refundTotal <= 0)
					return true;

				StringBuffer s = new StringBuffer();
				s.append(Language.apply("该退货小票总共需要扣{0}元\n\n",
						new Object[] { ManipulatePrecision
								.doubleToString(refundTotal) }));
				// 有扣回不允许退货
				if (GlobalInfo.sysPara.refundAllowBack != 'Y'
						&& refundTotal > 0) {
					s.append(Language.apply("\n扣回金额大于0,不能进行退货\n"));
					refundMessageBox(s.toString());

					return false;
				}

				refundMessageBox(s.toString());
		} catch (Exception er) {
			er.printStackTrace();
		} finally {
			if (pb != null) {
				pb.close();
				pb = null;
			}
		}

		// 标记扣回开始
		refundFinish = false;
		isRefundPayStatus = true;

		// 打开扣回付款输入窗口
		new SalePayForm().open(saleEvent.saleBS, true);

		isRefundPayStatus = false;
		return refundFinish;
	}
	
	public boolean clearSell(int index)
	{
		// 先取消VIP或临时折扣
		if (cancelMemberOrGoodsRebate(index)) { return true; }

		if (saleGoods.size() <= 0)
		{
			// 退货交易切换回销售交易
			if (SellType.ISBACK(saletype))
			{
				djlbBackToSale();
			}

			//
			initOneSale(this.saletype);

			return true;
		}

		if (new MessageBox(Language.apply("你确定要取消本笔交易输入吗?"), null, true).verify() != GlobalVar.Key1) { return false; }

		// 没有取消权限
		String grantgh;

		if (operPermission(clearPermission, curGrant))
		{
			OperUserDef staff = clearSellGrant();

			if (staff == null) { return false; }

			grantgh = staff.gh;
		}
		else
		{
			grantgh = saleHead.syyh;
		}
		if ((saleHead.str6 != null) && (!saleHead.str6.trim().equals(""))&&((saleHead.str3.trim()).length()>0)){//
    		TransComplete(saleHead.str3);//确认交易
//    		红冲退货  Of_UnMemberReturn()
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
			 */
    		String fphm=String.valueOf(saleHead.fphm);
    		String mktcode =GlobalInfo.sysPara.mktcode;
			//String syjh = GlobalInfo.syjDef.syjh;
			String syyh = GlobalInfo.posLogin.gh;
			String Yjyrq= (new ManipulateDateTime().getDateByEmpty()).substring(4, 8);
			//String cashAmount = String.valueOf(Math.abs(saleHead.ysje));
			String bonusAlterRequest = mktcode + "," + syyh
					+ "," + saleHead.str6 + "," + fphm + "," + saleHead.str3 + ","
					+ saleHead.str4 + "," + Yjyrq + "," + fphm;
			if (RdPlugins.getDefault().getPlugins1().exec(6, bonusAlterRequest)) {
				String bonusAlterReturn = (String) RdPlugins.getDefault()
						.getPlugins1().getObject();
				/**char       ResultCode[2]        // 应答代码
				 char       TraceNumber[12]      // 主机流水号
				 char       BatchNumber[6]       // 批次号
				 char       Bonus[12]            // 当前积分余额
				 char       ValidBonus[12]       // 当前可用积分
				 char       ReBonus[12]          // 积分退还
				 char       ReGiveBonus[12]      // 积分赠送退还
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
					bld.fphm = saleHead.fphm;
					bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh : "");
					bld.type = "10";
					bld.je = saleHead.bcjf;
					bld.oldrq = saleHead.rqsj;
					bld.typename ="积分退货撤销";
				    bld.oldtrace = 0;
					bld.cardno =saleHead.hykh;//会员卡号
					bld.trace = Convert.toLong(bonusAlterReturn.substring(2, 14));
					bld.authno = "";
					bld.bankinfo = "";
					bld.crc = "";
					bld.retcode = bonusAlterReturn.substring(0, 2);
					bld.retmsg = "";
					bld.retbz = 'N';
					bld.net_bz = 'N';
					bld.allotje = 0;
					bld.memo = "";
					bld.memo1 = "";
					bld.memo2 = "";
					bld.tempstr = "";
					bld.tempstr1 = "";
						if (!AccessDayDB.getDefault().writeBankLog(bld)) { return false; }
				}
				catch (Exception ex)
				{
					ex.printStackTrace();

					new MessageBox("写入请求数据交易日志失败\n\n" + ex.getMessage(), null, false);
					bld = null;

					return false;
				}
				if (bonusAlterReturn.substring(0, 2).equals("00")) {
					if (TransComplete(bonusAlterReturn.substring(2, 14))){//会员交易达成
					saleHead.ljjf = Double.parseDouble(bonusAlterReturn
							.substring(32, 44));
					}
				} else {
					new MessageBox("大会员积分退货撤销接口返回数据失败："
							+ bonusAlterReturn.substring(0, 2));
					return false;
				}
			}
		
    		
    	}
		//
		if (!SellType.ISEXERCISE(this.saletype))
		{
			// 记录日志
			String log = "取消交易,小票号:" + Convert.increaseLong(saleHead.fphm, 7) + ",金额:" + Convert.increaseChar(ManipulatePrecision.doubleToString(saleHead.ysje), '0', 10) + ",授权:" + grantgh;
			AccessDayDB.getDefault().writeWorkLog(log, StatusType.WORK_CLEARSALE);

			// 记汇总
			SaleSummaryDef saleSummaryDef = new SaleSummaryDef();
			saleSummaryDef.zl = 0;
			saleSummaryDef.sysy = 0;
			saleSummaryDef.sjfk = 0;
			saleSummaryDef.zkje = 0;
			saleSummaryDef.ysje = 0;
			saleSummaryDef.qxbs = 1;
			saleSummaryDef.qxje = saleHead.ysje;

			// 写入全天销售统计
			saleSummaryDef.bc = '0';
			saleSummaryDef.syyh = "全天";
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);

			// 写入当班收银员销售统计
			saleSummaryDef.bc = saleHead.bc;
			saleSummaryDef.syyh = saleHead.syyh;
			AccessDayDB.getDefault().writeSaleSummary(saleSummaryDef);
		}

		// 退货交易切换回销售交易
		if (SellType.ISBACK(saletype))
		{
			djlbBackToSale();
		}

		// 初始化新交易
		initOneSale(this.saletype);

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
	
	public boolean deleteAllSalePay()
    {
		if ((saleHead.str6 != null) && (!saleHead.str6.trim().equals(""))&&((saleHead.str3.trim()).length()>0)){//
    		TransComplete(saleHead.str3);//确认交易
//    		红冲退货  Of_UnMemberReturn()
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
			 */
    		String fphm=String.valueOf(saleHead.fphm);
    		String mktcode =GlobalInfo.sysPara.mktcode;
			//String syjh = GlobalInfo.syjDef.syjh;
			String syyh = GlobalInfo.posLogin.gh;
			String Yjyrq= (new ManipulateDateTime().getDateByEmpty()).substring(4, 8);
			//String cashAmount = String.valueOf(Math.abs(saleHead.ysje));
			String bonusAlterRequest = mktcode + "," + syyh
					+ "," + saleHead.str6 + "," + fphm + "," + saleHead.str3 + ","
					+ saleHead.str4 + "," + Yjyrq + "," + fphm;
			if (RdPlugins.getDefault().getPlugins1().exec(6, bonusAlterRequest)) {
				String bonusAlterReturn = (String) RdPlugins.getDefault()
						.getPlugins1().getObject();
				/**char       ResultCode[2]        // 应答代码
				 char       TraceNumber[12]      // 主机流水号
				 char       BatchNumber[6]       // 批次号
				 char       Bonus[12]            // 当前积分余额
				 char       ValidBonus[12]       // 当前可用积分
				 char       ReBonus[12]          // 积分退还
				 char       ReGiveBonus[12]      // 积分赠送退还
				 */
				PosLog.getLog(getClass()).info(
						"小票号:" + fphm + " " + bonusAlterReturn);
				if (bonusAlterReturn.substring(0, 2).equals("00")) {
					if (TransComplete(bonusAlterReturn.substring(2, 14))){//会员交易达成
					saleHead.ljjf = Double.parseDouble(bonusAlterReturn
							.substring(32, 44));
					if (GlobalInfo.sysPara.isPrintDHY.equals("Y")) {
						Ytsg_MenuFuncBS wm = new Ytsg_MenuFuncBS();
						wm.PrintDHYDoc();
					saleHead.str3="";
					}
					}
				} else {
					new MessageBox("大会员积分退货撤销接口返回数据失败："
							+ bonusAlterReturn.substring(0, 2));
					return false;
				}
			}
    	}
		return super.deleteAllSalePay();
		
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
				new MessageBox(Language.apply("小票附加信息输入失败,不能完成交易!"));
				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在汇总交易数据,请等待....."));
			if (!saleSummary())
			{
				new MessageBox(Language.apply("交易数据汇总失败!"));

				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在校验数据平衡,请等待....."));
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, Language.apply("正在输入客户信息,请等待......"));
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, Language.apply("正在打开钱箱,请等待....."));
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, Language.apply("正在记账付款数据,请等待....."));
				if (!saleCollectAccountPay())
				{
					new MessageBox(Language.apply("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!"));

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}
				
				if(calcPayFPMoney(saleHead, saleGoods,salePayment)!=0) saleHead.bcjf=1;

				setSaleFinishHint(status, Language.apply("正在写入交易数据,请等待......"));
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox(Language.apply("交易数据写盘失败!"));
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, Language.apply("正在清除断点保护数据,请等待......"));
				clearBrokenData();

				//
				setSaleFinishHint(status, Language.apply("正在清除付款冲正数据,请等待......"));
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!"));
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				// 上传当前小票
				setSaleFinishHint(status, Language.apply("正在上传交易小票数据,请等待......"));
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
				setSaleFinishHint(status, Language.apply("正在上传收银机交易汇总,请等待......"));
				DataService.getDefault().sendSyjStatus();

				doEvaluation(this.saleHead, this.saleGoods, this.salePayment);
				
				// 打印小票
				setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
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
			setSaleFinishHint(status, Language.apply("正在等待关闭钱箱,请等待......"));
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
			setSaleFinishHint(status, Language.apply("本笔交易结束,开始新交易"));

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox(Language.apply("完成交易时发生异常:\n\n") + ex.getMessage());

			return saleFinish;
		}
	}
}
