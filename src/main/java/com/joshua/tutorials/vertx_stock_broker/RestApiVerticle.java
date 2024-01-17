package com.joshua.tutorials.vertx_stock_broker;

import com.joshua.tutorials.vertx_stock_broker.assets.AssetsRestApi;
import com.joshua.tutorials.vertx_stock_broker.config.BrokerConfig;
import com.joshua.tutorials.vertx_stock_broker.config.ConfigLoader;
import com.joshua.tutorials.vertx_stock_broker.quotes.QuotesRestApi;
import com.joshua.tutorials.vertx_stock_broker.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
        .onFailure(startPromise::fail)
          .onSuccess(config->{
            LOG.info("Retrieved Configuration {}", config);
            startHttpServerAndAttachRoutes(startPromise, config);
          });

  }
  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, BrokerConfig config) {
    final SqlClient db = createDbPool(config);
    final Pool pool = createDbPool2(config);


    final Router restApi = Router.router(vertx);
    //for every http request, we try get the body of the request
    restApi.route().handler(BodyHandler.create()).failureHandler(handlerFailure());
    AssetsRestApi.attach(restApi, db);
    QuotesRestApi.attach(restApi, db);
    WatchListRestApi.attach(restApi, pool);

    vertx.createHttpServer().requestHandler(restApi).exceptionHandler(error->{
      LOG.error("HTTP SERVER ERROR: "+ error);
    }).listen(config.getServerPort(), http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("HTTP server started on port {}", config.getServerPort());
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private SqlClient createDbPool(BrokerConfig config) {
    final var connectOptions = new PgConnectOptions()
      .setHost(config.getDbConfig().getHost())
      .setPort(config.getDbConfig().getPort())
      .setDatabase(config.getDbConfig().getDatabase())
      .setUser(config.getDbConfig().getUser())
      .setPassword(config.getDbConfig().getPassword());

    var poolOptions = new PoolOptions()
      .setMaxSize(4);
    //create db pool

    return  PgBuilder
      .client()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();
  }
  private Pool createDbPool2(BrokerConfig config) {
    final var connectOptions = new PgConnectOptions()
      .setHost(config.getDbConfig().getHost())
      .setPort(config.getDbConfig().getPort())
      .setDatabase(config.getDbConfig().getDatabase())
      .setUser(config.getDbConfig().getUser())
      .setPassword(config.getDbConfig().getPassword());


    //create db pool

    return   Pool.pool(vertx, connectOptions, new PoolOptions().setMaxSize(4));
  }

  private static Handler<RoutingContext> handlerFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        //ignore
        //client stopped the request
        return;
      }
      LOG.error("Route error ", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }
}
