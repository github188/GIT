package com.efuture.DeBugTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;
import com.swtdesigner.SWTResourceManager;

public class DebugReader
{

	private StyledText styledText;
	private Text key;
	private Combo combo;
	private Text text;
	protected Shell shell;
	
	public Vector textInfo_vec = null;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DebugReader window = new DebugReader();
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
	
	public  StyleRange getColorStyle( int  startOffset,  int  length, Color color,boolean bold)  {
		     StyleRange styleRange  =   new  StyleRange(startOffset, length, color,  null );
		     if (bold) styleRange.fontStyle  =  SWT.BOLD;
		      return  styleRange;
		 } 

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		shell = new Shell(SWT.TITLE|SWT.APPLICATION_MODAL|SWT.CLOSE);
		shell.setLayout(new FormLayout());
		shell.setSize(754, 483);
		shell.setText("SWT Application");
		
		shell.setLocation((Display.getDefault().getBounds().width / 2) - (shell.getSize().x / 2), (Display.getDefault().getBounds().height / 2) - (shell.getSize().y / 2));

		text = new Text(shell, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
		final FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(0, 31);
		fd_text.top = new FormAttachment(0, 7);
		fd_text.right = new FormAttachment(0, 185);
		fd_text.left = new FormAttachment(0, 104);
		text.setLayoutData(fd_text);
		text.setText(new ManipulateDateTime().getDateByEmpty());
		
		Button dateButton;
		dateButton = new Button(shell, SWT.PUSH);
		dateButton.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
		final FormData fd_dateButton = new FormData();
		fd_dateButton.left = new FormAttachment(0, 30);
		fd_dateButton.bottom = new FormAttachment(0, 31);
		fd_dateButton.top = new FormAttachment(0, 7);
		fd_dateButton.right = new FormAttachment(0, 101);
		dateButton.setLayoutData(fd_dateButton);
		dateButton.setText(Language.apply("选择时间"));

		dateButton.addSelectionListener( new  SelectionAdapter()  {
             public   void  widgetSelected(SelectionEvent e)  {
                 final  Shell dialog  =   new  Shell(shell, SWT.DIALOG_TRIM);
                dialog.setLayout( new  GridLayout( 1 ,  false ));

                 final  DateTime calendar  =   new  DateTime(dialog, SWT.CALENDAR
                         |  SWT.BORDER);
                

                new  Label(dialog, SWT.NONE);
                Button ok  =   new  Button(dialog, SWT.PUSH);
                ok.setText( " OK " );
                ok.setLayoutData( new  GridData(SWT.FILL, SWT.CENTER,  false ,
                         false ));
                ok.addSelectionListener( new  SelectionAdapter()  {
                     public   void  widgetSelected(SelectionEvent e)  {

                    	 System.out
                         .println( " Calendar date selected (MM/DD/YYYY) =  "
                                  +  (calendar.getMonth()  +   1 )
                                  +   " / "
                                  +  calendar.getDay()
                                  +   " / "
                                  +  calendar.getYear());
                        text.setText(String.valueOf(calendar.getYear())+String.valueOf((calendar.getMonth()  +   1))+String.valueOf(calendar.getDay()));

                        dialog.close();
                    }
                } );
                dialog.setDefaultButton(ok);
                dialog.pack();
                dialog.open();
            }
        } );

		final String[] item = new String[] {Language.apply("常见信息"), Language.apply("DEBUG信息"), Language.apply("程序警告"), Language.apply("常见错误"), Language.apply("致命错误")};
		combo = new Combo(shell, SWT.NONE);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(final SelectionEvent arg0)
			{
				System.out.println("aaaa");
			}
			
			public void widgetSelected(final SelectionEvent arg0)
			{
				System.out.println("bbbbbb");
				if (textInfo_vec == null) textInfo_vec = new Vector();
				textInfo_vec.removeAllElements();
				String line  = null;
				switch (combo.getSelectionIndex())
				{
					case 0:
						line = "info";
						break;
					case 1:
						line = "debug";
						break;
					case 2:
						line = "warn";
						break;
					case 3:
						line = "error";
						break;
					case 4:
						line = "fatal";
						break;
				}
				String time = text.getText();
				
				String syjh = "";
				if (ConfigClass.CashRegisterCode == null)
				{
					
				}
				else
				{
					syjh = ConfigClass.CashRegisterCode;
				}
				
				BufferedReader br = CommonMethod.readFile("PosLog//"+time.substring(0, 6)+"//"+line+"-"+syjh+"-"+time+".txt");
				if (br !=null)
				{{}
					String line1 = null;
					try
					{
						while ((line1 = br.readLine()) != null)
						{
							textInfo_vec.add(line1);
						}
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finally
					{
						try
						{
							br.close();
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			
				br = CommonMethod.readFile("PosLog//"+time.substring(0,6)+"//"+line+"-"+"Local"+"-"+time+".txt");
				if (br != null)
				{
					String line1 = null;
					try
					{
						while ((line1 = br.readLine()) != null)
						{
							textInfo_vec.add(line1);
						}
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finally
					{
						try
						{
							br.close();
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				styledText.setText("");
				for (int i = 0 ; i < textInfo_vec.size(); i ++)
				{
					styledText.append((String)textInfo_vec.elementAt(i)+"\n");
				}
			}
		});
		combo.setItems(item);
		combo.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
		final FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(0, 345);
		fd_combo.bottom = new FormAttachment(0, 31);
		fd_combo.top = new FormAttachment(0, 7);
		fd_combo.left = new FormAttachment(0, 246);
		combo.setLayoutData(fd_combo);

		Label label_1;
		label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		final FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(0, 31);
		fd_label_1.top = new FormAttachment(0, 7);
		fd_label_1.right = new FormAttachment(0, 248);
		fd_label_1.left = new FormAttachment(0, 188);
		label_1.setLayoutData(fd_label_1);
		label_1.setText(Language.apply("类 型"));

		key = new Text(shell, SWT.BORDER);
		key.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
		final FormData fd_key = new FormData();
		fd_key.bottom = new FormAttachment(0, 31);
		fd_key.top = new FormAttachment(0, 7);
		fd_key.right = new FormAttachment(0, 594);
		fd_key.left = new FormAttachment(0, 429);
		key.setLayoutData(fd_key);

		Button search;
		search = new Button(shell, SWT.NONE);
		search.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(final SelectionEvent arg0)
			{
				
			}
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (key.getText().trim().length() > 0)
				{
					
					styledText.setStyleRange(getColorStyle( 0,  styledText.getText().length(), styledText.getForeground(),false));
					String key1 = key.getText();
					String line = styledText.getText();	
					int fromindex = 0;
					int start = 0;
					int v_start = 0;
					while((start = line.indexOf(key1,fromindex)) >= 0)
					{
						if (v_start ==0) v_start = start; 
						styledText.setStyleRange(getColorStyle( start,  key1.length(), Display.getCurrent().getSystemColor(SWT.COLOR_BLUE),true));
						fromindex = start + key1.length();
					}
					styledText.setSelection(v_start);
				}
			}
		});
		final FormData fd_search = new FormData();
		fd_search.bottom = new FormAttachment(0, 31);
		fd_search.top = new FormAttachment(0, 7);
		fd_search.left = new FormAttachment(0, 605);
		search.setLayoutData(fd_search);
		search.setText(Language.apply("搜索"));

		styledText = new StyledText(shell, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		styledText.setFont(SWTResourceManager.getFont("", 12, SWT.NONE));
		styledText.setSelectionForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		styledText.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		styledText.setJustify(true);
		final FormData fd_styledText = new FormData();
		fd_styledText.bottom = new FormAttachment(100, -13);
		fd_styledText.right = new FormAttachment(100, -13);
		fd_styledText.top = new FormAttachment(0, 45);
		fd_styledText.left = new FormAttachment(0, 10);
		styledText.setLayoutData(fd_styledText);

		Button download;
		download = new Button(shell, SWT.NONE);
		download.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
//				创建一个打开对话框,样式设置为SWT.OPEN
		        FileDialog dialog = new FileDialog(shell, SWT.OPEN);

		        //设置打开默认的路径
		        //dialog.setFilterPath(System.getProperty("java.home"));

		        //设置所打开文件的扩展名
		        dialog.setFilterExtensions(new String[] { "*.txt", "*.ini", "*.zip", "*.*" });

		        //设置显示到下拉框中的扩展名的名称
		        dialog.setFilterNames(new String[] { "Text Files(*.txt)", "INI Files(*.ini)", "ZIP Files(*.zip)", "ALL Files(*.*)" });

		        //打开窗口,返回用户所选的文件目录
		        String file = dialog.open();
		        
		        BufferedReader br = CommonMethod.readFile(file);
				if (br != null)
				{
					styledText.setText("");
					String line1 = null;
					try
					{
						while ((line1 = br.readLine()) != null)
						{
							styledText.append(line1+"\n");
						}
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finally
					{
						try
						{
							br.close();
						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		fd_search.right = new FormAttachment(download, 0, SWT.LEFT);
		final FormData fd_download = new FormData();
		fd_download.bottom = new FormAttachment(0, 31);
		fd_download.top = new FormAttachment(0, 7);
		fd_download.right = new FormAttachment(0, 741);
		fd_download.left = new FormAttachment(0, 673);
		download.setLayoutData(fd_download);
		download.setText(Language.apply("导入"));

		final Label label_2_1 = new Label(shell, SWT.NONE);
		final FormData fd_label_2_1 = new FormData();
		fd_label_2_1.bottom = new FormAttachment(0, 31);
		fd_label_2_1.top = new FormAttachment(0, 7);
		fd_label_2_1.right = new FormAttachment(0, 422);
		fd_label_2_1.left = new FormAttachment(0, 357);
		label_2_1.setLayoutData(fd_label_2_1);
		label_2_1.setFont(SWTResourceManager.getFont("", 14, SWT.NONE));
		label_2_1.setText(Language.apply("关键字"));

		final Button button = new Button(shell, SWT.CHECK);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (button.getSelection())
				{
					new MessageBox(Language.apply("日志功能开启"));
				}
				else
				{
					new MessageBox(Language.apply("日志功能关闭"));
				}
				PosLog.getLog(getClass()).setConnect(button.getSelection());
				
			}
		});
		final FormData fd_button = new FormData();
		fd_button.bottom = new FormAttachment(dateButton, 0, SWT.BOTTOM);
		fd_button.right = new FormAttachment(dateButton, 0, SWT.LEFT);
		fd_button.top = new FormAttachment(dateButton, 0, SWT.TOP);
		fd_button.left = new FormAttachment(styledText, 0, SWT.LEFT);
		button.setLayoutData(fd_button);
		
		boolean connect = PosLog.getLog(getClass()).getConnect();
		button.setSelection(connect);
		//
	}

}
