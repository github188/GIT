package posserver.Configure.CmdDef;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;

import oracle.jdbc.OracleTypes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import posserver.Configure.Common.GlobalVar;
import posserver.Configure.Common.KeyValueStruct;

import com.swtdesigner.SWTResourceManager;

public class FrmSqlParaCheck
{
	private Label lbProcPara;
	private Label lbCfgParaNum;
	private Button btnDelCfgPara;
	private Button btnAddCfgPara;
	private Label infoLabel;
	private Label lbTran_XX_SqlNum;
	private Text txtTran_XX_Sql;
	private Combo cmbdatasourename;
	private Table tabCfgPara;
	//Object[] 0-类型(IN,OUT),参数结构,是否全局参数(在SQL语句中配置的如{call java_findsyj(:mktcode,?,?,?),其中:mktcode就为全局参数})
	private Vector vCfgPara = new Vector();
	
	// 用于检查参数
	ProcStruct procdure = new ProcStruct();
	
	// 用于生成Sql语句
	ProcStruct oldprocedure = new ProcStruct();
	
	Vector resultsetpara = new Vector();
	CmdTextStruct cts;
	boolean isUpdateCmd = false;
	
	Shell parentshell;
	
    protected int[] currentPoint = new int[] { 6, 0 };
    

    protected TableEditor editor;
    protected Combo cmbNewEditor;
    protected Text txtNewEditor;
	
	/**
	 * Open the window
	 */
	public void open(CmdTextStruct cts1,Shell parentshe,boolean updatecmd) 
	{
		parentshell = parentshe;
		cts = cts1;
		isUpdateCmd = updatecmd;
		
		procdure.ProcName = "";
		procdure.PackAge = "";
		procdure.IsSql = false;
		procdure.ProcPara.clear();
		
		if (cts.Sql_XX_Type.equalsIgnoreCase("ResultSet"))
		{
			procdure.IsSql = true;
		}
		
		vCfgPara.clear();
		
		for (int i = 0;i < cts.Tran_XX_Para.size();i++)
		{
			vCfgPara.add(new Object[]{"IN",((CmdParaStruct)cts.Tran_XX_Para.get(i)).Copy(),"N"});
		}
		
		for (int i = 0;i < cts.Tran_XX_Col.size();i++)
		{
			vCfgPara.add(new Object[]{"OUT",((CmdParaStruct)cts.Tran_XX_Col.get(i)).Copy(),"N"});
		}
		
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	public void AddGlobalPara()
	{
		if (procdure == null || procdure.IsSql) return;
		
		// 删除已添加的全局参数
		for (int i = 0;i < vCfgPara.size();i ++)
		{
			Object[] objs = (Object[])vCfgPara.get(i);
			
			if (objs.length >=3)
			{
				String isglobalpara = (String)objs[2];
				
				if (isglobalpara.equals("Y"))
				{
					vCfgPara.remove(i);
				}
			}
			
		}
		
		// 添加配置全局参数
		int startindex = txtTran_XX_Sql.getText().trim().indexOf("(");
		int endindex = txtTran_XX_Sql.getText().trim().indexOf(")");
		
		boolean ishaveglobal = false;
		
		if (endindex > startindex)
		{
			String parastr = txtTran_XX_Sql.getText().trim().substring(startindex+1,endindex);
			String[] paras = parastr.split(",");
			for (int i = 0;i < paras.length;i ++)
			{
				CmdParaStruct cps = new CmdParaStruct();
				cps.Name = paras[i];
				cps.Type = "s";
				cps.TypeDesc = "s-字符串";
				
				if (paras[i].length() > 0 && paras[i].startsWith(":"))
				{
					if (i >vCfgPara.size())
					{
						vCfgPara.add(new Object[]{"IN",cps,"Y"});
					}
					else
					{
						vCfgPara.add(i,new Object[]{"IN",cps,"Y"});
					}
					
					ishaveglobal = true;
				}
			}
			
			if (ishaveglobal)
			{
				this.RefreshCfgPara();
			}
		}
	}
	
	public boolean GetProcecureInfo()
	{
		infoLabel.setText("");
		
		tabProcPara.removeAll();
		
		procdure.IsSql = false;
		procdure.PackAge = "";
		procdure.ProcName = "";
		procdure.ProcPara.clear();
		
		if (cts.Sql_XX_Type.equalsIgnoreCase("ResultSet"))
		{
			procdure.IsSql = true;
		}
		
		int datasourceIndex = cmbdatasourename.getSelectionIndex();
		
		if (datasourceIndex < 0) 
		{
			infoLabel.setText("请选择数据源!");
			return false;
		}
		
		if (!procdure.IsSql)
		{
			procdure.ProcName = GetProcedureName(txtTran_XX_Sql.getText().trim());
			
			if (procdure.ProcName.length() <= 0)
			{
				return false;
			}
			
			String[] strs = procdure.ProcName.split("[.]");
			
			if (strs.length >= 2)
			{
				procdure.PackAge = strs[0];
				procdure.ProcName = strs[1];
			}
		}
		
		DataSourceCommonStruct cscs = (DataSourceCommonStruct)GlobalVar.Datasourcecommon.get(datasourceIndex);
		
		// 判断是否为Oracle数据库
		boolean isoracledb = cscs.DriverClass != null && cscs.DriverClass.toLowerCase().indexOf("oracle")>=0;
		Connection conn = null;
		try
		{
			 Class.forName(cscs.DriverClass);
			 conn = DriverManager.getConnection(cscs.ConnectionUrl,cscs.UserName,cscs.Password);
			 
			 if (conn != null)
	         {
	        	 DatabaseMetaData dbmd = conn.getMetaData();
	        	 ResultSet rs = null;
	        	 
	        	 // 如果不是SQL才进行存储过程的查询
	        	 if (!procdure.IsSql)
	        	 {
		        	 //由于Oracle的存储过程名都是大写，而SqlServer不区分大小写，则在调用getProcedures,getProcedureColumns的时候都toupper操作
		        	 if (procdure.PackAge.length() <= 0)
		        	 {
		        		 rs = dbmd.getProcedures(null, isoracledb?cscs.UserName.toUpperCase():null, procdure.ProcName.toUpperCase());
		        	 }
		        	 else
		        	 {
		        		 rs = dbmd.getProcedures(procdure.PackAge.toUpperCase(), isoracledb?cscs.UserName.toUpperCase():null, procdure.ProcName.toUpperCase());
		        	 }
		        	 
		        	 if (!rs.next())
		        	 {
		        		 infoLabel.setText("未找到存储过程" + procdure.PackAge + (procdure.PackAge.equals("")?"":".") + procdure.ProcName + "!"); 
		        		 return false;
		        	 }
		        	 
		        	 if (procdure.PackAge.length() <= 0)
		        	 {
		        		 rs = dbmd.getProcedureColumns(null, isoracledb?cscs.UserName.toUpperCase():null, procdure.ProcName.toUpperCase(), "%");
		        	 }
		        	 else
		        	 {
		        		 rs = dbmd.getProcedureColumns(procdure.PackAge.toUpperCase(), isoracledb?cscs.UserName.toUpperCase():null, procdure.ProcName.toUpperCase(), "%");
		        	 }
		        	 
		        	 Vector v = new Vector();
		        	 
		        	 while(rs.next())
		        	 {
		        		 //0-COLUMN_NAME,1-column_type,2-cfgcolumntype,3-data_type,4-cfgdatatype,5-cfgdatatypedesc
		        		 ProcParaStruct pps = new ProcParaStruct();
		        		 pps.Name = rs.getString("COLUMN_NAME").toLowerCase();
		        		 pps.InOutType = rs.getString("COLUMN_TYPE");
		        		 pps.CfgInOutType = "XX-未知";
		        		 
		        		 if (pps.InOutType.equals("1"))
		        		 {
		        			 pps.CfgInOutType = "IN";
		        			 procdure.ProcPara.add(pps);
		        		 }
		        		 else if (pps.InOutType.equals("2"))
		        		 {
		        			 pps.CfgInOutType = "INOUT";
		        			 procdure.ProcPara.add(pps);
		        		 }
		        		 else if (pps.InOutType.equals("4"))
		        		 {
		        			 pps.CfgInOutType = "OUT";
		        			 procdure.ProcPara.add(pps);
		        		 }
		        		 else if (pps.InOutType.equals("3"))
		        		 {
		        			 pps.CfgInOutType = "RETURN";
		        			 v.add(pps);
		        		 }
		        		 else
		        		 {
		        			 v.add(pps);
		        		 }
		        		 
		        		 pps.DataType = String.valueOf(rs.getInt("DATA_TYPE"));
		        		 pps.DataTypeName = rs.getString("TYPE_NAME") + (rs.getInt("LENGTH")>0?"("+rs.getInt("LENGTH")+")":"");
		        		 pps.CfgDataType = getTypeFromJavaType(pps.DataType,rs.getInt("LENGTH"));
		        	 }
		        	 
		        	 //将非IN/OUT/INOUT的参数放入参数列表的最后
		        	 for (int i = 0;i<v.size();i++)
		        	 { 
		        		 procdure.ProcPara.add(v.get(i));
		        	 }
		        	 
		        	 if (!IsValidSql(txtTran_XX_Sql.getText().trim()))
		        	 {
		        		 this.txtTran_XX_Sql.setText(AutoSql());
		        		 
		        		 txtTran_XX_Sql.setText(txtTran_XX_Sql.getText().replace(",?", "").replace("?",""));
		        	 }
	        	 }
	        	 
	        	 // 保存原结构，原于生成SQL语句
	        	 oldprocedure = procdure.Clone();
	        	 
	        	 boolean issuc = getResultSet(conn);
	        	 
	        	 // 将结果集参数加入procdure.ProcPara
	        	 if (issuc && this.resultsetpara.size() > 0)
	        	 {
	        		 int i = 0;
	        		 for (;i < procdure.ProcPara.size();i++)
	        		 {
	        			 // Oracle特殊处理
	        			 ProcParaStruct pps = (ProcParaStruct)procdure.ProcPara.get(i);
	        			 
	        			 if (cts.Sql_XX_Type.equalsIgnoreCase("OracleResultSet"))
						 {
							 if (pps.DataTypeName.equalsIgnoreCase("REF CURSOR"))
							 {
								 procdure.ProcPara.remove(pps);	
								 i--;
							 }
						 }
	        			 
	        			 if (!pps.CfgInOutType.equals("IN") && !pps.CfgInOutType.equals("INOUT") && !pps.CfgInOutType.equals("OUT"))
	        			 {
	        				 break;
	        			 }
	        		 }
	        		 
	        		 for (int j = 0;j < resultsetpara.size();j++)
	        		 {
	        			 ProcParaStruct pps = (ProcParaStruct)resultsetpara.get(j);
	        			 
	        			 procdure.ProcPara.add(i+j,pps);
	        		 }
	        	 }
	        	 
        		 for (int i = 0;i< this.procdure.ProcPara.size();i++)
	        	 {
	        		 ProcParaStruct pps = (ProcParaStruct)procdure.ProcPara.get(i);

	        		 TableItem ti = new TableItem(tabProcPara,SWT.NULL);
	                 ti.setText(new String[]{String.valueOf(i),pps.Name,pps.CfgInOutType,pps.DataTypeName,pps.CfgDataType==null?pps.DataTypeName:pps.CfgDataType.value});
	        	 }
	        	 
	        	 if (issuc)
	        	 {
	        		 infoLabel.setText("存储过程查找成功!");
	        	 }
	        	 
	        	 // 增加全局参数
	        	 AddGlobalPara();
	        	 
	        	 return true;
	         }
	         else
	         {
	         	infoLabel.setText("数据库连接失败!");
	         	return false;
	         }	   
		}
		catch(Exception ex)
		{
			infoLabel.setText(ex.getMessage());
			return false;
		}
		finally
		{
			try
			{
				RefreshNum();
				if (conn != null) conn.close();
			}
			catch(Exception ex)
			{
				infoLabel.setText(ex.getMessage());
			}
		}
	}
	
	/*
	 *
	 */
	private KeyValueStruct getTypeFromJavaType(String javasqltype,int len)
	{
		 /*
		 s = s-字符串
		 i = i-整型
		 l = l-长整型
		 c = c-字符型
		 f = f-浮点型
		 */
		
		if (javasqltype.equals(String.valueOf(java.sql.Types.VARCHAR)) || (javasqltype.equals(String.valueOf(java.sql.Types.CHAR)) && len >= 2))
		 {
			 for (int j = 0;j < GlobalVar.Paradatatype.size();j++)
			 {
				 KeyValueStruct kvs = (KeyValueStruct)GlobalVar.Paradatatype.get(j);
				 if (kvs.key.equals("s"))
				 {
					 return kvs;
				 }
			 }

		 }
		 else if (javasqltype.equals(String.valueOf(java.sql.Types.INTEGER)))
		 {
			 for (int j = 0;j < GlobalVar.Paradatatype.size();j++)
			 {
				 KeyValueStruct kvs = (KeyValueStruct)GlobalVar.Paradatatype.get(j);
				 if (kvs.key.equals("i"))
				 {
					 return kvs;
				 }
			 }
		 }
		 else if (javasqltype.equals(String.valueOf(java.sql.Types.NUMERIC)) 
				 || javasqltype.equals(String.valueOf(java.sql.Types.DOUBLE)) 
				 || javasqltype.equals(String.valueOf(java.sql.Types.FLOAT))
				 || javasqltype.equals(String.valueOf(java.sql.Types.DECIMAL))
				 )
		 {
			 for (int j = 0;j < GlobalVar.Paradatatype.size();j++)
			 {
				 KeyValueStruct kvs = (KeyValueStruct)GlobalVar.Paradatatype.get(j);
				 if (kvs.key.equals("f"))
				 {
					 return kvs;
				 }
			 }
		 }
		 else if (javasqltype.equals(String.valueOf(java.sql.Types.BIGINT)))
		 {
			 for (int j = 0;j < GlobalVar.Paradatatype.size();j++)
			 {
				 KeyValueStruct kvs = (KeyValueStruct)GlobalVar.Paradatatype.get(j);
				 if (kvs.key.equals("l"))
				 {
					 return kvs;
				 }
			 }
		 }
		 else if (javasqltype.equals(String.valueOf(java.sql.Types.CHAR)))
		 {
			 for (int j = 0;j < GlobalVar.Paradatatype.size();j++)
			 {
				 KeyValueStruct kvs = (KeyValueStruct)GlobalVar.Paradatatype.get(j);
				 if (kvs.key.equals("c"))
				 {
					 return kvs;
				 }
			 }
		 }
		
		return null;
		
		 /**
        int dbColumnName2 = rs.getInt("DATA_TYPE");
        String dbColumnName0 = rs.getString("COLUMN_NAME");
        String dbColumnName1 = rs.getString("TYPE_NAME");
        String dbColumnName3 = rs.getString("COLUMN_TYPE");
        
        switch(dbColumnName2)
        {
       	 case java.sql.Types.INTEGER:
       		 break;
        }
        
        TableItem ti = new TableItem(tabProcPara,SWT.NULL);
        ti.setText(new String[]{String.valueOf(i),dbColumnName0,dbColumnName3,String.valueOf(dbColumnName2)});
        **/
	}
	
	/*
	 * 
	 */
	private boolean getResultSet(Connection conn)
	{
		resultsetpara.clear();
		
	   	 if (cts.Sql_XX_Type.toUpperCase().indexOf("RESULTSET") >= 0)
		 {
	   		 CallableStatement ps = null;
   			 Vector vresultsets = new Vector();
   			 
	   		 try
	   		 { 
	   			 // 用于存放Oracle的Ref Cursor
	   			 ArrayList lis = new ArrayList();

	   			 ps = conn.prepareCall(txtTran_XX_Sql.getText().trim(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	   			
	   			 int index = 1;
				 for (int i = 0;i < this.procdure.ProcPara.size();i++)
				 {
					 ProcParaStruct pps = (ProcParaStruct)procdure.ProcPara.get(i);
					 if (pps.CfgInOutType.equals("IN"))
					 {
						 if (pps.DataType.equals(String.valueOf(java.sql.Types.VARCHAR)))
		        		 {
							 ps.setString(index++, "");
		        		 }
						 if (pps.DataType.equals(String.valueOf(java.sql.Types.CHAR)))
		        		 {
							 ps.setString(index++, " ");
		        		 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.INTEGER)))
						 {
							ps.setInt(index++, 0); 
						 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.DECIMAL)))
						 {
							ps.setDouble(index++, 0); 
						 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.NUMERIC)))
						 {
							ps.setDouble(index++, 0); 
						 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.DOUBLE)))
						 {
							ps.setDouble(index++, 0); 
						 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.FLOAT)))
						 {
							ps.setFloat(index++, 0); 
						 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.DATE)))
						 {
							Date dt = Date.valueOf("1999/01/01");
							ps.setDate(index++, dt); 
						 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.TIMESTAMP)))
						 {
							Timestamp ts = Timestamp.valueOf("1999/01/01 00:00:00");
							ps.setTimestamp(index++, ts); 
						 }
						 else if (pps.DataType.equals(String.valueOf(java.sql.Types.TIME)))
						 {
							Time t = Time.valueOf("00:00:00");
							ps.setTime(index++, t); 
						 }
					 }
					 else  if (pps.CfgInOutType.equals("OUT"))
					 {
						if (cts.Sql_XX_Type.equalsIgnoreCase("OracleResultSet"))
					 	{
						 	if (pps.DataTypeName.equalsIgnoreCase("REF CURSOR"))
						 	{
						 		lis.add(String.valueOf(i));
						 		ps.registerOutParameter(index++, OracleTypes.CURSOR);
						 	}
					 	}
					 } 
				 }
				 	
                //如果等ResultSet代表直接返回记录集
                if (cts.Sql_XX_Type.equalsIgnoreCase("ResultSet"))
                {
                	ps.execute();
                    ResultSet rs = ps.getResultSet();
                    
                    if (rs == null)
                    {
                    	return false;
                    }
                    
                    vresultsets.add(rs);
                }
                else if (cts.Sql_XX_Type.equalsIgnoreCase("MsSqlResultSet"))
                {
                	ResultSet rs = ps.executeQuery();
                	
                    if (rs == null)
                    {
                    	return false;
                    }
                    
                    vresultsets.add(rs);
                }
                else if (cts.Sql_XX_Type.equalsIgnoreCase("OracleResultSet"))
                {
                	ps.execute();
                	for (int i = 0;i < lis.size();i++)
                	{
                		int ix = (int)Integer.parseInt(String.valueOf(lis.get(i)));
                		ResultSet rs = (ResultSet)ps.getObject(++ix);
                		
                        if (rs == null)
                        {
                        	return false;
                        }
                        
                        vresultsets.add(rs);
                	}
                }
			 	
                for(int k = 0;k < vresultsets.size();k++)
                {
                	ResultSet rs = (ResultSet)vresultsets.get(k);
				 	ResultSetMetaData rsmd = rs.getMetaData();

	        		// 更新命令的前三个列是用来判断的
				 	int i = 1;
	        		if (isUpdateCmd) i = 4;
				 	for (;i <= rsmd.getColumnCount();i++)
				 	{
				 		ProcParaStruct pps = new ProcParaStruct();
				 		pps.DataType = String.valueOf(rsmd.getColumnType(i));
				 		pps.DataTypeName = rsmd.getColumnTypeName(i) + (rsmd.getColumnDisplaySize(i)>0?"("+rsmd.getColumnDisplaySize(i)+")":"");
		        		pps.Name = rsmd.getColumnName(i).toLowerCase();

		        		pps.InOutType = "4";
			        	pps.CfgInOutType = "OUT";
		        		pps.CfgDataType = getTypeFromJavaType(pps.DataType,rsmd.getColumnDisplaySize(i));
		        		resultsetpara.add(pps);
				 	}
				 	if (isUpdateCmd)
				 	{
					 	for (i=1;i <= 3;i++)
					 	{
					 		ProcParaStruct pps = new ProcParaStruct();
					 		pps.DataType = String.valueOf(rsmd.getColumnType(i));
					 		pps.DataTypeName = rsmd.getColumnTypeName(i) + (rsmd.getColumnDisplaySize(i)>0?"("+rsmd.getColumnDisplaySize(i)+")":"");
			        		pps.Name = rsmd.getColumnName(i).toLowerCase();

			        		pps.InOutType = "3";
				        	pps.CfgInOutType = "RETURN";
			        		pps.CfgDataType = getTypeFromJavaType(pps.DataType,rsmd.getColumnDisplaySize(i));
			        		resultsetpara.add(pps);
					 	}
				 	}
                }
			 	
			 	conn.rollback();
			 	
			 	return true;
	   		 }
	   		 catch(Exception ex)
	   		 {
	   			 this.infoLabel.setText("未能获得结果集参数信息!\r\n" + ex.getMessage());
	   			 
	   			 return false;
	   		 }
	   		 finally
	   		 {
	   			 try
	   			 {
	   				 for (int i = 0;i < vresultsets.size();i++)
	   				 {
	   					 ResultSet rs = (ResultSet)vresultsets.get(i);
	   					 if (rs != null) rs.close();
	   				 }
	   				 
	   				 if (ps != null) ps.close();
	   			 }
	   			 catch(Exception ex)
	   			 {
	   				 this.infoLabel.setText(ex.getMessage());
	   			 }
	   		 }
		 }
	   	 
   		 return true;
	}
	
	/*
	 * 
	 */
	private Table tabProcPara;
	private Shell shell = null;

	/**
	 * This method initializes sShell
	 */
	private void createContents()
	{
		shell = new Shell(SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setLayout(new FillLayout());
		shell.setText("编辑");
		shell.setSize(new Point(618, 472));
		Rectangle rctparent = parentshell.getBounds();
		Rectangle rct = shell.getBounds();
		
		rct.x = rctparent.x + rctparent.width / 2 - rct.width /2;
		rct.y = rctparent.y + rctparent.height / 2 - rct.height /2;
		shell.setBounds(rct);
		
		final Composite composite = new Composite(shell, SWT.NONE);

		tabProcPara = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
		tabProcPara.setBounds(10, 120, 334, 219);
		tabProcPara.setLinesVisible(true);
		tabProcPara.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_2_2 = new TableColumn(tabProcPara, SWT.NONE);
		newColumnTableColumn_2_2.setWidth(28);
		newColumnTableColumn_2_2.setText("序");

		final TableColumn newColumnTableColumn_2_3 = new TableColumn(tabProcPara, SWT.NONE);
		newColumnTableColumn_2_3.setWidth(82);
		newColumnTableColumn_2_3.setText("参数名");

		final TableColumn newColumnTableColumn = new TableColumn(tabProcPara, SWT.NONE);
		newColumnTableColumn.setWidth(65);
		newColumnTableColumn.setText("类型");

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabProcPara, SWT.NONE);
		newColumnTableColumn_4.setWidth(87);
		newColumnTableColumn_4.setText("参数类型");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabProcPara, SWT.NONE);
		newColumnTableColumn_1.setWidth(84);
		newColumnTableColumn_1.setText("数据类型");
		
		final Button btnOk = new Button(composite, SWT.NONE);
		btnOk.setBounds(253, 407, 56, 21);
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnOkClick();
			}
		});
		btnOk.setText("保存");

		Button btnCancel;
		btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(315, 407, 56, 21);
		btnCancel.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnCancelClick();
			}
		});
		btnCancel.setText("取消");

		final Button btnRefresh = new Button(composite, SWT.NONE);
		btnRefresh.setBounds(5, 407, 56, 21);
		btnRefresh.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(final SelectionEvent arg0)
			{
				GetProcecureInfo();
			}
		});
		btnRefresh.setText("刷新过程");

		final Button btnCheck = new Button(composite, SWT.NONE);
		btnCheck.setBounds(69, 407, 56, 21);
		btnCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnCheckClick();
			}
		});
		btnCheck.setText("检查");

		final Label label_2 = new Label(composite, SWT.NONE);
		label_2.setBounds(15, 100, 65, 20);
		label_2.setText("过程参数:");

		tabCfgPara = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
		tabCfgPara.setBounds(350, 120, 252, 219);
		tabCfgPara.setLinesVisible(true);
		tabCfgPara.setHeaderVisible(true);

		editor = new TableEditor(tabCfgPara);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		
		tabCfgPara.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent event)
			{			
				int indextabCfgPara = -1;
				
				Point pt = new Point(event.x, event.y);
				int index = tabCfgPara.getTopIndex();
				boolean done = false;

				while (index < tabCfgPara.getItemCount())
				{
					final TableItem item = tabCfgPara.getItem(index);

					for (int i = 0; i < tabCfgPara.getColumnCount(); i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							indextabCfgPara = index;
							currentPoint[1] = index;
							currentPoint[0] = i;
							
							findLocation(indextabCfgPara);
							done = true;
							break;
						}
					}

					if (done) break;

					index++;
				}
			}

			public void mouseUp(MouseEvent e)
			{

			}
		});
		// 生成刚加入tableItem
		tabCfgPara.redraw();

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabCfgPara, SWT.NONE);
		newColumnTableColumn_3.setWidth(28);
		newColumnTableColumn_3.setText("序");
    	
		final TableColumn newColumnTableColumn_2_2_1 = new TableColumn(tabCfgPara, SWT.NONE);
		newColumnTableColumn_2_2_1.setWidth(82);
		newColumnTableColumn_2_2_1.setText("参数名");

		final TableColumn newColumnTableColumn_2_3_1 = new TableColumn(tabCfgPara, SWT.NONE);
		newColumnTableColumn_2_3_1.setWidth(56);
		newColumnTableColumn_2_3_1.setText("类型");

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabCfgPara, SWT.NONE);
		newColumnTableColumn_2.setWidth(100);
		newColumnTableColumn_2.setText("数据类型");

		new TableColumn(tabCfgPara, SWT.NONE);

		new TableColumn(tabCfgPara, SWT.NONE);

		final Label label_2_1 = new Label(composite, SWT.NONE);
		label_2_1.setBounds(350, 100, 65, 20);
		label_2_1.setText("配置参数:");

		final Label label_2_2 = new Label(composite, SWT.NONE);
		label_2_2.setBounds(15, 10, 46, 20);
		label_2_2.setText("数据源:");

		cmbdatasourename = new Combo(composite, SWT.NONE);
		cmbdatasourename.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				cmbdatasourenameSelectedIndexChange();
			}
		});
		cmbdatasourename.setBounds(70, 7, 532, 21);
		cmbdatasourename.select(0);
		
		cmbdatasourename.removeAll();
		for (int i = 0;i < GlobalVar.Datasourcecommon.size();i ++)
		{
			DataSourceCommonStruct csct = (DataSourceCommonStruct)GlobalVar.Datasourcecommon.get(i);
			cmbdatasourename.add(csct.DataSourceName);
		}
		
		cmbdatasourename.select(0);
		

		txtTran_XX_Sql = new Text(composite, SWT.BORDER | SWT.WRAP);
		txtTran_XX_Sql.addFocusListener(new FocusAdapter() {
			public void focusLost(final FocusEvent arg0)
			{
				RefreshNum();
			}
		});
		txtTran_XX_Sql.setBounds(70, 36, 532, 42);
		
		final Label label_2_1_1_1_1 = new Label(composite, SWT.NONE);
		label_2_1_1_1_1.setBounds(15, 35, 56, 20);
		label_2_1_1_1_1.setText("Sql语句:");

		lbTran_XX_SqlNum = new Label(composite, SWT.NONE);
		lbTran_XX_SqlNum.setBounds(0, 64, 23, 20);
		lbTran_XX_SqlNum.setAlignment(SWT.RIGHT);
		lbTran_XX_SqlNum.setText("0");

		final Label label_2_2_1_2_1_1 = new Label(composite, SWT.NONE);
		label_2_2_1_2_1_1.setBounds(28, 64, 39, 20);
		label_2_2_1_2_1_1.setText("个参数");

		infoLabel = new Label(composite, SWT.NONE);
		infoLabel.setBounds(10, 371, 598, 33);
		infoLabel.setForeground(SWTResourceManager.getColor(255, 0, 0));
		infoLabel.setText("Label");

		btnAddCfgPara = new Button(composite, SWT.NONE);
		btnAddCfgPara.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnAddCfgParaClick();
			}
		});
		btnAddCfgPara.setBounds(350, 345, 23, 21);
		btnAddCfgPara.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
		btnAddCfgPara.setText("+");

		btnDelCfgPara = new Button(composite, SWT.NONE);
		btnDelCfgPara.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnDelCfgParaClick();
			}
		});
		btnDelCfgPara.setBounds(379, 345, 23, 21);
		btnDelCfgPara.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
		btnDelCfgPara.setText("-");

		final Label label_2_2_1_1_1_1 = new Label(composite, SWT.NONE);
		label_2_2_1_1_1_1.setBounds(404, 348, 35, 20);
		label_2_2_1_1_1_1.setText("参数:");

		final Label label_2_2_1_2_1_2 = new Label(composite, SWT.NONE);
		label_2_2_1_2_1_2.setBounds(504, 348, 23, 20);
		label_2_2_1_2_1_2.setText("个");

		lbCfgParaNum = new Label(composite, SWT.NONE);
		lbCfgParaNum.setBounds(445, 348, 53, 20);
		lbCfgParaNum.setAlignment(SWT.RIGHT);
		lbCfgParaNum.setText("0");

		final Label label_2_2_1_1_1_1_1 = new Label(composite, SWT.NONE);
		label_2_2_1_1_1_1_1.setBounds(10, 345, 35, 20);
		label_2_2_1_1_1_1_1.setText("参数:");

		lbProcPara = new Label(composite, SWT.NONE);
		lbProcPara.setBounds(51, 345, 67, 20);
		lbProcPara.setAlignment(SWT.RIGHT);
		lbProcPara.setText("0");

		final Label label_2_2_1_2_1_2_1 = new Label(composite, SWT.NONE);
		label_2_2_1_2_1_2_1.setBounds(124, 345, 23, 20);
		label_2_2_1_2_1_2_1.setText("个");

		final Button btnAuto = new Button(composite, SWT.NONE);
		btnAuto.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				btnAutoClick();
			}
		});
		btnAuto.setBounds(133, 407, 114, 21);
		btnAuto.setText("自动生成配置参数");

		Init();
	}

	public void btnDelCfgParaClick()
	{
		int indextabListCfgPara = tabCfgPara.getSelectionIndex();
		
		if (indextabListCfgPara < 0) return;
		
		vCfgPara.remove(indextabListCfgPara);
		
		RefreshCfgPara();
		
		if (vCfgPara.size() > 0)
		{
			if (vCfgPara.size() > indextabListCfgPara)
			{
				tabCfgPara.select(indextabListCfgPara);
			}
			else if (vCfgPara.size() == indextabListCfgPara)
			{
				tabCfgPara.select(indextabListCfgPara -1);
			}
		}
		
		tabCfgPara.showSelection();
	}
	
	public void btnCheckClick()
	{
		boolean isok = true;
		int i = 0;

		for (i = 0;i < this.procdure.ProcPara.size();i++)
		{
			
			ProcParaStruct pps = (ProcParaStruct)procdure.ProcPara.get(i);
			String ppsinouttype = pps.CfgInOutType.equalsIgnoreCase("INOUT")?"OUT":pps.CfgInOutType;
			KeyValueStruct ppsdatatype = new KeyValueStruct();
			ppsdatatype.key = pps.CfgDataType==null?"":pps.CfgDataType.key;
			ppsdatatype.value = pps.CfgDataType==null?"":pps.CfgDataType.value;
			if (ppsdatatype.key.equalsIgnoreCase("i") || ppsdatatype.key.equalsIgnoreCase("l") || ppsdatatype.key.equalsIgnoreCase("f"))
			{
				ppsdatatype.key = "Number";
				//ppsdatatype.value = "Number-数值型";
			}
			
			CmdParaStruct cps = vCfgPara.size()>i?(CmdParaStruct)((Object[])vCfgPara.get(i))[1]:null;
			String cpsinouttype = vCfgPara.size()>i?(String)((Object[])vCfgPara.get(i))[0]:"";
			KeyValueStruct cpsdatatype = new KeyValueStruct();
			cpsdatatype.key = cps==null?"":cps.Type;
			cpsdatatype.value = cps==null?"":cps.TypeDesc;
			if (cpsdatatype.key.equalsIgnoreCase("i") || cpsdatatype.key.equalsIgnoreCase("l") || cpsdatatype.key.equalsIgnoreCase("f"))
			{
				cpsdatatype.key = "Number";
				//cpsdatatype.value = "Number-数值型";
			}
			
			// 参数不等
			if (vCfgPara.size() <= i)
			{
				MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				msgbox.setMessage("配置参数的数量 少于 过程参数的数量!\n\n\n是否继续检查下一项?");
				if (msgbox.open() == SWT.NO)
				{
					isok = false;					
					break;
				}
				
				break;
			}

			// 输入输出参数不一致
			if (!ppsinouttype.equalsIgnoreCase(cpsinouttype))
			{
				MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				msgbox.setMessage("第[" + i + "]行的过程参数与第[" + i + "]行的配置参数的参数方式不一致!\n\n"
				                  + "过程参数:" + pps.Name + "   (" + ppsinouttype + ")\n\n"
				                  + "配置参数:" + cps.Name + "   (" + cpsinouttype + ")\n\n\n" 
				                  + "是否继续检查下一项?");
				if (msgbox.open() == SWT.NO)
				{
					isok = false;					
					break;
				}
			}
			
			// 未知数据类型
			if (pps.CfgDataType == null)
			{
				MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				msgbox.setMessage("第[" + i + "]行的过程参数与第[" + i + "]行的配置参数的数据类型不一致!\n\n"
				                  + "过程参数:" + pps.Name + "   (null)\n\n"
								  + "配置参数:" + cps.Name + "   (" + cps.TypeDesc + ")\n\n\n"
								  + "是否继续检查下一项?"); 
				if (msgbox.open() == SWT.NO)
				{
					isok = false;					
					break;
				}
			}
			
			// 数据类型不一致
			//if (!ppsdatatype.key.equalsIgnoreCase(cpsdatatype.key))
			if (!isMatchDataType(ppsdatatype.key,cpsdatatype.key))
			{
				MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				msgbox.setMessage("第[" + i + "]行的过程参数与第[" + i + "]行的配置参数的数据类型不一致!\n\n"
				                  + "过程参数:" + pps.Name + "   (" + ppsdatatype.value + ")\n\n"			
                				  + "配置参数:" + cps.Name + "   (" + cpsdatatype.value + ")\n\n\n"
                				  + "是否继续检查下一项?");
				if (msgbox.open() == SWT.NO)
				{
					isok = false;					
					break;
				}
			}
		}
		
		if (isok)
		{
			if (i < vCfgPara.size())
			{
				MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				msgbox.setMessage("配置参数的数量 多于 过程参数的数量!\n\n\n是否继续检查下一项?");
				if (msgbox.open() == SWT.NO)
				{
					isok = false;
				}
			}
		}
		else
		{
			tabProcPara.select(i);
			tabProcPara.showSelection();
			
			tabCfgPara.select(i);
			tabCfgPara.showSelection();
		}
		
		//如果不是存储过程则不判断SQL与存储过程的参数
		if (!procdure.IsSql)
		{	
			// procedure para
			ParaNum procpn = new ParaNum();
			
			GetProcParaNum(this.oldprocedure,procpn);
	
			//sql para
			ParaNum sqlpn = new ParaNum();
			
			GetSqlParaNum(sqlpn);
			
			if (procpn.innum + procpn.outnum != sqlpn.allnum)
			{
				MessageBox msgbox1 = new MessageBox(new Shell(),SWT.YES|SWT.NO|SWT.ICON_WARNING);
				msgbox1.setMessage("Sql语句中的参数 " + ((sqlpn.allnum>(procpn.innum+procpn.outnum))?"多于":"少于") + " 过程参数数量，是否自动修改Sql语句!");
				if (SWT.YES == msgbox1.open())
				{
					this.txtTran_XX_Sql.setText(AutoSql());
				}
			}
		}
		/*
		// cfgpara
		ParaNum cfgpn = new ParaNum();
		
		GetCfgParaNum(cfgpn);
		
		if (cfgpn.innum != procpn.innum)
		{
		 	MessageBox msgbox=new MessageBox(new Shell(),SWT.NULL);
			msgbox.setMessage("配置的输出参数与存储过程的输入参数数量不等!");
			msgbox.open();
			isok = false;
		}
		
		if (cfgpn.outnum != procpn.outnum)
		{
		 	MessageBox msgbox=new MessageBox(new Shell(),SWT.NULL);
			msgbox.setMessage("配置的输出与存储过程的输出参数数量不等!");
			msgbox.open();
			isok = false;
		}
		*/
		
		if (isok)
		{
			MessageBox msgbox=new MessageBox(new Shell(),SWT.NULL|SWT.ICON_INFORMATION);
			msgbox.setMessage("检查完成!\n\n配置参数与过程参数相匹配");
			msgbox.open();
		}
	}
	
	public boolean isMatchDataType(String ppskey,String cpskey)
	{
		if (ppskey.equalsIgnoreCase(cpskey)) return true;
		
		if (ppskey.equalsIgnoreCase("c") && cpskey.equalsIgnoreCase("s")) return true;
		
		if (ppskey.equalsIgnoreCase("i") && (cpskey.equalsIgnoreCase("l") || cpskey.equalsIgnoreCase("f"))) return true;

		if (ppskey.equalsIgnoreCase("l") && cpskey.equalsIgnoreCase("f")) return true;
		
		return false;
	}
	
	public void btnAddCfgParaClick()
	{	
		int indextabListCftPara = tabCfgPara.getSelectionIndex();
		
		if (indextabListCftPara < 0)
		{
			indextabListCftPara = vCfgPara.size();
		}
		else
		{
			indextabListCftPara++;
		}
		
		CmdParaStruct cps = new CmdParaStruct();
		cps.Type = "";
		cps.TypeDesc = "";
		
		Object[] objs = new Object[2];
		objs[0] = "IN";
		objs[1] = cps;

		int i = 0;
		while(true)
		{
			int j = 0;
			for (j = 0;j < vCfgPara.size();j++)
			{
				Object[] objs1 = (Object[])vCfgPara.get(j);
				
				CmdParaStruct cps1 = (CmdParaStruct)objs1[1];
				if (cps1.Name.equalsIgnoreCase("NewPara" + String.valueOf(i)))
				{
					break;
				}
			}
			
			if (j >= vCfgPara.size())
			{
				cps.Name = "NewPara" + String.valueOf(i);
				break;
			}
			
			i++;
		}
		
		vCfgPara.add(indextabListCftPara,objs);
		
		RefreshCfgPara();
		
		tabCfgPara.select(indextabListCftPara);
		tabCfgPara.showSelection();
	}
	
	private  class ParaNum
	{
		int innum = 0;
		int outnum = 0;
		int othernum = 0;
		int allnum = 0;
	};
	
	public void GetCfgParaNum(ParaNum pn)
	{
		// cfgpara
		pn.innum = 0;
		pn.outnum = 0;
		pn.othernum = 0;
		pn.allnum = vCfgPara.size();

		for (int i = 0;i < vCfgPara.size();i++)
		{
			Object[] objs = (Object[])vCfgPara.get(i);
			String type = (String)objs[0];
			if (type.equals("IN"))
			{
				pn.innum ++;
			}
			else if (type.equals("OUT"))
			{
				pn.outnum ++;
			}
		}
	}
	
	public void GetProcParaNum(ParaNum pn)
	{
		GetProcParaNum(procdure,pn);
	}
	
	public void GetProcParaNum(ProcStruct proc,ParaNum pn)
	{
		// procpara
		pn.innum = 0;
		pn.outnum = 0;
		pn.othernum = 0;
		pn.allnum = proc.ProcPara.size();
		
		for (int i = 0;i < proc.ProcPara.size();i++)
		{
			ProcParaStruct pps = (ProcParaStruct)proc.ProcPara.get(i);
			String type = pps.CfgInOutType;
			if (type.equals("IN"))
			{
				pn.innum++;
			}
			else if (type.equals("OUT") || type.equals("INOUT"))
			{
				pn.outnum++;
			}
			else 
			{
				pn.othernum++;
			}
		}
	}
	
	public void GetSqlParaNum(ParaNum pn)
	{
		pn.innum = 0;
		pn.outnum = 0;
		pn.othernum = 0;
		pn.allnum = 0;

		StringBuffer sb = new StringBuffer(txtTran_XX_Sql.getText().trim());
		for(int i = 0;i < sb.length();i++)
		{
			if (sb.charAt(i) == '?')
			{
				pn.allnum++;
			}
		}
	}
	
	public void RefreshNum()
	{
		// cfgpara
		ParaNum pn = new ParaNum();
		
		GetCfgParaNum(pn);
		
		this.lbCfgParaNum.setText(pn.innum + "/" + pn.outnum + "/" + pn.allnum);
		
		// procedure para
		GetProcParaNum(pn);

		this.lbProcPara.setText(pn.innum + "/" + pn.outnum + "/" + pn.allnum + (pn.othernum>0?"/" + pn.othernum:""));

		//sql para
		GetSqlParaNum(pn);

		lbTran_XX_SqlNum.setText(String.valueOf(pn.allnum));
	}
	
	public void Init()
	{
		txtTran_XX_Sql.setText(cts.Tran_XX_Sql);
		
		RefreshCfgPara();
		
		GetProcecureInfo();
	}
	
	public void RefreshCfgPara()
	{	
		tabCfgPara.removeAll();
		
		for (int i = 0;i < vCfgPara.size() ; i++)
		{
			Object[] objs = (Object[])vCfgPara.get(i);
			String type = (String)objs[0];
			CmdParaStruct cps = (CmdParaStruct)(objs[1]);
			TableItem ti = new TableItem(tabCfgPara,SWT.NONE);
			ti.setText(new String[]{String.valueOf(i),cps.Name,type,cps.TypeDesc});
		}	
		
		RefreshNum();
	}
	
	public void findLocation(final int vindex)
	{		
		if (tabCfgPara.getItemCount() <= 0) { return; }
		
		TableItem items = tabCfgPara.getItem(currentPoint[1]);
		
		if (currentPoint[0] == 1)
		{	 
			txtNewEditor = new Text(tabCfgPara, SWT.LEFT | SWT.BORDER);
			txtNewEditor.addFocusListener(new FocusAdapter() {
	    		public void focusLost(final FocusEvent arg0) 
	    		{	
	    			txtNewEditor.setVisible(false);
	    			
	    			Object[] objs = (Object[])(vCfgPara.get(vindex));
	    			
	    			String type = (String)objs[0];
	    			
	    			CmdParaStruct cps = (CmdParaStruct)(objs[1]);
					
					Text text = (Text) editor.getEditor();
					
					for (int i = 0;i < vCfgPara.size();i++)
					{
						Object[] objs1 = (Object[])(vCfgPara.get(i));
		    			
		    			String type1 = (String)objs1[0];
		    			
		    			CmdParaStruct cps1 = (CmdParaStruct)(objs1[1]);
		    			
		    			if (cps1.Name.equalsIgnoreCase(text.getText().trim()) && type1.equalsIgnoreCase(type) && i != vindex)
		    			{
		    				System.out.print(i + "," + vindex);
		    				 MessageBox msgbox=new MessageBox(new Shell(),SWT.NULL);
		    			     msgbox.setMessage("你输入的参数名[" + cps1.Name + "]已存在!");
		    			     msgbox.open();
		    			     return;
		    			}
					}
					
					cps.Name = text.getText().trim();
					editor.getItem().setText(currentPoint[0], text.getText().trim());
	    		}
	    		
	    		public void focusGained(final FocusEvent arg0) 
	    		{
	    			txtNewEditor.setVisible(true);
	    		}
	    	});
			txtNewEditor.setText(items.getText(currentPoint[0]));
			editor.setEditor(txtNewEditor, items, currentPoint[0]);
			txtNewEditor.selectAll();
			txtNewEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					
				}
			});

			txtNewEditor.setFocus();
		}
		// table的第二列(参数类型)嵌入combo
		else if (currentPoint[0] == 2)
		{
			cmbNewEditor = new Combo(tabCfgPara, SWT.LEFT | SWT.BORDER | SWT.DROP_DOWN);
			cmbNewEditor.setVisibleItemCount(10);
			cmbNewEditor.addFocusListener(new FocusAdapter() {
				public void focusLost(final FocusEvent arg0) 
	    		{
	    			cmbNewEditor.setVisible(false);
	    			
	    			Object[] objs = (Object[])(vCfgPara.get(vindex));
	    			Combo comb = (Combo) editor.getEditor();
					int index = comb.getSelectionIndex();
					
					if (index >= 0)
					{
						objs[0] = comb.getText();
					}
					
					editor.getItem().setText(currentPoint[0], (String)objs[0]);
	    		}
				
	    		public void focusGained(final FocusEvent arg0) 
	    		{
					Combo comb = (Combo) editor.getEditor();
					
					String str = comb.getText();
					
					//解决获得选择项的问题，他不会自动选择相同的项目
					for(int i = 0;i < comb.getItemCount();i++)
					{
						if (comb.getItem(i).equalsIgnoreCase(str))
						{
							comb.select(i);
							break;
						}
					}
					
	    			cmbNewEditor.setVisible(true);
	    		}
			});
			
			cmbNewEditor.add("IN");
			cmbNewEditor.add("OUT");
			
			cmbNewEditor.setText(items.getText(currentPoint[0]));
			editor.setEditor(cmbNewEditor, items, currentPoint[0]);
			
			cmbNewEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					
				}
			});

			cmbNewEditor.setFocus();
		}
		// table的第三列(参数类型)嵌入combo
		else if (currentPoint[0] == 3)
		{
			cmbNewEditor = new Combo(tabCfgPara, SWT.LEFT | SWT.BORDER | SWT.DROP_DOWN);
			cmbNewEditor.setVisibleItemCount(10);
			cmbNewEditor.addFocusListener(new FocusAdapter() {
	    		public void focusLost(final FocusEvent arg0) 
	    		{
	    			cmbNewEditor.setVisible(false);
	    			
	    			Object[] objs = (Object[])(vCfgPara.get(vindex));
	    			
	    			//String type = (String)objs[0];
	    			
	    			CmdParaStruct cps = (CmdParaStruct)(objs[1]);
					
					Combo comb = (Combo) editor.getEditor();
					
					int index = comb.getSelectionIndex();
					String strparatype = "";
					
					if (index >= 0)
					{
						strparatype = ((KeyValueStruct)(GlobalVar.Paradatatype.get(index))).value;
						cps.Type = ((KeyValueStruct)(GlobalVar.Paradatatype.get(index))).key;
						cps.TypeDesc = strparatype;
					}
					else
					{
						strparatype = comb.getText();
						cps.Type = strparatype;
						cps.TypeDesc = strparatype;
					}
					
					editor.getItem().setText(currentPoint[0], strparatype);
	    		}
	    		
	    		public void focusGained(final FocusEvent arg0) 
	    		{
					Combo comb = (Combo) editor.getEditor();
					
					String str = comb.getText();
					
					//解决获得选择项的问题，他不会自动选择相同的项目
					for(int i = 0;i < comb.getItemCount();i++)
					{
						if (comb.getItem(i).equalsIgnoreCase(str))
						{
							comb.select(i);
							break;
						}
					}
					
	    			cmbNewEditor.setVisible(true);
	    		}
	    	});
			for (int i = 0; i < GlobalVar.Paradatatype.size(); i++)
			{
				KeyValueStruct kvs = (KeyValueStruct)(GlobalVar.Paradatatype.get(i));
				cmbNewEditor.add(kvs.value);
			}
			cmbNewEditor.setText(items.getText(currentPoint[0]));
			editor.setEditor(cmbNewEditor, items, currentPoint[0]);

			cmbNewEditor.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					
				}
			});

			cmbNewEditor.setFocus();
		}
	}
	
	public void btnAutoClick()
	{
		if (this.vCfgPara.size() > 0)
		{
			MessageBox msgbox=new MessageBox(new Shell(),SWT.YES|SWT.NO);
			msgbox.setMessage("自动生成会删除原有参数列表，是否继续!");
			if(msgbox.open() != SWT.YES)
			{
				return;
			}
		}
		
		vCfgPara.clear();
		
		for (int i = 0;i < procdure.ProcPara.size();i++)
		{
			ProcParaStruct pps = (ProcParaStruct)procdure.ProcPara.get(i);
			
			if (pps.CfgInOutType.equalsIgnoreCase("IN"))
			{
				CmdParaStruct cps = new CmdParaStruct();
				cps.Name = pps.Name.replace("@", "");
				cps.Type = pps.CfgDataType == null?"":pps.CfgDataType.key;
				cps.TypeDesc = pps.CfgDataType == null?"":pps.CfgDataType.value;
				
				Object[] objs = new Object[2];
				objs[0] = pps.CfgInOutType;
				objs[1] = cps;
				
				vCfgPara.add(objs);
			}
			else if (pps.CfgInOutType.equalsIgnoreCase("OUT") || pps.CfgInOutType.equalsIgnoreCase("INOUT"))
			{
				CmdParaStruct cps = new CmdParaStruct();
				cps.Name = pps.Name.replace("@","");
				cps.Type = pps.CfgDataType == null?"":pps.CfgDataType.key;
				cps.TypeDesc = pps.CfgDataType == null?"":pps.CfgDataType.value;
				
				Object[] objs = new Object[2];
				objs[0] = "OUT";
				objs[1] = cps;
				
				vCfgPara.add(objs);
			}
		}
		
		// 如果是SQL自动生成SQL 语句
		if (!procdure.IsSql)
		{
			txtTran_XX_Sql.setText(AutoSql());
		}
		
		RefreshCfgPara();
	}
	
	public String AutoSql()
	{
		String strsql = "";
		
		if (!oldprocedure.IsSql)
		{
			ParaNum procpn = new ParaNum();
			
			GetProcParaNum(oldprocedure,procpn);
			
			for (int i = 0;i < (procpn.innum + procpn.outnum);i++)
			{
				strsql = strsql + "?,";
			}
			
			if (strsql.length() > 0)
			{
				strsql = strsql.substring(0,strsql.length() - 1);
			}
			
			strsql = "{call " + (oldprocedure.PackAge.trim().length()<=0?"":(oldprocedure.PackAge + ".")) + procdure.ProcName + "(" + strsql + ")}";
		}
		
		return strsql;
	}
	
	public void btnOkClick()
	{
		cts.Tran_XX_Sql = this.txtTran_XX_Sql.getText().trim();
		cts.Tran_XX_Para.clear();
		cts.Tran_XX_Col.clear();
		
		for (int i = 0;i < vCfgPara.size();i++)
		{
			Object[] objs = (Object[])vCfgPara.get(i);
			
			String type = (String)objs[0];
			String isglobal = objs.length >= 3? (String)objs[2]:"N";
			
			if (!isglobal.equals("Y"))
			{
				CmdParaStruct cps = (CmdParaStruct)objs[1];
				
				if (type.equalsIgnoreCase("IN"))
				{
					this.cts.Tran_XX_Para.add(cps);
				}
				else if (type.equalsIgnoreCase("OUT"))
				{
					this.cts.Tran_XX_Col.add(cps);
				}
			}
		}
		
		shell.close();
	}
	
	public void btnCancelClick()
	{
		shell.close();
	}
	
	public void cmbdatasourenameSelectedIndexChange()
	{
		GetProcecureInfo();
	}
	
	public boolean IsValidSql(String sql)
	{
		if (sql.length()<10)
		{
			return false;
		}
		
		if (!sql.startsWith("{") || !sql.endsWith("}"))
		{
			return false;
		}
		
		int indexstar = sql.indexOf('(');
		int indexend = sql.indexOf(')');
		
		if (indexstar < 0 || indexend < 0 || indexend <= indexstar)
		{
			return false;
		}
		
		if (!sql.substring(0,5).equalsIgnoreCase("{call"))
		{
			return false;
		}
		
		String procedurename = sql.substring(5,indexstar).trim();
		
		if (procedurename.length() <= 0)
		{
			return false;
		}
		
		return true;
	}
	
	public String GetProcedureName(String sql)
	{
		int indexstar = sql.indexOf('(');
		String procedurename = sql;
		if (sql.length() > 6 && sql.substring(0,6).equalsIgnoreCase("{call "))
		{
			if (indexstar > 6)
			{
				procedurename = sql.substring(6,indexstar);
			}
			else
			{
				procedurename = sql.substring(6,sql.length());
			}
		}
		else
		{
			if (indexstar > 0)
			{
				procedurename = sql.substring(0,indexstar);
			}
		}
		
		return procedurename;
	}
}
