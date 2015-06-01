package custom.localize.Zspj;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

import bankpay.Bank.JavaZrxZSPJ_PaymentBankFunc;

public class Zspj_SaleBillMode extends SaleBillMode
{
	protected final static int SBM_BXJF = 202;
	protected final static int SBM_MZK = 103;
	protected final static int SBM_GMMZK = 113;
	
	protected final String mzkPayCode = "04";
	protected final String gmkPayCode = "0401";

	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				// 打印倍享积分
				case SBM_BXJF:
					if (((SaleHeadDef) super.salehead).num4 == 0)
						line = null;
					else			
						line = ManipulatePrecision.doubleToString(((SaleHeadDef) super.salehead).num4);
					
					break;
				case SBM_MZK:// 打印面值卡付款信息
					// 打印格式为:面值卡1234567890123456张数1金额123.00余额110.00
					// 所有面值卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(mzkPayCode))// 当前为面值卡付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(mzkPayCode))
								{
									if (index > i)
									{
										line = null;
									}
									else
									{
										int mzkCount = 0; // 面值卡消费总张数
										double mzkTotalJe = 0; // 面值卡消费总金额
										double mzkYe = 0; // 余额
										String payNo = "";
										SalePayDef pay = null;

										for (int j = 0; j < salepay.size(); j++)
										{
											pay = (SalePayDef) salepay.elementAt(j);
											if (pay.paycode.equals(spd.paycode))
											{
												mzkCount++;
												mzkTotalJe += pay.ybje * pay.hl;
												if (mzkCount == 1)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}
												if (mzkCount > 1 && pay.kye > 0)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}
											}
										}
										if (mzkCount > 0)
										{
											if (ConfigClass.RepPrintTrack == 3)
											{// 系统服务打印机
												// 面值卡 1234567890123456 总张数:1
												// 总金额100.00
												line = appendStringSize(spd.payname + payNo + "总张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe) + "余额" + ManipulatePrecision.doubleToString(mzkYe), item);

											}
											else
											{
												// 新打印机
												line = null;// spd.payname +
															// payNo + "总张数" +
															// String.valueOf(mzkCount)
															// + "\n总金额" +
															// ManipulatePrecision.doubleToString(mzkTotalJe)
															// + "余额" +
															// ManipulatePrecision.doubleToString(mzkYe);
												printLine(appendStringSize("面值" + payNo + " " + String.valueOf(mzkCount) + " " + ManipulatePrecision.doubleToString(mzkTotalJe) + " 余" + ManipulatePrecision.doubleToString(mzkYe), item));
												// printLine("总金额" +
												// ManipulatePrecision.doubleToString(mzkTotalJe)
												// + "余额" +
												// ManipulatePrecision.doubleToString(mzkYe));

											}
										}
									}
									break;// 找到一个就退出循环
								}
							}
						}

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					break;
				case SBM_GMMZK:// 打印工贸卡付款信息
					// 打印格式为:工贸卡1234567890123456张数1金额123.00余额110.00
					// 所有工贸卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(gmkPayCode))// 当前为工贸卡付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(gmkPayCode))
								{
									if (index > i)
									{
										line = null;
									}
									else
									{
										int mzkCount = 0; // 工贸卡消费总张数
										double mzkTotalJe = 0; // 工贸卡消费总金额
										double mzkYe = 0; // 余额
										String payNo = "";
										SalePayDef pay = null;

										for (int j = 0; j < salepay.size(); j++)
										{
											pay = (SalePayDef) salepay.elementAt(j);
											if (pay.paycode.equals(spd.paycode))
											{
												mzkCount++;
												mzkTotalJe += pay.ybje * pay.hl;
												if (mzkCount == 1)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}
												if (mzkCount > 1 && pay.kye > 0)
												{
													mzkYe = pay.kye;
													payNo = pay.payno;
												}
											}
										}
										if (mzkCount > 0)
										{
											if (ConfigClass.RepPrintTrack == 3)
											{// 系统服务打印机
												// 工贸卡 1234567890123456 总张数:1
												// 总金额100.00
												if (mzkYe < 0)
												{
													line = appendStringSize(spd.payname + payNo + "总张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);// 工贸卡当余额小于0时，表示未查询到余额,则不打印余额
												}
												else
												{
													line = appendStringSize(spd.payname + payNo + "总张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe) + "余额" + ManipulatePrecision.doubleToString(mzkYe), item);
												}

											}
											else
											{
												// 新打印机
												line = null;// spd.payname +
															// payNo + "总张数" +
															// String.valueOf(mzkCount)
															// + "\n总金额" +
															// ManipulatePrecision.doubleToString(mzkTotalJe)
															// + "余额" +
															// ManipulatePrecision.doubleToString(mzkYe);
												if (mzkYe < 0)
												{
													printLine(appendStringSize("工贸卡" + payNo + " " + String.valueOf(mzkCount) + " " + ManipulatePrecision.doubleToString(mzkTotalJe), item));// 工贸卡当余额小于0时，表示未查询到余额,则不打印余额
												}
												else
												{
													printLine(appendStringSize("工贸卡" + payNo + " " + String.valueOf(mzkCount) + " " + ManipulatePrecision.doubleToString(mzkTotalJe) + "余" + ManipulatePrecision.doubleToString(mzkYe), item));
												}
											}
										}
									}
									break;// 找到一个就退出循环
								}
							}
						}

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
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
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text
				.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);

			line = text + Convert.appendStringSize("", line, 0, maxline,
					maxline, item.alignment);
		}

		return line;
	}
	
	protected String appendStringSize(String line, PrintTemplateItem item)
	{
		try
		{
			int length = Width;
			if (item.text != null && item.text.toString().length() > 0)
			{
				length = Convert.toInt(item.text.toString());
				if (length < 1)
					length = Width;
			}
			return appendStringSize(line, 0, length, Width);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}
	}
	
	protected String appendStringSize(String line, int startindex, int length, int maxlength)
	{
		try
		{
			return Convert.rightTrim(Convert.appendStringSize("", line, startindex, length, maxlength));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "";
		}

	}

	public void printBottom()
	{
		JavaZrxZSPJ_PaymentBankFunc.getZrxZSPJ().printZrxSaleTicket(salehead);
		super.printBottom();
	}
}
