package com.dleibovych.epictale.game.profile

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.dleibovych.epictale.R
import com.dleibovych.epictale.game.di.GameComponentProvider
import kotlinx.android.synthetic.main.fragment_profile.*

import java.util.Comparator

import javax.inject.Inject

import org.thetale.api.models.AccountInfo

class ProfileFragment : Fragment(), ProfileView {

    @Inject lateinit var presenter: ProfilePresenter

    private var rootView: View? = null

    private var textName: TextView? = null
    private var textAffectGame: TextView? = null
    private var textMight: TextView? = null
    private var textAchievementPoints: TextView? = null
    private var textCollectionItemsCount: TextView? = null
    private var textReferralsCount: TextView? = null
    private var tableRatings: ViewGroup? = null
    private var textRatingsDescription: TextView? = null
    private var tablePlacesHistory: ViewGroup? = null
    private var tablePlacesHistorySwitcher: TextView? = null

    private var isNarrowMode: Boolean = false
    private var isTablePlacesHistoryCollapsed: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as GameComponentProvider)
                .provideGameComponent()
                ?.inject(this)

        presenter.view = this

        rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        textName = rootView!!.findViewById(R.id.profile_name)
        textAffectGame = rootView!!.findViewById(R.id.profile_affect_game)
        textMight = rootView!!.findViewById(R.id.profile_might)
        textAchievementPoints = rootView!!.findViewById(R.id.profile_achievement_points)
        textCollectionItemsCount = rootView!!.findViewById(R.id.profile_collection_items_count)
        textReferralsCount = rootView!!.findViewById(R.id.profile_referrals_count)
        tableRatings = rootView!!.findViewById(R.id.profile_container_ratings)
        textRatingsDescription = rootView!!.findViewById(R.id.profile_ratings_description)
        tablePlacesHistory = rootView!!.findViewById(R.id.profile_container_places_history)
        tablePlacesHistorySwitcher = rootView!!.findViewById(R.id.profile_container_places_history_switcher)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        error.onRetryClick(View.OnClickListener { presenter.retry() })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.view = null
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        content.visibility = View.GONE
    }

    override fun showAccountInfo(info: AccountInfo) {
        progress.visibility = View.GONE
        error.visibility = View.GONE
        content.visibility = View.VISIBLE


        textName!!.text = Html.fromHtml(getString(R.string.find_player_info_short, info.name))
        textAffectGame!!.text = getString(if (info.permissions.canAffectGame) R.string.game_affect_true else R.string.game_affect_false)
        textMight!!.text = Math.floor(info.might).toInt().toString()
        textAchievementPoints!!.text = info.achievements.toString()
        textCollectionItemsCount!!.text = info.collections.toString()
        textReferralsCount!!.text = info.referrals.toString()

        fillRatings(info)
        fillPlacesHistory(info)
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun showError() {
        error.visibility = View.VISIBLE
        progress.visibility = View.GONE
        content.visibility = View.GONE

        error.setErrorText(getString(R.string.common_error))
    }

    private fun getTableRow(parent: ViewGroup, text1: CharSequence, text2: CharSequence?, text3: CharSequence): View {
        val row = layoutInflater!!.inflate(R.layout.item_profile_table, parent, false)
        (row.findViewById<View>(R.id.item_profile_table_text_1) as TextView).text = text1
        (row.findViewById<View>(R.id.item_profile_table_text_2) as TextView).text = text2
        (row.findViewById<View>(R.id.item_profile_table_text_3) as TextView).text = text3
        return row
    }

    private fun fillRatings(info: AccountInfo) {
        tableRatings!!.removeAllViews()

        val captionRatingValue: Spannable?
        if (isNarrowMode) {
            captionRatingValue = null
        } else {
            captionRatingValue = SpannableString(getString(R.string.profile_rating_caption_value))
            captionRatingValue.setSpan(StyleSpan(Typeface.BOLD), 0, captionRatingValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val captionRatingName = SpannableString(getString(R.string.profile_rating_caption_name))
        captionRatingName.setSpan(StyleSpan(Typeface.BOLD), 0, captionRatingName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val captionRatingPlace = SpannableString(getString(R.string.profile_rating_caption_place))
        captionRatingPlace.setSpan(StyleSpan(Typeface.BOLD), 0, captionRatingPlace.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tableRatings!!.addView(getTableRow(tableRatings!!, captionRatingName, captionRatingValue, captionRatingPlace))

        for ((_, ratingItemInfo) in info.ratings) {
            layoutInflater!!.inflate(R.layout.item_profile_table_delimiter, tableRatings, true)
            val place: String
            if (isNarrowMode) {
                place = if (ratingItemInfo.value == 0.0)
                    getString(R.string.profile_rating_item_no_place_short)
                else
                    ratingItemInfo.place.toString()
            } else {
                place = if (ratingItemInfo.value == 0.0)
                    getString(R.string.profile_rating_item_no_place)
                else
                    getString(R.string.profile_rating_item_place, ratingItemInfo.place)
            }
            tableRatings!!.addView(getTableRow(tableRatings!!, ratingItemInfo.name, ratingItemInfo.place.toString(), place))
        }
    }

    private fun fillPlacesHistory(accountInfo: AccountInfo) {
        tablePlacesHistory!!.removeAllViews()

        val captionPlacesHistoryName = SpannableString(getString(R.string.profile_places_history_caption_name))
        captionPlacesHistoryName.setSpan(StyleSpan(Typeface.BOLD), 0, captionPlacesHistoryName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val captionPlacesHistoryValue = SpannableString(getString(R.string.profile_places_history_caption_value))
        captionPlacesHistoryValue.setSpan(StyleSpan(Typeface.BOLD), 0, captionPlacesHistoryValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val captionPlacesHistoryPlace = SpannableString(getString(R.string.profile_places_history_caption_place))
        captionPlacesHistoryPlace.setSpan(StyleSpan(Typeface.BOLD), 0, captionPlacesHistoryPlace.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tablePlacesHistory!!.addView(getTableRow(tablePlacesHistory!!, captionPlacesHistoryPlace, captionPlacesHistoryName, captionPlacesHistoryValue))

        val size = accountInfo.places.size
        if (size > 0) {
            accountInfo.places.sortedWith(Comparator { lhs, rhs ->
                if (lhs.count == rhs.count) {
                    lhs.place.name.compareTo(rhs.place.name, ignoreCase = true)
                } else {
                    (rhs.count - lhs.count).toInt()
                }
            })
            var lastCount = accountInfo.places[0].count
            for (i in 0 until size) {
                val accountPlaceHistoryInfo = accountInfo.places[i]
                if (isTablePlacesHistoryCollapsed) {
                    if (i >= PLACES_HISTORY_COUNT_POLITICS && accountPlaceHistoryInfo.count < lastCount) {
                        break
                    } else {
                        lastCount = accountPlaceHistoryInfo.count
                    }
                }

                layoutInflater!!.inflate(R.layout.item_profile_table_delimiter, tablePlacesHistory, true)
                tablePlacesHistory!!.addView(getTableRow(tablePlacesHistory!!, (i + 1).toString(),
                        accountPlaceHistoryInfo.place.name, accountPlaceHistoryInfo.count.toString()))
            }
        }

        tablePlacesHistorySwitcher!!.text = getString(if (isTablePlacesHistoryCollapsed)
            R.string.profile_places_history_expand
        else
            R.string.profile_places_history_collapse)
        tablePlacesHistorySwitcher!!.setOnClickListener { v ->
            isTablePlacesHistoryCollapsed = !isTablePlacesHistoryCollapsed
            fillPlacesHistory(accountInfo)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            presenter.dispose()
        }
    }

    companion object {

        private val NARROWNESS_MULTIPLIER_THRESHOLD = 30
        private val PLACES_HISTORY_COUNT_POLITICS = 10
    }

}
