package custom.localize.Zspj;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;


public class Zspj_SaleBS extends Zspj_SaleBS2Goods
{
	public boolean isMemberHyjMode()
	{
		// if (checkMemberSale() && (curCustomer != null) && (curCustomer.ishy
		// == 'H')) return true; bak wangyong by 2011.5.10 平价会员价促销
		if (checkMemberSale() && (curCustomer != null))
			return true;
		else
			return false;
	}

	
	public void initOneSale(String type)
	{
		try
		{
			// 显示客显
			DisplayMode.getDefault().lineDisplayWelcome();

			// 双屏显示
			this.sendSecMonitor("welcome");

			// 设置单据类别
			this.saletype = type;
			forwardVip = "";
			checkIndex =-1;
			
			// 收银员只有退货权且收银允许退货,切换到对应的退货单据类型
			if ((GlobalInfo.posLogin.privth == 'T') && SellType.ISSALE(this.saletype) && (GlobalInfo.syjDef.isth == 'Y'))
			{
				djlbSaleToBack();
			}

			// 初始化数据
			initNewSale();

			// 上笔交易如果是超市销售,在百货超市皆可模式下，本笔交易缺省按上笔也采用超市销售模式
			boolean iscsinput = false;
			if (saleEvent.yyyh.getText().trim().equals("超市"))
				iscsinput = true;

			// 清空GUI显示
			saleEvent.initGUI();

			setInfoGUI();

			// 显示自定义TABLE列
			initTable(type);

			// 是否输入营业员,Y-输入营业员/N-超市不输入营业员/B-百货不输入营业员/A-可输可不输,不输入时为超市,输入时为营业员
			initSetYYYGZ(type, iscsinput);

			// 读取断点数据
			boolean brokenflag = false;
			if (checkBrokenData() != null)
			{
				brokenflag = readBrokenData();

				// 没有断点清除数据并刷新界面
				if (!brokenflag)
				{
					initNewSale();
					saleEvent.initGUI();
					initSetYYYGZ(type, iscsinput);
				}
			}
			if (brokenflag)
				return;

			// 检查一次商品列表与界面列表个数是否一致，避免内存对象已清除但界面未刷新的问题
			if (saleGoods.size() != saleEvent.table.getItemCount())
			{
				initNewSale();
				saleEvent.initGUI();
				initSetYYYGZ(type, iscsinput);
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
				RetSYJForm frm = new RetSYJForm();

				int done = frm.open(thSyjh, thFphm);
				if (done == frm.Done)
				{
					thSyjh = RetSYJForm.syj;
					thFphm = Long.parseLong(RetSYJForm.fph);
					if (SellType.PREPARE_BACK.equals(this.saletype))
					{
						isbackticket = findPreSaleInfo();
					}
					else
					{
						isbackticket = findBackTicketInfo();

						if (!isbackticket && (GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C'))
						{
							// 退回对应的销售类型
							djlbBackToSale();

							// 初始化交易
							initOneSale(this.saletype);
						}
					}
				}
				else if ((done == frm.Cancel || done == frm.Clear) && (GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C'))
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

			// 预售提货
			if (SellType.ISPREPARETAKE(this.saletype))
			{
				boolean done = false;
				while (true)
				{
					RetSYJForm frm = new RetSYJForm();

					if (frm.open(thSyjh, thFphm, "请输入预售小票的收银机号和小票号") == frm.Done)
					{
						thSyjh = RetSYJForm.syj;
						thFphm = Long.parseLong(RetSYJForm.fph);

						done = findPreSaleInfo();

						// 如果没有查询到预销售信息，重新输入原收银机号和小票号
						if (!done)
							continue;

						break;
					}
					else
					{
						done = false;
						break;
					}
				}

				// 如果没有输入原收银机号和小票号，返回到零售销售状态
				if (!done)
				{
					initOneSale(SellType.RETAIL_SALE);
					return;
				}
			}

			// 盘点
			if (SellType.ISCHECKINPUT(this.saletype))
			{
				StringBuffer buffer = null;
				boolean newBillFlag = false;
				int billLength = 3; // 平价手工盘点单号为固定长度

				do
				{
					checkdjbh = null;
					checkgz = null;
					checkrq = null;
					buffer = new StringBuffer();

					newBillFlag = false;

					if (new TextBox().open("请输入长度为" + billLength + "位的盘点单号", "单号", "提示:请输入要盘点的空白盘点单号", buffer, 0, 0, false))
					{
						checkdjbh = buffer.toString().trim();
						buffer.delete(0, buffer.length());

						if (checkdjbh == null || checkdjbh.length() <= 0 || checkdjbh.length() != billLength)
						{
							new MessageBox("必须输入" + billLength + "位长度的单号!");
							continue;
						}

						// 输入柜组，日期
						if (GlobalInfo.sysPara.isblankcheckgoods == 'A' || GlobalInfo.sysPara.isblankcheckgoods == 'B')
						{
							if (new TextBox().open("请输入盘点柜组", "柜组", "提示:请输入要盘点的空白盘点柜组", buffer, 0, 0, false))
								checkgz = buffer.toString().trim();
							else
								continue;

							buffer.delete(0, buffer.length());

							if (checkgz == null || checkgz.length() < 1)
								continue;

							buffer.append(ManipulateDateTime.getCurrentDateBySign().replace("-", ""));
							if (new TextBox().open("请输入盘点日期", "日期", "提示:请输入要盘点的空白盘点日期【默认为当天日期】", buffer, 0, 0, false))
								checkrq = buffer.toString().trim();
							else
								continue;

							buffer.delete(0, buffer.length());

							/*
							 * CheckGoodsForm checkGoodsForm = new
							 * CheckGoodsForm(this); checkGoodsForm.open(); if
							 * (checkGoodsForm.isExit == 'Y') continue;
							 */

							if (!((Zspj_NetService) NetService.getDefault()).sendCheckData(this.checkgz, this.checkrq))
								// new MessageBox("当前输入的柜组不在盘点日期范围内");
								continue;

							checkeditflag = "";
							totalcheckrow = -1;
							// 查询是否存在原单
							Vector v = new Vector();
							if (NetService.getDefault().getOldCheckInfo(v, checkdjbh, checkgz, checkrq, checkcw, CmdDef.GETOLDCHECKINFO))
							{
								if (v.size() > 0)
								{
									// 未找到原始单据
									if (v.size() == 1)
									{
										int retcode = Integer.parseInt(((String[]) v.get(0))[14]);

										if (retcode == 1)
											newBillFlag = true;

										if (retcode == 2)
										{
											new MessageBox("另一个柜组已存在编号为【" + checkdjbh + "】的盘点单\n请重新输入盘点单号");
											continue;
										}
									}
								}
								// 不是新单，调出原始单据
								if (!newBillFlag)
								{
									initSellData();
									SaleGoodsDef sgd = null;
									GoodsDef goodsDef = null;

									for (int i = 0; i < v.size(); i++)
									{
										String rowno = ((String[]) v.get(i))[6];
										String gz = ((String[]) v.get(i))[1];
										String handinputcode = ((String[]) v.get(i))[2];
										String code = ((String[]) v.get(i))[7];
										String name = ((String[]) v.get(i))[8];
										String unit = ((String[]) v.get(i))[9];
										String bzhl = ((String[]) v.get(i))[10];
										String uid = ((String[]) v.get(i))[11];
										String pdsl = ((String[]) v.get(i))[12];
										String pdje = ((String[]) v.get(i))[13];

										sgd = new SaleGoodsDef();
										goodsDef = new GoodsDef();
										sgd.barcode = sgd.code = code;
										sgd.sl = Double.parseDouble(pdsl);
										sgd.hjje = Double.parseDouble(pdje);
										sgd.bzhl = Double.parseDouble(bzhl);
										sgd.uid = uid;
										sgd.gz = gz;
										sgd.name = name;
										sgd.unit = unit;
										sgd.rowno = Integer.parseInt(rowno);
										sgd.jg = ManipulatePrecision.doubleConvert((sgd.hjje / sgd.sl), 2, 1);
										sgd.inputbarcode = goodsDef.inputbarcode = handinputcode;
										goodsDef.code = code;
										goodsDef.bzhl = Double.parseDouble(bzhl);
										goodsDef.uid = uid;
										goodsDef.gz = gz;
										goodsDef.name = name;
										goodsDef.unit = unit;
										goodsDef.lsj = sgd.jg;
										// 加入商品列表
										addSaleGoodsObject(sgd, goodsDef, new SpareInfoDef());
									}
									calcHeadYsje();
									saleEvent.setTotalInfo();

									String editflag = ((String[]) v.get(0))[16];
									String totalrow = ((String[]) v.get(0))[18];
									checkeditflag = editflag.trim();
									totalcheckrow = Integer.parseInt(totalrow);
									// 刷新界面显示
									saleEvent.yyyh.setText(checkdjbh);
									saleEvent.gz.setText(sgd.gz);
									saleEvent.clearTableItem();
									saleEvent.updateSaleGUI();

									break;
								}
							}
							else
							{
								new MessageBox("查找盘点单失败\n系统无法确定该单号是否存在\n请重新输入单号");
								continue;
							}
						}

						if (newBillFlag)
						{
							// 可以开始输入盘点商品
							if (this.checkgz.length() > 0)
							{
								saleEvent.yyyh.setText(checkdjbh);
								saleEvent.gz.setText(this.checkgz);
								break;
							}
							else
							{
								new MessageBox("柜组不能为空,请重新输入");
								continue;
							}
						}
					}
					else
					{
						new MessageBox("没有输入盘点单号将不能进行盘点输入");
						saleEvent.gz.setText("无盘点单号");
						break;
					}
				} while (true);

				// 将盘点单号赋给小票号
				saleHead.yfphm = checkdjbh;

			}

			if (SellType.JDXX_FK.equals(saletype))
			{
				if (GlobalInfo.sysPara.jdxxfkflag == 'Y')
				{
					boolean done = false;

					RetSYJForm frm = new RetSYJForm();

					if (frm.open(thSyjh, thFphm, "请输入是家电小票的收银机号和小票号") == frm.Done)
					{
						thSyjh = RetSYJForm.syj;
						thFphm = Long.parseLong(RetSYJForm.fph);
						done = true;
					}
					else
					{
						done = false;
					}

					// 如果没有输入原收银机号和小票号，返回到零售销售状态
					if (!done)
					{
						initOneSale(SellType.RETAIL_SALE);
						return;
					}
				}
			}

			// 退家电下乡返款
			if (SellType.JDXX_BACK.equals(saletype))
			{
				boolean done = false;
				while (true)
				{
					RetSYJForm frm = new RetSYJForm();

					if (frm.open(thSyjh, thFphm, "请输入原家电返款小票的收银机号和小票号") == frm.Done)
					{
						thSyjh = RetSYJForm.syj;
						thFphm = Long.parseLong(RetSYJForm.fph);

						done = findBackTicketInfo();

						// 如果没有查询到预销售信息，重新输入原收银机号和小票号
						if (!done)
							continue;

						break;
					}
					else
					{
						done = false;
						break;
					}
				}

				// 如果没有输入原收银机号和小票号，返回到零售销售状态
				if (!done)
				{
					initOneSale(SellType.RETAIL_SALE);
					return;
				}
			}

			// 销售交易主动提示刷会员卡
			if (GlobalInfo.sysPara.customerbysale == 'Y' && saletype.equals(SellType.RETAIL_SALE))
			{
				NewKeyListener.sendKey(GlobalVar.MemberGrant);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(ex.getMessage());
		}
	}
	
	public boolean payAccount(PayModeDef mode, String money)
	{
			//为防止正常收银出现故障，新增的电子劵控制参数
			if(GlobalInfo.sysPara.isBankDzj == 'N' && mode.code.equals(dzjCode()))
			{
				new MessageBox("该功能已停用！");
				return false;
			}
		return super.payAccount(mode, money);
	}
	
	public String dzjCode()
	{
		String code = null;
		String payitms[] = GlobalInfo.sysPara.custompayobj.split(";");
		for(int i =0;i<payitms.length;i++)
		{
			if(payitms[i].indexOf("Zspj_PaymentDzj") >=0)
				{
				
					code = payitms[i].substring(payitms[i].indexOf(",")+1,payitms[i].length());
				
				}
		}
		return code;
	}
	
	
	public void execCustomKey0(boolean keydownonsale) {
		if (keydownonsale) {
			// 调出原交易的指定小票退货模式允许重新输入手机号改变当前会员卡(原卡可能失效、换卡等情况)
			if (saleGoods.size() > 0 && isNewUseSpecifyTicketBack(false)) {
				return;
			}

			boolean blnRet = false;
			if (isPreTakeStatus()) {
				new MessageBox("预售提货状态下不允许重新输入手机号");
				return;
			}

			// 会员卡必须在商品输入前,则输入了商品以后不能输手机号,指定小票除外
			if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0
					&& !isNewUseSpecifyTicketBack(false)) {
				new MessageBox("必须在输入商品前进行输手机号\n\n请把商品清除后再重输手机号");
				return;
			}
			try {
				// 读取手机号
				HykInfoQueryBS bs = CustomLocalize.getDefault()
						.createHykInfoQueryBS();
				StringBuffer phoneno = new StringBuffer();
				boolean done = new TextBox().open(Language.apply("请输入手机号"),
						Language.apply("手机号码"), Language.apply("请输入会员卡绑定的手机号"),
						phoneno, 0, 0, false, TextBox.IntegerInput);
				if (done) {
					String track2 = "@" + phoneno.toString();// bs.readMemberCard();

					if (track2 == null || track2.equals(""))
						return;
					// 查找会员卡
					CustomerDef cust = bs.findMemberCard(track2);
					if (cust == null)
						return;

					// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
					if (isNewUseSpecifyTicketBack(false)) {
						// 指定小票退仅记录卡号,不执行商品重算等处理
						curCustomer = cust;
						saleHead.hykh = cust.code;
						saleHead.hytype = cust.type;
						saleHead.str4 = cust.valstr2;
						blnRet = true;
					} else {
						// 记录会员卡
						blnRet = memberGrantFinish(cust);
					}
				}
				if (blnRet) {
					saleHead.hysq = curCustomer.code;

					if (curCustomer.iszk == 'Y') {
						saleHead.sqkh = curCustomer.code;
					}
					// 遍历查找已录入商品的CRM促销
					// calcMenberCrmPop();
					// 显示VIP顾客卡信息
					saleEvent.setVIPInfo(getVipInfoLabel());
 
					// 刷新商品列表
					saleEvent.updateTable(getSaleGoodsDisplay());
					saleEvent.table.setSelection(saleEvent.table.getItemCount() - 1);

					// 显示汇总
					saleEvent.setTotalInfo();

					// 显示商品大字信息
					saleEvent.setCurGoodsBigInfo();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public boolean saleCollectAccountPay()
	{
		Payment p = null;
		boolean czsend = true;

		// 付款对象记账
		for (int i = 0; i < payAssistant.size(); i++)
		{
			p = (Payment) payAssistant.elementAt(i);
			//电子红包付款交易这里不需要记账
			if(p != null && p.paymode.code.equals(GlobalInfo.sysPara.hbPaymentCode) && SellType.ISSALE(saleHead.djlb)) continue;
			if (p == null)
				continue;

			// 第一次记账前先检查是否有冲正需要发送
			if (czsend)
			{
				czsend = false;
				if (!p.sendAccountCz())
					return false;
			}

			// 付款记账
			if (!p.collectAccountPay())
				return false;
		}

		// 移动充值对象记账
		if (GlobalInfo.useMobileCharge && !mobileChargeCollectAccount(true))
			return false;

		return true;
	}
	
//	 获取退货小票信息
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

				boolean ishb = false;
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
					//是否存在红包付款
					if(spd1.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
					{
						ishb= true;
					}
				}

				int cho = -1;
				if (EBill.getDefault().isEnable() && EBill.getDefault().isBack())
				{
					cho = EBill.getDefault().getChoice(choice);
				}
				else
				{
					if(!ishb)
					{
						// 选择要退货的商品
						cho = new MutiSelectForm().open(Language.apply("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
					}
					else
					{
//						 选择要退货的商品
						cho = new Zspj_MutiSelectForm_ISHB().open(Language.apply("按确认键选定退货单)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);		
					}
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
				
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					
					//存在红包付款，自动添加到付款方式。
					if(spd1.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
					{
//						salePayment.add(spd1);
						PayModeDef payMode = DataService.getDefault().searchPayMode(GlobalInfo.sysPara.hbPaymentCode);

						// 创建一个付款方式对象
						Payment pay = CreatePayment.getDefault().createPaymentByPayMode(payMode, saleEvent.saleBS);
						
						//非本机退货syjh还是销售时的收银机号，记本地库会报错
						spd1.syjh = saleHead.syjh;
						addSalePayObject(spd1, pay);
					}

				}

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
	
	
	public boolean checkIsSalePay(String code)
	{
		//退货不允许修改货选择红包支付
		if(!SellType.ISSALE(saleHead.djlb) && code.equals(GlobalInfo.sysPara.hbPaymentCode))return true;
		return false;
	}
	
	public boolean checkDeleteSalePay(String string, boolean isDelete)
	{
//		//红包付款方式不允许被删除
//		if(string.indexOf(GlobalInfo.sysPara.hbPaymentCode) != -1) 
//			return true;
//		else
			return false;
	}
	
//	 自动删除不受系统参数控制
	public boolean deleteSalePay(int index, boolean isautodel)
	{
		// 是否允许删除当前付款方式
		if (!isautodel && !isDeletePay(index))
			return false;

		// 扣回处理
		if (isRefundStatus())
			return deleteRefundPay(index);

		try
		{
			if (index >= 0)
			{
				boolean flag = false;
				// 付款取消交易才能删除已付款
				Payment p = (Payment) payAssistant.elementAt(index);
				if(p.paymode.code.equals(GlobalInfo.sysPara.hbPaymentCode))
				{
					flag = ((Zspj_PaymentHb)p).sendhbcancel(salePayment);

					if(flag)
					{
					  for(int i = 0;i<salePayment.size();i++)
					  {
						  SalePayDef spd = (SalePayDef)salePayment.elementAt(i);
							if(spd.paycode.equals(GlobalInfo.sysPara.hbPaymentCode))
							{
								delSalePayObject(i);
								//刷新已付款列表
								salePayEvent.showSalePaymentDisplay();
								i =i-1;
							}
					  }
					  
					  	//重算剩余付款
						calcPayBalance();

						// 刷新已付款，更新断点文件
						getSalePaymentDisplay();

						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					flag = p.cancelPay();
					if(!flag) return false;
					// 删除已付款
					delSalePayObject(index);
					
					// 重算剩余付款
					calcPayBalance();

					// 刷新已付款，更新断点文件
					getSalePaymentDisplay();

					return true;
				}	
					
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return false;
	}
	
	public boolean deleteAllSalePay()
	{
//		 删除所有付款方式
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
			if(sp.paycode.equals(GlobalInfo.sysPara.hbPaymentCode) && !SellType.ISSALE(saleHead.djlb))continue;
			if (!deleteSalePay(i))
			{
				return false;
			}
			else
			{
				i--;
			}
		}

		// 删除所有扣回的付款,用信用卡支付扣回时,取消所有付款也得取消扣回
		if (!deleteAllSaleRefund())
			return false;

		return true;
	}

}
