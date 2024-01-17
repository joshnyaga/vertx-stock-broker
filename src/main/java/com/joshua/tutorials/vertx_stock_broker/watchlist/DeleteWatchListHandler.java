package com.joshua.tutorials.vertx_stock_broker.watchlist;

import com.joshua.tutorials.vertx_stock_broker.MainVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {
  private final HashMap<UUID, WatchList> watchListPerAccount;

  private static final Logger LOG = LoggerFactory.getLogger(DeleteWatchListHandler.class);

    public DeleteWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
        this.watchListPerAccount = watchListPerAccount;
    }

    @Override
  public void handle(RoutingContext context) {
    //get account id
    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    //remove it and get the removed object
    WatchList deleted = watchListPerAccount.remove(UUID.fromString(accountId));
    //send the res
    context.response().end(
      //convert it to json object the to buffer
      deleted.toJsonObject().toBuffer()
    );
  }
}
