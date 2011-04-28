/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao.script;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQLScriptUtility implements ScriptUtility {

	private static final String MULTI_LINE_COMMENT_START_SYMBOL = "\\/\\*";
	private static final String MULTI_LINE_COMMENT_END_SYMBOL = "\\*\\/";

	private static final String LINE_COMMENT_START_SYMBOL = "^--|^#";
	private static final String LINE_COMMENT_END_SYMBOL = "\\n";

	private static final String STRING_TYPE1_SYMBOL = "\'";
	private static final String STRING_TYPE2_SYMBOL = "\"";

	private static final String STATEMENT_TERMINATOR = ";";

	public List<String> getStatements(String script) {

		List<String> statements = new ArrayList<String>();

		List<Symbol> allSymbols = new ArrayList<Symbol>();
		allSymbols.addAll(this.findSymbols(STRING_TYPE1_SYMBOL, script));
		allSymbols.addAll(this.findSymbols(STRING_TYPE2_SYMBOL, script));
		allSymbols.addAll(this.findSymbols(LINE_COMMENT_START_SYMBOL, script));
		allSymbols.addAll(this.findSymbols(LINE_COMMENT_END_SYMBOL, script));
		allSymbols.addAll(this.findSymbols(MULTI_LINE_COMMENT_START_SYMBOL, script));
		allSymbols.addAll(this.findSymbols(MULTI_LINE_COMMENT_END_SYMBOL, script));

		String str = script;
		int startIndex = 0;
		for(Symbol terminator : this.getValidSymbols(this.findSymbols(STATEMENT_TERMINATOR, script), this.createRegions(this.getValidSymbols(allSymbols)))) {
			str = script.substring(startIndex, terminator.getOffsets().getEnd());
			startIndex += str.length();
			statements.add(str);
		}

		return statements; 
	}

	/**
	 * Searches the sequence for symbols
	 * @param regex - A regular expression used to find the symbol (a sequence) within the sequence
	 * @param sequence - The haystack in which to search for symbols 
	 * @return
	 */
	private List<Symbol> findSymbols(String regex, String sequence) {
		List<Symbol> symbols = new ArrayList<Symbol>();
		Pattern pattern = Pattern.compile(regex,Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(sequence);
		while(matcher.find()) {
			symbols.add(new Symbol(regex, new Offsets(matcher.start(),matcher.end())));
		}
		return symbols;
	}

	/**
	 * Creates regions between supplied symbols
	 * @param symbols
	 * @return
	 */
	private List<Region> createRegions(List<Symbol> symbols) {

		List<Region> regions = new ArrayList<Region>();

		Symbol stringType1Symbol = null;
		Symbol stringType2Symbol = null;
		Symbol lineCommentStartSymbol = null;
		Symbol multiLineCommentStartSymbol = null;

		for(Symbol symbol : symbols) {
			if(symbol.getKey().equals(STRING_TYPE1_SYMBOL)) {
				if(stringType1Symbol != null) {
					regions.add(new Region(stringType1Symbol, symbol));
					stringType1Symbol = null;
				} else {
					stringType1Symbol = symbol;
				}
			} else if(symbol.getKey().equals(STRING_TYPE2_SYMBOL)) {

				if(stringType2Symbol != null) {
					regions.add(new Region(stringType2Symbol, symbol));
					stringType2Symbol = null;
				} else {
					stringType2Symbol = symbol;
				}
			} else if(symbol.getKey().equals(LINE_COMMENT_END_SYMBOL) && lineCommentStartSymbol != null) {
				regions.add(new Region(lineCommentStartSymbol, symbol));
				lineCommentStartSymbol = null;
			} else if(symbol.getKey().equals(LINE_COMMENT_START_SYMBOL)) {
				lineCommentStartSymbol = symbol;
			} else if(symbol.getKey().equals(MULTI_LINE_COMMENT_END_SYMBOL) && multiLineCommentStartSymbol != null) {
				regions.add(new Region(multiLineCommentStartSymbol, symbol));
				multiLineCommentStartSymbol = null;
			} else if(symbol.getKey().equals(MULTI_LINE_COMMENT_START_SYMBOL)) {
				multiLineCommentStartSymbol = symbol;
			}

		}

		if(stringType1Symbol != null) {
			regions.add(new Region(stringType1Symbol, null));
			stringType1Symbol = null;
		}
		if(stringType2Symbol != null) {
			regions.add(new Region(stringType2Symbol, null));
			stringType2Symbol = null;
		}
		if(lineCommentStartSymbol != null) {
			regions.add(new Region(lineCommentStartSymbol, null));
			lineCommentStartSymbol = null;
		}
		if(multiLineCommentStartSymbol != null) {
			regions.add(new Region(multiLineCommentStartSymbol, null));
			multiLineCommentStartSymbol = null;
		}
		return regions;
	}

	/**
	 * Returns valid symbols from a collection of symbols
	 * @param symbols
	 * @return
	 */
	private List<Symbol> getValidSymbols(List<Symbol> symbols) {

		List<Symbol> validSymbols = new ArrayList<Symbol>();

		boolean withinStringType1 = false;
		boolean withinStringType2 = false;
		boolean withinLineComment = false;
		boolean withinMultiLineComment = false;

		Collections.sort(symbols, new Symbol.SymbolStartComparator());

		for(Symbol symbol : symbols) {
			if(!withinStringType1 && !withinStringType2 && !withinLineComment && !withinMultiLineComment) {
				validSymbols.add(symbol);
				if(symbol.getKey().equals(STRING_TYPE1_SYMBOL)) {
					withinStringType1 = true;
				} else if(symbol.getKey().equals(STRING_TYPE2_SYMBOL)) {
					withinStringType2 = true;
				} else if(symbol.getKey().equals(LINE_COMMENT_START_SYMBOL)) {
					withinLineComment = true;
				} else if(symbol.getKey().equals(MULTI_LINE_COMMENT_START_SYMBOL)) {
					withinMultiLineComment = true;
				}
			} else if(withinStringType1 && symbol.getKey().equals(STRING_TYPE1_SYMBOL)) {
				validSymbols.add(symbol);
				withinStringType1 = false;
			} else if(withinStringType2 && symbol.getKey().equals(STRING_TYPE2_SYMBOL)) {
				validSymbols.add(symbol);
				withinStringType2 = false;
			} else if(withinLineComment && symbol.getKey().equals(LINE_COMMENT_END_SYMBOL)) {
				validSymbols.add(symbol);
				withinLineComment = false;
			} else if(withinMultiLineComment && symbol.getKey().equals(MULTI_LINE_COMMENT_END_SYMBOL)) {
				validSymbols.add(symbol);
				withinMultiLineComment = false;
			}
		}
		return validSymbols;
	}

	/**
	 * Returns valid symbols from a set of symbols and regions
	 * @param symbols - symbols to validate against supplied regions
	 * @param invalidRegions - invalid regions. Symbols within these regions are considered invalid. 
	 * @return
	 */
	private List<Symbol> getValidSymbols(List<Symbol> symbols, List<Region> invalidRegions) {

		List<Symbol> validSymbols = new ArrayList<Symbol>();

		symbol:
			for(Symbol symbol : symbols) {
				for(Region invalidRegion : invalidRegions) {
					if(symbol.getOffsets().getStart() >= invalidRegion.getStart() && (invalidRegion.getEnd() == null || symbol.getOffsets().getStart() < invalidRegion.getEnd())) {
						continue symbol;
					}
				}
				validSymbols.add(symbol);
			}
		return validSymbols;
	}
}
