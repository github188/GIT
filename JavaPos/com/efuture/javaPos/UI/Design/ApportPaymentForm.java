package com.efuture.javaPos.UI.Design;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.ApportPaymentEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class ApportPaymentForm
{
    public Table table;
    public Shell shell;
    public Label label;
    public Label label_2;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            ApportPaymentForm window = new ApportPaymentForm();
            window.open(null, null, 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void open(Vector content, String info, double je,boolean readonly)
    {
    	final Display display = Display.getDefault();
        createContents();

        ApportPaymentEvent pme = new ApportPaymentEvent(this, content, info, je ,readonly);

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {
            //Rectangle rec = display.getPrimaryMonitor().getClientArea();
            shell.setBounds((GlobalVar.rec.x / 2) - (shell.getSize().x / 2), (GlobalVar.rec.y / 2) - (shell.getSize().y / 2), shell.getSize().x,
                            shell.getSize().y - GlobalVar.heightPL);
            shell.layout();
            shell.open();
            pme.findLocation();
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
    
    /**
     * Open the window
     */
    public void open(Vector content, String info, double je)
    {
    	open(content, info, je, false);
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setSize(750, 375);
        shell.setText(Language.apply("付款分摊界面"));

        table = new Table(shell, SWT.BORDER);
        table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setBounds(10, 47, 724, 277);

        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
        newColumnTableColumn.setWidth(30);
        newColumnTableColumn.setText(Language.apply("序"));
        
        final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn_1.setWidth(308);
        newColumnTableColumn_1.setText(Language.apply("商品信息"));

        final TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn_3.setAlignment(SWT.RIGHT);
        newColumnTableColumn_3.setWidth(120);
        newColumnTableColumn_3.setText(Language.apply("已分摊金额"));

        final TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn_4.setAlignment(SWT.RIGHT);
        newColumnTableColumn_4.setWidth(120);
        newColumnTableColumn_4.setText(Language.apply("分摊限制额"));

        final TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
        newColumnTableColumn_5.setAlignment(SWT.RIGHT);
        newColumnTableColumn_5.setWidth(120);
        newColumnTableColumn_5.setText(Language.apply("分摊金额"));

        label = new Label(shell, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText("Label");
        label.setBounds(10, 15, 458, 26);

        final Label label_1 = new Label(shell, SWT.NONE);
        label_1.setAlignment(SWT.RIGHT);
        label_1.setText(Language.apply("可分摊余额"));
        label_1.setBounds(470, 15, 115, 26);
        label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2 = new Label(shell, SWT.NONE);
        label_2.setAlignment(SWT.RIGHT);
        label_2.setText("1234567.89");
        label_2.setBounds(595, 15, 110, 26);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
    }
}
