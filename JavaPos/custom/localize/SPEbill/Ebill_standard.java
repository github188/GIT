package custom.localize.SPEbill;

import java.util.Vector;

import org.eclipse.swt.events.KeyEvent;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Plugin.EBill.AndroidService;
import com.efuture.javaPos.Plugin.EBill.CczzService;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.UI.SaleEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Ebill_standard extends EBill
{
	// 获取单据网络清单
	public boolean getSaleBill(SaleBS saleBS)
	{
		if (!Service.inithttp()) return false;
		
		StringBuffer buff=new StringBuffer();
		StringBuffer type = new StringBuffer();
		StringBuffer name = new StringBuffer();
		
		String code="";
		
		Vector typeVec = new Vector();
		StringBuffer sb = new StringBuffer();
		boolean done = false;
		try
		{
			StringBuffer thcode = new StringBuffer();
			TextBox txt = new TextBox();
			if (txt.open("请输入提货单号","提货单号输入", "提示:请输入提货单号",thcode, TextBox.MsrKeyInput, -1));
    		{
    			//done = true;
    			code = thcode.toString();
    			if (code.trim().length() <=0)
    			{
    				code = txt.Track2;
    			}
    		}	
	
    		new MessageBox(code);
    		//if (done!) return false;
			//根据序号得到商品
			Vector v1 = new Vector();
			done = Service.getSaleGoods(code,"1",v1);
			
			if(done)
			{
					if(v1.size() <= 0)
					{
						new MessageBox("没有找到单据！");
						//saleBS.curCustomer = null;
						return false;
					}
					
					for (int i = 0; i < v1.size(); i++)
					{
						SaleGoodsDef sg=(SaleGoodsDef)v1.elementAt(i);
						
						StringBuffer buff1 = new StringBuffer();
						buff1.append(sg.sl);
						GoodsDef gdf= saleBS.findGoodsInfo(sg.code, sg.yyyh, sg.gz,"",false,buff1,true);
						if (gdf == null)
						{
							return false;
						}
						else
						{
							SaleGoodsDef sgd=saleBS.goodsDef2SaleGoods(gdf, sg.yyyh, sg.sl, sg.jg, 0, false);
							
							sgd.syjh = GlobalInfo.syjDef.syjh;
							sgd.barcode=sg.barcode;
							sgd.code=sg.code;
							sgd.yyyh=sg.yyyh;
							sgd.name=sg.name;
							sgd.gz=sg.gz;
							sgd.unit=sg.unit;
							sgd.memo=sg.memo;
							sgd.jg=sg.jg;
							sgd.sl=sg.sl;
							sgd.hjje=sg.hjje;
							sgd.hjzk=sg.hjzk;
							sgd.yhzke=sg.yhzke;
							sgd.lszke=sg.lszke;
							sgd.hyzke=sg.hyzke;
							sgd.qtzke=sg.qtzke;
							sgd.str7=sg.str7;
							sgd.str9=code;
							sgd.fph = "#"+code;
							// 重算折扣
							saleBS.getZZK(sgd);
							
							
							saleBS.addSaleGoodsObject(sgd, gdf, saleBS.getGoodsSpareInfo(gdf, sgd));
					
					}
			
				}
			}
			
			saleBS.calcHeadYsje();
			saleBS.refreshSaleForm();
			
			return true;
		}catch(Exception er)
		{
			er.printStackTrace();
			return false;
		}

	}
	
	// 获取到单据，当按取消整单时，整单如何操作
	public boolean clearSaleBill()
	{
		return false;
	}
	
	//查询退货小票清单
	public boolean getBackSaleBill(SaleBS saleBS)
	{
		return false;
	}
	
	//查看退货选中的商品
	public int getChoice(Vector choice)
	{
		try{
			for (int i = 0 ; i < backVec.size();i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) backVec.elementAt(i);
				String[] row = (String[]) choice.elementAt(sgd.yrowno);
				row[6] = "Y";
				row[7] = String.valueOf(sgd.sl);
			}
			return 0;
		}
		catch(Exception er)
		{
			er.printStackTrace();
			return -1;
		}
		
	}
	
	public boolean isBack()
	{
		return false;
	}
	
	// 键盘输入时，判断是否允许修改单据
	public boolean isEditBillFlag()
	{
		return false;
	}
	
	//查看EBILL是否可用
	public boolean isEnable()
	{
		return true;
	}
	
	//检查需要屏蔽的按键
	public int keyReleased(SaleEvent evt, KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.Clear:
				evt.initOneSale(evt.saleBS.saletype);
				clearSaleBill();
				break;

			case GlobalVar.Enter:
			case GlobalVar.Quantity:
			case GlobalVar.SetPrice:
			case GlobalVar.Del:
			case GlobalVar.Rebate:
			case GlobalVar.RebatePrice:
			case GlobalVar.WholeRate:
			case GlobalVar.WholeRebate:
			case GlobalVar.writeHang:
			case GlobalVar.readHang:
			case GlobalVar.StaffText:
			case GlobalVar.ExchangeSell:
				key = -1;
				break;

		}
		return key;
	}
	
	//上传小票时，是否需要增加额外操作
	public boolean sendSaleBill(SaleHeadDef saleHead)
	{
		return true;
	}
}
