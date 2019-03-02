package io.github.buniaowanfeng.data;

/**
 * Created by caofeng on 16-7-31.
 */
public class VersionInfo {
    public String name;
    public int version;
    public String desc;
    public String url;
    @Override
    public String toString() {
        return "VersionInfo{" +
                "name='" + name + '\'' +
                ", version=" + version +
                ", des='" + desc + '\'' +
                ", url " + url +
                '}';
    }
}
