package custom.localize.Zmjc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class PersoninfoForm
{
	private Combo txtCType;
	private Text txtCID;
	private Text txtPhoneNo;
	private Shell shell;
		
	private String CType = null;		//证件类型
	private String CID = null;			//身份证
	private String phoneNo = null;		//手机号
	private boolean isSave = false;      //是否保存标志
	
    public PersoninfoForm()
    {
        this.open();
    }
	
	public void open()
	{
        final Display display = Display.getDefault();
        createContents();
        
        loadEvent();
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {
            shell.layout();
            shell.open();
            shell.setActive();
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
	}
	
	
	protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setSize(367, 220);
        shell.setText("会员卡激活");
        shell.setBounds((GlobalVar.rec.x / 2) - (shell.getSize().x / 2),
                        (GlobalVar.rec.y / 2) - (shell.getSize().y / 2),
                        shell.getSize().x,
                        shell.getSize().y - GlobalVar.heightPL);

        final Label label_1 = new Label(shell, SWT.NONE);
        label_1.setBounds(13, 25, 89, 27);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_1.setText(Language.apply("证件类型:"));       
        
        final Label label_2 = new Label(shell, SWT.NONE);
        label_2.setBounds(13, 72, 89, 27);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2.setText(Language.apply("证件号码:"));

        final Label label_3 = new Label(shell, SWT.NONE);
        label_3.setBounds(13, 120, 87, 27);
        label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_3.setText("手机号码:");

        txtCType = new Combo(shell, SWT.READ_ONLY);
        txtCType.setBounds(108, 20, 220, 27);
        txtCType.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        String[] items = {"1-身份证","2-护照","3-台胞证","4-港澳通行证","5-其它证件"};   //下拉列表中条目
        txtCType.setItems(items);
        txtCType.select(0);  //默认选项
        
        txtCID = new Text(shell, SWT.BORDER);
        txtCID.setBounds(108, 68, 220, 27);
        txtCID.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtCID.setTextLimit(18);

        txtPhoneNo = new Text(shell, SWT.BORDER);
        txtPhoneNo.setBounds(108, 115, 220, 27);
        txtPhoneNo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtPhoneNo.setTextLimit(11);
        
        final Label label1 = new Label(shell, SWT.NONE);
        label1.setBounds(1, 160, 360, 22);
        label1.setForeground(SWTResourceManager.getColor(255, 0, 0));
        label1.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
        label1.setText("提示：按【付款键】确认，【退出键】退出");
    }
	
    public String getCType()
    {
        return CType;
    }
	
    public String getCID()
    {
        return CID;
    }

    public String getPhoneNo()
    {
        return phoneNo;
    }
      
    public boolean getIsSave()
    {
        return isSave;
    }
    
    private String CType()
    {
    	String[] Items = txtCType.getText().trim().split("-");
    	CType = Items[0];
        return CType;
    }
    	
	
	
	//Zmjc_PersoninfoEvent内容
	 // 设定键盘事件
    private void loadEvent()
    {

        NewKeyEvent event = new NewKeyEvent()
        {
            public void keyDown(KeyEvent e, int key)
            {
               
            }

            public void keyUp(KeyEvent e, int key)
            {
                keyReleased(e, key);
            }
        };
        
        NewKeyListener key = new NewKeyListener();
        key.event = event;

        txtCType.addKeyListener(key);
        txtPhoneNo.addKeyListener(key);
        txtCID.addKeyListener(key);
        key.inputMode = key.inputMode;
        
        init();
    }
	

    public void keyReleased(KeyEvent e, int key)
    {
    	Widget curWidget = (Widget)e.widget;
    try{
    		
    	
        switch (key)
        {
            case GlobalVar.Enter:  //enter13
            	changeCursor(e,key); 

                break;
            case GlobalVar.Pay:   //Pay93
            	if (!input(txtCType,e))
            	{
            		e.data = "focus";
            		txtCType.setFocus();
            	}
            	else if (!input(txtCID,e))
            	{
            		e.data = "focus";
            		txtCID.setFocus();
            	}
            	else if (!input(txtPhoneNo,e))
            	{
            		e.data = "focus";
            		txtPhoneNo.setFocus();
            	}
            	break;
            case GlobalVar.Exit:  //Exit27
            	isSave = false;
            	close();

                break;
            case GlobalVar.Clear:  //清除键127
            	if (e.widget.equals(this.txtCType))
            	{
            		txtCType.select(0);
            	}
            	else if (e.widget.equals(this.txtCID))
            	{
            		txtCID.setText("");
            	}
            	else if (e.widget.equals(this.txtPhoneNo))
            	{
            		txtPhoneNo.setText("");
            	}
                break;
            case GlobalVar.ArrowUp:	   
            	
            	if (curWidget.equals(txtCID))
            	{
                    e.data = "focus";
                    txtCType.setFocus();
            	}
            	else if(curWidget.equals(txtPhoneNo))
            	{
                    e.data = "focus";
                    txtCID.setFocus();
                    txtCID.selectAll();
            	}
             
            	break;
            case GlobalVar.ArrowDown:
            	
            	if (curWidget.equals(txtCID))
            	{
                    e.data = "focus";
                    txtPhoneNo.setFocus();
                    txtPhoneNo.selectAll();
            	}
            	else if(curWidget.equals(txtPhoneNo))
            	{
                    e.data = "focus";
                    txtCType.setFocus();
            	}
             
            	break;
        }
    	}        
    	catch (Exception e1)
        {
    		PosLog.getLog(getClass()).fatal(e1);
        }
    }
    
    private void close()
    {
        shell.close();
        shell.dispose();
    }
    private void init()
    {
        try
        {
            txtCType.select(0);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private boolean input(Object object,KeyEvent e)
    {
        try
        {
        	if (object == txtCType)
            {
            	if (!isValiCType(txtCType))
            	{
            		return false;
            	}
            	
                if (txtCType.getText().trim() != null)
                {
                	e.data = "";
                	txtCID.setFocus();
                	txtCID.setSelection(txtCID.getText().length());
                    return true;
                }
                else
                {
                    new MessageBox(Language.apply("请选择证件类型"), null, false);
                    txtCType.select(0);
                }
            }
        	
        	else if (object == txtCID)
            {
            	if (!isValidate(txtCID))
            	{
            		return false;
            	}
            	
                if (txtCID.getText().trim() != null)
                {
                	e.data = "";
                    txtPhoneNo.setFocus();
                    txtPhoneNo.setSelection(txtPhoneNo.getText().length());
                    return true;
                }
                else
                {
                    new MessageBox(Language.apply("请输入合法的身份证号"), null, false);
                    txtCID.selectAll();
                }
            }
            else if (object == txtPhoneNo)
            {
            	if (!isValidate(txtPhoneNo))
            	{
            		return false;
            	}
            	
                if (txtPhoneNo.getText().trim() != null)
                {
                	e.data = "";
                	txtCType.setFocus();  
                    
                    MessageBox me = new MessageBox(Language.apply("你确定输入正确吗?"), null, true);

                    if (me.verify() == GlobalVar.Key1)
                    {
                    	isSave = true;
                    	CType = CType();
                    	CID = txtCID.getText().trim();
                    	phoneNo = txtPhoneNo.getText().trim();	
                    }else if(me.verify() == GlobalVar.Key2)
                    {
                    	isSave = false;
                    	return false;
                    }
                    
                	close();                       
                    return true;
                }
                else
                {
                    new MessageBox(Language.apply("请输入合法的电话号码"), null, false);
                    txtPhoneNo.selectAll();
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        
		return false;
    }
    
    private boolean isValidate(Text text)
    {
    	String regex1="[0-9]{17}[0-9Xx]"; 
    	String regex2="[0-9]{11}"; 
//    	String regex2="^(([0-9]{3,4}-)|([0-9]{3,4}))?[0-9]{7,8}$";

            if (text == txtCID)
            {
                if ((text == null) || text.getText().trim().equals(""))
                {
                    new MessageBox(Language.apply("证件号码不能为空,请重新输入!"), null, false);

                    return false;
                }
              if(CType().equals("1"))
              {
      	        if (text.getText().trim().length() != 18)
    	        {
    	            new MessageBox(Language.apply("请检查身份证号是否为18位\n请重新输入!"), null, false);
    	
    	            return false;
    	        }
    	        if(!text.getText().trim().matches(regex1))
    	        {
    	            new MessageBox(Language.apply("不合法的输入,请检查身份证号\n请重新输入!"), null, false);
    	
    	            return false;
    	        }
              }      
              }
        
        if (text == txtPhoneNo)
        {
            if ((text == null) || text.getText().trim().equals(""))
            {
                new MessageBox(Language.apply("手机号码不能为空,请重新输入!"), null, false);

                return false;
            }
        	if (text.getText().trim().length() != 11)
	        {
	            new MessageBox(Language.apply("请检查手机号码是否为11位\n(如 150xxxxxxxx)\n请重新输入!"), null, false);
	
	            return false;
	        }
	        else if(!text.getText().trim().matches(regex2))
	        {
	            new MessageBox(Language.apply("不合法的输入,请检查手机号码\n请重新输入!"), null, false);
	
	            return false;
	        }
        }
        

        return true;
    }
    
    
    private boolean isValiCType(Combo combo)
    {
    	return true;
    }
    
    public void changeCursor(KeyEvent e, int key)
    {
    	Widget curWidget = (Widget)e.widget;
    	try
    	{
    		
    	}catch(Exception e1)
    	{
    		e1.printStackTrace();
    	}
    	
    	if (curWidget.equals(txtCType))
    	{
    		e.data = "focus";
    		txtCID.setFocus();
    		txtCID.selectAll();
    	}
    	else if (curWidget.equals(txtCID))
    	{
    		e.data = "focus";
    		txtPhoneNo.setFocus();
    		txtPhoneNo.selectAll();
    	}
    	else if (curWidget.equals(txtPhoneNo))
    	{
    		e.data = "focus";
    		txtCType.setFocus();
    	}	    	
    }
    
}
