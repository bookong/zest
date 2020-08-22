package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Data;
import com.github.bookong.zest.support.xml.data.ParamField;
import com.github.bookong.zest.util.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
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
    private List<Source> sourceList   = Collections.synchronizedList(new ArrayList<>());

    /** 开始进行测试的时间 */
    private long         startTime;

    /** 测试结束的时间 */
    private long         endTime;

    public ZestData(String testCaseFilePath){
        this.filePath = testCaseFilePath;
        this.fileName = testCaseFilePath.substring(testCaseFilePath.lastIndexOf(File.separator) + 1);
    }

    public void load(ZestWorker worker) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(getFilePath()));
            List<Node> elements = ZestXmlUtil.getElements(doc.getChildNodes());

            if (elements.size() != 1 || !"Zest".equals(elements.get(0).getNodeName())) {
                throw new ZestException(Messages.parseZest());
            }

            loadZest(worker, elements.get(0).getNodeName(), elements.get(0));
        } catch (Exception e) {
            throw new ZestException(Messages.parse(getFilePath()), e);
        }
    }

    private void loadZest(ZestWorker worker, String nodeName, Node zestNode) {
        List<Node> elements = ZestXmlUtil.getElements(zestNode.getChildNodes());
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(zestNode);

        if (elements.size() != 3 //
            || !"Description".equals(elements.get(0).getNodeName()) //
            || !"Sources".equals(elements.get(1).getNodeName()) //
            || !"Param".equals(elements.get(2).getNodeName())) {
            throw new ZestException(Messages.parseZestNecessary());
        }

        this.description = ZestXmlUtil.getValue(elements.get(0));
        loadSources(worker, elements.get(1).getNodeName(), elements.get(1));
        loadParam(worker, elements.get(2).getNodeName(), elements.get(2));

        this.version = ZestXmlUtil.removeFloatAttr(nodeName, attrMap, "Version", 1.0F);
        String currentTime = ZestXmlUtil.removeAttr(nodeName, attrMap, "CurrentTime");
        this.transferTime = StringUtils.isNotBlank(currentTime);
        if (isTransferTime()) {
            Date currDbTime = ZestDateUtil.parseDate(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            currentTimeDiff = cal.getTimeInMillis() - currDbTime.getTime();
        }
        ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
    }

    private void loadSources(ZestWorker worker, String nodeName, Node sourcesNode) {
        List<Node> elements = ZestXmlUtil.getElements(sourcesNode.getChildNodes());
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(sourcesNode);

        for (Node item : elements) {
            if (!"Source".equals(item.getNodeName())) {
                throw new ZestException(Messages.parseSourcesType());
            }
        }

        getSourceList().clear();
        Set<String> sourceIds = new HashSet<>(elements.size() + 1);
        for (Node sourceNode : elements) {
            getSourceList().add(new Source(worker, nodeName, sourceNode, sourceIds));
        }
        ZestXmlUtil.attrMapMustEmpty(nodeName, attrMap);
    }

    private void loadParam(ZestWorker worker, String nodeName, Node paramNode) {
        List<Node> elements = ZestXmlUtil.getElements(paramNode.getChildNodes());
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(paramNode);

        for (Node item : elements) {
            if (!"ParamField".equals(item.getNodeName())) {
                throw new ZestException(Messages.parseParamType());
            }
        }

        Set<String> fieldNames = new HashSet<>(elements.size() + 1);
        for (Node item : elements) {
            loadParamField(worker, item.getNodeName(), item, fieldNames);
        }
    }

    private void loadParamField(ZestWorker worker, String nodeName, Node paramFieldNode, Set<String> fieldNames) {
        Map<String, String> attrMap = ZestXmlUtil.getAllAttrs(paramFieldNode);

        String fieldName = ZestXmlUtil.removeAttr(nodeName, attrMap, "Name");
        if (StringUtils.isBlank(fieldName)) {
            throw new ZestException(Messages.parseParamNameEmpty());
        }

        if (fieldNames.contains(fieldName)) {
            throw new ZestException(Messages.parseParamNameDuplicate(fieldName));
        }
        fieldNames.add(fieldName);

        if (ZestReflectHelper.getField(param, fieldName) == null) {
            throw new ZestException(Messages.parseParamNone(fieldName));
        }

        try {
            String value = ZestXmlUtil.getValue(paramFieldNode);
            Class<?> fieldClass = ZestReflectHelper.getField(param, fieldName).getType();

            if (Integer.class.isAssignableFrom(fieldClass) || "int".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(param, fieldName, Integer.valueOf(value.trim()));
            } else if (Long.class.isAssignableFrom(fieldClass) || "long".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(param, fieldName, Long.valueOf(value.trim()));
            } else if (Boolean.class.isAssignableFrom(fieldClass) || "boolean".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(param, fieldName, Boolean.valueOf(value.trim()));
            } else if (Double.class.isAssignableFrom(fieldClass) || "double".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(param, fieldName, Double.valueOf(value.trim()));
            } else if (Float.class.isAssignableFrom(fieldClass) || "float".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(param, fieldName, Float.valueOf(value.trim()));
            } else if (String.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValue(param, fieldName, value);
            } else if (Date.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValue(param, fieldName, ZestDateUtil.parseDate(value));
            } else if (List.class.isAssignableFrom(fieldClass) || Map.class.isAssignableFrom(fieldClass)) {
                throw new ZestException(Messages.parseParamContainerNonsupport());
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

    public void setTestParam(ZestParam param) {
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

    // FIXME
    /** 比较时间（考虑数据偏移情况) */
    public void assertDateEquals(String msg, String dateFormat, String expect, String actual) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            if (StringUtils.isBlank(expect) && StringUtils.isBlank(actual)) {
                return;
            }
            assertDateEquals(msg, sdf.parse(expect), sdf.parse(actual));
        } catch (ParseException e) {
            throw new ZestException(e);
        }
    }

    // FIXME
    /** 比较时间（考虑数据偏移情况) */
    public void assertDateEquals(String msg, Date expect, Date actual) {

        long expectMillisecond = expect.getTime();
        if (isTransferTime()) {
            expectMillisecond += getCurrentTimeDiff();
        }

        Assert.assertEquals(msg, expectMillisecond, actual.getTime());
    }

    public float getVersion() {
        return version;
    }
}
