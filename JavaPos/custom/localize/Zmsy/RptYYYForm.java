package custom.localize.Zmsy;


import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * 营业员报表
 *
 */

public class RptYYYForm
{

	private Button button_query;
	private Text text_code;
	private PosTable table;
	private int currow = 0;	
	private Shell shell = null;	    
    private SaleBS saleBS;    
    
    
    public RptYYYForm()
	{
    	
	}
    
    public RptYYYForm(SaleBS saleBS)
	{
    	this.saleBS = saleBS;
	}
	public void GwkInfoEvent(RptYYYForm info)
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
	
	        Zmsy_NewKeyListener_RPT key = new Zmsy_NewKeyListener_RPT();
	        key.event = event;
	        key.inputMode = key.inputMode;
	
	        table.addKeyListener(key);
	        text_code.addKeyListener(key);
	        table.addKeyListener(key);
	        button_query.addKeyListener(key);
	        
	        this.text_code.setFocus();
	        
		}catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}
    
	/* Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
    	new RptYYYForm(null).open();
    }
    	
    public void open()
    {
    	try
    	{
    		
    		final Display display = Display.getDefault();
	        createContents();
	        	        
	        // 创建触屏操作按钮栏 
	        ControlBarForm.createMouseControlBar(this,shell);
	
	        GwkInfoEvent(this);
	        
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
    		ex.printStackTrace();
    	}
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
    	shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);//定设定窗体的风格
    	shell.setText("[" + GlobalInfo.posLogin.gh + "]营业员报表");
    	shell.setLayout(new FormLayout());
    	shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
    	shell.setSize(737, 568);

    	table = new PosTable(shell, SWT.BORDER|SWT.FULL_SELECTION);
    	table.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
    	final FormData fd_table = new FormData();
    	fd_table.right = new FormAttachment(0, 720);
    	fd_table.left = new FormAttachment(0, 10);
    	table.setLayoutData(fd_table);
    	table.setLinesVisible(true);
    	table.setHeaderVisible(true);
    	
    	loadEvent();
    	

    	final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn.setWidth(74);//66
    	newColumnTableColumn.setText("营业员");

    	final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_3.setWidth(115);//153
    	newColumnTableColumn_3.setText("销售金额");
    	newColumnTableColumn_3.setAlignment(SWT.RIGHT);

    	final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_4.setWidth(74);
    	newColumnTableColumn_4.setText("笔数");
    	newColumnTableColumn_4.setAlignment(SWT.RIGHT);

    	final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_1.setWidth(100);
    	newColumnTableColumn_1.setText("折扣金额");
    	newColumnTableColumn_1.setAlignment(SWT.RIGHT);

    	final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_5.setWidth(115);
    	newColumnTableColumn_5.setText("退货金额");
    	newColumnTableColumn_5.setAlignment(SWT.RIGHT);
    	
    	final TableColumn newColumnTableColumn_6 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_6.setWidth(94);
    	newColumnTableColumn_6.setText("笔数");
    	newColumnTableColumn_6.setAlignment(SWT.RIGHT);

    	final TableColumn newColumnTableColumn_7 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_7.setWidth(100);
    	newColumnTableColumn_7.setText("折扣金额");
    	newColumnTableColumn_7.setAlignment(SWT.RIGHT);

    	Label label;
    	label = new Label(shell, SWT.NONE);
    	fd_table.bottom = new FormAttachment(label, -5, SWT.TOP);
    	
    	new TableColumn(table, SWT.NONE);
    	final FormData fd_label = new FormData();
    	fd_label.bottom = new FormAttachment(0, 530);
    	fd_label.top = new FormAttachment(0, 515);
    	fd_label.right = new FormAttachment(table, 460, SWT.LEFT);
    	fd_label.left = new FormAttachment(table, 0, SWT.LEFT);
    	label.setLayoutData(fd_label);
    	label.setText("提示：按【付款键】开始查询，按【ESC键】退出界面，按【上/下键】翻看信息");

    	Group group;
    	group = new Group(shell, SWT.NONE);
    	fd_table.top = new FormAttachment(group, 5, SWT.BOTTOM);
    	group.setText("查询条件");
    	final FormData fd_group = new FormData();
    	fd_group.right = new FormAttachment(table, 0, SWT.RIGHT);
    	fd_group.bottom = new FormAttachment(0, 65);
    	fd_group.top = new FormAttachment(0, 11);
    	fd_group.left = new FormAttachment(table, 0, SWT.LEFT);
    	group.setLayoutData(fd_group);


    	final Label label_1 = new Label(group, SWT.NONE);
    	label_1.setFont(SWTResourceManager.getFont("", 16, SWT.NONE));
    	label_1.setBounds(35, 23, 171, 21);
    	label_1.setText("日期(YYYYMMDD):");

    	text_code = new Text(group, SWT.BORDER);
    	text_code.setFont(SWTResourceManager.getFont("", 22, SWT.NONE));
    	text_code.setBounds(214, 19, 143, 30);
    	button_query = new Button(group, SWT.NONE);    	
    	button_query.setFont(SWTResourceManager.getFont("", 16, SWT.NONE));
    	button_query.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			queryData();
    		}
    	});
    	button_query.setBounds(397, 20, 88, 30);
    	button_query.setText("查  询");
        
    	initData();
    }
    
    public void keyPressed(KeyEvent e, int key)
    {
    	try
    	{
    		switch (key)
            {
	            case GlobalVar.ArrowUp:
	                	table.moveUp();
                        currow = table.getSelectionIndex();
                        break;
	            case GlobalVar.ArrowDown:
	            		table.moveDown();
                        currow = table.getSelectionIndex();
                        break;
            }
    	}
    	catch (Exception ex)
    	{
        	PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    }
    
    public void keyReleased(KeyEvent e, int key)
    {
        try
        {
            switch (key)
            {
                case GlobalVar.Exit:
                    shell.close();
                    shell.dispose();
                    shell = null;

                    break;
                    
                case GlobalVar.Enter:
                case GlobalVar.Pay:
                	//开始查询
                	queryData();
                	e.data="";
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
    
//	加载事件
	private void loadEvent()
	{
		try
		{
			if (table!=null)
			{
				table.addListener(SWT.MeasureItem, new Listener() 
		        {    
		        	//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
		            public void handleEvent(Event event) 
		            { 
		                event.width = table.getGridLineWidth();    										//设置宽度 
		                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 1.8);	//设置高度为字体高度的2倍 
		            }
		        });
			}
		}
		catch(Exception ex)
		{
        	PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
	}	

	private void initData()
	{
		this.text_code.setText(ManipulateDateTime.staticGetDateBySlash().replace("/", ""));
		this.text_code.setFocus();
		this.text_code.selectAll();
	}
	
	private void queryData()
	{
		this.table.removeAll();	
		
		String strCode = this.text_code.getText().trim();

		if (strCode.length()<=0)
		{
			new MessageBox("日期不能为空!");
			return;
		}
		if (strCode.length()!=8)
		{
			new MessageBox("日期格式不正确,比如20130908");
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(strCode);
		sb.insert(6, "-").insert(4, "-");
		if (!ManipulateDateTime.checkDate(sb.toString()))
		{
			sb=null;
			new MessageBox("日期内容输入不正确:" + strCode);
			return;
		}
		sb=null;
		
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
	        pb.setText("正在查询,请等待...");
	        			
			Vector vecKC = new Vector();
			if (!getKCResult(strCode, vecKC))
			{
				new MessageBox("查询失败!");
				return;
			}
			if (vecKC==null || vecKC.size()<=0)
			{
				new MessageBox("未找到数据!");
				return;
			}
			
			RptYYYDef rpt;
			String[] row;
			for (int i=0; i<vecKC.size(); i++)
			{
				rpt = (RptYYYDef)vecKC.elementAt(i);
				if (rpt==null) continue;
				
				row = new String[7];
 				row[0]=String.valueOf(rpt.yyyh).trim();
 				row[1]=ManipulatePrecision.doubleToString(rpt.saleje);
 				row[2]=String.valueOf(Convert.toLong(String.valueOf(rpt.salebs)));;
 				row[3]=ManipulatePrecision.doubleToString(rpt.salezk);
 				
 				row[4]=ManipulatePrecision.doubleToString(rpt.thje);
 				row[5]=String.valueOf(Convert.toLong(String.valueOf(rpt.thbs)));;
 				row[6]=ManipulatePrecision.doubleToString(rpt.thzk);
 				table.addRow(row);
			}
			table.setSelection(0);
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		finally
		{
			if (pb != null)
            {
                pb.close();
                pb = null;
            }
			
			this.text_code.setFocus();
		}
	}
	
	private boolean getKCResult(String rq, Vector vecKC)
	{
		try
		{
			return ((Zmsy_NetService)NetService.getDefault()).getRPT_YYY(ConfigClass.CashRegisterCode, GlobalInfo.posLogin.gh, rq, "", "", "", vecKC);			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("日期不能为空!");
			return false;
		}
	}
}
