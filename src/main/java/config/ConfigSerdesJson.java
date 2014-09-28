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
package config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Martin Steiger
 */
public class ConfigSerdesJson
{
	private static final Logger logger = LoggerFactory.getLogger(ConfigSerdesJson.class);

	private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".monitorbrightnesstool");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static Config load()
	{
		try (Reader reader = Files.newBufferedReader(CONFIG_PATH, Charsets.UTF_8))
		{
			Config config = gson.fromJson(reader, Config.class);
			return config;
		}
		catch (NoSuchFileException e)
		{
			logger.debug("Config file not found - creating a new one");
		}
		catch (IOException e)
		{
			logger.warn("Could not load config file {} - using default", CONFIG_PATH);
			logger.debug("An exception occurred", e);
		}

		return new Config();
	}

	/**
	 * @param config
	 */
	public static void save(Config config)
	{
		try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, Charsets.UTF_8))
		{
			gson.toJson(config, writer);
		}
		catch (IOException e)
		{
			logger.warn("Could not save config file {}", CONFIG_PATH);
		}
	}

}
