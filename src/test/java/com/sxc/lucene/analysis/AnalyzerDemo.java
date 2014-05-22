package com.sxc.lucene.analysis;

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
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.wltea.analyzer.lucene.IKAnalyzer;

// From chapter 4

/**
 * Adapted from code which first appeared in a java.net article written by Erik
 */
public class AnalyzerDemo extends TestCase {
	private static final String[] examples = {
			"The quick brown fox jumped over the lazy dog",
			"XY&Z Corporation - xyz@example.com", "Amsterdam Venice", "北京 南通市" };

	private static final Analyzer[] analyzers = new Analyzer[] {
			new WhitespaceAnalyzer(Version.LUCENE_47),
			new SimpleAnalyzer(Version.LUCENE_47),
			new StopAnalyzer(Version.LUCENE_47),
			new StandardAnalyzer(Version.LUCENE_47),
			new SmartChineseAnalyzer(Version.LUCENE_47), new IKAnalyzer() };

	public static void main(String[] args) throws IOException {

		String[] strings = examples;
		if (args.length > 0) { // A
			strings = args;
		}

		for (String text : strings) {
			analyze(text);
		}
	}

	private static void analyze(String text) throws IOException {
		System.out.println("Analyzing \"" + text + "\"");
		for (Analyzer analyzer : analyzers) {
			String name = analyzer.getClass().getSimpleName();
			System.out.println("  " + name + ":");
			System.out.print("    ");
			AnalyzerUtils.displayTokens(analyzer, text); // B
			System.out.println("\n");
		}
	}

	public void testStopAnalyzer2() throws Exception {
		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer2(),
				"The quick brown...", new String[] { "quick", "brown" });
	}

	public void testStopAnalyzerFlawed() throws Exception {
		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(),
				"The quick brown...", new String[] { "the", "quick", "brown" });
	}

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

	public void testMetaphoneReplacement() throws IOException {
		MetaphoneReplacementAnalyzer analyzer = new MetaphoneReplacementAnalyzer();
		AnalyzerUtils.displayTokens(analyzer,
				"The quick brown fox jumped over the lazy dog");
		System.out.println("");
		AnalyzerUtils.displayTokens(analyzer,
				"Tha quik brown phox jumpd ovvar tha lazi dag");
	}
}

// #A Analyze command-line strings, if specified
// #B Real work done in here
