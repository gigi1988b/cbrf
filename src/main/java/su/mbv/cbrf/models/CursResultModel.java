package su.mbv.cbrf.models;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class CursResultModel {

    public String code;
    public BigDecimal curs;
    public String date;

    public CursResultModel(String code, BigDecimal curs, String date){
        this.code = code;
        this.curs = curs;
        this.date = date;
    }

    public CursResultModel(){

    }

}


