package com.app.socialapp.service

import feign.FeignException
import feign.Response
import feign.codec.ErrorDecoder

// Error handling config

class CustomErrorDecoder : ErrorDecoder {

    override fun decode(methodKey: String?, response: Response): Exception {
        return when (response.status()) {
            400 -> FeignException.BadRequest("Bad Request", response.request(), null,null)
            401 -> FeignException.Unauthorized("Unauthorized", response.request(), null,null)
            403 -> FeignException.Forbidden("Forbidden", response.request(), null,null)
            404 -> FeignException.NotFound("Not Found", response.request(), null,null)
            500 -> FeignException.InternalServerError("Server Error", response.request(), null,null)
            else -> FeignException.errorStatus(methodKey, response)
        }
    }

}
