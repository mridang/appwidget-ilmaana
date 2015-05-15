package com.mridang.ilmaana;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

/**
 * This class is used to provide the stacks for the widget
 */
public class SlideFactory implements RemoteViewsFactory {

	/* This is the array containing the the list of accounts */
	private JSONArray jsoImages;
	/* The context of the calling activity */
	private final Context ctxContext;
	/* The view that is used for each of the slides */
	private RemoteViews remView;

	/*
	 * 
	 */
	public SlideFactory(Context ctxContext, Intent ittIntent) {

		remView = new RemoteViews(ctxContext.getPackageName(), R.layout.image);
		this.ctxContext = ctxContext;
		try {
			jsoImages = new JSONArray(ittIntent.getStringExtra("data"));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getCount()
	 */
	@Override
	public int getCount() {

		return jsoImages != null ? jsoImages.length() : 0;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getItemId(int)
	 */
	@Override
	public long getItemId(int intPosition) {

		return intPosition;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getLoadingView()
	 */
	@Override
	public RemoteViews getLoadingView() {

		return new RemoteViews(ctxContext.getPackageName(), R.layout.loading);

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getViewAt(int)
	 */
	@Override
	public RemoteViews getViewAt(int intPosition) {

		try {

			remView.setImageViewUri(R.id.dopper_image, Uri.parse(jsoImages.getString(intPosition)));

		} catch (final JSONException e) {
			Log.e("SlideFactory", "Unknown error encountered", e);
			ACRA.getErrorReporter().handleSilentException(e);
		} catch (final Exception e) {
			Log.e("SlideFactory", "Unknown error encountered", e);
			ACRA.getErrorReporter().handleSilentException(e);
		}

		return remView;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {

		return 1;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {

		return true;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#onCreate()
	 */
	@Override
	public void onCreate() {

		return;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#onDataSetChanged()
	 */
	@Override
	public void onDataSetChanged() {

		return;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#onDestroy()
	 */
	@Override
	public void onDestroy() {

		jsoImages = null;

	}

}