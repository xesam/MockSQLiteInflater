package dev.xiao.xesam.less.android.debug.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xe on 14-7-18.
 */
public class MockTable {

    public static final int TABLE_FROM_ASSET = 0;
    public static final int TABLE_FROM_FILE = 1;

    public static String ITEM_SEPARATOR = ",";
    public static String COMMEM_START = "#";

    private int tableFrom;
    private String filename;
    private String tableName;

    private List<MockScheme> schemes = new ArrayList<MockScheme>();
    private List<String[]> tuples = new ArrayList<String[]>();

    public static MockTable newAssetTable(String inAssetName) {
        return new MockTable(TABLE_FROM_ASSET, inAssetName, clip(inAssetName));
    }

    public static MockTable newAssetTable(String inAssetName, String inTableName) {
        return new MockTable(TABLE_FROM_ASSET, inAssetName, inTableName);
    }

    public static MockTable newFileTable(String inFilename) {
        return new MockTable(TABLE_FROM_FILE, inFilename, clip(inFilename));
    }

    public static MockTable newFileTable(String inFilename, String inTableName) {
        return new MockTable(TABLE_FROM_FILE, inFilename, inTableName);
    }

    private static String clip(String inFilename) {
        String[] pathes = inFilename.split(File.separator);
        String inTableName = inFilename;
        if (1 < pathes.length) {
            inTableName = pathes[pathes.length - 1];
        }
        int dotIndex = inTableName.indexOf(".");
        String defaultTableName = inTableName.substring(0, dotIndex == -1 ? inFilename.length() + 1 : dotIndex);
        return defaultTableName;
    }

    private MockTable(int inTableFrom, String inFilename, String inTableName) {
        tableFrom = inTableFrom;
        filename = inFilename;
        tableName = inTableName;
    }

    public String getTableName() {
        return tableName;
    }

    public MockTable create(SQLiteDatabase db) {
        db.execSQL(genTableCreateSql());
        return this;
    }

    public MockTable drop(SQLiteDatabase db) {
        db.execSQL(genTableDropSql());
        return this;
    }

    public MockTable insertAll(SQLiteDatabase db) {
        for (String insertion : genTupleInsertSql()) {
            db.execSQL(insertion);
        }
        return this;
    }

    private InputStream loadTable() {
        InputStream is = null;
        try {
            is = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return is;
    }

    private InputStream loadTableFromAsset(Context inContext) {
        InputStream is = null;
        try {
            is = inContext.getAssets().open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    public void inflateTo(Context inContext, SQLiteDatabase db) {
        preInflate(inContext);
        create(db);
        insertAll(db);
    }

    public void reInflateTo(Context inContext, SQLiteDatabase db) {
        preInflate(inContext);
        drop(db);
        create(db);
        insertAll(db);
    }

    private void preInflate(Context inContext) {
        InputStream is;
        if (tableFrom == TABLE_FROM_ASSET) {
            is = loadTableFromAsset(inContext);
        } else {
            is = loadTable();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        int lineCount = 0;
        try {
            while (null != (line = br.readLine())) {
                line = line.trim();
                if (line.startsWith(COMMEM_START)) {
                    continue;
                }
                String[] items = line.split(ITEM_SEPARATOR);
                if (0 == lineCount) {
                    parseSchemes(items);
                } else {
                    parseTuples(items);
                }
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<MockScheme> parseSchemes(String[] inItems) {
        for (String item : inItems) {
            schemes.add(checkScheme(item));
        }
        return schemes;
    }

    public static MockScheme checkScheme(String inItem) {
        String[] desc = inItem.trim().split("#");
        if (desc.length > 2) {
            throw new RuntimeException();
        }
        if (desc.length == 2) {
            return new MockScheme(desc[0], desc[1]);
        } else {
            return new MockScheme(desc[0]);
        }
    }

    private List<String[]> parseTuples(String[] inItems) {
        checkTuple(inItems);
        String[] copy = new String[inItems.length];
        for (int i = 0, size = inItems.length; i < size; ++i) {
            copy[i] = inItems[i];
        }
        tuples.add(copy);
        return tuples;
    }

    private void checkTuple(String[] inItems) {
        if (schemes.size() != inItems.length) {
            throw new RuntimeException("length not match");
        }
    }

    private String genTableCreateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ").append(tableName).append("(");
        for (MockScheme item : schemes) {
            sb.append(item.name + " " + item.type).append(",");
        }
        sb.deleteCharAt(sb.length() - 1).append(")");
        return sb.toString();
    }

    public String genTableDropSql() {
        return String.format("drop table if exists %s", tableName);
    }

    private List<String> insertSql = new ArrayList<String>();

    public List<String> genTupleInsertSql() {
        for (String[] tuple : tuples) {
            insertSql.add(genInsertion(tuple));
        }
        return insertSql;
    }

    private String genInsertion(String[] tuple) {
        return new StringBuilder().append("insert into ").append(tableName).append(" values(").append(joinStr(tuple, ",")).append(")").toString();
    }

    private String joinStr(String[] items, String sep) {
        StringBuilder sb = new StringBuilder();
        int len = items.length;
        for (int i = 0; i < len; ++i) {
            sb.append("'").append(items[i]).append("'");
            if (i != len - 1) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }
}
