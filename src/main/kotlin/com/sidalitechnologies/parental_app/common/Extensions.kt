package com.sidalitechnologies.parental_app.common

import kotlin.reflect.full.memberProperties

fun <T : Any> T.toNonNullMap(): Map<String, Any> {
    return this::class
        .memberProperties
        .filter { it.getter.call(this) != null } // Filter out null properties
        .associate { it.name to it.getter.call(this)!! } // Map property names to non-null values
}
