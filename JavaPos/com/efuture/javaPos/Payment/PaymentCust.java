package com.efuture.javaPos.Payment;

import java.util.Vector;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;


//会员付款对象
public class PaymentCust extends PaymentMzk
{
	private Vector rulelist;

	private boolean ch;
	
    public PaymentCust()
    {
    }
    
    public SalePayDef inputPay(String money)
    {
    	if (!checkUsed())
    	{
    		return null;
    	}
    	
    	return super.inputPay(money);
    }
    
    public boolean findMzk(String track1, String track2, String track3)
    {
    	if (super.findMzk(track1, track2, track3))
    	{
    		if (saleBS != null)
        	{
        		if (!saleBS.checkCust(mzkret))
        		{
        			return false;
        		}
        	}
    		return true;
    	}
    	
    	return false;
    }

    private boolean checkUsed()
	{
    	if (saleBS != null)
    	{
    		if (!saleBS.checkCust())
    		{
    			return false;
    		}
    	}
		return true;
	}

	public PaymentCust(PayModeDef mode, SaleBS sale)
    {
    	initPayment(mode, sale);
    }

    public PaymentCust(SalePayDef pay, SaleHeadDef head)
    {
    	initPayment(pay, head);
    }

    //	判断是否是会员冲正文件
    public boolean isCzFile(String filename)
    {
        if (filename.startsWith("Cus_") && filename.endsWith(".cz"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String GetMzkCzFile()
    {
        return ConfigClass.LocalDBPath + "/Cus_" + mzkreq.seqno + ".cz";
    }

    public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
    {
        return DataService.getDefault().sendHykSale(req, ret);
    }
    
    public int choicTrackType()
	{
    	mzkTrackType = -1;
    	
    	//如果手输情况下若不让用零钱包，则直接采用刷卡
    	if( GlobalInfo.sysPara.isusecoinbag == 'N')
    		return mzkTrackType;
    	
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 获取自定义的解析规则
		rulelist = bs.showRule();
		if (rulelist != null && rulelist.size() <= 0) rulelist = null;

		if (rulelist != null)
		{
			for (int i = 0; i < rulelist.size(); i++)
			{
				CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);
				
				if (filterDef.ispay == 1 )
				{
					rulelist.removeElementAt(i);
					i--;
				}
			}
		}
		
		// 先选择规则后刷会员卡 ，
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				Vector con = new Vector();
				for (int i = 0; i < rulelist.size(); i++)
				{
					CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);
					
					con.add(new String[] { filterDef.desc });
				}
				
				String[] title = { Language.apply("会员卡类型") };
				int[] width = { 500 };

				int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
				mzkTrackType = choice;
				
				if (choice != -1)
				{
					CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
					rulelist.removeAllElements();
					rulelist.add(rule);
				}
				if (rulelist != null) ch = true;
			}
		}

		return mzkTrackType;
	}
    
	public String[] parseTrack(String track1, String track2, String track3)
	{
		String[] s = new String[3];
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 检查磁道是否和规则相匹配
		if (GlobalInfo.sysPara.unionVIPMode != 'C')
		{
			if (rulelist != null && rulelist.size() > 0)
			{
				rulelist = bs.chkTrack(track1, track2, track3, rulelist,true);
			}

			// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
			if (rulelist != null && rulelist.size() > 1)
			{
				rulelist = bs.chooseRule(rulelist,true);
			}

			// 解析有效规则下的磁道号
			if (rulelist != null && rulelist.size() > 0)
			{
				track2 = bs.getTrackByDefine(track1, track2, track3, rulelist);
			}
			else
			{
				if (ch)
				{
					new MessageBox(Language.apply("刷卡与联名卡规则不匹配，该卡无效"));
					return null;
				}
			}
		}
		

		s[0] = track1;
		s[1] = track2;
		s[2] = track3;

		return s;
	}
}
