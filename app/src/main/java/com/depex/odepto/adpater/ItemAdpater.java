package com.depex.odepto.adpater;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.depex.odepto.GlideApp;
import com.depex.odepto.R;
import com.depex.odepto.listener.OnCardItemClickListener;
import com.depex.odepto.recent.Card;
import com.woxthebox.draglistview.DragItemAdapter;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAdpater extends DragItemAdapter<Card, ItemAdpater.ItemViewHolder> {
    private List<Card> cards;
    private Context context;
    OnCardItemClickListener onCardItemClickListener;
    public ItemAdpater(List<Card> cards, Context context, OnCardItemClickListener onCardItemClickListener){
        this.cards=cards;
        this.context=context;
        this.onCardItemClickListener=onCardItemClickListener;
        setItemList(cards);
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public long getUniqueItemId(int position) {
        return Long.parseLong(cards.get(position).getCardId());
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.board_list_card_layout, parent, false);
        return new ItemViewHolder(view, R.id.parent_card_item_view, true);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Card card=cards.get(position);
        String url=card.getCoverImage();
        String title=card.getTitle();
        holder.itemView.setTag(card);
        holder.cardTitle.setText(title);
        if(url!=null) {
            GlideApp.with(context).load(url).placeholder(R.drawable.splesh).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    holder.cardCover.setImageDrawable(resource);
                    holder.cardCover.setVisibility(View.VISIBLE);
                }
            });
        }else {
            holder.cardCover.setVisibility(View.GONE);
        }
    }


    class ItemViewHolder extends DragItemAdapter.ViewHolder{

        @BindView(R.id.card_cover)
        ImageView cardCover;

        @BindView(R.id.board_card_txt_title)
        TextView cardTitle;



        public ItemViewHolder(View itemView, int handleResId, boolean dragOnLongPress) {
            super(itemView, handleResId, dragOnLongPress);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemClicked(View view) {
                Card card= (Card) view.getTag();
                if(card!=null){
                        onCardItemClickListener.onCardItemClickListener(card);
                }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }



    public Card getItemCard(int position){
        return cards.get(position);
    }

}
