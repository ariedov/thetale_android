package com.dleibovych.epictale.fragment

import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView

import com.dleibovych.epictale.DataViewMode
import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.api.ApiResponseCallback
import com.dleibovych.epictale.api.request.AccountInfoRequest
import com.dleibovych.epictale.api.response.AccountInfoResponse
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.UiUtils

import java.net.CookieManager
import java.util.Comparator

import javax.inject.Inject

import okhttp3.OkHttpClient

class ProfileFragment : WrapperFragment() {

    @Inject lateinit var client: OkHttpClient
    @Inject lateinit var manager: CookieManager

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
        (activity!!.application as TheTaleApplication)
                .getApplicationComponent()
                .inject(this)


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

        return wrapView(layoutInflater, rootView)
    }

    override fun refresh(isGlobal: Boolean) {
        super.refresh(isGlobal)

        if (isGlobal) {
            isTablePlacesHistoryCollapsed = true
            isNarrowMode = true
        }

        val watchingAccountId = PreferencesManager.getWatchingAccountId()
        val accountId = if (watchingAccountId == 0) PreferencesManager.getAccountId() else watchingAccountId
        AccountInfoRequest(client, manager, accountId).execute(object : ApiResponseCallback<AccountInfoResponse> {
            override fun processResponse(response: AccountInfoResponse) {
                if (!isAdded) {
                    return
                }

                if (isGlobal) {
                    tableRatings!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            if (tableRatings!!.width > 0) {
                                val textRect = Rect()
                                textRatingsDescription!!.paint.getTextBounds("M", 0, 1, textRect)
                                isNarrowMode = tableRatings!!.width.toDouble() / textRect.width() < NARROWNESS_MULTIPLIER_THRESHOLD

                                UiUtils.removeGlobalLayoutListener(tableRatings!!, this)
                                if (!isNarrowMode) {
                                    fillRatings(response)
                                }
                            }
                        }
                    })
                }

                textName!!.text = Html.fromHtml(getString(R.string.find_player_info_short, response.name))
                textAffectGame!!.text = getString(if (response.canAffectGame) R.string.game_affect_true else R.string.game_affect_false)
                textMight!!.text = Math.floor(response.might).toInt().toString()
                textAchievementPoints!!.text = response.achievementPoints.toString()
                textCollectionItemsCount!!.text = response.collectionItemsCount.toString()
                textReferralsCount!!.text = response.referralsCount.toString()

                fillRatings(response)
                fillPlacesHistory(response)

                setMode(DataViewMode.DATA)
            }

            override fun processError(response: AccountInfoResponse) {
                setError(response.errorMessage)
            }
        })
    }

    private fun getTableRow(text1: CharSequence, text2: CharSequence?, text3: CharSequence): View {
        val row = layoutInflater!!.inflate(R.layout.item_profile_table, null)
        (row.findViewById<View>(R.id.item_profile_table_text_1) as TextView).text = text1
        (row.findViewById<View>(R.id.item_profile_table_text_2) as TextView).text = text2
        (row.findViewById<View>(R.id.item_profile_table_text_3) as TextView).text = text3
        return row
    }

    private fun fillRatings(accountInfoResponse: AccountInfoResponse) {
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
        tableRatings!!.addView(getTableRow(captionRatingName, captionRatingValue, captionRatingPlace))

        for ((key, ratingItemInfo) in accountInfoResponse.ratings) {
            layoutInflater!!.inflate(R.layout.item_profile_table_delimiter, tableRatings, true)
            val value = key.getValue(ratingItemInfo.value)
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
            tableRatings!!.addView(getTableRow(ratingItemInfo.name, value, place))
        }
    }

    private fun fillPlacesHistory(accountInfoResponse: AccountInfoResponse) {
        tablePlacesHistory!!.removeAllViews()

        val captionPlacesHistoryName = SpannableString(getString(R.string.profile_places_history_caption_name))
        captionPlacesHistoryName.setSpan(StyleSpan(Typeface.BOLD), 0, captionPlacesHistoryName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val captionPlacesHistoryValue = SpannableString(getString(R.string.profile_places_history_caption_value))
        captionPlacesHistoryValue.setSpan(StyleSpan(Typeface.BOLD), 0, captionPlacesHistoryValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val captionPlacesHistoryPlace = SpannableString(getString(R.string.profile_places_history_caption_place))
        captionPlacesHistoryPlace.setSpan(StyleSpan(Typeface.BOLD), 0, captionPlacesHistoryPlace.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tablePlacesHistory!!.addView(getTableRow(captionPlacesHistoryPlace, captionPlacesHistoryName, captionPlacesHistoryValue))

        val size = accountInfoResponse.placesHistory.size
        if (size > 0) {
            accountInfoResponse.placesHistory.sortWith(Comparator { lhs, rhs ->
                if (lhs.helpCount == rhs.helpCount) {
                    lhs.name.compareTo(rhs.name, ignoreCase = true)
                } else {
                    rhs.helpCount - lhs.helpCount
                }
            })
            var lastCount = accountInfoResponse.placesHistory[0].helpCount
            for (i in 0 until size) {
                val accountPlaceHistoryInfo = accountInfoResponse.placesHistory[i]
                if (isTablePlacesHistoryCollapsed) {
                    if (i >= PLACES_HISTORY_COUNT_POLITICS && accountPlaceHistoryInfo.helpCount < lastCount) {
                        break
                    } else {
                        lastCount = accountPlaceHistoryInfo.helpCount
                    }
                }

                layoutInflater!!.inflate(R.layout.item_profile_table_delimiter, tablePlacesHistory, true)
                tablePlacesHistory!!.addView(getTableRow((i + 1).toString(),
                        accountPlaceHistoryInfo.name, accountPlaceHistoryInfo.helpCount.toString()))
            }
        }

        tablePlacesHistorySwitcher!!.text = getString(if (isTablePlacesHistoryCollapsed)
            R.string.profile_places_history_expand
        else
            R.string.profile_places_history_collapse)
        tablePlacesHistorySwitcher!!.setOnClickListener { v ->
            isTablePlacesHistoryCollapsed = !isTablePlacesHistoryCollapsed
            fillPlacesHistory(accountInfoResponse)
        }
    }

    companion object {

        private val NARROWNESS_MULTIPLIER_THRESHOLD = 30
        private val PLACES_HISTORY_COUNT_POLITICS = 10
    }

}
