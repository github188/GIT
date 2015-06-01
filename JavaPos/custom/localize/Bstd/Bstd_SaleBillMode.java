package custom.localize.Bstd;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Bstd_SaleBillMode extends SaleBillMode
{
	protected final static int SBM_hyname = 202;

	// 用于解决实时打印时U51计算促销时会对商品明细排序，各版本对realTimePrintStartSale()进行重写
	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p, boolean flag)
	{
		try
		{
			if (!flag && GlobalInfo.sysPara != null && GlobalInfo.sysPara.isSuperMarketPop == 'Y')
			{
				Vector v = new Vector(s.size());
				v.addAll(s);

				for (int i = 0; i < s.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) s.elementAt(i);
					int index = Convert.toInt(sgd.num5);
					if (index < v.size())
						v.set(index, sgd.clone());
				}
				s = v;
			}

			salehead = h;
			salegoods = convertGoodsDetail(s);
			salepay = convertPayDetail(p);

			originalsalegoods = s;
			originalsalepay = p;

			super.salehead_temp = h;
			super.salegoods_temp = salegoods;
			super.salepay_temp = salepay;
			super.originalsalegoods_temp = s;
			super.originalsalepay_temp = p;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void setTemplateObject(SaleHeadDef h, Vector s, Vector p)
	{
		setTemplateObject(h, s, p, false);
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;

		SaleGoodsDef sgd = null;

		switch (Integer.parseInt(item.code))
		{
		case SBM_goodname: // 商品名称
			sgd = (SaleGoodsDef) salegoods.elementAt(index);

			if (GlobalInfo.sysPara.isprintpopflag == 'Y' && sgd.hjzk > 0)
				line = "(*) " + sgd.name;
			else
				line = sgd.name;

			// 记录商品所能打印的最大长度
			goodnamemaxlength = item.length;

			break;
		case SBM_cjdj: // 成交单价
			sgd = (SaleGoodsDef) salegoods.elementAt(index);
			if (sgd.flag == '2' && sgd.num4 > 0)
				line = ManipulatePrecision.doubleToString(sgd.num4);
			else
				line = ManipulatePrecision.doubleToString(ManipulatePrecision.div(sgd.hjje - sgd.hjzk, sgd.sl), 2, 1);

			break;

		case SBM_sl: // 数量
			sgd = (SaleGoodsDef) salegoods.elementAt(index);
			if (sgd.flag == '2' && GlobalInfo.sysPara.isprintdzcsl == 'Y')
				line = ManipulatePrecision.doubleToString(1 * SellType.SELLSIGN(salehead.djlb), 4, 1, true);
			else
				line = ManipulatePrecision.doubleToString(sgd.sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);

			break;

		case SBM_hyname:
			line = salehead.hykname;

			break;
		case SBM_ybje: // 付款方式金额
			SalePayDef pay = (SalePayDef) salepay.elementAt(index);
			if (pay.paycode.equals("0111") && pay.memo.equals("3"))
			{
				double je = pay.ybje * SellType.SELLSIGN(salehead.djlb);
				if (je < 0)
				{
					line = "(存入)" + ManipulatePrecision.doubleToString(je * -1, 2, 1);
					break;
				}
			}

			line = ManipulatePrecision.doubleToString(pay.ybje * SellType.SELLSIGN(salehead.djlb));
			break;
		}

		return line;
	}

	public void printSaleTicketMSInfo()
	{
		super.printSaleTicketMSInfo();

		if (this.zq == null || this.zq.size() <= 0) { return; }

		if (this.salemsinvo != 0 && salehead.fphm != this.salemsinvo)
		{
			this.salemsinvo = 0;
			this.zq = null;
			this.gift = null;
			return;
		}

		for (int i = 0; i < this.zq.size(); i++)
		{
			GiftGoodsDef def = (GiftGoodsDef) zq.elementAt(i);

			if (GiftBillMode.getDefault().checkTemplateFile())
			{
				GiftBillMode.getDefault().setTemplateObject(salehead, def);
				GiftBillMode.getDefault().PrintGiftBill();
				Printer.getDefault().cutPaper_Journal();
			}
		}
	}

	public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;

		// 分解赠品清单
		Vector fj = new Vector();
		for (int i = 0; gifts != null && i < gifts.size(); i++)
		{
			GiftGoodsDef g = (GiftGoodsDef) gifts.elementAt(i);

			if (g.type.trim().equals("-999"))
			{
				new MessageBox(g.memo);
				break;
			}
			else
			{
				fj.add(g);
			}
		}

		// 提示
		StringBuffer buff = new StringBuffer();
		double je = 0;
		for (int i = 0; i < fj.size(); i++)
		{
			GiftGoodsDef g = (GiftGoodsDef) fj.elementAt(i);
			buff.append(g.code + "   " + g.info + "      " + Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14) + "\n");
			je += g.je;
		}

		buff.append("返券总金额为: " + Convert.increaseChar(ManipulatePrecision.doubleToString(je), 14));

		if (je > 0)
			new MessageBox(buff.toString());

		// 设置
		if (fj.size() > 0)
			this.zq = fj;
		else
			this.zq = null;
	}
}
