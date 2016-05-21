package com.github.shadowsocks

import com.github.shadowsocks.utils.Key

/**
  * @author Mygod
  */
object ShadowsocksApplication {
  var app: ShadowsocksApplication = _
}

class ShadowsocksApplication extends ShadowsocksApplicationBase {
  import ShadowsocksApplication._

  override def onCreate() {
    super.onCreate()
    app = this
    editor.putBoolean(Key.isNAT, false).commit  // NAT mode has been deprecated
  }
}
