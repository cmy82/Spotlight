# Spotlight

![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)


This library is a modification of TakuSemba/Spotlight, modified and reworked to allow Single or Multiple
targets, custom direct drawing of a text description of the highlighted target, and closing on touch.

## Usage

Here are some example usages pulled directly from another project.

#Target
```kt
val menuTarget = SingleTarget.Builder()
    .setAnchorWithOffset(toolbar, -(toolbar.width/2f)+ImgUtils.dpToPx(30), 0f)
    .setShape(Circle(100f))
    .setOverlay(helpBack!!)
    .setEffect(PulseEffect(Triple(100f,0f,0f), 150f, Color.parseColor("#2e7d32"), 2000))
    .setOnRenderListener(object: OnTargetRenderListener {
        val bmp = RandomSpotlightArrow.getRandomArrowBitmap(ImgUtils.dpToPx(96).toFloat(), false, 180f)
        val msgBmp = Bitmap.createBitmap(requireActivity().window.decorView.width,requireActivity().window.decorView.height, Bitmap.Config.ARGB_8888)
        var msgRenderd = false
        override fun onRendered(c: Canvas, t: Target, p: Paint) {
            p.apply {
                alpha = 255
                colorFilter = PorterDuffColorFilter(Color.parseColor("#ffff0000"), PorterDuff.Mode.SRC_IN)
            }
            c.drawBitmap(bmp, t.anchor.x + 64f, t.anchor.y + 64f, p)
            if(!msgRenderd) {
                val ax = t.anchor.x + 64f + bmp.width/2
                val ay = t.anchor.y + 64f + bmp.height/2
                val bpl = IntArray(2)
                val bnl = IntArray(2)
                bPrev?.getLocationOnScreen(bpl)
                bNext?.getLocationOnScreen(bnl)
                val hits = listOf(HitArea(HitArea.CIRCLE, PointF(ax,ay), ImgUtils.dpToPx(64).toFloat()),
                    HitArea(HitArea.RECT, PointF(bpl[0].toFloat(), bpl[1].toFloat()), bPrev!!.width.toFloat(), bPrev!!.height.toFloat()),
                    HitArea(HitArea.RECT, PointF(bnl[0].toFloat(), bnl[1].toFloat()), bNext!!.width.toFloat(), bNext!!.height.toFloat()))
                t.renderTargetMessage(msgBmp, PointF(ax, ay), msgBmp.width - ax - 20, hits)
                msgRenderd = true
            }
            p.apply {
                colorFilter = null
            }
            c.drawBitmap(msgBmp, 0f, 0f, p)
            //Timber.e(t.message)
        }
    })
    .setOnTargetListener(object: OnTargetListener {
        override fun onStarted() {
            bPrev?.visibility = View.VISIBLE
            bNext?.visibility = View.VISIBLE
            bPrevTop?.visibility = View.GONE
            bNextTop?.visibility = View.GONE
        }
        override fun onEnded() { }
    })
    .setMessage("Welcome to the start of savings. \n Click the menu button to access your grocery lists, find stores, scan receipts, and more. \n " +
            "Click anywhere on the screen to close this help info (it can be accessed again at anytime from the menu).")
    .build()
helpTargets.add(menuTarget)
```

```kt
var v = (slistView.recyclerShopLists.findViewHolderForAdapterPosition(0)?.itemView)
    var cld: View? = null
    val pos = IntArray(2)
    var offst = 0f
    if(v != null) {
        //Timber.e("V is of type ${v.javaClass}")
        v.getLocationOnScreen(pos)
        cld = v.findViewById<View>(R.id.grocery_item_card)
        if(cld != null) {
            cld.getLocationOnScreen(pos)
        } else v = null
    }
    if(v == null) {
        v = (slistView.recyclerShopLists.findViewHolderForAdapterPosition(1)?.itemView)
        if(v != null) {
            //Timber.e("(2) V is of type ${v.javaClass}")
            v.getLocationOnScreen(pos)
            cld = v.findViewById<View>(R.id.grocery_item_card)
            if(cld != null) {
                cld.getLocationOnScreen(pos)
            } else v = null
        }
    }
    if(cld==null){
        slistView.recyclerShopLists.getLocationOnScreen(pos)
        offst = ResourcesCompat.getDrawable(resources, R.drawable.tgt_item_list, null)!!.intrinsicHeight / 2f
        //Timber.w("Unable to locate item card ..... defaulting to stored image")
    } else {
        offst = cld.height / 2f
    }
    val itemTarget = SingleTarget.Builder()
        .setAnchor(pos[0].toFloat() + slistView.recyclerShopLists.width/2, pos[1].toFloat() + offst)
        //.setAnchorWithOffset(spriceView.recyclerShopPrice, 0f, 0f)
        .setShape(RoundedRectangle(2*offst, slistView.recyclerShopLists.width.toFloat(), 5f))
        .setOverlay(helpBack!!)
        .setEffect(PulseEffect(Triple(50f,slistView.recyclerShopLists.width.toFloat(),2*offst + 25), 5f,
                               ContextCompat.getColor(requireContext(), R.color.colorAccent),2000, effectShape = EFFECT_SHAPE_RECTANGLE))
        .setOnRenderListener(object: OnTargetRenderListener {
            var dflt = ResourcesCompat.getDrawable(resources, R.drawable.tgt_item_list, null)!!
            var bmp: Bitmap? = null
            val msgBmp = Bitmap.createBitmap(requireActivity().window.decorView.width,requireActivity().window.decorView.height, Bitmap.Config.ARGB_8888)
            var msgRenderd = false
            var bmpRendered = false
            val itmsInList = (v!=null)
            override fun onPreRender(c: Canvas, t: Target, p: Paint) {
                //Timber.w("Is there an item in recyclerlist: $itmsInList")
                if(!itmsInList){
                    if(!bmpRendered){
                        val ratio = dflt.intrinsicWidth / slistView.recyclerShopLists.width.toFloat()
                        bmp = dflt.toBitmap(slistView.recyclerShopLists.width, (dflt.intrinsicHeight * ratio).toInt())
                        bmpRendered = true
                    }
                    c.drawBitmap(bmp!!, 0f, pos[1].toFloat(), p)
                }
            }
            override fun onRendered(c: Canvas, t: Target, p: Paint) {
                //Timber.w("Is there an item in recyclerlist: $itmsInList")
                if(!msgRenderd) {
                    val ax = t.anchor.x
                    val ay = t.anchor.y + offst + ImgUtils.dpToPx(36f)
                    val bpl = IntArray(2)
                    val bnl = IntArray(2)
                    val scrnCntr = requireActivity().window.decorView.width/2 // Want centered on object offset from center of screen, so available
                    val offset = Math.abs(scrnCntr - ax)                      // width is ScreenWidth - (2 * offset width)
                    bPrev?.getLocationOnScreen(bpl)
                    bNext?.getLocationOnScreen(bnl)
                    val hits = listOf(HitArea(HitArea.RECT, PointF(bpl[0].toFloat(), bpl[1].toFloat()), bPrev!!.width.toFloat(), bPrev!!.height.toFloat()),
                                      HitArea(HitArea.RECT, PointF(bnl[0].toFloat(), bnl[1].toFloat()), bNext!!.width.toFloat(), bNext!!.height.toFloat()))
                    t.renderTargetMessage(msgBmp, PointF(ax, ay), msgBmp.width - (offset*2f) - 30f, hits)
                    msgRenderd = true
                }
                p.apply {
                    colorFilter = null
                }
                c.drawBitmap(msgBmp, 0f, 0f, p)
            }
        })
        .setOnTargetListener(object: OnTargetListener {
            override fun onStarted() {
                bPrevTop?.visibility = View.GONE
                bNextTop?.visibility = View.GONE
                bPrev?.visibility = View.VISIBLE
                bNext?.visibility = View.VISIBLE
            }
            override fun onEnded() { }
        })
        .setMessage("Click on the remove button to remove an item from the list (when searching, click on the add button to add it to the list). \n " +
                    "When searching there will be a generic item added to the list, that you can add if unsure of which particular item you want " +
                    "(i.e. what specific bread).")
        .build()
helpTargets.add(itemTarget)
```

#Spotlight
```kt
val spotlight = Spotlight.Builder(requireActivity())
            .setTargets(helpTargets)
            .setDuration(1000)
            .setBackgroundColorRes(R.color.spotlight_bg)
            .setCloseOnTouch(true)
            .build()
```
