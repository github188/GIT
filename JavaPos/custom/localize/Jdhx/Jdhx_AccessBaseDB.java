package custom.localize.Jdhx;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.TimeDate;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SyjGrangeDef;

import custom.localize.Bstd.Bstd_AccessBaseDB;

public class Jdhx_AccessBaseDB extends Bstd_AccessBaseDB
{
	public int getGoodsDef(GoodsDef goodsDef, int searchFlag, String barcode, String gz, String proTime, String yhsj, int flag, String djlb)
	{
		int result = -1;

		int moregz;
		int moreret;
		ResultSet rs = null;

		GoodsDef finalGoods = null;

		try
		{
			PublicMethod.timeStart(Language.apply("正在查询本地商品库,请等待......"));

			if (flag == 0)
			{
				if (GlobalInfo.psGoodsCode == null)
					return -1;
				GlobalInfo.baseDB.setSql(GlobalInfo.psGoodsCode);
			}
			else
			{
				if (GlobalInfo.psGoodsBarCode == null)
					return -1;
				GlobalInfo.baseDB.setSql(GlobalInfo.psGoodsBarCode);
			}
			GlobalInfo.baseDB.paramSetString(1, barcode);
			rs = GlobalInfo.baseDB.selectData();

			if (rs == null) { return -1; }

			moregz = moreret = 0;
			Vector grouprange = null;

			while (rs.next())
			{
				if (moregz > 0)
					break;

				GoodsDef tempGoods = new GoodsDef();

				if (!GlobalInfo.baseDB.getResultSetToObject(tempGoods, GoodsDef.refLocal))
				{
					continue;
				}

				// 找代码,如果是多原印码、多包装、子商品继续找下一个
				if ((flag == 0) && ((tempGoods.type == 'A') || (tempGoods.type == 'B') || (tempGoods.type == 'C')))
				{
					continue;
				}

				// 检查收银机是否串柜销售
				if ((searchFlag < 4) && (GlobalInfo.syjDef.ists != 'Y') && !AccessLocalDB.getDefault().checkSyjGrange(tempGoods.gz))
				{
					if (result == -1)
					{
						result = 2; // 如果一个也没找到才设置为收银机串柜
					}
				}
				else
				{
					int valid = 0;

					if (searchFlag == 1)
					{
						if (AccessLocalDB.getDefault().checkManaframe(tempGoods.gz, 'Y'))
						{
							valid = 1;
						}
					}
					else if (searchFlag == 3)
					{
						if (AccessLocalDB.getDefault().checkManaframe(tempGoods.gz, 'N'))
						{
							valid = 1;
						}
					}

					if ((moreret < 1) || (valid > 0))
					{
						moregz++; // 已经找到的柜组个数

						if (valid > 0)
						{
							if (moreret < 1)
							{
								moregz = 1;
							}

							moreret++; // 有效柜组的个数
						}

						finalGoods = tempGoods;
						result = 0;

						// 如果本地查询商品不检查多柜，则不用检查多个柜组
						if (GlobalInfo.sysPara.localNotCheckMultiGz.equals("Y"))
						{
							if (grouprange == null)
							{
								grouprange = new Vector();
								AccessLocalDB.getDefault().readSyjGrange(grouprange);
								String strgz = "";
								for (int i = 0; i < grouprange.size(); i++)
								{
									strgz = strgz + ((SyjGrangeDef) grouprange.get(i)).gz.toString() + ";";
								}
							}

							SyjGrangeDef sgd = null;
							for (int i = 0; i < grouprange.size(); i++)
							{
								sgd = (SyjGrangeDef) grouprange.get(i);

								if (sgd.gz.equals(finalGoods.gz))
								{
									break;
								}
							}

							if (sgd != null && sgd.gz.equals(finalGoods.gz))
							{
								break;
							}
						}
						else
						{
							if ((moregz > 1) || (moreret > 1)) // 存在多个柜组，要求输入商品柜组
							{
								result = 4;
								break;
							}
						}
					}
				}

				if (tempGoods.attr01 != null && tempGoods.attr01.trim().length() > 0)
				{
					String[] types = tempGoods.attr01.trim().split(",");
					for (int i = 0; i < types.length; i++)
					{
						if (types[i].equals(djlb))
						{
							result = 10;
							break;
						}
					}
				}
			}

			if (result == 0)
			{
				GlobalInfo.baseDB.resultSetClose();

				// 查找优惠
				GoodsPopDef popDef = getPromotion(finalGoods.code, finalGoods.gz, finalGoods.catid, finalGoods.ppcode, finalGoods.uid, proTime, yhsj);

				// 将优惠单信息赋值到商品
				transferPopInfoToGoodsInfo(finalGoods, popDef);

				if (!PublicMethod.transferInfo(finalGoods, goodsDef, "ref", "ref"))
				{
					result = -1;
				}

				// 脱网商品允许销红
				goodsDef.isxh = 'Y';
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();

			result = -1;
		}
		finally
		{

			GlobalInfo.baseDB.resultSetClose();
			PublicMethod.timeEnd(Language.apply("查询本地商品库耗时: "));
		}

		return result;
	}

	public GoodsPopDef getPromotion(String code, String gz, String catid, String ppcode, String uid, String scsj, String yhsj)
	{
		if (yhsj == null || yhsj.length() <= 0)
			return null;

		ResultSet rs = null;

		double sjjg = 0; // 时间间隔
		String today = new ManipulateDateTime().getDateBySign();

		TimeDate timeObj = new TimeDate();
		timeObj.fullTime = yhsj;
		timeObj.split();
		String sj = timeObj.hh + ":" + timeObj.min;
		GoodsPopDef goods1 = new GoodsPopDef();

		if (scsj.length() >= 8)
		{
			long timeDis = new ManipulateDateTime().getDisDateTimeByMS(scsj, yhsj);
			sjjg = timeDis / 1000; // 将时间之差转换成秒
		}

		try
		{
			// 交叉重叠的档期内先找单品促销最低价的
			StringBuffer sb = new StringBuffer();

			String condseq = "SELECT MAX(Ppiseq) FROM GOODSPOPINFO ";
			// 查找最大序号商品
			sb.append(condseq);
			sb.append("WHERE PPISTARTDATE <= '" + today + "' AND PPIENDDATE   >='" + today + "' And PPISTARTTIME <= '" + sj + "' AND PPIENDTIME   >= '" + sj + "' And ");
			sb.append("PPIMFID = '" + GlobalInfo.sysPara.mktcode + "' AND ");
			sb.append("Ppihyzkfd = '1' AND " + "Pphdjlb = '1' AND "); // 找单品促销
			sb.append("Exists (Select 'x' From GOODSPOPINFOTIME A Where  A.PPIBILLNO = PPIBILLNO And A.PPIKSRQ  <='" + today + "' And A.PPIJSRQ  >= '" + today + "' And ");
			sb.append("A.PPIKSSJ <='" + sj + "' And A.PPIJSSJ >='" + sj + "') And ");
			sb.append("PPHDJLB ='1' AND ");
			sb.append("((PPIBARCODE = '" + code + "' AND (PPIMFID ='" + gz + "' OR PPIMFID = '0') AND ");
			sb.append("PPISPEC = '00'  AND PPIMAXNUM <> 0 AND ");
			sb.append("(PPIMODE = '1' OR (PPIMODE = '7' AND " + sjjg + ">= PPISPACE))) OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIMODE = '2') OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '4') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIMODE = '3') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '5') OR ");
			sb.append("(PPIBARCODE = '" + ppcode + "'  AND PPIMODE = '6')) ");

			System.out.println("查询明细1： " + sb.toString());
			// 找到最大序号
			String seqno1 = (String) GlobalInfo.baseDB.selectOneData(sb.toString());

			if (seqno1 != null && Convert.toLong(seqno1) > 0)
			{
				sb.delete(0, sb.toString().length());
				sb.append("SELECT PPISEQ,PPIMODE,PPIBILLNO,PPIBARCODE,PPIMFID,PPICATID," + "PPIPPCODE,PPISPEC,PPINEWSJ,PPINEWHYJ,PPINEWRATE,PPINEWHYRATE,PPIZKFD," + "PPISTARTDATE,PPIENDDATE,PPISTARTTIME,PPIENDTIME,PPISPACE,PPINEWPFJ," + "PPINEWPFRATE,PPIHYZKFD,PPIPFZKFD,PPIMAXNUM FROM GOODSPOPINFO WHERE Ppiseq = " + Convert.toLong(seqno1));
				rs = GlobalInfo.baseDB.selectData(sb.toString());

				if (rs.next())
				{
					goods1.seqno = Convert.toLong(rs.getString(1));
					goods1.type = rs.getString(2).charAt(0);
					goods1.djbh = rs.getString(3);
					// goods1.code = rs.getString(4);
					// goods1.gz = rs.getString(5);
					// goods1.catid = rs.getString(6);
					// goods1.ppcode = rs.getString(7);
					// goods1.uid = rs.getString(8);
					goods1.poplsj = Convert.toDouble(rs.getString(9));
					goods1.pophyj = Convert.toDouble(rs.getString(10));
					goods1.poplsjzkl = Convert.toDouble(rs.getString(11));
					goods1.pophyjzkl = Convert.toDouble(rs.getString(12));
					goods1.poplsjzkfd = Convert.toDouble(rs.getString(13));
					goods1.ksrq = rs.getString(14);
					goods1.jsrq = rs.getString(15);
					goods1.kssj = rs.getString(16);
					goods1.jssj = rs.getString(17);
					goods1.yhspace = Convert.toInt(rs.getString(18));
					goods1.poppfj = Convert.toDouble(rs.getString(19));
					goods1.poppfjzkl = Convert.toDouble(rs.getString(20));
					goods1.pophyjzkfd = Convert.toDouble(rs.getString(21));
					goods1.poppfjzkfd = Convert.toDouble(rs.getString(22));
					goods1.num3 = Convert.toDouble(rs.getString(23));
				}
			}

			GlobalInfo.baseDB.resultSetClose();
			// 找会员促销
			sb.delete(0, sb.toString().length());

			condseq = "SELECT MAX(Ppiseq) FROM GOODSPOPINFO ";
			// 查找最大序号商品
			sb.append(condseq);
			sb.append("WHERE PPISTARTDATE <= '" + today + "' AND PPIENDDATE   >='" + today + "' And PPISTARTTIME <= '" + sj + "' AND PPIENDTIME   >= '" + sj + "' And ");
			sb.append("PPIMFID = '" + GlobalInfo.sysPara.mktcode + "' AND ");
			sb.append("Ppihyzkfd = '1' AND " + "Pphdjlb = 'H' AND "); // 找会员促销
			sb.append("Exists (Select 'x' From GOODSPOPINFOTIME A Where  A.PPIBILLNO = PPIBILLNO And A.PPIKSRQ  <='" + today + "' And A.PPIJSRQ  >= '" + today + "' And ");
			sb.append("A.PPIKSSJ <='" + sj + "' And A.PPIJSSJ >='" + sj + "') And ");
			sb.append("PPHDJLB ='1' AND ");
			sb.append("((PPIBARCODE = '" + code + "' AND (PPIMFID ='" + gz + "' OR PPIMFID = '0') AND ");
			sb.append("PPISPEC = '00'  AND PPIMAXNUM <> 0 AND ");
			sb.append("(PPIMODE = '1' OR (PPIMODE = '7' AND " + sjjg + ">= PPISPACE))) OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIMODE = '2') OR ");
			sb.append("(PPIBARCODE = '" + gz + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '4') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIMODE = '3') OR ");
			sb.append("(PPIBARCODE = '" + catid + "' AND PPIPPCODE = '" + ppcode + "' AND PPIMODE = '5') OR ");
			sb.append("(PPIBARCODE = '" + ppcode + "'  AND PPIMODE = '6')) ");

			System.out.println("查询明细2： " + sb.toString());

			// 找到最大序号
			String seqno2 = (String) GlobalInfo.baseDB.selectOneData(sb.toString());

			if (seqno2 == null || Convert.toLong(seqno2) <= 0)
				return goods1;

			if (seqno1 != null && Convert.toLong(seqno1) > 0)
			{
				sb.delete(0, sb.toString().length());
				sb.append("SELECT pibillno, Ppinewhyj, Ppinewhyrate,Ppizkfd WHERE Ppiseq = " + Convert.toLong(seqno2));

				rs = GlobalInfo.baseDB.selectData(sb.toString());

				if (rs.next())
				{
					goods1.str5 = rs.getString(1);
					goods1.num5 = Convert.toDouble(rs.getString(2));
					goods1.num6 = Convert.toDouble(rs.getString(3));
					goods1.num7 = Convert.toDouble(rs.getString(4));
				}
			}
			else
			{

				sb.delete(0, sb.toString().length());
				sb.append("SELECT Ppimode pibillno, Ppinewsj,Ppinewhyj, Ppinewhyrate,Ppizkfd WHERE Ppiseq = " + Convert.toLong(seqno2));

				// GlobalInfo.baseDB.setSql(sb.toString());

				rs = GlobalInfo.baseDB.selectData(sb.toString());

				if (rs.next())
				{
					goods1.type = rs.getString(1).charAt(0);
					goods1.str5 = rs.getString(2);
					goods1.poplsj = Convert.toDouble(rs.getString(3));
					goods1.num5 = Convert.toDouble(rs.getString(4));
					goods1.num6 = Convert.toDouble(rs.getString(4));
					goods1.num7 = Convert.toDouble(rs.getString(5));
				}

			}
			GlobalInfo.baseDB.resultSetClose();
			return goods1;
		}
		catch (Exception er)
		{
			er.printStackTrace();

			return null;
		}
		finally
		{
			GlobalInfo.baseDB.resultSetClose();
		}
	}

	public void transferPopInfoToGoodsInfo(GoodsDef finalGoods, GoodsPopDef popDef)
	{
		if (popDef != null)
		{
			finalGoods.isxh = 'Y';
			finalGoods.kcsl = 0;
			finalGoods.popdjbh = popDef.djbh;
			finalGoods.poptype = popDef.type;
			finalGoods.poplsj = popDef.poplsj;
			finalGoods.pophyj = popDef.pophyj;
			finalGoods.poppfj = popDef.poppfj;

			finalGoods.poplsjzkl = popDef.poplsjzkl;
			finalGoods.pophyjzkl = popDef.pophyjzkl;
			finalGoods.poppfjzkl = popDef.poppfjzkl;
			finalGoods.poplsjzkfd = popDef.poplsjzkfd;
			finalGoods.pophyjzkfd = popDef.pophyjzkfd;
			finalGoods.poppfjzkfd = popDef.poppfjzkfd;
			finalGoods.pophymaxsl = popDef.num3;

			finalGoods.hypopdjbh = popDef.str5;
			finalGoods.hypophyj = popDef.num5;
			finalGoods.hypophyrate = popDef.num6;
			finalGoods.hypopzkfd = popDef.num7;
		}
		else
		{
			finalGoods.isxh = 'Y';
			finalGoods.kcsl = 0;
			finalGoods.poptype = '0';
			finalGoods.popdjbh = "";
		}
	}
}
