package com.depex.odepto;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;



public class SpinnerMoveCardListAdapter extends BaseAdapter {
    List<BoardList> boardLists;
    Activity activity;
    SpinnerMoveCardListAdapter(Activity activity, List<BoardList> boardLists){
        this.boardLists=boardLists;
        this.activity=activity;
    }

    @Override
    public int getCount() {
        return boardLists.size();
    }

    @Override
    public Object getItem(int i) {
        return boardLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1=  activity.getLayoutInflater().inflate(R.layout.spinner_move_card_list_layout, null, false);
        BoardList boardList =boardLists.get(i);
        String title=boardList.getTitle();
        TextView view2=view1.findViewById(R.id.spinner_list_text_view);
        view2.setText(title);
        return view1;
    }
}