package `in`.thetechguru.musiclogger.musiclogger.ui

import `in`.thetechguru.musiclogger.musiclogger.R
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by abami on 17-Mar-18.
 */
class FragmentYoutube:Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_youtube, container, false)
    }
}