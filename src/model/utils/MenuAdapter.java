package model.utils;

import java.util.ArrayList;

import utils.Constant;
import utils.DialogListener;
import utils.DialogManager;
import utils.Session;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.online.hcmup.android.MainActivity;
import com.online.hcmup.android.PrivateActivity;
import com.online.hcmup.android.PrivateActivity.SliderMenu;
import com.online.hcmup.android.R;
import com.online.hcmup.android.RegisterCurriculumFragment;
import com.online.hcmup.android.StudentFeeFragment;
import com.online.hcmup.android.StudentFragment;
import com.online.hcmup.android.StudyProgramFragment;

public class MenuAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<SliderMenu> menu;

	public static DrawerLayout mainLayout;
	public static ListView menuLV;
	Session session;
	Dialog dialog;

	public MenuAdapter(Activity a, ArrayList<SliderMenu> m, DrawerLayout mL,
			ListView mLV) {
		activity = a;
		menu = m;
		mainLayout = mL;
		menuLV = mLV;
		session = new Session(activity);
	}

	@Override
	public int getCount() {
		return menu.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = (activity.getLayoutInflater()).inflate(R.layout.menu_item,
				parent, false);
		TextView item = (TextView) row.findViewById(R.id.menuitem);
		item.setText(menu.get(position).Text);

		item.setCompoundDrawablesWithIntrinsicBounds(menu.get(position).Icon,
				0, 0, 0);

		// Set Click Event
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menuLV.setItemChecked(position, true);
				mainLayout.closeDrawer(menuLV);
				switch (position) {
				case 0: // Main --> Private News
					FragmentManager fragmentManager = activity
							.getFragmentManager();
					while (fragmentManager.getBackStackEntryCount() > 0)
						fragmentManager.popBackStack();
					// activity.setTitle(menu.get(position).Text);
					PrivateActivity.context.loadMainFragment();
					break;
				case 1: // Student Fragment
					// First load Student Info
					StudentFragment.TYPE = 0;
					openFragment(new StudentFragment(),
							Constant.TAG_STUDENT_INFO, menu.get(position).Text);
					break;
				case 2: // Study Program --> StudyProgramFragment
					openFragment(new StudyProgramFragment(),
							Constant.TAG_STUDY_PROGRAM, menu.get(position).Text);
					break;
				case 3: // Register Curriculum --> RegisterCurriculumFragment
					openFragment(new RegisterCurriculumFragment(),
							Constant.TAG_REGISTER_CURRICULUM,
							menu.get(position).Text);
					break;
				case 7: // Student Fee
					openFragment(new StudentFeeFragment(),
							Constant.TAG_STUDENT_FEE, menu.get(position).Text);
					break;
				case 9: // Sign out
					dialog = DialogManager.showAlertDialog(activity,
							R.string.logout_noti_detail, R.string.btn_confirm,
							R.string.logout_noti_negative,
							new DialogListener() {

								@Override
								public void setOnPositiveButtonClicked() {
									dialog.dismiss();
									session = Session.getInstance(activity);
									session.logoutUser(MainActivity.class);
									activity.finish();
								}
							});
					break;
				}
			}
		});
		return row;
	}

	public void openFragment(Fragment frag, String tag, String title) {
		FragmentManager fm = activity.getFragmentManager();
		while (fm.getBackStackEntryCount() > 0)
			fm.popBackStack();
		fm.beginTransaction().replace(R.id.content, frag, tag).commit();
		activity.setTitle(title);
	}
}
