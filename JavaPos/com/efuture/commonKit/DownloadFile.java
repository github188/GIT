package com.efuture.commonKit;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;


public class DownloadFile 
{
	private Display display = null;
	private Shell shell = null;
	private ProgressBar progress1 = null;
	private ProgressBar progress2 = null;
	private Label lblDownLoad = null;
	private Label lblUpdate = null;
	
	public DownloadFile(String Title,String progress1_lbl,String progress2_lbl)
	{
		this.open(Title,progress1_lbl,progress2_lbl);
	}
	
	public void open(String Title,String progress1_lbl,String progress2_lbl) 
	{
		display = Display.getDefault();
		createContents(Title,progress1_lbl,progress2_lbl);
		
		shell.open();
		shell.layout();
		
		//new UpdatePosBs(this);
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}


	protected void createContents(String Title,String progress1_lbl,String progress2_lbl) 
	{
		shell = new Shell(GlobalVar.style);
		shell.setSize(411, 176);
		Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setBounds(area.width/2-411/2,area.height/2-176/2 ,411,176);
		shell.setText(Title);		
		
		 //创建标题栏
        if (GlobalVar.heightPL != 0)
        {
       	 	Label labelTitle = new Label(shell, SWT.NONE);
       	       	  labelTitle.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
                  labelTitle.setBounds(0,0,411,30);
                  labelTitle.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                  labelTitle.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
                  labelTitle.setAlignment(SWT.CENTER);
                  labelTitle.setText(Title);  
        }
        
		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 10 + GlobalVar.heightPL, 387, 121);

		lblDownLoad = new Label(group, SWT.NONE);
		lblDownLoad.setBounds(8, 10,369, 22);
		lblDownLoad.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblDownLoad.setText(progress1_lbl);

		progress1 = new ProgressBar(group, SWT.NONE);
		progress1.setBounds(8, 38,372, 22);

		progress2 = new ProgressBar(group, SWT.NONE);
		progress2.setBounds(8, 92,372, 22);

		lblUpdate = new Label(group, SWT.NONE);
		lblUpdate.setBounds(8, 66,367, 20);
		lblUpdate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblUpdate.setText(progress2_lbl);
		
	}
	
	public Display getDisplay()
	{
		return display;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public ProgressBar getPbDownLoad()
	{
		return progress1;
	}
	
	public ProgressBar getPbUpdate()
	{
		return progress2;
	}
	
	public Label getLblDownLoad()
	{
		return lblDownLoad;
	}
	
	public Label getLblUpdate()
	{
		return lblUpdate;
	}
	
	public void setPrgress(int min,int max)
	{
		progress1.setMinimum(min);
		progress1.setMaximum(max);
		progress2.setMinimum(min);
		progress2.setMaximum(max);
	}
}
