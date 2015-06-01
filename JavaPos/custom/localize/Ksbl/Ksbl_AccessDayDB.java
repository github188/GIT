 package custom.localize.Ksbl;

import java.sql.ResultSet;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Struct.BankLogDef;

public class Ksbl_AccessDayDB extends AccessDayDB
{
	 public boolean updateBankLog(BankLogDef bcd,boolean allotflag,boolean firstupdate)
	    {
	    	try
	    	{
	    		Object obj = GlobalInfo.dayDB.selectOneData("select count(*) from BANKLOG where  rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'");
				
				if (obj == null || Long.parseLong(String.valueOf(obj)) < 1)
		        {
					new MessageBox("找不到信用卡交易记录", null, false); 
					return false;
		        }
				
				String sql;
				if (!allotflag)
				{
					// 第一次撤销或退货日志,要减少原交易的可分配金额
					if (firstupdate && GlobalInfo.sysPara.allowbankselfsale == 'Y' && (bcd.type.equals(String.valueOf(PaymentBank.XYKCX)) || bcd.type.equals(String.valueOf(PaymentBank.XYKTH))) && bcd.retbz == 'Y')
					{
						// 找原交易的日志行
						int oldrowcode = -1;
						String oldrqsj = "";
						String oldsyjh = "";
						if (bcd.oldtrace > 0)
						{
							ResultSet rs = GlobalInfo.dayDB.selectData("select * from BANKLOG where trace = " + bcd.oldtrace + " and allotje > 0");
				            if (rs != null && rs.next())
				            {
				            	oldrowcode = rs.getInt("rowcode");
				            	oldrqsj = rs.getString("rqsj");
				            	oldsyjh = rs.getString("syjh");
				            }
				            GlobalInfo.dayDB.resultSetClose();
						}
						else
						{
							ResultSet rs = GlobalInfo.dayDB.selectData("select * from BANKLOG where cardno = '" + bcd.cardno + "' and je = " + bcd.je + " and allotje > 0");
				            if (rs != null && rs.next())
				            {
				            	oldrowcode = rs.getInt("rowcode");
				            	oldrqsj = rs.getString("rqsj");
				            	oldsyjh = rs.getString("syjh");
				            }
				            GlobalInfo.dayDB.resultSetClose();
						}
						
						//
						if (!GlobalInfo.dayDB.beginTrans()) return false;
						
						
						// 更新当前交易返回信息	
						sql = "update BANKLOG set oldtrace = " + bcd.oldtrace + ",net_bz = '"+ bcd.net_bz +"' , je = " + bcd.je+" , cardno = '" + bcd.cardno + "' , trace = " + bcd.trace +" , " +
								"bankinfo = '"+ bcd.bankinfo +"' , retcode ='"+ bcd.retcode +"' ,retmsg = '"+ bcd.retmsg +"' , retbz ='"+ bcd.retbz+"' " ;			
						if (GlobalInfo.dayDB.isColumnExist("tempStr","BANKLOG"))
						{						
							sql += ", tempstr='"+ bcd.tempstr +"', tempstr1='"+ bcd.tempstr1 + "' ";	
						}
						
						if (GlobalInfo.dayDB.isColumnExist("authno", "BANKLOG"))
						{
							sql += ", authno='"+ bcd.authno +"' ";	
						}
						
						if (GlobalInfo.dayDB.isColumnExist("memo1", "BANKLOG"))
						{
							sql += ", memo1='"+ bcd.memo1 +"' ";	
						}
						
						if (GlobalInfo.dayDB.isColumnExist("memo2", "BANKLOG"))
						{
							sql += ", memo2='"+ bcd.memo2 +"' ";	
						}
						
						if (GlobalInfo.dayDB.isColumnExist("ylzk", "BANKLOG"))
						{
							sql += ", ylzk='"+ bcd.ylzk +"' ";	
						}
						
						sql += " where rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'";
						
						if (!GlobalInfo.dayDB.executeSql(sql))
						{
			                return false;
						}
						
						// 减少原交易可分配金额
						if (oldrowcode > 0)
						{
							sql = "update BANKLOG set allotje = allotje - " + bcd.je + " where rowcode = " + oldrowcode + " and rqsj = '"+ oldrqsj +"' and syjh = '" + oldsyjh +"'";
							
							if (!GlobalInfo.dayDB.executeSql(sql))
							{
				                return false;
							}
						}
						
						if (!GlobalInfo.dayDB.commitTrans()) return false;
					}
					else
					{
						sql = "update BANKLOG set net_bz = '"+ bcd.net_bz +"' , je = " + bcd.je+" , cardno = '" + bcd.cardno + "' , trace = " + bcd.trace +" , " +
								"bankinfo = '"+ bcd.bankinfo +"' , retcode ='"+ bcd.retcode +"' ,retmsg = '"+ bcd.retmsg +"' , retbz ='"+ bcd.retbz+"' " ;
						if (GlobalInfo.dayDB.isColumnExist("tempStr","BANKLOG"))
						{						
							sql += ", tempStr='"+ bcd.tempstr +"', tempStr1='"+ bcd.tempstr1 + "'  " ;					
						}
						
						if (GlobalInfo.dayDB.isColumnExist("ylzk", "BANKLOG"))
						{
							sql += ", ylzk='"+ bcd.ylzk +"' ";	
						}
						sql += " where rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'";
						
						if (!GlobalInfo.dayDB.executeSql(sql))
						{
			                return false;
						}				
					}
				}
				else
				{
					sql = "update BANKLOG set allotje = " + bcd.allotje + " where rowcode = " + bcd.rowcode + " and rqsj = '"+ bcd.rqsj +"' and syjh = '" + bcd.syjh +"'";
					
					if (!GlobalInfo.dayDB.executeSql(sql))
					{
		                return false;
					}
				}
				
	    		return true;
	    	}
	    	catch (Exception ex)
	    	{
	    		ex.printStackTrace();
	    		return false;
	    	}
	    }
	    
}
