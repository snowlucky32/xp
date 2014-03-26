package com.enonic.wem.core.support.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public final class JdomHelper
{

    public Document parse( final String xml )
        throws IOException, JDOMException
    {
        return parse( new StringReader( xml ) );
    }

    public Document parse( final Reader reader )
        throws IOException, JDOMException
    {
        return new SAXBuilder().build( reader );
    }

    public Document parse( final InputStream in )
        throws IOException, JDOMException
    {
        return new SAXBuilder().build( in );
    }

    public Document parse( final URL url )
        throws IOException, JDOMException
    {
        return new SAXBuilder().build( url );
    }

    public String serialize( final Document node, final boolean prettyPrint )
    {
        return newSerializer( prettyPrint ).outputString( node );
    }

    public String serialize( final Element node, final boolean prettyPrint )
    {
        return newSerializer( prettyPrint ).outputString( node );
    }

    private XMLOutputter newSerializer( final boolean prettyPrint )
    {
        final Format format = prettyPrint ? Format.getPrettyFormat() : Format.getCompactFormat();
        return new XMLOutputter( format );
    }
}
