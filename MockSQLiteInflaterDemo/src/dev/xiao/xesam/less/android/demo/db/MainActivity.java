package dev.xiao.xesam.less.android.demo.db;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.widget.TextView;
import dev.xiao.xesam.less.android.debug.db.MockSQLiteInflater;
import dev.xiao.xesam.less.android.debug.db.MockTable;

public class MainActivity extends Activity {

	public String aliasTableName = "test";
	public String dbName = "mock.db";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MockSQLiteInflater sqLiteInflater = new MockSQLiteInflater();
		SQLiteDatabase db2 = sqLiteInflater
					.addTable(MockTable.newAssetTable("mock_table.csv",aliasTableName))
					.reInflateTo(this, dbName);

		SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
		sqb.setTables(aliasTableName);
		Cursor c = sqb.query(db2, null, null, null, null, null, null);
		try {
			StringBuilder sb = new StringBuilder();
			while (c.moveToNext()) {
				sb.append(c.getString(0)).append(",").append(c.getInt(1)).append(",").append(c.getString(2)).append("\n");
			}
			((TextView) findViewById(R.id.demo_output)).setText(sb.toString());
		} finally {
			c.close();
		}
		db2.close();

	}
}
