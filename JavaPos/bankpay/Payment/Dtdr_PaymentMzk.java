package bankpay.Payment;

import java.io.BufferedReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Dtdr_PaymentMzk extends PaymentMzk
{
	private String mktid;
	private String connUrl;
	private String username;
	private String password;

	private Connection conn = null;
	private CallableStatement callst = null;

	private MzkRequestDef mzkReqDef;
	private MzkResponsDef mzkResDef;

	public Dtdr_PaymentMzk()
	{
		super();
	}

	public Dtdr_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Dtdr_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean readConfig()
	{
		BufferedReader br = null;
		String line;
		String[] sp;

		try
		{
			br = CommonMethod.readFile(GlobalVar.ConfigPath + "/oldMzk.ini");

			if (br == null)
			{
				new MessageBox("配置文件读取失败");
				return false;
			}

			while ((line = br.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;

				if (sp[0].trim().compareToIgnoreCase("mktid") == 0)
				{
					mktid = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("connUrl") == 0)
				{
					connUrl = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("username") == 0)
				{
					username = sp[1].trim();
				}
				else if (sp[0].trim().compareToIgnoreCase("password") == 0)
				{
					password = sp[1].trim();
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
			try
			{
				if (br != null)
					br.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			br = null;
		}

	}

	public boolean close()
	{
		try
		{
			if (callst != null)
				callst.close();

			if (conn != null)
				conn.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			callst = null;
			conn = null;
		}
		return true;
	}

	public boolean collectAccountPay()
	{
		return true;
	}

	public boolean findMzk(String track1, String track2, String track3)
	{
		if (!readConfig())
			return false;

		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("磁道数据为空!");
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

		return sendMzkSale();
	}

	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		mzkReqDef = new MzkRequestDef();

		mzkReqDef.batch = "";
		// 根据磁道生成查询请求包
		mzkReqDef.IFDLX = mktid;
		mzkReqDef.ICARD_NO = track2;

		String syjh = "000" + GlobalInfo.syjDef.syjh;
		syjh = syjh.substring(syjh.length() - 3);
		String fphmstr = "000000" + String.valueOf(GlobalInfo.syjStatus.fphm);
		fphmstr = fphmstr.substring(fphmstr.length() - 6);
		fphmstr = syjh + new ManipulateDateTime().getDateByEmpty() + fphmstr;

		mzkReqDef.IJLBH = fphmstr;
		mzkReqDef.SKTNO = syjh;
		mzkReqDef.RYDM = GlobalInfo.posLogin.gh;
		mzkReqDef.PERSON_NAME = GlobalInfo.posLogin.name;
		mzkReqDef.NO = "02";
		mzkReqDef.IXSJE = "0";
	}

	protected boolean setRequestDataByAccount(boolean flag)
	{
		// 根据磁道生成查询请求包
		mzkReqDef.batch = String.valueOf(getMzkSeqno());
		mzkReqDef.NO = "00";

		if (flag)
		{
			// 消费扣款
			if (SellType.ISSALE(saleBS.saletype))
				mzkReqDef.IXSJE = String.valueOf(salepay.ybje);

			// 退货扣款
			if (SellType.ISBACK(saleBS.saletype))
				mzkReqDef.IXSJE = String.valueOf(salepay.ybje * -1);
		}
		else
		{
			if (SellType.ISSALE(saleBS.saletype))
				mzkReqDef.IXSJE = String.valueOf(salepay.ybje * -1);

			if (SellType.ISBACK(saleBS.saletype))
				mzkReqDef.IXSJE = String.valueOf(salepay.ybje * 1);
		}

		return true;
	}

	public int getAccountInputMode()
	{
		return TextBox.MsrRetTracks;
	}

	public String getDisplayCardno()
	{
		return "************";
	}

	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		return true;
	}

	public boolean createSalePay(String money)
	{
		try
		{
			return super.createSalePay(money);
		}
		finally
		{
			close();
		}
	}

	public void showAccountYeMsg()
	{

	}

	public boolean realAccountPay()
	{
		// 付款即时记账
		if (mzkAccount(true))
			return true;

		return false;
	}

	public boolean cancelPay()
	{
		// 付款即时记账
		if (mzkAccount(false))
			return true;

		return false;
	}

	public boolean mzkAccount(boolean isAccount)
	{
		// 保存交易数据进行交易
		if (!setRequestDataByAccount(isAccount))
			return false;

		// 发送交易请求
		if (!sendMzkSale())
			return false;

		if (this.saleBS != null)
			this.saleBS.writeBrokenData();

		return true;
	}

	public boolean sendMzkSale()
	{
		if (!createConnection())
			return false;

		if (!setProcedure())
			return false;

		if (!checkSuccessed())
			return false;

		return true;
	}

	private boolean checkSuccessed()
	{
		if (mzkReqDef.NO.equals("02") && mzkResDef.S_NO.equals("20"))
		{
			mzkret.cardno = mzkReqDef.ICARD_NO;
			if (mzkResDef.S_MONEY != null && !mzkResDef.S_MONEY.trim().equals(""))
				mzkret.ye = Double.parseDouble(mzkResDef.S_MONEY);

			return true;
		}

		if (mzkReqDef.NO.equals("00") && mzkResDef.S_NO.equals("99"))
		{
			salepay.batch = mzkReqDef.batch;
			salepay.idno = mzkReqDef.ICARD_NO;

			if (mzkResDef.S_MONEY != null && !mzkResDef.S_MONEY.trim().equals(""))
				salepay.kye = Double.parseDouble(mzkResDef.S_MONEY);

			return true;
		}

		new MessageBox("交易失败:" + mzkResDef.S_INFO);
		return false;
	}

	private boolean setProcedure()
	{
		try
		{
			if (callst == null)
			{
				String call = "{call DR_CARD(?,?,?,?,?,?,?,?)}";
				callst = conn.prepareCall(call);

				if (callst == null)
				{
					new MessageBox("创建syBase过程调用对象失败");
					return false;
				}
			}

			if (mzkReqDef == null)
			{
				new MessageBox("面值卡请求参数不合法");
				return false;
			}

			// new MessageBox("开始执行...");
			// new MessageBox("IFDLX:" + mzkReqDef.IFDLX);
			callst.setString(1, mzkReqDef.IFDLX);
			// new MessageBox("ICARD_NO:"+mzkReqDef.ICARD_NO);
			callst.setString(2, mzkReqDef.ICARD_NO);
			// new MessageBox("IJLBH:"+mzkReqDef.IJLBH);
			callst.setString(3, mzkReqDef.IJLBH);
			// new MessageBox("SKTNO:"+mzkReqDef.SKTNO);
			callst.setString(4, mzkReqDef.SKTNO);
			// new MessageBox("RYDM:"+mzkReqDef.RYDM);
			callst.setString(5, mzkReqDef.RYDM);
			// new MessageBox("PERSON_NAME:"+mzkReqDef.PERSON_NAME);
			callst.setString(6, mzkReqDef.PERSON_NAME);
			// new MessageBox("NO:"+mzkReqDef.NO);
			callst.setString(7, mzkReqDef.NO);
			//new MessageBox("IXSJE:" + mzkReqDef.IXSJE);
			callst.setString(8, mzkReqDef.IXSJE);

			ResultSet rs = callst.executeQuery();

			if (rs == null)
			{
				new MessageBox("面值卡查询结果集返回Null");
				return false;

			}

			mzkResDef = new MzkResponsDef();

			while (rs.next())
			{
				mzkResDef.S_NO = rs.getString(1);
				mzkResDef.S_INFO = rs.getString(2);

				mzkResDef.S_CARDNO = rs.getString(3);
				this.mzkret.cardno = mzkResDef.S_CARDNO;

				mzkResDef.S_MONEY = rs.getString(4);
				this.mzkret.ye = Convert.toDouble(mzkResDef.S_MONEY);
			}

			if (ConfigClass.DebugMode)
				new MessageBox(mzkResDef.S_NO + "|" + mzkResDef.S_INFO + "|" + mzkResDef.S_CARDNO + "|" + mzkResDef.S_MONEY);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("面值查询卡过程调用失败:" + ex.getMessage());
			return false;
		}
	}

	public boolean createConnection()
	{
		try
		{
			if (conn != null)
				return true;

			Class.forName("com.sybase.jdbc3.jdbc.SybDataSource").newInstance();
			// Class.forName("com.sybase.jdbc2.jdbc.SybDriver").newInstance();

			conn = DriverManager.getConnection(connUrl, username, password);

			if (conn == null)
			{
				new MessageBox("创建syBase数据库连接失败");
				return false;
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("创建syBase数据库连接失败:" + ex.getMessage());
			return false;
		}
	}

	class MzkRequestDef
	{
		public String batch;
		public String IFDLX; // 分店编号05德润超市 06亲子乐园
		public String ICARD_NO; // 开储值卡卡号
		public String IJLBH; // 小票流水号
		public String SKTNO; // 收银机号
		public String RYDM; // 工号
		public String PERSON_NAME; // 收银员名称
		public String NO; // 输入02为查询卡的状态和余额00为消费
		public String IXSJE; // 销售金额
	}

	class MzkResponsDef
	{
		public String S_NO;
		public String S_INFO;
		public String S_CARDNO;
		public String S_MONEY;

		public boolean isInputPwd;// 1
		public String cardtype; // 2-3
		public int acctCount;// 4-5
		public String defaultAcct;// 6-7
		public String typename;// 8...

		public boolean convertCardInfo()
		{
			if (S_CARDNO == null || S_CARDNO.equals("") || S_CARDNO.length() < 8)
			{
				new MessageBox("卡类型数据不合法");
				return false;
			}

			isInputPwd = S_CARDNO.charAt(0) == '0' ? true : false;
			cardtype = S_CARDNO.substring(1, 3);
			acctCount = Convert.toInt(S_CARDNO.substring(3, 5));
			defaultAcct = S_CARDNO.substring(5, 7);
			typename = S_CARDNO.substring(7);
			return true;
		}
	}
}
