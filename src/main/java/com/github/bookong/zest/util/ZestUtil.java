package com.github.bookong.zest.util;

import com.github.bookong.zest.common.ZestGlobalConstant;
import com.github.bookong.zest.exception.ZestException;
import com.github.bookong.zest.runner.ZestWorker;
import com.github.bookong.zest.testcase.ZestData;
import org.apache.commons.lang3.StringUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jiang Xu
 */
public class ZestUtil {

    /**
     * 从绝对路径加载 xml 文件
     */
    public static void loadZestData(ZestWorker worker, ZestData zestData) {
        try {
            File file = new File(zestData.getFilePath());
            if (!file.exists()) {
                throw new ZestException(Messages.fileNotFound(zestData.getFilePath()));
            }

            zestData.setFileName(file.getName());
            zestData.setFilePath(file.getAbsolutePath());
            zestData.load(worker);

        } catch (Exception e) {
            throw new ZestException(Messages.parse(zestData.getFilePath()), e);
        }
    }

    public static Set<String> parsePathsFromJson(String content) {
        Set<String> paths = new LinkedHashSet<>();
        Map map = ZestJsonUtil.fromJson(content, Map.class);
        for (Object key : map.keySet()) {
            parsePathsFromJson(paths, StringUtils.EMPTY, String.valueOf(key), map.get(key));
        }
        return paths;
    }

    private static void parsePathsFromJson(Set<String> paths, String parentPath, String subPath, Object obj) {
        String path = ZestUtil.getPath(parentPath, subPath);
        if (obj == null) {
            paths.add(path);
        } else if (obj instanceof Map) {
            Map map = (Map) obj;
            for (Object key : map.keySet()) {
                parsePathsFromJson(paths, path, String.valueOf(key), map.get(key));
            }
        } else if (obj instanceof List) {
            List list = (List) obj;
            for (Object item : list) {
                parsePathsFromJson(paths, path, StringUtils.EMPTY, item);
            }
        } else {
            paths.add(path);
        }
    }

    public static String getDir(Class<?> testObjClass, String methodName) {
        return testObjClass.getResource(StringUtils.EMPTY).getPath() //
                           .concat(ZestGlobalConstant.FIX_SUB_DIR).concat(File.separator) //
                           .concat(testObjClass.getSimpleName()).concat(File.separator) //
                           .concat(methodName).concat(File.separator);
    }

    public static String getDir(TestClass testCase, FrameworkMethod frameworkMethod) {
        return getDir(testCase.getJavaClass(), frameworkMethod.getName());
    }

    public static String getPath(String parentPath, String subPath) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(parentPath)) {
            sb.append(parentPath);
        }
        if (StringUtils.isNotBlank(subPath)) {
            if (sb.length() > 0) {
                sb.append(ZestGlobalConstant.PATH_SEPARATOR);
            }
            sb.append(subPath);
        }
        return sb.toString();
    }
}
