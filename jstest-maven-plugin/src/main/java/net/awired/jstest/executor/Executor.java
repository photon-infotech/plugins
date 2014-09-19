package net.awired.jstest.executor;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

public interface Executor {
    
    void execute(String url) throws Exception;
    
    void close();

    void setTargetSrcDir(File targetSourceDirectory);
    
    void setReportDir(File reportDir);
    
    void setLog(Log log);

}
