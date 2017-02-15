package com.u1city.wzr.expandablererecyclerview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.u1city.wzr.expandablererecyclerview.adapter.ExpandableAdapter;
import com.u1city.wzr.expandablererecyclerview.adapter.ExpandableMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new MAdapter());
    }

    private class MAdapter extends ExpandableAdapter{

        @Override
        public int getMenuViewType(ExpandableMenu menu) {
            int d = menu.getDepth();
            if(d == 2){
                //二级子菜单
                return 2;
            }else if(d == 1){
                //一级子菜单
                return 1;
            }else{
                //父菜单
                return 0;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateMenuView(ViewGroup parent, int type) {
            TextView tv = new TextView(MainActivity.this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            if(type == 2){
                tv.setBackgroundColor(Color.parseColor("#D5D5D5"));
                tv.setPadding(60, 20, 20, 20);
            }else if(type == 1){
                tv.setBackgroundColor(Color.parseColor("#e5e5e5"));
                tv.setPadding(40,20,20,20);
            }else{
                tv.setBackgroundColor(Color.WHITE);
                tv.setPadding(20,20,20,20);
            }
            return new RecyclerView.ViewHolder(tv) {
            };
        }

        @Override
        public void onBindMenuData(RecyclerView.ViewHolder holder, final ExpandableMenu menu) {
            Data d = (Data) menu.getData();
            TextView tv = (TextView) holder.itemView;
            tv.setText(d.msg);
            if(menu.hasChildren()&&!menu.isAlwaysExpanded()){
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (menu.isExpanded()) {
                            notifyCloseMenu(menu);
                        } else {
                            notifyExpandMenu(menu);
                        }
                    }
                });
            }
        }

        @Override
        public List<ExpandableMenu> onCreateExpandableMenu() {
            List<ExpandableMenu> menus = new ArrayList<>();
            for(int j = 0;j < 10;j++){
                ExpandableMenu menu = new ExpandableMenu();
                menu.setData(new Data("菜单：" + j));
                List<ExpandableMenu> children = new ArrayList<>();
                for(int i = 0;i < 10;i++){
                    ExpandableMenu c = new ExpandableMenu();
                    c.setParent(menu);
                    c.setData(new Data("一级子菜单：" + j + "-" + i));
                    List<ExpandableMenu> children2 = new ArrayList<>();
                    for(int k = 0;k < 10; k++){
                        ExpandableMenu c1 = new ExpandableMenu();
                        c1.setParent(c);
                        c1.setData(new Data("二级子菜单：" + j + "-" + i + "-" + k));
//                        c1.setAlwaysExpanded(true);
                        children2.add(c1);
                    }
                    c.setChildren(children2);
                    children.add(c);
                }
                menu.setChildren(children);
                menus.add(menu);
            }
            return menus;
        }
    }

    private class Data{
        String msg;
        Data(String msg){
            this.msg = msg;
        }
    }

}
