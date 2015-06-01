package bankpay.Payment;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class ZkDzqHDL_PaymentMzk extends  Payment
{
	public MzkRequestDef mzkreq = new MzkRequestDef();
	public MzkResultDef mzkret  = new MzkResultDef();
	
	public ZkDzqHDL_PaymentMzk()
	{
		super();
	}
	
	public ZkDzqHDL_PaymentMzk(PayModeDef mode,SaleBS sale)
	{
		initPayment(mode,sale);
	}

	// 该构造函数用于红冲小票时,通过小票付款明细创建对象
	public ZkDzqHDL_PaymentMzk(SalePayDef pay,SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	
	public boolean createSalePay(String money)
	{
		if (!GlobalInfo.isOnline) 
		{
			new MessageBox("脱网时不能使用!");		
			return false;
		}
		
		setRequestDataByFind("",money,"");
		
		mzkret.money = Double.parseDouble(money);
		mzkret.ye	 = Double.parseDouble(money);
		
		if (!DataService.getDefault().sendMzkSale(mzkreq,mzkret))
		{
			 new MessageBox("调用的规则失败,无法使用!");		
			 
			 return false;
		}
		
		if (!super.createSalePay(money)) return false;
		
		if (!checkMzkMoneyValid()) 
		{
			salepay = null;
			
			return false;
		}
		
		//	付款打折业务处理
		double tempmoney = Double.parseDouble(money);
		
		// 消费金额大于余额 SellType.ISSALE(salehead.djlb)
		if (tempmoney < saleBS.calcPayBalance() && mzkret.num2 > 0)
		{
			ZkDzqHDL_PaymentMzk zkdzq = new ZkDzqHDL_PaymentMzk(paymode,saleBS);
			
			if (!zkdzq.createSalePayObject(ManipulatePrecision.doubleToString(mzkret.num2))) return false;
			
			zkdzq.salepay.paycode = mzkret.cardno;			// 付款方式编码
			zkdzq.salepay.payname = mzkret.cardname;		// 付款方式名称
			
			//	增加已付款
			saleBS.addSalePayObject(zkdzq.salepay,zkdzq);
			
			// 辅付款和主付款用num2进行关联
			zkdzq.salepay.num2 = zkdzq.salepay.num5;
			salepay.num2 = zkdzq.salepay.num5;
			
			//	计算剩余付款
			saleBS.calcPayBalance();
		}
		
		return true;
	}
	
	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		// 根据磁道生成查询请求包
		mzkreq.type = "05";		// 查询类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;		
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.invdjlb = ((salehead != null ) ? salehead.djlb : "");
		mzkreq.paycode = ((paymode != null) ? paymode.code : "");
		mzkreq.je = 0;
		mzkreq.track1 = track1;
		mzkreq.track2 = track2;
		mzkreq.track3 = track3;
		mzkreq.passwd = "";
		mzkreq.memo = "";
	}	
	
	public int getJoinPay()
	{
		for (int i = 0;i < saleBS.salePayment.size();i++)
		{
			SalePayDef spd = (SalePayDef)saleBS.salePayment.get(i);
			
			if (salepay.num2 == spd.num2) return i;
		}
		
		return -1;
	}
	
	public boolean checkMzkMoneyValid()
	{
		// 券必须一次付完,输入金额和可收金额
		if (ManipulatePrecision.doubleCompare(salepay.ybje,mzkret.ye, 2) != 0)
		{
			if (new MessageBox(salepay.payname + "的每张券必须一次性付完!\n是否将剩余部分计入损溢？", null, true).verify() == GlobalVar.Key1)
			{
				salepay.num1 = ManipulatePrecision.sub(ManipulatePrecision.mul(mzkret.ye, salepay.hl),Math.min(salepay.je, this.saleBS.calcPayBalance()));
				salepay.ybje = mzkret.ye;
				salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * salepay.hl, 2, 1);

				return true;
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}
}
