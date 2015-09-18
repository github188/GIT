package com.royalstone.pos.common;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.util.Vector;
import java.util.List;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.royalstone.pos.invoke.realtime.RealTime;
import com.royalstone.pos.invoke.realtime.RealTimeException;

/**
 * Created by IntelliJ IDEA.
 * User: fire
 * Date: 2005-5-27
 */
public class AccurateList {
    private Vector accurateList;

    public AccurateList() {
        accurateList = new Vector();
    }

  	public Element toElement() {
		Element elm_list = new Element("accurateList");
		for (int i = 0; i < accurateList.size(); i++)
			elm_list.addContent(((Accurate) accurateList.get(i)).toElement());
		return elm_list;
	}
    public void add(Accurate accurate) {
		accurateList.add(accurate);
	}

    public void toXMLFile(String file) {
        FileOutputStream out=null;
		try {
			XMLOutputter outputter = new XMLOutputter("  ", true, "GB2312");
			outputter.setTextTrim(true);
			 out = new FileOutputStream(file);
			outputter.output(new Document(this.toElement()), out);
             out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
            if(out!=null)
                try {
                    out.close();
                } catch (IOException e) {}
        }
	}
    public void fromElement(Element root) {
		List list;
		try {
			list = root.getChildren("accurates");

			for (int i = 0; i < list.size(); i++)
				this.add(new Accurate((Element) list.get(i)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  	public void fromXMLFile(String file) {
		try {
			Document doc = (new SAXBuilder()).build(file);
			fromElement(doc.getRootElement());
		} catch (JDOMException e) {
			// TODO 此处应作特殊处理.
			e.printStackTrace();
		}
	}
    public String findPrecentage(int cardLevelID,int deptID)throws RealTimeException{

//        PosConfig config=PosConfig.getInstance();
//        String isRealTime=config.getString("ISREALTIME");
        String precent="0";
//		 String ifSupportOffLine=PosConfig.getInstance().getString("IFSUPPORTOFFLINE");

        if (PosContext.getInstance().isOnLine ()) {
        	precent = RealTime.getInstance().findPrecentage(Integer.toString(cardLevelID),Integer.toString(deptID));
	    }
 //       else{
//
//        for(int i=0;i<this.accurateList.size();i++){
//            Accurate acc=(Accurate)accurateList.get(i);
//               if(acc.getCardLevelID()==cardLevelID&&acc.getDeptID()==deptID)
//                   return acc.getAccurate();
//
//        }
//      }
        return precent;
    }
}
