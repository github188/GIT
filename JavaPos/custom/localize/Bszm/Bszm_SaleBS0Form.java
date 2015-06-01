package custom.localize.Bszm;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.CommonMethod;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import  com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplate;
import com.efuture.javaPos.Struct.InputAreaItem;

import custom.localize.Bstd.Bstd_SaleBS;

public class Bszm_SaleBS0Form extends Bstd_SaleBS
{
	private String yyy = "专营专卖";
	private String gzh = "任意柜";
	private String code = "";
	private HashMap inputArea = null;
	private boolean curSaleType = false;

	public void setSaleForm()
	{
		readInputAreaTemplate();
		curSaleType = adjustInputArea(true);
	}

	protected boolean adjustInputArea(boolean saletype)
	{
		try
		{
			if (inputArea == null || inputArea.size() == 0)
				return false;

			Label lbl_yyy = (Label) clist.get("lbl_yyy");
			InputAreaItem item = (InputAreaItem) inputArea.get("lbl_yyy");
			setLabel(lbl_yyy, item, saletype);

			Text txt_yyy = (Text) clist.get("yyyh");
			item = (InputAreaItem) inputArea.get("yyyh");
			yyy = setText(txt_yyy, item, saletype);

			Label lbl_gz = (Label) clist.get("lbl_gz");
			item = (InputAreaItem) inputArea.get("lbl_gz");
			setLabel(lbl_gz, item, saletype);

			Text txt_gz = (Text) clist.get("gz");
			item = (InputAreaItem) inputArea.get("gz");
			gzh = setText(txt_gz, item, saletype);

			Label lbl_code = (Label) clist.get("lbl_barcode");
			item = (InputAreaItem) inputArea.get("lbl_barcode");
			setLabel(lbl_code, item, saletype);

			Text txt_code = (Text) clist.get("code");
			item = (InputAreaItem) inputArea.get("code");
			code = setText(txt_code, item, saletype);

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private void setLabel(Label lab, InputAreaItem item, boolean saletype)
	{
		try
		{
			if (item != null)
			{
				if (!item.visible)
					lab.setVisible(false);
				else if (item.text != null)
				{
					if (!item.text.equals(""))
					{
						if (saletype)
							lab.setText(item.text.trim());
						else
							lab.setText(item.checkText.trim());
					}
					lab.setBounds(item.x, item.y, item.width, item.heigh);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			lab.setText("未定义");
		}
	}

	private String setText(Text txt, InputAreaItem item, boolean saletype)
	{
		String title = "";

		//将title赋默认值
		if (item.title.trim().equalsIgnoreCase("yyyh"))
			title = GlobalInfo.posLogin.gh;  //默认为工号
		else if (item.title.trim().equalsIgnoreCase("gz"))
			title = ConfigClass.Market;		//默认为门店号
		else if (item.title.trim().equalsIgnoreCase("code"))
			title = code;

		try
		{
			if (item != null)
			{
				if (!item.visible)
				{
					txt.setVisible(false);
					return ""; // 控件处于隐藏状态时返回空串
				}
				else if (item.text != null)
				{
					String tmptxt = null;
					if (!item.text.trim().equals(""))
					{
						if (saletype)
						{
							if (item.text.trim().startsWith("$"))
							{
								tmptxt = item.text.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.sysPara, tmptxt, 0).toString();
							}
							else if (item.text.trim().startsWith("#"))
							{
								tmptxt = item.text.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.posLogin, tmptxt, 0).toString();
							}
							else if (item.text.trim().startsWith("@"))
							{
								tmptxt = item.text.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.syjDef, tmptxt, 0).toString();
							}
							else if (item.text.trim().startsWith("%"))
							{
								tmptxt = item.text.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.syjStatus, tmptxt, 0).toString();
							}
							else
								tmptxt = item.text.trim();
						}
						else
						{
							if (item.checkText.trim().startsWith("$"))
							{
								tmptxt = item.checkText.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.sysPara, tmptxt, 0).toString();
							}
							else if (item.checkText.trim().startsWith("#"))
							{
								tmptxt = item.checkText.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.posLogin, tmptxt, 0).toString();
							}
							else if (item.checkText.trim().startsWith("@"))
							{
								tmptxt = item.checkText.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.syjDef, tmptxt, 0).toString();
							}
							else if (item.checkText.trim().startsWith("%"))
							{
								tmptxt = item.checkText.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(GlobalInfo.syjStatus, tmptxt, 0).toString();
							}
							else if (item.checkText.trim().startsWith("&"))
							{
								tmptxt = item.checkText.trim().substring(1);
								tmptxt = PrintTemplate.findObjectValue(this, tmptxt, 0).toString();
							}
							else
								tmptxt = item.text.trim();
						}
					}

					if (tmptxt != null && tmptxt.length() > 0)
						title = tmptxt;

					txt.setBounds(item.x, item.y, item.width, item.heigh);
				}
			}
			return title;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return title;
		}
	}

	public void readInputAreaTemplate()
	{
		if (GlobalInfo.sysPara.iscfginputarea != 'Y')
			return;

		BufferedReader br = null;
		String line = null;
		String key = null;

		try
		{
			String cfgfile = GlobalVar.ConfigPath + "\\InputArea.ini";
			File file = new File(cfgfile);
			if (!file.exists())
				return;

			if ((br = CommonMethod.readFileGB2312(cfgfile)) == null)
				return;

			InputAreaItem item = new InputAreaItem();
			inputArea = new HashMap();

			while ((line = br.readLine()) != null)
			{
				if (line.startsWith(";") || line.equals(""))
					continue;

				if (line.trim().charAt(0) == '[' && line.trim().charAt(line.trim().length() - 1) == ']')
				{
					key = line.substring(1, line.length() - 1);
					item.title = key.toLowerCase();
					continue;
				}
				else
				{
					if (line.indexOf("=") > 0)
					{
						String[] tmp = line.split("=");
						if (tmp != null && tmp.length > 0)
						{
							if (tmp[0].trim().equalsIgnoreCase("Text"))
							{
								if (tmp.length > 1)
									item.text = tmp[1].trim();
								else
									item.text = "";
							}
							else if (tmp[0].trim().equalsIgnoreCase("PDtxt"))
							{
								if (tmp.length > 1)
									item.checkText = tmp[1].trim();
							}
							else if (tmp[0].trim().equalsIgnoreCase("Visible"))
							{
								if (tmp.length > 1)
									item.visible = (Integer.parseInt(tmp[1].trim()) > 0 ? true : false);
								else
									item.visible = true;
							}
							else if (tmp[0].trim().equalsIgnoreCase("X"))
							{
								if (tmp.length > 1)
									item.x = Integer.parseInt(tmp[1].trim());

							}
							else if (tmp[0].trim().equalsIgnoreCase("Y"))
							{
								if (tmp.length > 1)
									item.y = Integer.parseInt(tmp[1].trim());

							}
							else if (tmp[0].trim().equalsIgnoreCase("Width"))
							{
								if (tmp.length > 1)
									item.width = Integer.parseInt(tmp[1].trim());

							}
							else if (tmp[0].trim().equalsIgnoreCase("Heigh"))
							{
								if (tmp.length > 1)
									item.heigh = Integer.parseInt(tmp[1].trim());

								inputArea.put(key.toLowerCase(), item);
								item = new InputAreaItem();
							}
						}
					}

				}
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();

		}
		finally
		{
			br = null;
		}
	}

	public boolean setCheckGUI()
	{
		if (!adjustInputArea(false))
			return false;

		if (this.checkgz.length() > 0)
			gzh = this.checkgz;
		else if (GlobalInfo.sysPara.checklength > 0)
			gzh = checkdjbh.substring(0, GlobalInfo.sysPara.checklength) + (checkdjbh.length() > GlobalInfo.sysPara.checklength ? "-" + checkdjbh.substring(GlobalInfo.sysPara.checklength) : "");

		saleEvent.yyyh.setText(yyy);
		saleEvent.gz.setText(gzh);

		return true;
	}

	public void initSetYYYGZ(String type, boolean iscsinput)
	{
		// 是否输入营业员,Y-输入营业员/N-超市不输入营业员/B-百货不输入营业员/A-可输可不输,不输入时为超市,输入时为营业员
		if (SellType.ISCHECKINPUT(type))
		{
			if (curSaleType)
			{
				curSaleType = false;
				adjustInputArea(curSaleType);
			}

			saleEvent.saleform.setFocus(saleEvent.code);
		}
		else
		{
			if (GlobalInfo.syjDef.issryyy == 'N' || GlobalInfo.syjDef.issryyy == 'Z')
			{
				// 刷新一次输入区界面
				if (!curSaleType)
				{
					curSaleType = true;
					adjustInputArea(curSaleType);
				}

				saleEvent.yyyh.setText(yyy);
				saleEvent.gz.setText(gzh);

				saleEvent.saleform.setFocus(saleEvent.code);
			}
			else
			{
				if (iscsinput)
				{
					saleEvent.yyyh.setText("专营专卖");
					saleEvent.gz.setText("任意柜");
					saleEvent.saleform.setFocus(saleEvent.code);
				}
				else
				{
					saleEvent.saleform.setFocus(saleEvent.yyyh);
				}
			}
		}
	}

}
