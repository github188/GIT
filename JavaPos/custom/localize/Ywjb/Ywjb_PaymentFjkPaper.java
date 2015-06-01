package custom.localize.Ywjb;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentFjkPaper;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Ywjb_PaymentFjkPaper extends PaymentFjkPaper {
	public Ywjb_PaymentFjkPaper()
	{
		super();
	}
	
	// 查找返券卡规则
	public boolean findFjkRule(String track1, String track2, String track3)
	{
		return true;
	}
	
	public Ywjb_PaymentFjkPaper(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);

		FJKNAME_A = mode.name + "A";
		FJKNAME_B = mode.name + "B";
		FJKNAME_F = mode.name + "F";
	}
	
	public Ywjb_PaymentFjkPaper(SalePayDef pay,SaleHeadDef head)
	{
		super(pay,head);
	}
	
	public boolean calcFjkMaxJe()
	{
		try{
			if (SellType.ISBACK(saleBS.saletype))
			{
				fjkAMaxJe = 9999999;
				fjkBMaxJe = 9999999;
				return true;
			}
			else
			{
				return super.calcFjkMaxJe();
			}
		}catch(Exception er)
		{
			er.printStackTrace();
			new MessageBox(er.getMessage());
			return false;
		}
		
	}
	
	public boolean findFjk(String track1, String track2, String track3)
	{
		try{
			if (SellType.ISBACK(saleBS.saletype) && !track2.equals("0000"))
			{
				new MessageBox("在退货状态下输入的券号必须为 0000 ");
				return false;
			}
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
		return super.findFjk(track1, track2, track3);
	}

	public boolean checkMzkMoneyValid()
	{
		try{
			if (mzkreq.track2.equals("0000"))
			{
				return true;
			}
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
		return super.checkMzkMoneyValid();
	}
	
	protected String getDisplayAccountInfo()
	{
		return "输入券号";
	}
	
	public boolean isAcceptFjkRule()
	{
		if (paymode.code .equals("0520"))
			return false;
		else
			return super.isAcceptFjkRule();
	}
	
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		boolean done = false;
		if (paymode.code.equals("0520"))
		{
			// 0520调用总部CRM，所以通过传入 CmdDef.GETCUSTSELLJF 获取HTTP
			Http h =NetService.getDefault().getMemCardHttp(CmdDef.SENDFJK);
			//new MessageBox(h.getSvrURL());
			done = NetService.getDefault().sendFjkSale(h,req,ret);
			 			
		}
		else
		{
			Http h = NetService.getDefault().getMemCardHttp(11);
			//new MessageBox(h.getSvrURL());
			done = NetService.getDefault().sendFjkSale(NetService.getDefault().getMemCardHttp(11),req, ret);
			
		}
		
		// 根据券号查找返回券的类型
		if (req.type.equals("05") && ret.ispw == 'N' && ret.cardpwd != null && ret.cardpwd.trim().length() > 0)
		{
			this.setAccountYeType(ret.cardpwd);
		}
		
		return done;
	}
}
