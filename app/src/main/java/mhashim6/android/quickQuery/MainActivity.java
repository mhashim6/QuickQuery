package mhashim6.android.quickQuery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import static mhashim6.android.quickQuery.Utils.FLAVOR_FULL;
import static mhashim6.android.quickQuery.Utils.GOOGLE_PLAY_LINK;
import static mhashim6.android.quickQuery.Utils.GOOGLE_PLAY_LINK_PRO;

public class MainActivity extends AppCompatActivity {

	private InterstitialAd interstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getFragmentManager().beginTransaction().replace(R.id.content_frame, new PreferencesFragment()).commit();
		initToolbar();

		Intent clipboardMonitorStarter = new Intent(this, ClipboardMonitor.class);
		startService(clipboardMonitorStarter);

		if (!BuildConfig.FLAVOR.equals(FLAVOR_FULL))
			initAds();
	}

	private void initToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
		setSupportActionBar(toolbar);
	}
//===================================================

	private void initAds() {
		requestAds(); //banner
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId("ca-app-pub-1801049179059842/5610177963");
		interstitialAd.loadAd(new AdRequest.Builder().build());
				//.addTestDevice("5595A1D3B92A1BF4D4E4D1F164AD1A3F").build());
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				interstitialAd.show();
			}
		});
	}

	private void requestAds() {
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			//	.addTestDevice("5595A1D3B92A1BF4D4E4D1F164AD1A3F");

		AdRequest adRequest = adRequestBuilder.build();
		AdView adView = findViewById(R.id.adView);
		adView.loadAd(adRequest);
	}
//===================================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem rate = menu.findItem(R.id.rate_item);
		rate.setOnMenuItemClickListener(item -> {
			Utils.openWebPage(this, BuildConfig.FLAVOR.equals(FLAVOR_FULL) ?
					GOOGLE_PLAY_LINK_PRO
					: GOOGLE_PLAY_LINK);
			return true;
		});
		return super.onCreateOptionsMenu(menu);
	}
//===================================================

}
