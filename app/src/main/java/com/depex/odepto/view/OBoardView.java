package com.depex.odepto.view;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import com.depex.odepto.BoardList;
import com.depex.odepto.R;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragItemRecyclerView;

import java.util.HashMap;
import java.util.Map;

public class OBoardView extends BoardView {

    Context context;
    Map<Integer, BoardList> mColumnMap=new HashMap<>();

    public OBoardView(Context context) {
        super(context);
        this.context=context;
    }

    public OBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public OBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    public DragItemRecyclerView addColumnList(DragItemAdapter adapter, View header, View footer, BoardList boardList, boolean hasFixedItemSize) {
        DragItemRecyclerView recyclerView= super.addColumnList(adapter, header, hasFixedItemSize);
        ViewParent parent=recyclerView.getParent();
        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        layoutParams.weight=1;
        layoutParams.height=0;
        recyclerView.setLayoutParams(layoutParams);
        if(parent instanceof LinearLayout){
            LinearLayout layout= (LinearLayout) parent;
                LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) layout.getLayoutParams();
                params.setMargins(30, 50, 30, 50);
                params.height= LinearLayout.LayoutParams.WRAP_CONTENT;
                params.width=getResources().getDimensionPixelSize(R.dimen.list_width);
                layout.setLayoutParams(params);
                if(footer!=null)
                layout.addView(footer);
                mColumnMap.put(Math.max(getColumnCount()-1,0),boardList);
                recyclerView.setBackgroundColor(Color.parseColor("#44FFFFFF"));
        }
        return recyclerView;
    }

    public Map<Integer, BoardList> getColumnMap() {
        return mColumnMap;
    }

    public void setColumnMap(Map<Integer, BoardList> mColumnMap) {
        this.mColumnMap = mColumnMap;
    }
}