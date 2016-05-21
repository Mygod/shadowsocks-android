package com.github.shadowsocks

import android.util.Log
import com.github.shadowsocks.ShadowsocksApplication.app
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.utils.Key
import com.google.android.gms.wearable.{DataEvent, DataEventBuffer, DataMapItem, WearableListenerService}

import scala.collection.JavaConversions._

/**
  * @author Mygod
  */
final class ShadowsocksWearableService extends WearableListenerService {
  override def onDataChanged(dataEvents: DataEventBuffer) {
    Log.d("ss", "DATA CHANGED")
    for (event: DataEvent <- dataEvents) {
      val item = event.getDataItem
      if (item.getUri.getPath == "/profiles") {
        val db = app.profileManager.dbHelper.getWritableDatabase
        db.beginTransaction
        try {
          db.execSQL("DELETE FROM `profile`") // clear table
          if (event.getType == DataEvent.TYPE_CHANGED) {
            val dataMap = DataMapItem.fromDataItem(item).getDataMap
            for (key <- asScalaSet(dataMap.keySet)) {
              val profile = dataMap.getDataMap(key)
              if (profile != null) app.profileManager.createProfile(new Profile {
                id = key.toInt
                name = profile.getString(Key.profileName)
                host = profile.getString(Key.proxy)
                localPort = profile.getInt(Key.localPort)
                remotePort = profile.getInt(Key.remotePort)
                password = profile.getString(Key.sitekey)
                method = profile.getString(Key.encMethod)
                route = profile.getString(Key.route)
                proxyApps = profile.getBoolean(Key.isProxyApps)
                bypass = profile.getBoolean(Key.isBypassApps)
                udpdns = profile.getBoolean(Key.isUdpDns)
                auth = profile.getBoolean(Key.isAuth)
                ipv6 = profile.getBoolean(Key.isIpv6)
                individual = profile.getString(Key.proxied)
                userOrder = profile.getLong(Key.userOrder)
              })
            }
          }
          db.setTransactionSuccessful
        } finally db.endTransaction
        if (Shadowsocks.instance != null) Shadowsocks.instance.refresh
      }
    }
  }
}
