package com.efuture.javaPos.Logic;

import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.InvoiceInfoDef;

public class SetSaleTicketIdBS 
{
	private MessageBox me = null;
	
	public SetSaleTicketIdBS()
	{
		init();
	}
	
	private void init()
	{
		StringBuffer value = new StringBuffer();
		//获得网上最大小票号
		InvoiceInfoDef inv = new InvoiceInfoDef();
		if (!GlobalInfo.isOnline || !NetService.getDefault().getInvoiceInfo(inv))
		{
			inv.maxinv = GlobalInfo.syjStatus.fphm;
		}
		
		if (inv.maxinv > GlobalInfo.syjStatus.fphm)
		{
			value.append(inv.maxinv);
		}
		else
		{
			value.append(GlobalInfo.syjStatus.fphm);
		}
		
		if (!new TextBox().open(Language.apply("设置小票号"),Language.apply("请输入小票号"),Language.apply("请确定设置的小票号比网上最大小票号大,否则可能造成\n交易不能正常送网,请慎重设置"),value,0,0, false,TextBox.IntegerInput)) return ;
		
		if (value ==  null || value.length() <= 0) return ;
	
		
		if (inv.maxinv > Integer.parseInt(value.toString()))
		{
			me = new MessageBox(Language.apply("新设置的小票号比目前最大小票号小\n\n可能造成小票重号,部分销售不能发送\n\n你确定要设置吗?(1-是/2-否)?"), null, true);
			
			if(me.verify() == GlobalVar.Key1)
			{
				AccessDayDB.getDefault().writeWorkLog("新设置的小票号比目前最大小票号小");
				GlobalInfo.syjStatus.fphm = Integer.parseInt(value.toString());
				
				AccessLocalDB.getDefault().writeSyjStatus();
				
				new MessageBox(Language.apply("新的小票号已经设置成功"), null, false);
			}
		}
		else
		{
			GlobalInfo.syjStatus.fphm = Integer.parseInt(value.toString());
			
			AccessLocalDB.getDefault().writeSyjStatus();
			
			new MessageBox(Language.apply("新的小票号已经设置成功"), null, false);
		}
		
		
	}
}
