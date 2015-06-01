package custom.localize.Bxmx;

import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;

public class Bxmx_SaleBS extends Bxmx_SaleBS2Pay
{
	public boolean doCmPop(int sgindex)
	{
		// 先总是无满减规则方式的付款
		isPreparePay = payNormal;

		// 不参与促销计算的交易类型
		if (SellType.NOPOP(saletype) || !SellType.ISSALE(saletype) ) { return false; }

		// 先备份当前商品信息,以便放弃时付款时恢复
		doCmPopWriteData();

		// 对goodsCmPop所有的促销按优先级排序,优先级大的先执行
		// 先把商品同档期的所有促销执行完再执行下一个档期各商品的所有促销
		Vector dqvec = new Vector();
		for (int i = 0; i < goodsCmPop.size(); i++)
		{
			Vector popvec = (Vector) goodsCmPop.elementAt(i);
			for (int j = 0; popvec != null && j < popvec.size(); j++)
			{
				CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(j);

				// 是否已加入到档期列表
				int n = 0;
				for (n = 0; n < dqvec.size(); n++)
				{
					String dq = (String) dqvec.elementAt(n);
					String[] s = dq.split(",");
					if (s[0].equals(cmp.dqid)) // 先比较档期ID，若档期ID相同再跳出来比优先级
						break;
				}
				if (n >= dqvec.size())
				{
					for (n = 0; n < dqvec.size(); n++)
					{
						String dq = (String) dqvec.elementAt(n);
						String[] s = dq.split(",");
						int pri = Convert.toInt(s[1]);
						// 当前cmp优先级若大于dqvec中的或优先级相等或当当前cmp档期id大于dqvec中优先级则跳出加入到当前n处
						if (cmp.dqinfo.pri > pri || (cmp.dqinfo.pri == pri && cmp.dqinfo.dqid.compareTo(s[0]) > 0))
						{
							break;
						}
					}
					if (n >= dqvec.size()) // 当前cmp若比dqvec中的优先级低，则加到dqvec尾
						dqvec.add(cmp.dqinfo.dqid + "," + cmp.dqinfo.pri);
					else
						// 当前cmp的优先级若比dqvec中某个大，则加入到当前n处
						dqvec.insertElementAt(cmp.dqinfo.dqid + "," + cmp.dqinfo.pri, n);
				}
			}
		}

		// 计算需要进行累计的促销所产生的折扣
		boolean havepop = false;
		for (int n = 0; n < dqvec.size(); n++)
		{
			// 此时的dqvec已经按优先级高低排序了
			String dqid = ((String) dqvec.elementAt(n)).split(",")[0];
			// 在goodsCmPop中查找与dqid相同的促销进行计算
			for (int i = 0; i < goodsCmPop.size(); i++)
			{
				// 指定执行某行商品的累计促销
				if (sgindex >= 0 && i != sgindex) // sgindex 传入的是-1
					continue;

				Vector popvec = (Vector) goodsCmPop.elementAt(i);
				for (int j = 0; popvec != null && j < popvec.size(); j++)
				{
					CmPopGoodsDef cmp = (CmPopGoodsDef) popvec.elementAt(j);

					// 不累计的促销允许立即计算折扣的促销,则找到商品后立即计算促销折扣,付款时不处理
					if (cmp.ruleinfo.summode == '0')
						continue;

					// 按档期顺序执行
					if (!cmp.dqid.equals(dqid))
						continue;

					// 计算促销折扣
					// 将goodsCmpop,popvec，cmp当前位置传入
					// i,j可定位当前计算的促销在goodsCmpop中的位置

					//System.out.println("Start doCmPop-> SaleGoodsIndex:" + i + " CmpGoodsIndex:" + j);
					//zkLogger.log("CmpInfo-> Dqinfo:" + cmp.strdqinfo + " RuleInfo:" + cmp.strruleinfo + " LadderInfo:" + cmp.strruleladder + " Seqno:" + cmp.cmpopseqno + " Codeid:" + cmp.codeid);

					if (calcGoodsCMPOPRebate(i, cmp, j))
					{
						// 调试模式提示促销计算结果，便于了解促销计算规则
						if (ConfigClass.DebugMode)
						{
							refreshCmPopUI();

							new MessageBox("刚计算完的促销是第 " + (i + 1) + " 行商品在\n\n[" + dqid + " - " + cmp.dqinfo.name + "]促销档期内的\n\n[" + cmp.ruleinfo.ruleid + " - " + cmp.ruleinfo.rulename + "]促销规则\n\n请核对促销活动的结果");
						}

						// 设置存在CM促销标记
						havepop = true;

						// 如果计算出有促销,但又未标记促销,则说明本次自身被除外,需要再计算一次
						if (!cmp.used)
							j--;
					}
					//zkLogger.log("Stop doCmPop-> SaleGoodsIndex:" + i + " CmpGoodsIndex:" + j, "\r\n");

					// 计算促销时进了预付款,预付款已足够,交易已完成
					if (doCmPopExit)
						return havepop;
				}
			}
		}

		// 计算出CM促销折扣,重算小票
		if (havepop)
			refreshCmPopUI();

		return havepop;
	}
	
	protected void sellSingleCard()
	{
		StringBuffer cardno = new StringBuffer();
		TextBox cardTracker = new TextBox();

		try
		{
			do
			{
				
				// 异步执行线程
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(50);
							NewKeyListener.sendKey(GlobalVar.Enter);
						}
						catch (Exception ex)
						{

						}
					}
				});
				
				if (!cardTracker.open("请刷卡", "买卡交易", "按【退出键】取消/结束刷卡", cardno, 0, 0, false, TextBox.MsrKeyInput, -1, null, GlobalVar.Enter))
					break;

				if (saleGoods.size() >= 60)
				{
					new MessageBox("一次性充值发售只能为60张,请重新操作");
					return;
				}
				
				String track = cardTracker.Track2;
				if (track == null || track.equals(""))
					continue;

				Vector retVec = new Vector();
				if (((Bxmx_NetService) NetService.getDefault()).sellCardOrCoupon(SellType.ISBACK(saletype) ? "2" : "0", SellType.ISCARD(saletype) ? "0" : "1", track, "", "1", "", retVec))
				{
					String[] info = (String[]) retVec.get(0);
					if (info == null || info.length < 1 || info[0] == null)
						continue;

					String[] cardinfo = info[0].split("#");
					if (cardinfo == null || cardinfo.length < 1)
						continue;

					GoodsDef goodsDef = new GoodsDef();

					if (cardinfo.length < 1 || cardinfo[0] == null)
						continue;

					boolean issamegoods = false;
					for (int i = 0; i < saleGoods.size(); i++)
					{
						SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
						if (sgd.code.equals(cardinfo[0]))
						{
							new MessageBox("卡/券号:" + cardinfo[0] + "已在列表中存在\n相同卡号不允许重复售卖");
							issamegoods = true;
							break;
						}
					}

					if (issamegoods)
						continue;

					goodsDef.barcode = cardinfo[0];
					goodsDef.code = cardinfo[0];

					if (cardinfo.length < 2 && cardinfo[1] == null)
						continue;

					goodsDef.lsj = Convert.toDouble(cardinfo[1]);

					goodsDef.gz = "00";
					goodsDef.uid = "00";
					goodsDef.name = "卡券商品";
					goodsDef.unit = "张";
					goodsDef.issqkzk = 'Y';
					goodsDef.isvipzk = 'N';

					SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, saleEvent.yyyh.getText(), 1, goodsDef.lsj, 0, false);

					// 记录退货折扣
					if (cardinfo.length > 2 && cardinfo[2] != null)
						saleGoodsDef.lszzk = Convert.toDouble(cardinfo[2]);

					addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));
					getZZK(saleGoodsDef);
				}

				calcHeadYsje();

				refreshSaleForm();
				saleEvent.updateSaleGUI();

			} while (true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected void sellBatchCardAndCoupon()
	{
		String startcardno = null, endcardno = null;
		StringBuffer start = new StringBuffer();
		StringBuffer end = new StringBuffer();
		String precardno = null, varcardno = null;
		//new MessageBox("测试批量发卡");
		try
		{
			do
			{
				if (new TextBox().open("【起始】请输入起始号", "券/卡发售", SellType.ISBACK(saletype) ? "【退卡】" : "" + "起始卡号", start, 0, 0, false, TextBox.IntegerInput))
				{
					if (SellType.ISCOUPON(saletype))
						startcardno = start.toString().substring(0, start.toString().length() - 5);
					else
						startcardno = start.toString();
					break;
				}

				return;
			} while (true);

			do
			{
				if (new TextBox().open("请输入终止号【终止】", "券/卡发售", SellType.ISBACK(saletype) ? "【退卡】" : "" + "结束卡号", end, 0, 0, false, TextBox.IntegerInput))
				{
					if (SellType.ISCOUPON(saletype))
						endcardno = end.toString().substring(0, end.toString().length() - 5);
					else
						endcardno = end.toString();
					break;
				}
				return;
			} while (true);

			long count = Convert.toLong(endcardno) - Convert.toLong(startcardno);
			if (count + 1 > 60)
			{
				new MessageBox("一次性充值发售只能为60张,请重新操作");
				return;
			}

			Vector retVec = new Vector();
			// 校验卡号
			if (((Bxmx_NetService) NetService.getDefault()).sellCardOrCoupon(SellType.ISBACK(saletype) ? "2" : "0", SellType.ISCARD(saletype) ? "0" : "1", startcardno, endcardno, String.valueOf(count + 1), "", retVec))
			{
				String[] info = (String[]) retVec.get(0);
				if (info == null || info.length < 1 || info[0] == null)
					return;

				String[] cardinfo = info[0].split("#");
				if (cardinfo == null || cardinfo.length < 1)
					return;

				if (cardinfo.length < 2 || cardinfo[1] == null)
					return;

				double[] zkinfo = null;
				if (cardinfo.length > 2 && cardinfo[2] != null && cardinfo[2].indexOf(",") > -1)
				{
					String[] tmpzk = cardinfo[2].split(",");
					zkinfo = new double[tmpzk.length];

					for (int i = 0; i < tmpzk.length; i++)
						zkinfo[i] = Convert.toDouble(tmpzk[i]);
				}

				if ((Convert.toLong(endcardno) - Convert.toLong(startcardno) + 1) != Convert.toInt(cardinfo[0]))
				{
					new MessageBox("前后台售卡张数不匹配");
					return;
				}

/*				if (SellType.ISCOUPON(saletype))
				{
					precardno = startcardno.substring(0, startcardno.length() - 3);
					varcardno = startcardno.substring(startcardno.length() - 3, startcardno.length());
				}*/
				//new MessageBox(startcardno+"  "+SellType.ISCOUPON(saletype)+"  "+cardinfo[0]);
				for (int i = 0; i < Convert.toInt(cardinfo[0]); i++)
				{
					GoodsDef goodsDef = new GoodsDef();

					if (SellType.ISCOUPON(saletype))
					{
/*						String tmpcardno = Convert.increaseCharForward(String.valueOf(Convert.toInt(varcardno) + i), '0', 3);
						goodsDef.barcode = precardno + tmpcardno;
						goodsDef.code = goodsDef.barcode;*/
						goodsDef.barcode = String.valueOf(Convert.toLong(startcardno) + i);
						goodsDef.code = goodsDef.barcode;
					}
					else
					{
						goodsDef.barcode = String.valueOf(Convert.toLong(startcardno) + i);
						goodsDef.code = goodsDef.barcode;
					}

					goodsDef.gz = "00";
					goodsDef.uid = "00";
					goodsDef.name = "卡券商品";
					goodsDef.unit = "张";
					goodsDef.lsj = Convert.toDouble(cardinfo[1]);
					goodsDef.issqkzk = 'Y';
					goodsDef.isvipzk = 'N';

					// 生成商品明细
					SaleGoodsDef saleGoodsDef = goodsDef2SaleGoods(goodsDef, saleEvent.yyyh.getText(), 1, goodsDef.lsj, 0, false);

					if (zkinfo != null)
						saleGoodsDef.lszzk = zkinfo[i];

					addSaleGoodsObject(saleGoodsDef, goodsDef, getGoodsSpareInfo(goodsDef, saleGoodsDef));
					getZZK(saleGoodsDef);
				}

				calcHeadYsje();

				refreshSaleForm();
				saleEvent.updateSaleGUI();
			}

			return;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return;
		}
	}
	
	public boolean deleteSalePay(int index)
	{
		SalePayDef spdef = (SalePayDef) salePayment.elementAt(index);
		if (spdef.num2 == 1)
		{
			new MessageBox("");
		}
		return super.deleteSalePay(index);
	}

	protected void preSale()
	{
		fetchinfo = new Bxmx_FetchInfoDef();

		if (!new Bxmx_PreSaleForm().open(fetchinfo))
			fetchinfo = null;
		else
		{
			saleHead.str1 = fetchinfo.fetchmkt;
		}
	}

	protected void preSaleTake()
	{
		ProgressBox prg = new ProgressBox();
		prg.setText("正在查找本门店提货单,请稍等...");

		try
		{
			Vector saleHeadList = new Vector();
			if (!((Bxmx_NetService) NetService.getDefault()).getPreSaleHead(saleHeadList) || saleHeadList == null)
			{
				prg.close();
				prg = null;
				return;
			}

			prg.close();
			prg = null;

			Vector tabContent = new Vector();

			for (int i = 0; i < saleHeadList.size(); i++)
			{
				String mktname = "";
				SaleHeadDef head = (SaleHeadDef) saleHeadList.get(i);
				Bxmx_FetchInfoDef info = new Bxmx_FetchInfoDef();
				if (head != null && head.str2 != null && head.str2.length() > 5)
				{
					String[] fetchary = head.str2.split("#");
					if (fetchary == null)
						continue;

					if (fetchary.length > 0 && fetchary[0] != null)
						info.fetchmkt = fetchary[0];

					if (fetchary.length > 1 && fetchary[1] != null)
						info.fetchdate = fetchary[1];

					if (fetchary.length > 2 && fetchary[2] != null)
						info.fetcher = fetchary[2];

					if (fetchary.length > 3 && fetchary[4] != null)
						info.fetchtel = fetchary[3];

					if (fetchary.length > 4 && fetchary[4] != null)
						info.fetchmemo = fetchary[4];

					if (fetchary.length > 5 && fetchary[5] != null)
						info.srcmkt = fetchary[5];

					Vector retinfo = new Vector();
					if (((Bxmx_NetService) NetService.getDefault()).checkMktcode(info.srcmkt, retinfo) && retinfo.size() != 0)
					{
						String[] tmpmkt = (String[]) retinfo.get(0);
						if (tmpmkt != null && tmpmkt.length > 0)
							mktname = tmpmkt[0];
					}
				}

				tabContent.add(new String[] { String.valueOf(i + 1), info.srcmkt, mktname, String.valueOf(head.fphm), String.valueOf(head.ysje), String.valueOf(ManipulatePrecision.doubleConvert(head.ysje - head.num1, 2, 1)), String.valueOf(head.num1), info.fetchdate, info.fetcher, info.fetchtel, info.fetchmemo });
			}

			String[] title = { "序", "订货门店", "门店名称", "发票号", "总金额", "已付金额", "未付金额", "日期", "取货人", "联系方式", "备注" };
			int[] width = { 30, 100, 100, 80, 80, 95, 95, 110, 90, 130, 200 };

			int choice = new MutiSelectForm().open("请从列表中选择一单进行交易", title, width, tabContent, false, 800, 600, 775, 480, false, false, -1, false);

			if (choice == -1)
				return;

			SaleHeadDef shd = (SaleHeadDef) saleHeadList.get(choice);

			Vector saleGoodsList = new Vector();

			if (!((Bxmx_NetService) NetService.getDefault()).getPreSaleGoods(shd.mkt, shd.syjh, String.valueOf(shd.fphm), saleGoodsList) || saleGoodsList == null)
			{
				new MessageBox("查找单据商品明细失败");
				return;
			}

			for (int i = 0; i < saleGoodsList.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoodsList.get(i);
				saleGoods.add(sgd);
			}

			saleHead.str2 = shd.str2;
			saleHead.str3 = shd.str3;
			saleHead.num1 = shd.num1;
			saleHead.billno = shd.memo;
			saleHead.yfphm = shd.mkt + "#" + shd.syjh + "#" + String.valueOf(shd.fphm);

			// 刷新数据
			refreshSaleData();

			// 计算应付金额
			calcHeadYsje();

			// 刷新界面显示
			saleEvent.updateSaleGUI();

			// 非同门店取货代表已付清订金，不用再向下查找付款明细
			if (!shd.mkt.equals(GlobalInfo.sysPara.mktcode) || shd.str3.equals("A"))
				return;

			// 同门店str3=N时代表有未付清余款，记下原小票关键信息
			if (shd.str3.equals("N"))
			{
				this.srcmkt = shd.mkt;
				this.srcsyjh = shd.syjh;
				this.srcfphm = shd.fphm;
			}

			Vector salePayList = new Vector();

			if (!((Bxmx_NetService) NetService.getDefault()).getPreSalePay(shd.mkt, shd.syjh, String.valueOf(shd.fphm), salePayList) || salePayList == null)
			{
				new MessageBox("查找单据付款明细失败");
				return;
			}

			double wfje = 0;
			for (int i = 0; i < salePayList.size(); i++)
			{
				SalePayDef sp = (SalePayDef) salePayList.get(i);
				if (sp.flag == '4')
				{
					wfje = ManipulatePrecision.doubleConvert(wfje + sp.ybje);
					salePayList.removeElement(sp);
				}
				else
					sp.payname +="(预付款)";
			}
			
			if (wfje == 0)
			{
				saleHead.num1 = 0;
			}

			salePayment = salePayList;

			// 计算应付金额
			calcHeadYsje();

			// 刷新界面显示
			saleEvent.updateSaleGUI();

			// 焦点到编码输入框
			if (saleGoods.size() > 0)
			{
				// 检查是否存在付款断点
				if (salePayment.size() > 0)
				{
					// 根据付款信息创建付款对象
					SalePayDef sp = null;
					for (int i = 0; i < salePayment.size(); i++)
					{
						sp = (SalePayDef) salePayment.elementAt(i);

						// 创建付款对象
						Payment pay = CreatePayment.getDefault().createPaymentBySalePay(sp, saleHead);
						if (pay == null)
						{
							new MessageBox("处理付款失败");
							// 放弃所有已付款
							salePayment.removeAllElements();
							payAssistant.removeAllElements();
							return;
						}
						// 增加已付款
						payAssistant.add(pay);
					}
				}
			}
			return;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected void initBusiness()
	{
		if (SellType.ISCARD(saletype))
		{
			if (new MessageBox(SellType.ISBACK(saletype) ? "是否进行批量退卡" : "是否进行批量售卡", null, true).verify() == GlobalVar.Key1)
			{
				this.isBatchSellCardOrCoupon = true;
				sellBatchCardAndCoupon();
			}
			else
			{
				sellSingleCard();
			}

			return;
		}
		// 券，储值发售
		if (SellType.ISCOUPON(saletype))
		{
			if (new MessageBox(SellType.ISBACK(saletype) ? "是否进行批量退卡" : "是否进行批量售卡", null, true).verify() == GlobalVar.Key1)
			{
				this.isBatchSellCardOrCoupon = true;
				sellBatchCardAndCoupon();
			}
			return;
		}

		// //预收定金
		if (SellType.ISEARNEST(saletype))
		{
			if (!SellType.ISBACK(saletype))
				preSale();
			return;
		}

		// //预收提货
		if (SellType.ISPREPARETAKE(saletype))
		{
			preSaleTake();
			return;
		}
	}

	public boolean memberGrant()
	{

		if (SellType.ISCOUPON(saletype))
		{
			new MessageBox("卡券售卖业务不能使用会员卡");
			return false;
		}

		return super.memberGrant();
	}

	public boolean findBackTicketInfo()
	{
		if (SellType.ISEARNEST(saletype))
		{
			if (SellType.ISBACK(saletype))
				return findBackEarnestTicket();
		}

		return findBackTicket();
	}

	public boolean findBackEarnestTicket()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			thsaleHead = new SaleHeadDef();
			thsaleGoods = new Vector();
			thsalePayment = new Vector();

			// 联网查询原小票信息
			ProgressBox pb = new ProgressBox();
			pb.setText("开始查找定金退货小票.....");
			if (!((Bxmx_NetService) NetService.getDefault()).getPreBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
			{
				pb.close();
				pb = null;

				thSyjh = null;
				thFphm = 0;

				return false;
			}

			pb.close();
			pb = null;

			if (!thsaleHead.djlb.equals(SellType.EARNEST_SALE))
			{
				if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
				{
					new MessageBox("原小票是[" + SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) + "]交易\n\n与当前退货交易类型不匹配");

					// 清空原收银机号和原小票号
					thSyjh = null;
					thFphm = 0;
					return false;
				}
			}

			// 显示原小票商品明细
			Vector choice = new Vector();
			String[] title = { "序", "商品编码", "商品名称", "原数量", "原折扣", "原成交价", "退货", "退货数量" };
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
				row[6] = "Y";
				//row[7] = ManipulatePrecision.doubleToString(sgd.sl, 4, 1, true);
				choice.add(row);
			}

			String[] title1 = { "序", "付款名称", "账号", "付款金额" };
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
			cho = new MutiSelectForm().open("请仔细核对原定单信息，按【确认键】保存提交", title, width, choice, false, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);

			// 如果cho小于0且已经选择过退货小票
			if (cho < 0 && isbackticket)
				return true;

			if (cho < 0)
			{
				thSyjh = null;
				thFphm = 0;
				return false;
			}


			// 将退货授权保存下来
			String thsq = saleHead.thsq;
			initSellData();

			// 生成退货商品明细
			for (int i = 0; i < choice.size(); i++)
			{
				row = (String[]) choice.get(i);
				/*if (!row[6].trim().equals("Y"))
					continue;*/

				SaleGoodsDef sgd = (SaleGoodsDef) thsaleGoods.get(i);
				double thsl = sgd.sl;//ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);

				sgd.yfphm = sgd.fphm;
				sgd.ysyjh = sgd.syjh;
				sgd.yrowno = sgd.rowno;
				sgd.memonum1 = sgd.sl;
				sgd.syjh = ConfigClass.CashRegisterCode;
				sgd.fphm = GlobalInfo.syjStatus.fphm;
				sgd.rowno = saleGoods.size() + 1;

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
			}

			// 设置原小票头信息
			saleHead.hykh = thsaleHead.hykh;
			saleHead.hytype = thsaleHead.hytype;
			saleHead.jfkh = thsaleHead.jfkh;

			saleHead.num1 = thsaleHead.num1;
			saleHead.thsq = thsq;
			saleHead.ghsq = thsaleHead.ghsq;
			saleHead.hysq = thsaleHead.hysq;
			saleHead.sqkh = thsaleHead.sqkh;
			saleHead.sqktype = thsaleHead.sqktype;
			saleHead.sqkzkfd = thsaleHead.sqkzkfd;
			saleHead.hhflag = hhflag;
			saleHead.jdfhdd = thsaleHead.jdfhdd;
			saleHead.salefphm = String.valueOf(thsaleHead.fphm);

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
						new MessageBox("超出退货的最大限额，不能退货");

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
						new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
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

	public boolean findBackTicket()
	{
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			// 如果是新指定小票进入
			if (saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText("开始查找退货小票操作.....");
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

				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox("原小票是[" + SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) + "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { "序", "商品编码", "商品名称", "原数量", "原折扣", "原成交价", "退货", "退货数量" };
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

				String[] title1 = { "序", "付款名称", "账号", "付款金额" };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);

					// 若存在提货券付款的不让交易
					if (spd1.paycode.equals("0402"))
					{
						new MessageBox("所退小票存在提货券付款，不允许退货");
						return false;
					}

					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				int cho = -1;
				// 选择要退货的商品
				cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open("开单营业员号：", "", "请输入有效开单营业员号", backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox("该工号不是营业员!", null, false);
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
				saleHead.salefphm = String.valueOf(this.thFphm);

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
							new MessageBox("超出退货的最大限额，不能退货");

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
							new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(curGrant.thxe) + " 元");
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

	public void backSell()
	{
		if (SellType.RETAIL_SALE.equals(saletype))
		{
			new MessageBox("当前为零售销售，如需退货请使用红冲");
			return ;
		}
		
		if (SellType.ISPREPARETAKE(saletype))
		{
			new MessageBox("请在订货门店选择定金退货");
			return;
		}

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
		else if (EBill.getDefault().getBackSaleBill(this)) // android 指定小票退货
		{

		}
		else if (isSpecifyTicketBack())
		{
			if (SellType.ISCOUPON(saletype) && SellType.ISBACK(saletype))
			{
				initOneSale(this.saletype);
				return;
			}
			if (SellType.ISCARD(saletype) && SellType.ISBACK(saletype))
			{
				initOneSale(this.saletype);
				return;
			}
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
				new MessageBox("授权退货,限额为 " + ManipulatePrecision.doubleToString(thgrantuser.thxe) + " 元");
			}

			// 切换到退货交易类型
			djlbSaleToBack();

			// 初始化交易
			initOneSale(this.saletype);
		}
		else
		{
			new MessageBox("请先完成当前交易!", null, false);
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
			if (SellType.ISCOUPON(saletype) && SellType.ISBACK(saletype))
				return;
			if (SellType.ISCARD(saletype) && SellType.ISBACK(saletype))
				return;

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
			}
			else if ((done == frm.Cancel || done == frm.Clear) && (GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'Y'))
			{
				// 二次授权
				twoBackPersonGrant();

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

	public void afterInitPay()
	{
		if (isgoodscoupon)
			salePayEvent.paySelect("0402");
	}

	public void execCustomKey0(boolean flag)
	{
		if (checkAllowInit())
			initOneSale(SellType.EARNEST_SALE);
	}

	public void execCustomKey1(boolean flag)
	{
		if (checkAllowInit())
			initOneSale(SellType.PREPARE_TAKE);
	}

	public void execCustomKey2(boolean flag)
	{
		if (checkAllowInit())
			initOneSale(SellType.PURCHANSE_COUPON);
	}

	public void execCustomKey3(boolean flag)
	{
		if (SellType.ISCARD(saletype))
		{
			if (!this.isBatchSellCardOrCoupon)
				sellSingleCard();
			else
				sellBatchCardAndCoupon();
		}
		else if (SellType.ISCOUPON(saletype))
		{
			if (this.isBatchSellCardOrCoupon)
				sellBatchCardAndCoupon();
		}
		else if (SellType.ISEARNEST(saletype))
		{
			if (this.fetchinfo == null)
				return;

			StringBuffer info = new StringBuffer();

			info.append("门 店 号:" + Convert.appendStringSize("", fetchinfo.fetchmkt, 1, 12, 16, 0) + "\n");
			info.append("门店名称:" + Convert.appendStringSize("", fetchinfo.fetchmktname, 1, 12, 16, 0) + "\n");
			info.append("取货日期:" + Convert.appendStringSize("", fetchinfo.fetchdate, 1, 12, 16, 0) + "\n");
			info.append("联 系 人:" + Convert.appendStringSize("", fetchinfo.fetcher, 1, 12, 16, 0) + "\n");
			info.append("联系电话:" + Convert.appendStringSize("", fetchinfo.fetchtel, 1, 12, 16, 0) + "\n\n");
			// info.append("备    注:" + Convert.appendStringSize("",
			// fetchinfo.fetchmemo, 1, 12, 16, 0)+ "\n");

			int len = 0;
			String tmpmemo = fetchinfo.fetchmemo;
			if (tmpmemo != null && tmpmemo.length() > 0)
			{
				do
				{
					len = tmpmemo.length();
					if (len < 20)
					{
						info.append(tmpmemo.substring(0, len) + "\n");
						break;
					}

					info.append(tmpmemo.substring(0, 20) + "\n");
					tmpmemo = tmpmemo.substring(20);
				} while (true);

				new MessageBox(info.toString());
			}
		}
	}

}
