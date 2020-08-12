package com.github.bookong.zest.runner.junit5;

import java.io.File;

/**
 * @author Jiang Xu
 */
public class ZestInfo {

    private String testMethodName;
    private String testCaseFileName;
    private String testCaseFilePath;

    public ZestInfo(String testMethodName, String testCaseFilePath){
        this.testMethodName = testMethodName;
        this.testCaseFilePath = testCaseFilePath;
        this.testCaseFileName = testCaseFilePath.substring(testCaseFilePath.lastIndexOf(File.separator) + 1);
    }

    public String getName() {
        return String.format("[%s]", testCaseFileName);
    }
}
