package com.android.oleksandrpriadko.retrofit

class HeaderInterceptorMashape : HeaderInterceptor() {

    override val headers: Array<HeaderInterceptor.Header>
        get() {
            val header = HeaderInterceptor.Header(MASHAPE_TITLE, MASHAPE_VALUE)
            return arrayOf(header)
        }

    companion object {
        private const val MASHAPE_TITLE = "X-Mashape-Key"
        private const val MASHAPE_VALUE = "ZqZDokvSBlmshEMoSxzqhog0kOcOp1tnJgvjsnGOhZhcrGnywl"
    }
}
