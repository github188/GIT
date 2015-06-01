package custom.localize.Zmsy;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.TextEx;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

import custom.localize.Zmjc.FlightsDef;
import custom.localize.Zmjc.NationalityDef;
import custom.localize.Zmjc.Zmjc_NewKeyListener;


/**
 * @author sf
 *
 */
public class GwkForm
{
	private Label label_footmemo_1;
	private Text text_zjno;//证件号
	private Text text_name;//姓名
	private Text text_age;//年龄
	private Text text_fzjg;//发证机关
	private Text text_phoneno;//电话号码
	private Text text_zkl;//折扣率
	private Text text_email;//邮箱
    private Label label_footmemo;//底部说明
    
    private Combo combo_isDX;//是否短信
	private TextEx textEx_gklb;//顾客类别
	private TextEx textEx_flight;//航班
	private TextEx textEx_thplace;//提货地点
	private Combo combo_zjtype;//证件类别
	private Combo combo_sex;//姓别
	private TextEx textEx_national;//国籍
	
	private DateTime date_ljrq;//离境日期
	private	DateTime date_ljsj;//离境时间
	private DateTime date_csrq;//出生日期
	private boolean Done = false;
	private Shell shell = null;
		
	private SaleBS saleBS;
	private GwkDef gwk;
	private Vector vecNational;
	private Vector vecTHPlace;
	private Vector vecZJType;
	private Vector vecFlights;
	
	private boolean isChanageOK;
	private String retMsg;
	
	private boolean isInvokeZhxApi=false;//是否已经调用中航信接口 wangyong add by 2014.9.18
    
    /**
	 * 
	 */
	public GwkForm(SaleBS saleBS, GwkDef gwk)
	{
		this.saleBS = saleBS;		
		this.gwk = gwk;
	}
	
	public void setGwkBaseInfo(Vector vecNational, Vector vecTHPlace, Vector vecZJType, Vector vecFlights)
	{
		try
		{
			this.vecNational = vecNational;
			this.vecTHPlace = vecTHPlace;
			this.vecZJType = vecZJType;
			this.vecFlights = vecFlights;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
    
	/* Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
        	GwkForm window = new GwkForm(null,null);
            window.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
    /**
     * 顾客信息
     * @param parent
     * @return
     */
    public boolean open()
    {
    	final Display display = Display.getDefault();
        createContents();
        
        GwkFormEvent(this);
        
        gwkForm_Load();
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);

        // 创建对应的界面控制对象
        
        if (!shell.isDisposed())
        {	        
        	shell.layout();
	        shell.open();
	        shell.setActive();
	        //shell.forceActive();
	        //textEx_gklb.forceFocus();
        }
        
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        return Done;
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
    	shell = new Shell(GlobalVar.style | SWT.BORDER | SWT.CLOSE);
    	shell.setLayout(new FormLayout());
    	shell.setSize(682, 385);
    	shell.setText("顾客信息");
    	
        final Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(null);
        final FormData fd_composite = new FormData();
        fd_composite.left = new FormAttachment(100, -671);
        fd_composite.bottom = new FormAttachment(0, 335);
        fd_composite.top = new FormAttachment(0, 4);
        fd_composite.right = new FormAttachment(100, -5);
        composite.setLayoutData(fd_composite);

        final Label label_1 = new Label(composite, SWT.NONE);
        label_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
        label_1.setFont(SWTResourceManager.getFont("", 12, SWT.BOLD));
        label_1.setBounds(314, 48, 117, 23);
        label_1.setAlignment(SWT.RIGHT);
        label_1.setText("* 顾客类别:");

        final Label label_2 = new Label(composite, SWT.NONE);
        label_2.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_2.setBounds(10, 48, 95, 23);
        label_2.setAlignment(SWT.RIGHT);
        label_2.setText("* 离境日期:");

        final Label label_3;
        label_3 = new Label(composite, SWT.NONE);
        label_3.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_3.setBounds(10, 79, 95, 23);
        label_3.setAlignment(SWT.RIGHT);
        label_3.setText("* 离境航班:");

        final Label label_4;
        label_4 = new Label(composite, SWT.NONE);
        label_4.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_4.setBounds(10, 108, 95, 23);
        label_4.setAlignment(SWT.RIGHT);
        label_4.setText("* 离境时间:");

        final Label label_5;
        label_5 = new Label(composite, SWT.NONE);
        label_5.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_5.setBounds(316, 107, 117, 23);
        label_5.setAlignment(SWT.RIGHT);
        label_5.setText("* 提货地点：");

        final Label label_6;
        label_6 = new Label(composite, SWT.NONE);
        label_6.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_6.setBounds(314, 15, 117, 23);
        label_6.setAlignment(SWT.RIGHT);
        label_6.setText("* 证 件 号:");

        final Label label_7 = new Label(composite, SWT.NONE);
        label_7.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_7.setBounds(10, 18, 95, 23);
        label_7.setAlignment(SWT.RIGHT);
        label_7.setText("* 证件类别:");

        final Label label_8;
        label_8 = new Label(composite, SWT.NONE);
        label_8.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_8.setBounds(6, 139, 95, 23);
        label_8.setAlignment(SWT.RIGHT);
        label_8.setText("* 姓   名:");

        final Label label_9;
        label_9 = new Label(composite, SWT.NONE);
        label_9.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_9.setBounds(314, 140, 117, 23);
        label_9.setAlignment(SWT.RIGHT);
        label_9.setText("* 性    别:");

        final Label label_10;
        label_10 = new Label(composite, SWT.NONE);
        label_10.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_10.setBounds(10, 174, 95, 23);
        label_10.setAlignment(SWT.RIGHT);
        label_10.setText("* 出生日期:");

        final Label label_11;
        label_11 = new Label(composite, SWT.NONE);
        label_11.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_11.setBounds(314, 174, 117, 23);
        label_11.setAlignment(SWT.RIGHT);
        label_11.setText("* 年    龄:");

        final Label label_12;
        label_12 = new Label(composite, SWT.NONE);
        label_12.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_12.setBounds(10, 205, 95, 23);
        label_12.setAlignment(SWT.RIGHT);
        label_12.setText("* 发证机关:");

        final Label label_13;
        label_13 = new Label(composite, SWT.NONE);
        label_13.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_13.setBounds(314, 205, 117, 23);
        label_13.setAlignment(SWT.RIGHT);
        label_13.setText("* 国    籍:");

        final Label label_14;
        label_14 = new Label(composite, SWT.NONE);
        label_14.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_14.setBounds(10, 242, 95, 23);
        label_14.setAlignment(SWT.RIGHT);
        label_14.setText("* 手机号码:");

        final Label label_15;
        label_15 = new Label(composite, SWT.NONE);
        label_15.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_15.setBounds(314, 242, 117, 23);
        label_15.setAlignment(SWT.RIGHT);
        label_15.setText("* 折 扣 率:");

        final Label label_16;
        label_16 = new Label(composite, SWT.NONE);
        label_16.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_16.setBounds(10, 274, 95, 23);
        label_16.setAlignment(SWT.RIGHT);
        label_16.setText(" 电子邮件:");

        final Label label_17;
        label_17 = new Label(composite, SWT.NONE);
        label_17.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_17.setBounds(314, 276, 117, 23);
        label_17.setAlignment(SWT.RIGHT);
        label_17.setText("是否短信:");

        label_footmemo = new Label(composite, SWT.None);
        label_footmemo.setBounds(0, 309, 625, 22);
        label_footmemo.setText("提示：按【数量键】读取身份证号，按【付款键】保存，按【取消键】关闭窗口，按【回车键】移动输入框焦点");
        
		combo_isDX = new Combo(composite, SWT.READ_ONLY|SWT.DROP_DOWN);
		combo_isDX.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		combo_isDX.setBounds(437, 272, 85, 23);
		combo_isDX.setData("name", "是否短信");
		//combo_isDX.setItems(ITEMS);
//		combo.setText(" 是");

		textEx_gklb = new TextEx(composite, SWT.READ_ONLY);
		textEx_gklb.getSource().setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
		textEx_gklb.setBounds(437, 48, 200, 23);
		textEx_gklb.setData("name", "顾客类别");
		//textEx_gklb.setEnabled(false);

        textEx_flight = new TextEx(composite, SWT.NONE);
        textEx_flight.getSource().setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        textEx_flight.setBounds(111, 77, 526, 23);
        textEx_flight.setData("name", "离境航班");
        //textEx_flight.setItemsSource(new String[]{"001","002","010","011"});

        textEx_thplace = new TextEx(composite, SWT.NONE);
        textEx_thplace.getSource().setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        textEx_thplace.setBounds(437, 105, 200, 23);
        textEx_thplace.setData("name", "提货地点");

        text_zjno = new Text(composite, SWT.BORDER);
        text_zjno.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        text_zjno.setBounds(437, 10, 200, 23);
        text_zjno.setData("name", "证件号码");

        combo_zjtype = new Combo(composite, SWT.NONE);
        combo_zjtype.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        combo_zjtype.setBounds(111, 13, 200, 23);
        combo_zjtype.setData("name", "证件类别");

        text_name = new Text(composite, SWT.BORDER);
        text_name.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        //text_name.setTabs(5);    
        text_name.setBounds(111, 138, 200, 23);
        text_name.setData("name", "姓 名");    

        combo_sex = new Combo(composite, SWT.READ_ONLY|SWT.DROP_DOWN);
        combo_sex.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        combo_sex.setBounds(437, 137, 200, 23);
        combo_sex.setData("name", "性 别");

        text_age = new Text(composite, SWT.BORDER);
        text_age.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        text_age.setBounds(437, 171, 200, 23);
        text_age.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        text_age.setData("name", "年 龄");
        text_age.setEditable(false);//不允许修改 for 客户

        text_fzjg = new Text(composite, SWT.BORDER);
        text_fzjg.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        text_fzjg.setBounds(111, 205, 200, 23);
        text_fzjg.setData("name", "发证机关");

        textEx_national = new TextEx(composite, SWT.NONE);
        textEx_national.getSource().setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        textEx_national.setBounds(437, 205, 200, 23);
        textEx_national.setData("name", "国籍");

        text_phoneno = new Text(composite, SWT.BORDER);
        text_phoneno.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        text_phoneno.setBounds(111, 238, 200, 23);
        text_phoneno.setData("name", "手机号码");

        text_zkl = new Text(composite, SWT.BORDER);
        text_zkl.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        text_zkl.setBounds(437, 238, 150, 23);
        text_zkl.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        text_zkl.setData("name", "折扣率");
        text_zkl.setEditable(false);//不允许修改 for 客户

        final Label label_18 = new Label(composite, SWT.NONE);
        label_18.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        label_18.setBounds(597, 241, 25, 20);
        label_18.setText("%");

        text_email = new Text(composite, SWT.BORDER);
        text_email.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        text_email.setBounds(111, 272, 200, 23);
        text_email.setData("name", "电子邮件");
        
        date_ljrq = new DateTime(composite, SWT.DROP_DOWN );//| SWT.MEDIUM
        date_ljrq.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        date_ljrq.setBounds(111, 45, 200, 23);
        date_ljrq.setData("name", "离境日期");

        
        date_ljsj = new DateTime(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.TIME | SWT.SHORT);
        date_ljsj.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        date_ljsj.setBounds(111, 105, 200, 23);
        date_ljsj.setData("name", "离境时间");
        
        date_csrq = new DateTime(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.MEDIUM);
        date_csrq.setFont(SWTResourceManager.getFont("", 10, SWT.BOLD));
        date_csrq.setBounds(111, 171, 200, 23);
        date_csrq.setData("name", "出生日期");

    	label_footmemo_1 = new Label(shell, SWT.NONE);
    	final FormData fd_label_footmemo_1 = new FormData();
    	fd_label_footmemo_1.bottom = new FormAttachment(composite, 22, SWT.BOTTOM);
    	fd_label_footmemo_1.top = new FormAttachment(composite, 0, SWT.BOTTOM);
    	fd_label_footmemo_1.right = new FormAttachment(composite, 625, SWT.LEFT);
    	fd_label_footmemo_1.left = new FormAttachment(composite, 0, SWT.LEFT);
    	label_footmemo_1.setLayoutData(fd_label_footmemo_1);
    	label_footmemo_1.setText("       按【重打印键】获取顾客航班信息");
         
    }

    public void GwkFormEvent(GwkForm form)
	{	
    	shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y
    					- GlobalVar.heightPL);

    	shell.setBounds((GlobalVar.rec.x - shell.getSize().x) / 2, (GlobalVar.rec.y - shell.getSize().y) / 2, shell.getSize().x, shell.getSize().y
    					- GlobalVar.heightPL);
    			
		  // 设定键盘事件
        NewKeyEvent event = new NewKeyEvent()
        {
            public void keyDown(KeyEvent e, int key)
            {
                //keyPressed(e, key);
            }

            public void keyUp(KeyEvent e, int key)
            {
                keyReleased(e, key);
            }
        };

        Zmjc_NewKeyListener key = new Zmjc_NewKeyListener();
        key.event = event;
        key.inputMode = key.inputMode;
       
        /*KeyListener key2 = new KeyListener(){

			public void keyPressed(KeyEvent arg0)
			{				
			}

			public void keyReleased(KeyEvent keyEvent)
			{
				if (keyEvent.keyCode==13)
				{
					//回车
					controlEvent(keyEvent.getSource());
				}
				else if (keyEvent.keyCode==27)
				{
					//ESC
					isChanageOK = false;
                    retMsg = "款员取消刷卡窗口";
                	closeForm();
				}
			}
        	
        };*/
        text_zjno.addKeyListener(key);
        text_name.addKeyListener(key);
        text_age.addKeyListener(key);
        text_fzjg.addKeyListener(key);
        text_phoneno.addKeyListener(key);
        text_zkl.addKeyListener(key);
        text_email.addKeyListener(key);
        combo_isDX.addKeyListener(key);
        textEx_gklb.addKeyListener(key);
        textEx_flight.addKeyListener(key);
        textEx_thplace.addKeyListener(key);
        combo_zjtype.addKeyListener(key);
        combo_sex.addKeyListener(key);
        textEx_national.addKeyListener(key);
        
        date_ljrq.addKeyListener(key);
        date_ljsj.addKeyListener(key);
        date_csrq.addKeyListener(key);
        
       /* FocusListener focus = new FocusListener(){
			public void focusLost(final FocusEvent arg0)
			{
				focusLost(arg0);
			}

			public void focusGained(FocusEvent arg0)
			{
				
			}
		};*/
		
		/*text_cardno.addFocusListener(focus);
        text_zjno.addFocusListener(focus);
        text_name.addFocusListener(focus);
        text_age.addFocusListener(focus);
        text_fzjg.addFocusListener(focus);
        text_phoneno.addFocusListener(focus);
        text_zkl.addFocusListener(focus);
        text_email.addFocusListener(focus);
        combo_isDX.addFocusListener(focus);
        textEx_gklb.addFocusListener(focus);
        textEx_flight.addFocusListener(focus);
        textEx_thplace.addFocusListener(focus);
        textEx_zjtype.addFocusListener(focus);
        combo_sex.addFocusListener(focus);
        textEx_national.addFocusListener(focus);
        date_ljrq.addFocusListener(focus);
        date_ljsj.addFocusListener(focus);
        date_csrq.addFocusListener(focus);*/
		
		
	}
    
    public void focusLost(FocusEvent focus)
    {
    	try
    	{
    		//controlEvent(focus);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    /*public void keyPressed(KeyEvent e, int key)
    {
    	try
    	{
    		switch (key)
            {
	            case GlobalVar.ArrowUp:
	
	                break;
	            case GlobalVar.ArrowDown:
	            	
	            break;
            }
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }*/
    
    public void keyReleased(KeyEvent e, int key)
    {
        try
        {
            switch (key)
            {

                case GlobalVar.Exit:
                	int retMessage = new MessageBox( "是否退出刷卡窗口？", null, true ).verify();
                	if ( retMessage == GlobalVar.Key1 )
                	{
                		this.isChanageOK = false;
                        this.retMsg = "款员取消刷卡窗口";
                		this.writeLog("[款员取消]刷卡窗口");
                    	closeForm();
                	}
                	
                    break;

                case GlobalVar.Pay:
                	//保存购物卡信息
                	if (sendGWKInfo())
                	{
                		this.isChanageOK = true;
                		this.writeLog("[保存退出]刷卡窗口");
                		closeForm();
                	}
                	e.data="";
                	break;
                	
            	case GlobalVar.Enter:
            		controlEvent(e.getSource());
        			e.data="";
            		break;
            		
                    
                case GlobalVar.Quantity:
                	//读取身份证号
                	if (getpCard2())
                	{
                		invokeZhxApi(false);
                		//如果是快捷键读卡的焦点从 证件号 落到 手机号上‘XX电子邮件
                		//this.text_phoneno.setFocus();
                		//this.text_phoneno.selectAll();
                		
                		//跳到离境日期上面
                		this.date_ljrq.setFocus();
                	}
                	else
                	{
                		this.text_zjno.setFocus();
                		this.text_zjno.selectAll();
                	}
                	e.data="";
                	break;

                case GlobalVar.Print:
                	//强行从中航信接口获取顾客航班信息
                	//this.writeLog("keyReleased Print");
            		invokeZhxApi(true);
                	e.data="";
                	break;
                default:
                		e.data="";
                		break;
            }
        }
        catch (Exception ex)
        {
        	this.writeLog(ex);
        }
    }
    
    private void closeForm()
    {
    	try
    	{
    		shell.close();
           // shell.dispose();
           // shell = null;
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    public void disposeForm()
    {
    	try
    	{
    		//shell.close();
            shell.dispose();
            shell = null;
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    public boolean getIsChanageOK()
    {
    	return this.isChanageOK;
    }
    
    public String getRetMsg()
    {
    	return this.retMsg;
    }
    
    private  void controlEvent(Object ctlSource)
    {
    	try
    	{
    		int intRet = checkInput(ctlSource);
    		if (intRet==1)
    		{
    			//SendKeys.Send("{TAB}")
    			changeCtlFocus(ctlSource);
    		}
    		else if (intRet==2)
    		{
    			//最后一次焦点时，返回到第一个控件上
    			//this.date_ljrq.setFocus();
    			
    			/*if (this.textEx_zjtype.getEnabled())
    			{
    				this.textEx_zjtype.setFocus();
    			}*/
    			if (this.combo_zjtype.getEnabled())
    			{
    				this.combo_zjtype.setFocus();
    			}
    			else
    			{
    				//当证件类型不可用时，则将焦点返回到离境日期上
    				this.date_ljrq.setFocus();
    			}
    			
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    /**
     * 切换控件焦点
     * @param ctlSource
     */
    private void changeCtlFocus(Object ctlSource)
    {
    	try
    	{
    		if (ctlSource instanceof Text)
    		{
    			Text txt = (Text)ctlSource;
    			if (txt == null) return;
    			if (txt== this.textEx_flight.getSource())
    			{
    				//TAB 3-->4
    				this.text_name.setFocus();
    				text_name.setSelection(text_name.getText().length());
    			}
    			else if (txt==this.text_zjno)
    			{
    				//TAB 1-->2(第一个控件跳到第二个控件)
    				this.date_ljrq.setFocus();
    			}
    			/*else if (txt== this.textEx_zjtype.getSource())combo_zjtype
    			{
    				//TAB 12-->1(焦点回到第一个控件)
    				this.text_zjno.setFocus();
    				text_zjno.setSelection(text_zjno.getText().length());
    			}*/
    			else if (txt==this.text_name)
    			{
    				//TAB 4-->5
    				this.combo_sex.setFocus();
    			}
    			else if (txt==this.text_fzjg)
    			{
    				//TAB 7-->8
    				this.textEx_national.setFocus();
    			}
    			else if (txt== this.textEx_national.getSource())
    			{
    				//TAB 8-->9
    				this.text_phoneno.setFocus();
    				text_phoneno.setSelection(text_phoneno.getText().length());
    			}
    			else if (txt==this.text_phoneno)
    			{
    				//TAB 9-->10
    				this.text_email.setFocus();
    				text_email.setSelection(text_email.getText().length());
    			}
    			else if (txt==this.text_email)
    			{
    				//TAB 10-->11
    				this.combo_isDX.setFocus();
    			}
    			else if (txt==this.textEx_gklb.getSource())
    			{
    				this.textEx_flight.setFocus();//额外增加的
    				
    			}
    			else if (txt==this.textEx_thplace.getSource())
    			{
    				this.textEx_gklb.setFocus();
    			}
    			else if (txt==this.text_age)
    			{
    				//this.date_ljrq.setFocus();
    				this.text_fzjg.setFocus();
    			}
    			else if (txt== this.text_zkl)
    			{
    				this.text_email.setFocus();
    			}
    		}
    		else if (ctlSource instanceof Combo)
    		{
    			Combo combo = (Combo)ctlSource;
    			if (combo == null) return;
    			if (combo==this.combo_sex)
    			{
    				//TAB 5-->6
    				this.date_csrq.setFocus();
    			}
    			else if (combo==this.combo_isDX)
    			{
    				//TAB 11-->12
    				if (combo_zjtype.getEnabled())
    				{
    					this.combo_zjtype.setFocus();
    				}
    				else
    				{
    					//当证件类型不可用时
    					this.date_ljrq.setFocus();
    				}
    				
    			}
    			else if (combo== this.combo_zjtype)
    			{
    				//TAB 12-->1(焦点回到第一个控件)
    				if (text_zjno.getEnabled())
    				{
    					this.text_zjno.setFocus();
        				text_zjno.setSelection(text_zjno.getText().length());
    				}
    				else
    				{
    					//当证件类型不可用时
    					this.date_ljrq.setFocus();
    				}
    				
    				//this.text_zjno.setFocus();
    				//text_zjno.setSelection(text_zjno.getText().length());
    			}
    		}
    		else if (ctlSource instanceof DateTime)
    		{
    			DateTime datetime = (DateTime)ctlSource;
    			if (datetime == null) return;
    			if (datetime== this.date_ljrq)
    			{
    				//TAB 2-->3
    				this.textEx_flight.setFocus();
    			}
    			else if (datetime==this.date_csrq)
    			{
    				//TAB 6-->7
    				this.text_fzjg.setFocus();
    				text_fzjg.setSelection(text_fzjg.getText().length());
    			}
    			else if (datetime== this.date_ljsj)
    			{
    				//this.textEx_thplace.setFocus();
    				this.text_name.setFocus();//改完离境时间之后,焦点跳到姓名 for 吴进宝 by 213.10.12
    				text_name.setSelection(text_name.getText().length());
    			}
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }

    /**
     * 当intRet=1,,则SendKeys.Send("{TAB}")
     * @param ctlSource
     * @return
     */
    private int checkInput(Object ctlSource)
    {
    	try
    	{
    		if (ctlSource instanceof Text)
    		{
    			Text txt = (Text)ctlSource;
    			if (txt == null) return -1;
    			    			
    			//先检查是否为空
    			if ((txt==this.textEx_gklb.getSource() 
    				|| txt==this.textEx_flight.getSource() 
    				|| txt==this.textEx_thplace.getSource() 
    				//|| txt==this.textEx_zjtype.getSource() 
    				|| txt==this.textEx_national.getSource()) 
    				 && checkInputValue(txt.getText())==false)
        		{
    				showMsg("[" + String.valueOf(txt.getData("name")) +  "]不允许为空");
    				return 0;
        		}
        		else
        		{
        			if (txt== this.textEx_flight.getSource())
        			{
        				if (findHangBan()!=1)
        				{
        					return 0;
        				}
        			}
        			/*else if (txt== this.textEx_zjtype.getSource())
        			{
        				//录入了证件号以后，也要调用购物卡接口
        				if (findGWKInfo()) return 1;
        			}*/
        			else if (txt==this.text_email)
        			{
        				if (!this.checkEmail()) return 0;
        				return 1;
        			}
        			else if (txt==this.text_phoneno)
        			{
        				if (!this.checkPhoneNo()) return 0;
        				return 1;
        			}
        			else if (txt==this.text_age)
        			{
        				//当证件类型为身份证时，年龄字段为必填，且年龄与生日匹配
        				if (getID(this.combo_zjtype.getText()).toString().equals(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
        				{
        					if (checkInputValue(txt.getText())==false)
        					{
        						showMsg(txt.getData("name") + "不允许为空");
        						return 0;
        					}
        					if (Convert.toInt(this.text_age.getText()) != getAge())
        					{
        						showMsg("生日和年龄填写不一致");
        						return 0;
        					}
        				}
        			}
        			else if (txt==this.text_fzjg)
        			{
        				//当证件类型为身份证时，发证机关为必填项
        				if (checkInputValue(txt.getText())==false && getID(this.combo_zjtype.getText()).toString().equals(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
        				{
        					showMsg(txt.getData("name") + "不允许为空");
        					return 0;
        				}
        				if (!this.checkFZJG()) return 0;
        			}
        			else
        			{
        				if (checkInputValue(txt.getText()) == false)
        				{
        					showMsg(txt.getData("name") + "不允许为空");
        					return 0;
        				}
        				
        				if (txt == this.text_zjno)
        				{
        					if (checkInputValue(this.combo_zjtype.getText())==false)
        					{
        						this.combo_zjtype.setFocus();
        						//this.combo_zjtype.selectAll();
        						return 0;
        					}
        					
        					if (findGWKInfo())
        					{
        						return 1;
        					}
        					else
        					{
        						this.text_zjno.selectAll();
        						//未找到卡
        						return 0;
        					}
        					
        				}
        				else if (txt == this.text_name)
        				{
        					if (!this.checkName()) return 0;
        					invokeZhxApi(false);
        				}
        			}
        				
        		}
    		}
    		else if (ctlSource instanceof Combo)
    		{
    			Combo combo = (Combo)ctlSource;
    			if (combo == null) return -1;
    			if (checkInputValue(combo.getText())==false)
    			{
    				showMsg("[" + String.valueOf(combo.getData("name")) +  "]不允许为空");
    				return 0;
    			}
    			else if (combo== this.combo_zjtype)
    			{
    				//录入了证件号以后，也要调用购物卡接口
    				if (findGWKInfo()) return 1;
    				return 3;//返回失败时，不处理（焦点）
    			}
    			else 
    			{
    				if (combo==this.combo_isDX)
    				{
    					 //最后一次焦点时，返回到第一个控件上
    					return 2;
    				}
    				else if (combo == this.combo_sex)
    				{
    					
    				}
    			}
    		}
    		else if (ctlSource instanceof DateTime)
    		{
    			//当离境日期和离境航班同时有值时，则调用航班信息
    			DateTime datetime = (DateTime)ctlSource;
    			if (datetime == null) return -1;
    			if (datetime==this.date_ljrq)
    			{
    				//1.检查数据的合法性
    				if (!ManipulateDateTime.checkDate(ManipulateDateTime.getFormatDate(date_ljrq.getYear(), date_ljrq.getMonth(), date_ljrq.getDay())))
    				{
    					showMsg("[" + date_ljrq +"]数据不合法! ");
    					return 0;
    				}
    				//2.检查离境日期是否超过30天
    				long days = new ManipulateDateTime().getDisDateTime(new ManipulateDateTime().getDateTimeString(),ManipulateDateTime.getFormatDateTime(date_ljrq.getYear(), date_ljrq.getMonth(), date_ljrq.getDay()));//getDisDateTime("2013-07-29 00:00:00","2013-07-30 12:00:00");
    				if (days>30)
    				{
    					showMsg("[离境时间]已经超过 " + days +  " 天");
    				}
    				//3.当航班信息不为空时,则查找航班
    				if(checkInputValue(this.textEx_flight.getText())) 
    				{
    					if (findHangBan()!= 1) return 0;
    				}
    				    				    				
    			}
    			else if (datetime==this.date_ljsj)
    			{
    				
    			}
    			else if (datetime==this.date_csrq)
    			{
    				this.text_age.setText(String.valueOf(getAge()));
    			}
    			else
    			{
    				//
    			}
    		}
    		else
    		{
    			return 1;
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return 1;
    }
    
    private void gwkForm_Load()
    {
    	ProgressBox pb = null;
    	try
    	{
    		pb = new ProgressBox();
	        pb.setText("正在加载购物卡相关信息,请等待...");
	        
	        //getLoadInfo();
    		loadInfo();    		
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	finally
        {
    		 if (pb != null)
             {
                 pb.close();
                 pb = null;
             }
        }

		//this.textEx_gklb.setFocus();
    	if (this.text_zjno.getEditable())
    	{
    		this.text_zjno.setFocus();//tab1
    	}
    	else
    	{
    		//当证件类型不可修改改时，则将焦点置在离境日期上
    		date_ljrq.setFocus();
    	}
    	
    }
    
    private void loadInfo()
    {
    	try
    	{
    		loadNational();
    		loadThplace();
    		loadZJType();
    		loadFlight();
    		loadOther();
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    private boolean loadNational()
    {
    	try
    	{
    		if (this.vecNational == null && this.vecNational.size()<=0) return false;
    		ArrayList items = new ArrayList(); 
    		NationalityDef n;
    		for (int i=0; i<this.vecNational.size(); i++)
    		{
    			n = (NationalityDef)vecNational.elementAt(i);
    			if (n==null) continue;
    			items.add(n.PCRENAME + n.PCRCNAME + " - " + n.PCRCODE);
    		}
    		
    		this.textEx_national.setItemsSource(items.toArray());
    		loadNationalValue(gwk.nation);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
    }

    private boolean loadThplace()
    {
    	return loadThplace(null);
    }
    
    /**
     * 要过虑的顾客类别
     * @param filterGKLB
     * @return
     */
    private boolean loadThplace(String filterGKLB)
    {
    	try
    	{
    		if (this.vecTHPlace == null && this.vecTHPlace.size()<=0) return false;
    		ArrayList items = new ArrayList(); 
    		THPlaceDef n;
    		for (int i=0; i<this.vecTHPlace.size(); i++)
    		{
    			n = (THPlaceDef)vecTHPlace.elementAt(i);
    			if (n==null) continue;
    			if (filterGKLB!=null && filterGKLB.length()>0 && n.thgklb.equalsIgnoreCase(filterGKLB)) continue; 
    			items.add(n.thbillno + " - " + n.thsp);
    		}
    		
    		this.textEx_thplace.setItemsSource(items.toArray());
    		LoadThplaceValue(gwk.thdd);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
    }
    
    private boolean loadZJType()
    {
    	try
    	{
    		//ArrayList items = new ArrayList(); 
			ZJTypeDef zj = null;
    		if (gwk.zjlb != null && gwk.zjlb.length()>0 && gwk.passport !=null && gwk.passport.length()>0)
    		{
    			//当存在数据时，只加载该项，且不允许修改
    			for(int i=0; i< vecZJType.size(); i++)
    			{
    				zj = (ZJTypeDef)vecZJType.elementAt(i);
    				if (zj == null) continue;
    				if (zj.zjid.equalsIgnoreCase(gwk.zjlb))
    				{
    					/*items.add(zj.zjid + " - " + zj.zjname);
    					this.textEx_zjtype.setItemsSource(items.toArray());
    					this.textEx_zjtype.select(0);*/
    					combo_zjtype.add(zj.zjid + " - " + zj.zjname);
    					combo_zjtype.select(0);//只加载这一项
    					break;
    				}
    			}
    			
    		}
    		else
    		{
    			for(int i=0; i< vecZJType.size(); i++)
    			{
    				zj = (ZJTypeDef)vecZJType.elementAt(i);
    				if (zj == null) continue;
    				combo_zjtype.add(zj.zjid + " - " + zj.zjname);
    			}

				//this.textEx_zjtype.setItemsSource(items.toArray());
				
				if (gwk.zjlb==null || gwk.zjlb.length()<=0)
				{
					//NEW当是初始加载时，则默认为身份证
					loadZJTypeValue(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ);
				}
				else
				{
					loadZJTypeValue(gwk.zjlb);
				}
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
    }
    
    private boolean loadFlight()
    {
    	try
    	{
    		writeLog("loadFlight() flight_size=[" + this.vecFlights.size() + "]");
    		//writeLog("loadFlight() 1");
    		ArrayList items = new ArrayList();
    		FlightsDef f = null;
    		for(int i=0; i< this.vecFlights.size(); i++)
			{
				f = (FlightsDef)vecFlights.elementAt(i);
				if (f == null) continue;
				items.add(f.fnumber + " - " + f.fairlines + " - " + getTHDD_Name(f.fport1) + " - " + f.fjt + " - " + f.frealtime + " - " + f.fport2);
			}
    		//writeLog("loadFlight() 2");
    		    		
    		//简拼模式
    		for(int i=0; i< this.vecFlights.size(); i++)
			{
				f = (FlightsDef)vecFlights.elementAt(i);
				if (f == null) continue;
				
				//起飞地目的地:航班号 - 航空公司 - 离境地fport1 - 经停fjt - 时间frealtime - 目的地fport2
				items.add(getPY(getTHDD_Name(f.fport1.trim() + f.fport2.trim())) + f.fnumber + " - " + f.fairlines + " - " + getTHDD_Name(f.fport1.trim()) + " - " + f.fjt + " - " + f.frealtime + " - " + f.fport2);
				
	            //目的地:航班号 - 航空公司 - 离境地fport1 - 经停fjt - 时间frealtime - 目的地fport2
				items.add(getPY(f.fport2) + f.fnumber + " - " + f.fairlines + " - " + getTHDD_Name(f.fport1) + " - " + f.fjt + " - " + f.frealtime + " - " + f.fport2);
			}
    		//writeLog("loadFlight() 3");
			this.textEx_flight.setItemsSource(items.toArray());
			//writeLog("loadFlight() 4");
    		loadFlightValue(gwk.ljhb);
    		//writeLog("loadFlight() 5");
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
    }
    
    private boolean loadCustType()
    {
    	return loadCustType(true);
    }
    /**
     * 
     * @param isShowLJ 顾客类别是否显示离境
     * @return
     */
    private boolean loadCustType(boolean isShowLJ)
    {

    	try
    	{
    		//顾客类别
    		ArrayList items = new ArrayList();
    		if(isShowLJ) items.add(Zmsy_StatusType.ZMSY_GKTYPE_LJ + " - 离境");
    		items.add(Zmsy_StatusType.ZMSY_GKTYPE_LD + " - 离岛");
    		items.add(Zmsy_StatusType.ZMSY_GKTYPE_BDLD + " - 本地离岛");
    		this.textEx_gklb.setItemsSource(items.toArray());
    		if (gwk.gklb==null)
    		{
    			gwk.gklb = Zmsy_StatusType.ZMSY_GKTYPE_LD;
    		}
    		loadCustTypeValue(gwk.gklb);//gwk.gklb
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
    }

    private boolean loadOther()
    {
    	try
    	{
    		//顾客类别
    		loadCustType();
    		
    		//性别
    		this.combo_sex.removeAll();
    		this.combo_sex.clearSelection();
    		this.combo_sex.add(Zmsy_StatusType.ZMSY_SEX_MALE + " - 男");
    		this.combo_sex.add(Zmsy_StatusType.ZMSY_SEX_FEMALE + " - 女");
    		loadSexValue(gwk.gender);
    		
    		//是否短信
    		this.combo_isDX.removeAll();
    		this.combo_isDX.clearSelection();
    		this.combo_isDX.add("否");
    		this.combo_isDX.add("是");
    		if(gwk.isdx==null)
    		{
    			gwk.isdx = "Y";
    		}
    		loadIsDx(gwk.isdx);//gwk.isdx
    		
    		//其它
    		loadCardNoValue(gwk.code);
    		loadCardNameValue(gwk.name);
            loadZJNoValue(gwk.passport);//ZJNO
            loadBirthValue(gwk.birth);
            loadAgeValue(gwk.age);
            if (gwk.zkl==-1)
            {
            	gwk.zkl=1;
            }
            loadZKLValue(gwk.zkl);//gwk.zkl 默认100
            loadLjDateTimeValue(gwk.ljrq, gwk.ljsj);
            loadMailValue(gwk.email);
            loadFZJG(gwk.fzjg);
            loadPhoneNum(gwk.mobile);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
    }
   
    /*private boolean getLoadInfo()
    {
    	try
    	{
    		//测试用数据
    		GetNationality();
    		GetThplace();
    		GetZJType();
    		//GetFlights();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return true;
    }*/
    
    /*private boolean GetNationality()
    {
    	try
    	{
    		this.vecNational = new Vector();
    		
    		//从BASE里取数据
    		//以下是模拟测试数据
    		NationalityDef n = new NationalityDef();
    		n.PCRCODE = "1";
    		n.PCRCNAME = "中国";
    		n.PCRENAME = "CHN";
    		this.vecNational.add(n);
    		
    		n = new NationalityDef();
    		n.PCRCODE = "2";
    		n.PCRCNAME = "香港";
    		n.PCRENAME = "HKG";
    		this.vecNational.add(n);
    		
    		n = new NationalityDef();
    		n.PCRCODE = "3";
    		n.PCRCNAME = "澳门";
    		n.PCRENAME = "MAC";
    		this.vecNational.add(n);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return true;
    }*/

    /*private boolean GetThplace()
    {
    	try
    	{
    		this.vecTHPlace  = new Vector();
    		//从LOCAL里取数据
    		//以下是模拟测试数据
    		THPlaceDef p = new THPlaceDef();
    		p.thbillno = "01";
    		p.thsp = "Sanya International";
    		p.thtime = 4;
    		p.thjc = "INT";
    		this.vecTHPlace.add(p);
    		
    		p = new THPlaceDef();
    		p.thbillno = "02";
    		p.thsp = "三亚国内出发厅";
    		p.thtime = 6;
    		p.thjc = "";
    		this.vecTHPlace.add(p);
    		
    		p = new THPlaceDef();
    		p.thbillno = "03";
    		p.thsp = "海口国内出发厅";
    		p.thtime = 24;
    		p.thjc = "HAK";
    		this.vecTHPlace.add(p);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return true;
    }*/

   /* private boolean GetZJType()
    {
    	try
    	{
    		this.vecZJType = new Vector();
    		//从LOCAL里取数据
    		//以下是模拟测试数据
    		ZJTypeDef zj = new ZJTypeDef();
    		zj.zjid = "1";
    		zj.zjname = "护照";
    		this.vecZJType.add(zj);
    		
    		zj = new ZJTypeDef();
    		zj.zjid = "2";
    		zj.zjname = "身份证";
    		this.vecZJType.add(zj);
    		
    		zj = new ZJTypeDef();
    		zj.zjid = "3";
    		zj.zjname = "通行证";
    		this.vecZJType.add(zj);
    		
    		zj = new ZJTypeDef();
    		zj.zjid = "4";
    		zj.zjname = "台胞证";
    		this.vecZJType.add(zj);
    		    		
    		zj = new ZJTypeDef();
    		zj.zjid = "5";
    		zj.zjname = "其它";
    		this.vecZJType.add(zj);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return true;
    }*/

    /*private boolean GetFlights()
    {
    	try
    	{
    		this.vecFlights = new Vector();
    		//实时从POSDB里取数据
    		//以下是模拟测试数据
    		Zmsy_AccessLocalDB local = (Zmsy_AccessLocalDB)AccessLocalDB.getDefault();
    		local.getFlights(vecFlights, "", false);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return true;
    }*/

    //
    private void loadNationalValue(String value)
    {
    	try
    	{
    		if (value==null || value.length()<=0)
    		{
    			this.textEx_national.setText("");
    			return;
    		}
    		this.textEx_national.select(value);
    		/*NationalityDef n;
    		for (int i=0; i<this.vecNational.size(); i++)
    		{
    			n=(NationalityDef)this.vecNational.elementAt(i);
    			if (n!=null && (n.PCRCODE.equals(value) || n.PCRENAME.equals(value)))
    			{
    				this.textEx_national.select(i);
    				break;
    			}
    		}*/
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void LoadThplaceValue(String value)
    {
    	try
    	{
    		if (value==null || value.length()<=0)
    		{
    			this.textEx_thplace.setText("");
    			return;
    		}
    		this.textEx_thplace.select(value);
    		/*THPlaceDef p;
    		for (int i=0; i<this.vecTHPlace.size(); i++)
    		{
    			p = (THPlaceDef)this.vecTHPlace.elementAt(i);
    			if (p!=null && p.thbillno.equals(value))
    			{
    				this.textEx_thplace.select(i);
    				break;
    			}
    		}*/
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void loadZJTypeValue(String value)
    {
    	try
    	{
    		if (value==null || value.length()<=0)
    		{
    			this.combo_zjtype.setText("");
    			return;
    		}
    		/*ZJTypeDef zj;
    		for(int i=0; i<this.vecZJType.size(); i++)
    		{
    			zj = (ZJTypeDef)this.vecZJType.elementAt(i);
    			if (zj != null && zj.zjid.equals(value))  
    			{
    				this.textEx_zjtype.select(i);
    				break;
    			}
    		}*/
    		String[] items = combo_zjtype.getItems();
    		for(int i=0; i<items.length; i++)
    		{
    			if (getID(items[i]).equalsIgnoreCase(value))
    			{
    				combo_zjtype.select(i);
    				break;
    			}
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void loadFlightValue(String value)
    {
    	try
    	{
    		if (value==null || value.length()<=0) 
    		{
    			this.textEx_flight.setText("");
    			return;
    		}
    		this.textEx_flight.select(value);
    		/*FlightsDef f;
    		for (int i=0; i<this.vecFlights.size(); i++)
    		{
    			f = (FlightsDef)this.vecFlights.elementAt(i);
    			if (f!=null && f.fnumber.equals(value))
    			{
    				this.textEx_flight.select(i);
    				break;
    			}
    		}*/
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void loadCustTypeValue(String value)
    {
    	try
    	{
    		if (value==null || value.length()<=0)
    		{
    			this.textEx_gklb.setText("");
    			return;
    		}
    		this.textEx_gklb.select(value);
    		/*if (value.equals(Zmsy_StatusType.ZMSY_GKTYPE_LJ))
    		{
    			this.textEx_gklb.select(0);
    		}
    		else if (value.equals(Zmsy_StatusType.ZMSY_GKTYPE_LD))
    		{
    			this.textEx_gklb.select(1);
    		}
    		else if (value.equals(Zmsy_StatusType.ZMSY_GKTYPE_BDLD))
    		{
    			this.textEx_gklb.select(2);
    		}*/
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }   
    //
    private void loadSexValue(String value)
    {
    	try
    	{
    		if (value==null || value.length()<=0)
    		{
    			this.combo_sex.setText("");
    			return;
    		}

    		if (value.equals("0") || value.equals("男"))
    		{
    			this.combo_sex.select(0);//男
    		}
    		else
    		{
    			this.combo_sex.select(1);//女
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void loadIsDx(String value)
    {
    	try
    	{
    		if (value==null || value.length()<=0)
    		{
    			this.combo_isDX.setText("");
    			return;
    		}
    		if (value.equals("Y") || value.equals("是"))
    		{
    			this.combo_isDX.select(1);//是
    		}
    		else
    		{
    			this.combo_isDX.select(0);//否
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    //
    private void loadCardNoValue(String value)
    {    	
    }
    //
    private void loadCardNameValue(String value)
    {
    	if (value==null) value="";
    	this.text_name.setText(value);
    }
    //
    private void loadZJNoValue(String value)
    {
    	if (value==null) value="";
    	this.text_zjno.setText(value);
    	if (gwk.zjlb != null && gwk.zjlb.length()>0 && gwk.passport != null && gwk.passport.length()>0)
    	{
    		this.text_zjno.setEditable(false);
    		this.text_zjno.setEnabled(false);
    		//this.textEx_zjtype.setEditable(false);
    		this.combo_zjtype.setEnabled(false);
    	}
    }
    //
    private void loadBirthValue(String value)
    {
    	if (value == null || value.length()<6) return;
    	String arrDate[] = value.split("-");
    	if (arrDate.length<3) return;
    	this.date_csrq.setYear(Convert.toInt(arrDate[0]));
    	//this.date_csrq.setMonth(Convert.toInt(arrDate[1])-1);
    	this.date_csrq.setDay(Convert.toInt(arrDate[2]));
    	this.date_csrq.setMonth(Convert.toInt(arrDate[1])-1);//wangyong add by 2013.10.31 放到日期后面赋值,否则有问题
    	//date_csrq.getMonth()

    }
    //
    private void loadAgeValue(String value)
    {
    	this.text_age.setText(String.valueOf(Convert.toInt(value)));
    }
    //
    private void loadZKLValue(double value)
    {
    	this.text_zkl.setText(String.valueOf(ManipulatePrecision.doubleConvert(value*100.00)));
    }
    //
    private void loadLjDateTimeValue(String ljrq,String ljsj)
    {
    	try
    	{
    		if (ljrq == null || ljrq.length()<6) return;
    		 
        	
        	//离境日期
        	String arrDate[] = ljrq.split("-");
        	if (arrDate.length<3) return;
        	this.date_ljrq.setYear(Convert.toInt(arrDate[0]));
        	this.date_ljrq.setDay(Convert.toInt(arrDate[2]));
        	this.date_ljrq.setMonth(Convert.toInt(arrDate[1])-1);//日期要减1(值比实际显示的要小1)

    		if (ljsj == null || ljsj.length()<3) return;
        	//离境时间
        	String arrTime[] = ljsj.split(":");
        	if (arrTime.length<2) return;
        	this.date_ljsj.setHours(Convert.toInt(arrTime[0]));
        	this.date_ljsj.setMinutes(Convert.toInt(arrTime[1]));
        	
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void loadMailValue(String value)
    {
    	try
    	{
    		if (value==null) value="";
    		this.text_email.setText(value);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void loadFZJG(String value)
    {
    	try
    	{
    		if (value==null) value="";
    		this.text_fzjg.setText(value);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private void loadPhoneNum(String value)
    {
    	try
    	{
    		if (value==null) value="";
    		this.text_phoneno.setText(value);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    private String getTHDD_Name(String id)
    {
    	THPlaceDef p = null;
    	for (int i=0; i<this.vecTHPlace.size(); i++)
    	{
    		p = (THPlaceDef)vecTHPlace.elementAt(i);
    		if (p==null) continue;
    		if (p.thbillno.equalsIgnoreCase(id)) return p.thsp.trim();
    	}
    	return "[" + id + "]";
    }
    
    private boolean sendGWKInfo()
    {
    	try
    	{
    		if (!getInputValue()) return false;
    			
    		ProgressBox pb = null;
    		try
    		{
    			pb = new ProgressBox();
    	        pb.setText("正在获取和上传海关信息....");
    	        
    	        //1.获取海关信息
                //2.保存购物卡信息到后台DB
                //3.发送海关信息到后台DB
    	        StringBuffer sbMsg = new StringBuffer();
    	        Zmsy_SaleBS bs =  (Zmsy_SaleBS)saleBS;
    	        gwk.code = gwk.zjlb.trim() + gwk.passport.trim();
    	        int intRet = bs.SendGWKInfoToHG(sbMsg);
    	        if (intRet != 1 && intRet != -3)
    	        {
    	        	this.retMsg = sbMsg.toString();
    	        	this.isChanageOK = false;
    	        	if(retMsg!=null && retMsg.length()>0) this.showMsg("卡信息保存失败:" + this.retMsg);//去掉此提示框（有多个）
    	        	return false;    	        	
    	        }
    	        
    	        if (intRet == -3)
    	        {
    	        	if (bs.sendGwkInfo(gwk, sbMsg) != 1)
    	        	{
    	        		this.retMsg = sbMsg.toString();
        	        	this.isChanageOK = false;
        	        	//this.showMsg("卡信息保存失败:" + this.retMsg);//去掉此提示框（有多个）
        	        	return false;  
    	        	}
    	        }
    	        
    	        this.isChanageOK = true;
    	    	return true;
    		}
    		catch(Exception ex)
    		{
    			this.writeLog(ex);
    			return false;
    		}
    		finally
    		{
    			 if (pb != null)
                 {
                     pb.close();
                     pb = null;
                 }
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    		return false;
    	}
    }
    
    private boolean getInputValue()
    {
    	try
    	{
			//购物卡号

			//顾客类别
    		if (checkInputValue(this.textEx_gklb.getText()))
    		{
    			this.gwk.gklb = getID(this.textEx_gklb.getText());
    		}
    		else
    		{
    			this.setTextExFocus(this.textEx_gklb);
    			showMsg(textEx_gklb.getData("name") + "不允许为空");
    			return false;
    		}
    		
    		if (!checkJGJT_LJ(gwk.gklb)) return false;
    		
			//离境航班
    		if (checkInputValue(this.textEx_flight.getText()))
    		{
    			gwk.ljhb = getFlightId(this.textEx_flight.getText());
    			if (gwk.ljhb.equals("ZZZZZZZ"))//保存时判断如果航班号是ZZZZZZZ，不允许保存
    			{
    				this.setTextExFocus(this.textEx_flight);
        			showMsg("航班号不允许为:" + gwk.ljhb);
        			return false;
    			}
    		}
    		else
    		{
				this.setTextExFocus(this.textEx_flight);
    			showMsg(textEx_flight.getData("name") + "不允许为空");
    			return false;
    		}
    		//离境时间
    		gwk.ljrq = ManipulateDateTime.getFormatDate(this.date_ljrq.getYear(), date_ljrq.getMonth(), date_ljrq.getDay());
    		gwk.ljsj = ManipulateDateTime.getFormatTime(this.date_ljsj.getHours(), date_ljsj.getMinutes(), date_ljsj.getSeconds());
    		
			//提货地点
    		if (checkInputValue(this.textEx_thplace.getText()))
    		{
    			String thgklb=getGklbIdByThddId(this.getID(this.textEx_thplace.getText()));
    			if (thgklb!=null && thgklb.trim().length()>0)
    			{
    				if (thgklb.equalsIgnoreCase(Zmsy_StatusType.ZMSY_GKTYPE_LJ) &&
    						!gwk.gklb.equalsIgnoreCase(Zmsy_StatusType.ZMSY_GKTYPE_LJ))
    				{//若提货地点是【离境】（即国际厅），则顾客类别必须是离境
    					this.setTextExFocus(this.textEx_thplace);
    					//this.showMsg("提货地点(" + this.textEx_thplace.getText() + ") \n与\n顾客类别(" + this.textEx_gklb.getText() + ")\n不匹配！");
    					this.showMsg("【提货地点】与【顾客类别】不匹配！");
    					return false;
    				}
    				if (!thgklb.equalsIgnoreCase(Zmsy_StatusType.ZMSY_GKTYPE_LJ) &&
    						gwk.gklb.equalsIgnoreCase(Zmsy_StatusType.ZMSY_GKTYPE_LJ))
    				{//若提货地点是【非离境】（即国内厅），则顾客类别不能是离境
    					this.setTextExFocus(this.textEx_thplace);
    					//this.showMsg("提货地点(" + this.textEx_thplace.getText() + ") \n与\n顾客类别(" + this.textEx_gklb.getText() + ")\n不匹配！");
    					this.showMsg("【提货地点】与【顾客类别】不匹配！");
    					return false;
    				}
    			}
    			
    			gwk.thdd = getID(textEx_thplace.getText());
    		}
    		else
    		{
				this.setTextExFocus(this.textEx_thplace);
    			showMsg(textEx_thplace.getData("name") + "不允许为空");
    			return false;
    		}

			//证件号
    		if (checkInputValue(this.text_zjno.getText()))
    		{
    			gwk.passport = this.text_zjno.getText().trim();
    		}
    		else
    		{
    			this.setTextBoxFocus(this.text_zjno);
    			showMsg("请输入" + text_zjno.getData("name"));
    			return false;
    		}

			//证件类别
    		if (checkInputValue(this.combo_zjtype.getText()))
    		{
    			gwk.zjlb = getID(this.combo_zjtype.getText().trim());
    		}
    		else
    		{
    			this.setComboBoxFocus(this.combo_zjtype);
    			showMsg(combo_zjtype.getData("name") + "不允许为空");
    			return false;
    		}

			//离境，不能使用居民身份证
			if(!checkLJ_ZJTYPE()) 
			{
				this.setTextExFocus(this.textEx_gklb);
    			showMsg("【离境顾客】不能使用【境内旅客居民身份证】");
    			return false;
			}

			//顾客姓名
    		if (checkInputValue(this.text_name.getText()))
    		{
    			/*if (this.text_name.getText().trim().getBytes().length>50)
    			{
    				this.setTextBoxFocus(this.text_name);
        			showMsg(text_name.getData("name") + "内容输入超过限制50");
        			return false;
    			}*/
    			if (!checkName()) return false;
    			gwk.name = this.text_name.getText().trim();
    		}
    		else
    		{
    			this.setTextBoxFocus(this.text_name);
    			showMsg(text_name.getData("name") + "不允许为空");
    			return false;
    		}

			//性别
    		if (checkInputValue(this.combo_sex.getText()))
    		{
    			gwk.gender = getID(this.combo_sex.getText().trim());
    		}
    		else
    		{
    			this.setComboBoxFocus(this.combo_sex);
    			showMsg(combo_sex.getData("name") + "不允许为空");
    			return false;
    		}
    		
    		//生日
    		gwk.birth = ManipulateDateTime.getFormatDate(this.date_csrq.getYear(), date_csrq.getMonth(), date_csrq.getDay());
    		
    		//年龄,允许为空
    		gwk.age = String.valueOf(Convert.toInt(this.text_age.getText()));
    		/*if (Convert.toInt(gwk.age)>120)
    		{
    			//this.setTextBoxFocus(this.text_age);
    			this.date_csrq.setFocus();//年龄不允许修改，所以跳到出生日期控件上
    			showMsg(text_age.getData("name") + "必须小于120");
    			return false;
    		}*/
    		if (!checkCSRQ()) return false;

			//发证机关
    		if (checkInputValue(this.text_fzjg.getText()))
    		{
    			/*if (this.text_fzjg.getText().trim().getBytes().length>50)
    			{
    				this.setTextBoxFocus(this.text_fzjg);
        			showMsg(text_fzjg.getData("name") + "内容输入超过限制50");
        			return false;
    			}*/
    			if (!checkFZJG()) return false;
    			gwk.fzjg = this.text_fzjg.getText().trim();    			
    		}
    		else
    		{
    			//发证机关不允许为空 for 中免陈奕焕 2013.8.13
    			this.setTextBoxFocus(this.text_fzjg);
    			showMsg(text_fzjg.getData("name") + "不允许为空");
    			return false;
    			/*//当证件类型为身份证时，发证机关为必填项
    			if (getID(this.textEx_zjtype.getText()).equals(Zmsy_StatusType.ZJTYPE_SFZ))
    			{
    				this.setTextBoxFocus(this.text_fzjg);
        			showMsg(text_fzjg.getData("name") + "不允许为空");
        			return false;
    			}    */			
    		}

			//国籍
    		if (checkInputValue(this.textEx_national.getText()))
    		{    			
    			gwk.nation = getID(this.textEx_national.getText().trim(),'R');
    			/*if (!checkNational(gwk.nation))
    			{
    				//输入的国籍和系统记录的国籍不一致，系统提示输入失败 FOR SANYA
    				this.setTextExFocus(this.textEx_national);
        			showMsg(textEx_national.getData("name") + "输入错误：此国籍不存在");
        			return false;
    			}*/
    			if(!checkNation()) return false;
    		}
    		else
    		{
    			this.setTextExFocus(this.textEx_national);
    			showMsg(textEx_national.getData("name") + "不允许为空");
    			return false;
    		}
    		
    		//Email,允许为空,但如果填了就要合法
    		/*if (this.text_email.getText().trim().getBytes().length>11)
    		{
    			this.setTextBoxFocus(this.text_email);
    			showMsg(text_email.getData("name") + "内容输入超过限制30");
    			return false;
    		}*/
    		if(!checkEmail()) return false;
    		gwk.email = this.text_email.getText().trim();
    		/*if (gwk.email.length()>0 && gwk.email.indexOf("@")<1 && gwk.email.lastIndexOf(".")<2)
    		{
    			this.setTextBoxFocus(this.text_email);
    			showMsg(text_email.getData("name") + "不合法");
    			return false;
    		}*/

			//手机号码
    		if (checkInputValue(this.text_phoneno.getText()))
    		{
    			/*if (this.text_phoneno.getText().getBytes().length>11)
    			{
    				this.setTextBoxFocus(this.text_phoneno);
        			showMsg(text_phoneno.getData("name") + "内容输入超过限制11");
        			return false;
    			}
    			if (!isDigit(this.text_phoneno.getText()))
    			{
    				this.setTextBoxFocus(this.text_phoneno);
        			showMsg(text_phoneno.getData("name") + "输入不合法");
        			return false;
    			}*/
    			if (!checkPhoneNo()) return false;
    			gwk.mobile = this.text_phoneno.getText().trim();
    		}
    		else
    		{
    			this.setTextBoxFocus(this.text_phoneno);
    			showMsg(text_phoneno.getData("name") + "不允许为空");
    			return false;
    		}
    		
    		//是否发送短信
    		gwk.isdx = this.combo_isDX.getText().equals("是")==true ? "Y":"N";
    		
    		return true;
    		
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return false;
    }
    
    private boolean checkLJ_ZJTYPE()
    {
    	try
    	{

			//1、  如果为离岛，不限制证件类型。即身份证、护照、港澳通行证等合法证件均可。
			//2、  如果为离境，不能使用居民身份证。只能使用护照、港澳通行证等相关合法证件。
			//wangyong add 2014.6.12 for 张磊
			if(gwk.gklb.equalsIgnoreCase(Zmsy_StatusType.ZMSY_GKTYPE_LJ) && 
					gwk.zjlb.equals(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
			{
				return false;
			}
    		return true;
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return false;
    }
    
    private boolean checkName()
    {
    	if (this.text_name.getText().trim().getBytes().length>50)
		{
			this.setTextBoxFocus(this.text_name);
			showMsg(text_name.getData("name") + "内容输入超过限制50");
			return false;
		}
    	return true;
    }
    
    private boolean checkCSRQ()
    {
    	if (Convert.toInt(gwk.age)>120)
		{
			//this.setTextBoxFocus(this.text_age);
			this.date_csrq.setFocus();//年龄不允许修改，所以跳到出生日期控件上
			showMsg(text_age.getData("name") + "必须小于120");
			return false;
		}
    	return true;
    }
    
    private boolean checkFZJG()
    {
    	if (this.text_fzjg.getText().trim().getBytes().length>50)
		{
			this.setTextBoxFocus(this.text_fzjg);
			showMsg(text_fzjg.getData("name") + "内容输入超过限制50");
			return false;
		}
    	return true;
    }
    
    private boolean checkNation()
    {
    	if (!checkNational(getID(this.textEx_national.getText().trim(),'R')))//gwk.nation
		{
			//输入的国籍和系统记录的国籍不一致，系统提示输入失败 FOR SANYA
			this.setTextExFocus(this.textEx_national);
			showMsg(textEx_national.getData("name") + "输入错误：此国籍不存在");
			return false;
		}
    	return true;
    }
    
    private boolean checkEmail()
    {
    	String email=this.text_email.getText().trim();

    	if (email.length()>0 && email.indexOf("@")<1 && email.lastIndexOf(".")<2)
		{
			this.setTextBoxFocus(this.text_email);
			showMsg(text_email.getData("name") + "不合法");
			return false;
		}
    	
    	if (email.getBytes().length>30)
		{
			this.setTextBoxFocus(this.text_email);
			showMsg(text_email.getData("name") + "内容输入超过限制30");
			return false;
		}
    	return true;
    }
    
    private boolean checkPhoneNo()
    {
    	if (this.text_phoneno.getText().getBytes().length>11)
		{
			this.setTextBoxFocus(this.text_phoneno);
			showMsg(text_phoneno.getData("name") + "内容输入超过限制11");
			return false;
		}
		if (!isDigit(this.text_phoneno.getText()))
		{
			this.setTextBoxFocus(this.text_phoneno);
			showMsg(text_phoneno.getData("name") + "输入不合法");
			return false;
		}
		return true;
    }
    
    /**
     * 通过提货地点（从提货类中）获取顾客类别ID
     * @param thddid 提货地点ID
     * @return 顾客类别ID
     */
    private String getGklbIdByThddId(String thddid)
    {
    	String gklbid = null;
    	try
    	{
    		if (thddid==null || thddid.length()<=0)
    		{
    			return null;
    		}
    		
    		THPlaceDef p;
    		for (int i=0; i<this.vecTHPlace.size(); i++)
    		{
    			p = (THPlaceDef)this.vecTHPlace.elementAt(i);
    			if (p!=null && p.thbillno.equals(thddid))
    			{
    				gklbid=p.thgklb;
    				break;
    			}
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return gklbid;
    }
    
    private boolean getpCard2()
    {
    	try
    	{
    		this.writeLog("设备读身份证开始...");
    		//当证件号和证件类别有值的情况下，不再录入
    		if (checkInputValue(this.text_zjno.getText()) && checkInputValue(this.combo_zjtype.getText()))
    		{
    			this.writeLog("设备读身份证失败:当证件号和证件类别有值的情况下，不再录入");
    			showMsg("身份证号读取失败：当前已经存在证件信息\n\n若需要重新录入证件,请退出当前窗口后重试");
    			return true;
    		}
    		
    		//读取身份证信息
    		String str = ReadZJNO.getSfzInfo();
    		this.writeLog("设备读身份证:[" + String.valueOf(str) + "].");
    		if (str==null || !str.split(",")[0].trim().equals("0"))
    		{
    			showMsg("身份证号读取失败：" + String.valueOf(str));
    			return false;
    		}
    		setpCard2(str);
    		boolean blnRet = findGWKInfo();//true;//要自动触发去查证件号
    		setpCard2(str);//找卡之后,还要将之前读取的身份证信息再记录,否则会丢失
    		return blnRet;
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return false;
    }
    
    public void setpCard2(String str)
    {
    	try
    	{
    		//赋值身份证信息
    		String[] cardinfo = str.split(",");//\\|
    		//姓名
    		gwk.name = cardinfo[1];
    		loadCardNameValue(gwk.name);
    		
    		//性别
    		gwk.gender = cardinfo[2].trim().equals("男")==true ? Zmsy_StatusType.ZMSY_SEX_MALE:Zmsy_StatusType.ZMSY_SEX_FEMALE;
    		loadSexValue(gwk.gender);
    		
    		//一刷卡，就把类型写成身分证
    		gwk.zjlb = Zmsy_StatusType.ZMSY_ZJTYPE_SFZ;
    		loadZJType();
    		loadZJTypeValue(gwk.zjlb);
    		
    		//如果是“海南”那就是本岛离岛03,否则就是离岛02
    		if (cardinfo[5].trim().startsWith("海南"))
    		{
    			gwk.gklb = Zmsy_StatusType.ZMSY_GKTYPE_BDLD;
    		}
    		else
    		{
    			gwk.gklb = Zmsy_StatusType.ZMSY_GKTYPE_LD;//要求：顾客类别写成离岛
    		}
    		loadCustTypeValue(gwk.gklb);
    		
    		//家庭住址
    		gwk.address = cardinfo[5].trim(); 
    		
    		//证件号码
    		gwk.passport = cardinfo[6].trim();
    		this.text_zjno.setText(gwk.passport);
    		
    		//发证机关
    		gwk.fzjg = cardinfo[7].trim();
    		this.text_fzjg.setText(gwk.fzjg);
    		
    		//加载生日
    		checkLoadBirthValue();
    		gwk.birth = ManipulateDateTime.getFormatDate(this.date_csrq.getYear(), date_csrq.getMonth(), date_csrq.getDay());
    		
    		//国籍
    		loadNationalValue("CHN");
    		gwk.nation = getID(this.textEx_national.getText(),'R');
    		    			
    		//年龄
    		gwk.age = String.valueOf(getAge());
    		loadAgeValue(gwk.age);
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    /**
     * 获取身份证信息
     * @return 0成功 其它为失败
     *   成功时返回格式为 0,姓名,性别,民族,出生日期,地址,身份证号码,签发机关,有效开始日期,有效截止日期,新地址
     *//*
    private String getSfzInfo()
    {
    	try
    	{
    		String strRequest = ",,,,,,,,,,";//传10个逗号
    		String strResult = "";
    		String strRequestPath = ConfigClass.BankPath + "\\request.txt";
    		String strResultPath = ConfigClass.BankPath + "\\result.txt";
    		String strExePath = ConfigClass.BankPath + "\\javaposbank.exe";
    		
    		//删除响应文件
    		if (PathFile.fileExist(strResultPath))
            {
                PathFile.deletePath(strResultPath);
                
                if (PathFile.fileExist(strResultPath))
                {
            		return "读取护照号失败,删除通讯文件失败";   	
                }
            }
    		
    		//写入请求信息
    		PrintWriter pw = null;
    		try
	         {
	            pw = CommonMethod.writeFile(strRequestPath);
	            if (pw != null)
	            {
	                pw.println(strRequest);
	                pw.flush();
	            }
	         }
	         finally
	         {
	        	if (pw != null)
	        	{
	        		pw.close();
	        	}
	         }
    		
	         //调用接口
	         if (PathFile.fileExist(strExePath))
             {
	     		//读取javaposbank.exe PERSONINFO
             	CommonMethod.waitForExec(strExePath + " PERSONINFO", "javaposbank.exe");
             }
             else
             {
                 return "找不到读取身份证的工程接口文件:" + strExePath;
             }
    		
	         //读取数据
	         BufferedReader br = null;
	         if (!PathFile.fileExist(strResultPath) || ((br = CommonMethod.readFileGBK(strResultPath)) == null))
	         {	                
	                return "读取身份证应答数据失败!";
	          }
	         strResult = br.readLine();

	         if (strResult == null || strResult.trim().length() <= 0)
	         {
	            return "未读取到身份证数据!";
	         }
	         
	         //返回身份证数据
	         return strResult;
    		
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return "读取身份证数据时异常:" + ex.getMessage();
    	}
    }*/
    
    private boolean checkInputValue(String value)
    {
    	if (value == null || value.trim().length()<=0) return false;
    	return true;
    }
    
    //
    private int findHangBan()
    {
    	int intRet=-1;
    	ProgressBox pb = null;
    	try
    	{
    		String strMsg = "";
    		if (checkInputValue(this.textEx_flight.getText()))
    		{
    			//离境航班
    			gwk.ljhb = getFlightId(this.textEx_flight.getText());
    		}
    		else
    		{
    			this.setTextExFocus(this.textEx_flight);//SetComboBoxFocus(cbxFlight)
    			showMsg(textEx_flight.getData("name") + "不允许为空");
    			return 0;
    		}
    		
    		if (checkInputValue(this.textEx_gklb.getText().trim()))
    		{
    			//顾客类别
    			gwk.gklb=getID(this.textEx_gklb.getText());
    		}
    		else
    		{
    			this.setTextExFocus(this.textEx_gklb);//SetComboBoxFocus(cbxCustTYPE)
    			showMsg(textEx_gklb.getData("name") + "不允许为空");
    			return 0;
    		}
    		
    		gwk.ljrq = ManipulateDateTime.getFormatDate(this.date_ljrq.getYear(), date_ljrq.getMonth(), date_ljrq.getDay());
    		gwk.ljsj = ManipulateDateTime.getFormatTime(this.date_ljsj.getHours(), date_ljsj.getMinutes(), date_ljsj.getSeconds());
    		
    		pb = new ProgressBox();
    		pb.setText("正在从【EOP】获取航班信息,请等待...");
    		Zmsy_NetService netservice = (Zmsy_NetService)NetService.getDefault();
    		if (netservice.findHangBan(gwk.ljhb, gwk.ljrq, gwk.ljsj, gwk.gklb, gwk))//,strMsg
    		{
    			intRet = 1;
    			//赋值离境日期
    			int year = ManipulateDateTime.getYear(gwk.ljrq);
    			int month = ManipulateDateTime.getMonth(gwk.ljrq);
    			int day = ManipulateDateTime.getDay(gwk.ljrq);
    			if (year>-1 && month>-1 && day>-1)
    			{
    				this.date_ljrq.setYear(year);
    				this.date_ljrq.setDay(day);
    				this.date_ljrq.setMonth(month-1);
    			}
    			else
    			{
    				writeLog("FindHangBan() ljrq=[" + gwk.ljrq + "]格式不正确,离境日期赋值失败");    				
    			}
    			
    			//赋值离境时间
    			int hours =ManipulateDateTime.getHours(gwk.ljsj);;
    			int minutes=ManipulateDateTime.getMinutes(gwk.ljsj);
    			int seconds=ManipulateDateTime.getSeconds(gwk.ljsj);
    			if (hours>-1 && minutes>-1)
    			{
    				this.date_ljsj.setHours(hours);
    				this.date_ljsj.setMinutes(minutes);
    				if (seconds<0)seconds=0;
    				this.date_ljsj.setSeconds(seconds);
    			}
    			else
    			{
    				writeLog("FindHangBan() ljrq=[" + gwk.ljrq + "]格式不正确,离境时间赋值失败");
    			}
    			
    			LoadThplaceValue(gwk.thdd);
    			loadCustTypeValue(gwk.gklb);
    			
    			//processGKLB();//特殊处理//不处理，按付款键时处理
    		}
    		else
    		{   
    			strMsg = "";
    			this.retMsg = strMsg;
    			showMsg("从EOP查找航班信息失败:" + strMsg);
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
		finally
		{
			if (pb != null)
            {
                pb.close();
                pb = null;
            }
		}
    	return intRet;
    }
    
    /**
     * 处理顾客类别选项（当成功查找航班之后）
     *
     */
    private void processGKLB()
    {
    	try
    	{
    		//航班查询成功之后：
    		//（和航班绑定）如果是离境的航班，营业员无法自选顾客类别；
    		//           如果是离岛的，营业员可以选择顾客类别，但是无法选择离境
    		//即：当是顾客类别为离境时，不允许选择顾客类别，否则只能选离岛或本地离岛    		
    		//通过FlightsDef.fport1(提货地点ID)=THPlace.thbillno 取得-->THPlace.thgklb，并确认顾客类别的选择
    		if (gwk.gklb.equalsIgnoreCase(Zmsy_StatusType.ZMSY_GKTYPE_LJ))
    		{
    			//不允许选择顾客类别
    			loadCustType();
    			this.textEx_gklb.setEnabled(false);
    			
    			//如果是离境的航班，系统默认为国际厅(gwk.gklb=THPlace.thgklb -->THPlace.thbillno)，且不能更改
    			this.textEx_thplace.setEnabled(false);
    		}
    		else
    		{
    			//顾客类别只能选离岛或本地离岛 
    			this.textEx_gklb.setEnabled(true);
    			loadCustType(false);
    			
    			//提货地点不能选国际出发厅
    			this.textEx_thplace.setEnabled(true);
    			loadThplace(Zmsy_StatusType.ZMSY_GKTYPE_LJ);//将离境去掉
    		}
    		
    		
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	
    }
    
    private String getFlightId(String value)
    {
    	try
    	{
    		String v[] = this.textEx_flight.getText().split("-");
    		if (v.length<5) return "";
    		
    		String id = "";
    		String tmp = getPY(v[2].trim() + v[5].trim());
    		if (v[0].startsWith( tmp ))
    		{
    			id=v[0].substring(0, tmp.length());//+1 java环境不用加1
    		}
    		else if (v[0].startsWith( getPY( v[5].trim() ) ))
    		{
    			id=v[0].substring(0, getPY( v[5].trim() ).length());//+1 java环境不用加1
    		}
    		else
    		{
    			id=getID(this.textEx_flight.getText());
    		}
    		return id.trim();
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return "";
    }
    
    private void setComboBoxFocus(Combo combo)
    {
    	combo.setFocus();
    	//SelectAll
    }
    
    private void setTextBoxFocus(Text txt)
    {
    	txt.setFocus();
    	txt.selectAll();
    }
    
    private void setTextExFocus(TextEx txt)
    {
    	txt.setFocus();
    	txt.selectAll();
    }
    
    /**
     * 从中航信接口获取顾客航班信息
     * @param isSGGetInfo 是否手工强制获取
     */
    private void invokeZhxApi(boolean isSGGetInfo)
    {
    	if (GlobalInfo.sysPara.isInvokeZHX != 'Y') return;
    	
		if(isSGGetInfo) writeLog("invokeZhxApi 手动获取");
    	try
    	{
    		String certType=getID(this.combo_zjtype.getText());
        	if(!checkInputValue(certType))
        	{
        		writeLog("invokeZhxApi() false certType[" + certType + "]不合法");
        		if(isSGGetInfo) this.showMsg("操作失败：证件类型不合法");
        		return;
        	}
        	else if(certType.equalsIgnoreCase(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
        	{
        		certType="NI";
        	}
        	else
        	{
        		certType="PP";
        	}
        	
        	String certId = this.text_zjno.getText();
    		if (!checkInputValue(certId))
    		{
        		writeLog("invokeZhxApi() false certId[" + certId + "]不合法");
        		if(isSGGetInfo) this.showMsg("操作失败：证件号码不合法");			
    			return;    			
    		}
    		
    		String name = this.text_name.getText().trim();
    		if (!checkInputValue(name))
    		{
        		writeLog("invokeZhxApi() false name[" + name + "]不合法");
        		if(isSGGetInfo) this.showMsg("操作失败：顾客姓名不合法");
    			return;    			
    		}
    		String phone = this.text_phoneno.getText().trim();
    		
    		//时间在合理范围内
    		if(isSGGetInfo==false)
    		{
    			//离境日期时间
    			String ljrq = ManipulateDateTime.getFormatDate(this.date_ljrq.getYear(), date_ljrq.getMonth(), date_ljrq.getDay());
    			String ljsj = ManipulateDateTime.getFormatTime(this.date_ljsj.getHours(), date_ljsj.getMinutes(), date_ljsj.getSeconds());
    			boolean isOK = false;
    			long diff = new ManipulateDateTime().compareDate(ljrq, ManipulateDateTime.getCurrentDate());
    			if (diff==0)
    			{
    				//日期相等时，比较时间
    				diff = new ManipulateDateTime().compareTime(ljsj, ManipulateDateTime.getCurrentTime());
    				if(diff<0) isOK=true;
    			}
    			else if(diff>0)
    			{
    				
    			}
    			else
    			{
    				//当离境时间小于当前操作时间时，则调用中航信接口，否则不调
    				isOK=true;
    			}
    			if(!isOK)
    			{
    				writeLog("invokeZhxApi() false ljrq=[" + ljrq + "],ljsj=[" + ljsj + "]");
    				//this.showMsg("操作失败：离境日期和时间不正确");
    				return;
    			}
    		}
    		
    		//开始获取顾客航班信息
    		ZhxFlightApiDef f = ReadZJNO.getFlight(name, certId, certType, phone);
    		if (f==null) return;
    		isInvokeZhxApi=true;
    		//填充信息(航班号、起飞日期、起飞时间
    		if (f.pflightno!=null) f.pflightno=f.pflightno.trim();
    		if (f.deptdate!=null) f.deptdate=f.deptdate.trim();
    		if (f.depttime!=null) f.depttime=f.depttime.trim();
    		
    		this.loadFlightValue(f.pflightno);
    		this.loadLjDateTimeValue(f.deptdate, f.depttime);
    		this.writeLog("invokeZhxApi 从【中航信】获取顾客航班信息成功");
    		
    		//从中航信获取航班信息后，再从业务获取提货地点
    		if(findHangBan()==1) this.writeLog("从【EOP】获取航班信息成功");
    		this.loadLjDateTimeValue(f.deptdate, f.depttime);
    		
    		if(isSGGetInfo) showMsg("从【中航信】获取顾客航班信息成功");
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    //
    private boolean findGWKInfo()
    {
    	boolean blnRet=false;
    	try
    	{
    		blnRet = findGWKInfo(gwk.code, getID(this.textEx_gklb.getText()), getID(this.combo_zjtype.getText()), this.text_zjno.getText());
    		if(blnRet)
    		{
    			checkLoadBirthValue();
    			//调用中航信接口
    			isInvokeZhxApi=false;
    			invokeZhxApi(false);
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return blnRet;
    }
    //
    private boolean findGWKInfo(String cardNo, String custType, String ZJLB, String passport)
    {
    	try
    	{
    		/*if (!checkInputValue(cardNo))
    		{
    			this.showMsg("购物卡号不能为空!");
    			return false;
    		}    */		
    		/*if (!checkInputValue(custType))
    		{
    			this.textEx_gklb.setFocus();
    			this.textEx_gklb.selectAll();
    			this.showMsg("顾客类别为空或数据不规范,请重新选择!");
    			return false;
    		}  */  		
    		if (!checkInputValue(ZJLB))
    		{
    			this.combo_zjtype.setFocus();
    			//this.combo_zjtype.selectAll();
    			this.showMsg("证件类别为空或数据不规范,请重新选择!");
    			return false;
    		}
    		if (!checkInputValue(passport))
    		{
    			this.text_zjno.setFocus();
    			this.text_zjno.selectAll();
    			this.showMsg("请输入证件号!");
    			return false;    			
    		}

			//当是身份证时，则验证身份证长度必须为18位 for 熊慧芹 2013.08.26
			if (this.getID(this.combo_zjtype.getText()).equalsIgnoreCase(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
			{
				if (this.text_zjno.getText().trim().length()!=18)
				{
					this.text_zjno.setFocus();
	    			this.text_zjno.selectAll();
	    			this.showMsg("身份证号输入不合法：必须为18位!");
					return false;
				}
				
				//检查身份证上的生日是否合法
				if (!checkSfzBirth()) return false;
			}
			
    		gwk.zjlb=this.getID(this.combo_zjtype.getText());
    		gwk.passport=this.text_zjno.getText();
    		gwk.gklb = this.getID(this.textEx_gklb.getText());
    		if (!checkJGJT_LJ(gwk.gklb)) return false;
    		ProgressBox pb = null;
    		try
    		{
    			pb = new ProgressBox();
    	        pb.setText("正在查询卡信息,请等待...");
    	        Zmsy_DataService dataservice= (Zmsy_DataService)DataService.getDefault();
    	        if (!dataservice.findGwkInfo(gwk))
    	        {
    	        	this.showMsg("查询卡信息失败!");
    	        	return false;
    	        }
    	        else
    	        {
    	        	loadGWKInfo();
    	        	return true;
    	        }
    		}
    		catch(Exception ex)
    		{
    			this.writeLog(ex);
    		}
    		finally
    		{

       		 	if (pb != null)
                {
                    pb.close();
                    pb = null;
                }
    		}
    		
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
    }
    
    //当生日里的月份(1-12)和天(1-31)不合法时,不允许通过 for 小贺 by 2013.9.18
    private boolean checkSfzBirth()
    {
    	try
    	{
    		StringBuffer bf;
        	bf = new StringBuffer();
    		bf.append(this.text_zjno.getText().trim().substring(6, 6+8));
    		bf.insert(6, "-").insert(4, "-");
    		int month = ManipulateDateTime.getMonth(bf.toString());
    		int day = ManipulateDateTime.getDay(bf.toString());
    		if (month<1 || month>12)
    		{
    			this.showMsg("身份证号码上的生日(月份)不合法:" + bf.toString());
    			return false;
    		}
    		if (day<1 || day>31)
    		{
    			this.showMsg("身份证号码上的生日(天)不合法:" + bf.toString());
    			return false;
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    	return true;
		
    }
    //
    private void loadGWKInfo()
    {
    	try
    	{
    		//刷完卡号，重新赋值到界面
    		this.loadZJTypeValue(gwk.zjlb);
    		this.loadZJNoValue(gwk.passport);
    		this.loadLjDateTimeValue(gwk.ljrq, gwk.ljsj);
    		this.loadCustTypeValue(gwk.gklb);
    		this.loadFlightValue(gwk.ljhb);
    		this.LoadThplaceValue(gwk.thdd);
            loadCardNameValue(gwk.name);
            loadSexValue(gwk.gender);
            loadBirthValue(gwk.birth);
            loadAgeValue(gwk.age);
            loadFZJG(gwk.fzjg);
            loadNationalValue(gwk.nation);
            loadPhoneNum(gwk.mobile);
            this.loadZKLValue(gwk.zkl);
            this.loadMailValue(gwk.email);
            this.loadIsDx(gwk.isdx);
    		
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
    
    //
    private void checkLoadBirthValue()
    {
    	try
    	{
    		if (getID(this.combo_zjtype.getText()).equals(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
    		{
    			//若是身份证,则计算出生日
    			StringBuffer bf;
    			if (this.text_zjno.getText().trim().length()==15)
    			{
    				bf = new StringBuffer();
    				bf.append("19" + this.text_zjno.getText().trim().substring(6, 6+6));
    				bf.insert(6, "-").insert(4, "-");
    				loadBirthValue(bf.toString());
    			}
    			else if (this.text_zjno.getText().trim().length()==18)
    			{
    				bf = new StringBuffer();
    				bf.append(this.text_zjno.getText().trim().substring(6, 6+8));
    				bf.insert(6, "-").insert(4, "-");
    				loadBirthValue(bf.toString());
    			}
    			getAge();
    		}
    	}
    	catch(Exception ex)
    	{
    		this.writeLog(ex);
    	}
    }
            
    //
   private int getAge()
   {
	   int age=0;
   	try
   	{
   		int year = this.date_csrq.getYear();
   		int month = this.date_csrq.getMonth()+1;
   		int day = this.date_csrq.getDay();
   		
   		int month_Now = new ManipulateDateTime().getMonth();
   		int day_Now = new ManipulateDateTime().getDay();
   		age = new ManipulateDateTime().getYear()-year;
   		if (month_Now>month)
   		{
   			//age=age;
   		}
   		else if (month_Now==month)
   		{
   			if (day_Now>=day)
   			{
   				//age = age;
   			}
   			else
   			{
   				age = age-1;
   			}
   		}
   		else
   		{
   			age=age-1;
   		}
   		if (age<0) age=0;
   	}
   	catch(Exception ex)
   	{
   		this.writeLog(ex);
   	}
   	return age;
   }
   //
   private String getID(String value)
   {
	   return getID(value,'L');
   }
   //
   private String getID(String value, char chrDirect)
   {
	   try
	   {
		   if (checkInputValue(value))
		   {
			   if (chrDirect=='L')
			   {
				   return  value.split("-")[0].trim();
			   }
			   else if (chrDirect == 'R')
			   {
				   if (value.split("-").length>1)
				   {
					   return value.split("-")[1].trim();
				   }
			   }
		   }
	   }
	   catch(Exception ex)
	   {
		   this.writeLog(ex);
	   }
	   return "";
   }
   
   private void showMsg(String msg)
   {
	   new MessageBox(msg);
   }
    
    //拼音简写
    public static String getPY(String strText)
	{
		//name没有值，直接返回
		if (strText == null || strText.length() == 0) return strText;
		//
		StringBuffer str = new StringBuffer(strText.toLowerCase().trim());
		StringBuffer buffer = new StringBuffer();

		//是否存在
		boolean isExistChar = false;
		StringBuffer headName = new StringBuffer();
		for (int i = 0; i < str.length(); i++)
		{
			//判断是否汉字
			char nameChar = str.charAt(i);
			char[] tempChar = new char[] { nameChar };
			byte[] uniCode = new String(tempChar).getBytes();
			// 非汉字
			if (uniCode[0] < 128 && uniCode[0] > 0)
			{
				//System.out.println("非汉字 = " + uniCode[0]);
				buffer.append(tempChar);
				//结束一个nameChar判断
				headName.append(buffer.toString());
				buffer.setLength(0);
			}
			else
			{
				//System.out.println("汉字" + i + " = " + str.charAt(i));
				//strChineseCharList内的字符串数组
				isExistChar = false;
				for (int j = 0; j < strChineseCharList.length; j++)
				{
					if (strChineseCharList[j].indexOf(String.valueOf(nameChar))>0)
					{
						//若找到，则退出循环
						headName.append(strChineseCharList[j].charAt(0));
						isExistChar = true;
						break;
					}			
					
				}
				if (!isExistChar)
				{
					//若未找到，则原样返回
					headName.append(nameChar);
				}
				
				
			}
		}
		//this.writeLog("getPY() strText_in=[" + strText + "],strText_out=[" + headName.toString() + "].");
		return headName.toString();
	}
    
    private static final String[] strChineseCharList  = {"A阿啊锕嗄厑哎哀唉埃挨溾锿鎄啀捱皑凒溰嘊敳皚癌毐昹嗳矮蔼躷噯藹譪霭靄艾伌爱砹硋隘嗌塧嫒愛碍暧瑷僾壒嬡懓薆曖璦鴱皧瞹馤鑀鱫安侒峖桉氨庵谙萻腤鹌蓭誝鞌鞍盦馣鮟盫韽啽雸垵", "A俺唵埯铵隌揞罯銨犴岸按荌案胺豻堓婩暗貋儑錌黯肮岇昂昻枊盎醠凹坳垇柪軪爊敖厫隞嗷嗸嶅廒滶獒獓遨熬璈蔜翱聱螯翶謷翺鳌鏖鰲鷔鼇芺袄媪镺襖岙扷岰傲奡奥嫯慠骜奧嶴澳懊擙謸鏊", "B八仈巴叭扒朳玐吧夿岜芭疤哵捌笆粑紦羓蚆釟豝鲃魞叐犮抜坺妭拔茇炦癹胈釛菝詙跋軷魃把靶坝弝爸垻罢鲅鮁覇矲霸壩灞欛挀掰白百佰柏栢捭竡粨摆擺襬呗拝败拜唄敗稗粺鞁薭贁兡瓸扳", "B攽班般颁斑搬斒瘢螁癍辬阪坂岅昄板版瓪钣粄舨鈑蝂魬办半伴扮姅怑拌绊秚絆鉡靽辦瓣邦峀垹帮捠梆浜邫幇幚縍幫鞤绑綁榜膀玤蚌傍棒谤塝稖蒡蜯磅镑艕謗勹包佨孢苞胞笣煲龅蕔褒闁齙", "B窇嫑雹宝怉饱保鸨珤堡堢媬葆寚飹飽褓駂鳵緥鴇賲藵寳寶靌勽报抱豹趵菢鲍靤骲暴髱虣儤曓爆忁鑤萡陂卑杯盃桮悲揹碑鹎藣鵯喺北鉳贝狈貝邶备昁牬苝背钡俻倍悖狽被偝偹梖珼鄁備僃惫", "B焙軰辈愂碚禙蓓蛽犕褙誖骳輩鋇憊糒鞴鐾奔泍贲倴渀逩犇锛錛本苯奙畚楍坌捹桳笨撪輽伻崩绷閍嵭嘣綳繃甭埲菶琫鞛泵迸塴甏镚蹦鏰屄毴逼豍鲾鵖鰏柲荸鼻嬶匕比夶朼佊吡妣沘疕彼柀秕", 
    	                                                 "B俾笔粊舭筆鄙聛貏匂币必毕闭佖坒庇诐邲妼怭畀畁哔毖珌疪荜陛毙狴畢袐铋婢庳敝梐萆萞閇閉堛弻弼愊愎湢皕禆筚詖貱赑嗶彃楅滗滭煏痹腷蓖蓽蜌裨跸閟飶幣弊熚獙碧箅綼蔽鄪馝幤潷獘", "B罼襅駜髲壁嬖廦篦篳縪薜觱避鮅斃濞臂蹕髀奰璧鄨饆繴襞襣鏎鞸韠躃躄魓贔鐴驆鷝鷩鼊边砭笾编煸甂箯編蝙獱邉鍽鳊邊鞭鯾鯿籩炞贬扁窆匾惼碥稨褊糄鴘藊卞弁忭抃汳汴苄釆峅便变変昪", "B覍揙缏遍辡艑辧辨辩辫辮變彪标飑髟猋脿墂幖滮骠標熛膘瘭镖飙飚儦颷瀌爂臕贆鏢镳飆飇飈飊鑣表婊裱諘褾錶檦俵鳔鰾憋鳖鱉鼈虌龞別别咇莂蛂徶襒蟞蹩瘪癟彆邠宾彬傧斌椕滨缤槟瑸賓", "B賔镔儐濒濱濵虨豳瀕霦繽蠙鑌顮氞摈殡膑髩擯鬂殯臏髌鬓髕鬢仌氷冰兵栟掤梹鋲檳丙邴陃怲抦秉苪昺柄炳饼窉蛃棅禀鈵鞆餅餠燷并並併幷垪庰倂栤病竝偋傡寎摒誁鮩靐癶拨波玻剥盋袯钵", "B饽啵紴缽脖菠鉢僠嶓播餑蹳驋鱍仢伯孛驳帛泊狛瓝侼勃胉郣亳挬浡秡钹铂舶博渤葧鹁愽搏鈸鉑馎鲌僰煿牔箔膊艊馛駁踣鋍镈薄駮鮊懪礡簙鎛餺鵓犦欂襮礴鑮跛箥簸孹擘檗糪譒蘗蔔峬庯逋", "B钸晡鈽誧餔轐醭卜卟补哺捕補鳪鸔不布佈步咘怖歨歩钚勏埗悑部埠瓿廍蔀踄篰餢簿玢佛夯宀疒瀑", 
    	                                                 "C嚓擦礤礸遪猜才材财財戝裁采倸埰婇寀彩採睬跴綵踩菜棌蔡縩乲参飡骖湌嬠餐驂残蚕惭殘慚蝅慙蠶蠺惨慘噆憯黪黲灿粲儏澯薒燦璨爘仓仺伧沧苍倉舱傖凔嵢滄獊蒼濸艙螥罉藏欌鑶賶撡操", "C糙曺曹嘈嶆漕蓸槽褿艚螬鏪艹艸草愺騲肏襙册侧厕恻测荝敇萗惻測策萴筞蓛墄箣憡嵾膥岑梣涔噌层層竲驓蹭硛硳岾猠乽叉芆杈肞臿訍偛嗏插馇銟锸艖疀鍤餷垞查査茬茶嵖搽猹靫槎察碴檫", "C衩镲鑔奼汊岔侘诧姹差紁拆钗釵犲侪柴祡豺喍儕虿袃瘥蠆囆辿觇梴掺搀鋓幨婵谗孱棎湹禅馋嬋煘缠獑蝉誗鋋廛潹潺緾磛毚鄽镡瀍儳劖蟾酁嚵壥巉瀺纏纒躔镵艬讒鑱饞产刬旵丳浐剗谄產産", "C铲阐蒇剷嵼滻幝蕆諂閳簅冁繟醦鏟闡囅灛讇忏硟摲懴颤懺羼韂伥昌娼淐猖菖阊晿椙琩裮锠錩鲳鯧鼚兏肠苌尝偿常徜瓺萇甞腸嘗嫦瑺膓鋿償嚐蟐鲿鏛鱨厂场昶惝敞僘厰廠氅鋹怅玚畅倡鬯唱", "C悵暢畼誯韔抄弨怊欩钞焯超鈔繛牊晁巢巣朝鄛漅嘲潮窲罺轈吵炒眧煼麨巐仦耖觘车車砗唓莗硨蛼扯偖撦屮彻坼迠烢聅掣硩頙徹撤澈勶瞮爡抻郴棽琛嗔諃賝尘臣忱沉辰陈茞宸烥莐敐晨訦谌", "C揨煁蔯塵樄瘎霃螴諶薼麎曟鷐趻硶碜墋夦磣踸贂闯衬疢称龀趁榇稱齓齔嚫谶襯讖阷泟柽爯棦浾偁蛏铛牚琤赪憆摚靗撐撑緽橕瞠赬頳檉竀穪蟶鏳鏿饓丞成呈承枨诚郕城宬峸洆荿乘埕挰珹掁", 
    	                                                 "C窚脭铖堘惩棖椉程筬絾裎塍塖溗碀誠畻酲鋮澂澄橙檙鯎瀓懲騬悜逞骋庱睈騁秤吃妛杘侙哧彨蚩鸱瓻眵笞訵嗤媸摛痴瞝螭鴟鵄癡魑齝攡麶彲黐弛池驰迟岻茌持竾荎淔筂貾遅馳墀踟篪謘尺叺", "C呎肔侈卶齿垑胣恥耻蚇豉欼歯裭鉹褫齒彳叱斥灻赤饬抶勅恜炽翄翅敕烾痓啻湁傺痸腟鉓雴憏翤遫慗瘛翨熾懘趩饎鶒鷘充冲忡沖茺浺珫翀舂嘃摏憃憧衝罿艟蹖虫崇崈隀宠铳銃抽瘳篘犨犫仇", "C俦帱栦惆绸菗畴絒愁皗稠筹酧酬踌雔嬦懤燽雠疇躊讎讐丑丒吜杽侴瞅醜矁魗臭遚殠出岀初摴樗貙齣刍除厨滁蒢豠锄榋耡蒭蜍趎雏犓廚篨橱懨幮櫉蟵躇櫥蹰鶵躕杵础椘储楮禇楚褚濋儲檚璴", "C礎齭齼亍処处竌怵拀绌豖竐珿絀傗琡鄐搐触踀閦儊憷橻斶歜臅黜觸矗搋膗揣啜嘬踹巛川氚穿剶瑏传舡船圌遄椽歂暷輲舛荈喘僢汌串玔钏釧賗刅囱疮窓窗牎摐牕瘡窻床牀噇傸磢闖创怆刱剏", "C剙愴吹炊龡垂桘陲捶菙棰槌锤錘顀旾杶春萅堾媋暙椿槆瑃箺蝽橁櫄鰆鶞纯陙唇浱莼淳脣犉滣蒓鹑漘醇醕鯙偆萶惷睶賰踳蠢踔戳辵娖惙涰绰逴辍酫綽輟龊擉磭歠嚽齪鑡呲玼疵趀偨词珁垐柌", "C祠茈茨瓷詞辝慈甆辞磁雌鹚糍辤飺餈嬨濨鴜礠辭鶿鷀此佌皉朿次佽刺刾庛茦栨莿絘蛓赐螆賜嗭从匆囪苁忩枞茐怱悤焧葱漗聡蔥骢暰樬瑽璁聦聪瞛篵聰蟌繱鏦騘驄丛従婃孮徖悰淙琮慒誴賨", 
    	                                                 "C賩樷藂叢灇欉爜謥凑湊楱腠辏輳粗觕麁麄麤徂殂促猝媨瘄蔟誎趗憱醋瘯簇縬蹙鼀蹴蹵顣汆撺镩蹿攛躥鑹攅櫕巑窜熶篡殩簒竄爨崔催凗墔慛摧榱獕磪鏙乼漼璀皠忰疩翆脃脆啐啛悴淬萃毳焠", "C瘁粹翠膵膬竁襊臎邨村皴澊竴存刌忖寸籿搓瑳遳磋撮蹉醝髊虘嵯嵳痤矬蒫鹾鹺齹脞剉剒厝夎挫莝莡措逪棤锉蓌错銼刂刹畜曾膪澶骣粢", "D詫襜燀譂奲虰坘蚳赿跮揰裯儔幬篅搥錞踧吋咑哒耷畣搭嗒褡噠墶达妲怛垯炟羍荙匒笪答詚跶瘩靼薘鞑燵繨蟽躂鐽龖龘打大亣眔橽呆獃懛歹傣代汏轪侢垈岱帒甙绐迨带待怠柋殆玳贷帯軑埭", "D帶紿袋逮軩瑇叇曃緿鮘鴏戴艜黛簤瀻霴襶靆丹妉单担眈砃耼耽郸聃躭媅殚瘅匰箪褝鄲頕儋勯殫襌簞聸刐狚玬瓭胆衴疸紞掸馾澸黕膽旦但帎沊泹诞柦疍啖啗弹惮淡萏蛋啿氮腅蜑觛窞誕噉髧", "D憚憺澹禫駳鴠甔癚嚪贉霮饏当珰裆筜當儅噹澢璫襠簹艡蟷挡党谠擋譡灙讜氹凼圵宕砀垱荡档菪瓽雼碭瞊趤壋檔璗盪礑刀叨屶忉氘舠釖鱽魛捯导岛陦倒島捣祷禂搗隝嶋嶌槝導隯壔嶹擣蹈禱", "D到悼盗菿椡盜道稲噵稻衜檤衟翿軇瓙纛恴得淂悳惪锝嘚徳德鍀的揼扥扽灯登豋噔嬁燈璒竳簦艠覴蹬等戥邓凳鄧隥墱嶝瞪磴镫櫈鐙仾低彽袛啲埞羝隄堤趆嘀滴镝磾鞮鏑廸狄肑籴苖迪唙敌涤", 
    	                                                 "D荻梑笛觌靮滌嫡蔋蔐頔魡敵嚁藡豴糴鸐氐厎诋邸阺呧坻底弤抵柢砥掋菧軧聜骶鯳地弟旳杕玓怟枤俤帝埊娣递逓偙梊焍眱祶第菂谛釱棣睇缔蒂僀禘腣鉪馰墑墬碲蔕慸甋締嶳螮嗲敁掂傎厧嵮", "D滇槙瘨颠蹎巅癫巓巔攧癲齻典奌点婰敟椣碘蒧蕇踮电佃甸阽坫店垫扂玷钿婝惦淀奠琔殿蜔電墊壂橂澱靛磹癜簟驔刁叼汈刟虭凋奝弴彫蛁琱貂碉殦瞗雕鮉鲷簓鼦鯛鵰屌弔伄吊钓窎訋掉釣铞", "D鈟竨銱雿瘹窵鋽鑃爹跌褺苵迭垤峌恎绖胅瓞眣耊戜谍堞幉揲畳絰耋詄叠殜牃牒镻嵽碟蜨褋艓蝶疂蹀鲽曡疉疊氎嚸丁仃叮帄玎甼疔盯钉耵酊靪顶頂鼎嵿薡鐤订饤矴定訂飣啶萣椗腚碇锭碠聢", "D錠磸顁丟丢铥颩銩东冬咚岽東苳昸氡倲鸫埬娻崬涷笗菄氭蝀鮗鼕鯟鶇鶫董嬞懂箽蕫諌动冻侗垌姛峒恫挏栋洞胨迵凍戙胴動崠硐棟腖働駧霘剅唗都兜兠蔸橷篼艔斗乧阧抖陡蚪鈄豆郖浢荳逗", "D饾鬥梪脰酘痘閗窦鬦餖斣闘竇鬪鬬鬭嘟督醏毒涜读渎椟牍犊裻読蝳獨錖凟匵嬻瀆櫝殰牘犢瓄皾騳黩讀豄贕韣髑鑟韇韥黷讟厾独笃堵帾琽赌睹覩賭篤芏妒杜肚妬度荰秺渡靯镀螙殬鍍蠧蠹偳", "D媏端鍴短段断塅缎葮椴煅瑖腶碫锻緞毈簖鍛斷躖籪叾垖堆塠嵟痽頧鴭鐜队对兑対祋怼陮碓綐對憝濧薱镦懟瀩譈鐓譵吨惇敦墩墪壿撴獤噸撉犜礅蹲蹾驐盹趸躉伅沌炖盾砘逇钝顿遁鈍頓碷遯", 
    	                                                 "D潡踲多咄哆剟崜毲裰嚉夺铎剫掇敓敚敪痥鈬奪凙踱鮵鐸朵朶哚垛挅挆埵缍椯趓躱躲綞亸鬌嚲刴剁沲陊饳垜尮桗堕舵惰跢跥跺飿嶞憜墯鵽卩亻赕铫町铤夂丶", "E娾砵妸妿娿屙讹囮迗俄娥峨峩涐莪珴訛皒睋鈋锇鹅蛾磀誐頟额額鵝鵞譌枙砈婀騀鵈厄歺戹阨呃扼苊阸呝砐轭咢垩峉匎恶砨蚅饿偔卾悪硆谔軛鄂阏堮崿愕湂萼豟軶遌遏廅搹琧腭僫蝁锷鹗蕚", "E遻頞颚餓噩擜覨諤餩鍔鳄歞顎櫮鰐鶚讍鑩齶鱷奀恩蒽峎摁鞥仒乻旕儿而侕陑峏洏荋栭胹袻鸸粫輀鲕隭髵鮞鴯轜尔耳迩洱饵栮毦珥铒餌駬薾邇趰二弍弐佴刵咡贰貮衈貳誀嗯唔诶", "F颰墢鼥韛朌頒報賁獖祊埄偪胇貶昞眪袚撥柭襏舩发沷発發彂髪橃醗乏伐姂垡疺罚阀栰傠筏瞂罰閥罸藅佱法砝鍅灋珐琺髮帆忛番勫噃墦嬏幡憣旙旛翻藩轓颿飜鱕凡凢凣匥杋柉矾籵钒舤烦舧", "F笲釩棥煩緐樊蕃橎燔璠薠繁襎繙羳蹯瀿礬蘩鐇蠜鷭反仮払辺返氾犯奿汎泛饭范贩畈訉軓梵盕笵販軬飯飰滼嬎範嬔瀪方邡坊芳枋牥钫淓蚄堏鈁錺鴋防妨房肪埅鲂魴仿访纺昉昘瓬眆倣旊紡舫", "F訪髣鶭放飞妃非飛啡婓婔渄绯菲扉猆靟裶緋蜚霏鲱餥馡騑騛鯡飝肥淝暃腓蜰蟦匪诽奜悱斐棐榧翡蕜誹篚吠废杮沸狒肺昲费俷剕厞疿屝萉廃費痱镄廢蕟曊癈鼣濷櫠鐨靅分吩帉纷芬昐氛竕紛", 
    	                                                 "F翂棻訜躮酚鈖雰朆餴饙坟妢岎汾枌炃肦梤羒蚠蚡棼焚蒶馚隫墳幩蕡魵鳻橨燌燓豮鼢羵鼖豶轒鐼馩黂粉瞓黺份坋弅奋忿秎偾愤粪僨憤奮膹糞鲼瀵鱝丰风仹凨凬妦沣沨凮枫封疯盽砜風峯峰偑", "F桻烽琒崶渢溄猦葑锋楓犎蜂瘋碸僼篈鄷鋒檒豐鎽鏠酆寷灃靊飌麷冯捀逢堸綘缝艂縫讽覂唪諷凤奉甮俸湗焨煈赗鳯鳳鴌賵蘕瓰覅仏坲梻紑缶否缹缻雬鴀夫伕邞呋姇枎玞肤怤柎砆荂衭娐荴旉", "F紨趺酜麸稃跗鈇筟綒孵敷麩糐麬麱懯乀弗伏凫甶冹刜孚扶芙芣芾咈岪帗彿怫拂服泭绂绋苻茀俘垘枹柫氟洑炥玸畉畐祓罘茯郛韨鳬哹栿浮畗砩莩蚨匐桴涪烰琈符笰紱紼翇艴菔虙袱幅絥罦葍", "F福粰綍艀蜉辐鉘鉜颫鳧榑稪箙複韍幞澓蝠髴鴔諨踾輻鮄癁鮲黻鵩鶝抚甫府弣拊斧俌郙俯釜釡捬辅椨焤盙腑滏腐輔簠黼父讣付妇负附咐坿竎阜驸复祔訃負赴蚥袝偩冨副婏婦蚹傅媍富復萯蛗", "F覄詂赋椱缚腹鲋禣褔赙緮蕧蝜蝮賦駙縛輹鮒賻鍑鍢鳆覆馥鰒猤攵犭", "G玵閞鳺旮伽嘠钆尜嘎噶錷尕玍尬魀该陔垓姟峐荄晐赅畡祴該豥賅賌忋改絠鎅丐乢匃匄杚钙盖溉葢鈣戤概蓋槩槪漑瓂干甘芉迀杆玕肝坩泔苷柑竿疳酐粓亁凲尲尴筸漧尶尷魐皯秆衦赶敢笴稈", 
    	                                                 "G感澉趕橄擀簳鳡鱤旰盰矸绀倝凎淦紺詌骭幹檊赣灨冈罓冮刚岗纲肛岡牨疘缸钢剛罡堈掆釭棡犅堽綱罁鋼鎠崗港杠焵筻槓戆皋羔高皐髙槔睾膏槹橰篙糕餻櫜韟鷎鼛鷱夰杲菒稁搞缟槁獔稿镐", "G縞藁檺吿告勂诰郜峼祮祰锆筶禞誥鋯戈圪纥戓疙牱哥胳袼鸽割搁彁歌滒戨閤鴐鴚擱謌鴿鎶呄佮匌阁革敋格鬲愅臵葛隔嗝塥滆觡搿槅膈閣镉韐骼諽鮯櫊韚轕鞷騔鰪哿舸个各虼個硌铬箇獦给", "G根跟哏亘艮茛揯搄更刯庚畊浭耕掶菮椩焿絚赓鹒緪縆賡羹鶊郠哽埂峺挭绠耿莄梗綆鲠骾鯁亙堩啹喼嗰工弓公功攻杛供糼肱宫宮恭蚣躬龚匑塨幊觥躳匔碽髸觵龔巩汞拱拲栱珙輁鞏共贡貢慐", "G熕兝兣勾佝沟钩袧缑鈎緱褠篝簼鞲韝岣狗苟枸玽耇耉笱耈豿坸构诟购垢姤茩冓够夠訽媾彀搆遘雊煹觏撀覯購估咕姑孤沽泒柧轱唂唃罛鸪笟菇菰蛄蓇觚軱軲辜酤毂箍箛嫴篐橭鮕鴣轂鹘古汩", "G诂谷股峠牯骨罟羖逧钴傦啒脵蛊蛌尳愲硲詁馉榾鼓鼔嘏榖皷穀糓薣濲臌餶瀔盬瞽固故凅顾堌崓崮梏牿棝祻雇痼稒锢頋僱錮鲴鯝顧瓜刮苽胍鸹歄焻煱颪劀緺銽鴰騧冎叧呱剐剮啩寡卦坬诖挂", "G掛罣罫褂詿乖拐枴柺箉怪恠关观官冠覌倌棺蒄窤瘝癏観鳏觀鱞馆痯筦管輨舘錧館躀鳤毌贯泴悺惯掼涫悹祼慣摜遦樌盥罆鏆灌爟瓘礶鹳罐鑵鱹光灮侊炗炚炛咣垙姯茪桄烡珖胱僙輄銧黆欟广", 
    	                                                 "G広犷俇逛撗归圭妫龟规邽皈茥闺帰珪亀硅袿媯椝瑰郌摫閨鲑嬀槻槼璝瞡鬶瓌櫷宄轨庋佹匦诡陒垝癸軌鬼庪祪匭晷湀蛫觤詭厬簋蟡刽刿攰柜炅攱贵桂椢筀貴蓕跪瞆劊劌撌槶禬簂櫃鳜鱥衮惃", "G绲袞辊滚蓘滾蔉磙輥鲧鮌鯀棍棞睴璭謴呙埚郭啯崞聒鈛锅墎瘑嘓彉蝈鍋彍囯囶囻国圀國帼掴幗慖摑漍聝蔮虢馘果惈淉猓菓馃椁褁槨綶蜾裹餜鐹过桧咯莞呷", "H餲淲豩豰俿腄墮吪魤犿浲侅郂絯忓攼仠桿汵榦臯鎬暠犵挌蛤厷愩唝羾詬鶻淈鹄鈷縎鵠怘趏潅雚洸襘鞼緄腘膕粿輠過铪丷哈嗨孩骸海胲烸塰酼醢亥骇害氦嗐餀駭駴嚡饚乤兯佄顸哻蚶酣頇谽", "H憨馠魽鼾邗含邯函凾虷唅圅娢浛崡晗梒涵焓寒嵅韩甝筨爳蜬澏鋡韓厈罕浫喊蔊豃鬫汉屽扞汗闬旱垾悍捍晘涆猂莟晥焊琀菡釬閈皔睅傼蛿颔撖蜭暵銲鋎憾撼翰螒頷顄駻雗瀚鶾魧苀斻杭垳绗", "H笐航蚢颃貥筕絎頏沆蒿嚆薅竓蚝毫椃嗥獆噑豪嘷獋儫曍嚎壕濠籇蠔譹好郝号昊昦哠恏浩耗晧淏傐皓聕號暤暭澔皜皞皡薃皥颢灏顥鰝灝兞诃呵喝訶嗬蠚禾合何劾咊和姀河峆曷柇盇籺阂哬敆", "H核盉盍荷啝涸渮盒秴菏萂龁惒粭訸颌楁詥鉌阖鲄熆閡鹖麧澕篕翮魺闔齕覈鶡皬鑉龢佫垎贺寉焃湼賀煂碋褐赫鹤翯壑癋爀鶴齃靍靎鸖靏黒黑嘿潶嬒拫痕鞎佷很狠詪恨亨哼悙涥脝姮恆恒桁烆", 
    	                                                 "H珩胻鸻横橫衡鴴鵆蘅鑅堼囍乊乥叿灴轰哄訇烘軣焢硡薨輷嚝鍧轟仜弘妅红吰宏汯玒纮闳宖泓玜苰垬娂洪竑荭虹浤紘翃耾硔紭谹鸿渱竤粠葒葓鈜閎綋翝谼潂鉷鞃魟篊鋐彋蕻霐黉霟鴻黌晎嗊", "H讧訌撔澋澒銾侯矦喉帿猴葔瘊睺篌糇翭骺鍭餱鯸吼犼后郈厚垕後洉逅候鄇堠豞鲎鲘鮜鱟乎匢虍呼垀忽昒曶泘苸烀轷匫唿惚淴虖軤雽嘑寣滹雐歑囫抇弧狐瓳胡壶壷斛焀喖壺媩湖猢絗葫楜煳", "H瑚嘝蔛鹕槲箶糊蝴衚魱縠螜醐頶觳鍸餬瀫鬍鰗鶘鶦鶮乕汻虎浒唬萀琥虝箎錿鯱互弖戶户戸冱冴帍护沍沪岵怙戽昈枑祜笏粐婟扈瓠綔鄠嫭嫮摢滬蔰槴熩鳸簄鍙鹱護鳠韄頀鱯鸌花芲埖婲椛硴", "H糀誮錵蘤华哗姡骅铧滑猾嘩撶璍螖鏵驊鷨化划杹画话崋桦婳畫嬅畵話劃摦槬樺嫿澅諙諣黊繣蘳怀徊淮槐褢踝懐褱懷櫰耲蘹坏壊壞蘾欢歓鴅嚾懽獾歡貛讙驩还环峘洹荁桓萈萑堚寏絙雈羦貆", "H锾阛寰缳環豲鍰鹮糫繯轘闤鬟睆缓緩攌幻奂肒奐宦唤换浣涣烉患梙焕逭喚嵈愌換渙痪煥瑍豢漶瘓槵鲩擐澣瞣藧鯇鯶鰀巟肓荒衁塃慌皇偟凰隍黃黄喤堭媓崲徨惶湟葟遑楻煌瑝墴潢獚锽熿璜", "H篁艎蝗癀磺穔諻簧蟥鍠餭鳇趪鐄騜鰉鱑鷬怳恍炾宺晃晄奛谎幌愰詤縨謊櫎皩兤滉榥曂皝鎤灰灳诙咴恢拻挥洃虺晖烣珲豗婎媈揮翚辉隓暉楎琿禈詼幑睳噅噕翬輝麾徽隳瀈鰴囘回囬佪廻廽恛", 
    	                                                 "H洄茴迴烠逥痐蛔蛕蜖鮰悔螝毇檓燬譭卉屷汇会讳泋哕浍绘芔荟诲恚恵烩贿彗晦秽喙惠絵缋翙阓匯彙彚毀毁滙詯賄僡嘒蔧誨圚寭慧憓暳槥潓蕙徻橞獩璤薈薉諱頮檅檜燴篲藱餯嚖懳瞺穢繢蟪", "H櫘繪翽譓闠鐬靧譿顪昏昬荤婚涽阍惽棔睧睯閽忶浑馄渾魂繉鼲诨俒倱圂掍混焝溷慁觨諢吙耠锪劐鍃豁攉騞佸活秮火伙邩钬鈥夥沎或货咟俰捇眓获剨祸貨惑旤湱禍嗀奯濩獲霍檴謋穫镬嚯瀖", "H耯藿蠖嚿曤臛癨矐鑊夻行砉圜", "J皀髉畟筴簎笒覘樔伡俥鋤雛處諔堲蠀覿茤岋紇裓構颳夬叏獷臩臦昋鱖妎悎饸紅鵍丌讥击刉叽饥乩刏圾机玑肌芨矶鸡枅咭迹剞唧姬屐积笄飢基绩喞嵆嵇犄筓缉赍勣嗘畸跻鳮僟箕銈嘰撃槣樭", "J畿稽賫躸齑墼憿機激璣積錤隮磯簊績羁賷櫅耭雞譏韲鶏譤癪躋鞿鷄齎羇虀鑇覉鑙齏羈鸄覊亼及伋吉岌彶忣汲级即极亟佶郆卽叝姞急狤皍笈級揤疾觙偮卙庴脨谻戢棘極殛湒集塉嫉愱楫蒺蝍", "J趌辑槉耤膌銡嶯潗瘠箿蕀蕺鞊鹡橶檝螏輯襋蹐鍓艥籍轚鏶霵鶺鷑雦雧几己丮妀犱泲虮挤脊掎鱾幾戟嵴麂魢撠擠穖彑旡计记伎纪坖妓忌技芰芶际剂季哜垍峜既洎济紀茍計剤紒继觊記偈寂寄", 
    	                                                 "J徛悸旣梞祭惎臮葪兾痵継蓟裚跡際墍暨漃漈禝稩穊誋跽霁鲚暩稷諅鲫冀劑曁穄薊襀髻檕繋罽覬鮆檵蹟鵋齌廭懻癠糭蘎骥鯚瀱繼蘮鱀蘻霽鰶鰿鱭驥加夹抸佳泇迦枷毠浃珈家痂梜笳耞袈猳葭", "J跏犌腵鉫嘉镓糘豭貑鎵麚圿扴岬郏荚郟恝莢戛铗戞蛱颊蛺跲餄鋏頬頰鴶鵊甲玾胛斚贾钾婽斝椵賈鉀榎槚瘕檟价驾架假嫁幏榢稼駕嗧戋奸尖幵坚歼间冿戔肩艰姦姧兼监堅惤猏笺菅菺湔牋犍", "J缄葌葏間搛椷煎瑊睷缣蒹箋樫熞緘蕑蕳鲣鹣熸篯縑鋻艱鞬馢麉瀐鞯殱礛覸鵳瀸殲籛韀鰹囏虃韉囝拣枧俭柬茧倹挸捡笕减剪检湕趼揀検減睑硷裥詃锏弿瑐简絸谫彅戩戬碱儉翦檢藆襇襉謇蹇", "J瞼簡繭謭鬋鰎鹸瀽蠒鐗鐧鹻譾襺鹼见件侟建饯剑洊牮荐贱俴健剣涧珔舰剱徤渐谏釼寋旔楗毽溅腱臶践賎鉴键僭榗劍劎墹澗箭糋諓賤趝踐踺劒劔橺薦鍵餞瞷磵礀螹鍳擶繝覵艦轞鑑鑒鑬鑳江", "J姜将茳浆畕豇葁翞僵漿螀壃缰薑橿殭螿鳉疅礓疆繮韁鱂讲奖桨傋蒋奨奬蔣槳獎耩膙講顜匞匠夅弜杢降洚绛弶袶絳畺酱摾滰嵹犟糡醤糨醬櫤謽艽芁交郊姣娇峧浇茭骄胶椒焦蛟跤僬嘄虠鲛嬌", "J嶕嶣憍膠蕉膲礁穚鮫鹪簥蟭鐎鷦鷮櫵臫角佼挢狡绞饺晈笅皎矫脚铰搅筊剿勦敫煍腳賋摷暞踋鉸餃儌劋撹徼敽敿缴曒璬矯皦鵤孂纐攪灚鱎叫呌挍訆珓轿较敎教窖滘嘂嘦斠漖酵噍嬓獥藠趭轎", 
    	                                                 "J醮譥皭釂阶疖皆接掲痎秸階喈嗟堦媘揭脻街煯稭鞂蝔擑癤鶛孑尐节讦刦刧劫岊昅刼劼杰衱诘拮洁结迼桀桝莭訐婕崨捷袺傑結颉嵥楶滐睫節蜐詰鉣魝截榤碣竭蓵鲒潔羯誱踕幯嶻擮礍鍻巀櫭", "J蠞蠘蠽姐毑媎解飷檞丯介岕庎忦戒芥屆届斺玠界畍疥砎衸诫借蚧徣堺楐琾蛶骱犗誡褯魪藉巾今斤钅兓金釒津矜砛衿觔珒紟惍琎堻琻筋璡鹶黅襟仅卺巹紧堇菫僅谨锦嫤廑漌盡緊蓳馑槿瑾錦", "J謹饉劤尽劲妗近进侭枃勁浕荩晉晋浸烬赆祲進煡缙寖搢溍禁靳瑨僸凚殣觐儘噤縉賮嚍壗嬧濜藎燼璶覲贐齽坕京泾经茎亰秔荆荊婛惊旌旍猄経菁晶稉腈睛粳經兢精聙橸鲸鵛鯨鶁麖鼱驚麠井", "J丼阱刭宑汫汬肼剄穽颈景儆幜憬璄憼暻燝璟璥頸蟼警妌净弪径迳浄胫凈弳徑痉竞逕婙婧桱梷淨竟竫敬痙傹靖境獍誩静頚曔镜靜瀞鏡競竸冋坰扃埛絅駉駫蘏冏囧迥侰炯逈浻烱煚窘颎綗僒煛", "J熲澃燛褧蘔丩勼纠朻究糺鸠糾赳阄萛啾揪揫鬏鬮九久乆乣奺汣杦灸玖舏韭紤酒镹韮匛旧臼咎疚柩柾倃桕厩救就廄匓舅僦廏廐慦殧舊鹫鯦麔匶齨鷲欍凥抅匊居拘泃狙苴驹倶挶疽痀罝陱娵婅", "J婮崌掬梮涺椐琚腒锔裾雎艍蜛諊踘鋦駒鴡鞠鞫鶋局泦侷狊桔毩淗焗菊郹椈毱湨犑輂粷躹閰橘檋駶鵙蹫鵴巈蘜鶪驧咀沮举矩莒挙椇筥榉榘蒟龃聥舉踽擧櫸欅襷句巨讵姖岠怇拒洰苣邭具拠昛", 
    	                                                 "J歫炬秬钜俱倨冣剧粔耟蚷袓埧埾惧据詎距焣犋鉅飓虡豦锯愳窭聚駏劇勮屦踞鮔壉懅據澽遽鋸屨颶簴躆醵懼爠姢娟捐涓裐鹃勬鋑镌鎸鵑鐫蠲卷呟帣埍菤锩臇錈奆劵倦勌桊狷绢隽淃瓹眷鄄睊", "J絭罥睠慻蔨餋羂噘撅撧屩屫亅孒孓决刔氒诀抉芵玦玨挗珏砄绝虳觉倔欮崛掘斍桷殌覐觖訣赽趹厥絕絶覚趉鈌劂瑴谲嶡嶥憰熦爴獗瘚蕝蕨鴂鴃憠橛橜镼爵臄镢蟨蟩爑譎蹶蹷嚼矍覺鐝灍爝觼", "J彏戄攫玃鷢欔矡龣貜钁军君均汮袀軍钧莙蚐桾皲菌鈞碅筠皸皹覠銁銞鲪麇鍕鮶呁俊郡陖埈峻捃晙浚馂骏珺畯竣箟蜠儁寯懏餕燇駿鵔鵕鵘纟挟廴", "K錒嵦濭骯奟喫噄鉺朏胐阬槀稾藳溝絓鰥卝丱硄廣胿膭歸楇鉿妔薧蚵毼袔齁恗搰華磆蕐會澮璯餛秳漷監譼槛檻捁撟悈脛踁駃咔咖喀卡佧垰胩裃鉲开奒揩衉锎鐦凯剀垲恺闿铠凱剴慨蒈塏愷楷", "K輆暟锴鍇鎧闓颽忾炌欬烗勓嘅鎎乫刊栞勘龛堪戡龕冚坎侃砍莰偘惂塪輡竷轗看衎崁墈阚瞰磡矙忼砊粇康嫝嵻慷漮槺穅糠躿鏮鱇扛摃亢伉匟邟囥抗犺闶炕钪鈧閌尻髛攷考拷洘栲烤铐犒銬鲓", "K靠鮳鯌匼坷苛柯牁珂科胢轲疴趷钶嵙棵痾萪軻颏搕犐稞窠鈳榼薖颗樖瞌磕蝌頦醘顆髁礚壳咳翗嶱可岢炣渇嵑敤渴克刻剋勀勊客峇恪娔尅课堁氪骒缂嗑愙溘锞碦緙課錁礊騍肎肯肻垦恳啃豤", 
    	                                                 "K墾錹懇掯裉褃劥吭坑硁牼铿硻誙銵鍞鏗巪乬唟厼怾空倥埪崆悾硿箜躻錓鵼孔恐控鞚廤抠芤眍剾彄瞘口劶叩扣怐敂冦宼寇釦窛筘滱蔲蔻瞉簆鷇刳郀枯哭桍堀崫圐跍窟骷鮬狜苦楛库俈绔庫秙", "K焅袴喾絝裤瘔酷褲嚳夸姱舿侉咵垮銙挎胯跨骻蒯擓巜凷圦块快侩郐哙狯脍塊筷鲙儈鄶廥獪膾旝糩鱠宽寛寬髋鑧髖梡款窽窾匡劻诓邼匩哐恇洭筐筺誆軭狂狅诳軖軠誑鵟夼儣懭邝圹纩况旷岲", "K況矿昿贶框眖砿眶絋絖貺軦鉱鋛鄺壙黋懬曠爌矌礦穬纊鑛亏刲岿悝盔窥聧窺虧闚顝蘬奎晆逵鄈頄馗喹揆葵骙戣暌楏楑魁睽蝰頯櫆藈鍨鍷騤夔蘷虁躨卼傀煃跬頍蹞尯匮欳喟媿愦愧溃蒉馈匱", "K嘳嬇憒篑聩聭蕢樻殨餽簣聵籄鐀鑎坤昆晜堃堒婫崐崑猑菎裈焜琨髠裩锟髡鹍尡潉蜫褌髨熴瑻醌錕鲲臗騉鯤鵾鶤悃捆阃壸梱祵硱稇裍壼稛綑閫閸困涃睏扩拡括挄栝桰筈萿葀蛞阔廓頢濶闊鞟", "K韕懖霩鞹鬠穒", "L冫勑粚誺銐寵娕坔釘詻鄜膚鬴茖蛒鎘鉻羮篢睔腂蘫谾瘣暕撿諫倞靓靚牞摎樛畂寠窶垃拉柆啦翋菈邋旯砬揦磖喇藞嚹剌溂腊揧楋瘌蜡蝋辢辣蝲臈攋爉臘鬎櫴瓎镴鯻蠟鑞鞡来來俫倈崃徕涞莱", 
    	                                                 "L郲婡崍庲徠梾淶猍萊逨棶琜筙铼箂錸騋鯠鶆麳唻赉睐睞赖賚濑賴頼顂癞鵣瀨瀬籁藾癩襰籟兰岚拦栏婪嵐葻阑蓝谰澜褴儖斓篮燣藍襕镧闌璼襤譋幱攔瀾灆籃繿蘭斕欄礷襴囒灡籣欗讕躝襽鑭", "L钄韊览浨揽缆榄漤罱醂壈懒覧擥嬾懶孄覽孏攬欖爦纜烂滥燗嚂濫爁爛爤瓓灠糷啷勆郎郞欴狼莨嫏廊桹琅蓈榔瑯硠稂锒筤艆蜋郒螂躴鋃鎯駺悢朗阆朖烺塱蓢樃誏閬朤埌浪蒗唥捞粩撈劳労牢", "L狫窂哰唠崂浶勞痨铹僗嶗憥朥癆磱簩蟧醪鐒顟髝老佬咾姥恅荖栳珯硓铑蛯銠潦橑鮱轑涝烙嗠耢酪嫪憦澇橯耮軂仂阞乐叻忇扐氻艻玏泐竻砳勒楽韷簕鳓鰳饹餎雷嫘缧蔂樏畾檑縲镭櫑瓃羸礧", "L纍罍蘲鐳轠壨鑘靁虆鱩欙纝鼺厽耒诔垒塁絫傫誄磊蕌磥蕾儡壘癗藟櫐矋礨灅蠝蘽讄儽鑸鸓肋泪洡类涙淚累酹銇頛頪擂錑攂礌颣類纇蘱禷嘞脷塄棱楞碐稜踜薐冷倰堎愣睖唎刕厘剓梨狸离荲", "L莉骊悡梸犁菞喱棃犂鹂剺漓睝筣缡艃蓠蜊嫠孷樆璃盠竰糎蔾褵鋫鲡黎篱縭罹錅蟍謧醨嚟藜邌離鯏鏫鯬鵹黧囄灕蘺蠡蠫孋廲劙鑗穲籬驪鱺鸝礼李里俚峛哩娌峲浬逦理裡锂粴裏豊鋰鲤澧禮鯉", "L蟸醴鳢邐鱧欚力历厉屴立吏朸丽利励呖坜沥苈例岦戾枥疠苙隶俐俪栃栎疬砅茘荔赲轹郦娳悧栗栛栵涖猁珕砺砾秝莅唳婯悷琍笠粒粝蚸蛎傈凓厤棙痢蛠詈跞雳塛慄搮溧蒚蒞鉝鳨厯厲暦歴瑮", 
    	                                                 "L綟蜧勵曆歷篥隷鴗巁檪濿癘磿隸鬁儮曞櫔爄犡禲蠇嚦壢攊瀝瓅礪藶櫪爏瓑皪盭礫糲蠣儷癧礰鷅麜囇轢欐讈轣攭瓥靂鱱靋瓈俩倆嫾奁连帘怜涟莲連梿联裢亷嗹廉慩漣蓮匲奩覝劆匳噒憐磏聨", "L聫褳鲢濂濓縺翴聮薕螊櫣聯臁蹥謰鎌镰簾蠊鬑鐮鰱籢籨敛琏脸裣摙槤璉蔹嬚斂歛臉鄻襝羷练娈炼恋浰殓堜媡湅萰链僆楝煉瑓潋練澰錬殮鍊鏈瀲鰊戀纞簗良俍凉梁涼椋辌粮粱墚綡踉樑輬糧", "L両两兩唡啢掚脼裲緉蜽魉魎亮哴谅辆喨晾湸量煷輌諒輛鍄蹽辽疗聊僚寥嵺廖憀膋嘹嫽寮嶚嶛憭撩敹獠缭遼暸燎璙窷膫竂镣鹩屪廫簝蟟豂賿蹘爎鐐髎飉鷯钌釕鄝蓼爒镽了尥炓料尞撂瞭咧毟", "L挘埓列劣冽劽姴峢挒洌茢迾埒浖烈烮捩猎猟蛚裂煭睙聗趔巤颲儠鮤鴷擸獵犣躐鬛鬣鱲邻林临啉崊惏淋晽琳粦痳碄箖粼鄰隣嶙潾獜遴斴暽燐璘辚霖瞵磷臨繗翷麐轔壣瀶鏻鳞驎麟鱗菻亃稟僯", "L凛凜撛廩廪懍懔澟檁檩癛癝顲吝恡悋赁焛賃蔺橉甐膦閵疄藺蹸躏躙躪轥拎伶刢灵囹坽夌姈岭岺彾泠狑苓昤朎柃玲瓴凌皊砱秢竛铃陵鸰婈崚掕棂淩琌笭紷绫羚翎聆舲菱蛉衑祾詅跉軨蓤裬鈴", "L閝零龄綾蔆輘霊駖澪蕶錂霗魿鲮鴒鹷燯霛霝齢瀮酃鯪孁蘦齡櫺醽靈欞爧麢龗阾袊领領嶺令另呤炩溜熘刘沠畄浏流留旈琉畱硫裗媹嵧旒蒥蓅遛馏骝榴瑠飗劉瑬瘤磂镏駠鹠橊璢疁镠癅蟉駵嚠", 
    	                                                 "L懰瀏藰鎏鎦餾麍鏐飀鐂騮飅鰡鶹驑柳栁桞珋桺绺锍綹熮罶鋶橮羀嬼六翏塯廇澑磟鹨蹓霤雡飂鬸鷚瓼甅囖龙屸咙泷茏昽栊珑胧眬砻笼聋隆湰嶐槞漋癃窿篭嚨巃巄蘢鏧霳曨朧櫳爖瓏矓礱礲襱", "L籠聾蠪蠬龓豅躘鑨靇鸗陇垄垅拢儱隴壟壠攏竉哢梇硦徿贚娄偻婁喽溇蒌僂楼嘍廔慺蔞遱樓熡耧蝼瞜耬艛螻謱軁髅鞻髏嵝搂塿嶁摟漊甊篓簍陋屚漏瘘镂瘺瘻鏤露噜撸嚕擼卢庐芦垆枦泸炉栌", "L胪轳舮鸬玈舻颅鈩鲈魲盧嚧壚廬攎瀘獹璷蘆櫨爐瓐臚矑籚纑罏艫蠦轤鑪顱髗鱸鸕黸卤虏挔捛掳鹵硵鲁虜塷滷蓾樐澛魯擄橹磠镥瀂櫓氌艣鏀艪鐪鑥圥甪陆侓坴彔录峍勎赂辂陸娽淕淥渌硉菉", "L逯鹿椂琭祿禄僇剹勠滤盝睩碌稑賂路塶廘摝漉箓粶蔍戮樚熝膔膟觮趢踛辘醁潞穋蕗錄録錴璐簏螰鴼濾簶蹗轆騄鹭簬簵鏕鯥鵦鵱麓鏴騼籙觻虂鷺氇驴闾榈閭馿膢櫚藘曥鷜驢吕呂侣郘侶旅梠", "L焒祣稆铝屡絽缕屢膂膐褛鋁履褸儢穞縷穭寽垏律哷虑率绿嵂氯葎綠緑慮箻勴繂櫖爈鑢孪峦挛栾鸾脔滦銮鵉圝奱孌孿巒攣曫欒灓羉臠圞灤虊鑾癴癵鸞卵乱釠亂掠略畧锊圙鋝鋢抡掄仑伦囵沦", "L纶侖轮倫陯圇婨崘崙惀淪菕棆腀碖綸蜦踚輪磮錀鯩稐耣论埨溣論捋頱囉罗啰猡脶萝逻椤腡锣箩骡镙螺羅覶鏍儸覼騾玀蘿邏欏鸁籮鑼饠驘剆倮砢蓏裸躶瘰蠃臝攞曪癳泺峈洛络荦骆珞笿絡落", 
    	                                                 "L摞漯犖雒駱鮥鵅纙鱳", "M絔硥苾牑訬仯哋尒尓爾坆呒撫羙秏狢貈貉湏惛殙貇氂犛厸龍龒嘸呣妈媽嬤嬷麻痲嫲蔴犘蟆蟇马犸玛码蚂馬溤獁遤瑪碼螞鎷鷌鰢亇杩祃閁骂唛傌睰嘜榪禡罵駡礣鬕吗嗎嘛埋霾买荬買嘪蕒鷶", "M劢迈佅売麦卖脉脈麥衇勱賣邁霡霢颟顢姏悗蛮慲摱馒樠瞒瞞鞔饅鳗鬗鬘鰻蠻屘満睌满滿螨襔蟎鏋矕曼僈谩墁幔慢漫獌缦蔄蔓熳澷镘縵蟃謾鏝蘰牤邙吂忙汒芒杗杧盲厖恾笀茫哤娏浝牻硭釯", "M铓痝蛖鋩駹蘉莽莾茻壾漭蟒蠎匁猫貓毛矛枆牦茅旄渵軞酕蛑锚緢髦蝥錨蟊鶜冇卯戼峁泖茆昴铆笷蓩鉚冃皃芼冐茂冒眊贸耄袤覒媢帽貿鄚愗暓楙毷瑁瞀貌鄮蝐懋唜庅嚒濹嚰么癦沒没枚玫苺", "M栂眉脄莓梅珻脢郿堳媒嵋湄湈猸睂葿楣楳煤瑂禖腜塺槑酶镅鹛鋂霉徾鎇矀攗蘪鶥攟黴毎每凂美挴浼媄嵄渼媺镁嬍燘躾鎂黣妹抺沬昧祙袂眛媚寐痗跊鬽煝睸魅篃蝞嚜椚门扪玣钔門閅捫菛璊", "M穈鍆虋闷焖悶暪燜懑懣们們掹擝氓甿虻冡庬罞萌萠夢溕盟甍儚橗瞢蕄蝱鄳鄸幪懞濛獴曚朦檬氋矇礞鯍艨鹲矒靀饛顭鸏勐猛瓾蒙锰艋蜢錳懵蠓鯭鼆孟梦夣懜霥踎咪瞇冞弥祢迷猕谜蒾詸謎醚", 
    	                                                 "M擟糜縻麊麋靡獼麛爢戂攠蘼醾醿鸍釄米羋芈侎弭洣敉粎脒眯渳葞蝆蔝銤孊灖糸汨宓泌觅峚祕宻秘密淧覓覔幂谧塓幎覛嘧榓滵漞熐蔤蜜鼏冪樒幦濗藌謐櫁簚羃芇眠婂绵媔棉綿緜臱蝒嬵檰櫋", "M矈矊矏丏汅免沔黾俛勉眄娩偭冕勔喕愐湎缅葂腼緬鮸靣面糆麪麫麺麵喵苗媌描瞄鹋嫹鶓鱙杪眇秒淼渺缈篎緲藐邈妙庙竗庿廟吀咩哶孭灭搣滅蔑薎鴓幭懱瀎篾櫗蠛衊鑖鱴瓱民垊姄岷怋旻旼", "M玟苠珉盿冧罠崏捪琘琝缗暋瑉痻碈鈱緍緡錉鍲皿冺刡闵抿泯勄敃闽悯敏笢笽湣閔愍敯黽閩僶慜憫潣簢鳘蠠鰵名明鸣洺眀茗冥朙眳铭鄍嫇溟猽蓂暝榠銘鳴瞑螟覭佲姳凕慏酩命掵詺谬缪繆謬", "M摸嚤尛谟嫫馍摹模膜麼麽摩魹橅磨糢謨謩擵饃嚩蘑髍魔劘饝抹懡麿末劰圽妺怽歿殁沫茉陌帞昩枺皌眜眿砞秣莈莫眽粖絈袹蛨貃嗼塻寞漠蓦貊銆墨嫼暯瘼瞐瞙镆魩黙縸默貘藦蟔鏌爅驀礳纆", "M耱乮哞牟侔劺恈洠眸谋鉾謀鍪鴾麰某母毪獏氁亩牡姆拇峔牳畆畒胟畝畞砪畮鉧踇木仫目凩沐狇坶炑牧苜毣莯蚞钼募萺雮墓幕幙慔楘睦鉬慕暮艒霂穆鞪旀丆椧渑", "N懝抝拗秅莀袲唸毭咹児兒耏聏峊廾嫨跈聻茮澆涳巙崀尦竜袮彌镾濔瀰乜樢拏拿誽镎鎿乸哪雫内那吶妠纳肭娜衲钠納袦捺笝豽軜貀嗱蒳靹魶腉熋孻乃奶艿氖疓妳廼迺倷釢嬭奈柰耐萘渿鼐褦", 
    	                                                 "N螚錼囡男抩枏枬侽南娚畘莮难喃遖暔楠煵諵難赧揇湳萳腩蝻戁婻囔乪嚢囊鬞馕欜饢擃曩攮灢儾齉孬檂呶怓挠峱硇铙猱蛲詉碙嶩夒鐃巎獿垴恼悩脑匘脳堖惱嫐瑙腦碯闹婥淖閙鬧讷呐眲訥呢", "N馁腇餒鮾鯘氝焾嫩能莻鈪銰啱妮尼坭怩泥籾倪屔秜郳铌埿婗淣猊蚭棿跜鈮蜺觬貎霓鲵鯢麑齯臡伱伲你拟抳狔苨柅旎晲馜儞隬擬薿檷鑈氼迡昵胒逆匿痆眤堄惄嫟愵溺睨腻暱縌膩嬺拈年秊哖", "N秥鲇鮎鲶鵇黏鯰捻辇撚撵碾輦簐攆躎卄廿念姩埝娘嬢酿醸釀鸟茑袅嫋裊蔦嬝褭嬲尿脲捏揑帇圼苶枿陧涅聂臬啮惗隉敜嗫嵲踂摰踗踙镊镍嶭篞臲錜颞蹑鎳闑孼孽櫱籋蘖齧巕糱糵蠥囓讘躡鑷", "N顳脌囜您拰宁咛拧狞柠聍寍寕寜寧儜凝嚀嬣擰獰薴檸聹鑏鬡鸋橣矃佞侫泞甯寗澝濘妞牛牜忸扭沑狃纽杻炄钮紐鈕靵农侬哝浓脓秾農儂辳噥憹濃蕽禯膿穠襛醲欁繷弄挊挵癑齈羺譨啂槈耨鎒", "N鐞譳奴孥驽笯駑伮努弩砮胬怒傉搙女钕籹釹衂恧朒衄疟虐瘧奻渜暖煗餪硸黁燶郍挪梛傩搻儺橠诺喏掿逽搦锘榒稬諾糑懦懧糥穤糯恁蔫", "O吽摳噢哦筽夞乯鞰讴欧殴瓯鸥塸歐熰甌膒鴎櫙藲鏂鷗吘呕偶腢耦蕅藕怄沤慪漚", 
    	                                                 "P钯鈀跁罷猈螌褩闆湴牓棓徬鎊剝襃铇袌鉋鮑琲絣痭琣逬跰螕鎞粃枈痺辟稫箆鞞猵萹拚徧緶辯骉蔈颮麃藨謤穮驃驫摽汃砏璸鉼碆磻犻苩瓟桲淿湐猼馞嚗髆蚾獛鵏吥荹鈈郶茷籓膰趽彷衯夆馮", "P摓垺妚尃豧巿襆襥脯蜅秿鈲窌攈脟濼尨眫冖覕屰妑皅趴舥啪葩杷爬耙琶筢潖帊帕怕袙拍俳徘排猅棑牌箄輫簰犤哌派湃蒎鎃磗眅畨潘攀爿柈盘跘媻幋蒰搫槃磐縏蹒瀊蟠蹣鎜鞶坢冸判沜泮炍", "P叛牉盼畔袢詊溿頖鋬鵥襻鑻乓汸沗肨胮雱滂膖霶厐庞逄旁舽嫎篣螃鳑龎龐鰟蠭嗙耪覫髈炐胖抛拋脬刨咆垉庖狍炮炰爮袍匏蚫軳鞄褜麅跑奅泡疱皰砲萢麭礟礮呸怌肧柸胚衃醅阫陪陫培毰赔", "P锫裴裵賠錇俖伂沛佩帔姵斾旆浿珮配笩蓜辔馷嶏霈轡喷噴濆歕瓫盆湓葐呠翉翸喯匉怦抨泙恲胓砰梈烹硑軯閛漰嘭駍磞芃朋挷竼倗莑堋弸彭棚椖傰塜塳搒漨硼稝蓬鹏槰樥熢憉澎輣篷膨錋韸", "P髼蟚蟛鬅纄韼鵬騯鬔鑝捧淎皏剻掽椪碰踫浌巼闏乶喸丕伓伾批纰邳坯怶披抷炋狉狓砒悂秛秠紕铍旇翍耚豾鈚鈹鉟銔劈磇駓髬噼錃錍魾憵礔礕闢霹皮阰芘岯枇毞肶毗毘疲笓蚍郫陴啤埤崥蚽", "P豼椑焷琵脾腗榌鲏罴膍蜱隦魮壀鮍篺螷貔簲羆鵧朇鼙蠯匹庀仳圮苉脴痞銢諀鴄擗噽癖嚭屁淠渒揊媲嫓睤睥潎僻澼甓疈譬鷿鸊片囨偏媥犏篇翩鶣骈胼腁楄楩賆諚骿蹁駢騈覑谝貵諞骗魸騗騙", 
    	                                                 "P剽彯漂缥飘磦旚縹翲螵犥飃飄魒瓢薸闝殍瞟篻醥皫顠票僄勡嘌嫖徱慓氕撇撆暼瞥丿苤鐅嫳姘拼礗穦馪驞玭贫貧琕嫔频頻嬪薲嚬矉颦顰品榀朩牝汖娉聘乒甹俜涄砯艵竮頩平评凭呯坪岼苹郱", "P屏帡枰洴玶荓娦瓶屛帲淜萍蚲塀幈焩甁缾聠蓱蛢評軿鲆凴慿箳輧憑鮃檘簈蘋钋坡岥泼娝釙颇溌酦潑醱鏺婆嘙蔢鄱皤謈櫇叵尀钷笸鉕駊廹岶迫敀昢洦珀哱烞破砶粕蒪頗魄剖颒抔抙捊掊裒箁", "P咅哣婄犃兺哛仆攴扑抪炇巬巭柨陠痡铺駇噗撲鋪擈鯆圤匍莆菩菐葡蒱蒲僕酺墣璞濮瞨穙镤贌纀鏷朴圃埔浦烳普圑溥暜谱潽樸氆諩檏镨譜蹼鐠舖舗曝", "Q摮磝朁鸧鶬鼜詧軙儭櫬趍袳創敠匚釓隑矼肐鉤扢琯矔鸛龜氿肣馯抲頜礉隺渹舙酄攲敧稘毄緝觭禨鄿鐖饑焏踖躤蟣済萕濟袷唊脥價靬鳒鰜鶼鑯揃葥漸摪彊勥焳燋湫湬蟜譑峤嶠潐噭椄疌倢偼", "Q媫蛣楬鮚荕埐嶜厪墐慬歏濅涇鶄捄趄跔鮈跼趜弆瞿鐻捲弮蚗傕鶌躩麏麕焌箘開欿歁殻揢挳硜矻誇厱熑燫艌鳥毆七迉沏妻柒倛凄栖桤缼郪娸悽戚捿桼淒萋朞期欺紪褄僛嘁慽榿槭漆緀慼磎諆", "Q霋蹊魌鏚鶈亓祁齐圻岐岓芪其奇斉歧祈肵疧竒剘斊旂耆脐蚑蚚颀埼崎帺掑淇渏猉畦萁跂軝釮骐骑棊棋琦琪祺蛴愭碁碕褀頎鬾鬿旗粸綥綦綨緕蜝蜞璂禥蕲踑螧鲯懠濝藄檱櫀簱臍騎騏鳍蘄鯕", 
    	                                                 "Q鵸鶀麒籏纃艩蠐鬐騹魕鰭玂麡乞邔企屺岂芑启呇杞玘盀唘豈起啓啔啟婍绮晵棨綮諬簯闙气讫気汔迄弃汽矵芞呮泣炁盵咠契砌荠栔訖唭欫夡愒棄湆湇葺碛摖暣甈碶噐憇器憩磜磧薺礘罊蟿掐", "Q葜拤跒酠鞐圶冾帢恰洽殎硈愘髂千仟阡奷扦汘芊迁佥岍杄汧茾竏钎拪牵粁悭蚈谸铅婜牽釺谦雃僉愆签骞鹐搴摼撁箞諐遷褰顅檶攐攑櫏簽鏲鵮攓騫鐱鬜鬝籤韆仱岒忴扲拑乹前钤歬虔钱钳乾", "Q偂掮揵軡媊鈐鉗墘榩箝銭潜橬錢黔鎆黚騝濳騚灊籖鰬浅肷淺嵰慊遣蜸潛谴缱繾譴鑓欠刋芡茜倩悓堑傔嵌棈椠皘蒨塹歉蔳儙槧篏輤篟壍嬱縴呛羌戕戗斨枪玱猐琷跄嗴椌獇腔嗆溬蜣锖嶈戧槍", "Q牄瑲羫锵篬錆蹌镪蹡鎗鏘鏹強强墙嫱蔷樯漒蔃墻嬙檣牆謒艢蘠抢羟搶羥墏摤繈襁繦炝唴熗羻兛瓩悄硗郻鄗嵪跷鄡鄥劁敲踍锹墝碻頝墽幧橇缲磽鍫鍬繑趬蹺蹻乔侨荍荞桥硚喬僑槗谯嘺嫶憔", "Q蕎鞒樵橋犞癄瞧礄藮譙趫鐈鞽顦巧釥愀髜俏诮陗峭帩窍殼翘誚髚僺撬鞘竅翹躈切苆癿茄聺且厒妾怯匧窃倿悏挈洯惬淁笡愜蛪朅箧緁锲魥篋踥穕藒鍥鯜鐑竊籡亲侵钦衾骎菳媇嵚誛嶔親顉駸", "Q鮼寴庈芩芹埁珡矝秦耹菦蚙捦琴琹禽鈙雂勤嗪溱靲噙擒斳鳹懄檎澿瘽螓懃蠄坅昑笉梫赾寑锓寝寢鋟螼吢吣抋沁唚菣揿搇撳瀙藽靑青氢轻倾卿郬圊氫淸清傾廎蜻輕鲭鑋夝甠剠勍情殑硘晴棾", 
    	                                                 "Q氰葝暒擏樈擎檠黥苘顷请庼頃漀請檾謦庆凊掅碃箐靘慶磬儬濪罄櫦宆跫銎卭邛穷穹茕桏笻筇赹惸焪焭琼舼蛩蛬煢熍睘瞏窮儝憌橩璚藑竆藭丘丠邱坵恘秋秌寈蚯媝萩楸鹙篍緧蝵穐趥鳅蟗鞦", "Q鞧蘒鰌鰍鶖龝叴囚扏犰玌朹肍求虬泅虯俅觓訄訅酋唒浗紌莍逎逑釚梂殏毬球赇釻崷巯湭皳盚遒煪絿蛷裘巰觩賕璆蝤銶醔鮂鼽鯄鵭蠤鰽搝糗区曲佉匤岖诎阹驱坥屈岴抾浀祛胠袪區蛆躯筁粬", "Q蛐詘趋嶇駆憈敺誳駈麹髷趨麯軀麴黢驅鰸鱋佢劬斪朐菃衐鸲淭渠絇葋軥蕖璖磲螶鴝璩翵蟝鼩蘧匷忂灈戵欋氍籧臞癯蠷衢躣蠼鑺鸜取竘娶紶詓竬龋齲厺去刞呿迲郥耝阒觑趣閴麮闃覰覷鼁覻", "Q峑悛圈圏棬駩騡鐉全权佺诠姾泉洤荃拳牷辁啳埢婘惓痊硂铨湶犈筌絟腃葲搼楾瑔觠詮輇蜷銓権踡縓醛闎鳈鬈孉巏鰁權齤蠸颧顴犬汱畎烇绻綣虇劝券巻牶椦勧韏勸炔缺蒛瘸却埆崅悫雀硞确", "Q阕塙搉皵阙鹊愨榷墧慤毃確趞燩闋礐闕鵲礭夋囷峮逡宭帬裙羣群裠郄", "R吺兊兌熯卪坈繚髳挐鈉柟蟯臑抐內涊蹨孃菍莥獳檽蹃亽罖囕呥肰衻袇蚦袡蚺然髥嘫髯燃繎冄冉姌苒染珃媣蒅穣瀼獽禳瓤穰躟鬤壌嚷壤攘爙让懹譲讓荛饶桡橈襓饒犪扰娆隢擾绕遶繞惹热熱", 
    	                                                 "R人仁壬忈朲忎秂芢鈓魜銋鵀忍荏栠栣荵秹稔綛躵刃刄认仞仭讱任屻扨纫妊杒牣纴肕轫韧饪姙紉衽紝訒軔梕袵絍腍靭靱韌飪認餁扔仍辸礽芿陾日驲囸釰鈤馹戎肜栄狨绒茙茸荣容峵毧烿媶嵘", "R絨羢嫆搈搑摉榵溶蓉榕榮熔瑢穁蝾褣镕氄縙融螎駥髶嬫嶸爃鎔瀜曧蠑冗宂傇軵穃厹禸柔粈媃揉渘葇瑈腬糅蝚蹂輮鍒鞣瓇騥鰇鶔楺煣韖肉宍嶿邚如侞帤茹桇袽铷渪筎蒘銣蕠儒鴑嚅嬬孺濡薷", "R鴽曘燸襦蠕颥醹顬鱬汝肗乳辱鄏入扖込杁洳嗕媷溽缛蓐鳰褥縟擩堧撋壖阮朊软耎偄軟媆愞瑌腝嫰碝緛蝡輭瓀礝桵甤緌蕤蕊蕋橤繠蘂蘃芮枘蚋锐瑞睿叡壡闰润閏閠潤橍叒若偌弱鄀婼渃焫楉", "R嵶蒻箬篛爇鰙鰯鶸", "S鉍灬杓攃偲纔參叄叅喰傪穇懆鄵拺笧粣鎈褨剎摻攙摌顫塲場綝乗娍匙飭埫醻敊猭漺輴縒棇楤漎憁缞縗鎝単單擔伔僤宲鍉遞遰挕阇闍陏蘴棴丨鞨咶鏸韢閄靃濈櫼帴菨嫅潏麗攦療蕯瀧氀稤蠰", "S娞淰掱忯圱圲凵廧薔鐰韒鞩嫀殸棯葚挼仨桬撒洒訯靸潵卅飒脎萨摋隡馺颯薩櫒栍毢愢揌塞毸腮嘥噻鳃顋嗮赛僿賽簺虄三弎叁毵毶厁毿犙鬖壭伞傘散糁糂馓橵糝糣糤繖鏒饊俕閐桒桑槡嗓搡", 
    	                                                 "S磉褬颡鎟顙丧喪掻慅搔溞骚缫臊鳋颾騒鰠鱢扫掃嫂埽瘙氉矂髞色栜涩啬渋铯歮琗嗇瑟歰銫澁懎擌濇濏瘷穑澀璱瀒穡繬穯轖鏼譅飋裇聓森槮襂僧鬙閪縇杀沙纱乷砂唦挱猀粆紗莎铩痧硰蔱裟", "S樧魦鲨閷鎩鯊鯋繺傻儍繌倽唼啥帹萐喢歃煞翜翣閯霎筛篩簁簛晒曬山彡邖圸删刪杉杣芟姗衫钐埏狦珊舢痁脠軕笘閊跚剼搧嘇幓煽蔪潸澘曑檆膻鯅羴羶闪陕炶陝閃晱煔睒熌覢讪汕疝苫扇訕", "S赸傓善椫銏骟僐鄯墠墡缮嬗擅敾樿膳磰謆赡繕蟮譱贍鐥饍騸鳝灗鱔伤殇商觞傷墒慯滳蔏殤熵螪觴謪鬺裳垧扄晌赏賞鑜丄上仩尙尚恦绱緔弰捎梢烧焼稍旓筲艄蛸輎蕱燒髾鮹勺芍柖玿韶少劭", "S卲邵绍哨娋袑紹綤潲奢猞赊畲輋賒賖檨舌佘蛇蛥舍捨厍设社舎厙射涉涻渉設赦弽慑摄滠慴摵蔎蠂韘騇懾灄麝欇申屾扟伸身侁呻妽籶绅罙诜柛氠珅穼籸娠峷甡眒砷堔深紳兟椮葠裑訷罧蓡詵", "S甧蔘燊薓駪鲹鯓鵢鯵鰺神榊鉮鰰邥弞抌沈审矤哂矧宷谂谉婶渖訠審諗頣魫曋瞫嬸瀋覾讅肾侺昚甚胂涁眘渗祳脤腎愼慎椹瘆蜃滲鋠瘮升生阩呏声斘昇枡泩苼殅牲珄竔胜陞曻陹笙湦焺甥鉎聲", "S鍟鼪鵿绳憴澠譝省眚偗渻圣晟晠剰盛剩勝貹嵊聖墭榺蕂橳賸尸失师呞虱诗邿鸤屍施浉狮師絁湤湿葹溮溼獅蒒蓍詩瑡酾鳲蝨鳾褷鲺鍦鯴鰤鶳襹籭釃十饣什石辻佦时竍识实実旹飠峕拾炻祏蚀", 
    	                                                 "S食埘時莳寔湜遈塒嵵溡蒔榯蝕鉽篒鲥鮖鼫鼭鰣史矢乨豕使始驶兘屎笶榁鉂駛士氏礻世丗仕市示卋式叓事侍势呩柹视试饰冟室恀恃拭是枾柿眂贳适栻烒眎眡舐轼逝铈視釈崼弑揓谥貰释勢嗜", "S弒煶睗筮觢試軾鈰飾舓誓奭噬嬕澨諟諡遾餝螫簭籂襫鰘兙瓧収收手守垨首艏寿受狩兽售授绶痩膄壽瘦綬夀獣獸鏉书殳抒纾叔杸枢陎姝柕倏倐書殊紓掓梳淑焂菽軗鄃疎疏舒摅毹毺綀输跾踈", "S樞蔬輸橾鮛攄瀭鵨尗秫婌孰赎塾熟璹贖暏暑黍署鼠鼡蜀潻薯曙癙襡糬襩籔蠴鱪鱰朮术戍束沭述侸怷树竖荗恕庶庻絉蒁術裋数竪腧墅漱潄數澍豎樹濖錰鏣鶐虪刷唰耍誜衰摔甩帅帥蟀卛闩拴", "S閂栓涮腨双滝霜雙孀骦孇騻欆礵鷞鹴艭驦鸘爽塽慡樉縔鏯灀谁脽誰氵水氺閖帨涗涚祱税裞睡吮楯顺舜順蕣橓瞚瞤瞬鬊说妁烁朔铄欶硕矟嗍搠蒴嗽槊碩鎙厶丝司糹私咝泀俬思恖鸶媤斯絲缌", "S蛳楒禗鉰飔凘厮榹禠罳銯锶嘶噝廝撕澌緦蕬螄鍶蟖蟴颸騦鐁鷥鼶死巳亖四罒寺汜伺似佀兕姒泤祀価孠泗饲驷俟娰柶牭梩洍涘肂飤笥耜釲竢覗嗣肆貄鈻飼禩駟蕼儩騃瀃螦乺忪松枀枩娀柗倯", "S凇梥崧庺淞菘嵩硹蜙憽檧濍鬆怂悚捒耸竦傱愯嵷慫聳駷讼宋诵送颂訟頌誦餸鎹凁捜鄋嗖廀廋搜溲獀蒐蓃馊飕摗锼艘螋醙鎪餿颼騪叟傁嗾瞍擞薮擻藪櫢瘶苏甦酥稣窣穌鯂蘇蘓櫯囌俗玊夙诉", 
    	                                                 "S泝肃洬涑珟素速殐粛骕傃粟訴谡嗉塐塑嫊愫溯溸肅遡鹔僳愬榡膆蔌觫趚遬憟樎樕潥鋉餗縤璛簌藗謖蹜驌鱐鷫狻痠酸匴祘笇筭蒜算夊芕虽倠哸浽荽荾眭葰滖睢熣濉鞖雖绥隋随遀綏隨瓍膸瀡", "S髄髓亗岁砕祟粋谇埣嵗脺遂歲歳煫碎隧嬘澻穂誶賥檖燧璲禭穗穟繀襚邃旞繐繸鐆譢鐩孙狲荪飧搎猻蓀飱槂蕵薞畃损笋隼損榫箰鎨巺潠唆娑莏傞挲桫梭睃嗦羧蓑摍缩趖簑簔縮髿鮻所唢索琐", "S琑惢锁嗩暛溑瑣鎍鎖鎻鏁逤溹蜶厦忄莘疋栅属", "T諳啚裧儃禪蟬繵閶瞋鐺珵侱漦爞綢籌俶埱蓴鶉鈶撘迏迖沓荅逹溚達鎉韃呔蚮軚貸癉撣嘾彈餤黨攩欓逿蕩簜蘯闣朷焘燾僜奃髢詆弚苐媂諦顚顛鈿调蓧啑惵趃奵忊墥峝湩詷钭鋀褍蜳橔囤庉憞", "T燉軃鋨鞈騩咍漢宊她冂燑轁墤謉饋噋擴斄伖鍩奤嵜鉆汭蟺鱓愓漡苕萔姼忕徥褆扌涭瑹儵稅磃鋖枱他它牠祂咜趿铊塌榙溻褟蹹侤塔墖獭鮙鳎獺鰨挞狧闼崉涾搨遝遢阘榻毾禢撻澾誻踏嚃錔嚺", "T濌蹋鞜闒鞳闥譶躢襨囼孡骀胎台邰坮抬苔炱炲跆鲐箈臺颱儓鮐嬯擡薹檯籉太冭夳忲汰态肽钛泰粏舦酞鈦溙態燤坍贪怹啴痑舑貪摊滩嘽潬瘫擹攤灘癱坛昙倓谈郯婒惔弾覃榃痰锬谭墰墵憛潭", 
    	                                                 "T談醈壇曇檀顃罈藫壜譚貚醰譠罎鷤忐坦袒钽菼毯鉭嗿憳憻暺醓璮叹炭埮探傝湠僋嘆碳舕歎賧汤铴嘡劏羰蝪薚镗蹚鏜鐋鞺鼞饧坣唐堂傏啺棠鄌塘嵣搪溏蓎隚榶漟煻瑭禟膅樘磄糃膛橖篖糖螗", "T踼糛螳赯醣鎕餹闛饄鶶帑倘偒淌傥耥躺镋鎲儻戃曭爣矘钂烫摥趟燙仐夲弢涛绦掏絛詜嫍幍慆搯滔槄瑫韬飸縚縧濤謟鞱韜饕迯咷洮逃桃陶啕梼淘绹萄祹裪綯蜪鞀醄鞉鋾錭駣檮騊鼗讨套討畓", "T忑忒特貣脦铽慝鋱蟘膯鼟疼痋幐腾誊漛滕邆縢螣駦謄儯藤騰籐鰧籘虅驣霯唞朰剔梯锑踢銻鷈鷉厗绨偍珶啼崹惿提渧稊缇罤遆鹈嗁瑅綈碮徲漽緹蕛蝭题趧蹄醍謕蹏鍗鳀題鮷鵜騠鯷鶗鶙体挮", "T躰骵軆體戻屉剃洟倜悌涕逖悐惕掦逷惖揥替楴裼褅殢髰嚏鬀瓋鬄籊天兲婖添酟靔黇靝田屇沺恬畋畑胋畠甛甜菾湉填搷阗碵緂磌窴鴫璳闐鷆鷏忝殄倎唺悿捵淟晪琠腆觍痶睓舔餂覥賟錪靦掭", "T瑱睼舚旫佻庣挑祧聎芀条岧岹迢祒條笤蓚龆樤蜩鋚鞗髫鲦螩鯈鎥齠鰷宨晀朓脁窕誂窱嬥眺粜絩覜趒跳頫糶贴萜貼铁蛈僣鴩鐡鐵驖呫帖飻餮厅庁汀艼听耓厛烃烴綎鞓聴聼廰聽廳邒廷亭庭莛", "T停婷嵉渟筳葶蜓楟榳閮霆聤蝏諪鼮圢侹娗挺涏梃烶珽脡艇颋誔鋌頲濎乭囲炵通痌嗵蓪樋熥仝同佟彤峂庝哃狪茼晍桐浵烔砼蚒眮秱铜童粡絧衕酮鉖僮勭銅餇鲖潼曈朣橦氃犝膧瞳鮦统捅桶筒", 
    	                                                 "T統綂恸痛慟憅偷偸鍮头投骰頭妵紏敨斢黈蘣透凸禿秃怢突唋涋捸堗湥痜葖嶀鋵鵚鼵図图凃峹庩徒捈涂荼途屠梌揬稌塗嵞瘏筡腯蒤鈯圖圗廜跿酴馟鍎駼鵌鶟鷋鷵土圡吐汢钍釷兎迌兔莵堍菟", "T鵵湍猯煓貒团団抟團慱槫檲鏄糰鷒鷻圕疃彖湪褖推蓷藬颓隤頹頺頽魋穨蘈蹪俀脮腿僓蹆骽退娧煺蜕褪駾吞呑朜焞暾黗屯芚饨豘豚軘鲀魨霕臀臋氽畽坉乇讬托汑饦侂咃拕拖沰侻莌袥託涶脫", "T脱飥魠驮佗陀坨岮沱驼柁砣砤袉鸵紽堶跎酡碢馱槖駄踻駝駞橐鮀鴕鼧騨鼍驒鼉妥毤庹媠椭楕嫷橢鵎拓柝唾跅毻箨籜", "U辪癷袰蝊曢聣烪燞躼蒊蓞耂稥洜毜毝茒桛毮朑焽虲鶑鎼鐢艈霻闧焑屗歚徚鍂藔贘皼斏聁祍", "W趡惡噁唲陚龏萖関闗關貫窐姽恑瞶咼堝濄幠膴鋘譁瀤綄朚撝蒦嚄擭雘艧扝噲抂巋磈薶槾鄤澫堥夘呅韎雺霿沕忞鴖譕帓歾靺瞴蟱墲娒孯掔瓗渞蜹捼琞亠撱鰖屲劸哇娃徍挖洼娲畖窊媧嗗蛙搲", "W溛漥窪鼃攨瓦佤邷咓瓲砙袜嗢腽膃襪韈韤歪喎竵崴外弯剜婠帵塆湾睕蜿潫豌彎壪灣丸刓汍纨芄完岏忨抏玩笂紈捖顽烷琓貦頑邜宛倇唍挽晚盌埦婉惋晩梚绾脘菀晼椀琬皖畹碗綩綰輓踠鋔鍐", 
    	                                                 "W万卍卐杤捥腕萬翫鋄薍錽贃鎫贎尩尪尫汪亡亾兦王仼彺莣蚟网忹往徃枉罔惘菵暀棢焹蛧辋網蝄誷輞瀇魍妄忘迋旺盳望朢危威烓偎逶隇隈喴媁媙愄揋揻渨煀葨葳微椳楲溦煨詴縅蝛覣嶶薇鳂", "W癐巍鰃鰄囗为韦围帏沩违闱峗峞洈為韋桅涠唯帷惟维喡圍嵬幃湋溈爲違潍蓶鄬潙潿濰鍏闈鮠癓覹犩霺伟伪尾纬芛苇委炜玮洧娓捤浘荱诿偉偽崣梶硊萎隗骩嵔廆徫愇猥葦蒍骪骫暐椲煒瑋痿", "W腲艉韪僞碨蜲蜼鲔寪緯蔿諉踓韑頠儰濻鍡鮪壝韙颹瀢亹斖卫未位味苿畏胃軎尉硙菋谓喂媦渭猬煟墛蔚慰熭犚磑緭蝟衛懀濊璏罻衞謂餧鮇螱褽餵魏藯轊鏏霨鳚蘶饖讆躗讏躛昷塭温殟溫瑥榲", "W瘟豱鳁鎾饂鰛鰮文彣纹芠炆砇闻紋蚉蚊珳阌鈫雯瘒聞馼魰鳼鴍螡閺閿蟁闅鼤闦刎吻呚忟抆呡肳紊桽脗稳穏穩问妏汶莬問渂脕揾搵絻顐璺翁嗡鹟螉鎓鶲奣塕嵡滃蓊暡瞈聬瓮蕹甕罋齆挝倭涡", "W莴唩涹渦猧萵喔窝窩蜗蝸踒我婐婑捰仴沃肟卧臥偓媉幄握渥焥硪楃腛斡瞃濣瓁龌齷乌圬弙污邬呜杇巫屋洿诬钨趶剭窏釫鄔嗚誈誣箼螐鴮鎢鰞无毋吴吾呉芜梧洖浯茣莁珸祦鹀無禑蜈蕪璑鵐", "W鯃鼯鷡乄五午仵伍坞妩庑忤怃迕旿武玝侮俉倵捂啎娬牾珷塢摀熓碔鹉瑦舞嫵廡憮潕錻儛橆甒鵡躌兀勿务戊阢伆屼扤岉杌芴忢物矹敄误務悞悟悮粅逜晤焐婺嵍痦隖靰骛奦嵨溩雾寤熃誤鹜鋈", 
    	                                                 "W窹霚鼿霧齀蘁騖鶩", "X欸庍壆扱烲愖糦臰欪滀嘼錯廗諜摡給蚼規嶲鮭巂鬹咁譀迒茠滈欱郃螛嗃熇燺揈閧闀闂鬨銗謼滸芐觟懁郇狟澴還镮鐶儶譮孈葷轋掝擊彐蔇縘繫夾埉浹傢裌叚徦鰔梘筧礆見閒瞯鵁轇驕絞較湝", "X頡觧吤繲坙巠坓顈眗鼰鼳鋗絹獧決泬勪噱姰濬愾埳堿闞晇欵歀潰搚懢壏嘮釐溓蘝蘞稴漻衖莔禰瓕賯撓譊鉨鉩煖謳嘔盤嚊恓棲諿氣欦臤慳荨羬蕁槏伣俔嗛骹燆睄綅瓊蓲鱃魼胊卻舃碏儴勷蘘", "X纕繻灑钑鈒鰓騷雭殺榝姍釤縿莦颵姺濕宩昰笹齛咰鉥獡箾蜤燍叜宿碿潚橚孫鶽橝撢餳饀屜歒盷蓨赨緰圩韡捾夕兮忚汐西覀吸希卥昔析矽穸肸肹俙徆怸郗饻唏奚娭屖息悕晞氥浠牺狶莃唽悉", "X惜桸欷淅渓烯焁焈琋硒菥赥釸傒惁晰晳焟犀睎稀粞翕翖舾鄎厀嵠徯溪煕皙蒠锡僖榽熄熙緆蜥豨餏嘻噏嬆嬉瘜膝餙凞樨橀歙熹熺熻窸羲螅螇錫燨犠瞦礂蟋谿豀豯貕繥雟鯑鵗觹譆醯鏭隵巇曦", "X爔犧酅觽鼷蠵鸂觿鑴习郋席習袭觋媳椺蒵蓆嶍漝覡趘槢蝷薂隰檄謵鎴霫鳛飁騱騽襲鰼驨枲洗玺徙铣喜葈葸鈢屣漇蓰銑憘憙暿橲禧諰壐縰謑蟢蹝璽鱚矖纚躧匸卌戏屃系饩呬忥怬细係恄盻郤", 
    	                                                 "X欯绤細釳阋塈椞舄趇隙慀滊禊綌赩隟熂犔稧戯潟澙蕮覤戱黖戲磶虩餼鬩嚱闟霼衋虾谺傄閕敮煆颬瞎蝦鰕匣侠狎俠峡柙炠狭陜峽烚狹珨祫硖笚翈舺陿溊硤遐搳暇瑕筪碬舝辖縀蕸縖赮魻轄鍜", "X霞鎋黠騢鶷閜丅下吓圷疜夏梺廈睱諕嚇懗罅夓鏬仙仚屳先奾纤佡忺氙杴祆秈苮籼珗莶掀铦跹酰锨僊僲嘕銛鲜暹韯憸鍁繊褼韱鮮馦蹮孅廯攕譣纎鶱襳躚纖鱻伭咞闲妶弦贤咸挦涎胘娴娹婱絃", "X舷蚿衔啣痫蛝閑鹇嫌甉銜嫺嫻憪撏澖誸賢諴輱醎癇癎藖鹹礥贒鑦鷳鷴鷼冼狝显险毨烍猃蚬険赻筅尟尠搟禒蜆跣箲獫獮藓鍌燹顕幰攇櫶蘚玁韅顯灦县岘苋现线臽限姭宪県陥哯垷娊娨峴晛涀", "X莧陷現馅睍絤缐羡献粯腺僩僴綫誢撊線鋧憲橌縣錎餡豏瀗臔獻糮鏾霰鼸乡芗相香郷厢啌鄉鄊廂湘缃葙鄕楿薌箱緗膷襄忀骧麘欀瓖镶鱜鑲驤瓨佭详庠栙祥絴翔跭享亯响蚃饷晑飨想銄餉鲞嚮", "X蠁鯗響饗饟鱶向姠巷项珦象缿萫項像勨嶑曏橡襐蟓鐌鱌灱灲呺枭侾削哓枵骁宯宵庨恷消绡虓逍鸮啋婋梟焇猇萧痚痟硝硣窙翛萷销揱綃嘐歊潇箫踃嘵憢撨獢銷霄彇膮蕭魈鴞穘簘藃蟂蟏謞鴵", "X嚣瀟簫蟰髇嚻囂櫹髐鷍蠨驍毊虈洨郩崤淆訤誵小晓暁筱筿皛曉篠謏皢孝肖効咲恔俲哮效校涍笑啸傚敩滧詨嘋嘨誟嘯歗熽斅斆些楔歇蝎蠍劦协旪邪協胁垥奊恊拹挾脅脇脋衺偕斜谐猲絜翓嗋", 
    	                                                 "X愶携瑎綊熁膎勰撷擕緳缬蝢鞋諧燲擷鞵襭攜纈讗龤写冩寫藛伳灺泄泻祄绁缷卸炧炨卨娎屑屓偰徢械焎禼亵媟屟揳渫絬谢僁塮榍榭褉噧屧暬韰嶰廨懈澥獬糏薢薤邂燮褻謝夑瀉瀣爕蟹蠏齘齥", "X齂躠屭躞心邤妡忻芯辛昕杺欣盺俽惞鈊锌新歆廞噷噺嬜薪馨鑫馫枔鬵鐔伈潃阠伩囟孞炘信軐脪衅訫焮馸舋顖釁兴狌星垶骍惺猩煋瑆腥蛵觪箵篂興謃曐觲騂皨刑邢形陉侀郉哘型洐钘陘娙硎", "X裄铏鈃鉶銒鋞睲醒擤杏姓幸性荇倖莕婞悻涬塂緈嬹臖凶兄兇匈芎讻忷汹哅恟洶胷胸訩詾雄熊诇詗夐敻休俢修咻庥烋烌羞脙鸺臹貅馐樇銝髤髹鎀鮴鵂饈鏅飍苬朽綇滫糔秀岫珛绣袖琇锈溴綉", "X璓裦螑繍繡鏥鏽齅戌旴疞盱欨胥须訏顼虗虚谞媭幁揟欻虛須楈窢頊嘘稰需魆噓墟嬃歔縃蕦蝑歘諝譃魖驉鑐鬚俆徐蒣许呴姁诩冔栩珝偦許湑暊詡鄦糈醑盨旭伵序汿侐卹沀叙恤昫洫垿欰殈烅", "X珬勖勗敍敘烼绪续酗喣壻婿朂溆絮訹嗅慉煦続蓄賉槒漵潊盢瞁緒聟銊稸緖瞲藚續蓿吅轩昍咺宣晅軒梋谖喧塇媗愃愋揎萱萲暄煊瑄蓒睻儇禤箮翧蝖嬛蕿諠諼鍹駽矎翾藼蘐蠉譞鰚讂玄玹痃悬", "X旋琁蜁嫙漩暶璇檈璿懸选烜暅選癣癬怰泫昡炫绚眩袨铉琄眴衒渲絢楦鉉碹蔙镟鞙颴縼繏鏇贙疶蒆靴薛鞾穴斈乴坹学岤峃茓泶袕鸴踅學嶨澩燢觷雤鷽雪樰膤艝轌鳕鱈血吷怴泧狘疦桖烕谑趐", 
    	                                                 "X謔瀥坃勋埙焄勛塤熏窨蔒勲勳薫駨嚑壎獯薰曛燻臐矄蘍壦爋纁醺寻巡旬驯杊询峋恂洵浔紃荀栒桪毥珣偱尋循揗詢馴鄩鲟噚攳樳燂燅燖璕襑蟳鱏鱘灥卂训讯伨汛迅侚徇狥迿逊殉訊訓訙奞巽", "X殾遜愻賐噀蕈顨鑂吁", "Y叆賹礙譺靉菴媕葊痷闇鵪鶕晻洝媼澚墺驁頨瑒耛拸呾訑婸潒扚昳屵姶堊搤閼煾妋鳱輵夃焸蠱溎裷緷蟈淊嚛欥瓛輐喛揘韹熀袆煇褘蚘噦矆拁玪豜豣將侥烄僥伒莖俓泂揂圧僪貗飬妜焆鐍炏顑", "Y丂髺樂貍櫟躒輅擽嫚玅桙獶輗掜孴儗肀喦噛槷嚙钀汼齵堷踦锜錡裿綺鉛膁羗撽赺欽汓翑蝺輑橪蕘嬈嵤銳鋭挻烻剡虵縄繩鱦釶鉇箷戺謚釋哾說説爍鑠梀筍鉈珆旲錟湯匋鴺趯恌銕桯筩婾媮殕", "Y悇蛻涒扡挩捝狏迱詑彵啘乛涴妧尣燰琟維厃痏薳叞榅辒輼轀勜臒汙汚烏歍吳郚娪焬熈誒歖潝疨磍嬐薟唌湺衘崄險嶮硍羨麲詳峫鐷洩紲絏緤訢脩褎褏銹獝藇鱮楥辥廵潯丫压呀庘押鸦桠鸭孲", "Y铔椏鴉錏鴨壓鵶鐚牙伢岈芽厓枒琊笌蚜堐崕崖涯猚瑘睚衙漄齖厊庌哑唖啞痖雅瘂蕥劜圠亚穵襾讶亜犽迓亞玡垭娅挜砑俹氩埡婭掗訝揠氬猰聐圔稏窫齾咽恹剦烟珚胭偣崦淹焉菸阉湮腌傿煙", 
    	                                                 "Y鄢嫣漹嶖樮醃閹嬮篶懕臙黫讠円延闫严妍芫言訁岩昖沿炎郔姸娫狿研莚娮盐琂硏訮閆阎嵒嵓筵綖蜒塩揅楌詽碞蔅颜虤閻厳檐顏顔嚴壛巌簷櫩麙壧孍巖巗巚欕礹鹽麣夵抁沇乵兖奄俨兗匽弇", "Y衍偃厣掩眼萒郾酓嵃愝扊揜棪渰渷琰遃隒椼硽罨裺演褗戭蝘魇噞躽縯檿黡厴甗鰋鶠黤齞龑儼黬黭顩鼴巘曮魘鼹齴黶厌妟觃牪姲彥彦砚唁宴晏艳覎验偐掞焔谚隁喭堰敥焰焱猒硯葕雁椻滟鳫", "Y厭墕暥熖酽嬊谳餍鴈燄燕諺赝鬳曕鴳酀騐験嚥嬿艶贋軅爓醶騴鷃灔贗觾讌醼饜驗鷰艷灎釅驠灧讞豓豔灩央咉姎抰泱殃胦眏秧鸯鉠雵鞅鍈鴦扬羊阦阳旸杨炀佯劷氜疡钖飏垟徉昜洋羏烊珜眻", "Y陽崵崸揚蛘敭暘楊煬禓瘍諹輰鍚鴹颺鐊鰑霷鸉卬仰佒坱奍岟养炴氧痒紻傟楧軮慃氱羪養駚懩攁瀁癢礢怏柍恙样羕詇様漾樣幺夭吆妖枖祅訞喓葽楆腰鴁邀爻尧尭肴垚姚峣轺倄烑珧窑傜堯揺", "Y殽谣軺嗂媱徭愮搖摇猺遙遥摿暚榣瑤瑶銚飖餆嶢嶤徺磘窯窰餚繇謠謡鳐颻蘨顤鰩仸宎岆抭杳殀狕苭咬柼眑窅窈舀偠婹崾溔榚鴢闄騕齩鷕穾药要袎窔筄葯詏熎覞靿獟鹞薬鼼曜燿艞藥矅曣耀", "Y纅鷂讑鑰倻椰暍噎潱蠮爷耶捓揶铘爺釾鋣鎁擨也吔亪冶埜野嘢漜壄业叶曳页邺夜抴亱枼洂頁晔枽烨掖液谒堨殗腋葉鄓墷楪業馌僷曄曅歋燁擛皣瞱鄴靥嶪嶫澲謁餣嚈擫曗瞸鍱擪爗礏鎑饁鵺", 
    	                                                 "Y靨驜鸈膶岃一弌辷衤伊衣医吚壱依祎咿洢猗畩郼铱壹揖欹蛜禕嫛漪稦銥嬄噫夁瑿鹥繄檹毉醫黟譩鷖黳乁仪匜圯夷冝宐沂诒侇宜怡沶狋衪迤饴咦姨峓弬恞柂瓵荑贻迻宧巸扅栘桋眙胰袘酏痍", "Y移萓媐椬羠蛦詒貽遗暆椸誃跠頉颐飴疑儀熪遺嶬彛彜螔頤頥寲嶷簃顊鮧彝彞謻鏔籎觺讉鸃乙已以迆钇佁攺矣苡苢庡舣蚁釔倚扆笖逘偯崺旑椅鈘鉯鳦旖輢敼螘檥礒艤蟻顗轙齮乂义亿弋刈忆", "Y艺仡匇肊议阣亦伇屹异忔芅伿佚劮呓坄役抑曵杙耴苅译邑佾呭呹峄怈怿易枍泆炈绎诣驿俋奕帟帠弈枻浂玴疫羿衵轶唈垼悒挹栧欭浥浳益袣谊貤陭勚埶埸悘悥殹異羛翊翌萟訲訳豙豛逸釴隿", "Y幆敡晹棭殔湙焲蛡詍跇鈠骮亄兿意溢獈痬竩缢義肄裔裛詣勩嫕廙榏潩瘗膉蓺蜴靾駅億撎槸毅熠熤熼瘞誼镒鹝鹢黓劓圛墿嬑嬟嶧憶懌曀殪澺燚瘱瞖穓縊艗薏螠褹寱斁曎檍歝燡燱翳翼臆貖鮨", "Y癔藙藝贀鎰镱繶繹豷霬鯣鶂鶃鶍瀷蘙譯議醳醷饐囈鐿鷁鷊懿襼驛鷧虉鷾讛齸乚囙因阥阴侌垔姻洇茵荫音骃栶殷氤陰凐秵裀铟陻隂喑堙婣愔筃絪歅溵禋蒑蔭慇瘖銦磤緸鞇諲霒駰噾濦闉霠韾", "Y冘吟犾苂垠泿圁峾烎狺珢粌荶訔唫婬寅崟崯淫訡银鈝龂滛碒鄞夤蔩訚誾銀龈噖殥璌嚚檭蟫霪齗齦鷣尹引吲饮蚓隐淾釿鈏飲隠靷飮朄趛檃瘾隱嶾濥螾蘟櫽癮讔印茚洕胤垽湚猌廕酳慭癊憖憗", 
    	                                                 "Y鮣懚檼应応英偀桜珱莺啨婴媖愥渶绬朠煐瑛嫈碤锳嘤撄滎甇緓缨罂蝧賏樱璎噟罃褮霙鴬鹦嬰應膺韺甖鎣鹰鶧嚶孆孾攖瀴罌蘡櫻瓔礯譻鶯鑍纓蠳鷪軈鷹鸎鸚盁迎茔盈荥荧莹萤营萦蛍営溁溋", "Y萾僌塋楹滢蓥潆熒蝇瑩蝿嬴營縈螢濙濚濴藀覮謍赢巆攍攚瀛瀠瀯蠅櫿灐籝灜贏籯矨郢梬颍颕颖摬影潁瘿穎頴巊廮鐛癭映暎硬媵膡鞕瀅譍哟唷喲佣拥痈邕庸傭嗈鄘雍墉嫞慵滽槦牅噰壅擁澭", "Y郺镛臃癕雝鏞鳙廱灉饔鱅鷛癰喁颙顒鰫永甬咏怺泳俑勇勈栐埇悀柡涌恿傛惥愑湧硧詠塎嵱彮愹蛹慂踊禜鲬踴鯒用苚砽醟优忧攸呦怮泑幽悠麀滺憂優鄾嚘懮瀀櫌纋耰尢尤由沋犹邮怞油肬怣", "Y斿柚疣峳浟秞莜莤莸逌郵铀偤蚰訧逰游猶遊鱿楢猷鲉輏駀蕕蝣魷輶鮋櫾邎友有丣卣苃酉羑庮羐莠梄聈脜铕湵蒏禉蜏銪槱牖牗黝又右幼佑侑孧狖糿哊囿姷宥峟牰祐诱迶唀梎蚴亴貁釉酭誘鼬", "Y扜纡迂迃穻陓紆虶唹淤盓渝瘀箊于亐予邘伃余妤扵杅欤玗玙於盂臾衧鱼俞兪禺竽舁茰荢娛娯娱狳谀酑馀渔萸釪隃隅雩魚堣堬崳嵎嵛愉揄楰湡畬畭硢腴逾骬愚楡榆歈牏瑜艅虞觎漁睮窬舆褕", "Y歶羭蕍蝓諛雓餘魣嬩懙澞覦踰歟璵螸輿鍝礖謣髃鮽旟籅騟鯲鰅鷠鸆与伛宇屿羽雨俁俣挧禹语圄峿祤偊匬圉庾敔鄅萭萮铻傴寙斞楀瑀瘐與語窳鋙龉噳嶼貐斔麌蘌齬玉驭聿芋芌妪忬饫育郁彧", 
    	                                                 "Y昱狱秗茟俼峪栯浴砡钰预喐域堉悆惐欲淢淯袬谕逳阈喅喩喻媀寓庽御棛棜棫焴琙矞裕遇飫馭鹆愈滪煜稢罭蒮蓣誉鈺預嫗嶎戫毓獄瘉緎蜟蜮輍銉隩噊慾稶蓹薁豫遹鋊鳿澦燏燠蕷諭錥閾鴥鴧", "Y鴪儥礇禦魊鹬癒礜穥篽繘醧鵒櫲饇蘛譽轝鐭霱欎驈鬻籞鱊鷸鸒欝軉鬰鬱灪籲爩囦鸢剈冤弲悁眢鸳寃渁渆渊渕惌淵葾棩蒬蜎鹓箢鳶蜵駌鋺鴛嬽鵷灁鼘鼝元贠邧员园沅杬垣爰貟原員圆笎蚖袁", "Y厡酛圎援湲猨缘鈨鼋園圓塬媴嫄源溒猿獂蒝榞榬辕緣縁蝝蝯魭橼羱薗螈謜轅黿鎱櫞邍騵鶢鶰厵远盶逺遠夗肙妴苑怨院垸衏傆媛掾瑗禐愿裫褑褤噮願曰曱约約箹矱彟彠月戉刖岄抈礿岳枂玥", "Y恱钥悅悦蚎蚏軏钺阅捳跀跃粤越鈅粵鉞閱閲嬳樾篗嶽龠籆瀹蘥黦爚禴躍籥鸑籰龥鸙蒀煴蒕熅奫蝹赟頵馧贇云勻匀伝呍囩妘抣沄纭芸昀畇眃秐郧涢紜耘耺鄖雲愪氲溳筼蒷氳熉澐蕓鋆橒篔縜", "Y繧允阭夽抎狁玧陨荺殒喗鈗隕殞褞馻磒霣齫齳孕运枟郓恽晕鄆酝傊惲愠缊運慍暈腪韫韵熨緼蕰蕴縕薀賱醖醞餫藴韗韞蘊韻這", "Z敱捗囃謲匨蔵冊側厠廁嶒扠挿揷蹅茝僝欃倀长仧長镸鼌鼂謿謓迧陳鈂趂朾脀憕徎胵絺泜歭遟遲迣徸漴褈緟蝩蟲紬搊椆詶薵偢芻鉏蕏傳鶨倕埀箠鎚純湻辶腏趠齱骴薋泚跐從蓯樅熜緫潀潈潨", 
    	                                                 "Z酢噈欑穳篹嶉槯伜倅紣綷顇墫拵踆睉蔖剳蹛黱酖亶翢篴拞牴觝啇摕蝃踶點鳭調藋喋臷鰈眰鼑枓剢耑磓隊腞夛仛喥柮樲胕阝贛戇滜睪牫贑嫢啈楖嚌璾鯽穧椾碊鳽餰栫袸濺瀳鏩繳擳竧鳩砠蓻", "Z齟怚跙脧雋捔逫嵁沴酈譧驡狵沵摨聶囁釽亝祇蚔齊磩鈆謙綪繰菬埥軽鯖伹岨恮跧甽繅洓篸箑摂攝鉐實識亊鉃適銴檡薥藷鸀尌虒睟簨駘菭襢犆媞薙嚔鐟塡怗聑跕鉄鉵獞穜潳剬剸塼摶漙篿尵", "Z旽啍忳飩杔馲驝陁萚蘀聉顡醀錗撾扸杫咥枮鮝鞢鋅鮏鯹鎐蓔捙偞擖杝妷秇栺軼乑斦浧銿鈾圫汋帀匝沞咂拶沯桚紥紮鉔魳臜臢杂砸韴雑磼襍雜囐雥災灾甾哉栽烖菑渽溨睵賳宰载崽載再在扗", "Z洅傤酨儎縡兂糌簪簮鐕咱偺喒昝寁撍儧攒儹攢趱趲暂暫賛赞錾鄼濽蹔酂瓉贊鏨瓒酇囋灒讃瓚禶襸讚饡牂羘赃賍臧賘贓髒贜驵駔奘弉脏塟葬銺臓臟傮遭糟蹧醩凿鑿早枣栆蚤棗璅澡璪薻藻灶", "Z皁皂唕唣造梍喿慥煰艁噪簉燥竃譟趮躁竈啫伬则択沢择泎泽责迮則唶啧帻笮舴責溭矠嘖嫧幘箦蔶樍歵諎赜擇澤皟瞔簀耫礋襗謮賾蠌齚齰鸅仄夨庂汄昃昗捑崱稄贼賊鲗蠈鰂鱡怎谮譖譛囎曽", "Z増鄫增憎缯橧熷璔矰磳罾繒譄鱛锃鋥甑赠贈吒迊咋抯挓柤哳偧喳揸渣溠楂劄皶箚樝觰皻譇齄齇扎札甴轧軋闸蚻铡煠牐閘霅鍘譗厏苲眨砟搩鲊鲝踷鮓鮺乍灹诈咤奓柵炸宱痄蚱詐搾摣榨醡夈", 
    	                                                 "Z粂捚斋斎摘榸齋宅翟窄鉙债砦債寨瘵沾毡旃栴粘蛅飦惉詀趈詹閚谵噡嶦薝邅霑氈氊瞻鹯旜譫饘鳣驙魙鱣鸇讝拃斩飐展盏崭斬琖搌盞嶃嶄榐辗颭嫸醆橏蹍輾皽黵占佔战栈桟站偡绽菚棧湛戦", "Z綻嶘輚戰虥虦覱轏蘸驏张弡張章傽鄣嫜彰慞漳獐粻蔁遧暲樟璋餦蟑鏱騿鱆麞仉涨涱掌漲幥礃鞝鐣丈仗扙帐杖胀账粀帳脹痮障墇嶂幛賬瘬瘴瞕佋钊妱巶招昭炤盄釗啁鉊駋窼鍣爫找沼瑵召兆", "Z诏枛垗狣赵笊肁旐棹罀詔照罩箌肇肈趙曌燳鮡櫂瞾羄蜇嫬遮厇折歽矺砓籷虴哲埑粍袩啠悊晢晣辄喆棏蛰詟谪摺輒樀磔輙銸辙蟄嚞謫謺鮿轍讁襵讋者锗赭褶鍺这柘浙淛嗻蔗樜鹧蟅鷓贞针侦", "Z浈珍珎貞帪栕桢眞真砧祯針偵敒桭酙寊湞葴遉搸斟楨獉甄禎蒖蓁鉁靕榛槇殝瑧碪禛潧箴樼澵臻薽錱轃鍖鍼籈鱵屒诊抮枕姫弫昣胗轸畛疹眕袗紾聄萙裖覙診軫嫃缜稹駗縝縥辴鬒黰圳阵纼侲", "Z挋陣鸩振朕栚紖眹赈塦揕絼蜄敶誫賑鋴镇震鴆鎭鎮黮凧争佂姃征怔爭峥挣炡狰烝眐钲埩崝崢掙猙睁聇铮媜揁筝徰睜蒸鉦徴箏徵踭篜錚鬇癥氶抍糽拯掟塣晸愸撜整正证诤郑帧政症幀証鄭諍", "Z鴊證之支卮汁芝吱巵汥枝知织肢徔栀祗秓秖胑胝衹衼倁疷祬秪脂隻梔戠椥臸搘禔綕榰蜘馶鳷謢鴲織蘵鼅禵执侄坧直姪値值聀釞埴執职植殖禃絷跖瓡墌摭馽嬂慹漐踯樴膱縶職蟙蹠蹢軄躑止", 
    	                                                 "Z只凪劧旨阯址坁帋扺汦沚纸芷抧祉茋咫恉指枳洔砋轵淽疻紙訨趾軹黹酯藢襧阤至芖志忮扻豸制厔垁帙帜治炙质郅俧峙庢庤挃柣栉洷祑陟娡徏挚晊桎狾秩致袟贽轾乿偫徝掷梽猘畤痔秲秷窒", "Z紩翐袠觗貭铚鸷傂崻彘智滞痣蛭骘寘廌搱滍稙稚筫置跱輊锧雉墆槜滯潌疐瘈製覟誌銍幟憄摯潪熫稺膣觯質踬鋕旘瀄緻隲駤鴙儨劕懥擲擿櫛穉螲懫贄櫍瓆觶騭鯯礩豑騺驇躓鷙鑕豒中伀汷刣", "Z妐彸迚忠泈炂终柊盅衳钟舯衷終鈡幒蔠锺螤鴤螽鍾鼨蹱鐘籦肿种冢喠尰塚歱煄腫瘇種踵仲众妕狆祌茽衶重蚛偅眾堹媑筗衆諥州舟诌侜周洀洲炿诪烐珘辀郮婤徟淍矪週鸼喌粥赒輈銂賙輖霌", "Z駲嚋盩謅鵃騆譸妯轴軸碡肘帚疛菷晭睭箒鯞纣伷呪咒宙绉冑咮昼紂胄荮晝皱酎粙葤詋甃僽皺駎噣縐骤籀籕籒驟朱劯侏诛邾洙茱株珠诸猪硃袾铢絑蛛誅跦槠潴蝫銖橥諸豬駯鮢鴸瀦藸櫧櫫鼄", "Z鯺蠩竹泏竺炢笁茿烛窋逐笜舳瘃蓫燭蠋躅鱁劚孎灟斸曯欘爥蠾钃主宔拄砫罜陼渚煑煮詝嘱濐麈瞩屬囑矚伫佇住助纻芧苎坾杼注苧贮迬驻壴柱柷殶炷祝疰眝祩竚莇秼紵紸羜著蛀嵀筑註貯跓", "Z軴铸筯鉒飳馵墸箸翥樦鋳駐築篫霔麆鑄抓檛膼簻髽爪拽跩专叀専砖專鄟嫥瑼甎膞颛磚諯蟤顓鱄转孨転竱轉灷啭堟蒃瑑僎赚撰篆馔縳襈賺譔饌囀籑妆庄妝庒荘娤桩莊湷粧装裝樁糚丬壮壯状", 
    	                                                 "Z狀壵梉焋幢撞戅隹追骓椎锥錐騅鵻沝坠笍娷缀惴甀缒畷硾膇墜綴赘縋諈醊錣餟礈贅轛鑆宒迍肫窀谆諄衠准埻凖準綧訰稕卓拙炪倬捉桌棁涿棳琸窧槕穛穱蠿圴彴犳灼叕妰茁斫浊丵浞烵诼酌", "Z啄啅娺梲着斮晫椓琢斱硺窡罬撯擆斲禚劅諁諑鋜濁篧擢斀斵濯櫡謶镯鐯鵫灂蠗鐲籗鷟籱仔孖孜茊兹咨姕姿茲栥玆紎赀资崰淄秶缁谘赼嗞孳嵫椔湽滋葘辎鄑孶禌觜貲資趑锱稵緇鈭镃龇輜鼒", "Z澬諮趦輺錙髭鲻鍿鎡頾頿鯔鶅齍鰦齜籽子吇姉姊杍矷秄胏呰秭耔虸笫梓釨啙紫滓訾訿榟橴字自芓茡倳剚恣牸渍眥眦胔胾漬唨宗倧综骔堫嵏嵕惾棕猣腙葼朡椶嵸稯綜緃熧緵翪艐蝬踨踪磫豵", "Z蹤騌鬃騣鬉鬷鯮鯼鑁总偬捴惣愡揔搃傯蓗摠総縂燪總鍯鏓纵昮疭倊猔碂粽糉瘲縦錝縱邹驺诹郰陬掫菆棷棸鄒箃緅諏鄹鲰鯫黀騶齺赱走鯐奏揍媰租菹葅蒩卆足卒哫崒崪族傶稡箤踤踿镞鏃诅", "Z阻组俎爼珇祖組詛靻鎺謯劗躜鑚躦鑽繤缵纂纉籫纘钻揝攥厜朘嗺樶蟕纗嶊嘴噿濢璻枠栬絊酔晬最祽罪辠酻蕞醉嶵檇鋷錊檌穝欈尊嶟遵樽繜罇鶎鐏鳟鱒鷷僔噂撙譐捘銌昨秨莋捽椊葃稓筰鈼", "Z左佐繓作坐阼岝岞怍侳柞祚胙唑座袏做葄蓙飵糳咗"};
    	

    
    private void writeLog(String infos)
    {
    	try
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).info(infos);
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    }
    	
    private void writeLog(Exception ex)
    {
    	try
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).info(ex);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		PosLog.getLog(this.getClass().getSimpleName()).error(e);
    	}
    }
    
    /**
     * 判断是否为数字
     * @param value
     * @return
     */
    private boolean isDigit(String value)
    {
    	try
    	{
    		if (value == null || value.length()<=0) { return true; }
			int sz = value.length();
			for (int i = 0; i < sz; i++)
			{
				if (Character.isDigit(value.charAt(i)) == false) { return false; }
			}  
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
		return true;
    }
    
    private boolean checkNational(String nationalID)
    {
    	try
    	{
    		NationalityDef n;
    		for (int i=0; i<this.vecNational.size(); i++)
    		{
    			n = (NationalityDef)vecNational.elementAt(i);
    			if (n==null) continue;
    			if (n.PCRCODE.equalsIgnoreCase(nationalID)) return true;
    		}
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    	return false;
    }
    
    //判断离境顾客能否即购即提 add 2014.2.14
    private boolean checkJGJT_LJ(String gklb)
    {
    	try
    	{
    		if (gklb!=null && gklb.equalsIgnoreCase(Zmsy_StatusType.ZMSY_GKTYPE_LJ) && saleBS.saleHead.str8!=null && saleBS.saleHead.str8.length()>=2 && saleBS.saleHead.str8.charAt(1)=='Y')
    		{
    			//离境顾客不能即购即提
    			PosLog.getLog(this.getClass().getSimpleName()).info("离境顾客不能即购即提,gklb=" + gklb + ",str8=" + String.valueOf(saleBS.saleHead.str8));
    			this.showMsg("操作失败:【离境顾客】不能【即购即提】");
    			return false;
    		}
    		return true;
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			this.showMsg("操作失败,判断离境顾客能否即购即提时异常：" + ex.getMessage());
			return false;
    	}
    }
}

