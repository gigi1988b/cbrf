package su.mbv.cbrf;

import com.hazelcast.core.HazelcastInstance;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.cbr.web.DailyInfo;
import ru.cbr.web.DailyInfoSoap;
import ru.cbr.web.GetCursOnDateXMLResponse;
import ru.cbr.web.GetCursOnDateXMLResponse.GetCursOnDateXMLResult;
import su.mbv.cbrf.models.CursRequestModel;
import su.mbv.cbrf.models.CursResultModel;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CursParser {

    public static class Valute {
        public String name;
        public String chCode;
        public int code;
        public BigDecimal nom;
        public BigDecimal curs;

        public Valute() {

        }

        public Valute(String vname, String vchcode, int vcode, BigDecimal vnom, BigDecimal vcurs) {
            this.name = vname;
            this.chCode = vchcode;
            this.code = vcode;
            this.nom = vnom;
            this.curs = vcurs;
        }
    }

    private static GetCursOnDateXMLResult getCursOnDateXMLResult(XMLGregorianCalendar onDate) {
        DailyInfoSoap port = new DailyInfo().getDailyInfoSoap();
        GetCursOnDateXMLResponse.GetCursOnDateXMLResult result = port.getCursOnDateXML(onDate);
        return result;
    }

    private static XMLGregorianCalendar convertDateToXMLGregorianCalendar(Calendar onDate) throws DatatypeConfigurationException {
        GregorianCalendar gregorianCalendar = (GregorianCalendar) onDate;
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar result = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        return result;
    }

    /*
    public static Valute getValuteByValuteCh(String valuteCh, GetCursOnDateXMLResult result) throws Exception {

        Valute answer = new Valute();

        List<Object> list = result.getContent();
        org.w3c.dom.Element e = (org.w3c.dom.Element) list.get(0);
        NodeList chCodeList = e.getElementsByTagName("VchCode");
        int length = chCodeList.getLength();

        boolean isFound = false;
        for (int i = 0; i < length; i++) {
            if (isFound) break;

            Node valuteChNode = chCodeList.item(i);
            TextImpl textimpl = (TextImpl) valuteChNode.getFirstChild();
            String chVal = textimpl.getData();

            if (chVal.equalsIgnoreCase(valuteCh)) {
                isFound = true;
                Node parent = valuteChNode.getParentNode();
                NodeList nodeList = parent.getChildNodes();
                int paramLength = nodeList.getLength();

                for (int j = 0; j < paramLength; j++) {
                    Node currentNode = nodeList.item(j);

                    String name = currentNode.getNodeName();
                    Node currentValue = currentNode.getFirstChild();
                    String value = currentValue.getNodeValue();
                    if (name.equalsIgnoreCase("Vname")) {
                        answer.name = value;
                    }
                    if (name.equalsIgnoreCase("Vnom")) {
                        answer.nom = new BigDecimal(value);
                    }
                    if (name.equalsIgnoreCase("Vcurs")) {
                        answer.curs = new BigDecimal(value);
                    }
                    if (name.equalsIgnoreCase("Vcode")) {
                        answer.code = Integer.parseInt(value);
                    }
                    if (name.equalsIgnoreCase("VchCode")) {
                        answer.chCode = value;
                    }
                }
            }
        }

        return answer;

    }
    */


    private static CursResultModel parseResultCB(GetCursOnDateXMLResult result, String valuteCode, String date) {
        Valute answer = new Valute();
        CursResultModel cursResult = new CursResultModel();
        cursResult.date = date;
        List<Object> list = result.getContent();
        org.w3c.dom.Element e = (org.w3c.dom.Element) list.get(0);
        NodeList chCodeList = e.getElementsByTagName("VchCode");
        int length = chCodeList.getLength();

        boolean isFound = false;
        for (int i = 0; i < length; i++) {
            if (isFound) break;

            Node valuteChNode = chCodeList.item(i);
            TextImpl textimpl = (TextImpl) valuteChNode.getFirstChild();
            String chVal = textimpl.getData();

            if (chVal.equalsIgnoreCase(valuteCode)) {
                isFound = true;
                Node parent = valuteChNode.getParentNode();
                NodeList nodeList = parent.getChildNodes();
                int paramLength = nodeList.getLength();

                for (int j = 0; j < paramLength; j++) {
                    Node currentNode = nodeList.item(j);

                    String name = currentNode.getNodeName();
                    Node currentValue = currentNode.getFirstChild();
                    String value = currentValue.getNodeValue();
                    if (name.equalsIgnoreCase("Vcurs")) {
                        answer.curs = new BigDecimal(value);
                        cursResult.curs = new BigDecimal(value);
                    }
                    if (name.equalsIgnoreCase("VchCode")) {
                        answer.chCode = value;
                        cursResult.code = value;
                    }
                }
            }
        }

        return cursResult;
    }

    public static CursResultModel getValuteByValuteCode(String valuteCode, Calendar date) throws Exception {


        GetCursOnDateXMLResult result = getCursOnDateXMLResult(convertDateToXMLGregorianCalendar(date));
        CursResultModel answer = parseResultCB(result, valuteCode, getDateString(date));
        if (answer.code == null)
            return null;
        return answer;

    }

    public static List<CursResultModel> getValuteByValuteCodeAndDates(CursRequestModel request, HazelcastInstance hazelcastInstance) throws Exception {
        request.code = request.code.toUpperCase();
        Map<String, BigDecimal> hazelCastMap = hazelcastInstance.getMap("curs-map");
        List<CursResultModel> answer = new ArrayList<>();
        for (Calendar date : request.dates) {
            if (!hazelCastMap.containsKey(request.code + getDateString(date))) {
                GetCursOnDateXMLResult result = getCursOnDateXMLResult(convertDateToXMLGregorianCalendar(date));
                CursResultModel parseResult = parseResultCB(result, request.code, getDateString(date));
                if (parseResult != null) {
                    answer.add(parseResult);
                    hazelCastMap.put(request.code+getDateString(date),parseResult.curs);
                }
            }
            else {
                BigDecimal curs = hazelCastMap.get(request.code + getDateString(date));
                answer.add(new CursResultModel(request.code, curs, getDateString(date)));
            }
        }
        if (answer.size() == 0)
            return null;

        return answer;
    }


    public static String getDateString(Calendar cal) {

        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(date);
    }

}