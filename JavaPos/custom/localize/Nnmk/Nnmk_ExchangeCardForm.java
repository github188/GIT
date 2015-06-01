package custom.localize.Nnmk;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class Nnmk_ExchangeCardForm
{

	public StyledText styledText;
	public Text newcard;
	public Text oldcard;
	public Shell shell;
	public boolean exchangeDone = false;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Nnmk_ExchangeCardForm window = new Nnmk_ExchangeCardForm();
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
        new Nnmk_ExchangeCardEvent(this);
        
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
		shell.setSize(463, 223);
		shell.setText("SWT Application");

		final Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		label.setText("原会员卡");
		label.setBounds(33, 31, 79, 22);

		final Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		label_1.setText("新会员卡");
		label_1.setBounds(33, 124, 79, 22);

		oldcard = new Text(shell, SWT.BORDER);
		oldcard.setBounds(146, 28, 273, 25);
		oldcard.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		newcard = new Text(shell, SWT.BORDER);
		newcard.setBounds(146, 121, 273, 25);
		newcard.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		styledText = new StyledText(shell, SWT.BORDER);
		styledText.setBackground(SWTResourceManager.getColor(255, 255, 255));
		styledText.setBounds(33, 59, 386, 56);
		styledText.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		//
	}

}
