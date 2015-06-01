package custom.localize.Nxmx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Logic.SaleBS;
import com.swtdesigner.SWTResourceManager;

public class Nxmx_SaleShowAccountForm
{
	public Composite parent = null;
	public Composite composite_finished = null;
	public PosTable table_refund = null;
	public Group grp_zl_sy;
	public Label txt_yfje;
	public Label txt_zl;
	public Label txt_sfje;
	public Label status;
	public Group grp_yfje;
	public Group grp_sfje;
	public Composite composite_status = null;
	public Composite composite_show = null;
	public Button btnpayover;
	public Button btnpayok;
	public Button btnpayquit;

	public Nxmx_SaleShowAccountForm(Composite parent)
	{
		this.parent = parent;
	}

	public void createContents()
	{
		composite_finished = new Composite(parent, SWT.NONE);
		final FormData fd_composite_pay = new FormData();
		fd_composite_pay.left = new FormAttachment(0, 479);
		fd_composite_pay.bottom = new FormAttachment(100, 0);
		fd_composite_pay.top = new FormAttachment(0, 0);
		fd_composite_pay.right = new FormAttachment(100, -5);
		composite_finished.setLayoutData(fd_composite_pay);
		composite_finished.setLayout(new FormLayout());

		final Group group = new Group(composite_finished, SWT.NONE);
		final FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(100, -5);
		fd_group.right = new FormAttachment(100, -5);
		fd_group.left = new FormAttachment(0, 5);
		fd_group.top = new FormAttachment(0, 5);
		group.setLayoutData(fd_group);
		group.setLayout(new FormLayout());

		grp_yfje = new Group(group, SWT.NONE);
		grp_yfje.setLayout(new FormLayout());
		final FormData fd_grp_yfje = new FormData();
		fd_grp_yfje.right = new FormAttachment(100, -306);
		fd_grp_yfje.bottom = new FormAttachment(0, 178);
		fd_grp_yfje.top = new FormAttachment(0, 10);
		fd_grp_yfje.left = new FormAttachment(0, 10);
		grp_yfje.setLayoutData(fd_grp_yfje);
		grp_yfje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		grp_yfje.setText("应付金额");

		txt_yfje = new Label(grp_yfje, SWT.RIGHT);
		final FormData fd_txt_yfje = new FormData();
		fd_txt_yfje.left = new FormAttachment(100, -217);
		fd_txt_yfje.right = new FormAttachment(100, -17);
		fd_txt_yfje.bottom = new FormAttachment(0, 85);
		fd_txt_yfje.top = new FormAttachment(0, 40);
		txt_yfje.setLayoutData(fd_txt_yfje);
		txt_yfje.setFont(SWTResourceManager.getFont("宋体", 23, SWT.BOLD));
		txt_yfje.setText("1234567.00");

		grp_sfje = new Group(group, SWT.NONE);
		grp_sfje.setLayout(new FormLayout());
		final FormData fd_grp_sfje = new FormData();
		fd_grp_sfje.left = new FormAttachment(grp_yfje, 2, SWT.DEFAULT);
		fd_grp_sfje.right = new FormAttachment(100, -5);
		fd_grp_sfje.bottom = new FormAttachment(0, 178);
		fd_grp_sfje.top = new FormAttachment(0, 10);
		grp_sfje.setLayoutData(fd_grp_sfje);
		grp_sfje.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		grp_sfje.setText("实付金额");
		txt_sfje = new Label(grp_sfje, SWT.CENTER);
		final FormData fd_txt_sfje = new FormData();
		fd_txt_sfje.bottom = new FormAttachment(0, 85);
		fd_txt_sfje.top = new FormAttachment(0, 40);
		fd_txt_sfje.right = new FormAttachment(0, 245);
		fd_txt_sfje.left = new FormAttachment(0, 45);
		txt_sfje.setLayoutData(fd_txt_sfje);
		txt_sfje.setForeground(SWTResourceManager.getColor(0, 0, 255));
		txt_sfje.setFont(SWTResourceManager.getFont("宋体", 23, SWT.BOLD));
		txt_sfje.setText("1234567.00");

		int screenwidth = Display.getDefault().getBounds().width;

		grp_zl_sy = new Group(group, SWT.NONE);
		grp_zl_sy.setLayout(new FormLayout());
		final FormData fd_grp_zl_sy = new FormData();
		fd_grp_zl_sy.bottom = new FormAttachment(100, -47);
		fd_grp_zl_sy.right = new FormAttachment(grp_sfje, 0, SWT.RIGHT);
		fd_grp_zl_sy.top = new FormAttachment(grp_yfje, 19, SWT.DEFAULT);
		fd_grp_zl_sy.left = new FormAttachment(0, 10);
		grp_zl_sy.setLayoutData(fd_grp_zl_sy);
		grp_zl_sy.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		grp_zl_sy.setText("找零/溢余（123.00）");

		txt_zl = new Label(grp_zl_sy, SWT.NONE);
		final FormData fd_txt_zl = new FormData();
		fd_txt_zl.bottom = new FormAttachment(0, 150);
		fd_txt_zl.right = new FormAttachment(100, -12);
		fd_txt_zl.top = new FormAttachment(0, 32);
		fd_txt_zl.left = new FormAttachment(0, 7);
		txt_zl.setLayoutData(fd_txt_zl);
		if (screenwidth >= 800)
			txt_zl.setAlignment(SWT.RIGHT);
		else
			txt_zl.setAlignment(SWT.CENTER);
		txt_zl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		txt_zl.setFont(SWTResourceManager.getFont("宋体", 80, SWT.BOLD));
		txt_zl.setText("123.00");

		table_refund = new PosTable(grp_zl_sy, SWT.FULL_SELECTION | SWT.BORDER);
		final FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(100, -20154);
		fd_table.bottom = new FormAttachment(0, -20000);
		fd_table.right = new FormAttachment(100, -9);
		fd_table.left = new FormAttachment(0, 7);
		table_refund.setLayoutData(fd_table);
		table_refund.setFont(SWTResourceManager.getFont("宋体", 16, SWT.NONE));
		table_refund.setLinesVisible(true);
		table_refund.setHeaderVisible(true);

		final TableColumn newColumnTableColumn_2 = new TableColumn(table_refund, SWT.NONE);
		newColumnTableColumn_2.setWidth(306);
		newColumnTableColumn_2.setText("币种");

		final TableColumn newColumnTableColumn = new TableColumn(table_refund, SWT.NONE);
		newColumnTableColumn.setAlignment(SWT.RIGHT);
		newColumnTableColumn.setWidth(170);
		newColumnTableColumn.setText("汇率");

		final TableColumn newColumnTableColumn_1 = new TableColumn(table_refund, SWT.NONE);
		newColumnTableColumn_1.setAlignment(SWT.RIGHT);
		newColumnTableColumn_1.setWidth(252);
		newColumnTableColumn_1.setText("金额");

		// final Color mouseup =
		// SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
		// final Color mousedown = SWTResourceManager.getColor(0, 64, 128);

		composite_status = new Composite(group, SWT.NONE);
		final FormData fd_composite_status = new FormData();
		fd_composite_status.bottom = new FormAttachment(100, -5);
		fd_composite_status.right = new FormAttachment(100, -2);
		fd_composite_status.top = new FormAttachment(grp_zl_sy, 5, SWT.BOTTOM);
		fd_composite_status.left = new FormAttachment(grp_zl_sy, 0, SWT.LEFT);
		composite_status.setLayoutData(fd_composite_status);
		composite_status.setLayout(new FormLayout());

		btnpayquit = new Button(composite_status, SWT.NONE);
		final FormData fd_btnpayquit = new FormData();
		btnpayquit.setLayoutData(fd_btnpayquit);
		btnpayquit.setText("退出键");
		btnpayquit.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Exit);
			}

			public void mouseDown(final MouseEvent arg0)
			{
			}
		});

		btnpayover = new Button(composite_status, SWT.NONE);
		fd_btnpayquit.bottom = new FormAttachment(btnpayover, 30, SWT.TOP);
		fd_btnpayquit.top = new FormAttachment(btnpayover, 0, SWT.TOP);
		final FormData fd_btnpayover = new FormData();
		fd_btnpayover.bottom = new FormAttachment(0, 35);
		fd_btnpayover.top = new FormAttachment(0, 5);
		fd_btnpayover.right = new FormAttachment(0, 212);
		fd_btnpayover.left = new FormAttachment(0, 142);
		btnpayover.setLayoutData(fd_btnpayover);
		btnpayover.setText("付款键");
		btnpayover.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Pay);
			}

			public void mouseDown(final MouseEvent arg0)
			{
			}
		});

		btnpayok = new Button(composite_status, SWT.NONE);
		final FormData fd_btnpayok = new FormData();
		fd_btnpayok.bottom = new FormAttachment(0, 35);
		fd_btnpayok.top = new FormAttachment(0, 5);
		fd_btnpayok.right = new FormAttachment(0, 109);
		fd_btnpayok.left = new FormAttachment(0, 39);
		btnpayok.setLayoutData(fd_btnpayok);
		btnpayok.setText("确认键");
		btnpayok.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				NewKeyListener.sendKey(GlobalVar.Validation);
			}

			public void mouseDown(final MouseEvent arg0)
			{
			}
		});
		
        FocusListener listener = new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
            	btnpayok.setFocus();
            }

            public void focusLost(FocusEvent e)
            {
            }
        };
        
        composite_finished.addFocusListener(listener);
        
		final Label label = new Label(composite_status, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		final FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(0, 30);
		fd_label.top = new FormAttachment(0, 10);
		fd_label.right = new FormAttachment(0, 32);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setText("按");

		final Label label_1 = new Label(composite_status, SWT.NONE);
		final FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(0, 31);
		fd_label_1.top = new FormAttachment(0, 11);
		fd_label_1.right = new FormAttachment(0, 137);
		fd_label_1.left = new FormAttachment(0, 115);
		label_1.setLayoutData(fd_label_1);
		label_1.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		label_1.setText("或");

		Label label_2;
		label_2 = new Label(composite_status, SWT.NONE);
		fd_btnpayquit.right = new FormAttachment(label_2, 75, SWT.RIGHT);
		fd_btnpayquit.left = new FormAttachment(label_2, 5, SWT.RIGHT);
		final FormData fd_label_2 = new FormData();
		fd_label_2.bottom = new FormAttachment(label_1, 20, SWT.TOP);
		fd_label_2.top = new FormAttachment(label_1, 0, SWT.TOP);
		fd_label_2.right = new FormAttachment(btnpayover, 93, SWT.RIGHT);
		fd_label_2.left = new FormAttachment(btnpayover, 5, SWT.RIGHT);
		label_2.setLayoutData(fd_label_2);
		label_2.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		label_2.setText("确认付款");

		final Label label_2_1 = new Label(composite_status, SWT.NONE);
		final FormData fd_label_2_1 = new FormData();
		fd_label_2_1.bottom = new FormAttachment(label_2, 20, SWT.TOP);
		fd_label_2_1.top = new FormAttachment(label_2, 0, SWT.TOP);
		fd_label_2_1.right = new FormAttachment(btnpayquit, 125, SWT.RIGHT);
		fd_label_2_1.left = new FormAttachment(btnpayquit, 5, SWT.RIGHT);
		label_2_1.setLayoutData(fd_label_2_1);
		label_2_1.setFont(SWTResourceManager.getFont("", 15, SWT.NONE));
		label_2_1.setText("返回付款界面");

		composite_show = new Composite(group, SWT.NONE);
		final FormData fd_composite_show = new FormData();
		fd_composite_show.bottom = new FormAttachment(100, -5);
		fd_composite_show.right = new FormAttachment(100, -2);
		fd_composite_show.top = new FormAttachment(grp_zl_sy, 5, SWT.BOTTOM);
		fd_composite_show.left = new FormAttachment(grp_zl_sy, 0, SWT.LEFT);
		composite_show.setLayoutData(fd_composite_show);
		composite_show.setLayout(new FormLayout());

		status = new Label(composite_show, SWT.NONE);
		final FormData fd_status = new FormData();
		fd_status.bottom = new FormAttachment(100, -5);
		fd_status.right = new FormAttachment(100, -5);
		fd_status.left = new FormAttachment(0, 5);
		fd_status.top = new FormAttachment(0, 5);
		status.setLayoutData(fd_status);
		status.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		status.setText("");
	}

	public boolean open(SaleBS saleBS)
	{
		final Display display = Display.getDefault();

		createContents();

		new Nxmx_SaleShowAccountEvent(saleBS, this);

		if (!composite_finished.isDisposed())
		{
			parent.layout(true); // 一定得刷新一次，否则coposite_pay无法显示出来
			composite_finished.layout(true);
			composite_finished.setVisible(true);
			composite_finished.redraw();
			btnpayok.setFocus();
		}

		while (!composite_finished.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		return true;
	}

	public void dispose()
	{
		if (composite_finished != null && !composite_finished.isDisposed())
			composite_finished.dispose();
	}
}
