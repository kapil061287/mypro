package com.depex.odepto.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.depex.odepto.dao.CardDao;
import com.depex.odepto.recent.Card;


@Database(entities = {Card.class}, version = 1)
public abstract class OdaptoDatabase  extends RoomDatabase{
    public abstract CardDao cardDao();
}
