package com.sxc.lucene.searching;

/**
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan      
 */

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

// From chapter 3
public class TermRangeQueryTest extends TestCase {
	public void testTermRangeQuery() throws Exception {
		String indexDir = "D:/programming/lucene/indexingTest";
		Directory dir = FSDirectory.open(new File(indexDir));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TermRangeQuery query = TermRangeQuery.newStringRange("id", "1", "4", true, true);

		TopDocs matches = searcher.search(query, 100);

		assertEquals(2, matches.totalHits);
		reader.close();
		dir.close();
	}
}
