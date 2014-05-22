package com.sxc.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class StopAnalyzerFlawed extends Analyzer {
	
	private CharArraySet stopWords;

	public StopAnalyzerFlawed() {
		stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer source = new LetterTokenizer(Version.LUCENE_47, reader);
		TokenStream tokenStream = new LowerCaseFilter(Version.LUCENE_47,
				new StopFilter(Version.LUCENE_47, source, stopWords));
		
		return new TokenStreamComponents(source, tokenStream);
	}

}
