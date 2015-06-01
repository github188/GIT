package com.efuture.javaPos.Test;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class TestForm {


	private Text text;
	protected Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestForm window = new TestForm();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell(SWT.DIALOG_TRIM|SWT.ON_TOP);
		shell.setBackground(SWTResourceManager.getColor(0, 255, 255));
		Rectangle area = Display.getDefault().getPrimaryMonitor().getBounds();
		
		shell.setBounds(area);
		shell.setSize(500, 375);
		shell.setText("SWT Application");

		final Button button = new Button(shell, GlobalVar.style_windows);
		button.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NONE));
		
		final PaintListener p = new PaintListener() {
			public void paintControl(PaintEvent e) {
				Control control = Display.getCurrent().getFocusControl();
				if (control.getClass().getName().equals("org.eclipse.swt.widgets.Button"))
				{
					e.gc.setBackground(SWTResourceManager.getColor(0, 0, 255));
					e.gc.setForeground(SWTResourceManager.getColor(255, 255, 255));
					e.gc.fillRectangle(2,2,control.getBounds().width-4,control.getBounds().height-4);
					e.gc.setFont(control.getFont());
					int width = calcTextWidth(e.gc,((Button)control).getText());
					int height = e.gc.getFontMetrics().getHeight();
					e.gc.drawString(((Button)control).getText(),(control.getBounds().width-width)/2,(control.getBounds().height-height)/2);
				}
				//e.gc.setFont(button.getFont());
			}
			
            private int calcTextWidth(GC gc, String text) {
                int stWidth = 0;
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    stWidth += gc.getAdvanceWidth(c);
                }
                return stWidth;
            } 
		};
		//shell.addPaintListener(p);
		

		button.setText("button1111");
		button.setBounds(45, 258, 130, 49);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				
			}
		});
		FocusAdapter focus = new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Button"))
				{
					/**((Button)e.widget).addPaintListener(p);
					((Button)e.widget).redraw();
					*/

				}
				
			}
			
			public void focusLost(FocusEvent e)
			{
				if (e.widget.getClass().getName().equals("org.eclipse.swt.widgets.Button"))
				{
					((Button)e.widget).removePaintListener(p);
					((Button)e.widget).redraw();
				}
			}
		};
		
		button.addFocusListener(focus);

		final Label label = new Label(shell, SWT. PUSH);
		label.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0)
			{
				if (((Label)arg0.widget).getText().equals("取 消"))
				{
				System.out.println("1");
				}
				else
				{
					System.out.println("12");
				}
			}
		});
		label.setText("取 消");
		label.setBounds(136, 100, 159, 25);

		text = new Text(shell, SWT.BORDER);
		text.setBounds(136, 23, 175, 25);
		//
	}

}
