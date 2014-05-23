package com.sxc.lucene.analysis.stopanalyzer;

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

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

// From chapter 4
public class StopAnalyzer1 extends Analyzer {

	private CharArraySet stopWords;

	public StopAnalyzer1() {
		stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
	}

	public StopAnalyzer1(String[] stopWords) {
		this.stopWords = StopFilter.makeStopSet(Version.LUCENE_47, stopWords);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer source = new LowerCaseTokenizer(Version.LUCENE_47, reader);
		return new TokenStreamComponents(source, new StopFilter(Version.LUCENE_47, source, stopWords));
	}
}
