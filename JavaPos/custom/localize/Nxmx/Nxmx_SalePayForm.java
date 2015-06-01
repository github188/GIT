package custom.localize.Nxmx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;

public class Nxmx_SalePayForm
{
	public Composite parent = null;
	public Composite composite_pay = null;
    public PosTable table_paymode = null;
    public PosTable table_paylist = null;
    public Label payReqFee;
    public Label unpayfee;
    public Label lbl_ysje;
    public Label lbl_money;
    public Text text_money;
    
	public Nxmx_SalePayForm(Composite parent)
	{
		this.parent = parent;
	}
	
    protected void createContents()
    {
    	composite_pay= new Composite(parent, SWT.NONE);
		final FormData fd_composite_pay = new FormData();
		fd_composite_pay.left = new FormAttachment(0, 479);
		fd_composite_pay.bottom = new FormAttachment(100, 0);
		fd_composite_pay.top = new FormAttachment(0, 0);
		fd_composite_pay.right = new FormAttachment(100, -5);
		composite_pay.setLayoutData(fd_composite_pay);
		composite_pay.setLayout(new FormLayout());
		
		final Group group = new Group(composite_pay, SWT.NONE);
        final FormData fd_group = new FormData();
        fd_group.bottom = new FormAttachment(100, -5);
        fd_group.right = new FormAttachment(100, -5);
        fd_group.left = new FormAttachment(0, 5);
        fd_group.top = new FormAttachment(0, 5);
        group.setLayoutData(fd_group);
        group.setLayout(new FormLayout());
	
        table_paymode = new PosTable(group, SWT.BORDER | SWT.FULL_SELECTION, false);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(100, -51);
        formData.right = new FormAttachment(0, 280);
        formData.top = new FormAttachment(0, 10);
        formData.left = new FormAttachment(0, 5);
        table_paymode.setLayoutData(formData);
        table_paymode.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
        table_paymode.setLinesVisible(true);
        table_paymode.setHeaderVisible(true);
        table_paymode.IsLoopSelection = true;

        table_paymode.addListener(SWT.MeasureItem, new Listener() 
        {    
        	//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
            public void handleEvent(Event event) 
            { 
                event.width = table_paymode.getGridLineWidth();    										//设置宽度 
//                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 3.5);	//设置高度为字体高度的2倍 
                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight()*2.5);	//设置高度为字体高度的2倍 
            }
        });
        
        final TableColumn newColumnTableColumn = new TableColumn(table_paymode, SWT.NONE);
        newColumnTableColumn.setWidth(70);
        newColumnTableColumn.setText("代码");

        if (GlobalInfo.sysPara.salepayDisplayRate == 'Y')
        {
	        final TableColumn newColumnTableColumn_1 = new TableColumn(table_paymode,
	                                                                   SWT.NONE);
	        newColumnTableColumn_1.setWidth(113);
	        newColumnTableColumn_1.setText("付款名称");
	
	        final TableColumn newColumnTableColumn_5 = new TableColumn(table_paymode, SWT.NONE);
	        newColumnTableColumn_5.setAlignment(SWT.RIGHT);
	        newColumnTableColumn_5.setWidth(86);
	        newColumnTableColumn_5.setText("汇率");
        }
        else
        {
	        final TableColumn newColumnTableColumn_1 = new TableColumn(table_paymode,
	                                                                   SWT.NONE);
	        newColumnTableColumn_1.setWidth(199);
	        newColumnTableColumn_1.setText("付款名称");
        }
        lbl_money = new Label(group, SWT.NONE);
        final FormData formData_1 = new FormData();
        formData_1.right = new FormAttachment(0, 100);
        formData_1.top = new FormAttachment(table_paymode, 15, SWT.DEFAULT);
        formData_1.bottom = new FormAttachment(100, -11);
        formData_1.left = new FormAttachment(0, 10);
        lbl_money.setLayoutData(formData_1);
        lbl_money.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbl_money.setText("付款名");

        text_money = new Text(group, SWT.BORDER);
        final FormData formData_2 = new FormData();
        formData_2.left = new FormAttachment(0, 108);
        formData_2.bottom = new FormAttachment(100, -3);
        formData_2.top = new FormAttachment(table_paymode, 8, SWT.DEFAULT);
        formData_2.right = new FormAttachment(0, 280);
        text_money.setLayoutData(formData_2);
        text_money.setFont(SWTResourceManager.getFont("宋体", 23, SWT.NONE));

        FocusListener listener = new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
            	text_money.setFocus();
            }

            public void focusLost(FocusEvent e)
            {
            }
        };
        //text_money.addFocusListener(listener);
        composite_pay.addFocusListener(listener);
        
        table_paylist = new PosTable(group, SWT.BORDER | SWT.FULL_SELECTION, false);
        final FormData formData_3 = new FormData();
        formData_3.right = new FormAttachment(100, -5);
        formData_3.bottom = new FormAttachment(text_money, 0, SWT.BOTTOM);
        formData_3.left = new FormAttachment(table_paymode, 7, SWT.DEFAULT);
        formData_3.top = new FormAttachment(0, 100);
        table_paylist.setLayoutData(formData_3);
        table_paylist.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
        table_paylist.setLinesVisible(true);
        table_paylist.setHeaderVisible(true);
        table_paylist.addListener(SWT.MeasureItem, new Listener() 
        {    
        	//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
            public void handleEvent(Event event) 
            { 
                event.width = table_paylist.getGridLineWidth();    										//设置宽度 
//                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 3.5);	//设置高度为字体高度的2倍 
                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight()*2.5);	//设置高度为字体高度的2倍 
            }
        });
        
        final TableColumn newColumnTableColumn_2 = new TableColumn(table_paylist,
                                                                   SWT.NONE);
        newColumnTableColumn_2.setWidth(108);
        newColumnTableColumn_2.setText("付款名称");

        final TableColumn newColumnTableColumn_3 = new TableColumn(table_paylist,
                                                                   SWT.NONE);
        newColumnTableColumn_3.setWidth(122);
        newColumnTableColumn_3.setText("付款帐号");

        final TableColumn newColumnTableColumn_4 = new TableColumn(table_paylist,
                                                                   SWT.NONE);
        newColumnTableColumn_4.setAlignment(SWT.RIGHT);
        newColumnTableColumn_4.setWidth(126);
        newColumnTableColumn_4.setText("付款金额");

        lbl_ysje = new Label(group, SWT.NONE);
        lbl_ysje.setForeground(SWTResourceManager.getColor(0, 0, 255));
        final FormData formData_4 = new FormData();
        formData_4.bottom = new FormAttachment(0, 46);
        formData_4.top = new FormAttachment(0, 10);
        formData_4.right = new FormAttachment(0, 398);
        formData_4.left = new FormAttachment(0, 288);
        lbl_ysje.setLayoutData(formData_4);
        lbl_ysje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbl_ysje.setText("应付金额:");

        final Label label_2 = new Label(group, SWT.NONE);
        label_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
        final FormData formData_5 = new FormData();
        formData_5.bottom = new FormAttachment(0, 86);
        formData_5.top = new FormAttachment(0, 50);
        formData_5.right = new FormAttachment(0, 398);
        formData_5.left = new FormAttachment(0, 288);
        label_2.setLayoutData(formData_5);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2.setText("剩余金额:");

        int screenwidth = Display.getDefault().getBounds().width;
        payReqFee = new Label(group, SWT.NONE);
        payReqFee.setForeground(SWTResourceManager.getColor(0, 0, 255));
        final FormData formData_6 = new FormData();
        formData_6.bottom = new FormAttachment(0, 46);
        formData_6.right = new FormAttachment(100, -9);
        formData_6.top = new FormAttachment(0, 10);
        formData_6.left = new FormAttachment(0, 435);
        payReqFee.setLayoutData(formData_6);
        if (screenwidth >= 800) payReqFee.setAlignment(SWT.RIGHT);
        else payReqFee.setAlignment(SWT.CENTER);
        payReqFee.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
        payReqFee.setText("");

        unpayfee = new Label(group, SWT.NONE);
        unpayfee.setForeground(SWTResourceManager.getColor(255, 0, 0));
        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 86);
        formData_7.right = new FormAttachment(100, -9);
        formData_7.top = new FormAttachment(0, 50);
        formData_7.left = new FormAttachment(0, 435);
        unpayfee.setLayoutData(formData_7);
        if (screenwidth >= 800) unpayfee.setAlignment(SWT.RIGHT);
        else unpayfee.setAlignment(SWT.CENTER);
        unpayfee.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
        unpayfee.setText("");
    }

    public void open(SaleBS saleBS,boolean refund)
    {
        final Display display = Display.getDefault();
        
        createContents();
        
        Nxmx_SalePayEvent pe = null;
        // 扣回付款
        if (refund)
        {
        	new Nxmx_SaleRefundEvent(saleBS, this);
        }
        else
        {
        	pe = new Nxmx_SalePayEvent(saleBS, this);
        }
                
       if (!composite_pay.isDisposed())
        {        
    	   parent.layout(true); //一定得刷新一次，否则coposite_pay无法显示出来
           composite_pay.layout(true); 
           composite_pay.setVisible(true);
           composite_pay.redraw();
           
	    	// 通过快捷付款键进入付款窗口,立即执行按键处理
	        if (saleBS.quickpaykey != 0 && pe != null) 
	        {
	        	pe.keyReleased(null,saleBS.quickpaykey);
	        	saleBS.quickpaykey = 0;
	        }
        }
        
        while (!composite_pay.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        //System.out.println("test");
    }
    
    public void dispose()
    {
    	if (composite_pay!=null && !composite_pay.isDisposed())
    		composite_pay.dispose();
    }
}
