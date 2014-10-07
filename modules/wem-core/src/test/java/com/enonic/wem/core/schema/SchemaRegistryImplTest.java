package com.enonic.wem.core.schema;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;

public class SchemaRegistryImplTest
{
    private SchemaRegistryImpl schemaManager;

    private BundleContext bundleContext;

    @Before
    public void setup()
        throws Exception
    {
        this.schemaManager = new SchemaRegistryImpl();
        this.bundleContext = Mockito.mock( BundleContext.class );
        this.schemaManager.setBundleContext( bundleContext );
        this.schemaManager.start();
    }

    @After
    public void shutdown()
        throws Exception
    {
        this.schemaManager.stop();
    }

    @Test
    public void testGetContentType()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_content_type" ).
            displayName( "My content type" ).
            build();
        mockAddBundle( contentType );

        // exercise
        final ContentType existingSchema = this.schemaManager.getContentType( ContentTypeName.from( "mymodule:my_content_type" ) );
        final ContentType nonExistingSchema = this.schemaManager.getContentType( ContentTypeName.from( "mymodule:other_type" ) );

        // verify
        assertNotNull( existingSchema );
        assertNull( nonExistingSchema );
        assertEquals( "mymodule:my_content_type", existingSchema.getName().toString() );
    }

    @Test
    public void testGetContentTypeAfterUpdate()
        throws Exception
    {
        // setup
        ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_content_type" ).
            displayName( "My content type" ).
            build();
        mockAddBundle( contentType );

        ContentType existingSchema = this.schemaManager.getContentType( ContentTypeName.from( "mymodule:my_content_type" ) );
        assertNotNull( existingSchema );

        // update module schemas
        ContentType contentType2 = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_content_type2" ).
            displayName( "My content type" ).
            build();
        mockAddBundle( contentType2 );

        // exercise
        existingSchema = this.schemaManager.getContentType( ContentTypeName.from( "mymodule:my_content_type2" ) );
        ContentType nonExistingSchema = this.schemaManager.getContentType( ContentTypeName.from( "mymodule:my_content_type" ) );

        // verify
        assertNotNull( existingSchema );
        assertNull( nonExistingSchema );
        assertEquals( "mymodule:my_content_type2", existingSchema.getName().toString() );
    }

    @Test
    public void testGetMixin()
        throws Exception
    {
        // setup
        final Mixin mixin = Mixin.newMixin().
            name( "mymodule:my_mixin" ).
            displayName( "My mixin" ).
            build();
        mockAddBundle( mixin );

        // exercise
        final Mixin existingSchema = this.schemaManager.getMixin( MixinName.from( "mymodule:my_mixin" ) );
        final Mixin nonExistingSchema = this.schemaManager.getMixin( MixinName.from( "mymodule:other_mixin" ) );

        // verify
        assertNotNull( existingSchema );
        assertNull( nonExistingSchema );
        assertEquals( "mymodule:my_mixin", existingSchema.getName().toString() );
    }

    @Test
    public void testGetRelationshipType()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            name( "mymodule:my_rel_type" ).
            displayName( "My relationship type" ).
            build();
        mockAddBundle( relationshipType );

        // exercise
        final RelationshipType existingSchema =
            this.schemaManager.getRelationshipType( RelationshipTypeName.from( "mymodule:my_rel_type" ) );
        final RelationshipType nonExistingSchema =
            this.schemaManager.getRelationshipType( RelationshipTypeName.from( "mymodule:other_rel_type" ) );

        // verify
        assertNotNull( existingSchema );
        assertNull( nonExistingSchema );
        assertEquals( "mymodule:my_rel_type", existingSchema.getName().toString() );
    }

    @Test
    public void testGetAll()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_content_type" ).
            displayName( "My content type" ).
            build();
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            name( "mymodule:my_rel_type" ).
            displayName( "My relationship type" ).
            build();
        mockAddBundle( contentType, relationshipType );

        final Mixin mixin = Mixin.newMixin().
            name( "othermodule:my_mixin" ).
            displayName( "My mixin" ).
            build();
        mockAddBundle( mixin );

        // exercise
        final ContentTypes contentTypes = this.schemaManager.getAllContentTypes();
        final Mixins mixins = this.schemaManager.getAllMixins();
        final RelationshipTypes relationshipTypes = this.schemaManager.getAllRelationshipTypes();

        // verify
        assertNotNull( contentTypes );
        assertNotNull( mixins );
        assertNotNull( relationshipTypes );
        assertEquals( 1, contentTypes.getSize() );
        assertEquals( 1, mixins.getSize() );
        assertEquals( 1, relationshipTypes.getSize() );
        assertNotNull( contentTypes.getContentType( ContentTypeName.from( "mymodule:my_content_type" ) ) );
        assertNotNull( relationshipTypes.get( RelationshipTypeName.from( "mymodule:my_rel_type" ) ) );
        assertNotNull( mixins.getMixin( MixinName.from( "othermodule:my_mixin" ) ) );
    }

    @Test
    public void testRemoveModule()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_content_type" ).
            displayName( "My content type" ).
            build();
        mockAddBundle( contentType );

        final Mixin mixin = Mixin.newMixin().
            name( "othermodule:my_mixin" ).
            displayName( "My mixin" ).
            build();
        final Bundle bundleOtherModule = mockAddBundle( mixin );

        // exercise
        mockRemoveBundle( bundleOtherModule );
        final ContentTypes contentTypes = this.schemaManager.getAllContentTypes();
        final Mixins mixins = this.schemaManager.getAllMixins();

        // verify
        assertNotNull( contentTypes );
        assertEquals( 1, contentTypes.getSize() );
        assertNotNull( contentTypes.getContentType( ContentTypeName.from( "mymodule:my_content_type" ) ) );
        assertNull( mixins.getMixin( MixinName.from( "othermodule:my_mixin" ) ) );
    }

    @Test
    public void testUpdateModule()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "mymodule:my_content_type" ).
            displayName( "My content type" ).
            build();
        final Bundle moduleBundle = mockAddBundle( contentType );

        // exercise
        final ContentType otherContentType = ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            name( "othermodule:other_content_type" ).
            displayName( "Somet other content type" ).
            build();
        mockModifyBundle( moduleBundle, otherContentType );
        final ContentTypes contentTypes = this.schemaManager.getAllContentTypes();

        // verify
        assertNotNull( contentTypes );
        assertEquals( 1, contentTypes.getSize() );
        assertNull( contentTypes.getContentType( ContentTypeName.from( "mymodule:my_content_type" ) ) );
        assertNotNull( contentTypes.getContentType( ContentTypeName.from( "othermodule:other_content_type" ) ) );
    }

    private Bundle mockAddBundle( final Schema... schemas )
        throws Exception
    {
        final ModuleKey moduleKey = schemas[0].getName().getModuleKey();
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( moduleKey.toString() );
        Mockito.when( bundle.getVersion() ).thenReturn( new Version( "1.0.0" ) );

        final ServiceReference serviceRef = Mockito.mock( ServiceReference.class );
        Mockito.when( serviceRef.getBundle() ).thenReturn( bundle );
        final SchemaProvider schemasProvider = () -> Schemas.from( schemas );
        Mockito.when( this.bundleContext.getService( eq( serviceRef ) ) ).thenReturn( schemasProvider );
        this.schemaManager.addingService( serviceRef );

        return bundle;
    }

    private void mockModifyBundle( final Bundle bundle, final Schema... schemas )
        throws Exception
    {
        final ServiceReference serviceRef = Mockito.mock( ServiceReference.class );
        Mockito.when( serviceRef.getBundle() ).thenReturn( bundle );
        final SchemaProvider schemasProvider = () -> Schemas.from( schemas );
        Mockito.when( this.bundleContext.getService( eq( serviceRef ) ) ).thenReturn( schemasProvider );
        this.schemaManager.modifiedService( serviceRef, schemasProvider );
    }

    private void mockRemoveBundle( final Bundle bundle )
        throws Exception
    {
        final ServiceReference serviceRef = Mockito.mock( ServiceReference.class );
        Mockito.when( serviceRef.getBundle() ).thenReturn( bundle );
        final SchemaProvider schemasProvider = Schemas::empty;
        this.schemaManager.removedService( serviceRef, schemasProvider );
    }

}