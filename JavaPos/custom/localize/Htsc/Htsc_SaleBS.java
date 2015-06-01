package custom.localize.Htsc;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.SjjDateform;
import com.efuture.javaPos.UI.Design.SjjUserInfoForm;

import custom.localize.Bcrm.Bcrm_DataService;
import custom.localize.Htsc.Htsc_DataService;
import custom.localize.Cmls.Cmls_SaleBS;

public class Htsc_SaleBS extends Cmls_SaleBS
{
	private String tempGz ="";
	private String tempCode ="";
	private String tempPhone ="";
	private String tempcustno = "";
	private String tempname= "";
	private String temptel= "";
	private String tempaddr= "";
//	public void initSellData()
//	{
//		super.initSellData();
//		// 收银机组
//		saleHead.str1 = GlobalInfo.syjDef.priv;
//		saleHead.str3 = GlobalInfo.tempDef.str3; //顾客代码
//		//saleHead.str4 = GlobalInfo.tempDef.str4; //特价键
//		saleHead.str5 = GlobalInfo.tempDef.str5; //销售时间
//		//System.out.println("销售时间："+saleHead.str5);
//	}
	//根据特价标志控制促销
	public boolean paySellPop()
	{
		if(GlobalInfo.tempDef.str4.equals("Y"))
		{
			// 处理CRM促销
			doRulePopExit = false;

			//if (GlobalInfo.sysPara.isSuperMarketPop == 'Y') doSuperMarketCrmPop();

			//haveRulePop = doCrmPop();

			if (doRulePopExit) return false; // 不再继续进行付款

			if (havePaymode)
			{
				havePaymode = false;
				return false;
			}
			return true;
		}
		else
		{
			return super.paySellPop();
		}
	}
	private void getSaleNo()
	{
		if((!"".equals(tempGz)))
		{
			tempGz ="";
		}
		if(!"".equals(tempCode))
		{
			tempCode = "";
		}
		
		if(!"".equals(tempPhone))
		{
			tempPhone = "";
		}
		
		if(!"".equals(tempcustno))
		{
			tempcustno = "";
		}
		if(!"".equals(tempname))
		{
			tempname = "";
		}
		if(!"".equals(temptel))
		{
			temptel = "";
		}
		if(!"".equals(tempaddr))
		{
			tempaddr = "";
		}
		
		if(null!=GlobalInfo.custInfoDef)
		{
			GlobalInfo.custInfoDef.custno= "";
			GlobalInfo.custInfoDef.custname = "";
			GlobalInfo.custInfoDef.custphone = "";
			GlobalInfo.custInfoDef.custtel = "";
			GlobalInfo.custInfoDef. custaddr = "";
		}
		StringBuffer req = new StringBuffer();
		boolean done = new TextBox().open(Language.apply("请输入销售单号"), Language.apply("销售单号"), Language.apply("请根据营业员的单据输入销售单号"), req, 0, 0, false,
											TextBox.IntegerInput);
		if (done)
		{
			String saleNo = req.toString();
			Vector ret = new Vector();
			Htsc_NetService netService = (Htsc_NetService) NetService.getDefault();
			if (netService.validSaleNo(ret, saletype, saleNo))
			{
				saleHead.salefphm = saleNo;
				String curGz = (String)ret.get(0);
				String yyyh = (String)ret.get(4);
				String code = (String)ret.get(3);
				String custno = (String)ret.get(5);
				String  name= (String)ret.get(6);
				String phone = (String)ret.get(7);
				String  tel= (String)ret.get(8);
				String  addr= (String)ret.get(9);
				saleEvent.saleform.gz.setText(curGz);
				tempGz = curGz;
				tempCode= code;
				tempPhone = phone;
				tempcustno = custno;
				tempname=name;	
				temptel= tel;
				tempaddr = addr;
				saleEvent.saleform.setFocus(saleEvent.yyyh);
				saleEvent.saleform.yyyh.setText(yyyh);
			
			/*	// 补余款时获取之前的定金金额
				if (saletype.equals(SellType.JJ_FINAL_SALE))
				{
					saleHead.num9 = Double.parseDouble((String)ret.get(1));
				}*/
			}
			else
			{
				new MessageBox("输入的销售单号无效，请重新输入");
				getSaleNo();
			}
			
			/*//定金和补余款需要展示
			if (saletype.equals(SellType.JJ_FINAL_SALE)||saletype.equals(SellType.JJ_EARNEST_SALE)||saletype.equals(SellType.JJ_EARNEST_BACK))
			{
				new MessageBox("定金总金额："+Double.parseDouble((String)ret.get(1))+
						" "+"定金笔数："+ret.get(2), null, false);
				return;
			}*/
		}
		else
		{
			saleEvent.yyyh.setFocus();
		}
		
		
	}
	
	// 验证销售单号并获取柜组
	protected void initOperation()
	{
		if (SellType.ISSALE(saletype))
		{
			getSaleNo();
		}
		
		/*if(("".equals(tempPhone.trim())||null == tempPhone)&&!saletype.equals(SellType.JJ_EARNEST_BACK)&&!saletype.equals(SellType.RETAIL_BACK))
		{
			SjjUserInfoForm sjjUserInfo = new SjjUserInfoForm();
			sjjUserInfo.open();
			
		}
		else
		{
			Htsc_NetService netService = (Htsc_NetService) NetService.getDefault();
			
			if(!saletype.equals(SellType.JJ_EARNEST_BACK)&&!saletype.equals(SellType.RETAIL_BACK))
			{
				if(netService.addOrModUserInfo(tempcustno,tempname, tempPhone, temptel,tempaddr))
				{
				
					GlobalInfo.tempDef.str3 = tempcustno;
					new MessageBox("顾客代码："+tempcustno+"  "+
					"姓名："+tempname+"  "+
					"手机："+tempPhone+"  "+
					"电话："+temptel, null, false);
				}
			}
		}*/
		
		super.initOperation();
	}
	
	public void backSell()
	{
		if (GlobalInfo.syjDef.isth != 'Y')
		{
			new MessageBox(Language.apply("该收银机不允许退货!"));

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		// 已经是指定小票退货状态,再次按退货键则重新输入原小票信息
		if (SellType.isJS(this.saletype))
		{
		}
		else if (EBill.getDefault().getBackSaleBill(this)) // android 指定小票退货
		{

		}
		else if (isSpecifyTicketBack())
		{
			StringBuffer req = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入销售单号"), Language.apply("销售单号"), Language.apply("请根据营业员的单据输入销售单号"), req, 0, 0, false,
												TextBox.IntegerInput);
			if (done)
			{
				thFphm = Long.parseLong(req.toString());
				thSyjh = saletype;
				isbackticket = findBackTicketInfo();
			}
			else
			{
				thSyjh = null;
				thFphm = 0;
			}

			/*
			RetSYJForm frm = new RetSYJForm();

			int done = frm.open(thSyjh, thFphm);

			if (done == frm.Done)
			{
				thSyjh = RetSYJForm.syj;
				thFphm = Long.parseLong(RetSYJForm.fph);

				if (this.saletype.equals(SellType.PREPARE_BACK))
				{
					isbackticket = findPreSaleInfo();
				}
				else
				{
					isbackticket = findBackTicketInfo();
				}
			}
			else if (done == frm.Clear)
			{
				thSyjh = null;
				thFphm = 0;
			}
			else
			{
				// 放弃,不修改上次输入的原收银机号和小票号
			}

			return;
			*/
		}

		// 从销售状态切换到相应的退货状态
		if ((saleGoods.size() <= 0) && SellType.ISSALE(saletype))
		{
			// 检查权限
			thgrantuser = null;
			if (((curGrant.privth != 'Y') && (curGrant.privth != 'T')) || (curGrant.thxe <= 0))
			{
				OperUserDef staff = backSellGrant();

				if (staff == null) { return; }

				// 本次授权
				thgrantuser = staff;

				// 记录日志
				String log = "授权退货,小票号:" + GlobalInfo.syjStatus.fphm + ",最大退货限额:" + thgrantuser.thxe + ",授权:" + thgrantuser.gh;
				AccessDayDB.getDefault().writeWorkLog(log);
			}
			else
			{
				thgrantuser = (OperUserDef) (GlobalInfo.posLogin.clone());

				if (cursqktype == '1')
				{
					thgrantuser.gh = cursqkh;
				}
				else
				{
					thgrantuser.gh = GlobalInfo.posLogin.gh;
				}
			}

			// 提示退货权限
			if (curGrant.privth != 'T')
			{
				new MessageBox(Language.apply("授权退货,限额为{0}元", new Object[] { ManipulatePrecision.doubleToString(thgrantuser.thxe) }));
			}

			// 切换到退货交易类型
			djlbSaleToBack();

			// 初始化交易
			initOneSale(this.saletype);
		}
		else
		{
			new MessageBox(Language.apply("请先完成当前交易!"), null, false);
		}
	}
	
	protected void initBackSell()
	{
		// 如果是要输入家电发货地点,并且是新指定小票退货则输入家电发货地点
		if (GlobalInfo.sysPara.isinputjdfhdd != 'N' && !isNewUseSpecifyTicketBack(false))
		{
			if (!SellType.ISCHECKINPUT(this.saletype))
			{
				if (GlobalInfo.sysPara.isinputjdfhdd == 'S' && jdfhddcode.trim().length() > 0)
				{
					if (GlobalInfo.syjDef.issryyy == 'N' || GlobalInfo.syjDef.issryyy == 'B')
					{
						saleHead.jdfhdd = jdfhddcode;

						saleEvent.yyyh.setText(Language.apply("家电"));
						saleEvent.gz.setText("[" + jdfhddcode + "]" + jdfhddname);
					}
				}
				else
				{
					inputJdfhdd();
				}
			}
		}

		// 退货交易初始化以后，恢复授权信息
		if (SellType.ISBACK(this.saletype) && thgrantuser != null)
		{
			saleHead.thsq = thgrantuser.gh;
			curGrant.privth = thgrantuser.privth;
			curGrant.thxe = thgrantuser.thxe;

			thgrantuser = null;
		}

		// 判断退货是否输入收银机号
		if (SellType.isJS(this.saletype))
		{
		}
		else if (isSpecifyTicketBack())
		{
			StringBuffer req = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入销售单号"), Language.apply("销售单号"), Language.apply("请根据营业员的单据输入销售单号"), req, 0, 0, false,
												TextBox.IntegerInput);
			if (done)
			{
				thFphm = Long.parseLong(req.toString());
				thSyjh = saletype;
				isbackticket = findBackTicketInfo();
				
				if (!isbackticket)
				{
					if (GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C')
					{
						// 退回对应的销售类型
						djlbBackToSale();

						// 初始化交易
						initOneSale(this.saletype);
					}
					else if (GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'Y')
					{
						twoBackPersonGrant();
					}
				}
			}
			else if (!done && (GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'Y'))
			{
				// 二次授权
				twoBackPersonGrant();

			}
			else if (!done && (GlobalInfo.sysPara.inputydoc == 'C' || GlobalInfo.sysPara.inputydoc == 'B'))
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
			else if (SellType.isJF(saletype))
			{
				// 退回对应的销售类型
				djlbBackToSale();

				// 初始化交易
				initOneSale(this.saletype);
			}
		}
	}
	
	public void custMethod()
	{
		/*if (saletype.equals(SellType.JJ_FINAL_SALE))
		{
			if (saleHead.num9 > 0)
			{
				if (saleHead.num9 <= saleHead.ysje)
				{
					createJjEarnestPayment(saleHead.num9);
				}
			}
		}*/
		super.custMethod();
	}
	
	// 自动生成定金付款方式
	private boolean createJjEarnestPayment (double je)
	{
		PayModeDef paymode = DataService.getDefault().searchPayMode("0602");
		if (paymode == null)
		{
			new MessageBox(" 0602 付款方式未定义!\n无法计入定金");
			return false;
		}
		// 付款记账

		//创建一个付款方式对象
		Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
		if (pay == null) return false;

		// inputPay这个方法根据不同的付款方式进行重写
		SalePayDef sp = pay.inputPay(String.valueOf(je));

		// 标记本行付款唯一序号,用于删除对应商品的分摊
		if (sp != null) sp.num5 = salePayUnique++;

		// 加入付款明细
		salePayment.add(sp);
		payAssistant.add(pay);
		return true;
	}
	
	public void paySell()
	{
		if (SellType.ISSALE(saletype) && saleHead.fphm == 0)
		{
			getSaleNo();
		}
		else
		{
			super.paySell();
		}
	}
	
	public void payInput()
	{
		if(null==saleHead.salefphm||"".equals(saleHead.salefphm))
		{
			//获得发票号
			StringBuffer req = new StringBuffer();
			boolean done = new TextBox().open(Language.apply("请输入销售单号"), Language.apply("销售单号"), Language.apply("请根据营业员的单据输入销售单号"), req, 0, 0, false,
										TextBox.IntegerInput);
		
			if(done)
			{
				String saleNo = req.toString();
				Vector ret = new Vector();
				Htsc_NetService netService = (Htsc_NetService) NetService.getDefault();
				if (netService.validSaleNo(ret, saletype, saleNo))
				{
					saleHead.salefphm = saleNo;
					String curGz = (String)ret.get(0);
					saleEvent.gz.setText(curGz);
					//获得营业员柜组
					OperUserDef staff = findYYYH(saleEvent.yyyh.getText());
					//saleEvent.saleform.setFocus(saleEvent.yyyh);
					
					// 补余款时获取之前的定金金额
					/*if (saletype.equals(SellType.JJ_FINAL_SALE))
					{
						saleHead.num9 = Double.parseDouble((String)ret.get(1));
					}*/
					if(curGz.equals(staff.yyygz)&&(!("").equals(curGz)))
					{
						super.payInput();
					}
					else
					{
						new MessageBox(Language.apply("营业员柜组与销售单号柜组不匹配请重新输入！"+" "+"营业员柜组："+staff.yyygz
								+" "+"销售单号柜组："+curGz), null, false);
					}
				}
				else
				{
					new MessageBox("输入的销售单号无效，请重新输入");
					//this.payInput();
				}
				
			}
			
		}
		else
		{
			super.payInput();
		}
		
	}
	
	//获得营业员 更具营业员查询柜组
	public void enterInputYYY()
	{
		// 不输入营业员，认为超市销售
		if ((saleEvent.yyyh.getText().length() <= 0 || saleEvent.yyyh.getText().equals(Language.apply("超市"))) && (GlobalInfo.syjDef.issryyy == 'A') && saleTypeControl())
		{
			saleEvent.yyyh.setText(Language.apply("超市"));
			saleEvent.gz.setText(Language.apply("超市柜"));
			saleEvent.saleform.setFocus(saleEvent.code);

			// 输入超市柜的时候也需要输入发票单
			csExtendAction();

			// 家电销售
			if (jdfhddcode != null && jdfhddcode.length() > 0)
			{
				saleEvent.yyyh.setText(Language.apply("家电"));
				saleEvent.gz.setText("[" + jdfhddcode + "]" + jdfhddname);
			}

			return;
		}

		//
		if (saleEvent.yyyh.getText().length() <= 0)
		{
			new MessageBox(Language.apply("营业员不能为空，请重新输入!"), null, false);
			saleEvent.yyyh.selectAll();

			return;
		}

		// 查找营业员
		OperUserDef staff = null;

		if ((staff = findYYYH(saleEvent.yyyh.getText())) != null)
		{
			if (staff.type != '2')
			{
				new MessageBox(Language.apply("该工号不是营业员!"), null, false);
				saleEvent.yyyh.selectAll();

				return;
			}

			// 检查工号过期
			String expireDate = staff.maxdate + " 0:0:0";
			ManipulateDateTime mdt = new ManipulateDateTime();

			if (mdt.getDisDateTime(mdt.getDateBySlash() + " 0:0:0", expireDate) < 0)
			{
				new MessageBox(Language.apply("该工号已过期!"), null, false);
				saleEvent.yyyh.selectAll();

				return;
			}

			// 营业员扩展操作
			if (!yyhExtendAction(staff))
				return;

			// 不控制营业员创柜
			curyyygz = staff.yyygz;
			if (GlobalInfo.sysPara.yyygz != 'N')
			{
				// 营业员定义了所属柜组
				if (staff.yyygz.length() > 0)
				{
					String[] s = staff.yyygz.split(",");
					if (s.length <= 1)
					{
						saleEvent.gz.setText(getGZDisplay(staff)); // 只有一个所属柜
						//销售单号的柜组必须与营业员柜组相等
						if(!tempGz.equals(staff.yyygz)&&(!("").equals(tempGz)))
						{
							new MessageBox(Language.apply("营业员柜组与销售单号柜组不匹配请重新输入！"+" "+"营业员柜组："+staff.yyygz
									+" "+"销售单号柜组："+tempGz), null, false);
							saleEvent.yyyh.setFocus();
							return ;
						}
						
					}
					else
					{
						saleEvent.gz.setText(Language.apply("多个柜")); // 有多个所属柜,先按不控制柜方式查找商品
					}
					if (GlobalInfo.sysPara.yyygz == 'A') // A-可修改柜组
					{
						saleEvent.gz.selectAll();
						saleEvent.saleform.setFocus(saleEvent.gz);
					}
					else
					{
						saleEvent.saleform.setFocus(saleEvent.code);
						saleEvent.saleform.code.setText(tempCode);
					}
				}
				else
				{
					if (GlobalInfo.sysPara.yyygz == 'A')
					{
						if (getGzCode(saleEvent.gz.getText()).trim().equals(Language.apply("超市柜")))
						{
							saleEvent.gz.setText("");
						}
					}

					saleEvent.saleform.setFocus(saleEvent.gz);
				}
			}
			else
			{
				saleEvent.gz.setText(Language.apply("任意柜"));
				saleEvent.saleform.code.setText(tempCode);
				saleEvent.saleform.setFocus(saleEvent.code);
			}

			// 家电销售
			if (jdfhddname != null && jdfhddname.length() > 0)
			{
				saleEvent.gz.setText(jdfhddname);
			}
		}
		else
		{
			saleEvent.yyyh.selectAll();
		}
	}
	
	public boolean findBackTicketInfo()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if (GlobalInfo.sysPara.inputydoc == 'D')
			{
				// 只记录原单小票号和款机号,但不按原单找商品
				return false;
			}

			// 如果是新指定小票进入
			if (SellType.ISHH(saletype) || saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText(Language.apply("开始查找退货小票操作....."));
				if (!DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
				{
					pb.close();
					pb = null;
					thSyjh = null;
					thFphm = 0;

					return false;
				}

				pb.close();
				pb = null;
				// 检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				// Y为已在后台退回礼品 津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					new MessageBox(Language.apply("此小票有满赠礼品，请先到后台退回礼品再办理退货！"));
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + Language.apply("\n是否继续退货？"), null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE) && !SellType.ISHH(saletype))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox(Language.apply("原小票是[{0}]交易\n\n与当前退货交易类型不匹配", new Object[] { SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) }));
						// new MessageBox("原小票是[" +
						// SellType.getDefault().typeExchange(thsaleHead.djlb,
						// thsaleHead.hhflag, thsaleHead) +
						// "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { Language.apply("序"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("原数量"), Language.apply("原折扣"), Language.apply("原成交价"), Language.apply("退货"), Language.apply("退货数量") };
				int[] width = { 30, 100, 170, 80, 80, 100, 60, 100, 55 };
				String[] row = null;
				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					row = new String[8];
					row[0] = String.valueOf(sgd.rowno);

					if (sgd.inputbarcode.equals(""))
					{
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A"))
							sgd.inputbarcode = sgd.barcode;
						row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
							sgd.inputbarcode = sgd.code;
						row[1] = sgd.code;
					}
					else
					{
						row[1] = sgd.inputbarcode;
					}

					row[2] = sgd.name;
					row[3] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
					row[4] = ManipulatePrecision.doubleToString(sgd.hjzk);
					row[5] = ManipulatePrecision.doubleToString(sgd.hjje - sgd.hjzk);
					row[6] = "";
					row[7] = "";
					choice.add(row);
				}

				String[] title1 = { Language.apply("序"), Language.apply("付款名称"), Language.apply("账号"), Language.apply("付款金额") };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				int cho = -1;
				if (EBill.getDefault().isEnable() && EBill.getDefault().isBack())
				{
					cho = EBill.getDefault().getChoice(choice);
				}
				else
				{
					// 选择要退货的商品
					cho = new MutiSelectForm().open(Language.apply("在以下窗口输入单品退货数量(付款键全选,确认键保存退出)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open(Language.apply("开单营业员号："), "", Language.apply("请输入有效开单营业员号"), backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox(Language.apply("该工号不是营业员!"), null, false);
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}

				}

				// 如果cho小于0且已经选择过退货小票
				if (cho < 0 && isbackticket)
					return true;
				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 清除已有商品明细,重新初始化交易变量

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
					if (!row[6].trim().equals("Y"))
						continue;

					SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);

					sgd.yfphm = sgd.fphm;
					sgd.ysyjh = sgd.syjh;
					sgd.yrowno = sgd.rowno;
					sgd.memonum1 = sgd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					sgd.str4 = backYyyh.toString();
					sgd.ysl = sgd.sl;
					sgd.str13 = "";
					if(SellType.ISHH(saletype)) sgd.str13 = "T";

					// 重算商品行折扣
					if (ManipulatePrecision.doubleCompare(sgd.sl, thsl, 4) > 0)
					{
						sgd.hjje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hjje, sgd.sl), thsl), 2, 1); // 合计金额
						sgd.hyzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl), thsl), 2, 1); // 会员折扣额(来自会员优惠)
						sgd.yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl), thsl), 2, 1); // 优惠折扣额(来自营销优惠)
						sgd.lszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl), thsl), 2, 1); // 零时折扣额(来自手工打折)
						sgd.lszre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl), thsl), 2, 1); // 零时折让额(来自手工打折)
						sgd.lszzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl), thsl), 2, 1); // 零时总品折扣
						sgd.lszzr = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl), thsl), 2, 1); // 零时总品折让
						sgd.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl), thsl), 2, 1); // 批量折扣
						sgd.zszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl), thsl), 2, 1); // 赠送折扣
						sgd.cjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl), thsl), 2, 1); // 厂家折扣
						sgd.ltzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl), thsl), 2, 1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl), thsl), 2, 1);
						sgd.qtzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl), thsl), 2, 1);
						sgd.qtzre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl), thsl), 2, 1);
						sgd.hjzk = getZZK(sgd);
						sgd.sl = thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
				}

				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';

					/*
					 * 业务过程只支持磁道查询,不支持卡号查询,因此无法检查原交易会员卡是否有效 if
					 * (!DataService.getDefault().getCustomer(curCustomer,
					 * thsaleHead.hykh)) { curCustomer.code = thsaleHead.hykh;
					 * curCustomer.name = "无效卡"; curCustomer.ishy = 'Y';
					 * 
					 * new MessageBox("原交易的会员卡可能已失效!\n请重新刷卡后进行退货"); }
					 */
				}

				// 设置原小票头信息
				saleHead.hykh = thsaleHead.hykh;
				saleHead.hytype = thsaleHead.hytype;
				saleHead.jfkh = thsaleHead.jfkh;

				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.salefphm = thsaleHead.salefphm;

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				// 为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox(Language.apply("超出退货的最大限额，不能退货"));

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							new MessageBox(Language.apply("授权退货,限额为 {0} 元", new Object[] { ManipulatePrecision.doubleToString(curGrant.thxe) }));
						}
					}
				}

				backPayment.removeAllElements();
				backPayment.addAll(thsalePayment);

				// 刷新界面显示
				saleEvent.clearTableItem();
				saleEvent.updateSaleGUI();

				return isbackticket;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}
	}
	
	/*public String setGoodsLSJ(GoodsDef goodsDef, StringBuffer pricestr)
	{
		// 补余款时获取之前的定金金额
		if (saletype.equals(SellType.JJ_FINAL_SALE))
		{
		double min = 0.01;
		if (goodsDef.type == 'Z')
		{
			min = 0;
		}

		boolean done = new TextBox().open(Language.apply("请输入商品[") + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + Language.apply("价格"), Language.apply("价格"), "", pricestr, min, getMaxSaleGoodsMoney(), true);
		if (!done)
			return null;
			Double je = Double.parseDouble(pricestr.toString()) - saleHead.num9;
			if(je<0)
			{
				new MessageBox("商品金额不能小于定金金额，请重新输入商品金额");
				return null;
			}
			else
			{
				new MessageBox("商品订金为:"+saleHead.num9);
				return pricestr.toString();
			}
			
		}
		else
		{
			return super.setGoodsLSJ(goodsDef, pricestr);
		}

	}*/

	
	//石家庄定制需求调用顾客信息页面
	public void execCustomKey0(boolean keydownonsale)
	{
		SjjUserInfoForm sjjUserInfo = new SjjUserInfoForm();
		sjjUserInfo.open();
	}
	//石家庄定制需求 特价键
	public void execCustomKey1(boolean keydownonsale)
	{
		GlobalInfo.tempDef.str4 = "Y";
		new MessageBox("已屏蔽促销规则");
	}
	//石家庄定制需求 取消特价键
	public void execCustomKey2(boolean keydownonsale)
	{
		GlobalInfo.tempDef.str4 = "N";
		new MessageBox("已恢复促销规则");
	}
	//石家庄定制需求时间输入
	public void execCustomKey3(boolean keydownonsale)
	{
		SjjDateform  sjjDateform = new SjjDateform();
		sjjDateform.open();
	}
	
	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String cardno = null;
		String cardtype = null;
		String isfjk = "";
		String grouplist = "";
		String newyhsp = "90000000";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2) isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Htsc_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype,isfjk,grouplist,saletype,saleHead.salefphm);
		
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}
		
		// 换货状态下，不使用任何促销
		if (popDef.yhspace == Convert.toInt(newyhsp) || hhflag == 'Y')
		{
			popDef.yhspace = Convert.toInt(newyhsp);
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		//将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;

		// 促销联比例
		sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
		goods.xxtax = Convert.toDouble(popDef.ksrq);
		if (goods.memo == null) goods.memo = "";

		// 增加CRM促销信息
		crmPop.add(popDef);

		// 标志是否为9开头扩展的控制
		boolean append = false;
		// 无促销,此会员不允许促销
		if (popDef.yhspace == 0)
		{
			append = false;
			info.str1 = "0000";
		}
		else if (popDef.yhspace == Integer.parseInt(newyhsp))
		{
			append = true;
			info.str1 = newyhsp;
		}
		else
		{
			
			if (String.valueOf(popDef.yhspace).charAt(0) != '9')
			{
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
				else info.str1 = Convert.increaseInt(popDef.yhspace, 4);
				
				append = false;
			}
			else 
			{
				info.str1 = String.valueOf(popDef.yhspace);
				
				append = true;
			}
			//询问参加活动类型 满减或者满增
			String yh = info.str1;
			
			if (append) yh = yh.substring(1);
			
			StringBuffer buff = new StringBuffer(yh);
			Vector contents = new Vector();

			for (int i = 0; i < buff.length(); i++)
			{
				// 2-任选促销/1-存在促销/0-无促销
				if (buff.charAt(i) == '2')
				{
					if (i == 0)
					{
						contents.add(new String[] { "D", "参与打折促销活动", "0" });
					}
					else if (i == 1)
					{
						contents.add(new String[] { "J", "参与减现促销活动", "1" });
					}
					else if (i == 2)
					{
						contents.add(new String[] { "Q", "参与返券促销活动", "2" });
					}
					else if (i == 3)
					{
						contents.add(new String[] { "Z", "参与赠品促销活动", "3" });
					}
					else if (i == 5)
					{
						contents.add(new String[] { "F", "参与积分活动", "5" });
					}
				}
			}

			if (contents.size() <= 1)
			{
				if (contents.size() > 0)
				{
					String[] row = (String[]) contents.elementAt(0);
					int i = Integer.parseInt(row[2]);
					buff.setCharAt(i, '1');
				}
			}
			else
			{
				String[] title = { "代码", "描述" };
				int[] width = { 60, 400 };
				int choice = new MutiSelectForm().open("请选择参与满减满赠活动的规则", title, width, contents);

				for (int i = 0; i < contents.size(); i++)
				{
					if (i != choice)
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '0');
					}
					else
					{
						String[] row = (String[]) contents.elementAt(i);
						int j = Integer.parseInt(row[2]);
						buff.setCharAt(j, '1');
					}
				}
			}

			if (append) info.str1 = "9"+buff.toString();
			else info.str1 = buff.toString();
		}

		String line = "";
		
		String yh = info.str1;
		if (append) yh = info.str1.substring(1);
		

		if (yh.charAt(0) != '0')
		{
			line += "D";
		}

		if (yh.charAt(1) != '0')
		{
			line += "J";
		}

		if (yh.charAt(2) != '0')
		{
			line += "Q";
		}

		if (yh.charAt(3) != '0')
		{
			line += "Z";
		}
		
		if (yh.length() > 5 && yh.charAt(5) != '0')
		{
			line += "F";
		}

		if (line.length() > 0)
		{
			sg.name = "(" + line + ")" + sg.name;
		}

		if (!append)
		{
			// str3记录促销组合码
			if (GlobalInfo.sysPara.iscrmtjprice == 'Y') sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
			else sg.str3 = info.str1;
		}
		else
		{
			sg.str3 = info.str1;
		}
		// 将商品属性码,促销规则加入SaleGoodsDef里
		sg.str3 += (";" + goods.specinfo);
		sg.str3 += (";" + popDef.memo);
		sg.str3 += (";" + popDef.poppfjzkl);
		sg.str3 += (";" + popDef.poppfjzkfd);
		sg.str3 += (";" + popDef.poppfj);

		// 只有找到了规则促销单，就记录到小票
		if (!info.str1.equals("0000") || !info.str1.equals(newyhsp))
		{
			sg.zsdjbh = popDef.djbh;
			sg.zszkfd = popDef.poplsjzkfd;
		}
	}
	
	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		GoodsPopDef goodspopDef = (GoodsPopDef) crmPop.elementAt(index);
		
		// 未刷卡
		if (!checkMemberSale() || curCustomer == null) return;

		// 非零售开票
		/*if(!saletype.equals(SellType.JJ_FINAL_SALE ))
		{
			if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
			{
				goodsDef.hyj = 1;
				return;
			}
		}*/

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
		if (((Bcrm_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid,
																	saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
			goodsDef.num4 = popDef.num2;
			goodspopDef.num6 = popDef.num6;
			goodspopDef.num7 = popDef.num7;
			goodspopDef.num8 = popDef.num8;
			goodspopDef.str6 = popDef.str6;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
			goodsDef.num4 = 1;
		}
	}
	
	public void printSaleBill()
	{
		
		/*if(GlobalInfo.sysPara.printhgbill =='Y'&&(saletype.equals(SellType.RETAIL_SALE)||saletype.equals(SellType.JJ_FINAL_SALE)))
		{
			MessageBox codeMsg = new MessageBox("是否打印换购联\n"+"按数字键1 是，按数字键2 否", null, true);
			
			//是否打印换购凭证标识 
			if(codeMsg.verify() == GlobalVar.Key1)
			{
				saleHead.str6 ="1";
			}
			else
			{
				saleHead.str6= "0";
			}
		}*/
		// 打印小票前先查询满赠信息并设置到打印模板供打印
		if (!SellType.ISEXERCISE(saletype))
		{
			DataService dataservice = (DataService) DataService.getDefault();
			Vector gifts = dataservice.getSaleTicketMSInfo(saleHead, saleGoods, salePayment);
			SaleBillMode.getDefault(saleHead.djlb).setSaleTicketMSInfo(saleHead, gifts);
		}
		// 恢复暂停状态的实时打印
		stopRealTimePrint(false);

		// 实时打印只打印剩余部分
		if (isRealTimePrint())
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++)
				realTimePrintGoods(null, i);

			// 打印即扫即打剩余小票部分
			SaleBillMode.getDefault(saleHead.djlb).printRealTimeBottom();

			//
			setHaveRealTimePrint(false);
		}
		else
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);
			// 打印整张小票
			SaleBillMode.getDefault(saleHead.djlb).printBill();
		}

		// 只在交易完成时打印一次移动离线充值券,因此无需放到小票模板中
		if (GlobalInfo.useMobileCharge)
		{
			PaymentBankCMCC pay = CreatePayment.getDefault().getPaymentMobileCharge(this.saleEvent.saleBS);
			if (pay != null)
				pay.printOfflineChargeBill(saleHead.fphm);
		}
	}
}
