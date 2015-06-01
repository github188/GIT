package com.efuture.javaPos.UI.DesignTouch;

import java.lang.reflect.Field;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;

public class ControlBarForm extends Composite
{
	class FuncKeyDef
	{
		public String keyfunc = "";
		public String name = "";
		public int x = 0;
		public int y = 0;
		public int height = 0;
		public int width = 0;
		public String fontname = "隶书";
		public int fontsize = 12;
	}

	public final static int BarStyle_Bottom = 1;
	public final static int BarStyle_Right = 2;
	
	public int curbarstyle = ControlBarForm.BarStyle_Bottom;
	public int mx = 0;
	public int my = 0;
	public int mheight = 0;
	public int mwidth = 0;
	public int mparentbarheight = -1;
	public Color mbkground = null;
	public String fontName = "隶书";
	public int fontSize = 12;

	public Vector vkeydef = new Vector();

	public Vector menuDef = new Vector();

	public Vector doubleClick = new Vector();

	public Object form = null;

	Event event = null;

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public ControlBarForm(Composite parent, int style, String cfgfile)
	{
		super(parent, style);

		readCfg(cfgfile);

		//加载背景图片
		//Image bkimg = ConfigClass.changeBackgroundImage(this,shell,null);

		init();
	}

	public void sendFuncKey(String keyfunc)
	{
		try
		{
			String[] cmd = keyfunc.split(",");
			for (int i = 0; i < cmd.length; i++)
			{
				if (i != 0)
				{
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
	
				if (cmd[i].toUpperCase().startsWith("ASC_"))
				{
					event = new Event();
					event.widget = Display.getCurrent().getFocusControl();
					event.doit = true;
					String s = cmd[i].substring(4);
					if (s.length() >= 2)
					{
						event.keyCode = Convert.toInt(s);
						event.character = (char) Convert.toInt(s);
					}
					else
					{
						event.keyCode = s.charAt(0);
						event.character = s.charAt(0);
					}
					
					event.type = SWT.KeyDown;
					Display.getCurrent().post(event);
	
					event.type = SWT.KeyUp;
					Display.getCurrent().post(event);
				}
				else if (Convert.toInt(cmd[i]) > 0)
				{
					NewKeyListener.sendKey(Convert.toInt(cmd[i]));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void initMouseEvent()
	{
		Listener ls = new Listener()
		{
			public void handleEvent(Event arg0)
			{
				String key = (String) arg0.widget.getData("a1");
				sendFuncKey(key);
			}
		};

		String lastControl = null;
		Menu menu = null;
		for (int i = 0; i < menuDef.size(); i++)
		{
			String[] e = (String[]) menuDef.elementAt(i);

			if (lastControl == null || !lastControl.equals(e[2]))
			{
				Composite shell = this;
				do {
					shell = shell.getParent();
					if (shell instanceof Shell) break;
				} while(shell.getParent() != null);
				
				menu = new Menu((Shell)shell, SWT.POP_UP);
				Object control = findObjectValue(this.form, e[2]);
				((Control) control).setMenu(menu);
				lastControl = e[2];
			}

			if (e[0].trim().toUpperCase().equals("SEPARATOR"))
			{
				new MenuItem(menu, SWT.SEPARATOR);
				continue;
			}
			else
			{
				MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText(e[0]);
				menuItem.setData("a1", e[1]);
				menuItem.addListener(SWT.Selection, ls);
			}
		}

		MouseAdapter mouse = new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent arg0)
			{
				String key = (String) arg0.widget.getData("a1");
				sendFuncKey(key);
			}
		};

		for (int i = 0; i < doubleClick.size(); i++)
		{
			String[] e = (String[]) doubleClick.elementAt(i);
			Object control = findObjectValue(this.form, e[2]);
			((Widget) control).setData("a1", e[1]);
			((Control) control).addMouseListener(mouse);
		}
	}

	public Object findObjectValue(Object obj, String objname)
	{
		try
		{
			Class c1 = obj.getClass();
			Field field1 = null;

			//循环查找变量。getDeclaredField只能取到自身类的变量
			while (c1 != null)
			{
				try
				{
					field1 = c1.getDeclaredField(objname);
				}
				catch (NoSuchFieldException e)
				{
				}

				if (field1 == null)
				{
					c1 = c1.getSuperclass();
				}
				else
				{
					break;
				}
				if (c1.getName().indexOf("Object") >= 0) break;
			}

			if (field1 == null) return null;

			Object curObj = field1.get(obj);

			return curObj;
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void setControlBarForm()
	{
		initMouseEvent();

		if (vkeydef.size() <= 0) return;

		Composite parent = this.getParent();

		String obj = "";
		if (parent != null && parent.getLayout() != null) obj = parent.getLayout().getClass().toString();
		if (obj.equals(FormLayout.class.toString()))
		{
			final FormData formDatab = new FormData();
			
			int m_top = my + parent.getBounds().height;
			int m_bottom = my + parent.getBounds().height + mheight;
			int m_left = mx;
			int m_right = mx + (mwidth > 0 ? mwidth : parent.getBounds().width - mx);
			
			if (curbarstyle == ControlBarForm.BarStyle_Bottom)
			{
				m_top = my + parent.getBounds().height;
				m_bottom = my + parent.getBounds().height + mheight;
				m_left = mx;
				m_right = mx + (mwidth > 0 ? mwidth : parent.getBounds().width - mx);
			}
			else if (curbarstyle == ControlBarForm.BarStyle_Right)
			{
				m_top = my;
				m_bottom = my + (mheight>0?mheight:parent.getBounds().height - my);
				m_left = mx + parent.getBounds().width;
				m_right = mx + parent.getBounds().width + mwidth;
			}
			
			formDatab.bottom = new FormAttachment(0, m_bottom);
			formDatab.top = new FormAttachment(0, m_top);
			formDatab.right = new FormAttachment(0, m_right);
			formDatab.left = new FormAttachment(0, m_left);

			this.setLayoutData(formDatab);
		}
		else
		{
			if (curbarstyle == ControlBarForm.BarStyle_Bottom)
			{
				setBounds(mx, parent.getBounds().height + my, (mwidth > 0 ? mwidth : parent.getBounds().width - mx), mheight);
			}
			else if (curbarstyle == ControlBarForm.BarStyle_Right)
			{
				setBounds(parent.getBounds().width + mx, my, mwidth, (mheight > 0 ? mheight:parent.getBounds().height - my));
			}
		}

		if (mwidth < 0) new MessageBox("Shell Width:" + parent.getBounds().width);
		if (mbkground != null) this.setBackground(mbkground);

		if (mparentbarheight < 0)
		{
			if (curbarstyle == ControlBarForm.BarStyle_Bottom)
			{
				parent.setSize(parent.getSize().x, parent.getSize().y + mheight);
			}
			else if (curbarstyle == ControlBarForm.BarStyle_Right)
			{
				parent.setSize(parent.getSize().x + mwidth, parent.getSize().y);
			}
		}
		else
		{
			if (curbarstyle == ControlBarForm.BarStyle_Bottom)
			{
				parent.setSize(parent.getSize().x, parent.getSize().y + mparentbarheight);
			}
			else if (curbarstyle == ControlBarForm.BarStyle_Right)
			{
				parent.setSize(parent.getSize().x + mparentbarheight, parent.getSize().y);
			}
		}
		
		parent.redraw();
	}

	public void readCfg(String cfgfile)
	{
		if (!PathFile.fileExist(cfgfile)) return;

		Vector v = CommonMethod.readFileByVector(cfgfile);

		if (v == null) return;

		vkeydef.clear();

		int lastx = 0, lasty = 0;
		int backx = 0, backy = 0;
		for (int i = 0; i < v.size(); i++)
		{
			Object obj = v.get(i);
			if (obj == null) continue;

			String[] str = (String[]) obj;

			String key = str[0] != null ? str[0].trim() : "";
			String value = str[1] != null ? str[1].trim() : "";

			if (key.length() <= 0 || key.charAt(0) == ';' || key.charAt(0) == '[') continue;

			if (key.equalsIgnoreCase("barstyle"))
			{
				curbarstyle = Convert.toInt(value);
			}
			else if (key.equalsIgnoreCase("x"))
			{
				mx = Convert.toInt(value);
			}
			else if (key.equalsIgnoreCase("y"))
			{
				my = Convert.toInt(value);
			}
			else if (key.equalsIgnoreCase("heigth"))
			{
				mheight = Convert.toInt(value);
			}
			else if (key.equalsIgnoreCase("width"))
			{
				mwidth = Convert.toInt(value);
			}
			else if (key.equalsIgnoreCase("parentbarheight"))
			{
				mparentbarheight = Convert.toInt(value);
			}
			else if (key.equalsIgnoreCase("bkground"))
			{
				String[] s = value.split(",");
				if (s.length >= 3) mbkground = SWTResourceManager.getColor(Convert.toInt(s[0]), Convert.toInt(s[1]), Convert.toInt(s[2]));
				else if (Convert.toInt(s[0]) >= 1 && Convert.toInt(s[0]) <= 35) mbkground = SWTResourceManager.getColor(Convert.toInt(s[0]));
				else mbkground = null;
			}
			else if (key.equalsIgnoreCase("font"))
			{
				String[] s = value.split(",");

				if (s.length > 0) fontName = s[0];
				if (s.length > 1) fontSize = Convert.toInt(s[1]);
			}
			else
			{
				if (value.length() > 0)
				{
					if (value.split(",").length > 1 && (value.split(",")[1].equalsIgnoreCase("menu")))
					{
						String[] val = value.split(",");

						val[1] = key;
						menuDef.add(val);
					}
					else if (value.split(",").length > 1 && (value.split(",")[1].equalsIgnoreCase("doubleClick")))
					{
						String[] val = value.split(",");

						val[1] = key;
						doubleClick.add(val);
					}
					else
					{
						FuncKeyDef fkd = new FuncKeyDef();
						this.vkeydef.add(fkd);
						String[] strs = value.split(",");

						fkd.keyfunc = key;
						fkd.fontname = fontName;
						fkd.fontsize = fontSize;

						if (strs.length > 0)
						{
							fkd.name = strs[0];
							if (fkd.name.indexOf("\\n") >= 0) fkd.name = ExpressionDeal.replace(fkd.name, "\\n", "\n");
						}

						if (strs.length > 1)
						{
							if (strs[1].indexOf("+") >= 0) fkd.x = lastx + Convert.toInt(strs[1].substring(strs[1].indexOf("+") + 1));
							else if (strs[1].indexOf("-") >= 0)
							{
								if (Convert.toInt(strs[2]) != backy) backx = 0;
								int w = (mwidth > 0 ? mwidth : this.getParent().getBounds().width - mx);
								fkd.x = w - backx - Convert.toInt(strs[3]) - Convert.toInt(strs[1].substring(strs[1].indexOf("-") + 1));
								backx = w - fkd.x;
								backy = Convert.toInt(strs[2]);
							}
							else fkd.x = Convert.toInt(strs[1]);
						}

						if (strs.length > 2)
						{
							if (strs[2].indexOf("+") >= 0) fkd.y = lasty + Convert.toInt(strs[2].substring(strs[2].indexOf("+") + 1));
							else fkd.y = Convert.toInt(strs[2]);
						}

						if (strs.length > 3)
						{
							fkd.width = Convert.toInt(strs[3]);
						}

						if (strs.length > 4)
						{
							fkd.height = Convert.toInt(strs[4]);
						}

						if (strs.length > 5)
						{
							fkd.fontname = strs[5];
						}

						if (strs.length > 6)
						{
							fkd.fontsize = Convert.toInt(strs[6]);
						}

						lastx = fkd.x + fkd.width;
						lasty = fkd.y + fkd.height;
					}
				}
			}
		}
		
		// 以文件名优先
		if (cfgfile.toLowerCase().indexOf("_right") > 0)
		{
			curbarstyle = ControlBarForm.BarStyle_Right;
		}
	}

	public void init()
	{
		final Color bkmouseup = SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND);
		final Color bkmousedown = SWTResourceManager.getColor(0, 64, 128);

		final Color femouseup = SWTResourceManager.getColor(0, 64, 128);
		final Color femousedown = SWTResourceManager.getColor(255, 255, 255);

		for (int i = 0; i < vkeydef.size(); i++)
		{
			final FuncKeyDef fkd = (FuncKeyDef) vkeydef.get(i);

			final Composite com = new Composite(this, SWT.BORDER);
			com.setLayout(new FillLayout());
			com.setBounds(fkd.x, fkd.y, fkd.width, fkd.height);

			final CLabel label_2 = new CLabel(com, SWT.CENTER);
			label_2.setBackground(bkmouseup);
			label_2.setForeground(femouseup);
			label_2.setBounds(0, 0, fkd.width - 4, fkd.height - 4);
			label_2.setFont(SWTResourceManager.getFont(fkd.fontname, fkd.fontsize, SWT.NONE));

			label_2.setText(fkd.name);

			label_2.addMouseListener(new MouseAdapter()
			{
				public void mouseUp(final MouseEvent arg0)
				{
					label_2.setBackground(bkmouseup);
					label_2.setForeground(femouseup);
					sendFuncKey(fkd.keyfunc);
				}

				public void mouseDown(final MouseEvent arg0)
				{
					label_2.setBackground(bkmousedown);
					label_2.setForeground(femousedown);
				}
			});
		}

		this.redraw();
	}

	public void setForm(Object form)
	{
		this.form = form;
	}

	public static Vector getMouseControlBarFile(Object form)
	{
		if (!ConfigClass.MouseMode) return null;

		Vector vc = new Vector();
		
		int i = 0;
		while(true)
		{
			String file = form.getClass().getName().substring(form.getClass().getName().lastIndexOf(".") + 1) 
			+ (i > 0?"_" + String.valueOf(i):"") 
			+ ".ini";
			
			file = GlobalVar.ConfigPath + "\\mouseConfig\\" + file;
			
			if (!PathFile.fileExist(file)) break;
			
			vc.add(file);
			
			i ++;
		}
		
		String fileright = form.getClass().getName().substring(form.getClass().getName().lastIndexOf(".") + 1) 
		+ "_right" 
		+ ".ini";
		fileright = GlobalVar.ConfigPath + "\\mouseConfig\\" + fileright;
		
		if (PathFile.fileExist(fileright))
		{
			vc.add(fileright);
		}	
		
		return vc;
	}

	public static Vector createMouseControlBar(Object form, Composite shell)
	{
		Vector vc1 = new Vector();
		Vector vc = getMouseControlBarFile(form);
		for (int i = 0;vc != null && i < vc.size();i++)
		{
			String file = (String)vc.get(i);
			if (file == null) return vc1;
			if (shell.isDisposed()) return vc1;
			
			ControlBarForm ctrlform = new ControlBarForm(shell, SWT.NONE, file);
			ctrlform.setForm(form);
			ctrlform.setControlBarForm();
			
			vc1.add(ctrlform);
			//return ctrlform;
		}
		
		return vc1;
	}
}
