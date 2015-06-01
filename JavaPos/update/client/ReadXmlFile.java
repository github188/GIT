package update.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ReadXmlFile
{
	static int filecopycount = 1;

	public ReadXmlFile()
	{
		// offline test
	}

	public static NodeList getReadData(String filename)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = factory.newDocumentBuilder();
			Document cmddoc = build.parse(new File(filename));
			NodeList nl = cmddoc.getElementsByTagName("item");

			return nl;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static void delXmlData(String filename, String ftppath, String name)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder build = factory.newDocumentBuilder();

			//解析XML绝对路径
			Document document = build.parse(new File(filename));

			NodeList nodeList = document.getElementsByTagName("item");

			for (int i = 0; i < nodeList.getLength(); i++)
			{
				String tempftppath = ((Element) nodeList.item(i)).getAttribute("ftppath").trim();
				String tempname = ((Element) nodeList.item(i)).getFirstChild().getNodeValue().trim();

				if (tempftppath.trim().equals(ftppath.trim()) && tempname.trim().equals(name.trim()))
				{
					document.getDocumentElement().removeChild(nodeList.item(i));
					break;
				}
			}

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			File file = new File(filename);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//创建一个目录
	public static boolean createDir(String Localfilepath)
	{
		int index = -1;
		String strpath = "";
		String temppath[] = null;

		try
		{
			Localfilepath = SystemChangeChar(Localfilepath);

			index = Localfilepath.indexOf('/');

			if (index > -1)
			{
				temppath = Localfilepath.split("/");
				for (int i = 0; i < temppath.length; i++)
				{
					strpath = strpath + temppath[i] + "/";
					if (i == 0 && strpath.trim().indexOf('/') > -1 && temppath[i].trim().equals("")) continue;

					if (isExist(strpath.substring(0, strpath.lastIndexOf('/')))) continue;

					if (!fileMkdir(strpath.substring(0, strpath.lastIndexOf('/')))) return false;
				}
			}
			else if (Localfilepath.indexOf("\\") > -1)
			{
				if (Localfilepath.indexOf("\\") == Localfilepath.lastIndexOf("\\")) return true;

				temppath = Localfilepath.split("\\\\\\\\");

				for (int i = 0; i < temppath.length; i++)
				{
					strpath = strpath + temppath[i] + "\\";

					if (i == 0 && temppath[i].trim().indexOf(':') > -1) continue;

					if (isExist(strpath)) continue;

					if (!fileMkdir(strpath)) return false;
				}
			}
			else
			{
				if (!isExist(Localfilepath))
				{
					if (!fileMkdir(Localfilepath)) return false;
				}
			}

			return true;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	private static boolean fileMkdir(String filepath)
	{
		try
		{
			File file = new File(filepath);

			if (file.mkdir())
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

	//判断文件名是否是个目录
	public static boolean isDir(String filename)
	{
		try
		{
			File file = new File(filename);

			if (file.isDirectory())
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
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			read = null;
		}

		return br;
	}

	//解压程序
	public static void unzipPrc(String zipFile, String outFilePath, int mode) throws Exception
	{
		ZipFile zf = null;

		try
		{
			File file = new File(zipFile);

			String fileName = file.getName();

			//通过整行組成输出路径
			if (mode == 1)
			{
				outFilePath += File.separator;
			}
			else
			{
				outFilePath += File.separator + fileName.substring(0, fileName.length() - 4) + File.separator;
			}

			File tmpFileDir = new File(outFilePath);

			//创建目录
			tmpFileDir.mkdirs();

			//打开一个要读取的zip文件
			zf = new ZipFile(zipFile);

			FileOutputStream fos = null;

			byte[] buf = new byte[1024];

			//返回ZIP文件项的枚举
			Enumeration em = zf.getEntries();

			while (em.hasMoreElements())
			{
				//返回压缩文件项
				ZipEntry ze = (ZipEntry) em.nextElement();

				//如果此为目录项，返回true。
				if (ze.isDirectory())
				{
					continue;
				}

				//返回读取指定zip文件项内容的输入流
				DataInputStream dis = new DataInputStream(zf.getInputStream(ze));

				//返回项的名称
				String currentFileName = ze.getName();

				if (currentFileName.trim().equals("\\"))
				{
					continue;
				}

				int dex = currentFileName.lastIndexOf('/');

				String currentoutFilePath = null;
				currentoutFilePath = outFilePath;

				if (dex > 0)
				{
					currentoutFilePath = currentoutFilePath + currentFileName.substring(0, dex) + File.separator;
					File currentFileDir = new File(currentoutFilePath);
					currentFileDir.mkdirs();
				}

				// 目标文件不可写先删除，delete方法可以删除只读文件
				File f = new File(outFilePath + ze.getName());
				if (!f.canWrite()) f.delete();

				// 写入目标文件
				fos = new FileOutputStream(outFilePath + ze.getName());
				int readLen = 0;

				while ((readLen = dis.read(buf, 0, 1024)) > 0)
				{
					fos.write(buf, 0, readLen);
				}

				dis.close();
				fos.close();
			}

			zf.close();
		}
		finally
		{
			if (zf != null) zf.close();
		}
	}

	//copy程序
	public static boolean copyPrc(String str, String str1)
	{
		try
		{
			File file = new File(str);
			InputStream is = null;
			OutputStream os = null;
			byte[] b;
			File f = new File(str1);

			// 不可写先删除，delete方法可以删除只读文件
			if (!f.canWrite()) f.delete();

			if (!file.isDirectory())
			{
				is = new FileInputStream(str);
				b = new byte[is.available()];
				is.read(b);
				is.close();
				os = new FileOutputStream(str1);
				os.write(b);
				os.flush();
				os.close();

				return true;
			}
			else if (!f.exists())
			{
				f.mkdirs();
			}

			File[] filename = file.listFiles();

			for (int i = 0; i < filename.length; i++)
			{
				copyPrc(filename[i].getAbsolutePath(), str1 + "/" + filename[i].getName());
			}

			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();

			filecopycount++;
			if (filecopycount <= 3)
			{
				copyPrc(str, str1);
			}

			return false;
		}
	}

	public static String SystemChangeChar(String path)
	{
		if (System.getProperties().getProperty("os.name").substring(0, 5).equals("Linux"))
		{
			String str = path.replaceAll("\\\\", "/");
			return str;
		}
		else
		{
			String str = path.replaceAll("\\\\", "\\\\\\\\");
			return str.replaceAll("/", "\\\\\\\\");
		}
	}
}
