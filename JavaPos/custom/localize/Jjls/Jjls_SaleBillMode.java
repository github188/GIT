package custom.localize.Jjls;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Jjls_SaleBillMode extends Cmls_SaleBillMode
{
	protected final static int SBM_Kzk = 301; // 卡折扣
	protected final static int SBM_HDzk = 302; // 活动折扣
	protected final static int SBM_FLzk = 303; // 返利折扣
	protected final static int SBM_Jje = 304; // 净额
	protected final static int SBM_CUSTTYPENAME = 305; // 卡类别名称
	protected final static int SBM_POPDESC = 306; // 活动描述
	
	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
			
				case SBM_ye:
					SalePayDef sp = (SalePayDef)salepay.get(index);
					if (GlobalInfo.sysPara.yePrintPayCode.length() > 0)
					{
						if (("," + GlobalInfo.sysPara.yePrintPayCode + ",").indexOf("," + sp.paycode + ",") > -1)
						{
							if (sp.kye <= 0)
							{
								line = null;
							}
							else
							{
								line = ManipulatePrecision.doubleToString(sp.kye);
							}
						}
					}
				break;
					
				default:
					return super.getItemDataString(item, index);
			}
		}
		
		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text !=
		// null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);
			
			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
	}

	public void printBill()
	{
		SalePayDef sp = null;
		double cardSaleJe = 0; // 卡金额
		double promotionSaleJe = 0; // 活动金额
		double cardRebateJe = 0; // 返利金额
		for (int i = 0; i < salepay.size(); i++)
		{
			sp = (SalePayDef) salepay.get(i);
			if (("," + GlobalInfo.sysPara.cardDiscountPayCode + ",").indexOf("," + sp.paycode + ",") > -1)
			{
				if (sp.payno != null && sp.payno.charAt(0) == '3')
				{
					cardRebateJe += sp.je;
				}
				else
				{
					cardSaleJe += sp.je;
				}
				continue;
			}
			if (("," + GlobalInfo.sysPara.promotionDiscountPayCode + ",").indexOf("," + sp.paycode + ",") > -1)
			{
				promotionSaleJe += sp.je;
				continue;
			}
			if (("," + GlobalInfo.sysPara.cardRebatePayCode + ",").indexOf("," + sp.paycode + ",") > -1)
			{
				cardRebateJe += sp.je;
				continue;
			}
		}
		
		if (cardSaleJe > 0)
		{
			cardSaleJe = ManipulatePrecision.doubleConvert(cardSaleJe * GlobalInfo.sysPara.cardDiscountRate);
		}
		
		if (GlobalInfo.sysPara.cardDiscountPayCode1 != null && GlobalInfo.sysPara.cardDiscountPayCode1.length() > 0)
		{
			String[] rule = GlobalInfo.sysPara.cardDiscountPayCode1.split("|");
			for (int i=0;i < rule.length;i++)
			{
				String line = rule[i];
				double zxj = 0;
				for (int j = 0; j < salepay.size(); j++)
				{
					sp = (SalePayDef) salepay.get(i);
					if (("," + line + ",").indexOf("," + sp.paycode + ",") > -1)
					{
						zxj +=sp.je;
					}
				}
				
				if (zxj > 0)
				{
					cardSaleJe = ManipulatePrecision.doubleConvert(cardSaleJe + (zxj * Convert.toDouble(line.split(",")[0])));
				}
			}
		}



		// 记录到小票头
		salehead.num1 = cardSaleJe;
		salehead.num2 = promotionSaleJe;
		salehead.num3 = cardRebateJe;

		super.printBill();
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		switch (Integer.parseInt(item.code))
		{
			// 卡折扣
			case SBM_Kzk:
				line = ManipulatePrecision.doubleToString(salehead.num1);
				break;
			// 活动折扣
			case SBM_HDzk:
				line = ManipulatePrecision.doubleToString(salehead.num2);
				break;
			// 返利折扣	
			case SBM_FLzk:
				line = ManipulatePrecision.doubleToString(salehead.num3);
				break;
			// 净额	
			case SBM_Jje:
				line = ManipulatePrecision.doubleToString(salehead.ysje - salehead.num1 - salehead.num2 - salehead.num3);
				break;
			// 会员卡类别描述
			case SBM_CUSTTYPENAME:
				line = salehead.buyerinfo;
				break;
			// 买换活动描述
			case SBM_POPDESC:
				line = ((SaleGoodsDef)salegoods.get(index)).str7;
				break;
				
		}
		return line;
	}

	// 移植梦之岛停车券功能
	public void setSaleTicketMSInfo(SaleHeadDef sh, Vector gifts)
	{
		// 记录小票赠送清单
		this.salemsinvo = sh.fphm;
		this.salemsgift = gifts;

		// 分解赠品清单
		Vector goodsinfo = new Vector();
		Vector fj = new Vector();
		for (int i = 0; gifts != null && i < gifts.size(); i++)
		{
			GiftGoodsDef g = (GiftGoodsDef) gifts.elementAt(i);

			if (g.type.trim().equals("0"))
			{
				//无促销
				break;
			}
			else if (g.type.trim().equals("1") || g.type.trim().equals("2"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("3"))
			{
				goodsinfo.add(g);
			}
			else if (g.type.trim().equals("4"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("11"))
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("90")) // 停车券
			{
				fj.add(g);
			}
			else if (g.type.trim().equals("89")) // 停车券
			{
				fj.add(g);
			}
		}

		// 提示
		StringBuffer buff = new StringBuffer();
		double je = 0;

		Vector xv = new Vector();
		for (int i = 0; i < fj.size(); i++)
		{
			GiftGoodsDef g = (GiftGoodsDef) fj.elementAt(i);

			if (g.type.trim().equals("90")) // 停车券
			{
				continue;
			}

			int j = 0;
			for (j = 0; j < xv.size(); j++)
			{
				String[] g1 = (String[]) xv.elementAt(j);
				if (g1[0].equals(g.info))
				{
					g1[1] = ManipulatePrecision.doubleToString(Convert.toDouble(g1[1]) + g.je);
					break;
				}
			}

			if (j >= xv.size())
			{
				xv.add(new String[] { g.info, ManipulatePrecision.doubleToString(g.je) });
			}
		}

		for (int i = 0; i < xv.size(); i++)
		{
			String[] g1 = (String[]) xv.elementAt(i);

			String l = Convert.appendStringSize("", g1[0], 1, 16, 17, 1);

			buff.append(l + ":" + Convert.appendStringSize("", g1[1], 1, 10, 10, 0) + "\n");
			//buff.append(g.code+"   "+g.info+"      "+Convert.increaseChar(ManipulatePrecision.doubleToString(g.je), 14)+"\n");
			je += Convert.toDouble(g1[1]);
		}
		buff.append(Convert.increaseChar("-", '-', 27) + "\n");
		buff.append("返券总金额为: " + ManipulatePrecision.doubleToString(je));
		if (je > 0)
		{
			new MessageBox(buff.toString());
		}

		// 设置
		if (fj.size() > 0) this.zq = fj;
		else this.zq = null;
		if (goodsinfo.size() > 0) this.gift = goodsinfo;
		else this.gift = null;
	}
}
