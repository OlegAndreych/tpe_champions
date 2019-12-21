package org.andreych.tpe_champions.configuration.mvc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configures some knobs in MVC part.
 *
 * Created by oleg on 30.05.17.
 */
@Configuration
open class WebMvcConfigurer @Autowired constructor(private val noCacheInterceptor: NoCacheInterceptor) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry?) {
        registry?.addInterceptor(noCacheInterceptor)
    }
}