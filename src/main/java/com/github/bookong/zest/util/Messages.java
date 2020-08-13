package com.github.bookong.zest.util;

import com.github.bookong.zest.support.xml.data.ParamField;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author jiangxu
 */
public class Messages {

    private static final String         BUNDLE_NAME     = "com.github.bookong.zest.util.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages(){
    }

    public static String fileNotFound(String filePath) {
        return getString("file.found", filePath);
    }

    public static String parseFile(String filePath) {
        return getString("parse.file", filePath);
    }

    public static String parseDate(String value) {
        return getString("parse.date", value);
    }

    public static String parseParamPath(ParamField xmlParamField) {
        return getString("parse.param.path", xmlParamField.getPath());
    }

    public static String parseParamNull(ParamField xmlParamField) {
        return getString("parse.param.null", xmlParamField.getPath());
    }

    public static String parseParamObj(ParamField xmlParamField) {
        return getString("parse.param.obj", xmlParamField.getPath());
    }

    public static String parseParamSet(ParamField xmlParamField) {
        return getString("parse.param.set", xmlParamField.getPath());
    }

    public static String parseParamSetTypes(ParamField xmlParamField, Class<?> fieldClass) {
        return getString("parse.param.set.types", xmlParamField.getPath(), fieldClass.getName());
    }

    public static String parseParamSetGeneric(ParamField xmlParamField) {
        return getString("parse.param.set.generic", xmlParamField.getPath());
    }

    public static String parseParamSetContainerNull(ParamField xmlParamField) {
        return getString("parse.param.set.container.null", xmlParamField.getPath());
    }

    public static String parseParamSetContainer(ParamField xmlParamField) {
        return getString("parse.param.set.container", xmlParamField.getPath());
    }

    public static String parseDataSqlType(String tableName, String fieldName) {
        return getString("parse.data.sql.type", tableName, fieldName);
    }

    public static String parseDataSqlTypeUnsupport(String tableName, String fieldName, Integer colSqlType) {
        return getString("parse.data.sql.type.unsupport", tableName, fieldName, colSqlType);
    }

    public static String parseDataFieldDuplicate(String tableName, String fieldName) {
        return getString("parse.data.field.duplicate", tableName, fieldName);
    }

    public static String parseDataFieldUnder(String tableName, String fieldName) {
        return getString("parse.data.field.under", tableName, fieldName);
    }

    public static String parseDataFieldNone(String tableName, String fieldName) {
        return getString("parse.data.field.none", tableName, fieldName);
    }

    public static String statementEvaluate(String testCaseFilePath) {
        return getString("statement.evaluate", testCaseFilePath);
    }

    public static String parseDbMeta() {
        return getString("parse.db.meta");
    }

    public static String failRun() {
        return getString("fail.run");
    }

    public static String noData() {
        return getString("no.data");
    }

    public static String annotationConnection() {
        return getString("annotation.connection");
    }

    public static String initExecuter(String className) {
        return getString("init.executer", className);
    }

    public static String duplicateDs(String value) {
        return getString("duplicate.ds", value);
    }

    public static String parseDs() {
        return getString("parse.ds");
    }

    public static String initDc(String className) {
        return getString("init.dc", className);
    }

    public static String ignoreTargetData(String id) {
        return getString("ignore.target.data", id);
    }

    public static String ignoreTargetTable(String dataSourceId, String tableName) {
        return getString("ignore.target.table", dataSourceId, tableName);
    }

    public static String initParam() {
        return getString("init.param");
    }

    public static String noAnnotationZest() {
        return getString("no.annotation.zest");
    }

    public static String executerMatchSql() {
        return getString("executer.match.sql");
    }

    public static String checkDs(String value) {
        return getString("check.ds", value);
    }

    public static String statementRun(String desc) {
        return getString("statement.run", desc);
    }

    public static String startCheckTable(String dataSourceId, String tableName) {
        return getString("start.check.table", dataSourceId, tableName);
    }

    public static String checkTableSize(String dataSourceId, String tableName) {
        return getString("check.table.size", dataSourceId, tableName);
    }

    public static String ignoreTargetColUnspecified(String dataSourceId, String tableName) {
        return getString("ignore.target.col.unspecified", dataSourceId, tableName);
    }

    public static String checkTableColDate(String dataSourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date", dataSourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColdateCurrent(String dataSourceId, String tableName, int rowIdx,
                                                  String columnName) {
        return getString("check.table.col.date.current", dataSourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColNullable(String dataSourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.nullable", dataSourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateFrom(String dataSourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.from", dataSourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColNull(String dataSourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.null", dataSourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateType(String dataSourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.type", dataSourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableCol(String dataSourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col", dataSourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColRegexp(String dataSourceId, String tableName, int rowIdx, String columnName,
                                             String regExp) {
        return getString("check.table.col.regexp", dataSourceId, tableName, rowIdx, columnName, regExp);
    }

    public static String checkTableColDateFromUnit(String dataSourceId, String tableName, int rowIdx, String columnName,
                                                   String unit) {
        return getString("check.table.col.date.from.unit", dataSourceId, tableName, rowIdx, columnName, unit);
    }

    public static String parseDataTableQuery() {
        return getString("parse.data.table.query");
    }

    private static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!'; // $NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private static String getString(String key, Object... args) {
        return String.format(getString(key), args);
    }
}
