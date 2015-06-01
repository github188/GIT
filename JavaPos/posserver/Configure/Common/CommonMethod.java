package posserver.Configure.Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


class CmdExecStream extends Thread 
{
	InputStream is;

	CmdExecStream(InputStream is)
	{
		this.is = is;
	}

	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while (br.readLine() != null);
			br.close();
		}
		catch (Exception ioe) 
		{
		}
	}
}	

public class CommonMethod
{
	public static void waitForExec(String cmd) throws Exception
	{
		Process p = Runtime.getRuntime().exec(cmd);
		
        if (p != null)
        {
        	// 
        	CmdExecStream errorStream = new CmdExecStream(p.getErrorStream());            
        	CmdExecStream outputStream = new CmdExecStream(p.getInputStream());
        	errorStream.start();
        	outputStream.start();
        	
        	// 等待
            p.waitFor();
        }
	}
	
	public static void waitForExec(String cmd,String exefile) throws Exception
	{
		Process p = Runtime.getRuntime().exec(cmd);
		
        if (p != null)
        {
        	CmdExecStream errorStream = new CmdExecStream(p.getErrorStream());            
        	CmdExecStream outputStream = new CmdExecStream(p.getInputStream());
        	errorStream.start();
        	outputStream.start();
        	
        	// 等待
            waitForProcessExit(exefile);
            p.waitFor();
        }
	}
	
	public static void waitForProcessExit(String exefile)
	{
		try
		{
			// 先要找到进程为止,表示进程已运行
			while(!findProcess(exefile));
			
			// 然后再等待进程结束
			while(findProcess(exefile))
			{
				while (Display.getCurrent().readAndDispatch());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static String GetCurrentPath()
	{
		String str = System.getProperty("user.dir");

		if (str.length() >0 && (str.charAt(str.length()-1) == '\\' || str.charAt(str.length()-1) == '/'))
		{
			str = str.substring(0,str.length() - 1);
		}
		
		return str;
	}
	
	public static boolean findProcess(String exefile) throws Exception
	{
		boolean ret = false;
		
		Process p = Runtime.getRuntime().exec("ProcessList.exe");
		
        if (p != null)
        {
        	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream())); 
        	String line = ""; 
        	while((line = input.readLine()) != null)
        	{
        		//System.out.println(line);
        		if (line.toLowerCase().indexOf(exefile.toLowerCase()) >= 0 && !ret)
        		{
        			String[] s = line.split(",");
        			if (s.length >= 4)
        			{
	        			int hwnd = Integer.parseInt(s[2],16);
	        			if (OS.GetForegroundWindow() != hwnd)
	        			{
	        				//System.out.println("==============SetForegroundWindow===========");
	        				OS.SetForegroundWindow(hwnd);
	        			}
        			}
        			
        			ret = true;
        		}
        	} 
        	input.close();
        }
        
        return ret;
	}
	
	public static BufferedReader readFileGB2312(String name)
	{
        BufferedReader br = null;
        try
        {
            br   = new BufferedReader( new InputStreamReader(new FileInputStream(new File(name)),"GB2312"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return br;
	}
	
	public static BufferedReader readFileGBK(String name)
	{
        BufferedReader br = null;

        try
        {
            br   = new BufferedReader(new InputStreamReader(new FileInputStream(new File(name)), "GBK"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return br;
	}
	
    public static BufferedReader readFile(String name)
    {
        BufferedReader br = null;

        try
        {
            br   = new BufferedReader( new InputStreamReader(new FileInputStream(new File(name)),"UTF-8"));
            
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return br;
    }
    
    public static boolean isFileExist(String name)
    {
    	try
    	{	
    		return new File(name).exists();
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    		return false;
    	}
    }
    
    public static PrintWriter writeFileAppend(String name)
    {
        try
        {
        	return new PrintWriter(new BufferedWriter(new FileWriter(name, true)));
            
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public static PrintWriter writeFileUTF(String name)
    {
        try
        {
        	return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(name)),"UTF-8")));
        }
        catch (IOException e)
        {
            return null;
        }
    }
    public static PrintWriter writeFile(String name)
    {
        try
        {
            return new PrintWriter(new BufferedWriter(new FileWriter(name)),
                                   true);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public static String cutSquareBracket(String line)
    {
        return line.substring(line.indexOf("[") + 1, line.indexOf("]"));
    }

    public static String isNull(String base,String def)
    {
    	if (base == null || base.trim().equals("")) return def;
    	else return base;
    }

    public static boolean isNull(Object base)
    {
    	if (base == null || base.toString().trim().equals("")) return true;
    	else return false;
    }    
    
    public static boolean noEmpty(String str)
    {
    	if (isNull(str,"").length() <= 0) return false;
    	else return true;
    }
    
    public static String getInsertSql(String tabname,String[] ref)
    {
    	int i;
    	String sql = "insert into " + tabname + "(";
    	String line;
    	
    	// 列名
    	line = "";
        for (i = 0; i < ref.length; i++)
        {
        	line += ("," + ref[i]);
        }    	
        sql += line.substring(1);
        sql += ") values(";
        
        line = "";
        for (i = 0; i < ref.length; i++)
        {
            line += ",?";
        }        
        sql += line.substring(1);
        sql += ")";
        
        return sql;
    }
    
    //打开目录对话框
    public static boolean openDirectory(Shell shell,Text txtSourceDirectory)
    {
    	//创建一个打开对话框,样式设置为SWT.OPEN
		DirectoryDialog dialog = new DirectoryDialog(shell,SWT.OPEN);
		
		String str = txtSourceDirectory.getText().trim();
		if (str.length() > 0)
		{
			//设置打开默认的路径
			dialog.setFilterPath(str);
		}
		else
		{
			//设置打开默认的路径
			dialog.setFilterPath(".");
		}
		
		//打开窗口,返回用户所选的文件目录
		String directory = dialog.open();
		
		if (directory != null)
		{
			txtSourceDirectory.setText(directory);
			return true;
		}
		
		return false;
    }
    
	//打开文件对话框
	public static void openFileDialog(Shell shell,Text txtSourceFile)
	{
		openFileDialog(shell,txtSourceFile,new String[]{"*.ini","*.*"},new String[]{"INI Files(*.ini)","ALL Files(*.*)"});
	}
	
	//打开文件对话框
	public static boolean openFileDialog(Shell shell,Text txtSourceFile,String[] extendsvalue,String[] extenddescribe)
	{
		//创建一个打开对话框,样式设置为SWT.OPEN
		FileDialog dialog = new FileDialog(shell,SWT.OPEN);
		
		String str = txtSourceFile.getText().trim();
		if (str.length() > 0)
		{
			//设置打开默认的路径
			int i = str.replace("\\", "/").lastIndexOf('/');
			if (i >= 0) str = str.substring(0,i);
			dialog.setFilterPath(str);
		}
		else
		{
			//设置打开默认的路径
			dialog.setFilterPath(".");
		}
		
		if (extendsvalue != null)
		{
			//设置所打开文件的扩展名
			dialog.setFilterExtensions(extendsvalue);
			
			//设置显示到下拉框中的扩展名的名称
			dialog.setFilterNames(extenddescribe);
		}
			
		//打开窗口,返回用户所选的文件目录
		String file = dialog.open();
		
		if (file != null)
		{
			txtSourceFile.setText(file);
			return true;
		}
		
		return false;
	}
	
	/**
	   * 复制整个文件夹内容
	   * @param oldPath String 原文件路径 如：c:/fqf
	   * @param newPath String 复制后路径 如：f:/fqf/ff
	   * @param iscover boolean 文件已存在是否覆盖
	   * @return boolean
	   */
	  public static boolean CopyFolder(String oldPath, String newPath,Label lbinfo,boolean iscover) {

	    try 
	    {
	      (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
	      File a=new File(oldPath);
	      String[] file=a.list();
	      File temp=null;
	      for (int i = 0; i < file.length; i++) 
	      {
	        if(oldPath.endsWith(File.separator))
	        {
	          temp=new File(oldPath+file[i]);
	        }
	        else
	        {
	          temp=new File(oldPath+File.separator+file[i]);
	        }

	        if(temp.isFile())
	        {
	          if (!iscover)
	          {
	        	  File tfile = new File(newPath + "/" + (temp.getName()).toString());
	        	  if (tfile.exists()) continue;
	          }
	          
	          if (lbinfo != null)
	          {
	        	 lbinfo.setText("正在拷备文件" + temp.getName()+ ",请等待...");
	          }
	          
	          FileInputStream input = new FileInputStream(temp);
	          FileOutputStream output = new FileOutputStream(newPath + "/" +
	              (temp.getName()).toString());
	          byte[] b = new byte[1024 * 5];
	          int len;
	          while ( (len = input.read(b)) != -1) {
	            output.write(b, 0, len);
	          }
	          output.flush();
	          output.close();
	          input.close();
	        }
	        
	        if(temp.isDirectory())
	        {
	        	//如果是子文件夹
	          CopyFolder(oldPath+"/"+file[i],newPath+"/"+file[i],lbinfo,iscover);
	        }
	      }
	    }
	    catch (Exception e) 
	    {
	      System.out.println("复制整个文件夹内容操作出错");
	      e.printStackTrace();
	      return false;

	    }
	    
	    return true;

	  }
}
