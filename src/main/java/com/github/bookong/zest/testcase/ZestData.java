package com.github.bookong.zest.testcase;

import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Data;
import com.github.bookong.zest.support.xml.data.Field;
import com.github.bookong.zest.support.xml.data.ParamField;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestJsonUtil;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Jiang Xu
 */
public class ZestData {

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

    /**
     * 用 XML 数据初始化对象
     */
    public void load(ZestWorker worker, Data xmlData) {
        this.description = StringUtils.trimToEmpty(xmlData.getDescription());
        this.transferTime = StringUtils.isNotBlank(xmlData.getCurrentTime());
        if (isTransferTime()) {
            Date currDbTime = ZestDateUtil.parseDate(xmlData.getCurrentTime());
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            currentTimeDiff = cal.getTimeInMillis() - currDbTime.getTime();
        }

        for (ParamField xmlParamField : xmlData.getParam().getParamField()) {
            load(xmlParamField);
        }

        if (xmlData.getSources() != null) {
            for (com.github.bookong.zest.support.xml.data.Source xmlDataSource : xmlData.getSources().getSource()) {
                this.sourceList.add(new Source(worker, xmlDataSource));
            }
        }
    }

    private void load(ParamField xmlParamField) {
        try {
            String fieldName = StringUtils.trimToEmpty(xmlParamField.getName());
            String value = StringUtils.trimToEmpty(xmlParamField.getValue());
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
            } else if (List.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValue(param, fieldName, ZestJsonUtil.fromJsonArray(value, fieldClass));
            } else {
                ZestReflectHelper.setValue(param, fieldName, ZestJsonUtil.fromJson(value, fieldClass));
            }
        } catch (ZestException e) {
            throw e;
        } catch (Exception e) {
            throw new ZestException(Messages.parseParamObj(xmlParamField), e);
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

}
