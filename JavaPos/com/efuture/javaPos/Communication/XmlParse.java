package com.efuture.javaPos.Communication;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ExpressionDeal;

public class XmlParse
{
    DocumentBuilderFactory dbf = null;
    DocumentBuilder db = null;
    Document doc = null;

    public Document getDocument()
    {
    	return doc;
    }
    
    public String doc2String()
    {
    	return doc2String(doc);
    }
    
    public static String doc2String(Document doc) 
    {
        String str = null;
        
        try
        {
        	// 由于1.4Jdk不支持OutputFormat方法,所以改成下面的方式来转换
        	//OutputFormat format    = new OutputFormat(doc); 
            //StringWriter stringOut = new StringWriter();    
            //XMLSerializer serial   = new XMLSerializer(stringOut,format);
            //serial.serialize(doc);
            //return stringOut.toString();
        	
            DOMSource source = new DOMSource(doc); 
            StringWriter writer = new StringWriter();        
            Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.transform(source, new StreamResult(writer));             
           
            String str1 = writer.getBuffer().toString();
            
            str1 = ExpressionDeal.replace(str1,"\r","");
            str1 = ExpressionDeal.replace(str1,"\n","");
            
            return str1;
        }
        catch(Exception e)
        {
        	PosLog.getLog("XmlParse").error(e);
            e.printStackTrace();
        }
        
        return str;
    }
    
    public XmlParse(String arg)
    {
        try
        {
            dbf = DocumentBuilderFactory.newInstance();
            db  = dbf.newDocumentBuilder();
            
            doc = db.parse(new InputSource(new StringReader(arg)));
        }
        catch (ParserConfigurationException e)
        {
        	PosLog.getLog(getClass()).error(e);
            e.printStackTrace();
        }
        catch (SAXException e)
        {
        	PosLog.getLog(getClass()).error(e);
            e.printStackTrace();
        }
        catch (IOException e)
        {
        	PosLog.getLog(getClass()).error(e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        String line = "<root><table><row><code>1</code><name>早班</name><btime>08:00</btime><etime>12:30</etime></row><row><code>2</code><name>晚班</name><btime>18:00</btime><etime>22:00</etime></row><row><code>5</code><name>全班</name><btime>08:00</btime><etime>20:00</etime></row><row><code>3</code><name>中班</name><btime>12:00</btime><etime>18:30</etime></row></table></root>";

        //String line2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <General><configure CmdCode=\"1\"><Code>001</Code> <CmdType>HttpCmd</CmdType> </configure></General>";
        XmlParse xml = new XmlParse(line);
        Vector v = xml.parseMeth(0, new String[] { "code", "name", "btime1" });

        for (int i = 0; i < v.size(); i++)
        {
            String[] row = (String[]) v.elementAt(i);

            for (int j = 0; j < row.length; j++)
            {
            	if (row[j].length()>0)
                System.out.print(row[j] + "      ");
            	else
            	System.out.print("null         ");
            }

            System.out.println("");
        }
    }

    public Vector parseMeth(int index, String[] arg)
    {
        Vector v = new Vector();
        NodeList nList = doc.getElementsByTagName("table");
        String[] row = null;
        NodeList nll = ((Element) nList.item((index<0?nList.getLength()-1:index))).getElementsByTagName("row");

        for (int j = 0; j < nll.getLength(); j++)
        {
            row = new String[arg.length];

            for (int i = 0; i < arg.length; i++)
            {
                NodeList t = ((Element) nll.item(j)).getElementsByTagName(arg[i]);

                if ((t == null) || (t.item(0) == null))
                {
                    row[i] = "";
                }
                else
                {
                    Text t1 = (Text) t.item(0).getFirstChild();

                    if (t1 != null)
                    {
                        row[i] = t1.getNodeValue();
                    }
                    else
                    {
                        row[i] = "";
                    }
                }
            }

            v.add(row);
        }

        return v;
    }

    public Vector parseMeth(int index)
    {
        Vector v = new Vector();
        NodeList nList = doc.getElementsByTagName("table");

        NodeList nll = ((Element) nList.item((index<0?nList.getLength()-1:index))).getElementsByTagName("row");

        for (int j = 0; j < nll.getLength(); j++)
        {
            NodeList t = ((Element) nll.item(j)).getElementsByTagName("*");
            String[] row = new String[t.getLength()];

            for (int k = 0; k < t.getLength(); k++)
            {
                Text t1 = (Text) t.item(k).getFirstChild();

                row[k] = t1.getNodeValue();
            }

            v.add(row);
        }

        return v;
    }
    
    public static void setElementValue(Element element,String val)   
    {   
        Node node=element.getOwnerDocument().createTextNode(val);   
        NodeList nl=element.getChildNodes();   
        for(int i=0;i<nl.getLength();i++)   
        {   
            Node nd=nl.item(i);   
            if(nd.getNodeType()==Node.TEXT_NODE)//是一个Text Node   
            {               
                  nd.setNodeValue(val);    
                  return;   
            }   
        }      
        
        element.appendChild(node);           
    }
}
