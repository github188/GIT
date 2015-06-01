package registerPos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.PathFile;
import com.swtdesigner.SWTResourceManager;

/**
 * @author Administrator
 * @create 2010-3-4 下午03:55:04
 * @descri 文件说明
 */

public class ExportJarVersion
{

	private Text textBank;
	private Text textVersion;
	private Text textDevice;
	private Text textJavapos;
	private Text textLocalize;
	protected Shell shell;
	
	private static String svncmd = null;
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args != null && args.length > 0) svncmd = args[0];
			
			ExportJarVersion window = new ExportJarVersion();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();
		
		// 读取SVN版本信息
		readSVNVersion();
		
        Rectangle rec = Display.getCurrent().getClientArea();
        shell.setLocation((rec.width - shell.getSize().x) / 2,
                           (rec.height - shell.getSize().y)/ 2);
        
		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell();
		shell.setSize(675, 252);

		final Label javaposjarLabel = new Label(shell, SWT.NONE);
		javaposjarLabel.setBounds(8, 47, 91, 30);
		javaposjarLabel.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		javaposjarLabel.setText("基类模块:");

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(8, 87, 91, 30);
		label_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1.setText("客户模块:");

		textLocalize = new Text(shell, SWT.BORDER);
		textLocalize.setTextLimit(4);
		textLocalize.setBounds(104, 90, 423, 25);
		textLocalize.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		textJavapos = new Text(shell, SWT.BORDER);
		textJavapos.setText("com.efuture.javaPos.AssemblyInfo");
		textJavapos.setBounds(105, 50, 422, 25);
		textJavapos.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Label label_1_1 = new Label(shell, SWT.NONE);
		label_1_1.setBounds(8, 130, 91, 30);
		label_1_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_1.setText("设备模块:");

		textDevice = new Text(shell, SWT.BORDER);
		textDevice.setText("device.DeviceInfo");
		textDevice.setBounds(104, 133, 423, 25);
		textDevice.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Label javaposjarLabel_1 = new Label(shell, SWT.NONE);
		javaposjarLabel_1.setBounds(8, 10, 91, 30);
		javaposjarLabel_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		javaposjarLabel_1.setText("当前版本:");

		textVersion = new Text(shell, SWT.BORDER);
		textVersion.setBounds(105, 13, 422, 25);
		textVersion.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Button btnJavapos = new Button(shell, SWT.NONE);
		btnJavapos.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				writeVersion("javapos");
			}
		});
		btnJavapos.setBounds(533, 46, 117, 30);
		btnJavapos.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		btnJavapos.setText("写入版本信息");

		final Button btnLocalize = new Button(shell, SWT.NONE);
		btnLocalize.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				writeVersion("localize");
			}
		});
		btnLocalize.setBounds(533, 87, 117, 30);
		btnLocalize.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		btnLocalize.setText("写入版本信息");

		final Button btnDevice = new Button(shell, SWT.NONE);
		btnDevice.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				writeVersion("device");
			}
		});
		btnDevice.setBounds(533, 130, 117, 30);
		btnDevice.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		btnDevice.setText("写入版本信息");

		final Label label_1_1_1 = new Label(shell, SWT.NONE);
		label_1_1_1.setBounds(8, 175, 91, 30);
		label_1_1_1.setFont(SWTResourceManager.getFont("Tahoma", 15, SWT.NONE));
		label_1_1_1.setText("银联模块:");

		textBank = new Text(shell, SWT.BORDER);
		textBank.setBounds(104, 178, 423, 25);
		textBank.setText("bankpay.BankInfo");
		textBank.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));

		final Button btnBank = new Button(shell, SWT.NONE);
		btnBank.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				writeVersion("bankpay");
			}
		});
		btnBank.setBounds(533, 175, 117, 30);
		btnBank.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NONE));
		btnBank.setText("写入版本信息");
		//
	}

	void readSVNVersion()
	{
		if (svncmd == null) svncmd = "C:\\Program Files\\TortoiseSVN\\bin\\SubWCRev.exe";
		
    	try
		{
			Process p = Runtime.getRuntime().exec(svncmd + " \"" + System.getProperty("user.dir") + "\"");
	        if (p == null)
	        {
				MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
				mb.setMessage(svncmd + "\n\n文件不存在，不能获取SVN版本号");
				mb.open();
	        }
	        else
	        {
	        	BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream())); 
	        	String line = ""; 
	
				while((line = input.readLine()) != null)
				{
					System.out.println(line);
					
					if (line.startsWith("Last committed at revision"))
					{
						String s = line.substring("Last committed at revision".length()).trim() + " build " + ManipulateDateTime.getCurrentDate().replaceAll("/",".");
						textVersion.setText(s);
					}
				}
	
	        	input.close();
	        }
		}
		catch (IOException ex)
		{
			MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
			mb.setMessage(svncmd + "\n\n文件执行异常，不能获取SVN版本号\n\n"+ex.getMessage());
			mb.open();
		} 
	}
	
	void writeVersion(String name)
	{
		String filename = "",tempname = "versionjava.txt";
		if (name.equalsIgnoreCase("javapos"))
		{
			filename = textJavapos.getText().replaceAll("\\.","/")+".java";
		}
		else if (name.equalsIgnoreCase("localize"))
		{
			if (textLocalize.getText().trim().length() < 4)
			{
				MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
				mb.setMessage("请输入4位客户化模块编码!");
				mb.open();
				return;
			}
			
        	String module = Character.toUpperCase(textLocalize.getText().trim().charAt(0)) + textLocalize.getText().trim().substring(1).toLowerCase();
        	StringBuffer className = new StringBuffer();
        	className.append("custom.localize.");
        	className.append(module);
        	className.append(".");
        	className.append(module);
        	className.append("_CustomLocalize");
        	filename = className.toString().replaceAll("\\.","/")+".java";
		}
		else if (name.equalsIgnoreCase("device"))
		{
			filename = textDevice.getText().replaceAll("\\.","/")+".java";
		}
		else if (name.equalsIgnoreCase("bankpay"))
		{
			filename = textBank.getText().replaceAll("\\.","/")+".java";
		}
		else
		{
			MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
			mb.setMessage(name + " 模式不正确!");
			mb.open();
			return;
		}
		
        BufferedReader br = null;
        BufferedWriter bw = null;
        try
        {
            br = new BufferedReader( new InputStreamReader(new FileInputStream(new File(filename)),"UTF-8"));
            bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(new File(tempname)),"UTF-8"));
            
            if (br == null)
            {
    			MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
    			mb.setMessage(filename + "\n\n文件打开失败!");
    			mb.open();
    			return;
            }
            
            if (bw == null)
            {
    			MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
    			mb.setMessage(tempname + "\n\n文件创建失败!");
    			mb.open();
    			return;
            }
            
            String line = ""; 
			while((line = br.readLine()) != null)
			{
				if (name.equalsIgnoreCase("javapos"))
				{
					if (line.indexOf("AssemblyVersion") >= 0)
					{
						String[] s = line.split("=");
						line = s[0] + "= " + "\"" + textVersion.getText().trim() + "\";";
					}
				}
				else 
				{
					if (line.indexOf("getAssemblyVersion()") >= 0)
					{
						bw.write(line);
						bw.write("\r\n");
						bw.flush();
						
						while((line = br.readLine()) != null)
						{
							if (line.indexOf("}") >= 0) break;
							if (line.indexOf("return") >= 0)
							{
								String[] s = line.split("\"");
								line = s[0] + "\"" + textVersion.getText().trim() + "\";";
							}
							
							bw.write(line);
							bw.write("\r\n");
							bw.flush();
						}
					}
				}
				
				bw.write(line);
				bw.write("\r\n");
				bw.flush();
			}
			
			br.close();br = null;
			bw.close();bw = null;
			
			// 拷贝
			PathFile.copyPath(tempname,filename);
			
			MessageBox mb = new MessageBox(shell,SWT.ICON_INFORMATION|SWT.OK);
			mb.setMessage(filename + "\n\n文件版本信息写入成功!\n\n"+"请立即导出相应Jar文件！");
			mb.open();			
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        finally
        {
        	try
        	{
	        	if (br != null) br.close();
	        	if (bw != null) bw.close();
        	}
        	catch(Exception ex)
        	{
        		ex.printStackTrace();
        	}
        }
	}
}
