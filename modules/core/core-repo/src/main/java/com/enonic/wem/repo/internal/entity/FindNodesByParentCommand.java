package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.expr.QueryExpr;

public class FindNodesByParentCommand
    extends AbstractNodeCommand
{
    private final FindNodesByParentParams params;

    private FindNodesByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public FindNodesByParentResult execute()
    {
        NodePath parentPath = params.getParentPath();

        if ( parentPath == null )
        {
            Node parent = GetNodeByIdCommand.create( this ).
                id( params.getParentId() ).
                build().
                execute();

            if ( parent == null )
            {
                return FindNodesByParentResult.empty();
            }

            parentPath = parent.path();
        }

        final ChildOrder order = NodeChildOrderResolver.create( this ).
            nodePath( parentPath ).
            childOrder( params.getChildOrder() ).
            build().
            resolve();

        final NodeQueryResult nodeQueryResult = this.searchService.search( NodeQuery.create().
            parent( parentPath ).
            query( new QueryExpr( order.getOrderExpressions() ) ).
            from( params.getFrom() ).
            size( params.getSize() ).
            searchMode( params.isCountOnly() ? SearchMode.COUNT : SearchMode.SEARCH ).
            setOrderExpressions( order.getOrderExpressions() ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        if ( nodeQueryResult.getHits() == 0 )
        {
            return FindNodesByParentResult.empty();
        }

        final Nodes nodes = this.storageService.get( nodeQueryResult.getNodeIds(), InternalContext.from( ContextAccessor.current() ) );

        return FindNodesByParentResult.create().
            nodes( nodes ).
            totalHits( nodeQueryResult.getTotalHits() ).
            hits( nodeQueryResult.getHits() ).
            build();
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private FindNodesByParentParams params;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( FindNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public FindNodesByParentCommand build()
        {
            this.validate();
            return new FindNodesByParentCommand( this );
        }
    }
}
