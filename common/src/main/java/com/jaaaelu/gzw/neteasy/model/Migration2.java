package com.jaaaelu.gzw.neteasy.model;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by Gzw on 2017/9/15 0015.
 */

@Migration(version = 2, database = AppDatabase.class)
public class Migration2 extends AlterTableMigration<Book> {

    public Migration2(Class<Book> table) {
        super(table);
    }

    @Override
    public void onPreMigrate() {
        addColumn(SQLiteType.TEXT, "customTag");
        addColumn(SQLiteType.INTEGER, "readState");
    }
}