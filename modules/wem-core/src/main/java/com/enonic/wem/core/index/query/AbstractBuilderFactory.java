package com.enonic.wem.core.index.query;


import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.util.GeoPoint;

public class AbstractBuilderFactory
{
    Object getValueAsType( Value value )
    {
        if ( value.isDateType() )
        {
            return value.asDateTime();
        }

        if ( value.isNumericType() )
        {
            return value.asDouble();
        }

        if ( value.isGeoPoint() )
        {
            final GeoPoint geoPoint = value.asGeoPoint();
            return new org.elasticsearch.common.geo.GeoPoint( geoPoint.getLatitude(), geoPoint.getLongitude() );
        }

        return value.asString();
    }

    QueryBuilder buildNotQuery( final QueryBuilder negated )
    {
        return QueryBuilders.boolQuery().mustNot( negated );
    }

}
