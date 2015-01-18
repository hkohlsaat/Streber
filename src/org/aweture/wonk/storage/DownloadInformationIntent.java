package org.aweture.wonk.storage;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class DownloadInformationIntent extends Intent {
	private static final String LOG_TAG = DownloadInformationIntent.class.getSimpleName();
	
	public enum DownloadStates {
		/** The download is about to start after informing about it. */
		DOWNLOAD_STARTING,
		/** The download is aborted. */
		DOWNLOAD_ABORTED,
		/** The download completed but the data isn't saved by now */
		DOWNLOAD_COMPLETE,
		/** The just downloaded data is NEW and saved. */
		NEW_DATA_SAVED
	}
	
	private static final String NAME = "download_information";
	private static final String STATE_EXTRA = "state_extra";
	
	public DownloadInformationIntent() {
		super(NAME);
	}
	
	public void setState(DownloadStates state) {
		putExtra(STATE_EXTRA, state);
		Log.i(LOG_TAG, "Creating Intent with state " + state);
	}
	
	public DownloadStates getState() {
		return (DownloadStates) getSerializableExtra(STATE_EXTRA);
	}
	
	public static class DownloadInformationIntentFilter extends IntentFilter {
		
		public DownloadInformationIntentFilter() {
			super(NAME);
		}
	}
}