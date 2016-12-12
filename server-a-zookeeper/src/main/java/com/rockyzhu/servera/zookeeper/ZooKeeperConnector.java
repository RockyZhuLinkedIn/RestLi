package com.rockyzhu.servera.zookeeper;

import com.linkedin.common.callback.Callback;
import com.linkedin.common.util.None;
import com.linkedin.d2.balancer.config.PartitionDataFactory;
import com.linkedin.d2.balancer.servers.ZKUriStoreFactory;
import com.linkedin.d2.balancer.servers.ZooKeeperAnnouncer;
import com.linkedin.d2.balancer.servers.ZooKeeperConnectionManager;
import com.linkedin.d2.balancer.servers.ZooKeeperServer;
import com.linkedin.d2.discovery.util.D2Config;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by hozhu on 12/7/16.
 */
public class ZooKeeperConnector {
  public static void main(String[] args) throws Exception {
    String path = new File(new File(".").getAbsolutePath()).getCanonicalPath() + "/src/main/config/ZooKeeperConnector.json";
    JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(path));

    populateZooKeeper(json);
    ZooKeeperConnectionManager manager = createZooKeeperConnectionManager(json);

    // start zookeeper announcers
    System.out.println("Starting zookeeper announcers...");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    startAnnouncers(manager, executorService, ((Long) json.get("zkSessionTimeout")));

    //waiting for user to turn off this process
    System.in.read();
    System.out.println("shutting down...");
    shutdown(manager, executorService, ((Long)json.get("zkSessionTimeout")));
  }

  private static void populateZooKeeper(JSONObject json) throws Exception {
    D2Config d2Config = new D2Config(
        (String)json.get("zkConnectString"),
        ((Long)json.get("zkSessionTimeout")).intValue(),
        (String)json.get("zkBasePath"),
        ((Long)json.get("zkSessionTimeout")).intValue(),
        ((Long)json.get("zkRetryLimit")).intValue(),
        (Map<String, Object>) Collections.EMPTY_MAP,
        (Map<String, Object>)json.get("serviceDefaults"),
        (Map<String, Object>)json.get("clusterServiceConfigurations"),
        (Map<String, Object>)Collections.EMPTY_MAP,
        (Map<String, Object>)Collections.EMPTY_MAP);

    d2Config.configure();
    System.out.println("Finished populating zookeeper with d2 configuration");
  }

  private static ZooKeeperConnectionManager createZooKeeperConnectionManager(JSONObject json) {
    List<Map<String, Object>> serviceClusterMappings = (List <Map<String, Object>>) json.get("serviceClusterMappings");

    // create d2 announcers to announce the existence of these servers to zookeeper
    ZooKeeperAnnouncer[] zkAnnouncers = new ZooKeeperAnnouncer[serviceClusterMappings.size()];
    for (int i = 0; i < zkAnnouncers.length; i++) {
      Map<String, Object> d2ServerConfig = serviceClusterMappings.get(i);
      zkAnnouncers[i] = new ZooKeeperAnnouncer(new ZooKeeperServer());
      zkAnnouncers[i].setCluster((String) d2ServerConfig.get("d2Cluster"));
      zkAnnouncers[i].setUri((String) d2ServerConfig.get("serverUri"));
      zkAnnouncers[i].setWeightOrPartitionData(PartitionDataFactory.createPartitionDataMap((Map<String, Object>) d2ServerConfig.get("partitionData")));
    }

    // manager will keep track of the lifecycle of d2 announcers
    ZooKeeperConnectionManager manager = new ZooKeeperConnectionManager(
        (String)json.get("zkConnectString"),
        ((Long)json.get("zkSessionTimeout")).intValue(),
        (String)json.get("zkBasePath"),
        new ZKUriStoreFactory(),
        zkAnnouncers);

    return manager;
  }

  private static void startAnnouncers(final ZooKeeperConnectionManager manager,
                                      ExecutorService executorService,
                                      Long timeout) {
    Future task = executorService.submit(new Runnable() {
      @Override
      public void run () {
        manager.start(new Callback<None>() {
          @Override
          public void onError (Throwable e) {
            System.err.println("problem starting D2 announcers. Aborting...");
            e.printStackTrace();
            System.exit(1);
          }

          @Override
          public void onSuccess (None result) {
            System.out.println("D2 announcers successfully started. ");
            System.out.println("Press enter to stop echo servers and d2 announcers...");
          }
        });
      }
    });

    try {
      task.get(timeout, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      System.err.println("Cannot start zookeeper announcer. Timeout is set to " + timeout + " ms");
      e.printStackTrace();
      System.exit(1);
    }
  }


  private static void shutdown(final ZooKeeperConnectionManager manager,
                               ExecutorService executorService,
                               Long timeout)
      throws IOException
  {
    try {
      executorService.submit(new Runnable() {
        @Override
        public void run () {
          manager.shutdown(new Callback<None>() {
            @Override
            public void onError (Throwable e) {
              System.err.println("problem stopping D2 announcers.");
              e.printStackTrace();
            }

            @Override
            public void onSuccess (None result) {
              System.out.println("D2 announcers successfully stopped.");
            }
          });
        }
      }).get(timeout, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      System.err.println("Cannot stop zookeeper announcer. Timeout is set to " + timeout + " ms");
      e.printStackTrace();
    } finally {
      executorService.shutdown();
    }
  }
}
