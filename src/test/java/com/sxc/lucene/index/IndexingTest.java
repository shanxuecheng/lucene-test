package com.sxc.lucene.index;

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
import java.io.IOException;

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

// From chapter 2
public class IndexingTest extends TestCase {
	protected String[] ids = { "1", "2", "3", "4" };
	protected String[] unindexed = { "Netherlands", "Italy", "中国1", "中国2" };
	protected String[] unstored = { "Amsterdam has lots of bridges",
			"Venice has lots of canals", 
			"北京有很多人，如皋很少",
			"如皋很美"};
	protected String[] text = { "Amsterdam", "Venice", "北京市", "如皋市" };

	private Directory directory;

	protected void setUp() throws Exception { // 1
		directory = FSDirectory.open(new File(
				"D:/programming/lucene/indexingTest"));

		IndexWriter writer = getWriter(); // 2
		writer.deleteAll();

		for (int i = 0; i < ids.length; i++) { // 3
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("country", unindexed[i], Field.Store.YES));
			doc.add(new TextField("contents", unstored[i], Field.Store.NO));
			doc.add(new TextField("city", text[i], Field.Store.YES));

			writer.addDocument(doc);
		}
		writer.close();
	}

	private IndexWriter getWriter() throws IOException { // 2
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
				new SmartChineseAnalyzer(Version.LUCENE_47));
		return new IndexWriter(directory, config); // 2

	}

	protected int getHitCount(String fieldName, String searchString)
			throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader); // 4
		Term t = new Term(fieldName, searchString);
		Query query = new TermQuery(t); // 5
		int hitCount = TestUtil.hitCount(searcher, query); // 6
		reader.close();
		return hitCount;
	}

	public void testIndexWriter() throws IOException {
		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs()); // 7
		writer.close();
	}

	public void testIndexReader() throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
		assertEquals(ids.length, reader.maxDoc()); // 8
		assertEquals(ids.length, reader.numDocs()); // 8
		reader.close();
	}

	public void testHitCount() throws IOException {
		assertEquals(1, getHitCount("country", "中国"));
		assertEquals(1, getHitCount("city", "北京市"));
	}

	public void testDeleteBeforeOptimize() throws IOException {
		IndexWriter writer = getWriter();
		assertEquals(2, writer.numDocs()); // A
		writer.deleteDocuments(new Term("id", "1")); // B
		writer.commit();
		assertTrue(writer.hasDeletions()); // 1
		assertEquals(2, writer.maxDoc()); // 2
		assertEquals(1, writer.numDocs()); // 2
		writer.close();
	}

	public void testDeleteAfterOptimize() throws IOException {
		IndexWriter writer = getWriter();
		assertEquals(2, writer.numDocs());
		writer.deleteDocuments(new Term("id", "1"));
		writer.forceMergeDeletes(); // 3
		writer.commit();
		assertFalse(writer.hasDeletions());
		assertEquals(1, writer.maxDoc()); // C
		assertEquals(1, writer.numDocs()); // C
		writer.close();
	}

	public void testUpdate() throws IOException {

		assertEquals(1, getHitCount("city", "amsterdam"));

		IndexWriter writer = getWriter();

		Document doc = new Document(); // A
		doc.add(new StringField("id", "1", Field.Store.YES)); // A
		doc.add(new TextField("country", "Netherlands", Field.Store.YES)); // A
		doc.add(new TextField("contents", "Den Haag has a lot of museums",
				Field.Store.NO)); // A
		doc.add(new TextField("city", "Den Haag", Field.Store.YES)); // A

		writer.updateDocument(new Term("id", "1"), // B
				doc); // B
		writer.close();

		assertEquals(0, getHitCount("city", "amsterdam"));// C
		assertEquals(1, getHitCount("city", "haag")); // D
	}
}
