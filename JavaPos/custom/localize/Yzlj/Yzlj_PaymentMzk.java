package custom.localize.Yzlj;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.SalePayDef;

import custom.localize.Bcrm.Bcrm_PaymentMzk;

public class Yzlj_PaymentMzk extends Bcrm_PaymentMzk{
	
	private int intErrPsw = 0;
	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if ((track1 == null || track1.trim().length() <= 0) && 
			(track2 == null || track2.trim().length() <= 0) && 
			(track3 == null || track3.trim().length() <=0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}
		
		// 解析磁道
		String[] s = parseTrack(track1,track2,track3);
		if (s == null) return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];
		
//		根据不同键盘驱动解析磁道
		if(ConfigClass.KeyBoard1.trim().equals("device.KeyBoard.Wincor_KeyBoard")){
				track2 = track2.substring(7);
			}
			//截取磁道号前面的符号
			if(track2.indexOf(";")==0){
				track2 = track2.substring(1);
			}
			
		// 设置请求数据
		setRequestDataByFind(track1,track2,track3);

		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}
		
		//
		return DataService.getDefault().getMzkInfo(mzkreq, mzkret);
	}
	
	public SalePayDef inputPay(String money)
	{
		try
		{
			// 退货小票不能使用,退货扣回按销售算
			if (checkMzkIsBackMoney() && GlobalInfo.sysPara.thmzk != 'Y')
			{
				new MessageBox("退货时不能使用" + paymode.name);
				return null;
			}
			
			// 先检查是否有冲正未发送
			if (!sendAccountCz()) return null;
			
			// 打开明细输入窗口
			new Yzlj_PaymentMzkForm().open(this,saleBS);
			
			// 如果付款成功,则salepay已在窗口中生成
			return salepay;
        }
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public boolean findMzk(String track1, String track2, String track3)
	{
		
		if ((track1 == null || track1.trim().length() <= 0) && 
			(track2 == null || track2.trim().length() <= 0) && 
			(track3 == null || track3.trim().length() <=0))
		{
			new MessageBox("磁道数据为空!");
			return false;
		}
		
		// 解析磁道
		String[] s = parseTrack(track1,track2,track3);
		if (s == null) return false;
		track1 = s[0];
		track2 = s[1];
		track3 = s[2];
		
		
		
//		根据不同键盘驱动解析磁道
		if(ConfigClass.KeyBoard1.trim().equals("device.KeyBoard.Wincor_KeyBoard")){
				track2 = track2.substring(7);
			}
			//截取磁道号前面的符号
			if(track2.indexOf(";")==0){
				track2 = track2.substring(1);
			}
		

		// 设置请求数据
		setRequestDataByFind(track1,track2,track3);
		
		// 设置用户输入密码
		StringBuffer passwd = new StringBuffer();
//		passwd.append("PASSWORD");//非明文显示 
		if (!getPasswdBeforeFindMzk(passwd))
		{
			return false;
		}
		else
		{
			mzkreq.passwd = passwd.toString();
		}
		
		//
		return sendMzkSale(mzkreq,mzkret); 
	}
	
	public boolean sendMzkSale(MzkRequestDef req, MzkResultDef ret)
	{
		if(saleBS != null && saleBS.isRefundStatus())mzkreq.track1 = "#";//调用过程用于作扣回付款标志
		return DataService.getDefault().sendMzkSale(req,ret);
	}
	
	public boolean createSalePay(String money)
	{
		if(super.createSalePay(money)){
			if(!salehead.djlb.equals(SellType.RETAIL_BACK) && !salehead.djlb.equals(SellType.BATCH_BACK)){
				//有效金额大于等于面值卡金额则要一次付清面值卡金额；否则就要用面值卡付清
					/*if(!((salehead.ysje-salehead.sjfk >= mzkret.ye && Double.parseDouble(money) == mzkret.ye) || (salehead.ysje-salehead.sjfk < mzkret.ye && Double.parseDouble(money) == salehead.ysje-salehead.sjfk))){
						new MessageBox("面值卡余额小于应付金额\n,面值卡金额必须一次性付完!");
						salepay = null;
						return false;
					}*/
				
				double syje = ManipulatePrecision.doubleConvert(salehead.ysje - salehead.sjfk-salehead.fk_sysy,1,0);//剩余金额
	
				if((mzkret.ye >= syje && Double.parseDouble(money) < syje) || mzkret.ye <= syje && Double.parseDouble(money) != mzkret.ye){
					new MessageBox("面值卡余额小于应付金额,\n面值卡金额必须一次性付完!");
					salepay = null;
					return false;
				}
			}
			
			if(saleBS.isRefundStatus() && saleBS.refundTotal != 0)
			{
				if((mzkret.ye >= Double.parseDouble(saleBS.getRefundBalanceLabel()) && Double.parseDouble(money) != Double.parseDouble(saleBS.getRefundBalanceLabel())) || (mzkret.ye <= Double.parseDouble(saleBS.getRefundBalanceLabel()) && Double.parseDouble(money) != mzkret.ye)){
					new MessageBox("金额不允许修改!");
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
//	密码输入次数限制验证
	public boolean pswInputValid()
	{
		if (null != mzkret.func && mzkret.func.length()>=5 && Integer.parseInt(mzkret.func.substring(4, 5)) > 0)
		{
			if (Integer.parseInt(mzkret.func.substring(4, 5)) == intErrPsw) 
			{
				new MessageBox("输入验证码错误次数过多，请更换付款方式!");
				//sendMzkSale(mzkreq,mzkret);
				return false;
			}
			else if (Integer.parseInt(mzkret.func.substring(4, 5)) <= intErrPsw) 
			{
				new MessageBox("输入验证码错误次数过多，请更换付款方式!");
				return false;
			}
			
			intErrPsw++; //密码输入的次数
		}
		return true;
	}
	
	public String getDisplayStatusInfo()
	{
		//yinliang test
		//mzkret.func = "Y01Y";
		//mzkret.value3 = 100;
		
		try
		{
			String line = "";
			if (!checkMzkIsBackMoney())
			{
				// 如果卡有回收功能,显示回收提示
				double ye = -1;
				if (isRecycleType())
				{
					// 定义了回收功能键模式
					if (NewKeyListener.searchKeyCode(GlobalVar.MzkRecycle) > 0)
					{
						if (recycleStatus)
						{
							ye = mzkret.ye;
							line = "有效金额:" + ManipulatePrecision.doubleToString(ye) + " 元\n\n可用金额: " + ManipulatePrecision.doubleToString(mzkret.ye)+" 元";
						}
						else
						{
							ye = mzkret.ye - mzkret.value3;
							line = "有效金额:" + ManipulatePrecision.doubleToString(ye) + " 元\n\n可用金额: " + ManipulatePrecision.doubleToString(mzkret.ye)+" 元";
						}
					}
					else
					{
						ye = mzkret.ye;
						line ="有效金额:" + ManipulatePrecision.doubleToString(ye) + " 元\n\n可用金额: " + ManipulatePrecision.doubleToString(mzkret.ye)+" 元";
					}
				}

			}
			else
			{
				if (mzkret.money > 0)
				{
					line = "面值为:" + ManipulatePrecision.doubleToString(mzkret.money) + " 元\n\n退款后卡余额不能大于面值";
				}
				else
				{
					line = "";
				}
			}
			
			// 显示面值卡返回的提示信息
			if (mzkret.str3 != null && mzkret.str3.length() > 0)
			{
				if (line.length() > 0) line += "\n" + mzkret.str3;
				else line += mzkret.str3;
			}
			
			return line;
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return "";
		}
	}
	
	protected String getDisplayAccountInfo()
	{
		return "请 刷 卡";
	}
	
	protected boolean needFindAccount()
	{
		return true;
	}
	
	public void specialDeal (Yzlj_PaymentMzkEvent event)
	{
	}
	
	public void setMoneyVisible(Yzlj_PaymentMzkEvent paymentMzkEvent)
	{
	}
	
	public void setPwdAndYe (Yzlj_PaymentMzkEvent event, KeyEvent e)
	{
    	if (isPasswdInput())
    	{
    		// 显示密码
    		event.yeTips.setText(getPasswdLabel());        		
    		event.yeTxt.setVisible(false);
    		event.pwdTxt.setVisible(true);
    		event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));
    		
        	if (e != null) e.data = "focus";
        	event.pwdTxt.setFocus();
        	event.pwdTxt.selectAll();
    	}
    	else
    	{
            // 显示余额
    		event.yeTips.setText("账户余额");
    		event.yeTxt.setVisible(true);
    		event.pwdTxt.setVisible(false);
    		event.yeTxt.setText(ManipulatePrecision.doubleToString(getAccountYe()));

            // 输入金额
            if (e != null) e.data = "focus";
            event.moneyTxt.setFocus();
            event.moneyTxt.selectAll();
    	}
	}
	
	public boolean recycle()
	{
		// 手工回收标志，需要弹出提示
		if (null != (mzkret.func) && (mzkret.func).length()>=4 && ((mzkret.ye - salepay.ybje < mzkret.value3)))
		{			
			if ("M".equals((mzkret.func).substring(3, 4)))
			{
				new MessageBox("余额低于工本费，此卡可以回收");
				return true;
			}
		}
		
		if (isRecycleType()) 
		{
			// 定义了回收功能键模式
			if (NewKeyListener.searchKeyCode(GlobalVar.MzkRecycle) > 0)
			{
				if (recycleStatus)
				{
					// 设置交易回收标记
					if (mzkret.ye - salepay.ybje > 0 && mzkret.ye - salepay.ybje < mzkret.value3)
					{
						int ret = new MessageBox("卡内余额还有： "+ ManipulatePrecision.doubleToString(mzkret.ye - salepay.ybje)+" 元\n是否回收?",null,true).verify();
						if ( ret == GlobalVar.Key1 || ret == GlobalVar.Enter)
						{
							return true;
						}
						else
						{
							return false;
						}
					
					}
					return true;
				}
				else
				{
					if (mzkret.ye - salepay.ybje < mzkret.value3)
					{
						new MessageBox("卡不回收,只能使用有效金额");
						return false;
					}
				}
			}
			else
			{
				if (mzkret.ye - salepay.ybje < mzkret.value3)
				{
					double balance = mzkret.ye - salepay.ybje;
					if (new MessageBox("消费后余额为：" + ManipulatePrecision.doubleToString(balance) + " 元\n\n卡必须回收才能消费",null,true).verify() != GlobalVar.Key1)
					{
						return false;
					}
					else
					{
						return true;
					}
				}
			}
		}

		return true;
	}
	
	public void doAfterFail(Yzlj_PaymentMzkEvent mzkEvent)
	{
		mzkEvent.shell.close();
		mzkEvent.shell.dispose();
	}
}
