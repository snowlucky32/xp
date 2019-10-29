package com.enonic.xp.admin.impl.rest.resource.project;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProjectResourceTest
    extends AdminResourceTestSupport
{
    private ProjectService projectService;

    @Override
    protected ProjectResource getResourceInstance()
    {
        projectService = Mockito.mock( ProjectService.class );

        final ProjectResource resource = new ProjectResource();
        resource.setProjectService( projectService );

        return resource;
    }

    @Test
    public void get_project()
        throws Exception
    {
        final Project project = createProject( "project1", "project name", "project description", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.get( project.getName() ) ).thenReturn( project );

        final String jsonString = request().
            path( "project/get" ).
            queryParam( "name", project.getName().toString() ).
            get().
            getAsString();

        assertJson( "get_project.json", jsonString );
    }

    @Test
    public void list_projects()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        final Project project2 = createProject( "project2", "project2", null, null );
        final Project project3 = createProject( "project3", null, null, null );

        Mockito.when( projectService.list() ).thenReturn( Projects.create().addAll( List.of( project1, project2, project3 ) ).build() );

        String jsonString = request().path( "project/list" ).get().getAsString();

        assertJson( "list_projects.json", jsonString );
    }

    @Test
    public void create_project_exception()
        throws Exception
    {
        IllegalArgumentException e = new IllegalArgumentException( "Exception occured." );

        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenThrow( e );

        assertThrows( IllegalArgumentException.class, () -> {
            request().path( "project/create" ).
                entity( readFromFile( "create_project_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
                post().getAsString();
        } );
    }

    @Test
    public void create_project_success()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenReturn( project1 );

        String jsonString = request().path( "project/create" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void modify_project_success()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.modify( Mockito.isA( ModifyProjectParams.class ) ) ).thenReturn( project1 );

        String jsonString = request().path( "project/modify" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void delete_project()
        throws Exception
    {
        Mockito.when( projectService.delete( ProjectName.from( "project1" ) ) ).thenReturn( true );

        final String jsonString = request().
            path( "project/delete" ).
            entity( "{\"name\" : \"project1\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().
            getAsString();

        assertEquals( "true", jsonString );
    }

    private Project createProject( final String name, final String displayName, final String description, final Attachment icon )
    {
        return Project.create().
            name( ProjectName.from( name ) ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            build();
    }
}
