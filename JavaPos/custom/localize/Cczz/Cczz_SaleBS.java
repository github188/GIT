package custom.localize.Cczz;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.DisplaySaleTicketForm;
import custom.localize.Bcrm.Bcrm_SaleBS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Cczz_SaleBS extends Bcrm_SaleBS
{
	public String syjh = "";
	public long fphm = 0L;

	public String getFuncMenuByPaying()
	{
		return "0008,0806,0808";
	}

	public String getSyyInfoLabel()
	{
		return GlobalInfo.posLogin.name;
	}

	public void exchangeSale()
	{
		if ((this.saletype.equals("1")) || (this.saletype.equals("4")))
		{
			char oldHhflag = this.hhflag;
			if (this.hhflag == 'N')
			{
				this.hhflag = 'Y';
			}

			String type = this.saletype;
			if ((this.saletype.equals("1")) && (this.hhflag == 'Y'))
			{
				type = "4";
			}

			if (this.saleEvent.saleform.setSaleType(type)) return;
			this.hhflag = oldHhflag;
		}
		else
		{
			new MessageBox("当前状态不能进行换货交易!");
		}
	}

	public boolean findGoods(String code, String yyyh, String gz)
	{
		if ((SellType.ISBACK(this.saletype)) && (this.hhflag == 'Y'))
		{
			new MessageBox("换退状态下必须输入原收银机号和原小票号");
			return false;
		}

		if (this.saleGoods.size() <= 0)
		{
			if ((SellType.ISSALE(this.saletype)) && (this.hhflag == 'Y')
					&& (!(((Cczz_DataService) DataService.getDefault()).getHHback(ConfigClass.CashRegisterCode, new StringBuffer()))))
			{
				new MessageBox("上笔不是【换退】，本笔不能为【换销】");
				this.hhflag = 'N';
				this.saleEvent.initOneSale("1");
				return false;
			}

			if (((Cczz_AccessDayDB) AccessDayDB.getDefault()).getHcHHbackinfo(ConfigClass.CashRegisterCode,
																				String.valueOf(GlobalInfo.syjStatus.fphm - 1L)) > 0L)
			{
				new MessageBox("上笔为【红冲换销】，必须先进行【红冲换退】才能进行其他操作");
				return false;
			}
		}

		return super.findGoods(code, yyyh, gz);
	}
	
	public boolean inputPrice(int index)
	{
		if (SellType.isJS(saletype)) { return false; }

		SaleGoodsDef oldGoodsDef = null;
		double newjg;
		String grantgh = null;

		// 检查是否允许输入价格
		if (!allowInputPrice(index)) { return false; }

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);

		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8)) return false;

		GoodsDef goodsDef = null;
		if (goodsAssistant != null && goodsAssistant.size() > index) goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 定价商品授权改价
		if (SellType.ISSALE(saletype) && !SellType.ISBATCH(saletype) && (goodsDef == null || goodsDef.lsj > 0) && curGrant.privgj != 'Y')
		{
			OperUserDef staff = inputPriceGrant(index);
			if (staff == null) return false;

			// 记录授权号
			grantgh = staff.gh;
		}

		// 输入价格
		StringBuffer buffer = new StringBuffer();
		newjg = saleGoodsDef.jg;
		do
		{
			buffer.delete(0, buffer.length());
			buffer.append(newjg);

			if (!new TextBox().open("请输入该商品价格", "价格", "", buffer, 0.01, getMaxSaleGoodsMoney(), true)) { return false; }

			newjg = Convert.toDouble(buffer.toString());
			AccessDayDB.getDefault().writeWorkLog("商品编码"+saleGoodsDef.code+"原价格"+saleGoodsDef.jg+"修改为"+newjg);
			// 输入价格按价格金额截取
			if (goodsDef != null) newjg = getConvertPrice(Double.parseDouble(buffer.toString()), goodsDef);
			newjg = ManipulatePrecision.doubleConvert(newjg, 2, 1);

			// 检查价格(P:配件;Z:赠品)
			if (goodsDef != null && goodsDef.type != 'P' && goodsDef.type != 'Z' && newjg <= 0)
			{
				new MessageBox("该商品价格必须大于0");
				continue;
			}

			// 最低限价
			if (goodsDef != null && newjg < goodsDef.maxzke)
			{
				new MessageBox("该项商品价格不能小于最低限价" + ManipulatePrecision.doubleToString(goodsDef.maxzke));
				continue;
			}

			//	是否允许在商品退货时,商品是否在下限和上限的价格之内
			if (!isAllowedBackPriceLimit(goodsDef, newjg)) continue;

			// 跳出循环
			break;
		} while (true);

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 重算商品应收
		if (goodsDef != null && goodsDef.lsj > 0) saleGoodsDef.flag = '6'; // 标记该商品被议价
		saleGoodsDef.jg = newjg;
//		saleGoodsDef.lsj = newjg;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		clearGoodsGrantRebate(index);

		if (goodsDef != null) calcGoodsYsje(index);

		// 重算小票应收  
		calcHeadYsje();

		// 价格过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox("商品价格过大,导致销售金额达到上限\n\n商品价格修改无效");

			// 恢复价格
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 价格过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox("商品价格过大,导致退货金额超过限额\n\n商品价格修改无效");

			// 恢复数量
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.str8 = "A";
				saleGoodsDef.name += "[修改]";
			}

			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}

		// 记录授权日志
		if (grantgh != null)
		{
			String log = "授权修改价格,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",原价:" + oldGoodsDef.jg + ",新价格:" + saleGoodsDef.jg + ",授权:"
					+ grantgh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		return true;
	}
	
	public OperUserDef inputRebateGrant(int index)
	{
		OperUserDef staff = null;
		SaleGoodsDef sgd = (SaleGoodsDef) this.saleGoods.elementAt(index);
		if (sgd.lsj == 0.0D)
		{
			OperUserDef staff1 = GlobalInfo.posLogin;
			staff = (OperUserDef) staff1.clone();
			staff.dpzkl = 0.1D;
		}
		else
		{
			staff = DataService.getDefault().personGrant();
		}
		
		if (staff!= null && (staff.dpzkl * 100.0D >= 100.0D))
		{
			new MessageBox("该员工授权卡无法授权单品打折");

			return null;
		}
		
		return staff;
		
		/*
		OperUserDef staff = DataService.getDefault().personGrant();

		if (staff == null)
		{
			SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);

			if (saleGoodsDef.lsj > 0.0D) { return null; }

			OperUserDef staff1 = GlobalInfo.posLogin;
			staff = (OperUserDef) staff1.clone();
			staff.dpzkl = 0.1D;
		}

		if (staff.dpzkl * 100.0D >= 100.0D)
		{
			new MessageBox("该员工授权卡无法授权单品打折");

			return null;
		}

		return staff;
		*/
    }
    
    //
    public void initOneSale(String type)
    {
    	syjh = "";
    	fphm = 0;
    	
    	// 换消需要输入原收银机号和原小票号
    	StringBuffer buff = new StringBuffer();
    	if (((Cczz_AccessDayDB)AccessDayDB.getDefault()).getlasthhbackinfo(ConfigClass.CashRegisterCode, buff))
    	{
    		syjh = ConfigClass.CashRegisterCode;
    		fphm = Convert.toLong(buff.toString());
    		if (!SellType.ISSALE(type) || hhflag != 'Y')
    		{
    			type = SellType.RETAIL_SALE;
    			hhflag = 'Y';
    			new MessageBox("上笔交易为换货退货，本笔必须为换货销售");
    		}
    	}
    	
    	super.initOneSale(type);
    	
    	if (((Cczz_AccessDayDB)AccessDayDB.getDefault()).getlasthhbackinfo(ConfigClass.CashRegisterCode, buff))
    	{
    		SaleHeadDef temp = new SaleHeadDef();
    		if (((Cczz_AccessDayDB)AccessDayDB.getDefault()).getlasthhbackHead(temp , ConfigClass.CashRegisterCode))
    		{
    			if (temp.hykh != null && temp.hykh.length() > 0)
    			{
	    			curCustomer = new CustomerDef(); 
	    			HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
	    			CustomerDef cust = bs.findMemberCard("!"+temp.hykh);
	    			if (cust != null)
	    			{
						curCustomer = cust;
						saleHead.hykh = cust.code;
						saleHead.hytype = cust.type;
						saleHead.str4 = cust.valstr2;
						saleEvent.setVIPInfo(getVipInfoLabel());
	    			}
    			}
    		}
    	}
    	
       	if (SellType.ISSALE(saleHead.djlb))
    	{
    		if (GlobalInfo.syjDef.priv == null || GlobalInfo.syjDef.priv.trim().length() == 0)
    		{
    			NewKeyListener.sendKey(GlobalVar.MemberGrant);
    		}
    	}
	}

	public void backSell()
	{
		if (GlobalInfo.syjDef.isth != 'Y')
		{
			new MessageBox("该收银机不允许退货!");

			return;
		}

		if (SellType.ISCOUPON(this.saleHead.djlb) || SellType.ISJFSALE(this.saleHead.djlb))
		{
			super.backSell();
		}
		else
		{
			new DisplaySaleTicketForm(112);
		}
	}

	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] rowInfo = super.rowInfo(goodsDef);
		if (!(SellType.ISCHECKINPUT(this.saletype)))
		{
			rowInfo[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk);
		}

		return rowInfo;
	}

	public void addMemoPayment()
	{
		super.addMemoPayment();
		try
		{
			Vector payDetail = new Vector();
			StringBuffer buff = new StringBuffer();
			if (!(((Cczz_AccessDayDB) AccessDayDB.getDefault()).gethhbackpay(ConfigClass.CashRegisterCode, buff, payDetail))) return;
			boolean append = true;

			for (int j = 0; j < this.salePayment.size(); ++j)
			{
				SalePayDef def1 = (SalePayDef) this.salePayment.elementAt(j);
				if (!(def1.paycode.equals("0710"))) continue;
				append = false;
				break;
			}

			if (!(append)) return;
			for (int i = 0; i < payDetail.size(); ++i)
			{
				SalePayDef def = (SalePayDef) payDetail.elementAt(i);
				PayModeDef pmd = DataService.getDefault().searchPayMode(def.paycode);
				Payment pay = CreatePayment.getDefault().createPaymentByPayMode(pmd, this);
				pay.salepay = def;
				addSalePayObject(pay.salepay, pay);
			}

		}
		catch (Exception er)
		{
			er.printStackTrace();
		}
	}

	public boolean getBackSellStatus()
	{
		return false;
	}

	public boolean operPermission(int type, OperRoleDef oper)
	{
		switch (type)
		{
			case 1:
				if ((this.curGrant.privqx == 'Y') && (GlobalInfo.syjDef.issryyy != 'N')) break;
				return true;
		}

		return super.operPermission(type, oper);
	}

	public boolean memberGrantFinish(CustomerDef cust)
	{
		this.saleHead.num4 = Convert.toDouble(cust.str1);
		this.saleHead.hykname = cust.name;
		return super.memberGrantFinish(cust);
	}

	public void execCustomKey2(boolean keydownonsale)
	{
		if (this.curCustomer != null)
		{
			new MessageBox("请'取消'整单后，再进行返券卡查询");
			return;
		}
		FjkInfoQueryBS fjkbs = CustomLocalize.getDefault().createFjkInfoQueryBS();
		fjkbs.QueryFjkInfo();
	}

    public boolean allowInputPrice(int index)
    {
    	if (!super.allowInputPrice(index)) return false;
    	
        GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		if (SellType.ISSALE(saletype) && !SellType.ISBATCH(saletype) && goodsDef.lsj > 0 && curGrant.privgj != 'Y')
		{
			return false;
		}
    	 
    	return true;
    }

	public boolean memberGrant()
	{
		if ((this.saletype.equals("1")) && (this.hhflag == 'Y')) { return false; }
		return super.memberGrant();
	}

	public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if ((this.saletype.equals("1")) && (this.hhflag == 'Y'))
		{
			sg.yfphm = this.fphm;
			sg.ysyjh = this.syjh;
		}

		super.addSaleGoodsObject(sg, goods, info);

		if ((this.saleHead.str1 == null) || (this.saleHead.str1.length() <= 0) || (this.saleHead.str1.charAt(0) != 'Y')) return;
		sg.name = "(预)" + sg.name;
	}

	public void execCustomKey4(boolean keydownonsale)
	{
		if (!(keydownonsale)) return;
		if ((this.saleHead.str1 != null) && (this.saleHead.str1.length() > 0) && (this.saleHead.str1.charAt(0) == 'Y')) return;

		if (this.saleGoods.size() > 0)
		{
			new MessageBox("清空现有商品后，才能将小票转换为预售模式");
			return;
		}

		if (new MessageBox("是否转换为预售模式", null, true).verify() != 2) return;
		this.saleHead.num2 = 1.0D;
		this.saleEvent.setTypeInfo();
		this.saleEvent.setBigInfo("", "", "", -1);
	}

	public void enterInputGZ()
	{
		if (this.saleEvent.gz.getText().length() <= 0)
		{
			new MessageBox("必须输入柜组代码");
			return;
		}
		super.enterInputGZ();
	}

    public boolean getReprintAuth()
    {
    	if (curGrant.privdy != 'Y')
    	{
    		OperUserDef staff = DataService.getDefault().personGrant("重打印权限授权");
    		if(staff == null) return false;
    		
    		if (staff.privdy == 'Y')
    		{
    			curGrant.privdy = 'L';
    			return true;
    		}
    		else
    		{
    			new MessageBox("当前工号没有重打上笔小票权限!");
    			return false;
    		}
    	}
    	
    	return true;
    }

	public boolean HHinit()
	{
		return ((this.hhflag == 'Y') && (SellType.ISSALE(this.saletype)));
	}

	public boolean exchangeSale(boolean ishhPay)
	{
		if ((this.hhflag == 'Y') && (!(SellType.ISSALE(this.saletype)))) return true;
		return (!(ishhPay));
	}

	public Vector customApportion(SalePayDef spay, Payment payobj)
	{
		Vector v = new Vector();
		double ft = ManipulatePrecision.doubleConvert(spay.je - spay.num1);
		for (int i = this.saleGoods.size() - 1; i >= 0; --i)
		{
			String[] row;
			SaleGoodsDef sg = (SaleGoodsDef) this.saleGoods.elementAt(i);
			SpareInfoDef spinfo = (SpareInfoDef) this.goodsSpare.elementAt(i);
			double ftje = getftje(spinfo);
			double maxfdje = sg.hjje - getZZK(sg) - ftje;

			if (maxfdje < ft)
			{
				row = new String[] {
									sg.barcode,
									sg.name,
									ManipulatePrecision.doubleToString(ftje),
									ManipulatePrecision.doubleToString(maxfdje),
									ManipulatePrecision.doubleToString(maxfdje),
									String.valueOf(i) };
				v.add(row);
				ft = ManipulatePrecision.doubleConvert(ft - maxfdje);
			}
			else
			{
				row = new String[] {
									sg.barcode,
									sg.name,
									ManipulatePrecision.doubleToString(ftje),
									ManipulatePrecision.doubleToString(maxfdje),
									ManipulatePrecision.doubleToString(ft),
									String.valueOf(i) };
				v.add(row);
				break;
			}

		}

		return v;
	}

	private boolean StringInArray(String[] array, String s)
	{
		for (int i = 0; i < array.length; ++i)
		{
			if (s.equals(array[i])) return true;
		}
		return false;
	}

	public boolean saleSummary()
	{
		if (!(super.saleSummary())) return false;

		if ((this.saleHead.djlb.equals("1")) && (GlobalInfo.sysPara.printpopbill == 'Y'))
		{
			for (int i = 0; i < this.saleGoods.size(); ++i)
			{
				SaleGoodsDef sg = (SaleGoodsDef) this.saleGoods.elementAt(i);
				SpareInfoDef spinfo = (SpareInfoDef) this.goodsSpare.elementAt(i);

				if (sg.xxtax == 0.0D)
				{
					continue;
				}

				double je = 0.0D;
				if (spinfo.payft == null) spinfo.payft = new Vector();
				for (int j = 0; j < spinfo.payft.size(); ++j)
				{
					String[] rows = (String[]) spinfo.payft.elementAt(j);
					PayModeDef def = DataService.getDefault().searchPayMode(rows[1]);

					if ((def.type != '5') && (!(def.code.equals("05"))) && (!(StringInArray(GlobalInfo.sysPara.mjPaymentRule.split(","), def.code)))) continue;
					je += Convert.toDouble(rows[3]);
				}

				sg.num3 = ManipulatePrecision.doubleConvert((sg.hjje - sg.hjzk - je) * sg.xxtax);
				if (sg.num3 >= 0.0D) continue;
				sg.num3 = 0.0D;
			}

		}

		if ((this.curCustomer != null) && (this.curCustomer.value5 == 1.0D))
		{
			this.saleHead.buyerinfo = "N";
		}
		else
		{
			this.saleHead.buyerinfo = "Y";
		}

		return true;
	}

	public void selectAllCustomerInfo()
	{
	}

	public void paySell()
	{
		if ((((Cczz_DataService) DataService.getDefault()).getHHback(ConfigClass.CashRegisterCode, new StringBuffer()))
				&& (((!(SellType.ISSALE(this.saletype))) || (this.hhflag != 'Y'))))
		{
			this.saleEvent.initOneSale("1");
			return;
		}

		super.paySell();
	}

	public boolean paySellStart()
	{
		if (!(super.paySellStart())) return false;

		if ((SellType.ISSALE(this.saleHead.djlb)) && (this.hhflag == 'Y'))
		{
			double ysje = ((Cczz_AccessDayDB) AccessDayDB.getDefault()).gethhbackYsje(ConfigClass.CashRegisterCode,
																						String.valueOf(this.saleHead.fphm - 1L));
			if (this.saleHead.ysje < ysje)
			{
				new MessageBox("换消的商品总额必须大于换退的商品总额\n请继续输入其他商品");
				return false;
			}
		}
		return true;
	}

	public String getLastSjfk()
	{
		return ManipulatePrecision.doubleToString(this.lastsaleHead.sjfk + this.lastsaleHead.num3);
	}

	public double calcPayBalance()
	{
		double ye = super.calcPayBalance();

		double lcje = 0.0D;

		for (int i = 0; i < this.salePayment.size(); ++i)
		{
			SalePayDef spd = (SalePayDef) this.salePayment.elementAt(i);

			if (!(CreatePayment.getDefault().isPaymentLczc(spd))) continue;
			lcje = ManipulatePrecision.doubleConvert(lcje + Math.abs(spd.je));
		}

		this.saleHead.num3 = lcje;

		return ye;
	}

	public void findJfExchangeGoods(int index)
	{
		if ((this.curCustomer != null) && (this.curCustomer.status.indexOf("未实名") >= 0))
		{
			new MessageBox("请先到会员中心登记相关信息！");
			return;
		}
		super.findJfExchangeGoods(index);
	}

/*	public boolean checkCust()
	{
		if (this.curCustomer != null && this.curCustomer.status.indexOf("未实名") >= 0)
		{
			new MessageBox("请先到会员中心登记相关信息！");
			return false;
		}
		return true;
	}*/

	public boolean checkCust(MzkResultDef mrd)
	{
		if ((mrd != null) && (mrd.cardname != null) && (mrd.cardname.indexOf("未实名") >= 0))
		{
			new MessageBox("请先到会员中心登记相关信息!");
			return false;
		}
		return true;
	}

	public void initSetYYYGZ(String type, boolean iscsinput)
	{
		if ((GlobalInfo.syjDef.priv != null) && (GlobalInfo.syjDef.priv.trim().length() > 0))
		{
			this.saleEvent.yyyh.setText("美食城");
			this.saleEvent.gz.setText(GlobalInfo.syjDef.priv.trim());
			this.saleEvent.saleform.setFocus(this.saleEvent.code);
		}
		else
		{
			super.initSetYYYGZ(type, iscsinput);
		}
	}

	protected void initOperation()
	{
		if ((GlobalInfo.syjDef.priv != null) && (GlobalInfo.syjDef.priv.trim().length() > 0)) { return; }
		super.initOperation();
	}
	
//	删除商品
	public boolean deleteGoods(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if(saleGoodsDef.str9 !=null && saleGoodsDef.str9.length()> 0){
			if(!GlobalInfo.sysPara.isDel.equals("Y"))
			{
				new MessageBox("不允许删除电子开票商品！");
				return false;
			}
		}
		return super.deleteGoods(index);
	}
	
//	输入折扣
	public boolean inputRebate(int index){
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if(saleGoodsDef.str9 !=null){
			if(saleGoodsDef.str9.length()> 0){
				new MessageBox("专柜商品不允许折扣！");
				return false;
			}
		}
		return super.inputRebate(index);
	}
	
//	输入折让金额
	public boolean inputRebatePrice(int index){
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if(saleGoodsDef.str9 !=null){
			if(saleGoodsDef.str9.length()> 0){
				new MessageBox("专柜商品不允许折让！");
				return false;
			}
		}
		return super.inputRebatePrice(index);
	}
	
//	输入数量
	public boolean inputQuantity(int index, double quantity){
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		if(saleGoodsDef.str9 !=null){
			if(saleGoodsDef.str9.length()> 0){
				new MessageBox("专柜商品不允许修改数量！");
				return false;
			}
		}
		return super.inputQuantity(index,quantity);
	}
	
//	已经取消VIP卡或者折扣 true
	//否则 false
	protected boolean cancelMemberOrGoodsRebate(int index)
	{
		if ((this.saletype.equals(SellType.PREPARE_BACK)   || this.saletype.equals(SellType.PREPARE_TAKE ))) return false;
		if (isNewUseSpecifyTicketBack(false)) return false;
        
		// true=先打折，后刷VIP卡，因此取消时先取消VIP，再取消折扣
		// false=先VIP，再打折，因此取消时先取消折扣，再取消VIP
		if (memberAfterGoodsMode())
		{
			// 取消VIP
	    	if (checkMemberSale())
	    	{
	    		if (new MessageBox("已经刷了VIP卡,你确定要取消VIP卡吗?",null,true).verify()==GlobalVar.Key1)
	    		{
	    	        // 记录当前顾客卡
	    	        curCustomer = null;
	    	        
	    	    	// 记录到小票        	
	    	    	saleHead.hykh = null;
	    	    	
	    	    	// 重算所有商品应收
	    	    	for (int i=0;i<saleGoods.size();i++)
	    	    	{
	    	    		calcGoodsYsje(i);
	    	    	}
	    	    	
	    	        // 计算小票应收
	    	        calcHeadYsje();
	    	        saleEvent.updateSaleGUI();
	    		}
	    		return true;
	    	}
	    	
	    	// 取消临时折扣
			if (index >= 0)
			{
		    	SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		    	double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr); 
		    	if (sum > 0)
		    	{
			    	if (new MessageBox("【"+saleGoodsDef.name+"】存在临时折扣\n你确定要取消此商品的临时折扣吗?",null,true).verify()==GlobalVar.Key1)
			    	{
			    		saleGoodsDef.lszke = 0;
			    		saleGoodsDef.lszre = 0;
			    		saleGoodsDef.lszzk = 0;
			    		saleGoodsDef.lszzr = 0;
			    		
		    	        // 计算小票应收
			    		calcGoodsYsje(index);
		    	        calcHeadYsje();
		    	        saleEvent.updateSaleGUI();
			    	}
					return true;
		    	}
			}
		}
		else
		{
	    	// 取消临时折扣
			if (index >= 0)
			{
		    	SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		    	
		    	if (saleGoodsDef.str9 != null && saleGoodsDef.str9.length() >0 ) return false;
		    	
		    	double sum = ManipulatePrecision.doubleConvert(saleGoodsDef.lszke + saleGoodsDef.lszre + saleGoodsDef.lszzk + saleGoodsDef.lszzr); 
		    	if (sum > 0)
		    	{
			    	if (new MessageBox("【"+saleGoodsDef.name+"】存在临时折扣\n你确定要取消此商品的临时折扣吗?",null,true).verify()==GlobalVar.Key1)
			    	{
			    		saleGoodsDef.lszke = 0;
			    		saleGoodsDef.lszre = 0;
			    		saleGoodsDef.lszzk = 0;
			    		saleGoodsDef.lszzr = 0;
			    		
		    	        // 计算小票应收
			    		calcGoodsYsje(index);
		    	        calcHeadYsje();
		    	        saleEvent.updateSaleGUI();
			    	}
					return true;
		    	}
			}
			
			
			// 先刷卡状态下如果取消VIP必须取消整单
			/**
	    	if (checkMemberSale())
	    	{
	    		if (new MessageBox("已经刷了VIP卡,你确定要取消VIP卡吗?",null,true).verify()==GlobalVar.Key1)
	    		{
	    	        // 记录当前顾客卡
	    	        curCustomer = null;
	    	        
	    	    	// 记录到小票        	
	    	    	saleHead.hykh = null;
	    	    	saleHead.hytype = null;
	    	    	
	    	    	// 重算所有商品应收
	    	    	for (int i=0;i<saleGoods.size();i++)
	    	    	{
	    	    		calcGoodsYsje(i);
	    	    	}
	    	    	
	    	        // 计算小票应收
	    	        calcHeadYsje();
	    	        saleEvent.updateSaleGUI();
	    		}
	    		return true;
	    	}
	    	*/
		}
		
		// 返回false,执行基类取消交易的处理
    	return false;
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
			for (int i = 0; i < saleGoods.size(); i++)
				realTimePrintGoods(null, i);

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
			if (pay != null)
				pay.printOfflineChargeBill(saleHead.fphm);
		}
	}
	
	Vector hidePayCode = null; // 上传小票中需隐藏账号的代码
	public boolean payAccount(Payment pay, SalePayDef sp)
	{
		// 增加到付款集合
		if (sp != null || pay.alreadyAddSalePay)
		{
			if (sp != null)
			{
				//
				String payno = sp.payno;

				String code = sp.paycode;
				
				

				if (new File(GlobalVar.ConfigPath + "//HidePaycode.ini").exists())
				{
					if (hidePayCode == null)
						readHidePayCode();
					sp.payno = hidePayNo(code, payno);
				}
				
				
				// 付款覆盖模式,删除已有的付款
				if (GlobalInfo.sysPara.payover == 'Y')
				{
					int i = existPayment(sp.paycode, sp.payno, true);
					if (i >= 0)
					{
						// 不管已有的付款是否取消成功,都要把当前付款增加到已付款中
						deleteSalePay(i);
					}
				}

				// 增加已付款
				addSalePayObject(sp, pay);
			}

			// 计算剩余付款
			calcPayBalance();

			// 如果是需要循环输入的付款方式,则自动发送ENTER键再次进入付款
			loopInputPay(pay);

			return true;
		}

		return false;
	}
	
	public void readHidePayCode()
	{
		BufferedReader br = null;
		String line = null;
		br = CommonMethod.readFile(GlobalVar.ConfigPath + "//HidePaycode.ini");
		String[] value = null;
		if (hidePayCode == null)
			hidePayCode = new Vector();
		try
		{

			while ((line = br.readLine()) != null)
			{
				String temp = new String(line);
				value = temp.trim().split(";");
				if (value[0].trim().equals(""))
					continue;
				hidePayCode.add(value[0].trim());
			}

			br.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

	}
	
//	 隐藏付款账号
	public String hidePayNo(String code, String hp)
	{
		int len = hp.trim().length();
		String[] value = null;
		int begin = 0; // 显示前几位号
		int end = 0; // 显示后几位号
		String s1 = null;
		String s2 = null;

		for (int i = 0; i < hidePayCode.size(); i++)
		{
			String temp = (String) hidePayCode.get(i);

			value = temp.trim().split(",");

			if (value == null || value[0] == null || value.length < 4 || !value[3].equals("*"))
				continue;

			if (code.trim().equals(value[0].trim()))
			{
				begin = Integer.parseInt(value[1].trim());

				end = Integer.parseInt(value[2].trim());

				if (begin == 0 && end == 0)
					return hp;
				if (begin + end >= len)
					return hp;

				s1 = hp.trim().substring(0, begin);
				s2 = hp.trim().substring(len - end, len);

				for (int j = len - (begin + end); j > 0; j--)
				{
					s1 = s1 + "*";
				}
				hp = s1 + s2;
			}
		}
		return hp;
	}
}