package com.fate.file.parse.batch;

import java.util.List;

public interface BatchInsertDB<T> {

	void doWith(String tableName, List<T> list) throws Exception;
	
}
