package com.github.shadowsocks

import android.app.Activity
import android.app.backup.BackupManager
import android.content.Intent
import android.graphics.Typeface
import android.net.VpnService
import android.os.{Bundle, Handler}
import android.support.v4.content.ContextCompat
import android.support.wearable.view.WearableListView
import android.support.wearable.view.WearableListView.ViewHolder
import android.util.Log
import android.view.ViewGroup
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.utils.{ConfigUtils, Key, Utils}
import com.github.shadowsocks.widget.WearableListItem
import com.github.shadowsocks.ShadowsocksApplication.app

/**
  * @author Mygod
  */
object Shadowsocks {
  private final val TAG = "Shadowsocks"
  private final val REQUEST_CONNECT = 1

  var instance: Shadowsocks = _
}

final class Shadowsocks extends Activity with ServiceBoundContext {
  import Shadowsocks._

  private final class ProfilesAdapter extends WearableListView.Adapter with WearableListView.ClickListener {
    var profiles = app.profileManager.getAllProfiles.getOrElse(List.empty[Profile])
    if (profiles.isEmpty) profiles = List(app.profileManager.createDefault)

    def getTextStyle(selected: Boolean) = if (selected) Typeface.BOLD else Typeface.NORMAL

    def getItemCount = profiles.length + 1
    def onCreateViewHolder(viewGroup: ViewGroup, i: Int) =
      new WearableListView.ViewHolder(new WearableListItem(Shadowsocks.this))
    def onBindViewHolder(vh: ViewHolder, i: Int) {
      val item = vh.itemView.asInstanceOf[WearableListItem]
      val id = app.profileId
      if (i == 0) {
        item.setText(R.string.profile_disabled)
        item.setTypeface(null, getTextStyle(id < 0))
      } else {
        val profile = profiles(i - 1)
        item.setText(profile.name)
        item.setTypeface(null, getTextStyle(id == profile.id))
      }
    }

    def onTopEmptyRegionClick = ()
    def onClick(viewHolder: ViewHolder) = viewHolder.getAdapterPosition match {
      case 0 =>
        app.profileId(-1)
        if (bgService != null) bgService.use(null)
        app.editor.putBoolean(Key.isAutoConnect, false)
      case pos =>
        app.switchProfile(profiles(pos - 1).id)
        prepareStartService
        app.editor.putBoolean(Key.isAutoConnect, true)
    }

    def selectedIndex = profiles.zipWithIndex.collectFirst {
      case (profile, i) if profile.id == app.profileId => i + 1
    }.getOrElse(0)
  }

  lazy val profilesList = new WearableListView(this)
  val handler = new Handler

  override protected def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    profilesList.setBackgroundColor(ContextCompat.getColor(this, R.color.material_accent_500))
    setContentView(profilesList)
    refresh
    attachService()
    instance = this
  }
  override protected def onDestroy {
    instance = null
    super.onDestroy
    detachService()
    new BackupManager(this).dataChanged
    handler.removeCallbacksAndMessages(null)
  }

  def refresh {
    val adapter = new ProfilesAdapter
    profilesList.setAdapter(adapter)
    profilesList.setClickListener(adapter)
    profilesList.getLayoutManager.scrollToPosition(adapter.selectedIndex)
  }

  def serviceLoad = bgService.use(ConfigUtils.loadFromSharedPreferences)
  def prepareStartService {
    Utils.ThrowableFuture {
      if (app.isVpnEnabled) {
        val intent = VpnService.prepare(this)
        if (intent != null) startActivityForResult(intent, REQUEST_CONNECT)
        else handler.post(() => onActivityResult(REQUEST_CONNECT, Activity.RESULT_OK, null))
      } else serviceLoad
    }
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) = resultCode match {
    case Activity.RESULT_OK => serviceLoad
    case _ => Log.e(TAG, "Failed to start VpnService")
  }
}
