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
package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.XmlNode;
import com.github.bookong.zest.util.*;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Containing unit test case data.
 *
 * @author Jiang Xu
 */
public class ZestData {

    private float        version;
    private String       fileName;
    private String       filePath;
    private boolean      transferTime = false;
    private long         currentTimeDiff;
    private String       description;
    private ZestParam    param;
    private List<Source> sourceList   = new ArrayList<>();
    private long         startTime;
    private long         endTime;

    /**
     * Construct a new instance.
     *
     * @param filePath
     *          Test case file path.
     */
    public ZestData(String filePath){
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * Load data from test case file.
     *
     * @param worker
     *          An object that controls the entire <em>Zest</em> logic.
     */
    public void load(ZestWorker worker) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(getFilePath()));
            XmlNode xmlNode = new XmlNode(doc);
            List<Node> rootElements = xmlNode.getChildren();

            if (rootElements.size() != 1 || !Xml.ZEST.equals(rootElements.get(0).getNodeName())) {
                throw new ZestException(Messages.parseZest());
            }

            loadZest(worker, rootElements.get(0));
        } catch (Exception e) {
            throw new ZestException(Messages.parse(getFilePath()), e);
        }
    }

    private void loadZest(ZestWorker worker, Node node) {
        XmlNode xmlNode = new XmlNode(node);

        this.version = xmlNode.getAttrFloat(Xml.VERSION, 1.0F);
        String currentTime = xmlNode.getAttr(Xml.CURRENT_TIME);
        this.transferTime = StringUtils.isNotBlank(currentTime);
        if (isTransferTime()) {
            Date currDbTime = ZestDateUtil.parseDate(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            this.currentTimeDiff = cal.getTimeInMillis() - currDbTime.getTime();
        }
        xmlNode.checkSupportedAttrs(Xml.VERSION, Xml.CURRENT_TIME);

        List<Node> children = xmlNode.getSpecifiedNodes(Messages.parseZestNecessary(), //
                                                        Xml.DESCRIPTION, Xml.SOURCES, Xml.PARAM);

        this.description = new XmlNode(children.get(0)).getNodeValue();
        loadSources(worker, children.get(1));
        loadParam(children.get(2));
    }

    private void loadSources(ZestWorker worker, Node node) {
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs();

            List<Node> children = xmlNode.getFixedNodeList(Messages.parseCommonChildrenList(Xml.SOURCES, Xml.SOURCE),
                                                           Xml.SOURCE);
            Set<String> sourceIds = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                getSourceList().add(new Source(worker, this, child, sourceIds));
            }

        } catch (Exception e) {
            throw new ZestException(Messages.parseSourcesError(), e);
        }
    }

    private void loadParam(Node node) {
        try {
            XmlNode xmlNode = new XmlNode(node);
            xmlNode.checkSupportedAttrs();

            List<Node> children = xmlNode.getFixedNodeList(Messages.parseCommonChildrenList(Xml.PARAM, Xml.PARAM_FIELD),
                                                           Xml.PARAM_FIELD);
            Set<String> fieldNames = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                loadParamField(child, fieldNames);
            }

        } catch (Exception e) {
            throw new ZestException(Messages.parseParamError(), e);
        }
    }

    private void loadParamField(Node node, Set<String> fieldNames) {
        XmlNode xmlNode = new XmlNode(node);
        xmlNode.checkSupportedAttrs(Xml.NAME);

        String fieldName = xmlNode.getAttrNotEmpty(Xml.NAME);
        XmlNode.duplicateCheck(Xml.NAME, fieldNames, fieldName);

        Field field = ZestReflectHelper.getField(param, fieldName);
        if (field == null) {
            throw new ZestException(Messages.parseParamNone(fieldName));
        }

        try {
            String value = xmlNode.getNodeValue();
            Class<?> fieldClass = field.getType();

            if (Integer.class.isAssignableFrom(fieldClass) || "int".equals(fieldClass.getName())) {
                ZestReflectHelper.setValue(param, fieldName, Integer.valueOf(value.trim()));

            } else if (Long.class.isAssignableFrom(fieldClass) || "long".equals(fieldClass.getName())) {
                ZestReflectHelper.setValue(param, fieldName, Long.valueOf(value.trim()));

            } else if (Boolean.class.isAssignableFrom(fieldClass) || "boolean".equals(fieldClass.getName())) {
                ZestReflectHelper.setValue(param, fieldName, Boolean.valueOf(value.trim()));

            } else if (Double.class.isAssignableFrom(fieldClass) || "double".equals(fieldClass.getName())) {
                ZestReflectHelper.setValue(param, fieldName, Double.valueOf(value.trim()));

            } else if (Float.class.isAssignableFrom(fieldClass) || "float".equals(fieldClass.getName())) {
                ZestReflectHelper.setValue(param, fieldName, Float.valueOf(value.trim()));

            } else if (String.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValue(param, fieldName, value);

            } else if (Date.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValue(param, fieldName, ZestDateUtil.parseDate(value));

            } else if (List.class.isAssignableFrom(fieldClass)) {
                Class<?> listValueClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                ZestReflectHelper.setValue(param, fieldName, ZestJsonUtil.fromJsonArray(value, listValueClass));

            } else if (Map.class.isAssignableFrom(fieldClass)) {
                throw new ZestException(Messages.parseParamNonsupportMap());

            } else {
                ZestReflectHelper.setValue(param, fieldName, ZestJsonUtil.fromJson(value, fieldClass));
            }
        } catch (Exception e) {
            throw new ZestException(Messages.parseParamObjLoad(fieldName), e);
        }
    }

    /**
     * @return file name of test case data.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set file name of test case data.
     *
     * @param fileName
     *          File name.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return file path of test case data.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Set file path of test case data.
     *
     * @param filePath
     *          File path.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return for date types, do you need to do offset processing when inserting into the database.
     */
    public boolean isTransferTime() {
        return transferTime;
    }

    /**
     * @return if the date needs to be offset, how many milliseconds is the difference between the current time and the
     * time described in the test case.
     */
    public long getCurrentTimeDiff() {
        return currentTimeDiff;
    }

    /**
     * @return description of unit test.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return test parameter. It can be automatically filled according to the content in the unit test data file.
     */
    public ZestParam getParam() {
        return param;
    }

    /**
     * Set test parameter.
     *
     * @param param
     *          Test parameter.
     */
    public void setParam(ZestParam param) {
        this.param = param;
    }

    /**
     * @return list of data sources.
     */
    public List<Source> getSourceList() {
        return sourceList;
    }

    /**
     * @return time to start the test, in milliseconds.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Set time to start the test.
     *
     * @param startTime
     *          Time to start the test.
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return time when the main logic of the test end, in milliseconds.
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Set time when the main logic of the test end.
     * @param endTime
     *          Time when the main logic of the test end.
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return test data file version.
     */
    public float getVersion() {
        return version;
    }
}
