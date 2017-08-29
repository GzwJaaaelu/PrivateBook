package com.jaaaelu.gzw.neteasy.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Gzw on 2017/8/26 0026.
 */

@Table(database = AppDatabase.class)
public class HistorySearchInfo extends BaseModel {

    @PrimaryKey
    private String keyWord;
    @Column
    private long searchTime;
    @Column
    private int searchTimes;

    public HistorySearchInfo() {
    }

    public HistorySearchInfo(String bookName, long searchTime, int searchTimes) {
        this.keyWord = bookName;
        this.searchTime = searchTime;
        this.searchTimes = searchTimes;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    public int getSearchTimes() {
        return searchTimes;
    }

    public void setSearchTimes(int searchTimes) {
        this.searchTimes = searchTimes;
    }
}


