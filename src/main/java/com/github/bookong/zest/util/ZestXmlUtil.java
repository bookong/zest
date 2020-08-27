package com.github.bookong.zest.util;

import com.github.bookong.zest.exception.ZestException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * @author Jiang Xu
 */
public class ZestXmlUtil {

    public static List<Node> getChildren(Node node) {
        NodeList childNodes = node.getChildNodes();
        List<Node> list = new ArrayList<>(childNodes.getLength() + 1);
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                list.add(item);
            }
        }
        return list;
    }

    // ---------------------------------------------------

    public static String getValue(Node node) {
        if (node == null || node.getFirstChild() == null) {
            return StringUtils.EMPTY;
        }

        return StringUtils.trimToEmpty(node.getFirstChild().getNodeValue());
    }

    public static int removeNotNullIntAttr(String nodeName, Map<String, String> attrMap, String attrName) {
        Integer value = removeIntAttr(nodeName, attrMap, attrName);
        if (value == null) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, attrName));
        }
        return value;
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

    public static String removeNotEmptyAttr(String nodeName, Map<String, String> attrMap, String attrName) {
        String value = removeAttr(nodeName, attrMap, attrName);
        if (StringUtils.isBlank(value)) {
            throw new ZestException(Messages.parseCommonAttrEmpty(attrName));
        }
        return value;
    }

    public static String removeAttr(String nodeName, Map<String, String> attrMap, String attrName, String defValue) {
        String value = removeAttr(nodeName, attrMap, attrName);
        return value != null ? value : defValue;
    }

    public static String removeAttr(String nodeName, Map<String, String> attrMap, String attrName) {
        try {
            String value = attrMap.remove(attrName);
            return value != null ? value.trim() : null;
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

    public static void mustHaveNoChildrenElements(String nodeName, List<Node> children) {
        if (!children.isEmpty()) {
            throw new ZestException(Messages.parseCommonChildren(nodeName));
        }
    }

    public static void duplicateCheck(String attrName, Set<String> values, String value) {
        if (values.contains(value)) {
            throw new ZestException(Messages.parseCommonAttrDuplicate(attrName, value));
        }
        values.add(value);
    }
}
