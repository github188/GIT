package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.CheckGoodsEvent;
import com.swtdesigner.SWTResourceManager;

public class CheckGoodsForm
{
//	public boolean loginDone=false;//是否成功登陆
	public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
	public Text txtRq;
	public Combo comboCw;
	public Group group = null;
	public Label lblGz = null;
	public Label lblRq = null;
	public Label lblCw = null;
	public Text txtGz = null;
	public SaleBS saleBS = null;
	public char isExit = 'N';
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			CheckGoodsForm window = new CheckGoodsForm();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public CheckGoodsForm()
	{
	}
	
	public CheckGoodsForm(SaleBS saleBS)
	{
		this.saleBS = saleBS;
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		Display display = Display.getDefault();
		createContents();
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        // 创建对应的界面控制对象
        new CheckGoodsEvent(this);
        
        if (!sShell.isDisposed())
        {	        
	        sShell.open();
	        txtGz.forceFocus();
	        sShell.forceActive();
        }
        
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}
	
    /**
     * This method initializes group
     *界面的创建
     */
    private void createGroup()
    {
        group = new Group(sShell, SWT.SHADOW_NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 240);
        formData.top = new FormAttachment(0, 30);
        formData.right = new FormAttachment(0, 325);
        formData.left = new FormAttachment(0, 20);
        group.setLayoutData(formData);
        group.setText(Language.apply("盘点信息"));
        
        lblGz = new Label(group, SWT.RIGHT);
        lblGz.setBounds(new Rectangle(15, 30, 103, 28));
        lblGz.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        lblGz.setText(Language.apply("盘点柜组"));
        lblRq = new Label(group, SWT.RIGHT);
        lblRq.setBounds(new Rectangle(15, 88, 103, 28));
        lblRq.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        lblRq.setText(Language.apply("盘点日期"));
        lblCw = new Label(group, SWT.RIGHT);
        lblCw.setBounds(new Rectangle(15, 146, 103, 28));
        lblCw.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        lblCw.setText(Language.apply("盘点仓位"));
        
        txtGz = new Text(group, SWT.SINGLE | SWT.BORDER);
        txtGz.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtGz.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtGz.setBounds(new Rectangle(125, 30, 161, 28));
        
        txtRq = new Text(group, SWT.SINGLE | SWT.BORDER);
        txtRq.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtRq.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtRq.setBounds(new Rectangle(125, 88, 161, 28));
        
        comboCw = new Combo(group, SWT.READ_ONLY);
        comboCw.setText(Language.apply("请选择仓位"));
        comboCw.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        comboCw.setBounds(125, 146, 161, 28);
    }

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
        sShell = new Shell(GlobalVar.style);
        sShell.setLayout(new FormLayout());
        //sShell.setText("登录");
        sShell.setSize(350, 283);
        createGroup();
	}
}
