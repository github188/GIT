package custom.localize.Zmjc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class CustInfoForm
{
    public Table optionTable;
    public Table table;
    public Shell shell;
    public static TableColumn columnTableC0;
    public static TableColumn columnTableC1;
    public static TableColumn columnTableC2;
    public static TableColumn columnTableC3;
    
    public void open(Zmjc_SaleBS sale, StringBuffer DlInfo)
    {
        final Display display = Display.getDefault();
        createContents();

        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
        CustInfoEvent pme = new CustInfoEvent(this,sale, DlInfo);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {
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
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(GlobalVar.style);
        shell.setLayout(new FormLayout());
        shell.setSize(728, 452);
        shell.setText(Language.apply("顾客信息"));

        table = new Table(shell, SWT.NONE | SWT.BORDER);
        final FormData fd_table = new FormData();
        fd_table.bottom = new FormAttachment(0, 162);
        fd_table.top = new FormAttachment(0, 7);
        fd_table.right = new FormAttachment(0, 719);
        fd_table.left = new FormAttachment(0, 4);
        table.setLayoutData(fd_table);
        table.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
        newColumnTableColumn.setWidth(80);
        newColumnTableColumn.setText(Language.apply("行号"));

        final TableColumn newColumnTableColumn_1 = new TableColumn(table,
                                                                   SWT.NONE);
        newColumnTableColumn_1.setAlignment(SWT.LEFT);
        newColumnTableColumn_1.setText(Language.apply("名称"));
        newColumnTableColumn_1.setWidth(241);

        final TableColumn newColumnTableColumn_2 = new TableColumn(table,
                                                                   SWT.NONE);
        newColumnTableColumn_2.setAlignment(SWT.LEFT);
        newColumnTableColumn_2.setWidth(366);
        newColumnTableColumn_2.setText(Language.apply("值"));

		final Color mouseup = SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color mousedown = SWTResourceManager.getColor(0, 64, 128);

		optionTable = new Table(shell, SWT.BORDER|SWT.FULL_SELECTION);
        final FormData fd_optionTable = new FormData();
        fd_optionTable.bottom = new FormAttachment(0, 415);
        fd_optionTable.top = new FormAttachment(0, 170);
        fd_optionTable.right = new FormAttachment(0, 720);
        fd_optionTable.left = new FormAttachment(0, 3);
        optionTable.setLayoutData(fd_optionTable);
        optionTable.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
        optionTable.setLinesVisible(true);
        optionTable.setHeaderVisible(true);

        columnTableC0 = new TableColumn(optionTable, SWT.NONE);
        columnTableC0.setWidth(80);
        columnTableC0.setText(Language.apply("行号"));

        columnTableC1 = new TableColumn(optionTable, SWT.NONE);
        columnTableC1.setAlignment(SWT.LEFT);
        columnTableC1.setWidth(201);
        columnTableC1.setText(Language.apply("名称"));

        columnTableC2 = new TableColumn(optionTable, SWT.NONE);
        columnTableC2.setAlignment(SWT.LEFT);
        columnTableC2.setWidth(246);
        columnTableC2.setText(Language.apply("值"));

        columnTableC3 = new TableColumn(optionTable, SWT.NONE);
        columnTableC3.setAlignment(SWT.LEFT);
        columnTableC3.setWidth(160);
        columnTableC3.setText(Language.apply("其他信息"));
    }
}
