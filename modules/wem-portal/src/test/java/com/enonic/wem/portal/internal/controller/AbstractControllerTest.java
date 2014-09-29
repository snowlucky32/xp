package com.enonic.wem.portal.internal.controller;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.internal.ScriptEnvironment;
import com.enonic.wem.script.internal.ScriptServiceImpl;

public abstract class AbstractControllerTest
{
    private JsController controller;

    protected PostProcessor postProcessor;

    private JsControllerFactoryImpl factory;

    protected JsContext context;

    protected JsHttpRequest request;

    protected PortalResponse response;

    @Before
    public void setup()
        throws Exception
    {
        ResourceUrlTestHelper.mockModuleScheme().modulesClassLoader( getClass().getClassLoader() );

        this.context = new JsContext();
        this.response = this.context.getResponse();

        final ScriptEnvironment environment = new ScriptEnvironment();
        this.factory = new JsControllerFactoryImpl();
        this.factory.setScriptService( new ScriptServiceImpl( environment ) );

        this.postProcessor = Mockito.mock( PostProcessor.class );
        this.factory.setPostProcessor( this.postProcessor );

        final ResourceKey scriptDir = ResourceKey.from( "mymodule:/service/test" );
        this.controller = factory.newController( scriptDir );

        this.request = new JsHttpRequest();
        this.context.setRequest( this.request );
    }

    protected final void execute( final String scriptDir )
    {
        this.controller = this.factory.newController( ResourceKey.from( scriptDir ) );
        this.controller.execute( this.context );
    }
}
