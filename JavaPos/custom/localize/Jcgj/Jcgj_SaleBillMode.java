package custom.localize.Jcgj;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Cmls.Cmls_SaleBillMode;

public class Jcgj_SaleBillMode extends Cmls_SaleBillMode
{
	public void printBottom()
	{
		if (salehead.buyerinfo.length() > 0 && salehead.buyerinfo.indexOf(";") > 0)
		{
			String line = "";
			String[] pf = salehead.buyerinfo.split(";");

			if (pf.length > 0 && pf[0].trim().length() > 0)
			{
				Printer.getDefault().printLine_Normal("\n");

				int d = Integer.parseInt(pf[0]);
				String s = partTranslate(d);
				line = "恭喜您获得印花" + s + "枚\n凭多枚印花在活动期间可领取礼品一份";
				Printer.getDefault().printLine_Normal(line);

				if (pf.length > 1 && pf[1].length() > 0 && pf[1].indexOf(",") > 0)
				{
					String[] rq = pf[1].split(",");
					if (rq.length == 2)
					{
						Printer.getDefault().printLine_Normal("印花有效期为：" + rq[0] + " 到 " + rq[1]);
					}
				}
			}
		}
		super.printBottom();
	}

	protected String extendCase(PrintTemplateItem item, int index)
	{
		String line = null;
		if (Integer.parseInt(item.code) == SBM_bcjf)
		{
			if (salehead.num5 != 1)
			{
				line = "&!";
			}
			else
			{
				line = ManipulatePrecision.doubleToString(salehead.bcjf);
			}
			return line;
		}
		else if (Integer.parseInt(item.code) == SBM_ljjf)
		{
			if (salehead.num5 != 1)
			{
				line = "&!";
			}
			else
			{
				line = ManipulatePrecision.doubleToString(salehead.ljjf);
			}
			return line;
		}
		else if (Integer.parseInt(item.code) == SBM_sjje)
		{
			if (((SaleGoodsDef) salegoods.elementAt(index)).hjzk == 0)
			{
				line = "&!";
			}
			else
			{
				line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjje * SellType.SELLSIGN(salehead.djlb));
			}
			return line;
		}
		else if (Integer.parseInt(item.code) == SBM_goodname)
		{
			SaleGoodsDef sg = ((SaleGoodsDef) salegoods.elementAt(index));
			line = sg.name;
			if (sg.num1 == 2) line = "*" + line;
			// 记录商品所能打印的最大长度
			goodnamemaxlength = item.length;
			return line;
		}
		else
		{
			return super.extendCase(item, index);
		}
	}

	public void printSaleTicketMSInfo()
	{
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
			GiftGoodsDef def = (GiftGoodsDef) this.zq.elementAt(i);
			if (!def.type.trim().equals("99") && !def.type.trim().equals("4"))
			{
				if (GiftBillMode.getDefault().checkTemplateFile())
				{
					GiftBillMode.getDefault().setTemplateObject(salehead, def);
					GiftBillMode.getDefault().PrintGiftBill();
					printZqBarcode(def); // 打印赠券条码
					Printer.getDefault().cutPaper_Journal();
					continue;
				}

				Printer.getDefault().printLine_Journal("收银机号：" + salehead.syjh + "  小票号：" + Convert.increaseLong(salehead.fphm, 8));
				if (SellType.ISCOUPON(salehead.djlb)) Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "买券交易", 1, 37, 38, 2));

				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				手 工 券", 1, 37, 38));
				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				Printer.getDefault().printLine_Journal("== 券  号  : " + def.code);
				Printer.getDefault().printLine_Journal("== 券信息  : " + def.info);
				Printer.getDefault().printLine_Journal("== 券总额  : " + def.je);
				if (salehead.printnum > 0)
				{
					Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "				重 打 印", 1, 37, 38));
				}
				Printer.getDefault().printLine_Journal("== 券有效期: " + def.memo);
				Printer.getDefault().printLine_Journal(Convert.appendStringSize("", "=================================================", 1, 37, 38));
				printZqBarcode(def); // 打印赠券条码
				Printer.getDefault().cutPaper_Journal();
			}
		}
	}

	// 打印赠券条码
	private void printZqBarcode(GiftGoodsDef def)
	{
		// 选择字符打印位置（下方）
		char[] position = { (int) 29, (int) 72, (int) 2 };
		char[] width = { (int) 29, (int) 119, (int) 2 };
		char[] height = { (int) 29, (int) 104, (int) 90 };

		// 选择Code128模式打印条码
		char[] code128 = { (int) 29, (int) 107, (int) 73, (char) (def.code.length() + 2), (int) 123, (int) 65 };
		Printer.getDefault().printLine_Journal(
												String.valueOf(position) + String.valueOf(width) + String.valueOf(height) + String.valueOf(code128)
														+ def.code);
	}

	/** 
	 * 把一个 0~9999 之间的整数转换为汉字的字符串，如果是 0 则返回 "" 
	 * @param amountPart 
	 * @return 
	 */
	private String partTranslate(int amountPart)
	{
		String[] chineseDigits = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };

		if (amountPart < 0 || amountPart > 10000) { return String.valueOf(amountPart); }

		String[] units = new String[] { "", "拾", "佰", "仟" };

		int temp = amountPart;

		String amountStr = new Integer(amountPart).toString();
		int amountStrLength = amountStr.length();
		boolean lastIsZero = true; //在从低位往高位循环时，记录上一位数字是不是 0  
		String chineseStr = "";

		for (int i = 0; i < amountStrLength; i++)
		{
			if (temp == 0) // 高位已无数据  
			break;
			int digit = temp % 10;
			if (digit == 0)
			{ // 取到的数字为 0  
				if (!lastIsZero) //前一个数字不是 0，则在当前汉字串前加“零”字;  
				chineseStr = "零" + chineseStr;
				lastIsZero = true;
			}
			else
			{ // 取到的数字不是 0  
				chineseStr = chineseDigits[digit] + units[i] + chineseStr;
				lastIsZero = false;
			}
			temp = temp / 10;
		}
		return chineseStr;
	}
}
