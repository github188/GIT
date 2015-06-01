package com.efuture.commonKit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.GlobalVar;
import com.swetake.util.Qrcode;
import com.swtdesigner.SWTResourceManager;

public class QrcodeDisplay
{

	Shell shell;
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO 自动生成方法存根
//
//		int n = 34;
//		
//		String content = "https://qr.alipay.com/pm2bj7o3g7vcmo5y11";
////		content = "";
////		for (int i = 0; i < n; i++)
////		{
////			content += "1";
////		}
//		String text = "请扫二维码";
//		
//		System.out.println("Len = " + content.length());
//		display(650,50,content,text,15);
	}

	public static int display(String content,String text, int size,String msgString)
	{
		DisplayDialog dailog = null;
		try
		{
			int width = 0 ;
			int height = 0 ;
			int x = 0;
			int y = 0;
			if (Display.getDefault().getMonitors().length > 1)
		        {
		            Rectangle area = null;

		            for (int i = 0; i < Display.getDefault().getMonitors().length; i++)
		            {	
		                if (!Display.getDefault().getMonitors()[i].equals(Display.getDefault().getPrimaryMonitor()))
		                {
		                    area = Display.getDefault().getMonitors()[i].getBounds();
		                    break;
		                }
		            }

		            if (area == null)
		            {
		               return 0000;
		            }

		            if (area.width < 1000)
		            {
		                GlobalVar.secFont = -4;
		            }
		            //获得屏幕的想XY起始点
		            x = area.x;
		            y = area.y;
		            //获得屏幕分辨率
		            width  = area.width;
		            height = area.height;
		        }
			else
			{
				x = 100;
	            y = 100;
			}
			int xx = x+(width / 2) - (700/ 2);
			int yy = y+(height / 2) - (600/ 2);
			
			Display display = Display.getDefault();
			dailog = new DisplayDialog(xx,yy,content,text,size);
			dailog.open();
			MessageBox msg = new MessageBox(msgString, null, true);
			return msg.verify();
		
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			
			if(null != dailog && null != dailog.shell )
			{
				dailog.shell.close();
				dailog.shell.dispose();
			}
		}
		return -1;
		

		
	}
}

 class DisplayDialog //extends Dialog
{

	protected Object result;
	protected Display display;
	protected Shell shell;
	String content;
	String text;
	int x;
	int y;
	int n;
	int w = 3;

	/**
	 * Create the dialog
	 * @param parent
	 */
	public DisplayDialog(int x, int y, String content,String text, int size)
	{
		//this(parent, SWT.NONE);
		this.x = x;
		this.y = y;
		this.content = content;
		this.text = text;
		this.n = size;
	}

	/**
	 * Open the dialog
	 * @return the result
	 */
	public void open()
	{
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
//		while (!shell.isDisposed())
//		{
//			if (!display.readAndDispatch()) display.sleep();
//		}
//		return result;
	}

	/**
	 * Create contents of the dialog
	 */
	protected void createContents()
	{
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(700, 600);
		System.out.println(shell.getSize().x);
		System.out.println(shell.getSize().y);
		shell.setBounds(x, y, 700, 600);
		//shell.setLocation(x, y);
		shell.setText("二维码显示");

		final Canvas canvas = new Canvas(shell, SWT.NONE);
		//canvas.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent pe)
			{
				qRCodeCommon(pe,content,n);
			}
		});
		
		//int size = (int)((67 + 12 * (n - 1))/3*2)+ 2;
		//int size = 67 + 12 * (n - 1);
		
		int size = (23 + 4*(n-1))*w;
		System.out.println("Size =" + size);
		
		int x = (shell.getClientArea().width - size)/2;
		int y = (shell.getClientArea().height - size)/2;
		
		canvas.setBounds(x, y, size, size);

		final Label label = new Label(shell, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setVisible(true);
		label.setFont(SWTResourceManager.getFont("宋体", 18, SWT.BOLD));
		label.setText("请扫二维码");
		label.setBounds(65, 48, 519, 32);
		//
	}

	
	//在画板上画二维码
	private void qRCodeCommon(PaintEvent e,String content,int size)
	{
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			//设置二维码数据类型，纯数字(N/numeric),字母数字混合(A,alphanumeric),8bit字节数据(B/8bit)
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("UTF-8");

			//int c = qrcodeHandler.calStructureappendParity(contentBytes);
//			System.out.println("C = " + c);
			e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
			//e.gc.setBackground(SWT.COLOR_BLACK);

			// 设定图像颜色> BLACK
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 0;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					int j = 0;
					for (; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							e.gc.fillRectangle(j * w + pixoff, i * w + pixoff, w, w);
						}
					}
				}
			} else {
				 new MessageBox("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
			}
			//gs.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
