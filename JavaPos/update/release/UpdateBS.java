package update.release;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UpdateBS
{
    public static String path = System.getProperty("user.dir");
    public static String publishfile = path + "/PublishConfig.ini";
    private MessageBox mb = null;
    private Element element = null;
    private Ftp ftp = null;
    private String ftpUpdateFile = "Update.xml";
    private String msg = "";
    public Vector vecFtpCfg = new Vector();

    public String getMessage()
    {
    	return msg;
    }
    
    public UpdateBS()
    {
        ftp = new Ftp();
    }

    public void init()
    {
        this.readConfigFile();
    }


    //保存FTP设置
    public boolean saveFtpPara()
    {
        String tempstr = "";

        try
        {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(publishfile)), "UTF-8");
            PrintWriter out = new PrintWriter(new BufferedWriter(writer));

            for (int i = 0;i < vecFtpCfg.size();i++)
            {
            	FtpCfgDef fcd = (FtpCfgDef)vecFtpCfg.get(i);
            	
            	tempstr = fcd.FtpIP;
            	tempstr = tempstr + ";" + fcd.FtpPort;
            	tempstr = tempstr + ";" + fcd.FtpUser;
            	tempstr = tempstr + ";" + fcd.FtpPwd;
            	tempstr = tempstr + ";" + fcd.Ftppasv;
            	
            	out.println(tempstr);
            }

            out.close();

            writer = null;
            out    = null;

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            msg = "发布配置\n\n"+publishfile+"\n\n保存失败";

            return false;
        }
    }

    public void setPath(String mpath)
    {
    	path = mpath;
    }
    
    public void setPublishfile(String mpublishfile)
    {
    	publishfile = mpublishfile;
    }

    //发送文件
    public void sendProgramFile(FtpCfgDef fcd,boolean isdirector,UpdateEvent ue)
    {
        try
        {   
        	/*
            if (!ftp.connect(fcd.FtpIP, fcd.FtpPort, fcd.FtpUser,fcd.FtpPwd,fcd.Ftppasv))
            {
            	fcd.Message = "服务器登录失败...";
            	fcd.Status = 3;

                return;
            }
            */
        	
            if (!ftp.connect(fcd))
            {
            	fcd.Message = "服务器登录失败...";
            	fcd.Status = 3;

                return;
            }

            if (!ftp.getFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
                if (!ReadXmlFile.createUpdateXml(path + "/" + ftpUpdateFile))
                {
                	fcd.Message = "生成更新文件失败...";
                	fcd.Status = 3;

                    return;
                }
            }

            if (isdirector)
            {
                if (!sendPath(fcd,ue.getTxtSourceFile().getText().trim(), ue))
                {
                    return;
                }
            }
            else
            {
                if (!sendFile(fcd,ue.getTxtSourceFile().getText().trim(), ue.getTxtModeName().getText().trim(), ue.getTxtTarget().getText().trim(), ue))
                {
                	return;
                }
            }
            
            if (!ftp.sendFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
            	fcd.Message = "配置文件发送失败...";
            	fcd.Status = 3;
            	
                return;
            }
            else
            {
            	fcd.Message = "文件发送成功...";
            	fcd.Status = 0;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fcd.Message = ex.getMessage();
            fcd.Status = 3;
        }
        finally
        {
            ReadXmlFile.delDir(path + "/" + ftpUpdateFile);
            ftp.close();
        }
    }

    public boolean sendPath(FtpCfgDef fcd,String path, UpdateEvent ue)
    {
        File f = new File(path);

        if (f.isDirectory())
        {
            File[] file = f.listFiles(new DirFileFilter(ue.getTxtModeName().getText().trim()));

            for (int i = 0; i < file.length; i++)
            {
                if (file[i].isFile())
                {
                    String target = ue.getTxtTarget().getText().trim();
                    String subpath = file[i].getPath().replace('\\', '/').replaceAll(ue.getTxtSourceFile().getText().trim().replace('\\', '/'), "");
                    subpath = subpath.replace('\\', '/');
                    subpath = subpath.substring(0, subpath.lastIndexOf('/')).trim();

                    if (subpath.indexOf('/') >= 0)
                    {
                        subpath = subpath.substring(subpath.indexOf('/') + 1).trim();
                    }

                    if ((subpath != null) && (subpath.length() > 0))
                    {
                        if ((target != null) && (target.length() > 0))
                        {
                        	target = target.replace('\\', '/');
                        	if (target.lastIndexOf('/') >= 0) target = target.substring(0, target.lastIndexOf('/')).trim();
                            target = target + "/" + subpath;
                        }
                        else
                        {
                            target = "./" + subpath;
                        }
                    }

                    while (Display.getCurrent().readAndDispatch());
                    
                    if (!sendFile(fcd,file[i].getPath(), file[i].getName(), target, ue))
                    {
                        return false;
                    }
                }
                else if (file[i].isDirectory())
                {
                    if (!sendPath(fcd,file[i].getPath(), ue))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean sendFile(FtpCfgDef fcd,String file, String modulename, String target, UpdateEvent ue)
    {
    	try
    	{    
	        //String modulename = ue.getTxtModeName().getText().trim();
	        String ftppath = ue.getTxtFtpPath().getText().trim();
	
	        //String target = ue.getTxtTarget().getText().trim();
	        String rename = ue.getTxtModule().getText().trim();
	        String syjlist = ue.getTxtSYJ().getText().trim();
	        String execfile = ue.getTxtExecuFileName().getText().trim();
	        boolean isdeladv =  ue.getBtnDelAdv().getSelection();
	        boolean decompress = ue.getBtnDecompress().getSelection();
	        boolean del = ue.getBtnDel().getSelection();
	        boolean isexecute = ue.getBtnExecute().getSelection();
	        
	
	        // 进度
	        ue.getLblStatus().setText("正在上传 "+ modulename);
	        
	        // 添加到Update.xml
	        if (!this.isUpdateXml(ue.getShell(), modulename, ftppath, target, rename, syjlist, execfile, decompress, del, isexecute,isdeladv, fcd))
	        {
	            return false;
	        }
	
	        if ((ftppath == null) || ftppath.equals(""))
	        {
	            if (!ftp.sendFile(modulename, file))
	            {
	            	fcd.Message = file + " 文件发送失败...";
	
	                return false;
	            }
	        }
	        else
	        {
	            int result = ftp.sendFile(modulename, file, ftppath);
	
	            if (result != 1)
	            {
		            switch (result)
		            {
		                case -1:
		                    fcd.Message = "无法改变服务器目录,"+ file + " 文件发送失败...";
		                    return false;
		
		                case -2:
		                    fcd.Message = file + " 文件发送失败...";
		                    return false;
		            }
	            }
	        }
	
	        // 进度
	        ue.getLblStatus().setText("");

    	}
    	catch(Exception ex)
    	{
    		return false;
    	}
    	
        return true;
    }

    //更新XML文件
    private boolean isUpdateXml(Shell shell, String modename, String ftppath, String target, String module, String syj, String executefilename,
                                boolean decompress, boolean del, boolean isexecute,boolean isdeladv,FtpCfgDef fcd)
    {
        try
        {
            element = ReadXmlFile.getReadData(path + "/" + ftpUpdateFile);
            
            if (element == null)
            {
            	fcd.Message = "读取配置文件失败...";

                return false;
            }
/*            
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
*/          
            // 找匹配的ITEM
            boolean flag = false;
            Iterator iterator = element.elementIterator("item");
            Element node = null;
            Attribute attribute = null;

            while (iterator.hasNext())
            {
                node      = (Element) iterator.next();
                attribute = node.attribute("ftppath");

                if (modename.trim().equals(node.getText().trim()) && ftppath.trim().equals(attribute.getValue()))
                {
                    flag = true;

                    break;
                }
            }

            if (flag)
            {
                /*if (ReadXmlFile.modifyUpdateXmlData(path + "/" + ftpUpdateFile,modename,ftppath, target, module, syj,executefilename, decompress, del,isexecute,getDate()+" "+getTime(),node))
                {
                        return true;
                }
                else
                {
                        mb = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
                        mb.setMessage("修改配置失败...");
                        mb.open();
                        return false;
                }*/
                if (ReadXmlFile.delXmlData(element, node))
                {
                    if (ReadXmlFile.addUpdateXmlData(path + "/" + ftpUpdateFile, modename, ftppath, target, module, syj, executefilename, decompress,
                                                         del, isexecute,isdeladv, getDate() + " " + getTime()))
                    {
                        return true;
                    }
                    else
                    {
                        mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                        mb.setMessage("追加配置失败...");
                        mb.open();

                        return false;
                    }
                }
                else
                {
                    mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                    mb.setMessage("删除配置失败...");
                    mb.open();

                    return false;
                }
            }
            else
            {
                if (ReadXmlFile.addUpdateXmlData(path + "/" + ftpUpdateFile, modename, ftppath, target, module, syj, executefilename, decompress,
                                                 del, isexecute, isdeladv,getDate() + " " + getTime()))
                {
                    return true;
                }
                else
                {
                    mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                    mb.setMessage("增加配置失败...");
                    mb.open();

                    return false;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            fcd.Message = ex.getMessage();
            
            return false;
        }
    }

    //判断IP是否合法
    public boolean isValidIPAddress(String strip)
    {
        int count = 0;
        boolean isIP = true;
        String temp = strip;
        String tem = "";

        try
        {
            if ((temp.charAt(0) == '.') || (temp.charAt(temp.length() - 1) == '.') || (temp.length() > 15))
            {
                isIP = false;
            }

            temp = temp + '.'; //下面测试用到

            for (int i = 0; i < temp.length(); i++)
            {
                if (temp.charAt(i) == '.')
                {
                    count++;

                    if (tem.equals(""))
                    {
                        isIP = false;

                        continue;
                    }

                    if (Integer.parseInt(tem) > 255)
                    {
                        isIP = false;
                    }

                    tem = "";

                    continue;
                }

                if ((temp.charAt(i) < '0') || (temp.charAt(i) > '9'))
                {
                    isIP = false;
                }

                tem += String.valueOf(temp.charAt(i));
            }

            if (count != 4)
            {
                isIP = false;
            }

            return isIP;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }

    //判断是否为数字
    public boolean isNumeric(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches())
        {
            return false;
        }

        return true;
    }

    //得到当前时间
    private String getDate()
    {
        Calendar calendar = null;

        try
        {
            calendar = Calendar.getInstance();

            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);

            if (month.length() < 2)
            {
                month = "0" + month;
            }

            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

            if (day.length() < 2)
            {
                day = "0" + day;
            }

            return year + "-" + month + "-" + day;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    //得到当前时间
    public String getTime()
    {
        Calendar calendar = null;

        try
        {
            calendar = Calendar.getInstance();

            String houre = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));

            if (houre.length() < 2)
            {
                houre = "0" + houre;
            }

            String minute = String.valueOf(calendar.get(Calendar.MINUTE));

            if (minute.length() < 2)
            {
                minute = "0" + minute;
            }

            String second = String.valueOf(calendar.get(Calendar.SECOND));

            if (second.length() < 2)
            {
                second = "0" + second;
            }

            return houre + ":" + minute + ":" + second;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    public boolean queryPublish(FtpCfgDef fcd,Vector vecpd)
    {
        try
        {
            if (!ftp.connect(fcd))
            {
            	msg = "服务器登录失败...";

                return false;
            }

            if (!ftp.getFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
                return true;
            }
            
            // 刷新列表
            element = ReadXmlFile.getReadData(path + "/" + ftpUpdateFile);
            if (element == null)
            {
            	msg = "读取配置文件失败...";
                return false;
            }
            Iterator iterator = element.elementIterator("item");
            Element node = null;
            while (iterator.hasNext())
            {
                node      = (Element) iterator.next();
                
                try
                {
                	PublishDef pd = new PublishDef();
                	vecpd.add(pd);
                	pd.name = node.getText().trim();
                	pd.datetime = node.attribute("datetime").getValue();
                	pd.ftppath = node.attribute("ftppath").getValue();
                	pd.installpath = node.attribute("installpath").getValue();
                	pd.filename = node.attribute("filename").getValue();
                	pd.code = node.attribute("code").getValue();
                	pd.del = node.attribute("del").getValue();
                	pd.decompress = node.attribute("decompress").getValue();
                	pd.execute = node.attribute("execute")!=null?node.attribute("execute").getValue():"";
                	pd.executefilename = node.attribute("executefilename")!=null?node.attribute("executefilename").getValue():"";
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return false;
                }
            }
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            
            msg = "查询发布失败";
            
            return false;
        }
        finally
        {
        	ftp.close();
        }
    }
    
    /*
    public boolean queryPublish(String Ip,int Port,String user,String pws,Vector vecpd)
    {
        try
        {
            if (!ftp.connect(Ip, Port, user,pws))
            {
            	msg = "服务器登录失败...";

                return false;
            }

            if (!ftp.getFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
                return true;
            }
            
            // 刷新列表
            element = ReadXmlFile.getReadData(path + "/" + ftpUpdateFile);
            if (element == null)
            {
            	msg = "读取配置文件失败...";
                return false;
            }
            Iterator iterator = element.elementIterator("item");
            Element node = null;
            while (iterator.hasNext())
            {
                node      = (Element) iterator.next();
                
                try
                {
                	PublishDef pd = new PublishDef();
                	vecpd.add(pd);
                	pd.name = node.getText().trim();
                	pd.datetime = node.attribute("datetime").getValue();
                	pd.ftppath = node.attribute("ftppath").getValue();
                	pd.installpath = node.attribute("installpath").getValue();
                	pd.filename = node.attribute("filename").getValue();
                	pd.code = node.attribute("code").getValue();
                	pd.del = node.attribute("del").getValue();
                	pd.decompress = node.attribute("decompress").getValue();
                	pd.execute = node.attribute("execute")!=null?node.attribute("execute").getValue():"";
                	pd.executefilename = node.attribute("executefilename")!=null?node.attribute("executefilename").getValue():"";
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return false;
                }
            }
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            
            msg = "查询发布失败";
            
            return false;
        }
        finally
        {
        	ftp.close();
        }
    }
    */
    
    //  读取配置文件
    public boolean readConfigFile()
    {
        try
        {
        	vecFtpCfg.clear();
        	
            if (!ReadXmlFile.isExist(publishfile))
            {
                return false;
            }

            BufferedReader br = ReadXmlFile.readFile(publishfile);

            if (br == null)
            {
                return false;
            }

            String line = null;
            String[] sp = null;

            while ((line = br.readLine()) != null)
            {
                if ((line == null) || (line.trim().length() <= 0))
                {
                    continue;
                }
                /*
            	tempstr = tempstr + fcd.FtpIP;
            	tempstr = ";" + tempstr + fcd.FtpPort;
            	tempstr = ";" + tempstr + fcd.FtpUser;
            	tempstr = ";" + tempstr + fcd.FtpPwd;
            	tempstr = ";" + tempstr + fcd.FtpName;
            	tempstr = ";" + tempstr + fcd.Ftppasv;
            	*/
                
                FtpCfgDef fcd = new FtpCfgDef();
                vecFtpCfg.add(fcd);
                
                sp = line.split(";");

                if ((sp.length >= 1) && (sp[0] != null) && !sp[0].trim().equals(""))
                {
                	fcd.FtpIP = sp[0].trim();
                }

                if ((sp.length >= 2) && (sp[1] != null) && !sp[1].trim().equals(""))
                {
                	if (sp[1].trim().length() >0)
                	{
                		try
                		{
                			fcd.FtpPort = Integer.parseInt(sp[1].trim());
                		}
                		catch(Exception ex)
                		{}
                	}
                }

                if ((sp.length >= 3) && (sp[2] != null) && !sp[2].trim().equals(""))
                {
                	fcd.FtpUser = sp[2].trim();

                    if (sp[2].trim().equals("anonymous"))
                    {
                    	fcd.isanonymous = true;
                    }
                }

                if (!fcd.isanonymous && (sp.length >= 4) && (sp[3] != null) && !sp[3].trim().equals(""))
                {
                	fcd.FtpPwd = sp[3].trim();
                }
                
                if ((sp.length >= 5) && (sp[4] != null) && !sp[4].trim().equals(""))
                {
                	fcd.Ftppasv = sp[4].trim();
                }
            }

            br.close();

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            msg = "读取配置失败!";
            return false;
        }
    }

    public boolean deletePublish(FtpCfgDef fcd,String modulename,String ftppath)
    {    	
        //boolean delok = false;
        // 删除
        try
        {
            if (!ftp.connect(fcd))
            {
            	msg = "服务器登录失败...";
                return false;
            }

            if (!ftp.getFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
            	msg = "下载更新配置文件失败...";
                return false;
            }
            
            // 删除节点
            element = ReadXmlFile.getReadData(path + "/" + ftpUpdateFile);
            if (element == null)
            {
            	msg = "读取配置文件失败...";
                return false;
            }
            Iterator iterator = element.elementIterator("item");
            Element node = null;
            while (iterator.hasNext())
            {
                node      = (Element) iterator.next();
                
                if (modulename.trim().equals(node.getText().trim()) && 
                	ftppath.trim().equals(node.attribute("ftppath").getValue().trim()))
                {
                	if (!ReadXmlFile.delXmlData(element, node))
                	{
                		msg = "删除发布配置失败...";
                        return false;
                	}
                    break;
                }
            }
            
            // 无论文件是否删除成功都发送更新配置
            ftp.delFile(modulename,ftppath);
            if (!ftp.sendFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
            	msg = "发送删除后的配置文件失败...";
                return false;
            }
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally
        {
        	ftp.close();
        }
    }
    
    /*
     public boolean deletePublish(String Ip,int Port,String user,String pws,String modulename,String ftppath)
    {    	
        //boolean delok = false;
        // 删除
        try
        {
            if (!ftp.connect(Ip,Port,user,pws))
            {
            	msg = "服务器登录失败...";
                return false;
            }

            if (!ftp.getFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
            	msg = "下载更新配置文件失败...";
                return false;
            }
            
            // 删除节点
            element = ReadXmlFile.getReadData(path + "/" + ftpUpdateFile);
            if (element == null)
            {
            	msg = "读取配置文件失败...";
                return false;
            }
            Iterator iterator = element.elementIterator("item");
            Element node = null;
            while (iterator.hasNext())
            {
                node      = (Element) iterator.next();
                
                if (modulename.trim().equals(node.getText().trim()) && 
                	ftppath.trim().equals(node.attribute("ftppath").getValue().trim()))
                {
                	if (!ReadXmlFile.delXmlData(element, node))
                	{
                		msg = "删除发布配置失败...";
                        return false;
                	}
                    break;
                }
            }
            
            // 无论文件是否删除成功都发送更新配置
            ftp.delFile(modulename,ftppath);
            if (!ftp.sendFile(ftpUpdateFile, path + "/" + ftpUpdateFile))
            {
            	msg = "发送删除后的配置文件失败...";
                return false;
            }
            
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally
        {
        	ftp.close();
        }
    }
     */
    
    class DirFileFilter implements FileFilter
	{
		private char[] pattern;

		public DirFileFilter(String s)
		{
			s = s.trim();
			s = toMinimalPattern(s);
			s = s.toUpperCase();
			pattern = s.toCharArray();
		}

		public boolean accept(File pathname)
		{
			String s = pathname.getPath();
			s = s.toUpperCase();
			char[] ca = s.toCharArray();
			return match(pattern, 0, ca, 0);
		}

		public String toMinimalPattern(String s)
		{
			int len = s.length();
			StringBuffer sb = new StringBuffer();
			int x = 0;
			int y = s.indexOf('*');
			while (x <= y)
			{
				y++;
				sb.append(s.substring(x, y));
				x = y;
				while (y < len && s.charAt(y) == '*')
				{
					y++;
					x = y;
				}
				y = s.indexOf('*', x);
			}
			sb.append(s.substring(x));
			return sb.toString();
		}

		public boolean match(String pattern, String fileName)
		{
			pattern = pattern.trim();
			pattern = toMinimalPattern(pattern);
			pattern = pattern.toUpperCase();
			char[] ca1 = pattern.toCharArray();
			fileName = fileName.trim();
			fileName = fileName.toUpperCase();
			char[] ca2 = fileName.toCharArray();
			return match(ca1, 0, ca2, 0);
		}

		private boolean match(char[] pattern, int px, char[] name, int nx)
		{
			while (px < pattern.length)
			{
				char pc = pattern[px];
				if (pc == '*')
				{
					if (px >= (pattern.length - 1)) return true;
					int x = nx;
					while (x < name.length)
					{
						if (match(pattern, px + 1, name, x)) return true;
						x++;
					}
					px++;
					continue;
				}
				if (nx >= name.length) return false;
				if (pc != '?')
				{
					if (pc != name[nx]) return false;
				}
				px++;
				nx++;
			}
			return (nx >= name.length);
		}

		public boolean isMatch(String mask, String nick)
		{
			mask = mask.trim();
			if (mask.equalsIgnoreCase("*")) { return true; }
			if (mask.startsWith("*"))
			{
				int end = -1;
				if (mask.indexOf("*", mask.indexOf("*") + 1) == -1)
				{
					end = mask.length();
				}
				else
				{
					end = mask.indexOf("*", mask.indexOf("*") + 1);
				}
				if (end == -1) { return false; }
				String tmask = mask.substring(1, end).trim();
				if (nick.indexOf(tmask) == -1)
				{
					return false;
				}
				else
				{
					mask = mask.substring(end).trim();
					nick = nick.substring(nick.indexOf(tmask)).trim();
					return isMatch(mask, nick);
				}
			}
			int end = -1;
			if (mask.indexOf("*") == -1)
			{
				end = mask.length();
			}
			else
			{
				end = mask.indexOf("*");
			}
			if (end == -1) { return false; }
			String tmask = mask.substring(0, end).trim();
			if (nick.startsWith(tmask))
			{
				if (end == mask.length()) { return true; }
				mask = mask.substring(end).trim();
				nick = nick.substring(tmask.length()).trim();
				return isMatch(mask, nick);
			}
			else
			{
				return false;
			}
		}
	}
}
