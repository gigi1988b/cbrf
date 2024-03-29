package ru.cbr.web;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * ��� ������ ��� ��������� ���������� ������ ver 28.05.2019
 *
 * This class was generated by Apache CXF 3.3.3
 * 2019-10-06T01:36:31.404+04:00
 * Generated source version: 3.3.3
 *
 */
@WebServiceClient(name = "DailyInfo",
                  wsdlLocation = "http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx?WSDL",
                  targetNamespace = "http://web.cbr.ru/")
public class DailyInfo extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://web.cbr.ru/", "DailyInfo");
    public final static QName DailyInfoSoap = new QName("http://web.cbr.ru/", "DailyInfoSoap");
    public final static QName DailyInfoSoap12 = new QName("http://web.cbr.ru/", "DailyInfoSoap12");
    static {
        URL url = null;
        try {
            url = new URL("http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx?WSDL");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(DailyInfo.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx?WSDL");
        }
        WSDL_LOCATION = url;
    }

    public DailyInfo(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public DailyInfo(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DailyInfo() {
        super(WSDL_LOCATION, SERVICE);
    }

    public DailyInfo(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public DailyInfo(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public DailyInfo(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns DailyInfoSoap
     */
    @WebEndpoint(name = "DailyInfoSoap")
    public DailyInfoSoap getDailyInfoSoap() {
        return super.getPort(DailyInfoSoap, DailyInfoSoap.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DailyInfoSoap
     */
    @WebEndpoint(name = "DailyInfoSoap")
    public DailyInfoSoap getDailyInfoSoap(WebServiceFeature... features) {
        return super.getPort(DailyInfoSoap, DailyInfoSoap.class, features);
    }


    /**
     *
     * @return
     *     returns DailyInfoSoap
     */
    @WebEndpoint(name = "DailyInfoSoap12")
    public DailyInfoSoap getDailyInfoSoap12() {
        return super.getPort(DailyInfoSoap12, DailyInfoSoap.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DailyInfoSoap
     */
    @WebEndpoint(name = "DailyInfoSoap12")
    public DailyInfoSoap getDailyInfoSoap12(WebServiceFeature... features) {
        return super.getPort(DailyInfoSoap12, DailyInfoSoap.class, features);
    }

}
