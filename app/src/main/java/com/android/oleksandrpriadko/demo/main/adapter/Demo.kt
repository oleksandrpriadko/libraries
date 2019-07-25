package com.android.oleksandrpriadko.demo.main.adapter

import androidx.annotation.DrawableRes

class Demo(val clazz: Class<*>?,
           val name: String,
           val avatarUrl: String?,
           @field:DrawableRes val iconResId: Int)