package com.depex.odepto;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;


public class CardLabelViewAdapter extends RecyclerView.Adapter<CardLabelViewAdapter.CardLabelViewholder>{
    List<Label> labelButtons;
    Activity activity;

    public CardLabelViewAdapter(List<Label> labelButtons, Activity activity ){
        this.labelButtons=labelButtons;
        this.activity=activity;

    }

    @Override
    public CardLabelViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=activity.getLayoutInflater().inflate(R.layout.card_label_layout_list_for_recyclerview, null, false);
        return new CardLabelViewholder(view);
    }

    @Override
    public void onBindViewHolder(final CardLabelViewholder holder, int position) {
        Label cardLabelButton=labelButtons.get(position);
        GradientDrawable drawable=(GradientDrawable) holder.card_label_color_button.getBackground();
        drawable.setColor(Color.parseColor(cardLabelButton.getColor()));
        //holder.card_label_color_button.setBackgroundColor(Color.parseColor(cardLabelButton.getColor()));
    }

    @Override
    public int getItemCount() {
        return labelButtons.size();
    }

    class CardLabelViewholder extends RecyclerView.ViewHolder {
        View labelView;
        Button card_label_color_button;
        public CardLabelViewholder(View labelView) {
            super(labelView);
            this.labelView=labelView;
            card_label_color_button=labelView.findViewById(R.id.card_label_color_button);
        }
    }
}