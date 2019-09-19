package com.enonic.xp.extractor.impl;

import java.util.List;
import java.util.Map;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.DefaultParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.extractor.impl.config.ExtractorConfigImpl;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryExtractorImplTest
{
    private BinaryExtractorImpl extractor;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.extractor = new BinaryExtractorImpl();
        extractor.setParser( new DefaultParser() );
        extractor.setDetector( new DefaultDetector() );
        extractor.setExtractorConfig( new ExtractorConfigImpl() );
    }

    @Test
    public void extract_image()
        throws Exception
    {
        final ExtractedData extractedData = this.extractor.extract(
            Resources.asByteSource( BinaryExtractorImplTest.class.getResource( "Multiple-colorSpace-entries.jpg" ) ) );

        final Map<String, List<String>> metadata = extractedData.getMetadata();

        assertEquals( "image/jpeg", metadata.get( HttpHeaders.CONTENT_TYPE ).iterator().next() );
    }

    @Test
    public void extract_pdf()
        throws Exception
    {
        final ExtractedData extractedData =
            this.extractor.extract( Resources.asByteSource( BinaryExtractorImplTest.class.getResource( "sommerfest.pdf" ) ) );

        final Map<String, List<String>> metadata = extractedData.getMetadata();

        assertEquals( "application/pdf", metadata.get( HttpHeaders.CONTENT_TYPE ).iterator().next() );
        final String extractedText = extractedData.getText();
        assertFalse( Strings.isNullOrEmpty( extractedText ) );
        assertTrue( extractedText.contains( "Velkommen" ) );
    }
}
