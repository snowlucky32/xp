package com.enonic.wem.web.rest.rpc.content.schema.mixin;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.content.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.content.schema.mixin.GetMixins;
import com.enonic.wem.api.command.content.schema.mixin.UpdateMixin;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.schema.mixin.editor.SetMixinEditor;
import com.enonic.wem.core.content.schema.mixin.MixinXmlSerializer;
import com.enonic.wem.core.support.serializer.ParsingException;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.rpc.UploadedIconFetcher;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.content.schema.mixin.editor.SetMixinEditor.newSetMixinEditor;

@Component
public class CreateOrUpdateMixinRpcHandler
    extends AbstractDataRpcHandler
{
    private final MixinXmlSerializer mixinXmlSerializer = new MixinXmlSerializer();

    private UploadService uploadService;

    public CreateOrUpdateMixinRpcHandler()
    {
        super( "mixin_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String mixinJson = context.param( "mixin" ).required().asString();
        final String iconReference = context.param( "iconReference" ).asString();
        final Mixin mixin;
        try
        {
            mixin = mixinXmlSerializer.toMixin( mixinJson );
        }
        catch ( ParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid Mixin format" ) );
            return;
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( JsonRpcException e )
        {
            context.setResult( new JsonErrorResult( e.getError().getMessage() ) );
            return;
        }

        if ( !mixinExists( mixin.getQualifiedName() ) )
        {
            final CreateMixin createCommand = buildCreateMixinCommand( mixin, icon );
            client.execute( createCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.created() );
        }
        else
        {
            final UpdateMixin updateCommand = buildUpdateMixinCommand( mixin, icon );
            client.execute( updateCommand );
            context.setResult( CreateOrUpdateMixinJsonResult.updated() );
        }
    }

    private CreateMixin buildCreateMixinCommand( final Mixin mixin, final Icon icon )
    {
        return mixin().create().
            displayName( mixin.getDisplayName() ).
            formItem( mixin.getFormItem() ).
            moduleName( mixin.getModuleName() ).
            icon( icon );
    }

    private UpdateMixin buildUpdateMixinCommand( final Mixin mixin, final Icon icon )
    {
        final SetMixinEditor editor = newSetMixinEditor().
            displayName( mixin.getDisplayName() ).
            formItem( mixin.getFormItem() ).
            icon( icon ).
            build();
        return mixin().update().
            qualifiedName( mixin.getQualifiedName() ).
            editor( editor );
    }

    private boolean mixinExists( final QualifiedMixinName qualifiedName )
    {
        final GetMixins getMixins = mixin().get().names( QualifiedMixinNames.from( qualifiedName ) );
        return !client.execute( getMixins ).isEmpty();
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
