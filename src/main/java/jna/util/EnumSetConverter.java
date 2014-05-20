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

package jna.util;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import jna.FlagEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

public class EnumSetConverter<T extends FlagEnum> implements TypeConverter {
	 
    private static final Logger logger = LoggerFactory.getLogger(EnumSetConverter.class);

    public EnumSetConverter()
    {
    }
    
    @Override
	public Set<T> fromNative(Object input, FromNativeContext context) {
        int i = (Integer) input;
        
        Class<?> targetClass = context.getTargetType();
        Class<T> clazz = (Class<T>) targetClass;
        
        Object[] enums = targetClass.getEnumConstants();
        if (enums.length == 0) {
            logger.error("Could not convert desired enum type (), no valid values are defined.",targetClass.getName());
            return null;
        }

        T[] vals = clazz.getEnumConstants();
        Set<T> result = new HashSet<T>();
        
        for (T val : vals)
        {
        	if ((i & val.getFlag()) != 0)
        	{
        		result.add(val);
        	}
        }
        
        return result;
    }
 
    @Override
	public Integer toNative(Object input, ToNativeContext context) {
    	Set<T> set = (Set<T>)(input);
    	int sum = 0;
    	
    	for (T t : set)
    	{
    		sum += t.getFlag();
    	}
    		
        return Integer.valueOf(sum);
    }
 
    @Override
	public Class<Integer> nativeType() {
        return Integer.class;
    }

}


