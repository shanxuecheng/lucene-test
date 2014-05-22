package com.sxc.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.util.Version;

public class MetaphoneReplacementAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		Tokenizer source = new LetterTokenizer(Version.LUCENE_47, reader);
		TokenStream stream = new MetaphoneReplacementFilter(source);
		return new TokenStreamComponents(source, stream);
	}

}
