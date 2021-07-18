/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Alexander
 */
public class NodeVO implements Serializable{

    private static final long serialVersionUID = 983752295698779620L;

    private String href;
    private String text;
    private int[] tags;
    private String parent_id;
    private boolean selectable = false;
    private ArrayList<NodeVO> nodes=new ArrayList<>();
    
    public NodeVO() {
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public int[] getTags() {
        return tags;
    }

    public void setTags(int[] tags) {
        this.tags = tags;
    }
    
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<NodeVO> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<NodeVO> nodes) {
        this.nodes = nodes;
    }

    
    
}
