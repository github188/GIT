package com.efuture.javaPos.UI.Design;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
//import org.eclipse.wb.swt.SWTResourceManager;
import com.swtdesigner.SWTResourceManager;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;

import custom.localize.Htsc.Htsc_SaleBS;

public class SjjDateform extends Htsc_SaleBS{

	protected Shell shell;
	private Text subdate;
	private Label lblNewLabel;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SjjDateform window = new SjjDateform();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() 
	{
		shell = new Shell(GlobalVar.style);
		shell.setSize(328, 150);
		//弹出框居中
		Monitor primary = shell.getMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    if (x < 0)
	    {
	        x = 0;
	    }
	    if (y < 0)
	    {
	        y = 0;
	    }
	    shell.setLocation(x, y);
	    
	    shell.setText("付货日期");
	    
	    // 加载背景图片
        ImageData data = new ImageData(ConfigClass.BackImagePath + "salebkimage.jpg");
        data    = data.scaledTo(shell.getClientArea().width,shell.getClientArea().height);
        Display display = Display.getDefault();
        Image originalImage =  new Image(display, data);
        // 设置背景图片
        shell.setBackgroundMode(SWT.INHERIT_DEFAULT);        
        shell.setBackgroundImage(originalImage);
		lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("宋体", 12, SWT.NONE));
		lblNewLabel.setBounds(10, 22, 249, 21);
		lblNewLabel.setText("请输入付货日期!(格式为YYYY-MM-DD)");
		subdate = new Text(shell, SWT.BORDER);
		subdate.setBounds(10, 67, 277, 23);
		subdate.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if (e.keyCode == 27) 
				{
					shell.close();
				}
				
				if (e.keyCode == 13) 
				{
					GlobalInfo.tempDef.str5 = subdate.getText();
					//initSellData();
					if(checkDate(subdate.getText()))
					{
						new MessageBox("设置成功");
						shell.close();
					}
					else
					{
						new MessageBox("设置失败");
						shell.open();
					}
			
					
				}
			}
		});
		
	}
	
	//日期校验
	public boolean checkDate(String str){ 
        //Pattern p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\/\\/\\s]?((((0?"+"[13578])|(1[02]))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))" +"|(((0?[469])|(11))[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|" +"(0?2[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12"+"35679])|([13579][01345789]))[\\/\\/\\s]?((((0?[13578])|(1[02]))" +"[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))" +"[\\/\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\/\\/\\s]?((0?[" +"1-9])|(1[0-9])|(2[0-8]))))))"); 
        Pattern p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\-\\s]?((((0?" +"[13578])|(1[02]))[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))" +"|(((0?[469])|(11))[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|" +"(0?2[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12" +"35679])|([13579][01345789]))[\\-\\-\\s]?((((0?[13578])|(1[02]))" +"[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))" +"[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\-\\s]?((0?[" +"1-9])|(1[0-9])|(2[0-8]))))))"); 
        boolean b = p.matcher(str).matches();
        return b;
        
    }

}
