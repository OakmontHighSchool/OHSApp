package us.rjuhsd.ohs.OHSApp.DrawerList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import us.rjuhsd.ohs.OHSApp.R;
import us.rjuhsd.ohs.OHSApp.activities.ClassesOverviewActivity;
import us.rjuhsd.ohs.OHSApp.activities.Preferences;

public class OHSDrawerList {
	@SuppressWarnings("UnusedParameters")
	public OHSDrawerList(Context c, final DrawerLayout drawerLayout, final ListView drawerList) {
		int[] imageSrcs = {R.drawable.ic_accept, R.drawable.ic_web, R.drawable.ic_date, R.drawable.ic_cog};

		final ArrayAdapter<String> testAA = new DrawerListAdapter(c, R.layout.drawer_list_item, R.id.drawer_list_item_TextView, c.getResources().getStringArray(R.array.drawer_list_values), imageSrcs);
		drawerList.setAdapter(testAA);

		drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			Context c = null;

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				c = view.getContext();
				Intent newIntent = null;
				if (c != null) {
					switch (position) {
						case 0:
							newIntent = new Intent(c, ClassesOverviewActivity.class);
							break;
						case 1:
							newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ohs.rjuhsd.us"));
							break;
						case 2:
							newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ohs.rjuhsd.us/Page/2"));
							break;
						case 3:
							newIntent = new Intent(c, Preferences.class);
							break;
					}
				}
				if (newIntent != null) {
					c.startActivity(newIntent);
				}

				drawerLayout.closeDrawer(drawerList);
			}
		});

	}
}