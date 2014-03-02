package com.theEd209s.utils;

/**
 * This class contains several methods which can be
 * used to check if the user has requested a shutdown.
 * 
 * @author Matthew Weiler
 * */
public class ShutdownRequestedUtils
{	
	
	/* PUBLIC METHODS */
	/**
	 * This will check if a shutdown command has been requested.
	 * 
	 * @return
	 * <code>true</code> if a shutdown command has been requested;
	 * <code>false</code> otherwise.
	 * */
	public static boolean isShutdownRequested()
	{
		return ShutdownRequestedUtils.shutdownRequested;
	}
	
	/**
	 * This will set if a shutdown command has been requested.
	 * 
	 * @param shutdownRequested
	 * <code>true</code> if a shutdown command has been requested;
	 * <code>false</code> otherwise.
	 * */
	public static void setShutdownRequested(final boolean shutdownRequested)
	{
		ShutdownRequestedUtils.shutdownRequested = shutdownRequested;
	}
	
	/* PRIVATE VARIABLES */
	/**
	 * This will keep track of whether-or-not a shutdown request
	 * came-in.
	 * */
	private static boolean shutdownRequested = false;
	
	/* PUBLIC CLASSES */
	/**
	 * This class will be used to signify that the user requested to close the
	 * application.
	 * 
	 * @author Matthew Weiler
	 * */
	public static class UserRequestedShutdownException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * This will create a new instance of a
		 * {@link UserRequestedShutdownException}.
		 * */
		public UserRequestedShutdownException()
		{
			super("User requested shutdown.");
		}
		
	}
	
}
