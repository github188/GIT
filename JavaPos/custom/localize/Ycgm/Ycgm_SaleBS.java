package custom.localize.Ycgm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.eclipse.swt.widgets.Label;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Test.TwoDimesion;
import com.efuture.javaPos.UI.Design.SalePayForm;

import custom.localize.Cmls.Cmls_SaleBS;

public class Ycgm_SaleBS extends Cmls_SaleBS
{
	
	public boolean memberGrantFinish(CustomerDef cust)
    {
        if (cust.status == null || cust.status.trim().length() <=0 || cust.status.charAt(0) != 'Y')
        {
        	new MessageBox(Language.apply("该顾客卡已失效!"));
        	return false;
        }
        
        // 记录当前顾客卡
        curCustomer = cust;
        
    	// 记录到小票        	
    	saleHead.hykh = cust.code;
    	saleHead.hytype = cust.type;
    	
    	saleHead.str4 = cust.track; //记录会员卡磁道信息
    	saleHead.str5 = cust.str3; //卡号密码
    	
    	saleHead.hymaxdate = cust.maxdate;
    	
    	// 重算所有商品应收
    	for (int i=0;i<saleGoods.size();i++)
    	{
    		calcGoodsYsje(i);
    	}
    	
        // 计算小票应收
        calcHeadYsje();
        
        return true;
    }
	
	//扣回-处理负积分问题
    public boolean doRefundEvent()
    {	
    	if (!SellType.ISBACK(saletype)) return true;
  	
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
	    	
	    	//宜昌国贸退货负积分问题
	    	if (null != this.curCustomer && !"".equals(this.curCustomer.code))
	    	{
	    		if (!sendRefund())    return false;	    		
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
    
    public boolean sendRefund()
    {
    	String line = "";
		String pPosNo = Convert.increaseChar(saleHead.ysyjh, ' ', 10);
		String itemCount = Convert.increaseChar(saleGoods.size() + "", ' ', 4);
		String payCount = Convert.increaseChar(salePayment.size() + "", ' ', 4);
		String billNo =  Convert.increaseChar(GlobalInfo.syjStatus.fphm + "", ' ', 8);
		String pBillNo = Convert.increaseChar(saleHead.yfphm, ' ', 8);
		ManipulateDateTime mdt = new ManipulateDateTime();
		String tranDate = Convert.increaseChar(mdt.getDateByEmpty() + mdt.getTimeByEmpty() + "", ' ', 14);
		String pTranDate = Convert.increaseChar(saleHead.str7, ' ', 14);
		
		String track = "";
		if  (saleHead.str4.length() >= 40)
	    	track = saleHead.str4.substring(0, 40);
	    else
	    	track = Convert.increaseChar(saleHead.str4, ' ', 40);
		
		String password = Convert.increaseChar(saleHead.str5, ' ', 20);
		
		String idNo = Convert.increaseChar("", ' ', 20);
		String cardNo = Convert.increaseChar(saleHead.hykh, ' ', 20);
		String isBack = "1"; //0－销售 1－退货
		String validNo = getRandom();
		String branCode = Convert.increaseChar(ConfigClass.Market, ' ', 20);
		String posNo = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 10);
		String operator = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ',10);
		line = branCode + posNo + pPosNo + billNo + pBillNo+ tranDate + pTranDate + itemCount + payCount + track + password + operator + idNo + cardNo  + isBack +validNo ; 
		
		//商品明细
		String goodsDetail = "";
		for (int i = 0 ; i < saleGoods.size(); i++)
		{
			SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
			String itemCode = Convert.increaseChar(sgd.code, ' ', 20);
			String qty = Convert.increaseChar(sgd.sl + "", ' ', 10);
			String amt = Convert.increaseChar(sgd.hjje + "", ' ',13);
			String netAmt = Convert.increaseChar(sgd.hjje + "", ' ',13);
			int len = Convert.countLength(sgd.yyyh); //汉字占两字节，上传数据要按字节长度计算的
			String shoppeNo = Convert.increaseChar(sgd.yyyh, ' ',10 - len + sgd.yyyh.length());
			String sno = Convert.increaseChar((i + 1) + "", ' ',3);
			String deptCode = Convert.increaseChar(sgd.catid, ' ',20);
			String areaCode = Convert.increaseChar(sgd.gz, ' ',20);
			String vendCode = Convert.increaseChar(" ", ' ',20);
			String brandCode = Convert.increaseChar(" ", ' ',20);
			String property = Convert.increaseChar(" ", ' ',10);
			goodsDetail += itemCode + qty + amt + netAmt + shoppeNo + sno + deptCode + areaCode + vendCode + brandCode + property;
		}
		//付款明细
		String payDetail = "";
		for (int i = 0; i < salePayment.size(); i++)
		{
			SalePayDef spd = (SalePayDef) salePayment.get(i);
			String payType = Convert.increaseChar(spd.paycode, ' ', 4);
			String amt = Convert.increaseChar(spd.je + "", ' ', 13);
			//String payNo = Convert.increaseChar(spd.paycode, ' ', 80);
			String payNo = "1";
			String sno = Convert.increaseChar((i +1) + "", ' ', 3);
			payDetail += payType + amt + payNo + sno;
		}
		line = line + goodsDetail + payDetail; 
		
		String rs = Excute.sendRefund(saleHead,line);
		if (null == rs || "".equals(rs.trim()))
			return false;
		
		//连接超时继续操作
		if ("save".equals(rs) )
		{
			if (GlobalVar.Key1 == new MessageBox("与第三方CRM通讯异常，是否继续操作？", null,true).verify())
			{
				//超时继续操作
				return true;
			}
			else
			{
				return false;
			}
		}

		
		if (rs.length() > 7)
		{
			String r = rs.substring(6,7);
			
			if("1".equals(r))
			{
				new MessageBox("退货可能出现负积分问题，请到服务台处理。");
				
				return false;
			}
		}
		
		return true;		
    }
    
    public void takeBackTicketInfo(SaleHeadDef thsaleHead,Vector thsaleGoods,Vector thsalePayment)
    {
    	super.takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);
		//saleHead.yfphm = thsaleHead.fphm + "";
		//saleHead.ysyjh = thsaleHead.syjh;
		saleHead.str7 = thsaleHead.rqsj.substring(0,10).replace("-", ""); //记录员交易时间
    }
	
	public String getVipInfoLabel()
	{
		if (curCustomer == null) return "";
		else
		{
			return "[" + curCustomer.code + "]" + curCustomer.name;
		}
	}

	
	//提交销售信息的内容
	public boolean sendTransInfo()
	{	
		try{
			
			String line = "";
			//先检查是否有为提交的数据
			File file = new File(ConfigClass.LocalDBPath);
			File[] list = file.listFiles();
	
//			for (int i = 0; i < list.length; i++)
//			{
//					// 读取文件
//					String name = list[i].getName();
//					
//					if (!name.startsWith("SocketMS") || !name.endsWith(".dat"))
//						continue;
//					
//					BufferedReader input = null;
//					input = new BufferedReader(new FileReader(list[i]));
//					line = input.readLine();
//					input.close();
//					String rs = Excute.sendTransInfo(saleHead,line, false);
//					if (!(null == rs || "".equals(rs.trim())))
//					{
//						list[i].delete();
//					}
//					saleHead.str6 = "";
//					
//			}
			
			String itemCount = Convert.increaseChar(saleGoods.size() + "", ' ', 4);
			String payCount = Convert.increaseChar(salePayment.size() + "", ' ', 4);
			String billNo =  Convert.increaseChar(GlobalInfo.syjStatus.fphm + "", ' ', 8);
			ManipulateDateTime mdt = new ManipulateDateTime();
			String tranDate = Convert.increaseChar(mdt.getDateByEmpty() + mdt.getTimeByEmpty() + "", ' ', 14);
			String track = "";
			//当手输入卡号时，轨道信息为空
			if  (saleHead.str4.length() >= 40)
		    	track = saleHead.str4.substring(0, 40);
		    else
		    	track = Convert.increaseChar(saleHead.str4, ' ', 40);
				
			String password = Convert.increaseChar(saleHead.str5, ' ', 20);
			String idNo = Convert.increaseChar("", ' ', 20);
			String cardNo = Convert.increaseChar(saleHead.hykh, ' ', 20);
			String isBack = "0"; //0－销售 1－退货
			
			//销售退货时
			if (SellType.ISBACK(saleHead.djlb))
			{
				isBack = "1";
			}
			
			String validNo = getRandom();
	//		商品明细
			String goodsDetail = "";
			for (int i = 0 ; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				String itemCode = Convert.increaseChar(sgd.code, ' ', 20);
				String qty = Convert.increaseChar(sgd.sl + "", ' ', 10);
				String amt = Convert.increaseChar(sgd.hjje + "", ' ',13);
				String netAmt = Convert.increaseChar(sgd.hjje + "", ' ',13);
				//String shoppeNo = Convert.increaseChar(sgd.yyyh, ' ',10);
				int len = Convert.countLength(sgd.yyyh); //汉字占两字节，上传数据要按字节长度计算的
				String shoppeNo = Convert.increaseChar(sgd.yyyh, ' ',10 - len + sgd.yyyh.length());
				String sno = Convert.increaseChar((i + 1)+ "", ' ',3);
				String deptCode = Convert.increaseChar(sgd.catid, ' ',20);
				String areaCode = Convert.increaseChar(sgd.gz, ' ',20);
				String vendCode = Convert.increaseChar(" ", ' ',20);
				String brandCode = Convert.increaseChar(" ", ' ',20);
				String property = Convert.increaseChar(" ", ' ',10);
				goodsDetail += itemCode + qty + amt + netAmt + shoppeNo + sno + deptCode + areaCode + vendCode + brandCode + property;
			}
			//付款明细
			String payDetail = "";
			for (int i = 0; i < salePayment.size(); i++)
			{
				SalePayDef spd = (SalePayDef) salePayment.get(i);
				String payType = Convert.increaseChar(spd.paycode, ' ', 4);
				String amt = Convert.increaseChar(spd.je + "", ' ', 13);
				
				String addPoint = "0"; //是否参与积分,0 否，1 是
				//根据后台参数定义是否积分
				if (GlobalInfo.sysPara.jfPayCodeList != null && GlobalInfo.sysPara.jfPayCodeList.trim().length()<=0)
				{
					if (GlobalInfo.sysPara.jfPayCodeList.indexOf(payType) >= 0)
						addPoint = "1";
				}
	
				
				String sno = Convert.increaseChar((i +1) +  "", ' ', 3);
				payDetail += payType + amt + addPoint + sno;
			}
			
			String branCode = Convert.increaseChar(ConfigClass.Market, ' ', 20);
			String posNo = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 10);
			String operator = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ',10);
			line = itemCount + payCount +  branCode + posNo + billNo + tranDate + track + password + operator + idNo + cardNo + isBack + validNo ; 
			line = line + goodsDetail + payDetail;
			 
			String rs = Excute.sendTransInfo(saleHead,line, true);
			if (null == rs || "".equals(rs.trim()))
				return false;
			
			if ("save".equals("rs"))
				return true;
			
			saleHead.bcjf = Double.parseDouble(rs.substring(47,60).trim());
			saleHead.ljjf  = Double.parseDouble(rs.substring(60,73).trim());
			String risBack = rs.substring(73, 74);
			int itme = Integer.parseInt(rs.substring(39, 43).trim());
			int pay = Integer.parseInt(rs.substring(43, 47).trim());
				
			//商品明细返回信息
			for(int i = 0; i < itme; i++)
			{
				String rg = rs.substring(78 + i *43 - 1, 78 + (i + 1) * 43 - 1);
			}
			
			//付款明细返回信息
			for(int i = 0; i < pay; i++)
			{
				String rp = rs.substring(78 + 43 * itme + i *20 - 1, 78 + 43 * itme + (i +1) *20 - 1 );
				
			}
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean sendTransInfoCz()
	{
		String rs = Excute.saleOrTransBack();
		if (null == rs || "".equals(rs.trim()))
			return false;
		
		return true;
	}
	
	//提交退货信息
	public boolean returnGoods()
	{		
		try
		{
			String line = "";
			//先检查是否有为提交的数据
			File file = new File(ConfigClass.LocalDBPath);
			File[] list = file.listFiles();
	
//			for (int i = 0; i < list.length; i++)
//			{
//					// 读取文件
//					String name = list[i].getName();
//					
//					if (!name.startsWith("SocketMN") || !name.endsWith(".dat"))
//						continue;
//					
//					BufferedReader input = null;
//					input = new BufferedReader(new FileReader(list[i]));
//					line = input.readLine();
//					input.close();
//					String rs = Excute.returnGoods(saleHead,line);
//					if (!(null == rs || "".equals(rs.trim())))
//					{
//						list[i].delete();
//					}
//					saleHead.str6 = "";
//					
//			}
			
			String pPosNo = Convert.increaseChar(saleHead.ysyjh, ' ', 10);
			String itemCount = Convert.increaseChar(saleGoods.size() + "", ' ', 4);
			String payCount = Convert.increaseChar(salePayment.size() + "", ' ', 4);
			String billNo =  Convert.increaseChar(GlobalInfo.syjStatus.fphm + "", ' ', 8);
			String pBillNo = Convert.increaseChar(saleHead.yfphm, ' ', 8);
			ManipulateDateTime mdt = new ManipulateDateTime();
			String tranDate = Convert.increaseChar(mdt.getDateByEmpty() + mdt.getTimeByEmpty() + "", ' ', 14);
			String pTranDate = Convert.increaseChar(saleHead.str7, ' ', 14);
			
			String track = "";
			if  (saleHead.str4.length() >= 40)
		    	track = saleHead.str4.substring(0, 40);
		    else
		    	track = Convert.increaseChar(saleHead.str4, ' ', 40);
			
			String password = Convert.increaseChar(saleHead.str5, ' ', 20);
			
			String idNo = Convert.increaseChar("", ' ', 20);
			String cardNo = Convert.increaseChar(saleHead.hykh, ' ', 20);
			String isBack = "1"; //0－销售 1－退货
			String validNo = getRandom();
			String branCode = Convert.increaseChar(ConfigClass.Market, ' ', 20);
			String posNo = Convert.increaseChar(GlobalInfo.syjDef.syjh, ' ', 10);
			String operator = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ',10);
			line = branCode + posNo + pPosNo + billNo + pBillNo+ tranDate + pTranDate + itemCount + payCount + track + password + operator + idNo + cardNo  + isBack +validNo ; 
			
			//商品明细
			String goodsDetail = "";
			for (int i = 0 ; i < saleGoods.size(); i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.get(i);
				String itemCode = Convert.increaseChar(sgd.code, ' ', 20);
				String qty = Convert.increaseChar(sgd.sl + "", ' ', 10);
				String amt = Convert.increaseChar(sgd.hjje + "", ' ',13);
				String netAmt = Convert.increaseChar(sgd.hjje + "", ' ',13);
				int len = Convert.countLength(sgd.yyyh); //汉字占两字节，上传数据要按字节长度计算的
				String shoppeNo = Convert.increaseChar(sgd.yyyh, ' ',10 - len + sgd.yyyh.length());
				String sno = Convert.increaseChar((i + 1) + "", ' ',3);
				String deptCode = Convert.increaseChar(sgd.catid, ' ',20);
				String areaCode = Convert.increaseChar(sgd.gz, ' ',20);
				String vendCode = Convert.increaseChar(" ", ' ',20);
				String brandCode = Convert.increaseChar(" ", ' ',20);
				String property = Convert.increaseChar(" ", ' ',10);
				goodsDetail += itemCode + qty + amt + netAmt + shoppeNo + sno + deptCode + areaCode + vendCode + brandCode + property;
			}
			//付款明细
			String payDetail = "";
			for (int i = 0; i < salePayment.size(); i++)
			{
				SalePayDef spd = (SalePayDef) salePayment.get(i);
				String payType = Convert.increaseChar(spd.paycode, ' ', 4);
				String amt = Convert.increaseChar(spd.je + "", ' ', 13);
				//String payNo = Convert.increaseChar(spd.paycode, ' ', 80);
				String payNo = "1";
				String sno = Convert.increaseChar((i +1) + "", ' ', 3);
				payDetail += payType + amt + payNo + sno;
			}
			line = line + goodsDetail + payDetail; 
			
			String rs = Excute.returnGoods(saleHead,line);
			if (null == rs || "".equals(rs.trim()))
				return false;
			
			if ("save".equals("rs"))
				return true;
			
			saleHead.bcjf = Double.parseDouble(rs.substring(47,60).trim());
			saleHead.ljjf  = Double.parseDouble(rs.substring(60,73).trim());
			String risBack = rs.substring(73, 74);
			int itme = Integer.parseInt(rs.substring(39, 43).trim());
			int pay = Integer.parseInt(rs.substring(43, 47).trim());
				
	//		//商品明细返回信息
	//		for(int i = 0; i < itme; i++)
	//		{
	//			String rg = rs.substring(77 + i *144, 78 + (i + 1) * 144);
	//		}
	//		
	//		//付款明细返回信息
	//		for(int i = 0; i < pay; i++)
	//		{
	//			String rp = rs.substring(78 + 144 * itme + i *20, 78 + 144 * itme + (i +1) *20 );
	//			
	//		}
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	//退货冲正
	public boolean returnGoodsCz()
	{
		String rs = Excute.saleOrTransBack();
		if (null == rs || "".equals(rs.trim()))
			return false;
		
		return true;
	}
	
	
	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox(Language.apply("小票附加信息输入失败,不能完成交易!"));
				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在汇总交易数据,请等待....."));
			if (!saleSummary())
			{
				new MessageBox(Language.apply("交易数据汇总失败!"));

				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在校验数据平衡,请等待....."));
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, Language.apply("正在输入客户信息,请等待......"));
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, Language.apply("正在打开钱箱,请等待....."));
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, Language.apply("正在记账付款数据,请等待....."));
				if (!saleCollectAccountPay())
				{
					new MessageBox(Language.apply("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!"));

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}
                
				//当为1时，向同程那边发送数据
				if ("1".equals(Excute.validate) || "3".equals(Excute.validate))
				{
					//宜昌国贸要求每笔交易都要想同程CRM提交销售数据
					//提交失败后，会记录数据，在下一次提交数据时，先提交上笔失败的数据
					if ("".equals(saleHead.ysyjh) || "".equals(saleHead.yfphm) || "".equals(saleHead.str7) )
					{ //退货时，只有在收银机号，小票号，原退货日期信息完整时，才能 走原单退货 ，否则只能走销售退货
						setSaleFinishHint(status, "正在提交销售信息.....");
						if (!sendTransInfo())
						{
							new MessageBox("提交销售信息失败.....");
							
							setSaleFinishHint(status, "正在发送销售冲正数据,请等待.....");
							
							if (!sendTransInfoCz())
							{
								new MessageBox("发送销售冲正数据失败.....");
							}
							
							//return false;
						}	
					}
					else
					{
						setSaleFinishHint(status, "正在提交退货信息.....");
						if (!returnGoods())
						{
							new MessageBox("提交退货信息失败.....");
							
							setSaleFinishHint(status, "正在发送退货冲正数据,请等待.....");
							
							if (returnGoodsCz())
							{
								new MessageBox("发送退货冲正数据失败.....");
							}
							
							//return false;
						}
					}
				}
				
					
				setSaleFinishHint(status, Language.apply("正在写入交易数据,请等待......"));
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox(Language.apply("交易数据写盘失败!"));
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				//删掉保存的Socket冲正文件
				File file = null; 
				try{                
					file = new File(ConfigClass.LocalDBPath +"SocketMS-" + (GlobalInfo.syjStatus.fphm - 1) + ".cz");
					if (file.exists())
					{
						file.delete();
						if (file.exists())
						{
							new MessageBox("删除保存的" + file.getPath() + "冲正文件失败");
						}
						file.renameTo(new File(ConfigClass.LocalDBPath +"DEL_SocketMS-" + (GlobalInfo.syjStatus.fphm - 1) + ".cz"));
					}
					
					file = new File(ConfigClass.LocalDBPath +"SocketMD-" + (GlobalInfo.syjStatus.fphm - 1) + ".cz");
					if (file.exists())
					{
						file.delete();
						if (file.exists())
						{
							new MessageBox("删除保存的" + file.getPath() + "冲正文件失败");
						}
						file.renameTo(new File(ConfigClass.LocalDBPath +"DEL_SocketMD-" + (GlobalInfo.syjStatus.fphm - 1) + ".cz"));
					}
					
					file = new File(ConfigClass.LocalDBPath +"SocketMN-" + (GlobalInfo.syjStatus.fphm - 1) + ".cz");
					if (file.exists())
					{
						file.delete();
						if (file.exists())
						{
							new MessageBox("删除保存的" + file.getPath() + "冲正文件失败");
						}
						file.renameTo(new File(ConfigClass.LocalDBPath +"DEL_SocketMN-" + (GlobalInfo.syjStatus.fphm - 1) + ".cz"));
					}

				}
				catch(Exception e )
				{
					
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, Language.apply("正在清除断点保护数据,请等待......"));
				clearBrokenData();

				//
				setSaleFinishHint(status, Language.apply("正在清除付款冲正数据,请等待......"));
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!"));
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				// 上传当前小票
				setSaleFinishHint(status, Language.apply("正在上传交易小票数据,请等待......"));
				boolean bsend = GlobalInfo.isOnline;
				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment))
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
				}

				// 发送当前收银状态
				setSaleFinishHint(status, Language.apply("正在上传收银机交易汇总,请等待......"));
				DataService.getDefault().sendSyjStatus();

				// 打印小票
				setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
				printSaleBill();
			}
			else
			{
				if (GlobalInfo.sysPara.lxprint == 'Y')
				{
					// 打印小票
					setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
					printSaleBill();
				}

				// 标记本次交易已完成
				saleFinish = true;
			}

			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}

			// 清除本次交易数据
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, Language.apply("正在等待关闭钱箱,请等待......"));
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, Language.apply("本笔交易结束,开始新交易"));

			// 标记本次交易已完成
			saleFinish = true;

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox(Language.apply("完成交易时发生异常:\n\n") + ex.getMessage());

			return saleFinish;
		}
	}

	//生成四位随机数
	public static String getRandom()
	{
		String crcstr = String.valueOf(Math.round(Math.random() * 10000));

		if (crcstr.length() > 3)
		{
			return crcstr.substring(0, 4);
		}
		else
		{
			return Convert.increaseChar(crcstr, '0', 4);
		}

	}
	
//	public void execCustomKey0(boolean keydownonsale)
//	{
//		TwoDimesion.display();		
//	}
}
