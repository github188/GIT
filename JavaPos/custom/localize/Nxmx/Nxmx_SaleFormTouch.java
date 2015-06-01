package custom.localize.Nxmx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.Design.SaleForm;
import com.swtdesigner.SWTResourceManager;

/**
 * @author yinl
 * @create 2011-2-28 下午04:12:32
 * @descri 文件说明
 */



public class Nxmx_SaleFormTouch extends SaleForm // Composite
{
	public Nxmx_SaleFormTouch(Composite functab,Composite parent,int style)
	{
		super(functab,parent,style);

	}

	/*
	 * public Label vipinfo = null; public Text focus = null; public StyledText
	 * zhongwenStyledText = null; public Label lbl_yfje = null; public Label
	 * yfje = null; public Label hjje = null; public Label hjzk = null; public
	 * Label lbl_hjzje = null; public Label hjzje = null; public Label hjzsl =
	 * null; public Label hjzke = null; public Label fphm = null; public Label
	 * syyh = null; public Group syjInfo = null; public Label fmType = null;
	 * public Text code = null; public Text gz = null; public Text yyyh = null;
	 * public PosTable table = null; public PosTable table_pop= null; public
	 * Group group_3 = null; public Group group_4 = null; public Group group_5 =
	 * null; public Shell parent = null; public SaleEvent sale = null; public
	 * Image bkimg = null;
	 * 
	 * public Group group_class = null; public Group group_group = null; public
	 * Group group_goods = null;
	 * 
	 * public Group class_page = null; public Group group_page = null; public
	 * Group goods_page = null;
	 * 
	 * public ControlBarForm ctrlform = null;
	 * 
	 * Label lbl_barcode = null; Label lbl_gz = null; Label lbl_yyy = null;
	 * 
	 * public CLabel[][] groupBotton = null; public String[][] groupCode = null;
	 * public CLabel[][] goodsBotton = null; public String[][] goodsCode = null;
	 * 
	 * public HashMap clist = null;
	 */

	protected void createTouchBtn()
	{
		composite_category = new Composite(composite, SWT.NONE);
		final FormData fd_composite_category = new FormData();
		fd_composite_category.left = new FormAttachment(0, 479);
		fd_composite_category.bottom = new FormAttachment(100, 0);
		fd_composite_category.top = new FormAttachment(0, 0);
		fd_composite_category.right = new FormAttachment(100, -5);
		composite_category.setLayoutData(fd_composite_category);
		composite_category.setLayout(new FormLayout());

		group_class = new Group(composite_category, SWT.NONE);
		final FormData fd_group_3_1 = new FormData();
		fd_group_3_1.left = new FormAttachment(0, 1);
		fd_group_3_1.bottom = new FormAttachment(0, 111);
		fd_group_3_1.top = new FormAttachment(0, 5);
		group_class.setLayoutData(fd_group_3_1);
		group_class.setLayout(new FormLayout());

		class_page = new Group(composite_category, SWT.NONE);
		fd_group_3_1.right = new FormAttachment(class_page, 0, SWT.LEFT);
		final FormData fd_class_page = new FormData();
		fd_class_page.left = new FormAttachment(100, -50);
		fd_class_page.bottom = new FormAttachment(0, 111);
		fd_class_page.right = new FormAttachment(100, -5);
		fd_class_page.top = new FormAttachment(0, 5);
		class_page.setLayoutData(fd_class_page);
		class_page.setLayout(new FormLayout());

		group_group = new Group(composite_category, SWT.NONE);
		final FormData fd_group_3 = new FormData();
		fd_group_3.left = new FormAttachment(0, 1);
		fd_group_3.bottom = new FormAttachment(0, 220);
		fd_group_3.top = new FormAttachment(0, 115);
		group_group.setLayoutData(fd_group_3);
		group_group.setLayout(new FormLayout());

		group_page = new Group(composite_category, SWT.NONE);
		// fd_group_3.right = new FormAttachment(group_page, 0, SWT.LEFT);
		fd_group_3.right = new FormAttachment(group_page, 0, SWT.LEFT);
		final FormData fd_group_page = new FormData();
		fd_group_page.left = new FormAttachment(100, -50);
		fd_group_page.bottom = new FormAttachment(0, 220);
		fd_group_page.top = new FormAttachment(0, 115);
		fd_group_page.right = new FormAttachment(100, -5);
		group_page.setLayoutData(fd_group_page);
		group_page.setLayout(new FormLayout());

		group_goods = new Group(composite_category, SWT.NONE);
		final FormData fd_group_6 = new FormData();
		// fd_group_6.right = new FormAttachment(class_page, -35, SWT.RIGHT);
		fd_group_6.bottom = new FormAttachment(100, -5);
		fd_group_6.top = new FormAttachment(0, 220);
		fd_group_6.left = new FormAttachment(0, 1);
		group_goods.setLayoutData(fd_group_6);
		group_goods.setLayout(new FormLayout());

		goods_page = new Group(composite_category, SWT.NONE);
		fd_group_6.right = new FormAttachment(goods_page, 0, SWT.LEFT);
		final FormData fd_goods_page = new FormData();
		fd_goods_page.left = new FormAttachment(100, -50);
		fd_goods_page.top = new FormAttachment(group_page, 0, SWT.DEFAULT);
		fd_goods_page.right = new FormAttachment(100, -5);
		fd_goods_page.bottom = new FormAttachment(100, -5);
		goods_page.setLayoutData(fd_goods_page);
		goods_page.setLayout(new FormLayout());
	}

	protected void createContents()
	{
		Rectangle area = Display.getCurrent().getPrimaryMonitor().getClientArea();
		setLayout(new FormLayout());
		this.setSize(800, 600);

		parent.layout();
		
		//新建一个composite,画到tabFolder上
		composite = new Composite(this, SWT.NONE);
		final FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, -5);
		fd_composite.right = new FormAttachment(100, 0);
		fd_composite.top = new FormAttachment(0, 0);
		fd_composite.left = new FormAttachment(0, 0);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new FormLayout());

		final Group group = new Group(composite, SWT.NONE);
		group.setVisible(false);
		group.setLayout(new FormLayout());

		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -475);
		formData.top = new FormAttachment(0, 0);
		formData.left = new FormAttachment(0, 10);
		group.setLayoutData(formData);

		final Label lbl_zke = new Label(group, SWT.NONE);
		final FormData fd_lbl_zke = new FormData();
		fd_lbl_zke.left = new FormAttachment(0, 212);
		fd_lbl_zke.right = new FormAttachment(0, 285);
		lbl_zke.setLayoutData(fd_lbl_zke);
		lbl_zke.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		lbl_zke.setText("折扣额");
		lbl_zke.setVisible(false);

		final Label lbl_cjj = new Label(group, SWT.NONE);
		lbl_cjj.setVisible(false);
		lbl_cjj.setForeground(SWTResourceManager.getColor(0, 0, 255));
		FormData fd_lbl_cjj;
		fd_lbl_cjj = new FormData();
		fd_lbl_cjj.top = new FormAttachment(0, 85);
		fd_lbl_cjj.right = new FormAttachment(0, 90);
		fd_lbl_cjj.left = new FormAttachment(0, 5);
		lbl_cjj.setLayoutData(fd_lbl_cjj);
		lbl_cjj.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		lbl_cjj.setText("成交价");

		hjje = new Label(group, SWT.NONE);
		hjje.setVisible(false);
		hjje.setForeground(SWTResourceManager.getColor(0, 0, 255));
		hjje.setAlignment(SWT.RIGHT);
		fd_lbl_zke.bottom = new FormAttachment(hjje, 20, SWT.TOP);
		fd_lbl_zke.top = new FormAttachment(hjje, 0, SWT.TOP);

		final FormData formData_15 = new FormData();
		formData_15.right = new FormAttachment(100, -9);
		formData_15.top = new FormAttachment(0, 75);
		formData_15.bottom = new FormAttachment(0, 108);
		formData_15.left = new FormAttachment(lbl_cjj, 5, SWT.RIGHT);
		hjje.setLayoutData(formData_15);
		hjje.setFont(SWTResourceManager.getFont("宋体", 25, SWT.BOLD));
		hjje.setText("00000000.00");

		hjzk = new Label(group, SWT.NONE);
		hjzk.setVisible(false);

		final FormData formData_16 = new FormData();
		formData_16.right = new FormAttachment(100, -5);
		formData_16.bottom = new FormAttachment(hjje, 20, SWT.TOP);
		formData_16.top = new FormAttachment(hjje, 0, SWT.TOP);
		formData_16.left = new FormAttachment(0, 285);
		hjzk.setLayoutData(formData_16);
		hjzk.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		hjzk.setText("00000000.00");

		Group group_1;
		group_1 = new Group(composite, SWT.NONE);

		final FormData formData_1 = new FormData();
		formData_1.right = new FormAttachment(0, 479);
		formData_1.top = new FormAttachment(group, 0, SWT.BOTTOM);
		formData_1.bottom = new FormAttachment(0, 55);
		formData_1.left = new FormAttachment(0, 10);
		group_1.setLayoutData(formData_1);

		lbl_hjzje = new Label(group_1, SWT.NONE);
		lbl_hjzje.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		lbl_hjzje.setText("总金额");
		lbl_hjzje.setBounds(10, 12, 50, 15);

		hjzje = new Label(group_1, SWT.NONE);
		hjzje.setAlignment(SWT.RIGHT);
		hjzje.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		hjzje.setText("00000000.00");
		hjzje.setBounds(65, 12, 130, 15);

		final Label label_8 = new Label(group_1, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		label_8.setText("单类型  ");
		label_8.setBounds(275, 12, 50, 15);

		fmType = new Label(group_1, SWT.NONE);
		fmType.setText("退货");
		fmType.setAlignment(SWT.RIGHT);
		fmType.setFont(SWTResourceManager.getFont("宋体", 12, SWT.BOLD));
		fmType.setBounds(325, 12, 130, 15);

		final Label label_10 = new Label(group_1, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		label_10.setText("总折扣");
		label_10.setBounds(10, 30, 50, 15);

		hjzsl = new Label(group_1, SWT.NONE);
		hjzsl.setAlignment(SWT.RIGHT);
		hjzsl.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		hjzsl.setText("00000");
		hjzsl.setBounds(325, 30, 130, 15);

		final Label label_12 = new Label(group_1, SWT.NONE);
		label_12.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		label_12.setText("总件数");
		label_12.setBounds(275, 30, 50, 15);

		hjzke = new Label(group_1, SWT.NONE);
		hjzke.setAlignment(SWT.RIGHT);
		hjzke.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		hjzke.setText("00000000.00");
		hjzke.setBounds(65, 30, 130, 15);

		zhongwenStyledText = new StyledText(group, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.NO_FOCUS);
		zhongwenStyledText.setVisible(false);
		zhongwenStyledText.setJustify(true);
		final FormData fd_zhongwenStyledText = new FormData();
		fd_zhongwenStyledText.top = new FormAttachment(0, 5);
		fd_zhongwenStyledText.right = new FormAttachment(100, -7);
		fd_zhongwenStyledText.left = new FormAttachment(0, 3);
		fd_zhongwenStyledText.bottom = new FormAttachment(hjje, -5, SWT.TOP);
		zhongwenStyledText.setLayoutData(fd_zhongwenStyledText);

		StyleRange styleRange = new StyleRange();
		styleRange.fontStyle = SWT.BOLD;
		zhongwenStyledText.setStyleRange(styleRange);
		fd_lbl_cjj.bottom = new FormAttachment(zhongwenStyledText, 34, SWT.BOTTOM);
		zhongwenStyledText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		// zhongwenStyledText.setBackground(group_2.getBackground());
		final FormData formData_17 = new FormData();
		formData_17.left = new FormAttachment(lbl_cjj, 0, SWT.LEFT);
		formData_17.bottom = new FormAttachment(0, 74);
		formData_17.right = new FormAttachment(100, -7);
		formData_17.top = new FormAttachment(0, 2);
		zhongwenStyledText.setLayoutData(formData_17);
		zhongwenStyledText.setText("中文中文中文中文中文\n中文中文中文中文中文");
		zhongwenStyledText.setFont(SWTResourceManager.getFont("宋体", 25, SWT.BOLD));
		zhongwenStyledText.setEditable(false);
		zhongwenStyledText.setEnabled(false);

		syjInfo = new Group(composite, SWT.SHADOW_NONE);

		final FormData formData_3 = new FormData();
		formData_3.right = new FormAttachment(0, 479);
		formData_3.bottom = new FormAttachment(0, 83);
		formData_3.top = new FormAttachment(0, 53);
		formData_3.left = new FormAttachment(0, 10);
		syjInfo.setLayoutData(formData_3);
		syjInfo.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));

		final Label label_14 = new Label(syjInfo, SWT.NONE);
		label_14.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		label_14.setText("收银机");
		label_14.setBounds(10, 12, 50, 15);

		fphm = new Label(syjInfo, SWT.NONE);

		fphm.setAlignment(SWT.RIGHT);
		fphm.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		fphm.setText("0000(00000000)");
		fphm.setBounds(65, 12, 130, 15);

		final Label label_17 = new Label(syjInfo, SWT.NONE);
		label_17.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		label_17.setText("收银员");
		label_17.setBounds(275, 12, 50, 15);

		syyh = new Label(syjInfo, SWT.NONE);
		syyh.setAlignment(SWT.RIGHT);
		syyh.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		syyh.setText("00000000");
		syyh.setBounds(325, 12, 130, 15);

		Group group_2;
		group_2 = new Group(composite, SWT.NONE);

		final FormData formData_2 = new FormData();
		formData_2.right = new FormAttachment(0, 479);
		formData_2.bottom = new FormAttachment(0, 111);
		formData_2.top = new FormAttachment(0, 81);
		formData_2.left = new FormAttachment(0, 10);
		group_2.setLayoutData(formData_2);

		final Label label = new Label(group_2, SWT.NONE);
		label.setBounds(10, 12, 50, 15);
		label.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		label.setText("顾客卡");

		vipinfo = new Label(group_2, SWT.NONE);
		vipinfo.setBounds(65, 12, 200, 15);
		vipinfo.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		vipinfo.setText("VIP");
		vipinfo.setText("");

		group_4 = new Group(composite, SWT.NONE);
		group_4.setLayout(new FormLayout());

		final FormData formData_4 = new FormData();
		formData_4.right = new FormAttachment(0, 479);
		formData_4.top = new FormAttachment(0, 115);
		formData_4.left = new FormAttachment(0, 10);
		group_4.setLayoutData(formData_4);
		group_4.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		group_4.setText("商品明细");

		table = new PosTable(group_4, SWT.BORDER | SWT.FULL_SELECTION, true);
		table.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NONE));

		final FormData formData_6 = new FormData();
		formData_6.right = new FormAttachment(100, -5);
		formData_6.bottom = new FormAttachment(100, -5);
		formData_6.left = new FormAttachment(0, 5);
		formData_6.top = new FormAttachment(0, 5);
		table.setLayoutData(formData_6);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 设置TABLE高度
		table.addListener(SWT.MeasureItem, new Listener()
		{
			// 向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。
			public void handleEvent(Event event)
			{
				event.width = table.getGridLineWidth(); // 设置宽度
				// event.height =
				// (int)Math.floor(event.gc.getFontMetrics().getHeight() * 3.5);
				// //设置高度为字体高度的2倍
				event.height = (int) Math.floor(event.gc.getFontMetrics().getHeight() * 2.5); // 设置高度为字体高度的2倍
			}
		});

		createTouchBtn();

		group_5 = new Group(composite, SWT.NONE);
		formData_4.bottom = new FormAttachment(group_5, -5, SWT.DEFAULT);
		group_5.setLayout(new FormLayout());

		final FormData formData_5 = new FormData();
		formData_5.top = new FormAttachment(100, -190);
		formData_5.right = new FormAttachment(0, 479);
		formData_5.left = new FormAttachment(0, 10);
		group_5.setLayoutData(formData_5);
		group_5.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		group_5.setText("输入区");

		final Label label_19 = new Label(group_5, SWT.NONE);
		label_19.setVisible(false);
		final FormData formData_7 = new FormData();
		formData_7.top = new FormAttachment(0, 5);
		formData_7.bottom = new FormAttachment(0, 20);
		formData_7.left = new FormAttachment(0, 140);
		formData_7.right = new FormAttachment(0, 190);
		label_19.setLayoutData(formData_7);
		label_19.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
		label_19.setText("营业员");

		int screenwidth = Display.getDefault().getBounds().width;
		if (screenwidth >= 800)
		{
			yyyh = new Text(group_5, SWT.BORDER);
			yyyh.setVisible(false);
			final FormData formData_8 = new FormData();
			formData_8.right = new FormAttachment(0, 220);
			formData_8.bottom = new FormAttachment(0, 22);
			formData_8.top = new FormAttachment(0, 2);
			formData_8.left = new FormAttachment(0, 189);
			yyyh.setLayoutData(formData_8);
			yyyh.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
			yyyh.setText("");

			Label label_20;
			label_20 = new Label(group_5, SWT.NONE);
			label_20.setVisible(false);
			final FormData formData_9 = new FormData();
			formData_9.bottom = new FormAttachment(0, 19);
			formData_9.top = new FormAttachment(0, 4);
			formData_9.right = new FormAttachment(0, 48);
			formData_9.left = new FormAttachment(0, 8);
			label_20.setLayoutData(formData_9);
			label_20.setAlignment(SWT.CENTER);
			label_20.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
			label_20.setText("柜组");

			gz = new Text(group_5, SWT.BORDER);
			gz.setVisible(false);
			final FormData formData_10 = new FormData();
			formData_10.bottom = new FormAttachment(0, 21);
			formData_10.top = new FormAttachment(0, 1);
			formData_10.right = new FormAttachment(0, 96);
			formData_10.left = new FormAttachment(0, 45);
			gz.setLayoutData(formData_10);
			gz.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
			gz.setText("");

			Label label_21;
			label_21 = new Label(group_5, SWT.NONE);
			final FormData formData_11 = new FormData();
			formData_11.bottom = new FormAttachment(0, 19);
			formData_11.top = new FormAttachment(0, 5);
			formData_11.right = new FormAttachment(0, 69);
			formData_11.left = new FormAttachment(0, 7);
			label_21.setLayoutData(formData_11);
			label_21.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));
			label_21.setText("商品条码");

			code = new Text(group_5, SWT.BORDER);
			code.setForeground(SWTResourceManager.getColor(255, 0, 0));
			final FormData formData_12 = new FormData();
			formData_12.right = new FormAttachment(0, 450);
			formData_12.bottom = new FormAttachment(0, 21);
			formData_12.top = new FormAttachment(0, 0);
			formData_12.left = new FormAttachment(0, 70);
			code.setLayoutData(formData_12);
			code.setFont(SWTResourceManager.getFont("宋体", 10, SWT.BOLD));
		}
		else
		{
			yyyh = new Text(group_5, SWT.BORDER);
			final FormData formData_8 = new FormData();
			formData_8.right = new FormAttachment(0, 120);
			formData_8.bottom = new FormAttachment(0, 40);
			formData_8.top = new FormAttachment(0, 10);
			formData_8.left = new FormAttachment(0, 82);
			yyyh.setLayoutData(formData_8);
			yyyh.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
			yyyh.setText("");

			Label label_20;
			label_20 = new Label(group_5, SWT.NONE);
			final FormData formData_9 = new FormData();
			formData_9.bottom = new FormAttachment(0, 42);
			formData_9.top = new FormAttachment(0, 12);
			formData_9.left = new FormAttachment(0, 125);
			formData_9.right = new FormAttachment(0, 212);
			label_20.setLayoutData(formData_9);
			label_20.setAlignment(SWT.CENTER);
			label_20.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
			label_20.setText("柜组");

			gz = new Text(group_5, SWT.BORDER);
			final FormData formData_10 = new FormData();
			formData_10.bottom = new FormAttachment(0, 40);
			formData_10.top = new FormAttachment(0, 10);
			formData_10.left = new FormAttachment(0, 212);
			formData_10.right = new FormAttachment(0, 320);
			gz.setLayoutData(formData_10);
			gz.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
			gz.setText("");

			Label label_21;
			label_21 = new Label(group_5, SWT.NONE);
			final FormData formData_11 = new FormData();
			formData_11.bottom = new FormAttachment(0, 42);
			formData_11.top = new FormAttachment(0, 12);
			formData_11.right = new FormAttachment(0, 428);
			formData_11.left = new FormAttachment(gz, 5, SWT.RIGHT);
			label_21.setLayoutData(formData_11);
			label_21.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
			label_21.setText("商品条码");

			code = new Text(group_5, SWT.BORDER);
			code.setForeground(SWTResourceManager.getColor(255, 0, 0));
			final FormData formData_12 = new FormData();
			formData_12.left = new FormAttachment(label_21, 0, SWT.RIGHT);
			formData_12.bottom = new FormAttachment(0, 40);
			formData_12.top = new FormAttachment(0, 10);
			formData_12.right = new FormAttachment(100, -5);
			code.setLayoutData(formData_12);
			code.setFont(SWTResourceManager.getFont("宋体", 17, SWT.BOLD));
		}

		FocusListener listener = new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				if (focus != null && focus != e.widget)
				{
					if (!code.getEnabled() && composite_pay != null && composite_pay.getVisible())
					{
						composite_pay.setFocus();
						return;
					}
					else if ((!code.getEnabled() && composite_finished != null && composite_finished.getVisible()))
					{
						composite_finished.setFocus();
						return;
					}
					
					focus.setFocus();
				}
			}

			public void focusLost(FocusEvent e)
			{
			}
		};

		yyyh.addFocusListener(listener);
		gz.addFocusListener(listener);
		code.addFocusListener(listener);
		table.addFocusListener(listener);

		final TableColumn newColumn = new TableColumn(table, SWT.NONE);
		newColumn.setWidth(30);
		newColumn.setText("序");

		final TableColumn newColumn1 = new TableColumn(table, SWT.NONE);
		if (screenwidth >= 800)
			newColumn1.setWidth(100);
		else
			newColumn1.setWidth(70);
		newColumn1.setText("商品条码");

		final TableColumn newColumn2 = new TableColumn(table, SWT.NONE);
		newColumn2.setWidth(80);
		int append = area.width - 654 - 480;
		newColumn2.setWidth(50 + append / 2);
		newColumn2.setText("商品名称");

		final TableColumn newColumn3 = new TableColumn(table, SWT.NONE);
		newColumn3.setToolTipText("80");
		newColumn3.setAlignment(SWT.CENTER);
		newColumn3.setWidth(40);
		newColumn3.setText("单位");

		final TableColumn newColumn4 = new TableColumn(table, SWT.NONE);
		newColumn4.setAlignment(SWT.RIGHT);
		newColumn4.setWidth(80);
		newColumn4.setText("数量");

		final TableColumn newColumn5 = new TableColumn(table, SWT.NONE);
		newColumn5.setAlignment(SWT.RIGHT);
		newColumn5.setWidth(100);
		newColumn5.setText("单价");

		final TableColumn newColumn6 = new TableColumn(table, SWT.NONE);
		newColumn6.setAlignment(SWT.RIGHT);
		newColumn6.setWidth(100);
		newColumn6.setText("折扣");

		final TableColumn newColumn8 = new TableColumn(table, SWT.NONE);
		newColumn8.setAlignment(SWT.RIGHT);
		newColumn8.setWidth(120);
		newColumn8.setText("应收金额");

		final TableItem newItemTableItem = new TableItem(table, SWT.BORDER);
		newItemTableItem.setText(7, "1234567.00");
		newItemTableItem.setText(6, "1234.00");
		newItemTableItem.setText(5, "1234567.00");
		newItemTableItem.setText(4, "123.3456");
		newItemTableItem.setText(3, "条条");
		newItemTableItem.setText(2, "一一一一一一一一");
		newItemTableItem.setText(1, "1234567890123");
		newItemTableItem.setText(0, "1");
		// newItemTableItem.setText("New item");
		zhongwenStyledText.addFocusListener(listener);

		Group group_6;
		group_6 = new Group(composite, SWT.NONE);
		formData_5.bottom = new FormAttachment(group_6, -5, SWT.TOP);
		group_6.setText("商品促销");
		final FormData fd_group_6_1 = new FormData(100, -491);
		fd_group_6_1.left = new FormAttachment(0, 9);
		fd_group_6_1.top = new FormAttachment(100, -145);
		fd_group_6_1.right = new FormAttachment(0, 479);
		fd_group_6_1.bottom = new FormAttachment(100, -5);
		group_6.setLayoutData(fd_group_6_1);
		group_6.setLayout(new FormLayout());

		table_pop = new PosTable(group_6, SWT.BORDER);
		final FormData formData_8 = new FormData();
		formData_8.bottom = new FormAttachment(100, -5);
		formData_8.right = new FormAttachment(100, -5);
		formData_8.left = new FormAttachment(0, 6);
		formData_8.top = new FormAttachment(0, 12);
		table_pop.setLayoutData(formData_8);
		final FormData formData_6_1 = new FormData();
		formData_6_1.right = new FormAttachment(100, -5);
		formData_6_1.bottom = new FormAttachment(100, -5);
		formData_6_1.top = new FormAttachment(0, 5);
		formData_6_1.left = new FormAttachment(0, 5);
		table_pop.setLayoutData(formData_6_1);
		table_pop.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NONE));
		table_pop.setLinesVisible(true);
		table_pop.setHeaderVisible(true);

		table_pop.addFocusListener(listener);
		
		final TableColumn newColumn_0 = new TableColumn(table_pop, SWT.NONE);
		newColumn_0.setAlignment(SWT.CENTER);
		newColumn_0.setWidth(80);
		newColumn_0.setText("促销序号");
		
		final TableColumn newColumn_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn_1.setAlignment(SWT.CENTER);
		newColumn_1.setWidth(80);
		newColumn_1.setText("档期ID");

		final TableColumn newColumn1_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn1_1.setAlignment(SWT.CENTER);
		newColumn1_1.setWidth(80);
		newColumn1_1.setText("档期名称");

		final TableColumn newColumn2_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn2_1.setAlignment(SWT.CENTER);
		newColumn2_1.setWidth(120);
		newColumn2_1.setText("规则ID");

		final TableColumn newColumn3_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn3_1.setAlignment(SWT.CENTER);
		newColumn3_1.setWidth(80);
		newColumn3_1.setText("规则名称");

		final TableColumn newColumn4_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn4_1.setAlignment(SWT.CENTER);
		newColumn4_1.setWidth(90);
		newColumn4_1.setText("商品编码");

		final TableColumn newColumn5_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn5_1.setAlignment(SWT.CENTER);
		newColumn5_1.setWidth(100);
		newColumn5_1.setText("参与方式");
		
		final TableColumn newColumn6_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn6_1.setAlignment(SWT.CENTER);
		newColumn6_1.setWidth(100);
		newColumn6_1.setText("条件方式");

		final TableColumn newColumn8_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn8_1.setAlignment(SWT.CENTER);
		newColumn8_1.setWidth(80);
		newColumn8_1.setText("开始日期");
		
		final TableColumn newColumn9_1 = new TableColumn(table_pop, SWT.NONE);
		newColumn9_1.setAlignment(SWT.CENTER);
		newColumn9_1.setWidth(80);
		newColumn9_1.setText("结束日期");

		// 设置TABLE高度
		table_pop.addListener(SWT.MeasureItem, new Listener()
		{
			// 向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。
			public void handleEvent(Event event)
			{
				event.width = table_pop.getGridLineWidth(); // 设置宽度
				event.height = (int) Math.floor(event.gc.getFontMetrics().getHeight() * 2.5); // 设置高度为字体高度的2倍
			}
		});

/*		final TableItem newItemTableItemPop = new TableItem(table_pop, SWT.BORDER);
		newItemTableItemPop.setText(7, " ");
		newItemTableItemPop.setText(6, "2011/10/20 00:00:00");
		newItemTableItemPop.setText(5, "2011/10/01 00:00:00");
		newItemTableItemPop.setText(4, "00001");
		newItemTableItemPop.setText(3, "组合促销");
		newItemTableItemPop.setText(2, "0002190888");
		newItemTableItemPop.setText(1, "888");
		newItemTableItemPop.setText(0, "1");*/

		lbl_yfje = new Label(group, SWT.NONE);
		lbl_yfje.setVisible(false);
		lbl_yfje.setForeground(SWTResourceManager.getColor(255, 0, 0));
		final FormData formData_18 = new FormData();
		formData_18.bottom = new FormAttachment(100, -5);
		formData_18.top = new FormAttachment(0, 114);
		formData_18.right = new FormAttachment(0, 90);
		formData_18.left = new FormAttachment(lbl_cjj, 0, SWT.LEFT);
		lbl_yfje.setLayoutData(formData_18);
		lbl_yfje.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		lbl_yfje.setText("应付额");

		yfje = new Label(group, SWT.NONE);
		yfje.setVisible(false);
		yfje.setForeground(SWTResourceManager.getColor(255, 0, 0));
		yfje.setAlignment(SWT.RIGHT);
		final FormData formData_19 = new FormData();
		formData_19.top = new FormAttachment(hjje, 0, SWT.BOTTOM);
		formData_19.bottom = new FormAttachment(100, -5);
		formData_19.right = new FormAttachment(100, -9);
		formData_19.left = new FormAttachment(lbl_yfje, 5, SWT.RIGHT);
		yfje.setLayoutData(formData_19);
		yfje.setFont(SWTResourceManager.getFont("宋体", 25, SWT.BOLD));
		yfje.setText("00000000.00");

		// 清空模拟数据
		zhongwenStyledText.setText("");
		hjje.setText("");
		hjzk.setText("");
		hjzsl.setText("");
		fphm.setText("");
		syyh.setText("");
		yfje.setText("");
		hjzje.setText("");

		// clistInit();
		// 创建EVENT
		sale = new Nxmx_SaleEventTouch(this);

		// 自动设置窗口大小,加载背景图片GlobalVar.rec.x, GlobalVar.rec.y - 60
		this.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60);
		bkimg = ConfigClass.changeBackgroundImage(this,this.composite, null);

/*		// 创建触屏操作按钮栏
		Vector vc = ControlBarForm.createMouseControlBar(this, this);
		for (int i = 0; i < vc.size(); i++)
		{
			ControlBarForm cbf = (ControlBarForm) vc.get(i);
			if (cbf.curbarstyle == ControlBarForm.BarStyle_Bottom)
			{
				ctrlform = cbf;
			}
		}

		if (ctrlform != null)
		{
			composite.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60 - ctrlform.mheight);
			ctrlform.setBounds(composite.getBounds().x + ctrlform.mx, composite.getBounds().y + composite.getBounds().height + ctrlform.my, ctrlform.mwidth > 0 ? ctrlform.mwidth : composite.getBounds().width - ctrlform.mx, ctrlform.mheight);
		}
		else
		{
			composite.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60);
		}*/
	}
}
