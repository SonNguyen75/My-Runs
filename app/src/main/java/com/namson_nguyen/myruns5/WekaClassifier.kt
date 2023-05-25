package com.namson_nguyen.myruns5

internal object WekaClassifier {
    @Throws(Exception::class)
    fun classify(i: Array<Double>): Double {
        var p = Double.NaN
        p = N463233260(i)
        return p
    }

    fun N463233260(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[64] == null) {
            p = 0.0
        } else if ((i[64] as Double?)!!.toDouble() <= 1.05182) {
            p = 0.0
        } else if ((i[64] as Double?)!!.toDouble() > 1.05182) {
            p = N6264c56b1(i)
        }
        return p
    }

    fun N6264c56b1(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[0] == null) {
            p = 1.0
        } else if ((i[0] as Double?)!!.toDouble() <= 508.234256) {
            p = N106aad7c2(i)
        } else if ((i[0] as Double?)!!.toDouble() > 508.234256) {
            p = 2.0
        }
        return p
    }

    fun N106aad7c2(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[15] == null) {
            p = 1.0
        } else if ((i[15] as Double?)!!.toDouble() <= 7.361533) {
            p = 1.0
        } else if ((i[15] as Double?)!!.toDouble() > 7.361533) {
            p = N14c0a29f3(i)
        }
        return p
    }

    fun N14c0a29f3(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[1] == null) {
            p = 1.0
        } else if ((i[1] as Double?)!!.toDouble() <= 110.736261) {
            p = N43bc18224(i)
        } else if ((i[1] as Double?)!!.toDouble() > 110.736261) {
            p = 0.0
        }
        return p
    }

    fun N43bc18224(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[22] == null) {
            p = 2.0
        } else if ((i[22] as Double?)!!.toDouble() <= 3.356831) {
            p = 2.0
        } else if ((i[22] as Double?)!!.toDouble() > 3.356831) {
            p = N737c2a35(i)
        }
        return p
    }

    fun N737c2a35(i: Array<Double>): Double {
        var p = Double.NaN
        if (i[18] == null) {
            p = 0.0
        } else if ((i[18] as Double?)!!.toDouble() <= 3.373161) {
            p = 0.0
        } else if ((i[18] as Double?)!!.toDouble() > 3.373161) {
            p = 1.0
        }
        return p
    }
}