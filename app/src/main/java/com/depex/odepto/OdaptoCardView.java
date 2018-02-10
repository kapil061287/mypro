package com.depex.odepto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.depex.odepto.recent.Card;
import com.depex.odepto.view.LabelsView;

public class OdaptoCardView extends OCardView{
    private Card card;
    private Context context;
    private View cardContainer;
    private ImageView cardCoverImage;
    private LabelsView labelsView;
    private TextView cardNameTextView;
    private Adapter parentBaseAdapter;


    public void setParentBaseAdapter(Adapter parentBaseAdapter) {
        this.parentBaseAdapter = parentBaseAdapter;
    }

    public OdaptoCardView(@NonNull Context context) {
        this(context , null);
        this.context=context;
    }

    public OdaptoCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
        this.context=context;
    }

    public OdaptoCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        cardContainer=findViewById(R.id.card_odpato);
        cardCoverImage=cardContainer.findViewById(R.id.card_cover);
        labelsView=cardContainer.findViewById(R.id.labels_view_odpatocard);
        cardNameTextView=cardContainer.findViewById(R.id.card_text);
    }

    public static View renderForAdapter(Context context, final int position, View convertView, ViewGroup parent, Card card, final Adapter adapter){
                View view=null;
                if(convertView==null){
                    view= LayoutInflater.from(context).inflate(R.layout.card_item_layout, parent, false);

                }else{
                    view=convertView;
                }

                final OdaptoCardView odaptoCardView=view.findViewById(R.id.card_odpato);
                if(parent instanceof ListView){
                    final ListView listView= (ListView) parent;
                    final AdapterView.OnItemClickListener onClickListener=listView.getOnItemClickListener();
                    if(onClickListener!=null){
                        View finalView = view;
                       odaptoCardView.setOnClickListener(new OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               onClickListener.onItemClick(listView, finalView, position, adapter.getItemId(position));
                           }
                       });
                    }

                    AdapterView.OnItemLongClickListener longClickListener=listView.getOnItemLongClickListener();
                    if(longClickListener!=null){
                        View finalView1 = view;
                       odaptoCardView.setOnLongClickListener(new OnLongClickListener() {
                           @Override
                           public boolean onLongClick(View view) {
                               return longClickListener.onItemLongClick(listView, finalView1, position, adapter.getItemId(position));
                           }
                       });

                    }
                }


            odaptoCardView.setParentBaseAdapter(adapter instanceof BaseAdapter ? (BaseAdapter) adapter : null);
            return view;
    }


}