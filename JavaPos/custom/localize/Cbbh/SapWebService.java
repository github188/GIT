package custom.localize.Cbbh;

import java.io.Serializable;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

public class SapWebService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String endpoint = null;
	
	public SapWebService() {
		// TODO Auto-generated constructor stub
		endpoint = "http://192.1.33.41:50000/XISOAPAdapter/MessageServlet?senderParty=&senderService=EFUT&receiverParty=&receiverService=&interface=SI_CREDIT_INFO_OUT_SYN&interfaceNamespace=urn%3Acb%3Aefut%3Acredit_limit_info_in";	
	}
	
	public static void main(String args[])
	{
		SapWebService  service = new SapWebService();
		service.SI_CREDIT_INFO_OUT_SYN("32");
	}
	
	public boolean SI_CREDIT_INFO_OUT_SYN(String id)
	{
		Service service = new Service();

		Call call;
		try {
			call = (Call) service.createCall();
		call.setUsername("EFUT_USER");
		call.setPassword("123456");

		call.setTargetEndpointAddress(endpoint);

		call.setOperationName(new QName("urn:cb:efut:credit_limit_info_in", "SI_CREDIT_INFO_OUT_SYN"));
		MT_CREDIT_INFO_REQ req =  new MT_CREDIT_INFO_REQ();
		req.setKUNNR(id);
		
		QName qn = new QName("urn:cb:efut:credit_limit_info_in", "MT_CREDIT_INFO_REQ");
		call.addParameter("MT_CREDIT_INFO_REQ", qn, javax.xml.rpc.ParameterMode.IN); // 接口的参数
		call.registerTypeMapping(MT_CREDIT_INFO_REQ.class, qn,
				     new BeanSerializerFactory(MT_CREDIT_INFO_REQ.class, qn),//序列化
				     new BeanDeserializerFactory(MT_CREDIT_INFO_REQ.class, qn));

		call.setReturnClass(DT_CREDIT_INFO_RESP.class); // 设置返回类型
		
		DT_CREDIT_INFO_RESP result = (DT_CREDIT_INFO_RESP) call.invoke(new Object[] {req});
		
		System.out.println("result is " + result.getSKFOR());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public class MT_CREDIT_INFO_REQ implements Serializable 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String KUNNR;

		public String getKUNNR() {
			return KUNNR;
		}

		public void setKUNNR(String kUNNR) {
			KUNNR = kUNNR;
		}
	}
	
	public class DT_CREDIT_INFO_RESP implements Serializable 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String SKFOR;

		public String getSKFOR() {
			return SKFOR;
		}

		public void setSKFOR(String sKFOR) {
			SKFOR = sKFOR;
		}
		
	}
	
}
