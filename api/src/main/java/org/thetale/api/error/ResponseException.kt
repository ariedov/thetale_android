package org.thetale.api.error

class ResponseException(val error: String? = null,
                        val errors: Map<String, List<String>>?): RuntimeException()