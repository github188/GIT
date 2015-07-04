/*
 * Created on 2004-8-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.royalstone.pos.util;

//import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class getCardType {
	
	public static void main(String[] args)
	{
		getCardType cardtype = getCardType.getInstance();
	}
	
	public getCardType()
	{
		AllCard=new ArrayList();
	}
	
	public static getCardType getInstance()
	{
		if( card_type == null ){
			card_type = new getCardType();
			card_type.fromXML( "cardtype.xml" );
		}
		return card_type;
	}
	
	public void fromXML( String file )
	{
		
		try{
			Document doc = (new SAXBuilder()).build( file );
			Element  root = doc.getRootElement();

			List cardtype = root.getChildren( "card_type" );

			for (int i = 0; i < cardtype.size(); i++)
			{
				ArrayList Card=new ArrayList();
				Element parm_item = (Element) cardtype.get(i);
				Element bankID= parm_item.getChild("bank_id");
				Element cardtypevalue= parm_item.getChild("cardtype");
				Element Bankvalue= parm_item.getChild("bank");
				Element Cardvalue= parm_item.getChild("cardnote");
				Card.add(bankID.getTextTrim());
				Card.add(cardtypevalue.getTextTrim());
				Card.add(Bankvalue.getTextTrim());
				Card.add(Cardvalue.getTextTrim());
				this.add(Card);
			}
			
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
	}
	public void add(ArrayList card)
	{
		AllCard.add(card);
	}
	public String getString(String name)
	{
		ArrayList card;
		for(int i=0;i<AllCard.size();i++)
		{
			card=(ArrayList)AllCard.get(i);
			if(((String)card.get(1)).equals(name))
			{
				return String.valueOf(card.get(0));
			}
		}
		return "";
	}
	public ArrayList getAllCardType()
	{
		return AllCard;
	}
	private static getCardType card_type = null;
	private ArrayList AllCard=null; 

}
