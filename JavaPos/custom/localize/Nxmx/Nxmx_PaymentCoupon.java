package custom.localize.Nxmx;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentDzq;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Nxmx_PaymentCoupon extends PaymentDzq
{
	private String curTradeType = "XX";
	private double activemaxje = 0.0;

	public Nxmx_PaymentCoupon()
	{
		super();
	}

	public Nxmx_PaymentCoupon(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Nxmx_PaymentCoupon(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}

	public boolean activeCoupon(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && (track2 == null || track2.trim().length() <= 0) && (track3 == null || track3.trim().length() <= 0))
		{
			new MessageBox("券号为空!");
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
		setRequestDataByActive(track1, track2, track3);

		return sendMzkSale(mzkreq, mzkret);
	}

	public boolean checkMzkIsBackMoney()
	{
		return true;
	}

	public boolean createSalePay(int index, double money)
	{
		try
		{
			if (this.paymode.code.equals("0302"))
				money = Math.min(money, calcPayRuleMaxMoney());
			else
			{
				if (calcPayRuleMaxMoney() > 0)
					money = calcPayRuleMaxMoney();
			}

			String[] rows = (String[]) dzqList.elementAt(index);

			// 删除付款集合中同付款卡号同券种的付款行
			if (!deleteSamePayment(this.paymode.code, this.mzkret.cardno, this.curpayflag))
			{
				new MessageBox("删除已存在的付款行错误!");
				return false;
			}

			// 金额<=0可清除已存在的付款行
			if (money <= 0)
				return true;

			// 生成付款行
			PaymentDzq dzq = new Nxmx_PaymentCoupon(paymode, saleBS);

			dzq.paymode = (PayModeDef) this.paymode.clone();
			dzq.paymode.hl = Convert.toDouble(rows[3]);
			dzq.salehead = this.salehead;
			dzq.saleBS = this.saleBS;

			dzq.mzkreq = (MzkRequestDef) mzkreq.clone();
			dzq.mzkret = (MzkResultDef) mzkret.clone();

			dzq.dzqList = this.dzqList;
			dzq.dzqIndx = index;
			dzq.dzqType = rows[4];
			if (dzq.dzqType.equals("Y")) // 纸券溢余不找零
			{
				dzq.paymode.isyy = 'Y';
				dzq.paymode.iszl = 'N';
			}

			dzq.mzkret.ye = Convert.toDouble(rows[2]); // 余额
			dzq.mzkreq.memo = rows[0]; // 券种

			dzq.allowpayje = this.allowpayje;
			if (this.allowgoods != null)
				dzq.allowgoods = (Vector) this.allowgoods.clone();
			else
				dzq.allowgoods = null;
			dzq.allowpayjealready = this.allowpayjealready;
			dzq.curpayflag = this.curpayflag;

			// 创建付款对象
			if (dzq.createSalePay(String.valueOf(money)))
			{
				// 改变付款方式名称并增加到已付款
				dzq.salepay.payname = rows[1];
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					dzq.salepay.payname += "扣回";
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

	/*
	 * public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret) {
	 * ret.cardno = "0123456789"; ret.cardname = "模拟测试卡"; ret.status= "1";
	 * ret.money = 100; ret.memo ="12345,45678"; ret.ye = 100;
	 * ret.memo="01,5元券,5,1,Y"; new MessageBox("当前为模拟测试数据的虚假数据,仅供测试部分流程!!!");
	 * return true;
	 * 
	 * //return DataService.getDefault().sendMzkSale(req,ret); }
	 */

	public void setRequestDataByActive(String track1, String track2, String track3)
	{
		// 根据磁道生成查询请求包
		mzkreq.type = getCurTradeType(); // 查询类型
		mzkreq.seqno = 0;
		mzkreq.termno = ConfigClass.CashRegisterCode;
		mzkreq.mktcode = GlobalInfo.sysPara.mktcode;
		mzkreq.syyh = GlobalInfo.posLogin.gh;
		mzkreq.syjh = ConfigClass.CashRegisterCode;
		mzkreq.fphm = GlobalInfo.syjStatus.fphm;
		mzkreq.invdjlb = ((salehead != null) ? salehead.djlb : "");
		mzkreq.paycode = ((paymode != null) ? paymode.code : "");
		mzkreq.je = getActivemaxje();
		mzkreq.track1 = track1;
		mzkreq.track2 = track2;
		mzkreq.track3 = track3;
		mzkreq.passwd = "";
		mzkreq.memo = "";
	}

	public boolean setYeShow(Table table)
	{
		// 设置余额列表
		table.removeAll();
		dzqList.removeAllElements();

		// yinliang test
		// mzkret.memo = "01,A券,100,1,Y|02,B券,300,1";

		// 分解余额
		if (mzkret.memo != null && mzkret.memo.trim().length() > 0)
		{
			String row[] = mzkret.memo.split("\\|");
			if (row.length <= 0)
				return false;

			for (int i = 0; i < row.length; i++)
			{
				String line[] = row[i].split(",");
				if (line.length < 3)
					continue;

				String hl = "1";
				String paper = "N";
				if (line.length > 3)
					hl = line[3].trim();
				if (line.length > 4)
					paper = line[4].trim();

				String[] lines = { line[0].trim(), line[1].trim(), line[2].trim(), hl, paper };
				dzqList.add(lines);
			}
		}

		// 显示券类型
		for (int i = 0; i < dzqList.size(); i++)
		{
			String str[] = new String[3];
			TableItem item = null;
			String row[] = (String[]) dzqList.elementAt(i);

			// 找已刷卡的付款
			String yfk = "0.00";
			Vector v = null;
			if (saleBS.isRefundStatus())
				v = saleBS.refundPayment;
			else
				v = saleBS.salePayment;
			for (int n = 0; n < v.size(); n++)
			{
				SalePayDef sp = (SalePayDef) v.elementAt(n);

				if (sp.paycode.equals(paymode.code) && sp.payno.equals(mzkret.cardno) && sp.idno.equals(row[0]))
				{
					yfk = ManipulatePrecision.doubleToString(sp.ybje);
					break;
				}
			}

			str[0] = "[" + row[0] + "]" + row[1];
			str[1] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));

			if (this.paymode.code.equals("0302"))
				str[2] = String.valueOf(calcPayRuleMaxMoney());
			else
				str[2] = yfk;
			item = new TableItem(table, SWT.NONE);
			item.setText(str);
		}

		if (dzqList.size() > 0)
			return true;
		else
		{
			new MessageBox("该卡无券种余额!");
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
			if (allowpayje >= 0)
			{
				double showprice = saleBS.getDetailOverFlow(allowpayje);
				line = "[" + rows[0] + "]" + rows[1] + "的限制金额为: " + ManipulatePrecision.doubleToString(allowpayje) + " 元";
				allowpayje = Math.max(allowpayje, showprice);
			}
			else
			{
				if (this.paymode.code.equals("0302"))
					line = "该券未指定关联商品,无法使用";
			}

		}
		else
		{

			allowpayje = -1;
		}
		return line;
	}

	public String getCurTradeType()
	{
		return curTradeType;
	}

	public void setCurTradeType(String curTradeType)
	{
		this.curTradeType = curTradeType;
	}

	public double getActivemaxje()
	{
		return activemaxje;
	}

	public void setActivemaxje(double activemaxje)
	{
		this.activemaxje = activemaxje;
	}
}
