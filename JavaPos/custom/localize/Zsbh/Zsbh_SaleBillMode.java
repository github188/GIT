/**
 * 
 */
package custom.localize.Zsbh;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.CardSaleBillMode;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_SaleBillMode;

/**
 * @author wangyong
 * 
 */
public class Zsbh_SaleBillMode extends Bcrm_SaleBillMode
{
	// 倍享积分
	protected final static int SBM_BXJF = 202;
	/**
	 * 面值卡付款方式代码 04
	 */
	protected final String _mzkPayCode = "04";

	/**
	 * 银行卡付款方式代码 0317
	 */
	protected final String _bankPayCode = "0317";

	/**
	 * 移动积分卡付款方式代码 0721
	 */
	protected final String _SPPayCode = "0721";

	/**
	 * 工贸卡付款方式代码 0727
	 */
	protected final String _gmkPayCode = "0401";
	
	protected static String rePrintStr = "";

	protected String getItemDataString(PrintTemplateItem item, int index)
	{
		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case SBM_salefphm:// 打印收银员的发票编号
					this.salefph = Printer.getDefault().getCurrentSaleFphm();
					line = Convert.increaseLong(this.salefph, item.length);
					break;

				case SBM_yyyh: // 营业员号
					line = String.valueOf(((SaleGoodsDef) salegoods.elementAt(0)).yyyh);

					break;
				// 打印倍享积分
				case SBM_BXJF:
					line = String.valueOf(((SaleHeadDef) super.salehead).num4);
					break;

				case SBM_printnum: // 重打小票标志及重打次数
					line = null;
					text = "";
					if (salehead.printnum == 0)
					{
						line = null;
						if (ConfigClass.RepPrintTrack != 3)
						{
							// printLine(" \n");
						}
					}
					else
					{
						if (rePrintStr==null || rePrintStr.length()<=0) rePrintStr=item.text;
						String[] print = null;
						if (rePrintStr != null) print = rePrintStr.split("\\|");
						int choice = GlobalVar.Key1;
						StringBuffer info = new StringBuffer();
						info.append("请按键选择重打印标志：  " + "\n");
						info.append("按 1 或 回车键为补打发票      " + "\n");
						info.append("按 2 或 其它键为未正常打印发票" + "\n");

						choice = new MessageBox(info.toString(), null, false).verify();
						if (choice == GlobalVar.Key1 || choice == GlobalVar.Enter)
						{
							//line = item.text;// "重打小票不作报销、营销活动使用";//+
							if (print != null && print.length >= 1)
							{
								line = print[0];
							}
							// salehead.printnum;
						}
						else
						{
							if (print != null && print.length >= 2)
							{
								line = print[1];
							}
							if (ConfigClass.RepPrintTrack != 3)
							{
								// printLine(" \n");
							}
						}
					}

					break;
				case SBM_hjzke: // 总折扣

					if (salehead.hjzke == 0)
					{
						line = "0.00";
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.hjzke * SellType.SELLSIGN(salehead.djlb));
					}

					break;
				case SBM_fphm: // 小票号码
					line = String.valueOf(salehead.fphm);

					break;

				case 101:// 填充商品行数
					line = null;
					text = "";
					int detailLine = Area_Detail;// Convert.toInt(item.text);//Area_Detail
					// item.text = null;
					if (detailLine == 0)
						break;

					index = salegoods.size() * 2;
					if (index > 0)
					{
						// line = "";
						if (index > detailLine)
						{
							// 如果商品行数超过定义的行数时,则换页
							for (int j = 0; j < (Area_Bottom - index - Area_Header); j++)
							{
								// line += "\n";
								printLine("\n");

							}
						}
						else
						{
							// 如果商品行数未达到预订行数,则填充行数
							for (int j = 0; j < (detailLine - index); j++)
							{
								// line += "\n";
								printLine(" \n");
							}
						}

					}

					break;
				case 102: // 客户化:无会员卡时,写时间
					// 打印格式为:会员卡号: 123456789012/营业员号: 123456789

					text = "";
					if ((salehead.hykh == null) || (salehead.hykh.length() <= 0))
					{
						// 无会员卡时打营业员号
						// line = "日期:" +
						// salehead.rqsj;//String.valueOf(((SaleGoodsDef)
						// salegoods.elementAt(0)).yyyh);
						line = salehead.rqsj.split(" ")[0];// String.valueOf(((SaleGoodsDef)
															// salegoods.elementAt(0)).yyyh);
					}
					else
					{
						line = "会员卡号:" + salehead.hykh;
					}

					line = appendStringSize(line, item);

					break;
				case 103:// 打印面值卡付款信息
					// 打印格式为:面值卡1234567890123456张数1金额123.00余额110.00
					// 所有面值卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(_mzkPayCode))// 当前为面值卡付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(_mzkPayCode))
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
				case 113:// 打印工贸卡付款信息
					// 打印格式为:工贸卡1234567890123456张数1金额123.00余额110.00
					// 所有工贸卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(_gmkPayCode))// 当前为工贸卡付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(_gmkPayCode))
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
				case 111:// 付款方式行数

					text = "";
					try
					{
						line = null;
						if (salegoods.size() > Area_Detail)
							break;// 如果商品行数超过预定义的行数后（即需换页），则不控制111项
						for (int i = 0; i < Convert.toInt(item.text) - salepay.size(); i++)
						{
							printLine("\n");
						}

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					break;
				case 104:// 打印银行卡付款信息
					// 打印格式为:银行卡1234567890123456流水155568授权155568 12200.2
					// 一张卡打印一行
					text = "";
					SalePayDef sp = ((SalePayDef) salepay.elementAt(index));
					if (sp.paycode.equals(_bankPayCode))
					{
						String sq = "";// 授权
						if (sp.memo.trim().equals("000000") || sp.memo.trim().length() < 1)
						{
							sq = "";
						}
						else
						{
							sq = "授权" + String.valueOf(sp.memo);
						}
						if (ConfigClass.RepPrintTrack == 3)
						{
							line = appendStringSize(sp.payname + sp.payno + "流水" + Convert.increaseCharForward(String.valueOf(sp.batch), '0', 6) + sq + " " + ManipulatePrecision.doubleToString(sp.je * sp.hl), item);
						}
						else
						{
							line = null;// sp1.payname + sp1.payno + "流水" +
										// Convert.increaseCharForward(String.valueOf(sp1.batch),'0',
										// 6) + "\n" + sq + " " +
										// ManipulatePrecision.doubleToString(sp1.je
										// * sp1.hl);
							printLine(appendStringSize(sp.payno + " 流水" + Convert.increaseCharForward(String.valueOf(sp.batch), '0', 6), item));
							printLine(appendStringSize("  " + sq + " " + ManipulatePrecision.doubleToString(sp.je * sp.hl), item));
							// printLine(sq + " " +
							// ManipulatePrecision.doubleToString(sp.je *
							// sp.hl));
						}

					}
					else
					{
						line = null;
					}

					break;

				case 105:// 打印非MZK/BANK/SP的付款信息
					// 打印格式为:人民币 1.00
					text = "";
					SalePayDef pay = ((SalePayDef) salepay.elementAt(index));
					if (pay.paycode.equals(_mzkPayCode) || pay.paycode.equals(_bankPayCode) || pay.paycode.equals(_SPPayCode) || pay.paycode.equals(_gmkPayCode))// ||
																																									// pay.paycode.equals("05")
																																									// ||
																																									// pay.paycode.equals("52")
					{
						line = null;
					}
					else
					{
						/*
						 * line = appendStringSize( pay.payname + " " +
						 * ManipulatePrecision .doubleToString(pay.je * pay.hl),
						 * item);
						 */
						line = appendStringSize(pay.payname + " " + ManipulatePrecision.doubleToString(pay.je), item);
					}

					break;
				case 106:// 打印MZK/BANK/SP/工贸 签购单Header
					// 打印格式为:\r\n机号:1129小票号:9998081营业员号:6251收银员号:6251
					// 只打一次
					text = "";
					line = null;
					for (int i = 0; i < salepay.size(); i++)
					{
						SalePayDef p = ((SalePayDef) salepay.elementAt(i));
						if (p.paycode.equals(_mzkPayCode) || p.paycode.equals(_bankPayCode) || p.paycode.equals(_SPPayCode) || p.paycode.equals(_gmkPayCode))
						{
							line = "机号:" + salehead.syjh + "小票号:" + String.valueOf(salehead.fphm) + "收银员号:" + salehead.syyh;
							if (ConfigClass.RepPrintTrack == 3)
							{
								line = "\n\n" + appendStringSize(line + "营业员号:" + String.valueOf(((SaleGoodsDef) salegoods.elementAt(0)).yyyh), item);
							}
							else
							{
								// printLine("\n");
								printLine(appendStringSize("------------------------------------------------------------", item));
								printLine(appendStringSize(line, item));
								line = null;
							}

							break;
						}
					}

					break;
				case 107:// 打印银行卡付款信息
					// 打印格式为:银行卡1234567890123456张数1合计金额123.00
					// 所有银行卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(_bankPayCode))// 当前为银行卡付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(_bankPayCode))
								{
									if (index > i)
									{
										line = null;
									}
									else
									{
										int bankCount = 0; // 银行卡消费总张数
										double bankTotalJe = 0; // 银行卡消费总金额

										for (int j = 0; j < salepay.size(); j++)
										{
											SalePayDef p = (SalePayDef) salepay.elementAt(j);
											if (p.paycode.equals(spd.paycode))
											{
												bankCount++;
												bankTotalJe += p.ybje * p.hl;
											}
										}
										if (bankCount > 0)
										{
											if (ConfigClass.RepPrintTrack == 3)
											{
												// 银行卡 1234567890123456 总张数:1
												// 总金额100.00
												line = appendStringSize(spd.payname + " " + spd.payno + "  总张数:" + String.valueOf(bankCount) + " 总金额:" + ManipulatePrecision.doubleToString(bankTotalJe), item);

											}
											else
											{
												line = appendStringSize(spd.payno + " " + String.valueOf(bankCount) + " " + ManipulatePrecision.doubleToString(bankTotalJe), item);
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
				case 108:// 打印面值卡签购单信息
					// 打印格式为:面值卡1234567890123456张数1金额123.00
					// 所有面值卡合计为打一次
					text = "";
					try
					{
						line = null;
						int mzkCount = 0; // 面值卡消费总张数
						double mzkTotalJe = 0; // 面值卡消费总金额
						String payNo = "";
						String payName = "";
						SalePayDef pay1 = null;

						for (int j = 0; j < salepay.size(); j++)
						{
							pay1 = (SalePayDef) salepay.elementAt(j);
							if (pay1.paycode.equals(_mzkPayCode))
							{
								mzkCount++;
								mzkTotalJe += pay1.ybje * pay1.hl;
								if (mzkCount == 1)
								{
									payName = pay1.payname;
									payNo = pay1.payno;
								}
								if (mzkCount > 1 && pay1.kye > 0)
								{
									payNo = pay1.payno;
								}
							}
						}
						if (mzkCount > 0 && pay1 != null)
						{
							if (ConfigClass.RepPrintTrack == 3)
							{
								// 面值卡 1234567890123456 总张数:1 总金额100.00
								line = appendStringSize(payName + payNo + "总张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);
							}
							else
							{
								line = appendStringSize(payName + payNo + "张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);

							}

						}

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					break;
				case 114:// 打印工贸卡签购单信息
					// 打印格式为:工贸卡1234567890123456张数1金额123.00
					// 所有工贸卡合计为打一次
					text = "";
					try
					{
						line = null;
						int mzkCount = 0; // 工贸卡消费总张数
						double mzkTotalJe = 0; // 工贸卡消费总金额
						String payNo = "";
						String payName = "";
						SalePayDef pay1 = null;

						for (int j = 0; j < salepay.size(); j++)
						{
							pay1 = (SalePayDef) salepay.elementAt(j);
							if (pay1.paycode.equals(_gmkPayCode))
							{
								mzkCount++;
								mzkTotalJe += pay1.ybje * pay1.hl;
								if (mzkCount == 1)
								{
									payName = pay1.payname;
									payNo = pay1.payno;
								}
								if (mzkCount > 1 && pay1.kye > 0)
								{
									payNo = pay1.payno;
								}
							}
						}
						if (mzkCount > 0 && pay1 != null)
						{
							if (ConfigClass.RepPrintTrack == 3)
							{
								// 工贸卡 1234567890123456 总张数:1 总金额100.00
								line = appendStringSize(payName + payNo + "总张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);
							}
							else
							{
								line = appendStringSize(payName + payNo + "张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);

							}

						}

					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					break;
				case 109:// 打印移动积分消费付款信息
					// 打印格式为(与面值卡一样):面值卡1234567890123456张数1金额123.00余额110.00
					// 所有面值卡合计为打一次
					text = "";
					try
					{
						line = null;
						if (((SalePayDef) salepay.elementAt(index)).paycode.equals(_SPPayCode))// 当前为移动积分消费付款
						{
							SalePayDef spd = null;
							for (int i = 0; i < salepay.size(); i++)
							{
								spd = (SalePayDef) salepay.elementAt(i);
								if (spd.paycode.equals(_SPPayCode))
								{
									if (index > i)
									{
										line = null;
									}
									else
									{
										int mzkCount = 0; // 移动积分消费总张数
										double mzkTotalJe = 0; // 移动积分消费总金额
										// double mzkYe = 0; //余额
										String payNo = "";
										String payName = "";
										SalePayDef paysp = null;

										for (int j = 0; j < salepay.size(); j++)
										{
											paysp = (SalePayDef) salepay.elementAt(j);
											if (paysp.paycode.equals(spd.paycode))
											{
												mzkCount++;
												mzkTotalJe += paysp.ybje * paysp.hl;
												if (mzkCount == 1)
												{
													payName = paysp.payname;
													// mzkYe = paysp.kye;
													payNo = paysp.payno;
												}
												if (mzkCount > 1 && paysp.kye > 0)
												{
													// mzkYe = paysp.kye;
													payNo = paysp.payno;
												}
											}
										}
										if (mzkCount > 0)
										{
											if (ConfigClass.RepPrintTrack == 3)
											{
												// 移动积分 1234567890123456 总张数:1
												// 总金额100.00
												line = appendStringSize(payName + payNo + "总张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);// +
																																															// "余额"
																																															// +
																																															// ManipulatePrecision.doubleToString(mzkYe);

											}
											else
											{
												line = appendStringSize(payName + payNo + "张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);// +
																																															// "余额"
																																															// +
																																															// ManipulatePrecision.doubleToString(mzkYe);

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
				case 110:// 打印移动积分消费签购单信息
					// 打印格式为:移动积分1234567890123456张数1金额123.00
					// 所有移动积分合计为打一次
					text = "";
					try
					{
						line = null;
						int mzkCount = 0; // 移动积分消费总张数
						double mzkTotalJe = 0; // 移动积分消费总金额
						String payNo = "";
						String payName = "";
						SalePayDef pay1 = null;

						for (int j = 0; j < salepay.size(); j++)
						{
							pay1 = (SalePayDef) salepay.elementAt(j);
							if (pay1.paycode.equals(_SPPayCode))
							{
								mzkCount++;
								mzkTotalJe += pay1.ybje * pay1.hl;
								if (mzkCount == 1)
								{
									payName = pay1.payname;
									payNo = pay1.payno;
								}
								if (mzkCount > 1 && pay1.kye > 0)
								{
									payNo = pay1.payno;
								}
							}
						}
						if (mzkCount > 0 && pay1 != null)
						{
							if (ConfigClass.RepPrintTrack == 3)
							{
								// 移动积分 1234567890123456 总张数:1 总金额100.00
								line = appendStringSize(payName + payNo + "总张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);

							}
							else
							{
								line = appendStringSize(payName + payNo + "张数" + String.valueOf(mzkCount) + "总金额" + ManipulatePrecision.doubleToString(mzkTotalJe), item);

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
		super.printBill();

		if (ConfigClass.RepPrintTrack == 3)
		{
			// 开始打印
			Printer.getDefault().setPagePrint_Slip(true, Area_PageFeet);
			Printer.getDefault().cutPaper_Slip();
		}
		else
		{
			// 待打印（小票+面值卡/银行卡签购单）完毕以后，再切纸
			// printCutPaper();
		}

	}

	protected void printSellBill()
	{

		// GlobalInfo.sysPara.printInBill
		// 当为 N 时,退货与红冲都不打印小票
		// 当为 A 时,退货不打印小票
		// 当为 B 时,红冲不打印小票
		if ((SellType.ISBACK(salehead.djlb) || SellType.ISHC(salehead.djlb)) && (GlobalInfo.sysPara.printInBill == 'N') || (SellType.ISBACK(salehead.djlb) && GlobalInfo.sysPara.printInBill == 'A') || (SellType.ISHC(salehead.djlb)) && GlobalInfo.sysPara.printInBill == 'B') { return; }

		// GlobalInfo.sysPara.fdprintyyy =
		// (N-不打营业员联但打印小票联/Y-打印营业员联也打印小票/A-打印营业员联但不打印小票)
		// 非超市小票且系统参数定义只打印营业员分单，则不打印机制小票
		if (!((GlobalInfo.syjDef.issryyy == 'N') || (GlobalInfo.syjDef.issryyy == 'A' && ((SaleGoodsDef) salegoods.elementAt(0)).yyyh.equals("超市"))) && (GlobalInfo.sysPara.fdprintyyy == 'A')) { return; }

		// 设置打印方式
		printSetPage();

		// 打印银行签购单
		// printBankBillEx(false);

		// 打印头部区域
		printHeader();
		// 打印明细区域
		printDetail();

		// 打印汇总区域
		printTotal();

		// 打印付款区域
		printPay();

		// 打印尾部区域
		printBottom();

		// 切纸
		// if (ConfigClass.RepPrintTrack != 3)
		// {
		printCutPaper();
		// }

	}

	/*
	 * public void printBankBillEx(boolean isCut) { // 在原始付款清单中,查找是否有银联卡付款方式 for
	 * (int i = 0; i < originalsalepay.size(); i++) { SalePayDef pay =
	 * (SalePayDef) originalsalepay.elementAt(i); PayModeDef mode =
	 * DataService.getDefault().searchPayMode(pay.paycode);
	 * 
	 * if ((mode.code.equals("0317")) && (pay.batch != null) &&
	 * (pay.batch.length() > 0))//mode.isbank == 'Y {
	 * Zsbh_PaymentBank.printXYKDoc("Bankdoc_" + salehead.syjh + "_" +
	 * salehead.fphm + "_" + pay.batch + ".txt", isCut); } } }
	 */

	public void printBankBill()
	{
		// 若为新银联接口，则跳出（新接口是先打印，旧接口是后打印）
		if (ConfigClass.CustomItem5.split("\\|").length >= 4 && ConfigClass.CustomItem5.split("\\|")[3].trim().equalsIgnoreCase("Y"))
			return;

		boolean isExistsBank = false;
		// 在原始付款清单中,查找是否有银联卡付款方式
		for (int i = 0; i < originalsalepay.size(); i++)
		{
			SalePayDef pay = (SalePayDef) originalsalepay.elementAt(i);
			PayModeDef mode = DataService.getDefault().searchPayMode(pay.paycode);

			if ((mode.code.equals(_bankPayCode)) && (pay.batch != null) && (pay.batch.length() > 0))// mode.isbank
																									// ==
																									// 'Y
			{
				if (isExistsBank == false)
				{
					printLine("机号:" + salehead.syjh + "小票号:" + String.valueOf(salehead.fphm) + "收银员号:" + salehead.syyh);
				}
				isExistsBank = true;
				Zsbh_PaymentBank.printXYKDoc("Bankdoc_" + salehead.syjh + "_" + salehead.fphm + "_" + pay.batch + ".txt", false);
			}
		}
		if (isExistsBank)
		{
			printCutPaper();
		}
	}

	public void printAppendBill()
	{
		// super.printAppendBill();

		// 检查是否有未打印的银联签购单
		// if (Zsbh_PaymentBank.haveXYKDoc)
		// {
		printBankBill();
		// }

		// 检查是否有
		CardSaleBillMode.getDefault().setLoad(false);
		if (CardSaleBillMode.getDefault().isLoad())
		{
			// printMZKBillPrintMode();
		}
		else
		{
			// 打印面值卡联
			// printMZKBill(1);

			// 打印返券卡联
			// printMZKBill(2);
		}

		// 打印赠券联
		printSaleTicketMSInfo();

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

}
