package com.android.oleksandrpriadko.overlay;

import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.android.oleksandrpriadko.overlay.Overlay.OverlayListener;

public interface OverlayHelper {
    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#areAllHidden
     */
    boolean areAllHidden();

    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#isOverlayOnTop
     */
    boolean isOverlayOnTop(@NonNull final Overlay overlay);

    /**
     * Use this version of the method if you used addViewAsDefaultOverlay(ViewGroup)
     *
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#isOverlayOnTop
     */
    boolean isOverlayOnTop(final ViewGroup rootViewGroupInOverlayHolder);

    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#add
     */
    void addOverlay(Overlay overlay);

    /**
     * Create default Overlay with:
     * - viewGroup as root view
     * - default slide up and slide down animations for content view
     * - default fade in and fade out animations for background view
     * <p>
     * and add it to OverlayManager
     */
    default void addViewAsOverlay(@NonNull final ViewGroup viewGroup) {
        addOverlay(
                getDefaultOverlayHolderBuilder(
                        viewGroup, R.id.contentOverlayLayout, R.id.backgroundOverlayLayout)
                        .build());
    }

    /**
     * Create default Overlay with:
     * - viewGroup as root view
     * - default slide up and slide down animations for content view
     * - default fade in and fade out animations for background view
     * - provided overlay listener
     * <p>
     * and add it to OverlayManager
     */
    default void addViewAsOverlay(@NonNull final ViewGroup viewGroup,
                                  final OverlayListener overlayListener) {
        addOverlay(
                getDefaultOverlayHolderBuilder(
                        viewGroup, R.id.contentOverlayLayout, R.id.backgroundOverlayLayout)
                        .overlayListener(overlayListener)
                        .build());
    }

    /**
     * Create default Overlay with:
     * - viewGroup as root view
     * - default slide up and slide down animations for content view
     * - default fade in and fade out animations for background view
     * - provided overlay listener
     * <p>
     * and add it to OverlayManager
     */
    default void addViewAsOverlay(@NonNull final ViewGroup viewGroup,
                                  @IdRes final int contentId,
                                  @IdRes final int backgroundId,
                                  final OverlayListener overlayListener) {
        addOverlay(
                getDefaultOverlayHolderBuilder(
                        viewGroup, contentId, backgroundId).overlayListener(overlayListener)
                        .build());
    }

    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#hideLast
     */
    boolean hideLastOverlay();

    boolean hideLastOverlay(final boolean onBackPressed);

    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#hideAll
     */
    void hideAllOverlays();

    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#stop
     */
    void stopOverlayManager();

    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#resume
     */
    void resumeOverlayManager();

    /**
     * @see com.android.oleksandrpriadko.overlay.OverlayManager#destroy
     */
    void destroyOverlayManager();

    /**
     * Create default Overlay with:
     * - viewGroup as root view
     * - default fade in and fade out animation for background view
     * - default slide up and slide down animation for content view
     */
    @NonNull
    default Overlay.Builder getDefaultOverlayHolderBuilder(@NonNull final ViewGroup viewGroup,
                                                           @IdRes final int contentId,
                                                           @IdRes final int backgroundId) {
        return new Overlay.Builder(viewGroup)

                .contentView(contentId)
                .animationShowContent(R.anim.overlay_module_slide_up)
                .animationHideContent(R.anim.overlay_module_slide_down)

                .backgroundView(backgroundId)
                .animationShowBackground(R.anim.overlay_module_fade_in)
                .animationHideBackground(R.anim.overlay_module_fade_out);
    }
}
