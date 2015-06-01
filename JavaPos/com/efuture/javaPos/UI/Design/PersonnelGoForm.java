package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.PersonnelGoEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class PersonnelGoForm 
{

	private StyledText txtgotime = null;
	private Text txtpass = null;
	private StyledText txtname = null;
	private StyledText txtgh = null;
	private Label lblcalculagraph = null;
	protected Shell shell = null;
	
	
	public PersonnelGoForm()
	{
		this.open();
	}
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
		new PersonnelGoEvent(this);
        
        if (!shell.isDisposed())     
        {		
			shell.open();
			shell.layout();
        }
        
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch()) display.sleep();
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}


	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style & ~SWT.CLOSE);
	    //Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
	    shell.setSize(362, 298);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		
		shell.setText(Language.apply("锁定界面"));

		final Label label = new Label(shell, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
		label.setAlignment(SWT.CENTER);
		label.setText(Language.apply("该收银机已经被锁定"));
		label.setBounds(72, 10, 210, 20);

		final Group dddGroup = new Group(shell, SWT.NONE);
		dddGroup.setBounds(10, 36, 338, 215);

		final Label lblgh = new Label(dddGroup, SWT.NONE);
		lblgh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblgh.setText(Language.apply("工    号"));
		lblgh.setBounds(10, 55, 85, 20);

		txtgh = new StyledText(dddGroup, SWT.BORDER);
		txtgh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtgh.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtgh.setEnabled(false);
		txtgh.setEditable(false);
		txtgh.setBounds(100, 50, 227, 26);

		final Label lblname = new Label(dddGroup, SWT.NONE);
		lblname.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblname.setText(Language.apply("姓    名"));
		lblname.setBounds(10, 90, 85, 20);

		txtname = new StyledText(dddGroup, SWT.BORDER);
		txtname.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtname.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtname.setEnabled(false);
		txtname.setEditable(false);
		txtname.setBounds(100, 85, 227, 26);

		final Label lblpass = new Label(dddGroup, SWT.NONE);
		lblpass.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblpass.setText(Language.apply("密    码"));
		lblpass.setBounds(10, 125,85,20);

		txtpass = new Text(dddGroup, SWT.PASSWORD | SWT.BORDER);
		txtpass.setTextLimit(10);
		txtpass.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtpass.setBounds(100, 120, 227, 26);

		final Label lblgotime = new Label(dddGroup, SWT.NONE);
		lblgotime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblgotime.setText(Language.apply("离开时间"));
		lblgotime.setBounds(10, 20, 80, 26);

		txtgotime = new StyledText(dddGroup, SWT.BORDER);
		txtgotime.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtgotime.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtgotime.setEnabled(false);
		txtgotime.setEditable(false);
		txtgotime.setBounds(101, 18, 227, 26);

		final Label lblrbt = new Label(dddGroup, SWT.NONE);
		lblrbt.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblrbt.setText(Language.apply("计    时"));
		lblrbt.setBounds(10, 165, 85, 22);

		lblcalculagraph = new Label(dddGroup, SWT.NONE);
		lblcalculagraph.setText("00:00:00");
		lblcalculagraph.setBounds(100, 159,225, 35);
		lblcalculagraph.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lblcalculagraph.setFont(SWTResourceManager.getFont("宋体", 24, SWT.NONE));
	}
	
	public StyledText getTxtgotime()
	{
		return txtgotime;
	}
	
	public Text getTxtpass()
	{
		return txtpass;
	}
	
	public StyledText getTxtname()
	{
		return txtname;
	}
	
	public StyledText getTxtgh()
	{
		return txtgh;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Label getLblcalculagraph()
	{
		return lblcalculagraph;
	}
}
