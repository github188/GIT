package custom.localize.Nxmx;

import java.io.BufferedReader;
import java.io.File;
import java.util.Vector;
import java.lang.reflect.Field;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.efuture.javaPos.Struct.ContentItemForTouchScrn;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.UI.Design.MenuFuncForm;
import com.efuture.javaPos.UI.Design.SaleForm;
import com.efuture.javaPos.UI.SaleEvent;
import com.swtdesigner.SWTResourceManager;

public class Nxmx_SaleEventTouch extends SaleEvent
{
	// [初始化状态]常量
	public static final int UPDATE_CLASS_INIT_STATUS = 11;
	public static final int OVER_CLASS_INIT_STATUS = 12;

	public static final int UPDATE_GROUP_INIT_STATUS = 21;
	public static final int OVER_GROUP_INIT_STATUS = 22;

	public static final int UPDATE_GOODS_INIT_STATUS = 31;
	public static final int OVER_GOODS_INIT_STATUS = 32;

	// [更新状态]常量
	public static final int UPDATE_CLASS_PAGE_STATUS = 41;
	public static final int OVER_CLASS_PAGE_STATUS = 42;

	public static final int UPDATE_GROUP_PAGE_STATUS = 51;
	public static final int OVER_GROUP_PAGE_STATUS = 52;

	public static final int UPDATE_GOODS_PAGE_STATUS = 61;
	public static final int OVER_GOODS_PAGE_STATUS = 62;

	// [更新下一级菜单目录]常量
	public static final int UPDATE_CLASS_NEXT_STATUS = 71;
	public static final int OVER_CLASS_NEXT_STATUS = 72;

	public static final int UPDATE_GROUP_NEXT_STATUS = 81;
	public static final int OVER_GROUP_NEXT_STATUS = 82;

	public static final int UPDATE_GOODS_NEXT_STATUS = 91;
	public static final int OVER_GOODS_NEXT_STATUS = 92;

	// 开始查找的类型级别
	protected static int STARTLEVEL = 0;
	// 定义按钮布局属性
	protected ButtonLayout classBtnLayout = null;
	protected ButtonLayout classPageBtnLayout = null;

	protected ButtonLayout groupBtnLayout = null;
	protected ButtonLayout groupPageBtnLayout = null;

	protected ButtonLayout goodsBtnLayout = null;
	protected ButtonLayout goodsPageBtnLayout = null;

	// 定义监听
	private MouseAdapter classBtnMouseAdp = null;
	private MouseAdapter classPageBtnMouseAdp = null;

	private MouseAdapter groupBtnMouseAdp = null;
	private MouseAdapter groupPageBtnMouseAdp = null;

	private MouseAdapter goodsBtnMouseAdp = null;
	private MouseAdapter goodsPageBtnMouseAdp = null;

	// 按钮容器
	public CLabel lblClassBtnAry[][] = null;
	public CLabel lblClassPageBtnAry[][] = null;

	public CLabel lblGroupBtnAry[][] = null;
	public CLabel lblGroupPageBtnAry[][] = null;

	public CLabel lblGoodsBtnAry[][] = null;
	public CLabel lblGoodsPageBtnAry[][] = null;

	// 翻页键状态
	public PageButtonStatus classPageBtnStatus = null;
	public PageButtonStatus groupPageBtnStatus = null;
	public PageButtonStatus goodsPageBtnStatus = null;

	// 用于更新
	private UpdateDataCommand updateDataCmd = null;
	private RedrawCommand redrawCmd = null;

	public static void main(String[] args)
	{
		Nxmx_SaleEventTouch xmx = new Nxmx_SaleEventTouch(null);
		xmx.readTouchScreenMode();
	}

	public Nxmx_SaleEventTouch(SaleForm saleForm)
	{
		super(saleForm);
		initEvent(saleForm);
		
		readTouchScreenMode();

		initTouchButton(saleForm);

		initAllBtnStatus();

		addTouchButtonListener();
	
	}
	
	
	
	//======以下为重写基类方法============
	public void keyPressed(KeyEvent e, int key)
	{
		switch (key)
		{
			case GlobalVar.ArrowUp:
				table.moveUp();
				setCurGoodsBigInfo();
				super.saleBS.doShowInfoFinish();
				break;

			case GlobalVar.ArrowDown:
				table.moveDown();
				setCurGoodsBigInfo();
				super.saleBS.doShowInfoFinish();
				break;

			case GlobalVar.PageDown:
				table.PageDown();
				setCurGoodsBigInfo();

				break;

			case GlobalVar.PageUp:
				table.PageUp();
				setCurGoodsBigInfo();

				break;
		}
	
	}
	
	public void showFuncMenu()
	{
		try
		{

			// 显示功能菜单窗口
			new MenuFuncForm(saleform.composite.getShell(), GlobalInfo.posLogin.funcmenu);

			// 如果小票号发生改变
			if (saleBS.saleHead.fphm != GlobalInfo.syjStatus.fphm)
			{
				// 刷新数据
				saleBS.refreshSaleData();

				// 刷新小票号显示
				setSYJInfo();

			}

			try
			{
				setCurGoodsBigInfo();
			}
			catch (Exception er)
			{

			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			new MessageBox("打开功能菜单时发生异常\n\n" + ex.getMessage());
		}
	}
	
	public void updateSaleGUI()
	{
		super.updateSaleGUI();
		super.saleBS.doShowInfoFinish();
	}
	
	protected void changeBackGroundImage(boolean salenormal)
	{
		String file = null;
		
		// 先获取BK文件名
		if (salenormal) file = ConfigClass.getBackgroundImageFile(saleform,"norm_"+saleBS.saleHead.djlb);
		else file = ConfigClass.getBackgroundImageFile(saleform,"warn_"+saleBS.saleHead.djlb);
		
		// 比较文件名和上次一致则不改变
		if (file.equalsIgnoreCase(lastsalebk)) return;
		lastsalebk = file;
		
		// 先释放再加载
		ConfigClass.disposeBackgroundImage(saleform.bkimg);		
		saleform.bkimg = ConfigClass.changeBackgroundImage(saleform.composite,lastsalebk);
	}
	
	
	public void clearSell()
	{
		super.clearSell();
		super.saleBS.doShowInfoFinish();
	}

	

	
	//=======以下为创建触屏按键过程=============
	
	private void initEvent(SaleForm saleForm)
	{
		category = saleForm.composite_category;
		
		if (poptable!=null)
			poptable.clearRow();
		
	}
	
	public void initAllBtnStatus()
	{
		try
		{
			classPageBtnStatus = new PageButtonStatus(this, UPDATE_CLASS_INIT_STATUS, classBtnLayout.RowCount, classBtnLayout.ColumnCount, classPageBtnLayout.RowCount * classPageBtnLayout.ColumnCount, 1);
			groupPageBtnStatus = new PageButtonStatus(this, UPDATE_GROUP_INIT_STATUS, groupBtnLayout.RowCount, groupBtnLayout.ColumnCount, groupPageBtnLayout.RowCount * groupPageBtnLayout.ColumnCount, 2);
			goodsPageBtnStatus = new PageButtonStatus(this, UPDATE_GOODS_INIT_STATUS, goodsBtnLayout.RowCount, goodsBtnLayout.ColumnCount, goodsPageBtnLayout.RowCount * goodsPageBtnLayout.ColumnCount, 3);

			/* ======发送更新数据命令======= */
			// 更新一级菜单内容
			updateDataCmd = new UpdateDataCommand(classPageBtnStatus, null, new ClassGroupReceiver());
			updateDataCmd.execute();
			redrawCmd = new RedrawCommand(classPageBtnStatus, new ClassGroupReceiver());
			redrawCmd.execute();

			// 更新二级菜单内容
			updateDataCmd = new UpdateDataCommand(groupPageBtnStatus, classPageBtnStatus, new GroupGroupReceiver());
			updateDataCmd.execute();
			redrawCmd = new RedrawCommand(groupPageBtnStatus, new GroupGroupReceiver());
			redrawCmd.execute();

			// 更新三级菜单内容
			updateDataCmd = new UpdateDataCommand(goodsPageBtnStatus, groupPageBtnStatus, new GoodsGroupReceiver());
			updateDataCmd.execute();
			redrawCmd = new RedrawCommand(goodsPageBtnStatus, new GoodsGroupReceiver());
			redrawCmd.execute();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/* ======读取配置文件======================================== */
	public void readTouchScreenMode()
	{
		BufferedReader br = null;
		String line = null;
		String key = null;

		try
		{
			String btnLayoutFile = GlobalVar.ConfigPath + "\\TouchScreenMode.ini";
			File file = new File(btnLayoutFile);
			if (!file.exists())
				return;

			if ((br = CommonMethod.readFileGB2312(btnLayoutFile)) == null)
				return;

			classBtnLayout = new ButtonLayout();
			classPageBtnLayout = new ButtonLayout();

			groupBtnLayout = new ButtonLayout();
			groupPageBtnLayout = new ButtonLayout();

			goodsBtnLayout = new ButtonLayout();
			goodsPageBtnLayout = new ButtonLayout();

			while ((line = br.readLine()) != null)
			{
				if (line.startsWith(";") || line.equals(""))
					continue;
				if (line.startsWith("#"))
				{
					String strVal = line.trim().substring(1);
					if (strVal.indexOf("=") > 0)
					{
						String[] val = strVal.split("=");
						if (val.length > 1 && val[0].trim().equalsIgnoreCase("StartLevel"))
						{
							STARTLEVEL = Integer.parseInt(val[1].trim());
							continue;
						}
					}
				}

				if (line.trim().charAt(0) == '[' && line.trim().charAt(line.trim().length() - 1) == ']')
				{
					key = line.substring(1, line.length() - 1).trim();

					if (key.equalsIgnoreCase("ClassButton"))
					{
						classBtnLayout.BtnOwer = key;
						key = "classBtnLayout";
					}
					else if (key.equalsIgnoreCase("ClassPageButton"))
					{
						classPageBtnLayout.BtnOwer = key;
						key = "classPageBtnLayout";
					}
					else if (key.equalsIgnoreCase("GroupButton"))
					{
						groupBtnLayout.BtnOwer = key;
						key = "groupBtnLayout";
					}
					else if (key.equalsIgnoreCase("GroupPageButton"))
					{
						groupPageBtnLayout.BtnOwer = key;
						key = "groupPageBtnLayout";
					}
					else if (key.equalsIgnoreCase("GoodsButton"))
					{
						goodsBtnLayout.BtnOwer = key;
						key = "goodsBtnLayout";
					}
					else if (key.equalsIgnoreCase("GoodsPageButton"))
					{
						goodsPageBtnLayout.BtnOwer = key;
						key = "goodsPageBtnLayout";
					}
					continue;
				}
				else
				{
					if (line.indexOf("=") > 0)
					{
						String[] item = line.trim().split("=");
						if (item.length > 1 && item[0].trim().equalsIgnoreCase("RowCount"))
							convertItem(key, item[0].trim(), item[1].trim(), "Int");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("ColumnCount"))
							convertItem(key, item[0].trim(), item[1].trim(), "Int");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("RowInterval"))
							convertItem(key, item[0].trim(), item[1].trim(), "Int");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("ColumnInterval"))
							convertItem(key, item[0].trim(), item[1].trim(), "Int");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("Width"))
							convertItem(key, item[0].trim(), item[1].trim(), "Int");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("Height"))
							convertItem(key, item[0].trim(), item[1].trim(), "Int");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("Font"))
							convertItem(key, item[0].trim(), item[1].trim(), "String");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("BtnDefaultColor"))
							convertItem(key, item[0].trim(), item[1].trim(), "String");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("BtnUpBackground"))
							convertItem(key, item[0].trim(), item[1].trim(), "String");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("BtnUpForeground"))
							convertItem(key, item[0].trim(), item[1].trim(), "String");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("BtnDownBackground"))
							convertItem(key, item[0].trim(), item[1].trim(), "String");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("BtnDownForeground"))
							convertItem(key, item[0].trim(), item[1].trim(), "String");
						else if (item.length > 1 && item[0].trim().equalsIgnoreCase("FunctionID"))
							convertItem(key, item[0].trim(), item[1].trim(), "Int");
					}
				}
			}

			classBtnLayout.transformStyle();
			classPageBtnLayout.transformStyle();

			groupBtnLayout.transformStyle();
			groupPageBtnLayout.transformStyle();

			goodsBtnLayout.transformStyle();
			goodsPageBtnLayout.transformStyle();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
				br = null;
			}
			catch (Exception ex)
			{
				br = null;
			}
		}
	}

	private void convertItem(String key, String lparam, String wparam, String type)
	{
		try
		{
			Object obj = this.getClass().getDeclaredField(key).get(this);
			Field field = obj.getClass().getDeclaredField(lparam);
			Object value = null;
			if (type.equalsIgnoreCase("Int"))
				value = new Integer(Integer.parseInt(wparam));
			else if (type.equalsIgnoreCase("String"))
				value = wparam;

			field.set(obj, value);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/* =======加载按钮=========== */
	public void initTouchButton(SaleForm saleForm)
	{
		try
		{
			// 加载第一级按钮
			drawClassTouchButton(saleForm.group_class, saleForm.class_page);
			// 加载第二级按钮
			drawGroupTouchButton(saleForm.group_group, saleForm.group_page);
			// 加载 第三级按钮
			drawGoodsTouchButton(saleForm.group_goods, saleForm.goods_page);

			// 创建各组监听器
			createMouseListener();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/* 绘制三级按钮 */
	// 创建第一级按钮
	private void drawClassTouchButton(Composite lparam, Composite wparam)
	{
		try
		{
			if (lparam != null)
			{
				lblClassBtnAry = new CLabel[classBtnLayout.RowCount][classBtnLayout.ColumnCount];

				for (int i = 0; i < lblClassBtnAry.length; i++)
				{
					for (int j = 0; j < lblClassBtnAry[i].length; j++)
					{
						CLabel button = new CLabel(lparam, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
						FormData buttonForm = new FormData();
						buttonForm.top = new FormAttachment(classBtnLayout.ColumnInterval, i * (classBtnLayout.Height + classBtnLayout.RowInterval));
						buttonForm.bottom = new FormAttachment(0, (i * classBtnLayout.Height) + classBtnLayout.Height);
						buttonForm.left = new FormAttachment(0, j * (classBtnLayout.Width + classBtnLayout.ColumnInterval));
						buttonForm.right = new FormAttachment(0, j * (classBtnLayout.Width + classBtnLayout.ColumnInterval) + classBtnLayout.Width);
						button.setLayoutData(buttonForm);
						button.setBackground(classBtnLayout.BtnStyle.getBtnDefaultColor());
						button.setText("Test");

						lblClassBtnAry[i][j] = button;
					}
				}

				lparam.redraw();
			}

			if (wparam != null)
			{
				lblClassPageBtnAry = new CLabel[classPageBtnLayout.RowCount][classPageBtnLayout.ColumnCount];
				for (int i = 0; i < lblClassPageBtnAry.length; i++)
				{
					for (int j = 0; j < lblClassPageBtnAry[i].length; j++)
					{
						CLabel button = new CLabel(wparam, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
						FormData buttonForm = new FormData();
						buttonForm.top = new FormAttachment(classPageBtnLayout.ColumnInterval, i * (classPageBtnLayout.Height + classPageBtnLayout.RowInterval));
						buttonForm.bottom = new FormAttachment(0, (i * classPageBtnLayout.Height) + classPageBtnLayout.Height);
						buttonForm.left = new FormAttachment(0, j * (classPageBtnLayout.Width + classPageBtnLayout.ColumnInterval));
						buttonForm.right = new FormAttachment(0, j * (classPageBtnLayout.Width + classPageBtnLayout.ColumnInterval) + classPageBtnLayout.Width);
						button.setLayoutData(buttonForm);

						button.setBackground(classPageBtnLayout.BtnStyle.getBtnDefaultColor());
						button.setText("Test");

						lblClassPageBtnAry[i][j] = button;
					}
				}
				wparam.redraw();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 创建第二级按钮
	private void drawGroupTouchButton(Composite lparam, Composite wparam)
	{
		try
		{
			if (lparam != null)
			{
				lblGroupBtnAry = new CLabel[groupBtnLayout.RowCount][groupBtnLayout.ColumnCount];

				for (int i = 0; i < lblGroupBtnAry.length; i++)
				{
					for (int j = 0; j < lblGroupBtnAry[i].length; j++)
					{
						CLabel button = new CLabel(lparam, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
						FormData buttonForm = new FormData();
						buttonForm.top = new FormAttachment(groupBtnLayout.ColumnInterval, i * (groupBtnLayout.Height + groupBtnLayout.RowInterval));
						buttonForm.bottom = new FormAttachment(0, (i * groupBtnLayout.Height) + groupBtnLayout.Height);
						buttonForm.left = new FormAttachment(0, j * (groupBtnLayout.Width + groupBtnLayout.ColumnInterval));
						buttonForm.right = new FormAttachment(0, j * (groupBtnLayout.Width + groupBtnLayout.ColumnInterval) + groupBtnLayout.Width);

						button.setLayoutData(buttonForm);
						button.setBackground(groupBtnLayout.BtnStyle.getBtnDefaultColor());

						button.setText("Test");

						lblGroupBtnAry[i][j] = button;
					}
				}

				lparam.redraw();
			}

			if (wparam != null)
			{
				lblGroupPageBtnAry = new CLabel[groupPageBtnLayout.RowCount][groupPageBtnLayout.ColumnCount];
				for (int i = 0; i < lblGroupPageBtnAry.length; i++)
				{
					for (int j = 0; j < lblGroupPageBtnAry[i].length; j++)
					{
						CLabel button = new CLabel(wparam, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
						FormData buttonForm = new FormData();

						buttonForm.top = new FormAttachment(groupPageBtnLayout.ColumnInterval, i * (groupPageBtnLayout.Height + groupPageBtnLayout.RowInterval));
						buttonForm.bottom = new FormAttachment(0, (i * groupPageBtnLayout.Height) + groupPageBtnLayout.Height);
						buttonForm.left = new FormAttachment(0, j * (groupPageBtnLayout.Width + groupPageBtnLayout.ColumnInterval));
						buttonForm.right = new FormAttachment(0, j * (groupPageBtnLayout.Width + groupPageBtnLayout.ColumnInterval) + groupPageBtnLayout.Width);
						button.setLayoutData(buttonForm);
						button.setBackground(groupPageBtnLayout.BtnStyle.getBtnDefaultColor());

						button.setText("Test");

						lblGroupPageBtnAry[i][j] = button;
					}
				}
				wparam.redraw();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 创建第三级按钮
	private void drawGoodsTouchButton(Composite lparam, Composite wparam)
	{
		try
		{
			if (lparam != null)
			{
				lblGoodsBtnAry = new CLabel[goodsBtnLayout.RowCount][goodsBtnLayout.ColumnCount];

				for (int i = 0; i < lblGoodsBtnAry.length; i++)
				{
					for (int j = 0; j < lblGoodsBtnAry[i].length; j++)
					{
						CLabel button = new CLabel(lparam, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
						FormData buttonForm = new FormData();
						/*
						 * buttonForm.top = new FormAttachment(0, 1 + i *
						 * (goodsBtnLayout.Height +
						 * goodsBtnLayout.RowInterval)); buttonForm.bottom = new
						 * FormAttachment(0, 60 + i * (goodsBtnLayout.Height +
						 * goodsBtnLayout.RowInterval)); buttonForm.left = new
						 * FormAttachment(0, 6 + j * (goodsBtnLayout.Width +
						 * goodsBtnLayout.ColumnInterval)); buttonForm.right =
						 * new FormAttachment(0, 66 + j * (goodsBtnLayout.Width +
						 * goodsBtnLayout.ColumnInterval));
						 */
						/*
						 * buttonForm.top = new
						 * FormAttachment(goodsBtnLayout.ColumnInterval, i *
						 * (goodsBtnLayout.Height +
						 * goodsBtnLayout.RowInterval)); buttonForm.bottom = new
						 * FormAttachment(0, i *( goodsBtnLayout.Height
						 * +goodsBtnLayout.RowInterval)+ goodsBtnLayout.Height);
						 * buttonForm.left = new FormAttachment(0, j *
						 * (goodsBtnLayout.Width +
						 * goodsBtnLayout.ColumnInterval)); buttonForm.right =
						 * new FormAttachment(0, j * (goodsBtnLayout.Width +
						 * goodsBtnLayout.ColumnInterval)+goodsBtnLayout.Width);
						 */

						buttonForm.top = new FormAttachment(0, i * (goodsBtnLayout.Height + goodsBtnLayout.RowInterval));
						buttonForm.bottom = new FormAttachment(0, i * (goodsBtnLayout.Height + goodsBtnLayout.RowInterval) + goodsBtnLayout.Height);
						buttonForm.left = new FormAttachment(0, j * (goodsBtnLayout.Width + goodsBtnLayout.ColumnInterval));
						buttonForm.right = new FormAttachment(0, j * (goodsBtnLayout.Width + goodsBtnLayout.ColumnInterval) + goodsBtnLayout.Width);

						button.setLayoutData(buttonForm);
						button.setBackground(goodsBtnLayout.BtnStyle.getBtnDefaultColor());

						button.setText("三明治和\n奶黄面包\n¥ 100.00");

						lblGoodsBtnAry[i][j] = button;
					}
				}

				lparam.redraw();
			}

			if (wparam != null)
			{
				lblGoodsPageBtnAry = new CLabel[goodsPageBtnLayout.RowCount][goodsPageBtnLayout.ColumnCount];
				for (int i = 0; i < lblGoodsPageBtnAry.length; i++)
				{
					for (int j = 0; j < lblGoodsPageBtnAry[i].length; j++)
					{
						CLabel button = new CLabel(wparam, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
						FormData buttonForm = new FormData();
						/*
						 * buttonForm.top = new FormAttachment(0, (i *
						 * goodsPageBtnLayout.Height) +
						 * goodsPageBtnLayout.RowInterval); buttonForm.bottom =
						 * new FormAttachment(0, (i * goodsPageBtnLayout.Height) +
						 * goodsPageBtnLayout.Height); buttonForm.left = new
						 * FormAttachment(0, goodsPageBtnLayout.ColumnInterval);
						 * buttonForm.right = new FormAttachment(0,
						 * goodsPageBtnLayout.Width);
						 */
						buttonForm.top = new FormAttachment(0, i * (goodsPageBtnLayout.Height + goodsPageBtnLayout.RowInterval));
						buttonForm.bottom = new FormAttachment(0, i * (goodsPageBtnLayout.Height + goodsPageBtnLayout.RowInterval) + goodsPageBtnLayout.Height);
						buttonForm.left = new FormAttachment(0, j * (goodsPageBtnLayout.Width + goodsPageBtnLayout.ColumnInterval));
						buttonForm.right = new FormAttachment(0, j * (goodsPageBtnLayout.Width + goodsPageBtnLayout.ColumnInterval) + goodsPageBtnLayout.Width);
						button.setLayoutData(buttonForm);
						button.setBackground(goodsPageBtnLayout.BtnStyle.getBtnDefaultColor());

						button.setText("Test");

						lblGoodsPageBtnAry[i][j] = button;
					}
				}
				wparam.redraw();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/* =======绘制添加按钮监听=========== */
	public void addTouchButtonListener()
	{
		try
		{
			if (lblClassBtnAry != null)
			{
				for (int i = 0; i < lblClassBtnAry.length; i++)
					for (int j = 0; j < lblClassBtnAry[i].length; j++)
					{
						lblClassBtnAry[i][j].addMouseListener(classBtnMouseAdp);
						lblClassBtnAry[i][j].setData(i + "," + j); // 设置按钮在容器中的位置
					}
			}

			if (lblClassPageBtnAry != null && classPageBtnMouseAdp != null)
			{
				for (int i = 0; i < lblClassPageBtnAry.length; i++)
					for (int j = 0; j < lblClassPageBtnAry[i].length; j++)
					{
						lblClassPageBtnAry[i][j].addMouseListener(classPageBtnMouseAdp);
						lblClassPageBtnAry[i][j].setData(i + "," + j);
					}
			}

			if (lblGroupBtnAry != null && groupBtnMouseAdp != null)
			{
				for (int i = 0; i < lblGroupBtnAry.length; i++)
					for (int j = 0; j < lblGroupBtnAry[i].length; j++)
					{
						lblGroupBtnAry[i][j].addMouseListener(groupBtnMouseAdp);
						lblGroupBtnAry[i][j].setData(i + "," + j);
					}
			}
			if (lblGroupPageBtnAry != null && groupPageBtnMouseAdp != null)
			{
				for (int i = 0; i < lblGroupPageBtnAry.length; i++)
					for (int j = 0; j < lblGroupPageBtnAry[i].length; j++)
					{
						lblGroupPageBtnAry[i][j].addMouseListener(groupPageBtnMouseAdp);
						lblGroupPageBtnAry[i][j].setData(i + "," + j);
					}
			}

			if (lblGoodsBtnAry != null && goodsBtnMouseAdp != null)
			{
				for (int i = 0; i < lblGoodsBtnAry.length; i++)
					for (int j = 0; j < lblGoodsBtnAry[i].length; j++)
					{
						lblGoodsBtnAry[i][j].addMouseListener(goodsBtnMouseAdp);
						lblGoodsBtnAry[i][j].setData(i + "," + j);
					}
			}
			if (lblGoodsPageBtnAry != null && goodsPageBtnMouseAdp != null)
			{
				for (int i = 0; i < lblGoodsPageBtnAry.length; i++)
					for (int j = 0; j < lblGoodsPageBtnAry[i].length; j++)
					{
						lblGoodsPageBtnAry[i][j].addMouseListener(goodsPageBtnMouseAdp);
						lblGoodsPageBtnAry[i][j].setData(i + "," + j);
					}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// 创建各部分监听对象
	private void createMouseListener()
	{
		classBtnMouseAdp = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				try
				{
					final CLabel label = (CLabel) arg0.widget;

					Display.getDefault().syncExec(new Runnable()
					{
						public void run()
						{
							if (label.getText().length() > 0)
							{
								label.setBackground(classBtnLayout.BtnStyle.getBtnUpBackground());
								label.setForeground(classBtnLayout.BtnStyle.getBtnUpForeground());
							}
						};
					});

					updateDataCmd = new UpdateDataCommand(classPageBtnStatus, groupPageBtnStatus, new ClassGroupReceiver());
					updateDataCmd.execute();

					redrawCmd = new RedrawCommand(groupPageBtnStatus, new GroupGroupReceiver());
					redrawCmd.execute();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();

				}
			}

			public void mouseDown(final MouseEvent arg0)
			{
				try
				{
					final CLabel label = (CLabel) arg0.widget;

					if (label.getText().length() > 0)
					{
						label.setBackground(classBtnLayout.BtnStyle.getBtnDownBackground());
						label.setForeground(classBtnLayout.BtnStyle.getBtnDownForeground());
					}
					
					// 设置状态
					classPageBtnStatus.setUpdateTarget(UPDATE_CLASS_NEXT_STATUS);
					classPageBtnStatus.setMemo(label.getData());
/*					Display.getDefault().syncExec(new Runnable()
					{
						public void run()
						{

						}
					});*/
				}
				catch (Exception ex)
				{
					ex.printStackTrace();

				}
			}
		};
		classPageBtnMouseAdp = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(classPageBtnLayout.BtnStyle.getBtnUpBackground());
					label.setForeground(classPageBtnLayout.BtnStyle.getBtnUpForeground());
				}

				updateDataCmd = new UpdateDataCommand(classPageBtnStatus, null, new ClassGroupReceiver());
				updateDataCmd.execute();
				
				redrawCmd = new RedrawCommand(classPageBtnStatus, new ClassGroupReceiver());
				redrawCmd.execute();
			}

			public void mouseDown(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(classPageBtnLayout.BtnStyle.getBtnDownBackground());
					label.setForeground(classPageBtnLayout.BtnStyle.getBtnDownForeground());
				}
				// 设置状态
				classPageBtnStatus.setUpdateTarget(UPDATE_CLASS_PAGE_STATUS);
				classPageBtnStatus.setMemo(label.getData());


			}
		};
		groupBtnMouseAdp = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(groupBtnLayout.BtnStyle.getBtnUpBackground());
					label.setForeground(groupBtnLayout.BtnStyle.getBtnUpForeground());
				}
				
				updateDataCmd = new UpdateDataCommand(groupPageBtnStatus, goodsPageBtnStatus, new GroupGroupReceiver());
				updateDataCmd.execute();
				
				redrawCmd = new RedrawCommand(goodsPageBtnStatus, new GoodsGroupReceiver());
				redrawCmd.execute();
			}

			public void mouseDown(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(groupBtnLayout.BtnStyle.getBtnDownBackground());
					label.setForeground(groupBtnLayout.BtnStyle.getBtnDownForeground());
				}

				// 设置状态
				groupPageBtnStatus.setUpdateTarget(UPDATE_GROUP_NEXT_STATUS);
				groupPageBtnStatus.setMemo(label.getData());


			}
		};
		groupPageBtnMouseAdp = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(groupPageBtnLayout.BtnStyle.getBtnUpBackground());
					label.setForeground(groupPageBtnLayout.BtnStyle.getBtnUpForeground());
				}

				updateDataCmd = new UpdateDataCommand(groupPageBtnStatus, null, new GroupGroupReceiver());
				updateDataCmd.execute();
				
				redrawCmd = new RedrawCommand(groupPageBtnStatus, new GroupGroupReceiver());
				redrawCmd.execute();
			}

			public void mouseDown(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(groupPageBtnLayout.BtnStyle.getBtnDownBackground());
					label.setForeground(groupPageBtnLayout.BtnStyle.getBtnDownForeground());
				}
				// 设置状态
				groupPageBtnStatus.setUpdateTarget(UPDATE_GROUP_PAGE_STATUS);
				groupPageBtnStatus.setMemo(label.getData());
			}
		};
		goodsBtnMouseAdp = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(goodsBtnLayout.BtnStyle.getBtnUpBackground());
					label.setForeground(goodsBtnLayout.BtnStyle.getBtnUpForeground());
				}

				updateDataCmd = new UpdateDataCommand(goodsPageBtnStatus, null, new GoodsGroupReceiver());
				updateDataCmd.execute();
			}

			public void mouseDown(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(goodsBtnLayout.BtnStyle.getBtnDownBackground());
					label.setForeground(goodsBtnLayout.BtnStyle.getBtnDownForeground());
				}
				// 设置状态
				goodsPageBtnStatus.setUpdateTarget(UPDATE_GOODS_NEXT_STATUS);
				goodsPageBtnStatus.setMemo(label.getData());
			}
		};
		goodsPageBtnMouseAdp = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(goodsPageBtnLayout.BtnStyle.getBtnUpBackground());
					label.setForeground(goodsPageBtnLayout.BtnStyle.getBtnUpForeground());
				}
				
				updateDataCmd = new UpdateDataCommand(goodsPageBtnStatus, null, new GoodsGroupReceiver());
				updateDataCmd.execute();
				
				redrawCmd = new RedrawCommand(goodsPageBtnStatus, new GoodsGroupReceiver());
				redrawCmd.execute();
			}

			public void mouseDown(final MouseEvent arg0)
			{
				CLabel label = (CLabel) arg0.widget;

				if (label.getText().length() > 0)
				{
					label.setBackground(goodsPageBtnLayout.BtnStyle.getBtnDownBackground());
					label.setForeground(goodsPageBtnLayout.BtnStyle.getBtnDownForeground());
				}

				goodsPageBtnStatus.setUpdateTarget(UPDATE_GOODS_PAGE_STATUS);
				goodsPageBtnStatus.setMemo(label.getData());

			}
		};
	}

	/* ==========命令模式=============== */
	// 命令抽象类-发送相关group重绘命令
	public abstract class ButtonCommand
	{
		protected CommandReceiver receiver = null;

		public abstract void execute();
	}

	public abstract class CommandReceiver
	{
		protected Nxmx_SaleEventTouch owner = null;
		protected PageButtonStatus curBtnStatus = null;
		protected PageButtonStatus asisistBtnStatus = null;
		protected UpdateContext context = null;

		public Nxmx_SaleEventTouch getOwner()
		{
			return owner;
		}

		public void setOwner(Nxmx_SaleEventTouch owner)
		{
			this.owner = owner;
		}

		public PageButtonStatus getCurBtnStatus()
		{
			return curBtnStatus;
		}

		public void setCurBtnStatus(PageButtonStatus curBtnStatus)
		{
			this.curBtnStatus = curBtnStatus;
		}

		public PageButtonStatus getAsisistBtnStatus()
		{
			return asisistBtnStatus;
		}

		public void setAsisistBtnStatus(PageButtonStatus asisistBtnStatus)
		{
			this.asisistBtnStatus = asisistBtnStatus;
		}

		public abstract void getData();// (PageButtonStatus btnStaus);

		public abstract void refreshUI();// (SaleEventTouchXMX ower);

		// oprtypte=true 则更新数据 ,为false则刷新界面
		public final void doAction(boolean oprtype)
		{
			if (oprtype)
			{
				getData();
			}
			else
			{
				refreshUI();
			}
		}
	}

	// 具体的命令类
	public class RedrawCommand extends ButtonCommand
	{
		public RedrawCommand(PageButtonStatus curBtnStatus, CommandReceiver receiver)
		{
			this.receiver = receiver;
			this.receiver.setCurBtnStatus(curBtnStatus);
			this.receiver.setOwner(curBtnStatus.getMainClass());
		}

		public void execute()
		{
			receiver.doAction(false);
		}
	}

	public class UpdateDataCommand extends ButtonCommand
	{
		public UpdateDataCommand(PageButtonStatus curBtnStatus, PageButtonStatus asisistBtnStatus, CommandReceiver receiver)
		{
			this.receiver = receiver;
			this.receiver.setCurBtnStatus(curBtnStatus);
			this.receiver.setAsisistBtnStatus(asisistBtnStatus);
			this.receiver.setOwner(curBtnStatus.getMainClass());
		}

		public void execute()
		{
			receiver.doAction(true);
		}
	}

	// 主要用于接收初始化及更新下一级菜单的命令
	public class ClassGroupReceiver extends CommandReceiver
	{
		public void getData()
		{
			// 根据当前设置的状态来构造不同的策略对象
			if (curBtnStatus.getUpdateTarget() == UPDATE_CLASS_INIT_STATUS)
			{
				context = new UpdateContext(new UpdateClassInitGroup(curBtnStatus, null));
				context.doUpdate();
			}
			else if (curBtnStatus.getUpdateTarget() == UPDATE_CLASS_PAGE_STATUS)
			{
				context = new UpdateContext(new UpateClassPageTurning(curBtnStatus, null));
				context.doUpdate();
			}
			// 处理点击上/下页翻页键
			else if (curBtnStatus.getUpdateTarget() == UPDATE_CLASS_NEXT_STATUS)
			{
				context = new UpdateContext(new UpdateClassNextGroup(curBtnStatus, asisistBtnStatus));
				context.doUpdate();
			}
		}

		public void refreshUI()
		{

			curBtnStatus.setCurBtnText(new String[] { "上\n\n页", "下\n\n页" });
			for (int i = 0; i < owner.lblClassPageBtnAry.length; i++)
				for (int j = 0; j < owner.lblClassPageBtnAry[i].length; j++)
					lblClassPageBtnAry[i][j].setText(curBtnStatus.getCurBtnText()[i]);
			owner.saleform.class_page.redraw();

			if ((curBtnStatus.getCurUpdateStatus() == OVER_CLASS_INIT_STATUS || curBtnStatus.getCurUpdateStatus() == OVER_CLASS_PAGE_STATUS) && curBtnStatus.getPageContent() != null)
			{
				for (int i = 0; i < curBtnStatus.getPageContent().length; i++)
					for (int j = 0; j < curBtnStatus.getPageContent()[i].length; j++)
					{
						ContentItemForTouchScrn item = curBtnStatus.getPageContent()[i][j];
						lblClassBtnAry[i][j].setText(item.catename);
					}
			}
			owner.saleform.group_class.redraw();
		}
	}

	public class GroupGroupReceiver extends CommandReceiver
	{
		public void getData()
		{
			// 根据当前设置的状态来构造不同的策略对象
			if (curBtnStatus.getUpdateTarget() == UPDATE_GROUP_INIT_STATUS)
			{
				context = new UpdateContext(new UpdateGroupInitGroup(curBtnStatus, asisistBtnStatus));
				context.doUpdate();
			}
			else if (curBtnStatus.getUpdateTarget() == UPDATE_GROUP_PAGE_STATUS)
			{
				context = new UpdateContext(new UpateGroupPageTurning(curBtnStatus, null));
				context.doUpdate();
			}
			else if (curBtnStatus.getUpdateTarget() == UPDATE_GROUP_NEXT_STATUS)
			{
				context = new UpdateContext(new UpdateGroupNextGroup(curBtnStatus, asisistBtnStatus));
				context.doUpdate();
			}
		}

		public void refreshUI()
		{
			// btnStatus = super.owner.groupPageBtnStatus;
			curBtnStatus.setCurBtnText(new String[] { "上\n\n页", "下\n\n页" });
			for (int i = 0; i < super.owner.lblGroupPageBtnAry.length; i++)
				for (int j = 0; j < super.owner.lblGroupPageBtnAry[i].length; j++)
					lblGroupPageBtnAry[i][j].setText(curBtnStatus.getCurBtnText()[i]);
			super.owner.saleform.group_page.redraw();

			if ((curBtnStatus.getCurUpdateStatus() == OVER_GROUP_INIT_STATUS || curBtnStatus.getCurUpdateStatus() == OVER_GROUP_PAGE_STATUS || curBtnStatus.getCurUpdateStatus() == OVER_CLASS_NEXT_STATUS) && curBtnStatus.getPageContent() != null)
			{
				for (int i = 0; i < curBtnStatus.getPageContent().length; i++)
					for (int j = 0; j < curBtnStatus.getPageContent()[i].length; j++)
					{
						ContentItemForTouchScrn item = curBtnStatus.getPageContent()[i][j];
						lblGroupBtnAry[i][j].setText(item.catename);
					}
			}
			owner.saleform.group_group.redraw();
		}
	}

	public class GoodsGroupReceiver extends CommandReceiver
	{
		private PageButtonStatus btnStatus = null;

		public void getData()
		{
			// 根据当前设置的状态来构造不同的策略对象
			if (curBtnStatus.getUpdateTarget() == UPDATE_GOODS_INIT_STATUS)
			{
				context = new UpdateContext(new UpdateGoodsInitGroup(curBtnStatus, asisistBtnStatus));
				context.doUpdate();
			}
			else if (curBtnStatus.getUpdateTarget() == UPDATE_GOODS_PAGE_STATUS)
			{
				context = new UpdateContext(new UpateGoodsPageTurning(curBtnStatus, null));
				context.doUpdate();
			}
			else if (curBtnStatus.getUpdateTarget() == UPDATE_GOODS_NEXT_STATUS)
			{
				context = new UpdateContext(new UpdatePosTableGroup(curBtnStatus, null));
				context.doUpdate();
			}
		}

		public void refreshUI()
		{
			btnStatus = super.owner.goodsPageBtnStatus;
			curBtnStatus.setCurBtnText(new String[] { "首\n\n页", "上\n\n一\n\n页", "第\n\n" + btnStatus.getCurPage() + "\n\n页", "下\n\n一\n\n页", "末\n\n页" });
			for (int i = 0; i < super.owner.lblGoodsPageBtnAry.length; i++)
				for (int j = 0; j < super.owner.lblGoodsPageBtnAry[i].length; j++)
					lblGoodsPageBtnAry[i][j].setText(btnStatus.getCurBtnText()[i]);
			super.owner.saleform.goods_page.redraw();

			if ((curBtnStatus.getCurUpdateStatus() == OVER_GOODS_INIT_STATUS || curBtnStatus.getCurUpdateStatus() == OVER_GOODS_PAGE_STATUS || curBtnStatus.getCurUpdateStatus() == OVER_GROUP_NEXT_STATUS) && curBtnStatus.getPageContent() != null)
			{
				for (int i = 0; i < curBtnStatus.getPageContent().length; i++)
					for (int j = 0; j < curBtnStatus.getPageContent()[i].length; j++)
					{
						ContentItemForTouchScrn item = curBtnStatus.getPageContent()[i][j];
						lblGoodsBtnAry[i][j].setText(item.name);
					}
			}
			owner.saleform.group_goods.redraw();
		}
	}

	/* ============策略模式============== */

	// =======初始化按钮=========
	// 目标按钮GROUP基类
	public abstract class TargetGroup
	{
		// protected int curUpdateGroup;
		protected PageButtonStatus pbs1 = null, pbs2 = null;

		public TargetGroup(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			this.pbs1 = pbs1;
			this.pbs2 = pbs2;
		}

		public void initVector(Vector goods)
		{
			for (int i = 0; i < goods.capacity(); i++)
			{
				ContentItemForTouchScrn item = new ContentItemForTouchScrn();
				item.barcode = " ";
				item.catename = " ";
				item.code = " ";
				item.gz = " ";
				item.name = " ";
				item.aliasesName = " ";

				goods.add(item);
			}
		}

		public abstract void execute();
	}

	// 处理程序启动时按钮初始化
	public class UpdateClassInitGroup extends TargetGroup
	{
		public UpdateClassInitGroup(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			try
			{
				long maxPage = pbs1.getMainClass().saleBS.getGoodsOrCateMaxCount(false, STARTLEVEL, pbs1.getCurCateLevel());

				if (maxPage == 0)
					return;

				pbs1.setCurSearchCateId(STARTLEVEL);

				pbs1.setMaxPage(maxPage);

				Vector goodslist = new Vector(pbs1.getItemsPerPage());

				initVector(goodslist);

				pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, false, 0, pbs1.getItemsPerPage(), pbs1.getCurSearchCateId(), pbs1.getCurCateLevel());

				if (pbs1.getPageContent() != null)
				{
					int count = 0;
					for (int i = 0; i < pbs1.getPageContent().length; i++)
						for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
							pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);

					pbs1.setCurUpdateStatus(OVER_CLASS_INIT_STATUS);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public class UpdateGroupInitGroup extends TargetGroup
	{
		public UpdateGroupInitGroup(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			try
			{
				long maxPage = pbs1.getMainClass().saleBS.getGoodsOrCateMaxCount(false, pbs2.getPageContent()[0][0].cateid, pbs1.getCurCateLevel());

				if (maxPage == 0)
					return;

				pbs1.setCurSearchCateId(pbs2.getPageContent()[0][0].cateid);

				pbs1.setMaxPage(maxPage);

				Vector goodslist = new Vector(pbs1.getItemsPerPage());

				initVector(goodslist);

				pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, false, 0, pbs1.getItemsPerPage(), pbs1.getCurSearchCateId(), pbs1.getCurCateLevel());

				if (pbs1.getPageContent() != null)
				{
					int count = 0;
					for (int i = 0; i < pbs1.getPageContent().length; i++)
						for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
							pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);

					pbs1.setCurUpdateStatus(OVER_GROUP_INIT_STATUS);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public class UpdateGoodsInitGroup extends TargetGroup
	{
		public UpdateGoodsInitGroup(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			try
			{
				long maxPage = pbs1.getMainClass().saleBS.getGoodsOrCateMaxCount(true, pbs2.getPageContent()[0][0].cateid, pbs1.getCurCateLevel());

				if (maxPage == 0)
					return;

				pbs1.setCurSearchCateId(pbs2.getPageContent()[0][0].cateid);

				pbs1.setMaxPage(maxPage);

				Vector goodslist = new Vector(pbs1.getItemsPerPage());
				initVector(goodslist);

				pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, true, 0, pbs1.getItemsPerPage(), pbs1.getCurSearchCateId(), pbs1.getCurCateLevel());

				if (pbs1.getPageContent() != null)
				{
					int count = 0;
					for (int i = 0; i < pbs1.getPageContent().length; i++)
						for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
						{
							/*
							 * ContentItemForTouchScrn tmpItem =
							 * (ContentItemForTouchScrn) goodslist.get(count++);
							 * tmpItem.cateid =
							 * pbs2.getPageContent()[0][0].cateid;
							 * tmpItem.catelevelid =
							 * pbs2.getPageContent()[0][0].catelevelid;
							 * tmpItem.parentcateid =
							 * pbs2.getPageContent()[0][0].parentcateid;
							 * pbs1.getPageContent()[i][j] = tmpItem;
							 */
							pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);
						}
					pbs1.setCurUpdateStatus(OVER_GOODS_INIT_STATUS);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	// 处理上/下翻页按钮
	public class UpateClassPageTurning extends TargetGroup
	{
		public UpateClassPageTurning(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			try
			{
				String[] pageBtn = ((String) pbs1.getMemo()).split(",");

				// >1则下一页
				if (Integer.parseInt(pageBtn[0].trim()) > 0)
				{
					if (pbs1.getMaxPage() == pbs1.getCurPage())
						return;

					long startpos = pbs1.getCurPage() * pbs1.getItemsPerPage();
					pbs1.setCurPage(pbs1.getCurPage() + 1);
					long endpos = startpos + pbs1.getItemsPerPage();

					Vector goodslist = new Vector(pbs1.getItemsPerPage());
					initVector(goodslist);

					pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, false, startpos, endpos, pbs1.getCurSearchCateId(), pbs1.getCurCateLevel());

					if (pbs1.getPageContent() != null)
					{
						int count = 0;
						for (int i = 0; i < pbs1.getPageContent().length; i++)
							for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
								pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);

						pbs1.setCurUpdateStatus(OVER_CLASS_PAGE_STATUS);
					}
				}
				else
				{
					if (pbs1.getCurPage() == 1)
						return;

					long endpos = pbs1.getCurPage() * pbs1.getItemsPerPage() - pbs1.getItemsPerPage();
					long startpos = endpos - pbs1.getItemsPerPage();
					pbs1.setCurPage(pbs1.getCurPage() - 1);

					Vector goodslist = new Vector(pbs1.getItemsPerPage());
					initVector(goodslist);

					pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, false, startpos, endpos, pbs1.getCurSearchCateId(), pbs1.getCurCateLevel());

					if (pbs1.getPageContent() != null)
					{
						int count = 0;
						for (int i = 0; i < pbs1.getPageContent().length; i++)
							for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
								pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);

						pbs1.setCurUpdateStatus(OVER_CLASS_PAGE_STATUS);
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public class UpateGroupPageTurning extends TargetGroup
	{
		public UpateGroupPageTurning(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			try
			{
				String[] pageBtn = ((String) pbs1.getMemo()).split(",");

				// >1则下一页
				if (Integer.parseInt(pageBtn[0].trim()) > 0)
				{
					if (pbs1.getMaxPage() == pbs1.getCurPage())
						return;

					long startpos = pbs1.getCurPage() * pbs1.getItemsPerPage();
					pbs1.setCurPage(pbs1.getCurPage() + 1);
					long endpos = startpos + pbs1.getItemsPerPage();

					Vector goodslist = new Vector(pbs1.getItemsPerPage());
					initVector(goodslist);

					pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, false, startpos, endpos, pbs1.getCurSearchCateId(), pbs1.getCurCateLevel());

					if (pbs1.getPageContent() != null)
					{
						int count = 0;
						for (int i = 0; i < pbs1.getPageContent().length; i++)
							for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
								pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);

						pbs1.setCurUpdateStatus(OVER_GROUP_PAGE_STATUS);
					}
				}
				else
				{
					if (pbs1.getCurPage() == 1)
						return;

					long endpos = pbs1.getCurPage() * pbs1.getItemsPerPage() - pbs1.getItemsPerPage();
					long startpos = endpos - pbs1.getItemsPerPage();
					pbs1.setCurPage(pbs1.getCurPage() - 1);

					Vector goodslist = new Vector(pbs1.getItemsPerPage());
					initVector(goodslist);

					pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, false, startpos, endpos, pbs1.getCurSearchCateId(), pbs1.getCurCateLevel());

					if (pbs1.getPageContent() != null)
					{
						int count = 0;
						for (int i = 0; i < pbs1.getPageContent().length; i++)
							for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
								pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);

						pbs1.setCurUpdateStatus(OVER_GROUP_PAGE_STATUS);
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public class UpateGoodsPageTurning extends TargetGroup
	{
		public UpateGoodsPageTurning(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			try
			{
				String[] pageBtn = ((String) pbs1.getMemo()).split(",");
				long startpos = 0;
				long endpos = 0;

				// 首页
				if (Integer.parseInt(pageBtn[0].trim()) == 0)
				{
					pbs1.setCurPage(1);
					endpos = pbs1.getItemsPerPage();
				}
				else if (Integer.parseInt(pageBtn[0].trim()) == 1)
				{
					if (pbs1.getCurPage() <= 1)
						return;
					endpos = pbs1.getCurPage() * pbs1.getItemsPerPage() - pbs1.getItemsPerPage();
					startpos = endpos - pbs1.getItemsPerPage();
					pbs1.setCurPage(pbs1.getCurPage() - 1);
				}
				else if (Integer.parseInt(pageBtn[0].trim()) == 2)
				{
					return;
				}
				else if (Integer.parseInt(pageBtn[0].trim()) == 3)
				{
					if (pbs1.getMaxPage() == pbs1.getCurPage())
						return;

					startpos = pbs1.getCurPage() * pbs1.getItemsPerPage();
					pbs1.setCurPage(pbs1.getCurPage() + 1);
					endpos = startpos + pbs1.getItemsPerPage();
				}
				else if (Integer.parseInt(pageBtn[0].trim()) == 4)
				{
					pbs1.setCurPage(pbs1.getMaxPage());
					endpos = pbs1.getCurPage() * pbs1.getItemsPerPage();
					startpos = endpos - pbs1.getItemsPerPage();
				}

				Vector goodslist = new Vector(pbs1.getItemsPerPage());
				initVector(goodslist);

				if (!pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, true, startpos, endpos, pbs1.getCurSearchCateId(), pbs1.getCurCateLevel()))
					return;

				if (pbs1.getPageContent() != null && goodslist.size() > 0)
				{
					int count = 0;
					for (int i = 0; i < pbs1.getPageContent().length; i++)
						for (int j = 0; j < pbs1.getPageContent()[i].length; j++)
						{
							/*
							 * ContentItemForTouchScrn tmpItem =
							 * (ContentItemForTouchScrn) goodslist.get(count++);
							 * tmpItem.cateid =
							 * pbs1.getPageContent()[0][0].cateid;
							 * tmpItem.catelevelid =
							 * pbs1.getPageContent()[0][0].catelevelid;
							 * tmpItem.parentcateid =
							 * pbs1.getPageContent()[0][0].parentcateid;
							 */
							pbs1.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);
						}

					pbs1.setCurUpdateStatus(OVER_GOODS_PAGE_STATUS);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	// 处理各级按钮所触发的下级菜单更新
	// 更新下一级菜单
	public class UpdateClassNextGroup extends TargetGroup
	{
		public UpdateClassNextGroup(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			// 初始化classGroup
			int row = 0, col = 0;

			try
			{
				if (pbs1.getMemo() != null)
				{
					String[] pos = ((String) pbs1.getMemo()).trim().split(",");
					row = Integer.parseInt(pos[0]);
					col = Integer.parseInt(pos[1]);

					ContentItemForTouchScrn curItem = pbs1.getPageContent()[row][col];

					// 根据cateid找
					long maxPage = pbs1.getMainClass().saleBS.getGoodsOrCateMaxCount(false, curItem.cateid, pbs2.getCurCateLevel());

					if (maxPage == 0)
						return;

					pbs2.setCurSearchCateId(curItem.cateid);
					pbs2.setMaxPage(maxPage);

					Vector goodslist = new Vector(pbs2.getItemsPerPage());
					initVector(goodslist);

					pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, false, 0, pbs2.getItemsPerPage(), pbs2.getCurSearchCateId(), pbs2.getCurCateLevel());

					if (pbs2.getPageContent() != null)
					{
						int count = 0;
						for (int i = 0; i < pbs2.getPageContent().length; i++)
							for (int j = 0; j < pbs2.getPageContent()[i].length; j++)
								pbs2.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);

						pbs2.setCurUpdateStatus(OVER_CLASS_NEXT_STATUS);
					}
				}
				pbs1.setMemo(null);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public class UpdateGroupNextGroup extends TargetGroup
	{
		public UpdateGroupNextGroup(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			try
			{
				// 初始化classGroup
				int row = 0, col = 0;

				if (pbs1.getMemo() != null)
				{
					String[] pos = ((String) pbs1.getMemo()).trim().split(",");
					row = Integer.parseInt(pos[0]);
					col = Integer.parseInt(pos[1]);
					ContentItemForTouchScrn curItem = pbs1.getPageContent()[row][col];

					long maxPage = pbs1.getMainClass().saleBS.getGoodsOrCateMaxCount(true, curItem.cateid, pbs2.getCurCateLevel());

					if (maxPage == 0)
						return;

					pbs2.setCurSearchCateId(curItem.cateid);
					pbs2.setMaxPage(maxPage);
					pbs2.setCurPage(1);

					Vector goodslist = new Vector(pbs2.getItemsPerPage());
					initVector(goodslist);

					pbs1.getMainClass().saleBS.getGoodsOrCatePages(goodslist, true, 0, pbs2.getItemsPerPage(), pbs2.getCurSearchCateId(), pbs2.getCurCateLevel());

					if (pbs2.getPageContent() != null)
					{
						int count = 0;
						for (int i = 0; i < pbs2.getPageContent().length; i++)
							for (int j = 0; j < pbs2.getPageContent()[i].length; j++)
							{
								/*
								 * ContentItemForTouchScrn tmpItem =
								 * (ContentItemForTouchScrn)
								 * goodslist.get(count++); tmpItem.cateid =
								 * curItem.cateid; tmpItem.catelevelid =
								 * curItem.catelevelid; tmpItem.parentcateid =
								 * curItem.parentcateid;
								 */
								pbs2.getPageContent()[i][j] = (ContentItemForTouchScrn) goodslist.get(count++);
							}
						pbs2.setCurUpdateStatus(OVER_GROUP_NEXT_STATUS);
					}
				}
				pbs1.setMemo(null);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public class UpdatePosTableGroup extends TargetGroup
	{
		public UpdatePosTableGroup(PageButtonStatus pbs1, PageButtonStatus pbs2)
		{
			super(pbs1, pbs2);
		}

		public void execute()
		{
			// 初始化classGroup
			int row = 0, col = 0;

			try
			{
				if (pbs1.getMemo() != null)
				{
					String[] pos = ((String) pbs1.getMemo()).trim().split(",");
					row = Integer.parseInt(pos[0]);
					col = Integer.parseInt(pos[1]);

					ContentItemForTouchScrn curItem = pbs1.getPageContent()[row][col];
					if (curItem.barcode.trim().length() > 0)
					{
						pbs1.getMainClass().code.setText(curItem.barcode);

						NewKeyListener.sendKey(GlobalVar.Enter);
					}
				}
				pbs1.setMemo(null);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	// 策略执行类
	public class UpdateContext
	{
		TargetGroup target = null;

		public UpdateContext(TargetGroup target)
		{
			this.target = target;
		}

		public void doUpdate()
		{
			target.execute();
		}
	}

	/* ==============业务数据定义============= */

	// 上下翻页键状态
	class PageButtonStatus
	{
		private Nxmx_SaleEventTouch mainClass = null;

		private boolean isFirstPage; // 是否首页
		private boolean isLastPage; // 是否末页
		private long maxPage; // 最大页
		private int itemsPerPage; // 每页所拥有的记录数
		private long curPage; // 当前页
		private int curCateLevel; // 当前所处级次
		private int curSearchCateId; // 当前所属类编号
		private long curCursorPos; // 当前记录rownum
		private String[] curBtnText = null; // 翻页键所显示的文本
		// 每一页的数据
		private ContentItemForTouchScrn[][] pageContent = null;
		private int curUpdateStatus;
		private int updateTarget; // 用于保存更新目标
		private Object memo; // 用于保存附加数据;

		public PageButtonStatus(Nxmx_SaleEventTouch parent, int status, int rows, int cols, int btn, int level)
		{
			mainClass = parent;
			isFirstPage = true;
			itemsPerPage = rows * cols;
			curPage = 1;
			curBtnText = new String[btn];
			curCateLevel = level;
			pageContent = new ContentItemForTouchScrn[rows][cols];
			updateTarget = status; // 初始化为初始状态
		}

		public Nxmx_SaleEventTouch getMainClass()
		{
			return mainClass;
		}

		public void setMainClass(Nxmx_SaleEventTouch mainClass)
		{
			this.mainClass = mainClass;
		}

		public int getCurUpdateStatus()
		{
			return curUpdateStatus;
		}

		public void setCurUpdateStatus(int curUpdateStatus)
		{
			this.curUpdateStatus = curUpdateStatus;
		}

		public int getCurCateLevel()
		{
			return curCateLevel;
		}

		public void setCurCateLevel(int curCateLevel)
		{
			this.curCateLevel = curCateLevel;
		}

		public Object getMemo()
		{
			return memo;
		}

		public void setMemo(Object memo)
		{
			this.memo = memo;
		}

		public int getUpdateTarget()
		{
			return updateTarget;
		}

		public void setUpdateTarget(int updateTarget)
		{
			this.updateTarget = updateTarget;
		}

		public String[] getCurBtnText()
		{
			return curBtnText;
		}

		public void setCurBtnText(String[] curBtnText)
		{
			this.curBtnText = curBtnText;
		}

		public void setItemsPerPage(int items)
		{
			itemsPerPage = items;
		}

		public int getItemsPerPage()
		{
			return itemsPerPage;
		}

		public int getCurSearchCateId()
		{
			return curSearchCateId;
		}

		public void setCurSearchCateId(int curSearchCateId)
		{
			this.curSearchCateId = curSearchCateId;
		}

		public long getCurCursorPos()
		{
			return curCursorPos;
		}

		public void setCurCursorPos(int curCursorPos)
		{
			this.curCursorPos = curCursorPos;
		}

		public long getCurPage()
		{
			return curPage;
		}

		public void setCurPage(long curPage)
		{
			this.curPage = curPage;
		}

		public boolean isFirstPage()
		{
			return isFirstPage;
		}

		public void setFirstPage(boolean isFirstPage)
		{
			this.isFirstPage = isFirstPage;
		}

		public boolean isLastPage()
		{
			return isLastPage;
		}

		public void setLastPage(boolean isLastPage)
		{
			this.isLastPage = isLastPage;
		}

		public long getMaxPage()
		{
			return maxPage;
		}

		public void setMaxPage(long maxPage)
		{
			long pages = maxPage / this.getItemsPerPage();
			long modpage = maxPage % this.getItemsPerPage();

			if (pages < 1)
			{
				this.maxPage = 1;
			}
			else
			{
				this.maxPage = pages;
				if (modpage > 0)
					this.maxPage++;
			}
		}

		public ContentItemForTouchScrn[][] getPageContent()
		{
			return pageContent;
		}

		public void setPageContent(ContentItemForTouchScrn[][] pageContent)
		{
			this.pageContent = pageContent;
		}
	}

	/* =====================界面数据定义========================= */
	class ButtonLayout // 按钮布局
	{
		public String BtnOwer;
		public int RowCount;
		public int ColumnCount;
		public int RowInterval;
		public int ColumnInterval;
		public int Width;
		public int Height;
		public String Font;
		public String BtnDefaultColor;
		public String BtnUpBackground;
		public String BtnUpForeground;
		public String BtnDownBackground;
		public String BtnDownForeground;
		public int FunctionID;
		public ButtonBehavior BtnStyle;

		public ButtonLayout()
		{
			BtnStyle = new ButtonBehavior();
		}

		private int[] RGB(String background)
		{
			int[] rgb = { 0, 0, 0 };

			try
			{
				if (background != null && background.length() > 0)
				{
					if (background.startsWith("0x"))
					{
						String srgb = ExpressionDeal.replace(background.toLowerCase().trim(), "0x", "");
						if (srgb.trim().length() == 6)
						{
							rgb[0] = Integer.parseInt(srgb.substring(0, 2), 16);
							rgb[1] = Integer.parseInt(srgb.substring(2, 4), 16);
							rgb[2] = Integer.parseInt(srgb.substring(4, 6), 16);
						}
					}
				}
				return rgb;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return rgb;
			}
		}

		private Font FONT(String font)
		{
			Font defaultFont = SWTResourceManager.getFont("宋体", 10, SWT.BOLD);

			try
			{
				if (font != null && font.length() > 0)
				{
					if (font.indexOf(",") > 0)
					{
						String[] fontPara = Font.trim().split(",");
						if (fontPara.length > 1)
							defaultFont = SWTResourceManager.getFont(fontPara[0].trim(), Integer.parseInt(fontPara[1].trim()), SWT.BOLD);
					}
				}
				return defaultFont;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return defaultFont;
			}
		}

		public void transformStyle()
		{
			int[] rgb = null;
			try
			{
				rgb = RGB(BtnDefaultColor);
				BtnStyle.setBtnDefaultColor(SWTResourceManager.getColor(rgb[0], rgb[1], rgb[2]));

				rgb = RGB(BtnDownBackground);
				BtnStyle.setBtnDownBackground(SWTResourceManager.getColor(rgb[0], rgb[1], rgb[2]));
				rgb = RGB(BtnDownForeground);
				BtnStyle.setBtnDownForeground(SWTResourceManager.getColor(rgb[0], rgb[1], rgb[2]));

				rgb = RGB(BtnUpBackground);
				BtnStyle.setBtnUpBackground(SWTResourceManager.getColor(rgb[0], rgb[1], rgb[2]));
				rgb = RGB(BtnUpForeground);
				BtnStyle.setBtnUpForeground(SWTResourceManager.getColor(rgb[0], rgb[1], rgb[2]));

				BtnStyle.setShowFont(FONT(Font));

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	class ButtonBehavior // 按钮行为风格
	{
		private Color btnDefaultColor;
		private Color btnUpBackground;
		private Color btnDownBackground;
		private Color btnUpForeground;
		private Color btnDownForeground;
		private Font showFont;

		public Color getBtnDefaultColor()
		{
			return btnDefaultColor;
		}

		public void setBtnDefaultColor(Color btnDefaultColor)
		{
			this.btnDefaultColor = btnDefaultColor;
		}

		public Color getBtnDownBackground()
		{
			return btnDownBackground;
		}

		public void setBtnDownBackground(Color btnDownBackground)
		{
			this.btnDownBackground = btnDownBackground;
		}

		public Color getBtnDownForeground()
		{
			return btnDownForeground;
		}

		public void setBtnDownForeground(Color btnDownForeground)
		{
			this.btnDownForeground = btnDownForeground;
		}

		public Color getBtnUpBackground()
		{
			return btnUpBackground;
		}

		public void setBtnUpBackground(Color btnUpBackground)
		{
			this.btnUpBackground = btnUpBackground;
		}

		public Color getBtnUpForeground()
		{
			return btnUpForeground;
		}

		public void setBtnUpForeground(Color btnUpForeground)
		{
			this.btnUpForeground = btnUpForeground;
		}

		public Font getShowFont()
		{
			return showFont;
		}

		public void setShowFont(Font showFont)
		{
			this.showFont = showFont;
		}
	}
}
