package custom.localize.Nmzd;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBankCMCC;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;

import custom.localize.Bcrm.Bcrm_DataService;
import custom.localize.Cmls.Cmls_SaleBS;

public class Nmzd_SaleBS extends Cmls_SaleBS
{
	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null) return;

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE) && !saletype.equals(SellType.PREPARE_SALE1))
		{
			goodsDef.hyj = 1;
			return;
		}

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
		if (((Bcrm_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid,
																	saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
			goodsDef.num4 = popDef.num2;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
			goodsDef.num4 = 1;
		}
	}
	
	public String[] rowInfo(SaleGoodsDef goodsDef)
	{
		String[] row = super.rowInfo(goodsDef);
		
		if (!SellType.ISCHECKINPUT(saletype))
		{
			row[4] = ManipulatePrecision.doubleToString(Convert.toDouble(row[4])*SellType.SELLSIGN(saletype),4,1,true);
			row[7] = ManipulatePrecision.doubleToString(Convert.toDouble(row[7])*SellType.SELLSIGN(saletype));
		}
		return row;
	}
	//  返回到正常销售界面
	public void backToSaleStatus()
	{
		if (SellType.ISCOUPON(this.saletype) || SellType.ISEARNEST(this.saletype))
		{
			saletype = SellType.RETAIL_SALE;
		}
		else
		{
			super.backToSaleStatus();
		}
	}
	
	public void initTable(String type)
	{
		if (SellType.isJF(saletype)) saleEvent.table.getColumn(3).setText("开票");
		else 
		{
			saleEvent.table.getColumn(3).setText("单位");
			super.initTable(type);
		}
	}
	public boolean paySellStart()
	{
		if (SellType.isJF(saletype))
		{
			double je = 0;
			for (int i = 0 ; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(i);
				if (sgd.isvipzk == 'Y')je = ManipulatePrecision.add(je, (sgd.hjje-sgd.hjzk));
			}
			if (je > 0) new MessageBox("开票金额："+ManipulatePrecision.doubleToString(je));
		}
		return super.paySellStart();
	}
	
	public boolean inputQuantity(int index)
	{
		if (SellType.isJF(saletype))
		{
			return setKP(index);
		}
		else
		{
			return super.inputQuantity(index);
		}
	}
	
	// 设定是否开票
	public boolean setKP(int index)
	{
		if (index < 0 || index >= saleGoods.size()) return false;
		
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(index);
		
		if (sgd.isvipzk == 'Y')
		{
			sgd.unit = "N";
			sgd.isvipzk = 'N';
			return true;
		}
		
		if (sgd.num1 > 0) 
		{
			sgd.isvipzk = 'Y';
			sgd.unit = "Y";
			return true;
		}
		return false;
	}
	
	public void printSaleBill()
	{
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
		if (isRealTimePrint() && !SellType.isJS(saleHead.djlb))
		{
			SaleBillMode.getDefault(saleHead.djlb).setTemplateObject(saleHead, saleGoods, salePayment);

			// 标记即扫即打结束
			Printer.getDefault().enableRealPrintMode(false);

			// 打印那些即扫即打未打印的商品
			for (int i = 0; i < saleGoods.size(); i++) realTimePrintGoods(null, i);

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
			if (pay != null) pay.printOfflineChargeBill(saleHead.fphm);
		}
	}
	
	public boolean yyhExtendAction(OperUserDef staff)
	{
		if (SellType.isJF(saletype) && SellType.ISBACK(saletype))
		{
			StringBuffer req = new StringBuffer();
			boolean done = new TextBox().open("请输入原缴费单号", "原缴费单号", "请输入原缴费单号", req, 0, 0, false, TextBox.IntegerInput);
			if (done)
			{
				saleHead.str3 = req.toString();
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if (GlobalInfo.sysPara.inputyyyfph == 'Y')
			{
				StringBuffer req = new StringBuffer();
				boolean done = new TextBox().open("请输入现沽单号", "现沽单号", "请根据营业员的单据输入现沽单码", req, 0, 0, false, TextBox.IntegerInput);
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
		
	}
	
	// 通过交易类型判断是否可以是不输入营业员
	// 定金的模式下必须输入营业员
	public boolean saleTypeControl()
	{
		if (SellType.ISEARNEST(this.saletype)) return false;
		return true;
	}
	
	// 当定金交易时，将这个里面传入标志标志为定金交易
	public String convertDzcmScsj(String dzcmscsj,boolean isdzcm)
	{
		String scsj = super.convertDzcmScsj(dzcmscsj, isdzcm);
		// 由于查询商品的过程里面没有
		if (SellType.ISEARNEST(this.saletype)) scsj = "X"+this.saletype;
		return scsj;
	}
	
	public boolean deleteSalePay(int index,boolean isautodel)
	{
		if (isautodel) AccessDayDB.getDefault().writeWorkLog("按 删除 删除付款操作");
		return super.deleteSalePay(index, isautodel);
	}
	
	public boolean deleteAllSalePay()
	{
		// 添加操作日志
		AccessDayDB.getDefault().writeWorkLog("按 退出键 删除付款操作");
		return super.deleteAllSalePay();
	}
	
	// 弹出会员升级消息
	public boolean memberGrant()
	{
		// TODO 自动生成方法存根
		if (super.memberGrant())
		{
			if (curCustomer != null && curCustomer.valstr1 != null && curCustomer.valstr1.length() > 0)
			{
				new MessageBox(curCustomer.valstr1);
			}
			return true;
		}
		return false;
	}
}
