package com.depex.odepto.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.depex.odepto.OdaptoCardView;
import com.depex.odepto.recent.Card;

import java.util.List;



public class CardListAdapter extends BaseAdapter {


    List<Card> cards;
    Context context;
    public CardListAdapter(Context context, List<Card> cards){
        this.cards=cards;
        this.context=context;
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
        return OdaptoCardView.renderForAdapter(context, i, view, viewGroup, cards.get(i), this);
    }
}