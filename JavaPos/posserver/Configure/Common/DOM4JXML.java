package posserver.Configure.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class DOM4JXML 
{
	   private Document document = null;   
	   //private File file = null;
	   private Element rootelement = null;
	   private String strmsg = "";
	   private String path = "";
	   
	   /**
	    * 返回根元素
	    * @return 返回根元素
	    */
	   public Element GetRootElement()
	   {
		   return rootelement;
	   }
	   
	   /**
	    * 删除子结点
	    * @param element
	    */
	   public void RemoveChildElement(Element element)
	   {
		   	Iterator iter = element.elementIterator();
		   	while(iter.hasNext())
		   	{
		   		Element element1 = (Element)iter.next();
		   		element.remove(element1);
		   	}
	   }
	   
	   /**
	    * 返回document
	    * @return 返回document
	    */
	   public Document GetDocument()
	   {
		   return document;
	   }
	   
	   public void RemoveRootElement()
	   {
		   if (rootelement != null)
		   {
			   document.remove(this.rootelement);
			   rootelement = null;
		   }
	   }
	   
	   /** 
	    * 返回Xml路径
	    * @return 返回Xml路径
	    */
	   public String GetPath()
	   {
		   return path;
	   }
	   
	   /**
	    * 获得提示信息
	    * @return 返回信息
	    */
	   public String GetMsg()
	   {
		   return strmsg;
	   }
	   
		public boolean LoadXml(String xmlpath)
		{
			try
			{
				path = xmlpath;
				
				SAXReader reader = new SAXReader();

				File file = new File(xmlpath);
				
				if (!file.exists())
				{
					strmsg = "找不到文件 " + xmlpath;
					
					clear();
					
					return false;
				}
				
				document = reader.read(file);
				
				rootelement = document.getRootElement();

				return true;
			}
			catch(DocumentException ex)
			{
				ex.printStackTrace();
				strmsg = ex.getMessage();
				
				clear();
					
				return false;
			}
		}
	   
		private void clear()
		{
			path = "";
			document = null;
			rootelement = null;
		}
		
	   /**
	   * 根据子结点的值删除根结点
	   * @param nodename 子结点名称
	   * @param value 子结点值
	   * @return 是否删除成功
	   */
       public boolean DelNodeFromChildNode(String node,String childnode, String value)
       {
    	   List lisnode = document.selectNodes(node);
    	   
    	   Iterator iter = lisnode.iterator();
    	   while(iter.hasNext())
    	   {
    		   Element elementparent = (Element)iter.next();
    		   
    		   List lisnode1 = elementparent.selectNodes(childnode);
    		   
    		   Iterator iter1 = lisnode1.iterator();
    		   
    		   while(iter1.hasNext())
        	   {
    			   Element elementchildnode = (Element)iter1.next();
    			   
    			   if (elementchildnode.getText().equalsIgnoreCase(value))
    			   {
    				   Element elementparentparent = elementparent.getParent();
    				   elementparentparent.remove(elementparent);
    				   break;
    			   }
        	   }
    	   }
    	   
    	   return true;
       }
       
       public Vector GetElementValue(String node)
       {
    	   try
    	   {
	    	   Vector v = new Vector();
			   // 加入子结点
			   List listnode = document.selectNodes(node);
			   
			   Iterator iter = listnode.iterator();
			   
			   while(iter.hasNext())
			   {
				   Element element = (Element)iter.next();
				   
				   v.add(element.getTextTrim());
			   }
			   
			   return v;
    	   }
    	   catch(Exception ex)
    	   {
    		   ex.printStackTrace();
    		   return null;
    	   }
       }
       
       public void AddElement(String node,Element element)
       {
    	   // 加入根结点
    	   if (node == null || node.trim().length() <= 0)
    	   {
    		   document.add(element);
    		   rootelement = document.getRootElement();
    	   }
    	   else
    	   {
    		   // 加入子结点
    		   List listnode = document.selectNodes(node);
    		   
    		   Iterator iter = listnode.iterator();
    		   
    		   while(iter.hasNext())
    		   {
    			   Element elementparent = (Element)iter.next();
    			   
    			   elementparent.add(element);
    			   
    			   break;
    		   }
    	   }
       }
       
    
    /**
     * 创建XML文件
     * @param 文件名
     * @param XML根结点
     * @return
     */   
   	public boolean CreateXml(String filename,String rootelementname,Element element)
   	{
   		try
   		{
   			path = filename;
   			document = DocumentHelper.createDocument();
   			
   			if (rootelementname != null && rootelementname.length() > 0)
   			{
   				rootelement = document.addElement(rootelementname); 
   				
   				if (element != null) document.getRootElement().add(element);
   			}
   			else
   			{
   				if (element != null) document.add(element);
   				
   				rootelement = document.getRootElement();
   			}
   			
   			if (!WriteXmlDocument(filename))
   			{
   				clear();
   				return false;
   			}
   			
   			return true;
   		}
   		catch(Exception ex)
   		{
   			ex.printStackTrace();
   			strmsg = ex.getMessage();
   			
   			clear();
			
   			return false;
   		}
   	}
   	
   	public boolean WriteXmlDocument()
   	{
   		return WriteXmlDocument(path);
   	}
   	
   	public boolean WriteXmlDocument(String filename)
   	{
   		try
   		{
   			//用FileWriter写xml如果包含中文则会有乱码出现，所以改用FileOutputStream
	   		//FileWriter filewriter = new FileWriter(filename);   
   			FileOutputStream filewriter = new FileOutputStream(filename);
   			OutputFormat format = OutputFormat.createPrettyPrint();
   			format.setLineSeparator("\r\n");
   			format.setEncoding("UTF-8");
	   		//OutputFormat format = new OutputFormat("",true,"UTF-8");      
	   		//format.createPrettyPrint()
			XMLWriter  output = new XMLWriter(filewriter,format);  
			output.write(document);  
			output.close();   
				
	   		return true;
	   		
   			
   		  //java.io.Writer wr=new java.io.OutputStreamWriter(new java.io.FileOutputStream(filename),"UTF-8");   
   		  //document.write(wr);   
   		  //wr.close();
   		  //return true;
   		}
   		catch(Exception ex)
   		{
   			ex.printStackTrace();
   			strmsg = ex.getMessage();
   			return false;
   		}
   	}
}
