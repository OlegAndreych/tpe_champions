package org.andreych.tpe_champions.configuration.mvc

import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Sets "no-cache" http header on http responses.
 *
 * Created by oleg on 30.05.17.
 */
@Component
class NoCacheInterceptor : HandlerInterceptorAdapter()
{
    override fun postHandle(request: HttpServletRequest?,
                            response: HttpServletResponse?,
                            handler: Any?,
                            modelAndView: ModelAndView?)
    {
        response?.addHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noStore().headerValue)
    }
}