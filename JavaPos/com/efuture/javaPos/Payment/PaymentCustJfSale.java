package com.efuture.javaPos.Payment;

import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.JfSaleRuleDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class PaymentCustJfSale extends PaymentCustLczc
{
	public PaymentCustJfSale()
	{
	}
	
	public PaymentCustJfSale(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public PaymentCustJfSale(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public boolean createSalePayObject(String money)
	{
		if (super.createSalePayObject(money))
		{
			if (saleBS.curCustomer != null)
			{
				//分解积分折现规则
				mzkret.memo = saleBS.curCustomer.memo;
				mzkret.value1 = saleBS.curCustomer.valuememo;
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean createSalePay(String money)
	{
		
		if (super.createSalePay(money))
		{
			// 如果是指定小票退货，查询原小票是否有积分的付款方式
			if (saleBS != null && saleBS.isNewUseSpecifyTicketBack(false))
			{
				Vector v = new Vector();
				for (int i = 0; i < saleBS.backPayment.size(); i++)
				{
					SalePayDef spd = (SalePayDef) saleBS.backPayment.elementAt(i);
					if (paymode.code.equals(spd.paycode))
					{
						String[] row = spd.idno.split(",");
						
						try{
							Double.parseDouble(row[1]);
							Double.parseDouble(row[2]);
						}catch(Exception er)
						{
							new MessageBox(spd.idno);
							continue;
						}
						v.add(new String[]{Convert.increaseChar(row[1], 10)+Language.apply("积分 兑换 ")+Convert.increaseChar(row[2], 10)+Language.apply("元"),spd.idno});
					}
				}
				
				if (v.size() > 0)
				{
					String[] title = { Language.apply("积分规则描述") };
	                int[] width = { 460 };

	                int choice = -1;
	                if (v.size() == 1)
	                	choice = 0;
	                else
	                	choice= new MutiSelectForm().open(Language.apply("请选择你使用的积分规则"), title, width, v);
	                
	                if (choice >= 0)
	                {
	                	String[] line = (String[]) v.elementAt(choice);
	                	String[] row = line[1].split(",");
	                	mzkret.memo = row[1]+","+row[2];
	                }
				}
			}
			
			if (mzkret.memo == null || mzkret.memo.length() <= 0)
			{
				new MessageBox(Language.apply("没有积分消费的规则，不允许使用积分消费"));
				return false;
			}
			//分解积分折现规则
			String[] num = mzkret.memo.split(",");
			
			//num1:积分数  num2:折现金额
			double num1 = Double.parseDouble(num[0]);
			double num2 = Double.parseDouble(num[1]);
			if (num2 == 0)
			{
				new MessageBox("此会员没有积分消费规则，无法使用积分消费");
				return false;
			}
			//要扣的积分,规则中定义的积分数,规则中定义的折现金额,换购规则单号,商品编码，商品数量,积分类型,档期
			//为了防止拼出来的字符串对应错误，以后这里直接往后累加。
			salepay.idno = String.valueOf(ManipulatePrecision.mul(num1, ManipulatePrecision.div(salepay.ybje, num2))) + "," + num1 + "," + num2+",,,,"+((saleBS.curCustomer != null)?String.valueOf(saleBS.curCustomer.value4):"")+","+((saleBS.curCustomer != null)?saleBS.curCustomer.valstr2:"");
		
			//1-积分消费
			salepay.memo = "1";
			
			return true;
		}
		
		return false;
	}
	
	public boolean checkMzkMoneyValid()
	{
		if (super.checkMzkMoneyValid())
		{
			if (SellType.ISSALE(salehead.djlb))
			{
				String[] num = mzkret.memo.split(",");
				double num2 = Double.parseDouble(num[1]);
				
				// 
				if (ManipulatePrecision.mod(salepay.ybje , num2 ) != 0)
				{
//					new MessageBox("消费的金额必须为"+num2+" 的倍数！");
					new MessageBox(Language.apply("消费的金额必须为{0} 的倍数！" ,new Object[]{num2+""}));
					return false;
				}
			}
			return true;
		}
		
		return false;
	}
	
	//计算积分转换成金额
	private double calcJfeSaleMoney()
	{
		if (saleBS.curCustomer	== null)
		{
//			new MessageBox("当前未刷会员卡\n无法使用" + paymode.name + "付款方式!");
			new MessageBox(Language.apply("当前未刷会员卡\n无法使用{0}付款方式!" ,new Object[]{paymode.name}));
			return 0;
		}
			
		if (saleBS.curCustomer.valuememo <= 0)
		{
			new MessageBox(Language.apply("此会员卡没有积分，不能用此付款方式付款"));
			return 0;
		}
			
		String[] num = saleBS.curCustomer.memo.split(",");
		try
		{
			if (num.length != 2)
			{
				new MessageBox(Language.apply("未定义折现基数或折现标准，不能用此付款方式付款"));
				return 0;
			}
			
			double num1 = Double.parseDouble(num[0]);
			double num2 = Double.parseDouble(num[1]);
			
			if (num1 <=0 || num2 <= 0)
			{
				new MessageBox(Language.apply("未定义折现基数或折现标准，不能用此付款方式付款")+saleBS.curCustomer.memo);
				return 0;
			}
			
			int num3 = ManipulatePrecision.integerDiv(saleBS.curCustomer.valuememo, num1);
			if (num3 < 1)
			{
				new MessageBox(Language.apply("卡积分不足以折现，不能用此付款方式付款"));
				return 0;
			}
			
			return ManipulatePrecision.mul(num3, num2);
		}
		catch(Exception er)
		{
			new MessageBox(Language.apply("折现基数或折现标准定义错误,或者，不能用此付款方式付款"));
			return 0;
		}
	}
			
	protected String getDisplayStatusInfo()
	{
		String line = "";
		
		if (SellType.ISSALE(salehead.djlb))
		{			
			allowpayje = Math.min(getAccountAllowPay(), mzkret.ye);
			
			if (mzkret.memo != null && mzkret.memo.split(",").length == 2)
			{
				String[] num = mzkret.memo.split(",");
				
//				line +="当前会员的积分是:" + mzkret.value1 + "\n" + (int)Double.parseDouble(num[0]) +" 积分兑换 " + ManipulatePrecision.doubleToString(Double.parseDouble(num[1])) + "元\n最大可收积分消费金额: "+allowpayje+"元";
				line +=Language.apply("当前会员的积分是:{0}\n{1} 积分兑换 {2}元\n最大可收积分消费金额: {3}元" ,new Object[]{mzkret.value1+"" ,(int)Double.parseDouble(num[0])+"" ,ManipulatePrecision.doubleToString(Double.parseDouble(num[1])) ,allowpayje+""});
			}
		}
		return line;
	}
	
    public double getftje(SpareInfoDef spinfo)
    {
    	double ftje = 0;
        if (spinfo.payft != null)
        {
        	for (int j=0;j<spinfo.payft.size();j++)
        	{
        		String[] s = (String[])spinfo.payft.elementAt(j);
        		ftje += Convert.toDouble(s[3]);
        	}
        }
        return ftje;
    }
	
    // 将会员卡信息赋给面值卡结构
    public boolean setCustomerInfo()
    {
    	if (!super.setCustomerInfo()) return false;
    	
		mzkret.memo = saleBS.curCustomer.memo;
		mzkret.value1 = saleBS.curCustomer.valuememo;
    	//
        mzkret.ye = calcJfeSaleMoney();
        mzkret.money = 999999;
        if (mzkret.ye <= 0) return false;
        

		
        return true;
    }	
    
    public boolean createJfExchangeSalePay(double je,double jf,JfSaleRuleDef jfrd,int index)
    {
    	// 设置PaymentJfSale对象初始值
    	super.setCustomerInfo();
    	
    	// 创建SalePay对象
    	if (!createSalePayObject(String.valueOf(je)))
    	{
    		return false;
    	}
    	
    	// 记录账号信息到SalePay
    	if (!saveFindMzkResultToSalePay())
    	{
    		return false;
    	}
    	
    	//要扣的积分,XX积分,兑单个商品XX金额		
    	salepay.idno = jf + "," + String.valueOf(jfrd.jf) + "," + String.valueOf(je / (jf/jfrd.jf));

    	// 标记为积分换购	
		salepay.payname = Language.apply("积分换购");
    	salepay.memo = "2";
    	
    	return true;
    }
    
	// 分摊在BS里计算
	public boolean isApportionInBS()
	{
		if (salepay.paycode.equals("0509")) return false;
		return super.isApportionInBS();
		
	}
    
    // 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
    {
		if (!super.setRequestDataByAccount()) return false;

		// 要扣的积分,规则中定义的积分数,规则中定义的折现金额
		String salepaylist[] = salepay.idno.split(",");
		mzkreq.je = Double.parseDouble(salepaylist[0]);
		mzkreq.memo = salepay.idno;
		
		if (salehead.ysyjh != null || salehead.yfphm != null)
		{
			mzkreq.memo += ","+salehead.ysyjh+","+salehead.yfphm;
		}
		return true;
    }
	
	// 分摊是否在baseApportion里分摊---防止多重重载的情况下不能调用基类
	public boolean isBaseApportion()
	{
		if (!isApportionInBS()) return false;
		
		// 积分消费都在baseApportion里分摊
		if (salepay.memo.equals("1")) return true;
		
		return false;
	}
	
	public double getAccountAllowPay()
	{
		String memo = null;
		// 先判断当前刷卡的积分规则，再判断会员卡内的积分消费规则
		if (mzkret.memo != null && mzkret.memo.length() > 0)
		{
			memo = mzkret.memo;
		}
		else
		{
			memo = saleBS.curCustomer.memo;
		}
		String[] num = memo.split(",");
		
		double multiple = ManipulatePrecision.mod(getCalcKfJe() , Double.parseDouble(num[1]));
		
		multiple = ManipulatePrecision.doubleConvert(getCalcKfJe() - multiple);
		
		return multiple;
	}
	
	public double getCalcKfJe()
	{
		double kfje = 0;
		
		// 计算可进行积分消费的商品总金额
		for (int i = 0 ; i < saleBS.saleGoods.size() ; i ++)
		{
            SpareInfoDef info = (SpareInfoDef)saleBS.goodsSpare.elementAt(i);
        	SaleGoodsDef saleGoodsDef = (SaleGoodsDef)saleBS.saleGoods.get(i);
			if (info.char3 == 'N') continue;
			double je = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - saleGoodsDef.hjzk - getftje(info));
			kfje += je;
		}
		//return 2;
		return ManipulatePrecision.doubleConvert(kfje);
	}
	
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		return true;
	}
	
	protected boolean saveFindMzkResultToSalePay()
	{
		// 积分消费不计算余额（单位不匹配）
		//salepay.kye = 0;
		//mzkret.ye = 0;
		return super.saveFindMzkResultToSalePay();
	}
	
    protected void saveAccountMzkResultToSalePay()
    {
    	// batch标记本付款方式已记账,这很重要
    	salepay.batch = String.valueOf(mzkreq.seqno);

    	// 标记记账返回的卡号
    	if (!CommonMethod.isNull(mzkret.cardno)) salepay.payno = mzkret.cardno;
    	
    	//new MessageBox("salepay.kye="+salepay.kye+" mzkret.ye="+mzkret.ye);
    	// 后台退货时没有刷卡所以记录后台返回的卡余额,或者记账过程返回了最终余额
    	if (salepay.kye <= 0 || mzkret.ye > 0 || (mzkret.status != null && mzkret.status.equals("RETURNYE")))
    	{	
    		salepay.kye = mzkret.ye;	
    	}
    	else
    	{
        	// 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
    		if (mzkreq.type == "01") salepay.kye = mzkret.value1 - Convert.toDouble(salepay.idno.substring(0,salepay.idno.indexOf(",")));
     		else salepay.kye = mzkret.value1 + Convert.toDouble(salepay.idno.substring(0,salepay.idno.indexOf(",")));
    	}
    	salepay.kye = ManipulatePrecision.doubleConvert(salepay.kye);
    	//new MessageBox("salepay.kye="+salepay.kye);
		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null) this.saleBS.writeBrokenData();
    }
}
