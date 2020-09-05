package xyz.noark.xml;

import xyz.noark.core.util.StringUtils;

import java.util.*;

/**
 * XML节点
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.4
 */
class XmlNode {
    /**
     * 节点名称
     */
    private final String name;
    /**
     * 节点属性
     */
    private final Map<String, String> attributes = new HashMap<>();
    /**
     * 父节点
     */
    private final XmlNode parentNode;
    /**
     * 子节点列表
     */
    private final LinkedHashMap<String, List<XmlNode>> childNodeMap = new LinkedHashMap<>();

    XmlNode(String name, XmlNode parentNode) {
        this.name = name;
        this.parentNode = parentNode;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public XmlNode getParentNode() {
        return parentNode;
    }

    public LinkedHashMap<String, List<XmlNode>> getChildNodeMap() {
        return childNodeMap;
    }

    @Override
    public String toString() {
        return "XmlNode{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", parentNode=" + parentNode +
                ", childNodeMap=" + childNodeMap +
                '}';
    }


    public String getAttributesValue(String name) {
        // 配置Key=多个节点+1个属性
        XmlNode curNode = this;
        String[] array = StringUtils.split(name, ".");
        for (int i = 0, len = array.length - 1; i < len; i++) {
            String nodeName = array[i];
            if (curNode.getName().equals(nodeName)) {
                continue;
            }
            List<XmlNode> nodeList = curNode.getChildNodeMap().getOrDefault(nodeName, Collections.emptyList());
            if (nodeList.size() != 1) {
                // 多节点就不科学了...
            }
            curNode = nodeList.get(0);
        }
        return curNode.getAttributes().get(array[array.length - 1]);
    }

    public List<XmlNode> getNodeList(String name) {
        return getNodeList(StringUtils.split(name, "."), 0);
    }

    private List<XmlNode> getNodeList(String[] nodeNameArray, int index) {
        // 子节点中的名称
        String nodeName = nodeNameArray[index];
        List<XmlNode> xmlNodeList = childNodeMap.getOrDefault(nodeName, Collections.emptyList());
        if (nodeNameArray.length == index + 1) {
            return xmlNodeList;
        }
        // 向下一层继续查找
        List<XmlNode> result = new LinkedList<>();
        for (XmlNode xmlNode : xmlNodeList) {
            result.addAll(xmlNode.getNodeList(nodeNameArray, index + 1));
        }
        return result;
    }

    public void fillExpression() {
    }
}
