package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.SaleShowAccountEvent;
import com.swtdesigner.SWTResourceManager;


public class SaleShowAccountForm
{
    public Table table;
    public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
    private Display display = null;
    public Group grp_zl_sy;
    public Label txt_yfje;
    public Label txt_zl;
    public Label txt_sfje;
    public Label status;
    public Group grp_yfje;
    public Group grp_sfje;
    
    public Composite composite = null;
    public Composite  composite_1 = null;
    public boolean done = false;
	public Composite composite_4;
	public Composite composite_2;

    /**
     * This method initializes sShell
     */
    public boolean open(SaleBS saleBS)
    {
        display = Display.getDefault();

        createSShell();

        new SaleShowAccountEvent(this, saleBS);
        
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,sShell,null);
        
        if (!sShell.isDisposed())
        {        
	        sShell.open();
	        sShell.setActive();
        }
        
        while (!sShell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }

        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        
        return done;
    }

    private void createSShell()
    {
        sShell = new Shell(GlobalVar.style);
        
        sShell.setSize(new org.eclipse.swt.graphics.Point(800, 510));
        sShell.setLocation(150, 50);

        grp_yfje = new Group(sShell, SWT.NONE);
        grp_yfje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        grp_yfje.setText(Language.apply("应付金额"));
        grp_yfje.setBounds(10, 10, 382, 168);

        txt_yfje = new Label(grp_yfje, SWT.RIGHT);
        txt_yfje.setFont(SWTResourceManager.getFont("宋体", 50, SWT.BOLD));
        txt_yfje.setText("1234567.00");
        txt_yfje.setBounds(10, 64, 362, 79);

        grp_sfje = new Group(sShell, SWT.NONE);
        grp_sfje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        grp_sfje.setText(Language.apply("实付金额"));
        grp_sfje.setBounds(398, 10, 390, 168);

        int screenwidth = Display.getDefault().getBounds().width;
        if (screenwidth >= 800) txt_sfje = new Label(grp_sfje, SWT.RIGHT);
        else txt_sfje = new Label(grp_sfje, SWT.CENTER);
        txt_sfje.setForeground(SWTResourceManager.getColor(0, 0, 255));
        txt_sfje.setFont(SWTResourceManager.getFont("宋体", 50, SWT.BOLD));
        txt_sfje.setText("1234567.00");
        txt_sfje.setBounds(10, 64, 370, 79);

        grp_zl_sy = new Group(sShell, SWT.NONE);
        grp_zl_sy.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        grp_zl_sy.setText(Language.apply("找零/溢余（123.00）"));
        grp_zl_sy.setBounds(10, 197, 778, 202);

        txt_zl = new Label(grp_zl_sy, SWT.NONE);
        if (screenwidth >= 800) txt_zl.setAlignment(SWT.RIGHT);
        else txt_zl.setAlignment(SWT.CENTER);
        txt_zl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        if (GlobalInfo.ModuleType.indexOf("ZM")!=0)
        {
        	txt_zl.setFont(SWTResourceManager.getFont("宋体", 80, SWT.BOLD));
        }
        else
        {
        	txt_zl.setFont(SWTResourceManager.getFont("宋体", 40, SWT.BOLD));
        }
        
        txt_zl.setText("123.00");
        txt_zl.setBounds(10, 52, 758, 119);

        table = new Table(grp_zl_sy, SWT.FULL_SELECTION | SWT.BORDER);
        table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setBounds(10, 170, 758, 22);

        final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn_2.setWidth(306);
        newColumnTableColumn_2.setText(Language.apply("币种"));
        
        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
        newColumnTableColumn.setAlignment(SWT.RIGHT);
        newColumnTableColumn.setWidth(170);
        newColumnTableColumn.setText(Language.apply("汇率"));

        final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn_1.setAlignment(SWT.RIGHT);
        newColumnTableColumn_1.setWidth(252);
        newColumnTableColumn_1.setText(Language.apply("金额"));
        
        //
    	table.setBounds(10, 42, 758, 139);

		final Color mouseup = SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color mousedown = SWTResourceManager.getColor(0, 64, 128);
    	
		composite = new Composite(sShell, SWT.NONE);
        composite.setBounds(10, 417, 778, 51);

        composite_2 = new Composite(composite, SWT.BORDER);
        composite_2.setBounds(45, 10, 89, 31);

        if (GlobalInfo.ModuleType.indexOf("ZM")!=0)
        {
        	final Label label_1 = new Label(composite_2, SWT.NONE);
            label_1.addMouseListener(new MouseAdapter() {
    			public void mouseUp(final MouseEvent arg0)
    			{
    				label_1.setBackground(mouseup);
    				NewKeyListener.sendKey(GlobalVar.Validation);
    			}
    			
    			public void mouseDown(final MouseEvent arg0)
    			{
    				label_1.setBackground(mousedown);
    			}
    		});
            label_1.setBounds(0, 2,85, 27);
            label_1.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_1.setText(Language.apply("确认键"));

            final Label label = new Label(composite, SWT.NONE);
            label.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label.setText(Language.apply("按"));
            label.setBounds(10, 11, 29, 29);

            final Label label_2 = new Label(composite, SWT.NONE);
            label_2.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_2.setText(Language.apply("或"));
            label_2.setBounds(137, 12, 28, 26);

            composite_4 = new Composite(composite, SWT.BORDER);
            composite_4.setBounds(171, 10, 92, 31);

            final Label label_3 = new Label(composite_4, SWT.NONE);
            label_3.addMouseListener(new MouseAdapter() {
    			public void mouseUp(final MouseEvent arg0)
    			{
    				label_3.setBackground(mouseup);
    				NewKeyListener.sendKey(GlobalVar.Pay);
    			}
    			
    			public void mouseDown(final MouseEvent arg0)
    			{
    				label_3.setBackground(mousedown);
    			}
    		});
            label_3.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_3.setText(Language.apply("付款键"));
            label_3.setBounds(0, 1, 88, 26);

            final Label label_4 = new Label(composite, SWT.NONE);
            label_4.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_4.setText(Language.apply("确认付款，按"));
            label_4.setBounds(269, 12, 133, 26);
        }
        else
        {
        	//中免要用回车键进行确定付款，而不采用确认键和付款键
        	composite_2.setVisible(false);
        	/*final Label label_1 = new Label(composite_2, SWT.NONE);
            label_1.addMouseListener(new MouseAdapter() {
    			public void mouseUp(final MouseEvent arg0)
    			{
    				label_1.setBackground(mouseup);
    				NewKeyListener.sendKey(GlobalVar.Validation);
    			}
    			
    			public void mouseDown(final MouseEvent arg0)
    			{
    				label_1.setBackground(mousedown);
    			}
    		});
            label_1.setBounds(0, 2,85, 27);
            label_1.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_1.setText("确认键");

            final Label label = new Label(composite, SWT.NONE);
            label.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label.setText("按");
            label.setBounds(10, 11, 29, 29);*/

            final Label label_2 = new Label(composite, SWT.NONE);
            label_2.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_2.setText(Language.apply("按"));
            label_2.setBounds(137, 12, 28, 26);

            composite_4 = new Composite(composite, SWT.BORDER);
            composite_4.setBounds(171, 10, 92, 31);

            final Label label_3 = new Label(composite_4, SWT.NONE);
            label_3.addMouseListener(new MouseAdapter() {
    			public void mouseUp(final MouseEvent arg0)
    			{
    				label_3.setBackground(mouseup);
    				NewKeyListener.sendKey(GlobalVar.Enter);//GlobalVar.Pay
    			}
    			
    			public void mouseDown(final MouseEvent arg0)
    			{
    				label_3.setBackground(mousedown);
    			}
    		});
            label_3.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_3.setText(Language.apply("回车键"));
            label_3.setBounds(0, 1, 88, 26);

            final Label label_4 = new Label(composite, SWT.NONE);
            label_4.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
            label_4.setText(Language.apply("确认付款，按"));
            label_4.setBounds(269, 12, 133, 26);
        }
        

        final Composite composite_5 = new Composite(composite, SWT.BORDER);
        composite_5.setBounds(408, 10, 88, 31);

        final Label label_5 = new Label(composite_5, SWT.NONE);
        label_5.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				label_5.setBackground(mouseup);
				NewKeyListener.sendKey(GlobalVar.Exit);
			}
			
			public void mouseDown(final MouseEvent arg0)
			{
				label_5.setBackground(mousedown);
			}
		});
        label_5.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
        label_5.setText(Language.apply("退出键"));
        label_5.setBounds(0, 2, 84, 25);

        final Label label_6 = new Label(composite, SWT.NONE);
        label_6.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
        label_6.setText(Language.apply("返回付款界面"));
        label_6.setBounds(502, 12, 203, 26);

        composite_1 = new Composite(sShell, SWT.NONE);
        composite_1.setVisible(false);
        composite_1.setBounds(10, 417, 778, 51);
        status = new Label(composite_1, SWT.NONE);
        status.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
        status.setText("status");
        status.setBounds(10, 11, 750, 40);
        

    }
}
