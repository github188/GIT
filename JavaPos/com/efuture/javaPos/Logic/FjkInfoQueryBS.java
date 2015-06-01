package com.efuture.javaPos.Logic;

import java.util.ArrayList;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentDzq;
import com.efuture.javaPos.Payment.PaymentDzqFjk;
import com.efuture.javaPos.Payment.PaymentFjk;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.CouponQueryInfoForm;
import com.efuture.javaPos.UI.Design.FjkQueryInfoForm;
import com.efuture.javaPos.UI.Design.MutiSelectForm;


public class FjkInfoQueryBS 
{
	public FjkInfoQueryBS()
	{
		
	}
	
	public void QueryFjkInfo()
	{
		QueryFjkInfo(null);
	}
	
	public void QueryFjkInfo(String[] track)
    {
    	if (!GlobalInfo.isOnline)
    	{
    		new MessageBox(Language.apply("此功能必须联网使用"));
    		return ;
    	}
    	
    	// 根据返券卡对象确定查询模式
    	for (int i = 0; ConfigClass.CustomPayment != null && i < ConfigClass.CustomPayment.size(); i++)
    	{
			String[] s = ((String)ConfigClass.CustomPayment.elementAt(i)).split(",");
			if (s.length < 2) continue;
			if (s[0].endsWith("PaymentCoupon"))
			{
				// 采用PaymentCoupon作为返券付款对象
				if (PaymentCouponQuery(s,track)) return;
    		}
    		else if (s[0].endsWith("PaymentMzk"))
    		{
    			// 采用PaymentMzk作为返券付款对象
    			if (PaymentMzkQuery(s,track)) return;
    		}
    		else if (s[0].endsWith("PaymentDzq"))
    		{
    			// 采用PaymentDzq作为返券付款对象
    			if (PaymentDzqQuery(s,track)) return;
    		}
    		else if (s[0].endsWith("PaymentDzqFjk"))
    		{
    			// 采用PaymentDzqFjk作为返券付款对象
    			if (PaymentDzqFjkQuery(s,track)) return;
    		}
    	}
    	
    	// 采用PaymentFjk作为返券付款对象
    	PaymentFjkQuery(track);
    }
	
	public void PaymentFjkQuery(String[] track)
	{
		StringBuffer cardno = new StringBuffer();
    	String track1,track2,track3;
    	ArrayList fjklist = null;
    	
        // 创建返券卡付款对象
        PaymentFjk fjk = CreatePayment.getDefault().getPaymentFjk();
    	if (track == null)
    	{
    		//选择返券卡的类型以便解析磁道
    		fjk.choicFjkType();
    		
        	//刷面值卡
        	TextBox txt = new TextBox();
            if (!txt.open(Language.apply("请刷返券卡"), Language.apply("返券卡"), Language.apply("请将返券卡从刷卡槽刷入"), cardno, 0, 0,false, fjk.getAccountInputMode()))
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

        	progress.setText(Language.apply("正在查询返券卡信息，请等待....."));
    	
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
	
	public boolean PaymentCouponQuery(String[] pay,String[] track)
	{
		CouponQueryInfoForm window = new CouponQueryInfoForm();
		window.open();
		return true;
	}
	
	public boolean PaymentMzkQuery(String[] pay,String[] track)
	{
		for (int j=1;j<pay.length;j++)
		{
			PayModeDef paymode = DataService.getDefault().searchPayMode(pay[j]);
			if (paymode != null && paymode.type == '5')
			{
				try
				{
					Class cl = CreatePayment.getDefault().payClassName(pay[0]);
					if (cl != null)
					{
						PaymentMzk mzk = (PaymentMzk) cl.newInstance();
						mzk.initPayment(paymode, null);
						
        	    		MzkInfoQueryBS mzkbs = CustomLocalize.getDefault().createMzkInfoQueryBS();
        	    		mzkbs.QueryMzkInfo(mzk);
        	    		return true;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	public boolean PaymentDzqQuery(String[] pay,String[] track)
	{
		PaymentDzq dzq = null;
		for (int j=1;j<pay.length;j++)
		{
			PayModeDef paymode = DataService.getDefault().searchPayMode(pay[j]);
			if (paymode != null && paymode.type == '5')
			{
				try
				{
					Class cl = CreatePayment.getDefault().payClassName(pay[0]);
					if (cl != null)
					{
						dzq = (PaymentDzq) cl.newInstance();
						dzq.initPayment(paymode, null);
						break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		if (dzq == null) return false;
		
		boolean ret = true;
    	StringBuffer cardno = new StringBuffer();
    	String track1,track2,track3;
    	
    	if (track == null)
    	{
    		//选择返券卡的类型以便解析磁道
    		dzq.choicTrackType();
    		
        	//刷面值卡
        	TextBox txt = new TextBox();
            if (!txt.open(Language.apply("请刷电子返券卡"), Language.apply("返券卡"), Language.apply("请将电子返券卡从刷卡槽刷入"), cardno, 0, 0,false, dzq.getAccountInputMode()))
            {
                return ret;
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
        	progress.setText(Language.apply("正在查询电子返券卡信息，请等待....."));
    	
	        // 先发送冲正
	        if (!dzq.sendAccountCz()) return true;
	        
	        // 再查询
	        if (!dzq.findMzkInfo(track1, track2, track3))
	        {
	        	return ret;
	        }

	        //
			progress.close();
			progress = null;
			
			// 券种提示
			String dzqtext = Language.apply("卡号:[")+dzq.mzkret.cardno+"]";
			if (dzq.mzkret.cardname != null && dzq.mzkret.cardname.trim().length() > 0) dzqtext += " " + Language.apply("持卡人:")+ dzq.mzkret.cardname.trim();
			if (dzq.mzkret.status != null && dzq.mzkret.status.trim().length() > 0) dzqtext += " " + Language.apply("状态:")+ dzq.mzkret.status.trim();
			
			//dzq.mzkret.memo = "01,A券,100,2009-12-12,2009-12-12|02,B券,300,2009-12-12,2009-12-12|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11";
			
	        // 显示返券信息 
            Vector dzqyevec = new Vector();
    		String[] dzqyetitle = {Language.apply("券种"),Language.apply("券种名称"),Language.apply("券种余额")};
    		int[] dzqyewidth ={60,150,150};

            Vector dzqpcvec = new Vector();
    		String[] dzqpctitle = {Language.apply("券种"),Language.apply("券种名称"),Language.apply("券种余额"),Language.apply("开始日期"),Language.apply("结束日期"),Language.apply("状态")};
    		int[] dzqpcwidth ={60,150,150,120,120,120};
    		
    		// 分解券种余额
			String[] row = dzq.mzkret.memo.split("\\|");
			for (int i = 0; row != null && i < row.length ; i++)
			{
				String line[] = row[i].split(",");
				if (line.length < 3) continue;
				
				String ksrq = null,jsrq = null,stat = null;
				if (line.length > 3) ksrq = line[3].trim();
				if (line.length > 4) jsrq = line[4].trim();
				if (line.length > 5) stat = line[5].trim();
				
				String[] lines = {line[0].trim(),line[1].trim(),ManipulatePrecision.doubleToString(Convert.toDouble(line[2])),ksrq,jsrq,stat};
				dzqpcvec.add(lines);

				// 汇总同券种余额
				int j = 0;
				for (j=0;j<dzqyevec.size();j++)
				{
					String[] s = (String[])dzqyevec.elementAt(j);
					if (line[0].trim().equals(s[0]))
					{
						s[2] = ManipulatePrecision.doubleToString(Convert.toDouble(s[2]) + Convert.toDouble(line[2].trim()));
						break;
					}
				}
				if (j >= dzqyevec.size())
				{
					String[] s = {line[0].trim(),line[1].trim(),ManipulatePrecision.doubleToString(Convert.toDouble(line[2]))};
					dzqyevec.add(s);
				}
			}
			
    		new MutiSelectForm().open(dzqtext,dzqyetitle, dzqyewidth, dzqyevec, false,
    			780,480,750,120,false,false,-1,false,750,230,dzqpctitle,dzqpcwidth,dzqpcvec,0);
        }
        catch(Exception er)
        {
        	er.printStackTrace();
        	new MessageBox(er.getMessage());
        }
        finally
        {
        	if (progress != null) progress.close();
        }
        
        return ret;
	}

	public boolean PaymentDzqFjkQuery(String[] pay,String[] track)
	{
		PaymentDzqFjk dzqfjk = null;
		for (int j=1;j<pay.length;j++)
		{
			PayModeDef paymode = DataService.getDefault().searchPayMode(pay[j]);
			if (paymode != null && paymode.type == '5')
			{
				try
				{
					Class cl = CreatePayment.getDefault().payClassName(pay[0]);
					if (cl != null)
					{
						dzqfjk = (PaymentDzqFjk) cl.newInstance();
						dzqfjk.initPayment(paymode, null);
						break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		if (dzqfjk == null) return false;
		
		boolean ret = true;
    	StringBuffer cardno = new StringBuffer();
    	String track1,track2,track3;
    	
    	if (track == null)
    	{
    		//选择返券卡的类型以便解析磁道
    		dzqfjk.choicTrackType();
    		
        	//刷面值卡
        	TextBox txt = new TextBox();
            if (!txt.open(Language.apply("请刷电子返券卡"), Language.apply("返券卡"), Language.apply("请将电子返券卡从刷卡槽刷入"), cardno, 0, 0,false, dzqfjk.getAccountInputMode()))
            {
                return ret;
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
        	progress.setText(Language.apply("正在查询电子返券卡信息，请等待....."));
    	
	        // 先发送冲正
	        if (!dzqfjk.sendAccountCz()) return true;
	        
	        // 再查询
	        if (!dzqfjk.findMzkInfo(track1, track2, track3))
	        {
	        	return ret;
	        }

	        //
			progress.close();
			progress = null;
			
			// 券种提示
			String dzqtext = Language.apply("卡号:[")+dzqfjk.mzkret.cardno+"]";
			if (dzqfjk.mzkret.cardname != null && dzqfjk.mzkret.cardname.trim().length() > 0) dzqtext += " " + Language.apply("持卡人:")+ dzqfjk.mzkret.cardname.trim();
			if (dzqfjk.mzkret.status != null && dzqfjk.mzkret.status.trim().length() > 0) dzqtext += "   " + Language.apply("状态:")+ dzqfjk.mzkret.status.trim();
			
			//dzq.mzkret.memo = "01,A券,100,2009-12-12,2009-12-12|02,B券,300,2009-12-12,2009-12-12|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11|02,B券,500,2009-11-11,2009-12-11";
			
	        // 显示返券信息 
            Vector dzqyevec = new Vector();
    		String[] dzqyetitle = {Language.apply("券种"),Language.apply("券种名称"),Language.apply("券种余额")};
    		int[] dzqyewidth ={60,150,150};

            Vector dzqpcvec = new Vector();
    		String[] dzqpctitle = {Language.apply("券种"),Language.apply("券种名称"),Language.apply("券种余额"),Language.apply("开始日期"),Language.apply("结束日期"),Language.apply("状态")};
    		int[] dzqpcwidth ={60,150,150,120,120,120};
    		
    		// 分解券种余额
			String[] row = dzqfjk.mzkret.memo.split("\\|");
			for (int i = 0; row != null && i < row.length ; i++)
			{
				String line[] = row[i].split(",");
				if (line.length < 3) continue;
				
				String ksrq = null,jsrq = null,stat = null;
				if (line.length > 3) ksrq = line[3].trim();
				if (line.length > 4) jsrq = line[4].trim();
				if (line.length > 5) stat = line[5].trim();
				
				String[] lines = {line[0].trim(),line[1].trim(),ManipulatePrecision.doubleToString(Convert.toDouble(line[2])),ksrq,jsrq,stat};
				dzqpcvec.add(lines);

				// 汇总同券种余额
				int j = 0;
				for (j=0;j<dzqyevec.size();j++)
				{
					String[] s = (String[])dzqyevec.elementAt(j);
					if (line[0].trim().equals(s[0]))
					{
						s[2] = ManipulatePrecision.doubleToString(Convert.toDouble(s[2]) + Convert.toDouble(line[2].trim()));
						break;
					}
				}
				if (j >= dzqyevec.size())
				{
					String[] s = {line[0].trim(),line[1].trim(),ManipulatePrecision.doubleToString(Convert.toDouble(line[2]))};
					dzqyevec.add(s);
				}
			}
			
    		new MutiSelectForm().open(dzqtext,dzqyetitle, dzqyewidth, dzqyevec, false,
    			780,480,750,120,false,false,-1,false,750,230,dzqpctitle,dzqpcwidth,dzqpcvec,0);
        }
        catch(Exception er)
        {
        	er.printStackTrace();
        	new MessageBox(er.getMessage());
        }
        finally
        {
        	if (progress != null) progress.close();
        }
        
        return ret;
	}
}
