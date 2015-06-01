package custom.localize.Zjzd;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;

import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.GoodsAmountDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bcrm.Bcrm_SaleBS;


public class Zjzd_SaleBS extends Bcrm_SaleBS {
    public boolean writeHangGrant() {
        if ((this.curGrant.priv.length() > 6) &&
                (this.curGrant.priv.charAt(6) != 'Y') &&
                (this.curGrant.priv.charAt(6) != 'A')) {
            OperUserDef staff = DataService.getDefault().personGrant("收银挂单授权");

            if (staff == null) {
                return false;
            }

            String log = "授权写入挂单,授权工号:" + staff.gh;
            AccessDayDB.getDefault().writeWorkLog(log);

            if (staff != null) {
                if ((staff.priv.length() > 6) && (staff.priv.charAt(6) != 'Y') &&
                        (staff.priv.charAt(6) != 'A')) {
                    new MessageBox("当前工号没有挂单权限!");

                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public void getVIPZK(int index, int type) {
        if (type != this.vipzk2) {
            return;
        }

        char zszflag = 'Y';

        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
        SpareInfoDef spareInfo = (SpareInfoDef) this.goodsSpare.elementAt(index);
        
        if (goodsDef.poptype != '0') {
            if ((goodsDef.pophyjzkl % 10.0) > 0) {
                zszflag = 'Y';
            } else {
                zszflag = 'N';
            }
        }

        if ((checkMemberSale()) && (this.curCustomer != null) &&
                (goodsDef.isvipzk == 'Y')) {
            calcVIPZK(index);

            if ((getZZK(saleGoodsDef) >= 0.01D) && (goodsDef.hyj < 1.0D)) {
                if ((spareInfo.char1 != 'Y') ||
                        ((saleGoodsDef.yhzke > 0.0D) && (zszflag != 'Y'))) {
                    zszflag = 'Y';
                    spareInfo.char1 = 'N';
                }

                if (zszflag == 'Y') {
                    double zkl = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje -
                            getZZK(saleGoodsDef)) / saleGoodsDef.hjje, 2, 1);

                    if (spareInfo.char1 == 'Y') {
                        double[] nvalues = {
                                this.curCustomer.value1, this.curCustomer.value2,
                                this.curCustomer.value3, this.curCustomer.value4,
                                this.curCustomer.value5
                            };

                        if ((zkl >= nvalues[0]) && (zkl <= nvalues[1])) {
                            if ((this.curCustomer.func.length() > 3) &&
                                    (this.curCustomer.func.charAt(3) == 'Y')) {
                                saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1.0D -
                                        nvalues[2]) * (saleGoodsDef.hjje -
                                        getZZK(saleGoodsDef)), 2, 1);
                            }
                        } else if (zkl > nvalues[1]) {
                            if ((this.curCustomer.func.length() > 4) &&
                                    (this.curCustomer.func.charAt(4) == 'Y')) {
                                if (nvalues[3] == 0.0D) {
                                    nvalues[3] = goodsDef.hyj;
                                }

                                zkl = ManipulatePrecision.doubleConvert((1.0D -
                                        nvalues[3]) * saleGoodsDef.hjje, 2, 1);

                                if (zkl > getZZK(saleGoodsDef)) {
                                    saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zkl -
                                            getZZK(saleGoodsDef), 2, 1);
                                }
                            }
                        } else if ((zkl < nvalues[0]) &&
                                (this.curCustomer.func.length() > 5) &&
                                (this.curCustomer.func.charAt(5) == 'Y')) {
                            saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1.0D -
                                    nvalues[4]) * (saleGoodsDef.hjje -
                                    getZZK(saleGoodsDef)), 2, 1);
                        }

                        double zkl1 = ManipulatePrecision.doubleConvert((1.0D -
                                goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);

                        if (zkl1 > getZZK(saleGoodsDef)) {
                            saleGoodsDef.hyzke += ManipulatePrecision.doubleConvert(zkl1 -
                                getZZK(saleGoodsDef), 2, 1);
                        }
                    } else if (goodsDef.hyj < zkl) {
                        zkl = ManipulatePrecision.doubleConvert((1.0D -
                                goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);

                        if (zkl > getZZK(saleGoodsDef)) {
                            saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zkl -
                                    getZZK(saleGoodsDef), 2, 1);
                        }
                    }
                }
            } else {
                saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1.0D -
                        goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
            }

            saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
        }

        spareInfo.char1 = 'N';
        getZZK(saleGoodsDef);
    }

    public void calcAllRebate(int index) {
        //        char zszflag = 'Y';
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);

        //        SpareInfoDef spareInfo = (SpareInfoDef) this.goodsSpare.elementAt(index);
        if (isSpecifyBack(saleGoodsDef)) {
            return;
        }

        if (SellType.ISBATCH(this.saletype)) {
            return;
        }

        if (SellType.ISEARNEST(this.saletype)) {
            return;
        }

        if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) {
            return;
        }

        saleGoodsDef.hyzke = 0.0D;
        saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
        saleGoodsDef.yhzke = 0.0D;
        saleGoodsDef.yhzkfd = 0.0D;
        saleGoodsDef.plzke = 0.0D;
        saleGoodsDef.zszke = 0.0D;

        if (goodsDef.poptype == '0') {
            return;
        }

        if ((saleGoodsDef.lsj > 0.0D) &&
                ((goodsDef.poptype == '1') || (goodsDef.poptype == '7'))) {
            if ((saleGoodsDef.lsj > goodsDef.poplsj) &&
                    (goodsDef.poplsj > 0.0D)) {
                saleGoodsDef.yhzke = ((saleGoodsDef.lsj - goodsDef.poplsj) * saleGoodsDef.sl);
                saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
            }
        } else if ((1.0D > goodsDef.poplsjzkl) && (goodsDef.poplsjzkl > 0.0D)) {
            saleGoodsDef.yhzke = (saleGoodsDef.hjje * (1.0D -
                goodsDef.poplsjzkl));
            saleGoodsDef.yhzkfd = goodsDef.poplsjzkfd;
        }

        saleGoodsDef.yhzke = ManipulatePrecision.doubleConvert(saleGoodsDef.yhzke,
                2, 1);

        saleGoodsDef.yhzke = getConvertRebate(index, saleGoodsDef.yhzke);
        getZZK(saleGoodsDef);
    }

    public void calcVIPZK(int index) {
        SaleGoodsDef saleGoodsDef = (SaleGoodsDef) this.saleGoods.elementAt(index);
        GoodsDef goodsDef = (GoodsDef) this.goodsAssistant.elementAt(index);
        SpareInfoDef spareInfo = (SpareInfoDef) this.goodsSpare.elementAt(index);

        if ((!(checkMemberSale())) || (this.curCustomer == null)) {
            return;
        }

        if ((!this.saletype.equals(SellType.RETAIL_SALE)) &&
                (!this.saletype.equals(SellType.PREPARE_SALE))) {
            goodsDef.hyj = 1.0D;
            spareInfo.char1 = 'N';

            return;
        }

        GoodsAmountDef VIPZK = new GoodsAmountDef();

        if (DataService.getDefault()
                           .findAmountDef(VIPZK, saleGoodsDef.code,
                    saleGoodsDef.gz, this.curCustomer.type, 0.0D)) {
            goodsDef.hyj = VIPZK.plhyj;
            spareInfo.char1 = VIPZK.memo.charAt(0);
        } else {
            goodsDef.hyj = this.curCustomer.zkl;
            spareInfo.char1 = 'Y';
        }

    }
}
