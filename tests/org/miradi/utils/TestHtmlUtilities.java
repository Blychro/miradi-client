/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 

package org.miradi.utils;

import org.miradi.main.MiradiTestCase;

public class TestHtmlUtilities extends MiradiTestCase
{
	public TestHtmlUtilities(String name)
	{
		super(name);
	}
	
	public void testCustomEdit()
	{
		String htmlText = 
		  "<html>\n" +
		  "<head>\n" +
		  "	</head>\n" +
		  "	  <body>\n" +
		  "		<div>\n" +
		  "	 	 text on line 1\n" +
		  "     </div>\n" +
		  "     <div>\n" +
		  "      text on line 2\n" +
		  "		</div>\n" +
		  "	  </body>\n" +
		  "	</html>\n";

		assertEquals("wrong new lines inserted?", "text on line 1<br/>text on line 2", HtmlUtilities.prepareForSaving(htmlText, getTagsToKeep()));
	}
	
	public void testRemoveAllExcept()
	{
		verifyNothingStripped("text");
		verifyRemoveAllExcept("text<br/>", "<font>text<br/>");
		verifyRemoveAllExcept("text<br/ >", "<font>text<br/ >");
		verifyRemoveAllExcept("text<br />", "<font>text<br />");
		verifyRemoveAllExcept("text< br/>", "<font>text< br/>");
		verifyNothingStripped("<b>text");
		verifyNothingStripped("<b>text</b>");
		verifyNothingStripped("<b>text</b >");
		verifyNothingStripped("<b>text< /b>");
		verifyNothingStripped("<b>text</ b>");
		verifyRemoveAllExcept("<b>text", "<body><b>text");
		verifyRemoveAllExcept("<b>text</b>", "<body><b>text</b></body>");
		verifyRemoveAllExcept("<b>text</b>", "<html><body><b>text</b></htm></body>");
		verifyRemoveAllExcept("<b>text", "<font size=\"5\"><b>text</font>");
		verifyRemoveAllExcept("<b someAttribute=\"x\">text", "<b someAttribute=\"x\">text</font>");
		verifyRemoveAllExcept("text", "<html><head></head><body><p style=\"margin-top: 0\">text</p></body></html>");
	}
	
	private void verifyNothingStripped(String text)
	{
		assertEquals("tags were removed?", text, HtmlUtilities.removeAllExcept(text, getTagsToKeep()));
	}

	private void verifyRemoveAllExcept(String expected, String htmlText)
	{
		assertEquals("tags were removed?", expected, HtmlUtilities.removeAllExcept(htmlText, getTagsToKeep()));
	}
	
	private String[] getTagsToKeep()
	{
		return new String[] {"br", "b", "i", "ul", "ol", "li", "u", "strike", };
	}
	
	public void testAppendNewlineToEndDivTags()
	{
		verifyDivWasAppendedWithNewline("sometext</div>\n", "sometext</div>");
		verifyDivWasAppendedWithNewline("sometext</div>\n", "sometext<div/>");
		verifyDivWasAppendedWithNewline("<div>sometext</div>\n", "<div>sometext<div/>");
	}

	protected void verifyDivWasAppendedWithNewline( String expectedValue, String htmlText)
	{
		final String actualValue = HtmlUtilities.appendNewlineToEndDivTags(htmlText);
		assertEquals("div tag was not found and appended with newline, new line missing?", expectedValue, actualValue);
	}
	
	public void testReplaceHtmlTags()
	{
		verifyReplacingTags("sometext<br>", "br", "", "sometext");
		verifyReplacingTags("sometext<br >", "br", "", "sometext");
		verifyReplacingTags("<br>sometext</br>", "br", "", "sometext");
		verifyReplacingTags("<br>sometext</br >", "br", "", "sometext");
		verifyReplacingTags("sometext<br/>", "br", "", "sometext");
		verifyReplacingTags("sometext<br />", "br", "", "sometext");
		verifyReplacingTags("sometext<font size=\"4\">", "font", "", "sometext");
		verifyReplacingTags("sometext<font size=\"4\" >", "font", "", "sometext");
	}
	
	public void testStripAllHtmlTags()
	{
		verifyStringHtmlTags("", "");
		verifyStringHtmlTags("<html>", "");
		verifyStringHtmlTags("<html><body><h1><br/>someText</body></html>", "someText");
		verifyStringHtmlTags("<html><body><h1><font size=\"5\">someText</body></html>", "someText");
	}

	private void verifyStringHtmlTags(String textWithHtmlTags, String expectedValue)
	{
		assertEquals("html tags were not stripped correctly?", expectedValue, HtmlUtilities.stripAllHtmlTags(textWithHtmlTags));
	}
	
	private void verifyReplacingTags(final String htmlText, final String tagToReplace, final String replacement, final String expectedValue)
	{
		assertEquals("html tag was not replaced?", expectedValue, HtmlUtilities.replaceHtmlTags(htmlText, tagToReplace, replacement));
	}
}
