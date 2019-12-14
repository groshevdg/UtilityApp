package ru.groshevdg.utilityhelper.data

import android.provider.BaseColumns

class UtilityContract {

    class AllObjects : BaseColumns {

        companion object {
            val TABLE_NAME = "objects"
            val _ID = BaseColumns._ID
            val CURRENT_OBJECT = "current_object"
        }
    }

    class WaterData : BaseColumns {

        companion object {
            val TABLE_NAME = "water"
            val _ID = BaseColumns._ID
            val OBJECT = "object"
            val COLD = "cold"
            val WARM = "warm"
            val SEWERAGE = "sewerage"
            val MONTH = "month"
            val YEAR = "year"
            val SUM = "sum"
        }
    }

    class GasData : BaseColumns {

        companion object {
            val TABLE_NAME = "gas"
            val _ID = BaseColumns._ID
            val OBJECT = "object"
            val VALUE = "value"
            val MONTH = "month"
            val YEAR = "year"
            val SUM = "sum"
        }
    }

    class LightData : BaseColumns {

        companion object {
            val TABLE_NAME = "light"
            val _ID = BaseColumns._ID
            val OBJECT = "object"
            val DAY = "day"
            val NIGHT = "night"
            val MONTH = "month"
            val YEAR = "year"
            val SUM = "sum"
        }
    }
}