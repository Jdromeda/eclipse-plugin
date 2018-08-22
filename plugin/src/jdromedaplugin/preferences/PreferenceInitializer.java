package jdromedaplugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import jdromedaplugin.JdromedaPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore projectPreferenceStore = JdromedaPlugin.getDefault().getPreferenceStore();
		projectPreferenceStore.setDefault(Preferences.PROFILES(), "default");
		projectPreferenceStore.setDefault(Preferences.CANDY_JS_OUTPUT_FOLDER(Preferences.DEFAULT_PROFILE_NAME), "webapp/js/candy");
		projectPreferenceStore.setDefault(Preferences.BUNDLES_DIRECTORY(Preferences.DEFAULT_PROFILE_NAME), "webapp/js");
		projectPreferenceStore.setDefault(Preferences.BUNDLE(Preferences.DEFAULT_PROFILE_NAME), true);
		projectPreferenceStore.setDefault(Preferences.JS_OUTPUT_FOLDER(Preferences.DEFAULT_PROFILE_NAME), "webapp/js");
		projectPreferenceStore.setDefault(Preferences.TS_OUTPUT_FOLDER(Preferences.DEFAULT_PROFILE_NAME), "webapp/ts");
		projectPreferenceStore.setDefault(Preferences.DEBUG_MODE(Preferences.DEFAULT_PROFILE_NAME), true);
	}

}
