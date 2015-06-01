package custom.localize.Zsbh;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.WithdrawBS;
import com.efuture.javaPos.PrintTemplate.PayinBillMode;
import com.efuture.javaPos.Struct.PayinDetailDef;

public class Zsbh_WithdrawBS extends WithdrawBS
{
	 public void printPayJk()
	    {
	    	try
	    	{
	    		if (hmPayMode == null || phd == null || payListMode == null)
	    		{
	    			new MessageBox("未发现缴款单对象,不能打印");
	    			return ; 
	    		}
	    		
	    		//for (int i = 0; i< GlobalInfo.sysPara.printjknum; i++)
	    		//{
	    			PayinBillMode.getDefault().setTemplateObject(phd, payListMode, hmPayMode);
	        		PayinBillMode.getDefault().printBill();
	    		//}
	    		// 打印
	    		//PayinBillMode.getDefault().setTemplateObject(phd, payListMode, hmPayMode);
	    		//PayinBillMode.getDefault().printBill();
	    	}
	    	catch(Exception ex)
	    	{
	    		new MessageBox("打印时出现异常,不能打印");
	    		ex.printStackTrace();
	    	}
	    }
	
	protected boolean savePayin(String code, String date)
    {
    	ProgressBox pb = null;
    	 
        try
        {
            if (!createPaylistmode(code, date,'1'))
            {
                return false;
            }

            // save
            if (!AccessDayDB.getDefault().writePayin(phd, payListMode))
            {
                new MessageBox("缴款保存本地失败", null, false);
              
                return false;
            }
            
            //成功缴款后,更改当前现金存量
            for (int i = 0;i<payListMode.size();i++)
            {
            	PayinDetailDef pdd = (PayinDetailDef) payListMode.get(i);
            	
            	Object obj = GlobalInfo.localDB.selectOneData("select type from PayinMode where code = '" + pdd.code + "'");
            	
            	if (obj != null && String.valueOf(obj).equals("1"))
            	{
            		GlobalInfo.syjStatus.xjje -= ManipulatePrecision.doubleConvert(pdd.je * pdd.hl,2,1);
            	}
            }
            
            GlobalInfo.dayDB.resultSetClose();
            
            if (GlobalInfo.syjStatus.xjje < 0) GlobalInfo.syjStatus.xjje = 0; 
            
            pb = new ProgressBox();
            
            // send
            pb.setText("正在发送缴款信息,请等待...");
           
            if (NetService.getDefault().sendPayin(phd, payListMode))
            {
                GlobalInfo.dayDB.setSql("update PAYINHEAD set NETBZ = ? where SYJH = ? and SEQNO = ?");
                GlobalInfo.dayDB.paramSetString(1, "Y");
                GlobalInfo.dayDB.paramSetString(2, phd.syjh);
                GlobalInfo.dayDB.paramSetInt(3, phd.seqno);

                GlobalInfo.dayDB.executeSql();
            }
            
            //打印缴款单
            pb.setText("正在打印缴款单,请等待...");
    		//for (int i = 0; i< GlobalInfo.sysPara.printjknum; i++)
    		//{
        		printPayJk();
    		//}
            pb.close();
            
            new MessageBox("本次缴款已完成!", null, false);
            
            return true;
        }
        catch (Exception ex)
        {
        	if (pb != null)
        	{
        		pb.close();
        		pb = null;
        	}
        	  
            ex.printStackTrace();
            return false;
        }
        finally
        {
        	clear();
        }
    }

	public boolean checkReprint()
    {
		return true;
    	/*boolean check = true;
        for (int j = 0; j < payListMode.size(); j++)
        {
        	PayinDetailDef pdd = (PayinDetailDef) payListMode.get(j);

            if ((pdd.zs != 0) && (pdd.je != 0))
            {
            	check = false;
            	break;
            }
        }        
        return check;*/
    }

    //打印
    public void  printPayJk(int code,String syjcode,String querydate)
    {
		//中商百货要求不允许重打缴款单，按打印键后不给任何响应或提示
		//new MessageBox("操作失败，不能进行重打印！");
		return;
    }
    
}
