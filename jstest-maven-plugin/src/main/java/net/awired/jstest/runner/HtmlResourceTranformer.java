package net.awired.jstest.runner;

import java.io.File;
import java.util.Map;

public class HtmlResourceTranformer {

    public String buildTagsFromResources(Map<String, File> resources) {
        StringBuilder res = new StringBuilder();
        for (String key : resources.keySet()) {
        	if (!key.startsWith("/src/yui") && !key.endsWith("/UseYUI.js")) {
        		appendTag(res, key);
        	}
        	if (key.endsWith("/yui-min.js")){
        		appendTag(res, key);
        	}
        }
        return res.toString();
    }

    public void appendTag(StringBuilder builder, String path) {
        if (path.endsWith(".js")) {
            builder.append("<script type=\"text/javascript\" src=\"");
            builder.append(path);
            builder.append("\"></script>\n");
        } else if (path.endsWith(".css")) {
            builder.append("<link href=\"");
            builder.append(path);
            builder.append("\" rel=\"stylesheet\" type=\"text/css\">\n");
        }
    }
}
