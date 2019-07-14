package com.android.oleksandrpriadko.loggalitic.analytics

import com.android.oleksandrpriadko.core.policy.AnalyticsEvent
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter
import com.android.oleksandrpriadko.loggalitic.policy.DefaultPolicy
import com.android.oleksandrpriadko.core.policy.Policy
import java.util.*

/**
 * Publish events to analytic services.
 */

abstract class Publisher(private val policy: Policy) {
    private var converters: MutableList<Converter<*>> = mutableListOf()

    protected val tag: String
        get() = this.javaClass.simpleName

    constructor(policy: Policy, converters: List<Converter<*>>) : this(policy) {
        this.converters.clear()
        this.converters.addAll(converters)
    }

    fun addConverter(vararg converter: Converter<*>) {
        Collections.addAll(converters, *converter)
    }

    fun removeConvertor(vararg converter: Converter<*>) {
        for (item in converter) {
            converters.remove(item)
        }
    }

    protected fun <P : Converter<*>> findConverter(clazz: Class<*>): P? {
        for (converter in converters) {
            if (converter.getType() == clazz) {
                return converter as P
            }
        }
        LogPublishService.logger().d(tag, "no such converter ${clazz.simpleName}")
        return null
    }

    fun event(name: String): Boolean {
        val event = AnalyticsEvent(name)
        return checkAndSend(event)
    }

    fun event(name: String, description: String): Boolean {
        val event = AnalyticsEvent(name, description)
        return checkAndSend(event)
    }

    private fun checkAndSend(analyticsEvent: AnalyticsEvent): Boolean {
        return if (isAllowedByPolicy(analyticsEvent)) {
            send(analyticsEvent)
        } else {
            LogPublishService.logger().d(tag, "checkAndSend: notAllowed by policy")
            false
        }
    }

    /**
     * Ask implementations to send event.
     */
    abstract fun send(event: AnalyticsEvent): Boolean

    private fun isAllowedByPolicy(event: AnalyticsEvent): Boolean {
        return policy.isEventAllowed(event)
    }

    @Suppress("unused")
    class Param protected constructor() {
        companion object {
            // firebase
            val ACHIEVEMENT_ID = "achievement_id"
            val CHARACTER = "character"
            val TRAVEL_CLASS = "travel_class"
            val CONTENT_TYPE = "content_type"
            val CURRENCY = "currency"
            val COUPON = "coupon"
            val START_DATE = "start_date"
            val END_DATE = "end_date"
            val FLIGHT_NUMBER = "flight_number"
            val GROUP_ID = "group_id"
            val ITEM_CATEGORY = "item_category"
            val ITEM_ID = "item_id"
            val ITEM_LOCATION_ID = "item_location_id"
            val ITEM_NAME = "item_name"
            val LOCATION = "location"
            val LEVEL = "level"
            val SIGN_UP_METHOD = "sign_up_method"
            val NUMBER_OF_NIGHTS = "number_of_nights"
            val NUMBER_OF_PASSENGERS = "number_of_passengers"
            val NUMBER_OF_ROOMS = "number_of_rooms"
            val DESTINATION = "destination"
            val ORIGIN = "origin"
            val PRICE = "price"
            val QUANTITY = "quantity"
            val SCORE = "score"
            val SHIPPING = "shipping"
            val TRANSACTION_ID = "transaction_id"
            val SEARCH_TERM = "search_term"
            val TAX = "tax"
            val VALUE = "value"
            val VIRTUAL_CURRENCY_NAME = "virtual_currency_name"
            val CAMPAIGN = "campaign"
            val SOURCE = "source"
            val MEDIUM = "medium"
            val TERM = "term"
            val CONTENT = "content"
            val ACLID = "aclid"
            val CP1 = "cp1"
            val ITEM_BRAND = "item_brand"
            val ITEM_VARIANT = "item_variant"
            val ITEM_LIST = "item_list"
            val CHECKOUT_STEP = "checkout_step"
            val CHECKOUT_OPTION = "checkout_option"
            val CREATIVE_NAME = "creative_name"
            val CREATIVE_SLOT = "creative_slot"
            val AFFILIATION = "affiliation"
            val INDEX = "index"
        }
    }

    @Suppress("unused")
    class Event protected constructor() {
        companion object {

            val ADD_PAYMENT_INFO = "add_payment_info"
            val ADD_TO_CART = "add_to_cart"
            val ADD_TO_WISHLIST = "add_to_wishlist"
            val APP_OPEN = "app_open"
            val BEGIN_CHECKOUT = "begin_checkout"
            val CAMPAIGN_DETAILS = "campaign_details"
            val ECOMMERCE_PURCHASE = "ecommerce_purchase"
            val GENERATE_LEAD = "generate_lead"
            val JOIN_GROUP = "join_group"
            val LEVEL_UP = "level_up"
            val LOGIN = "login"
            val POST_SCORE = "post_score"
            val PRESENT_OFFER = "present_offer"
            val PURCHASE_REFUND = "purchase_refund"
            val SEARCH = "search"
            val SELECT_CONTENT = "select_content"
            val SHARE = "share"
            val SIGN_UP = "sign_up"
            val SPEND_VIRTUAL_CURRENCY = "spend_virtual_currency"
            val TUTORIAL_BEGIN = "tutorial_begin"
            val TUTORIAL_COMPLETE = "tutorial_complete"
            val UNLOCK_ACHIEVEMENT = "unlock_achievement"
            val VIEW_ITEM = "view_item"
            val VIEW_ITEM_LIST = "view_item_list"
            val VIEW_SEARCH_RESULTS = "view_search_results"
            val EARN_VIRTUAL_CURRENCY = "earn_virtual_currency"
            val REMOVE_FROM_CART = "remove_from_cart"
            val CHECKOUT_PROGRESS = "checkout_progress"
            val SET_CHECKOUT_OPTION = "set_checkout_option"

            val GP_CLIENT_CONNECTION_FAILED = "GP_client_on_connection_failed"
            val SERVICE_WIDGET_DELETED = "service_widget_deleted"
            val SERVICE_WIDGET_ADDED = "service_widget_added"
            val LAST_SERVICE_WIDGET_DELETED = "last_service_widget_deleted"
            val USER_WANT_WIDGET_NO_POINTS = "user_want_widget_no_points"
            val POINT_COORDINATES_ADDED = "point_coordinates_added"
            val POINT_COORDINATES_SKIPPED = "point_coordinates_skipped"
            val BACKED_BEFORE_ANIMATION_DONE = "backed_before_animation_done"
            val POINT_ADDED = "point_added"
            val POINT_EDITED = "point_edited"
            val POINT_DELETED = "point_deleted"
            val REFRESHES_POINT_IST = "refreshes_point_ist"
            val WANT_DELETE_POINT = "want_delete_point"
            val WANT_ADD_POINT = "want_add_point"
            val WANT_EDIT_POINT = "want_edit_point"
            val DISTANCE_CHANGED = "distance_changed"
        }
    }

    private class DummyPublisher internal constructor(policy: Policy) : Publisher(policy) {

        override fun send(event: AnalyticsEvent): Boolean {
            return false
        }
    }

    companion object {

        val NOT_SET: Publisher = DummyPublisher(DefaultPolicy())
    }
}
