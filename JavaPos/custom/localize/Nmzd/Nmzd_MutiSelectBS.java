package custom.localize.Nmzd;

import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Device.LineDisplay;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.MutiSelectBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.MutiSelectEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Nmzd_MutiSelectBS extends MutiSelectBS
{
	public String text = null;
	public Vector contents = null;
	
	public void initBS(MutiSelectEvent event, MutiSelectForm form, int funcID, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		if (funcID == 301)
		{
			NewKeyListener.sendKey(GlobalVar.Enter);
		}
		//enterBS(event, form, funcID, content, cannotchoice, cannotchoice, cannotchoice, rowindex, cannotchoice, cannotchoice);
	}
	
	public boolean enterBS(MutiSelectEvent event, MutiSelectForm form, int funcID, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		if (funcID == 301)
		{
			PaymentMzk mzk = getSelectPaymentMzk();
			
			if (mzk == null)
			{
				mzk = CreatePayment.getDefault().getPaymentMzk();
				text = "面值卡";
			}
			else
			{
				text = mzk.paymode.name;
			}
			StringBuffer cardno = new StringBuffer();
			String track1, track2, track3;
			
			if (contents == null)
			{
				contents = content;
				if (contents == null) contents = new Vector();
			}
			String cardno1 = null;
			String je1 = null;
			while(true)
			{
				
				String info = "请刷" + text ;
				if (cardno1 != null) info = "上笔卡号"+cardno1+"  余额"+je1;
				// 刷面值卡
				TextBox txt = new TextBox();
				if (!txt.open("请刷" + text, text, info, cardno, 0, 0, false, mzk.getAccountInputMode())) { break; }
		
				ProgressBox progress = null;
		
				try
				{
					progress = new ProgressBox();
					progress.setText("正在查询" + text + "信息，请等待.....");
		
					// 得到磁道信息
					track1 = txt.Track1;
					track2 = txt.Track2;
					track3 = txt.Track3;
		
					// 先发送冲正
					if (!mzk.sendAccountCz()) break;
		
					// 再查询
					if (!mzk.findMzkInfo(track1, track2, track3)) { break;}
		
					// 在客显上显示面值卡号及余额
					LineDisplay.getDefault().displayAt(0,0,Convert.increaseChar(ManipulatePrecision.doubleToString(mzk.mzkret.ye),20));
					LineDisplay.getDefault().displayAt(1,0,Convert.increaseChar(" ",20));
					// LineDisplay.getDefault().displayAt(1, 1,
					// ManipulatePrecision.doubleToString(mzk.mzkret.ye));
		
					//
					progress.close();
					progress = null;
					//检查卡是否已经刷过
					boolean add = false;
					
					for (int i = 0 ; i < contents.size(); i ++)
					{
						String row[] = (String[]) contents.elementAt(i);
						if (mzk.mzkret.cardno.equals(row[0]))
						{
							contents.removeElementAt(i);
							String row1[] = new String[]{mzk.mzkret.cardno, ManipulatePrecision.doubleToString(mzk.mzkret.ye)};
							contents.add(0,row1);
							add = true;
							break;
						}
					}
					
					if (!add)
					{
						String row1[] = new String[]{mzk.mzkret.cardno, ManipulatePrecision.doubleToString(mzk.mzkret.ye)};
						contents.add(0,row1);
					}
					
					//刷新后面的界面
					form.table.exchangeContent(contents);
					if (contents.size() > 0)
					{
						String row1[] = (String[]) contents.elementAt(0);
						cardno1 = row1[0];
						je1 = row1[1];
						form.table.setSelection(0);
					}
					event.content = contents;
					//计算总金额
					double je = 0;
					for (int i = 0 ; i < contents.size(); i ++)
					{
						String row[] = (String[]) contents.elementAt(i);
						je = ManipulatePrecision.doubleConvert(je + Convert.toDouble(row[1]));
					}
					
					
					
					form.label.setText("卡数量:"+contents.size()+"   总余额:"+ManipulatePrecision.doubleToString(je));
				}
				catch (Exception er)
				{
					er.printStackTrace();
					new MessageBox(er.getMessage());
				}
				finally
				{
					if (progress != null) progress.close();
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean exitBS(MutiSelectEvent event, MutiSelectForm form, int funcid, Vector content, boolean modifyvalue, boolean manychoice, boolean textInput, int rowindex, boolean specifyback, boolean cannotchoice)
	{
		if (funcid == 301)
		{
			if(contents != null && contents.size() > 0)
			{
				//计算总金额
				double je = 0;
				for (int i = 0 ; i < contents.size(); i ++)
				{
					String row[] = (String[]) contents.elementAt(i);
					je = ManipulatePrecision.doubleConvert(je + Convert.toDouble(row[1]));
				}
				LineDisplay.getDefault().displayAt(0,0,Convert.increaseChar(ManipulatePrecision.doubleToString(je),20));
				LineDisplay.getDefault().displayAt(1,0,Convert.increaseChar("",20));
			}
		}
		return false;
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
	    		
	    		String[] title = {"面值卡名称" };
	            int[] width = {530};
	            int choice = new MutiSelectForm().open("请选择查询面值卡类型", title, width, v);
	        	
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
					new MessageBox("付款对象 " + classname + " 不存在");
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
