package com.joshua.tutorials.vertx_stock_broker;

import com.joshua.tutorials.vertx_stock_broker.config.ConfigLoader;
import com.joshua.tutorials.vertx_stock_broker.db.FlywayMigration;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 8888;
  public static void main(String[] args) {
    System.setProperty(ConfigLoader.SERVER_PORT, "8888");
    var vertx = Vertx.vertx();
    //adding exceptions handlers
    vertx.exceptionHandler(error ->{
      LOG.error("Unhandled:", error);
    });

    vertx.deployVerticle(new MainVerticle())
      .onFailure(err->{LOG.error("Failed to deploy", err);})
      .onSuccess(id-> {
          LOG.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id);
        }
        );

  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //deploy the version verticle
    vertx.deployVerticle(VersionsInfoVerticle.class.getName())
        .onFailure(startPromise::fail)
      .onSuccess(id -> {
        LOG.info("Deployed {} with id {}", VersionsInfoVerticle.class.getName(), id);

      }).compose(next->migrateDatabase()).onFailure(startPromise::fail)
      .onSuccess(id->LOG.info("Migrated db schema to latest version"))
          .compose(next ->
            deployRestApiVerticle(startPromise)

          );
  //deploy verticle using the name to be able to redeploy several threads on the event bus

  }

  private Future<Void> migrateDatabase() {
   return ConfigLoader.load(vertx)
      .compose(config->{
        return  FlywayMigration.migrate(vertx, config.getDbConfig());
      });

  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
        //deployment options to get availble cpu processors
        new DeploymentOptions().setInstances(processors()))
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        LOG.info("Deployed {} with id {}", RestApiVerticle.class.getName(), id);
        startPromise.complete();
      });
  }

  private static int processors() {
    //make sure atleast 1 verticle is deployed
    return Math.max(1,Runtime.getRuntime().availableProcessors());
  }


}
