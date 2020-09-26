/**
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bookong.zest.support.xml;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.util.Messages;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Help parse XML content.
 *
 * @author Jiang Xu
 */
public class XmlNode {

    private String              nodeName;
    private String              nodeValue = StringUtils.EMPTY;
    private List<Node>          children;
    private Map<String, String> attributes;

    /**
     * Construct a new instance.
     *
     * @param node
     *          XML element.
     */
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

    /**
     * Check for duplicate content.
     *
     * @param attrName
     *          Attribute name.
     * @param values
     *          A set of discovered values.
     * @param value
     *          A new value that needs to be checked, if there is no duplicate, add it to {@code values}.
     */
    public static void duplicateCheck(String attrName, Set<String> values, String value) {
        if (values.contains(value)) {
            throw new ZestException(Messages.parseCommonAttrDuplicate(attrName, value));
        }
        values.add(value);
    }

    /**
     * @return XML node name.
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @return list of child elements.
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Make sure there are no child elements.
     */
    public void mustNoChildren() {
        if (!children.isEmpty()) {
            throw new ZestException(Messages.parseCommonChildren(getNodeName()));
        }
    }

    /**
     * Make sure there are specified child elements.
     *
     * @param errorMsg
     *          Error message when something goes wrong.
     * @param nodeNames
     *          A list of clearly specified child element names.
     * @return list of child elements.
     */
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

    /**
     * Make sure there are child elements with specified names.
     *
     * @param errorMsg
     *          Error message when something goes wrong.
     * @param nodeName
     *          Clearly specified child element name.
     * @return list of child elements.
     */
    public List<Node> getFixedNodeList(String errorMsg, String nodeName) {
        for (Node child : children) {
            if (!nodeName.equals(child.getNodeName())) {
                throw new ZestException(errorMsg);
            }
        }
        return children;
    }

    /**
     * Get attribute value.
     *
     * @param name
     *          Attribute name.
     * @return attribute value.
     */
    public String getAttr(String name) {
        return attributes.get(name);
    }

    /**
     * Get the attribute value, if not return the default value.
     *
     * @param name
     *          Attribute name.
     * @param defValue
     *          Default value.
     * @return attribute value.
     */
    public String getAttr(String name, String defValue) {
        String value = attributes.get(name);
        return value != null ? value : defValue;
    }

    /**
     * @return the content under the XML element.
     */
    public String getNodeValue() {
        return nodeValue;
    }

    /**
     * Get attribute value that cannot be empty.
     *
     * @param name
     *          Attribute name.
     * @return attribute value.
     */
    public String getAttrNotEmpty(String name) {
        String value = attributes.get(name);
        if (StringUtils.isBlank(value)) {
            throw new ZestException(Messages.parseCommonAttrEmpty(name));
        }
        return value;
    }

    /**
     * Get boolean attribute value.
     *
     * @param name
     *          Attribute name.
     * @param defValue
     *          Default value.
     * @return attribute value.
     */
    public boolean getAttrBoolean(String name, boolean defValue) {
        try {
            String value = attributes.get(name);
            return value != null ? Boolean.parseBoolean(value) : defValue;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    /**
     * Get integer attribute value.
     *
     * @param name
     *          Attribute name.
     * @param defValue
     *          Default value.
     * @return attribute value.
     */
    public int getAttrInt(String name, int defValue) {
        try {
            String value = attributes.get(name);
            return value != null ? Integer.parseInt(value) : defValue;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    /**
     * Get double attribute value.
     *
     * @param name
     *          Attribute name.
     * @return attribute value.
     */
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

    /**
     * Get integer attribute value.
     *
     * @param name
     *          Attribute name.
     * @return attribute value.
     */
    public int getAttrInt(String name) {
        try {
            return Integer.parseInt(getAttrNotEmpty(name));
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    /**
     * Get float attribute value.
     *
     * @param name
     *          Attribute name.
     * @param defValue
     *          Default value.
     * @return attribute value.
     */
    public float getAttrFloat(String name, float defValue) {
        try {
            String value = attributes.get(name);
            return value != null ? Float.parseFloat(value) : defValue;
        } catch (Exception e) {
            throw new ZestException(Messages.parseCommonAttr(nodeName, name), e);
        }
    }

    /**
     * Make sure that the element only supports the attributes listed.
     *
     * @param attrNames
     *          Attribute names.
     */
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
