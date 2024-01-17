package com.joshua.tutorials.vertx_stock_broker.watchlist;

import com.joshua.tutorials.vertx_stock_broker.MainVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class PutWatchListHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(PutWatchListHandler.class);
  private final HashMap<UUID, WatchList> watchListPerAccount;

  public PutWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
      this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    //get the account id
    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    // the get the body
    var json = context.getBodyAsJson();
    //map the body to watchlist class using fastxml.jackson
    var watchList = json.mapTo(WatchList.class);
    //add to the empty hashmap the account id and the watchlist
    watchListPerAccount.put(UUID.fromString(accountId), watchList);
    //send a response to the user
    context.response().end(json.toBuffer());
  }
}
