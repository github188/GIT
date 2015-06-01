package custom.localize.Ytbh;

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
import com.efuture.javaPos.Communication.NetService;
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
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SaleSummaryDef;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Ytbh_SaleBS extends Cmls_SaleBS {
	protected BankLogDef bld =null;
	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods,
			Vector thsalePayment) {
		super.takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);
		saleHead.bcjf=thsaleHead.bcjf;
	}

	public boolean memberGrant() {
		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (saleGoods.size() > 0 && !memberAfterGoodsMode()
				&& !isNewUseSpecifyTicketBack(false)) {
			new MessageBox("必须在输入商品前进行刷会员卡\n请把商品清除后再重刷卡");
			return false;
		}
		//		 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;
		track2=track2.replace(";","");
        if(track2.length()>16){
        	if (track2.contains("=")){
        	String[] s = track2.split("=");
        	track2 = s[0];
        	}	
        }
        String track="";
        if (track2.length()==16)
        	track="#"+track2;
        if (track2.length()==11)
        	track="@"+track2;
		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track);
		//		后台数据库没查到就去调用大会员接口查
		if (cust == null || cust.code == null || cust.code.trim().equals("")) {
			/**
			 * 1.调用dll获取大会员详细信息 2.将大会员详细信息上传到百货后台 3.生成CustomerDef对象返回
			 */
			String memberInfoReturn = findMemberDHYCard(track2);
			if (memberInfoReturn != null
					&& memberInfoReturn.trim().length() > 0) {
				/**
				 * char ResultCode[2] // 应答代码 
				 * char CardID[19] // 卡号 
				 * char Bonus[12] // 当前积分余额 char ValidBonus[12] // 当前可用积分 char
				 * CertificateType[2] // 证件类型：居民身份证 01、士官证 02、学生证 03、驾驶证
				 * 04、护照、05、港澳通行证 06、其他07 
				 * char Certificate[32] // 证件号 
				 * char Sex[2] // 性别 男01 女02 char Phone[11] // 手机 
				 * char MemberLevel[1] //会员级别：注册会员 0、金卡会员 1、白金卡会员 2、钻石卡会员 3 
				 * char Address[80] // 地址 char Email[40] // 电子邮件 
				 * char ResultText[42] // 回应信息
				 */
				if (memberInfoReturn.substring(0, 2).equals("00")) {
					byte[] mir = memberInfoReturn.getBytes();
					String cardID = new String(subBytes(mir, 2, 19)).trim();
					String bonus = new String(subBytes(mir, 21, 12)).trim();
					String validBonus = new String(subBytes(mir, 33, 12))
							.trim();
					String certificateType = new String(subBytes(mir, 63, 2))
							.trim();
					String certificate = new String(subBytes(mir, 65, 32))
							.trim();
					String sex = new String(subBytes(mir, 97, 2)).trim();
					String phone = new String(subBytes(mir, 99, 11)).trim();
					String memberLevel = new String(subBytes(mir, 110, 1))
							.trim();
					String address = new String(subBytes(mir, 111, 80)).trim();
					String email = new String(subBytes(mir, 191, 40)).trim();
					String name = new String(subBytes(mir, 231, 32)).trim();
					Ytbh_NetService wn = new Ytbh_NetService();
					// CustomerDef cust = new CustomerDef();//后台插入的过程需要更改
					if (!wn.sendMemberInfo(cust, name, track2, cardID, bonus,
							validBonus, certificateType, certificate, sex,
							phone, memberLevel, address, email)) {
					new MessageBox("该顾客卡已失效!");
					return false;
				}

			}
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
		} else {
			if (memberGrantFinish(cust)) {
				saleHead.str6 = track2;
				return true;
			}
			// 记录会员卡
			return false;
		}
		return true;
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
		if (track2.length() > 16) {
			String[] s = track2.split("=");
			track2 = s[0];
		}
		if (GlobalInfo.sysPara.mktcode.indexOf(",") >= 0)
			GlobalInfo.sysPara.mktcode = GlobalInfo.sysPara.mktcode.split(",")[1];
		String syjh = GlobalInfo.syjDef.syjh;
		String mktcode = GlobalInfo.sysPara.mktcode;
		String syyh = GlobalInfo.posLogin.gh;
		String memberInfoReturn = "";
		PosLog.getLog(getClass()).info(
				"收银机号:" + syjh + " 收银员号：" + syyh + " 轨道号:" + track2.trim());

		if (RdPlugins.getDefault().getPlugins1().exec(2,mktcode+"," +
				syyh + "," + track2.trim() + "," + saleHead.fphm)) {
			memberInfoReturn = (String) RdPlugins.getDefault().getPlugins1()
					.getObject();
			return memberInfoReturn;
		}
		PosLog.getLog(getClass()).info(
				"小票号:" + saleHead.fphm + " " + memberInfoReturn);

		return null;

	}

	public String findDHYJF(String track) {
		String result = "";
		result = findMemberDHYCard(track);
		if (result != null && result.length() > 0) {
			return result.substring(33, 45).trim();
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
		if (saleHead.hykh.length()>0) {
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
		char bc = saleHead.bc;
		try {
			saleHead.bc = '#';
			// 发送当前退货小票到后台数据库
			pb.setText(Language.apply("正在发送退货小票用于计算扣回金额......"));
			if (!this.saleEvent.saleBS.saleSummary()) {
				new MessageBox(Language.apply("交易数据汇总失败!"));
				return false;
			}
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods,
					salePayment)) {
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 发送当前退货小票以计算扣回
			// jdfhdd标记当前发送的是用于计算扣回的小票信息
			String oldfhdd = saleHead.jdfhdd;
			saleHead.jdfhdd = "KHINV";
			if (GlobalInfo.sysPara.refundByPos == 'B') {
				if (DataService.getDefault().doRefundExtendSaleData(saleHead,
						saleGoods, salePayment, null) != 0) {
					saleHead.jdfhdd = oldfhdd;
					return false;
				}
			} else {
				// = 'Y',扣回在付款前进行处理，生成缺省付款便于发送小票
				Vector tempPay = new Vector();
				SalePayDef tempsp = new SalePayDef();
				tempsp.syjh = saleHead.syjh;
				tempsp.fphm = saleHead.fphm;
				tempsp.rowno = 1;
				tempsp.flag = '1';
				tempsp.paycode = "KHFK";
				tempsp.payname = "扣回虚拟付款";
				tempsp.ybje = saleHead.ysje;
				tempsp.hl = 1;
				tempsp.je = saleHead.ysje;
				tempPay.add(tempsp);
				if (DataService.getDefault().doRefundExtendSaleData(saleHead,
						saleGoods, tempPay, null) != 0) {
					saleHead.jdfhdd = oldfhdd;
					return false;
				}
			}

			saleHead.jdfhdd = oldfhdd;

			// 调用后台过程返回需要扣回的金额
			pb.setText(Language.apply("正在获取退货小票的扣回金额......"));
			RefundMoneyDef rmd = new RefundMoneyDef();
				if (!NetService.getDefault().getRefundMoney(saleHead.mkt,
						saleHead.syjh, saleHead.fphm, rmd)) {
					return false;
				}
				if ((saleHead.hykh.length()>0)&&(Math.abs(rmd.jfkhje)>0)&&((saleHead.str3 =="")||(saleHead.str3 ==null))) {
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
				String cashAmount = String.valueOf(Math.abs(saleHead.ysje));
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
				if (!txt.open("请输入原交易日期", "原交易日期", "请将在键盘输入原交易日期(MMDD)", Unjyrq, 0, 0,
					false, TextBox.IntegerInput))
					return false;
				String Yjyrq = Unjyrq.toString();

				String bonusAlterRequest = mktcode + "," + syyh
						+ "," + track + "," + fphm + "," + UnTraceNum + ","
						+ UnBatchNum + "," + Yjyrq + "," + Yfphm + ",2,"
						+ cashAmount +  ",0,"+Math.abs(rmd.jfkhje);
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
							bld.fphm = saleHead.fphm;
							bld.syyh = (GlobalInfo.posLogin != null ? GlobalInfo.posLogin.gh : "");
							bld.type = "9";
							bld.je = rmd.jfkhje;
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
						} else {
							new MessageBox("大会员积分退货接口返回数据失败："
									+ bonusAlterReturn.substring(0, 2));
							return false;
						}
					}
				} else {
					new MessageBox("未找到匹配交易类型，无法发送积分调整数据!");
					return false;
				}
				}
				
//				 关闭提示
				if (pb != null) {
					pb.close();
					pb = null;
				}

				// 存在家电下乡返款扣回，不允许退货
				if (rmd.jdxxfkje > 0) {
					new MessageBox(Language
							.apply("该退货小票存在家电下乡返款\n请退返款之后再进行退货交易"));
					return false;
				}

				// 无扣回金额,不用输入
				System.out.println(jfkhje);
				refundTotal = rmd.fqkhje + rmd.qtkhje+jfkhje;

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
				if ((SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype))
						&& SellType.ISBACK(saletype)) {
					if (refundlist == null)
						refundlist = new Vector();
					else
						refundlist.removeAllElements();

					String[] rows = rmd.qtdesc.split("\\|");
					for (int i = 0; i < rows.length; i++) {
						String row[] = rows[i].split(",");
						refundlist.add(row);
						s.append(Convert
								.appendStringSize("", row[1], 0, 15, 10)
								+ " :"
								+ Convert.increaseCharForward(row[2], 10)
								+ "\n");
					}
				} else {
					if (rmd.jfdesc.length() > 0)
						s.append(rmd.jfdesc + "\n");
					else if (jfkhje > 0)
						s.append(Language.apply("其中因为积分原因需扣回{0}元\n",
								new Object[] { ManipulatePrecision
										.doubleToString(jfkhje) }));
					if (rmd.fqdesc.length() > 0)
						s.append(rmd.fqdesc + "\n");
					else if (rmd.fqkhje > 0)
						s.append(Language.apply("其中因为返券原因需扣回{0} 元\n",
								new Object[] { ManipulatePrecision
										.doubleToString(rmd.fqkhje) }));
					if (rmd.qtdesc.length() > 0)
						s.append(rmd.qtdesc + "\n");
					else if (rmd.qtkhje > 0)
						s.append(Language.apply("其中因为其他原因需扣回{0}元\n",
								new Object[] { ManipulatePrecision
										.doubleToString(rmd.qtkhje) }));
				}
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
			saleHead.bc = bc;
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
					saleHead.num1 = Double.parseDouble(bonusAlterReturn
								.substring(32, 44));
					saleHead.num2 = Double.parseDouble(bonusAlterReturn
							.substring(44, 56));
					saleHead.str3="";
					saleHead.str4="";
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
					saleHead.str3="";
					saleHead.str4="";
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
}