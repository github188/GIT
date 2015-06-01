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
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * 单品库存查询表 原RptKCForm
 * @author MAXUN
 *
 */

public class RptDPKCForm
{

	private Button button_query;
	private Text text_code;
	private PosTable table;
	private int currow = 0;	
	private Shell shell = null;	    
    private SaleBS saleBS;    
    
    
    public RptDPKCForm()
	{
    	
	}
    
    public RptDPKCForm(SaleBS saleBS)
	{
    	this.saleBS = saleBS;
	}
	public void GwkInfoEvent(RptDPKCForm info)
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
    	new RptDPKCForm(null).open();
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
    	shell.setText("单品库存查询表");
    	shell.setLayout(new FormLayout());
    	shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
    	shell.setSize(626, 568);

    	table = new PosTable(shell, SWT.BORDER|SWT.FULL_SELECTION);
    	table.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
    	final FormData fd_table = new FormData();
    	fd_table.right = new FormAttachment(0, 610);
    	fd_table.left = new FormAttachment(0, 10);
    	table.setLayoutData(fd_table);
    	table.setLinesVisible(true);
    	table.setHeaderVisible(true);
    	
    	loadEvent();
    	

    	final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn.setWidth(54);//66
    	newColumnTableColumn.setText("序号");

    	final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_3.setWidth(100);//153
    	newColumnTableColumn_3.setText("商品编码");

    	final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_4.setWidth(193);
    	newColumnTableColumn_4.setText("中文名称");

    	Label label;
    	label = new Label(shell, SWT.NONE);
    	fd_table.bottom = new FormAttachment(label, -5, SWT.TOP);

    	final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_1.setWidth(132);
    	newColumnTableColumn_1.setText("柜组");

    	final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_5.setWidth(100);
    	newColumnTableColumn_5.setText("库存数量");
    	newColumnTableColumn_5.setAlignment(SWT.RIGHT);

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
    	fd_group.bottom = new FormAttachment(0, 65);
    	fd_group.right = new FormAttachment(0, 610);
    	fd_group.top = new FormAttachment(0, 11);
    	fd_group.left = new FormAttachment(table, 0, SWT.LEFT);
    	group.setLayoutData(fd_group);


    	final Label label_1 = new Label(group, SWT.NONE);
    	label_1.setFont(SWTResourceManager.getFont("", 16, SWT.NONE));
    	label_1.setBounds(8, 23, 163, 21);
    	label_1.setText("商品条码或编码：");

    	text_code = new Text(group, SWT.BORDER);
    	text_code.setFont(SWTResourceManager.getFont("", 18, SWT.NONE));
    	text_code.setBounds(177, 17, 208, 30);
    	button_query = new Button(group, SWT.NONE);    	
    	button_query.setFont(SWTResourceManager.getFont("", 16, SWT.NONE));
    	button_query.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			queryData();
    		}
    	});
    	button_query.setBounds(484, 18, 88, 30);
    	button_query.setText("查  询");
        
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
		this.text_code.setFocus();
	}
	
	private void queryData()
	{

		if (this.text_code.getText().trim().length()<=0)
		{
			new MessageBox("条码不能为空!");
			return;
		}
		
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
	        pb.setText("正在查询单品库存信息,请等待...");
	        
			this.table.removeAll();			
			
			Vector vecKC = new Vector();
			if (!getKCResult(this.text_code.getText().trim(), vecKC))
			{
				new MessageBox("查询失败!");
				return;
			}
			if (vecKC==null || vecKC.size()<=0)
			{
				new MessageBox("未找到数据!");
				return;
			}
			
			RptDef rpt;
			String[] row;
			for (int i=0; i<vecKC.size(); i++)
			{
				rpt = (RptDef)vecKC.elementAt(i);
				if (rpt==null) continue;
				
				row = new String[5];
 				row[0]=String.valueOf(i+1);//序号
 				row[1]=rpt.goodscode;//商品编码
 				row[2]=rpt.goodsname;//商品名称
 				row[3]=rpt.gzname;//柜组名称
 				row[4]=String.valueOf(rpt.kcsl);//库存数量
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
	
	private boolean getKCResult(String code, Vector vecKC)
	{
		try
		{
			return ((Zmsy_NetService)NetService.getDefault()).getRPT_KC(code, vecKC);			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("条码不能为空!");
			return false;
		}
	}
}
