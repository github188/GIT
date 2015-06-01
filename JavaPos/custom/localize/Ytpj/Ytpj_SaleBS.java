package custom.localize.Ytpj;

import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.BuyInfoForm;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Bstd.Bstd_SaleBS;

public class Ytpj_SaleBS extends Bstd_SaleBS {
	protected BankLogDef bld = null;
	public void takeBackTicketInfo(SaleHeadDef thsaleHead, Vector thsaleGoods,
			Vector thsalePayment) {
		super.takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);
		saleHead.hykh=thsaleHead.hykh;
		saleHead.bcjf=thsaleHead.bcjf;
	}

	public boolean memberGrant() {
		if (curCustomer != null) {
			for (int i = 0; i < this.saleGoods.size(); i++) {
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				if (sgd.yhdjbh.equals("w")) {
					new MessageBox("当前商品存在积分换购,不允许更新会员!");
					return false;
				}
			}
		}

		if (isPreTakeStatus()) {
			new MessageBox(Language.apply("预售提货状态下不允许重新刷卡"));
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0
				&& !isNewUseSpecifyTicketBack(false)) {
			new MessageBox(Language.apply("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡"));
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;
		if (track2.length() > 16 || track2.contains("=")) {
			String[] s = track2.split("=");
			track2 = s[0];
		}
		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null)
			return false;
		saleHead.str6= track2;

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false)) {
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			return true;
		} else {
			// 记录会员卡
			return memberGrantFinish(cust);
		}

	}

	
	public boolean doRefundEvent()
    {	
    	if (!SellType.ISBACK(saletype)) return true;
    	
    	if (saleHead.hykh.length()>0) {
			boolean b = false;
			while ((saleHead.str6 == null || saleHead.str6.trim().equals(""))) {
				// 读取会员卡
				HykInfoQueryBS bs = CustomLocalize.getDefault()
						.createHykInfoQueryBS();
				String track2 = bs.readMemberCard();
				if (track2 == null || track2.equals(""))
					return false;
				if (track2.length() > 16 || track2.contains("=")) {
					String[] s = track2.split("=");
					track2 = s[0];
				}
				CustomerDef cust = bs.findMemberCard(track2);

				if (cust == null)
					track2="";
				saleHead.str6= track2;
				if (saleHead.str6 == null || saleHead.str6.trim().equals("")) {
					if (new MessageBox("本笔小票有大会员刷卡,必须刷正确的大会员卡进行退货!\n是否继续刷大会员？",
							null, true).verify() != GlobalVar.Key2) {
						continue;
					} else {
						PosLog.getLog(getClass()).info(
								"小票号:" + saleHead.fphm + " 收银员选择不刷大会员卡");
						saleHead.str6 = "";
						b = true;
						break;
					}
				}
			}
			if (b)
				return false;
		}
  	
    	if (GlobalInfo.sysPara.refundByPos == 'N') return true;
    	
    	if (!GlobalInfo.isOnline)
    	{
    		if (isNewUseSpecifyTicketBack())
    		{
	    		new MessageBox(Language.apply("必须在联网状态下检查退货扣回！"));
	    		return false;
    		}
    		else
    		{
    			return true;
    		}
    	}
    	
    	//isRefundPayStatus = true;
    	//String ss = null;
    	//if (ss.equals("AA")) return true;
    	
    	// 清除扣回付款集合
    	if (refundPayment == null) refundPayment = new Vector();
    	else refundPayment.clear();
    	if (refundAssistant == null) refundAssistant = new Vector();
    	else refundAssistant.clear();

    	// 获取需要扣回的金额 
    	ProgressBox pb = new ProgressBox();
    	char bc = saleHead.bc;
    	try
    	{
    		saleHead.bc = '#';
	    	// 发送当前退货小票到后台数据库
    		pb.setText(Language.apply("正在发送退货小票用于计算扣回金额......"));
	        if (!this.saleEvent.saleBS.saleSummary())
	        {
	            new MessageBox(Language.apply("交易数据汇总失败!"));
	        	
	        	return false;
	        }
	        if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
	        {
	            new MessageBox(Language.apply("交易数据校验错误!"));
	
	            return false;
	        }
	        
	        // 发送当前退货小票以计算扣回
        	// jdfhdd标记当前发送的是用于计算扣回的小票信息
        	String oldfhdd = saleHead.jdfhdd;
        	saleHead.jdfhdd = "KHINV";	        
	        if (GlobalInfo.sysPara.refundByPos == 'B')
	        {
		    	if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, salePayment, null) != 0)
				{
		    		saleHead.jdfhdd = oldfhdd;
		    		return false;
				}
	        }
	        else
	        {
	        	// = 'Y',扣回在付款前进行处理，生成缺省付款便于发送小票
	        	Vector tempPay = new Vector();
	        	SalePayDef tempsp = new SalePayDef();
	        	tempsp.syjh = saleHead.syjh;				
	        	tempsp.fphm = saleHead.fphm;	
	        	tempsp.rowno= 1;
	        	tempsp.flag = '1';
	        	tempsp.paycode = "KHFK";
	        	tempsp.payname = "扣回虚拟付款";
	        	tempsp.ybje = saleHead.ysje;
	        	tempsp.hl = 1;
	        	tempsp.je = saleHead.ysje;
	        	tempPay.add(tempsp);
	        	if (DataService.getDefault().doRefundExtendSaleData(saleHead, saleGoods, tempPay, null) != 0)
				{
		    		saleHead.jdfhdd = oldfhdd;
		    		return false;
				}
	        }
	        
	        
	        saleHead.jdfhdd = oldfhdd;
	        
	    	// 调用后台过程返回需要扣回的金额
	    	pb.setText(Language.apply("正在获取退货小票的扣回金额......"));
	    	RefundMoneyDef rmd = new RefundMoneyDef();
	    	if (!NetService.getDefault().getRefundMoney(saleHead.mkt,saleHead.syjh,saleHead.fphm,rmd))
			{
	    		return false;
			}
	    	
	    	// 关闭提示
    		if (pb != null)
    		{
	    		pb.close();
	    		pb = null;
    		}
    		
    		// 存在家电下乡返款扣回，不允许退货
    		if (rmd.jdxxfkje > 0) 
    		{
    			new MessageBox(Language.apply("该退货小票存在家电下乡返款\n请退返款之后再进行退货交易"));
    			return false;
    		}
    		
	    	// 无扣回金额,不用输入
	    	refundTotal = rmd.jfkhje + rmd.fqkhje + rmd.qtkhje;
	    	
	    	// 员工缴费和结算单如果存在扣回，不允许通过
	    	if ((SellType.isJF(saletype) || SellType.isJS(saletype)) && Math.abs(refundTotal) > 0)
	    	{
	    		new MessageBox(Language.apply("员工缴费 或 结算单 不允许存在扣回\n"));
	    		return false;
	    	}
	    	
	    	//liwj test
	    	/*refundTotal = 1;*/
	    	if (refundTotal <= 0) return true;
	    	
	    	StringBuffer s = new StringBuffer();
    		s.append(Language.apply("该退货小票总共需要扣{0}元\n\n",new Object[]{ ManipulatePrecision.doubleToString(refundTotal)}));
	    	if ((SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype)) && SellType.ISBACK(saletype))
	    	{
	    		if (refundlist == null ) refundlist = new Vector();
	    		else refundlist.removeAllElements();
	    		
	    		String[] rows = rmd.qtdesc.split("\\|");
	    		for (int i = 0 ; i < rows.length; i++)
	    		{
	    			String row[] = rows[i].split(",");
	    			refundlist.add(row);
	    			s.append(Convert.appendStringSize("", row[1], 0, 15, 10)+" :"+Convert.increaseCharForward(row[2],10)+"\n");
	    		}
	    	}
	    	else {
		    	if (rmd.jfdesc.length() > 0) s.append(rmd.jfdesc + "\n");
		    	else if (rmd.jfkhje > 0) s.append(Language.apply("其中因为积分原因需扣回{0}元\n", new Object[]{ManipulatePrecision.doubleToString(rmd.jfkhje)}));
		    	if (rmd.fqdesc.length() > 0) s.append(rmd.fqdesc + "\n");
		    	else if (rmd.fqkhje > 0) s.append(Language.apply("其中因为返券原因需扣回{0} 元\n", new Object[]{ManipulatePrecision.doubleToString(rmd.fqkhje)}));
		    	if (rmd.qtdesc.length() > 0) s.append(rmd.qtdesc + "\n");
		    	else if (rmd.qtkhje > 0) s.append(Language.apply("其中因为其他原因需扣回{0}元\n", new  Object[]{ManipulatePrecision.doubleToString(rmd.qtkhje)}));
	    	}
	    	// 有扣回不允许退货
	    	if (GlobalInfo.sysPara.refundAllowBack != 'Y' && refundTotal > 0)
	    	{
	    		s.append(Language.apply("\n扣回金额大于0,不能进行退货\n"));
	    		refundMessageBox(s.toString());
	    		
	    		return false;
	    	}
	    	
	    	refundMessageBox(s.toString());
    	}
    	catch(Exception er)
    	{
    		er.printStackTrace();
    	}
    	finally
    	{
    		saleHead.bc = bc;
    		if (pb != null)
    		{
	    		pb.close();
	    		pb = null;
    		}
    	}
    	
    	// 标记扣回开始
    	refundFinish = false;
    	isRefundPayStatus = true;
    	
    	// 打开扣回付款输入窗口
    	new SalePayForm().open(saleEvent.saleBS,true);
    	

    	
    	isRefundPayStatus = false;
	    return refundFinish;
    }
	
}