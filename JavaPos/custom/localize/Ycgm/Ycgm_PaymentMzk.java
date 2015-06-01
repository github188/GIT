package custom.localize.Ycgm;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentCust;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

import custom.localize.Bcrm.Bcrm_PaymentMzk;

public class Ycgm_PaymentMzk extends PaymentCust
{	
	public String no = "";
	public String name = "";
	
	public Ycgm_PaymentMzk()
	{
		super();
	}
	
	public Ycgm_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Ycgm_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		super(pay,head);
	}
	
	// 保存交易数据进行交易
	protected boolean setRequestDataByAccount()
	{
		// 得到消费序号
		long seqno = getMzkSeqno();
		if (seqno <= 0)
			return false;

		// 打消费交易包
		mzkreq.seqno = seqno;
		mzkreq.je = salepay.ybje;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.paycode = salepay.paycode;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");

		// 告诉后台过程磁道信息是存放的是卡号,只采用卡号记账方式,不使用磁道记账方式
//		mzkreq.track1 = "CARDNO";
//		mzkreq.track2 = salepay.payno;

		return true;
	}
	
//    public int choicTrackType()
//	{
//    	mzkTrackType = -1;
//    	
//    	//如果手输情况下若不让用零钱包，则直接采用刷卡
//    	if( GlobalInfo.sysPara.isusecoinbag == 'N')
//    		return mzkTrackType;
//    	
//		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
//		// 获取自定义的解析规则
//		rulelist = bs.showRule();
//		if (rulelist != null && rulelist.size() <= 0) rulelist = null;
//
//		if (rulelist != null)
//		{
//			for (int i = 0; i < rulelist.size(); i++)
//			{
//				CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);
//				
//				if (filterDef.ispay == 1 )
//				{
//					rulelist.removeElementAt(i);
//					i--;
//				}
//			}
//		}
//		
//		// 先选择规则后刷会员卡 ，
//		if (GlobalInfo.sysPara.unionVIPMode == 'A')
//		{
//			if (rulelist != null && rulelist.size() > 1)
//			{
//				Vector con = new Vector();
//				for (int i = 0; i < rulelist.size(); i++)
//				{
//					CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);
//					
//					con.add(new String[] { filterDef.desc });
//				}
//				
//				String[] title = { Language.apply("会员卡类型") };
//				int[] width = { 500 };
//
//				int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
//				mzkTrackType = choice;
//				
//				if (choice != -1)
//				{
//					CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
//					rulelist.removeAllElements();
//					rulelist.add(rule);
//				}
//				if (rulelist != null) ch = true;
//			}
//		}
//
//		return mzkTrackType;
//	}
    
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox(Language.apply("磁道数据为空!"));
			return false;
		}

		// 解析磁道
		String[] s = parseTrack(track1, track2, track3);
		if (s == null)
			return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];

		// 设置请求数据
		setRequestDataByFind(track1, track2, track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}

		//
		return sendMzkSale(mzkreq, mzkret);
	}
	
	//发送交易请求数据,接受返回数据
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		//查询
		if (req.type.equals("05"))
		{
			if(!queryCardInfo(req,ret))
			{
				
				return false;
			}
		}
		else if (req.type.equals("01")) //消费
		{
			if (null == salehead)//小票头为空无法设置,获取TransID
			{
				new MessageBox("小票头对象为空，无法得到交易ID");
				return false;
			}
			
			if(!consumption(salehead,req, ret))
			{
				
				return false;
			}
		}
		else if (req.type.equals("02")) //消费冲正
		{
			if(!AccountCz(req, ret))
			{
				
				return false;
			}
		}
		else if (req.type.equals("03")) //消费退货
		{
			if (null == salehead)//小票头为空无法设置,获取TransID
			{
				new MessageBox("小票头对象为空，无法得到交易ID");
				return false;
			}
			
			//同程消费退货和消费调用同一个接口，把金额修改为负数
			req.je = (-req.je);
			if(!consumption(salehead,req, ret))
			{
				
				return false;
			}
		}
		else if (req.type.equals("04")) //消费退货冲正
		{
			if(!AccountCz(req, ret))
			{
				
				return false;
			}
		}
		else
		{
			new MessageBox("该付款类型不支持" + req.type + "交易类型");
			
			return false;
		}
		
		return true;
	}
	
	//设置面值卡应答数据
	public boolean setResultData(String line)
	{
		mzkret.cardno = "";
		
		return true;
	}
	
	//查询卡信息
	public boolean queryCardInfo(MzkRequestDef req, MzkResultDef ret)
	{
		String rs = Excute.queryJfOrCzInfo(req.track2, "", req.passwd);
		if (rs == null || "".equals(rs))
			return false;
		ret.func = "YYCNN";
		ret.ye = Convert.toDouble(rs.substring(72, 85).trim());
			
//		if (ret.ye <= 0)
//		{
//			new MessageBox("卡余额为" + ret.ye + ",请更换张卡");
//		}
		
		ret.str1 = rs.substring(85,93);
		no = ret.cardno = Convert.newSubString(rs, 93,113).trim();
		name = ret.cardname = Convert.newSubString(rs,113,133).trim();
		return true;
	}
	
	//消费交易
	public boolean consumption(SaleHeadDef salehead,MzkRequestDef req, MzkResultDef ret)
	{
		String rs = Excute.trans(salehead,req.track2, req.paycode, req.je);
		if (rs == null || "".equals(rs))
			return false;
		
		//交易成功后，记录同程CRM中交易ID，用来冲正。同程CRM积点消费是通过交易ID来查找信息的
		req.str2 = salehead.str6;
		
		ret.func = "YYCNN";
		ret.cardno = no;
		ret.cardname = name;
		ret.ye = Double.parseDouble(rs.substring(47, 60));
		ret.memo = rs.substring(7,39);
		
		
		//交易成功后，记录磁道和密码，为提交交易数据提供信息
		salehead.str4 = req.track2;
		salehead.str5 = req.passwd;
		
		return true;
	}
	
	//消费冲正
	public boolean AccountCz(MzkRequestDef req, MzkResultDef ret)
	{
		String rs = Excute.saleOrTransBack();
		if (rs == null || "".equals(rs))
			return false;
		
		ret.func = "YYCNN";
		ret.cardno = no;
		ret.cardname = name;
		//ret.memo = rs.substring(8, 40);
		return true;
	}
	
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

			// 先写冲正文件
			//积点支付时，在MzkRequestDef.str2字段保存了同程CRM交易ID，
			//而交易ID只有在交易成功后才能获得，因此这个把冲正文件改到交易发生成功之后记录
			if (!writeMzkCz())
			{
				if (paynoMsrflag)
				{
					salepay.payno = "";
					continue;
				}
				return false;
			}
			
			// 记录应答信息, batch标记本付款方式已记账,这很重要
			saveAccountMzkResultToSalePay();

			// 记账完成操作,可用于记录记账日志或其他操作
			return mzkAccountFinish(isAccount, bld);
		} while (true);
	}
	
	protected boolean saveFindMzkResultToSalePay()
	{
		salepay.batch = "";
		salepay.payno = mzkret.cardno;
		salepay.kye = mzkret.ye;
		
		//记录积点余额，方便打印时取值
		salehead.str8 = mzkret.ye + "";

		return true;
	}
}
