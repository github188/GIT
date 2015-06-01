package com.efuture.javaPos.Logic;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class MzkInfoQueryBS
{
	public String text = null;
	
	public MzkInfoQueryBS()
	{
	}

	public void QueryMzkInfo()
	{
		QueryMzkInfo(getSelectPaymentMzk());
	}

	public void QueryMzkInfo(PaymentMzk pay)
	{
		StringBuffer cardno = new StringBuffer();
		String track1, track2, track3;

		// 创建面值卡付款对象
		
		PaymentMzk mzk = null;
		if (pay == null)
		{
			mzk = CreatePayment.getDefault().getPaymentMzk();
			text = Language.apply("面值卡");
		}
		else
		{
			mzk = pay;
			if (text == null || text.equals("")) text = mzk.paymode.name;
		}
		
		if (!mzk.allowMzkOffline() && !GlobalInfo.isOnline)
		{
			new MessageBox(Language.apply("此功能必须联网使用"));
			return;
		}
		
		// 刷面值卡
		TextBox txt = new TextBox();
		if (!txt.open(Language.apply("请刷") + text, text, Language.apply("请将{0}从刷卡槽刷入", new Object[]{text}), cardno, 0, 0, false, mzk.getAccountInputMode())) { return; }

		ProgressBox progress = null;

		try
		{
			progress = new ProgressBox();
			progress.setText(Language.apply("正在查询{0}信息，请等待.....", new Object[]{text}));

			// 得到磁道信息
			track1 = txt.Track1;
			track2 = txt.Track2;
			track3 = txt.Track3;

			// 先发送冲正
			if (!mzk.sendAccountCz()) return;

			// 再查询
			if (!mzk.findMzkInfo(track1, track2, track3)) { return; }

			// 在客显上显示面值卡号及余额
			// LineDisplay.getDefault().displayAt(0, 1,
			// mzk.getDisplayCardno());
			// LineDisplay.getDefault().displayAt(1, 1,
			// ManipulatePrecision.doubleToString(mzk.mzkret.ye));

			//
			progress.close();
			progress = null;

			// 显示卡信息
			mzkDisplayInfo(mzk);

		}
		catch (Exception er)
		{
			er.printStackTrace();
			new MessageBox(er.getMessage());
		}
		finally
		{
			if (progress != null) progress.close();
			
			text = null;
		}
	}

	protected void mzkDisplayInfo(PaymentMzk mzk)
	{
		StringBuffer info = new StringBuffer();

		// 组织提示信息
		info.append(Language.apply("卡  号: ") + Convert.appendStringSize("", mzk.getDisplayCardno(), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("持卡人: ") + Convert.appendStringSize("", mzk.mzkret.cardname, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("卡状态: ") + Convert.appendStringSize("", mzk.mzkret.status, 1, 20, 20, 0) + "\n");
		info.append(Language.apply("面  值: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.money), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("余  额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye), 1, 20, 20, 0) + "\n");

		if (mzk.mzkret.str1 != null && mzk.mzkret.str1.trim().length() > 0)
		{
			info.append(Language.apply("有效期: ") + Convert.appendStringSize("", mzk.mzkret.str1, 1, 20, 20, 0) + "\n");
		}

		if (mzk.isRecycleType(mzk.mzkret.func))
		{
			info.append(Language.apply("工本费: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("有效额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye - mzk.mzkret.value3), 1, 20, 20, 0)
					+ "\n");
		}

		// 弹出显示
		new MessageBox(info.toString());
	}
	
	public PaymentMzk getSelectPaymentMzk()
	{
		try
		{
			if (ConfigClass.CustomPayment != null && ConfigClass.CustomPayment.size() > 0)
	    	{
				String classname = null;
				
				Vector v = new Vector();
				
	    		for (int i=0;i<ConfigClass.CustomPayment.size();i++)
	    		{
	    			String s = (String)ConfigClass.CustomPayment.elementAt(i);
	    			
	    			String[] sp = s.split(",");
	    		
	    			if (sp.length >= 2 && sp[0].endsWith("PaymentMzk"))
	    			{
	    				for (int k =0 ; k < GlobalInfo.payMode.size(); k++)
		            	{
		            		PayModeDef mdf = (PayModeDef) GlobalInfo.payMode.elementAt(k);
		            		
		            		if (!mdf.code.trim().equals(sp[1])) continue;
		            		
		            		v.add(new String[]{mdf.name,sp[0],sp[1]});
		            	}
	    			}
	    		}
	    		
	    		if (v.size() <= 0) return null;
	    		
	    		PayModeDef pmd = null;
	    		if (v.size() == 1) 
	    		{
	    			classname = ((String[])v.elementAt(0))[1]; 
	    			pmd = DataService.getDefault().searchPayMode(((String[])v.elementAt(0))[2]);
	    			
	    			Class cl = CreatePayment.getDefault().payClassName(classname);
	    			PaymentMzk mzk = (PaymentMzk)cl.newInstance();
	    			mzk.initPayment(pmd, null);
	    			return mzk;
	    		}
	    		
	    		String[] title = {Language.apply("面值卡名称") };
	            int[] width = {530};
	            int choice = new MutiSelectForm().open(Language.apply("请选择查询面值卡类型"), title, width, v);
	        	
	            if (choice == -1 ) 
	            {
	            	classname = ((String[])v.elementAt(0))[1]; 
	            	
	            	text = ((String[])v.elementAt(0))[0];
	            	pmd =  DataService.getDefault().searchPayMode(((String[])v.elementAt(0))[2]);
	            }
	            else
	            {
	            	classname = ((String[])v.elementAt(choice))[1]; 
	            	
	            	text = ((String[])v.elementAt(choice))[0];
	            	pmd = DataService.getDefault().searchPayMode(((String[])v.elementAt(choice))[2]);
	            }
	            
	            if (classname == null) return null;
	            
	            Class cl = CreatePayment.getDefault().payClassName(classname);
	            
	            if (cl != null)
	            {
	    			PaymentMzk mzk = (PaymentMzk)cl.newInstance();
	    			mzk.initPayment(pmd, null);
	    			return mzk;
	            }
				else 
				{
					new MessageBox(Language.apply("付款对象{0}不存在", new Object[]{classname}));
				}
	    	}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
    	
    	return null;
	}
}
