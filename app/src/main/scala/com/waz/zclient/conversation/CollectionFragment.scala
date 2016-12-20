/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.conversation

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.{GridLayoutManager, LinearLayoutManager, RecyclerView, Toolbar}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.waz.threading.Threading
import com.waz.zclient.pages.BaseFragment
import com.waz.zclient.pages.main.conversation.collections.CollectionItemDecorator
import com.waz.zclient.utils.ViewUtils
import com.waz.zclient.{FragmentHelper, OnBackPressedListener, R}

class CollectionFragment extends BaseFragment[CollectionFragment.Container] with FragmentHelper with OnBackPressedListener {

  private implicit lazy val context: Context = getContext

  lazy val controller = new CollectionController
  var adapter: CollectionAdapter = null

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val view = inflater.inflate(R.layout.fragment_collection, container, false)

    val name: TextView  = ViewUtils.getView(view, R.id.tv__collection_toolbar__name)
    controller.conversationName.on(Threading.Ui)(name.setText)

    val recyclerView: RecyclerView = ViewUtils.getView(view, R.id.rv__collection)
    val columns = 4
    adapter = new CollectionAdapter(ViewUtils.getRealDisplayWidth(context), columns, controller)
    recyclerView.setAdapter(adapter)
    recyclerView.addItemDecoration(new CollectionItemDecorator(adapter, columns))
    val layoutManager = new GridLayoutManager(context, columns, LinearLayoutManager.VERTICAL, false)
    layoutManager.setSpanSizeLookup(new CollectionSpanSizeLookup(columns, adapter))
    recyclerView.setLayoutManager(layoutManager)

    val toolbar: Toolbar = ViewUtils.getView(view, R.id.t_collection_toolbar)
    toolbar.setNavigationOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = onBackPressed
    })

    view
  }

  override def onBackPressed(): Boolean = {
    if (!adapter.onBackPressed)
      getControllerFactory.getGiphyController.closeCollection()
    true
  }
}

object CollectionFragment {

  val TAG = CollectionFragment.getClass.getSimpleName

  def newInstance() = new CollectionFragment

  trait Container

}
