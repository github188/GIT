package bankpay.Payment;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.ICCard;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentMzk;
import com.efuture.javaPos.Struct.MzkRequestDef;
import com.efuture.javaPos.Struct.MzkResultDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class ICKMoney_PaymentMzk extends PaymentMzk
{
	boolean offlinemode = true;
	double  lastmoney = 0;
	
	public ICKMoney_PaymentMzk()
	{
		super();
	}
	
	public ICKMoney_PaymentMzk(PayModeDef mode, SaleBS sale)
	{
		initPayment(mode,sale);
	}
	
	public ICKMoney_PaymentMzk(SalePayDef pay, SaleHeadDef head)
	{
		initPayment(pay,head);
	}
	
	public boolean cancelPay()
	{
		// 余额即时写入IC卡,总是立即记账
		if (mzkAccount(false))
		{
			deleteMzkCz();
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean realAccountPay()
	{
		// 付款即时记账			
		if (mzkAccount(true))
		{
			deleteMzkCz();
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean collectAccountPay()
	{
		// batch为空表示付款没有记账，进行集中记账
		if (salepay.batch == null || salepay.batch.trim().length() <= 0)
		{
			// 付款记账		
			if (mzkAccount(true))
			{			
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// 已记账,直接返回
			return true;
		}
	}
	
	public boolean findMzkInfo(String track1, String track2, String track3)
	{
		if (offlinemode)
		{
			String cardno = ICCard.getDefault().getICCardNo();
			if (cardno == null || cardno.trim().length() <= 0)
			{
				new MessageBox("没有读到IC卡信息!");
				return false;
			}
			
			// 先同步卡余额,同步失败无法保障数据库余额和IC卡余额匹配则不允许交易
			if (GlobalInfo.isOnline) 
			{
				super.findMzk(track1, track2, track3);
			}
			
			// 离线模式直接从IC卡读取余额
			mzkret.cardno = cardno;
			mzkret.ye = ICCard.getDefault().getICCardMoney();
			
			
			return true;
		}
		else
		{
			return super.findMzkInfo(track1, track2, track3);
		}
	}
	
	public boolean findMzk(String track1, String track2, String track3)
	{
		if (offlinemode)
		{
			String cardno = ICCard.getDefault().getICCardNo();
			if (cardno == null || cardno.trim().length() <= 0)
			{
				new MessageBox("没有读到IC卡信息!");
				return false;
			}
			
			// 先同步卡余额,同步失败无法保障数据库余额和IC卡余额匹配则不允许交易
			if (!GlobalInfo.isOnline || !super.findMzk(track1, track2, track3)) 
			{
				if (new MessageBox("IC卡同步失败，你确定要进行脱机交易吗?",null,true).verify() != GlobalVar.Key1)
				{
					return false;
				}
			}
				
			// 离线模式直接从IC卡读取余额
			mzkret.cardno = ICCard.getDefault().getICCardNo();
			mzkret.ye = ICCard.getDefault().getICCardMoney();
			
			return true;
		}
		else
		{
			return super.findMzk(track1, track2, track3);
		}
	}
	
	public void setRequestDataByFind(String track1, String track2, String track3)
	{
		if (offlinemode)
		{
			super.setRequestDataByFind(track1, track2, track3);
			
			// 告诉后台查询过程磁道信息是存放的是卡号和卡余额,同步数据库余额主档
			mzkreq.track1 = "CARDYE";
			mzkreq.track2 = ICCard.getDefault().getICCardNo();
			mzkreq.track3 = ManipulatePrecision.doubleToString(ICCard.getDefault().getICCardMoney());
		}
		else
		{
			super.setRequestDataByFind(track1, track2, track3);
		}
	}
	
	public boolean mzkAccount(boolean isAccount)
	{
		if (offlinemode)
		{
			// 离线模式先更新IC卡余额，再调用super.mzkAccount记录交易日志并上传
			if (!updateICKData(isAccount)) return false;
			
			// IC卡余额已更新成功,基类记录的冲正作为交易日志，不管日志是否送网成功都应该认为本次付款记账成功,日志留待下次上传
			if (super.mzkAccount(isAccount)) super.deleteMzkCz();

			// 标记记账成功,IC卡内交易后余额记账
			mzkret.cardno = ICCard.getDefault().getICCardNo();
			mzkret.ye = ICCard.getDefault().getICCardMoney();
			mzkret.status = "RETURNYE";
			super.saveAccountMzkResultToSalePay();
			super.mzkAccountFinish(isAccount);
			
			return true;
		}
		else
		{
			// 在线模式先联网记账余额，再通过mzkAccountFinish更新IC卡余额
			if (super.mzkAccount(isAccount))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	public boolean mzkAccountFinish(boolean isAccount)
	{
		if (offlinemode)
		{
			// 离线模式先更新IC卡余额，super.mzkAccount用于上传交易日志
			return true;
		}
		else
		{
			// 在线模式先联网记账余额，再更新IC卡余额
			if (!updateICKData(isAccount)) return false;

			return super.mzkAccountFinish(isAccount);
		}
	}
	
	protected boolean setRequestDataByAccount()
	{
		if (offlinemode)
		{
			if (!super.setRequestDataByAccount()) return false;
			
			// 告诉后台记账过程磁道信息是存放的是卡号和卡余额,在记录交易日志的同时记录交易发生后的余额备查
			mzkreq.track1 = "CARDYE";
			mzkreq.track2 = ICCard.getDefault().getICCardNo();
			mzkreq.track3 = ManipulatePrecision.doubleToString(ICCard.getDefault().getICCardMoney());
			return true;
		}
		else
		{
			return super.setRequestDataByAccount();
		}
	}
	
	public boolean deleteMzkCz()
	{
		if (offlinemode)
		{
			return true;
		}
		else
		{
			return super.deleteMzkCz();
		}
	}
	
	public String GetMzkCzFile()
	{
		if (offlinemode)
		{
			return ConfigClass.LocalDBPath + "/ICK_LOG_" + mzkreq.seqno + ".cz";
		}
		else
		{
			return ConfigClass.LocalDBPath + "/ICK_" + mzkreq.seqno + ".cz";
		}
	}
	
	public boolean isCzFile(String filename)
	{
		if (filename.startsWith("ICK_") && filename.endsWith(".cz"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean sendAccountCzData(MzkRequestDef req,String czfile,String czname)
	{
		if (czname.startsWith("ICK_LOG_") && czname.endsWith(".cz"))
		{
			if (!GlobalInfo.isOnline) return true;
			
			// 文件名为ICK_LOG开头的冲正文件为脱机消费的交易日志,无需转换冲正交易类型直接送网
			// 即使发送失败也留待下次再发送交易日志，如果发送成功则删除日志文件
	        MzkResultDef ret = new MzkResultDef();
			if (sendMzkSale(req,ret)) 
			{
				deleteMzkCz(czfile);
			}
			else
			{
				if (new MessageBox("IC卡交易日志发送失败,是否继续发送其他未送网交易日志？\n\n任意键 - 是 / 2 - 否",null,false).verify() == GlobalVar.Key2)
				{
					return false;
				}
			}
			
			return true;
		}
		else 
		{
			return super.sendAccountCzData(req,czfile,czname);
		}
	}
	
	public boolean updateICKData(boolean isAccount)
	{
		if (ICCard.getDefault() == null)
		{
			new MessageBox("没有定义IC卡读卡设备,不能更新IC卡余额");
			return false;
		}
		
		new MessageBox("请将卡号 ["+salepay.payno+"] 的IC卡\n\n放入读卡器准备写卡");
		
		try
		{
			boolean isminus = true;
			
			if (offlinemode)
			{
				// 如果红冲mzkret卡为空
				if (mzkret.cardno == null || mzkret.cardno.equals("")) 
				{
					String msg = ICCard.getDefault().findCard();
					if (msg.indexOf("error:") >= 0)
					{
						new MessageBox(msg);
						return false;
					}
					
					mzkret.cardno = ICCard.getDefault().getICCardNo();
					mzkret.ye	  = ICCard.getDefault().getICCardMoney();
				}
				
				// 离线模式以IC卡内余额为准进行加减,设置交易类型,isAccount=true是记账,false是撤销
				if (isAccount)
				{
					if (SellType.SELLSIGN(salehead.djlb) > 0) isminus = true;	// 消费,减
					else isminus = false;										// 退货,加
				}
				else
				{
					if (SellType.SELLSIGN(salehead.djlb) > 0) isminus = false;	// 退货,加
					else isminus = true;										// 消费,减
				}	
				
				if (ICCard.getDefault().updateCardMoney(salepay.payno,isminus?"MINUS":"ADDED",salepay.ybje))
				{
					return this.checkICmoney(isminus);
				}
				else
				{
					this.checkICmoney(isminus);
					
					return false;
				}
			}
			else
			{
				// 在线模式以数据库的余额为准，再更新IC卡余额
				if (ICCard.getDefault().updateCardMoney(salepay.payno,"UPDATE",salepay.kye))
				{			
					return this.checkICmoney(isminus);
				}
				else
				{
					this.checkICmoney(isminus);
					
					return false;
				}
			}
		}
		finally
		{

		}
	}
	
	public boolean checkICmoney(boolean isminus)
	{
		String saleinfo = "本次消费 : ";
		
		if (!isminus) saleinfo = "本次退款 : ";
		
		StringBuffer sb = new StringBuffer();
		
		try
		{
			if (offlinemode)
			{	
				double salemoney = (isminus?(mzkret.ye - salepay.ybje):(mzkret.ye + salepay.ybje));
				
				sb.append("卡    号 : " + Convert.appendStringSize("",mzkret.cardno,0,10,10,1) + "\n");
				sb.append("上次结存 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkret.ye),0,10,10,1) + "\n");
				sb.append(saleinfo + Convert.appendStringSize("",ManipulatePrecision.doubleToString(salepay.ybje),0,10,10,1) + "\n");
				sb.append("剩余金额 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(ICCard.getDefault().getICCardMoney()),0,10,10,1));
				
				if (salemoney == ICCard.getDefault().getICCardMoney())
				{
					new MessageBox(sb.toString());
					
					return true;
				}
				else
				{
					sb.append("\n警告:消费前后金额不相等,请确认!");
					new MessageBox(sb.toString());
					
					return false;
				}
			}
			else
			{
				sb.append("卡    号 : " + Convert.appendStringSize("",mzkret.cardno,0,10,10,1) + "\n");
				sb.append(saleinfo + Convert.appendStringSize("",ManipulatePrecision.doubleToString(salepay.ybje),0,10,10,1) + "\n");
				sb.append("剩余金额 : " + Convert.appendStringSize("",ManipulatePrecision.doubleToString(mzkret.ye),0,10,10,1));
				
				if (mzkret.ye == ICCard.getDefault().getICCardMoney())
				{
					new MessageBox(sb.toString());
					
					return true;
				}
				else
				{
					sb.append("\n警告:消费前后金额不相等,请确认!");
					new MessageBox(sb.toString());
					
					return false;
				}
			}
		}
		finally
		{
			if (sb != null)
			{
				sb.delete(0,sb.length());
				sb = null;
			}
		}
	}
	
	public boolean allowMzkOffline()
	{
		return true;
	}
	
	
}
