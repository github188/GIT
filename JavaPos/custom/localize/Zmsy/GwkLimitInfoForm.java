package custom.localize.Zmsy;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class GwkLimitInfoForm
{

	private int currow = 0;
	
	private PosTable table;
	public static boolean Done = false;
	public Shell shell = null;

	protected TableItem item;

	private String cardInfo;

	private String title;

	private String limitInfo;
	private String times;
	
    public static Display display;
    
    
    
    public GwkLimitInfoForm()
	{
    	
	}
    
    public GwkLimitInfoForm(String strTitle, String strCardInfo, String strTimes, String strLimitInfo)
	{
    	this.title = strTitle;
   		this.cardInfo = strCardInfo;
   		this.times = strTimes;
    	this.limitInfo = strLimitInfo;
	}
	public void GwkInfoEvent(GwkLimitInfoForm info)
	{
		
		try
		{
				table = info.table;

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
	        key.inputMode = key.IntegerInput;
	
	        table.addKeyListener(key);
	//        table.addSelectionListener(new SelectionAdapter()
	//        {
	//			public void widgetSelected(SelectionEvent arg0)
	//			{
	//				currow = table.getSelectionIndex();
	//			}
	//        });
	//        table.addMouseListener(new MouseAdapter()
	//        {
	//            public void mouseDoubleClick(MouseEvent mouseevent) 
	//            {
	//            	currow = table.getSelectionIndex();
	//            	keyReleased(null,GlobalVar.Enter);
	//            }
	//        });
	//        txtDate.addKeyListener(key);
	//        txtFphm.addKeyListener(key);
	//        cmbDjlb.addKeyListener(key);
	//        cmbDjlb.select(0);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
    
	/* Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
    	new GwkLimitInfoForm("购物卡信息","购物卡信息","","").open();
    }
    
    public void init()
    {
    	 try
         {
    		 
//             String result =(String) Zmsy_WebService.getDefault().execute("s","inputStr",new Object[]{"01,123"},"getInformation","s");

    		 //limitInfo= "11111,22222222,333333333333,44444444444,55555555,666666,77777,.6|aaaaaaaaa,bbbbbbbbbb,cccccccccccc,dddddddddddd,eeeeeeee,ffffff,ggggggg,hhhhhh";

    		 String result = limitInfo;
             if(result != null && result.length() >0)
             {
	             String[] resultStr = result.split("\\|");
	             for(int i =0;i<resultStr.length;i++){
		 				item = new TableItem(table,SWT.NONE);
		 				if(resultStr[i] == null){
		 					break;
		 				}
	 				String[] rowData = resultStr[i].split(",");
	 				String[] row = new String[6];
	 				row[0] = rowData[0];//序号
	 				row[1] = rowData[1];//品类
	 									//rowData[2];//规格件数
	 				row[2] = rowData[3];//已购件数
	 				row[3] = rowData[4];//可购件数
										//rowData[5];//规格重量
	 				row[4] = ManipulatePrecision.doubleToString(Convert.toDouble(rowData[6]));//已购重量
	 				row[5] = ManipulatePrecision.doubleToString(Convert.toDouble(rowData[7]));//可购重量
	 				item.setText(row);
	 			}
	            table.setSelection(0);
             }
             else
             {
            	 new MessageBox("WebService 返回串为空！");
             }
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
    }
	
    /**
     * 购物卡信息
     * @param parent
     * @return
     */
    public boolean open()
    {
    	try
    	{
    		
	    	display = Display.getDefault();
	        createContents();
	        
	        init();
	        
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
        return Done;
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
    	shell = new Shell(GlobalVar.style);//定设定窗体的风格
    	shell.setLayout(new FormLayout());
    	shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
    	shell.setSize(760, 584);//640+120, 584
    	shell.setText(title);

    	final Label infoLabel = new Label(shell, SWT.CENTER);
    	final FormData fd_infoLabel = new FormData();
    	fd_infoLabel.bottom = new FormAttachment(0, 35);
    	fd_infoLabel.top = new FormAttachment(0, 15);
    	fd_infoLabel.right = new FormAttachment(0, 624);
    	fd_infoLabel.left = new FormAttachment(0, 10);
    	infoLabel.setLayoutData(fd_infoLabel);
    	infoLabel.setText(cardInfo);

    	table = new PosTable(shell, SWT.BORDER|SWT.FULL_SELECTION);
    	table.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
    	final FormData fd_table = new FormData();
    	fd_table.top = new FormAttachment(0, 75);
    	fd_table.right = new FormAttachment(0, 745);
    	fd_table.left = new FormAttachment(0, 10);
    	table.setLayoutData(fd_table);
    	table.setLinesVisible(true);
    	table.setHeaderVisible(true);
    	
    	loadEvent();
    	

    	final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn.setWidth(60);
    	newColumnTableColumn.setText("序号");

    	final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_1.setWidth(149);
    	newColumnTableColumn_1.setText("品类");

    	final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_2.setWidth(130);
    	newColumnTableColumn_2.setText("已购件数");

    	final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_3.setWidth(131);
    	newColumnTableColumn_3.setText("可购件数");

    	final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_4.setWidth(120);
    	newColumnTableColumn_4.setText("已购重量");
    	
    	final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
    	newColumnTableColumn_5.setWidth(120);
    	newColumnTableColumn_5.setText("可购重量");

    	Label label;
    	label = new Label(shell, SWT.NONE);
    	fd_table.bottom = new FormAttachment(label, -5, SWT.TOP);
    	final FormData fd_label = new FormData();
    	fd_label.bottom = new FormAttachment(100, -2);
    	fd_label.top = new FormAttachment(100, -22);
    	fd_label.right = new FormAttachment(table, 460, SWT.LEFT);
    	fd_label.left = new FormAttachment(table, 0, SWT.LEFT);
    	label.setLayoutData(fd_label);
    	label.setText("提示：按【ESC键】退出查询，按【上/下键】翻看信息");

    	final Label infoLabel2 = new Label(shell, SWT.CENTER);
    	final FormData fd_infoLabel2 = new FormData();
    	fd_infoLabel2.bottom = new FormAttachment(0, 65);
    	fd_infoLabel2.top = new FormAttachment(0, 45);
    	fd_infoLabel2.right = new FormAttachment(infoLabel, 614, SWT.LEFT);
    	fd_infoLabel2.left = new FormAttachment(infoLabel, 0, SWT.LEFT);
    	infoLabel2.setLayoutData(fd_infoLabel2);
    	infoLabel2.setText(times);
        
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
    		ex.printStackTrace();
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
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
			ex.printStackTrace();
		}
	}
    
}
