package com.enonic.wem.api.content.schema.content.form.inputtype;


import java.io.IOException;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.enonic.wem.api.XmlTestHelper;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.content.schema.content.form.inputtype.EmbeddedImageConfig.newEmbeddedImageConfig;
import static junit.framework.Assert.assertEquals;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class EmbeddedImageConfigXmlSerializerTest
{
    private XmlTestHelper xmlHelper;

    private EmbeddedImageConfigXmlSerializer serializer = new EmbeddedImageConfigXmlSerializer();

    @Before
    public void before()
    {
        xmlHelper = new XmlTestHelper( this );
    }

    @Test
    public void serializeConfig()
        throws IOException, SAXException
    {
        // setup
        EmbeddedImageConfig.Builder builder = newEmbeddedImageConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        EmbeddedImageConfig config = builder.build();

        // exercise
        Element configEl = new Element( "config" );
        serializer.serializeConfig( config, configEl );

        // verify
        assertXMLEqual( xmlHelper.loadTestFile( "serializeConfig.xml" ), xmlHelper.serialize( configEl, true ) );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        EmbeddedImageConfig.Builder builder = newEmbeddedImageConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        EmbeddedImageConfig expected = builder.build();

        // exercise
        EmbeddedImageConfig parsed = serializer.parseConfig( xmlHelper.loadXml( "parseConfig.xml" ).getRootElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }


    @Test
    public void parseConfig_with_contentTypeFilter_as_empty()
        throws IOException
    {
        // setup
        EmbeddedImageConfig.Builder builder = newEmbeddedImageConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        EmbeddedImageConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter></content-type-filter>" );
        xml.append( "<relationship-type>System:like</relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        EmbeddedImageConfig parsed = serializer.parseConfig( xmlHelper.parse( xml.toString() ).getRootElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test(expected = NullPointerException.class)
    public void parseConfig_relationshipType_as_empty()
        throws IOException
    {
        // setup
        EmbeddedImageConfig.Builder builder = EmbeddedImageConfig.newEmbeddedImageConfig();
        EmbeddedImageConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<relationship-type></relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        EmbeddedImageConfig parsed = serializer.parseConfig( xmlHelper.parse( xml.toString() ).getRootElement() );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test(expected = NullPointerException.class)
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "</config>\n" );

        // exercise
        serializer.parseConfig( xmlHelper.parse( xml.toString() ).getRootElement() );
    }
}
