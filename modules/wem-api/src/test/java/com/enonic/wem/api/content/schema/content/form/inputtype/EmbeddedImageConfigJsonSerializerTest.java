package com.enonic.wem.api.content.schema.content.form.inputtype;


import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.JsonTestHelper.assertJsonEquals;
import static junit.framework.Assert.assertEquals;

public class EmbeddedImageConfigJsonSerializerTest
{
    private JsonTestHelper jsonHelper;

    private EmbeddedImageConfigJsonSerializer serializer = new EmbeddedImageConfigJsonSerializer();

    @Before
    public void before()
    {
        jsonHelper = new JsonTestHelper( this );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        EmbeddedImageConfig.Builder builder = EmbeddedImageConfig.newEmbeddedImageConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        EmbeddedImageConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config, jsonHelper.objectMapper() );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }

    @Test(expected = NullPointerException.class)
    public void serializeConfig_with_no_relationShipType()
        throws IOException
    {
        // setup
        EmbeddedImageConfig.Builder builder = EmbeddedImageConfig.newEmbeddedImageConfig();
        EmbeddedImageConfig config = builder.build();

        // exercise
        serializer.serializeConfig( config, jsonHelper.objectMapper() );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        EmbeddedImageConfig.Builder builder = EmbeddedImageConfig.newEmbeddedImageConfig();
        builder.relationshipType( QualifiedRelationshipTypeName.LIKE );
        EmbeddedImageConfig expected = builder.build();

        // exercise
        EmbeddedImageConfig parsed = serializer.parseConfig( jsonHelper.loadTestJson( "parseConfig.json" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test(expected = NullPointerException.class)
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        StringBuilder json = new StringBuilder();
        json.append( "{\n" );
        json.append( "\"allowContentTypes\": [\"System:audio\", \"System:image\"]\n" );
        json.append( "}\n" );

        // exercise
        serializer.parseConfig( jsonHelper.stringToJson( json.toString() ) );
    }
}
