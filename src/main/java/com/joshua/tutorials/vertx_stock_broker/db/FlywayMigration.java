package com.joshua.tutorials.vertx_stock_broker.db;

import com.joshua.tutorials.vertx_stock_broker.RestApiVerticle;
import com.joshua.tutorials.vertx_stock_broker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {
  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {
    LOG.debug("DB Config: {}", dbConfig);
    //run on a blocking thread
    return vertx.<Void>executeBlocking(promise -> {
      //flyway migration is blocking coz it uses JDBC
      execute(dbConfig);
      promise.complete();
    }).onFailure(err-> LOG.error("Failed to migrate db schema with error: ",err));

  }

  private static void execute(DbConfig dbConfig) {
    final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
        dbConfig.getHost(),
      dbConfig.getPort(),
      dbConfig.getDatabase()
      );
    LOG.debug("Migrating DB Schema using JDBC url {}", jdbcUrl);
    //flyway config from the config load
    final Flyway flyway =Flyway.configure()
      .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();
    //get info about db schema
    var current = Optional.ofNullable(flyway.info().current());
    current.ifPresent(info-> LOG.info("db schema is at version: {}", info.getVersion()));
    //pending migrations
    var pendingMigrations = flyway.info().pending();
    LOG.debug("Pending migrations are: {}", printMigrations(pendingMigrations));

    //Migrate the schema
    flyway.migrate();
  }

  private static String printMigrations(MigrationInfo[] pendingMigrations) {
    if(Objects.isNull(pendingMigrations)){
      return "[]";
    }
    return Arrays.stream(pendingMigrations)
      .map(each -> each.getVersion() + " - "+ each.getDescription())
      .collect(Collectors.joining(",", "[", "]"));
  }
}
