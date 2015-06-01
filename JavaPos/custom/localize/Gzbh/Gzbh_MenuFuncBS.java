package custom.localize.Gzbh;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.MenuFuncBS;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.MenuFuncDef;
import com.efuture.javaPos.UI.MenuFuncEvent;

public class Gzbh_MenuFuncBS extends MenuFuncBS
{
    public final static int MN_QMZK	 = 207;							//当日面值卡消费列表
    public final static int MN_CHECKMZK = 506;						//打印面值卡信息
    public final static int MN_CHECKXDXMZK = 509;						//打印新大新面值卡信息
    public final static int MN_REPRINTMZK = 512;						//补打面值卡结算
    public final static int MN_REPRINTXDXMZK = 513;						//补打新大新面值卡结算
    public final static int MN_REPRINTTMXJQ = 514;						//补打条码现金券结算
    
    private void printDzk(String msg, String payCode)
    {
		ProgressBox progress = null;
		String[] mzkinfo = null;
		boolean result = false;
		try
		{
			progress = new ProgressBox();
			progress.setText("正在查询" + msg + "交易信息，请等待.....");
			Gzbh_NetService netservice = (Gzbh_NetService) NetService.getDefault();
			mzkinfo = new String[4];
			result = netservice.getDzkInfo(mzkinfo, payCode);
		}
		finally
		{
			if (progress != null) progress.close();
		}

		if (result && mzkinfo != null && mzkinfo.length > 0)
		{
			try
			{
				progress = new ProgressBox();
				progress.setText("正在打印" + msg + "交易信息，请等待.....");
				Gzbh_SaleBillMode saleBillMode = (Gzbh_SaleBillMode) SaleBillMode.getDefault();
				saleBillMode.printDzkInfo(mzkinfo, payCode);
			}
			finally
			{
				if (progress != null) progress.close();
			}
		}
    }
    
	public void execFuncMenu(MenuFuncDef mfd, MenuFuncEvent mffe)
	{
		System.out.println(mfd.code);
		//打印积分卡消费信息
		if (Integer.parseInt(mfd.code) == MN_CHECKMZK)
		{
			if (new MessageBox("您确定要打印积分卡消费信息吗", null, true).verify() == GlobalVar.Key1)
			{
				printDzk("广百消费卡", "0010");
				printDzk("条码现金券", "0031");
				/*
				ProgressBox progress = null;
				String[] mzkinfo = null;
				boolean result = false;
				try
				{
					progress = new ProgressBox();
					progress.setText("正在查积分卡交易信息，请等待.....");
					Gzbh_NetService netservice = (Gzbh_NetService) NetService.getDefault();
					mzkinfo = new String[4];
					result = netservice.getDzkInfo(mzkinfo, "0010");
				}
				finally
				{
					if (progress != null) progress.close();
				}

				if (result && mzkinfo != null && mzkinfo.length > 0)
				{
					try
					{
						progress = new ProgressBox();
						progress.setText("正在打印分卡交易信息，请等待.....");
						Gzbh_SaleBillMode saleBillMode = (Gzbh_SaleBillMode) SaleBillMode.getDefault();
						saleBillMode.printDzkInfo(mzkinfo, "0010");
					}
					finally
					{
						if (progress != null) progress.close();
					}
				}
				*/
			}
		}
		else if (Integer.parseInt(mfd.code) == MN_CHECKXDXMZK)
		{
			if (new MessageBox("您确定要打印积分卡消费信息吗", null, true).verify() == GlobalVar.Key1)
			{
				ProgressBox progress = null;
				String[] mzkinfo = null;
				boolean result = false;
				try
				{
					progress = new ProgressBox();
					progress.setText("正在查积分卡交易信息，请等待.....");
					Gzbh_NetService netservice = (Gzbh_NetService) NetService.getDefault();
					mzkinfo = new String[4];
					result = netservice.getDzkInfo(mzkinfo, "0021");
				}
				finally
				{
					if (progress != null) progress.close();
				}

				if (result && mzkinfo != null && mzkinfo.length > 0)
				{
					try
					{
						progress = new ProgressBox();
						progress.setText("正在打印分卡交易信息，请等待.....");
						Gzbh_SaleBillMode saleBillMode = (Gzbh_SaleBillMode) SaleBillMode.getDefault();
						saleBillMode.printDzkInfo(mzkinfo, "0021");
					}
					finally
					{
						if (progress != null) progress.close();
					}
				}
			}
		}
		else if (Integer.parseInt(mfd.code) == MN_QMZK)
		{
			new Gzbh_QuerySellMzkForm();
		}
		else if (Integer.parseInt(mfd.code) == MN_REPRINTMZK)
		{
			new Gzbh_RePrintJFkForm();
		}
		else if (Integer.parseInt(mfd.code) == MN_REPRINTXDXMZK)
		{
			new Gzbh_RePrintXdxForm();
		}
		else if (Integer.parseInt(mfd.code) == MN_REPRINTTMXJQ)
		{
			new Gzbh_RePrintTMQForm();
		}
		else
		{
			super.execFuncMenu(mfd, mffe);
		}
	}
}
