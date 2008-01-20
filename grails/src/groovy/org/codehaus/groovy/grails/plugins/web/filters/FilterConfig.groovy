/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.plugins.web.filters

import org.springframework.web.servlet.ModelAndView
import org.codehaus.groovy.grails.commons.GrailsClassUtils

/**
 * @author mike
 * @author Graeme Rocher
 */
class FilterConfig {
    String name
    Map scope
    Closure before
    Closure after
    Closure afterView
    ModelAndView modelAndView
    boolean initialised = false

    /**
     * Redirects attempt to access an 'errors' property, so we provide
     * one here with a null value.
     */
    def errors = null

    /**
     * This is the filters definition bean that declared the filter
     * config. Since it may contain injected services, etc., we
     * delegate any missing properties or methods to it.
     */
    def filtersDefinition

    def propertyMissing(String propertyName) {
        if(!initialised) {
            log.warn "Setting $propertyName is invalid for filter config $name"
        }

        // Delegate to the parent definition if it has this property.
        if (this.filtersDefinition.metaClass.hasProperty(this.filtersDefinition, propertyName)) {
            def getterName = GrailsClassUtils.getGetterName(propertyName)
            this.metaClass."$getterName" = {-> this.filtersDefinition."$propertyName" }
            return this.filtersDefinition."$propertyName"
        }
        else {
            throw new MissingPropertyException(propertyName, this.class)
        }
    }

    def methodMissing(String methodName, args) {
        // Delegate to the parent definition if it has this method.
        if (this.filtersDefinition.metaClass.respondsTo(this.filtersDefinition, methodName)) {
            this.metaClass."$methodName" = { varArgs -> this.filtersDefinition."$methodName"(varArgs) }
            return this.filtersDefinition."$methodName"(args)
        }
        else {
            throw new MissingMethodException(methodName, this.class, args)
        }
    }
    
    public String toString() {"FilterConfig[$name, scope=$scope]"}
}
