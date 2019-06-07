package com.enonic.xp.repo.impl.node;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodesDependenciesCommand
    extends AbstractNodeCommand
{
    private final NodeIds nodeIds;

    private final NodeIds excludedIds;

    private final Set<NodeId> processed = Sets.newHashSet();

    private final boolean recursive;

    private final Branch target;

    private final Set<CompareStatus> statusesToStopRecursiveSearch;

    private FindNodesDependenciesCommand( final Builder builder )
    {
        super( builder );
        recursive = builder.recursive;
        nodeIds = builder.nodeIds.build();
        excludedIds = builder.excludedIds.build();
        target = builder.target;
        statusesToStopRecursiveSearch = builder.statusesToStopRecursiveSearch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public NodeIds execute()
    {
        return resolveDependencies( nodeIds );
    }

    private NodeIds resolveDependencies( final NodeIds nodeIds )
    {
        Set<NodeId> nonProcessedNodes = nodeIds.getSet().stream().
            filter( ( nodeId ) -> !processed.contains( nodeId ) ).
            collect( Collectors.toSet() );

        if ( nonProcessedNodes.isEmpty() )
        {
            return NodeIds.empty();
        }

        final SearchResult result = getReferences( nonProcessedNodes );

        this.processed.addAll( nonProcessedNodes );

        final NodeIds.Builder builder = NodeIds.create();

        if ( result.isEmpty() )
        {
            return NodeIds.empty();
        }

        addNodeIdsFromReferenceReturnValues( result, builder );

        if ( this.recursive )
        {
            final NodeIds currentLevelDependencies = builder.build();

            final NodeComparisons currentLevelNodeComparisons = CompareNodesCommand.create().
                nodeIds( currentLevelDependencies ).
                target( target ).
                storageService( this.nodeStorageService ).
                build().
                execute();

            final List<NodeId> nextLevelRoot = currentLevelDependencies.stream().filter(
                nodeId -> !statusesToStopRecursiveSearch.contains( currentLevelNodeComparisons.get( nodeId ).
                    getCompareStatus() ) ).
                collect( Collectors.toList() );

            builder.addAll( resolveDependencies( NodeIds.from( nextLevelRoot ) ) );
        }

        return builder.build();
    }

    private SearchResult getReferences( final Set<NodeId> nonProcessedNodes )
    {
        return this.nodeSearchService.query( NodeQuery.create().
            addQueryFilter( ExistsFilter.create().
                fieldName( NodeIndexPath.REFERENCE.getPath() ).
                build() ).
            addQueryFilter( IdFilter.create().
                fieldName( NodeIndexPath.ID.getPath() ).
                values( NodeIds.from( nonProcessedNodes ) ).
                build() ).
            from( 0 ).
            size( nonProcessedNodes.size() ).
            build(), ReturnFields.from( NodeIndexPath.REFERENCE ), SingleRepoSearchSource.from( ContextAccessor.current() ) );

    }

    private void addNodeIdsFromReferenceReturnValues( final SearchResult result, final NodeIds.Builder builder )
    {
        for ( SearchHit hit : result.getHits() )
        {
            final ReturnValue returnValue = hit.getReturnValues().get( NodeIndexPath.REFERENCE.getPath() );

            if ( returnValue == null || returnValue.getValues().isEmpty() )
            {
                continue;
            }

            returnValue.getValues().stream().
                map( ( value ) -> NodeId.from( value.toString() ) ).
                filter( ( value ) -> !processed.contains( value ) ).
                filter( ( value ) -> !excludedIds.contains( value ) ).
                forEach( builder::add );
        }
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds.Builder nodeIds = NodeIds.create();

        private NodeIds.Builder excludedIds = NodeIds.create();

        private Branch target = ContextAccessor.current().getBranch();

        private Set<CompareStatus> statusesToStopRecursiveSearch = Sets.newHashSet();

        private boolean recursive;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeIds( final NodeIds val )
        {
            nodeIds.addAll( val );
            return this;
        }

        public Builder excludedIds( final NodeIds val )
        {
            excludedIds.addAll( val );
            return this;
        }

        public Builder recursive( final boolean val )
        {
            recursive = val;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder statusesToStopRecursiveSearch( final Set<CompareStatus> statusesToStopRecursiveSearch )
        {
            if ( statusesToStopRecursiveSearch != null )
            {
                this.statusesToStopRecursiveSearch.addAll( statusesToStopRecursiveSearch );
            }
            return this;
        }

        public FindNodesDependenciesCommand build()
        {
            return new FindNodesDependenciesCommand( this );
        }
    }
}
