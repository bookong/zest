package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Xu
 */
public class ZestXmlUtil {

    public static List<Node> getElements(NodeList nodeList) {
        List<Node> list = new ArrayList<>(nodeList.getLength() + 1);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                list.add(node);
            }
        }
        return list;
    }

    public static String getValue(Node node) {
        if (node == null || node.getFirstChild() == null) {
            return StringUtils.EMPTY;
        }

        return StringUtils.trimToEmpty(node.getFirstChild().getNodeValue());
    }

    public static int removeIntAttr(String nodeName, Map<String, String> attrMap, String attrName, int defValue) {
        Integer value = removeIntAttr(nodeName, attrMap, attrName);
        return value != null ? value : defValue;
    }

    public static Integer removeIntAttr(String nodeName, Map<String, String> attrMap, String attrName) {
        try {
            String value = removeAttr(nodeName, attrMap, attrName);
            return value != null ? Integer.valueOf(value) : null;
        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, attrName), e);
        }
    }

    public static float removeFloatAttr(String nodeName, Map<String, String> attrMap, String attrName, float defValue) {
        Float value = removeFloatAttr(nodeName, attrMap, attrName);
        return value != null ? value : defValue;
    }

    public static Float removeFloatAttr(String nodeName, Map<String, String> attrMap, String attrName) {
        try {
            String value = removeAttr(nodeName, attrMap, attrName);
            return value != null ? Float.valueOf(value) : null;
        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, attrName), e);
        }
    }

    public static boolean removeBooleanAttr(String nodeName, Map<String, String> attrMap, String attrName,
                                            boolean defValue) {
        Boolean value = removeBooleanAttr(nodeName, attrMap, attrName);
        return value != null ? value : defValue;
    }

    public static Boolean removeBooleanAttr(String nodeName, Map<String, String> attrMap, String attrName) {
        try {
            String value = removeAttr(nodeName, attrMap, attrName);
            return value != null ? Boolean.valueOf(value) : null;
        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, attrName), e);
        }
    }

    public static String removeAttr(String nodeName, Map<String, String> attrMap, String attrName) {
        try {
            String value = attrMap.remove(attrName);
            return value != null ? StringUtils.trimToEmpty(value) : null;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, attrName));
        }
    }

    public static Map<String, String> getAllAttrs(Node node) {
        NamedNodeMap namedNodeMap = node.getAttributes();
        Map<String, String> map = new HashMap<>(namedNodeMap.getLength() + 1);
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Node attr = namedNodeMap.item(i);
            if ("xmlns".equals(attr.getNodeName()) || StringUtils.indexOf(attr.getNodeName(), ':') > 0) {
                continue;
            }
            map.put(attr.getNodeName(), attr.getNodeValue());
        }
        return map;
    }

    public static void attrMapMustEmpty(String nodeName, Map<String, String> attrMap) {
        if (!attrMap.isEmpty()) {
            throw new ZestException(Messages.parseCommonAttrUnknown(nodeName, StringUtils.join(attrMap.keySet(), ',')));
        }
    }
}
