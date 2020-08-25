package com.github.bookong.zest.util;

import org.apache.commons.lang3.StringUtils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jiang Xu
 */
public class Messages {

    private static final String   BUNDLE_NAME     = "com.github.bookong.zest.util.messages"; //$NON-NLS-1$

    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages(){
    }

    public static void reBundle() {
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    }

    public static String failRun() {
        return getString("fail.run");
    }

    public static String noAnnotationZest() {
        return getString("no.annotation.zest");
    }

    public static String fileNotFound(String filePath) {
        return getString("file.found", filePath);
    }

    public static String noData() {
        return getString("no.data");
    }

    public static String parse(String filePath) {
        return getString("parse", filePath);
    }

    public static String parseCommonAttr(String nodeName, String attrName) {
        return getString("parse.common.attr", nodeName, attrName);
    }

    public static String parseCommonAttrUnknown(String nodeName, String attrNames) {
        return getString("parse.common.attr.unknown", nodeName, attrNames);
    }

    public static String parseCommonAttrNeed(String nodeName, String attrName) {
        return getString("parse.common.attr.need", nodeName, attrName);
    }

    public static String parseCommonAttrDuplicate(String attrName, String attrValue) {
        return getString("parse.common.attr.duplicate", attrName, attrValue);
    }

    public static String parseCommonAttrEmpty(String attrName) {
        return getString("parse.common.attr.empty", attrName);
    }

    public static String parseCommonClassFound(String className) {
        return getString("parse.common.class.found", className);
    }

    public static String parseCommonChildren(String nodeName) {
        return getString("parse.common.children", nodeName);
    }

    public static String parseCommonChildrenUnknown(String parentElement, String childrenElement) {
        return getString("parse.common.children.unknown", parentElement, childrenElement);
    }

    public static String parseCommonChildrenList(String parentElement, String childrenElement) {
        return getString("parse.common.children.list", parentElement, childrenElement);
    }

    public static String parseZest() {
        return getString("parse.zest");
    }

    public static String parseZestNecessary() {
        return getString("parse.zest.necessary");
    }

    public static String parseSourcesError() {
        return getString("parse.sources.error");
    }

    public static String parseSourceError(String sourceId) {
        return getString("parse.source.error", sourceId);
    }

    public static String parseSourceNecessary() {
        return getString("parse.source.necessary");
    }

    public static String parseSourceInitError() {
        return getString("parse.source.init.error");
    }

    public static String parseSourceVerifyError() {
        return getString("parse.source.verify.error");
    }

    public static String parseSourceOperationMatch(String operation, String nodeName, String subNodeName) {
        return getString("parse.source.operation.match", operation, nodeName, subNodeName);
    }

    public static String parseSourceOperationUnknown(String operation) {
        return getString("parse.source.operation.unknown", operation);
    }

    public static String parseSourceOperationNone() {
        return getString("parse.source.operation.none");
    }

    public static String parseTableError(String name) {
        return getString("parse.table.error", name);
    }

    public static String parseTableMeta() {
        return getString("parse.table.meta");
    }

    public static String parseTableData() {
        return getString("parse.table.data");
    }

    public static String parseTableSortExist(String fieldName) {
        return getString("parse.table.sort.exist", fieldName);
    }

    public static String parseTableRule(String path) {
        return getString("parse.table.rule", path);
    }

    public static String parseTableRowExist(String tableName, String fieldName) {
        return getString("parse.table.row.exist", tableName, fieldName);
    }

    public static String parseCollectionError(String name) {
        return getString("parse.collection.error", name);
    }

    public static String parseCollectionSortExits(String fieldName) {
        return getString("parse.collection.sort.exits", fieldName);
    }

    public static String parseCollectionRule(String path) {
        return getString("parse.collection.rule", path);
    }

    public static String parseSortsError() {
        return getString("parse.sorts.error");
    }

    public static String parseSortsPosition() {
        return getString("parse.sorts.position");
    }

    public static String parseSortsOrder() {
        return getString("parse.sorts.order");
    }

    public static String parseSortError(String fieldName) {
        return getString("parse.sort.error", fieldName);
    }

    public static String parseSortDirection() {
        return getString("parse.sort.direction");
    }

    public static String parseRulesPosition() {
        return getString("parse.rules.position");
    }

    public static String parseRuleError(String path) {
        return getString("parse.rule.error", path);
    }

    public static String parseRuleFromUnitUnknown(String unit) {
        return getString("parse.rule.from.unit.unknown", unit);
    }

    public static String parseParamError() {
        return getString("parse.param.error");
    }

    public static String parseRulesError() {
        return getString("parse.rules.error");
    }

    public static String parseRuleChoice() {
        return getString("parse.rule.choice");
    }

    public static String parseParamNone(String fieldName) {
        return getString("parse.param.none", fieldName);
    }

    public static String parseParamNonsupportMap() {
        return getString("parse.param.nonsupport.map");
    }

    public static String parseDataError(int dataIdx) {
        return getString("parse.data.error", dataIdx);
    }

    public static String parseParamObjLoad(String fieldName) {
        return getString("parse.param.obj.load", fieldName);
    }

    public static String parseDate(String value) {
        return getString("parse.date", value);
    }

    public static String parseDataSqlType(String tableName, String fieldName) {
        return getString("parse.data.sql.type", tableName, fieldName);
    }

    public static String parseDataSqlTypeUnsupported(String tableName, String fieldName, Integer colSqlType) {
        return getString("parse.data.sql.type.unsupported", tableName, fieldName, colSqlType);
    }

    public static String parseDataFieldDuplicate(String tableName, String fieldName) {
        return getString("parse.data.field.duplicate", tableName, fieldName);
    }

    public static String parseDataFieldUnder(String tableName, String fieldName) {
        return getString("parse.data.field.under", tableName, fieldName);
    }

    public static String parseOperation() {
        return getString("parse.operation");
    }

    public static String duplicateOperation(String value) {
        return getString("duplicate.operation", value);
    }

    public static String initExecutor(String className) {
        return getString("init.executor", className);
    }

    public static String initParam() {
        return getString("init.param");
    }

    public static String executorMatch() {
        return getString("executor.match");
    }

    public static String statementEvaluate(String testCaseFilePath) {
        return getString("statement.evaluate", testCaseFilePath);
    }

    public static String statementRun(String desc) {
        return getString("statement.run", desc);
    }

    public static String ignoreTargetData(String id) {
        return getString("ignore.target.data", id);
    }

    public static String ignoreTargetTable(String sourceId, String tableName) {
        return getString("ignore.target.table", sourceId, tableName);
    }

    public static String ignoreTargetColUnspecified(String sourceId, String tableName) {
        return getString("ignore.target.col.unspecified", sourceId, tableName);
    }

    public static String startCheckTable(String sourceId, String tableName) {
        return getString("start.check.table", sourceId, tableName);
    }

    public static String checkDs(String value) {
        return getString("check.ds", value);
    }

    public static String checkTableSize(String sourceId, String tableName) {
        return getString("check.table.size", sourceId, tableName);
    }

    public static String checkTableColDate(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateCurrent(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.current", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateFrom(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.from", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColRegexp(String sourceId, String tableName, int rowIdx, String columnName,
                                             String regExp) {
        return getString("check.table.col.regexp", sourceId, tableName, rowIdx, columnName, regExp);
    }

    public static String checkTableColNullable(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.nullable", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColNull(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.null", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableColDateType(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col.date.type", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkTableCol(String sourceId, String tableName, int rowIdx, String columnName) {
        return getString("check.table.col", sourceId, tableName, rowIdx, columnName);
    }

    public static String checkDocSize(String sourceId, String tableName) {
        return getString("check.doc.size", sourceId, tableName);
    }

    private static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    private static String getString(String key, Object... args) {
        Object[] objs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                objs[i] = "";
            } else {
                objs[i] = arg;
            }
        }
        return String.format(getString(key), objs);
    }
}
