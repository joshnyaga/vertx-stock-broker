package com.joshua.tutorials.vertx_stock_broker.watchlist;

import com.joshua.tutorials.vertx_stock_broker.MainVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class GetWatchListHandler implements Handler<RoutingContext> {
  private final HashMap<UUID, WatchList> watchListPerAccount;

  private static final Logger LOG = LoggerFactory.getLogger(GetWatchListHandler.class);
  public GetWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount){
    this.watchListPerAccount = watchListPerAccount;
  }
  @Override
  public void handle(RoutingContext context) {
      //get the account id from path params
      var accountId = context.pathParam("accountId");
      //show the path and account id param
      LOG.debug("{} for account {}", context.normalizedPath(), accountId);
      //check if the account id is nullable
      var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
      if(watchList.isEmpty()){
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "watchlist for account " + accountId + " not available")
            .put("path", context.normalizedPath())
            .toBuffer()
          );
        return;
      }
      context.response().end(watchList.get().toJsonObject().toBuffer());

  }
}
