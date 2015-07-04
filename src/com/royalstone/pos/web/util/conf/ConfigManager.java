package com.royalstone.pos.web.util.conf;
import java.io.IOException;

/**
 * User: fire
 * Date: 2005-3-21 || 
 */
/**
 * 提供访问配置文件的接口
 * 配置文件可以是 XML 格式或者 Properties 文件格式
 * 配置文件的属性名称是如下格式：root.child.property
 * 在 XML 里为：<root><child><property>value</property></child></root>
 * 在 Propetties 里为 root.child.property=value
 */
public interface ConfigManager {

        /**
     * 返回指定名称的属性值
     * 属性名称格式为：root.child.property
     * @param name - 属性名称
     * @return String 属性值
     */
    public String getPropertyValue(String name) throws IOException;


}
