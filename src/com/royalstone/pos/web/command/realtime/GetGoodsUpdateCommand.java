package com.royalstone.pos.web.command.realtime;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.complex.DiscComplexList;
import com.royalstone.pos.data.PosPriceData;
import com.royalstone.pos.favor.BulkPrice;
import com.royalstone.pos.favor.DiscCriteria;
import com.royalstone.pos.notify.GoodsUpdate;
import com.royalstone.pos.web.command.ICommand;
import com.royalstone.pos.web.util.DBConnection;

/**
 * @author liangxinbiao
 */

public class GetGoodsUpdateCommand implements ICommand {

	private String errorMsg1;
	private String errorMsg2;

	/**
	 * @see com.royalstone.pos.web.command.ICommand#excute(java.lang.Object[])
	 */
	public Object[] excute(Object[] values) {

		if (values != null
			&& values.length == 2) {

			Connection con = null;

			try {
				con = DBConnection.getConnection("java:comp/env/dbpos");

				Object[] result = new Object[3];
				result[0] = getGoodsUpdate(con, (ArrayList) values[1]);
				result[1] = errorMsg1;
				result[2] = errorMsg2;
				return result;

			} catch (SQLException e) {
				e.printStackTrace();
				errorMsg1 = "数据库连接错误!";
				errorMsg2 = e.getMessage();
				
				Object[] result = new Object[3];
				result[0] = null;
				result[1] = errorMsg1;
				result[2] = errorMsg2;
				
				return result;


			} finally {
				DBConnection.closeAll(null, null, con);
			}

		}
		return null;
	}

	public ArrayList getGoodsUpdate(
		Connection connection,
		ArrayList goodsNoList) {

		ArrayList result = new ArrayList();

		LookupGoodsCommand lookupGoods = new LookupGoodsCommand();
		LookupGoodsExtCommand lookupGoodsExt = new LookupGoodsExtCommand();
		GetBulkPriceCommand getBulkPrice = new GetBulkPriceCommand();
		GetComplexListCommand getComplexList = new GetComplexListCommand();
		GetDiscCriteriaCommand getDiscCriteria = new GetDiscCriteriaCommand();

		try {
			for (int i = 0; i < goodsNoList.size(); i++) {

				Goods goods = null;
				ArrayList goodsextList = null;
				BulkPrice bulkPrice = null;
				DiscComplexList discComplexList = null;
				DiscCriteria discCriteria = null;

				String goodsNo = (String) goodsNoList.get(i);
			    /*TODO 药品限购*/
			    PosPriceData codenew = new PosPriceData();
			    codenew.setSaleCode(goodsNo);
				goods = lookupGoods.lookup(connection, codenew);
				if (goods != null) {
					goodsextList =
						lookupGoodsExt.getGoodsExtListWithException(
							connection,
							goodsNo);

					if (goods.getPType().equals(DiscCriteria.BULKPRICE)) {
						bulkPrice = getBulkPrice.getBulkPrice(connection, goodsNo);
					} else if (goods.getPType().equals(DiscCriteria.DISCCOMPLEX)) {
						discComplexList =
							getComplexList.getComplexList(connection, goodsNo);
					} else if (!goods.getPType().equals(DiscCriteria.NORMAL)) {
						discCriteria =
							getDiscCriteria.getDiscCriteria(
								connection,
								goodsNo,
								goods.getPType(),
								"-1");
					}
				}

				GoodsUpdate goodsUpdate = new GoodsUpdate();
				goodsUpdate.setGoodsNo(goodsNo);
				goodsUpdate.setGoods(goods);
				goodsUpdate.setGoodsExtList(goodsextList);
				goodsUpdate.setBulkPrice(bulkPrice);
				goodsUpdate.setComplexList(discComplexList);
				goodsUpdate.setDiscCriteria(discCriteria);

				result.add(goodsUpdate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
