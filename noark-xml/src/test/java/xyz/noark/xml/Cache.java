package xyz.noark.xml;

import xyz.noark.core.annotation.tpl.TplAttr;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class Cache {
    @TplAttr(name = "redis.type")
    private int type;
    @TplAttr(name = "redis.ip")
    private String ip;
    @TplAttr(name = "redis.port")
    private int port;
    @TplAttr(name = "redis.index")
    private int index;
    @TplAttr(name = "redis.password")
    private String password;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Cache{" +
                "type=" + type +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", index=" + index +
                ", password='" + password + '\'' +
                '}';
    }
}
