package com.efuture.javaPos.UI.Design;

import java.util.Vector;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.SaleEventTouch;
import com.efuture.javaPos.UI.DesignTouch.ControlBarForm;
import com.swtdesigner.SWTResourceManager;

/**
 * @author yinl
 * @create 2011-2-28 下午04:12:32
 * @descri 文件说明
 */

public class SaleFormTouch extends SaleForm 
{   
	public SaleFormTouch(Shell parent, int style)
	{
        super(parent, style);
	}

	protected void createContents()
	{
        Rectangle area = Display.getCurrent().getPrimaryMonitor().getClientArea();
        setLayout(new FormLayout());
        this.setSize(800, 600);

		final Composite composite = new Composite(this, SWT.NONE);
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

        final Label lbl_zke = new Label(group, SWT.NONE);
        final FormData fd_lbl_zke = new FormData();
        fd_lbl_zke.left = new FormAttachment(0, 215);
        fd_lbl_zke.right = new FormAttachment(0, 285);
        lbl_zke.setLayoutData(fd_lbl_zke);
        lbl_zke.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbl_zke.setText(Language.apply("折扣额"));
        lbl_zke.setVisible(false);
        
        final Label lbl_cjj = new Label(group, SWT.NONE);
        lbl_cjj.setForeground(SWTResourceManager.getColor(0, 0, 255));
        FormData fd_lbl_cjj;
        fd_lbl_cjj = new FormData();
        fd_lbl_cjj.top = new FormAttachment(0, 85);
        fd_lbl_cjj.right = new FormAttachment(0, 90);
        fd_lbl_cjj.left = new FormAttachment(0, 5);
        lbl_cjj.setLayoutData(fd_lbl_cjj);
        lbl_cjj.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        lbl_cjj.setText(Language.apply("成交价"));
        
        hjje               = new Label(group, SWT.NONE);
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
        formData_1.right = new FormAttachment(100, -10);
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
        zhongwenStyledText.setBackground(Display.getCurrent()
                                                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        //zhongwenStyledText.setBackground(group_2.getBackground());
        final FormData formData_17 = new FormData();
        formData_17.left = new FormAttachment(lbl_cjj, 0, SWT.LEFT);
        formData_17.bottom = new FormAttachment(0, 74);
        formData_17.right = new FormAttachment(100, -7);
        formData_17.top    = new FormAttachment(0, 2);
        zhongwenStyledText.setLayoutData(formData_17);
        zhongwenStyledText.setText("中文中文中文中文中文\n中文中文中文中文中文");
        zhongwenStyledText.setFont(SWTResourceManager.getFont("宋体", 25, SWT.BOLD));
        zhongwenStyledText.setEditable(false);
        zhongwenStyledText.setEnabled(false);
        
        syjInfo           = new Group(composite, SWT.SHADOW_NONE);

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

        Group group_2;
        group_2 = new Group(composite, SWT.NONE);

        final FormData formData_2 = new FormData();
        formData_2.left = new FormAttachment(100, -422);
        formData_2.bottom = new FormAttachment(syjInfo, 50, SWT.BOTTOM);
        formData_2.top = new FormAttachment(syjInfo, 5, SWT.BOTTOM);
        formData_2.right = new FormAttachment(syjInfo, 412, SWT.LEFT);
        group_2.setLayoutData(formData_2);

        final Label label = new Label(group_2, SWT.NONE);
        label.setBounds(10, 15, 68, 25);
        label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        label.setText(Language.apply("顾客卡"));

        vipinfo = new Label(group_2, SWT.NONE);
        vipinfo.setBounds(84, 15, 318, 20);
        vipinfo.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        vipinfo.setText("VIP");
        vipinfo.setText("");

        group_4 = new Group(composite, SWT.NONE);
        group_4.setForeground(Display.getCurrent()
                                     .getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
        group_4.setLayout(new FormLayout());

        final FormData formData_4 = new FormData();
        formData_4.bottom = new FormAttachment(100, -80);
        formData_4.right = new FormAttachment(group, 0, SWT.RIGHT);
        formData_4.top = new FormAttachment(0, 164);
        formData_4.left = new FormAttachment(0, 10);
        group_4.setLayoutData(formData_4);
        group_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        group_4.setText(Language.apply("商品明细"));
        
        /*
        final FormData formData_4 = new FormData();
        formData_4.right = new FormAttachment(100, -10);
        formData_4.top = new FormAttachment(0, 164);
        formData_4.left = new FormAttachment(0, 10);
        group_4.setLayoutData(formData_4);
        group_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        group_4.setText("商品明细");
        */
        
        table = new PosTable(group_4, SWT.BORDER | SWT.FULL_SELECTION, true);
        table.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NONE));

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
                event.height = (int)Math.floor(event.gc.getFontMetrics().getHeight() * 3.5);	//设置高度为字体高度的2倍 
            }
        });

        group_group = new Group(composite, SWT.NONE);
        final FormData fd_group_3 = new FormData();
        fd_group_3.left = new FormAttachment(100, -422);
        fd_group_3.right = new FormAttachment(100, -10);
        fd_group_3.top = new FormAttachment(0, 165);
        fd_group_3.bottom = new FormAttachment(0, 265);
        group_group.setLayoutData(fd_group_3);
        group_group.setLayout(new FormLayout());
        
        group_goods = new Group(composite, SWT.NONE);
        final FormData fd_group_6 = new FormData();
        fd_group_6.top = new FormAttachment(group_group, 0, SWT.BOTTOM);
        fd_group_6.left = new FormAttachment(100, -422);
        fd_group_6.right = new FormAttachment(100, -10);
        fd_group_6.bottom = new FormAttachment(100, -80);
        group_goods.setLayoutData(fd_group_6);
        group_goods.setLayout(new FormLayout());
/*        
        final CLabel label_1 = new CLabel(group_3, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
        label_1.setFont(SWTResourceManager.getFont("宋体", 11, SWT.NONE));
        label_1.setAlignment(SWT.CENTER);
        final FormData fd_label_1 = new FormData();
        fd_label_1.bottom = new FormAttachment(0, 30);
        fd_label_1.right = new FormAttachment(0, 66);
        fd_label_1.top = new FormAttachment(0, -3);
        fd_label_1.left = new FormAttachment(0, 6);
        label_1.setLayoutData(fd_label_1);
        label_1.setText("散装月饼");
*/        

        /*
        // 创建商品分组按钮
        groupBotton = new CLabel[2][6];
        groupCode = new String[2][6];
        for (int i=0;i<2;i++)
        {
        	for (int j=0;j<6;j++)
        	{
        		CLabel label_2 = new CLabel(group_group, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
        		label_2.setAlignment(SWT.CENTER);
                FormData fd_label_2 = new FormData();
                fd_label_2.top = new FormAttachment(0, -3 + i*(34+12));
                fd_label_2.bottom = new FormAttachment(0, 30 + i*(34+12));
                fd_label_2.left = new FormAttachment(0, 6 + j*(61+6));
                fd_label_2.right = new FormAttachment(0, 66 + j*(61+6));
                label_2.setLayoutData(fd_label_2);                
                label_2.setText("散装月饼");
                
                groupBotton[i][j] = label_2;
        	}
        }
        */
        
        /*
        // 创建商品明细按钮
        goodsBotton = new CLabel[6][6];
        goodsCode = new String[6][6];
        for (int i=0;i<goodsBotton.length;i++)
        {
        	for (int j=0;j<goodsBotton[i].length;j++)
        	{
                final CLabel label_3 = new CLabel(group_6, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
                final FormData fd_label_3 = new FormData();
                fd_label_3.top = new FormAttachment(0, 1 + i*(60+10));
                fd_label_3.bottom = new FormAttachment(0, 60 + i*(60+10));
                fd_label_3.left = new FormAttachment(0, 6 + j*(61+6));
                fd_label_3.right = new FormAttachment(0, 66 + j*(61+6));
                label_3.setLayoutData(fd_label_3);
                label_3.setText("三明治和\n奶黄面包\n¥ 100.00");        
                
    	        goodsBotton[i][j] = label_3;
        	}
        }        
     */
        group_5         = new Group(composite, SWT.NONE);
        group_5.setLayout(new FormLayout());

        final FormData formData_5 = new FormData();
        formData_5.right = new FormAttachment(100, -10);
        formData_5.bottom = new FormAttachment(100, -4);
        formData_5.top = new FormAttachment(100, -74);
        formData_5.left = new FormAttachment(0, 10);
        group_5.setLayoutData(formData_5);
        group_5.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
        group_5.setText(Language.apply("输入区"));

        final Label label_19 = new Label(group_5, SWT.NONE);
        final FormData formData_7 = new FormData();
        formData_7.bottom = new FormAttachment(0, 42);
        formData_7.top = new FormAttachment(0, 12);
        formData_7.right = new FormAttachment(0, 77);
        formData_7.left = new FormAttachment(0, 4);
        label_19.setLayoutData(formData_7);
        label_19.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
        label_19.setText(Language.apply("营业员"));

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
	
	        Label label_20;
	        label_20 = new Label(group_5, SWT.NONE);
	        final FormData formData_9 = new FormData();
	        formData_9.top = new FormAttachment(0, 12);
	        formData_9.bottom = new FormAttachment(0, 42);
	        formData_9.right = new FormAttachment(0, 258);
	        formData_9.left = new FormAttachment(0, 198);
	        label_20.setLayoutData(formData_9);
	        label_20.setAlignment(SWT.CENTER);
	        label_20.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        label_20.setText(Language.apply("柜组"));
	
	        gz = new Text(group_5, SWT.BORDER);
	        final FormData formData_10 = new FormData();
	        formData_10.top = new FormAttachment(yyyh, -30, SWT.BOTTOM);
	        formData_10.bottom = new FormAttachment(yyyh, 0, SWT.BOTTOM);
	        formData_10.left = new FormAttachment(0, 260);
	        formData_10.right = new FormAttachment(0, 465);
	        gz.setLayoutData(formData_10);
	        gz.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        gz.setText("");

	        Label label_21;
	        label_21 = new Label(group_5, SWT.NONE);
	        final FormData formData_11 = new FormData();
	        formData_11.top = new FormAttachment(0, 12);
	        formData_11.bottom = new FormAttachment(0, 42);
	        formData_11.right = new FormAttachment(0, 573);
	        formData_11.left = new FormAttachment(0, 473);
	        label_21.setLayoutData(formData_11);
	        label_21.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        label_21.setText(Language.apply("商品条码"));
	        
	        code = new Text(group_5, SWT.BORDER);
	        code.setForeground(SWTResourceManager.getColor(255, 0, 0));
	        final FormData formData_12 = new FormData();
	        formData_12.top = new FormAttachment(0, 10);
	        formData_12.right = new FormAttachment(100, -5);
	        formData_12.bottom = new FormAttachment(0, 40);
	        formData_12.left = new FormAttachment(0, 575);
	        /*
	        formData_12.left = new FormAttachment(0, 740);
	        formData_12.top = new FormAttachment(0, 10);
	        formData_12.right = new FormAttachment(100, -5);
	        formData_12.bottom = new FormAttachment(0, 40);
	        */
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
	
	        Label label_20;
	        label_20 = new Label(group_5, SWT.NONE);
	        final FormData formData_9 = new FormData();
	        formData_9.bottom = new FormAttachment(0, 42);
	        formData_9.top = new FormAttachment(0, 12);
	        formData_9.left = new FormAttachment(0, 155);
	        formData_9.right = new FormAttachment(0, 215);
	        label_20.setLayoutData(formData_9);
	        label_20.setAlignment(SWT.CENTER);
	        label_20.setFont(SWTResourceManager.getFont("宋体", 17, SWT.NONE));
	        label_20.setText(Language.apply("柜组"));
	
	        gz = new Text(group_5, SWT.BORDER);
	        final FormData formData_10 = new FormData();
	        formData_10.bottom = new FormAttachment(0, 40);
	        formData_10.top = new FormAttachment(0, 10);
	        formData_10.left = new FormAttachment(0, 215);
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
	        label_21.setText(Language.apply("商品条码"));
	
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
        if (screenwidth >= 800) newColumn1.setWidth(101);
        else newColumn1.setWidth(70);
        newColumn1.setText(Language.apply("商品条码"));
        
        final TableColumn newColumn2 = new TableColumn(table, SWT.NONE);
        int append = area.width - 654 - 480;
        newColumn2.setWidth(160 + append);
        newColumn2.setText(Language.apply("商品名称"));

        final TableColumn newColumn3 = new TableColumn(table, SWT.NONE);
        newColumn3.setAlignment(SWT.CENTER);
        newColumn3.setWidth(57);
        newColumn3.setText(Language.apply("单位"));

        final TableColumn newColumn4 = new TableColumn(table, SWT.NONE);
        newColumn4.setAlignment(SWT.RIGHT);
        newColumn4.setWidth(60);
        newColumn4.setText(Language.apply("数量"));

        final TableColumn newColumn5 = new TableColumn(table, SWT.NONE);
        newColumn5.setAlignment(SWT.RIGHT);
        newColumn5.setWidth(83);
        newColumn5.setText(Language.apply("单价"));

        final TableColumn newColumn6 = new TableColumn(table, SWT.NONE);
        newColumn6.setAlignment(SWT.RIGHT);
        newColumn6.setWidth(73);
        newColumn6.setText(Language.apply("折扣"));

        final TableColumn newColumn8 = new TableColumn(table, SWT.NONE);
        newColumn8.setAlignment(SWT.RIGHT);
        newColumn8.setWidth(90);
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
        formData_18.left = new FormAttachment(lbl_cjj, 0, SWT.LEFT);
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
        
        // 清空模拟数据
        zhongwenStyledText.setText("");
        hjje.setText("");
        hjzk.setText("");
        hjzsl.setText("");
        fphm.setText("");
        syyh.setText("");
        yfje.setText("");
        hjzje.setText("");
        
        clistInit();
        // 创建EVENT
        sale = new SaleEventTouch(this);
        
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
	}
}
