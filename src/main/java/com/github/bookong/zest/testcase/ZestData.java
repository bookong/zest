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
 * @author Jiang Xu
 */
public class ZestData {

    private float        version;

    /** 测试数据文件名称 */
    private String       fileName;

    /** 测试数据文件路径 */
    private String       filePath;

    /** 对于日期类型，在插入数据库时是否需要做偏移处理 */
    private boolean      transferTime = false;

    /** 如果日期需要偏移处理，当前时间与测试用例上描述的时间相差多少毫秒 */
    private long         currentTimeDiff;

    /** 描述 */
    private String       description;

    /** 测试参数 */
    private ZestParam    param;

    /** 数据源描述 */
    private List<Source> sourceList   = new ArrayList<>();

    /** 开始进行测试的时间 */
    private long         startTime;

    /** 测试结束的时间 */
    private long         endTime;

    public ZestData(String filePath){
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isTransferTime() {
        return transferTime;
    }

    public long getCurrentTimeDiff() {
        return currentTimeDiff;
    }

    public String getDescription() {
        return description;
    }

    public ZestParam getParam() {
        return param;
    }

    public void setParam(ZestParam param) {
        this.param = param;
    }

    public List<Source> getSourceList() {
        return sourceList;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public float getVersion() {
        return version;
    }
}
