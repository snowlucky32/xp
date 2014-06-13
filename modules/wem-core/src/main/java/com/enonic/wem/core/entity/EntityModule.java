package com.enonic.wem.core.entity;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.elasticsearch.ElasticsearchVersionService;
import com.enonic.wem.core.elasticsearch.ElasticsearchWorkspaceService;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.NodeDaoImpl;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;

public final class EntityModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( NodeService.class ).to( NodeServiceImpl.class ).in( Singleton.class );
        bind( NodeDao.class ).to( NodeDaoImpl.class ).in( Singleton.class );
        bind( IndexService.class ).to( ElasticsearchIndexService.class ).in( Singleton.class );
        bind( WorkspaceService.class ).to( ElasticsearchWorkspaceService.class ).in( Singleton.class );
        bind( VersionService.class ).to( ElasticsearchVersionService.class ).in( Singleton.class );
    }
}
