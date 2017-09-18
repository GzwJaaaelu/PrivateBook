package com.jaaaelu.gzw.neteasy.model;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.UpdateTableMigration;

/**
 * Created by Gzw on 2017/9/18 0018.
 */

@Migration(version = 2, priority = 2, database = AppDatabase.class)
public class UpdateMigration2 extends UpdateTableMigration<Book> {

    /**
     * Creates an update migration.
     *
     * @param table The table to update
     */
    public UpdateMigration2(Class<Book> table) {
        super(table);
        set(Book_Table.customTag.eq("default"));
    }

}
