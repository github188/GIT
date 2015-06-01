package com.efuture.commonKit;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;


//用来操作数据库的类
public class Sqldb
{
    private Connection conn = null;
    private PreparedStatement ps = null;
    private Statement st = null;
    private ResultSet rs = null;
    private String jdbcdriver = null;
    private String connurl = null;
    private String user = null;
    private String pwd = null;
    private int m_nTransCount = 0;
    private String errorlog = null;
    private int affectRow;
    private final String errorfoone = Language.apply("数据连接未打开");
    private boolean outsideps = false;
    private boolean isDisConnection = false;
    
    public Sqldb()
    {
    }

    public Sqldb(String jdbcdriver, String connurl)
    {
        startCreate(jdbcdriver, connurl);
    }
    
    public Sqldb(String jdbcdriver, String connurl,String user,String pwd)
    {
        startCreate(jdbcdriver, connurl, user, pwd);
    }

    public PreparedStatement getPS()
    {
    	return ps;
    }
    
    public Connection getConnection()
    {
    	return conn;
    }
    
    public boolean getIsDisConnection()
    {
    	return isDisConnection;
    }
    
    public boolean startCreate(String jdbcdriver, String connurl)
    {
    	return startCreate(jdbcdriver, connurl,null,null);
    }
    
    public boolean startCreate(String jdbcdriver, String connurl,String user,String pwd)
    {
    	return startCreate(jdbcdriver, connurl, user, pwd, true);
    }

    public boolean startCreate(String jdbcdriver, String connurl,String user,String pwd, boolean isShowMsg)
    {
        try
        {
        	isDisConnection=false;
        	
            this.jdbcdriver = jdbcdriver;
            this.connurl    = connurl;
            this.user = user;
            this.pwd = pwd;
            Class.forName(jdbcdriver);
            if (user != null)
            {
            	conn = DriverManager.getConnection(connurl,user,pwd);
            	//conn = DriverManager.getConnection("jdbc:microsoft:sqlserver://172.17.8.194:1433;DatabaseName=JavaPos;user=sa;password=sa;");
            }
            else
            {
            	conn = DriverManager.getConnection(connurl);
            }
            /*            
            st   = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY);
             */
            st   = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    					ResultSet.CONCUR_READ_ONLY);
            return true;
        }
        catch (Exception ex)
        {
        	isDisConnection = true;
            ex.printStackTrace();
            if (isShowMsg) this.setErrorlog(ex.getMessage());

            return false;
        }
    }
    
    public boolean isOpen()
    {
        try
        {
            if ((conn != null) && !conn.isClosed())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }

    public ResultSet selectData()
    {
    	return selectData(false);
    }
    
    public ResultSet selectData(boolean outsiders)
    {
        try
        {
            if (!isOpen())
            {
                if (!Open())
                {
                    return null;
                }
            }

            if (!outsiders)
            {
            	rs = ps.executeQuery();
            	return rs;
            }
            else
            {
            	return ps.executeQuery();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return null;
        }
    }

    public ResultSet selectData(String sql)
    {
    	return selectData(sql,false);
    }
    
    public ResultSet selectData(String sql,boolean outsiders)
    {
        try
        {
            if (!isOpen())
            {
                if (!Open())
                {
                    return null;
                }
            }

            if (!outsiders)
            {
            	rs = st.executeQuery(sql);
            	return rs;
            }
            else
            {
            	return st.executeQuery(sql);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return null;
        }
    }

    public Object selectOneData(String sql)
    {
    	ResultSet lrs = null;
    	
        try
        {
            if (!isOpen())
            {
                if (!Open())
                {
                    return null;
                }
            }

            lrs = st.executeQuery(sql);

            if (lrs != null && lrs.next())
            {
            	return lrs.getObject(1);
            }
            else
            {
                return null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return null;
        }
        finally
        {
            resultSetClose(lrs);
        }
    }

    public Object selectOneData()
    {
    	ResultSet lrs = null;

        try
        {
            if (!isOpen())
            {
                if (!Open())
                {
                    return null;
                }
            }
            
            lrs = ps.executeQuery();

            if (lrs != null && lrs.next())
            {
                return lrs.getObject(1);
            }
            else
            {
                return null;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return null;
        }
        finally
        {
            resultSetClose(lrs);
        }
    }

    public boolean executeSql(String sql)
    {
        try
        {
            if (!isOpen())
            {
                if (!Open())
                {
                    return false;
                }
            }

            setAffectRow(st.executeUpdate(sql));

            return true;
        }
        catch (Exception ex)
        {
            rollbackTrans();
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }

    public boolean executeSql()
    {
        try
        {
            if (!isOpen())
            {
                if (!Open())
                {
                    return false;
                }
            }

            setAffectRow(ps.executeUpdate());

            return true;
        }
        catch (Exception ex)
        {
            rollbackTrans();
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }

    private boolean setSql(String sql,PreparedStatement sps)
    {
        try
        {
            if (!isOpen())
            {
                if (!Open())
                {
                    return false;
                }
            }

            if (sps == null)
            {
	            outsideps = false;
	            
	            ClosePS();
/*                
	            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
	                                       ResultSet.CONCUR_READ_ONLY);
*/
	            ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                        				   ResultSet.CONCUR_READ_ONLY);                
	        }
            else
            {
            	outsideps = true;
            	
            	ps = sps;
            }
            
            return true;
        }
        catch (Exception ex)
        {
            rollbackTrans();
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }

    public boolean setSql(String sql)
    {
    	return setSql(sql,null);
    }
    
    public boolean setSql(PreparedStatement sps)
    {
    	return setSql(null,sps);
    }
    
    public void paramSetInt(int index, int value)
    {
        try
        {
            if (ps != null)
            {
                ps.setInt(index, value);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());
        }
    }

    public void paramSetFloat(int index, float value)
    {
        try
        {
            if (ps != null)
            {
                ps.setFloat(index, value);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());
        }
    }

    public void paramSetDouble(int index, double value)
    {
        try
        {
            if (ps != null)
            {
                ps.setDouble(index, value);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());
        }
    }

    public void paramSetString(int index, String value)
    {
        try
        {
            if (ps != null)
            {
                ps.setString(index, value);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());
        }
    }

    public void paramSetChar(int index, char value)
    {
        paramSetString(index, String.valueOf(value));
    }

    public void paramSetLong(int index, long value)
    {
        try
        {
            if (ps != null)
            {
                ps.setLong(index, value);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());
        }
    }

    public boolean beginTrans()
    {
        try
        {
            if (isOpen())
            {
                if (m_nTransCount > 0)
                {
                    rollbackTrans();
                }

                conn.setAutoCommit(false);
                m_nTransCount++;

                return true;
            }
            else
            {
                this.setErrorlog(errorfoone);

                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }
    
    public boolean commitTrans()
    {
        try
        {
            if (isOpen())
            {
                if (m_nTransCount > 0)
                {
                    conn.commit();
                    m_nTransCount = 0;
                }

                conn.setAutoCommit(true);

                return true;
            }
            else
            {
                this.setErrorlog(errorfoone);

                return false;
            }
        }
        catch (Exception ex)
        {
            rollbackTrans();
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
        finally
        {
            try
            {
                resultSetClose();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                this.setErrorlog(ex.getMessage());
            }
        }
    }

    public boolean rollbackTrans()
    {
        try
        {
            if (isOpen())
            {
                if (m_nTransCount > 0)
                {
                    conn.rollback();
                    m_nTransCount = 0;
                }

                conn.setAutoCommit(true);

                return true;
            }
            else
            {
                this.setErrorlog(errorfoone);

                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
        finally
        {
            try
            {
                resultSetClose();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                this.setErrorlog(ex.getMessage());
            }
        }
    }

    private boolean Open()
    {
        try
        {
            if ((jdbcdriver != null) && !jdbcdriver.equals("") &&
                    (connurl != null) && !connurl.equals(""))
            {
                startCreate(this.jdbcdriver, this.connurl, this.user, this.pwd);

                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }

    public boolean resultSetClose()
    {
    	if (resultSetClose(rs))
    	{
    		rs = null;
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public boolean resultSetClose(ResultSet lrs)
    {
        try
        {
            if (lrs != null)
            {
            	lrs.close();
            	lrs = null;
            }

            if (ps != null)
            {
            	if (!outsideps)
            	{
            		ClosePS();
            	}
            	else
            	{
            		ps = null;
            	}
            }

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }

    public boolean Close()
    {
        try
        {
            resultSetClose();

            CloseST();

            if (conn != null)
            {
                conn.close();
                conn = null;
            }

            //
            System.gc();

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            this.setErrorlog(ex.getMessage());

            return false;
        }
    }
    
    private void CloseST()
    {
    	try
    	{
    		if (st != null)
            {
                st.close();
                st = null;
            }
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		st = null;
    	}
    }
    
    private void ClosePS()
    {
    	try
    	{
    		if (ps != null)
    		{
	    		ps.close();
	            ps = null;
    		}
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		ps = null;
    	}
    }
    
    private void setErrorlog(String errorlog)
    {
		this.errorlog = errorlog;
        
		new MessageBox(this.connurl + Language.apply(" 数据库访问异常:\n\n") + errorlog, null, false);	      
    }

    public String getErrorlog()
    {
        return errorlog;
    }

    private void setAffectRow(int affectRow)
    {
        this.affectRow = affectRow;
    }

    public int getAffectRow()
    {
        return affectRow;
    }

    public boolean getResultSetToObject(Object obj)
    {
    	return getResultSetToObject(obj,rs);
    }
    
    public boolean getResultSetToObject(Object obj,ResultSet rs)
    {
    	String[] ref = null;
    	
    	try
    	{
    		ref = (String[]) obj.getClass().getDeclaredField("ref").get(obj);
    	}
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    	
    	return getResultSetToObject(obj,ref,rs);
    }
    
    public boolean getResultSetToObject(Object obj,String[] ref)
    {
    	return getResultSetToObject(obj,ref,rs);
    }
    
    public boolean getResultSetToObject(Object obj,String[] ref,ResultSet rs)
    {
        Field field1 = null;
        Class classInst = obj.getClass();
        int i=0,j=0;
        
        try
        {
        	
            for (i = 0; i < ref.length; i++)
            {
                if (ref[i].length() <= 0)
                {
                    continue;
                }
                
               
                field1 = classInst.getDeclaredField(ref[i]);
                
                try
                {
                	j = rs.findColumn(ref[i]);
                }
                catch(Exception ex)
                {
                	continue;
                }
                
                try
                {
	                if (field1.getType().getName().equals("char"))
	                {
	                	String s = (rs.getObject(j) != null?rs.getString(j):null);
	                	if (s != null && s.trim().length() > 0) field1.setChar(obj, s.charAt(0));
	                	else field1.setChar(obj, '\0');
	                }
	                else if (field1.getType().getName().equals("int"))
	                {
	                	field1.setInt(obj, rs.getInt(j));
	                }
	                else if (field1.getType().getName().equals("double"))
	                {
	                    field1.setDouble(obj,rs.getDouble(j));
	                }
	                else if (field1.getType().getName().equals("float"))
	                {
	                    field1.setFloat(obj, rs.getFloat(j));
	                }
	                else if (field1.getType().getName().equals("long"))
	                {
	                    field1.setLong(obj, rs.getLong(j));
	                }
	                else if (field1.getType().getName().equals("java.lang.String"))
	                {
	                	Object data = rs.getObject(j);
	                	if (data != null) field1.set(obj, data.toString());
	                	else field1.set(obj, null);
	                }
	                else
	                {
	                	Object data = rs.getObject(j);
	                	if (data != null) field1.set(obj, data.toString());
	                	else field1.set(obj, null);
	                }
                }
                catch(Exception er)
                {
                 	er.printStackTrace();
                	continue;
                }
            }
           
            return true;
        }
        catch (Exception e)
        {
        	e.printStackTrace();

        	new MessageBox(Language.apply("结果集转换{0}数据对象的\n{1}成员时发生错误!",new Object[]{classInst.getName(),ref[i]}));
//        	new MessageBox("结果集转换" + classInst.getName() +"数据对象的\n"+ref[i]+"成员时发生错误!");
        	
            return false;        	
        }
    }
    
    public boolean getResultSetToObject_A(Object obj,String[] ref,ResultSet rs)
    {
        Field field1 = null;
        Class classInst = obj.getClass();
        int i=0,j=0;
        
        try
        {
        	
            for (i = 0; i < ref.length; i++)
            {
                if (ref[i].length() <= 0)
                {
                    continue;
                }
                
               
                field1 = classInst.getDeclaredField(ref[i]);
                
                try
                {
                	j = i;
                }
                catch(Exception ex)
                {
                	continue;
                }
                
                try
                {
	                if (field1.getType().getName().equals("char"))
	                {
	                	String s = (rs.getObject(j) != null?rs.getString(j):null);
	                	if (s != null && s.trim().length() > 0) field1.setChar(obj, s.charAt(0));
	                	else field1.setChar(obj, '\0');
	                }
	                else if (field1.getType().getName().equals("int"))
	                {
	                	field1.setInt(obj, rs.getInt(j));
	                }
	                else if (field1.getType().getName().equals("double"))
	                {
	                    field1.setDouble(obj,rs.getDouble(j));
	                }
	                else if (field1.getType().getName().equals("float"))
	                {
	                    field1.setFloat(obj, rs.getFloat(j));
	                }
	                else if (field1.getType().getName().equals("long"))
	                {
	                    field1.setLong(obj, rs.getLong(j));
	                }
	                else if (field1.getType().getName().equals("java.lang.String"))
	                {
	                	Object data = rs.getObject(j);
	                	if (data != null) field1.set(obj, data.toString());
	                	else field1.set(obj, null);
	                }
	                else
	                {
	                	Object data = rs.getObject(j);
	                	if (data != null) field1.set(obj, data.toString());
	                	else field1.set(obj, null);
	                }
                }
                catch(Exception er)
                {
                 	er.printStackTrace();
                	continue;
                }
            }
           
            return true;
        }
        catch (Exception e)
        {
        	e.printStackTrace();

        	new MessageBox(Language.apply("结果集转换{0}数据对象的\n{1}成员时发生错误!",new Object[]{classInst.getName(),ref[i]}));
//        	new MessageBox("结果集转换" + classInst.getName() +"数据对象的\n"+ref[i]+"成员时发生错误!");
        	
            return false;        	
        }
    }
  
    public boolean setObjectToParam(Object obj,String[] ref)
    {
        Field field1 = null;
        Class classInst = obj.getClass();
        int i = 0;
        
        try
        {
            for (i = 0; i < ref.length; i++)
            {
                if (ref[i].length() <= 0)
                {
                    continue;
                }

                field1 = classInst.getDeclaredField(ref[i]);

                if (field1.getType().getName().equals("char"))
                {
                    paramSetChar(i + 1, field1.getChar(obj));
                }
                else if (field1.getType().getName().equals("int"))
                {
                    paramSetInt(i + 1, field1.getInt(obj));
                }
                else if (field1.getType().getName().equals("double"))
                {
                    paramSetDouble(i + 1, field1.getDouble(obj));
                }
                else if (field1.getType().getName().equals("float"))
                {
                    paramSetFloat(i + 1, field1.getFloat(obj));
                }
                else if (field1.getType().getName().equals("long"))
                {
                    paramSetLong(i + 1, field1.getLong(obj));
                }
                else if (field1.getType().getName().equals("java.lang.String"))
                {
                	String s = (String) field1.get(obj);
                	if (s == null) s = "";
                    paramSetString(i + 1, s);
                }
                else
                {
                	String s = (String) field1.get(obj);
                	if (s == null) s = "";
                    paramSetString(i + 1, s);
                }                	
            }

            return true;
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	
        	new MessageBox(Language.apply("数据对象 {0} 的\n{1}成员转换参数时发生错误!",new Object[]{classInst.getName(),ref[i]}));
//        	new MessageBox("数据对象 "+classInst.getName()+" 的\n"+ref[i]+" 成员转换参数时发生错误!");
        	
        	return false;
        }
    }
    
    public String[] getTableColumns(String table)
    {
    	ResultSet rs = selectData("select * from " + table);
    	if (rs == null) return null;
    	
    	String[] cols = null;
    	try
		{
			ResultSetMetaData s = rs.getMetaData();
			cols = new String[s.getColumnCount()];
			for (int i=0;i<s.getColumnCount();i++)
			{
				cols[i] = s.getColumnName(i+1).toLowerCase();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			resultSetClose();
		}
		
		return cols;
    }
    
    public boolean isTableExist(String tabname)
    {
    	Object obj = null;
    	
    	if (ConfigClass.LocalDBType.equalsIgnoreCase("SQLite"))
		{
    		obj = selectOneData("select tbl_name from sqlite_master where type = 'table' and (tbl_name = '"+tabname.toLowerCase()+"' or tbl_name = '"+tabname.toUpperCase()+"')");
		}
		else
		{
			obj = selectOneData("select TABLENAME from SYS.SYSTABLES where TABLETYPE = 'T' and (TABLENAME = '"+tabname.toLowerCase()+"' or TABLENAME = '"+tabname.toUpperCase()+"')");
		}
    	if (obj != null) return true;
    	else return false;
    }

    public boolean isColumnExist(String columnName,String tableName)
    {
    	try
    	{
    		String cols[] = getTableColumns(tableName);
    		if (cols != null)
    		{
        		for (int i=0;i<cols.length;i++)
        		{
        			if (cols[i].equalsIgnoreCase(columnName)) return true;
        		}
    		}
			return false;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    
    }
}
