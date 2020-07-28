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
        return String.format(getString("file.found"), filePath);
    }

    public static String failParseFile(String filePath) {
        return String.format(getString("fail.parse.file"), filePath);
    }

    public static String failParseDate(String value) {
        return String.format(getString("fail.parse.date"), value);
    }

    public static String failParseParamPath(ParamField xmlParamField) {
        return String.format(getString("fail.parse.param.path"), xmlParamField.getPath());
    }

    public static String failParseParamNull(ParamField xmlParamField) {
        return String.format(getString("fail.parse.param.null"), xmlParamField.getPath());
    }

    public static String failParseParamObj(ParamField xmlParamField) {
        return String.format(getString("fail.parse.param.obj"), xmlParamField.getPath());
    }

    public static String failParseParamSet(ParamField xmlParamField) {
        return String.format(getString("fail.parse.param.set"), xmlParamField.getPath());
    }

    public static String failParseParamSetTypes(ParamField xmlParamField, Class<?> fieldClass) {
        return String.format(getString("fail.parse.param.set.types"), xmlParamField.getPath(), fieldClass.getName());
    }

    public static String failParseParamSetGeneric(ParamField xmlParamField) {
        return String.format(getString("fail.parse.param.set.generic"), xmlParamField.getPath());
    }

    public static String failParseParamSetContainerNull(ParamField xmlParamField) {
        return String.format(getString("fail.parse.param.set.container.null"), xmlParamField.getPath());
    }

    public static String failParseParamSetContainer(ParamField xmlParamField) {
        return String.format(getString("fail.parse.param.set.container"), xmlParamField.getPath());
    }

    public static String failParseDataSqlType(String tableName, String fieldName) {
        return String.format(getString("fail.parse.data.sql.type"), tableName, fieldName);
    }

    public static String failParseDataSqlTypeUnsupport(String tableName, String fieldName, Integer colSqlType) {
        return String.format(getString("fail.parse.data.sql.type.unsupport"), tableName, fieldName, colSqlType);
    }

    public static String failParseDataFieldDuplicate(String tableName, String fieldName) {
        return String.format(getString("fail.parse.data.field.duplicate"), tableName, fieldName);
    }

    public static String failParseDataFieldUnder(String tableName, String fieldName) {
        return String.format(getString("fail.parse.data.field.under"), tableName, fieldName);
    }

    public static String failParseDataFieldNone(String tableName, String fieldName) {
        return String.format(getString("fail.parse.data.field.none"), tableName, fieldName);
    }

    private static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!'; // $NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Deprecated
    public static String getString(String key, Object... args) {
        return String.format(getString(key), args);
    }
}
