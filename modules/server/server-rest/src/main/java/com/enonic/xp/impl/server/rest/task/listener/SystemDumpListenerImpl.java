package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

public class SystemDumpListenerImpl
    implements SystemDumpListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int progress = 0;

    private int currentBranchTotal = 0;

    private int currentBranchProgress = 0;

    public SystemDumpListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void totalBranches( final long total )
    {
        this.total = Math.toIntExact( total );
    }

    @Override
    public void dumpingBranch( final RepositoryId repositoryId, final Branch branch, final long total )
    {
        currentBranchTotal = Math.toIntExact( total );
        currentBranchProgress = 0;
    }

    @Override
    public void nodeDumped()
    {
        currentBranchProgress++;

        if ( currentBranchTotal != 0 && total != 0 )
        {
            progress += currentBranchProgress / currentBranchTotal / total;
            progressReporter.progress( progress, total );
        }
    }

    /*@Override
    public void branchDumped()
    {
        progressReporter.progress( ++progressCount, this.total );
    }*/
}
