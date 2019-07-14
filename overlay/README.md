#### Display simple Overlay:
1) Create Overlay:
```java
Overlay overlay = new Overlay.Builder(rootViewGroup).build();
```
2) Create OverlayManager or reuse existing from WgtBaseFragmentActivity
```java
OverlayManager OverlayManager = new OverlayManager(containerForAllOverlays);
```
3) Add new Overlay:
```java
OverlayManager.add(overlay);
```
*Note: Will not proceed if OverlayManager#dismissAll() is in progress*


#### Display simple Overlay with default inner/outer animations for backgroundView and default inner/outer animations for contentView
1) Create Overlay with backgroundView and contentView:
```java
Overlay overlay = new Overlay.Builder(createViewGroup(), id)

            .backgroundView(R.id.back)
            
            .contentView(R.id.content)

            .build();
```
2) Create OverlayManager or reuse existing from WgtBaseFragmentActivity
```java
OverlayManager OverlayManager = new OverlayManager(containerForAllOverlays);
```
3) Add Overlay to queue (will be shown immediately if first it queue):
```java
OverlayManager.add(overlay);
```
*Note: Will not proceed if OverlayManager#hideAll() is in progress*


#### Display simple Overlay with custom inner/outer animations for backgroundView and custom inner/outer animations for contentView
1) Create Overlay with backgroundView and contentView:
```java
Overlay overlay = new Overlay.Builder(createViewGroup(), id)

            .backgroundView(R.id.back)
            .animationShowBackground(R.anim.slide_up_popup_fill_after)
            .animationHideBackground(R.anim.slide_down_popup_fill_after)

            .contentView(R.id.content)
            .animationShowContent(R.anim.slide_up_popup_fill_after)
            .animationHideContent(R.anim.slide_down_popup_fill_after)

            .build();
```
2) Create OverlayManager or reuse existing from WgtBaseFragmentActivity
```java
OverlayManager OverlayManager = new OverlayManager(containerForAllOverlays);
```
3) Add Overlay:
```java
OverlayManager.add(overlay);
```
*Note: Will not proceed if OverlayManager#dismissAll() is in progress*

#### Display simple Overlay with custom inner/outer animations for root view
1) Create Overlay with backgroundView and contentView:
```java
Overlay overlay = new Overlay.Builder(rootView, id)

            .animationShowRoot(R.anim.slide_up)
            .animationHideRoot(R.anim.slide_down)
            .build();
```
2) Create OverlayManager or reuse existing from WgtBaseFragmentActivity
```java
OverlayManager OverlayManager = new OverlayManager(containerForAllOverlays);
```
3) Add Overlay:
```java
OverlayManager.add(overlay);
```
*Note: Will not proceed if OverlayManager#dismissAll() is in progress*

#### Hide last added Overlay
```java
overlayManager.hideLast();
```
*Note: Will not proceed if OverlayManager#hideAll() is in progress*


#### Hide all added Overlay
```java
overlayManager.hideAll();
```
*Note: Will not proceed if OverlayManager#hideAll() is in progress*