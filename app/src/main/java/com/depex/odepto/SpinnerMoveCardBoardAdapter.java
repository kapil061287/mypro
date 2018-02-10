package com.depex.odepto;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by we on 12/28/2017.
 */

public class SpinnerMoveCardBoardAdapter extends BaseAdapter {
    List<Board> boards;
    Activity activity;
    SpinnerMoveCardBoardAdapter(Activity activity, List<Board> boards){
        this.activity=activity;
        this.boards=boards;
    }
    @Override
    public int getCount() {
        return boards.size();
    }

    @Override
    public Object getItem(int i) {
        return boards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1=LayoutInflater.from(activity).inflate(R.layout.spinner_move_card_board_layout,null, false);
        TextView textView=view1.findViewById(R.id.spinner_board_text_view);
        Board board=boards.get(i);
        String title=board.getBoardTitle();
        textView.setText(title);
        return view1;
    }
}