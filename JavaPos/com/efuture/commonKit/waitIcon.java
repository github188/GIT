package com.efuture.commonKit;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class waitIcon
{

	protected Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			waitIcon window = new waitIcon();
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
		shell.setSize(500, 375);
		shell.setText("SWT Application");
		//
		final Display display = Display.getDefault();
		
		GC gc = new GC(shell);
		 gc.setAdvanced(true); 
		 if (!gc.getAdvanced()) { 
		     gc.drawText("Advanced graphics not supported", 30, 30, true); 
		     gc.dispose(); 
		     return; 
		 } 
		 
		 ImageData data = new ImageData("c:\\a.jpg");
	     
	     Image  originalImage = new Image(display, data);
	        
		 gc.drawImage(originalImage, 0, 0); 
		 Transform transform = new Transform(display); 
		 transform.rotate(45); 
		 gc.setTransform(transform); 
		 gc.drawImage(originalImage, 20, 50); 
		        
		 transform.dispose(); 
		 gc.dispose();
	}

}
