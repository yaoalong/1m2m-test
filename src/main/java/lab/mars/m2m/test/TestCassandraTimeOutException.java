package lab.mars.m2m.test;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lab.mars.m2m.protocol.common.m2m_childResourceRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
import static lab.mars.m2m.persistence.impl.DBCassandraAgent.*;
import static lab.mars.m2m.reflection.ResourceReflection.deserialize;

/**
 * Created by haixiao on 8/5/2015.
 * Email: wumo@outlook.com
 */
public class TestCassandraTimeOutException {
    protected Cluster cluster;
    protected Session session;

    public Session getSession() {
        return this.session;
    }

    @Before
    public void start() {
        connect("127.0.0.1");
    }

    public void connect(String node) {
        cluster = Cluster.builder()
                         .addContactPoint(node)
                         .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                          metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                              host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
    }


    @After
    public void close() {
        session.close();
        cluster.close();
    }


    @Test
    public void testSelect() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Select.Selection selection = new QueryBuilder(cluster).select();
        RetrieveChildTags.forEach(selection::column);
        Select select = selection.from(keyspace, childrenTable);
        String resourceID = "/csebase/1";
        select.where(eq(ResourceIDTag, resourceID));
        select.orderBy(asc(ChildCreationTimeTag));
//        select.setFetchSize(10);
//        select.limit(1000);

        ResultSetFuture future = session.executeAsync(select);
        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            @Override public void onSuccess(ResultSet resultSet) {//runs on cassandra network thread
                List<m2m_childResourceRef> children = new LinkedList<m2m_childResourceRef>();
                page(resultSet, row -> {
                    m2m_childResourceRef resource = parseResult(row, m2m_childResourceRef.class);
                    children.add(resource);
                }, t -> {
                    t.printStackTrace();
                    latch.countDown();
                }, () -> {
                    System.out.println(children.size());
                    latch.countDown();
                });
            }

            @Override public void onFailure(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }
        }, ForkJoinPool.commonPool());

        latch.await();
    }

    private void page(ResultSet resultSet, Consumer<Row> rowHandler, Consumer<Throwable> failureHandler, Runnable finishHandler) {
        Iterator<Row> iter = resultSet.iterator();
        ListenableFuture<ResultSet> fetchFuture = null;
        boolean finished = true;
        while (iter.hasNext()) {
            if (resultSet.getAvailableWithoutFetching() == 1000 && !resultSet.isFullyFetched())
                fetchFuture = resultSet.fetchMoreResults();
            if (resultSet.getAvailableWithoutFetching() == 0) {
                Futures.addCallback(fetchFuture, new FutureCallback<ResultSet>() {
                    @Override public void onSuccess(ResultSet resultSet) {//runs on cassandra network thread
                        page(resultSet, rowHandler, failureHandler, finishHandler);
                    }

                    @Override public void onFailure(Throwable t) {
                        failureHandler.accept(t);
                    }
                },ForkJoinPool.commonPool());
                finished = false;
                break;
            } else {
                Row row = iter.next();
                rowHandler.accept(row);
            }
        }

        if (finished) {//cannot use iter.hasNext() to check whether this is finished or not, 'cause iter.hasNext() refer to the global state of the
            // resultSet which can be modified by other future operations.
            finishHandler.run();
        }
    }


    private <R> R parseResult(Row row, Class<R> clz) {
        if (row == null) {
            return null;
        }
        ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
        Map<String, Object> result = new HashMap<>();
        columnDefinitions.forEach(d -> {
            String name = d.getName();
            Object buffer = row.getObject(name);
            if (buffer == null) return;
            result.put(name, buffer);
        });
        return deserialize(clz, result);
    }

}
