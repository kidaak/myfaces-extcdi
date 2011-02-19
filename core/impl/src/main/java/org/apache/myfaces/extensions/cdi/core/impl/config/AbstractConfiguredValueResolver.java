/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.extensions.cdi.core.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.config.ConfiguredValueResolver;
import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;
import org.apache.myfaces.extensions.cdi.core.api.ClassDeactivator;

import java.util.List;
import java.util.ArrayList;
import java.util.ServiceLoader;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractConfiguredValueResolver implements ConfiguredValueResolver
{
    private boolean deactivated = false;

    private List instances = new ArrayList();
    private List<String> configuredValues = new ArrayList<String>();

    protected AbstractConfiguredValueResolver()
    {
        ServiceLoader<ClassDeactivator> serviceLoader =
                ServiceLoader.load(ClassDeactivator.class, ClassUtils.getClassLoader(null));

        for(ClassDeactivator currentInstance : serviceLoader)
        {
            if(currentInstance.getDeactivatedClasses().contains(getClass()))
            {
                this.deactivated = true;
                break;
            }
        }
    }

    protected <T> void add(Class<T> targetType)
    {
        T instance = ClassUtils.tryToInstantiateClass(targetType);

        if(instance != null)
        {
            instances.add(instance);
        }
    }

    protected <T> void add(T value)
    {
        if(!String.class.isAssignableFrom(value.getClass()))
        {
            instances.add(value);
        }
        else
        {
            configuredValues.add((String)value);
        }
    }

    protected <T> List<T> getConfiguredValues(Class<T> targetType)
    {
        if(this.instances.size() > 0 && this.configuredValues.size() > 0)
        {
            //TODO
            throw new IllegalStateException("Mixed config found.");
        }

        if(!String.class.isAssignableFrom(targetType))
        {
            return this.instances;
        }
        return (List<T>)this.configuredValues;
    }

    public boolean isActivated()
    {
        return !deactivated;
    }
}