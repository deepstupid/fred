/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package freenet.config;

import freenet.support.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Global configuration object for a node. SubConfig's register here.
 * Handles writing to a file etc.
 */
public class Config {
	public enum RequestType {
		CURRENT_SETTINGS, DEFAULT_SETTINGS, SORT_ORDER, EXPERT_FLAG, FORCE_WRITE_FLAG, SHORT_DESCRIPTION, LONG_DESCRIPTION, DATA_TYPE
	};

	protected final Map<String, SubConfig> configsByPrefix;
	
	public Config() {
		configsByPrefix = new ConcurrentHashMap<String, SubConfig>();
	}
	
	public void register(SubConfig sc) {
			SubConfig existing = configsByPrefix.putIfAbsent(sc.prefix, sc);
			if(existing!=null)
				throw new IllegalArgumentException("Already registered "+sc.prefix+": "+sc);

	}
	
	/** Write current config to disk 
	 * @throws IOException */
	public void store() {
		// Do nothing
	}

	/** Finished initialization */
	public void finishedInit() {
	    SubConfig[] configs;
            // FIXME maybe keep a cache of this?
            configs = configsByPrefix.values().toArray(new SubConfig[configsByPrefix.size()]);

        for(SubConfig config : configs) {
            if(!config.hasFinishedInitialization())
                Logger.error(this, "Not finished initialization: "+config.prefix);
        }
	}

	public void onRegister(SubConfig config, Option<?> o) {
		// Do nothing
	}

	/** Fetch all the SubConfig's. Used by user-facing config thingies. */
	public SubConfig[] getConfigs() {
		return configsByPrefix.values().toArray(new SubConfig[configsByPrefix.size()]);
	}
	
	public SubConfig get(String subConfig){
		return configsByPrefix.get(subConfig);
	}

	@SuppressWarnings("deprecation")
	public SubConfig createSubConfig(String subConfig) {
		return new SubConfig(subConfig, this);
	}

}
