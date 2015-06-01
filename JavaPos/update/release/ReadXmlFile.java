package update.release;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultText;

import org.dom4j.Element;

public class ReadXmlFile
{
	static Document document = null;

	static File file = null;

	public ReadXmlFile()
	{

	}

	public static Element getReadData(String filename)
	{
		try
		{
			SAXReader reader = new SAXReader();

			file = new File(filename);
			document = reader.read(file);

			Element element = document.getRootElement();

            // 删除空行节点
            for (int i=0;i<element.content().size();i++)
            {
            	if (element.content().get(i).getClass().getName().equals("org.dom4j.tree.DefaultText"))
            	{
	            	String s = ((DefaultText)(element.content().get(i))).getText().replaceAll("\n", "");
	            	if (s == null || s.trim().length() <= 0)
	            	{
	            		element.content().remove(i);
	            		i--;
	            	}
            	}
            }
            
			return element;
		}
		catch (DocumentException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean addUpdateXmlData(String filename, String sourcefile, String ftppath, String target, String module, String syj, String executefilename, boolean decompress, boolean del, boolean isexecute, boolean isdeladv,String datetime)
	{
		try
		{
			Element item = document.getRootElement().addElement("item");

			//添加二级节点属性
			if (target != null)
			{
				item.addAttribute("installpath", target);
			}
			else
			{
				item.addAttribute("installpath", "");
			}

			if (ftppath != null)
			{
				item.addAttribute("ftppath", ftppath);
			}
			else
			{
				item.addAttribute("ftppath", "");
			}

			if (module != null)
			{
				item.addAttribute("filename", module);
			}
			else
			{
				item.addAttribute("filename", "");
			}

			if (decompress)
			{
				item.addAttribute("decompress", "yes");
			}
			else
			{
				item.addAttribute("decompress", "no");
			}

			if (del)
			{
				item.addAttribute("del", "yes");
			}
			else
			{
				item.addAttribute("del", "no");
			}

			if (isexecute)
			{
				item.addAttribute("execute", "yes");
			}
			
			if (isdeladv)
			{
				item.addAttribute("isdeladv", "yes");
			}
			else
			{
				item.addAttribute("execute", "no");
			}

			item.addAttribute("datetime", datetime);

			if (syj != null)
			{
				item.addAttribute("code", syj);
			}
			else
			{
				item.addAttribute("code", "");
			}

			if (decompress && executefilename != null && !executefilename.equals(""))
			{
				item.addAttribute("executefilename", executefilename);
				item.addAttribute("execute", "yes");
			}
			else
			{
				item.addAttribute("executefilename", "");
			}

			item.setText(sourcefile);

			OutputFormat format = new OutputFormat("", true, "UTF-8");

			XMLWriter output = new XMLWriter(new FileWriter(file), format);

			output.write(document);
			output.flush();
			output.close();

			return true;
		}
		catch (Exception exa)
		{
			exa.printStackTrace();
			return false;
		}
	}

	//创建XML文件
	public static boolean createUpdateXml(String filename)
	{
		try
		{
			document = DocumentHelper.createDocument();

			//Element root = 
			document.addElement("filelist");

			FileWriter filewriter = new FileWriter(filename);

			OutputFormat format = new OutputFormat("", true, "UTF-8");

			XMLWriter output = new XMLWriter(filewriter, format);

			output.write(document);
			output.close();
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean modifyUpdateXmlData(String filename, String sourcefile, String ftppath, String target, String module, String syj, String executefilename, boolean decompress, boolean del, boolean isexecute, String datetime, Element item)
	{
		try
		{
			Attribute attribute = null;

			if (target != null)
			{
				attribute = item.attribute("installpath");
				attribute.setValue(target);
			}
			else
			{
				attribute = item.attribute("installpath");
				attribute.setValue("");
			}

			if (ftppath != null)
			{
				attribute = item.attribute("ftppath");
				attribute.setValue(ftppath);
			}
			else
			{
				attribute = item.attribute("ftppath");
				attribute.setValue("");
			}

			if (module != null)
			{
				attribute = item.attribute("filename");
				attribute.setValue(module);
			}
			else
			{
				attribute = item.attribute("filename");
				attribute.setValue("");
			}

			if (decompress)
			{
				attribute = item.attribute("decompress");
				attribute.setValue("yes");
			}
			else
			{
				attribute = item.attribute("decompress");
				attribute.setValue("no");
			}

			if (del)
			{
				attribute = item.attribute("del");
				attribute.setValue("yes");
			}
			else
			{
				attribute = item.attribute("del");
				attribute.setValue("no");
			}

			if (isexecute)
			{
				attribute = item.attribute("execute");

				if (attribute != null)
				{
					attribute.setValue("yes");
				}
			}
			else
			{
				attribute = item.attribute("execute");
				if (attribute != null)
				{
					attribute.setValue("no");
				}
			}

			attribute = item.attribute("datetime");
			attribute.setValue(datetime);

			if (syj != null)
			{
				attribute = item.attribute("code");
				attribute.setValue(syj);
			}
			else
			{
				attribute = item.attribute("code");
				attribute.setValue("");
			}

			if (decompress && executefilename != null && !executefilename.equals(""))
			{
				attribute = item.attribute("executefilename");
				if (attribute != null)
				{
					attribute.setValue(executefilename);
				}

				attribute = item.attribute("execute");

				if (attribute != null)
				{
					attribute.setValue("yes");
				}
			}
			else
			{
				attribute = item.attribute("executefilename");
				if (attribute != null)
				{
					attribute.setValue("");
				}

				attribute = item.attribute("execute");

				if (attribute != null && !isexecute)
				{
					attribute.setValue("no");
				}
			}

			OutputFormat format = new OutputFormat("", true, "UTF-8");
			XMLWriter output = new XMLWriter(new FileWriter(file), format);

			output.write(document);
			output.flush();
			output.close();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}

	public static boolean delXmlData(Element element, Element node)
	{
		try
		{
			element.remove(node);
			
			OutputFormat format = new OutputFormat("", true, "UTF-8");
			XMLWriter output = new XMLWriter(new FileWriter(file), format);
		
			output.write(document);
			output.flush();
			output.close();
			
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			return false;
		}
	}

	//读取文件
	public static BufferedReader readFile(String name)
	{
		BufferedReader br = null;
		InputStreamReader read = null;

		try
		{
			read = new InputStreamReader(new FileInputStream(new File(name)), "UTF-8");
			br = new BufferedReader(read);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			read = null;
		}

		return br;
	}

	//判断文件是否存在
	public static boolean isExist(String filename)
	{
		try
		{
			File file = new File(filename);

			if (file.exists())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	//删除目录
	public static void delDir(String filename)
	{
		try
		{
			File file = new File(filename);

			if (!file.isDirectory())
			{
				file.delete();

				return;
			}
			else if (file.isDirectory())
			{
				String[] filelist = file.list();

				for (int i = 0; i < filelist.length; i++)
				{
					File delfile = new File(filename + "/" + filelist[i]);

					if (!delfile.isDirectory())
					{
						delfile.delete();
					}
					else if (delfile.isDirectory())
					{
						delDir(delfile.getPath());
					}
				}

				file.delete();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
