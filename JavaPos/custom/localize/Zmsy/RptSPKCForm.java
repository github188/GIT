package custom.localize.Zmsy;


import java.util.ArrayList;
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
import com.efuture.commonKit.TextEx;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * 商品库存列表 原RptKCListForm
 * @author MAXUN
 *
 */

public class RptSPKCForm
{

	private TextEx textEx_PP;
	private TextEx textEx_GZ;
	private Button button_query;
	private PosTable table;
	private int currow = 0;	
	private Shell shell = null;
    private SaleBS saleBS;  
    
    public RptSPKCForm()
	{
    	
	}
    
    public RptSPKCForm(SaleBS saleBS)
	{
    	this.saleBS = saleBS;
	}
	public void GwkInfoEvent(RptSPKCForm info)
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
	        button_query.addKeyListener(key);
	        table.addKeyListener(key);
	        textEx_GZ.addKeyListener(key);
	        textEx_PP.addKeyListener(key);
	        
	        textEx_GZ.setFocus();
	
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
    	new RptSPKCForm(null).open();
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
	        
	        initData();
	        
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
    	shell.setText("商品库存列表");
    	shell.setLayout(new FormLayout());
    	shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
    	shell.setSize(626, 568);

    	table = new PosTable(shell, SWT.BORDER|SWT.FULL_SELECTION);
    	table.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
    	final FormData fd_table = new FormData();
    	fd_table.top = new FormAttachment(0, 100);
    	fd_table.right = new FormAttachment(0, 610);
    	fd_table.left = new FormAttachment(0, 10);
    	table.setLayoutData(fd_table);
    	table.setLinesVisible(true);
    	table.setHeaderVisible(true);
    	
    	loadEvent();
    	

    	final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn.setWidth(55);
    	newColumnTableColumn.setText("序号");

    	final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_1.setWidth(112);
    	newColumnTableColumn_1.setText("柜组");

    	final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_2.setWidth(109);
    	newColumnTableColumn_2.setText("品牌");

    	final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_3.setWidth(87);
    	newColumnTableColumn_3.setText("商品编码");

    	final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_4.setWidth(120);
    	newColumnTableColumn_4.setText("中文名称");

    	Label label;
    	label = new Label(shell, SWT.NONE);
    	fd_table.bottom = new FormAttachment(label, -5, SWT.TOP);

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

    	final Group group = new Group(shell, SWT.NONE);
    	group.setText("查询条件");
    	final FormData fd_group = new FormData();
    	fd_group.bottom = new FormAttachment(table, -5, SWT.TOP);
    	fd_group.right = new FormAttachment(0, 610);
    	fd_group.top = new FormAttachment(0, 11);
    	fd_group.left = new FormAttachment(table, 0, SWT.LEFT);
    	group.setLayoutData(fd_group);

    	final Label label_1 = new Label(group, SWT.NONE);
    	label_1.setBounds(8, 26, 40, 20);
    	label_1.setText("柜组:");

    	textEx_GZ = new TextEx(group, SWT.NONE);
    	textEx_GZ.setBounds(53, 21, 327, 25);

    	textEx_PP = new TextEx(group, SWT.NONE);
    	textEx_PP.setBounds(53, 52, 327, 25);

    	Label label_2;
    	label_2 = new Label(group, SWT.NONE);
    	label_2.setBounds(8, 57, 45, 20);
    	label_2.setText("品牌:");

    	final Label label_3 = new Label(group, SWT.NONE);
    	label_3.setBounds(328, 22, 0, 12);
    	label_3.setText("Label");

    	button_query = new Button(group, SWT.NONE);    	
    	button_query.setFont(SWTResourceManager.getFont("", 16, SWT.NONE));
    	button_query.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(final SelectionEvent arg0)
    		{
    			queryData();
    		}
    	});
    	button_query.setBounds(502, 45, 88, 30);
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
                	Object ctlSource = e.getSource();
                	if (ctlSource instanceof Text)
            		{
                		Text txt = (Text)ctlSource;
                		if (txt==this.textEx_GZ.getSource())
                		{
                			if (textEx_GZ.getDoit()) return;
                		}  
                		else if (txt==this.textEx_PP.getSource())
                		{
                			if (textEx_PP.getDoit()) return;
                		}
            		}
                    shell.close();
                    shell.dispose();
                    shell = null;

                    break;
                    
                case GlobalVar.Enter:
                	//切换焦点
                	ctlSource = e.getSource();
                	if (ctlSource instanceof Text)
            		{
                		Text txt = (Text)ctlSource;
                		if (txt==this.textEx_GZ.getSource())
                		{
                			this.textEx_PP.setFocus();
                			this.textEx_PP.selectAll();
                		}
                		else if (txt==this.textEx_PP.getSource())
                		{
                			this.button_query.setFocus();
                		}
            		}
                	else if(ctlSource == this.table)
                	{
                		this.textEx_PP.setFocus();
            			this.textEx_PP.selectAll();
                	}
                	else if (ctlSource == this.button_query)
                	{
                		queryData();
                	}
                	                	
                	e.data="";
                	break;
                	
                case GlobalVar.Pay:
                	//开始查询
                	queryData();
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
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
	        pb.setText("正在读取品牌和柜组信息,请等待...");
	        
	        Zmsy_NetService net = (Zmsy_NetService)NetService.getDefault();
	        initGZ(net);
	        initPP(net);
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
			 
			 this.textEx_GZ.setFocus();
		}
	}
	
	private void initGZ(Zmsy_NetService net)
	{
		try
		{
			Vector vecGZ = new Vector();
			if (net.getRpt_GZInfo(vecGZ) && vecGZ!=null && vecGZ.size()>0)
			{
				RptDef gz;
				ArrayList items = new ArrayList(); 
				for (int i=0; i<vecGZ.size(); i++)
				{
					gz = (RptDef)vecGZ.elementAt(i);
					if (gz==null) continue;
					
					items.add(gz.gzcode + " - " + gz.gzname);
					
					items.add(GwkForm.getPY(gz.gzname) + " - " + gz.gzcode + " - " + gz.gzname);
				}
				this.textEx_GZ.setItemsSource(items.toArray());
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("柜组信息加载失败,请退出界面重试!");
		}
	}
	
	private void initPP(Zmsy_NetService net)
	{
		try
		{
			Vector vecPP = new Vector();
			if (net.getRpt_PPInfo(vecPP) && vecPP!=null && vecPP.size()>0)
			{
				RptDef pp;
				ArrayList items = new ArrayList(); 
				for (int i=0; i<vecPP.size(); i++)
				{
					pp = (RptDef)vecPP.elementAt(i);
					if (pp==null) continue;
					
					items.add(pp.ppcode + " - " + pp.ppname);
					
					items.add(GwkForm.getPY(pp.ppname) + " - " + pp.ppcode + " - " + pp.ppname);
				}
				this.textEx_PP.setItemsSource(items.toArray());
			}
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("品牌信息加载失败,请退出界面重试!");
		}
	}
	
	private void queryData()
	{
		if (this.textEx_PP.getText().trim().length()<=0)
		{
			new MessageBox("品牌不能为空!");
			this.textEx_PP.setFocus();
			return;
		}
		
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
	        pb.setText("正在查询商品库存列表信息,请等待...");
	        
			this.table.removeAll();
			
			Vector vecKCList = new Vector();
			if (!getKCListResult(getID(this.textEx_GZ.getText().trim()), getID(this.textEx_PP.getText().trim()), vecKCList))
			{
				new MessageBox("查询失败!");
				return;
			}
			
			if (vecKCList==null || vecKCList.size()<=0) 
			{
				new MessageBox("未找到数据!");
				return;
			}
			
			RptDef rpt;
			String[] row;
			for (int i=0; i<vecKCList.size(); i++)
			{
				rpt = (RptDef)vecKCList.elementAt(i);
				if (rpt==null) continue;
				
				row = new String[6];
 				row[0]=String.valueOf(i+1);//序号
 				row[1]=rpt.gzname;//柜组名称
 				row[2]=rpt.ppname;//品牌名称
 				row[3]=rpt.goodscode;//商品编码
 				row[4]=rpt.goodsname;//商品名称
 				row[5]=String.valueOf(rpt.kcsl);//库存数量
 				table.addRow(row);
			}
			table.setSelection(0);
			table.forceFocus();
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
			//this.textEx_GZ.setFocus();
			//this.textEx_GZ.selectAll();
		}
	}
	
	private boolean getKCListResult(String gz, String pp, Vector vecKC)
	{
		try
		{
			return ((Zmsy_NetService)NetService.getDefault()).getRPT_KCList(gz, pp, vecKC);			
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
			new MessageBox("条码不能为空!");
			return false;
		}
	}
	
	private String getID(String strValue)
	{		
		try
		{
			if (strValue==null || strValue.length()<=0) return "";
			
			String strID="";
			String[] arr= strValue.split("-");
			if (arr.length==3)
			{
				strID=arr[1];
			}
			else if (arr.length==2)
			{
				strID=arr[0];
			}
			else if (arr.length==1)
			{
				strID=arr[0];
			}
			else
			{
				strID="";
				PosLog.getLog(this.getClass().getSimpleName()).info("getID(" + String.valueOf(strValue) + "): 获取ID失败,ID不合法.");
			}
			return strID;
		}
		catch(Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return strValue;
	}
    
}
