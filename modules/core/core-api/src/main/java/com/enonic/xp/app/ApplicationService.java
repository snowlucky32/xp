package com.enonic.xp.app;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

import com.enonic.xp.node.NodeId;

@Beta
public interface ApplicationService
{
    Application getInstalledApplication( ApplicationKey key )
        throws ApplicationNotFoundException;

    ApplicationKeys getInstalledApplicationKeys();

    Applications getInstalledApplications();

    boolean isLocalApplication( ApplicationKey key );

    void startApplication( ApplicationKey key, final boolean triggerEvent );

    void stopApplication( ApplicationKey key, final boolean triggerEvent );

    Application installApplication( final ByteSource byteSource, final boolean cluster, final boolean triggerEvent );

    Application installApplication( final NodeId nodeId );

    void uninstallApplication( final ApplicationKey key, final boolean cluster, final boolean triggerEvent );

}
