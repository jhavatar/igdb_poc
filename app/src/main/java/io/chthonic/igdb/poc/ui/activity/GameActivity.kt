package io.chthonic.igdb.poc.ui.activity

import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import io.chthonic.igdb.poc.R
import io.chthonic.igdb.poc.data.model.IgdbGame
import io.chthonic.igdb.poc.data.model.IgdbImage
import io.chthonic.igdb.poc.data.model.Order
import io.chthonic.igdb.poc.utils.TextUtils
import io.chthonic.igdb.poc.utils.UiUtils
import kotlinx.android.synthetic.main.activity_game.*
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Note: Since this activity only performs 1time UI display work, MVP pattern is not required
 */
class GameActivity: BaseActivity() {

    companion object {
        const val KEY_GAME = "key_game"
        const val KEY_RANK = "key_rank"
        const val KEY_ORDER = "key_order"
        const val KEY_IMAGE = "key_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

        // read passed data
        val game = intent.getParcelableExtra<IgdbGame>(KEY_GAME)
        val rank = intent.getIntExtra(KEY_RANK, -1)
        val order = Order.fromId(intent.getIntExtra(KEY_ORDER, Order.POPULARITY.id), Order.POPULARITY)
        val image = if (intent.hasExtra(KEY_IMAGE)) intent.getParcelableExtra<IgdbImage>(KEY_IMAGE) else null

        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        this.toolbar_collapse.title = game.name

        // dynamically resize image view to find best fit for screen
        val imageView = this.game_image
        val height = IgdbImage.WIDTH_COVER_BIG
        val width = IgdbImage.HEIGHT_COVER_BIG
        val params = imageView.layoutParams
        params.height = UiUtils.getMaxHeightForMaxWidth(this, width, height, 0.4)

        if (image != null) {

            // Shared view animation should work fine since Picasso should have cached the image when displaying on the list
            Picasso.get()
                    .load(image.coverBigUrl)
                    .into(imageView)

        } else {
            app_bar.setExpanded(false)
        }

        val summary = game.summary
        if (summary != null) {
            this.game_summary.text = summary

        } else {
            this.game_summary.visibility = View.GONE
        }

        if (rank > 0) {
            this.game_rank.text = TextUtils.fromHtml("<b>Ranked:</b> #$rank")

        } else {
            this.game_summary.visibility = View.GONE
        }

        val date = game.first_release_date
        if (date != null) {
            this.game_date.text = TextUtils.fromHtml("<b>Release:</b> ${TextUtils.getDateStringFromUnixTime(date)}")

        } else {
            this.game_date.visibility = View.GONE
        }

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP
        val pop = game.popularity
        if (pop != null) {
            this.game_pop.text = df.format(pop)
            if (order == Order.POPULARITY) {
                UiUtils.tintImageView(this.game_image_pop, this, R.color.colorAccent)
            }

        } else {
            this.game_layout_pop.visibility = View.GONE
        }

        val rating = game.rating
        if (rating != null) {
            this.game_user.text = df.format(rating)
            if (order == Order.USER_REVIEW) {
                UiUtils.tintImageView(this.game_image_user, this, R.color.colorAccent)
            }

        } else {
            this.game_layout_user.visibility = View.GONE
        }

        val critic = game.aggregated_rating
        if (critic != null) {
            this.game_critic.text = df.format(critic)
            if (order == Order.CRITIC_REVIEW) {
                UiUtils.tintImageView(this.game_image_critic, this, R.color.colorAccent)
            }

        } else {
            this.game_layout_critic.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}