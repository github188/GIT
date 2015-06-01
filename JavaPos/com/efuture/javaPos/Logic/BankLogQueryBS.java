package com.efuture.javaPos.Logic;

import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.Sqldb;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.UI.BankLogQueryEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class BankLogQueryBS 
{
	private String rowcode = null;
	protected Sqldb sql = null;
	private String[] banktypelist = null;
	protected boolean exist = false; // 表里是否存在typename字段
	private String curclasspaycode = null;
	
	public BankLogQueryBS()
	{
		// 检查表里是否有typename字段，如果存在，直接取库里的信息
		String[] ref = GlobalInfo.dayDB.getTableColumns("BANKLOG");
		
		for (int i = 0; i < ref.length ; i++)
		{
			if(ref[i].equals("typename"))
			{
				exist = true;
				break;
			}
		}
		
		if (!exist)
		{
			PaymentBankFunc pbfunc = CustomLocalize.getDefault().createCreatePayment().getPaymentBankFunc();
			banktypelist = pbfunc.getBankClassConfig("getFuncItem", 0);
			if (banktypelist == null) banktypelist = pbfunc.getFuncItem();
		}
	}
	
	public boolean init(BankLogQueryEvent  bcqe)
	{
		try
		{
			if (!this.getBankCardInfo(bcqe)) return false;
			
			getBankCardInfo(rowcode,bcqe);
			
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean getBankCardInfo(BankLogQueryEvent  bcqe)
	{
		ResultSet rs = null;
		int num = 0;
		
		try
		{
			Table tabBankCard = bcqe.getTabBankCard();
			String date = bcqe.getTxtDate().getText();
			int inputdjlb = bcqe.getCmbDjlb().getSelectionIndex();
			
			tabBankCard.removeAll();
			
			if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date + "/" + LoadSysInfo.getDefault().getDayDBName()))
			{
				new MessageBox(Language.apply("您输入的本地数据库不存在,请重新输入!"), null, false);
				return false;
			}
			
			if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
			
			// 检查本地是否存在多种金卡银联日志，如果存在选择一种进行分组查询
			curclasspaycode = null;
			Vector vec = null;
			String[] tabcols = sql.getTableColumns("BANKLOG");
			for (int i=0;i<tabcols.length;i++)
			{
				if (tabcols[i].equalsIgnoreCase("classname")) 
				{
					vec = new Vector();
					break;
				}
			}
			String wherecond = "";
			if (vec != null)
			{
				if ((rs = sql.selectData("select classname from BANKLOG group by classname")) != null)
				{
					while(rs.next())
					{
						String clname = rs.getString(1);
						String cldesc = clname;
						
						
						// 查找classname对应的付款代码，找到付款代码对应的付款方式名称
				        if (ConfigClass.Bankfunc != null && ConfigClass.Bankfunc.length() > 0)
				        {
				            String conf[] = ConfigClass.Bankfunc.split("\\|"); 
				            for (int i = 0 ; i < conf.length ; i++)
	                		{
	                			String[] s = conf[i].split(",");
	                			if (s[0].equalsIgnoreCase(clname))
	                			{
	                				for (int j=1;j<s.length;j++)
		                			{
		                				PayModeDef pm = DataService.getDefault().searchPayMode(s[j]);
		                				if (pm != null) 
		                				{
		                					curclasspaycode = pm.code;
		                					cldesc = pm.name;
		                					break;
		                				}
		                			}
	                				break;
	                			}
	                		}
	                	}
				        
				        vec.add(new String[]{curclasspaycode,cldesc,clname});
					}
					sql.resultSetClose();
					
            		if (vec.size() > 1)
            		{
        				String[] title = {Language.apply("付款方式"), Language.apply("付款接口名称"), Language.apply("接口对象") };
        				int[] width = {100,300, 300 };
        				int choice = new MutiSelectForm().open(Language.apply("请选择银联金卡工程接口"), title, width, vec);
        				if (choice < 0) return false;
        				
        				// 分组查询条件
        				String[] s = (String[])vec.elementAt(choice);
        				curclasspaycode = s[0];
        				wherecond = " where classname = '" + s[2] + "' ";
            		}
				}
			}
			
			// 不是全部日志
			if (inputdjlb >= 1 && inputdjlb <= 2)
			{
				String djlbcond = "";
				if (inputdjlb == 1) djlbcond = " retbz = 'Y' ";
				if (inputdjlb == 2) djlbcond = " retbz = 'N' ";
				if (wherecond != null && wherecond.length() > 0) wherecond += " and " + djlbcond;
				else wherecond += " where " + djlbcond;
			}
			
			// 查询日志列表
			if ((rs = sql.selectData("select * from BANKLOG " + wherecond + " order by rowcode desc")) != null)
			{
				boolean ret = false;
				while(rs.next())
				{
					String retbz = null;
					
					if (rs.getString("retbz").trim().equals("Y"))
					{
						retbz = Language.apply("成功");
					}
					else
					{
						retbz = "";
					}
					
					if (rs.getString("net_bz").trim().equals("Y"))
					{
						String[] bankinfo = {"↑" + rs.getString("rowcode"),retbz,rs.getString("rqsj"),exist?rs.getString("typename"):getChangeType(rs.getString("type")),ManipulatePrecision.doubleToString(rs.getDouble("je"))};
						
						TableItem item = new TableItem(tabBankCard, SWT.NONE);
						item.setText(bankinfo);
						
						if (num == 0)
						{
							rowcode = "↑" + rs.getString("rowcode");
						}
					}
					else
					{
						String[] bankinfo = {"  " + rs.getString("rowcode"),retbz,rs.getString("rqsj"),exist?rs.getString("typename"):getChangeType(rs.getString("type")),ManipulatePrecision.doubleToString(rs.getDouble("je"))};
						
						TableItem item = new TableItem(tabBankCard, SWT.NONE);
						item.setText(bankinfo);
						
						if (num == 0)
						{
							rowcode = "  " + rs.getString("rowcode");
						}
					}
					
					num = num + 1;
					
					ret = true;
				}
				
				return ret;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			sql.resultSetClose();
		}
	}
	
	public void getBankCardInfo(String rowcode,BankLogQueryEvent  bcqe)
	{
		ResultSet rs = null;
		
		try
		{
			String date = bcqe.getTxtDate().getText();
			
			if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date + "/" + LoadSysInfo.getDefault().getDayDBName()))
			{
				new MessageBox(Language.apply("您输入的本地数据库不存在,请重新输入!"), null, false);
				return ;
			}
			
			if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
			
			if ((rs = sql.selectData("select * from BANKLOG where rowcode = "+ Integer.parseInt(rowcode.substring(1).trim()))) != null)
			{
				if(rs.next())
				{
					bcqe.getTxtSyjCode().setText(rs.getString("syjh") + " - " + rs.getString("fphm"));
					bcqe.getTxtSyyCode().setText(rs.getString("syyh"));
					bcqe.getTxtCardCode().setText(CommonMethod.isNull(rs.getString("cardno"),""));
					bcqe.getTxtMoney().setText(ManipulatePrecision.doubleToString(rs.getDouble("je")));
					bcqe.getTxtType().setText(exist?rs.getString("typename"):getChangeType(rs.getString("type")));
					bcqe.getTxtTrace().setText(CommonMethod.isNull(rs.getString("trace"),""));
					bcqe.getTxtBank().setText(CommonMethod.isNull(rs.getString("bankinfo"),""));
					bcqe.getTxtLodTime().setText(CommonMethod.isNull(rs.getString("oldrq"),""));
					bcqe.getTxtReturnCode().setText(CommonMethod.isNull(rs.getString("retcode"),""));
					bcqe.getTxtOldTrace().setText(CommonMethod.isNull(rs.getString("oldtrace"),""));
					bcqe.getTxtReturnMsg().setText(CommonMethod.isNull(rs.getString("retmsg"),""));
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			sql.resultSetClose();
		}
	}
	
	public void printAgainBankCardInfo(String rowcode,BankLogQueryEvent  bcqe)
	{
		ResultSet rs = null;
		
		try
		{
			String date = bcqe.getTxtDate().getText();
			
			if (!PathFile.isPathExists(ConfigClass.LocalDBPath + "Invoice/" + date + "/" + LoadSysInfo.getDefault().getDayDBName()))
			{
				new MessageBox(Language.apply("您输入的本地数据库不存在,请重新输入!"), null, false);
				return ;
			}
			
			if (date.trim().equals(new ManipulateDateTime().getDateByEmpty()))
			{
				sql = GlobalInfo.dayDB;
			}
			else
			{
				sql = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(date.trim()));
			}
			
			if ((rs = sql.selectData("select * from BANKLOG where rowcode = "+ Integer.parseInt(rowcode.substring(1).trim()))) != null)
			{
				if (rs.next())
				{
					String trace = rs.getString("trace");
					String retbz = rs.getString("retbz");
					if (!retbz.equalsIgnoreCase("Y") || trace == null || trace.length() <= 0)
					{
						new MessageBox(Language.apply("你选择的日志交易未成交执行，不能重印签购单"));
						return;
					}
//					if (new MessageBox("你确定要重印["+trace+"]流水的交易签购单吗?",null,true).verify() != GlobalVar.Key1)
					if (new MessageBox(Language.apply("你确定要重印[{0}]流水的交易签购单吗?", new Object[]{trace}),null,true).verify() != GlobalVar.Key1)
					{
						return;
					}
					
					// 创建银联对象调用重印功能
					PaymentBankFunc pbfunc = CreatePayment.getDefault().getPaymentBankFuncByMenu(curclasspaycode);
					//new MessageBox("付款方式代码: "+curclasspaycode+" PaymentBankFunc:"+pbfunc.getClass().getName());
					boolean ret = pbfunc.callBankFunc(PaymentBank.XYKCD, 0, null, null, null, trace, null, null, null);
					if (!ret) new MessageBox(pbfunc.getErrorMsg());
					else new MessageBox("["+trace+"]" + Language.apply("流水号的交易签购单已重打印!"));
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			sql.resultSetClose();
		}
	}
	
	public String getChangeType(String type)
	{
		String strtype = null;
		
		if (banktypelist != null)
		{
	        for (int i=0;i<banktypelist.length;i++)
	        {
	        	if (Convert.codeInString(banktypelist[i].trim(),'[').equals(type))
	        	{
	        		strtype = banktypelist[i];
	        		return strtype.substring(strtype.indexOf("]")+1);
	        	}
	        }
		}
		else
		{
			
		}
		
        return "[" + type + "]" + Language.apply("未知交易类型");
	}
}
