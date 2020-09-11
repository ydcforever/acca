package com.fate.file.parse.batch;

import java.util.List;

public interface BatchInsertDB<T> {

	public boolean doWith(String tableName, List<T> list);
	
}
