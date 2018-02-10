package com.depex.odepto;

import android.arch.persistence.room.Insert;

/**
 * Created by we on 12/29/2017.
 */

public interface OdaptoDAO {

    @Insert
    public void insertUser(Board board);
}
