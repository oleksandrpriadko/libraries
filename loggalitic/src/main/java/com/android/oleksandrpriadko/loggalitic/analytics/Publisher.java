package com.android.oleksandrpriadko.loggalitic.analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.oleksandrpriadko.loggalitic.analytics.converter.Converter;
import com.android.oleksandrpriadko.loggalitic.policy.Policy;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Oleksandr Priadko.
 * 6/9/17
 * <p>
 * Publish events to analytic services.
 */

public abstract class Publisher {

    private final Policy mPolicy;
    private List<Converter> mConverters = new ArrayList<>();

    public Publisher(Policy policy) {
        checkNotNull(policy);
        this.mPolicy = policy;
    }

    public Publisher(Policy policy, @NonNull List<Converter> converters) {
        this(policy);
        this.mConverters = converters;
    }

    //region Converters

    private void initConvertersIfNecessary() {
        if (this.mConverters == null) {
            this.mConverters = new ArrayList<>();
        }
    }

    public final void addConverter(@NonNull Converter... converter) {
        Collections.addAll(getConverters(), converter);
    }

    public final void removeConvertor(@NonNull Converter... converter) {
        for (Converter item : converter) {
            getConverters().remove(item);
        }
    }

    public final void setConverters(@NonNull List<Converter> converters) {
        this.mConverters = converters;
    }

    @NonNull
    private List<Converter> getConverters() {
        initConvertersIfNecessary();
        return this.mConverters;
    }

    @Nullable
    protected final Converter findConverter(@Converter.Type String type) {
        for (Converter converter : getConverters()) {
            if (converter.getType().equals(type)) {
                return converter;
            }
        }
        Log.d(getTag(), "findConverter: null. type = " + type);
        return null;
    }
    //endregion

    //region Event transformation
    public final void event(String name) {
        AnalyticsEvent event = new AnalyticsEvent(name);
        checkAndSend(event);
    }

    public final void event(String name, String description) {
        AnalyticsEvent event = new AnalyticsEvent(name, description);
        checkAndSend(event);
    }

    private void checkAndSend(AnalyticsEvent analyticsEvent) {
        if (isAllowedByPolicy(analyticsEvent)) {
            send(analyticsEvent);
        } else {
            Log.d(getTag(), "checkAndSend: notAllowed by mPolicy");
        }
    }
    //endregion

    //region Event sending

    /**
     * Ask implementations to send event.
     */
    public abstract void send(AnalyticsEvent event);
    //endregion

    //region Policy

    private boolean isAllowedByPolicy(AnalyticsEvent event) {
        return mPolicy.isEventAllowed(event);
    }
    //endregion

    protected String getTag() {
        return this.getClass().getSimpleName();
    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    public static class Param {
        // firebase
        public static final String ACHIEVEMENT_ID = "achievement_id";
        public static final String CHARACTER = "character";
        public static final String TRAVEL_CLASS = "travel_class";
        public static final String CONTENT_TYPE = "content_type";
        public static final String CURRENCY = "currency";
        public static final String COUPON = "coupon";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String FLIGHT_NUMBER = "flight_number";
        public static final String GROUP_ID = "group_id";
        public static final String ITEM_CATEGORY = "item_category";
        public static final String ITEM_ID = "item_id";
        public static final String ITEM_LOCATION_ID = "item_location_id";
        public static final String ITEM_NAME = "item_name";
        public static final String LOCATION = "location";
        public static final String LEVEL = "level";
        public static final String SIGN_UP_METHOD = "sign_up_method";
        public static final String NUMBER_OF_NIGHTS = "number_of_nights";
        public static final String NUMBER_OF_PASSENGERS = "number_of_passengers";
        public static final String NUMBER_OF_ROOMS = "number_of_rooms";
        public static final String DESTINATION = "destination";
        public static final String ORIGIN = "origin";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String SCORE = "score";
        public static final String SHIPPING = "shipping";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String SEARCH_TERM = "search_term";
        public static final String TAX = "tax";
        public static final String VALUE = "value";
        public static final String VIRTUAL_CURRENCY_NAME = "virtual_currency_name";
        public static final String CAMPAIGN = "campaign";
        public static final String SOURCE = "source";
        public static final String MEDIUM = "medium";
        public static final String TERM = "term";
        public static final String CONTENT = "content";
        public static final String ACLID = "aclid";
        public static final String CP1 = "cp1";
        public static final String ITEM_BRAND = "item_brand";
        public static final String ITEM_VARIANT = "item_variant";
        public static final String ITEM_LIST = "item_list";
        public static final String CHECKOUT_STEP = "checkout_step";
        public static final String CHECKOUT_OPTION = "checkout_option";
        public static final String CREATIVE_NAME = "creative_name";
        public static final String CREATIVE_SLOT = "creative_slot";
        public static final String AFFILIATION = "affiliation";
        public static final String INDEX = "index";

        protected Param() {
        }
    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    public static class Event {

        //region firebase events
        public static final String ADD_PAYMENT_INFO = "add_payment_info";
        public static final String ADD_TO_CART = "add_to_cart";
        public static final String ADD_TO_WISHLIST = "add_to_wishlist";
        public static final String APP_OPEN = "app_open";
        public static final String BEGIN_CHECKOUT = "begin_checkout";
        public static final String CAMPAIGN_DETAILS = "campaign_details";
        public static final String ECOMMERCE_PURCHASE = "ecommerce_purchase";
        public static final String GENERATE_LEAD = "generate_lead";
        public static final String JOIN_GROUP = "join_group";
        public static final String LEVEL_UP = "level_up";
        public static final String LOGIN = "login";
        public static final String POST_SCORE = "post_score";
        public static final String PRESENT_OFFER = "present_offer";
        public static final String PURCHASE_REFUND = "purchase_refund";
        public static final String SEARCH = "search";
        public static final String SELECT_CONTENT = "select_content";
        public static final String SHARE = "share";
        public static final String SIGN_UP = "sign_up";
        public static final String SPEND_VIRTUAL_CURRENCY = "spend_virtual_currency";
        public static final String TUTORIAL_BEGIN = "tutorial_begin";
        public static final String TUTORIAL_COMPLETE = "tutorial_complete";
        public static final String UNLOCK_ACHIEVEMENT = "unlock_achievement";
        public static final String VIEW_ITEM = "view_item";
        public static final String VIEW_ITEM_LIST = "view_item_list";
        public static final String VIEW_SEARCH_RESULTS = "view_search_results";
        public static final String EARN_VIRTUAL_CURRENCY = "earn_virtual_currency";
        public static final String REMOVE_FROM_CART = "remove_from_cart";
        public static final String CHECKOUT_PROGRESS = "checkout_progress";
        public static final String SET_CHECKOUT_OPTION = "set_checkout_option";
        //endregion

        //region own events
        public static final String GP_CLIENT_CONNECTION_FAILED = "GP_client_on_connection_failed";
        public static final String SERVICE_WIDGET_DELETED = "service_widget_deleted";
        public static final String SERVICE_WIDGET_ADDED = "service_widget_added";
        public static final String LAST_SERVICE_WIDGET_DELETED = "last_service_widget_deleted";
        public static final String USER_WANT_WIDGET_NO_POINTS = "user_want_widget_no_points";
        public static final String POINT_COORDINATES_ADDED = "point_coordinates_added";
        public static final String POINT_COORDINATES_SKIPPED = "point_coordinates_skipped";
        public static final String BACKED_BEFORE_ANIMATION_DONE = "backed_before_animation_done";
        public static final String POINT_ADDED = "point_added";
        public static final String POINT_EDITED = "point_edited";
        public static final String POINT_DELETED = "point_deleted";
        public static final String REFRESHES_POINT_IST = "refreshes_point_ist";
        public static final String WANT_DELETE_POINT = "want_delete_point";
        public static final String WANT_ADD_POINT = "want_add_point";
        public static final String WANT_EDIT_POINT = "want_edit_point";
        public static final String DISTANCE_CHANGED = "distance_changed";
        //endregion

        protected Event() {
        }
    }
}
