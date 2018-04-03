package com.depex.odepto.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.depex.odepto.recent.Card;

import java.util.List;



@Dao
public interface CardDao {
    @Query("select *from card")
    List<Card> getAll();

    @Query("select *from card where card_id=:id")
    Card findById(String id);

    @Query("select *from card where list_id=:listid")
    List<Card> getCardsByListId(String listid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Card card);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Card> cards);


    @Update
    void update(Card card);

    @Delete
    void delete(Card card);


}
