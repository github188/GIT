package bankpay.alipay.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bankpay.ailpay.obj.ResponseXmlObj;


public class ParseXml 
{
	public HashMap domParseXml(String str)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = null;  
		Document document = null;
		Element element = null;
		InputSource source = null;
		StringReader read = null;
		ResponseXmlObj responseXmlObj = new ResponseXmlObj();
		Map map = new HashMap();
		try {
			builder = factory.newDocumentBuilder();
			read = new StringReader(str);
            source = new InputSource(read);
			document = builder.parse(source);
			element = document.getDocumentElement();
//			//遍历获取request节点下的值
//			NodeList requestNodes = element.getElementsByTagName("param");
//			for(int r =0;r<requestNodes.getLength();r++)
//			{
//				Element paramElement = (Element) requestNodes.item(r);
//				//System.out.println(paramElement.getAttribute("name"));
//				for(int x=0;x<responseXmlObj.ref.length;x++)
//				{
//					if(responseXmlObj.ref[x].equals(paramElement.getAttribute("name")))
//					{
//						//System.out.println(paramElement.getFirstChild().getNodeValue());
//						map.put(responseXmlObj.ref[x], paramElement.getFirstChild().getNodeValue());
//					}
//				}
//			}
//			//end
			
			//遍历response节点下的值
			NodeList responseNodes = element.getElementsByTagName("response");
			if(null !=  responseNodes.item(0))
			{
			Element responseElement = (Element) responseNodes.item(0);
			NodeList childNodes = responseElement .getElementsByTagName("alipay");
			Element ailPayElement = (Element) childNodes.item(0);
			NodeList ccNodes = ailPayElement.getChildNodes();
			for(int i =0 ; i<ccNodes.getLength();i++)
			{
				//System.out.println(ccNodes.item(i).getNodeName());
				for(int j=0;j<responseXmlObj.ref.length;j++)
				{
					if(responseXmlObj.ref[j].equals(ccNodes.item(i).getNodeName()))
					{
						//System.out.println("1111"+ccNodes.item(i).getFirstChild().getNodeValue());
						map.put(ccNodes.item(i).getNodeName(), ccNodes.item(i).getFirstChild().getNodeValue());
						
					}
				}
			}
			}
			else//错误参数时解析
			{
				NodeList AilErrNodes = element.getChildNodes();
				for(int i =0 ; i<AilErrNodes.getLength();i++)
				{
					Element AilErrElement = (Element) AilErrNodes.item(i);
					//System.out.println(ccNodes.item(i).getNodeName());
					for(int j=0;j<responseXmlObj.ref.length;j++)
					{
						if(responseXmlObj.ref[j].equals(AilErrNodes.item(i).getNodeName()))
						{
							//System.out.println("1111"+ccNodes.item(i).getFirstChild().getNodeValue());
							map.put(AilErrNodes.item(i).getNodeName(), AilErrNodes.item(i).getFirstChild().getNodeValue());
							
						}
					}
				}
			}
			//END
			read.close();
			return (HashMap) map;
			 
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;  

	}
	public static void main(String [] args)
	{
		String str="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+"<alipay>"
					+"<is_success>T</is_success>"
					+"<request>"
					+"<param name=\"sign\">e6df6d9fdd6efc361a8c35045a24a8c9</param>"
					+"<param name=\"_input_charset\">utf-8</param>"
					+"<param name=\"total_fee\">0.01</param>"
					+"<param name=\"product_code\">QR_CODE_OFFLINE</param>"
					+"<param name=\"subject\">二维码支付-sky</param>"
					+"<param name=\"sign_type\">MD5</param>"
					+"<param name=\"service\">alipay.acquire.precreate</param>"
					+"<param name=\"partner\">2088201565141845</param>"
					+"<param name=\"out_trade_no\">8247173979129320</param>"
					+"</request>"
					+"<response>"
					+"<alipay>"
					+"<big_pic_url>https://mobilecodec.alipay.com/show.htm?code=pmxla8fifxd8dnna03&amp;d&amp;picSize=L</big_pic_url>"
					+"<out_trade_no>8247173979129320</out_trade_no>"
					+"<pic_url>https://mobilecodec.alipay.com/show.htm?code=pmxla8fifxd8dnna03&amp;d&amp;picSize=M</pic_url>"
					+"<qr_code>https://qr.alipay.com/pmxla8fifxd8dnna03</qr_code>"
					+"<result_code>SUCCESS</result_code>"
					+"<small_pic_url>https://mobilecodec.alipay.com/show.htm?code=pmxla8fifxd8dnna03&amp;d&amp;picSize=S</small_pic_url>"
					+"<voucher_type>qrcode</voucher_type>"
					+"</alipay>"
					+"</response>"
					+"<sign>3240f3ebc629ea2e4e67ed3cbe626319</sign>"
					+"<sign_type>MD5</sign_type>"
					+"</alipay>";
		
		String errStr ="<?xml version=\"1.0\" encoding=\"utf-8\"?>" 
					   +"<alipay>" 
	    			   +"<is_success>F</is_success>" 
	    			   +"<error>ILLEGAL_SIGN</error>" 
	    			   +"</alipay>"; 
		
		ParseXml parseXml = new ParseXml();
		parseXml.domParseXml(str);
	} 

}
