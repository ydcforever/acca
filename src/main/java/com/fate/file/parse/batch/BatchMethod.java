package com.fate.file.parse.batch;

import java.util.List;

public interface BatchMethod<T> {

	void insert(List<T> list) throws Exception;
	
}
