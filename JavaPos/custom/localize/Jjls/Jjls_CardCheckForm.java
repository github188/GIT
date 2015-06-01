package custom.localize.Jjls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Jjls_CardCheckForm
{

	public Text oldcard;
	public Shell shell;
	public boolean exchangeDone = false;
	public Label rescardNo;
	public Label resje;
	public Label resljzs;
	public Label resljje;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Jjls_CardCheckForm window = new Jjls_CardCheckForm();
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
	public boolean open()
	{
		final Display display = Display.getDefault();
		createContents();
		
		//加载背景图片
        Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);
        
        // 创建对应的界面控制对象
        new Jjls_CardCheckEvent(this);
        
		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
		
		//释放背景图片
        ConfigClass.disposeBackgroundImage(bkimg);
        
        return exchangeDone;
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(GlobalVar.style);
		shell.setSize(500, 310);
		shell.setText("SWT Application");

		final Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		label.setText("请 刷 卡");
		label.setBounds(33, 30, 100, 25);
		label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));

		oldcard = new Text(shell, SWT.BORDER);
		oldcard.setBounds(146, 30, 315, 25);
		oldcard.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("卡  号");
		label_1.setBounds(33, 80, 100, 25);
		label_1.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));

		rescardNo = new Label(shell, SWT.NONE);
		rescardNo.setBounds(146, 80, 315, 25);
		rescardNo.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));
		rescardNo.setForeground(SWTResourceManager.getColor(255, 0, 0));

		final Label label_1_2 = new Label(shell, SWT.NONE);
		label_1_2.setBounds(33, 130, 100, 25);
		label_1_2.setText("金  额");
		label_1_2.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));

		resje = new Label(shell, SWT.NONE);
		resje.setBounds(146, 130, 315, 25);
		resje.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));
		resje.setForeground(SWTResourceManager.getColor(255, 0, 0));

		final Label label_1_4 = new Label(shell, SWT.NONE);
		label_1_4.setBounds(33, 180, 100, 25);
		label_1_4.setText("累计张数");
		label_1_4.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));

		resljzs = new Label(shell, SWT.NONE);
		resljzs.setBounds(146, 180, 315, 25);
		resljzs.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));
		resljzs.setForeground(SWTResourceManager.getColor(255, 0, 0));

		final Label label_1_6 = new Label(shell, SWT.NONE);
		label_1_6.setBounds(33, 230, 100, 25);
		label_1_6.setText("累计金额");
		label_1_6.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));

		resljje = new Label(shell, SWT.NONE);
		resljje.setBounds(146, 230, 315, 25);
		resljje.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));
		resljje.setForeground(SWTResourceManager.getColor(255, 0, 0));
		//
	}

}
