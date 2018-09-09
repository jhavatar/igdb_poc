package io.chthonic.igdb.poc.ui.activity

import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.chthonic.igdb.poc.ui.presenter.GamePresenter
import io.chthonic.igdb.poc.utils.TextUtils
import io.chthonic.igdb.poc.utils.UiUtils
import kotlinx.android.synthetic.main.vu_game.*
import timber.log.Timber
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by jhavatar on 9/8/2018.
 */
class GameActivity: BaseActivity() {

    companion object {
        const val KEY_RANK = "key_ranked"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.vu_game)

        val game = intent.getParcelableExtra<IgdbGame>(GamePresenter.KEY_GAME)
        val rank = intent.getIntExtra(KEY_RANK, -1)
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

        if (rank > 0) {
            this.game_rank.text = "Ranked: #$rank"

        } else {
            this.game_summary.visibility = View.GONE
        }

        val date = game.first_release_date
        if (date != null) {
            this.game_date.text = "Release: ${TextUtils.getDateTimeString(date)}"

        } else {
            this.game_date.visibility = View.GONE
        }

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP
        val pop = game.popularity
        if (pop != null) {
            this.game_pop.text = df.format(pop)

        } else {
            this.game_layout_pop.visibility = View.GONE
        }

        val rating = game.rating
        if (rating != null) {
            this.game_user.text = df.format(rating)

        } else {
            this.game_layout_user.visibility = View.GONE
        }

        val critic = game.aggregated_rating
        if (critic != null) {
            this.game_critic.text = df.format(critic)

        } else {
            this.game_layout_critic.visibility = View.GONE
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}