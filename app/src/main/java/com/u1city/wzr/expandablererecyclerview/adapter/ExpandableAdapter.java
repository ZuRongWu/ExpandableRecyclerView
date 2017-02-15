package com.u1city.wzr.expandablererecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 可以无限扩展的recyclerView
 * 实现无限极菜单，继承{@link #onBindMenuData(RecyclerView.ViewHolder, ExpandableMenu)}用于绑定
 * 菜单项数据；{@link #onCreateMenuView(ViewGroup, int)}用于创建菜单视图；{@link #getMenuViewType(ExpandableMenu)}
 * 返回菜单视图的类型，该值会传给{@link #onCreateMenuView(ViewGroup, int)}，可以为不同菜单视图类型创建
 * 不同的视图
 *
 * tip:菜单的刷新是用递归实现的，如果菜单的层级太多可能导致性能问题
 *
 * Created by wuzr on 2017/1/12.
 */
public abstract class ExpandableAdapter extends RecyclerView.Adapter {
    //所有菜单集合
    private List<ExpandableMenu> mExpandableMenus;
    //当前显示的所有菜单
    private List<ExpandableMenu> mShowExpandableMenus;
    private List<OnExpandableMenuStateChangeListener> mListeners = new ArrayList<>();

    /**
     * 菜单状态变化接口，在需要的地方
     */
    public interface OnExpandableMenuStateChangeListener{
        /**
         * 关闭子菜单
         * @param menu 对应的菜单
         */
        void onMenuClose(ExpandableMenu menu);

        /**
         * 展开子菜单
         * @param menu 对应的菜单
         */
        void onMenuExpand(ExpandableMenu menu);
    }

    public ExpandableAdapter(){
        mExpandableMenus = onCreateExpandableMenu();
        if(mExpandableMenus == null){
            mExpandableMenus = new ArrayList<>();
        }
        mShowExpandableMenus = generateShowExpandableMenus(mExpandableMenus);
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateMenuView(parent,viewType);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpandableMenu menu = mShowExpandableMenus.get(position);
        onBindMenuData(holder,menu);
    }

    @Override
    public final int getItemViewType(int position) {
        ExpandableMenu menu = mShowExpandableMenus.get(position);
        return getMenuViewType(menu);
    }


    /**
     * 实现者应该根据menu返回一个整形值作为菜单视图的类型
     * @param menu 菜单
     * @return menu对应的菜单视图的类型
     */
    public abstract int getMenuViewType(ExpandableMenu menu);

    /**
     * 根据type返回菜单视图
     * @param parent 菜单视图的父类
     * @param type 类型，由{@link #getMenuViewType(ExpandableMenu)}产生
     * @return 菜单视图
     */
    public abstract RecyclerView.ViewHolder onCreateMenuView(ViewGroup parent,int type);

    /**
     * 绑定菜单项数据
     * @param holder 描述菜单视图
     * @param menu 菜单，可以通过{@link ExpandableMenu#getData()}取得菜单项数据，在创建菜单项时应该通过
     *             {@link ExpandableMenu#setData(Object)}来设置数据
     */
    public abstract void onBindMenuData(RecyclerView.ViewHolder holder,ExpandableMenu menu);

    @Override
    public final int getItemCount() {
        return mShowExpandableMenus.size();
    }

    /**
     * 创建菜单，子类必须实现此方法，并且在这里创建菜单。此方法通过{@link #ExpandableAdapter()}中和
     * {@link #notifyUpdateMenu()}方法调用
     * @return 菜单列表
     */
    public abstract List<ExpandableMenu> onCreateExpandableMenu();

    public void addExpandablMenuStateChangeListener(OnExpandableMenuStateChangeListener listener){
        if(listener == null){
            throw new IllegalArgumentException("listener 不可以为null");
        }
        mListeners.add(listener);
    }

    /**
     * 生成此时应该显示的菜单项
     * @param menus 所有菜单由{@link #onCreateExpandableMenu()}返回
     * @return 应该显示的菜单项
     */
    private List<ExpandableMenu> generateShowExpandableMenus(List<ExpandableMenu> menus){
        List<ExpandableMenu> shows = new ArrayList<>();
        for(ExpandableMenu m:menus){
            shows.add(m);
            fillShowExpandableMenus(shows,m);
        }
        return shows;
    }

    /**
     * 递归查找应该显示的菜单项并添加到{@link #mShowExpandableMenus}集合中
     */
    private void fillShowExpandableMenus(List<ExpandableMenu> shows,ExpandableMenu menu){
        if(menu.isAlwaysExpanded()){
            menu.setExpanded(true);
        }
        if(menu.isExpanded()&&menu.hasChildren()){
            for(ExpandableMenu m:menu.getChildren()){
                shows.add(m);
                fillShowExpandableMenus(shows,m);
            }
        }
    }

    /**
     * 更新菜单集合,在需要使用者在需要更新菜单的地方调用，此方法会调用{@link #onCreateExpandableMenu()}
     * 在{@link #onCreateExpandableMenu()}中创建一个新的菜单，利用此方法来更新数据而不是{@link #notifyDataSetChanged()}
     */
    public void notifyUpdateMenu(){
        mExpandableMenus = onCreateExpandableMenu();
        if(mExpandableMenus == null){
            mExpandableMenus = new ArrayList<>();
        }
        mShowExpandableMenus = generateShowExpandableMenus(mExpandableMenus);
        notifyDataSetChanged();
    }

    /**
     * 关闭指定菜单的子菜单
     * @param menu 指定的菜单
     */
    public void notifyCloseMenu(ExpandableMenu menu){
        if(menu == null||!menu.isExpanded()){
            return;
        }
        int index = mShowExpandableMenus.indexOf(menu);
        if(index == -1){
            return;
        }
        ExpandableMenu realMenu = mShowExpandableMenus.get(index);
        realMenu.setExpanded(false);
        int oldCount = mShowExpandableMenus.size();
        mShowExpandableMenus = generateShowExpandableMenus(mExpandableMenus);
        int len = oldCount - mShowExpandableMenus.size();
        notifyItemRangeRemoved(index + 1, len);
        for(OnExpandableMenuStateChangeListener listener:mListeners){
            listener.onMenuClose(menu);
        }
    }

    /**
     * 展开指定菜单的子菜单
     * @param menu 指定的菜单
     */
    public void notifyExpandMenu(ExpandableMenu menu){
        if(menu == null){
            return;
        }
        int index = mShowExpandableMenus.indexOf(menu);
        if(index == -1){
            return;
        }
        ExpandableMenu reaMenu = mShowExpandableMenus.get(index);
        reaMenu.setExpanded(true);
        int oldCount = mShowExpandableMenus.size();
        mShowExpandableMenus = generateShowExpandableMenus(mExpandableMenus);
        int len = mShowExpandableMenus.size() - oldCount;
        notifyItemRangeInserted(index + 1, len);
        for(OnExpandableMenuStateChangeListener listener:mListeners){
            listener.onMenuExpand(menu);
        }
    }
}
