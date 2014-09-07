package com.mxmind.scraper.internal;

import akka.actor.*;
import com.mxmind.scraper.api.Executor;
import com.mxmind.scraper.internal.supported.IndexerImpl;
import com.mxmind.scraper.internal.supported.PageParserImpl;
import org.apache.lucene.index.IndexWriter;
import com.mxmind.scraper.api.Process;

/**
 * The WebScraper solution.
 *
 * @author mxmind
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class Main implements Process {

    public static final String ACTOR_SYSTEM = "web-scraper-actor-system";

    @Override
    public void proc(String path, IndexWriter writer) {
        final ActorSystem actorSystem = ActorSystem.create(ACTOR_SYSTEM);
        final ActorRef supervisor = actorSystem.actorOf(
            Props.create(Supervisor.class, new IndexerImpl(writer), new PageParserImpl(path))
        );

        supervisor.tell(path, actorSystem.guardian());
        actorSystem.awaitTermination();
    }

    @SafeVarargs
    public static void main(String... args) {
        final Main process = new Main();
        final Executor executor = new ProcessExecutor(process);

        executor.exec("http://www.youtube.com/playlist?list=PLOU2XLYxmsIIwGK7v7jg3gQvIAWJzdat_");
    }
}