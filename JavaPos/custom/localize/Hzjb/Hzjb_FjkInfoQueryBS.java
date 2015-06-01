package custom.localize.Hzjb;

import java.util.ArrayList;

import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Logic.FjkInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.UI.Design.FjkQueryInfoForm;

public class Hzjb_FjkInfoQueryBS extends FjkInfoQueryBS
{
	public void PaymentFjkQuery(String[] track)
	{
		StringBuffer cardno = new StringBuffer();
    	String track1,track2,track3;
    	ArrayList fjklist = null;
    	
        // 创建返券卡付款对象
        PaymentFjk fjk = CreatePayment.getDefault().getPaymentFjk();
        fjk.paymode = DataService.getDefault().searchPayMode("0530");
    	if (track == null)
    	{
    		//选择返券卡的类型以便解析磁道
    		fjk.choicFjkType();
    		
        	//刷面值卡
        	TextBox txt = new TextBox();
            if (!txt.open("请刷返券卡", "返券卡", "请将返券卡从刷卡槽刷入", cardno, 0, 0,false, fjk.getAccountInputMode()))
            {
                return;
            }
    		
	        // 得到磁道信息
	        track1 = txt.Track1;
	        track2 = txt.Track2;
	        track3 = txt.Track3;
    	}
    	else
    	{
	        // 得到磁道信息
	        track1 = track[0];
	        track2 = track[1];
	        track3 = track[2];
    	}
        
        ProgressBox progress = null;
        try
        {
        	progress = new ProgressBox();

        	progress.setText("正在查询返券卡信息，请等待.....");
    	
	        //先发送冲正
	        if (!fjk.sendAccountCz()) return;
	    
	        //再查询
	        fjklist = new ArrayList();
	        if (!fjk.findFjkInfo(track1,track2,track3,fjklist)) return;
	        
	        //查询当前余额
	        if (!fjk.findFjk(track1,track2,track3)) return;
	        
	        // 关闭
			progress.close();
			progress = null;

			// 无结果
			if (fjklist.size() < 1) return ;
			
			// 显示窗口
			new FjkQueryInfoForm (fjklist,fjk.getCardName(),fjk.getAccountYeA(),fjk.getAccountYeB(),fjk.getAccountYeF());
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	if (progress != null) progress.close();
        	
        	if (fjklist != null)
        	{
        		fjklist.clear();
        		fjklist = null;
        	}
        }
	}
}
