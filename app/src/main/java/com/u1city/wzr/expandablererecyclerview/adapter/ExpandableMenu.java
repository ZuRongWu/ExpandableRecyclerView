package com.u1city.wzr.expandablererecyclerview.adapter;

import java.util.List;

/**
 * 描述可以展开菜单
 * Created by user on 2017/1/13.
 */
public class ExpandableMenu {
    //相对于父菜单的位置
    private int mPosition;
    //子菜单列表
    private List<ExpandableMenu> mChildren;
    //父菜单
    private ExpandableMenu mParent;
    //标识菜单是否展开
    private boolean mExpanded;
    //标识是否始终展开
    private boolean mAlwaysExpanded;
    //关联的数据
    private Object mData;

    public void setData(Object d){
        mData = d;
    }

    public Object getData(){
        return mData;
    }

    public void setPosition(int pos) {
        mPosition = pos;
    }

    public int getPositon() {
        return mPosition;
    }

    public void setChildren(List<ExpandableMenu> children) {
        mChildren = children;
    }

    public List<ExpandableMenu> getChildren() {
        return mChildren;
    }

    public void setParent(ExpandableMenu parent) {
        mParent = parent;
    }

    public ExpandableMenu getParent() {
        return mParent;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    public boolean isAlwaysExpanded() {
        return mAlwaysExpanded;
    }

    public void setAlwaysExpanded(boolean alwaysExpanded) {
        ExpandableMenu m = this;
        while (m != null){
            m.setAlwaysExpandedInternal(alwaysExpanded);
            m = m.getParent();
        }
    }

    private void setAlwaysExpandedInternal(boolean alwaysExpanded){
        this.mAlwaysExpanded = alwaysExpanded;
    }

    public boolean hasChildren() {
        return mChildren != null&&mChildren.size() > 0;
    }

    public int calculateAllChildrenCount(ExpandableMenu parent){
        if(!parent.hasChildren()){
            return 0;
        }
        int count = parent.getChildren().size();
        for(ExpandableMenu c:parent.getChildren()){
            count += calculateAllChildrenCount(c);
        }
        return count;
    }

    public int calculateAllVisibleChildrenCount(ExpandableMenu parent){
        if(!parent.isExpanded()||!parent.hasChildren()){
            return 0;
        }
        int count = parent.getChildren().size();
        for(ExpandableMenu c:parent.getChildren()){
            count += calculateAllVisibleChildrenCount(c);
        }
        return count;
    }

    /**
     * 计算菜单的深度，没有父菜单时深度为0，有一级父菜单时深度为1，以此类推
     * @return 菜单项的深度
     */
    public int getDepth(){
        int d = 0;
        ExpandableMenu m = this;
        while (m.getParent() != null){
            d += 1;
        }
        return d;
    }
}
