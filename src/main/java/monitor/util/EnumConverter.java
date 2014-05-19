/*
 * Copyright 2014 Martin Steiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monitor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public class EnumConverter<T extends Enum<T>> implements TypeConverter {
	 
    private static final Logger logger = LoggerFactory.getLogger(EnumConverter.class);
	
    private final Class<T> clazz;
 
    public EnumConverter(Class<T> clazz)
    {
    	this.clazz = clazz;
    }
    
    @Override
	public T fromNative(Object input, FromNativeContext context) {
        Integer i = (Integer) input;
        
        Class<?> targetClass = context.getTargetType();
        if (!clazz.isAssignableFrom(targetClass)) {
            return null;
        }
        
        Object[] enums = targetClass.getEnumConstants();
        if (enums.length == 0) {
            logger.error("Could not convert desired enum type (), no valid values are defined.",targetClass.getName());
            return null;
        }
        // In order to avoid nasty reflective junk and to avoid needing
        // to know about every subclass of JnaEnum, we retrieve the first
        // element of the enum and make IT do the conversion for us.

        T[] vals = clazz.getEnumConstants();
        return vals[i];
    }
 
    @Override
	public Integer toNative(Object input, ToNativeContext context) {
    	T t = clazz.cast(input);
    	
        return Integer.valueOf(t.ordinal());
    }
 
    @Override
	public Class<Integer> nativeType() {
        return Integer.class;
    }
    
    public static <E extends Enum<E>> int toInteger(E val)
    {
        E[] vals = (E[]) val.getClass().getEnumConstants();
    	for (int idx = 0; idx < vals.length; idx++)
    	{
    		if (vals[idx] == val)
    			return idx;
    	}
    	
    	throw new IllegalArgumentException();
    }
    
    public static <E extends Enum<E>> E fromInteger(int idx, Class<E> clazz)
    {
    	E[] vals = clazz.getEnumConstants();
    	return vals[idx];
    }

}


