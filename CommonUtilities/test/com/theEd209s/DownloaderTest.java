package com.theEd209s;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.theEd209s.utils.DownloadUtils;
import com.theEd209s.utils.DownloadUtils.Downloader;
import com.theEd209s.utils.DownloadUtils.Downloader.DownloadCancelledCmd;
import com.theEd209s.utils.DownloadUtils.Downloader.DownloadCompleteCmd;
import com.theEd209s.utils.DownloadUtils.Downloader.UpdateGuiDownloadProgressCmd;

public class DownloaderTest
{	
	
	public static void main(String[] args)
	{
		try
		{
			final Downloader downloader = DownloadUtils.getDownloader(new DownloadCompleteCmd()
			{
				@Override
				public void downloadComplete(boolean downloadSuccessful)
				{
					if (downloadSuccessful)
					{
						System.out.println("Download was successful.");
					}
					else
					{
						System.out.println("Download failed.");
					}
				}
			}, new DownloadCancelledCmd()
			{
				@Override
				public void downloadCancelled(boolean userRequested)
				{
					if (userRequested)
					{
						System.out.println("User requested cancel.");
					}
					else
					{
						System.out.println("System shutdown.");
					}
				}
			}, new UpdateGuiDownloadProgressCmd()
			{
				@Override
				public void updateGuiDownloadProgress(long totalBytesDownloaded)
				{
					System.out.println("Downloading: " + totalBytesDownloaded + " bytes downloaded ...");
				}
			}, 0L, new URL("http://www.scala-lang.org/docu/files/ScalaByExample.pdf"), 0L, new File("C:\\Users\\mweiler\\Desktop\\ScalaByExample.pdf"), null, 0, 0, 0, true);
			final Thread downloadThread = new Thread(downloader);
			downloadThread.start();
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			downloader.requestCancel();
			downloadThread.join();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
}
