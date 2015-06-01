package custom.localize.Sxtx;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.CustomerVipZklDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.PayRuleDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SalePayForm;
import com.swtdesigner.SWTResourceManager;

import custom.localize.Bstd.Bstd_SaleBS;

public class Sxtx_SaleBS extends Bstd_SaleBS
{
	GoodsDef inGoods = null;
	String instr_price = null;
	public String setGoodsLSJ(GoodsDef goodsDef,StringBuffer pricestr)
	{
		inGoods = goodsDef;
		Label lbl_barcode = (Label) clist.get("lbl_barcode");
		//String lbl_barcode1 = lbl_barcode.getText();
		lbl_barcode.setText("商品金额");
		lbl_barcode.setForeground(SWTResourceManager.getColor(255, 0, 0));
		return "0.01";
		//return a;
	}
	
	public boolean memberGrantFinish(CustomerDef cust)
    {
        if (cust.status == null || cust.status.trim().length() <=0 || cust.status.charAt(0) != 'Y')
        {
        	new MessageBox("该顾客卡已失效!");
        	return false;
        }
        
        // 记录当前顾客卡
        curCustomer = cust;
        
    	// 记录到小票        	
    	saleHead.hykh = cust.code;
    	saleHead.hytype = cust.type;
    	saleHead.str4 = cust.valstr2;
    	saleHead.hymaxdate = cust.maxdate;
    	
/*    	// 重算所有商品应收
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		calcGoodsYsje(i);
    	}
    	
        // 计算小票应收
        calcHeadYsje();*/
        
        return true;
    }
	
	
	//CRM
	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null) return;

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
		if (((Sxtx_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid,
																	saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
			goodsDef.num4 = popDef.num2;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
			goodsDef.num4 = 1;
		}
	}
	
	//CRM
	public void calcGoodsVIPRebate(int index)
	{
		if (!Sxtx_CustomLocalize.crmMode())
		{
			super.calcGoodsVIPRebate(index);
			return ;
		}
		
		boolean zszflag = true;
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);
		
		if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'H')
		{
			double je = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)));
			double hyj = 0;
			if (goodsDef.pophyj != 0)
			{
				hyj = goodsDef.pophyj;
			}

			if (goodsDef.hyj != 0)
			{
				if (hyj == 0) hyj = goodsDef.hyj;
				else hyj = Math.min(hyj, goodsDef.hyj);
			}

			if (hyj != 0 && je > ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl))
			{
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(je - ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl));
				saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
			}

		}
		//存在会员卡， 商品允许VIP折扣， CRM促销单允许享用VIP折扣
		else if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'Y'  && curCustomer.iszk == 'Y')
		{
			// 获取VIP折扣率定义
			calcVIPZK(index);

			// 折上折标志
			zszflag = zszflag && (goodsDef.num4 == 1);

			// 不计算会员卡折扣
			if (goodsDef.hyj == 1) return;

			// vipzk1 = 输入商品时计算商品VIP折扣,原VIP折上折模式

				//有折扣,进行折上折
				if (getZZK(saleGoodsDef) >= 0.01 && goodsDef.hyj < 1.00)
				{
					// 需要折上折
					if (zszflag)
					{
						saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
					}
					else
					{
						// 商品不折上折时，取商品的hyj和综合折扣较低者
						if (ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), goodsDef.hyj * saleGoodsDef.hjje, 2) > 0)
						{
							double zke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
							if (zke > getZZK(saleGoodsDef))
							{
								saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef), 2, 1);
							}
						}
					}
				}
				else
				{
					//无折扣,按商品缺省会员折扣打折
					saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
				}
			

			// 按价格精度计算折扣
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}

		getZZK(saleGoodsDef);

	}
	

	
	//CRM
//	会员授权
	public boolean memberGrant()
	{
		if (!Sxtx_CustomLocalize.crmMode())
		{
			return super.memberGrant();
		}
		
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

		String[] title = {"卡类型"};
		int[] width = {500};
		Vector v = new Vector();
		v.add(new String[]{"本门店卡"});
		v.add(new String[]{"手机号"});
		v.add(new String[]{"外门店卡"});
		int choice = new MutiSelectForm().open("请选择卡类型", title, width, v);
		
//		 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals("")) return false;
		CustomerDef cust = null;
		if (choice == 2)
		{
			// 查找会员卡
			cust = bs.findMemberCard(track2);
		}
		else
		{
			ProgressBox progress = new ProgressBox();
			try{
				if (choice == 1) track2 = "@"+track2;
				progress.setText("正在查询会员卡信息，请等待.....");
				cust = new CustomerDef();
				if (!((Sxtx_NetService)NetService.getDefault()).getCRMCust(cust, track2)) { return false; }
				saleHead.num2 = 1;
			}catch(Exception er)
			{
				er.printStackTrace();
			}
			finally
			{
				progress.close();
			}
		}
		
		if (cust == null) return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			return true;
		}
		else
		{
			// 记录会员卡
			return memberGrantFinish(cust);
		}
	}
	
//	 获取退货小票信息
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

				pb.close();
				pb = null;

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
					row[6] = "Y";
					row[7] = row[3];
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
				int cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);

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
	
	public void enterInputCODE()
	{
		if (((Label) clist.get("lbl_barcode")).getText().equals("商品金额"))
		{
			if (saleGoods.size() > 0)
			{
				GoodsDef lastgoods = (GoodsDef) goodsAssistant.lastElement();
				if (inGoods.barcode.equals(lastgoods.barcode))
				{
					instr_price = ((Text) clist.get("code")).getText();
					SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleGoods.lastElement();
					double price = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(instr_price), lastgoods), 2, 1);
					
					saleGoodsDef.jg = price;
					saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
					clearGoodsGrantRebate((saleGoods.size() - 1));
					
					if (lastgoods != null) calcGoodsYsje((saleGoods.size() - 1));
	
					// 重算小票应收  
					calcHeadYsje();
					
					((Label) clist.get("lbl_barcode")).setText("商品编码");
					((Label) clist.get("lbl_barcode")).setForeground(SWTResourceManager.getColor(0, 0, 255));
					getSaleGoodsDisplay();
					if (saleEvent != null) 
					{
						saleEvent.setCurGoodsBigInfo();
						saleEvent.setTotalInfo();
					}
					
					((Text) clist.get("code")).setText("");
					return ;
				}
				
			}
			((Label) clist.get("lbl_barcode")).setText("商品编码");
			((Label) clist.get("lbl_barcode")).setForeground(SWTResourceManager.getColor(0, 0, 255));
		}
		super.enterInputCODE();
	}
	
	public void paySell()
	{
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleGoods.elementAt(i);
			if (saleGoodsDef.jg == 0.01)
			{
				new MessageBox("商品零售价为0.01，不允许成交");
				return;
			}
		}
		//无论是否刷会员卡，都要提示是否积分
		if (new MessageBox("此单小票是否积分?",null,true).verify() == GlobalVar.Key1)
		{
			saleHead.str1 = "1";
		}
		else
		{
			saleHead.str1 = "0";
		}
		
		super.paySell();
	}
	
	private boolean getGoodsIsJFXF(GoodsDef goods, SpareInfoDef info)
	{
    	if (GlobalInfo.isOnline)
    	{
    		Sxtx_NetService netservice = (Sxtx_NetService)NetService.getDefault();
			return netservice.getGoodsIsJFXF(saleHead,goods, info, NetService.getDefault().getMemCardHttp(131));
    	}
    	else
    	{
    		return false;
    	}
	}
	
    public void addSaleGoodsObject(SaleGoodsDef sg,GoodsDef goods,SpareInfoDef info)
    {
        saleGoods.add(sg);
        goodsAssistant.add(goods);
        goodsSpare.add(info);
    	
        // goods不为空才是销售的商品,查找商品对应收款规则
        if ((GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A') && goods != null && info != null)
        {
        	info.payrule = DataService.getDefault().getGoodsPayRule(goods);
        	
        	if (info.payrule != null)
        	{
	        	for (int i = 0;i < info.payrule.size();i++)
	        	{
	        		PayRuleDef pr = (PayRuleDef)info.payrule.elementAt(i);
	
	        		sg.str6 = (sg.str6 == null?"":sg.str6) + pr.paycode + ":" + ManipulatePrecision.doubleToString(pr.payje) + ",";
	        	}
	        	
	        	if (sg.str6 != null && sg.str6.length() > 0)
	        	{
	        		sg.str6 = sg.str6.substring(0,sg.str6.length()-1);
	        	}
        	}
        }
        

		// goods不为空才是销售的商品,查找商品对应促销情况
		if (goods != null && info != null)
			findGoodsCMPOPInfo(sg, goods, info);
		else
			goodsCmPop.add(null);
        
		if (goods != null && info != null &&Sxtx_CustomLocalize.crmMode())
		{
			getGoodsIsJFXF(goods,info);
		}
		
		// 是否自动进行积分换购
        if (GlobalInfo.sysPara.autojfexchange == 'Y') 
        {
        	// 换货状态不允许使用积分换购
        	if (hhflag == 'Y')
        	{
        		return ;
        	}
        	
        	// 没有刷会员卡不允许积分换购
        	if (curCustomer == null)
        	{
        		return;
        	}

        	// 无0509付款方式,不能进行积分换购
    		PayModeDef paymode = DataService.getDefault().searchPayMode("0509");
    		if (paymode == null) 
    		{
    			return;
    		}
    		
    		
        	NewKeyListener.sendKey(GlobalVar.JfExchange);
        }
    }
    
	public void printSaleBill()
	{
		// 打印小票前先查询满赠信息并设置到打印模板供打印
		if (!SellType.ISEXERCISE(saletype))
		{
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(saleHead, saleGoods, salePayment);
			SaleBillMode.getDefault(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
		}

		// 恢复暂停状态的实时打印
		stopRealTimePrint(false);

		// 实时打印只打印剩余部分
		if (isRealTimePrint())
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++) realTimePrintGoods(null, i);

			// 打印即扫即打剩余小票部分
			SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

			//
			setHaveRealTimePrint(false);
		}
		else
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 打印整张小票
			SaleBillMode.getDefault(saleHead.djlb).printBill();
		}
		
		// 只在交易完成时打印一次移动离线充值券,因此无需放到小票模板中
		if (GlobalInfo.useMobileCharge)
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (pay != null) pay.printOfflineChargeBill(saleHead.fphm);
		}
	}
	
	public void customerIsHy(CustomerDef cust)
	{
		saleHead.hykh = cust.code;
		saleHead.hytype = cust.type;

		//刷完卡后，不重新计算以前的会员折扣，刷会员卡后的折扣继续计算
		/**
		if (cust.ishy == 'Y' || cust.ishy == 'V' || cust.ishy == 'H')
		{
			// 重算所有商品应收
			for (int i = 0; i < saleGoods.size(); i++)
			{
				calcGoodsYsje(i);
			}

			// 计算小票应收
			calcHeadYsje();
		}
		*/
	}
	
    public void setGoodsVIPRebateInfo(SaleGoodsDef sgd ,CustomerVipZklDef zklDef)
    {
    	sgd.hydjbh = String.valueOf(zklDef.seqno);
    	sgd.hyzkfd = Convert.toDouble(zklDef.memo);
    }
    
    public String getVipInfoLabel()
    {
    	if (curCustomer == null)
    		return "";
    	else 
    	{
    		if (SellType.ISSALE(saletype))
    			return "[" + curCustomer.code + "]" + curCustomer.valuememo;
    		else
    			return "[" + curCustomer.code + "]";
    	}
    }
    
    public boolean doRefundEvent()
    {	
    	if (!SellType.ISBACK(saletype)) return true;
  	
    	if (GlobalInfo.sysPara.refundByPos == 'N') return true;
    	
    	if (!GlobalInfo.isOnline)
    	{
    		if (isNewUseSpecifyTicketBack())
    		{
	    		new MessageBox("必须在联网状态下检查退货扣回！");
	    		return false;
    		}
    		else
    		{
    			return true;
    		}
    	}
    	
    	//isRefundPayStatus = true;
    	//String ss = null;
    	//if (ss.equals("AA")) return true;
    	
    	// 清除扣回付款集合
    	if (refundPayment == null) refundPayment = new Vector();
    	else refundPayment.clear();
    	if (refundAssistant == null) refundAssistant = new Vector();
    	else refundAssistant.clear();

    	// 获取需要扣回的金额 
    	ProgressBox pb = new ProgressBox();
    	char bc = saleHead.bc;
    	try
    	{
    		saleHead.bc = '#';
	    	// 发送当前退货小票到后台数据库
    		pb.setText("正在发送退货小票用于计算扣回金额......");
	        if (!this.saleEvent.saleBS.saleSummary())
	        {
	            new MessageBox("交易数据汇总失败!");
	        	
	        	return false;
	        }
	        if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
	        {
	            new MessageBox("交易数据校验错误!");
	
	            return false;
	        }
	        
	        // 发送当前退货小票以计算扣回
        	// jdfhdd标记当前发送的是用于计算扣回的小票信息
        	String oldfhdd = saleHead.jdfhdd;
        	saleHead.jdfhdd = "KHINV";	        
	        if (GlobalInfo.sysPara.refundByPos == 'B')
	        {
		    	if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, salePayment, null) != 0)
				{
		    		saleHead.jdfhdd = oldfhdd;
		    		return false;
				}
	        }
	        else
	        {
	        	// = 'Y',扣回在付款前进行处理，生成缺省付款便于发送小票
	        	Vector tempPay = new Vector();
	        	SalePayDef tempsp = new SalePayDef();
	        	tempsp.syjh = saleHead.syjh;				
	        	tempsp.fphm = saleHead.fphm;	
	        	tempsp.rowno= 1;
	        	tempsp.flag = '1';
	        	tempsp.paycode = "KHFK";
	        	tempsp.payname = "扣回虚拟付款";
	        	tempsp.ybje = saleHead.ysje;
	        	tempsp.hl = 1;
	        	tempsp.je = saleHead.ysje;
	        	tempPay.add(tempsp);
	        	if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, tempPay, null) != 0)
				{
		    		saleHead.jdfhdd = oldfhdd;
		    		return false;
				}
	        }
	        
	        
	        saleHead.jdfhdd = oldfhdd;
	        
	    	// 调用后台过程返回需要扣回的金额
	    	pb.setText("正在获取退货小票的扣回金额......");
	    	RefundMoneyDef rmd = new RefundMoneyDef();
	    	if (!NetService.getDefault().getRefundMoney(saleHead.mkt,saleHead.syjh,saleHead.fphm,rmd))
			{
	    		return false;
			}
	    	
	    	// 关闭提示
    		if (pb != null)
    		{
	    		pb.close();
	    		pb = null;
    		}
    		
    		// 存在家电下乡返款扣回，不允许退货
    		if (rmd.jdxxfkje > 0) 
    		{
    			new MessageBox("该退货小票存在家电下乡返款\n请退返款之后再进行退货交易");
    			return false;
    		}
    		
	    	// 无扣回金额,不用输入
	    	refundTotal = rmd.jfkhje + rmd.fqkhje + rmd.qtkhje;
	    	
	    	// 员工缴费和结算单如果存在扣回，不允许通过
	    	if ((SellType.isJF(saletype) || SellType.isJS(saletype)) && Math.abs(refundTotal) > 0)
	    	{
	    		new MessageBox("员工缴费 或 结算单 不允许存在扣回\n");
	    		return false;
	    	}
	    	
	    	//liwj test
	    	/*refundTotal = 1;*/
	    	if (refundTotal <= 0) return true;
	    	
	    	StringBuffer s = new StringBuffer();
    		s.append("该退货小票总共需要扣回 " + ManipulatePrecision.doubleToString(refundTotal) + " 元\n\n");
	    	if ((SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype)) && SellType.ISBACK(saletype))
	    	{
	    		if (refundlist == null ) refundlist = new Vector();
	    		else refundlist.removeAllElements();
	    		
	    		String[] rows = rmd.qtdesc.split("\\|");
	    		for (int i = 0 ; i < rows.length; i++)
	    		{
	    			String row[] = rows[i].split(",");
	    			refundlist.add(row);
	    			s.append(Convert.appendStringSize("", row[1], 0, 15, 10)+" :"+Convert.increaseCharForward(row[2],10)+"\n");
	    		}
	    	}
	    	else {
		    	if (rmd.jfdesc.length() > 0) s.append(rmd.jfdesc + "\n");
		    	else if (rmd.jfkhje > 0) s.append("其中因为积分原因需扣回 " + ManipulatePrecision.doubleToString(rmd.jfkhje) + " 元\n");
		    	if (rmd.fqdesc.length() > 0) s.append(rmd.fqdesc + "\n");
		    	else if (rmd.fqkhje > 0) s.append("其中因为返券原因需扣回 " + ManipulatePrecision.doubleToString(rmd.fqkhje) + " 元\n");
		    	if (rmd.qtdesc.length() > 0) s.append(rmd.qtdesc + "\n");
		    	else if (rmd.qtkhje > 0) s.append("其中因为其他原因需扣回 " + ManipulatePrecision.doubleToString(rmd.qtkhje) + " 元\n");
	    	}
	    	// 有扣回不允许退货
	    	if (GlobalInfo.sysPara.refundAllowBack != 'Y' && refundTotal > 0)
	    	{
	    		s.append("\n扣回金额大于0,不能进行退货\n");
	    		refundMessageBox(s.toString());
	    		
	    		return false;
	    	}
	    	
	    	refundMessageBox(s.toString());
    	}
    	catch(Exception er)
    	{
    		er.printStackTrace();
    	}
    	finally
    	{
    		saleHead.bc = bc;
    		if (pb != null)
    		{
	    		pb.close();
	    		pb = null;
    		}
    	}
    	
    	// 标记扣回开始
    	refundFinish = false;
    	isRefundPayStatus = true;
    	
    	// 打开扣回付款输入窗口
    	new SalePayForm().open(saleEvent.saleBS,true);
    	

    	
    	isRefundPayStatus = false;
	    return refundFinish;
    }
}
