package custom.localize.Cczz;


import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.DisplaySaleTicketBS;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cczz_DisplaySaleTicketBS extends DisplaySaleTicketBS {
    public boolean tips(int flag)
    {
    	return true;
    }
    
    protected boolean setSaleRedQuash()
    {
    	if (SellType.ISCOUPON(salehead.djlb))
    	{
    		new MessageBox("券交易小票不允许红冲，请使用退货交易");
    		return false;
    	}
    	
    	if (SellType.ISJFSALE(salehead.djlb))
    	{
    		new MessageBox("积分交易小票不允许红冲，请使用退货交易");
    		return false;
    	}
    	
    	// 换消需要输入原收银机号和原小票号
    	StringBuffer buff = new StringBuffer();
    	if (((Cczz_DataService)DataService.getDefault()).getHHback(ConfigClass.CashRegisterCode, buff))
    	{
    			new MessageBox("上笔为【换货退货】\n必须先进行【换货销售】才能进行其他操作");
    			return false;
    	}
    	
    	return super.setSaleRedQuash();
    }

//  查询退货小票信息
    public void getBackSaleInfo(String code, Text txtTicketCode,StyledText txtSaleTime, StyledText txtSyy,StyledText txtSaleType,Table tabTicketDeatilInfo, Table tabPay,StyledText txtMemberCardCode,StyledText txtGrantCardCode,StyledText txtShouldInceptMoney,StyledText txtAgioMoney,StyledText txtFactInceptMoney,StyledText txtGiveChangeMoney,StyledText txtSpoilageMoney, Label lblNet,Group group,Text khje)
    {
    	String vyyyh = null;
        String vbarcode = null;
        String vname = null;
        String vpayname = null;
        String vpayno = null;
        ProgressBox pb = null;
        
    	try
    	{	
    		salehead = new SaleHeadDef();
    		salegoods = new Vector();
    		salepay = new Vector();
    		
    		pb = new ProgressBox();
    		pb.setText("开始查找退货小票操作.....");
    		
    		if (! DataService.getDefault().getBackSaleInfo("",code,salehead,salegoods,salepay))
    		{
    			salehead = null;
    			salegoods.clear();
    			salegoods = null;
    			salepay.clear();
    			salepay = null;
    			pb.close();
                pb = null;
    			this.clear(txtSaleTime, txtSyy, txtSaleType, tabTicketDeatilInfo, tabPay, txtMemberCardCode, txtGrantCardCode, txtShouldInceptMoney, txtAgioMoney, txtFactInceptMoney, txtGiveChangeMoney, txtSpoilageMoney, lblNet, group);
    			return ;
    		}
    		
    		 pb.close();
             pb = null;
             
    		if (salehead.syjh != null)
        	{
        		group.setText("收银机:" + salehead.syjh);
        	}
       
        	if (salehead.rqsj != null)
        	{
        		txtSaleTime.setText(salehead.rqsj);
        	}
        
        	if (salehead.syyh != null)
        	{
        		txtSyy.setText(salehead.syyh);
        	}
        
        	if (salehead.djlb != null)
        	{
        		txtSaleType.setText(SellType.getDefault().typeExchange(salehead.djlb,salehead.hhflag,salehead));
        	}
        	
        	if (salehead.hykh != null)
        	{
        		txtMemberCardCode.setText(salehead.hykh);
        	}
        	
        	if (salehead.sqkh != null)
        	{
        		txtGrantCardCode.setText(salehead.sqkh);
        	}
            
        	
            txtShouldInceptMoney.setText(ManipulatePrecision.doubleToString(salehead.ysje));
            txtAgioMoney.setText(ManipulatePrecision.doubleToString(salehead.hjzke));
            txtFactInceptMoney.setText(ManipulatePrecision.doubleToString(salehead.sjfk));
            txtGiveChangeMoney.setText(ManipulatePrecision.doubleToString(salehead.zl));
            txtSpoilageMoney.setText(ManipulatePrecision.doubleToString(salehead.sswr_sysy + salehead.fk_sysy));
            
            
            tabTicketDeatilInfo.removeAll();
            
            for (int i = 0;i < salegoods.size();i++)
            {
            	SaleGoodsDef sgd = (SaleGoodsDef)salegoods.get(i);
            	
            	if (sgd.yyyh != null)
            	{
            		vyyyh = sgd.yyyh; 
            	}
            	else
            	{
            		vyyyh = "";
            	}
            	
            	if (sgd.barcode != null)
            	{
            		vbarcode = sgd.barcode;
            	}
            	else
            	{
            		vbarcode = "";
            	}
            	
            	if (sgd.name != null)
            	{
            		vname = sgd.name;
            	}
            	else
            	{
            		vname = "";
            	}
            	
                String[] saleinfo = {vyyyh,vbarcode,vname,ManipulatePrecision.doubleToString(sgd.jg),ManipulatePrecision.doubleToString(sgd.sl,4,1,true),ManipulatePrecision.doubleToString(sgd.hjzk),ManipulatePrecision.doubleToString(ManipulatePrecision.sub(sgd.hjje,sgd.hjzk))};
                TableItem item = new TableItem(tabTicketDeatilInfo, SWT.NONE);
                item.setText(saleinfo);
            }
            
            tabPay.removeAll();
            double yfzje = 0;
            double hhje = 0;
            for (int i = 0;i < salepay.size();i++)
            {
            	SalePayDef spd = (SalePayDef)salepay.get(i);
            	if (spd.str1 != null && spd.str1.indexOf("CCZZ") == 0)
            	{
            		if (AccessDayDB.getDefault().isBuckleMoney(spd))
            		{
            			hhje += spd.je; 
            		}
            		
                	String[] rows = spd.str1.split(";");
                	
                	if (Convert.toDouble(rows[2]) != 0)
                	{
	                	spd.payname = checkName(spd);
	                	vpayname = spd.payname;
	                	
	                	if (spd.payno != null)
	                	{
	                		vpayno = spd.payno;
	                	}
	                	else
	                	{
	                		vpayno = "";
	                	}
	                	
	            		yfzje += Convert.toDouble(rows[2]);
	                    String[] payinfo = {vpayname,vpayno,ManipulatePrecision.doubleToString(Convert.toDouble(rows[2]))};
	                    TableItem item = new TableItem(tabPay, SWT.NONE);
	                    item.setText(payinfo);
                	}
            	}
            }
            
            if (yfzje != 0)
            {
                String[] payinfo1 = {"--------------------","----------------------------","-------------------"};
                TableItem item1 = new TableItem(tabPay, SWT.NONE);
                item1.setText(payinfo1);
                
                String[] payinfo = {"合计应退金额","",ManipulatePrecision.doubleToString(yfzje)};
                TableItem item = new TableItem(tabPay, SWT.NONE);
                item.setText(payinfo);
            }
            
            khje.setText(ManipulatePrecision.doubleToString(hhje));
            
            shbillno = code;
            
            //显示功能提示
    		GlobalInfo.statusBar.setHelpMessage("'付款键'切换商品付款,'确认键'退货小票");
    	}
    	catch (Exception ex)
    	{
    		salehead = null;
			salegoods.clear();
			salegoods = null;
			salepay.clear();
			salepay = null;
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
    		ex.printStackTrace();
    	}
    	finally
    	{
    		isth = false;
    	}
    }
}
