package com.efuture.javaPos.Logic;

import java.util.Vector;

import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.RefundMoneyDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.SalePayForm;


// 退货扣回相关业务类
public class SaleBS4Refund extends SaleBS3Modify
{
    public double refundTotal = 0;
    
    public Vector refundAssistant = null;    	
    protected boolean refundFinish = false;
    protected boolean isRefundPayStatus = false;
    public Vector refundlist = null; //用于计算退券交易，保存退券明细。退券交易付款方式必须与明细完全一致才能通过
    
    public SaleBS4Refund()
    {
        super();
    }

    public boolean isRefundStatus()
    {
    	return isRefundPayStatus;
    }
    
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
    
    public void refundMessageBox(String message)
    {
    	new MessageBox(message);
    }
    
    public void cancelRefundEvent()
    {
    	// 删除所有扣回付款
    	deleteAllSaleRefund();

    	// 扣回在付款后进行的模式,删除添加到付款明细的扣回付款
    	if (GlobalInfo.sysPara.refundByPos == 'B')
    	{
    		delRefundFormSalePay();
    	}    	
    }
    
    public void delRefundFormSalePay()
    {
    	// 从付款列表中删除扣回付款
    	for (int i=0;i<salePayment.size();i++)
    	{
    		SalePayDef spd = (SalePayDef) salePayment.elementAt(i);
    		if (spd.flag != '3') continue;

    		// 删除
    		delSalePayObject(i);
    		i--;
    	}
    }
    
    public void addRefundToSalePay()
    {
    	if (refundPayment == null) return;
    	
    	// 将扣回付款加入付款列表
    	for (int i = 0; i < refundPayment.size(); i++)
    	{
    		SalePayDef spd = (SalePayDef) refundPayment.elementAt(i);
    		Payment p = (Payment) refundAssistant.elementAt(i);
    		
            // 扣回付款记负数,flag='3'
    		spd.ybje = Math.abs(spd.ybje) * -1;
    		spd.je   = Math.abs(spd.je) * -1;
    		spd.flag = '3';
            
    		// 加入到付款列表
    		addSalePayObject(spd,p);
    	}
    }
    
    public boolean refundComplete()
    {
    	// 扣回在付款后进行的模式,扣回完成后添加到付款明细
    	if (GlobalInfo.sysPara.refundByPos == 'B')
    	{
    		addRefundToSalePay();
    	}
    	
    	// 完成扣回
    	refundFinish = true;
    	return refundFinish;
    }
    
    public String getRefundLabelByChange()
    {
        SalePayDef paydef = null;
        double khje = 0;

        // 计算已输入的扣回
        if (refundPayment != null)
        {
	        for (int i = 0; i < refundPayment.size(); i++)
	        {
	        	paydef = (SalePayDef) refundPayment.elementAt(i);
	
	        	khje += Math.abs(paydef.je);
	        }
        }
        
        if (khje > 0)
        	return Language.apply("/扣回({0})", new Object[]{ManipulatePrecision.doubleToString(khje)});
        else
        	return "";
    }
    
    public String getRefundPayMoneyLabel()
    {
    	return ManipulatePrecision.doubleToString(refundTotal);
    }
    
    public String getRefundBalanceLabel()
    {
    	return ManipulatePrecision.doubleToString(calcRefundBalance());
    }
    
    public double calcRefundBalance()
    {
        SalePayDef paydef = null;
        boolean done = true;
        //String msg = "需要补齐以下券种:\n";
        //String msg1 = "您多扣回以下券种:\n";
        double je = 0;
    	// 退券交易必须完全匹配
    	if ((SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype)) && SellType.ISBACK(saletype))
    	{
    		for (int i = 0; i < refundlist.size(); i++)
    		{
    			String[] row = (String[]) refundlist.elementAt(i);
    			char type = row[0].charAt(0);
    			double value = Convert.toDouble(row[2]);
    			
    			for (int j = 0 ; j < refundPayment.size(); j ++)
    			{
    				paydef = (SalePayDef) refundPayment.elementAt(j);
    				if (paydef.idno != null && paydef.idno.length() >0 && paydef.idno.charAt(0) == type)
    				{
    					value = value - paydef.ybje;
    					paydef.isused = 'Y';
    				}
    			}
    			
    			if (value > 0)
    			{
    				//msg += row[1]+":"+ManipulatePrecision.doubleToString(value)+"\n";
    				je += value;
    				done = false;
    			}
    			else
    			{
    				//msg1 += row[1]+":"+ManipulatePrecision.doubleToString(value*-1)+"\n";
    			}
    		}
    		
    		//如果金额《=0 判断是否有多余付款，如果存在，及删除
    		if (je <=0)
    		{
    			boolean de = true;
    			for (int j = 0 ; j < refundPayment.size(); j ++)
    			{
    				paydef = (SalePayDef) refundPayment.elementAt(j);
    				if (paydef.isused != 'Y')
    				{
    					delSaleRefundObject(j);
    					j--;
    					de =false;
    				}
    			}
    			
    			if (!de)
    			{
    				String desc = Language.apply("券");
    				if (SellType.ISJFSALE(saletype)) desc = Language.apply("积分");
    				new MessageBox(Language.apply("买{0}扣回存在多余付款方式，系统已经自动删除", new Object[]{desc}));
    			}
    		}
    		
    		return je;
    	}

        double khje = 0;

        // 计算已输入的扣回
        for (int i = 0; i < refundPayment.size(); i++)
        {
        	paydef = (SalePayDef) refundPayment.elementAt(i);

        	khje += paydef.je;
        }

        // 计算扣回余额
        double ye = refundTotal - khje;
        if (ye < 0) ye = 0;
        
        if (!done && ye <=0)
        {
        	//new MessageBox("请按照提示付足扣券金额");
        	return  1;
        }
        
        return ManipulatePrecision.doubleConvert(ye,2,1);
    }
    
    public Vector getSaleRefundDisplay()
    {
        Vector v = new Vector();
        String[] detail = null;
        SalePayDef spd = null;

        for (int i = 0; i < refundPayment.size(); i++)
        {
        	spd   = (SalePayDef) refundPayment.elementAt(i);

            detail    = new String[3];
            detail[0] = "[" + spd.paycode + "]" + spd.payname;
            detail[1] = spd.payno;
            detail[2] = ManipulatePrecision.doubleToString(spd.ybje);
            v.add(detail);
        }
        
        return v;
    }
    
    public Vector getPayModeByRefund(String sjcode)
    {
    	return getPayModeByRefund(sjcode,null,null);
    }
    
    public Vector getPayModeByRefund(String sjcode,StringBuffer index,String code)
    {
        Vector child = new Vector();
        String[] temp = null;
        PayModeDef mode = null;
        int k = -1;
        for (int i = 0; i < GlobalInfo.payMode.size(); i++)
        {
            mode = (PayModeDef) GlobalInfo.payMode.elementAt(i);

            if ((mode.sjcode.trim().equals(sjcode.trim()) || (sjcode.equals("0") && mode.sjcode.trim().equals(mode.code))) && 
            	getRefundNeedPayMode(mode))
            {
            	k++;
            	
            	// 标记code付款方式在vector中的位置
            	if (index != null && code != null && mode.code.compareTo(code) == 0)
            	{
            		index.append(String.valueOf(k));
            	}
            	
            	//
            	if (GlobalInfo.sysPara.salepayDisplayRate == 'Y')
            	{
	                temp    = new String[3];
	                temp[0] = mode.code.trim();
	                temp[1] = mode.name + Language.apply("扣回");
	                temp[2] = ManipulatePrecision.doubleToString(mode.hl,4,1,false);
            	}
            	else
            	{
            		temp    = new String[2];
	                temp[0] = mode.code.trim();
	                temp[1] = mode.name + Language.apply("扣回");
	                if (mode.hl != 1)temp[1] = temp[1]+"<"+ManipulatePrecision.doubleToString(mode.hl,4,1,false)+">";
            	}
                child.add(temp);
            }
        }

        return child;
    }
    
    public boolean getRefundNeedPayMode(PayModeDef paymode)
    {
    	String[] pay = null;
    	if (SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype))
    	{
    		if (GlobalInfo.sysPara.refundCouponPaymode.length() <= 0) return true;
    		
    		pay= GlobalInfo.sysPara.refundCouponPaymode.split(",");
    	}
    	else
    	{
	    	if (GlobalInfo.sysPara.refundPayMode.length() <= 0) return true;
	    	
	    	// 被允许的扣回付款方式
	        pay= GlobalInfo.sysPara.refundPayMode.split(",");
    	}
    	
        for (int i = 0; i < pay.length; i++)
        {
            if (paymode.code.equals(pay[i].trim()) || DataService.getDefault().isChildPayMode(paymode.code, pay[i].trim()))
            {
                return true;
            }
        }

        return false;
    }
    
    public void setRefundMoneyInputDefault(Text txt, PayModeDef paymode)
    {
        if (CreatePayment.getDefault().allowQuickInputMoney(paymode))
        {
            // 一级主付款方式,允许直接输入付款金额
            txt.setEditable(true);

            // 付款覆盖模式,找已有的付款金额
            if (GlobalInfo.sysPara.payover == 'Y')
            {
            	int i = existRefund(paymode.code,"",true);
            	if (i >= 0)
            	{
            		SalePayDef salepay = (SalePayDef) refundPayment.elementAt(i);
            		txt.setText(ManipulatePrecision.doubleToString(salepay.ybje));
            		txt.selectAll();
            		return;
            	}
            }

            // 计算剩余付款
            double needPay = calcRefundBalance();
            if (paymode.hl <= 0) paymode.hl = 1;
            txt.setText(getPayMoneyByPrecision(needPay / paymode.hl,paymode));
            txt.selectAll();
        }
        else
        {
        	// 一级主付款方式,不允许直接输入金额
        	// 二级辅付款方式,允许输入付款代码
        	if (paymode.level <= 1)
        	{
	            txt.setText("");
	            txt.setEditable(false);
        	}
        	else
        	{
	            txt.setText("");
	            txt.setEditable(true);        		
        	}
        }
    }
    
    public void addSaleRefundObject(SalePayDef spay,Payment payobj)
    {
    	// 标记本行付款唯一序号,用于删除对应商品的分摊
        if (spay != null) spay.num5 = salePayUnique++;
        
    	refundPayment.add(spay);
    	refundAssistant.add(payobj);
    }
    
    public void delSaleRefundObject(int index)
    {
    	refundPayment.removeElementAt(index);
    	refundAssistant.removeElementAt(index);
    }
    
    public boolean deleteRefundPay(int index)
    {
        try
        {
            if (index >= 0)
            {
            	// 付款取消交易才能删除已付款
            	Payment p = (Payment) refundAssistant.elementAt(index);
            	if (p.cancelPay())
            	{
            		// 删除已付款的扣回
            		delSaleRefundObject(index);
            		
            		// 重算剩余扣回
            		calcRefundBalance();
            		
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
    
    public void loopInputPay(Payment pay)
    {
    	if (GlobalInfo.sysPara.loopInputPay != null && !GlobalInfo.sysPara.loopInputPay.equals(""))
    	{
			String[] s = GlobalInfo.sysPara.loopInputPay.split(",");
			for (int i = 0 ; i < s.length ; i++)
			{
				if (pay.paymode.code.equals(s[i].trim()))
				{
					GlobalInfo.statusBar.setHelpMessage(Language.apply("【退出键】输入其他付款"));
					
					NewKeyListener.sendKey(GlobalVar.Enter);
					
					break;
				}
			}
    	}
    }
    
    public boolean refundAccount(PayModeDef mode,String money)
    {
        // 创建一个付款方式对象
        Payment pay = CreatePayment.getDefault().createPaymentByPayMode(mode,saleEvent.saleBS);
        if (pay == null) return false;
        
        // 扣回付款时不检查付款的溢余控制,都是允许溢余的
        char oldyy = mode.isyy;
        mode.isyy = 'Y';
        
        // inputPay这个方法根据不同的付款方式进行重写
        SalePayDef sp = pay.inputPay(money);
        
        // 恢复付款溢余控制
        mode.isyy = oldyy;
        
        // 增加到付款集合
        if (sp != null || pay.alreadyAddSalePay)
        {
        	if (sp != null)
        	{
	            // 付款覆盖模式,删除已有的付款
	            if (GlobalInfo.sysPara.payover == 'Y')
	            {
	            	int i = existRefund(sp.paycode,sp.payno,true);
	            	if (i >= 0)
	            	{
	            		// 删除已付款的扣回
	            		delSaleRefundObject(i);
	            	}
	            }
	            
	            // 增加已扣回付款
	            sp.payname += Language.apply("扣回");
	            addSaleRefundObject(sp,pay);
        	}
        	
        	// 计算剩余付款
        	double ye = calcRefundBalance();
        	
        	// 如果是需要循环输入的付款方式,则自动发送ENTER键再次进入付款
        	// 只有在ye>0的时候才循环，否则无效
        	if (ye > 0)loopInputPay(pay);
        	
        	return true;
        }
        
    	return false;
    }
    
    public int existRefund(String code, String account,boolean overmode)
    {
        SalePayDef saledef = null;

        for (int i = 0; i < refundPayment.size(); i++)
        {
            saledef = (SalePayDef) refundPayment.elementAt(i);

            // 查找模式时,必须付款代码和账号相同
            // 覆盖模式时,必须付款代码和账号相同且是直接输入付款金额的付款方式才允许覆盖
            if (saledef.paycode.equals(code) && saledef.payno.trim().equals(account) &&
            	((!overmode) ||
                 ( overmode  && CreatePayment.getDefault().allowQuickInputMoney(DataService.getDefault().searchPayMode(saledef.paycode)))
                ))
            {
        		if (saledef.batch == null || saledef.batch.trim().length() <= 0)
        		{
        			// 未记账,直接返回
        			return i;
        		}
        		else
        		{
        			return -1;
        		}
            }
        }

        return -1;
    }

    public boolean deleteAllSaleRefund()
    {
    	if (refundPayment != null)
    	{
	    	// 删除所有扣回付款
	    	for (int i=0;i<refundPayment.size();i++)
	    	{
	    		if (!deleteRefundPay(i))
	    		{
	    			return false;
	    		}
	    		else
	    		{
	    			i--;
	    		}
	    	}
    	}
    	
        return true;
    }
    
    public boolean exitRefundSell()
    {
    	// 提醒确认
    	if (refundPayment.size() > 0 && new MessageBox(Language.apply("你确定要放弃所有已输入的扣回吗？"), null, true).verify() != GlobalVar.Key1)
        {
    		return false;
        }
    	    	
    	return deleteAllSaleRefund();
    }
    
    public boolean haveRefundPayment()
    {
    	for (int i=0;i<salePayment.size();i++)
    	{
    		SalePayDef spd = (SalePayDef) salePayment.elementAt(i);
    		if (spd.flag == '3') return true;
    	}
    	return false;
    }
    
    public boolean checkKh()
    {
    	boolean done = false;
    	try{
    	if ((SellType.ISCOUPON(saletype) || SellType.ISJFSALE(saletype)) && SellType.ISBACK(saletype))
    	{
    		StringBuffer buff = new StringBuffer();
    		for (int i = 0; i < refundlist.size(); i++)
    		{
    			String[] row = (String[]) refundlist.elementAt(i);
    			buff.append(Convert.appendStringSize("", row[1], 0, 15, 10)+" :"+Convert.increaseCharForward(row[2],10)+"\n");
    		}
    		
    		String desc = Language.apply("券");
    		if (SellType.ISJFSALE(saletype)) desc = Language.apply("积分");
    		buff.append(Language.apply("退{0}交易不能使用议价权", new Object[]{desc}));
    		new MessageBox(buff.toString());
    		return done;
    	}
    	
    	if (GlobalInfo.posLogin.priv.trim().length() > 3 && GlobalInfo.posLogin.priv.trim().charAt(3) == 'Y')
    	{
    		done = true;
    		return done;
    	}
    	// 收银机付款精度截断后进行比较，判断是否需要授权
    	if (getDetailOverFlow(calcRefundBalance(),GlobalInfo.sysPara.refundScale) <= 0 )
    	{
    		done = true;
    		return done;
    	}
    	
    	OperUserDef staff = DataService.getDefault().personGrant(Language.apply("扣回权限授权"));
		if (staff == null) return done;
		
		if (staff.priv.trim().length() >3 && staff.priv.trim().charAt(3) == 'Y')
		{
			done = true;
			return done;
		}
		else
		{
			new MessageBox(Language.apply("此工号没有扣回权限,必须付全扣回金额"));
			return done;
		}
    	}catch(Exception er)
    	{
    		er.printStackTrace();
    		return done;
    	}
    	finally
    	{
    		if (done)
    		{
    			// 检查是否一个付款方式都没有输入
    			if (refundPayment.size() <= 0)
    			{
    				if (new MessageBox(Language.apply("没有输入任何扣回，是否继续"),null,true).verify() == GlobalVar.Key1)
    				{
    					return true;
    				}
    				else
    				{
    					return false;
    				}
    			}
    		}
    	}

    }
}

