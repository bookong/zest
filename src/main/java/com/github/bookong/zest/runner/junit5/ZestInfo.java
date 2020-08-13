package com.github.bookong.zest.runner.junit5;

import com.github.bookong.zest.core.testcase.ZestTestParam;

import java.io.File;

/**
 * @author Jiang Xu
 */
public class ZestInfo<T extends ZestTestParam> {

    private String testCaseFileName;
    private String testCaseFilePath;
    private T      testParam;

    public ZestInfo(String testCaseFilePath){
        this.testCaseFilePath = testCaseFilePath;
        this.testCaseFileName = testCaseFilePath.substring(testCaseFilePath.lastIndexOf(File.separator) + 1);
    }

    public String getName() {
        return String.format("[%s]", testCaseFileName);
    }

    public T getTestParam() {
        return testParam;
    }

    public void setTestParam(T testParam) {
        this.testParam = testParam;
    }
}
