MockSQLiteInflater
==================

[Android] inflate mock data to sqlitedatabase from files
-------
Usage  
> /assets/mock_table.csv:  
-----
> name#string, age#int, course#string  
> xesam_1, 1, desc_A  
xesam_2, 2, desc_B  
xesam_3, 3, desc_C    
xesam_4, 4, desc_D  
xesam_5, 5, desc_E  
xesam_6, 6, desc_F  
xesam_7, 7, desc_G
in Activity  
--------

> MockSQLiteInflater sqLiteInflater = new MockSQLiteInflater();
> SQLiteDatabase db2 = sqLiteInflater
					.addTable(MockTable.newAssetTable("mock_table.csv",aliasTableName))
					.reInflateTo(this, dbName);
					
					
