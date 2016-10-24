package com.enonic.xp.util;

import org.junit.Test;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import static org.junit.Assert.*;

public class MetricsTest
{
    @Test
    public void registry()
    {
        final MetricRegistry registry = Metrics.registry();
        assertNotNull( registry );
        assertSame( registry, Metrics.registry() );
    }

    @Test
    public void meter()
    {
        final Meter meter = Metrics.meter( MetricsTest.class, "meter" );
        assertNotNull( meter );
        assertSame( meter, Metrics.registry().getMeters().get( MetricsTest.class.getName() + ".meter" ) );
    }

    @Test
    public void counter()
    {
        final Counter counter = Metrics.counter( MetricsTest.class, "counter" );
        assertNotNull( counter );
        assertSame( counter, Metrics.registry().getCounters().get( MetricsTest.class.getName() + ".counter" ) );
    }

    @Test
    public void histogram()
    {
        final Histogram histogram = Metrics.histogram( MetricsTest.class, "histogram" );
        assertNotNull( histogram );
        assertSame( histogram, Metrics.registry().getHistograms().get( MetricsTest.class.getName() + ".histogram" ) );
    }

    @Test
    public void timer()
    {
        final Timer timer = Metrics.timer( MetricsTest.class, "timer" );
        assertNotNull( timer );
        assertSame( timer, Metrics.registry().getTimers().get( MetricsTest.class.getName() + ".timer" ) );
    }

    @Test
    public void time()
    {
        final Timer timer = Metrics.timer( MetricsTest.class, "timer2" );

        final String result = Metrics.time( timer, () -> "test" );
        assertEquals( "test", result );
    }

    @Test
    public void gauge()
    {
        final Gauge gauge = Metrics.register( MetricsTest.class, "gauge", (Gauge<Integer>) () -> 0 );

        assertNotNull( gauge );
        assertSame( gauge, Metrics.registry().getGauges().get( MetricsTest.class.getName() + ".gauge" ) );
    }
}