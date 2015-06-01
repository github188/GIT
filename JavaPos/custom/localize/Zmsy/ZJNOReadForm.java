package custom.localize.Zmsy;


import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * 暂时未用
 * @author yw
 *
 */
public class ZJNOReadForm
{

	private Text text_zjno;
	private Label label_Input_Msg;	
	private PosTable table;
	
	private Shell shell = null; 
	
	private Vector vecZJType;
	private GwkDef gwk;
	private Zmsy_SaleBS saleBS;
	
	private boolean isRead;
	
	private String cardNo;
	private String cardName;
	    
    public ZJNOReadForm()
	{
    	
	}
    
    public ZJNOReadForm(SaleBS saleBS)
	{ 
    	if(saleBS!=null)
    	{
    		this.saleBS = (Zmsy_SaleBS)saleBS;
        	this.gwk = this.saleBS.getGwk();
    	}
    	
	}
	public void ZJNOReadFormEvent(ZJNOReadForm info)
	{
		
		try
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
	                keyPressed(e, key);
	            }
	
	            public void keyUp(KeyEvent e, int key)
	            {
	                keyReleased(e, key);
	            }
	        };
	
	        NewKeyListener key = new NewKeyListener();
	        key.event = event;
	        key.inputMode = key.inputMode;
	
	        table.addKeyListener(key);
	        this.text_zjno.addKeyListener(key);
		}catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}
    
	public boolean getIsRead()
	{
		return isRead;
	}
	
	public String getCardNo()
	{
		return this.cardNo;
	}
	
	public String getCardName()
	{
		return this.cardName;
	}
	
	/* Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
    	new ZJNOReadForm().open();
    }
    private void getTestData()
    {
    	if (this.vecZJType == null) this.vecZJType = new Vector();
    	if (saleBS!=null) saleBS.getZJType(vecZJType);
    	/*ZJTypeDef zj=new ZJTypeDef();
    	zj.zjid="1";
    	zj.zjname="护照";
    	vecZJType.add(zj);
    	
    	zj=new ZJTypeDef();
    	zj.zjid="2";
    	zj.zjname="身份证";
    	vecZJType.add(zj);
    	
    	zj=new ZJTypeDef();
    	zj.zjid="3";
    	zj.zjname="通行证";
    	vecZJType.add(zj);
    	
    	zj=new ZJTypeDef();
    	zj.zjid="4";
    	zj.zjname="台胞证";
    	vecZJType.add(zj);
    	
    	zj=new ZJTypeDef();
    	zj.zjid="5";
    	zj.zjname="其它";
    	vecZJType.add(zj);*/
    	
    }
    public void init()
    {
    	 try
         {
    		 getTestData();
			 this.table.clearAll();
    		 if (this.vecZJType != null && vecZJType.size()>0)
    		 {
    			ZJTypeDef zj;
    			String[] row;
     			for(int i=0; i<vecZJType.size(); i++)
     			{
     				zj = (ZJTypeDef) vecZJType.elementAt(i);
     				if (zj==null)continue;
     				row = new String[2];
     				row[0]=zj.zjid;
     				row[1]=zj.zjname;
     				table.addRow(row);
     				
     				if (i==0) table.setSelection(0);
     				if (gwk!=null && gwk.zjlb!=null)
     				{
     					if (gwk.zjlb.trim().equalsIgnoreCase(zj.zjid.trim()))
     					{
     						//默认选择当前证件类型及证件号
     		     			table.setSelection(i);
     		     			this.text_zjno.setText("");
     		     			if (gwk.passport!=null) this.text_zjno.setText(gwk.passport);
     					}
     				}
     			}

     			showTips();
    		 }
    		 else
    		 {
    			 this.label_Input_Msg.setText("证件类型获取失败,当前无法录入证件号！");
    			 new MessageBox(this.label_Input_Msg.getText());
    		 }
    		
         }
         catch (Exception e)
         {
        	 PosLog.getLog(this.getClass().getSimpleName()).error(e);
         }
		 this.text_zjno.setFocus();
    }
	
    /**
     * 购物卡信息
     * @param parent
     * @return
     */
    public void open()
    {
    	try
    	{
    		final Display display = Display.getDefault();
	        createContents();
	        
	        init();
	        
	        // 创建触屏操作按钮栏 
	        ControlBarForm.createMouseControlBar(this,shell);
	
	        ZJNOReadFormEvent(this);
	        
	        // 加载背景图片
	        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
	
	        // 创建对应的界面控制对象
	        
	        if (!shell.isDisposed())
	        {	        
	        	shell.layout();
		        shell.open();
		        shell.setActive();
		        shell.forceActive();
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
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
    	shell = new Shell(SWT.BORDER | SWT.CLOSE | SWT.CENTER |SWT.APPLICATION_MODAL);//定设定窗体的风格
    	shell.setSize(640, 413);
    	shell.setText("证件号码录入");
    	
    	table = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, true);
    	table.setFont(SWTResourceManager.getFont("宋体", 13, SWT.NONE));
    	table.setBounds(10, 45, 293, 165);
    	
    	 table.addListener(SWT.MeasureItem, new Listener() 
         {    
         	//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
             public void handleEvent(Event event) 
             { 
                 event.width = table.getGridLineWidth();    										//设置宽度 
                 event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 1.2);	//设置高度为字体高度的2倍 
             }
         });
    	
    	TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("序号");
		col.setWidth(60);
		

		col = new TableColumn(table, SWT.NONE);
		col.setText("证件名称");
		col.setWidth(200);

    	text_zjno = new Text(shell, SWT.BORDER|SWT.MULTI);
    	text_zjno.setFont(SWTResourceManager.getFont("", 18, SWT.NONE));
    	text_zjno.setBounds(10, 253, 614, 118);

    	final Group group = new Group(shell, SWT.NONE);
    	group.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	group.setText("操作说明");
    	group.setBounds(322, 22, 302, 188);

    	final Label label_1 = new Label(group, SWT.NONE);
    	label_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
    	label_1.setText("1、按【上/下光标键】，选择证件名称");
    	label_1.setBounds(10, 28, 282, 17);

    	final Label label_1_1 = new Label(group, SWT.NONE);
    	label_1_1.setBounds(10, 59, 282, 17);
    	label_1_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
    	label_1_1.setText("2、按【左/右光标键】，移动光标位置");

    	final Label label_1_1_1 = new Label(group, SWT.NONE);
    	label_1_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	label_1_1_1.setBounds(10, 90, 282, 17);
    	label_1_1_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
    	label_1_1_1.setText("3、按【数量键】，从设备读取数据");

    	final Label label_1_1_1_1 = new Label(group, SWT.NONE);
    	label_1_1_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	label_1_1_1_1.setBounds(10, 123, 282, 17);
    	label_1_1_1_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
    	label_1_1_1_1.setText("4、按【付款键】，确认当前证件号");

    	final Label label_1_1_1_1_1 = new Label(group, SWT.NONE);
    	label_1_1_1_1_1.setBounds(10, 154, 282, 17);
    	label_1_1_1_1_1.setFont(SWTResourceManager.getFont("", 11, SWT.NONE));
    	label_1_1_1_1_1.setText("5、按【退出键】，取消并退出录入");

    	final Label label = new Label(shell, SWT.NONE);
    	label.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	label.setText("证件列表:");
    	label.setBounds(10, 18, 352, 21);

    	label_Input_Msg = new Label(shell, SWT.NONE);
    	label_Input_Msg.setForeground(SWTResourceManager.getColor(255, 0, 0));
    	label_Input_Msg.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
    	label_Input_Msg.setText("请输入【】号码，或从设备上读取");
    	label_Input_Msg.setBounds(10, 222, 614, 26);
        
    }
    
    public void keyPressed(KeyEvent e, int key)
    {
    	try
    	{
    		switch (key)
            {
	            case GlobalVar.ArrowUp:
	            	table.moveUp();
	            	showTips();
	
	                break;
	            case GlobalVar.ArrowDown:
	            	table.moveDown();
	            	showTips();
	            break;
            }
    	}
    	catch (Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    }
    
    private void showTips()
    {
    	this.text_zjno.setText("");
    	String[] row = getRowZJ();//table.changeItemVar(table.getSelectionIndex());
    	if (row==null || row.length<=0) 
    	{
    		this.text_zjno.setText("");
    		return;
    	}
    	String str = "";
    	if (row[0].equals(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
    	{
    		str = "，或从设备上读取";
    	}
    	this.label_Input_Msg.setText("请输入【" + row[1] + "】号码" + str);
    	if(row[0].equalsIgnoreCase(gwk.zjlb)) this.text_zjno.setText(gwk.passport);
    	this.text_zjno.setFocus();
    	text_zjno.setSelection(text_zjno.getText().length());
    	this.text_zjno.selectAll();
    }
    
        
    public void keyReleased(KeyEvent e, int key)
    {
        try
        {
            switch (key)
            {
                case GlobalVar.Exit:
                	//退出证件号的录入
                	isRead = false;
                	closeForm();

                    break;
                    
                case GlobalVar.Quantity:
                	//从设备上读取                	
                	readZJNO();
                	break;
                	
                case GlobalVar.Pay:
                	//确认当前证件号的录入,并获取当前的证件号码
                	if (getZJNO())
                	{
                		isRead = true;
                    	closeForm();
                	}
                	
                	break;
                	
                default:
                	break;
            }
        }
        catch (Exception ex)
        {
        	PosLog.getLog(this.getClass().getSimpleName()).error(ex);
        }
    }
    
    /**
     * 当按数量键时,从设备端获取数据,并赋值以证件输入框内
     */
    private void readZJNO()
    {
    	try
    	{
    		String[] row = getRowZJ();//table.changeItemVar(table.getSelectionIndex());
    		if (row == null || row.length<=0)
    		{
    			new MessageBox("失败：证件类型获取失败,当前无法录入证件号！");
    			return;
    		}
    		if (row[0].equals(Zmsy_StatusType.ZMSY_ZJTYPE_SFZ))
    		{
    			//身份证
    			String sfz = ReadZJNO.getSfzInfo();
    			if (sfz.startsWith("0"))
    			{
    				//sfz=sfz.substring(1);
    				String[] arr = sfz.split(",");
    				if (arr.length>=6)
    				{
    					this.text_zjno.setText(arr[6]);
    				}
    				else
    				{
    					new MessageBox("获取失败：设备返回的身份信息不合法[" + sfz + "]");
    				}
        			
    			}
    			else
    			{
    				new MessageBox("身份证读取失败:" + sfz);
    			}
    		}
    		
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    		new MessageBox("失败,从设备端获取证件时异常:" + ex.getMessage());
    	}
    }
    
    /**
     * 当按付款键时,从证件输入框内获取并解析证件号码
     * @return
     */
    private boolean getZJNO()
    {
    	try
    	{
    		String[] row = getRowZJ();
    		if (row==null || row.length<=0)
    		{
    			new MessageBox("失败：证件类型获取失败,当前无法录入证件号！");
    			return false;
    		}
    		String strValue = this.text_zjno.getText();
    		if (strValue.trim().length()<=0)
    		{
    			new MessageBox("失败：请先录入证件号！");
    			return false;
    		}
    		//gwk.zjlb = row[0];//证件类别
    		//gwk.passport = row[1];//证件号码
    		cardNo = row[0] + this.text_zjno.getText().trim();
    		cardName = row[1];
    		return true;
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    		return false;
    	}
    }
    
    //获取证件列表的当前选中行
    private String[] getRowZJ()
    {
    	try
    	{
    		if (table.getItemCount()<=0) return null;
        	return table.changeItemVar(table.getSelectionIndex());
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    		return null;
    	}
    	
    }
    
    //关闭窗口
    private void closeForm()
    {
    	 shell.close();
         shell.dispose();
         shell = null;
    }
    
}
