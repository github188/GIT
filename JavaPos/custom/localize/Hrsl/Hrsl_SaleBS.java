package custom.localize.Hrsl;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bstd.Bstd_SaleBS;

public class Hrsl_SaleBS extends Bstd_SaleBS
{
	Vector couponPay = new Vector();
	public boolean memberGrantFinish(CustomerDef cust)
	{
		if(super.memberGrantFinish(cust))
		{
			//保留会员返回的member_id
			saleHead.str6 = cust.track;
			return true;
		}
		saleHead.str6 = cust.track;
		return false;
	}
	
//	 获取退货小票信息
	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if(GlobalInfo.sysPara.inputydoc == 'D')
			{
				//只记录原单小票号和款机号,但不按原单找商品				
				return false;
			}
			
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
				//检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				//Y为已在后台退回礼品   津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y"))){
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
					cho= EBill.getDefault().getChoice(choice);
				}
				else{
					// 选择要退货的商品
					cho= new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh=new StringBuffer();
				if(GlobalInfo.sysPara.backyyyh =='Y'){
					new TextBox().open("开单营业员号：","", "请输入有效开单营业员号",backYyyh, 0);
//					 查找营业员
					OperUserDef staff = null;
					if(backYyyh.length() != 0){
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox("该工号不是营业员!", null, false);
								return false;
							}
						}else{
							return false;
						}
					}else{
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
				saleHead.memo = thsaleHead.memo;

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
	
	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;
		
		couponPay.removeAllElements();
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null)
				continue;
			if(p.salepay.paycode.equals("0202")){
				Hrsl_PaymentCoupon hp = (Hrsl_PaymentCoupon) payAssistant.elementAt(i);
				String tp = hp.mzkreq.str1+","+hp.salepay.je+","+hp.salepay.idno.split(",")[0];
				couponPay.add(tp);
			}
		}

		boolean ispay = false;
		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			if (p == null)
				continue;
			if(p.salepay.paycode.equals("0202")&&!ispay){
				Hrsl_PaymentCoupon hp = (Hrsl_PaymentCoupon) payAssistant.elementAt(i);
//				 第一次记账前先检查是否有冲正需要发送
				if (czsend)
				{
					czsend = false;
					if (!hp.sendAccountCz())
						return false;
				}

				// 付款记账
				if (!hp.collectAccountPay(couponPay))
					return false;
				
				if (hp.mzkreq.type == "01")
					hp.salepay.kye -= hp.mzkreq.je;
				else
					hp.salepay.kye += hp.mzkreq.je;
				
				ispay = true;
			}else{
//				 第一次记账前先检查是否有冲正需要发送
				if (czsend)
				{
					czsend = false;
					if (!p.sendAccountCz())
						return false;
				}
				
				if(p.salepay.paycode.equals("0202")){
					Hrsl_PaymentCoupon hp = (Hrsl_PaymentCoupon) payAssistant.elementAt(i);
//					 付款记账
					if (!hp.collectAccountPay1(couponPay))
						return false;
					if (hp.mzkreq.type == "01")
						hp.salepay.kye -= hp.mzkreq.je;
					else
						hp.salepay.kye += hp.mzkreq.je;
				}else{
//					 付款记账
					if (!p.collectAccountPay())
						return false;
				}
			}
			
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true))
			return false;

		return true;
	}

	public boolean deleteRefundPay(int index)
    {
        try
        {
            if (index >= 0)
            {
            	// 付款取消交易才能删除已付款
            	Payment p = (Payment) refundAssistant.elementAt(index);
            	if (p.cancelPay())
            	{
            		// 删除已付款的扣回
            		delSaleRefundObject(index);
            		
            		// 重算剩余扣回
            		calcRefundBalance();
            		
            		return true;
            	}
            }
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        
        return false;
    }
	
	public void execCustomKey6(boolean keydownonsale)
	{
		HykInfoQueryBS hq = CustomLocalize.getDefault().createHykInfoQueryBS();
		String tk2 = hq.readMemberCard();
		if(tk2 == null ||tk2.trim().equals("")) return;
		CustomerDef cust = hq.findMemberCard(tk2);
		if(cust==null)return;
		String mesg = "";
		double maxje = 0;
		String a = GlobalInfo.sysPara.CuseJFSaleRule.split(",")[0];  // 积分   2000
		String b = GlobalInfo.sysPara.CuseJFSaleRule.split(",")[1];  // 金额   20
		int num = (int) Math.floor(cust.num1/Double.parseDouble(a));
		maxje = Double.parseDouble(b) * num;
		StringBuffer buffer = new StringBuffer();
		double je = 0;
		while(true){
			if (!new TextBox().open("请输入需要兑换的积分分值", "单品折扣", mesg, buffer, 20, maxje, true))
			{
				return;
			}
			 je = Integer.parseInt(buffer.toString());
			if(je%20!=0){
				if (new MessageBox("兑换券值必须为"+b+"倍数!"+"\n"+"是否重新输入兑换券值？", null, true).verify() != GlobalVar.Key1) 
				{
					break;
					
				}else{
					continue;
				}
				
			}else{
				double jf = (je/Double.parseDouble(b)) * Double.parseDouble(a);
				Hrsl_ServiceCrmModule.getDefault().sendVipPointSale(saleHead, jf, cust);
				break;
			}
		}
	}
	
//	 会员授权
	public boolean memberGrant()
	{
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
		
		if(cust.track == null || cust.track.trim().equals("")){
			new MessageBox("查找会员卡ID失败,请重新刷卡");
			System.out.println(ManipulateDateTime.getCurrentDateTimeMilliSencond() + "  查找会员卡ID失败: 小票号"+saleHead.fphm +" 会员卡号"+cust.code );
			PosLog.getLog(getClass()).info(ManipulateDateTime.getCurrentDateTimeMilliSencond() + "  查找会员卡ID失败: 小票号"+saleHead.fphm +" 会员卡号"+cust.code );
			return false;
		}

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			saleHead.str6 = cust.track;
			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}
	
	public boolean paySellStart()
	{
		if(!saleHead.hykh.trim().equals("")){
			if(saleHead.str6 == null || saleHead.str6.trim().equals("")){
				new MessageBox("会员信息记录不完整,请重新刷卡");
				System.out.println(ManipulateDateTime.getCurrentDateTimeMilliSencond() + "  会员信息记录不完整: 小票号"+saleHead.fphm +" 会员卡号"+curCustomer.code );
				PosLog.getLog(getClass()).info(ManipulateDateTime.getCurrentDateTimeMilliSencond() + "  会员信息记录不完整: 小票号"+saleHead.fphm +" 会员卡号"+curCustomer.code );
				return false;
			}
		}
		return super.paySellStart();
	}
	
}
