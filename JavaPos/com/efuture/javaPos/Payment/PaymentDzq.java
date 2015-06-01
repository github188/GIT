package com.efuture.javaPos.Payment;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

public class PaymentDzq extends PaymentMzk
{
	public Vector dzqList = new Vector();		// 券种代码,券种名称,券种余额,券种汇率,纸券标记|
	public int dzqIndx = -1;					// 券种序号
	public String dzqType = "N";				// 是否纸券
	
	public PaymentDzq()
	{
	}
	
	public PaymentDzq(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public PaymentDzq(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);		
	}

	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
				new MessageBox(Language.apply("退货时不能使用") + paymode.name);
				return null;
			}
			
			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;
			
			// 打开明细输入窗口
			new PaymentDzqForm().open(this,saleBS);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
	
	// 设置金额
    public boolean setYeShow(Table table)
    {   
    	// 设置余额列表
		table.removeAll();
		dzqList.removeAllElements();
		
		// yinliang test
		//mzkret.memo = "01,A券,100,1,Y|02,B券,300,1";
		
		// 分解余额
		if (mzkret.memo != null && mzkret.memo.trim().length() > 0)
		{
			String row[] = mzkret.memo.split("\\|");
			if (row.length <= 0) return false;
			
			for (int i = 0; i < row.length ; i++)
			{
				String line[] = row[i].split(",");
				if (line.length < 3) continue;
				
				String hl = "1";
				String paper = "N";
				if (line.length > 3) hl = line[3].trim();
				if (line.length > 4) paper = line[4].trim();
				
				String[] lines = {line[0].trim(),line[1].trim(),line[2].trim(),hl,paper};
				dzqList.add(lines);
			}
		}
				
    	// 显示券类型
    	for (int i = 0 ; i < dzqList.size(); i++)
    	{
    		String str [] = new String[3];
    		TableItem item = null;
    		String row[] = (String[])dzqList.elementAt(i);
    		
    		// 找已刷卡的付款
    		String yfk = "0.00";
    		Vector v = null;
    		if (saleBS.isRefundStatus()) v = saleBS.refundPayment;
    		else v = saleBS.salePayment;
			for (int n=0;n <v.size();n++)
			{
				SalePayDef sp = (SalePayDef)v.elementAt(n);
				
				if (sp.paycode.equals(paymode.code) && sp.payno.equals(mzkret.cardno) && sp.idno.equals(row[0]))
				{
					yfk = ManipulatePrecision.doubleToString(sp.ybje);
					break;
				}
			}
    		
			str[0] = "["+row[0]+"]"+row[1];
			str[1] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
			str[2] = yfk;
			item = new TableItem(table, SWT.NONE);
			item.setText(str);
    	}
    	
		if (dzqList.size() > 0) return true;
		else
		{
			new MessageBox(Language.apply("该卡无券种余额!"));
			return false;
		}
    }
    
    public String getDisplayStatusInfo(int index)
    {
    	String[] rows = (String[]) dzqList.elementAt(index);
    	String line = "";
    	
		// 标记当前付款行的付款标识,用于判断收款规则
		curpayflag = rows[0];
		dzqIndx = index;
		dzqType = rows[4];
		
		// 退货时不记算最大能退金额
		if (SellType.ISSALE(salehead.djlb))
    	{
    		allowpayje = this.calcPayRuleMaxMoney() / Convert.toDouble(rows[3]);
    		//new MessageBox("测试: allowpayje"+allowpayje+" calcPayRuleMaxMoney"+calcPayRuleMaxMoney()+"  Convert.toDouble(rows[3]"+ Convert.toDouble(rows[3]));
    		if (allowpayje >= 0)
    		{
	    		double showprice = saleBS.getDetailOverFlow(allowpayje);
//	    		line  = "["+rows[0]+"]"+rows[1]+"的限制金额为: " + ManipulatePrecision.doubleToString(allowpayje) + " 元";
	    		line  = Language.apply("[{0}]{1}的限制金额为: {2} 元" ,new Object[]{rows[0] ,rows[1] ,ManipulatePrecision.doubleToString(allowpayje)});
	    		allowpayje = Math.max(allowpayje, showprice);
    		}
    	}
		else
		{
    		allowpayje = -1;
		}
		
    	return line;
    }
    
	public double calcPayRuleAlreadyMoney()
	{
		double yfje = 0;
		for (int i=0;i<saleBS.salePayment.size();i++)
		{
			SalePayDef sp = (SalePayDef)saleBS.salePayment.elementAt(i);
			if (!sp.paycode.equals(paymode.code)) continue;
			
			// 同卡多次付款为覆盖模式，因此同卡不算已付
			if (sp.paycode.equals(paymode.code) && sp.payno.equals(mzkret.cardno)) continue;
			// 当此付款方式再次付款时，已付的部分已经扣减
			//if (sp.paycode.equals(paymode.code)) continue;
			if (sp.ispx == 'Y') continue; 
			
			// 同券种不同卡号要算已付款
			if (sp.idno.equals(((String[])dzqList.elementAt(dzqIndx))[0]))
			{
				yfje += ManipulatePrecision.doubleConvert(sp.je - sp.num1);
				yfje  = ManipulatePrecision.doubleConvert(yfje);
			}
		}
		
		//new MessageBox("测试： calcPayRuleAlreadyMoney:"+yfje);
		return yfje;
	}
	
	public double calcGoodsLeavingsApportion(int i,SaleGoodsDef sg)
	{
        // 计算商品已分摊的付款
        double ftje = getGoodsApportionTotal(i);
        double pxje = getGoodsMatchMoneyTotal(i);
        
        // 已分摊再减去同卡号同券种
        double ftce = 0;
        double pxce = 0;
        if (saleBS.goodsSpare != null && saleBS.goodsSpare.size() > i)
        {
        	SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);
        	if (spinfo != null && spinfo.payft != null)
        	{
	        	for (int j=0;j<spinfo.payft.size();j++)
	        	{
	        		String[] s = (String[])spinfo.payft.elementAt(j);
	        		if (s.length <= 3) continue;
	        		
	        		// 查找分摊的付款行
					for (int k = 0; k < saleBS.salePayment.size(); k++)
					{
						SalePayDef sp = (SalePayDef)saleBS.salePayment.elementAt(k);
						//new MessageBox("sp.num5:"+sp.num5+" Convert.toInt(s[0]):"+Convert.toInt(s[0]));
						if (sp.num5 == Convert.toInt(s[0]))
						{
							// 同卡号同券种
							//if (sp.paycode.equals(paymode.code) && sp.payno.equals(mzkret.cardno) &&
							//	sp.idno.equals(((String[])dzqList.elementAt(dzqIndx))[0]))
								if (sp.paycode.equals(paymode.code)  &&
										sp.idno.equals(((String[])dzqList.elementAt(dzqIndx))[0]))
							{
								if (s.length > 3) ftce += Convert.toDouble(s[3]);
								if (s.length > 4) pxce += Convert.toDouble(s[4]);
							}
							break;
						}
					}
	        	}
        	}
        }
        
        //new MessageBox("ftje:"+ftje+" pxje:"+pxje+" "+" ftce"+ftce+" pxce"+pxce);
        ftje -= ftce;
        pxje -= pxce;
        
        double limitje = ManipulatePrecision.doubleConvert(sg.hjje - sg.hjzk - ftje - pxje);
        
        
        return limitje;
	}
	
	public String convertName(String[] rows)
	{
		return rows[1];
	}
	
	public boolean createSalePay(int index,double money)
    {
		try
		{
			String[] rows = (String[])dzqList.elementAt(index);

			// 删除付款集合中同付款卡号同券种的付款行
			if (!deleteSamePayment(this.paymode.code,this.mzkret.cardno,this.curpayflag))
			{
				new MessageBox(Language.apply("删除已存在的付款行错误!"));
				return false;
			}

			// 金额<=0可清除已存在的付款行
	        if (money <= 0) return true;
	        
			// 生成付款行
			PaymentDzq dzq = new PaymentDzq(paymode,saleBS);
			
			dzq.paymode = (PayModeDef)this.paymode.clone();
			dzq.paymode.hl = Convert.toDouble(rows[3]);
			dzq.salehead = this.salehead;
			dzq.saleBS = this.saleBS;
			
			dzq.mzkreq = (MzkRequestDef)mzkreq.clone();
			dzq.mzkret = (MzkResultDef)mzkret.clone();

			dzq.dzqList = this.dzqList;
			dzq.dzqIndx = index;
			dzq.dzqType = rows[4];
			if (dzq.dzqType.equals("Y"))				// 纸券溢余不找零
			{
				dzq.paymode.isyy = 'Y';
				dzq.paymode.iszl = 'N';
			}		
			
			dzq.mzkret.ye = Convert.toDouble(rows[2]);	// 余额
			dzq.mzkreq.memo = rows[0];					// 券种

			dzq.allowpayje = this.allowpayje;
			if (this.allowgoods != null) dzq.allowgoods = (Vector)this.allowgoods.clone();
			else dzq.allowgoods = null;
			dzq.allowpayjealready = this.allowpayjealready;
			dzq.curpayflag = this.curpayflag;
			
			// 创建付款对象
			if (dzq.createSalePay(String.valueOf(money)))
			{
				// 改变付款方式名称并增加到已付款
				if (convertName(rows) != null)
					dzq.salepay.payname = convertName(rows);
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					dzq.salepay.payname += Language.apply("扣回");
					saleBS.addSaleRefundObject(dzq.salepay, dzq);
				}
				else
				{
					saleBS.addSalePayObject(dzq.salepay, dzq);
				}
				this.alreadyAddSalePay = true;
				
				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return false;
    }
	
	public boolean checkMzkMoneyValid()
	{
		if (!super.checkMzkMoneyValid()) return false;
		
		// 纸券必须一次性使用完
		if (dzqType.equals("Y") && checkMzkIsBackMoney())
		{
	        if (ManipulatePrecision.doubleCompare(salepay.ybje,this.getAccountYe(),2) != 0)
	        {
//				if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？",null,true).verify() == GlobalVar.Key1)
				if (new MessageBox(Language.apply("{0}的每张券必须一次性付完!\n是否将剩余部分计入损溢？" ,new Object[]{salepay.payname}),null,true).verify() == GlobalVar.Key1)
				{
					// num1记录券付款溢余部分
					salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(this.getAccountYe(), salepay.hl), Math.min(salepay.je,this.saleBS.calcPayBalance()));
					salepay.ybje = this.getAccountYe();
					salepay.je   = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2,1);
					return true;
				}
				else
				{
					return false;
				}
	        }
		}
		
		return true;
	}
	
	public boolean deleteSamePayment(String paycode,String payno,String payflag)
	{
		Vector v = null;
		if (saleBS.isRefundStatus()) v = saleBS.refundPayment;
		else v = saleBS.salePayment;
		
		for (int n=0;n <v.size();n++)
		{
			SalePayDef sp = (SalePayDef)v.elementAt(n);
			
			if (sp.paycode.equals(paycode) && sp.payno.equals(payno) && sp.idno.equals(payflag))
			{
				this.alreadyAddSalePay = true;
				
				if (saleBS.isRefundStatus()) saleBS.delSaleRefundObject(n);
				else saleBS.delSalePayObject(n);
				n--;
			}
		}

		return true;
	}
	
	public void showAccountYeMsg()
	{
	    // 不显示面值卡的余额提示
	}
	
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
		return true;
	}
	
	public void setRequestDataBySalePay()
	{
		super.setRequestDataBySalePay();
		
		// 标记券种类型
		mzkreq.memo = salepay.idno;
	}

    protected boolean saveFindMzkResultToSalePay()
    {
    	if (!super.saveFindMzkResultToSalePay()) return false;
    	
    	// 在salepay.idno记录券种
		salepay.idno = mzkreq.memo;
		
		return true;
    }
}
