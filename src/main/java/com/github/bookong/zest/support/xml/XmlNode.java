package com.github.bookong.zest.support.xml;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * @author Jiang Xu
 */
public class XmlNode {

    private String              nodeName;
    private String              nodeValue = StringUtils.EMPTY;
    private List<Node>          children;
    private Map<String, String> attributes;

    public XmlNode(Node node){
        this.nodeName = node.getNodeName();
        if (node.getFirstChild() != null) {
            this.nodeValue = StringUtils.trimToEmpty(node.getFirstChild().getNodeValue());
        }
        NodeList childNodes = node.getChildNodes();
        this.children = new ArrayList<>(childNodes.getLength() + 1);
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                children.add(item);
            }
        }

        NamedNodeMap namedNodeMap = node.getAttributes();
        if (namedNodeMap == null) {
            attributes = Collections.emptyMap();
        } else {
            attributes = new LinkedHashMap<>(namedNodeMap.getLength() + 1);
            for (int i = 0; i < namedNodeMap.getLength(); i++) {
                Node namedNode = namedNodeMap.item(i);
                if ("xmlns".equals(namedNode.getNodeName()) || StringUtils.indexOf(namedNode.getNodeName(), ':') > 0) {
                    continue;
                }

                attributes.put(namedNode.getNodeName(), namedNode.getNodeValue());
            }
        }
    }

    public static void duplicateCheck(String attrName, Set<String> values, String value) {
        if (values.contains(value)) {
            throw new ZestException(Messages.parseCommonAttrDuplicate(attrName, value));
        }
        values.add(value);
    }

    public String getNodeName() {
        return nodeName;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void mustNoChildren() {
        if (!children.isEmpty()) {
            throw new ZestException(Messages.parseCommonChildren(getNodeName()));
        }
    }

    public List<Node> getSpecifiedNodes(String errorMsg, String... nodeNames) {
        if (children.size() != nodeNames.length) {
            throw new ZestException(errorMsg);
        }

        for (int i = 0; i < children.size(); i++) {
            if (!nodeNames[i].equals(children.get(i).getNodeName())) {
                throw new ZestException(errorMsg);
            }
        }

        return children;
    }

    public List<Node> getFixedNodeList(String errorMsg, String nodeName) {
        for (Node child : children) {
            if (!nodeName.equals(child.getNodeName())) {
                throw new ZestException(errorMsg);
            }
        }
        return children;
    }

    public String getAttr(String name) {
        return attributes.get(name);
    }

    public String getAttr(String name, String defValue) {
        String value = attributes.get(name);
        return value != null ? value : defValue;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public String getAttrNotEmpty(String name) {
        String value = attributes.get(name);
        if (StringUtils.isBlank(value)) {
            throw new ZestException(Messages.parseCommonAttrEmpty(name));
        }
        return value;
    }

    public boolean getAttrBoolean(String name, boolean defValue) {
        try {
            String value = attributes.get(name);
            return value != null ? Boolean.valueOf(value) : defValue;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    public int getAttrInt(String name, int defValue) {
        try {
            String value = attributes.get(name);
            return value != null ? Integer.parseInt(value) : defValue;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    public Double getAttrDoubleObj(String name) {
        try {
            String str = getAttr(name);
            if (str == null) {
                return null;
            }
            return Double.parseDouble(str);
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    public int getAttrInt(String name) {
        try {
            return Integer.parseInt(getAttrNotEmpty(name));
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    public float getAttrFloat(String name, float defValue) {
        try {
            String value = attributes.get(name);
            return value != null ? Float.parseFloat(value) : defValue;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    public void checkSupportedAttrs(String... attrNames) {
        Set<String> set = new HashSet<>(Arrays.asList(attrNames));
        List<String> nonsupportAttrs = new ArrayList<>(attributes.size() + 1);
        for (String name : attributes.keySet()) {
            if (!set.contains(name)) {
                nonsupportAttrs.add(name);
            }
        }

        if (!nonsupportAttrs.isEmpty()) {
            throw new ZestException(Messages.parseCommonAttrUnknown(nodeName, StringUtils.join(nonsupportAttrs, ',')));
        }
    }
}
