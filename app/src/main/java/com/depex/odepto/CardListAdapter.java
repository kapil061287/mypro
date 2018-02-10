package com.depex.odepto;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;



public class CardListAdapter extends BaseAdapter {


    List<BoardCard> cards;
    public CardListAdapter(Context context, List<BoardCard> cards){
        this.cards=cards;
    }


    @Override
    public int getCount() {
        return cards.size();
    }



    @Override
    public Object getItem(int i) {
        return cards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return cards.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
