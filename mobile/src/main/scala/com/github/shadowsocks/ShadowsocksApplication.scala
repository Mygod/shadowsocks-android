package com.github.shadowsocks

/**
  * @author Mygod
  */
object ShadowsocksApplication {
  var app: ShadowsocksApplication = _
}

class ShadowsocksApplication extends ShadowsocksApplicationBase {
  override def onCreate() {
    super.onCreate()
    ShadowsocksApplication.app = this
    ShadowsocksWearableService.wearableApi.connect
  }

  override def onProfilesChanged = ShadowsocksWearableService.pushProfiles
}
