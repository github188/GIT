/*
 * Created on 2004-6-5
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.GoodsList;

/**
 * @author root
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TouchXGoods {

	public TouchXGoods() {
	}

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException 
	{
		GoodsList list = new GoodsList();
		Goods g ;
		g =
			new Goods(
				"000120",
				"9787508315164",
				"JAVA±‡≥Ã”Ô—‘",
				"223141",
				"BOOK",
				"volume",
				6500);
		Element elm = g.toElement();
		Goods   n   = new Goods( elm );
		System.out.println( n );
//		output( new Document(elm), System.out );
//		output( new Document(elm), new FileOutputStream( "f.xml" ) );
		System.out.println( outputString( elm ) ); 
		

	}

	public static String outputString( Element root )
	{
		XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
		return outputter.outputString( new Document(root) );    
	}
	
	public static void output( Element root, java.io.OutputStream out )
	{
		try{
			XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
			outputter.output(root, out );
		} catch (IOException e) {
				e.printStackTrace();
		}    
	}

	public static void output( Document doc, java.io.OutputStream out )
	{
		try{
			XMLOutputter outputter = new XMLOutputter( "  ", true, "GB2312" );
			outputter.output(doc, out );
		} catch (IOException e) {
				e.printStackTrace();
		}    
	}

}
