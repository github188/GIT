package com.efuture.javaPos.UI.DesignTouch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;


public class LoginTouch
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
    public CLabel logolabel = null;
    public int width = 300;
    public int height = 320;

    /**
     * This method initializes group
     *界面的创建
     */
    private void createGroup()
    {
        group = new Group(sShell, SWT.SHADOW_NONE);
        fd_logolabel.bottom = new FormAttachment(group, 0, SWT.TOP);
        final FormData formData = new FormData();
        formData.right = new FormAttachment(0, 310);
        formData.bottom = new FormAttachment(0, 265);
        formData.top = new FormAttachment(0, 66);
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
        txtStaffID.setBounds(new Rectangle(103, 30, 166, 28));
        txtStaffID.setTextLimit(GlobalVar.MaxLengthOfStaffID);
        txtPasswd = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        txtPasswd.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtPasswd.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtPasswd.setBounds(new Rectangle(103, 88, 166, 28));
        txtPasswd.setTextLimit(GlobalVar.MaxlengthOfPasswd);
        
        combo = new Combo(group, SWT.READ_ONLY);
        combo.setText(Language.apply("请选择班次"));
        combo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        combo.setBounds(103, 146, 166, 28);
    }

    /**
     * Create contents of the window
     */
    public void createContents()
    {
        sShell = new Shell(GlobalVar.style);
        sShell.setLayout(new FormLayout());
        //sShell.setText("登录");
        sShell.setSize(331, 344);

        final Button button = new Button(sShell, SWT.NONE);
        button.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        final FormData fd_button = new FormData();
        fd_button.top = new FormAttachment(0, 276);
        fd_button.right = new FormAttachment(0, 163);
        fd_button.left = new FormAttachment(0, 63);
        button.setLayoutData(fd_button);
        button.setText(Language.apply("登录"));
        button.addSelectionListener(new SelectionAdapter() 
        {
        	public void widgetSelected(final SelectionEvent arg0)
        	{
        		GlobalInfo.posLogin = null;
        		combo.forceFocus();
        		NewKeyListener.sendKey(GlobalVar.Enter);        		
        	}
        });
        
        final Button button_1 = new Button(sShell, SWT.NONE);
        final FormData fd_button_1 = new FormData();
        fd_button_1.bottom = new FormAttachment(0, 306);
        fd_button_1.top = new FormAttachment(0, 276);
        fd_button_1.right = new FormAttachment(0, 273);
        fd_button_1.left = new FormAttachment(0, 173);
        button_1.setLayoutData(fd_button_1);
        button_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        button_1.setText(Language.apply("取消"));
        button_1.addSelectionListener(new SelectionAdapter() 
        {
        	public void widgetSelected(final SelectionEvent arg0)
        	{
        		txtStaffID.forceFocus();
        		NewKeyListener.sendKey(GlobalVar.Exit);
        	}
        });

        fd_logolabel = new FormData();
        fd_logolabel.right = new FormAttachment(100, -5);
        fd_logolabel.top = new FormAttachment(0, 5);
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
