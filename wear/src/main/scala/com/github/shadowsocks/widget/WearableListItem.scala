package com.github.shadowsocks.widget

import android.content.Context
import android.support.wearable.view.WearableListView
import android.util.{AttributeSet, TypedValue}
import android.view.Gravity
import android.widget.TextView
import com.github.shadowsocks.utils.Utils

/**
  * @author Mygod
  */
class WearableListItem(context: Context, attrs: AttributeSet = null) extends TextView(context, attrs)
  with WearableListView.OnCenterProximityListener {
  {
    val typedArray = context.obtainStyledAttributes(Array(android.R.attr.selectableItemBackground))
    setBackgroundResource(typedArray.getResourceId(0, 0))
    typedArray.recycle
    val dp16 = Utils.dpToPx(context, 16)
    setPadding(dp16, dp16, dp16, dp16)
  }
  setGravity(Gravity.CENTER_VERTICAL)
  setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources.getDisplayMetrics))

  override def onCenterPosition(animate: Boolean) = if (animate) this.animate().scaleX(1).scaleY(1).alpha(1) else {
    setScaleX(1)
    setScaleY(1)
    setAlpha(1)
  }
  override def onNonCenterPosition(animate: Boolean) =
    if (animate) this.animate().scaleX(.8f).scaleY(.8f).alpha(.6f) else {
      setScaleX(.8f)
      setScaleY(.8f)
      setAlpha(.6f)
    }
}
