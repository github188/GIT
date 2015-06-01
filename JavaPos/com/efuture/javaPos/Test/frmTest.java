package com.efuture.javaPos.Test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

/**
 * 多语言演示
 * @author yw
 *
 */
public class frmTest
{

	private Text text;
	protected Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			frmTest window = new frmTest();
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
		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell();
		shell.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));//字体中文不用翻译
		shell.setSize(500, 375);
		
		//翻译举例
		//shell.setText("多语言测试页");//翻译写法如下:
		shell.setText(Language.apply("多语言测试页"));//翻译写法

		final Button button_1 = new Button(shell, SWT.NONE);
		button_1.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NONE));//字体中文不用翻译
		button_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				eventBtn1(text.getText());
			}
		});
		
		//翻译
		//button_1.setText("查看");//翻译写法如下:
		button_1.setText(Language.apply("查看"));//翻译写法
		
		
		button_1.setBounds(36, 45, 101, 39);
		text = new Text(shell, SWT.BORDER);
		text.setBounds(159, 47, 193, 25);
		
	}
	
	private void eventBtn1(String strText)
	{
		//翻译举例1
		//new MessageBox("输入框内容为[" + strText + "]");//翻译写法如下:
		new MessageBox(Language.apply("输入框1内容为[{0}]", new Object[]{strText}));//翻译写法1
		new MessageBox(Language.apply("输入框1内容为[{0}]", this.getClass().getName(), "eventBtn1", new Object[]{strText}));//翻译写法2
		
		
		
		//翻译举例2
		//new MessageBox("输入框内容为[" + getInputText1() + "]");//翻译写法如下:
		new MessageBox(Language.apply("输入框2内容为[{0}]", new Object[]{getInputText1()}));
		
		new MessageBox(getInputText2());//此行无中文,不翻译

		
		
		//翻译举例3
		//new MessageBox("输入框内容为[" + getInputText3("测试参数") + "]");//翻译写法如下:
		new MessageBox(Language.apply("输入框内容为[{0}]", new Object[]{getInputText3(Language.apply("测试参数"))}));//翻译写法1
		
		String str = getInputText3(Language.apply("测试参数"));
		new MessageBox(Language.apply("输入框内容为[{0}]", new Object[]{str}));//翻译写法2
		//以下两种为错误写法
		new MessageBox(Language.apply("输入框内容为[" + getInputText3(Language.apply("测试参数")) + "]"));//错误写法1
		new MessageBox(Language.apply("输入框内容为[" + getInputText3("{0}") + "]", new Object[]{Language.apply("测试参数")}));//错误写法2
		
		
		
		//翻译举例4
		//new MessageBox("查看成功!");//翻译写法如下:
		new MessageBox(Language.apply("查看成功!"));//翻译写法1
		new MessageBox(Language.apply("查看成功!", this.getClass().getName(), "eventBtn1"));//翻译写法2
	}
	
	private String getInputText1()
	{
		return text.getText();
	}

	private String getInputText2()
	{
		//return "输入框内容为[" + text.getText() + "]";//翻译写法如下:
		return Language.apply("输入框1内容为[{0}]", new Object[]{text.getText()});//翻译写法
	}
	
	private String getInputText3(String strMsg)
	{
		return strMsg + text.getText();
	}

}
