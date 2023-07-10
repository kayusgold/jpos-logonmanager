package com.kayusgold.logonmanager.controller;

import com.kayusgold.logonmanager.MyISORequestListener;
import com.kayusgold.logonmanager.TripleDES;
import org.jpos.iso.*;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.PostPackager;
import org.jpos.q2.iso.QMUX;
import org.jpos.util.LogSource;
import org.jpos.util.NameRegistrar;
import org.jpos.util.SimpleLogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.jpos.iso.channel.PostChannel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@RestController
public class TransactionController {
    //transaction endpoint
    private Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @GetMapping("neapay")
    public ResponseEntity<Object> neapayRequest() throws ISOException, IOException, NameRegistrar.NotFoundException {
        logger.info("Preparing transaction...");
        MUX mux = QMUX.getMUX("clientsimulator-mux");

        ISOMsg neapayIsoMsg = neapayIsoMsg();

        ISOMsg response = mux.request(neapayIsoMsg, 10000);

//        response.unpack(response.pack());

        String message = "";
        //check if 39 is 00
        if(response.getString(39).equals("00")) {
            message = "Transaction successful";
            logger.info("Neapay Transaction successful");
        } else {
            message = "Transaction failed";
            logger.info("Neapay Transaction failed");
        }

        logger.info("Neapay Response: " + response.toString());
        logger.info("Neapay Response 2: {}", response);

        // SENT ==> 11: 000100, 41: 20390059

        Map<String, Object> data = new HashMap<String, Object>();

        data.put("2", neapayIsoMsg.getString(2));
        data.put("11", response.getString(11));
        data.put("41", response.getString(41));
        data.put("39", response.getString(39));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("status", response.getString(39));
        map.put("data", data);

        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    @GetMapping("purchase")
    public void echo() throws NameRegistrar.NotFoundException, ISOException, IOException {
        logger.info("Preparing transaction...");
        //get mux
        MUX mux = QMUX.getMUX("clientsimulator-mux");

        InputStream is = com.kayusgold.logonmanager.controller.TransactionController.class
                .getResourceAsStream("/neapay.xml");
        GenericPackager packager = new GenericPackager(is);

//        ISOBasePackager packager = new PostPackager();

        BaseChannel channel = new PostChannel("127.0.0.1", 9009, packager);;

        ISOMsg msg = new ISOMsg();
        msg.setMTI("0200");
        msg.set(1, "2000022");
        msg.set(2, "539923******4844");
        msg.set(3, "010000");
        msg.set(4, "50000");
        msg.set(7, "0104160000");
        msg.set(11, "000001");
        msg.set(12, "000000");
        msg.set(13, "0104");
        msg.set(15, "0104");
        msg.set(18, "6012");
        msg.set(22, "51");
        msg.set(23, "00");
        msg.set(25, "00");
        msg.set(26, "12");
        msg.set(28, "000000000");
        msg.set(32, "539923");
        msg.set(35, "539923******4844=22021010000000000000");
        msg.set(37, "4054000163");
        msg.set(40, "221");
        msg.set(41, "2033AXNZ");
        msg.set(42, "2AGTLAGPOOO7964");
        msg.set(43, "TIJANI YUSUF                          NG");
        msg.set(49, "566");
        msg.set(52, TripleDES.h2b("E0E0E0E0F1F1F1F1"));
        msg.set(103, "87001505");
        msg.set(123, "151010151134411");
        msg.set("127.002", "3944065709");
        msg.set("127.003", "UBATAMAGYsrcUPAgencySnk 703834703834VISMASTOTGRP");
        msg.set("127.012", "111128");
        msg.set("127.013", "1111111111    566");
        msg.set("127.020", "20220829");
        msg.set("127.022", "17REFCODE2277070_PAX~3K440945~7.8.23UBA");
        msg.set("127.025", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<IccData>" +
                "<IccRequest>" +
                "<AmountAuthorized>000000005000</AmountAuthorized>" +
                "<AmountOther>000000000000</AmountOther>" +
                "<ApplicationInterchangeProfile>3900</ApplicationInterchangeProfile>" +
                "<ApplicationTransactionCounter>15F5</ApplicationTransactionCounter>" +
                "<Cryptogram>545F4F2B56B8E464</Cryptogram>" +
                "<CryptogramInformationData>80</CryptogramInformationData>" +
                "<CvmResults>420300</CvmResults>" +
                "<IssuerApplicationData>0110A0C0002A0000C8F000000000000000FF</IssuerApplicationData>" +
                "<TerminalCapabilities>E0F8C8</TerminalCapabilities>" +
                "<TerminalCountryCode>566</TerminalCountryCode>" +
                "<TerminalType>22</TerminalType>" +
                "<TerminalVerificationResults>0000000000</TerminalVerificationResults>" +
                "<TransactionCurrencyCode>566</TransactionCurrencyCode>" +
                "<TransactionDate>210210</TransactionDate>" +
                "<TransactionStatusInformation>80</TransactionStatusInformation>" +
                "<TransactionType>00</TransactionType>" +
                "</IccRequest>" +
                "</IccData>");

//        ISOMsg response = mux.request(msg, 10000);

//        ISOMsg isoMsg = new ISOMsg();
//        isoMsg.setMTI("0200");
//
//        //set fields 2,3,4,7,11,12,13,14,15,18,22,23,25,26,28,32,33,35,37,40,41,42,43,49,52,98,100,103,123,127.002,127.013,127.020,127.022,127.025,127.033,127.041
//        isoMsg.set(2, "555940******7877");
//        isoMsg.set(3, "501000");
//        isoMsg.set(4, "000000100000");
//        isoMsg.set(7, "1207193655");
//        isoMsg.set(11, "415495");
//        isoMsg.set(12, "203655");
//        isoMsg.set(13, "1207");
//        isoMsg.set(14, "2007");
//        isoMsg.set(15, "1207");
//        isoMsg.set(18, "6013");
//        isoMsg.set(22, "051");
//        isoMsg.set(23, "000");
//        isoMsg.set(25, "00");
//        isoMsg.set(26, "04");
//        isoMsg.set(28, "D00010000");
//        isoMsg.set(32, "12345678");
//        isoMsg.set(33, "111111");
//        isoMsg.set(35, "555940******7877=2007************");
//        isoMsg.set(37, "734119415495");
//        isoMsg.set(40, "221");
//        isoMsg.set(41, "2AFTXXXX");
//        isoMsg.set(42, "2AFTXXXXXXXXXXX");
//        isoMsg.set(43, "S&O TELECOM");
//        isoMsg.set(49, "566");
//        isoMsg.set(52, TripleDES.h2b("E0E0E0E0F1F1F1F1"));
//        isoMsg.set(98, "3FAB0001");
//        isoMsg.set(100, "666214");
//        isoMsg.set(103, "**********");
//        isoMsg.set(123, "861201515001002");
//        isoMsg.set("127.002", "0200:415495:1207193655:787755594");
//        isoMsg.set("127.013", "000000  566");
//        isoMsg.set("127.020", "20171207");
//        isoMsg.set("127.022", "<ORIGINAL_RID>639609</ORIGINAL_RID>");
//        isoMsg.set("127.025", "<IccData><IccRequest><AmountAuthorized>000000100000</AmountAuthorized><AmountOther>000000000000</AmountOther><ApplicationInterchangeProfile>3800</ApplicationInterchangeProfile><ApplicationTransactionCounter>0019</ApplicationTransactionCounter><Cryptogram>756265BE55892F68</Cryptogram><CryptogramInformationData>80</CryptogramInformationData><CvmResults>420300</CvmResults><IssuerApplicationData>0110A08003220000947500000000000000FF</IssuerApplicationData><TerminalCapabilities>E040C8</TerminalCapabilities><TerminalCountryCode>566</TerminalCountryCode><TerminalVerificationResult>0000040000</TerminalVerificationResult><TransactionCurrencyCode>566</TransactionCurrencyCode><TransactionDate>171207</TransactionDate><TransactionType>50</TransactionType><UnpredictableNumber>67C408CA</UnpredictableNumber></IccRequest></IccData>");
//        isoMsg.set("127.033", "0000");
//        isoMsg.set("127.041", "10.2.103.19,36065");

        org.jpos.util.Logger loggerx = new org.jpos.util.Logger();
        loggerx.addListener(new SimpleLogListener(System.out));

        ((LogSource) channel).setLogger(loggerx, "test-channel");
        channel.connect();

        packager.setLogger(loggerx, "test-channel");

        msg.setPackager(packager);

//        ISOMsg neapayIsoMsg = neapayIsoMsg();
//
//        neapayIsoMsg.setPackager(packager);

        System.out.println("I am here 5");

        channel.send(msg);
        ISOMsg response = channel.receive();
        channel.disconnect();

        logger.info("Echo Response: {}", response);
    }

    public ISOMsg neapayIsoMsg() throws ISOException {
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI("0200");
        isoMsg.set(2, "9876500000306082");
        isoMsg.set(3, "010000");
        isoMsg.set(4, "000000005000");
        isoMsg.set(7, "0707184402");
        isoMsg.set(11, "005000");
        isoMsg.set(12, "230707");
        isoMsg.set(13, "0707");
        isoMsg.set(14, "3012");
        isoMsg.set(18, "5961");
        isoMsg.set(22, "020");
        isoMsg.set(23, "000");
        isoMsg.set(25, "00");
        isoMsg.set(26, "53");
        isoMsg.set(28, "000000000");
        isoMsg.set(32, "27610000001");
        isoMsg.set(35, "9876500000306082=30121011123123000");
        isoMsg.set(37, "440212");
        isoMsg.set(41, "11276100");
        isoMsg.set(42, "111120000012");
        isoMsg.set(43, "contact@neaPay.com\\Almere-Amsterdam\\Neth");
        isoMsg.set(49, "566");
        isoMsg.set(51, "566");
        isoMsg.set(52, TripleDES.h2b("FEE8CA6A604F09F0"));

        return isoMsg;
    }
}
