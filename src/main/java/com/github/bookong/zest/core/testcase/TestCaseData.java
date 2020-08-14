package com.github.bookong.zest.core.testcase;

import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.support.xml.data.Data;
import com.github.bookong.zest.support.xml.data.Source;
import com.github.bookong.zest.support.xml.data.ParamField;
import com.github.bookong.zest.support.xml.data.TestParam;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestDateUtil;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangxu
 */
public class TestCaseData {

    /** 测试数据文件名称 */
    private String                   fileName;

    /** 测试数据文件路径 */
    private String                   filePath;

    /** 对于日期类型，在插入数据库时是否需要做偏移处理 */
    private boolean                  transferTime = false;

    /** 如果日期需要偏移处理，当前时间与测试用例上描述的时间相差多少毫秒 */
    private long                     currDbTimeDiff;

    /** 描述 */
    private String                   description;

    /** 测试参数 */
    private ZestTestParam            testParam;

    /** 数据源描述 */
    private List<TestCaseDataSource> dataSources  = new ArrayList<>();

    /** 开始进行测试的时间 */
    private long                     startTime;

    /** 测试结束的时间 */
    private long                     endTime;

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
            currDbTimeDiff = cal.getTimeInMillis() - currDbTime.getTime();
        }

        load(xmlData.getTestParam());
        if (xmlData.getSources() != null) {
            for (Source xmlDataSource : xmlData.getSources().getSource()) {
                List<AbstractDataConverter> dataConverterList = worker.getDataConverter(xmlDataSource.getId());
                Connection conn = worker.getJdbcConn(xmlDataSource.getId());
                dataSources.add(new TestCaseDataSource(xmlDataSource, dataConverterList, conn));
            }
        }
    }

    private void load(TestParam xmlTestParam) {
        for (ParamField xmlParamField : xmlTestParam.getParamField()) {
            try {
                Triple<Object, String, String> triple = getObjectAndFieldAndMapKey(xmlParamField);
                setParamObjValue(xmlParamField, triple.getLeft(), triple.getMiddle(), triple.getRight());

            } catch (Exception e) {
                throw new RuntimeException(Messages.parseParamPath(xmlParamField), e);
            }
        }
    }

    private Triple<Object, String, String> getObjectAndFieldAndMapKey(ParamField xmlParamField) {
        try {
            if (xmlParamField.isNull() && StringUtils.isBlank(xmlParamField.getValue())) {
                throw new RuntimeException(Messages.parseParamNull(xmlParamField));
            }

            String path = xmlParamField.getPath();
            int pos;
            String mapKey = "";
            if (path.endsWith("-")) {
                // List
                if (xmlParamField.isNull()) {
                    throw new RuntimeException(Messages.parseParamSetContainerNull(xmlParamField));
                }
                path = path.substring(0, path.length() - 1);

            } else if ((pos = path.indexOf(':')) > 0) {
                // Map
                if (xmlParamField.isNull()) {
                    throw new RuntimeException(Messages.parseParamSetContainerNull(xmlParamField));
                }
                path = path.substring(0, pos);
                mapKey = path.substring(pos + 1);

            }

            Object obj = getTestParam();
            String[] fieldNames = StringUtils.split(path, '.');
            for (int i = 0; i < fieldNames.length - 1; i++) {
                obj = ZestReflectHelper.getValue(obj, fieldNames[i]);
            }

            if (obj == null) {
                throw new RuntimeException(Messages.parseParamObj(xmlParamField));
            }

            return new ImmutableTriple<>(obj, fieldNames[fieldNames.length - 1], mapKey);

        } catch (Exception e) {
            throw new RuntimeException(Messages.parseParamObj(xmlParamField), e);
        }
    }

    private void setParamObjValue(ParamField xmlParamField, Object obj, String lastFieldName, String mapKey) {
        try {
            if (xmlParamField.isNull()) {
                ZestReflectHelper.setValue(obj, lastFieldName, null);
                return;
            }

            Field field = ZestReflectHelper.getField(obj, lastFieldName);
            if (field == null) {
                throw new RuntimeException(Messages.parseParamObj(xmlParamField));
            }

            String value = StringUtils.trimToEmpty(xmlParamField.getValue());
            Class<?> fieldClass = field.getType();
            if (Integer.class.isAssignableFrom(fieldClass) || "int".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(obj, lastFieldName, Integer.valueOf(value));

            } else if (Long.class.isAssignableFrom(fieldClass) || "long".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(obj, lastFieldName, Long.valueOf(value));

            } else if (Boolean.class.isAssignableFrom(fieldClass) || "boolean".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(obj, lastFieldName, Boolean.valueOf(value));

            } else if (Double.class.isAssignableFrom(fieldClass) || "double".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(obj, lastFieldName, Double.valueOf(value));

            } else if (Float.class.isAssignableFrom(fieldClass) || "float".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValue(obj, lastFieldName, Float.valueOf(value));

            } else if (String.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValue(obj, lastFieldName, value);

            } else if (Date.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValue(obj, lastFieldName, ZestDateUtil.parseDate(value));

            } else if (List.class.isAssignableFrom(fieldClass)) {
                addParamObjContainerValue(xmlParamField, obj, field, mapKey, value, true);

            } else if (Map.class.isAssignableFrom(fieldClass)) {
                addParamObjContainerValue(xmlParamField, obj, field, mapKey, value, false);

            } else {
                throw new RuntimeException(Messages.parseParamSetTypes(xmlParamField, fieldClass));
            }

        } catch (Exception e) {
            throw new RuntimeException(Messages.parseParamSet(xmlParamField), e);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void addParamObjContainerValue(ParamField xmlParamField, Object obj, Field field, String mapKey,
                                           String value, boolean isList) {
        try {
            Type gt = field.getGenericType();
            if (!(gt instanceof ParameterizedType)) {
                throw new RuntimeException(Messages.parseParamSetGeneric(xmlParamField));
            }

            Type[] types = ((ParameterizedType) gt).getActualTypeArguments();

            if (isList) {
                if (types.length != 1) {
                    throw new RuntimeException(Messages.parseParamSetGeneric(xmlParamField));
                }

                Class<?> listValueClass = (Class<?>) types[0];
                List list = ((List) ZestReflectHelper.getValue(obj, field));
                list.add(convertParamValue(xmlParamField, listValueClass, value));
            } else {
                // Map
                if (types.length != 2) {
                    throw new RuntimeException(Messages.parseParamSetGeneric(xmlParamField));
                }

                Class<?> mapKeyClass = (Class<?>) types[0];
                Class<?> mapValueClass = (Class<?>) types[1];
                Map map = (Map) ZestReflectHelper.getValue(obj, field);
                Object key = convertParamValue(xmlParamField, mapKeyClass, mapKey);
                map.put(key, convertParamValue(xmlParamField, mapValueClass, value));
            }

        } catch (Exception e) {
            throw new RuntimeException(Messages.parseParamSetContainer(xmlParamField), e);
        }
    }

    private Object convertParamValue(ParamField xmlParamField, Class<?> target, String value) {
        if (Integer.class.isAssignableFrom(target)) {
            return Integer.valueOf(value);

        } else if (Long.class.isAssignableFrom(target)) {
            return Long.valueOf(value);

        } else if (Boolean.class.isAssignableFrom(target)) {
            return Boolean.valueOf(value);

        } else if (Double.class.isAssignableFrom(target)) {
            return Double.valueOf(value);

        } else if (Float.class.isAssignableFrom(target)) {
            return Float.valueOf(value);

        } else if (String.class.isAssignableFrom(target)) {
            return value;

        } else if (Date.class.isAssignableFrom(target)) {
            return ZestDateUtil.parseDate(value);

        } else {
            throw new RuntimeException(Messages.parseParamSetTypes(xmlParamField, target));
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

    public long getCurrDbTimeDiff() {
        return currDbTimeDiff;
    }

    public String getDescription() {
        return description;
    }

    public ZestTestParam getTestParam() {
        return testParam;
    }

    public void setTestParam(ZestTestParam testParam) {
        this.testParam = testParam;
    }

    public List<TestCaseDataSource> getDataSources() {
        return dataSources;
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

    // --------------------------------
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
            throw new RuntimeException(e);
        }
    }

    // FIXME
    /** 比较时间（考虑数据偏移情况) */
    public void assertDateEquals(String msg, Date expect, Date actual) {

        long expectMillisecond = expect.getTime();
        if (isTransferTime()) {
            expectMillisecond += getCurrDbTimeDiff();
        }

        Assert.assertEquals(msg, expectMillisecond, actual.getTime());
    }

}
