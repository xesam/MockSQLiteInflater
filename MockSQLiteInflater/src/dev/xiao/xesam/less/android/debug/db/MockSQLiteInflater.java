package dev.xiao.xesam.less.android.debug.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xe on 14-7-18.
 */
public class MockSQLiteInflater {

    private List<MockTable> tables = new ArrayList<MockTable>();

    public MockSQLiteInflater() {

    }

    private boolean isTableExists(SQLiteDatabase inDb, MockTable inMockTable) throws Exception {
        Cursor c = inDb.rawQuery("select count(*) from sqlite_master where type='table' and name=?", new String[]{inMockTable.getTableName()});
        boolean result = false;
        try {
            while (c.moveToNext()) {
                if (c.getInt(0) > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            c.close();
        }
        return result;
    }

    public MockSQLiteInflater addTable(MockTable inMockTable) {
        tables.add(inMockTable);
        return this;
    }

    public SQLiteDatabase inflateTo(Context inContext, String inDbName) {
        return innerInflateTo(inContext, inDbName, false);
    }

    public SQLiteDatabase reInflateTo(Context inContext, String inDbName) {
        return innerInflateTo(inContext, inDbName, true);
    }

    public SQLiteDatabase inflateTo(Context inContext, SQLiteDatabase inDb) {
        return innerInflateTo(inContext, inDb, false);
    }

    public SQLiteDatabase reInflateTo(Context inContext, SQLiteDatabase inDb) {
        return innerInflateTo(inContext, inDb, true);
    }

    private SQLiteDatabase innerInflateTo(Context inContext, String inDbName, boolean inDrop) {
        SQLiteDatabase db = inContext.openOrCreateDatabase(inDbName, Context.MODE_PRIVATE, null);
        return innerInflateTo(inContext, db, inDrop);
    }

    private SQLiteDatabase innerInflateTo(Context inContext, SQLiteDatabase inDb, boolean inDrop) {
        inDb.beginTransaction();
        try {
            for (MockTable table : tables) {
                if (inDrop) {
                    table.reInflateTo(inContext, inDb);
                } else {
                    table.inflateTo(inContext, inDb);
                }
            }
            inDb.setTransactionSuccessful();
        } finally {
            inDb.endTransaction();
        }
        return inDb;
    }

}
