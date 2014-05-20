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

import java.util.HashSet;
import java.util.Set;

import jna.FlagEnum;

public class EnumUtils
{
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
    	if (idx == EnumConverter.UNINITIALIZED)
    		return null;
    	
    	E[] vals = clazz.getEnumConstants();
    	return vals[idx];
    }
    
    public static <T extends FlagEnum> Set<T> setFromInteger(int i, Class<T> clazz)
    {
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
    
	public static <T extends FlagEnum> int setToInteger(Set<T> set) {
    	int sum = 0;
    	
    	for (T t : set)
    	{
    		sum += t.getFlag();
    	}

    	return sum;
    }
}


