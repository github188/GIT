package com.efuture.javaPos.UI.Design;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.SaleEvent;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;


public class SaleForm extends Composite
{
	//=============通用控件=====================
	public Composite composite = null;

	public Label vipinfo = null;
	public Text focus = null;
    public StyledText zhongwenStyledText = null;
	public Label lbl_yfje = null;
	public Label yfje = null;
    public Label hjje = null;
    public Label hjzk = null;
    public Label lbl_hjzje = null;
    public Label hjzje = null;
    public Label hjzsl = null;
    public Label hjzke = null;
    public Label fphm = null;
    public Label syyh = null;
    public Group syjInfo = null;
    public Label fmType = null;
    public Text code = null;
    public Text gz = null;
    public Text yyyh = null;
    public PosTable table = null;
    public Group group_4 = null;
    public Group group_5 = null;
    public Composite parent = null;
    public SaleEvent sale = null;
    public Image bkimg = null;
    
    Label lbl_barcode = null;
    Label lbl_gz = null;
    Label lbl_yyy = null;
    

    //==============触屏所用控件=======================
    
	public Composite composite_category = null;
	public Composite composite_pay = null;
	public Composite composite_finished = null;
    public Composite posfunctab = null;
    
    public PosTable table_pop=null;

    
    public Group group_class = null;
    public Group group_group = null;
    public Group group_goods = null;
    
    public Group class_page = null;
    public  Group group_page = null;
    public Group goods_page = null;
    
    public ControlBarForm ctrlform = null;
    
    public CLabel[][] groupBotton = null;
    public String[][] groupCode = null;
    public CLabel[][] goodsBotton = null;
    public String[][] goodsCode = null;
    
    //====================================================
    public HashMap clist = null;
    
    //创建pos tab页时构造函数
    public SaleForm(Composite functab,Composite parent,int style)
    {
    	this(parent,style);
    	posfunctab = functab;
    }
    
    
    public SaleForm(Composite parent, int style)
    {
        super(parent, style);
        this.parent = parent;
        clist = new HashMap();
        
        createContents();
        
    }
    
    public void clistInit()
    {
    	clist.put("vipinfo", vipinfo);
    	clist.put("focus", focus);
    	clist.put("zhongwenStyledText", zhongwenStyledText);
    	clist.put("lbl_yfje", lbl_yfje);
    	clist.put("yfje", yfje);
    	clist.put("hjje", hjje);
    	clist.put("hjzk", hjzk);
    	clist.put("lbl_hjzje", lbl_hjzje);
    	clist.put("hjzje", hjzje);
    	clist.put("hjzsl", hjzsl);
    	clist.put("hjzke", hjzke);
    	clist.put("fphm", fphm);
    	clist.put("syyh", syyh);
    	clist.put("fmType", fmType);
    	clist.put("code", code);
    	clist.put("gz", gz);
    	clist.put("yyyh", yyyh);
    	clist.put("table", table);    
    	clist.put("lbl_barcode", lbl_barcode);   
    	clist.put("lbl_gz", lbl_gz);    
    	clist.put("lbl_yyy", lbl_yyy);    
    }
    
    protected void createContents()
    {
        setLayout(new FormLayout());
        this.setSize(800, 600);

		composite = new Composite(this, SWT.NONE);
		final FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, 0);
		fd_composite.right = new FormAttachment(100, 0);
		fd_composite.top = new FormAttachment(0, 0);
		fd_composite.left = new FormAttachment(0, 0);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new FormLayout());
		
        final Group group = new Group(composite, SWT.NONE);
        group.setLayout(new FormLayout());

        final FormData formData = new FormData();
        formData.top    = new FormAttachment(0, 0);
        formData.right  = new FormAttachment(100, -432);
        formData.bottom = new FormAttachment(0, 160);
        formData.left   = new FormAttachment(0, 10);
        group.setLayoutData(formData);

        final Label label_3 = new Label(group, SWT.NONE);
        final FormData formData_14 = new FormData();
        formData_14.left = new FormAttachment(0, 215);
        formData_14.right = new FormAttachment(0, 285);
        label_3.setLayoutData(formData_14);
        label_3.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_3.setText(Language.apply("折扣额"));
        label_3.setVisible(false);
        
        final Label label_2 = new Label(group, SWT.NONE);
        label_2.setForeground(SWTResourceManager.getColor(0, 0, 255));
        FormData formData_13;
        formData_13 = new FormData();
        formData_13.top = new FormAttachment(0, 85);
        formData_13.right = new FormAttachment(0, 90);
        formData_13.left = new FormAttachment(0, 5);
        label_2.setLayoutData(formData_13);
        label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_2.setText(Language.apply("成交价"));
        
        hjje               = new Label(group, SWT.NONE);
        hjje.setForeground(SWTResourceManager.getColor(0, 0, 255));
        hjje.setAlignment(SWT.RIGHT);
        formData_14.bottom = new FormAttachment(hjje, 20, SWT.TOP);
        formData_14.top = new FormAttachment(hjje, 0, SWT.TOP);

        final FormData formData_15 = new FormData();
        formData_15.right = new FormAttachment(100, -9);
        formData_15.top = new FormAttachment(0, 75);
        formData_15.bottom = new FormAttachment(0, 108);
        formData_15.left = new FormAttachment(label_2, 5, SWT.RIGHT);
        hjje.setLayoutData(formData_15);
        hjje.setFont(SWTResourceManager.getFont("宋体", 25, SWT.BOLD));
        hjje.setText("00000000.00");

        hjzk               = new Label(group, SWT.NONE);
        hjzk.setVisible(false);

        final FormData formData_16 = new FormData();
        formData_16.bottom = new FormAttachment(hjje, 20, SWT.TOP);
        formData_16.top = new FormAttachment(hjje, 0, SWT.TOP);
        formData_16.right = new FormAttachment(0, 402);
        formData_16.left = new FormAttachment(0, 285);
        hjzk.setLayoutData(formData_16);
        hjzk.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        hjzk.setText("00000000.00");

        Group group_1;
        group_1 = new Group(composite, SWT.NONE);

        final FormData formData_1 = new FormData();
        formData_1.left   = new FormAttachment(100, -422);
        formData_1.bottom = new FormAttachment(0, 65);
        formData_1.top    = new FormAttachment(0, 0);
        group_1.setLayoutData(formData_1);

        lbl_hjzje = new Label(group_1, SWT.NONE);
        lbl_hjzje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbl_hjzje.setText(Language.apply("总金额"));
        lbl_hjzje.setBounds(10, 13, 68, 20);

        hjzje = new Label(group_1, SWT.NONE);
        hjzje.setAlignment(SWT.RIGHT);
        hjzje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
        hjzje.setText("00000000.00");
        hjzje.setBounds(84, 13, 146, 20);

        final Label label_8 = new Label(group_1, SWT.NONE);
        label_8.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_8.setText(Language.apply("单类型  "));
        label_8.setBounds(236, 13, 68, 20);

        fmType = new Label(group_1, SWT.NONE);
        fmType.setText(Language.apply("退货"));
        fmType.setAlignment(SWT.RIGHT);
        fmType.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        fmType.setBounds(302, 13, 100, 20);

        final Label label_10 = new Label(group_1, SWT.NONE);
        label_10.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_10.setText(Language.apply("总折扣"));
        label_10.setBounds(10, 39, 68, 20);

        hjzsl = new Label(group_1, SWT.NONE);
        hjzsl.setAlignment(SWT.RIGHT);
        hjzsl.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
        hjzsl.setText("00000");
        hjzsl.setBounds(302, 39, 100, 20);

        final Label label_12 = new Label(group_1, SWT.NONE);
        label_12.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_12.setText(Language.apply("总件数"));
        label_12.setBounds(236, 39, 68, 20);

        hjzke = new Label(group_1, SWT.NONE);
        hjzke.setAlignment(SWT.RIGHT);
        hjzke.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));
        hjzke.setText("00000000.00");
        hjzke.setBounds(84, 39, 146, 20);
        
    	zhongwenStyledText = new StyledText(group,
                                        SWT.MULTI | SWT.READ_ONLY |
                                        SWT.WRAP | SWT.NO_FOCUS );
    	if (GlobalInfo.ModuleType.indexOf("ZM")!=0) zhongwenStyledText.setJustify(true);//防止第一行被拉伸
    	final FormData fd_zhongwenStyledText = new FormData();
    	fd_zhongwenStyledText.top = new FormAttachment(0, 5);
    	fd_zhongwenStyledText.right = new FormAttachment(100, -7);
    	fd_zhongwenStyledText.left = new FormAttachment(0, 3);
    	fd_zhongwenStyledText.bottom = new FormAttachment(hjje, -5, SWT.TOP);
    	zhongwenStyledText.setLayoutData(fd_zhongwenStyledText);
    	
        StyleRange styleRange = new StyleRange();
        styleRange.fontStyle = SWT.BOLD;
        zhongwenStyledText.setStyleRange(styleRange);
        formData_13.bottom = new FormAttachment(zhongwenStyledText, 34, SWT.BOTTOM);
        zhongwenStyledText.setBackground(Display.getCurrent()
                                                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        //zhongwenStyledText.setBackground(group_2.getBackground());
        final FormData formData_17 = new FormData();
        formData_17.left = new FormAttachment(label_2, 0, SWT.LEFT);
        formData_17.bottom = new FormAttachment(0, 74);
        formData_17.right = new FormAttachment(100, -7);
        formData_17.top    = new FormAttachment(0, 2);
        zhongwenStyledText.setLayoutData(formData_17);
        zhongwenStyledText.setText(Language.apply("中文中文中文中文中文\n中文中文中文中文中文"));
        zhongwenStyledText.setFont(SWTResourceManager.getFont("宋体", 25, SWT.BOLD));
        zhongwenStyledText.setEditable(false);
        zhongwenStyledText.setEnabled(false);
        
        syjInfo           = new Group(composite, SWT.SHADOW_NONE);
        formData_1.right  = new FormAttachment(syjInfo, 0, SWT.RIGHT);

        final FormData formData_3 = new FormData();
        formData_3.left   = new FormAttachment(100, -422);
        formData_3.top    = new FormAttachment(0, 65);
        formData_3.bottom = new FormAttachment(0, 110);
        formData_3.right  = new FormAttachment(100, -10);
        syjInfo.setLayoutData(formData_3);
        syjInfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));

        final Label label_14 = new Label(syjInfo, SWT.NONE);
        label_14.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_14.setText(Language.apply("收银机"));
        label_14.setBounds(10, 18, 68, 20);

        fphm = new Label(syjInfo, SWT.NONE);
       
        fphm.setAlignment(SWT.RIGHT);
        fphm.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        fphm.setText("0000(00000000)");
        fphm.setBounds(84, 18, 146, 20);

        final Label label_17 = new Label(syjInfo, SWT.NONE);
        label_17.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label_17.setText(Language.apply("收银员"));
        label_17.setBounds(236, 18, 68, 20);

        syyh = new Label(syjInfo, SWT.NONE);
        syyh.setAlignment(SWT.RIGHT);
        syyh.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        syyh.setText("00000000");
        syyh.setBounds(302, 18, 100, 20);

        group_4 = new Group(composite, SWT.NONE);
        group_4.setForeground(Display.getCurrent()
                                     .getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
        group_4.setLayout(new FormLayout());

        final FormData formData_4 = new FormData();
        formData_4.right = new FormAttachment(100, -10);
        formData_4.top = new FormAttachment(0, 164);
        formData_4.left = new FormAttachment(0, 10);
        group_4.setLayoutData(formData_4);
        group_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        group_4.setText(Language.apply("商品明细"));

        table = new PosTable(group_4, SWT.BORDER | SWT.FULL_SELECTION, true);
        table.setFont(SWTResourceManager.getFont("宋体", 13, SWT.NONE));

        final FormData formData_6 = new FormData();
        formData_6.left = new FormAttachment(0, 5);
        formData_6.top = new FormAttachment(0, 5);
        formData_6.bottom = new FormAttachment(100, -9);
        formData_6.right = new FormAttachment(100, -5);
        table.setLayoutData(formData_6);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        //  设置TABLE高度
        table.addListener(SWT.MeasureItem, new Listener() 
        {    
        	//向表格增加一个SWT.MeasureItem监听器，每当需要单元内容的大小的时候就会被调用。 
            public void handleEvent(Event event) 
            { 
                event.width = table.getGridLineWidth();    										//设置宽度 
                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 1.8);	//设置高度为字体高度的2倍 
            }
        });

        group_5         = new Group(composite, SWT.NONE);
        formData_4.bottom = new FormAttachment(group_5, -5, SWT.TOP);
        group_5.setLayout(new FormLayout());

        final FormData formData_5 = new FormData();
        formData_5.right = new FormAttachment(100, -10);
        formData_5.bottom = new FormAttachment(100, -4);
        formData_5.top = new FormAttachment(100, -74);
        formData_5.left = new FormAttachment(0, 10);
        group_5.setLayoutData(formData_5);
        group_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        group_5.setText(Language.apply("输入区"));

        lbl_yyy = new Label(group_5, SWT.NONE);
        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 42);
        formData_7.top = new FormAttachment(0, 12);
        formData_7.right = new FormAttachment(0, 77);
        formData_7.left = new FormAttachment(0, 4);
        lbl_yyy.setLayoutData(formData_7);
        lbl_yyy.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
        lbl_yyy.setText(Language.apply("营业员"));

        int screenwidth = Display.getDefault().getBounds().width;
        if (screenwidth >= 800)
        {
	        yyyh = new Text(group_5, SWT.BORDER);
	        final FormData formData_8 = new FormData();
	        formData_8.bottom = new FormAttachment(0, 40);
	        formData_8.top = new FormAttachment(0, 10);
	        formData_8.right = new FormAttachment(0, 195);
	        formData_8.left = new FormAttachment(0, 82);
	        yyyh.setLayoutData(formData_8);
	        yyyh.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        yyyh.setText("");
	
	        
	        lbl_gz = new Label(group_5, SWT.NONE);
	        final FormData formData_9 = new FormData();
	        formData_9.top = new FormAttachment(0, 12);
	        formData_9.bottom = new FormAttachment(0, 42);
	        formData_9.right = new FormAttachment(0, 258);
	        formData_9.left = new FormAttachment(0, 198);
	        lbl_gz.setLayoutData(formData_9);
	        lbl_gz.setAlignment(SWT.CENTER);
	        lbl_gz.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        lbl_gz.setText(Language.apply("柜组"));
	
	        gz = new Text(group_5, SWT.BORDER);
	        final FormData formData_10 = new FormData();
	        formData_10.top = new FormAttachment(0, 10);
	        formData_10.bottom = new FormAttachment(0, 40);
	        formData_10.right = new FormAttachment(0, 465);
	        formData_10.left = new FormAttachment(0, 260);
	        gz.setLayoutData(formData_10);
	        gz.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        gz.setText("");
	

	        lbl_barcode = new Label(group_5, SWT.NONE);
	        final FormData formData_11 = new FormData();
	        formData_11.top = new FormAttachment(0, 12);
	        formData_11.bottom = new FormAttachment(0, 42);
	        formData_11.right = new FormAttachment(0, 573);
	        formData_11.left = new FormAttachment(0, 473);
	        lbl_barcode.setLayoutData(formData_11);
	        lbl_barcode.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        lbl_barcode.setText(Language.apply("商品条码"));
	
	        code = new Text(group_5, SWT.BORDER);
	        code.setForeground(SWTResourceManager.getColor(255, 0, 0));
	        final FormData formData_12 = new FormData();
	        formData_12.top = new FormAttachment(0, 10);
	        formData_12.right = new FormAttachment(100, -5);
	        formData_12.bottom = new FormAttachment(0, 40);
	        formData_12.left = new FormAttachment(0, 575);
	        code.setLayoutData(formData_12);
	        code.setFont(SWTResourceManager.getFont("宋体", 17, SWT.BOLD)); 
        }
        else
        {
	        yyyh = new Text(group_5, SWT.BORDER);
	        final FormData formData_8 = new FormData();
	        formData_8.right = new FormAttachment(0, 150);
	        formData_8.bottom = new FormAttachment(0, 40);
	        formData_8.top = new FormAttachment(0, 10);
	        formData_8.left = new FormAttachment(0, 82);
	        yyyh.setLayoutData(formData_8);
	        yyyh.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        yyyh.setText("");
	
	        
	        lbl_gz = new Label(group_5, SWT.NONE);
	        final FormData formData_9 = new FormData();
	        formData_9.bottom = new FormAttachment(0, 42);
	        formData_9.top = new FormAttachment(0, 12);
	        formData_9.left = new FormAttachment(0, 155);
	        formData_9.right = new FormAttachment(0, 215);
	        lbl_gz.setLayoutData(formData_9);
	        lbl_gz.setAlignment(SWT.CENTER);
	        lbl_gz.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        lbl_gz.setText(Language.apply("柜组"));
	
	        gz = new Text(group_5, SWT.BORDER);
	        final FormData formData_10 = new FormData();
	        formData_10.bottom = new FormAttachment(0, 40);
	        formData_10.top = new FormAttachment(0, 10);
	        formData_10.left = new FormAttachment(0, 215);
	        formData_10.right = new FormAttachment(0, 320);
	        gz.setLayoutData(formData_10);
	        gz.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        gz.setText("");
	
	        lbl_barcode = new Label(group_5, SWT.NONE);
	        final FormData formData_11 = new FormData();
	        formData_11.bottom = new FormAttachment(0, 42);
	        formData_11.top = new FormAttachment(0, 12);
	        formData_11.right = new FormAttachment(0, 428);
	        formData_11.left = new FormAttachment(gz, 5, SWT.RIGHT);
	        lbl_barcode.setLayoutData(formData_11);
	        lbl_barcode.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        lbl_barcode.setText(Language.apply("商品条码"));
	
	        code = new Text(group_5, SWT.BORDER);
	        code.setForeground(SWTResourceManager.getColor(255, 0, 0));
	        final FormData formData_12 = new FormData();
	        formData_12.left = new FormAttachment(lbl_barcode, 0, SWT.RIGHT);
	        formData_12.bottom = new FormAttachment(0, 40);
	        formData_12.top = new FormAttachment(0, 10);
	        formData_12.right = new FormAttachment(100, -5);
	        code.setLayoutData(formData_12);
	        code.setFont(SWTResourceManager.getFont("宋体", 17, SWT.BOLD));  	       
        }
/*        
        // 鼠标模式动态添加查询按钮
        if (!ConfigClass.MouseMode)
        {
	        final FormData formData_12 = new FormData();
	        formData_12.left = new FormAttachment(0, 575);
	        formData_12.right  = new FormAttachment(100, -4);
	        formData_12.bottom = new FormAttachment(0, 35);
	        formData_12.top    = new FormAttachment(0, 5);
	        code.setLayoutData(formData_12);
	        code.setFont(SWTResourceManager.getFont("宋体", 17, SWT.BOLD));
        }
        else
        {
            final FormData formDatacode = new FormData();
            formDatacode.left = new FormAttachment(0, 575);
            formDatacode.right  = new FormAttachment(0,parent.getClientArea().width - 90);
            formDatacode.bottom = new FormAttachment(0, 35);
            formDatacode.top    = new FormAttachment(0, 5);
            code.setLayoutData(formDatacode);
	        code.setFont(SWTResourceManager.getFont("宋体", 17, SWT.BOLD));
	        
	        final Button btnSearch = new Button(group_5, SWT.NONE);	        
	        btnSearch.setText("查询");
	        btnSearch.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
	        final FormData formDatasearch = new FormData();
	        formDatasearch.left = new FormAttachment(0, parent.getClientArea().width - 80);
	        formDatasearch.right  = new FormAttachment(100, -4);
	        formDatasearch.bottom = new FormAttachment(0, 35);
	        formDatasearch.top    = new FormAttachment(0, 5);
	        btnSearch.setLayoutData(formDatasearch);	        
        }
*/        
        FocusListener listener = new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
                if (focus != null && focus != e.widget)
                {
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
        newColumn.setText(Language.apply("序"));

        final TableColumn newColumn1 = new TableColumn(table, SWT.NONE);
        if (screenwidth >= 800) newColumn1.setWidth(129);
        else newColumn1.setWidth(90);
        newColumn1.setText(Language.apply("商品条码"));
        
        Rectangle area = Display.getCurrent().getPrimaryMonitor().getClientArea();
        final TableColumn newColumn2 = new TableColumn(table, SWT.NONE);
        int append = area.width - 835;
        newColumn2.setWidth(151 + append);
        newColumn2.setText(Language.apply("商品名称"));

        final TableColumn newColumn3 = new TableColumn(table, SWT.NONE);
        newColumn3.setAlignment(SWT.CENTER);
        newColumn3.setWidth(58);
        newColumn3.setText(Language.apply("单位"));

        final TableColumn newColumn4 = new TableColumn(table, SWT.NONE);
        newColumn4.setAlignment(SWT.RIGHT);
        newColumn4.setWidth(82);
        newColumn4.setText(Language.apply("数量"));

        final TableColumn newColumn5 = new TableColumn(table, SWT.NONE);
        newColumn5.setAlignment(SWT.RIGHT);
        newColumn5.setWidth(100);
        newColumn5.setText(Language.apply("单价"));

        final TableColumn newColumn6 = new TableColumn(table, SWT.NONE);
        newColumn6.setAlignment(SWT.RIGHT);
        newColumn6.setWidth(112);
        newColumn6.setText(Language.apply("折扣"));

        final TableColumn newColumn8 = new TableColumn(table, SWT.NONE);
        newColumn8.setAlignment(SWT.RIGHT);
        newColumn8.setWidth(110);
        newColumn8.setText(Language.apply("应收金额"));

        final TableItem newItemTableItem = new TableItem(table, SWT.BORDER);
        newItemTableItem.setText(7, "1234567.00");
        newItemTableItem.setText(6, "1234.00");
        newItemTableItem.setText(5, "1234567.00");
        newItemTableItem.setText(4, "123.3456");
        newItemTableItem.setText(3, "条条");
        newItemTableItem.setText(2, "一一一一一一一一");
        newItemTableItem.setText(1, "1234567890123");
        newItemTableItem.setText(0, "1");
        //newItemTableItem.setText("New item");
        zhongwenStyledText.addFocusListener(listener);

        lbl_yfje = new Label(group, SWT.NONE);
        lbl_yfje.setForeground(SWTResourceManager.getColor(255, 0, 0));
        final FormData formData_18 = new FormData();
        formData_18.bottom = new FormAttachment(100, -5);
        formData_18.top = new FormAttachment(0, 114);
        formData_18.right = new FormAttachment(0, 90);
        formData_18.left = new FormAttachment(label_2, 0, SWT.LEFT);
        lbl_yfje.setLayoutData(formData_18);
        lbl_yfje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbl_yfje.setText(Language.apply("应付额"));

        yfje = new Label(group, SWT.NONE);
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

        Group group_2;
        group_2 = new Group(composite, SWT.NONE);

        final FormData formData_2 = new FormData();
        formData_2.bottom = new FormAttachment(syjInfo, 50, SWT.BOTTOM);
        formData_2.top = new FormAttachment(syjInfo, 5, SWT.BOTTOM);
        formData_2.right = new FormAttachment(syjInfo, 412, SWT.LEFT);
        formData_2.left = new FormAttachment(syjInfo, 0, SWT.LEFT);
        group_2.setLayoutData(formData_2);

        final Label label = new Label(group_2, SWT.NONE);
        label.setBounds(10, 15, 68, 25);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("顾客卡"));

        vipinfo = new Label(group_2, SWT.NONE);
        vipinfo.setBounds(84, 15, 318, 20);
        vipinfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        vipinfo.setText("VIP");
        
        
        // 清空模拟数据
        zhongwenStyledText.setText("");
        hjje.setText("");
        hjzk.setText("");
        hjzsl.setText("");
        fphm.setText("");
        syyh.setText("");
        yfje.setText("");
        hjzje.setText("");
        vipinfo.setText("");
        
        // 自动设置窗口大小,加载背景图片
    	this.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60);
    	bkimg = ConfigClass.changeBackgroundImage(this,this,null);
    	
        // 创建触屏操作按钮栏 
    	Vector vc = ControlBarForm.createMouseControlBar(this,this);
    	for (int i = 0;i < vc.size();i++)
    	{
    		ControlBarForm cbf = (ControlBarForm)vc.get(i);
    		if (cbf.curbarstyle == ControlBarForm.BarStyle_Bottom)
    		{
    			ctrlform = cbf;
    		}
    	}

        if (ctrlform != null)
        {
        	composite.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60 - ctrlform.mheight);
        	ctrlform.setBounds(composite.getBounds().x + ctrlform.mx, composite.getBounds().y + composite.getBounds().height + ctrlform.my,ctrlform.mwidth > 0?ctrlform.mwidth:composite.getBounds().width-ctrlform.mx,ctrlform.mheight);
        }
        else
        {
        	composite.setBounds(0, GlobalVar.heightPL, GlobalVar.rec.x, GlobalVar.rec.y - 60);
        }

        clistInit();
        
        // 创建EVENT
        sale = new SaleEvent(this);
    }

    public SaleEvent getSaleEvent()
    {
    	return sale;
    }
    
    public boolean setSaleType(String type)
    {
        if (sale.checkAllowInit())
        {
            sale.initOneSale(type);
            return true;
        }
        return false;
    }

    public boolean closeForm()
    {
        if (sale.checkAllowExit())
        {
            dispose();

            return true;
        }
        else
        {
            return false;
        }
    }

    public Shell getShellParent()
    {
        return parent.getShell();
    }

    public void dispose()
    {
        super.dispose();
        
        if (posfunctab !=null)
        	posfunctab.dispose();
        
        // 释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
    }

    protected void checkSubclass()
    {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setFocus(Text focus)
    {
        this.focus = focus;
        
        this.focus.setFocus();
        this.focus.selectAll();
    }

    public Text getFocus()
    {
        return focus;
    }

}
