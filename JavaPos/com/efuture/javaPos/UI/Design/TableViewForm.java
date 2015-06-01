package com.efuture.javaPos.UI.Design;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.TableViewBS.TableVeiwStruct;
import com.efuture.javaPos.UI.TableViewEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class TableViewForm
{
    public Label lbCurrentDb;
    public Label lbInfo_3;
    public Label lbInfo;
    public Label lbInfo_2;
    public Label lbInfo_1;
	public Text txtSql;
    public PosTable table;
    public Shell shell;
    public Button btnExec;
    public Button btnExit;
    
    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            TableViewForm window = new TableViewForm();
            window.open(null,null,null,null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open(TableVeiwStruct tvs,String[] title,int[] width,Vector content)
    {
        final Display display = Display.getDefault();
        createContents();

        changeCurrentDb(tvs.DB);
        
        ShowTableInfo(title,width,content);
        
        new TableViewEvent(this,tvs);
		
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
        shell.setSize(750, 522);
        shell.setText(Language.apply("本地库查询功能"));

        table = new PosTable(shell, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL, false);
        table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setBounds(10, 191, 724, 289);

        lbInfo = new Label(shell, SWT.NONE);
        lbInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbInfo.setText(Language.apply("执行SQL语句"));
        lbInfo.setBounds(101, 18, 109, 20);

        txtSql = new Text(shell, SWT.BORDER);
        txtSql.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        txtSql.setBounds(10, 79, 724, 106);

        btnExec = new Button(shell, SWT.NONE);
        btnExec.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        btnExec.setText(Language.apply("回车键"));
        btnExec.setBounds(10, 15, 85, 26);

        btnExit = new Button(shell, SWT.NONE);
        btnExit.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        btnExit.setText(Language.apply("ESC键"));
        btnExit.setBounds(216, 15, 85, 26);

        lbInfo_1 = new Label(shell, SWT.NONE);
        lbInfo_1.setBounds(308, 18, 80, 20);
        lbInfo_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbInfo_1.setText(Language.apply("关闭窗口"));

        lbInfo_2 = new Label(shell, SWT.NONE);
        lbInfo_2.setForeground(SWTResourceManager.getColor(255, 0, 0));
        lbInfo_2.setBounds(10, 47, 724, 20);
        lbInfo_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbInfo_2.setText(Language.apply("格式")+":[Day:yyyymmdd / Local / Base]|Select * from TableName");

        lbInfo_3 = new Label(shell, SWT.NONE);
        lbInfo_3.setBounds(399, 18, 136, 20);
        lbInfo_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbInfo_3.setText(Language.apply("| 当前数据库:"));

        lbCurrentDb = new Label(shell, SWT.NONE);
        lbCurrentDb.setBounds(541, 18, 193, 20);
        lbCurrentDb.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
    }
    
    public void changeCurrentDb(String currentdb)
    {
    	this.lbCurrentDb.setText(currentdb);
    }
    
    public void ShowTableInfo(String[] title,int[] width,Vector content)
    {
    	// 删除行
    	table.removeAll();
    	
    	// 删除列
    	while(table.getColumnCount()>0) table.getColumns()[0].dispose(); 

        table.setTitle(title);
        table.setWidth(width);
        table.initialize();
        table.exchangeContent(content);
    }
}
