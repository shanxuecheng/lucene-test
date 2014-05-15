package com.sxc.lucene.searching;

import java.io.File;

import junit.framework.TestCase;

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
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class NearRealTimeTest extends TestCase {
	public void testNearRealTime() throws Exception {

		Directory directory = FSDirectory.open(new File(
				"D:/programming/lucene/NearRealTimeTest"));

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
				new SmartChineseAnalyzer(Version.LUCENE_47));
		IndexWriter writer = new IndexWriter(directory, config);
		for (int i = 0; i < 10; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", "" + i, Field.Store.NO));
			doc.add(new TextField("text", "aaa", Field.Store.NO));
			writer.addDocument(doc);
		}

		IndexReader reader = DirectoryReader.open(writer, true);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new TermQuery(new Term("text", "aaa"));
		TopDocs docs = searcher.search(query, 1);
		assertEquals(10, docs.totalHits);

		writer.deleteDocuments(new Term("id", "7"));
		Document doc = new Document();
		doc.add(new StringField("id", "11", Field.Store.NO));
		doc.add(new TextField("text", "bbb", Field.Store.NO));
		writer.addDocument(doc);

		IndexReader newReader = DirectoryReader.open(writer, true);
		assertFalse(reader == newReader);
		reader.close();
		searcher = new IndexSearcher(newReader);

		TopDocs hits = searcher.search(query, 10);
		assertEquals(9, hits.totalHits);

		query = new TermQuery(new Term("text", "bbb"));
		hits = searcher.search(query, 1);
		assertEquals(1, hits.totalHits);
		newReader.close();
		writer.close();
	}
}
