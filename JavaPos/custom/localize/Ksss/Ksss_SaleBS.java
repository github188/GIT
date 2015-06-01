package custom.localize.Ksss;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;


public class Ksss_SaleBS extends Ksss_SaleBS0CRMPop
{
	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef sg = super.goodsDef2SaleGoods(goodsDef,yyyh,quantity,price,allprice,dzcm);
		sg.str6 = goodsDef.str2;
		return sg;
	}
	
	public String getSyyInfoLabel()
	{
		return saleHead.syyh + GlobalInfo.posLogin.name;
	}
	
	public boolean inputQuantity(int index, double quantity)
	{
		SaleGoodsDef oldGoodsDef = null;
		SpareInfoDef oldSpare = null;
		double newsl = -1;
		boolean flag = false;
		// 如果输入了
		if (quantity >= 0)
		{
			flag = true;
			newsl = quantity;
		}
		
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 判断是否允许修改数量
		if (!allowInputQuantity(index)) return false;
		
		// 输入数量
		StringBuffer buffer = new StringBuffer();
		do
		{
			
			if (!flag)
			{
				buffer.delete(0, buffer.length());
				buffer.append(ManipulatePrecision.doubleToString(saleGoodsDef.sl, 4, 1, true));
				if (SellType.ISCOUPON(saletype) || (saleEvent.yyyh.getText().trim().equals("超市") && GlobalInfo.sysPara.goodsAmountInteger == 'Y'))
				{
					if (!new TextBox().open("请输入该商品数量", "数量", "", buffer, 1, getMaxSaleGoodsQuantity(), true, TextBox.IntegerInput, -1)) { return false; }
				}
				else
				{
					if (!new TextBox().open("请输入该商品数量", "数量", "", buffer, 0.0001, getMaxSaleGoodsQuantity(), true)) { return false; }
				}
				newsl = Double.parseDouble(buffer.toString());
				newsl = ManipulatePrecision.doubleConvert(newsl, 4, 1);
				flag = true;
			}
			// 检查销红
			if (SellType.ISSALE(saletype) && (GlobalInfo.sysPara.isxh != 'Y') && (goodsDef.kcsl > 0))
			{
				//统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl,goodsDef.bzhl)) + ManipulatePrecision.mul(newsl,goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					new MessageBox("销售数量已大于该商品库存【"+goodsDef.kcsl+"】\n\n不能销售");
					if (flag) return false;
					continue;
				}
			}

			// 指定小票退货
			if (isSpecifyBack(saleGoodsDef))
			{
				//统计商品数量
				double hjsl = calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
				hjsl = (hjsl - ManipulatePrecision.mul(saleGoodsDef.sl,goodsDef.bzhl)) + ManipulatePrecision.mul(newsl,goodsDef.bzhl);

				if (goodsDef.kcsl < hjsl)
				{
					new MessageBox("退货数量已大于该商品原销售数量\n\n不能退货");
					if (flag) return false;
					continue;
				}
			}

			// 跳出循环
			break;
		} while (true);
		
		if (newsl < 0) return false;

		// 无权限
		if ((newsl < saleGoodsDef.sl) && (curGrant.privqx != 'Y') && (curGrant.privqx != 'Q'))
		{
			//
			OperUserDef staff = inputQuantityGrant(index);
			if (staff == null) return false;

			// 记录日志
			String log = "授权修改数量,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",数量:" + newsl + ",授权:" + staff.gh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();
		
		if (info != null) oldSpare = (SpareInfoDef) info.clone();

		// 重算商品应收
		double oldsl = saleGoodsDef.sl;
		saleGoodsDef.sl = newsl;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		double lszre = ManipulatePrecision.doubleConvert(saleGoodsDef.lszre / oldsl * newsl);
		clearGoodsGrantRebate(index);
		saleGoodsDef.lszre = lszre;
		getZZK(saleGoodsDef);
		calcGoodsYsje(index);
	
		
		// 重算小票应收  
		calcHeadYsje();

		// 数量过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox("商品数量过大,导致销售金额达到上限\n\n商品数量修改无效");

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 退货金额过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox("商品数量过大,导致退货金额超过限额\n\n商品数量修改无效");

			// 恢复数量
			goodsSpare.setElementAt(oldSpare, index);
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		return true;
	}
	
	public boolean getSaleGoodsDisplay()
	{

    	SaleGoodsDef goodsDef1, goodsDef2;

    	// 设置正在销售状态
    	if (SellType.ISCHECKINPUT(this.saletype))
    	{
    		GlobalInfo.syjStatus.status = StatusType.STATUS_CHECK;
    	}
    	else if (saleGoods.size() > 0)
    	{
        	GlobalInfo.syjStatus.status = StatusType.STATUS_SALEING;
    	}
    	else
    	{
    		GlobalInfo.syjStatus.status = StatusType.STATUS_LOGIN;
    	}
    	
    	// 检查已显示的信息是否需要更新
		for (int i=0;i<Math.min(lastGoodsDetail.size(),saleGoods.size());i++)
		{
			goodsDef1 = (SaleGoodsDef)saleGoods.elementAt(i);
			goodsDef2 = (SaleGoodsDef)lastGoodsDetail.elementAt(i);
			
			//此代码无效,不知是谁加的
			//if (goodsDef1.sl == 0) continue;
			
			if (((goodsDef1.hjje - goodsDef1.hjzk) != (goodsDef2.hjje - goodsDef2.hjzk)) || (goodsDef1.sl != goodsDef2.sl))
			{
				//修改已经改变的商品明细
				saleEvent.table.modifyRow(rowInfo(goodsDef1),i);
				
				//即扫即打被改变的商品
				realTimePrintGoods(goodsDef2,i);
			}
		}
    	
		// 检查是否有新的商品需要添加
		if (saleGoods.size() > lastGoodsDetail.size())
		{   
			for (int i=lastGoodsDetail.size();i<saleGoods.size();i++)
			{
				goodsDef1 = (SaleGoodsDef)saleGoods.elementAt(i);
				
				// 刷新明细显示
				saleEvent.updateTable(rowInfo(goodsDef1));	
			}
		}
		else
		{
/*			删除商品由于可删除中间行商品,用上次商品集合和本次商品集合比较的方式不正确
			// 检查是否删除了商品需要删除列表
			if (saleGoods.size() < lastGoodsDetail.size())
			{
				for (int i=lastGoodsDetail.size()-1;i>=saleGoods.size();i--)
				{
					goodsDef1 = (SaleGoodsDef)lastGoodsDetail.elementAt(i);
					
					//
					saleEvent.table.deleteRow(i);
					
					//即扫即打被删除的商品
					realTimePrintGoods(goodsDef1,-1);   					
				}
	        	
	        	// 如果所有商品都删除掉了，作废之前打印
		        if (isRealTimePrint() && saleGoods.size() <= 0) realTimePrintCancelSale();				
			}
*/			
		}
		
		// 盘点和买卷交易不实时打印
		if (!SellType.ISCHECKINPUT(saletype) && !SellType.ISCOUPON(saletype))
		{
	        // 即扫即打状态，第一个商品前先打印交易头 
	    	if (lastGoodsDetail.size() <= 0 && saleGoods.size() > lastGoodsDetail.size())
	    	{
	            realTimePrintStartSale();
	    	}
	    	
	        // 当商品为第一次输入时,不需要打印但需要扩展打印标记
	    	if (saleGoods.size() == 1)
	    	{
	            realTimePrintGoods(null,-1);
	    	}
	    	else
	    	{
				// 即扫即打新增的商品,最后一个新增的商品不在本次打印,输入下一个商品时打印前一个商品
				for (int i = 0 ; i < saleGoods.size()-1; i++)
				{
					realTimePrintGoods(null,i);	
				}
	    	}
		}
		
		// 备份本次商品列表明细
		lastGoodsDetail = null;
    	lastGoodsDetail = cloneSaleGoodsVector(saleGoods);
    	
        // 在要刷新商品列表时,写入断点数据
        writeBrokenData();
        
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
				// 检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				// Y为已在后台退回礼品 津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					new MessageBox("此小票有满赠礼品，请先到后台退回礼品再办理退货！");
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + "\n是否继续退货？", null, true).verify() != GlobalVar.Key1) { return false; }
				}
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
							row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
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
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				// 选择要退货的商品
				int cho = new MutiSelectForm().open("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)", title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);

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
	
	public void custMethod()
	{
		if (!SellType.ISBACK(saletype)) {return; }
		
		for (int j = 0; j < saleGoods.size(); j++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(j);
			SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(j);

			if(sgd.num1>0){
				createMDPayment((sgd.num1*sgd.sl));
				
				SalePayDef sp = (SalePayDef) salePayment.elementAt(salePayment.size() - 1);
				String s[] = { String.valueOf(sp.num5), sp.paycode, sp.payname, String.valueOf(sgd.num1*sgd.sl) };
				if (spinfo.payft == null)spinfo.payft = new Vector();
				spinfo.payft.add(s);
				
			}
		}
//		 重算应收
		calcHeadYsje();
	}
	
	public void doBrokenData()
	{
		if(GlobalInfo.sysPara.mdcode.split(",")[0].trim().equals("")) {
	    	return ;
	    }
		SalePayDef sp = null;
		for (int i = salePayment.size() - 1; i > -1; i--)
		{
			sp = (SalePayDef) salePayment.elementAt(i);
			if (sp.paycode.equals(GlobalInfo.sysPara.mdcode.split(",")[0]))
			{
				salePayment.remove(i);
			}
		}
	}
	
	public boolean checkDeleteSalePay(String ax, boolean isDelete)
	{
		String code ="";
		if(ax.trim().indexOf("]")>-1)
		{
			code = ax.substring(1,ax.trim().indexOf("]"));
		}else{
			code = ax;
		}
		if(code.equals(GlobalInfo.sysPara.mdcode.split(",")[0])){
			return true;
		}
		return false;
	}
}
