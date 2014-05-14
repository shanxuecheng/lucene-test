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

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

// From chapter 3
public class BasicSearchingTest extends TestCase {

	private Directory directory;

	@Override
	protected void setUp() throws Exception { // 1
		directory = FSDirectory.open(new File(
				"D:/programming/lucene/indexingTest"));
	}

	public void testTerm() throws Exception {
		IndexReader reader = DirectoryReader.open(directory); // A
		IndexSearcher searcher = new IndexSearcher(reader); // B

		Term t = new Term("country", "中国");
		Query query = new TermQuery(t);
		TopDocs docs = searcher.search(query, 1);
		System.out.println(docs.totalHits);

		reader.close();
		directory.close();
	}

	public void testQueryParser() throws Exception {
		IndexReader reader = DirectoryReader.open(directory); // A
		IndexSearcher searcher = new IndexSearcher(reader); // B
		
		QueryParser parser = new QueryParser(Version.LUCENE_47, "contents",
				new SmartChineseAnalyzer(Version.LUCENE_47));
		
		Query query = parser.parse("北京* OR 如皋*");
		TopDocs docs = searcher.search(query, 10);
		assertEquals(2, docs.totalHits);
		Document d = searcher.doc(docs.scoreDocs[0].doc);
		assertEquals("中国", d.get("country"));
		
		directory.close();
	}

}
