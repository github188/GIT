package custom.localize.Jdhx;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Jdhx.Jdhx_WaterFee.FeeHeadDef;

public class Jdhx_SaleBS extends Jdhx_SaleBS1Goods
{
	public void enterInput()
	{
		if (SellType.isJF(saletype))
		{
			if (saleGoods.size() > 0)
			{
				new MessageBox("请完成当前交易!");
				return;
			}
			saleEvent.code.setText("");
			if (Jdhx_WaterFee.getDefault().execute(saleHead, "01", null, null))
				addWaterFeeVirtualGoods();
			
			return;
		}
		else if (SellType.isGroupbuy(saletype))
		{
			new MessageBox("团购交易不允许新增商品!");
			return;
		}

		super.enterInput();
	}

	public boolean memberGrant()
	{
		if (SellType.isJF(saletype))
		{
			if (saleGoods.size() > 0)
			{
				new MessageBox("请完成当前交易!");
				return true;
			}

			if (Jdhx_WaterFee.getDefault().execute(saleHead, "01", null, null))
				addWaterFeeVirtualGoods();

			return true;
		}
		return super.memberGrant();
	}

	public boolean memberGrantFinish(CustomerDef cust)
	{
		// 记录当前顾客卡
		curCustomer = cust;

		// 具有积分功能
		customerIsJf(cust);
		
		//客户要求，在销售状态，未录入商品，刷会员卡后 ，如果发现会员卡和水费绑定， 则自动查询水费金额，发现有欠水费，提醒用户缴纳水费
		if (GlobalInfo.sysPara.isWater != null && GlobalInfo.sysPara.isWater.equals("Y") && SellType.RETAIL_SALE.equals(saletype) && saleGoods.size() <= 0 
				&&  cust.str1 != null && !cust.str1.equals("") )
		{
			saleHead.hykh = cust.code;
			saleHead.hykname = cust.name;
			saleHead.str3 = cust.str1;
			
			SaleHeadDef tmpHead = null;
			
			if (Jdhx_WaterFee.getDefault().execute(saleHead, "01", null, null))
			{
		        if (checkAllowInit())
		        {
                //	对initOperation() 进行了客户化，为了避免客户化的影响，
		        //  这里将 initOneSale(SellType.JF_FK); 方法拆开
                //	initOneSale(SellType.JF_FK);
		        	
		        	tmpHead = saleHead;
		    		if (!initGui(SellType.JF_FK))
		    			return false;

		    		//原小票头对象和水费有关信息赋给新的小票头对象
		            saleHead.hykh = tmpHead.hykh;
		            saleHead.hykname = tmpHead.hykname;
		            saleHead.str3 = tmpHead.str3;
		            saleHead.num5 = tmpHead.num5;
//					saleHead.memo = 


		            
		    		initBackSell();
		    		initBusiness();
		    		
//					saleHead.memo = 
//					saleHead.str3 = head.yhh; // 用于绑定会员卡
//					saleHead.num5 = FeeHeadDef.XFL; // 记录水量
		    				
//					saleHead.hykh = cust.code;
//					saleHead.hykname = cust.name;
//					saleHead.str3 = cust.str1;
		            
		            
		    		initOperation();
		            

		            addWaterFeeVirtualGoods();
		            
		            return true;
		        }				
			}
		}

		// 具有会员功能
		customerIsHy(cust);

		// 具有折扣功能
		customerIsZk(cust);

		return true;
	}
	
	protected void initOperation()
	{
		if (SellType.isJF(saletype) && (saleHead.str3 == null || saleHead.str3.trim().equals("")))
		{
			if (SellType.ISBACK(saletype))
			{
				if (thFphm == 0)
				{
					djlbBackToSale();
					initOneSale(saletype);
					return;
				}

				StringBuffer sb = new StringBuffer();
				TextBox txt = new TextBox();
				do
				{
					if (!txt.open("请输入水费用户号或刷会员卡", "水费冲正", "系统需要校验用户有效性", sb, 0, 0, false, TextBox.MsrKeyInput))
					{
						super.djlbBackToSale();
						super.initOneSale(saletype);
						return;
					}

					if (txt.Track2 == null || txt.Track2.trim().equals(""))
						continue;

					if (txt.Track2.length() > 9 || txt.Track2.length() == 11)
					{
						CustomerDef cust = new CustomerDef();
						if (!DataService.getDefault().getCustomer(cust, txt.Track2))
							continue;

						// 校验用户帐号有效性
						if (cust.str1 != null && !cust.str1.trim().equals("") && cust.str1.equals(saleHead.str3))
							break;
					}
					else
					{
						if (txt.Track2.length() >0 && txt.Track2.length() < 9)
						{
							txt.Track2 = Convert.increaseCharForward(txt.Track2, '0', 8);
						}
						if (txt.Track2.equals(saleHead.str3))
							break;

						new MessageBox("水费用户号错误!");

					}
				}
				while (true);
			}
			else
			{
				if ( (saleHead.str3 == null || saleHead.str3.trim().equals("") )
						&& Jdhx_WaterFee.getDefault().execute(saleHead, "01", null, null ))
				{
					addWaterFeeVirtualGoods();
				}
			}
			return;
		}

		super.initOperation();
	}

	protected void addWaterFeeVirtualGoods()
	{
//		StringBuffer sb = new StringBuffer();
//		TextBox txt = new TextBox();
//
//		if (saleHead.hykh != null && !saleHead.hykh.equals(""))
//		{
//			curCustomer = new CustomerDef();
//			curCustomer.code = saleHead.hykh;
//			curCustomer.name = saleHead.hykname;
//			saleEvent.setVIPInfo(getVipInfoLabel());
//		}
//
//		do
//		{
//			sb.append(Jdhx_WaterFee.getDefault().getFeeHead().sjje + "");
//			if (!txt.open("请输入待缴水费金额", "水费金额", "请输入待缴水费金额", sb, 0, 99999999, false, 0))
//			{
//				saleHead.hykh = "";
//				curCustomer = null;
//				saleEvent.setVIPInfo("");
//				return;
//			}
//
//			if (sb.toString().trim().equals(""))
//				continue;
//
//			break;
//		}
//		while (true);

		GoodsDef goodsDef = new GoodsDef();
		goodsDef.barcode = goodsDef.code = "000000";

		goodsDef.gz = "00";
		goodsDef.uid = "00";
		goodsDef.name = "水费"+saleHead.num5 +"吨";
//		goodsDef.lsj = ManipulatePrecision.doubleConvert(Convert.toDouble(sb.toString()));
		goodsDef.lsj = ManipulatePrecision.doubleConvert(Jdhx_WaterFee.getDefault().getFeeHead().sjje);
		goodsDef.unit = "笔";
		goodsDef.type = 1;
		goodsDef.issqkzk = 'N';
		goodsDef.isvipzk = 'N';

		SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, saleEvent.yyyh.getText(), 1, goodsDef.lsj, 0, false);

		addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));
		getZZK(saleGoodsDef);

		calcHeadYsje();

		refreshSaleForm();
		saleEvent.updateSaleGUI();
		//发送付款按钮事件
		NewKeyListener.sendKey(GlobalVar.Pay);
	}

	public boolean findBackTicketInfo()
	{

		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if (GlobalInfo.sysPara.inputydoc == 'D')
			{
				// 只记录原单小票号和款机号,但不按原单找商品
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

				// str1控制是还是可以重复退货 检查此小票是否已经退货过

				if (thsaleHead.str1 != null && thsaleHead.str1.trim().equals("Y"))
				{
					if (GlobalInfo.sysPara.backticketctrl == 'Y')
					{
						new MessageBox("该小票已退货,不允许重复退货!");
						return false;
					}

					if (new MessageBox("该小票已退过货,是否继续退货?", null, true).verify() != GlobalVar.Key1)
						return false;
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
					sgd.sl = sgd.sl - sgd.num1;
					if (sgd.sl < 0)
						sgd.sl = 0;

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
					cho = EBill.getDefault().getChoice(choice);
				}
				else
				{
					// 选择要退货的商品
					cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open("开单营业员号：", "", "请输入有效开单营业员号", backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox("该工号不是营业员!", null, false);
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					else
					{
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
					sgd.hydjbh = sgd.hydjbh;

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

				// 取回水费冲正时需要用的缴费信息信息,上月节余和本月节余, 用水量
				saleHead.memo = thsaleHead.memo; //过程返回的是小票编号
				saleHead.str3 = thsaleHead.str3;
				saleHead.str4 = thsaleHead.str4; //记录员原小票头，方便取用户信息
				saleHead.num3 = thsaleHead.num3;
				saleHead.num4 = thsaleHead.num4;
				saleHead.num5 = thsaleHead.num5;

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

	public boolean getPayModeByNeed(PayModeDef paymode)
	{
		// 0601 团购赊账付款方式，只有在团购时可以使用
		if (!paymode.code.equals("0601"))
		{
			return true;
		}
		else if (!SellType.isGroupbuy(saletype) && paymode.code.equals("0601"))
		{
			return false;
		}
		else if (SellType.isGroupbuy(saletype) && paymode.code.equals("0601"))
		{
			return true;
		}
		
		return false;
	}
	
//	 添加团购赊账付款信息  团购赊账付款编码固定位 0601
//	public void addMemoPayment()
//	{
//		super.addMemoPayment();
//		if (SellType.GROUPBUY_SALE.equals(saleHead.djlb))
//		if (SellType.GROUPBUY_SALE.equals(saletype))
//		{
//			for (int i = 0; i < memoPayment.size(); i++)
//			{
//				Payment pay = (Payment) memoPayment.elementAt(i);
//
//				if (pay.salepay.paycode.equals("0601") && pay.salepay != null)
//					addSalePayObject(pay.salepay, pay);
//			}
//		}
//		
//	}
	
	
	// 缴纳水费 时，不打印小票头
    public boolean getSaleGoodsDisplay()
    {
    	SaleGoodsDef goodsDef1, goodsDef2;

    	// 设置正在销售状态
    	if (SellType.ISCHECKINPUT(this.saletype))
    	{
    		GlobalInfo.syjStatus.status = StatusType.STATUS_CHECK;
    	}
    	else if (saleGoods.size() > 0)
    	{
        	GlobalInfo.syjStatus.status = StatusType.STATUS_SALEING;
    	}
    	else
    	{
    		GlobalInfo.syjStatus.status = StatusType.STATUS_LOGIN;
    	}
    	
    	// 检查已显示的信息是否需要更新
		for (int i=0;i<Math.min(lastGoodsDetail.size(),saleGoods.size());i++)
		{
			goodsDef1 = (SaleGoodsDef)saleGoods.elementAt(i);
			goodsDef2 = (SaleGoodsDef)lastGoodsDetail.elementAt(i);
			
			// 只有进行单品折扣 零时折扣额  零时折扣额 时才重复 打印
			if (!goodsDef1.code.equals(goodsDef2.code) ||(goodsDef1.hjje - goodsDef1.hjzk) != (goodsDef2.hjje - goodsDef2.hjzk) || (goodsDef1.lszke  != goodsDef2.lszke) || ManipulatePrecision.doubleCompare(goodsDef1.sl,goodsDef2.sl,4) != 0 || !goodsDef1.unit.equals(goodsDef2.unit) || (SellType.ISCHECKINPUT(saletype) && GlobalInfo.sysPara.isblankcheckgoods == 'B' && !goodsDef1.name.equals(goodsDef2.name)))
			{
				//修改已经改变的商品明细
				saleEvent.table.modifyRow(rowInfo(goodsDef1),i);
				
				//商品附加处理
				doSaleGoodsDisplayEvent(goodsDef2,i);
				
				//江都宏信要求及时打印时 商品手工设置总折扣后，折扣额体现在总折扣了就行，不需要把所有折扣改变的商品进行重复打印 ，只有单品折扣改变是才重新打印
				if (goodsDef1.lszre  != goodsDef2.lszre || ManipulatePrecision.doubleCompare(goodsDef1.sl,goodsDef2.sl,4) != 0 ) // || (goodsDef1.hjje - goodsDef1.lszzk - goodsDef1.lszzr) !=  (goodsDef2.hjje - goodsDef2.lszzk - goodsDef2.lszzr)) 
				{
					//即扫即打被改变的商品
					realTimePrintGoods(goodsDef2,i);					
				}
			}
		}
    	
		// 检查是否有新的商品需要添加
		if (saleGoods.size() > lastGoodsDetail.size())
		{   
			for (int i=lastGoodsDetail.size();i<saleGoods.size();i++)
			{
				goodsDef1 = (SaleGoodsDef)saleGoods.elementAt(i);
				
				// 刷新明细显示
				saleEvent.updateTable(rowInfo(goodsDef1));	
				
				// 商品附加处理
				doSaleGoodsDisplayEvent(null,i);
			}
			

		}
		else
		{
			// 当前商品列表行数减少，从列表删除多余行
			if (saleGoods.size() < lastGoodsDetail.size())
			{
				for (int i=lastGoodsDetail.size()-1;i>=saleGoods.size();i--)
				{
					goodsDef1 = (SaleGoodsDef)lastGoodsDetail.elementAt(i);
					
					// 删除多余的商品
					getDeleteGoodsDisplay(i,goodsDef1);
				}
			}
		}
		
		doSaleGoodsDisplayFinishedEvent();
		
		// 盘点和买卷交易不实时打印
		//缴水费时，不打印交易头，水费用专门的小票打印模式
		if (!SellType.ISCHECKINPUT(saletype) && !SellType.isJF(saleHead.djlb))
		{
	        // 即扫即打状态，第一个商品前先打印交易头 
	    	if (lastGoodsDetail.size() <= 0 && saleGoods.size() > lastGoodsDetail.size())
	    	{
	            realTimePrintStartSale();
	    	}
	    	
	        // 当商品为第一次输入时,不需要打印但需要扩展打印标记
	    	if (saleGoods.size() == 1)
	    	{
	            realTimePrintGoods(null,-1);
	    	}
	    	else
	    	{
				// 即扫即打新增的商品,最后一个新增的商品不在本次打印,输入下一个商品时打印前一个商品
				for (int i = 0 ; i < saleGoods.size()-1; i++)
				{
					realTimePrintGoods(null,i);
				}
	    	}
		}
		
		//做判断，检查是否有跳号出现，如果跳号，刷新表
		if(saleEvent.table.getTableCount() != saleGoods.size())
		{
			lastGoodsDetail = new Vector();
			saleEvent.table.clear();
			getSaleGoodsDisplay();
		}
		
		// 备份本次商品列表明细
		lastGoodsDetail = null;
    	lastGoodsDetail = cloneSaleGoodsVector(saleGoods);
    	
        // 在要刷新商品列表时,写入断点数据
        writeBrokenData();
        
        return true;
    }

    // 江苏宏信要求在小票上打印促销信息，在此处获得促销信息
	public void doSaleFinshed(SaleHeadDef saleHead, Vector saleGoods, Vector salePayment)
	{
		super.doSaleFinshed(saleHead, saleGoods, salePayment);
		
		((Jdhx_DataService)DataService.getDefault()).getU51Info(saleHead.syjh, saleHead.mkt, saleHead);
	}
	
	public void printSaleBill()
	{
		if (SellType.isJF(saletype))
		{
			Jdhx_WaterFee.getDefault().printBill();
			return;
		}

		super.printSaleBill();
	}

	public void printSaleTicket(SaleHeadDef vsalehead, Vector vsalegoods, Vector vsalepay, boolean isRed)
	{
		try
		{
			if (SellType.isJF(vsalehead.djlb))
			{
				Jdhx_WaterFee.getDefault().head = new FeeHeadDef();
			    Jdhx_WaterFee.getDefault().head.yhh = vsalehead.str3 ;//vsalehead.str4.substring(0,5);
			    Jdhx_WaterFee.getDefault().head.xm = Convert.newSubString(vsalehead.str4, 18, 58);
			    Jdhx_WaterFee.getDefault().head.jylb = vsalehead.str4.substring(4,6);
			    Jdhx_WaterFee.getDefault().head.qfzje = Convert.toDouble(vsalehead.str4.substring(100,110));
			    Jdhx_WaterFee.getDefault().head.qfbs = Convert.toInt(vsalehead.str4.substring(98,100));
				Jdhx_WaterFee.getDefault().writeWaterFeeBill(Jdhx_WaterFee.getDefault().head, vsalehead, vsalegoods, vsalepay);
				Jdhx_WaterFee.getDefault().printBill();
				return;
			}
			
			super.printSaleTicket(vsalehead, vsalegoods, vsalepay, isRed);	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ;
	}
	

}
