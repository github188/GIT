package custom.localize.Cbbh;

import java.util.HashMap;
import java.util.Vector;

import jp.sourceforge.qrcode.util.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.Struct.CalcRulePopDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.swtdesigner.SWTResourceManager;

import org.eclipse.swt.widgets.Table;

public class Cbbh_PaymentCouponNew extends Cbbh_PaymentCoupon
{
	protected String autoTrack1 = null;
	protected String autoTrack2 = null;
	protected String autoTrack3 = null;
	
	private HashMap<Integer,String> alreadyPays = null; 
	
	public Vector poplist =new Vector();//整单参与接劵，送劵，满送信息

	private Cbbh_PaymentCouponNewForm form = null;
	
	public Cbbh_PaymentCouponNew()
	{
	}

	public Cbbh_PaymentCouponNew(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode, sale);
	}

	public Cbbh_PaymentCouponNew(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay, head);
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && (GlobalInfo.sysPara.thmzk != 'Y'))
			{
				new MessageBox(Language.apply("退货时不能使用") + paymode.name, null, false);

				return null;
			}

			// 先检查是否有冲正未发送
			if (!sendAccountCz()) { return null; }
			
			//是否通过外部设备读取卡号
			if(!autoFindCard()) return null;

			// 打开明细输入窗口
			form = new Cbbh_PaymentCouponNewForm();
			form.open(this, saleBS);

			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}
	
	public boolean isAlreadyPays(int index)
	{
		if(alreadyPays == null)
		{	
			alreadyPays = new HashMap<Integer, String>();
		}
		else
		{
			if(alreadyPays.containsKey(Integer.valueOf(index)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void addAlreadyPays(int index,String text)
	{
		if(alreadyPays == null)
			alreadyPays = new HashMap<Integer, String>();
		else
		{	
			alreadyPays.put(Integer.valueOf(index), text);
			
			displayAlreadyPays(index);
		}
	}

	public void displayAlreadyPays(int index)
	{	
		form.getTable().getItem(index).setForeground(SWTResourceManager.getColor(255,0,0));
	}	
	
	public boolean deletePayment(int index, PaymentCoupon pcp1)
	{
		int i;
		SalePayDef pay = null;
		for(i = 0; i < saleBS.salePayment.size(); ++ i)
		{
			pay = (SalePayDef)saleBS.salePayment.elementAt(i);
			if(pay.paycode.equals(pcp1.paymode.code) && pay.rowno == index + 1) break;
		}

		if(i < saleBS.salePayment.size())
		{	
			if (saleBS.isRefundStatus())
			{
				saleBS.delSaleRefundObject(i);
			}
			else
			{
				saleBS.delSalePayObject(i);
			}
		}

		return true;
	}
	
	public boolean CreateNewjPayment(int index, double money, StringBuffer bufferStr)
	{
		try
		{
			/*
			if (money <= 0)
			{
				new MessageBox(Language.apply("付款金额必须大于0"));

				return false;
			}*/
			
			//如果已经存在付款则返回
			//if(isAlreadyPays(index)) return true;
						
			Cbbh_PaymentCouponNew cpf = new Cbbh_PaymentCouponNew(paymode, saleBS);

			cpf.paymode = (PayModeDef) this.paymode.clone();
			cpf.salehead = this.salehead;
			cpf.saleBS = this.saleBS;
			cpf.couponList = this.couponList;

			cpf.mzkreq = (MzkRequestDef) mzkreq.clone();
			cpf.mzkret = (MzkResultDef) mzkret.clone();

			// ///////////////////// 创建新的付款明细对象
			// 设置券类型,得到当前券的本次可用金额
			String[] rows = (String[]) couponList.elementAt(index);
			if (Convert.toInt(rows[5]) > 0)
			{
				cpf.CouponType = Convert.toInt(rows[5]);
			}

			cpf.mzkreq.memo = rows[0];
			cpf.mzkret.ye = Convert.toDouble(rows[2]);

			if ((GlobalInfo.sysPara.fjkkhhl != null) && (GlobalInfo.sysPara.fjkkhhl.length() > 0) && saleBS.isRefundStatus() && !SellType.ISCOUPON(saleBS.saletype))
			{
				String[] lines = null;
				if (GlobalInfo.sysPara.fjkkhhl.indexOf(";") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split(";");
				else if (GlobalInfo.sysPara.fjkkhhl.indexOf("|") >= 0)
					lines = GlobalInfo.sysPara.fjkkhhl.split("\\|");

				if (lines == null)
					lines = new String[] { GlobalInfo.sysPara.fjkkhhl };

				if (lines != null)
				{
					int i = 0;

					for (i = 0; i < lines.length; i++)
					{
						String l = lines[i];

						if (l.indexOf(",") > 0)
						{
							String cid = l.substring(0, l.indexOf(","));

							if (cid.equals(rows[0]))
							{
								cpf.paymode.hl = Convert.toDouble(l.substring(l.indexOf(",") + 1));

								break;
							}
						}
					}

					if (i >= lines.length)
					{
						cpf.paymode.hl = Convert.toDouble(rows[3]);
					}
				}
			}
			else
			{
				cpf.paymode.hl = Convert.toDouble(rows[3]);
			}
			cpf.allowpayje = this.allowpayje;

			// 查询并删除原付款
			// 如果是退货且非扣回时，不删除原付款方式
			if (!(SellType.ISBACK(salehead.djlb) && !saleBS.isRefundStatus()) || GlobalInfo.sysPara.isBackPaymentCover == 'Y')
			{
				if (!deletePayment(index, cpf))
				{
					new MessageBox(Language.apply("删除原付款方式失败！"));

					return false;
				}
			}

			if (this.allowpayje >= 0 && money > this.allowpayje && paymode.isyy != 'Y')
			{
//				new MessageBox("该付款方式最多允许付款 " + ManipulatePrecision.doubleToString(allowpayje) + " 元");
				new MessageBox(Language.apply("该付款方式最多允许付款 {0} 元" ,new Object[]{ManipulatePrecision.doubleToString(allowpayje)}));

				return false;
			}

			
			double yy = 0;
						if (yyje > 0 && sjje > 0)
			{
				double min = Math.min(ManipulatePrecision.doubleConvert(sjje / cpf.paymode.hl), cpf.allowpayje);
				if (sjje > 0 && money > min)
				{
//					new MessageBox("最大可退金额为: " + min);
					new MessageBox(Language.apply("最大可退金额为: {0}" ,new Object[]{min+""}));
					return false;
				}

				if (GlobalInfo.sysPara.oldqpaydet == 'A')
				{
					StringBuffer buf = new StringBuffer();
					buf.append(ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)));
					TextBox txt = new TextBox();
//					txt.open("请输入券面值", "券面值", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大券面值为:" + ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert(money + (yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					txt.open(Language.apply("请输入券面值"), Language.apply("券面值"), Language.apply("实际付款为:{0}\n最大券面值为:{1}" ,new Object[]{ManipulatePrecision.doubleToString(money) ,ManipulatePrecision.doubleToString(money + (yyje / cpf.paymode.hl))}), buf, 0, ManipulatePrecision.doubleConvert(money + (yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					double yfk = money;
					money = Convert.toDouble(buf.toString());
					if (money > yfk)
						yy = ManipulatePrecision.doubleConvert(money - yfk);
				}
				else
				{
					StringBuffer buf = new StringBuffer();
					// buf.append(ManipulatePrecision.doubleToString((yyje/cpf.paymode.hl)));
					TextBox txt = new TextBox();
//					txt.open("请输入此券益余金额", "益余金额", "实际付款为:" + ManipulatePrecision.doubleToString(money) + "\n最大益余金额为:" + ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl)), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);
					txt.open(Language.apply("请输入此券益余金额"), Language.apply("益余金额"), Language.apply("实际付款为:{0}\n最大益余金额为:{1}" ,new Object[]{ManipulatePrecision.doubleToString(money) ,ManipulatePrecision.doubleToString((yyje / cpf.paymode.hl))}), buf, 0, ManipulatePrecision.doubleConvert((yyje / cpf.paymode.hl)), true, TextBox.DoubleInput, -1);

					if (Convert.toDouble(buf.toString()) > 0)
						yy = Convert.toDouble(buf.toString());
				}
			}
			
						
			if(SellType.ISBACK(saleBS.saletype) && ManipulatePrecision.doubleConvert(Convert.toDouble(rows[2]),2,0) != Convert.toDouble(money))
			 {
			    new MessageBox(Language.apply("退货不允许修改退劵金额，请直接确认"));
			
			    return false;
			 }
			
			String type="1";//1修改用券数量
			if(salehead.num1 > 0)
			{
				type = "4";//4家电修改用券数量
			}
			
			if(!SellType.ISBACK(salehead.djlb))
			{
				//判断本次输入金额是否和上次输入金额一样
				if(!rows[2].equals(String.valueOf(money)))
				{
					//检查接劵金额
					if(!checkJjJe(type,salehead.hykh,rows[0], String.valueOf(money),saleBS.saletype))
					{
						return false;
					}
					else
					{
						//得到本次所使用的券信息
						rows = (String[]) couponList.elementAt(index);
						
						if (Convert.toInt(rows[5]) > 0)
						{
							cpf.CouponType = Convert.toInt(rows[5]);
						}

						cpf.mzkreq.memo = rows[0];
						cpf.mzkret.ye = Convert.toDouble(rows[2]);

						getValidJe(0);
					}
				}
			}
			
			//输入0表示不用劵
			if(money <= 0)
			{
				salepay = null;
				return true;
			}
			
			// 创建付款对象
			if (cpf.createSalePay(String.valueOf(money + yy)))
			{
				cpf.salepay.payno=rows[0];
				// 设置付款方式名称
				cpf.salepay.payname = rows[1];
				cpf.salepay.je = money;
				
				cpf.salepay.rowno = index + 1;
				if (yy > 0)
					cpf.salepay.num1 = ManipulatePrecision.doubleConvert(yy * cpf.salepay.hl);

				// 增加已付款
				if (SellType.ISBACK(saleBS.saletype) && saleBS.isRefundStatus())
				{
					cpf.salepay.payname += Language.apply("扣回");
					saleBS.addSaleRefundObject(cpf.salepay, cpf);

				}
				else
				{
					saleBS.addSalePayObject(cpf.salepay, cpf);
				}
				
				//添加记录当前行已经付款
				//alreadyAddSalePay = true;
				//addAlreadyPays(index,String.valueOf(money));
				
				// 记录当前付款方式
				rows[4] = String.valueOf(cpf.salepay.num5);

				addMessage(cpf, bufferStr);

				// 开始分摊到各个商品
				paymentApportion(cpf.salepay, cpf, false);

				if (GlobalInfo.sysPara.oldqpaydet != 'N' && sjje > 0 && yyje > 0)
				{
					isCloseShell = true;
				}

				return true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
//	 查询是否存在重复的付款方式
	public int getpaymentIndex(String paycode, String payno, String payidno)
	{
		Vector vi = saleBS.salePayment;

		if (saleBS.isRefundStatus())
		{
			vi = saleBS.refundPayment;
		}

		for (int n = 0; n < vi.size(); n++)
		{
			SalePayDef sp = (SalePayDef) vi.elementAt(n);

			if (sp.paycode.equals(paycode))
			{
				if (sp.idno.length() > 0 && payidno.length() > 0)
				{
					if (sp.idno.charAt(0) == payidno.charAt(0)) { return (int) sp.num5; }
				}
			}
		}

		return -1;
	}
	
//	判断接劵金额
    private boolean checkJjJe(String type,String custno,String jh,String je,String saletype) {
		
    	poplist = ((Cbbh_NetService)NetService.getDefault()).getTicketPop(String.valueOf(salehead.fphm),Cbbh_SaleBS.crmpopgoodsdetail,type,custno,jh,je,saletype);
    	
    	if(poplist != null)
    	{	
    		couponList.clear();
    		
        	((Cbbh_SaleBS)saleBS).setYeShowNew(this);
        	
        	setYeShow(form.getTable());
        
    		return true;
    	}

		return false;
	}
	
	public boolean createSalePay(String money)
	{
		try
		{
			// 创建付款信息
			if (super.createSalePay(money))
			{
				// salepay对象有效
				if (checkMzkMoneyValid())
				{
					// 记录帐号信息
					if (saveFindMzkResultToSalePay())
					{
						// 显示余额提示
						showAccountYeMsg();

						// 需要即时记账
						if (realAccountPay())
							return true;
					}
				}
			}
		}
		catch (Exception ex)
		{
			new MessageBox(Language.apply("生成交易付款对象失败\n\n") + ex.getMessage());
			ex.printStackTrace();
		}

		//
		salepay = null;
		return false;
	}

	protected boolean saveFindMzkResultToSalePay()
	{
		String memo = mzkreq.memo;		
		if (!super.saveFindMzkResultToSalePay()) { return false; }
		mzkreq.memo = memo;
		salepay.idno = memo;
		return true;
	}
	

	// 查询可付金额
	public double getCouponJe(String paycode, String payno, String couponID, String hl, int oldpayindex)
	{

		CalcRulePopDef calPop = null;
		// 如果原付款行号为-1，查询对应的商品行号
		if (oldpayindex == -1)
		{
			oldpayindex = getpaymentIndex(paycode, payno, couponID);
		}

		if (vi == null)
		{
			vi = new Vector();
		}
		else
		{
			vi.removeAllElements();
		}
		// 行号，满金额，收金额，分组规则，剩余未付金额
		for (int i = 0; i < saleBS.goodsAssistant.size(); i++)
		{
			GoodsDef goods = (GoodsDef) saleBS.goodsAssistant.elementAt(i);
			SaleGoodsDef sgd = (SaleGoodsDef) saleBS.saleGoods.elementAt(i);
			SpareInfoDef spinfo = (SpareInfoDef) saleBS.goodsSpare.elementAt(i);

			if (goods.str4 != null && goods.str4.length() > 0)
			{				
				String line = "";
				String[] gz = goods.str4.split("\\|");
				for(int k=0; k<gz.length; k++)
				{
					String[] arr = gz[k].split(",");
					//是否存在此券
					if (arr.length > 1 && arr[0].trim().equals(couponID))
					{
						line = gz[k].trim();
						break;

					}
				}
				// 券ID，收券条件，收券金额， 活动单号，是否跨柜
				if (line.length()>0)//goods.str4.indexOf(couponID + ",") >= 0)
				{
					/*String line = goods.str4.substring(goods.str4.indexOf(couponID + ","));
					if (line.indexOf("|") >= 0)
						line = line.substring(0, line.indexOf("|"));
					*/
					

					String[] values = line.split(",");

					String isOverGz = "Y";
					if (values.length > 4)
						isOverGz = values[4];

					// 剩余可分摊金额
					double je = getValidValue(i, oldpayindex);

					// 相同券已分摊金额
					double je1 = getftje(spinfo, paycode, payno, couponID.charAt(0));

					// 此券可收金额
					je = ManipulatePrecision.doubleConvert(je + je1);
					if (je >= 0)
					{
						int j = 0;
						for (j = 0; j < vi.size(); j++)
						{
							boolean isMerge = false;
							calPop = (CalcRulePopDef) vi.elementAt(j);
							if (isOverGz.equals("Y"))
							{
								if (calPop.code.equals(values[3]) && calPop.catid.equals(values[1]) && calPop.str1.equals(values[2]))
								{
									isMerge = true;
								}
							}
							else
							{
								if (calPop.str4.equals(sgd.gz) && calPop.code.equals(values[3]) && calPop.catid.equals(values[1]) && calPop.str1.equals(values[2]))
								{
									isMerge = true;
								}
							}
							if (isMerge)
							{
								if (GlobalInfo.sysPara.couponRuleType == 'Y')
								{
									calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + sgd.hjje - saleBS.getZZK(sgd));
								}
								else
								{
									calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + je);
								}
								calPop.row_set.add(new String[] { String.valueOf(i), String.valueOf(je) });
								calPop.str2 = ManipulatePrecision.doubleToString((Convert.toDouble(calPop.str2) + je1));
								break;
							}
							// if (calPop.code.equals(values[3]) &&
							// calPop.catid.equals(values[1]) &&
							// calPop.str1.equals(values[2]))
							// {
							// if (GlobalInfo.sysPara.couponRuleType == 'Y')
							// {
							// calPop.popje =
							// ManipulatePrecision.doubleConvert(calPop.popje +
							// sgd.hjje - saleBS.getZZK(sgd));
							// }
							// else
							// {
							// calPop.popje =
							// ManipulatePrecision.doubleConvert(calPop.popje +
							// je);
							// }
							// calPop.row_set.add(new String[] {
							// String.valueOf(i), String.valueOf(je) });
							// calPop.str2 =
							// ManipulatePrecision.doubleToString((Convert.toDouble(calPop.str2)
							// + je1));
							// break;
							// }
						}

						if (j >= vi.size())
						{
							if (Convert.toDouble(values[1]) <= 0)
								continue;

							calPop = new CalcRulePopDef();
							// calPop.code = values[3]; // 活动单号
							// calPop.rulecode = values[0]; // 规则码
							// calPop.catid = values[1]; // 条件金额
							// calPop.str1 = values[2]; // 收券金额
							// calPop.str2 = String.valueOf(je1); //
							calPop.rulecode = values[0]; // 规则码
							calPop.catid = values[1]; // 条件金额
							calPop.str1 = values[2]; // 收券金额
							if (values.length > 3)
								calPop.code = values[3]; // 活动单号
							else 
								calPop.code="";
							if (values.length > 4)
								calPop.str3 = values[4]; // 是否跨柜统计标志
							else
								calPop.str3 = "Y"; // 默认跨柜
							calPop.str4 = sgd.gz; // 柜组

							if (GlobalInfo.sysPara.couponRuleType == 'Y')
							{
								calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + sgd.hjje - saleBS.getZZK(sgd));
							}
							else
							{
								calPop.popje = ManipulatePrecision.doubleConvert(calPop.popje + je);
							}
							calPop.row_set = new Vector();
							calPop.row_set.add(new String[] { String.valueOf(i), String.valueOf(je) });// 行数和金额
							vi.add(calPop);
						}
					}
				}
			}
		}

		// 计算满收金额
		double ksje = 0; // 可收金额
		// double syze = 0; // 剩余总额
		for (int i = 0; i < vi.size(); i++)
		{
			calPop = (CalcRulePopDef) vi.elementAt(i);

			// 计算商品合计
			double syje = 0;

			if (calPop.popje >= Convert.toDouble(calPop.catid))
			{
				for (int j = 0; j < calPop.row_set.size(); j++)
				{
					String[] row = (String[]) calPop.row_set.elementAt(j);
					syje += Convert.toDouble(row[1]);
				}

				int num = ManipulatePrecision.integerDiv(calPop.popje, Convert.toDouble(calPop.catid));
				double je1 = ManipulatePrecision.doubleConvert(num * Convert.toDouble(calPop.str1));
				ksje += Math.min(je1, syje);
			}
			else
			{
				vi.remove(i);
				i--;
			}
		}

		// 减去已付款的此券金额（不包含同卡的已付金额）
		double yfje = 0;

		for (int i = 0; i < saleBS.salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) saleBS.salePayment.elementAt(i);

			if (DataService.getDefault().searchPayMode(sp.paycode).type == '5')
			{
				// 不同类型付款方式
				if (!isSameTypePayment(sp))
				{
					continue;
				}

				// 同卡
				if (sp.paycode.equals(paycode) && sp.payno.equals(payno))
				{
					continue;
				}

				if (sp.idno.charAt(0) == couponID.charAt(0)) // 券种相同
				{
					yfje += ManipulatePrecision.doubleConvert(sp.je - sp.num1);
				}
			}
		}

		double maxkfje = ManipulatePrecision.doubleConvert(ksje - yfje);

		// 模拟分摊，查看是否存在损益
		SalePayDef sp = new SalePayDef();
		sp.je = maxkfje;
		sp.paycode = paycode;
		sp.payno = payno;
		sp.idno = couponID;

		if (paymentApportion(sp, null, true))
		{
			maxkfje = ManipulatePrecision.doubleConvert(maxkfje - sp.num1);
		}

		return maxkfje;
	}
	
//	 分摊付款方式
	public boolean paymentApportion(SalePayDef spay, Payment payobj, boolean test)
	{
		//小a劵不分摊
		if(spay.idno.equalsIgnoreCase("a") || vi == null)return true;
		
		return super.paymentApportion(spay,payobj,test);
	}
	
//	 查询可付金额
	public String getValidJe(int index)
	{
		String line = "";
		
		if(poplist != null && poplist.size() > 0)
		{

			// 显示券类型
			for (int i = 0; i < poplist.size(); i++)
			{
				String[] row = (String[]) poplist.elementAt(i);
				
				if(row[2].equals("4"))continue;
				line += row[6]+"\n";
			}
		}
		//小票头保存整单促销描述信息
		salehead.str8 = "";
		salehead.str8 = line;
		
		return line;
	}
	

	 public static boolean ISSALE(String c)
	    {
	        if (c .equals( SellType.RETAIL_SALE)) return true;
	        if (c .equals( SellType.BATCH_SALE)) return true;
	        if (c .equals( SellType.EARNEST_SALE)) return true;
	        if (c .equals( SellType.PREPARE_TAKE)) return true;
	        if (c .equals( SellType.PREPARE_SALE)) return true;
	        if (c .equals( SellType.PREPARE_SALE1)) return true;
	        if (c .equals( SellType.EXERCISE_SALE)) return true;
	        if (c .equals( SellType.PURCHANSE_COUPON)) return true;
	        if (c.equals(SellType.JS_FK)) return true;
	        if (c.equals(SellType.JF_FK)) return true;
	        if (c.equals(SellType.GROUPBUY_SALE)) return true;
	        if (c.equals(SellType.CARD_SALE)) return true;
	        if (c.equals(SellType.PURCHANSE_JF)) return true;
//	        if (c.equals(HH_SALE)) return true;
//	        if (c .equals( JDXX_BACK) return true;
	        
	        return false;
	    }
	
	 
	 public boolean autoFindCard()
		{
		 
		 return true;
			/*ProgressBox pb = null;
			try
			{
				if(!isAutoFindCard())return true;
				
				this.autoTrack1 = null;
				this.autoTrack2 = null;
				this.autoTrack3 = null;
				
				//读取卡号
				pb = new ProgressBox();
				pb.setText("请在银联设备上刷卡...");
				String strTrack = ICCard.getDefault().findCard();
				PosLog.getLog(this.getClass().getSimpleName()).info("银联设备上刷卡 strTrack=[" + String.valueOf(strTrack) + "].");
				if(strTrack==null)
				{
					new MessageBox("从银联设备上读卡失败！");
					return false;
				}
				String[] arrTrack = strTrack.split(";");
				this.autoTrack1 = arrTrack[0].trim();
				if(arrTrack.length>1) this.autoTrack2 = arrTrack[1].trim();
				if(arrTrack.length>2) this.autoTrack3 = arrTrack[2].trim();
				return true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			}
			finally
			{
				if(pb!=null) 
				{
					pb.close();
					pb=null;
				}
			}
			return false;*/
		}
		
		public boolean isAutoFindCard()
		{
			try
			{
				if(GlobalInfo.sysPara.isUseBankReadTrack=='Y') return true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return false;
		}

		public boolean findFjk(String track1, String track2, String track3)
		{
			if(isAutoFindCard())
			{
				track1=this.autoTrack1;
				track2=this.autoTrack2;
				track3=this.autoTrack3;
				PosLog.getLog(this.getClass().getSimpleName()).info("findFjk() track1=[" + String.valueOf(autoTrack1) + "],track2=[" + String.valueOf(autoTrack2) + "],track3=[" + String.valueOf(autoTrack3) + "].");
			}
			return super.findFjk(track1, track2, track3);
		}
		
		
		protected String getDisplayAccountInfo()
		{
			return Language.apply("请 刷 卡");
		}
		
		public void specialDeal (Cbbh_PaymentCouponNewEvent event)
		{
		}
		
		
	/*	//获取整单促销，接劵，送劵列表
		public Vector getTicketPop(Vector popsalegoods)
		{
			return ((Cbbh_NetService)NetService.getDefault()).getTicketPop(popsalegoods,"0","123123","","");
		}
		
		
		//设置劵金额
		public boolean setYeShowNew(SaleBS sale)
		{
			poplist = getTicketPop(((Cbbh_SaleBS)sale).crmpopgoodsdetail);
			
			if(poplist == null)return false;
			
			if(poplist.size() > 0)
			{

				// 显示券类型
				for (int i = 0; i < poplist.size(); i++)
				{
//					String[] str = new String[3];
					String[] row = (String[]) poplist.elementAt(i);
					
					//积点
					if(row[2].equals("3"))
					{
						for(int j=0;j<((Cbbh_SaleBS)sale).crmpopgoodsdetail.size();j++)
						{
							CrmPopDetailDef cpd = (CrmPopDetailDef) ((Cbbh_SaleBS)sale).crmpopgoodsdetail.elementAt(j);
							if(String.valueOf(cpd.rowno).equals(row[1]))
							{
								((CrmPopDetailDef) ((Cbbh_SaleBS)sale).crmpopgoodsdetail.elementAt(j)).jdsl = Convert.toDouble(row[11]);
							}
						}
					}
					
					//满减
					if(row[2].equals("5"))
					{						
						for(int j=0;j<((Cbbh_SaleBS)sale).crmpopgoodsdetail.size();j++)
						{
							CrmPopDetailDef cpd = (CrmPopDetailDef) ((Cbbh_SaleBS)sale).crmpopgoodsdetail.elementAt(j);
							
							((SaleGoodsDef)saleBS.saleGoods.elementAt(j)).yhzke = cpd.zdrulepopzk;
							((SaleGoodsDef)saleBS.saleGoods.elementAt(j)).yhzkfd = cpd.zdrulepopzkfd;
							
							
//							 计算商品的合计
							saleBS.getZZK(((SaleGoodsDef)saleBS.saleGoods.elementAt(j)));
							
							saleBS.calcHeadYsje();
						}
						
					}
					
					if(!row[2].equals("4"))continue;
					
					String[] lines = {row[7],row[8],String.valueOf(Convert.toDouble(row[9])*Convert.toDouble(row[10])),"","-1","1"};
					couponList.add(lines);
				}
				
				return true;
			}
			return true;
		}*/
		
//		 设置金额
		public boolean setYeShow(Table table)
		{
			// 设置余额列表
			table.removeAll();

			// 显示券类型
			for (int i = 0; i < couponList.size(); i++)
			{
				//String[] str = new String[3];
				String[] str = new String[2];
				TableItem item = null;
				String[] row = (String[]) couponList.elementAt(i);

				str[0] = row[1];
				str[1] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
				//str[2] = ManipulatePrecision.doubleToString(Convert.toDouble(row[2]));
				
				item = new TableItem(table, SWT.NONE);
				item.setText(str);
			}
			return true;
		}
		
		protected boolean checkMoneyValid(String money, double ye)
		{
			try
			{
				if (money.equals(""))
				{
					new MessageBox(Language.apply("付款金额不能为空!"));

					return false;
				}

				double ybje = Double.parseDouble(saleBS.getPayMoneyByPrecision(Double.parseDouble(money), paymode));

				if (ybje <= 0)
				{
					new MessageBox(Language.apply("付款金额必须大于0"));

					return false;
				}

				if (GlobalInfo.sysPara.payprecision == 'Y')
				{
					if (!checkPayPrecision(Double.parseDouble(money))) { return false; }
				}

				if (ybje < paymode.minval || ybje > paymode.maxval)
				{
					// new MessageBox("该付款方式的有效付款金额必须在\n\n" +
					// ManipulatePrecision.doubleToString(paymode.minval) + " 和 " +
					// ManipulatePrecision.doubleToString(paymode.maxval) + " 之间!");
					new MessageBox(Language.apply("该付款方式的有效付款金额必须在\n\n{0} 和 {1} 之间!", new Object[] { ManipulatePrecision.doubleToString(paymode.minval), ManipulatePrecision.doubleToString(paymode.maxval) }));

					return false;
				}

				// 判断是否溢余应都转换成原币金额再比较,以避免汇率带来的误差
				/*if (paymode.isyy != 'Y' && ManipulatePrecision.doubleCompare(ybje, Double.parseDouble(saleBS.getPayMoneyByPrecision(ye / paymode.hl, paymode)), 2) > 0)
				{
					new MessageBox(Language.apply("该付款方式不允许溢余!"));

					return false;
				}*/

				// 检查金额是否超过该付款方式的收款规则
				/*if (GlobalInfo.sysPara.havePayRule == 'Y' || GlobalInfo.sysPara.havePayRule == 'A')
				{
					if (!this.allowpayjealready)
						this.allowpayje = ManipulatePrecision.doubleConvert(calcPayRuleMaxMoney() / paymode.hl + 0.009, 2, 0);
					if (this.allowpayje >= 0 && paymode.isyy != 'Y' && ManipulatePrecision.doubleCompare(ybje, this.allowpayje, 2) > 0)
					{
						// new MessageBox("该付款方式最多允许付款 " +
						// ManipulatePrecision.doubleToString(allowpayje) + " 元");
						new MessageBox(Language.apply("该付款方式最多允许付款 {0} 元", new Object[] { ManipulatePrecision.doubleToString(allowpayje) }));

						return false;
					}
				}*/

				// 在销售时检查付款限额
				if (SellType.ISSALE(salehead.djlb) && !checkPaymentLimit(ybje))
					return false;

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			return false;
		}

}
