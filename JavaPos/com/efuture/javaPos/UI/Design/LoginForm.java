package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
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

import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.LoginEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.efuture.javaPos.UI.DesignTouch.LoginTouch;
import com.swtdesigner.SWTResourceManager;


public class LoginForm
{
	private FormData fd_logolabel;
	public Combo combo;
	public boolean loginDone=false;//是否成功登陆
    public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
    public Shell parent = null;
    private Group group = null;
    private Label lblStaffID = null;
    private Label lblPasswd = null;
    private Label lblClass = null;
    public Text txtStaffID = null;
    public Text txtPasswd = null;
    public int width = 300;
    public int height = 320;
    public CLabel logolabel = null;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            LoginForm window = new LoginForm();
            window.open(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public boolean open(Shell parent)
    {
    	// 把主窗口激活
    	GlobalInfo.mainshell.forceActive();
    	
        Display display = Display.getDefault();
        if (!ConfigClass.MouseMode) createContents();
        else 
        {
        	LoginTouch touch = new LoginTouch();
        	touch.createContents();
            sShell = touch.sShell;
            combo = touch.combo;
            txtStaffID = touch.txtStaffID;
            txtPasswd = touch.txtPasswd;
            logolabel = touch.logolabel;
        }
                
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,sShell);
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);

        
        if (ConfigClass.MouseMode)
        {
	        Image img = logolabel.getImage();
	        
	        if (img != null)
	        {
	        	ImageData imgdata = img.getImageData();
	        	if (imgdata != null)
	        	{
	        		int h,w;
	        		
	        		h = logolabel.getBounds().height;
	            	w = logolabel.getBounds().width - 10;

	        		if (h <= 0 && w <= 0)
    				{
	        			h = imgdata.height;
	        			w = imgdata.width;
	        			h = h - 10;
	        			w = w - 20;
    				}
	        		
	        		if (h > 0 && w > 0)
	        		{
		            	imgdata = imgdata.scaledTo(w,h);
		            	img = new Image(logolabel.getDisplay(),imgdata);
		            	logolabel.setImage(img);
		            	logolabel.redraw();
	        		}
	        	}
	        }
        }
        
        
        // 创建对应的界面控制对象
        new LoginEvent(this);
        
        if (!sShell.isDisposed())
        {	        
	        sShell.open();
	        txtStaffID.forceFocus();
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
        
        return loginDone;
    }

    /**
     * This method initializes group
     *界面的创建
     */
    private void createGroup()
    {
        group = new Group(sShell, SWT.SHADOW_NONE);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 269);
        formData.top = new FormAttachment(0, 70);
        formData.right = new FormAttachment(0, 305);
        formData.left = new FormAttachment(0, 20);
        group.setLayoutData(formData);
        group.setText("");
        lblStaffID = new Label(group, SWT.RIGHT);
        lblStaffID.setBounds(new Rectangle(15, 30, 73, 28));
        lblStaffID.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        lblStaffID.setText(Language.apply("收银员"));
        lblPasswd = new Label(group, SWT.RIGHT);
        lblPasswd.setBounds(new Rectangle(15, 88, 73, 28));
        lblPasswd.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        lblPasswd.setText(Language.apply("密  码"));
        lblClass = new Label(group, SWT.RIGHT);
        lblClass.setBounds(new Rectangle(15, 146, 73, 28));
        lblClass.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        lblClass.setText(Language.apply("班  次"));
        txtStaffID = new Text(group, SWT.SINGLE | SWT.BORDER);
        txtStaffID.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtStaffID.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtStaffID.setBounds(new Rectangle(103, 30, 161, 28));
        txtStaffID.setTextLimit(GlobalVar.MaxLengthOfStaffID);
        txtPasswd = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        txtPasswd.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtPasswd.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtPasswd.setBounds(new Rectangle(103, 88, 161, 28));
        txtPasswd.setTextLimit(GlobalVar.MaxlengthOfPasswd);
        
        combo = new Combo(group, SWT.READ_ONLY);
        combo.setText(Language.apply("请选择班次"));
        combo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        combo.setBounds(103, 146, 161, 28);
    }

    /**
     * Create contents of the window
     * @wbp.parser.entryPoint
     */
    protected void createContents()
    {
    	if (GlobalInfo.ModuleType.indexOf("ZM")==0)
    	{
    		sShell = new Shell(SWT.NONE|SWT.APPLICATION_MODAL);
    	}
    	else
    	{
    		sShell = new Shell(GlobalVar.style);
    	}
        
        sShell.setLayout(new FormLayout());
        //sShell.setText("登录");
        sShell.setSize(331, 315);

        fd_logolabel = new FormData();
        fd_logolabel.bottom = new FormAttachment(0, 66);
        fd_logolabel.top = new FormAttachment(0, 5);
        fd_logolabel.right = new FormAttachment(0, 320);
        fd_logolabel.left = new FormAttachment(0, 5);
        String loginfile = ConfigClass.BackImagePath + "login.png";
        if (!PathFile.fileExist(loginfile)) 
        {
        	logolabel = new CLabel(sShell, SWT.CENTER | SWT.SHADOW_NONE);
        	logolabel.setFont(SWTResourceManager.getFont("宋体", 18, SWT.NONE));
        	logolabel.setText(Language.apply("请登录POS收银系统"));
            logolabel.setLayoutData(fd_logolabel);
        }
        else
        {
        	logolabel = new CLabel(sShell, SWT.CENTER | SWT.SHADOW_NONE);
            logolabel.setLayoutData(fd_logolabel);
        	logolabel.setImage(SWTResourceManager.getImage(loginfile));
        }      
        
        createGroup();
    }
}
