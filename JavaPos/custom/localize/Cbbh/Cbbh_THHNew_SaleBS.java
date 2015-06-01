package custom.localize.Cbbh;

import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDosDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_SaleBS;

public class Cbbh_THHNew_SaleBS extends Bcrm_SaleBS
{

	protected String syjh = "";
	protected long fphm = 0L;
	protected Vector thhPayCheck = null;//退、换货付款方式检查
	protected boolean ispay = true;//是否按付款键
	
	public void exchangeSale()
	{		
		/*if(1==1)
		{
			new MessageBox("换货功能开发中。。。");
			return;
		}*/
		this.saleEvent.saleform.setSaleType(SellType.HH_SALE);
	}
	//true:不通过,false：通过
	public boolean checkIsSalePay(String code)
	{
		if(super.checkIsSalePay(code)) return true;

		//不能直接进行付款
		if (GlobalInfo.sysPara.noinputpaycode == null || GlobalInfo.sysPara.noinputpaycode.equals("") || GlobalInfo.sysPara.noinputpaycode.equals("0000")) return true;
		
		String paycodes[] = GlobalInfo.sysPara.noinputpaycode.split(",");
			
		for (int i = 0;i < paycodes.length;i++)
		{
			if (paycodes[i].equals(code))
			{
//				new MessageBox("当前 [" + paymode.code +"] 付款不能进行直接付款!");
				//new MessageBox(Language.apply("当前 [{0}] 付款不能进行直接付款!", new Object[]{code}));
				return true;
			}
		}
		
		return false;
	}
	
	public boolean payCompleteDoneEvent()
	{
		if(!super.payCompleteDoneEvent()) return false;
		
		//检查付款方式里必须存在付款方式及金额
		if(thhPayCheck!=null && thhPayCheck.size()>0)
		{
			SalePayDef pay;
			SalePayDef salePayDef;
			String msg="";
			boolean isOK = true;
			for(int i=0; i<thhPayCheck.size(); i++)
			{
				pay = (SalePayDef)thhPayCheck.elementAt(i);
				if(pay==null) continue;
				double dblJE = 0;
				for(int j=0; j<salePayment.size(); j++)
				{
					salePayDef = (SalePayDef) salePayment.elementAt(i);
					if(salePayDef.paycode.equalsIgnoreCase(pay.paycode))
					{
						dblJE += salePayDef.ybje;
					}
				}
				if(dblJE<pay.ybje)
				{
					msg = msg + pay.payname + "[" + pay.paycode + "],支付金额：" + ManipulatePrecision.doubleToString(pay.ybje) + "\n";					
					isOK = false;
				}
				
			}
			if(!isOK)
			{
				PosLog.getLog(this.getClass()).info("当前小票必须存在以下支付方式:\n" + msg);
				new MessageBox("当前小票必须存在以下支付方式:\n" + msg);
				return false;
			}
		}
		
		//换货时，再次上传小票及检查，当返回失败时，则不允许通过（只是提示）
		if(SellType.ISHH(saletype)&& (saleHead.hykh!=null && saleHead.hykh.length()>0))
		{
			ispay = false;
			PosLog.getLog(this.getClass()).info("换货付款结束后，再次发预传上票并检查能否换货 start");
			RefundMoneyDef rmd = new RefundMoneyDef();
			if(!sendTmpSaleToCrm(rmd))
			{
				PosLog.getLog(this.getClass()).info("end,检查不通过。");
				return false;
			}
			PosLog.getLog(this.getClass()).info("end,检查通过");
		}		
		
		return true;
	}
	/*public boolean checkPaymodeValid(PayModeDef mode, String money)
	{
		if(!super.checkPaymodeValid(mode, money)) return false;
		
		//不能直接进行付款
		if (GlobalInfo.sysPara.noinputpaycode == null || GlobalInfo.sysPara.noinputpaycode.equals("") || GlobalInfo.sysPara.noinputpaycode.equals("0000")) return true;
		
		String paycodes[] = GlobalInfo.sysPara.noinputpaycode.split(",");
			
		for (int i = 0;i < paycodes.length;i++)
		{
			if (paycodes[i].equals(mode.code))
			{
//				new MessageBox("当前 [" + paymode.code +"] 付款不能进行直接付款!");
				new MessageBox(Language.apply("当前 [{0}] 付款不能进行直接付款!", new Object[]{mode.code}));
				return false;
			}
		}
		
		return true;
	}	*/ 
	
	public boolean checkHHFlag(PayModeDef paymode)
	{
		try
		{
			// 换退时只需要显示0710换退付款方式就行
			/*if (!SellType.ISSALE(this.saletype) && hhflag == 'Y')
			{
				if (paymode.code.equals("0710")) return true;
				else return false;
			}*/
			
			//重百：退货或换货（大换小时），不显示这些付款方式：1001、0405、0402、0400、0401、0508、42
			//     不允许修改的付款方式：91，92，93，94
			if(SellType.ISBACK(saletype) || SellType.HH_BACK.equals(saletype))
			{
				if (("," + GlobalInfo.sysPara.noBackPaycodeList + ",").indexOf("," + paymode.code + ",")>=0) return false;//不显示的付款方式
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	/*public void addSaleGoodsObject(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		if ((this.saletype.equals(SellType.RETAIL_SALE)) && (this.hhflag == 'Y'))
		{
			sg.yfphm = this.fphm;
			sg.ysyjh = this.syjh;
		}

		super.addSaleGoodsObject(sg, goods, info);

		if ((this.saleHead.str1 == null) || (this.saleHead.str1.length() <= 0) || (this.saleHead.str1.charAt(0) != 'Y')) return;
		sg.name = "(预)" + sg.name;
	}*/
	
	public boolean exchangeSale(boolean ishhPay)
	{
		//if ((this.hhflag == 'Y') && (!(SellType.ISSALE(this.saletype)))) return true;
		if(SellType.ISHH(this.saletype)) return true;
		return (!(ishhPay));
	}
	
	public boolean findGoods(String code, String yyyh, String gz)
	{
		/*if ((SellType.ISBACK(this.saletype)) && (this.hhflag == 'Y'))
		{
			new MessageBox("换退状态下必须输入原收银机号和原小票号");
			return false;
		}

		if (this.saleGoods.size() <= 0)
		{
			if (SellType.ISSALE(this.saletype) 
					&& this.hhflag == 'Y'
					&& (((Cbbh_DataService) DataService.getDefault()).getHHback(ConfigClass.CashRegisterCode, new StringBuffer()))==false
				)
			{
				new MessageBox("上笔不是【换退】，本笔不能为【换销】");
				this.hhflag = 'N';
				this.saleEvent.initOneSale(SellType.RETAIL_SALE);
				return false;
			}

			if (((Cbbh_AccessDayDB) AccessDayDB.getDefault()).getHcHHbackinfo(ConfigClass.CashRegisterCode,
																				String.valueOf(GlobalInfo.syjStatus.fphm - 1L)) > 0L)
			{
				new MessageBox("上笔为【红冲换销】，必须先进行【红冲换退】才能进行其他操作");
				return false;
			}
		}*/

		return super.findGoods(code, yyyh, gz);
	}
	public boolean HHinit()
	{
		return ((this.hhflag == 'Y') && (SellType.ISSALE(this.saletype)));
	}
	public void initOneSale(String type)
    {
    	syjh = "";
    	fphm = 0;
    	
    	/*// 换消需要输入原收银机号和原小票号
    	StringBuffer buff = new StringBuffer();
    	if (((Cbbh_AccessDayDB)AccessDayDB.getDefault()).getlasthhbackinfo(ConfigClass.CashRegisterCode, buff))
    	{
    		syjh = ConfigClass.CashRegisterCode;
    		fphm = Convert.toLong(buff.toString());
    		if (!SellType.ISSALE(type) || hhflag != 'Y')
    		{
    			type = SellType.RETAIL_SALE;
    			hhflag = 'Y';
    			new MessageBox("上笔交易为换货退货，本笔必须为换货销售");
    		}
    	}*/
    	
    	super.initOneSale(type);
    	
    	/*if (((Cbbh_AccessDayDB)AccessDayDB.getDefault()).getlasthhbackinfo(ConfigClass.CashRegisterCode, buff))
    	{
    		SaleHeadDef temp = new SaleHeadDef();
    		if (((Cbbh_AccessDayDB)AccessDayDB.getDefault()).getlasthhbackHead(temp , ConfigClass.CashRegisterCode))
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
    	}*/
    	
       	if (SellType.ISSALE(saleHead.djlb) && !SellType.ISHH(saleHead.djlb))
    	{
    		if (GlobalInfo.syjDef.priv == null || GlobalInfo.syjDef.priv.trim().length() == 0)
    		{
    			NewKeyListener.sendKey(GlobalVar.MemberGrant);
    		}
    	}
	}
	public void paySell()
	{
		/*boolean blnOk  = ((Cbbh_DataService) DataService.getDefault()).getHHback(ConfigClass.CashRegisterCode, new StringBuffer());
		if (blnOk && (SellType.ISSALE(this.saletype) == false || this.hhflag != 'Y'))
		{
			this.saleEvent.initOneSale(SellType.RETAIL_SALE);
			return;
		}*/
		//检查换货商品
		if (SellType.ISHH(saletype))
		{
			//换货时，必须存在换货和换销商品
			boolean isExistsHtGoods=false;
			boolean isExistsHxGoods=false;
			SaleGoodsDef saleGoodsDef = null;
			for (int i = 0; i < saleGoods.size(); i++)
			{
				saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
				if(saleGoodsDef.str13!=null && saleGoodsDef.str13.equalsIgnoreCase("T"))
				{
					isExistsHtGoods=true;
				}
				if(saleGoodsDef.str13!=null && saleGoodsDef.str13.equalsIgnoreCase("S"))
				{
					isExistsHxGoods=true;
				}
				if(isExistsHtGoods && isExistsHxGoods) break;
			}
			if(!isExistsHtGoods)
			{
				new MessageBox("操作失败：换货小票，必须存在【换退】商品");
				return;
			}
			if(!isExistsHxGoods)
			{
				new MessageBox("操作失败：换货小票，必须存在【换销】商品");
				return;
			}
			
			//处理大换小
			setHHType();
		}
		super.paySell();
		
		if(SellType.ISHH(saletype) && !saleFinish)
		{
			reHHType();
		}
	}
	
	public void paySellCancel()
	{
		super.paySellCancel();
		
		//当时退换货时，则调用取消事务号命令
		if(SellType.ISHH(saletype) || SellType.ISBACK(saletype))
		{
			if(saleHead.num10>0)
			{
				String ishh = "N";
				if(SellType.ISHH(saletype)) ishh="Y";
				if(sendTHHTransID(saleHead.syjh, saleHead.fphm, ishh, saleHead.num10))
					saleHead.num10=0;
				else
					PosLog.getLog(this.getClass()).info("syjh=[" + saleHead.syjh + "],fphm=[" + saleHead.fphm + "],transid=[" + String.valueOf(saleHead.num10) + "].");
			}
		}
		reHHType();
	}
	
	protected void reHHType()
	{
		//恢复为小换大类型
		if(SellType.ISHH(saletype) && saletype.equals(SellType.HH_BACK) && calcHeadYfje() > 0)
		{
			saleHead.ysje = -1*saleHead.ysje;
			saleyfje = -1*saleyfje;
			saletype = SellType.HH_SALE;
			saleHead.djlb = saletype;
		}
	}
	protected void setHHType()
	{
		//处理大换小
		if(calcHeadYfje() < 0) 
		{
			saleHead.ysje = -1*saleHead.ysje;
			saleyfje = -1*saleyfje;
			saletype = SellType.HH_BACK;
			saleHead.djlb = saletype;
		}
	}
	
	public boolean paySellStart()
	{
		if (!(super.paySellStart())) return false;

		/*重百可以换任意金额的商品
		if ((SellType.ISSALE(this.saleHead.djlb)) && (this.hhflag == 'Y'))
		{
			double ysje = ((Cbbh_AccessDayDB) AccessDayDB.getDefault()).gethhbackYsje(ConfigClass.CashRegisterCode,
																						String.valueOf(this.saleHead.fphm - 1L));
			if (this.saleHead.ysje < ysje)
			{
				new MessageBox("换销的商品总额必须大于换退的商品总额\n请继续输入其他商品");
				return false;
			}
		}*/
		
		if(!checkTHH()) return false;
		return true;
	}
	
	//检查能否删除付款方式（true 不允许删除， false 可以删除）
	public boolean checkDeleteSalePay(String ax, boolean isDelete)
	{
		if(!super.checkDeleteSalePay(ax, isDelete))
		{
			String code = "";
			if (ax.trim().indexOf("]") > -1)
			{
				code = ax.substring(1, ax.trim().indexOf("]"));
			}
			else
			{
				code = ax;
			}
			//自动添加的付款方式不能删除，但可以取消
			//if (code.equals(GlobalInfo.sysPara.noinputpaycode.split(",")[0])) { return true; }
			if (("," + GlobalInfo.sysPara.noinputpaycode + ",").indexOf("," + code + ",")>=0) return true; //modify wagyong by 2014.4.14
			return false;
		}
		return true;
	}
	public boolean isJfExchangeSalePay(SalePayDef spd)
    {
    	/*if (spd.paycode.trim().equals("0509") && spd.memo.trim().equals("2"))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}*/
		return false;
    }
	public boolean deleteAllSalePay()
	{
		Vector tempSalePayment = null;
    	Vector tempPayAssistant = null;
    	
    	try
    	{
	    	tempSalePayment = new Vector();
	    	tempPayAssistant = new Vector();
	    	
	    	// 先保存换购付款
	    	for (int i = 0;i < salePayment.size();i++)
	    	{
	    		SalePayDef tempspay = (SalePayDef)salePayment.elementAt(i);
	    		Payment tempp = (Payment)payAssistant.elementAt(i);
	    		
	    		if (isJfExchangeSalePay(tempspay))
	    		{
	    			tempSalePayment.add(tempspay);
	    			tempPayAssistant.add(tempp);
	    		}
	    	}
	    	
	    	// 删除所有付款
	    	//if (!super.deleteAllSalePay()) return false;
			// 删除所有付款方式
	    	boolean blnRet = false;
			for (int i = 0; i < salePayment.size(); i++)
			{
				blnRet = isAutoAddPaycode(i);
				if(blnRet)
					blnRet = deleteSalePay(i, true);//（退货）自动补录的付款方式可以直接取消
				else
					blnRet = deleteSalePay(i);
				
				if (!blnRet)//deleteSalePay(i)
				{
					return false;
				}
				else
				{
					i--;
				}
			}

			// 删除所有扣回的付款,用信用卡支付扣回时,取消所有付款也得取消扣回
			if (!deleteAllSaleRefund())
				return false;

			//return true;
	    	
	    	// 恢复换购的付款
	    	for (int i = 0;i < tempSalePayment.size() ; i++)
	    	{
	    		salePayment.add(tempSalePayment.elementAt(i));
	    		payAssistant.add(tempPayAssistant.elementAt(i));
	    		
	    		// 重新检查是否有积分换购的商品，重新记录分摊
	    		for (int j = 0; j < saleGoods.size(); j++)
	    		{
	    			SpareInfoDef info = (SpareInfoDef)goodsSpare.elementAt(j);
	    			if (info.char2 == 'Y')
	    			{
	    				int seqnum = Convert.toInt(info.str3.split(",")[0]);
	    		    	// 先保存换购付款
	    		    	for (int k = 0;k < salePayment.size();k++)
	    		    	{
	    		    		SalePayDef tempspay = (SalePayDef)salePayment.elementAt(i);
	    		    		if (tempspay.num5 == seqnum)
	    		    		{
	    		    			if (info.payft == null) info.payft = new Vector();
	    		                String[] ft = new String[] {String.valueOf(tempspay.num5),tempspay.paycode,tempspay.payname,ManipulatePrecision.doubleToString(tempspay.je - tempspay.num1)};
	    		                info.payft.add(ft);
	    		                break;
	    		    		}
	    		    	}
	    			}
	    		}
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
    		if (tempSalePayment != null)
    		{
    			tempSalePayment.clear();
    			tempSalePayment = null;
    		}
    		
    		if (tempPayAssistant != null)
    		{
    			tempPayAssistant.clear();
    			tempPayAssistant = null;
    		}
    	}
    	
	}
	

	//检查当前付款方式是否为自动添加的付款方式
	protected boolean isAutoAddPaycode(int index)
	{
		if (index >= 0)
		{
			Payment p = (Payment) payAssistant.elementAt(index);

			String paycodes[] = GlobalInfo.sysPara.noinputpaycode.split(",");

			for (int i = 0; i < paycodes.length; i++)
			{
				if (paycodes[i].equals(p.salepay.paycode))
				{
					//new MessageBox(Language.apply("当前 [{0}] 付款不能进行删除!", new Object[]{p.salepay.payname}));
					return true;
				}
			}
		}
		return false;
	}
	
	/*public void addMemoPayment()
	{
		super.addMemoPayment();
				
		for (int i = 0; i < memoPayment.size(); i++)
		{
			Payment pay = (Payment) memoPayment.elementAt(i);

			if (pay.salepay != null)
				addSalePayObject(pay.salepay, pay);
		}
		
		// 刷新界面显示
		saleEvent.clearTableItem();
		saleEvent.updateSaleGUI();

	}*/
	
	/*public void paySellCancel()
	{
		super.paySellCancel();
		
		//清除换货的临时（商品）信息
		SaleGoodsDef saleGoodsDef;
		for(int j=0; j<this.saleGoods.size(); j++)
		{
			saleGoodsDef = (SaleGoodsDef)saleGoods.elementAt(j);
			if(saleGoodsDef==null) continue;
			saleGoodsDef.str6 = "";
		}
	}*/

	public boolean checkTHH()
	{
		ispay = true;
		/*检查退换货：（存在刷卡的退/换货，否则不预传及检查）
		 * 1.预上传小票
		 * 2.获取扣回信息（补录的付款方式、商品明细记录信息等）
		 * 3.增加补录的付款方式到memoPayment
		 * */		
		if( (SellType.ISHH(saletype) || SellType.ISBACK(saletype))
			&& ((saleHead.hykh!=null && saleHead.hykh.length()>0) 
				//&& (this.thFphm>0 && this.thSyjh!=null && this.thSyjh.trim().length()>0)
				)
			)
		{
			if(saleHead.num10>0)
			{
				String ishh = "N";
				if(SellType.ISHH(saletype)) ishh="Y";
				if(!sendTHHTransID(saleHead.syjh, saleHead.fphm, ishh, saleHead.num10)) return false;
				saleHead.num10 = 0;
			}
			
			thhPayCheck = new Vector();
		}
		else
		{
			thhPayCheck=null;
			return true;
		}

    	RefundMoneyDef rmd = new RefundMoneyDef();
    	if(!sendTmpSaleToCrm(rmd)) return false;
    	if(refundPayment!=null) refundPayment.removeAllElements();
    	if(refundAssistant!=null) refundAssistant.removeAllElements();
    	
    	//退换货事务号
    	/*if(rmd.refunddesc1==null || rmd.refunddesc1.length()<=0)
    	{
    		new MessageBox("获取数据失败：退换货事务号为空!");	        	
	        return false;
    	}*///上新促销屏蔽
    	double thhTransID = Convert.toDouble(rmd.refunddesc1);
    	saleHead.num10=thhTransID;
    	
    	//添加付款方式
    	//rmd.refunddesc2 ///*换销商品的固定付款方式   行号rowno;付款方式代码paycode;原币金额ybje;卡号payno;劵种inno;盈余金额kye;标志flag1付款2找零3扣回,*/    	
    	if(rmd.refunddesc2!=null && rmd.refunddesc2.length()>0)
    	{
    		Vector vPay = new Vector();
    		SalePayDef salePay;
    		String[] arr = rmd.refunddesc2.split(",");
    		String[] arrSub;
    		for(int i=0; i<arr.length; i++)
    		{
    			arrSub = arr[i].split(";");
    			if(arrSub.length<7) continue;
    			//if(!arrSub[1].equals("92") && !arrSub[1].equals("93") && !arrSub[1].equals("94")) continue;
    			
    			salePay = new SalePayDef();
    			salePay.rowno = Convert.toInt(arrSub[0]);
    			salePay.paycode = arrSub[1].trim();
    			salePay.ybje = Convert.toDouble(arrSub[2]);
    			salePay.payno = arrSub[3].trim();
    			salePay.idno = arrSub[4].trim();
    			salePay.num1 = Convert.toDouble(arrSub[5]);
    			salePay.flag = arrSub[6].charAt(0);
    			vPay.add(salePay);
    		}
			
    		for(int j=0; j<vPay.size(); j++)
    		{
    			if (!addPayment((SalePayDef) vPay.elementAt(j)))  return false; 
    		}
    		addRefundToSalePay();//添加扣回到付款
    		showPayCheckMsg();
    	}
    	
    	/*//test data
    	SalePayDef salePay = new SalePayDef();
    	salePay.paycode="93";
    	salePay.ybje=2;
    	salePay.payno="991234";
    	salePay.idno="12345";
    	salePay.kye=0;
    	salePay.flag='1';
    	if(!addPayment(salePay)) return false;*/
		
		return true;
	}

	public boolean sendTmpSaleToCrm(RefundMoneyDef rmd)
	{
    	// 预上传小票到CRM 
    	ProgressBox pb = new ProgressBox();
    	char bc = saleHead.bc;
    	try
    	{
    		saleHead.bc = '#';
	    	// 发送当前退货小票到后台数据库
    		pb.setText("正在预上传小票......");
	        if (!this.saleEvent.saleBS.saleSummary())
	        {
	            new MessageBox("交易数据汇总失败!");
	        	
	        	return false;
	        }
	       /* if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
	        {
	            new MessageBox("交易数据校验错误!");
	
	            return false;
	        }*///新促销取消 --maxun 2015年1月12日22:50:43
	        
	        // 发送当前退货小票以计算扣回
        	// jdfhdd标记当前发送的是用于计算扣回的小票信息
        	String oldfhdd = saleHead.jdfhdd;
        	saleHead.jdfhdd = "KHINV";	        

        	if(SellType.ISHH(saletype) && saleHead.num10>0)
        	{//当是换货时，且付款完毕后（根据事务号来判断），则发送实际付款
        		if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, salePayment, null) != 0)
    			{
    	    		saleHead.jdfhdd = oldfhdd;
    	    		new MessageBox("操作失败：预上传小票失败！");
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
            	tempsp.payname = "预上传虚拟付款";
            	tempsp.ybje = saleHead.ysje;
            	tempsp.hl = 1;
            	tempsp.je = saleHead.ysje;
            	tempPay.add(tempsp);
            	if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, tempPay, null) != 0)
    			{
    	    		saleHead.jdfhdd = oldfhdd;
    	    		new MessageBox("操作失败：预上传小票失败！");
    	    		return false;
    			}
        	}
	        	        
	        saleHead.jdfhdd = oldfhdd;
	        String ishh = "N";
	        if(SellType.ISHH(saletype)) ishh="Y";
	    	// 调用后台过程返回需要扣回的金额
	    	pb.setText("正在检查当前退货小票信息......");
	    	if(ispay)ishh = ishh+"P";else ishh = ishh+"N";
	    	if (!NetService.getDefault().getRefundMoney_Dos(saleHead.mkt,saleHead.syjh,saleHead.fphm,ishh, rmd,CmdDosDef.GETREFUNDMONEY_EX))
			{
	    		return false;
			}
	    	
	    	//一直获取最新事务号
	    	double thhTransID = Convert.toDouble(rmd.refunddesc1);
	    	saleHead.num10=thhTransID;
	    	
    		return true;
    	}
    	catch(Exception er)
    	{
    		er.printStackTrace();
    		PosLog.getLog(this.getClass()).error(er);
    		new MessageBox("检查退货小票信息时异常：" + er.getMessage());
    		return false;
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
	}
	
	
	//发送退换货事务号
	public boolean sendTHHTransID(String syjh, long fphm, String ishh, double transid)
	{
		if(NetService.getDefault().cancelSaleTH_Dos(GlobalInfo.sysPara.mktcode,syjh,fphm,ishh,(long)transid))
		{
			return true;
		}
		else
		{
			//new MessageBox("取消退换货时失败！");
			return false;
		}
	}
	
	private void showPayCheckMsg()
	{
		try
		{
			if(this.thhPayCheck!=null && this.thhPayCheck.size()>0)
			{
				String msg="";
				SalePayDef pay;
				for(int i=0; i<thhPayCheck.size(); i++)
				{
					pay = (SalePayDef)thhPayCheck.elementAt(i);
					if(pay==null) continue;
					 
					msg = msg + pay.payname + "[" + pay.paycode + "]" + ",支付金额:" 
						+ ManipulatePrecision.doubleToString(pay.ybje) + "\n";
				}
				//当前小票必须存在以下支付方式
				//提货卡[03]，支付金额：10.00
				msg = "当前小票必须存在以下支付方式:\n" + msg;
				new MessageBox(msg);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
		
	public void addSaleRefundObject(SalePayDef spay,Payment payobj)
    {
		if(refundPayment==null) refundPayment = new Vector();
		if(refundAssistant==null) refundAssistant = new Vector();
		super.addSaleRefundObject(spay, payobj);
		
    }
	public boolean addPayment(SalePayDef salePay)
	{		
		if(salePay==null) return false;
		double dj = salePay.ybje;//yfqk;
		Payment pay = null;
		pay = CreatePayment.getDefault().createPaymentByPayMode(DataService.getDefault().searchPayMode(salePay.paycode), this);

		if (pay != null && dj > 0)
		{
			//pay1.inputPay(String.valueOf(dj));
			if(salePay.flag=='4')
			{
				PayModeDef  payModeDef  = DataService.getDefault().searchPayMode(salePay.paycode);
				if(payModeDef!=null) salePay.payname = payModeDef.name;
				thhPayCheck.add(salePay);
				return true;
			}
			if(salePay.flag != '3' && !pay.createSalePay(String.valueOf(dj)))
			{
				new MessageBox("自动添加付款方式失败！");
				return false;
			}
			else
			{
				if(!pay.createSalePayObject(String.valueOf(dj)))
				{
					new MessageBox("自动添加扣回付款方式失败！");
					return false;
				}
			}
			
			pay.salepay.payname = "("+salePay.idno+")"+pay.salepay.payname;
			pay.salepay.rowno=salePay.rowno;
			pay.salepay.payno=salePay.payno;
			pay.salepay.idno=salePay.idno;
			pay.salepay.num1=salePay.num1;
			pay.salepay.flag=salePay.flag;
			
			//memoPayment.add(pay1);
			if (pay.salepay != null)
			{
				if(pay.salepay.flag=='1')
				{
					addSalePayObject(pay.salepay, pay);
				}
				else if(pay.salepay.flag=='3')
				{
					addSaleRefundObject(pay.salepay, pay);
				}				 
				
				return true;
			}
			
		}
		if(pay==null)
		{
			new MessageBox("失败：付款方式自动添加失败，未找到 [" + salePay.paycode + "] 付款方式");
		}
		else if(dj<=0)
		{
			new MessageBox("失败：付款方式自动添加失败，[" + salePay.paycode + "] 付款方式金额为" + dj);
		}
		
		return false;
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
			if (SellType.ISHH(saletype) || saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText(Language.apply("开始查找退货小票操作....."));
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
				// 检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				// Y为已在后台退回礼品 津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					new MessageBox(Language.apply("此小票有满赠礼品，请先到后台退回礼品再办理退货！"));
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + Language.apply("\n是否继续退货？"), null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				/*if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE) && !SellType.ISHH(saletype))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox(Language.apply("原小票是[{0}]交易\n\n与当前退货交易类型不匹配", new Object[] { SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) }));
						// new MessageBox("原小票是[" +
						// SellType.getDefault().typeExchange(thsaleHead.djlb,
						// thsaleHead.hhflag, thsaleHead) +
						// "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}*/  //maxun add 换货小票也可以退货 

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { Language.apply("序"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("原数量"), Language.apply("原折扣"), Language.apply("原成交价"), Language.apply("退货"), Language.apply("退货数量") };
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

				String[] title1 = { Language.apply("序"), Language.apply("付款名称"), Language.apply("账号"), Language.apply("付款金额") };
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
					cho = new MutiSelectForm().open(Language.apply("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open(Language.apply("开单营业员号："), "", Language.apply("请输入有效开单营业员号"), backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox(Language.apply("该工号不是营业员!"), null, false);
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
					sgd.str13 = "";
					if(SellType.ISHH(saletype)) sgd.str13 = "T";
					crmPop.add(new GoodsPopDef());
					
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
							new MessageBox(Language.apply("超出退货的最大限额，不能退货"));

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
							new MessageBox(Language.apply("授权退货,限额为 {0} 元", new Object[] { ManipulatePrecision.doubleToString(curGrant.thxe) }));
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
	

	public boolean isDeletePay(int index)
	{
		/*退货不控制
		 * if (SellType.ISBACK(saleHead.djlb))
			return true;*/

		if (GlobalInfo.sysPara.nodeletepaycode == null || GlobalInfo.sysPara.nodeletepaycode.equals("") || GlobalInfo.sysPara.nodeletepaycode.equals("0000"))
			return true;

		if (index >= 0)
		{
			Payment p = (Payment) payAssistant.elementAt(index);

			String paycodes[] = GlobalInfo.sysPara.nodeletepaycode.split(",");

			for (int i = 0; i < paycodes.length; i++)
			{
				if (paycodes[i].equals(p.salepay.paycode))
				{
					new MessageBox(Language.apply("当前 [{0}] 付款不能进行删除!", new Object[] { p.salepay.payname }));
					return false;
				}
			}
		}

		return true;
	}
}
