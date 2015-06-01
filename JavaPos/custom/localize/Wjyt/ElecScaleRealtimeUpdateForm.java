package custom.localize.Wjyt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import java.util.Vector;
import com.efuture.javaPos.Global.ConfigClass;
import com.swtdesigner.SWTResourceManager;

public class ElecScaleRealtimeUpdateForm
{
	public Label lbgoodsname;
	public Label lbgoodsweight;
	public Label lbgoodsprice;
	public Label lbtip;
	public  Button button; 
	protected Shell shell;
	
	public double price;
	public Vector data;
	private String goodsname;

	
	public ElecScaleRealtimeUpdateForm(String name,double jg,Vector dt)
	{
		goodsname = name;
		price = jg;
		data = dt;
	}
	
	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			ElecScaleRealtimeUpdateForm window = new ElecScaleRealtimeUpdateForm("Test",0.0,null);
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open()
	{
		final Display display = Display.getDefault();
		createContents();
		Image bkimg = ConfigClass.changeBackgroundImage(this, shell, null);

		new ElecScaleRealtimeUpdateEvent(this);

		shell.open();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		// 释放背景图片
		ConfigClass.disposeBackgroundImage(bkimg);
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.APPLICATION_MODAL);
		shell.setLayout(new FormLayout());
		shell.setSize(394, 181);
		shell.setText("提示");

		final Label label = new Label(shell, SWT.NONE);
		final FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(0, 38);
		fd_label.top = new FormAttachment(0, 13);
		fd_label.right = new FormAttachment(0, 111);
		fd_label.left = new FormAttachment(0, 19);
		label.setLayoutData(fd_label);
		label.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label.setText("商品名称:");

		final Label label_1 = new Label(shell, SWT.NONE);
		final FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(0, 76);
		fd_label_1.top = new FormAttachment(0, 52);
		fd_label_1.right = new FormAttachment(0, 111);
		fd_label_1.left = new FormAttachment(0, 19);
		label_1.setLayoutData(fd_label_1);
		label_1.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_1.setText("商品重量:");

		final Label label_2 = new Label(shell, SWT.NONE);
		final FormData fd_label_2 = new FormData();
		fd_label_2.bottom = new FormAttachment(0, 117);
		fd_label_2.top = new FormAttachment(0, 91);
		fd_label_2.right = new FormAttachment(0, 111);
		fd_label_2.left = new FormAttachment(0, 19);
		label_2.setLayoutData(fd_label_2);
		label_2.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		label_2.setText("商品价格:");

		lbgoodsname = new Label(shell, SWT.NONE);
		final FormData fd_lbgoodsname = new FormData();
		fd_lbgoodsname.bottom = new FormAttachment(0, 38);
		fd_lbgoodsname.top = new FormAttachment(0, 13);
		fd_lbgoodsname.right = new FormAttachment(0, 377);
		fd_lbgoodsname.left = new FormAttachment(0, 117);
		lbgoodsname.setLayoutData(fd_lbgoodsname);
		lbgoodsname.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		lbgoodsname.setText(goodsname);

		lbgoodsweight = new Label(shell, SWT.NONE);
		final FormData fd_lbgoodsweight = new FormData();
		fd_lbgoodsweight.bottom = new FormAttachment(0, 76);
		fd_lbgoodsweight.top = new FormAttachment(0, 51);
		fd_lbgoodsweight.right = new FormAttachment(0, 311);
		fd_lbgoodsweight.left = new FormAttachment(0, 116);
		lbgoodsweight.setLayoutData(fd_lbgoodsweight);
		lbgoodsweight.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));

		lbgoodsprice = new Label(shell, SWT.NONE);
		final FormData fd_lbgoodsprice = new FormData();
		fd_lbgoodsprice.bottom = new FormAttachment(0, 116);
		fd_lbgoodsprice.top = new FormAttachment(0, 91);
		fd_lbgoodsprice.right = new FormAttachment(0, 310);
		fd_lbgoodsprice.left = new FormAttachment(0, 115);
		lbgoodsprice.setLayoutData(fd_lbgoodsprice);
		lbgoodsprice.setFont(SWTResourceManager.getFont("宋体", 15, SWT.BOLD));

		button = new Button(shell, SWT.NONE);
		final FormData fd_button = new FormData();
		fd_button.bottom = new FormAttachment(0, 1);
		fd_button.top = new FormAttachment(0, 0);
		fd_button.right = new FormAttachment(0, 1);
		fd_button.left = new FormAttachment(0, 0);
		button.setLayoutData(fd_button);
		button.setText("button");

		lbtip = new Label(shell, SWT.NONE);
		lbtip.setForeground(SWTResourceManager.getColor(255, 0, 0));
		lbtip.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		final FormData fd_lbtip = new FormData();
		fd_lbtip.right = new FormAttachment(0, 385);
		fd_lbtip.bottom = new FormAttachment(0, 160);
		fd_lbtip.top = new FormAttachment(0, 131);
		fd_lbtip.left = new FormAttachment(0, 69);
		lbtip.setLayoutData(fd_lbtip);
		lbtip.setText("[确认键]-获取重量 [取消键]-退出  ");

		Label label_4;
		label_4 = new Label(shell, SWT.NONE);
		
		label_4.setForeground(SWTResourceManager.getColor(255, 0, 0));
		label_4.setFont(SWTResourceManager.getFont("宋体", 15, SWT.NONE));
		final FormData fd_label_4 = new FormData();
		fd_label_4.bottom = new FormAttachment(0, 160);
		fd_label_4.top = new FormAttachment(0, 131);
		fd_label_4.right = new FormAttachment(0, 75);
		fd_label_4.left = new FormAttachment(0, 19);
		label_4.setLayoutData(fd_label_4);
		label_4.setText("提示:");
		//
	}

}
