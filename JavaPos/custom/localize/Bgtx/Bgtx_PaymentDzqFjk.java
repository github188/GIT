package custom.localize.Bgtx;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentDzq;
import com.efuture.javaPos.Payment.PaymentDzqFjk;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bgtx_PaymentDzqFjk extends PaymentDzqFjk
{
	public Bgtx_PaymentDzqFjk()
	{
		super();
	}
	
	public Bgtx_PaymentDzqFjk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public Bgtx_PaymentDzqFjk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public void initPayment(PayModeDef mode, SaleBS sale)
	{
		super.initPayment(mode, sale);		
	}
	
	protected boolean getPasswdBeforeFindMzk(StringBuffer passwd)
	{
//		if (GlobalInfo.sysPara.cardpasswd.equals("Y"))
//		{
//	    	TextBox txt = new TextBox();
//	    	
//	    	if (!txt.open("请输入钢印号", "PASSWORD", "需要先输入卡钢印号以后才能查询卡资料", passwd, 0, 0,false, TextBox.AllInput))
//	        {
//	            return false;
//	        }
//		}
	    return true;
	}
	
	public String convertName(String[] rows)
	{
		//采用付款方式名称
		return null;
	}
	
	
	
//	 设置金额
//    public boolean setYeShow(Table table)
//    {   
//    	// 设置余额列表
//		table.removeAll();
//		dzqList.removeAllElements();
//		
//		// yinliang test
//		//mzkret.memo = "01,A券,100,1,Y|02,B券,300,1";
//		
//		// 券余额
//		if ( mzkret.ye >0 )
//		{	
//				
//				String hl = "1";
//				String paper = "Y";			
//				String[] lines = {"01","电子券",String.valueOf(mzkret.ye),hl,paper};
//				dzqList.add(lines);
//			
//		}
//				
//    	// 显示券类型
//    	for (int i = 0 ; i < dzqList.size(); i++)
//    	{
//    		String str [] = new String[3];
//    		TableItem item = null;
//    		String row[] = (String[])dzqList.elementAt(i);
//    		
//    		// 找已刷卡的付款
//    		String yfk = "0.00";
//    		Vector v = null;
//    		if (saleBS.isRefundStatus()) v = saleBS.refundPayment;
//    		else v = saleBS.salePayment;
//			for (int n=0;n <v.size();n++)
//			{
//				SalePayDef sp = (SalePayDef)v.elementAt(n);
//				
//				if (sp.paycode.equals(paymode.code) && sp.payno.equals(mzkret.cardno) && sp.idno.equals(row[0]))
//				{
//					yfk = ManipulatePrecision.doubleToString(sp.ybje);
//					break;
//				}
//			}
//    		
//			str[0] = "["+row[0]+"]"+row[1];
//			str[1] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
//			str[2] = yfk;
//			item = new TableItem(table, SWT.NONE);
//			item.setText(str);
//    	}
//    	
//		if (dzqList.size() > 0) return true;
//		else
//		{
//			new MessageBox(Language.apply("该卡无券种余额!"));
//			return false;
//		}
//    }
    
    public boolean mzkAccount(boolean isAccount)
	{
		do
		{
			// 退货交易卡号为空时提示刷卡
			paynoMsrflag = false;
			if (!paynoMSR())
				return false;

			// 设置交易类型,isAccount=true是记账,false是撤销
			if (isAccount)
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "01"; // 消费,减
				else
					mzkreq.type = "03"; // 退货,加
			}
			else
			{
				if (SellType.SELLSIGN(salehead.djlb) > 0)
					mzkreq.type = "03"; // 退货,加
				else
					mzkreq.type = "01"; // 消费,减
			}

			// 保存交易数据进行交易
			if (!setRequestDataByAccount())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 先写冲正文件
			if (!writeMzkCz())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			// 记录面值卡交易日志
			BankLogDef bld = mzkAccountLog(false, null, mzkreq, mzkret);

			// 发送交易请求
			if (!sendMzkSale(mzkreq, mzkret))
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}

			
			
//			 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
			if (mzkreq.type == "01")
				salepay.kye -= mzkreq.je;
			else
				salepay.kye += mzkreq.je;
			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		} while (true);
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
	        
	        //交易金额>券面值不允许交易
	        if(money > this.mzkret.money)
	        {
	        	new MessageBox("付款金额不允许大于券面值！");
	        	return false;
	        }
	        	
	        	
	        
			// 生成付款行
			PaymentDzq dzq = new Bgtx_PaymentDzqFjk(paymode,saleBS);
			
			dzq.paymode = (PayModeDef)this.paymode.clone();
			dzq.paymode.hl = Convert.toDouble(rows[3]);
			dzq.salehead = this.salehead;
			dzq.saleBS = this.saleBS;
			
			dzq.mzkreq = (MzkRequestDef)mzkreq.clone();
			dzq.mzkret = (MzkResultDef)mzkret.clone();

			dzq.dzqList = this.dzqList;
			dzq.dzqIndx = index;
			dzq.dzqType = rows[4] ;
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
		// 纸券必须一次性使用完
		if (dzqType.equals("Y") && !checkMzkIsBackMoney())
		{
	        if (ManipulatePrecision.doubleCompare(salepay.ybje,this.getAccountYe(),2) != 0)
	        {
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
	
	
	protected void saveAccountMzkResultToSalePay()
	{
		// batch标记本付款方式已记账,这很重要
		salepay.batch = String.valueOf(mzkreq.seqno);

		// 标记记账返回的卡号
		if (!CommonMethod.isNull(mzkret.cardno))
			salepay.payno = mzkret.cardno;

		// new MessageBox("salepay.kye="+salepay.kye+" mzkret.ye="+mzkret.ye);
		// 后台退货时没有刷卡所以记录后台返回的卡余额,或者记账过程返回了最终余额
//		if (salepay.kye <= 0 || mzkret.ye > 0 || (mzkret.status != null && mzkret.status.equals("RETURNYE")))
//		{
//			salepay.kye = mzkret.ye;
//		}
//		else
//		{
			// 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
//			if (mzkreq.type == "01")
//				salepay.kye -= mzkreq.je;
//			else
//				salepay.kye += mzkreq.je;
////		}
		salepay.kye = 0.0;
		// new MessageBox("salepay.kye="+salepay.kye);
		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}
}
