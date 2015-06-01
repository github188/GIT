package custom.localize.Smtj;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class Smtj_SyySaleStatForm
{

	private Text txtDate = null;
	private Table tabPayInfo = null;
	private Table tabBaseInfo = null;
	private Shell shell = null;
	private Label lblSaleAmount = null;
	private Label lblSaleMoney = null;
	private Label lblReturnGoodsAmount = null;
	private Label lblReturnGoodsMoney = null;
	private Label lblRedCancelAmount = null;
	private Label lblRedCancelMoney = null;
	private Label lblCancelAmount = null;
	private Label lblCancelMoney = null;
	private Label lblSpoilageMoney = null;
	private Label lblGiveChangeMoney = null;
	private Label lblShouldInceptMoney = null;
	private Label lblFactInceptMoney = null;
	
	public Smtj_SyySaleStatForm()
	{
		this.open();
	}
	
	public void open() 
	{
		final Display display = Display.getDefault();
		createContents();
		
		new Smtj_SyySaleStatEvent(this);
		
        // 创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
                
        // 加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        { 			
			shell.open();
			shell.layout();
        }
        
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}

	
	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(800,510);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
	
		shell.setText("收银员销售统计");

		tabBaseInfo = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabBaseInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabBaseInfo.setLinesVisible(true);
		tabBaseInfo.setHeaderVisible(true);
		tabBaseInfo.setBounds(10, 63, 269, 405);

		final TableColumn newColumnTableColumn = new TableColumn(tabBaseInfo, SWT.NONE);
		newColumnTableColumn.setWidth(100);
		newColumnTableColumn.setText("班次");

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabBaseInfo, SWT.NONE);
		newColumnTableColumn_1.setWidth(147);
		newColumnTableColumn_1.setText("收银员工号");

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(285, 56, 499, 151);

		final Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText("销售笔数:");
		label.setBounds(5, 15, 90, 20);

		lblSaleAmount = new Label(group, SWT.RIGHT);
		lblSaleAmount.setForeground(SWTResourceManager.getColor(0, 0, 255));
		lblSaleAmount.setAlignment(SWT.RIGHT);
		lblSaleAmount.setBounds(90, 15, 154, 20);
		lblSaleAmount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		final Label label_2 = new Label(group, SWT.NONE);
		label_2.setBounds(250, 15, 90, 20);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText("销售金额:");

		lblSaleMoney = new Label(group, SWT.RIGHT);
		lblSaleMoney.setForeground(SWTResourceManager.getColor(0, 0, 255));
		lblSaleMoney.setBounds(335, 15, 154, 20);
		lblSaleMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblSaleMoney.setAlignment(SWT.RIGHT);

		final Label label_3 = new Label(group, SWT.NONE);
		label_3.setBounds(5, 40, 90, 20);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText("退货笔数:");

		lblReturnGoodsAmount = new Label(group, SWT.RIGHT);
		lblReturnGoodsAmount.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lblReturnGoodsAmount.setBounds(95, 40, 149, 20);
		lblReturnGoodsAmount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblReturnGoodsAmount.setAlignment(SWT.RIGHT);

		final Label label_3_1 = new Label(group, SWT.NONE);
		label_3_1.setBounds(250, 40, 90, 20);
		label_3_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1.setText("退货金额:");

		lblReturnGoodsMoney = new Label(group, SWT.RIGHT);
		lblReturnGoodsMoney.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lblReturnGoodsMoney.setBounds(340, 40, 150, 20);
		lblReturnGoodsMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblReturnGoodsMoney.setAlignment(SWT.RIGHT);

		final Label label_3_2 = new Label(group, SWT.NONE);
		label_3_2.setBounds(5, 65, 90, 20);
		label_3_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2.setText("红冲笔数:");

		lblRedCancelAmount = new Label(group, SWT.RIGHT);
		lblRedCancelAmount.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lblRedCancelAmount.setBounds(95, 65, 150, 20);
		lblRedCancelAmount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblRedCancelAmount.setAlignment(SWT.RIGHT);

		final Label label_3_1_1 = new Label(group, SWT.NONE);
		label_3_1_1.setBounds(250, 65, 90, 20);
		label_3_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1_1.setText("红冲金额:");

		lblRedCancelMoney = new Label(group, SWT.RIGHT);
		lblRedCancelMoney.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lblRedCancelMoney.setBounds(340, 65, 149, 20);
		lblRedCancelMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblRedCancelMoney.setAlignment(SWT.RIGHT);

		final Label label_3_2_1 = new Label(group, SWT.NONE);
		label_3_2_1.setBounds(5, 90, 90, 20);
		label_3_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2_1.setText("折扣金额:");

		lblCancelAmount = new Label(group, SWT.RIGHT);
		lblCancelAmount.setBounds(95, 90, 149, 20);
		lblCancelAmount.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblCancelAmount.setAlignment(SWT.RIGHT);

		final Label label_3_1_1_1 = new Label(group, SWT.NONE);
		label_3_1_1_1.setBounds(250, 90, 90, 20);
		label_3_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1_1_1.setText("取消金额:");

		lblCancelMoney = new Label(group, SWT.RIGHT);
		lblCancelMoney.setBounds(335, 90, 154, 20);
		lblCancelMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblCancelMoney.setAlignment(SWT.RIGHT);

		final Label label_3_2_1_1 = new Label(group, SWT.NONE);
		label_3_2_1_1.setBounds(5, 116, 90, 20);
		label_3_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2_1_1.setText("损溢金额:");

		lblSpoilageMoney = new Label(group, SWT.RIGHT);
		lblSpoilageMoney.setBounds(90, 115, 155, 20);
		lblSpoilageMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblSpoilageMoney.setAlignment(SWT.RIGHT);

		final Label label_3_1_1_1_1 = new Label(group, SWT.NONE);
		label_3_1_1_1_1.setBounds(250, 115, 90, 20);
		label_3_1_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1_1_1_1.setText("找零金额:");

		lblGiveChangeMoney = new Label(group, SWT.RIGHT);
		lblGiveChangeMoney.setBounds(340, 115, 149, 20);
		lblGiveChangeMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lblGiveChangeMoney.setAlignment(SWT.RIGHT);

		tabPayInfo = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tabPayInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabPayInfo.setLinesVisible(true);
		tabPayInfo.setHeaderVisible(true);
		tabPayInfo.setBounds(285, 211, 499, 221);

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabPayInfo, SWT.NONE);
		newColumnTableColumn_2.setWidth(200);
		newColumnTableColumn_2.setText("付款方式");

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabPayInfo, SWT.RIGHT);
		newColumnTableColumn_4.setWidth(100);
		newColumnTableColumn_4.setText("笔数");

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabPayInfo, SWT.RIGHT);
		newColumnTableColumn_3.setWidth(179);
		newColumnTableColumn_3.setText("实收金额");

		final Group group_1 = new Group(shell, SWT.NONE);
		group_1.setBounds(285, 430, 499, 39);

		final Label label_3_2_1_1_1 = new Label(group_1, SWT.NONE);
		label_3_2_1_1_1.setBounds(5, 15, 90, 20);
		label_3_2_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_2_1_1_1.setText("应收金额:");

		lblShouldInceptMoney = new Label(group_1, SWT.RIGHT);
		lblShouldInceptMoney.setForeground(SWTResourceManager.getColor(0, 0, 255));
		lblShouldInceptMoney.setBounds(95, 15, 149, 20);
		lblShouldInceptMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
		lblShouldInceptMoney.setAlignment(SWT.RIGHT);

		final Label label_3_1_1_1_1_1 = new Label(group_1, SWT.NONE);
		label_3_1_1_1_1_1.setBounds(250, 15, 90, 20);
		label_3_1_1_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1_1_1_1_1.setText("实收金额:");

		lblFactInceptMoney = new Label(group_1, SWT.RIGHT);
		lblFactInceptMoney.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lblFactInceptMoney.setBounds(340, 15, 150, 20);
		lblFactInceptMoney.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
		lblFactInceptMoney.setAlignment(SWT.RIGHT);

		final Group group_2 = new Group(shell, SWT.NONE);
		group_2.setBounds(10, 0, 774, 55);

		final Label label_1 = new Label(group_2, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setBounds(10, 20, 140, 20);
		label_1.setText("请输入查询日期");

		txtDate = new Text(group_2, SWT.BORDER);
		txtDate.setTextLimit(8);
		txtDate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtDate.setBounds(155, 15, 172, 30);

		final Label label_1_1 = new Label(group_2, SWT.NONE);
		label_1_1.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1_1.setBounds(333, 20, 431, 20);
		label_1_1.setText("格式为:YYYYMMDD[使用付款键切回输入日期框]");
	}
	
	public Table getTabBaseInfo()
	{
		return tabBaseInfo;
	}
	
	public Table getTabPayInfo()
	{
		return tabPayInfo;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Label getLblSaleAmount()
	{
		return lblSaleAmount;
	}
	
	public Label getLblSaleMoney()
	{
		return lblSaleMoney;
	}
	
	public Label getLblReturnGoodsAmount()
	{
		return lblReturnGoodsAmount;
	}
	
	public Label getLblReturnGoodsMoney()
	{
		return lblReturnGoodsMoney;
	}
	
	public Label getLblRedCancelAmount()
	{
		return lblRedCancelAmount;
	}
	
	public Label getLblRedCancelMoney()
	{
		return lblRedCancelMoney;
	}
	
	public Label getLblCancelAmount()
	{
		return lblCancelAmount;
	}
	
	public Label getLblCancelMoney()
	{
		return lblCancelMoney;
	}
	
	public Label getLblSpoilageMoney()
	{
		return lblSpoilageMoney;
	}
	
	public Label getLblGiveChangeMoney()
	{
		return lblGiveChangeMoney;
	}
	
	public Label getLblShouldInceptMoney()
	{
		return lblShouldInceptMoney;
	}
	
	public Label getLblFactInceptMoney()
	{
		return lblFactInceptMoney;
	}
	
	public Text getTxtDate()
	{
		return txtDate;
	}


}
