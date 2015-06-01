package bankpay.alipay.tools;

import java.util.HashMap;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String a = "11111\n22222？";
		HashMap map = new HashMap();
		map.put(a, "3333");
		System.out.println(map.keySet().toString().getBytes());

	}

}
