package com.theEd209s.utils;

import java.util.ArrayList;

/**
 * This class contains several methods which can be used to parse the contents
 * of a CSV formatted {@link String}.
 * 
 * @author Matthew Weiler
 * */
public class CsvParser
{	
	
	/* PUBLIC CONSTANTS */
	/**
	 * This stores the default separator character to be used if non is
	 * specified. <br />
	 * <br />
	 * <b><u>Default Value</u>:</b> <code>,</code>
	 * */
	public static final char DEFAULT_SEPARATOR_CHAR = ',';
	/**
	 * The character to represent no character. <br />
	 * <br />
	 * <b><u>Default Value</u>:</b> <code>\u0000</code>
	 * */
	public static final char EMPTY_CHAR = '\u0000';
	/**
	 * This stores the single-line quotation mark character. <br />
	 * <br />
	 * <b><u>Default Value</u>:</b> <code>&#39;</code>
	 * */
	public static final char QUOTE_CHAR_SINGLE = '\'';
	/**
	 * This stores the single-line quotation mark character. <br />
	 * <br />
	 * <b><u>Default Value</u>:</b> <code>&quot;</code>
	 * */
	public static final char QUOTE_CHAR_DOUBLE = '"';
	
	/* CONSTRUCTORS */
	/**
	 * This will create a new instance of a {@link CsvParser}.
	 * */
	public CsvParser()
	{	
		
	}
	
	/**
	 * This will create a new instance of a {@link CsvParser}.
	 * 
	 * @param separatorChar
	 *            The <code>char</code> which is to be used as the separating
	 *            character between the pieces. <br />
	 *            <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 *            {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark
	 *            character</i>
	 * */
	public CsvParser(final char separatorChar)
	{
		this.setSeparatorChar(separatorChar);
	}
	
	/**
	 * This will create a new instance of a {@link CsvParser}.
	 * 
	 * @param separatorChar
	 *            The <code>char</code> which is to be used as the separating
	 *            character between the pieces. <br />
	 *            <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 *            {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark
	 *            character</i>
	 * @param groupDuplicateRogueQuotes
	 *            <code>true</code> if any groups of 2 quotation marks should be
	 *            grouped together when the piece in question is not being
	 *            wrapped with the same quotation marks. <br />
	 *            <i>any groups of 2 quotation marks which are within a piece
	 *            that is being wrapped with the same type of quotation marks
	 *            will always be grouped</i>
	 * */
	public CsvParser(final char separatorChar, final boolean groupDuplicateRogueQuotes)
	{
		this.setSeparatorChar(separatorChar);
		this.setGroupDuplicateRogueQuotes(groupDuplicateRogueQuotes);
	}
	
	/**
	 * This will create a new instance of a {@link CsvParser}.
	 * 
	 * @param separatorChar
	 *            The <code>char</code> which is to be used as the separating
	 *            character between the pieces. <br />
	 *            <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 *            {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark
	 *            character</i>
	 * @param groupDuplicateRogueQuotes
	 *            <code>true</code> if any groups of 2 quotation marks should be
	 *            grouped together when the piece in question is not being
	 *            wrapped with the same quotation marks. <br />
	 *            <i>any groups of 2 quotation marks which are within a piece
	 *            that is being wrapped with the same type of quotation marks
	 *            will always be grouped</i>
	 * @param trimPieces
	 *            <code>true</code> if the contents of each piece should be
	 *            trimmed of any white-space surrounding it.
	 * */
	public CsvParser(final char separatorChar, final boolean groupDuplicateRogueQuotes, final boolean trimPieces)
	{
		this.setSeparatorChar(separatorChar);
		this.setGroupDuplicateRogueQuotes(groupDuplicateRogueQuotes);
		this.setTrimPieces(trimPieces);
	}
	
	/* PUBLIC METHODS */
	/**
	 * This method will parse a CSV formatted {@link String} to extract the
	 * pieces and return them in an Array. <br />
	 * <br />
	 * <b><u>Notes</u></b>
	 * <ol>
	 * <li><code>{@link CsvParser#DEFAULT_SEPARATOR_CHAR}</code> will be used as
	 * the separator character.</li>
	 * <li>Any groups of 2 quotation marks will not be grouped together, unless
	 * they are contained within a piece which was wrapped with those same
	 * quotation marks.</li>
	 * <li>The pieces will not be trimmed of any excess white space surrounding
	 * them.</li>
	 * </ol>
	 * 
	 * @param line
	 *            The CSV formatted {@link String} to parse.
	 * */
	public String[] parseLine(final String line)
	{
		return this.parseLine(line, this.separatorChar);
	}
	
	/**
	 * This method will parse a CSV formatted {@link String} to extract the
	 * pieces and return them in an Array. <br />
	 * <br />
	 * <b><u>Notes</u></b>
	 * <ol>
	 * <li>Any groups of 2 quotation marks will not be grouped together, unless
	 * they are contained within a piece which was wrapped with those same
	 * quotation marks.</li>
	 * <li>The pieces will not be trimmed of any excess white space surrounding
	 * them.</li>
	 * </ol>
	 * 
	 * @param line
	 *            The CSV formatted {@link String} to parse.
	 * @param separatorChar
	 *            The <code>char</code> which is to be used as the separating
	 *            character between the pieces. <br />
	 *            <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 *            {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark
	 *            character</i>
	 * */
	public String[] parseLine(final String line, final char separatorChar)
	{
		return this.parseLine(line, separatorChar, this.groupDuplicateRogueQuotes, this.trimPieces);
	}
	
	/**
	 * This method will parse a CSV formatted {@link String} to extract the
	 * pieces and return them in an Array.
	 * 
	 * @param line
	 *            The CSV formatted {@link String} to parse.
	 * @param separatorChar
	 *            The <code>char</code> which is to be used as the separating
	 *            character between the pieces. <br />
	 *            <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 *            {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark
	 *            character</i>
	 * @param groupDuplicateRogueQuotes
	 *            <code>true</code> if any groups of 2 quotation marks should be
	 *            grouped together when the piece in question is not being
	 *            wrapped with the same quotation marks. <br />
	 *            <i>any groups of 2 quotation marks which are within a piece
	 *            that is being wrapped with the same type of quotation marks
	 *            will always be grouped</i>
	 * @param trimPieces
	 *            <code>true</code> if the contents of each piece should be
	 *            trimmed of any white-space surrounding it.
	 * */
	public String[] parseLine(final String line, final char separatorChar, final boolean groupDuplicateRogueQuotes, final boolean trimPieces)
	{
		return CsvParser.parseLineS(line, separatorChar, groupDuplicateRogueQuotes, trimPieces);
	}
	
	/**
	 * This method will parse a CSV formatted {@link String} to extract the
	 * pieces and return them in an Array. <br />
	 * <br />
	 * <b><u>Notes</u></b>
	 * <ol>
	 * <li><code>{@link CsvParser#DEFAULT_SEPARATOR_CHAR}</code> will be used as
	 * the separator character.</li>
	 * <li>Any groups of 2 quotation marks will not be grouped together, unless
	 * they are contained within a piece which was wrapped with those same
	 * quotation marks.</li>
	 * <li>The pieces will not be trimmed of any excess white space surrounding
	 * them.</li>
	 * </ol>
	 * 
	 * @param line
	 *            The CSV formatted {@link String} to parse.
	 * */
	public static String[] parseLineS(final String line)
	{
		return CsvParser.parseLineS(line, CsvParser.DEFAULT_SEPARATOR_CHAR);
	}
	
	/**
	 * This method will parse a CSV formatted {@link String} to extract the
	 * pieces and return them in an Array. <br />
	 * <br />
	 * <b><u>Notes</u></b>
	 * <ol>
	 * <li>Any groups of 2 quotation marks will not be grouped together, unless
	 * they are contained within a piece which was wrapped with those same
	 * quotation marks.</li>
	 * <li>The pieces will not be trimmed of any excess white space surrounding
	 * them.</li>
	 * </ol>
	 * 
	 * @param line
	 *            The CSV formatted {@link String} to parse.
	 * @param separatorChar
	 *            The <code>char</code> which is to be used as the separating
	 *            character between the pieces. <br />
	 *            <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 *            {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark character;
	 *            if they are, the default
	 *            {@link CsvParser#DEFAULT_SEPARATOR_CHAR} will be used</i>
	 * */
	public static String[] parseLineS(final String line, final char separatorChar)
	{
		return CsvParser.parseLineS(line, separatorChar, false, false);
	}
	
	/**
	 * This method will parse a CSV formatted {@link String} to extract the
	 * pieces and return them in an Array.
	 * 
	 * @param line
	 *            The CSV formatted {@link String} to parse.
	 * @param separatorChar
	 *            The <code>char</code> which is to be used as the separating
	 *            character between the pieces. <br />
	 *            <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 *            {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark character;
	 *            if they are, the default
	 *            {@link CsvParser#DEFAULT_SEPARATOR_CHAR} will be used</i>
	 * @param groupDuplicateRogueQuotes
	 *            <code>true</code> if any groups of 2 quotation marks should be
	 *            grouped together when the piece in question is not being
	 *            wrapped with the same quotation marks. <br />
	 *            <i>any groups of 2 quotation marks which are within a piece
	 *            that is being wrapped with the same type of quotation marks
	 *            will always be grouped</i>
	 * @param trimPieces
	 *            <code>true</code> if the contents of each piece should be
	 *            trimmed of any white-space surrounding it.
	 * */
	public static synchronized String[] parseLineS(final String line, char separatorChar, final boolean groupDuplicateRogueQuotes, final boolean trimPieces)
	{
		final ArrayList<String> pieces = new ArrayList<String>()
		{
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean add(String e)
			{
				if (trimPieces && (e != null))
				{
					e = e.trim();
				}
				return super.add(e);
			}
			
		};
		
		if ((separatorChar == CsvParser.QUOTE_CHAR_SINGLE) || (separatorChar == CsvParser.QUOTE_CHAR_DOUBLE))
		{
			separatorChar = CsvParser.DEFAULT_SEPARATOR_CHAR;
		}
		
		QuoteMode quoteMode = null;
		QuoteMode currentPieceQuoteMode = null;
		StringBuilder tmpPiece = new StringBuilder();
		
		int n = 0;
		while (n < line.length())
		{
			char c = line.charAt(n);
			char c_next = CsvParser.EMPTY_CHAR;
			if (line.length() > (n + 1))
			{
				c_next = line.charAt(n + 1);
			}
			
			// If we find an instance of a separator character and we're not currently in quote mode, then consider this the end of this piece.
			if ((quoteMode == null) && (c == separatorChar))
			{
				String tmpPieceStr = tmpPiece.toString();
				if (currentPieceQuoteMode != null)
				{
					if (currentPieceQuoteMode.getQuoteChar() == CsvParser.QUOTE_CHAR_SINGLE)
					{
						tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_SINGLE + CsvParser.QUOTE_CHAR_SINGLE, "" + CsvParser.QUOTE_CHAR_SINGLE);
						if (groupDuplicateRogueQuotes)
						{
							tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_DOUBLE + CsvParser.QUOTE_CHAR_DOUBLE, "" + CsvParser.QUOTE_CHAR_DOUBLE);
						}
					}
					else if (currentPieceQuoteMode.getQuoteChar() == CsvParser.QUOTE_CHAR_DOUBLE)
					{
						tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_DOUBLE + CsvParser.QUOTE_CHAR_DOUBLE, "" + CsvParser.QUOTE_CHAR_DOUBLE);
						if (groupDuplicateRogueQuotes)
						{
							tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_SINGLE + CsvParser.QUOTE_CHAR_SINGLE, "" + CsvParser.QUOTE_CHAR_SINGLE);
						}
					}
				}
				else if (groupDuplicateRogueQuotes)
				{
					tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_SINGLE + CsvParser.QUOTE_CHAR_SINGLE, "" + CsvParser.QUOTE_CHAR_SINGLE).replace("" + CsvParser.QUOTE_CHAR_DOUBLE + CsvParser.QUOTE_CHAR_DOUBLE, "" + CsvParser.QUOTE_CHAR_DOUBLE);
				}
				pieces.add(tmpPieceStr);
				tmpPiece = new StringBuilder();
				quoteMode = null;
				currentPieceQuoteMode = null;
			}
			else
			{
				// If we haven't extracted anything yet from this piece, we're not in quote mode, the current character is a quote character and the following character is not the same quote character, then we know that this is the opening quote for this piece.
				if ((tmpPiece.length() == 0) && (quoteMode == null) && ((CsvParser.QUOTE_CHAR_DOUBLE == c) || (CsvParser.QUOTE_CHAR_SINGLE == c)))
				{
					quoteMode = QuoteMode.determineQuoteMode(c);
					currentPieceQuoteMode = QuoteMode.determineQuoteMode(c);
				}
				else
				{
					// If we're not in quote mode, the current character is a quote character and the following character is the same quote character, then we know that an instance of the current quote character is to be added to this piece.
					if (groupDuplicateRogueQuotes && (quoteMode == null) && ((CsvParser.QUOTE_CHAR_DOUBLE == c) || (CsvParser.QUOTE_CHAR_SINGLE == c)) && (c == c_next))
					{
						tmpPiece.append(c).append(c_next);
						n++;
					}
					// If we're currently in quote mode and the current character is the same quote character as the enclosing quotes, then check deeper.
					else if (quoteMode != null)
					{
						if ((CsvParser.QUOTE_CHAR_DOUBLE == c) || (CsvParser.QUOTE_CHAR_SINGLE == c))
						{
							// If the next character is the same quote character as the current, then check deeper.
							if (c == c_next)
							{
								// If the current character is the same quote character as the enclosing quotes, then an instance of the current quote character is to be added to this piece.
								if (groupDuplicateRogueQuotes || (quoteMode.getQuoteChar() == c))
								{
									tmpPiece.append(c).append(c_next);
									n++;
								}
								else
								{
									tmpPiece.append(c);
								}
							}
							else
							{
								if (quoteMode.getQuoteChar() == c)
								{
									quoteMode = null;
									// If the next character is not an empty character or a separating character, then the current character is just another character in the string.
									if ((c_next != CsvParser.EMPTY_CHAR) && (c_next != separatorChar))
									{
										tmpPiece = new StringBuilder(c + tmpPiece.toString());
										tmpPiece.append(c);
									}
								}
								else
								{
									tmpPiece.append(c);
								}
							}
						}
						else
						{
							tmpPiece.append(c);
						}
					}
					else
					{
						tmpPiece.append(c);
					}
				}
			}
			n++;
		}
		// If the current piece began with a quote, but that quote never got closed, the starting quote was not supposed to be a quote.
		if (quoteMode != null)
		{
			tmpPiece = new StringBuilder(quoteMode.getQuoteChar() + tmpPiece.toString());
			quoteMode = null;
			currentPieceQuoteMode = null;
		}
		String tmpPieceStr = tmpPiece.toString();
		if (currentPieceQuoteMode != null)
		{
			if (currentPieceQuoteMode.getQuoteChar() == CsvParser.QUOTE_CHAR_SINGLE)
			{
				tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_SINGLE + CsvParser.QUOTE_CHAR_SINGLE, "" + CsvParser.QUOTE_CHAR_SINGLE);
				if (groupDuplicateRogueQuotes)
				{
					tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_DOUBLE + CsvParser.QUOTE_CHAR_DOUBLE, "" + CsvParser.QUOTE_CHAR_DOUBLE);
				}
			}
			else if (currentPieceQuoteMode.getQuoteChar() == CsvParser.QUOTE_CHAR_DOUBLE)
			{
				tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_DOUBLE + CsvParser.QUOTE_CHAR_DOUBLE, "" + CsvParser.QUOTE_CHAR_DOUBLE);
				if (groupDuplicateRogueQuotes)
				{
					tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_SINGLE + CsvParser.QUOTE_CHAR_SINGLE, "" + CsvParser.QUOTE_CHAR_SINGLE);
				}
			}
		}
		else if (groupDuplicateRogueQuotes)
		{
			tmpPieceStr = tmpPieceStr.replace("" + CsvParser.QUOTE_CHAR_SINGLE + CsvParser.QUOTE_CHAR_SINGLE, "" + CsvParser.QUOTE_CHAR_SINGLE).replace("" + CsvParser.QUOTE_CHAR_DOUBLE + CsvParser.QUOTE_CHAR_DOUBLE, "" + CsvParser.QUOTE_CHAR_DOUBLE);
		}
		pieces.add(tmpPieceStr);
		return pieces.toArray(new String[pieces.size()]);
	}
	
	/* GETTERS & SETTERS */
	/**
	 * This will get the currently assigned {@link CsvParser#separatorChar}.
	 * 
	 * @return The currently assigned {@link CsvParser#separatorChar}.
	 * */
	public char getSeparatorChar()
	{
		return this.separatorChar;
	}
	
	/**
	 * This will set the currently assigned {@link CsvParser#separatorChar}. <br />
	 * <br />
	 * <i>this cannot be a {@link CsvParser#QUOTE_CHAR_SINGLE} or
	 * {@link CsvParser#QUOTE_CHAR_DOUBLE} quotation mark character</i>
	 * 
	 * @param separatorChar
	 *            The currently assigned {@link CsvParser#separatorChar}.
	 * */
	public void setSeparatorChar(final char separatorChar)
	{
		if ((separatorChar != CsvParser.QUOTE_CHAR_SINGLE) && (separatorChar != CsvParser.QUOTE_CHAR_DOUBLE))
		{
			this.separatorChar = separatorChar;
		}
	}
	
	/**
	 * This will get the {@link CsvParser#groupDuplicateRogueQuotes} flag.
	 * 
	 * @return The {@link CsvParser#groupDuplicateRogueQuotes} flag.
	 * */
	public boolean isGroupDuplicateRogueQuotes()
	{
		return this.groupDuplicateRogueQuotes;
	}
	
	/**
	 * This will set the {@link CsvParser#groupDuplicateRogueQuotes} flag.
	 * 
	 * @param groupDuplicateRogueQuotes
	 *            The {@link CsvParser#groupDuplicateRogueQuotes} flag.
	 * */
	public void setGroupDuplicateRogueQuotes(final boolean groupDuplicateRogueQuotes)
	{
		this.groupDuplicateRogueQuotes = groupDuplicateRogueQuotes;
	}
	
	/**
	 * This will get the {@link CsvParser#trimPieces} flag.
	 * 
	 * @return The {@link CsvParser#trimPieces} flag.
	 * */
	public boolean isTrimPieces()
	{
		return this.trimPieces;
	}
	
	/**
	 * This will set the {@link CsvParser#trimPieces} flag.
	 * 
	 * @param trimPieces
	 *            The {@link CsvParser#trimPieces} flag.
	 * */
	public void setTrimPieces(final boolean trimPieces)
	{
		this.trimPieces = trimPieces;
	}
	
	/* PRIVATE VARIABLES */
	/**
	 * This will store the character to be used as the separator between pieces. <br />
	 * <br />
	 * <b><u>Default Value</u>:</b> {@link CsvParser#DEFAULT_SEPARATOR_CHAR}
	 * */
	private char separatorChar = CsvParser.DEFAULT_SEPARATOR_CHAR;
	/**
	 * This will store the boolean flag to denote if groups of 2 quotation marks
	 * should be paired as a single instance when the piece that they are within
	 * was not surrounded by their own type of quotation mark. <br />
	 * <br />
	 * <b><u>Default Value</u>:</b> <code>false</code>
	 * */
	private boolean groupDuplicateRogueQuotes = false;
	/**
	 * This will store the boolean flag to denote if each piece extracted should
	 * be trimmed of any surrounding white-space. <br />
	 * <br />
	 * <b><u>Default Value</u>:</b> <code>false</code>
	 * */
	private boolean trimPieces = false;
	
	/* PRIVATE ENUMS */
	/**
	 * This enum contains all possible quotation marks.
	 * */
	private static enum QuoteMode
	{
		/**
		 * A single-line quotation mark.
		 * */
		SINGLE('\''),
		/**
		 * A double-line quotation mark.
		 * */
		DOUBLE('"');
		
		/* PRIVATE CONSTRUCTORS */
		/**
		 * This will create a new instance of a {@link QuoteMode}.
		 * 
		 * @param quoteChar
		 *            The quotation mark character.
		 * */
		private QuoteMode(final char quoteChar)
		{
			this.quoteChar = quoteChar;
		}
		
		/* PUBLIC METHODS */
		/**
		 * This will return the correct type of {@link QuoteMode} enum value for
		 * the given quotation mark character.
		 * 
		 * @return The correct type of {@link QuoteMode} enum value for the
		 *         given quotation mark character.
		 * */
		public static QuoteMode determineQuoteMode(final char quoteChar)
		{
			for (QuoteMode quoteMode : QuoteMode.values())
			{
				if (quoteMode.getQuoteChar() == quoteChar)
				{
					return quoteMode;
				}
			}
			return null;
		}
		
		/* GETTERS & SETTERS */
		/**
		 * This will get the quotation mark character.
		 * 
		 * @return The quotation mark character.
		 * */
		public char getQuoteChar()
		{
			return this.quoteChar;
		}
		
		/* PRIVATE VARIABLES */
		/**
		 * The quotation mark character.
		 * */
		private char quoteChar;
		
	}
	
}
