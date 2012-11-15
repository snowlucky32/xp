package com.enonic.wem.core.content.type;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.content.ParsingException;
import com.enonic.wem.core.content.SerializingException;

public interface ContentTypeSerializer
{
    public String toString( ContentType type )
        throws SerializingException;

    public ContentType toContentType( String xml )
        throws ParsingException;
}
