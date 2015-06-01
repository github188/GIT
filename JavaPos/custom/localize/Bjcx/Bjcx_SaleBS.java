package custom.localize.Bjcx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.HangBillMode;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Bjcx_SaleBS extends Cmls_SaleBS {
	boolean cxRebate = false;
	
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
		else if (EBill.getDefault().getBackSaleBill(this)) //android 指定小票退货
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

	
	public boolean comfirmPay()
	{
		//商品数量小于1
		
		//一单只能有一个以旧换新码
		int  numjjm = -1;
		for (int i = 0; i < saleGoods.size(); i++)
		{
			SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.get(i);

			if (ManipulatePrecision.doubleConvert(goodsDef.hjje -  goodsDef.hjzk) < 0)
			{
				new MessageBox("第 " + (i + 1) + " 行商品 [" + goodsDef.code + "] 金额不合法，不能付款\n检查商品、删除此商品后重新录入");
				return false;
			}
			
			if (goodsDef.type == '8' && numjjm != -1)
			{
				SaleGoodsDef goodsDef1 = (SaleGoodsDef) saleGoods.get(numjjm);
				new MessageBox("第 " + (numjjm + 1) + " 行商品 [" + goodsDef1.code + "] \n和\n "+"第 " + (i + 1) + " 行商品 [" + goodsDef.code + "] "+"\n都为以旧换新码，不能付款\n检查商品、删除其中一个商品后重新录入");
				return false;
			} 
			else if (goodsDef.type == '8')
			{
				numjjm = i;
			}
		}
		
		return super.comfirmPay();
	}
	
	public boolean allowFinishFindGoods(GoodsDef goodsDef, double quantity, double price)
	{
		if (goodsDef.type == '8' && ((SaleGoodsDef)(saleGoods.lastElement())).type != '1')	
		{
			new MessageBox("此编码为以旧换新码，上一个商品需为正常编码");
			return false;
		}
		return super.allowFinishFindGoods(goodsDef, quantity, price);
	}
	
	 public String[] rowInfo(SaleGoodsDef goodsDef)
	    {
	    	if (SellType.ISCHECKINPUT(saletype))
	    	{
		    	String[] detail = new String[8];
		    	
		        if (goodsDef.inputbarcode != null && goodsDef.inputbarcode.trim().length() > 0 && GlobalInfo.sysPara.barcodeshowcode == 'N') 
		        {
		        	detail[1] = goodsDef.inputbarcode;
		        }
		        else if (GlobalInfo.sysPara.barcodeshowcode == 'Y')
		        {
		        	detail[1] = goodsDef.code;
		        }
		        else
		        {
		        	detail[1] = goodsDef.barcode;
		        }
		        
		        detail[2] = goodsDef.name;
		        detail[3] = goodsDef.unit;
		        
	        	if (goodsDef.sqkh != null && goodsDef.sqkh.trim().length() > 0)
	        	{
	        		detail[4] = goodsDef.sqkh;
	        	}
	        	
		        if (goodsDef.jg > 0 || goodsDef.type == 'Z')
		        {
			        detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
			        detail[6] = ManipulatePrecision.doubleToString(goodsDef.sl,4,1,true);
		        }
		        else
		        {
			        detail[5] = "";
			        detail[6] = "";
			        
			        // 不定价商品需要盘点数量时，界面也要显示数量
			        if (GlobalInfo.sysPara.ischeckquantity == 'Y')
			        {
			        	detail[6] = ManipulatePrecision.doubleToString(goodsDef.sl,4,1,true);
			        }
		        }
		        
		        detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje,2,1);
		        
		        return detail;
	    	}
	    	else
	    	{
		    	String[] detail = new String[8];
		    	
		        if (goodsDef.inputbarcode != null && goodsDef.inputbarcode.trim().length() > 0 && GlobalInfo.sysPara.barcodeshowcode == 'N') 
		        {
		        	detail[1] = goodsDef.inputbarcode;
		        }
		        else if (GlobalInfo.sysPara.barcodeshowcode == 'Y')
		        {
		        	detail[1] = goodsDef.code;
		        }
		        else
		        {
		        	detail[1] = goodsDef.barcode;
		        }
		        
		        /*if (GlobalInfo.syjDef.issryyy == 'Y' && goodsDef.gz != null && !goodsDef.gz.equals("")) 
		        {
		        	detail[2] = "["+goodsDef.gz + "]"+goodsDef.name;
		        }
		        else 
		        {
		        	detail[2] = goodsDef.name;
		        }*/
		        detail[2] = goodsDef.name;//商品名称前面不显示柜组信息 wangyong update 2011.10.25
		        detail[3] = goodsDef.unit;
		        detail[4] = ManipulatePrecision.doubleToString(goodsDef.sl,4,1,true);
		        detail[5] = ManipulatePrecision.doubleToString(goodsDef.jg);
		        detail[6] = ManipulatePrecision.doubleToString(goodsDef.hjzk) + ((goodsDef.hjzk>0)&&(goodsDef.hjje-goodsDef.hjzk>0)?"(" + ManipulatePrecision.doubleToString((goodsDef.hjje - goodsDef.hjzk)/goodsDef.hjje*100,0,1) + "%)":"");
		        detail[7] = ManipulatePrecision.doubleToString(goodsDef.hjje - goodsDef.hjzk,2,1);
		        
		        return detail;
	    	}
	    }
	   

	//输入金额
	public boolean inputPrice(int index)
	{
		if (SellType.isJS(saletype))
		{
			return false;
		}
		
		SaleGoodsDef oldGoodsDef = null;
		double newjg;
		String grantgh = null;

		// 检查是否允许输入价格
		if (!allowInputPrice(index)) { return false; }

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && "D".equals(saleGoodsDef.str8)) return false;
		
		GoodsDef goodsDef = null;
		if (goodsAssistant != null && goodsAssistant.size() > index) goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 定价商品授权改价
		if ( SellType.ISSALE(saletype) && !SellType.ISBATCH(saletype) && (goodsDef == null || goodsDef.lsj > 0) && curGrant.privgj != 'Y')
		{
			OperUserDef staff = inputPriceGrant(index);
			if (staff == null) return false;

			// 记录授权号
			grantgh = staff.gh;
		}
		
		// 输入价格
		StringBuffer buffer = new StringBuffer();
		newjg = saleGoodsDef.jg;
		do
		{
			buffer.delete(0, buffer.length());
			buffer.append(newjg);
			
			if (!new TextBox().open("请输入该商品价格", "价格", "", buffer, 0.01, getMaxSaleGoodsMoney(), true)) { return false; }

			newjg = Convert.toDouble(buffer.toString());
			// 输入价格按价格金额截取
			if (goodsDef != null ) newjg = getConvertPrice(Double.parseDouble(buffer.toString()), goodsDef);
			newjg = ManipulatePrecision.doubleConvert(newjg, 2, 1);

			// 检查价格(P:配件;Z:赠品)
			if (goodsDef != null && goodsDef.type != 'P' && goodsDef.type != 'Z' && newjg <= 0)
			{
				new MessageBox("该商品价格必须大于0");
				continue;
			}

			// 最低限价
			if (goodsDef != null &&  newjg < goodsDef.maxzke)
			{
				new MessageBox("该项商品价格不能小于最低限价" + ManipulatePrecision.doubleToString(goodsDef.maxzke));
				continue;
			}
			
			//	是否允许在商品退货时,商品是否在下限和上限的价格之内
            if (!isAllowedBackPriceLimit(goodsDef,newjg)) continue;
            
			// 跳出循环
			break;
		} while (true);

		// 备份数据
		oldGoodsDef = (SaleGoodsDef) saleGoodsDef.clone();

		// 重算商品应收
		if (goodsDef != null && goodsDef.lsj > 0) saleGoodsDef.flag = '6';	// 标记该商品被议价
		saleGoodsDef.jg = newjg;
		saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
		
		SaleGoodsDef saleGoodsDefTmp = (SaleGoodsDef) saleGoods.elementAt(index);
		saleGoodsDefTmp.yhzke = 0;//清除降价促销的折扣
		//saleGoodsDefTmp.yhzkfd = 0;
		//saleGoodsDefTmp.yhdjbh = "";
		
		clearGoodsGrantRebate(index);
		
		if (goodsDef != null) calcGoodsYsje(index);

		// 重算小票应收  
		calcHeadYsje();

		// 价格过大
		if (saleHead.ysje > getMaxSaleMoney())
		{
			new MessageBox("商品价格过大,导致销售金额达到上限\n\n商品价格修改无效");

			// 恢复价格
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}

		// 价格过大
		if (SellType.ISBACK(saletype) && saleHead.ysje > curGrant.thxe)
		{
			new MessageBox("商品价格过大,导致退货金额超过限额\n\n商品价格修改无效");

			// 恢复数量
			saleGoods.setElementAt(oldGoodsDef, index);
			calcHeadYsje();

			return false;
		}
		
		// 盘点处理
		if (SellType.ISCHECKINPUT(saletype) && isSpecifyCheckInput() && !"U".equals(saleGoodsDef.str8))
		{
			if ("A".equals(saleGoodsDef.str8))
			{
				saleGoodsDef.str8 = "A";
				saleGoodsDef.name += "[修改]";
			}
			
			else if (saleGoodsDef.str8 == null || saleGoodsDef.str8.length() == 0)
			{
				saleGoodsDef.name += "[修改]";
				saleGoodsDef.str8 = "U";
			}
		}
		
		// 记录授权日志
		if (grantgh != null)
		{
			String log = "授权修改价格,小票号:" + saleHead.fphm + ",商品:" + saleGoodsDef.barcode + ",原价:" + oldGoodsDef.jg + ",新价格:" + saleGoodsDef.jg + ",授权:"
					+ grantgh;
			AccessDayDB.getDefault().writeWorkLog(log);
		}

		return true;
	}


	public boolean allowInputPrice(int index)
	{
		if (SellType.isJF(saletype) || SellType.isJS(saletype)) return true;
		
		// A模式指定小票退货不允许修改商品
		if (isNewUseSpecifyTicketBack()) return false;

		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		
		// 电子秤商品不允许修改数量
		//if (saleGoodsDef.flag == '2') return false;

		// 指定小票退货不允许修改价格
		if (isSpecifyBack(saleGoodsDef)) return false;
		
		
		// 商品不允许议价
		if (!SellType.ISBATCH(saletype)  && goodsDef.ischgjg == 'N')//SellType.ISSALE(saletype) && && goodsDef.lsj > 0
		{
			new MessageBox("该商品不允许改价");
			return false;
		}
		
		return true;
	}
	
	public boolean checkFindGoodsAllowSale(GoodsDef goodsDef, double quantity, boolean isdzcm, double dzcmsl, double dzcmjg)
	{
		// 检查电子秤码,'Y'秤重的电子秤商品,'O'非秤重的电子秤商品;非秤重的电子秤商品允许输入数量
		if (isdzcm)
		{
			if ((goodsDef.isdzc != 'Y') && (goodsDef.isdzc != 'O'))
			{
				new MessageBox("该商品不是电子秤商品\n不能用电子秤码销售");

				return false;
			}

			if ((dzcmsl > 0) && (dzcmjg < 0) && (goodsDef.lsj <= 0))
			{
				new MessageBox("该电子秤商品未定价,不能销售");

				return false;
			}

			if ((goodsDef.isdzc == 'Y') && (dzcmsl > 0 && ManipulatePrecision.doubleCompare(ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1), quantity, 4) != 0))
			{
				new MessageBox("电子秤条码不允许输入数量");

				return false;
			}
		}
		else
		{
			if ((goodsDef.isdzc == 'Y' && GlobalInfo.sysPara.dzccodesale != 'B'))
			{
				new MessageBox("该商品是电子秤商品\n不能直接销售");

				return false;
			}
		}

		// 以旧换新码处理
		if (goodsDef.type == '8')
		{
			if (!checkOldExChangeNew(goodsDef))
			{
				new MessageBox("请先输入以旧换新码对应的新品编码");

				return false;
			}
		}

		// 子母商品销售
		if (goodsDef.type == '6')
		{
			new MessageBox("母商品不能直接销售，请选择相应的子商品销售!");
			return false;
		}

		// 特卖码商品是否允许销售
		if (goodsDef.type == 'T' && goodsDef.iszs != 'Y' && SellType.ISSALE(saletype))
		{
			new MessageBox("特卖码未生效或已过期,不能销售！");
			return false;
		}

		if (GlobalInfo.sysPara.isEARNESTZT == 'N' && SellType.ISEARNEST(saletype) && goodsDef.iszt != 'Y')
		{
			new MessageBox("该商品不能进行" + SellType.getDefault().typeExchange(saletype, saleHead.hhflag, saleHead));
			return false;
		}

		// 不允许销红,检查库存
		if ((SellType.ISSALE(saletype) && GlobalInfo.sysPara.isxh != 'Y' && goodsDef.isxh != 'Y'))
		{
			// 统计商品销售数量
			double hjsl = ManipulatePrecision.mul(quantity, goodsDef.bzhl) + calcSameGoodsQuantity(goodsDef.code, goodsDef.gz);
			if (goodsDef.kcsl < hjsl)
			{
				new MessageBox("该商品库存为 " + ManipulatePrecision.doubleToString(goodsDef.kcsl) + "\n库存不足,不能销售");

				return false;
			}
		}

		// T代表此商品已经被停用
		if (SellType.ISSALE(saletype) && goodsDef.iszs == 'T')
		{
			new MessageBox("当前商品已停售,不能销只能退！");
			return false;
		}

		return true;
	}

		
	public boolean findGoods(String code, String yyyh, String gz)
	{		
		String comcode = "";
		String barcode = "";
		boolean isdzcm;
		double dzcmjg = 0;
		double dzcmsl = 0;
		String dzcmscsj = "";
		double quantity = 1;
		double price = 0;
		
		// 检查是否允许找商品
		if (!allowStartFindGoods())
			return false;

		// 分解输入码 数量*编码
		String[] s = convertQuantityBarcode(code);
		if (s == null)
			return false;
		quantity = Convert.toDouble(s[0]);
		barcode = s[1];

		// 解析电子秤码
		String[] codeInfo = new String[4];
		isdzcm = analyzeBarcode(barcode, codeInfo);
		if (isdzcm)
		{
			comcode = codeInfo[0];
			dzcmjg = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[1]), 2, 1);
			dzcmsl = ManipulatePrecision.doubleConvert(Double.parseDouble(codeInfo[2]), 4, 1);
			dzcmscsj = codeInfo[3];

			// 验证电子秤校验位
			if (!verifyDzcmCheckbit(barcode))
			{
				new MessageBox("电子秤码校验位错误", null, false);

				return false;
			}

			if (dzcmjg <= 0 && dzcmsl <= 0)
			{
				new MessageBox("该电子秤格式条码无效", null, false);

				return false;
			}
		}
		else
		{
			comcode = barcode;
		}

		// 查找详细商品资料,可支持数量转换
		StringBuffer slbuf = new StringBuffer("1");
		GoodsDef goodsDef = findGoodsInfo(comcode, yyyh, gz, dzcmscsj, isdzcm, slbuf);
		if (goodsDef == null)
			return false;
		quantity *= Convert.toDouble(slbuf.toString());

		// 获得最小批量数量
		quantity = getMinPlsl(quantity, goodsDef);

		// 电子秤商品记录原始电子秤码
		goodsDef.inputbarcode = barcode;
		if (isdzcm)
			goodsDef.barcode = convertDzcmBarcode(goodsDef, barcode, isdzcm);

		// 设置商品缺省售价
		price = setGoodsDefaultPrice(goodsDef);

		// 电子秤码没有通过条码解析销售，补入商品价格或数量
		if (goodsDef.isdzc == 'Y' && !isdzcm)
		{
			// 输入价格模式
			if (GlobalInfo.sysPara.dzccodesale == 'Y')
			{
				isdzcm = true;

				StringBuffer pricestr = new StringBuffer();
				do
				{
					pricestr.delete(0, pricestr.length());
					pricestr.append(price);

					boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "总价格", "总价格", "", pricestr, 0.01, getMaxSaleGoodsMoney(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmjg = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

						if (dzcmjg <= 0)
						{
							new MessageBox("该商品价格必须大于0");
						}
						else
						{
							break;
						}
					}
				} while (true);
			}

			// 输入数量模式
			if (GlobalInfo.sysPara.dzccodesale == 'A')
			{
				isdzcm = true;

				StringBuffer slstr = new StringBuffer();
				do
				{
					slstr.delete(0, slstr.length());
					slstr.append(price);

					boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "数量", "数量", "", slstr, 0.01, getMaxSaleGoodsQuantity(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmsl = Double.parseDouble(slstr.toString());

						if (dzcmsl <= 0)
						{
							new MessageBox("该商品数量必须大于0");
						}
						else
						{
							break;
						}
					}
				} while (true);
			}
			
			if (GlobalInfo.sysPara.dzccodesale == 'B')
			{
				/*if (goodsDef.lsj > 0 )
				{
					new MessageBox("该电子秤商品是定价商品，不能手输数量和价格！", null, false);
					return false;
				}*/
				
				isdzcm = true;
								
				StringBuffer slstr = new StringBuffer();
				do
				{
					slstr.delete(0, slstr.length());
					slstr.append(1);

					boolean done = new TextBox().open("请输入 电子秤商品[" + goodsDef.inputbarcode + "]" + " 数量", "数量", "", slstr, 0.01, getMaxSaleGoodsQuantity(), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmsl = Double.parseDouble(slstr.toString());

						if (dzcmsl <= 0)
						{
							new MessageBox("该商品数量必须大于0");
						}
						else
						{
							break;
						}
					}
				} while (true);
				
				StringBuffer pricestr = new StringBuffer();
				//double dj=0 ;//单价
				do
				{
					pricestr.delete(0, pricestr.length());
					//pricestr.append(price);
					double maxje = getMaxSaleGoodsMoney();
					double tmpje = dzcmsl * goodsDef.lsj;
					double defaultje = tmpje;
					if (goodsDef.poplsj>0)
					{
						defaultje = dzcmsl * goodsDef.poplsj;
					}
					if (tmpje > 0 && (maxje > tmpje))//如果是定价商品，输入的总价不超过（商品*当前输入的数量）
					{
						maxje = tmpje;
					}
					pricestr.append(ManipulatePrecision.doubleConvert(defaultje,2,1));
					boolean done = new TextBox().open("请输入 电子秤商品[" + goodsDef.inputbarcode + "]" +  " 总价", "总价", "", pricestr, 0.01, ManipulatePrecision.doubleConvert(maxje,2,1), true);

					if (!done)
					{
						return false;
					}
					else
					{
						dzcmjg = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

						if (dzcmjg <= 0)
						{
							new MessageBox("该商品总价必须大于0");
						}
						else
						{							
							break;
						}
					}
				} while (true);
				
				//dzcmjg=dj * dzcmsl;
			}
		}

		// 电子秤条码的数量价格处理
		int dzcprice = 0;
		double allprice = 0;
		if (isdzcm)
		{
			dzcmjgzk = 0;

			if ((dzcmsl > 0) && (dzcmjg <= 0)) // 只有数量
			{
				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				price = ManipulatePrecision.doubleConvert(goodsDef.lsj, 2, 1);
				allprice = quantity * price;
				dzcprice = 1;

				// 电子秤打印的合计一般都是从第三位截断再四舍五入
				allprice = ManipulatePrecision.doubleConvert(allprice, 3, 0);
				allprice = ManipulatePrecision.doubleConvert(allprice, 2, 1);

				// 按价格精度进行计算,差额记折扣
				double jg = getConvertPrice(allprice, goodsDef);
				if (ManipulatePrecision.doubleCompare(allprice, jg, 2) != 0)
				{
					dzcmjgzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.sub(allprice, jg));
				}
			}
			else if ((dzcmsl <= 0) && (dzcmjg > 0)) // 只有金额
			{
				if (goodsDef.lsj <= 0) // 不定价商品
				{
					quantity = 1;
					price = dzcmjg;
					allprice = price;
					dzcprice = 1;
				}
				else
				// 定价商品,反算数量
				{
					// pfj存放电子秤实际秤上的价格(可能是促销价),如果和商品主档价格不一致,说明有促销,
					// 用秤的价格反算出数量然后再正常计算促销
					//GlobalInfo.sysPara.isCalcAsPfj ='Y';
					if (GlobalInfo.sysPara.isCalcAsPfj == 'Y' && (goodsDef.pfj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj, goodsDef.pfj, 2) != 0))
					{
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.pfj), 4, 1);
						price = goodsDef.lsj;
						allprice = ManipulatePrecision.doubleConvert(quantity * price);
						dzcprice = 2;

						if (SellType.ISBACK(saletype))
						{
							double zk1 = allprice - ManipulatePrecision.doubleConvert(quantity * goodsDef.pfj);
							zk1 = ManipulatePrecision.doubleConvert(zk1, 3, 0);
							zk1 = ManipulatePrecision.doubleConvert(zk1, 2, 1);
								
							dzcmjgzk = ManipulatePrecision.doubleConvert(zk1);
						}
					}
					else
					{
						quantity = ManipulatePrecision.doubleConvert((dzcmjg / goodsDef.lsj), 4, 1);
						price = goodsDef.lsj;
						allprice = dzcmjg;
						dzcprice = 2;
					}
				}
			}
			else if ((dzcmsl > 0) && (dzcmjg > 0)) // 即有数量又有价格
			{
				// bzhl记录电子秤称重单位和商品主档单位的转换比例
				// 如果定价商品单价*数量的成交金额已经与秤的成交价四舍五入精度后一致,则无需重算商品单价
				if (goodsDef.bzhl <= 0)
					goodsDef.bzhl = 1;
				quantity = ManipulatePrecision.doubleConvert(dzcmsl / goodsDef.bzhl, 4, 1);
				allprice = dzcmjg;
				if (goodsDef.lsj > 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, ManipulatePrecision.getDoubleScale(allprice)) == 0 && ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, 2) != 0)
				{
					// 电子秤的成交价可能到角,秤的成交价和数量*单价到分的成交价之间的四舍五入差额记折扣
					if (ManipulatePrecision.doubleCompare(goodsDef.lsj * quantity, allprice, 2) != 0)
					{
						allprice = ManipulatePrecision.doubleConvert(goodsDef.lsj * quantity, 2, 1);
						dzcmjgzk = ManipulatePrecision.sub(allprice, dzcmjg);
						dzcmjgzk = ManipulatePrecision.doubleConvert(dzcmjgzk, 2, 1);
					}
				}
				else
				{
					if (goodsDef.lsj > 0)
					{
						//若存在单品促销单，则清除；以电子称上的促销为准
						if (goodsDef.poplsj > 0)
						{
							goodsDef.poplsj=0;
							goodsDef.pophyj=0;
						}
						double tmpzk =(goodsDef.lsj * quantity) - dzcmjg;
						tmpzk = ManipulatePrecision.doubleConvert(tmpzk, 3, 0);
						tmpzk = ManipulatePrecision.doubleConvert(tmpzk, 2, 1);
						if (tmpzk > 0 )
						{
							dzcmjgzk = ManipulatePrecision.doubleConvert(tmpzk);
							allprice = ManipulatePrecision.doubleConvert(goodsDef.lsj * quantity);
							double minus = 0;
							if ((minus = ManipulatePrecision.doubleConvert(allprice-dzcmjgzk-dzcmjg)) > 0)
							{
								dzcmjgzk = ManipulatePrecision.doubleConvert(minus + dzcmjgzk);
							}
						}
						//new MessageBox(tmpzk+"    "+dzcmjgzk);
					}
					else
					{
						goodsDef.lsj = goodsDef.hyj = goodsDef.pfj = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
					}
					/*if (goodsDef.lsj > 0 && (goodsDef.poplsj <= 0 || isInputDZCJg))
					{//当没有单据促销时（即可视为是仅电子称临时促销）
						
												
						double tmpzk =(goodsDef.lsj * quantity) - dzcmjg;//计算是否有折扣，若有，则在小票中体现，便于后台不计算积分
						if (tmpzk>0 )
						{
							dzcmjgzk = tmpzk;
							allprice = ManipulatePrecision.doubleConvert(goodsDef.lsj * quantity);
						}
						
						double tmpzk =(goodsDef.lsj * quantity) - dzcmjg;//计算是否有折扣，若有，则在小票中体现，便于后台不计算积分
						if (tmpzk>0 )
						{
							dzcmjgzk = tmpzk;
							allprice = goodsDef.lsj * quantity;
						}
					}
					else
					{
						if (goodsDef.lsj>0 && goodsDef.poplsj>0)
						{
							//该电子称商品是否存在单据促销
							double tmpje = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
							if (tmpje == goodsDef.poplsj)
							{
								goodsDef.lsj = goodsDef.hyj = tmpje;//goodsDef.pfj = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
							}
							else
							{
								//若存在，就以促销单据中的促销价为准（防止反算得出的单价与时间段促销单据中的促销价不一致，而产生误差折扣）
								goodsDef.lsj = goodsDef.hyj = goodsDef.pfj = goodsDef.poplsj;//ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
							}
							
						}
						else
						{
							goodsDef.lsj = goodsDef.hyj = goodsDef.pfj = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
						}
						
					}*/
					
					
					//goodsDef.lsj = goodsDef.hyj = goodsDef.pfj = ManipulatePrecision.doubleConvert(dzcmjg / (dzcmsl / goodsDef.bzhl), 2, 1);
					
					
					
				}
				price = goodsDef.lsj;
			}
		}

		// 检查找到的商品是否允许销售
		if (!checkFindGoodsAllowSale(goodsDef, quantity, isdzcm, dzcmsl, dzcmjg))
			return false;

		// 未定价商品或退货或批发要求输入售价
		if (isPriceConfirm(goodsDef))
		{
			// 指定小票退货,查询退货原始交易信息
			if (isSpecifyBack())
			{
				Vector back = new Vector();

				if (!DataService.getDefault().getBackGoodsDetail(back, thSyjh, String.valueOf(thFphm), goodsDef.code, goodsDef.gz, goodsDef.uid)) { return false; }

				int cho = 0;
				if (back.size() > 1)
				{
					Vector choice = new Vector();
					String[] title = { "商品编码", "数量", "单价", "合计折扣", "应付金额" };
					int[] width = { 100, 100, 100, 100, 100 };
					String[] row = null;
					for (int j = 0; j < back.size(); j++)
					{
						thSaleGoods = (SaleGoodsDef) back.elementAt(j);
						row = new String[5];
						row[0] = thSaleGoods.code;
						row[1] = ManipulatePrecision.doubleToString(thSaleGoods.sl, 4, 1, true);
						row[2] = ManipulatePrecision.doubleToString(thSaleGoods.lsj, 2, 1);
						row[3] = ManipulatePrecision.doubleToString(thSaleGoods.hjzk, 2, 1);
						row[4] = ManipulatePrecision.doubleToString(thSaleGoods.hjje - thSaleGoods.hjzk, 2, 1);
						choice.add(row);
					}

					cho = new MutiSelectForm().open("请选择退货商品信息", title, width, choice);
				}
				thSaleGoods = (SaleGoodsDef) back.elementAt(cho);

				if (thSaleGoods.sl < quantity)
				{
					new MessageBox("该商品退货数量大于原销售数量\n\n不能退货");
					thSaleGoods = null;
					return false;
				}
			}

			// 如果是指定小票退货，不进行价格确认
			// 如果是电子秤商品且价格确定，不进行价格确认
			if (!isConfirmPrice(isdzcm, dzcprice, goodsDef))
			{
			}
			else
			{
				if (!isonlinegdjging)
				{
					StringBuffer pricestr = new StringBuffer();
					do
					{
						pricestr.delete(0, pricestr.length());
//						pricestr.append(price);
						pricestr.append(goodsDef.lsj);

						double min = 0.01;
						if (goodsDef.type == 'Z')
						{
							min = 0;
						}

						boolean done = new TextBox().open("请输入商品[" + goodsDef.inputbarcode + "]" + (goodsDef.name.trim().length() > 20 ? goodsDef.name.trim().substring(0, 19) : goodsDef.name.trim()) + "价格", "价格", "", pricestr, min, getMaxSaleGoodsMoney(), true);
						if (!done)
						{
							return false;
						}
						else
						{
							price = ManipulatePrecision.doubleConvert(getConvertPrice(Double.parseDouble(pricestr.toString()), goodsDef), 2, 1);

							// 检查价格
							if (price <= 0 && goodsDef.type != 'Z')
							{
								new MessageBox("该商品价格必须大于0");
							}
							else
							{
								// 电子秤商品重新计算
								if (isdzcm && (dzcprice > 0))
								{
									if (dzcprice == 1)
									{
										allprice = quantity * price;
									}
									else
									{
										quantity = ManipulatePrecision.doubleConvert(dzcmjg / price, 4, 1);
									}
								}

								// 是否允许在商品退货时,商品是否在下限和上限的价格之内
								if (!isAllowedBackPriceLimit(goodsDef, price))
									continue;

								break;
							}
						}
					} while (true);
				}
			}
		}

		// 如果是联网挂单状态，则不输入商品附加信息
		if (!isonlinegdjging && !inputGoodsAddInfo(goodsDef))
			return false;

		// 检查找到的商品最后是否OK
		if (!allowFinishFindGoods(goodsDef, quantity, price))
			return false;

		// 增加商品到商品明细中
		if (!addSaleGoods(goodsDef, yyyh, quantity, price, allprice, isdzcm))
			return false;

		return true;
	}


	// 打印挂单小票    
	public void printHang(int maxGD)
	{
        // 计算小票应收
        calcHeadYsje();
        
		ProgressBox progress = new ProgressBox();
		try
		{
			int printNum = 1;
			printNum = com.efuture.commonKit.Convert.toInt(ConfigClass.CustomItem1);		
			if (printNum <=0)
			{
				printNum = 1;
			}
			progress.setText("正在打印挂单信息，请等待.....");
			
			SaleBillMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);
			for (int i =0; i<printNum; i++)
			{
				HangBillMode.getDefault().printBill(maxGD, saleHead.ysje);
			}	

			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			progress.close();
		}
		
	}
	
	
	//走纸键，即打印一空行
	public void execCustomKey9(boolean keydownonsale)
	{
		//execCustomKey(keydownonsale, GlobalVar.CustomKey9);
		if (isRealTimePrint()) 
		{
			new MessageBox("实时打印模式下不能使用该功能！");
			return;
		}
		
		((Bjcx_SaleBillMode)SaleBillMode.getDefault()).printOneLine("  ");
	}	
	
	public boolean yyhExtendAction(OperUserDef staff)
	{
		if (GlobalInfo.sysPara.inputyyyfph == 'Y')
		{
			StringBuffer req = new StringBuffer();
			req.append(com.efuture.commonKit.Convert.toInt(curyyyfph) + 1);
			
			boolean done = new TextBox().open("请输入发票号", "发票号", "请根据营业员的单据输入发票号码", req, 0, 0, false, TextBox.IntegerInput);
			if (done)
			{
				curyyyfph = req.toString();
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	// 遍历查找CRM促销（后刷会员卡用）
	protected void calcMenberCrmPop()
	{
		if (saleGoods.size() > 0)
		{
			ProgressBox pb = null;
			try
			{
				pb = new ProgressBox();
				pb.setText("正在查找CRM促销信息,请等待...");
				
				for (int i = 0; i < saleGoods.size(); i++)
				{					
					// 查找CRM促销
					findGoodsCRMPop((SaleGoodsDef)saleGoods.elementAt(i), 
										(GoodsDef)goodsAssistant.elementAt(i), 
											(SpareInfoDef)goodsSpare.elementAt(i));
				}

		        // 计算小票应收
		        calcHeadYsje();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}
			}
			
		}
	}

	//会员授权:要求支持后刷会员卡
	public boolean memberGrant()
	{
		if (isPreTakeStatus())
		{
			new MessageBox("预售提货状态下不允许重新刷卡");
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡");
			return false;
		}
				
		boolean blnRet = false;
		
		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals("")) return false;

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);
		
		if (cust == null) return false;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;

			return true;
		}
		else
		{
			// 记录会员卡
			blnRet = memberGrantFinish(cust);
		}
		
		if (blnRet)
		{
			saleHead.hysq = curCustomer.code;
			
			if (curCustomer.iszk == 'Y')
			{				
				saleHead.sqkh = curCustomer.code;
			}
			
			//遍历查找已录入商品的CRM促销
			calcMenberCrmPop();
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	// 北京城乡的团购直接录入，不在网上取
	public boolean Groupbuy_Change()
	{
		return true;
	}
	
	public boolean allowStartFindGoods()
	{
		if(super.allowStartFindGoods())
		{
			//团购模式下，必须要刷(先)机关卡
			if (SellType.isGroupbuy(saletype) && !checkMemberSale())
			{
				new MessageBox("团购模式未刷机关卡,不能增加商品\n\n请刷机关卡后再输入");
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean inputGoodsAddInfo(GoodsDef goodsDef)
	{
		// 输入商品批号,加入商品明细时检查是否加入批号了
		if (goodsDef.isbatch == 'Y' || goodsDef.isbatch == 'A')
		{
			// A模式商品继承上一个商品的批号,不清除批号
			if (goodsDef.isbatch == 'Y')
				curBatch = "";
			if (goodsDef.isbatch == 'A' && saleGoods.size() <= 0)
				curBatch = "";

			// 输入批号
			if (curBatch == null || curBatch.equals(""))
			{
				StringBuffer bstr = new StringBuffer();
				if (new TextBox().open("请输入商品批号", "批号", "该商品要求输入商品批号", bstr, -1, -1, false, TextBox.AllInput))
				{
					curBatch = bstr.toString();
				}
				bstr = null;
			}
		}
		else
		{
			curBatch = "";
		}

		// 批发销售输入折扣分担
		curPfzkfd = 1;
		if (SellType.ISBATCH(saletype))
		{
			StringBuffer bstr = new StringBuffer();
			bstr.append(String.valueOf(goodsDef.pfjzkfd*100));
			if (!new TextBox().open("请输入商品的商家折扣分担(%):", "折扣分担", "请输入百分比,不能大于100,也不能小于0", bstr, 0, 100, true))
			{
				new MessageBox("未输入商品商家折扣分担\n\n商品查询失败！");
				return false;
			}
			curPfzkfd = ManipulatePrecision.doubleConvert(Double.parseDouble(bstr.toString()) / 100, 4, 1);
			bstr = null;
		}

		return true;
	}

	
	 public boolean isUseMemberHyj(char isvipzk)
	 {
	    if (isvipzk=='H')
	    	return true;
	    else
	    	return false;
	 }
	 
	 public boolean isMemberHyjMode()
	 {
	    	if (checkMemberSale() && (curCustomer != null) && (curCustomer.ishy == 'Y')) return true;
	    	else return false;
	 }
	 
	 public void calcAllRebate(int index)
	 {	        
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		//GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }
		
		if (SellType.ISCHECKINPUT(saletype)) { return; }

		// 削价商品和赠品不计算,积分换购商品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1') || (saleGoodsDef.flag == '6')) { return; }//被议价的商品不再计算促销

		saleGoodsDef.hyzke = 0;
		saleGoodsDef.yhzke = 0;
		saleGoodsDef.yhzkfd = 0;
		saleGoodsDef.zszke = 0;

        // 促销单缺省允许VIP折上折,可通过促销单定义改变
        //popvipzsz = 'Y';

        //促销优惠
        //换消状态下不计算定期促销 from Bcrm_SaleBS0CRMPop
        if (hhflag != 'Y')
        {
	        // 计算商品促销折扣
	        calcGoodsPOPRebate(index);
        }

        // 计算会员VIP折上折
        //calcGoodsVIPRebate(index);

        // 
        saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke, 2, 1);
        saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hyzke, 2, 1);

        // 按价格精度计算折扣
        if (saleGoodsDef.yhzke > 0) saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
        if (saleGoodsDef.hyzke > 0) saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
        

		// vipzk1表示实时输商品的时候立即计算VIP折扣
		getVIPZK(index, vipzk1);
	 }
	 
	 public String[] convertQuantityBarcode(String code)
		{
			String barcode;
			double quantity = 1;

			if ((code.indexOf("*") <= 0) || (code.indexOf("*") >= (code.length() - 1)))
			{
				barcode = code;
				quantity = 1;
			}
			else
			{
				// 解析出数量
				String[] codes = new String[2];
				codes[0] = code.substring(0, code.indexOf("*"));
				codes[1] = code.substring(code.indexOf("*") + 1);

				// 检查数量是否合法
				try
				{
					if (codes[0].indexOf(".") > -1 && GlobalInfo.sysPara.goodsAmountInteger == 'Y' )
					{
						quantity = 0;
						new MessageBox("数量不能输入小数,请重新输入");
						return null;
					}
					
					quantity = Double.parseDouble(codes[0]);
					if (quantity <= 0)
						quantity = 1;
				}
				catch (Exception ex)
				{
					quantity = 0;
					new MessageBox("数量输入不是有效数字,请重新输入");
					return null;
				}

				// 检查数量是否合法
				if (quantity > getMaxSaleGoodsQuantity())
				{
					new MessageBox("商品数量过大，请分行输入");
					return null;
				}

				//
				barcode = codes[1];
				codes = null;
			}

			return new String[] { String.valueOf(quantity), barcode };
		}
	 
	 public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	 {
		 super.findGoodsCRMPop(sg, goods, info);
		 
		 String cardno = null;
		 String cardtype = null;
		 String isfjk = "";
		 String grouplist = "";

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
			if (curCustomer.func.length() >= 2) isfjk = String.valueOf(curCustomer.func.charAt(1));
			grouplist = curCustomer.valstr3;
		}
		
		if (!GlobalInfo.isOnline) { return ; }
		if (GlobalInfo.sysPara.isGroupJSLB != 'N' || !SellType.isGroupbuy(this.saletype))
		{
			//查询商品价随量变信息
			((Bjcx_NetService) NetService.getDefault()).findBatchRule(info, sg.code, sg.gz, sg.uid, goods.str1, sg.catid, sg.ppcode,
																		saleHead.rqsj, cardno, cardtype,isfjk,grouplist,saletype,GlobalInfo.localHttp);
			if (info.Zklist!=null && info.Zklist.trim().length() > 1) sg.name = "B"+sg.name;
		}
	 }
	 
	 
	 class CxRebateDef
	 {
			public String pmbillno;/*促销单号*/
			public String addrule ;/*累计规则 'YYYYY'*/
			public String Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
			public String pmrule;/*价随量变规则*/
			public String etzkmode2;/*1-阶梯折扣 2-统一打折*/
			public String seq; /*规则序号*/
			public double zkfd;
			public String bz;/*阶梯折扣时  Y/N是否循环   统一折扣时  Y/N是否启用上限数量*/
			public double maxnum; /*上限数量*/
			
			public double zsl;
			
			public double sl_cond; // 满足数量条件
			public double zkl_result;//折扣率
			public double cursl; //计算阶梯折扣时，记录当前数量
			
			//供应商 柜组 品牌 类别 商品
			public String gys;
			public String gz;
			public String pp;
			public String catid;
			public String code;
			
			Vector list = new Vector();
	 };
	 
	 public boolean doCmPopWriteData()
		{
			FileOutputStream f = null;

			try
			{
				String name = ConfigClass.LocalDBPath + "/Cmpop.dat";

				f = new FileOutputStream(name);
				ObjectOutputStream s = new ObjectOutputStream(f);

				// 将交易对象写入对象文件
				s.writeObject(saleGoods);
				s.writeObject(goodsAssistant);
				s.writeObject(goodsSpare);
				s.writeObject(goodsCmPop);

				s.flush();
				s.close();
				f.close();
				s = null;
				f = null;

				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();

				return false;
			}
			finally
			{
				try
				{
					if (f != null)
						f.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	 
	 public boolean delCmPopReadData()
		{
			FileInputStream f = null;

			try
			{
				String name = ConfigClass.LocalDBPath + "/Cmpop.dat";
				if (!new File(name).exists())
					return true;

				f = new FileInputStream(name);
				ObjectInputStream s = new ObjectInputStream(f);

				// 读交易对象
				Vector saleGoods1 = (Vector) s.readObject();
				Vector assistant = (Vector)s.readObject();
				Vector spare1 = (Vector) s.readObject();
				Vector goodsCmPop1 = (Vector) s.readObject();
				
		        // 不能更改对象引用，即扫即打时还在引用原对象
				// 赋对象
		        /*
		    	saleGoods = saleGoods1;
		    	goodsSpare = spare1;
		    	goodsCmPop = goodsCmPop1;
		    	*/
		        
		    	saleGoods.clear();
		    	saleGoods.addAll(saleGoods1);
		    	goodsAssistant.clear();
		    	goodsAssistant.addAll(assistant);
		    	goodsSpare.clear();
		    	goodsSpare.addAll(spare1);
		    	if (goodsCmPop != null)goodsCmPop.clear();
		    	if (goodsCmPop != null)goodsCmPop.addAll(goodsCmPop1);
		    	
				// 关闭断点文件
				s.close();
				s = null;
				f.close();
				f = null;

				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();

				return false;
			}
			finally
			{
				try
				{
					if (f != null)
						f.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	 
	 public boolean calcCXRebate()
	 {
		 // 价随量变的促销

		 
		 Vector group = new Vector();
		 // 先进行分组
		 for (int i = 0 ; i < goodsSpare.size(); i++)
		 {
			 SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(i);
			 if (sid.Zklist == null || sid.Zklist.length() <=0 || sid.Zklist.equals("0"))
			 {
				 continue;
			 }
			 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
			 GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(i);
			 int n = 0;
			 for (;n < group.size(); n++)
			 {
				 CxRebateDef cx = (CxRebateDef) group.elementAt(n);
				 String condition  = null;
				 if (sid.Zklist.indexOf("1:") == 0) condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 else condition = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 //？？ cx.seq.equals(sid.seq) 不相同
				 if (cx.addrule.equals(sid.addrule) && cx.Zklist.equals(condition)  && cx.pmbillno.equals(sid.pmbillno) && cx.bz.equals(sid.bz))
				 {
					 boolean cond = true;
					 if (cx.addrule.length() > 0 && cx.addrule.charAt(0) == 'Y')
					 {
						 cond = cond && cx.gys.equals(goodsDef.str1);
					 }
					 
					 if (cx.addrule.length() > 1 && cx.addrule.charAt(1) == 'Y')
					 {
						 cond = cond && cx.gz.equals(goodsDef.gz);
					 }
					 
					 if (cx.addrule.length() > 2 && cx.addrule.charAt(2) == 'Y')
					 {
						 cond = cond && cx.pp.equals(goodsDef.ppcode);
					 }
					 
					 if (cx.addrule.length() > 3 && cx.addrule.charAt(3) == 'Y')
					 {
						 cond = cond && cx.catid.equals(goodsDef.catid);
					 }
					 
					 if (cx.addrule.length() > 4 && cx.addrule.charAt(4) == 'Y')
					 {
						 cond = cond && cx.code.equals(goodsDef.code);
					 }
					 
					 if (cond)
					 {
						 cx.zsl = ManipulatePrecision.doubleConvert(cx.zsl + saleGoodsDef.sl);
						 cx.list.add(String.valueOf(i));
						 break;
					 }
				 }
			 }
			 
			 if (n >=  group.size())
			 {
				 CxRebateDef cx = new CxRebateDef();
				 cx.pmbillno = sid.pmbillno;/*促销单号*/
				 cx.addrule = sid.addrule;/*累计规则 'YYYYY'*/
				 if (sid.Zklist.indexOf("1:") == 0) cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 else cx.Zklist = sid.Zklist;/* 折扣列表 1:X|2:Y|3:Z*/
				 cx.pmrule = sid.pmrule;/*价随量变规则*/
				 cx.etzkmode2 = sid.etzkmode2;/*1-阶梯折扣 2-统一打折*/
				 cx.seq = sid.seq; /*规则序号*/
				 cx.zkfd = Convert.toDouble(sid.zkfd);
				 cx.bz = sid.bz;
				 cx.maxnum = sid.maxnum;
				 
				 cx.zsl = ManipulatePrecision.doubleConvert(saleGoodsDef.sl);
				 cx.list.add(String.valueOf(i));
				 
				 cx.gys = goodsDef.str1;
				 cx.gz  = goodsDef.gz;
				 cx.pp = goodsDef.ppcode;
				 cx.catid = goodsDef.catid;
				 cx.code = goodsDef.code;
				 
				 group.add(cx);
			 }
		 }
		 
		 if (group.size() <=0) return false;
		 //检查促销生效
		 for (int i = 0 ; i < group.size(); i++)
		 {
			 CxRebateDef cx = (CxRebateDef) group.elementAt(i);
			 
			 //new MessageBox("开始检查促销生效信息"+cx.Zklist+" "+i);

			 
			 String zklist = cx.Zklist;
			 String[] zk = zklist.split("\\|");
			 int n = zk.length - 1;
			 for (; n >=0; n--)
			 {
				 String rule = zk[n];
				 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
				 double zkl = Convert.toDouble(rule.substring(rule.indexOf(":")+1));
				 if (cx.zsl < sl)
				 {
					 continue;
				 }
				 else
				 {
					 cx.sl_cond = sl;
					 cx.zkl_result = zkl;
					 break;
				 }
			 }
			 
			 if (n < 0)
			 {
				 group.removeElementAt(i);
				 i--;
			 }
		 }
		 
		 boolean done = false;
		 //开始计算促销
		 for (int i = 0 ; i < group.size(); i++)
		 {
			 CxRebateDef cx = (CxRebateDef) group.elementAt(i);
			 
			 String zkcheck1 = cx.Zklist;
			 String[] zkcheck2 = zkcheck1.split("\\|");
			 if (zkcheck2[0] != null)
			 {
				 double sl1 = Convert.toDouble(zkcheck2[0].substring(0,zkcheck2[0].indexOf(":")));
				 //new MessageBox(String.valueOf(sl1));
				 if (sl1 > 1)
				 {
					 cx.Zklist = "1:1|"+cx.Zklist;
				 }
				 //new MessageBox(cx.Zklist);
			 }
			 
			 for (int x = 0; x < cx.list.size(); x++)
			 {
				 int index = Convert.toInt(cx.list.elementAt(x));
				 SpareInfoDef sid = (SpareInfoDef) goodsSpare.elementAt(index);
				 SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
				 double yhzke = 0;
				 //new MessageBox("计算促销 "+Convert.toInt(cx.etzkmode2)+" "+cx.bz);
				 //  不循环且为阶梯折扣时
				 if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("N"))
				 {
					 yhzke = caculateSL(cx,saleGoodsDef,saleGoodsDef.sl,cx.Zklist.split("\\|"),0); 
				 }
				 else if (Convert.toInt(cx.etzkmode2) == 1 && cx.bz.trim().equals("Y"))
				 {
					 yhzke = caculateSL1(cx,saleGoodsDef,saleGoodsDef.sl,cx.Zklist.split("\\|"),0); 
					 //new MessageBox("done");
				 }
				 else if (Convert.toInt(cx.etzkmode2) == 2 && cx.bz.trim().equals("Y"))
				 {
					 if (cx.cursl + saleGoodsDef.sl > cx.maxnum)
					 {
						 double num1 = ManipulatePrecision.doubleConvert(cx.maxnum - cx.cursl);
						 yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.jg *(num1) * (1 - cx.zkl_result));
						 cx.cursl = cx.maxnum;
					 }
					 else
					 {
						 yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
						 cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl + saleGoodsDef.sl);
					 }
				 }
				 else
				 {
					 yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje * (1 - cx.zkl_result));
				 }
				 
				 if (yhzke >= 0)
				 {
					 
					 saleGoodsDef.hyzke = 0;
					 saleGoodsDef.zszke = 0;
					 saleGoodsDef.lszke = 0;
					 saleGoodsDef.lszre = 0;
					 saleGoodsDef.lszzk = 0;
					 saleGoodsDef.lszzr = 0;
					 saleGoodsDef.yhzke = yhzke;
					 saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
					 getZZK(saleGoodsDef);
					 saleGoodsDef.yhzkfd = cx.zkfd;
					 saleGoodsDef.yhdjbh =cx.pmbillno;
					 if (sid.str1 != null)
					 {
						 StringBuffer buf = new StringBuffer(sid.str1.trim());
						 for (int z =0 ; z < buf.length(); z++)
						 {
							 buf.setCharAt(z, '0');
						 }
						 sid.str1 = buf.toString();
						 saleGoodsDef.isvipzk = 'N';
					 }
					
					 done = true;
				 }
			 }
			 
			 
		 }
		 
		 if (done)
		 {
			 	//重算应收
				calcHeadYsje();

				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
				return true;
		 }
		 return false;
		 
		 
		 
		 
	 }
	 
	 //阶梯且循环
	 public double caculateSL1(CxRebateDef cx,SaleGoodsDef sgd,double use_sl,String[] zk,int index)
	 {
		 double sumzk = 0;
		 for (int i = 0; i < use_sl;i++)
		 {
			 cx.cursl ++;
			 
			 int n = zk.length - 1;
			 for (; n >=0; n-- )
			 {
				 String rule = zk[n];
				 //new MessageBox(rule);
				 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
				 double zkl = Convert.toDouble(rule.substring(rule.indexOf(":")+1));

				 //代表为最后一级，需要循环
				 if (cx.cursl >= sl && n == (zk.length - 1))
				 {
					 cx.cursl = 0;
					 sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1-zkl));
					 break;
				 }
				 
				 if (cx.cursl >= sl)
				 {
					 sumzk = ManipulatePrecision.doubleConvert(sumzk + sgd.jg * (1-zkl));
					 break;
				 }
				 
			 }
		 }
		 
		 return sumzk;
	 }
	 
	 public double caculateSL(CxRebateDef cx,SaleGoodsDef sgd,double use_sl,String[] zk,int index)
	 {
		 if (index >= zk.length) return 0;
		
		 if (index ==0)
		 {
			 int n = 0;
			 for (; n < zk.length; n++ )
			 {
				 String rule = zk[n];
				 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
				 double sl1 = -1;
				 if ((n + 1)< zk.length)
				 {
					 sl1 = Convert.toDouble(zk[n+1].substring(0,zk[n+1].indexOf(":")));
				 }
				
				 if (sl1 < 0)
				 {
					 
					 double use_sl1 = ManipulatePrecision.doubleConvert(cx.cursl +use_sl - sl);
					 if (use_sl1 <=0)
					 {
						 continue; 
					 }
					 use_sl = use_sl1;
					 index = n;
					 break;
				 }
				 else if (cx.cursl >sl && cx.cursl >= sl1)
				 {
					 
					 continue;
				 }
				 else if (cx.cursl >=sl && cx.cursl < sl1)
				 {
					 if (sl1 - cx.cursl <= use_sl) index = n + 1;
					 else index = n;
					 break;
				 }
				 else if (ManipulatePrecision.doubleConvert(cx.cursl +use_sl) >= sl1 && ManipulatePrecision.doubleConvert(cx.cursl +use_sl) > sl )
				 {
					 index = n;
					 break;
					 
				 }
				 else if (ManipulatePrecision.doubleConvert(cx.cursl +use_sl) >= sl )
				 {
					 index = n;
					 break;
				 }
			 }
			 
			 if (n >= zk.length) 
			 {
				 cx.cursl = ManipulatePrecision.doubleConvert(cx.cursl +use_sl);
				 return 0;
			 }
		 }
		 
		 String rule = zk[index];
		 
		 double sl = Convert.toDouble(rule.substring(0,rule.indexOf(":")));
		 double zkl = Convert.toDouble(rule.substring(rule.indexOf(":")+1));
		 
		 boolean done_b = false;
		 if ((index + 1)< zk.length)
		 {
			 double sl1 = Convert.toDouble(zk[index+1].substring(0,zk[index+1].indexOf(":")));
			 if (cx.cursl >= sl1)  done_b  = true;
		 }
		 

		 //证明有下一及
		 if ((index + 1)< zk.length)
		 {
			 String rule1 = zk[index+1];
			 double sl1 = Convert.toDouble(rule1.substring(0,rule1.indexOf(":")));
			 
			 //本级需要计算的数量
			 double x_sl = (cx.cursl+use_sl - sl1);
			 if (x_sl <0) 
			 {
				 cx.cursl =  ManipulatePrecision.doubleConvert(cx.cursl +use_sl);
				 double zkje = ManipulatePrecision.doubleConvert(sgd.jg * (use_sl) * (1 - zkl));
				 return zkje;
			 }
			 else
			 {
				 x_sl = ManipulatePrecision.doubleConvert(sl1 - sl);
			 }
			 double zkje = ManipulatePrecision.doubleConvert(sgd.jg * x_sl * (1 - zkl));
			 
			 //当前已经计算的数量
			 cx.cursl =  ManipulatePrecision.doubleConvert(cx.cursl +x_sl);
			 //当前未计算的数量
			 double y_sl = ManipulatePrecision.doubleConvert(use_sl - x_sl);
			 return zkje + caculateSL(cx,sgd,y_sl,zk,(index+1));
		 }
		 else //没有下一集，用本级计算
		 {
			 double zkje = ManipulatePrecision.doubleConvert(sgd.jg * use_sl * (1 - zkl));
			 return zkje;
		 }
	 }
	 
		public boolean paySellPop()
		{
			//doCmPopWriteData();
			if (calcCXRebate())
			{
				cxRebate = true;
			}
			return super.paySellPop();
		}
		
		public void paySellCancel()
		{
			//delCmPopReadData();
			
			super.paySellCancel();
			
			if (cxRebate)
			{
				cxRebate = false;
				calcHeadYsje();
	
				// 刷新商品列表
				saleEvent.updateTable(getSaleGoodsDisplay());
				saleEvent.setTotalInfo();
			}
		}
		
		
	 

	 
	  
	 
}
