package com.drore.cloud.tdp.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.json.XML;
import net.sf.json.xml.XMLSerializer;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zsj on 2016/12/24.
 */
public class XmlUtil {

    /**
     * 通过文件路径读取xml文件
     * 返回xml格式的字符串
     *
     * @param filepath
     * @return
     */
    public static String xmlToString(String filepath) {
        String documentStr = null;
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(new File(filepath));
            documentStr = document.asXML();
            System.out.println("xml 字符串：");
            System.out.println(documentStr);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return documentStr;
    }


    /**
     * 传入Document对象
     * 获得xml格式的字符串
     *
     * @param document
     * @return
     */
    public static String getXmlString(Document document) {
        String xmlString = "";
        OutputFormat outputFormat = OutputFormat.createPrettyPrint(); //xml输出格式设置
        outputFormat.setEncoding("UTF-8");
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
        try {
            xmlWriter.write(document);
            stringWriter.close();
            xmlWriter.close();
            xmlString = stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(xmlString);
        return xmlString;
    }


    /**
     * 海康lcd屏幕 叫号接口 xml数据封装
     *
     * @param obj {
     *            "materialNo":"50",
     *            "terminalNoList":["1","2","3"],
     *            "columnList":[
     *            [{"id":"11","value":"1a"}, {"id":"12","value":"1b"} ],
     *            [{"id":"21","value":"2a"}, {"id":"22","value":"2b"} ],
     *            [{"id":"31","value":"3a"}, {"id":"32","value":"3b"} ]
     *            ]
     *            }
     * @return
     */
    public static String getHkLcdXml(JSONObject obj) {
        Document document = DocumentHelper.createDocument();
        Element transData = document.addElement("TransData");
        transData.addAttribute("version", "2.0");
        Element materialNo = transData.addElement("materialNo");
        materialNo.setText(obj.getString("materialNo"));
        Element destType = transData.addElement("destType");
        destType.setText("byTerminal");
        Element terminalNoList = transData.addElement("TerminalNoList");
        JSONArray terminalNoArray = obj.getJSONArray("terminalNoList");
        for (int i = 0; i < terminalNoArray.size(); i++) {
            Element terminalNo = terminalNoList.addElement("terminalNo");
            terminalNo.setText(terminalNoArray.getString(i));
        }
        Element dataType = transData.addElement("dataType");
        dataType.setText("data");
        Element sendData = transData.addElement("SendData");
        Element refreshType = sendData.addElement("refreshType");
        refreshType.setText("all");
        Element itemDataList = sendData.addElement("ItemDataList");
        JSONArray columnList = obj.getJSONArray("columnList");
        for (int j = 0; j < columnList.size(); j++) {
            Element dataList = itemDataList.addElement("DataList");
            JSONArray rowList = columnList.getJSONArray(j);
            for (int k = 0; k < rowList.size(); k++) {
                JSONObject datainfo = rowList.getJSONObject(k);
                Element data = dataList.addElement("Data");
                Element id = data.addElement("id");
                Element value = data.addElement("value");
                id.setText(datainfo.getString("id"));
                value.setText(datainfo.getString("value"));
            }
        }
        return XmlUtil.getXmlString(document);
    }

    /**
     * 将xml字符串转换成json对象
     *
     * @param xmlString
     * @return
     */
    public static JSONObject xml2json(String xmlString) {
        JSONObject object = null;
        try {
            object = JSONObject.parseObject(String.valueOf(XML.toJSONObject(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static String json2xml(String objectString) {
        XMLSerializer serializer = new XMLSerializer();
        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(objectString);
        String xml = serializer.write(jsonObject);
        return xml;
    }

    public static JSONObject xmlToJson(String xmlString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSON json = xmlSerializer.read(xmlString);
        return JSONObject.parseObject(json.toString());
    }

}
