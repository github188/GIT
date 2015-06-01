package custom.localize.Cbbh;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class Cbbh_PaymentCouponNewForm 
{
	private Table table = null;
	private StyledText txtStatus = null;
	private Text txtMoney = null;
	private Text txtAccount = null;
	private Label lblPayName = null;
	public Label account = null;
	
	private Shell shell = null;
	
	public Cbbh_PaymentCouponNewForm()
	{
	
	}
	
	public void open(Cbbh_PaymentCouponNew pay,SaleBS sale) 
	{
		final Display display = Display.getDefault();
		createContents();
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
		Cbbh_PaymentCouponNewEvent ev = new Cbbh_PaymentCouponNewEvent(this,pay,sale);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        { 		
			shell.open();
			shell.layout();
        }
        
        ev.afterFormOpenDoEvent();
        
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

	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style_linux);
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		
		shell.setSize(489,421);
		
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		
		shell.setText(Language.apply("返券卡界面"));

		final Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("付款名称"));
		label.setBounds(10, 10, 80, 20);

		lblPayName = new Label(shell, SWT.NONE);
		lblPayName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblPayName.setBounds(100, 10, 324, 20);

		account= new Label(shell, SWT.NONE);
		account.setBounds(10, 48, 82, 20);
		account.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		account.setText(Language.apply("请 刷 卡"));
		account.setVisible(false);

		txtAccount = new Text(shell, SWT.BORDER);
		txtAccount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtAccount.setBounds(100, 45, 377, 26);
		txtAccount.setVisible(false);


		final Label label_3 = new Label(shell, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("付款余额"));
		label_3.setBounds(10, 48, 82, 20);

		txtMoney  = new Text(shell, SWT.BORDER);
		txtMoney.setBounds(100, 45, 377, 26);
		txtMoney .setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMoney.setEditable(false);

		txtStatus = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
		txtStatus.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtStatus.setBounds(10, 266, 467, 140);
		txtStatus.setEnabled(false);
		txtStatus.setEditable(false);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 85, 467, 169);

		final TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		//newColumnTableColumn_2.setWidth(135);
		newColumnTableColumn_2.setWidth(260);
		newColumnTableColumn_2.setText(Language.apply("券名称"));

		//final TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		//newColumnTableColumn.setWidth(132);
		//newColumnTableColumn.setText(Language.apply("本次可用金额"));
		
		final TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.RIGHT);
		newColumnTableColumn_1.setWidth(170);
		newColumnTableColumn_1.setText(Language.apply("付款金额"));
/*		
		String str []= {"A券","B券","F券"};
		for (int i = 0;i < str.length;i++)
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(str[i]);
		}
*/
	}
	
	public StyledText getTxtStatus()
	{
		return txtStatus;
	}
	
	public Text getTxtMoney()
	{
		return txtMoney;
	}
	
	public Text getTxtAccount()
	{
		return txtAccount;
	}
	
	public Label getLblPayName()
	{
		return lblPayName;
	}
	
	public Table getTable()
	{
		return table;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	

}
