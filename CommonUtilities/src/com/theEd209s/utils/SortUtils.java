package com.theEd209s.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class contains several methods for sorting {@link List}s.
 * 
 * @author Matthew Weiler
 * */
public class SortUtils
{	
	
	/* PUBLIC METHODS */
	/**
	 * This method will sort the specified {@link List} in <code>ascending</code> order.
	 * 
	 * @param list
	 * The list to sort.
	 * 
	 * @return
	 * The sorted {@link List}.
	 * */
	public static <T extends Comparable<? super T>> List<T> sortAsc(Collection<T> list)
	{
		return SortUtils.sort(list, true);
	}
	
	/**
	 * This method will sort the specified {@link List} in <code>descending</code> order.
	 * 
	 * @param list
	 * The list to sort.
	 * 
	 * @return
	 * The sorted {@link List}.
	 * */
	public static <T extends Comparable<? super T>> List<T> sortDesc(Collection<T> list)
	{
		return SortUtils.sort(list, false);
	}
	
	/* PRIVATE METHODS */
	/**
	 * This method will sort the specified {@link List} in <code>ascending</code> or
	 * <code>descending</code> order; as specified.
	 * 
	 * @param list
	 * The list to sort.
	 * @param asc
	 * <code>true</code> if the {@link List} should be sorted in <code>ascending</code>
	 * order; <code>false</code> if the {@link List} should be sorted in
	 * <code>descending</code> order.
	 * 
	 * @return
	 * The sorted {@link List}.
	 * */
	private static <T extends Comparable<? super T>> List<T> sort(Collection<T> list, final boolean asc)
	{
		final List<T> outputList = new ArrayList<T>(list);
		java.util.Collections.sort(outputList);
		if (!asc)
		{
			java.util.Collections.reverse(outputList);
		}
		return outputList;
	}
	
}
