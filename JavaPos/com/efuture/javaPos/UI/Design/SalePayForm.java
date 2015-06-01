package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.SalePayEvent;
import com.efuture.javaPos.UI.SaleRefundEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class SalePayForm
{
    private Label Label_ZJSJ_JE;
    public PosTable table1;
    public Text text;
    public PosTable table;
    public Shell shell;
    public Label payReqFee;
    public Label unpayfee;
    public Label lbl_ysje;
    public Label lbl_money;
    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            SalePayForm window = new SalePayForm();
            window.open(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void open(SaleBS saleBS)
    {
    	open(saleBS,false);
    }
    
    /**
     * Open the window
     */
    public void open(SaleBS saleBS,boolean refund)
    {
        final Display display = Display.getDefault();
        createContents();
        
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        SalePayEvent pe = null;
        
        // 扣回付款
        if (refund)
        {
        	new SaleRefundEvent(saleBS, this);
        }
        else
        {
        	pe = new SalePayEvent(saleBS, this);
        }
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {        
	        shell.open();
	        text .setFocus();
	        shell.redraw();
	        
	    	// 通过快捷付款键进入付款窗口,立即执行按键处理
	        if (saleBS.quickpaykey != 0 && pe != null) 
	        {
	        	pe.keyReleased(null,saleBS.quickpaykey);
	        	saleBS.quickpaykey = 0;
	        }
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

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setLayout(new FormLayout());
        shell.setSize(800, 510);
        
        shell.setText(Language.apply("销售付款"));
        table = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, false);
        final FormData formData = new FormData();
        formData.bottom = new FormAttachment(0, 418);
        formData.top = new FormAttachment(0, 10);
        formData.right = new FormAttachment(0, 303);
        formData.left = new FormAttachment(0, 10);
        table.setLayoutData(formData);
        table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.IsLoopSelection = true;

        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
        newColumnTableColumn.setWidth(70);
        newColumnTableColumn.setText(Language.apply("代码"));

        if (GlobalInfo.sysPara.salepayDisplayRate == 'Y')
        {
	        final TableColumn newColumnTableColumn_1 = new TableColumn(table,
	                                                                   SWT.NONE);
	        newColumnTableColumn_1.setWidth(131);
	        newColumnTableColumn_1.setText(Language.apply("付款名称"));
	
	        final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
	        newColumnTableColumn_5.setAlignment(SWT.RIGHT);
	        newColumnTableColumn_5.setWidth(86);
	        newColumnTableColumn_5.setText(Language.apply("汇率"));
        }
        else
        {
	        final TableColumn newColumnTableColumn_1 = new TableColumn(table,
	                                                                   SWT.NONE);
	        newColumnTableColumn_1.setWidth(199);
	        newColumnTableColumn_1.setText(Language.apply("付款名称"));
        }
        lbl_money = new Label(shell, SWT.NONE);
        lbl_money.setForeground(SWTResourceManager.getColor(0, 0, 0));
        final FormData formData_1 = new FormData();
        formData_1.top = new FormAttachment(0, 430);
        formData_1.bottom = new FormAttachment(0, 462);
        formData_1.right = new FormAttachment(0, 111);
        formData_1.left = new FormAttachment(0, 10);
        lbl_money.setLayoutData(formData_1);
        lbl_money.setFont(SWTResourceManager.getFont("宋体", 25, SWT.NONE));
        lbl_money.setText(Language.apply("付款名"));

        text = new Text(shell, SWT.BORDER);
        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(0, 462);
        formData_2.top = new FormAttachment(0, 430);
        formData_2.right = new FormAttachment(0, 303);
        formData_2.left = new FormAttachment(0, 117);
        text.setLayoutData(formData_2);
        text.setFont(SWTResourceManager.getFont("宋体", 25, SWT.NONE));

        lbl_ysje = new Label(shell, SWT.NONE);
        lbl_ysje.setForeground(SWTResourceManager.getColor(0, 0, 255));
        final FormData formData_4 = new FormData();
        formData_4.bottom = new FormAttachment(0, 42);
        formData_4.top = new FormAttachment(0, 10);
        formData_4.right = new FormAttachment(0, 468);
        formData_4.left = new FormAttachment(0, 319);
        lbl_ysje.setLayoutData(formData_4);
        lbl_ysje.setFont(SWTResourceManager.getFont("宋体", 23, SWT.NONE));
        lbl_ysje.setText(Language.apply("应付金额:"));

        final Label label_2 = new Label(shell, SWT.NONE);
        label_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
        final FormData formData_5 = new FormData();
        formData_5.bottom = new FormAttachment(0, 82);
        formData_5.top = new FormAttachment(0, 50);
        formData_5.right = new FormAttachment(0, 468);
        formData_5.left = new FormAttachment(0, 319);
        label_2.setLayoutData(formData_5);
        label_2.setFont(SWTResourceManager.getFont("宋体", 23, SWT.NONE));
        label_2.setText(Language.apply("剩余金额:"));

        int screenwidth = Display.getDefault().getBounds().width;
        payReqFee = new Label(shell, SWT.NONE);
        payReqFee.setForeground(SWTResourceManager.getColor(0, 0, 255));
        final FormData formData_6 = new FormData();
        formData_6.bottom = new FormAttachment(0, 49);
        formData_6.top = new FormAttachment(0, 10);
        formData_6.right = new FormAttachment(0, 783);
        formData_6.left = new FormAttachment(0, 474);
        payReqFee.setLayoutData(formData_6);
        if (screenwidth >= 800) payReqFee.setAlignment(SWT.RIGHT);
        else payReqFee.setAlignment(SWT.CENTER);
        payReqFee.setFont(SWTResourceManager.getFont("宋体", 28, SWT.BOLD));
        payReqFee.setText("Label");

        unpayfee = new Label(shell, SWT.NONE);
        unpayfee.setForeground(SWTResourceManager.getColor(255, 0, 0));
        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 90);
        formData_7.top = new FormAttachment(0, 50);
        formData_7.right = new FormAttachment(0, 784);
        formData_7.left = new FormAttachment(0, 474);
        unpayfee.setLayoutData(formData_7);
        if (screenwidth >= 800) unpayfee.setAlignment(SWT.RIGHT);
        else unpayfee.setAlignment(SWT.CENTER);
        unpayfee.setFont(SWTResourceManager.getFont("宋体", 28, SWT.BOLD));
        unpayfee.setText("Label");
        
        
        if (GlobalInfo.ModuleType.indexOf("ZMSY")==0 && GlobalInfo.saleform.sale.saleBS.saleHead.num9>0)
		{
        	//中免三亚即购即提

            table1 = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, false);
            final FormData formData_3 = new FormData();
            formData_3.bottom = new FormAttachment(0, 462);
            formData_3.right = new FormAttachment(0, 781);
            formData_3.left = new FormAttachment(0, 319);
            table1.setLayoutData(formData_3);
            table1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
            table1.setLinesVisible(true);
            table1.setHeaderVisible(true);

            final TableColumn newColumnTableColumn_2 = new TableColumn(table1,
                                                                       SWT.NONE);
            newColumnTableColumn_2.setWidth(158);
            newColumnTableColumn_2.setText(Language.apply("付款名称"));

            final TableColumn newColumnTableColumn_3 = new TableColumn(table1,
                                                                       SWT.NONE);
            newColumnTableColumn_3.setWidth(152);
            newColumnTableColumn_3.setText(Language.apply("付款帐号"));

            final TableColumn newColumnTableColumn_4 = new TableColumn(table1,
                                                                       SWT.NONE);
            newColumnTableColumn_4.setAlignment(SWT.RIGHT);
            newColumnTableColumn_4.setWidth(126);
            newColumnTableColumn_4.setText(Language.apply("付款金额"));
        	
            final Label label__ZJSJ = new Label(shell, SWT.NONE);
            final FormData fd_label__ZJSJ = new FormData();
            fd_label__ZJSJ.bottom = new FormAttachment(unpayfee, 37, SWT.BOTTOM);
            fd_label__ZJSJ.top = new FormAttachment(unpayfee, 5, SWT.BOTTOM);
            fd_label__ZJSJ.left = new FormAttachment(label_2, 0, SWT.LEFT);
            label__ZJSJ.setLayoutData(fd_label__ZJSJ);
            //label__ZJSJ.setForeground(SWTResourceManager.getColor(255, 0, 0));
            label__ZJSJ.setFont(SWTResourceManager.getFont("宋体", 23, SWT.NONE));
            label__ZJSJ.setText(Language.apply("税款担保金:"));

            Label_ZJSJ_JE = new Label(shell, SWT.NONE);
            fd_label__ZJSJ.right = new FormAttachment(Label_ZJSJ_JE, -5, SWT.LEFT);
            formData_3.top = new FormAttachment(Label_ZJSJ_JE, 5, SWT.BOTTOM);
            final FormData fd_label_ZJSJ_JE = new FormData();
            fd_label_ZJSJ_JE.left = new FormAttachment(0, 555);
            fd_label_ZJSJ_JE.bottom = new FormAttachment(unpayfee, 45, SWT.BOTTOM);
            fd_label_ZJSJ_JE.top = new FormAttachment(unpayfee, 5, SWT.BOTTOM);
            fd_label_ZJSJ_JE.right = new FormAttachment(unpayfee, 310, SWT.LEFT);
            Label_ZJSJ_JE.setLayoutData(fd_label_ZJSJ_JE);
            //Label_ZJSJ_JE.setForeground(SWTResourceManager.getColor(255, 0, 0));
            Label_ZJSJ_JE.setFont(SWTResourceManager.getFont("宋体", 28, SWT.BOLD));
            Label_ZJSJ_JE.setAlignment(SWT.RIGHT);
            Label_ZJSJ_JE.setText(ManipulatePrecision.doubleToString(GlobalInfo.saleform.sale.saleBS.saleHead.num9));
		}
        else
        {
            table1 = new PosTable(shell, SWT.BORDER | SWT.FULL_SELECTION, false);
            final FormData formData_3 = new FormData();
            formData_3.bottom = new FormAttachment(0, 462);
            formData_3.top = new FormAttachment(0, 102);
            formData_3.right = new FormAttachment(0, 781);
            formData_3.left = new FormAttachment(0, 319);
            table1.setLayoutData(formData_3);
            table1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
            table1.setLinesVisible(true);
            table1.setHeaderVisible(true);

            final TableColumn newColumnTableColumn_2 = new TableColumn(table1,
                                                                       SWT.NONE);
            newColumnTableColumn_2.setWidth(158);
            newColumnTableColumn_2.setText(Language.apply("付款名称"));

            final TableColumn newColumnTableColumn_3 = new TableColumn(table1,
                                                                       SWT.NONE);
            newColumnTableColumn_3.setWidth(152);
            newColumnTableColumn_3.setText(Language.apply("付款帐号"));

            final TableColumn newColumnTableColumn_4 = new TableColumn(table1,
                                                                       SWT.NONE);
            newColumnTableColumn_4.setAlignment(SWT.RIGHT);
            newColumnTableColumn_4.setWidth(126);
            newColumnTableColumn_4.setText(Language.apply("付款金额"));
        }
    }
}
