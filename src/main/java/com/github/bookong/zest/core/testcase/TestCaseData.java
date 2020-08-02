package com.github.bookong.zest.core.testcase;

import com.github.bookong.zest.core.Launcher;
import com.github.bookong.zest.exception.LoadTestCaseFileException;
import com.github.bookong.zest.support.xml.data.Data;
import com.github.bookong.zest.support.xml.data.DataSource;
import com.github.bookong.zest.support.xml.data.ParamField;
import com.github.bookong.zest.support.xml.data.TestParam;
import com.github.bookong.zest.util.LoadTestCaseUtil;
import com.github.bookong.zest.util.Messages;
import com.github.bookong.zest.util.ZestReflectHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangxu
 */
public class TestCaseData {

    private static Logger                                  logger       = LoggerFactory.getLogger(TestCaseData.class);

    /** 测试数据文件名称 */
    private String                                         fileName;

    /** 测试数据文件路径 */
    private String                                         filePath;

    /** 对于日期类型，在插入数据库时是否需要做偏移处理 */
    private boolean                                        transferTime = false;

    /** 如果日期需要偏移处理，当前时间与测试用例上描述的时间相差多少毫秒 */
    private long                                           currDbTimeDiff;

    /** 描述 */
    private String                                         description;

    /** 测试参数 */
    private ZestTestParam                                  testParam;

    /** 数据源描述 */
    private List<TestCaseDataSource>                       dataSources  = new ArrayList<>();

    /** 开始进行测试的时间 */
    private long                                           startTime;

    /** 测试结束的时间 */
    private long                                           endTime;

    /** 关系型数据库的 SqlType，第一层 key 是 TestDataSource 的 id, 第二层的 key 是表名，第三层的 key 是列名，不会并发处理，不考虑多线程问题 */
    private Map<String, Map<String, Map<String, Integer>>> rmdbSqlTypes = new HashMap<>();

    /**
     * 用 XML 数据初始化对象
     *
     * @param xmlData
     * @throws LoadTestCaseFileException
     */
    public void load(Launcher launcher, Data xmlData) throws LoadTestCaseFileException {
        this.description = StringUtils.trimToEmpty(xmlData.getDescription());
        this.transferTime = StringUtils.isNotBlank(xmlData.getCurrDbTime());
        if (isTransferTime()) {
            Date currDbTime = LoadTestCaseUtil.parseDate(xmlData.getCurrDbTime());
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            currDbTimeDiff = cal.getTimeInMillis() - currDbTime.getTime();
        }

        load(xmlData.getTestParam());
        if (xmlData.getDataSources() != null) {
            for (DataSource xmlDataSource : xmlData.getDataSources().getDataSource()) {
                dataSources.add(new TestCaseDataSource(this, xmlDataSource,
                                                       launcher.getDataConverterMap().getOrDefault(xmlDataSource.getId(),
                                                                                                   Collections.emptyList())));
            }
        }
    }

    private void load(TestParam xmlTestParam) throws LoadTestCaseFileException {
        for (ParamField xmlParamField : xmlTestParam.getParamField()) {
            try {
                Triple<Object, String, String> triple = getObjectAndFieldAndMapKey(xmlParamField);
                setParamObjValue(xmlParamField, triple.getLeft(), triple.getMiddle(), triple.getRight());

            } catch (LoadTestCaseFileException e) {
                throw e;
            } catch (Exception e) {
                throw new LoadTestCaseFileException(Messages.failParseParamPath(xmlParamField), e);
            }
        }
    }

    private Triple<Object, String, String> getObjectAndFieldAndMapKey(ParamField xmlParamField) throws LoadTestCaseFileException {
        try {
            if (xmlParamField.isNull() && StringUtils.isBlank(xmlParamField.getValue())) {
                throw new LoadTestCaseFileException(Messages.failParseParamNull(xmlParamField));
            }

            String path = xmlParamField.getPath();
            int pos;
            String mapKey = "";
            if (path.endsWith("-")) {
                // List
                if (xmlParamField.isNull()) {
                    throw new LoadTestCaseFileException(Messages.failParseParamSetContainerNull(xmlParamField));
                }
                path = path.substring(0, path.length() - 1);

            } else if ((pos = path.indexOf(':')) > 0) {
                // Map
                if (xmlParamField.isNull()) {
                    throw new LoadTestCaseFileException(Messages.failParseParamSetContainerNull(xmlParamField));
                }
                path = path.substring(0, pos);
                mapKey = path.substring(pos + 1);

            }

            Object obj = getTestParam();
            String[] fieldNames = StringUtils.split(path, '.');
            for (int i = 0; i < fieldNames.length - 1; i++) {
                obj = ZestReflectHelper.getValueByFieldName(obj, fieldNames[i]);
            }

            if (obj == null) {
                throw new LoadTestCaseFileException(Messages.failParseParamObj(xmlParamField));
            }

            return new ImmutableTriple<>(obj, fieldNames[fieldNames.length - 1], mapKey);

        } catch (LoadTestCaseFileException e) {
            throw e;
        } catch (Exception e) {
            throw new LoadTestCaseFileException(Messages.failParseParamObj(xmlParamField), e);
        }
    }

    private void setParamObjValue(ParamField xmlParamField, Object obj, String lastFieldName,
                                  String mapKey) throws LoadTestCaseFileException {
        try {
            if (xmlParamField.isNull()) {
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, null);
                return;
            }

            Field field = ZestReflectHelper.getFieldByFieldName(obj, lastFieldName);
            if (field == null) {
                throw new LoadTestCaseFileException(Messages.failParseParamObj(xmlParamField));
            }

            String value = StringUtils.trimToEmpty(xmlParamField.getValue());
            Class<?> fieldClass = field.getType();
            if (Integer.class.isAssignableFrom(fieldClass) || "int".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, Integer.valueOf(value));

            } else if (Long.class.isAssignableFrom(fieldClass) || "long".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, Long.valueOf(value));

            } else if (Boolean.class.isAssignableFrom(fieldClass) || "boolean".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, Boolean.valueOf(value));

            } else if (Double.class.isAssignableFrom(fieldClass) || "double".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, Double.valueOf(value));

            } else if (Float.class.isAssignableFrom(fieldClass) || "float".equals(fieldClass.getName())) { //$NON-NLS-1$
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, Float.valueOf(value));

            } else if (String.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, value);

            } else if (Date.class.isAssignableFrom(fieldClass)) {
                ZestReflectHelper.setValueByFieldName(obj, lastFieldName, LoadTestCaseUtil.parseDate(value));

            } else if (List.class.isAssignableFrom(fieldClass)) {
                addParamObjContainerValue(xmlParamField, obj, field, mapKey, value, true);

            } else if (Map.class.isAssignableFrom(fieldClass)) {
                addParamObjContainerValue(xmlParamField, obj, field, mapKey, value, false);

            } else {
                throw new LoadTestCaseFileException(Messages.failParseParamSetTypes(xmlParamField, fieldClass));
            }

        } catch (LoadTestCaseFileException e) {
            throw e;
        } catch (Exception e) {
            throw new LoadTestCaseFileException(Messages.failParseParamSet(xmlParamField), e);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void addParamObjContainerValue(ParamField xmlParamField, Object obj, Field field, String mapKey,
                                           String value, boolean isList) throws LoadTestCaseFileException {
        try {
            Type gt = field.getGenericType();
            if (!(gt instanceof ParameterizedType)) {
                throw new LoadTestCaseFileException(Messages.failParseParamSetGeneric(xmlParamField));
            }

            Type[] types = ((ParameterizedType) gt).getActualTypeArguments();

            if (isList) {
                if (types.length != 1) {
                    throw new LoadTestCaseFileException(Messages.failParseParamSetGeneric(xmlParamField));
                }

                Class<?> listValueClass = (Class<?>) types[0];
                List list = ((List) ZestReflectHelper.getValueByFieldName(obj, field));
                list.add(convertParamValue(xmlParamField, listValueClass, value));
            } else {
                // Map
                if (types.length != 2) {
                    throw new LoadTestCaseFileException(Messages.failParseParamSetGeneric(xmlParamField));
                }

                Class<?> mapKeyClass = (Class<?>) types[0];
                Class<?> mapValueClass = (Class<?>) types[1];
                Map map = (Map) ZestReflectHelper.getValueByFieldName(obj, field);
                Object key = convertParamValue(xmlParamField, mapKeyClass, mapKey);
                map.put(key, convertParamValue(xmlParamField, mapValueClass, value));
            }

        } catch (LoadTestCaseFileException e) {
            throw e;
        } catch (Exception e) {
            throw new LoadTestCaseFileException(Messages.failParseParamSetContainer(xmlParamField), e);
        }
    }

    private Object convertParamValue(ParamField xmlParamField, Class<?> target,
                                     String value) throws LoadTestCaseFileException {
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
            return LoadTestCaseUtil.parseDate(value);

        } else {
            throw new LoadTestCaseFileException(Messages.failParseParamSetTypes(xmlParamField, target));
        }
    }

    /**
     * 获取指定数据源下指定表下各个列的 SqlType
     *
     * @param dataSourceId 数据源ID
     * @param tableName 表名
     * @return 返回一个 map，key 为列名，value 是 SqlType
     */
    public Map<String, Integer> getRmdbTableColSqlTypes(String dataSourceId, String tableName) {
        dataSourceId = dataSourceId.toLowerCase();
        tableName = tableName.toLowerCase();
        Map<String, Map<String, Integer>> map1 = rmdbSqlTypes.computeIfAbsent(dataSourceId, o -> new HashMap<>());
        return map1.computeIfAbsent(tableName, o -> new HashMap<>());
    }

    /**
     * 设定指定数据源下指定表的 SqlType
     *
     * @param dataSourceId 数据源ID
     * @param tableName 表名
     * @param colName 列名
     * @param sqlType 关系型数据库的 SqlType
     */
    public void putRmdbTableColSqlTypes(String dataSourceId, String tableName, String colName, Integer sqlType) {
        dataSourceId = dataSourceId.toLowerCase();
        tableName = tableName.toLowerCase();
        colName = colName.toLowerCase();
        Map<String, Map<String, Integer>> map1 = rmdbSqlTypes.computeIfAbsent(dataSourceId, o -> new HashMap<>());
        map1.computeIfAbsent(tableName, o -> new HashMap<>()).put(colName, sqlType);
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
