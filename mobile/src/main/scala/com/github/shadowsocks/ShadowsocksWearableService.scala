package com.github.shadowsocks

import android.os.Bundle
import android.util.Log
import com.github.shadowsocks.ShadowsocksApplication.app
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.utils.Key
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.{GoogleApiClient, ResultCallback}
import com.google.android.gms.wearable.DataApi.DataItemResult
import com.google.android.gms.wearable._

/**
  * @author Mygod
  */
object ShadowsocksWearableService extends GoogleApiClient.ConnectionCallbacks
  with GoogleApiClient.OnConnectionFailedListener with ResultCallback[DataItemResult] {
  private final val TAG = "ShadowsocksWearableService"
  lazy val wearableApi = new GoogleApiClient.Builder(app).addConnectionCallbacks(this)
    .addOnConnectionFailedListener(this).addApi(Wearable.API).build

  override def onConnected(connectionHint: Bundle) = pushProfiles
  override def onConnectionSuspended(cause: Int) = () // ignore

  override def onConnectionFailed(result: ConnectionResult) =
    if (result.getErrorCode == ConnectionResult.API_UNAVAILABLE) Log.d(TAG, "Wearable API unavailable.")
    else Log.w(TAG, "Wearable API connection failed: " + result.getErrorMessage)

  private def toDataMap(profile: Profile) = {
    val result = new DataMap
    result.putString(Key.profileName, profile.name)
    result.putString(Key.proxy, profile.host)
    result.putInt(Key.localPort, profile.localPort)
    result.putInt(Key.remotePort, profile.remotePort)
    result.putString(Key.sitekey, profile.password)
    result.putString(Key.encMethod, profile.method)
    result.putString(Key.route, profile.route)
    result.putBoolean(Key.isProxyApps, profile.proxyApps)
    result.putBoolean(Key.isBypassApps, profile.bypass)
    result.putBoolean(Key.isUdpDns, profile.udpdns)
    result.putBoolean(Key.isAuth, profile.auth)
    result.putBoolean(Key.isIpv6, profile.ipv6)
    result.putString(Key.proxied, profile.individual)
    result.putLong(Key.userOrder, profile.userOrder)
    result
  }
  def pushProfiles = if (wearableApi.isConnected) {
    val request = PutDataMapRequest.create("/profiles")
    val dataMap = request.getDataMap
    for (profile <- app.profileManager.getAllProfiles.getOrElse(List.empty[Profile]))
      dataMap.putDataMap(profile.id.toString, toDataMap(profile))
    Wearable.DataApi.putDataItem(wearableApi, request.setUrgent.asPutDataRequest).setResultCallback(this)
  }

  override def onResult(result: DataItemResult) =
    if (!result.getStatus.isSuccess) Log.w(TAG, "Wearable profiles sync failed: " + result.getStatus.getStatusMessage)
    else Log.d(TAG, "Wearable profiles sync finished: " + result.getStatus.getStatusMessage)  // TODO: remove
}

final class ShadowsocksWearableService extends WearableListenerService {
  import ShadowsocksWearableService._

  // try connecting again on a device is connected
  override def onCapabilityChanged(capabilityInfo: CapabilityInfo) = if (!wearableApi.isConnected) wearableApi.connect
}
