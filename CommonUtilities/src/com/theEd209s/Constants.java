package com.theEd209s;

import com.theEd209s.utils.ShutdownRequestedUtils;

/**
 * This class contains several constant values that are
 * used throughout the system.
 * 
 * @author Matthew Weiler
 * */
public class Constants
{	
	
	/* DOWNLOADING SETTINGS */
	/**
	 * This will store the number of bytes that must be written before a flush
	 * will occur using the
	 * {@link ShutdownRequestedUtils#downloadFile(Logger, URL, File, String, int, int, int, boolean)
	 * downloadFile(Logger, URL, File, String, int, int, int, boolean)} method.
	 * */
	public static final int DOWNLOAD_FILE_FLUSH_THRESHOLD = 5 * 1025;
	/**
	 * This will store the number of bytes that must be written before an update
	 * of the GUI will occur using the
	 * {@link ShutdownRequestedUtils#downloadFile(Logger, URL, File, String, int, int, int, boolean)
	 * downloadFile(Logger, URL, File, String, int, int, int, boolean)} method.
	 * */
	public static final int DOWNLOAD_GUI_UPDATE_THRESHOLD = 4 * 1024 * 1024;
	/**
	 * This will store the number of bytes that must be written before a cancel
	 * event is acknowledged and propagated.
	 * */
	public static final int DOWNLOAD_CANCEL_CHECK_THRESHOLD = 64 * 1024;
	/**
	 * This will store the default number of max retries for a file that fails to download using
	 * the {@link ShutdownRequestedUtils#downloadFile(Logger, URL, File, String, int, int, int, boolean)
	 * downloadFile(Logger, URL, File, String, int, int, int, boolean)} method.
	 * */
	public static final int DOWNLOAD_FILE_MAX_RETRIES_DEFAULT = 5;
	/**
	 * This will store the default number of max retries for a section of a file that
	 * fails to download using the
	 * {@link ShutdownRequestedUtils#downloadFile(Logger, URL, File, String, int, int, int, boolean)
	 * downloadFile(Logger, URL, File, String, int, int, int, boolean)} method.
	 * */
	public static final int DOWNLOAD_FILE_MAX_CHUNK_RETRIES_DEFAULT = 5;
	/**
	 * This will store the default buffer size to use when downloading a file using the
	 * {@link ShutdownRequestedUtils#downloadFile(Logger, URL, File, String, int, int, int, boolean)
	 * downloadFile(Logger, URL, File, String, int, int, int, boolean)} method.
	 * */
	public static final int DOWNLOAD_FILE_INITIAL_BUFFER_SIZE_DEFAULT = 5 * 1024 * 1024;
	/**
	 * This will store the total number of milliseconds that can pass before a connection must be made.
	 * */
	public static final int TIMEOUT_CONNECTION = 2000;
	/**
	 * This will store the total number of milliseconds that can pass before a return must be
	 * acknowledged when reading from a remote server.
	 * */
	public static final int TIMEOUT_READ = 5000;
	
}
