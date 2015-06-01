package custom.localize.Cqdr;

import java.util.Vector;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.PrintTemplate.DisplayMode;
import com.efuture.javaPos.Struct.CheckGoodsDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.CheckGoodsForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;

public class Cqdr_SaleBS extends SaleBS {
	
	public void backSell()
	{
		if (GlobalInfo.syjDef.isth != 'Y')
		{
			new MessageBox("该收银机不允许退货!");

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		// 已经是指定小票退货状态,再次按退货键则重新输入原小票信息
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
		}

		// 从销售状态切换到相应的退货状态
		if ((saleGoods.size() <= 0) && SellType.ISSALE(saletype))
		{
			curGrant.privth = 'S';
			// 检查权限
			thgrantuser = null;
			if (((curGrant.privth != 'Y') && (curGrant.privth != 'T')&& (curGrant.privth != 'S')) || (curGrant.thxe <= 0))
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
				new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(thgrantuser.thxe) + " 元");
			}

			// 切换到退货交易类型
			djlbSaleToBack();

			// 初始化交易
			initOneSale(this.saletype,thgrantuser);
		}
		else
		{
			new MessageBox("请先完成当前交易!", null, false);
		}
	}
	
//	 初始化一笔新交易
	public void initOneSale(String type , OperUserDef thgrantuser1)
	{
		// 显示客显
		DisplayMode.getDefault().lineDisplayWelcome();

		// 双屏显示
		this.sendSecMonitor("welcome");

		// 设置单据类别
		this.saletype = type;

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

						saleEvent.yyyh.setText("家电");
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
			else if (!(thgrantuser1.privth == 'S')&&(done == frm.Cancel || done == frm.Clear) && (GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C'))
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
			StringBuffer buffer = new StringBuffer();
			do
			{
				checkdjbh = null;
				String lentext = "任意长度的";
				String grouplentext = "";

				if (GlobalInfo.sysPara.checklength > 0)
					lentext = "长度" + GlobalInfo.sysPara.checklength + "位的";

				if (GlobalInfo.sysPara.checkgrouplen > 0)
					grouplentext = "与长度" + GlobalInfo.sysPara.checkgrouplen + "位的盘点组号";

				if (new TextBox().open("请输入" + lentext + "盘点单号" + grouplentext, "单号", "请输入要盘点的空白盘点单号,格式为\n\n盘点单号+盘点组号", buffer, 0, 0, false))
				{
					checkdjbh = buffer.toString().trim();

					if (checkdjbh.length() <= 0)
						continue;

					if (GlobalInfo.sysPara.checklength > 0 && checkdjbh.length() < GlobalInfo.sysPara.checklength)
					{
						// new MessageBox("必须输入" +
						// GlobalInfo.sysPara.checklength + "位长度的单号!");
						continue;
					}

					if (GlobalInfo.sysPara.checkgrouplen > 0 && checkdjbh.length() != (GlobalInfo.sysPara.checklength + GlobalInfo.sysPara.checkgrouplen))
					{
						continue;
					}

					if (GlobalInfo.sysPara.checkgrouplen < 0 && GlobalInfo.sysPara.checklength != checkdjbh.length())
					{
						continue;
					}

					// 联网查询盘点单
					if (checkgoods == null)
						checkgoods = new Vector();
					else
						checkgoods.removeAllElements();

					// 联网盘点
					if (GlobalInfo.sysPara.isblankcheckgoods != 'A' && GlobalInfo.sysPara.isblankcheckgoods != 'B')
					{
						if (!NetService.getDefault().getCheckGoodsList(checkgoods, checkdjbh, "", ""))
							continue;

						// 不允许使用空白盘点单时
						if (checkgoods.size() <= 0 && GlobalInfo.sysPara.isblankcheckgoods == 'N')
						{
							new MessageBox("没有当前盘点单,不允许进行盘点!");
							continue;
						}

						// 盘点单不存在提示是否生成新的盘点单
						if (checkgoods.size() <= 0 && new MessageBox("[" + checkdjbh + "]盘点单不存在,你要生成新的盘点单吗？", null, true).verify() != GlobalVar.Key1)
						{
							continue;
						}
					}

					// 输入柜组，日期，仓位
					if (GlobalInfo.sysPara.isblankcheckgoods == 'A' || GlobalInfo.sysPara.isblankcheckgoods == 'B')
					{
						CheckGoodsForm checkGoodsForm = new CheckGoodsForm(this);
						checkGoodsForm.open();
						if (checkGoodsForm.isExit == 'Y')
						{
							continue;
						}

						if (GlobalInfo.sysPara.isblankcheckgoods == 'B')
						{
							checkeditflag = "";
							totalcheckrow = -1;
							// 查询是否存在原单
							Vector v = new Vector();
							if (NetService.getDefault().getOldCheckInfo(v, checkdjbh, checkgz, checkrq, checkcw, CmdDef.GETOLDCHECKINFO))
							{
								if (v.size() > 0)
								{
									if (v.size() == 1)
									{
										int retcode = Integer.parseInt(((String[]) v.get(0))[14]);
										String retmsg = ((String[]) v.get(0))[15];
										if (retcode != 0)
										{
											new MessageBox(retmsg);
											continue;
										}
									}
									initSellData();
									SaleGoodsDef sgd = null;
									GoodsDef goodsDef = null;
									for (int i = 0; i < v.size(); i++)
									{
										String rowno = ((String[]) v.get(i))[6];
										String gz = ((String[]) v.get(i))[1];
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
									String editflag = ((String[]) v.get(0))[16];
									String totalrow = ((String[]) v.get(0))[18];
									checkeditflag = editflag.trim();
									totalcheckrow = Integer.parseInt(totalrow);
									curcheckrow = totalcheckrow;
									// 刷新界面显示
									saleEvent.clearTableItem();
									saleEvent.updateSaleGUI();
								}
							}
							else
							{
								continue;
							}
						}
					}

					CheckGoodsDef chkgd = null;

					for (int i = 0; i < checkgoods.size(); i++)
					{
						chkgd = (CheckGoodsDef) checkgoods.elementAt(i);

						if (chkgd.gz != null && chkgd.gz.trim().length() > 0 && chkgd.gz.charAt(0) == 'Y')
						{
							break;
						}
					}

					if (chkgd != null && chkgd.gz != null && chkgd.gz.trim().length() > 0 && chkgd.gz.charAt(0) == 'Y')
					{
						String checkgroup = null;

						if (GlobalInfo.sysPara.checkgrouplen > 0)
						{
							checkgroup = checkdjbh.substring(0, GlobalInfo.sysPara.checklength) + "-" + checkdjbh.substring(GlobalInfo.sysPara.checklength, (GlobalInfo.sysPara.checklength + GlobalInfo.sysPara.checkgrouplen));
						}
						else
						{
							checkgroup = checkdjbh.substring(0, GlobalInfo.sysPara.checklength);
						}

						if (new MessageBox("[" + checkgroup + "]\n盘点单组已盘点,是否重盘该盘点组号?", null, true).verify() != GlobalVar.Key1)
						{
							continue;
						}
					}

					if (setCheckGUI())
						break;
					
					// 可以开始输入盘点商品
					if (this.checkgz.length() > 0)
					{
						saleEvent.gz.setText(this.checkgz);
					}
					else if (GlobalInfo.sysPara.checklength > 0)
					{
						saleEvent.gz.setText(checkdjbh.substring(0, GlobalInfo.sysPara.checklength) + (checkdjbh.length() > GlobalInfo.sysPara.checklength ? "-" + checkdjbh.substring(GlobalInfo.sysPara.checklength) : ""));
					}
					else
					{
						saleEvent.gz.setText(checkdjbh);
					}
					break;
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

		// 家电下乡返款
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
	public boolean isSpecifyTicketBack()
	{
		if (SellType.ISBACK(saletype) && ( GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C'))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
