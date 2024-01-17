package com.joshua.tutorials.vertx_stock_broker;

import com.joshua.tutorials.vertx_stock_broker.config.ConfigLoader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionsInfoVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(VersionsInfoVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(config->{
        LOG.info("current application version is {}", config.getVersion());
        startPromise.complete();
      });
  }
}
