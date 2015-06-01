package com.efuture.javaPos.Test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.swetake.util.Qrcode;

public class TwoDimesion extends Shell
{

	 static Display display;
	 
	 Image img;
	static int n = 20;
	/**
	 * Launch the application
	 * @param args
	 */
	public static void display()
	{
		try
		{
			display = Display.getDefault();
			//MessageBox msg = new MessageBox("二维码扫描是否完成？", null, true);
			
			TwoDimesion shell = null;
			{
				shell = new TwoDimesion(display, SWT.SHELL_TRIM);
				shell.open();
				shell.layout();
//				while (!shell.isDisposed())
//				{								
//					if (!display.readAndDispatch()) display.sleep();
//				}
				MessageBox msg = new MessageBox("二维码扫描是否完成？", null, true);
				if (GlobalVar.Key1 == msg.verify())
				{
					shell.close();
					shell.dispose();

				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell
	 * @param display
	 * @param style
	 */
	public TwoDimesion(Display display, int style)
	{
		super(display, style);
		createContents();
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		setText("SWT Application");
		//setSize(850, 700);
		this.setBounds(1440, 30, 850, 700);
		//img = new Image(display, "D:\\ErWeiCode\\Michael_QRCode.png");
		
		int size = 67 + 12 * (n - 1);
		
		final Canvas canvas = new Canvas(this, SWT.NONE);
		//canvas.setBounds(100, 100, size, size);
		canvas.setBounds(100,100,size,size);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		
		//GC gc= new GC(canvas, SWT.NONE);
		//gc.drawRectangle(20, 30, 100, 100);
		//gc.drawLine(10, 20, 100, 50);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e)
			{
				//String content = "Hello world!11111";
				
				int len = 14;
				String content = "";
				for(int i = 0 ; i < len; i++)
				{
					content += 1;
				}
			    content = "Hello world!!!!!\n Wellcome to Efuture.";
			    //len = content.getBytes().length;
				//通过监听绘制二维码图片
				qRCodeCommon(e,content,n);	
				
			}
		});

	}
	
	private void qRCodeCommon(PaintEvent e,String content,int size)
	{
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");

			e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

			// 设定图像颜色> BLACK
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					int j = 0;
					for (; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							e.gc.fillRectangle(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
					System.out.println("Length = " + codeOut.length + "\t i = " + (i * 3 + 2 + 3) + "\t j = " + (j * 3 + 2 + 3));
				}
			} else {
				throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
			}
			//gs.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	//2、在弹出窗口中显示表的当时图像状态。
	/*
	 public static void main(String[] args) {
	  final Display display = new Display();
	  final Shell shell = new Shell(display);
	  shell.setText("Widget");
	  
	  //建立一个简单的表
	  final Table table = new Table(shell, SWT.MULTI);
	  table.setLinesVisible(true);
	  table.setBounds(10, 10, 100, 100);
	  for (int i = 0; i < 9; i++) {
	   new TableItem(table, SWT.NONE).setText("item" + i);
	  }
	  
	  //建立捕捉图像的按钮
	  Button button = new Button(shell, SWT.PUSH);
	  button.setText("Capture");
	  button.pack();
	  button.setLocation(10, 140);
	  
	  
	  button.addListener(SWT.Selection, new Listener() {
	   public void handleEvent(Event event) {
	    Point tableSize = table.getSize(); //获取表的大小
	    GC gc = new GC(table); //建立表的GC对象
	    final Image image =
	     new Image(display, tableSize.x, tableSize.y); //建立表大小的图像image
	    gc.copyArea(image, 0, 0); //利用表的GC对象把表的图像复制到image中
	    gc.dispose();
	    
	    //建立一个弹出面板Shell对象popup
	    Shell popup = new Shell(shell);
	    popup.setText("Image");
	    popup.addListener(SWT.Close, new Listener() {
	     public void handleEvent(Event e) {
	      image.dispose();
	     }
	    });
	    //在popup上建立画布对象canvas
	    Canvas canvas = new Canvas(popup, SWT.NONE);
	    canvas.setBounds(10, 10, tableSize.x+10, tableSize.y+10);
	    canvas.addPaintListener(new PaintListener() {
	     public void paintControl(PaintEvent e) {
	      e.gc.drawImage(image, 0, 0); //在画布上绘出表的图像image
	     }
	    });
	    popup.pack();
	    popup.open();
	   }
	  });
	  shell.pack();
	  shell.open();
	  while (!shell.isDisposed()) {
	   if (!display.readAndDispatch()) display.sleep();
	  }
	  display.dispose();
	 }
	 */

}
