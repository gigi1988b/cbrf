package su.mbv.cbrf.controllers;


import com.hazelcast.core.HazelcastInstance;
import com.sun.org.apache.xerces.internal.*;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.cbr.web.DailyInfo;
import ru.cbr.web.DailyInfoSoap;
import ru.cbr.web.GetCursOnDateXMLResponse;
import su.mbv.cbrf.CursParser;
import su.mbv.cbrf.models.CursRequestModel;
import su.mbv.cbrf.models.CursResultModel;
import sun.rmi.rmic.Main;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.*;


@RestController
public class MainController {

    private final HazelcastInstance hazelcastInstance;
    @Autowired
    MainController(HazelcastInstance hazelcastInstance){
        this.hazelcastInstance = hazelcastInstance;
    }

    @GetMapping("/curs/{valuteCode}")
    public ResponseEntity<Object> curs(@PathVariable String valuteCode) {
        valuteCode = valuteCode.toUpperCase();
        CursResultModel result = null;
        Map<String,BigDecimal> hazelCastMap = hazelcastInstance.getMap("curs-map");
        Calendar onDate = Calendar.getInstance();
        if(!hazelCastMap.containsKey(valuteCode+CursParser.getDateString(onDate))) {
            try {
                result = CursParser.getValuteByValuteCode(valuteCode, onDate);

            } catch (Exception e) {
                return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (result == null)
                return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
            hazelCastMap.put(result.code+result.date,result.curs);
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        BigDecimal curs = hazelCastMap.get(valuteCode+CursParser.getDateString(onDate));
        return new ResponseEntity<Object>(new CursResultModel(valuteCode, curs, CursParser.getDateString(onDate)), HttpStatus.OK);

    }

    @GetMapping("/curs/{valuteCode}/date/{date}")
    public ResponseEntity<Object> curs(@PathVariable String valuteCode, @PathVariable("date")
    @DateTimeFormat(pattern = "yyyy-MM-dd") Calendar date) {
        valuteCode = valuteCode.toUpperCase();
        CursResultModel result = null;
        Map<String,BigDecimal> hazelCastMap = hazelcastInstance.getMap("curs-map");
        if(!hazelCastMap.containsKey(valuteCode+CursParser.getDateString(date))) {
            try {
                result = CursParser.getValuteByValuteCode(valuteCode, date);
            } catch (Exception e) {
                return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (result == null)
                return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
            hazelCastMap.put(result.code+result.date,result.curs);
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        BigDecimal curs = hazelCastMap.get(valuteCode+CursParser.getDateString(date));
        return new ResponseEntity<Object>(new CursResultModel(valuteCode, curs, CursParser.getDateString(date)), HttpStatus.OK);

    }

    @PostMapping("/curs")
    public ResponseEntity<Object> curs(@RequestBody CursRequestModel request){

        List<CursResultModel> result = null;
        try {
            result = CursParser.getValuteByValuteCodeAndDates(request, hazelcastInstance);
        } catch (Exception e) {
            return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (result == null)
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }


}
