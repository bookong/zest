package com.github.bookong.zest.testcase;

import com.github.bookong.zest.common.ZestGlobalConstant.Xml;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.util.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            List<Node> rootElements = ZestXmlUtil.getChildren(doc);

            if (rootElements.size() != 1 || !Xml.ZEST.equals(rootElements.get(0).getNodeName())) {
                throw new ZestException(Messages.parseZest());
            }

            loadZest(worker, rootElements.get(0).getNodeName(), rootElements.get(0));
        } catch (Exception e) {
            throw new ZestException(Messages.parse(getFilePath()), e);
        }
    }

    private void loadZest(ZestWorker worker, String nodeName, Node node) {
        List<Node> children = ZestXmlUtil.getChildren(node);
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);

        if (children.size() != 3 //
            || !Xml.DESCRIPTION.equals(children.get(0).getNodeName()) //
            || !Xml.SOURCES.equals(children.get(1).getNodeName()) //
            || !Xml.PARAM.equals(children.get(2).getNodeName())) { //
            throw new ZestException(Messages.parseZestNecessary());
        }

        this.description = ZestXmlUtil.getValue(children.get(0));
        loadSources(worker, children.get(1).getNodeName(), children.get(1));
        loadParam(children.get(2).getNodeName(), children.get(2));

        this.version = ZestXmlUtil.removeFloatAttr(nodeName, attrMap, Xml.VERSION, 1.0F);
        String currentTime = ZestXmlUtil.removeAttr(nodeName, attrMap, Xml.CURRENT_TIME);
        this.transferTime = StringUtils.isNotBlank(currentTime);
        if (isTransferTime()) {
            Date currDbTime = ZestDateUtil.parseDate(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            this.currentTimeDiff = cal.getTimeInMillis() - currDbTime.getTime();
        }
        ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
    }

    private void loadSources(ZestWorker worker, String nodeName, Node node) {
        try {
            List<Node> children = ZestXmlUtil.getChildren(node);
            Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);

            for (Node item : children) {
                if (!Xml.SOURCE.equals(item.getNodeName())) {
                    throw new ZestException(Messages.parseCommonChildrenList(Xml.SOURCES, Xml.SOURCE));
                }
            }

            Set<String> sourceIds = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                getSourceList().add(new Source(worker, child.getNodeName(), child, sourceIds));
            }
            ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
        } catch (Exception e) {
            throw new ZestException(Messages.parseSourcesError(), e);
        }
    }

    private void loadParam(String nodeName, Node node) {
        try {
            List<Node> children = ZestXmlUtil.getChildren(node);
            Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);

            for (Node item : children) {
                if (!Xml.PARAM_FIELD.equals(item.getNodeName())) {
                    throw new ZestException(Messages.parseCommonChildrenList(Xml.PARAM, Xml.PARAM_FIELD));
                }
            }

            Set<String> fieldNames = new HashSet<>(children.size() + 1);
            for (Node child : children) {
                loadParamField(child.getNodeName(), child, fieldNames);
            }

            ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
        } catch (Exception e) {
            throw new ZestException(Messages.parseParamError(), e);
        }
    }

    private void loadParamField(String nodeName, Node node, Set<String> fieldNames) {
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(node);

        String fieldName = ZestXmlUtil.removeNotEmptyAttr(nodeName, attrMap, Xml.NAME);
        ZestXmlUtil.duplicateCheck(Xml.NAME, fieldNames, fieldName);

        Field field = ZestReflectHelper.getField(param, fieldName);
        if (field == null) {
            throw new ZestException(Messages.parseParamNone(fieldName));
        }

        try {
            String value = ZestXmlUtil.getValue(node);
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

        ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
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
