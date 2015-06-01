package bankpay.Payment;

import java.net.Socket;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.SocketService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class JLCZK_PaymentMzk extends PaymentMzk
{	
	public static int SALE = 1; //消费
	public static int SALE_HC = 2; //消费红冲
	public static int BACK = 3; //退货
	public static int BACK_HC = 4; //退货红冲
	public static int SEARCH = 5; //查询余额
	
	Socket s = null;
	public JLCZK_PaymentMzk()
	{
		super();
	}
	
	public JLCZK_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}
	
	public JLCZK_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}
	
	
	
	public void initPayment(PayModeDef mode,SaleBS sale)
	{
		super.initPayment(mode, sale);
	}
	
	public void initPayment(SalePayDef pay,SaleHeadDef head)
	{
		super.initPayment(pay, head);
	}
	
	public boolean cancelPay()
	{
		return true;
	}
	
	public boolean realAccountPay()
	{
		// 全部输入完后才开始付款		
		return true;
	}
	
	public boolean createSalePay(String money)
	{
		if (super.createSalePay(money))
		{
			salepay.str4 = mzkreq.passwd;
			return true;
		}
		
		return false;
	}
	
	public String decodeStr(int type,String str)
	{
		
		if (str == null) return null;
		if (type == SEARCH)
		{
			//1	包长	PKGLEN	4	
			//2	交易代码	PCODE	2	13 – 卡余额查询回应包
			//3	回应码	RCODE	2	01 – 交易成功
			//4	收款台号	POSID	4	
			//5	收银员号	CASHID	4	
			//6	卡号	CARDID	16	
			//7	卡余额	LMONEY	(8,2)	
			//8	卡积分	LSCORE	6	
			//9	卡有效期	VALIDDATE	10	
			//10	折扣帐户类型	DISTYPE	2	
			//11	折扣帐户余额	DISBALANCE	（8，2）	
			//12	折扣帐户扣率	DISRATA	(8,2)	
			//13	折扣有效期	DISVALIDDATE	10	
			//14	卡附属状态	OTHERSTATUS	8	

			//String leg = Convert.newSubString(str, 0, 4);
			
			String PCODE = Convert.newSubString(str, 4, 6);
			if (!PCODE.equals("13"))
			{
				new MessageBox("回应包PCODE不为13，接受的回应包不正确");
				return null;
			}
			String RCODE = Convert.newSubString(str, 6, 8);
			if (!RCODE.equals("01"))
			{
				String msg = "";
				
				msg += "03 – 卡不存在\n";
				msg +="04 – 卡无效（过有效期）\n";
				msg +="05 – 已挂失\n";
				msg +="06 – 已冻结\n";
				msg +="07 – 已清户\n";
				msg +="08 – 密码错误\n";
				msg +="09 – 卡未启用\n";
				msg +="10 – 处理失败\n";
				msg +="11 – 卡已作废\n";
				msg +="12 – 卡临时挂失\n";
				new MessageBox("卡余额查询失败 "+RCODE+"\n"+msg);
				return null;
			}
			
			
			
			
			mzkret = new MzkResultDef();
			mzkret.cardno = Convert.newSubString(str, 16, 32);
			mzkret.ye = Convert.toDouble(Convert.newSubString(str, 32, 40).trim());
			String VALIDDATE = Convert.newSubString(str, 46, 56);
			ManipulateDateTime a = new ManipulateDateTime();
			if (a.compareDate(VALIDDATE, a.getDateBySign()) < 0)
			{
				new MessageBox("此卡有效期为 "+ VALIDDATE+"\n已过期或者作废");
				return null;
			}
			mzkret.str1 = VALIDDATE;
			
			return mzkret.cardno;
		}
	
		return null;
	}
	
	public String encodeStr(int type,double money,String track1,String track2,String track3,StringBuffer buff)
	{
		/**
		//1	包长	PKGLEN	4	
		//2	交易代码	PCODE	2	10 – 卡消费请求包
		String PCODE = "10";
		//3	回应码	RCODE	2	00 － 消费请求
		String RCODE = "00";
		//4	收款台号	POSID	4
		String POSID = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, 4);
		//5	收银员号	CASHID	4
		String CASHID = Convert.increaseCharForward(GlobalInfo.posLogin.gh, 4);
		//6	交易流水号	SEQID	10
		String SEQID = Convert.increaseCharForward("", 10);
		//7	消费金额	MONEY	（8,2）
		String MONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(money), 8);
		//8	消费总金额	TMONEY	（8,2）	所有支付方式的总金额
		String TMONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
		//9	找零金额	CHARGE	（8,2）
		String CHARGE = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
		//10	折扣帐户类型	DISTYPE	2	
		String DISTYPE = Convert.increaseCharForward("00", 2);
		//11	折扣帐户存入额	DISDEPOSITMONEY	(8,2)	
		String DISDEPOSITMONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
		//12	折扣帐户使用金额	DISUSEMONEY	(8,2)
		String DISUSEMONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
		//13	折扣存商品明细	DISINDETAIL	不定长	
		
		//14	卡号	CARDID	16	
		String CARDID = Convert.increaseCharForward("", 16);
		//15	卡密码	CARDPWD	8	
		String CARDPWD = Convert.increaseCharForward("", 8);
		//16	卡消费金额	CMONEY	(8, 2)	
		String CMONEY = Convert.increaseCharForward(ManipulatePrecision.doubleToString(0), 8);
		 */
		if (type == SALE)
		{
			
		}
		else if (type == SALE_HC)
		{
			
		}
		else if (type == BACK)
		{
			
		}
		else if (type == BACK_HC)
		{
			
		}
		else if (type == SEARCH)
		{
			//	包长	PKGLEN	4	
			//	交易代码	PCODE	2	12 – 卡余额查询交易
			//	回应码	RCODE	2	00 – 查询请求
			//	收款台号	POSID	4	
			//	收银员号	CASHID	4	
			//	卡号	CARDID	16	
			//	卡密码	CARDPWD	8	

			String PCODE = "12";
			String RCODE = "00";
			String POSID = Convert.increaseCharForward(GlobalInfo.syjDef.syjh, 4);
			String CASHID = Convert.increaseCharForward(GlobalInfo.posLogin.gh, 4);
			
			if (track2.length() != 24)
			{
				new MessageBox("卡磁道号不为24位长!请检查卡");
				return null;
			}
			
			String cardno = track2.substring(0,16);
			String pwd = track2.substring(16);
			
			mzkreq.track2 = track2;
			mzkreq.passwd = pwd;
			buff.append(pwd);
			//StringBuffer passwd = new StringBuffer();
			//TextBox txt = new TextBox();
			
			/**
			if (!txt.open("请输入密码", "PASSWORD", "需要先输入卡密码以后才能查询卡资料", passwd, 0, 0,false, TextBox.AllInput))
	        {
	            return null;
	        }*/
			
			String line = PCODE+RCODE+POSID+CASHID+cardno+pwd;
			line = Convert.increaseCharForward(String.valueOf(line.length()),'0', 4)+line;
			return line;
		}
		return null;
		
	}
	
	public boolean collectAccountPay()
	{
		return true;
	}

	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		System.out.println("find "+track2);
		// 设置请求数据
		StringBuffer buff = new StringBuffer();
		String info = encodeStr(SEARCH,0,track1,track2,track3,buff);
		System.out.println("encode "+info);
		//SocketService soc = SocketService.getDefault(0);
		String retline = SocketService.getDefault(0).sendMessage(info, null);
		//String retline = "";
		if(decodeStr(SEARCH,retline) == null) return false;
		return true;
		
	}
	
	
	public boolean findMzk(String track1, String track2, String track3)
	{
		return findMzkInfo(track1,track2,track3);
	}
	
	/**
	public boolean mzkAccount(boolean isAccount)
	{
		// 设置交易类型,isAccount=true是记账,false是撤销
		String oldseqno = null;
		if (isAccount)
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0) type = PaymentBank.XYKXF;
			else type = SellType.ISHC(salehead.djlb)?PaymentBank.XYKCX:PaymentBank.XYKTH;
		}
		else
		{
			if (SellType.SELLSIGN(salehead.djlb) > 0) type = PaymentBank.XYKCX;
			else type = PaymentBank.XYKXF;
			
			// str1为撤销的原流水
			oldseqno = salepay.str1;
		}
		
		// 调用银联接口
		boolean ret = pbfunc.callBankFunc(type, salepay.ybje, mzkreq.track1, mzkreq.track2, mzkreq.track3, oldseqno, null, null, null);
		if (!ret)
		{
			// 如果是显示ERROR消息模式，则弹出错误信息
			if (pbfunc.getErrorMsgShowMode())
			{
				new MessageBox(pbfunc.getErrorMsg());
			}
			
			return ret;
		}
		else
		{
			// 标记记账成功,卡内交易后余额记账
			BankLogDef bld = pbfunc.getBankLog();
			mzkret.cardno = bld.cardno;
			mzkret.ye     = (bld.memo != null && bld.memo.length() > 0)?Convert.toDouble(bld.memo):0;
			
			salepay.payno = bld.cardno;
			salepay.batch = String.valueOf(bld.trace);
			salepay.str1  = salepay.batch;
			salepay.idno  = salepay.batch;
			salepay.ybje  = bld.je;
			salepay.je    = ManipulatePrecision.doubleConvert(salepay.ybje * ((paymode!=null)?paymode.hl:1));
			if (salepay.kye <= 0 || mzkret.ye > 0) salepay.kye = mzkret.ye;
	    	else
	    	{
	        	// 记账过程没有返回最终余额，以查询的余额做基准加减计算新余额
	    		if (mzkreq.type == "01") salepay.kye -= mzkreq.je;
	     		else salepay.kye += mzkreq.je;
	    	}
	    	salepay.kye   = ManipulatePrecision.doubleConvert(salepay.kye);
			
			// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
			if (this.saleBS != null) this.saleBS.writeBrokenData();
		
			return true;
		}
	}
	*/
	
	public boolean allowMzkOffline()
	{
		return true;
	} 
	
}
