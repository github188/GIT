package custom.localize.Zmjc;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * 本地挂单解挂列表
 * @author wy
 *
 */
public class frmReadHang {

	private Vector vecGDList;
	private Text text_gdno;
	protected Shell shell;
	private PosTable posTable_head;
	private PosTable posTable_detail;
	
	//private Vector head;
	//private Vector detail;
	private Image bkimg;
	
	private boolean isExistsHang;
	private int gdno=-1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			frmReadHang window = new frmReadHang();
			window.open(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isReadOK()
	{
		return isExistsHang;
	}
	
	public int getGDNO()
	{
		return gdno;
	}

	/**
	 * Open the window.
	 */
	public void open(Vector vecGDList) {
		Display display = Display.getDefault();
		this.vecGDList = vecGDList;
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.APPLICATION_MODAL | SWT.CENTER | SWT.TITLE|SWT.CLOSE);		
		shell.setSize(850, 517);//640
		shell.setText(Language.apply("(今天)本机挂单列表"));
		shell.setLayout(null);
		
		//窗口居中显示
		Monitor primary = shell.getMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        shell.setLocation(x, y);
		
		posTable_head = new PosTable(shell, SWT.NONE|SWT.FULL_SELECTION | SWT.BORDER);
		posTable_head.setLinesVisible(true);
		posTable_head.setHeaderVisible(true);
		posTable_head.setBounds(10, 46, 828, 174);
		
		posTable_detail = new PosTable(shell, SWT.NONE|SWT.FULL_SELECTION | SWT.BORDER);
		posTable_detail.setLinesVisible(true);
		posTable_detail.setHeaderVisible(true);
		posTable_detail.setBounds(10, 234, 828, 228);
				
		// 加载背景图片
        bkimg = ConfigClass.changeBackgroundImage(this,shell,null);

		final Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 16, SWT.BOLD));
		label.setText(Language.apply("挂单编号:"));
		label.setBounds(10, 13, 101, 22);

		text_gdno = new Text(shell, SWT.BORDER);
		text_gdno.setFont(SWTResourceManager.getFont("", 20, SWT.BOLD));
		text_gdno.setBounds(117, 6, 214, 32);

		loadTable();//初始化table列
		loadEvent();//初始化table事件
		
		 // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
		//加载挂单数据
		loadData();
		text_gdno.forceFocus();
		
	}
	
	//初始化table
	private void loadTable()
	{
		try
		{
			String[] title = { Language.apply("挂单编号"), Language.apply("挂单时间"), Language.apply("收银员号"), Language.apply("交易类型"), Language.apply("交易金额") };
			int[] width = { 80, 150, 120, 120, 128 };
			posTable_head.setTitle(title);
			posTable_head.setWidth(width);
			posTable_head.initialize();
			
			title = new String[] { Language.apply("序号"), Language.apply("营业员"),Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("数量"), Language.apply("单位"), Language.apply("零售价"), Language.apply("折扣"), Language.apply("应收金额") };
			width = new int[]{ 40, 60,90, 110, 80, 60, 120,100,140 };
			posTable_detail.setTitle(title);
			posTable_detail.setWidth(width);
			posTable_detail.initialize();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//加载事件
	private void loadEvent()
	{
		try
		{				

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
	        text_gdno.addKeyListener(key);
	        posTable_head.addKeyListener(key);
	        posTable_detail.addKeyListener(key);
			
			if (posTable_head!=null)
			{
				posTable_head.addListener(SWT.MeasureItem, new Listener() 
		        {    
		        	//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
		            public void handleEvent(Event event) 
		            { 
		                event.width = posTable_head.getGridLineWidth();    										//设置宽度 
		                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 1.8);	//设置高度为字体高度的2倍 
		            }
		        });
			}
			
			if (posTable_detail!=null)
			{
				posTable_detail.addListener(SWT.MeasureItem, new Listener() 
		        {    
		        	//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
		            public void handleEvent(Event event) 
		            { 
		                event.width = posTable_detail.getGridLineWidth();    										//设置宽度 
		                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 1.8);	//设置高度为字体高度的2倍 
		            }
		        });
			}
			
			// 鼠标事件
			posTable_head.addMouseListener(new MouseAdapter()
	        {
	            public void mouseDoubleClick(MouseEvent mouseevent)
	            {
	            	
	            }
	                
	            public void mouseDown(MouseEvent mouseevent) 
	            {          	
	            	
	            	      	
	            }

				public void mouseUp(MouseEvent e) {
					if (posTable_head.getSelectionIndex() >= 0)
	            	{
		            	String[] row = posTable_head.changeItemVar(posTable_head.getSelectionIndex());		            	
		            	loadDetail(row[0]);
	            	}
				}
	            
	            
	        });
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void keyReleased(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.Enter:            	
            	selectGD();
                break;
        
            case GlobalVar.Exit:
            	isExistsHang=false;
                shell.close();
                shell.dispose();

                break;
        }
    }
	
	public void keyPressed(KeyEvent e, int key)
    {
        switch (key)
        {
            case GlobalVar.ArrowUp:
            	posTable_head.moveUp();
                
                if (posTable_head.getSelectionIndex() >= 0)
                {
                	String[] ax = posTable_head.changeItemVar(posTable_head.getSelectionIndex());
                	text_gdno.setText(ax[0]);
                	text_gdno.forceFocus();
                	text_gdno.selectAll();
                	loadDetail(ax[0]);
                }
                break;

            case GlobalVar.ArrowDown:
            	posTable_head.moveDown();
                
                if (posTable_head.getSelectionIndex() >= 0)
                {
                	String[] ax = posTable_head.changeItemVar(posTable_head.getSelectionIndex());
                	text_gdno.setText(ax[0]);
                	text_gdno.forceFocus();
                	text_gdno.selectAll();
                	loadDetail(ax[0]);
                }
                break;
            case GlobalVar.PageDown:
            	if (posTable_head != null) posTable_head.moveDown();
            	break;
            case GlobalVar.PageUp:
            	if (posTable_head != null) posTable_head.moveUp();
            	break;
        }
    }
	
	private void loadData()
	{
		try
		{
			Vector vecGD;
			Vector vecHead = new Vector();
			for(int i=0; i<this.vecGDList.size(); i++)
			{				
				vecGD = (Vector)vecGDList.elementAt(i);
				if (vecGD==null || vecGD.size()<1) continue;
				
				//头
				String[] head = (String[])vecGD.elementAt(0);
				vecHead.add(head);
				
				//明细
				Vector detail = (Vector)vecGD.elementAt(1);
				if(i==0)
				{
					//加载第一行明细
					loadDetail(detail);
					text_gdno.setText(head[0]);
					text_gdno.selectAll();
				}
				
			}
			posTable_head.exchangeContent(vecHead);
			if (posTable_head.getItemCount()>0)posTable_head.setSelection(0);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void loadDetail(String strGDNO)
	{
		if (strGDNO==null || strGDNO.length()<=0) return;
		
		try
		{
			Vector vecGD;
			for(int i=0; i<this.vecGDList.size(); i++)
			{				
				vecGD = (Vector)vecGDList.elementAt(i);
				if (vecGD==null || vecGD.size()<1) continue;
				
				//头
				String[] head = (String[])vecGD.elementAt(0);
				if(head==null || head.length<1) continue;
				if(strGDNO.equalsIgnoreCase(head[0]))
				{
					loadDetail((Vector)vecGD.elementAt(1));
					break;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void loadDetail(Vector detail)
	{
		if(detail==null || detail.size()<=0) return;
		try
		{
			Vector vecDetail = new Vector();
			SaleGoodsDef goods;
			for(int i=0; i<detail.size(); i++)
			{
				goods = (SaleGoodsDef)detail.elementAt(i);
				if (goods==null)continue;
				String[] row = new String[9];
				row[0] = String.valueOf(goods.rowno);
				row[1] = goods.yyyh;
				row[2] = goods.code;
				row[3] = goods.name;
				row[4] = String.valueOf(goods.sl);
				row[5] = goods.unit;
				row[6] = ManipulatePrecision.doubleToString(goods.lsj);
				row[7] = ManipulatePrecision.doubleToString(goods.hjzk);
				row[8] = ManipulatePrecision.doubleToString(goods.hjje-goods.hjzk);

				
				vecDetail.add(row);
			}
			posTable_detail.exchangeContent(vecDetail);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	private void selectGD()
	{
		try
		{
			String strGDNO = text_gdno.getText();
			if (strGDNO.trim().length()<=0)
			{
				new MessageBox(Language.apply("请输入挂单编号"));
				return;
			}
			
			//检查本地是否存在此挂单号
			if(!checkGDNO(strGDNO)) return;
			
			//填充挂单明细
			loadDetail(strGDNO);
			
			//提示是否解挂
			if (new MessageBox(Language.apply("挂单已经找到，是否解挂？"), null, true).verify() != GlobalVar.Key1)
			{
				this.gdno=-1;	
				return;
			}			
			isExistsHang=true;

			//关闭窗口
			shell.close();
            shell.dispose();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			new MessageBox(Language.apply("选择挂单信息时异常:") + ex.getMessage());
		}
	}
	
	private boolean checkGDNO(String strGDNO)
	{
		try
		{
			gdno = -1;
			Vector vecGD;
			for(int i=0; i<this.vecGDList.size(); i++)
			{				
				vecGD = (Vector)vecGDList.elementAt(i);
				if (vecGD==null || vecGD.size()<1) continue;
				
				//头
				String[] head = (String[])vecGD.elementAt(0);
				if(head==null || head.length<1) continue;
				if(strGDNO.equalsIgnoreCase(head[0]))
				{
					gdno = Convert.toInt(strGDNO);
					return true;
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
		
	}
	 
}
