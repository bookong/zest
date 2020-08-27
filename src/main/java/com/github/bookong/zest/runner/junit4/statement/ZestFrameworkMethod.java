package com.github.bookong.zest.runner.junit4.statement;

import java.io.File;

import org.junit.runners.model.FrameworkMethod;

/**
 * @author Jiang Xu
 */
public class ZestFrameworkMethod extends FrameworkMethod {

    private String filePath;
    private String fileName;

    public ZestFrameworkMethod(FrameworkMethod method, String filePath){
        super(method.getMethod());
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    @Override
    public String getName() {
        return String.format("%s [%s] ", super.getName(), fileName);
    }

    public String getFilePath() {
        return filePath;
    }
}
