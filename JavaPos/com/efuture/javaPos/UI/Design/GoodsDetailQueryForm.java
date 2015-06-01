package com.efuture.javaPos.UI.Design;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.ICallBack;
import com.efuture.javaPos.UI.GoodsDetailQueryEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

public class GoodsDetailQueryForm 
{

	private PosTable tabpop;
	private Text txtMemo = null;
	private Text txtPickGoods = null;
	private Text txtFittings = null;
	private Text txtBalance = null;
	private Text txtLargess = null;
	private Text txtWholesaleAgioPartake = null;
	private Text txtMemberAgioPartake = null;
	private Text txtStockpile = null;
	private Text txtAgioRate = null;
	private Text txtPricePrecision = null;
	private Text txtMinBatch = null;
	private Text txtManyUnit = null;
	private Text txtVipAgio = null;
	private Text txtAgio = null;
	private Text txtBatchCode = null;
	private Text txtQuashRed = null;
	private Text txtAgioQuota = null;
	private Text txtWholesalePrice = null;
	private Text txtMemberPrice = null;
	private Text txtRetailPrice = null;
	private Text txtContent = null;
	private Text txtUnit = null;
	private Text txtGoodsBrand = null;
	private Text txtGoodsName = null;
	private Text txtAnalyseCode = null;
	private Text txtGoodsCode = null;
	private Text txtGoodsBarCode = null;
	private Text txtCodeType = null;
	private Text txtArkGroup = null;
		
	protected Shell shell = null;
	
	public GoodsDetailQueryForm(StringBuffer sbBarcode,ICallBack callBack,GoodsDef goods,ArrayList batchList,ArrayList yhList,StringBuffer sbIsCloseForm)
	{
		this.open(sbBarcode,callBack,goods,batchList,yhList,sbIsCloseForm);
	}
	
	public void open(StringBuffer sbBarcode,ICallBack callBack,GoodsDef goods,ArrayList batchList,ArrayList yhList,StringBuffer sbIsCloseForm) 
	{
		final Display display = Display.getDefault();
		createContents();

		new GoodsDetailQueryEvent(sbBarcode,callBack,this,goods,batchList,yhList,sbIsCloseForm);

		//创建触屏操作按钮栏 
        ControlBarForm.createMouseControlBar(this,shell);
        
		 //加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        if (!shell.isDisposed())
        {		
			//Rectangle area = Display.getDefault().getPrimaryMonitor().getClientArea();
			shell.setBounds(GlobalVar.rec.x/2-shell.getSize().x/2,GlobalVar.rec.y/2-shell.getSize().y/2 ,shell.getSize().x,shell.getSize().y-GlobalVar.heightPL);
	
			shell.open();
			
			shell.layout();
        }
        
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
//		释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
	}


	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		shell.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		shell.setSize(800, 510);
		shell.setText(Language.apply("商品明细  (按 确认键 添加商品)"));
		
		final Group yGroup = new Group(shell, SWT.NONE);
		yGroup.setBounds(10, 0, 774, 296);

		final Label label_1 = new Label(yGroup, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText(Language.apply("商品柜组"));
		label_1.setBounds(540, 55, 80, 20);

		txtArkGroup = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtArkGroup.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtArkGroup.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtArkGroup.setBounds(625, 50, 142, 26);

		final Label label_2 = new Label(yGroup, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText(Language.apply("编码类型"));
		label_2.setBounds(5, 90, 79, 20);

		txtCodeType = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtCodeType.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtCodeType.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtCodeType.setBounds(90, 85, 189, 26);
		
		final Label label = new Label(yGroup, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText(Language.apply("商品条码"));
		label.setBounds(5, 20, 79, 20);

		txtGoodsBarCode = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtGoodsBarCode.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtGoodsBarCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtGoodsBarCode.setBounds(90, 15, 189, 26);

		final Label label_3 = new Label(yGroup, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_3.setText(Language.apply("商品代码"));
		label_3.setBounds(290, 20, 79, 20);

		txtGoodsCode = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtGoodsCode.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtGoodsCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtGoodsCode.setBounds(375, 18, 154, 26);

		final Label label_4 = new Label(yGroup, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_4.setText(Language.apply("分 析 码"));
		label_4.setBounds(540, 20, 80, 20);

		txtAnalyseCode = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtAnalyseCode.setBackground(SWTResourceManager.getColor(255, 255, 255));
		txtAnalyseCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtAnalyseCode.setBounds(625, 15, 142, 26);

		final Label label_5 = new Label(yGroup, SWT.NONE);
		label_5.setBounds(5, 55, 79, 20);
		label_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_5.setText(Language.apply("商品名称"));

		txtGoodsName = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtGoodsName.setBounds(90, 50, 439, 26);
		txtGoodsName.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtGoodsName.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1 = new Label(yGroup, SWT.NONE);
		label_6_1.setBounds(290, 90, 80, 20);
		label_6_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1.setText(Language.apply("商品品牌"));

		txtGoodsBrand = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtGoodsBrand.setBounds(380, 85, 149, 26);
		txtGoodsBrand.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtGoodsBrand.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_1 = new Label(yGroup, SWT.NONE);
		label_6_1_1.setBounds(540, 90, 80, 20);
		label_6_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_1.setText(Language.apply("商品单位"));

		txtUnit = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtUnit.setBounds(625, 85, 142, 26);
		txtUnit.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtUnit.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_1_1 = new Label(yGroup, SWT.NONE);
		label_6_1_1_1.setBounds(540, 193, 80, 20);
		label_6_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_1_1.setText(Language.apply("商品含量"));

		txtContent = new Text(yGroup, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtContent.setBounds(625, 190, 142, 26);
		txtContent.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtContent.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_2 = new Label(yGroup, SWT.NONE);
		label_6_2.setBounds(5, 125, 80, 20);
		label_6_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_2.setText(Language.apply("零售价格"));

		txtRetailPrice = new Text(yGroup, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtRetailPrice.setBounds(90, 120, 189, 26);
		txtRetailPrice.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtRetailPrice.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_2 = new Label(yGroup, SWT.NONE);
		label_6_1_2.setBounds(290, 125, 79, 20);
		label_6_1_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_2.setText(Language.apply("会员价格"));

		txtMemberPrice = new Text(yGroup, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtMemberPrice.setBounds(380, 120, 149, 26);
		txtMemberPrice.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMemberPrice.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_2_1 = new Label(yGroup, SWT.NONE);
		label_6_1_2_1.setBounds(540, 125, 80, 20);
		label_6_1_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_2_1.setText(Language.apply("批发价格"));

		txtWholesalePrice = new Text(yGroup, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtWholesalePrice.setBounds(625, 120, 142, 26);
		txtWholesalePrice.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtWholesalePrice.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_2_1 = new Label(yGroup, SWT.NONE);
		label_6_2_1.setBounds(5, 160, 79, 20);
		label_6_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_2_1.setText(Language.apply("折扣限额"));

		txtAgioQuota = new Text(yGroup, SWT.RIGHT | SWT.READ_ONLY | SWT.BORDER);
		txtAgioQuota.setBounds(90, 155, 189, 26);
		txtAgioQuota.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtAgioQuota.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_2_2 = new Label(yGroup, SWT.NONE);
		label_6_1_2_2.setBounds(5, 265, 40, 20);
		label_6_1_2_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_2_2.setText(Language.apply("销红"));

		txtQuashRed = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtQuashRed.setBounds(52, 260, 17, 26);
		txtQuashRed.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtQuashRed.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_2_2_1 = new Label(yGroup, SWT.NONE);
		label_6_1_2_2_1.setBounds(75, 265, 40, 20);
		label_6_1_2_2_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_2_2_1.setText(Language.apply("批号"));

		txtBatchCode = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtBatchCode.setBounds(120, 260, 17, 26);
		txtBatchCode.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtBatchCode.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_2_2_1_1 = new Label(yGroup, SWT.NONE);
		label_6_1_2_2_1_1.setBounds(145, 265, 40, 20);
		label_6_1_2_2_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_2_2_1_1.setText(Language.apply("折扣"));

		txtAgio = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtAgio.setBounds(190, 260, 17, 26);
		txtAgio.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtAgio.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_2_2_1_1_1 = new Label(yGroup, SWT.NONE);
		label_6_1_2_2_1_1_1.setBounds(215, 265, 50, 20);
		label_6_1_2_2_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_2_2_1_1_1.setText(Language.apply("vip折"));

		txtVipAgio = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtVipAgio.setBounds(270, 260, 17, 26);
		txtVipAgio.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtVipAgio.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_6_1_2_2_1_1_1_1 = new Label(yGroup, SWT.NONE);
		label_6_1_2_2_1_1_1_1.setBounds(295, 265, 60, 20);
		label_6_1_2_2_1_1_1_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6_1_2_2_1_1_1_1.setText(Language.apply("多单位"));

		txtManyUnit = new Text(yGroup, SWT.READ_ONLY | SWT.BORDER);
		txtManyUnit.setBounds(360, 260, 17, 26);
		txtManyUnit.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtManyUnit.setBackground(SWTResourceManager.getColor(255, 255, 255));

		final Label label_7 = new Label(yGroup, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_7.setText(Language.apply("最小批量"));
		label_7.setBounds(4, 193, 85, 18);

		txtMinBatch = new Text(yGroup, SWT.BORDER);
		txtMinBatch.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMinBatch.setBounds(90, 190, 189, 26);

		final Label label_10 = new Label(yGroup, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_10.setText(Language.apply("价格精度"));
		label_10.setBounds(540, 160, 80, 24);

		txtPricePrecision = new Text(yGroup, SWT.RIGHT | SWT.BORDER);
		txtPricePrecision.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtPricePrecision.setBounds(625, 155, 142, 26);

		final Label label_11 = new Label(yGroup, SWT.NONE);
		label_11.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_11.setText(Language.apply("折扣限率"));
		label_11.setBounds(290, 160, 80, 20);

		txtAgioRate = new Text(yGroup, SWT.RIGHT | SWT.BORDER);
		txtAgioRate.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtAgioRate.setBounds(380, 155, 149, 26);

		final Label label_14 = new Label(yGroup, SWT.NONE);
		label_14.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_14.setText(Language.apply("库存数量"));
		label_14.setBounds(290, 190, 80, 20);

		txtStockpile = new Text(yGroup, SWT.RIGHT | SWT.BORDER);
		txtStockpile.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtStockpile.setBounds(380, 190, 149, 26);

		final Label label_15 = new Label(yGroup, SWT.NONE);
		label_15.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_15.setText(Language.apply("会员分担"));
		label_15.setBounds(5, 230, 80, 19);

		txtMemberAgioPartake = new Text(yGroup, SWT.RIGHT | SWT.BORDER);
		txtMemberAgioPartake.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMemberAgioPartake.setBounds(90, 225, 189, 26);

		final Label label_16 = new Label(yGroup, SWT.NONE);
		label_16.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_16.setText(Language.apply("批发分担"));
		label_16.setBounds(290, 230, 87, 20);

		txtWholesaleAgioPartake = new Text(yGroup, SWT.RIGHT | SWT.BORDER);
		txtWholesaleAgioPartake.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtWholesaleAgioPartake.setBounds(380, 225, 149, 26);

		final Label label_18 = new Label(yGroup, SWT.NONE);
		label_18.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_18.setText(Language.apply("赠送"));
		label_18.setBounds(385, 265, 40, 19);

		txtLargess = new Text(yGroup, SWT.BORDER);
		txtLargess.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtLargess.setBounds(430, 260, 17, 26);

		final Label label_19 = new Label(yGroup, SWT.NONE);
		label_19.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_19.setText(Language.apply("电子秤"));
		label_19.setBounds(455, 265, 59, 18);

		txtBalance = new Text(yGroup, SWT.BORDER);
		txtBalance.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtBalance.setBounds(520, 260, 17, 26);

		final Label label_20 = new Label(yGroup, SWT.NONE);
		label_20.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_20.setText(Language.apply("配件"));
		label_20.setBounds(545, 265, 40, 19);

		txtFittings = new Text(yGroup, SWT.BORDER);
		txtFittings.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtFittings.setBounds(590, 260, 17, 26);

		final Label label_21 = new Label(yGroup, SWT.NONE);
		label_21.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_21.setText(Language.apply("自提"));
		label_21.setBounds(615, 265, 40, 18);

		txtPickGoods = new Text(yGroup, SWT.BORDER);
		txtPickGoods.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtPickGoods.setBounds(660, 260, 17, 26);

		final Label label_6 = new Label(yGroup, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_6.setText(Language.apply("商品备注"));
		label_6.setBounds(540, 230, 80, 20);

		txtMemo = new Text(yGroup, SWT.BORDER);
		txtMemo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		txtMemo.setBounds(625, 227, 142, 26);

		tabpop = new PosTable(shell, SWT.FULL_SELECTION | SWT.BORDER);
		tabpop.setBounds(10, 302, 774, 173);
		tabpop.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		tabpop.setLinesVisible(true);
		tabpop.setHeaderVisible(true);

		final TableColumn tableColumn_1 = new TableColumn(tabpop, SWT.NONE);
		tableColumn_1.setWidth(80);
		tableColumn_1.setText(Language.apply("类型"));

		final TableColumn newColumnTableColumn = new TableColumn(tabpop, SWT.NONE);
		newColumnTableColumn.setWidth(172);
		newColumnTableColumn.setText(Language.apply("开始日期"));

		final TableColumn tableColumn = new TableColumn(tabpop, SWT.NONE);
		tableColumn.setWidth(172);
		tableColumn.setText(Language.apply("结束日期"));

		final TableColumn newColumnTableColumn_1 = new TableColumn(tabpop, SWT.NONE);
		newColumnTableColumn_1.setAlignment(SWT.RIGHT);
		newColumnTableColumn_1.setWidth(115);
		newColumnTableColumn_1.setText(Language.apply("优惠零售价"));

		final TableColumn newColumnTableColumn_2 = new TableColumn(tabpop, SWT.NONE);
		newColumnTableColumn_2.setAlignment(SWT.RIGHT);
		newColumnTableColumn_2.setWidth(115);
		newColumnTableColumn_2.setText(Language.apply("优惠会员价"));

		final TableColumn newColumnTableColumn_3 = new TableColumn(tabpop, SWT.NONE);
		newColumnTableColumn_3.setAlignment(SWT.RIGHT);
		newColumnTableColumn_3.setWidth(115);
		newColumnTableColumn_3.setText(Language.apply("优惠批发价"));

		
	}
	
	public Text getTxtMemo()
	{
		return txtMemo;
	}
	
	public Text getTxtPickGoods()
	{
		return txtPickGoods;
	}
	
	public Text getTxtFittings()
	{
		return txtFittings;
	}
	
	public Text getTxtBalance()
	{
		return txtBalance;
	}
	
	public Text getTxtLargess()
	{
		return txtLargess;
	}
	
	
	public Text getTxtWholesaleAgioPartake()
	{
		return txtWholesaleAgioPartake;
	}
	
	public Text getTxtMemberAgioPartake()
	{
		return txtMemberAgioPartake;
	}
	
	public Text getTxtStockpile()
	{
		return txtStockpile;
	}
	
	
	public Text getTxtAgioRate()
	{
		return txtAgioRate;
	}
	
	public Text getTxtPricePrecision()
	{
		return txtPricePrecision;
	}
	
	public Text getTxtMinBatch()
	{
		return txtMinBatch;
	}

	public Text getTxtManyUnit()
	{
		return txtManyUnit;
	}

	
	public Text getTxtVipAgio()
	{
		return txtVipAgio;
	}
	
	public Text getTxtAgio()
	{
		return txtAgio;
	}
	
	public Text getTxtBatchCode()
	{
		return txtBatchCode;
	}
	
	public Text getTxtQuashRed()
	{
		return txtQuashRed;
	}
	
	public Text getTxtAgioQuota()
	{
		return txtAgioQuota;
	}
	
	public Text getTxtWholesalePrice()
	{
		return txtWholesalePrice;
	}
	
	public Text getTxtMemberPrice()
	{
		return txtMemberPrice;
	}
	
	public Text getTxtRetailPrice()
	{
		return txtRetailPrice;
	}
	
	public Text getTxtContent()
	{
		return txtContent;
	}
	
	public Text getTxtUnit()
	{
		return txtUnit;
	}
	
	public Text getTxtGoodsBrand()
	{
		return txtGoodsBrand;
	}
	
	
	public Text getTxtGoodsName()
	{
		return txtGoodsName;
	}
	
	public Text getTxtAnalyseCode()
	{
		return txtAnalyseCode;
	}
	
	public Text getTxtGoodsCode()
	{
		return txtGoodsCode;
	}
	
	public Text getTxtGoodsBarCode()
	{
		return txtGoodsBarCode;
	}
	
	public Text getTxtCodeType()
	{
		return txtCodeType;
	}
	
	public Text getTxtArkGroup()
	{
		return txtArkGroup;
	}
	
	public PosTable getTabpop()
	{
		return tabpop;
	}
	
	public Shell getShell()
	{
		return shell;
	}
}
