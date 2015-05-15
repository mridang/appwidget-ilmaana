package com.mridang.ilmaana;

import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import com.mridang.widgets.BaseWidget;
import com.mridang.widgets.SavedSettings;
import com.mridang.widgets.WidgetHelpers;
import com.mridang.widgets.utils.CacheCleaner;
import com.mridang.widgets.utils.GzippedClient;
import com.mridang.widgets.utils.ImageFetcher;

/**
 * This class is the provider for the widget and updates the data
 */
public class LauncherWidget extends BaseWidget {

	/*
	 * @see com.mridang.widgets.BaseWidget#fetchContent(android.content.Context, java.lang.Integer,
	 * com.mridang.widgets.SavedSettings)
	 */
	@Override
	public String fetchContent(Context ctxContext, Integer intInstance, SavedSettings objSettings)
			throws Exception {

		CacheCleaner.deleteOlderThan(ctxContext.getFilesDir(), "*", new Date(new Date().getTime() - 86400000L));
		final DefaultHttpClient dhcClient = GzippedClient.createClient();
		JSONArray jsoData = new JSONArray();

		Log.v("LauncherWidget", "Fetching the doppler radar map images");
		final HttpGet getImages = new HttpGet("http://en.ilmatieteenlaitos.fi/rain-and-cloudiness/" + objSettings.get("location") + "-finland?flash=0");
		final HttpResponse resImages = dhcClient.execute(getImages);

		final Integer intImages = resImages.getStatusLine().getStatusCode();
		if (intImages != HttpStatus.SC_OK) {
			throw new HttpResponseException(intImages, "Server responded with code " + intImages);
		}

		Log.d("LauncherWidget", "Fetched the doppler radar map images");
		final String strImages = EntityUtils.toString(resImages.getEntity(), "UTF-8");
		final Pattern p = Pattern.compile("\"(http://.*?img\\.php.*?)\"");
		final Matcher m = p.matcher(strImages);
		while (m.find()) {
			final Uri uriImage = ImageFetcher.getImage(ctxContext, m.group(1), false);
			jsoData.put(uriImage.toString());
		}

		return jsoData.toString(2);

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#getIcon()
	 */
	@Override
	public Integer getIcon() {

		return R.drawable.ic_notification;

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#getKlass()
	 */
	@Override
	protected Class<?> getKlass() {

		return getClass();

	}

	/*
	 * @see com.mridang.BaseWidget#getToken()
	 */
	@Override
	public String getToken() {

		return "a1b2c3d4";

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#updateWidget(android.content.Context, java.lang.Integer,
	 * com.mridang.widgets.SavedSettings, java.lang.String)
	 */
	@Override
	public void updateWidget(Context ctxContext, Integer intInstance, SavedSettings objSettings, String strContent)
			throws Exception {

		final RemoteViews remView = new RemoteViews(ctxContext.getPackageName(), R.layout.widget);

		final Intent ittSlides = new Intent(ctxContext, SlideService.class);
		ittSlides.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, intInstance);
		ittSlides.setData(Uri.fromParts("content", String.valueOf(new Random().nextInt()), null));
		ittSlides.putExtra("data", strContent);

		final String strWebpage = "http://en.ilmatieteenlaitos.fi/rain-and-cloudiness/";
		final PendingIntent pitOptions = WidgetHelpers.getIntent(ctxContext, WidgetSettings.class, intInstance);
		final PendingIntent pitWebpage = WidgetHelpers.getIntent(ctxContext, strWebpage);
		remView.setTextViewText(R.id.last_update, DateFormat.format("kk:mm", new Date()));
		remView.setOnClickPendingIntent(R.id.settings_button, pitOptions);
		remView.setOnClickPendingIntent(R.id.widget_icon, pitWebpage);
		remView.setRemoteAdapter(R.id.image_flipper, ittSlides);

		AppWidgetManager.getInstance(ctxContext).updateAppWidget(intInstance, remView);

	}

}