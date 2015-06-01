package com.efuture.javaPos.UI.Design;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.ICallBack;
import com.efuture.javaPos.UI.GoodsStockQueryEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class GoodsStockQueryForm 
{
	private Label label_Pay = null;
	private Label label_Clear = null;
	private Label label_Enter = null;
	private Text txtCode2;
	private Combo combo = null;
	private PosTable tabGoods = null;
	private Text txtCode = null;
	private Shell shell = null;
	private Label lblBetween = null;

	public GoodsStockQueryForm(StringBuffer sbBarcode,ICallBack callBack)
	{		
		this.open(sbBarcode ,callBack);
	}

	public void open(StringBuffer sbBarcode,ICallBack callBack) 
	{
		final Display display = Display.getDefault();
		createContents();
		new GoodsStockQueryEvent(this,sbBarcode,callBack);
		
		//创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
		 //加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		//释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}


	protected void createContents() 
	{	
		shell = new Shell(GlobalVar.style|SWT.SHADOW_ETCHED_OUT); //new Shell(GlobalVar.style);
		//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
		shell.setSize(800, 510);
		shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
		shell.setText(Language.apply("库存查询"));

		final Group group = new Group(shell, SWT.NONE);
		group.setBounds(15, -3, 766, 43);

		txtCode = new Text(group, SWT.BORDER);
		if(!GlobalInfo.sysPara.setTextLimit.equals("0")){
			txtCode.setTextLimit(Integer.parseInt(GlobalInfo.sysPara.setTextLimit));
		}
		txtCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtCode.setBounds(200, 11, 260, 27);

		combo = new Combo(group, SWT.NONE);
		combo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		combo.setBounds(5, 11, 180, 28);
		String strlist[] = {Language.apply("商品流水码"),Language.apply("商品货号"),Language.apply("商品编码")};
		combo.setItems(strlist);
		combo.select(0);
		combo.setFocus();

		txtCode2 = new Text(group, SWT.BORDER);
		txtCode2.setVisible(false);
		txtCode2.setBounds(495, 11, 260, 27);
		if(!GlobalInfo.sysPara.setTextLimit.equals("0")){
			txtCode.setTextLimit(Integer.parseInt(GlobalInfo.sysPara.setTextLimit));
		}
		txtCode2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

		lblBetween = new Label(group, SWT.NONE);
		lblBetween.setVisible(false);
		lblBetween.setText("-");
		lblBetween.setBounds(470, 15, 30, 27);
		lblBetween.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		
		tabGoods = new PosTable(shell,SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tabGoods.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabGoods.setLinesVisible(true);
		tabGoods.setHeaderVisible(true);
		tabGoods.setBounds(15, 45, 766, 365);

		final TableColumn newColumnTableColumn = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn.setWidth(42);
		newColumnTableColumn.setText(Language.apply("行"));
		
		final TableColumn newColumnTableColumn_1 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_1.setWidth(143);
		newColumnTableColumn_1.setText(Language.apply("商品大码"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_2.setWidth(100);
		newColumnTableColumn_2.setText(Language.apply("厂商货号"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_3.setWidth(127);
		newColumnTableColumn_3.setText(Language.apply("商品编码"));

		final TableColumn newColumnTableColumn_4 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_4.setWidth(58);
		newColumnTableColumn_4.setText(Language.apply("年份"));

		final TableColumn newColumnTableColumn_5 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_5.setWidth(52);
		newColumnTableColumn_5.setText(Language.apply("季节"));

		final TableColumn newColumnTableColumn_6 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_6.setAlignment(SWT.RIGHT);
		newColumnTableColumn_6.setWidth(78);
		newColumnTableColumn_6.setText(Language.apply("颜色"));
		
		final TableColumn newColumnTableColumn_7 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_7.setAlignment(SWT.RIGHT);
		newColumnTableColumn_7.setWidth(72);
		newColumnTableColumn_7.setText(Language.apply("尺码"));
		
		final TableColumn newColumnTableColumn_8 = new TableColumn(tabGoods, SWT.NONE);
		newColumnTableColumn_8.setAlignment(SWT.RIGHT);
		newColumnTableColumn_8.setWidth(86);
		newColumnTableColumn_8.setText(Language.apply("库存"));
		
		final Composite composite = new Composite(shell, SWT.BORDER);
		composite.setBounds(25, 416,71, 28);
		label_Enter = new Label(composite, SWT.NONE);
		label_Enter.setAlignment(SWT.CENTER);
		label_Enter.setBounds(0, 2,68, 21);
		label_Enter.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_Enter.setText(Language.apply("回车键"));

		final Label label_3 = new Label(shell, SWT.NONE);
		label_3.setBounds(100, 420,154, 27);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("查询或添加商品"));

		final Composite composite_2_1_1 = new Composite(shell, SWT.BORDER);
		composite_2_1_1.setBounds(305, 415, 71, 28);

		label_Clear = new Label(composite_2_1_1, SWT.NONE);
		label_Clear.setAlignment(SWT.CENTER);
		label_Clear.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_Clear.setBounds(0, 2, 68, 21);
		label_Clear.setText(Language.apply("清除键"));

		final Label label_3_1_1_1 = new Label(shell, SWT.NONE);
		label_3_1_1_1.setBounds(380, 420, 136, 27);
		label_3_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3_1_1_1.setText(Language.apply("清除查询结果"));

		final Label label_1_1 = new Label(shell, SWT.NONE);
		label_1_1.setBounds(640, 420, 127, 20);
		label_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1_1.setText(Language.apply("切换输入焦点"));

		final Composite composite_1_1 = new Composite(shell, SWT.BORDER);
		composite_1_1.setBounds(567, 415, 71, 28);

		label_Pay = new Label(composite_1_1, SWT.NONE);
		label_Pay.setAlignment(SWT.CENTER);
		label_Pay.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_Pay.setBounds(-1, 2, 71, 21);
		label_Pay.setText(Language.apply("付款键"));
	}
	
	public PosTable getTabGoods()
	{
		return tabGoods;
	}
	
	public Text getTxtCode()
	{
		return txtCode;
	}
	
	public Label getLblBetween()
	{
		return lblBetween;
	}
	
	public Text getTxtCode2()
	{
		return txtCode2;
	}
	
	public Combo getCombo()
	{
		return combo;
	}
	
	public Shell getShell()
	{
		return shell;
	}
	
	public Label getLabel_Enter()
	{
		return label_Enter;
	}
	
	
	public Label getLabel_Pay()
	{
		return label_Pay;
	}
	
	public Label getLabel_Clear()
	{
		return label_Clear;
	}
	
}
