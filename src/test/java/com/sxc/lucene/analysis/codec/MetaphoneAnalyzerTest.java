package com.sxc.lucene.analysis.codec;

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

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.sxc.lucene.analysis.AnalyzerUtils;

// From chapter 4
public class MetaphoneAnalyzerTest extends TestCase {
	public void testKoolKat() throws Exception {
		RAMDirectory directory = new RAMDirectory();
		Analyzer analyzer = new MetaphoneReplacementAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_47, analyzer);
		IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
		Document doc = new Document();
		doc.add(new TextField("contents", "cool cat", Field.Store.YES));
		writer.addDocument(doc);
		writer.close();
		IndexSearcher searcher = new IndexSearcher(
				DirectoryReader.open(directory));
		Query query = new QueryParser(Version.LUCENE_47, "contents", analyzer)
				.parse("kool kat");
		TopDocs hits = searcher.search(query, 1);
		assertEquals(1, hits.totalHits);
		int docID = hits.scoreDocs[0].doc;
		doc = searcher.doc(docID);
		assertEquals("cool cat", doc.get("contents"));
		searcher.getIndexReader().close();
	}

  /*
    #A Index document
    #B Parse query text
    #C Verify match
    #D Retrieve original value
  */

  public static void main(String[] args) throws IOException {
    MetaphoneReplacementAnalyzer analyzer =
                                 new MetaphoneReplacementAnalyzer();
    AnalyzerUtils.displayTokens(analyzer,
                   "The quick brown fox jumped over the lazy dog");

    System.out.println("");
    AnalyzerUtils.displayTokens(analyzer,
                   "Tha quik brown phox jumpd ovvar tha lazi dag");
  }
}
