package mediaPlayer;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Shell;

public class ImageMessDisp
{
    protected Shell shell;
    int x = 100;
    int y = 100;
    int width = 100;
    int height = 100;

    public ImageMessDisp(int x, int y, int width, int height)
    {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            ImageMessDisp window = new ImageMessDisp(0, 0, 10, 10);
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
        //final Display display = Display.getDefault();
        createContents();
        shell.open();
        this.visible(false);
        shell.layout();

        /**
        while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                        display.sleep();
        }*/
    }

    /**
     * Create contents of the window
     */
    protected void createContents()
    {
        shell = new Shell(SWT.BORDER|SWT.ON_TOP);
        shell.setLayout(new FormLayout());
        shell.setBounds(this.x, this.y, this.width, this.height);
        shell.setText("锁屏");
        //
    }

    public void updateFrame()
    {
        shell.update();
    }

    public void visible(final boolean b)
    {
    	visible(b,"");
    }
    
    public void visible(final boolean b,final String imagepath)
    {
        shell.getDisplay().syncExec(new Runnable()
            {
                public void run()
                {
                    if (b)
                    {
                    	shell.setLocation(x, y);
                    }
                    else
                    {
                        shell.setLocation(x, y + shell.getBounds().height);
                    }
                    
                    if (!imagepath.equals(""))
                    {
	                	if (new File(imagepath).exists())
	                	{
	            	        // 加载背景图片
	            	        ImageData data = new ImageData(imagepath);
	            	        data    = data.scaledTo(shell.getClientArea().width,shell.getClientArea().height);
	            	        Image image = new Image(shell.getDisplay(), data);
	            	        shell.setBackgroundImage(image);
	            	        
	            	        updateFrame();
	                	}
                    }
                }
            });
    }

    /*
    public void setimage(String filepath)
    {
    	if (new File(filepath).exists())
    	{
	        // 加载背景图片
	        ImageData data = new ImageData(filepath);
	        data    = data.scaledTo(width,height);
	        Image image = new Image(shell.getDisplay(), data);
	        shell.setBackgroundImage(image);
	        
	        updateFrame();
    	}
    }
    */
    
    public void dispose()
    {
        shell.getDisplay().syncExec(new Runnable()
            {
                public void run()
                {
                    shell.close();
                    shell.dispose();
                }
            });
    }
}
