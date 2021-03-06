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
import java.io.StringReader;

import junit.framework.Assert;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

// From chapter 4
public class AnalyzerUtils {
	public static void displayTokens(Analyzer analyzer, String text)
			throws IOException {
		displayTokens(analyzer.tokenStream("contents", new StringReader(text))); // A
	}

	public static void displayTokens(TokenStream stream) throws IOException {
		stream.reset();
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		while (stream.incrementToken()) {
			System.out.print("[" + term + "] "); // B
		}
		stream.close();
	}

	/*
	 * #A Invoke analysis process #B Print token text surrounded by brackets
	 */

	public static int getPositionIncrement(AttributeSource source) {
		PositionIncrementAttribute attr = source
				.addAttribute(PositionIncrementAttribute.class);
		return attr.getPositionIncrement();
	}

	public static String getTerm(AttributeSource source) {
		CharTermAttribute attr = source.addAttribute(CharTermAttribute.class);
		return attr.toString();
	}

	public static String getType(AttributeSource source) {
		TypeAttribute attr = source.addAttribute(TypeAttribute.class);
		return attr.type();
	}

	public static void setPositionIncrement(AttributeSource source, int posIncr) {
		PositionIncrementAttribute attr = source
				.addAttribute(PositionIncrementAttribute.class);
		attr.setPositionIncrement(posIncr);
	}

	public static void setTerm(AttributeSource source, String term) {
		CharTermAttribute attr = source.addAttribute(CharTermAttribute.class);
		attr.append(term);
	}

	public static void setType(AttributeSource source, String type) {
		TypeAttribute attr = source.addAttribute(TypeAttribute.class);
		attr.setType(type);
	}

	public static void displayTokensWithPositions(Analyzer analyzer, String text)
			throws IOException {

		TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
		stream.reset();
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posIncr = stream
				.addAttribute(PositionIncrementAttribute.class);

		int position = 0;
		while (stream.incrementToken()) {
			int increment = posIncr.getPositionIncrement();
			if (increment > 0) {
				position = position + increment;
				System.out.println();
				System.out.print(position + ": ");
			}

			System.out.print("[" + term + "] ");
		}
		stream.close();
		System.out.println();
	}

	public static void displayTokensWithFullDetails(Analyzer analyzer,
			String text) throws IOException {

		TokenStream stream = analyzer.tokenStream("contents", // #A
				new StringReader(text));
		stream.reset();

		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class); // #B
		PositionIncrementAttribute posIncr = // #B
		stream.addAttribute(PositionIncrementAttribute.class); // #B
		OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class); // #B
		TypeAttribute type = stream.addAttribute(TypeAttribute.class); // #B

		int position = 0;
		while (stream.incrementToken()) { // #C

			int increment = posIncr.getPositionIncrement(); // #D
			if (increment > 0) { // #D
				position = position + increment; // #D
				System.out.println(); // #D
				System.out.print(position + ": "); // #D
			}

			System.out.print("[" + // #E
					term + ":" + // #E
					offset.startOffset() + "->" + // #E
					offset.endOffset() + ":" + // #E
					type.type() + "] "); // #E
		}
		stream.close();
		System.out.println();
	}

	/*
	 * #A Perform analysis #B Obtain attributes of interest #C Iterate through
	 * all tokens #D Compute position and print #E Print all token details
	 */

	public static void assertAnalyzesTo(Analyzer analyzer, String input,
			String[] output) throws Exception {
		TokenStream stream = analyzer.tokenStream("field", new StringReader(
				input));
		stream.reset();
		CharTermAttribute termAttr = stream.addAttribute(CharTermAttribute.class);
		for (String expected : output) {
			Assert.assertTrue(stream.incrementToken());
			Assert.assertEquals(expected, termAttr.toString());
		}
		Assert.assertFalse(stream.incrementToken());
		stream.close();
	}

	public static void displayPositionIncrements(Analyzer analyzer, String text)
			throws IOException {
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(
				text));
		stream.reset();
		PositionIncrementAttribute posIncr = stream
				.addAttribute(PositionIncrementAttribute.class);
		while (stream.incrementToken()) {
			System.out.println("posIncr=" + posIncr.getPositionIncrement());
		}
		stream.close();
	}

	public static void main(String[] args) throws IOException {
		System.out.println("SimpleAnalyzer");
		displayTokensWithFullDetails(new SimpleAnalyzer(Version.LUCENE_47),
				"The quick brown fox....");

		System.out.println("\n----");
		System.out.println("StandardAnalyzer");
		displayTokensWithFullDetails(new StandardAnalyzer(Version.LUCENE_47),
				"I'll email you at xyz@example.com");
	}
}

/*
 * #1 Invoke analysis process #2 Output token text surrounded by brackets
 */

