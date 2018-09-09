package io.chthonic.igdb.poc.ui.activity

import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.chthonic.igdb.poc.ui.presenter.GamePresenter
import io.chthonic.igdb.poc.utils.UiUtils
import kotlinx.android.synthetic.main.vu_game.*
import timber.log.Timber

/**
 * Created by jhavatar on 9/8/2018.
 */
class GameActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.vu_game)

        val game = intent.getParcelableExtra<IgdbGame>(GamePresenter.KEY_GAME)
        Timber.d("OnCreate: game = $game")

        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        this.toolbar_collapse.title = game.name
//        this.toolbar_collapse.contentScrim

        val imageView = this.game_image
        val height = IgdbImage.WIDTH_LARGE
        val width = IgdbImage.HEIGHT_LARGE
        val params = imageView.layoutParams
        params.height = UiUtils.getMaxHeightForMaxWidth(this, width, height, 0.4)
//        if ((height != null) && (width != null)) {
//
//
//        }

        val largeUrl = game.cover?.largeUrl
        if (largeUrl != null) {
            Picasso.get()
                    .load(largeUrl)
                    .into(imageView)

        } else {
//            params.height = toolbar.height + 1
//            imageView.visibility = View.INVISIBLE
            app_bar.setExpanded(false)
//            supportActionBar?.title = game.name
        }

        val summary = game.summary
        if (summary != null) {
            this.game_summary.text = summary

        } else {
            this.game_summary.visibility = View.GONE
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}