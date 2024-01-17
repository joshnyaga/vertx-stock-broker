package com.joshua.tutorials.vertx_stock_broker.watchlist;

import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlClient;

import java.util.HashMap;
import java.util.UUID;

public class WatchListRestApi {

  public static void attach(final Router parent, Pool db){
    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<UUID, WatchList>();
    //a string path for the similar requests
    String path = "/account/watchlist/:accountId";
    //handle the get path
    parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));
    //handle the put
    parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));
    //handle the delete
    parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));
    String pgPath = "/pg/account/watchlist/:accountId";
    parent.get(pgPath).handler(new GetWatchListFromDatabaseHandler(db));
    parent.put(pgPath).handler(new PutWatchListDatabaseHandler(db));
    parent.delete(pgPath).handler(new DeleteWatchListDatabaseHandler(db));

  }
}
