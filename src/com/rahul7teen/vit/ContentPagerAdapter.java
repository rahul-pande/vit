
package com.rahul7teen.vit;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class ContentPagerAdapter extends FragmentStatePagerAdapter {
	Context context;
	Item item;
	Document vitDoc;

	public ContentPagerAdapter(FragmentManager paramFragmentManager,
			Document paramDocument, Context paramContext) {
		super(paramFragmentManager);
		this.vitDoc = paramDocument;
		this.context = paramContext;
	}

	private Item getDownloadItem(Document paramDocument) {
		Item localItem = new Item();
		try {
			Elements localElements1 = paramDocument
					.select("div.width20.separator.floatleft:eq(2) a");
			Elements localElements2 = paramDocument
					.select("div.width20.separator.floatleft:eq(2) a");
			localItem.texts = new String[localElements1.size()];
			localItem.links = new String[localElements2.size()];
			for (int i = 0; i < localElements1.size(); i++) {
				localItem.texts[i] = localElements1.get(i).text();
				String str = localElements2.get(i).attr("href");
				if ((!str.startsWith("http")) && (!str.startsWith("www")))
					str = "http://www.vitacademics.in" + str;
				localItem.links[i] = str.replace(" ", "%20");
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return localItem;
	}

	private Item getExamItem(Document paramDocument) {
		Item localItem = new Item();
		try {
			Elements localElements1 = paramDocument
					.select("li#iceMenu_107 li.iceMenuLiLevel_2 a[href]");
			Elements localElements2 = paramDocument
					.select("li#iceMenu_107 li.iceMenuLiLevel_2 a[href]");
			localItem.texts = new String[localElements1.size()];
			localItem.links = new String[localElements2.size()];
			for (int i = 0; i < localElements1.size(); i++) {
				localItem.texts[i] = localElements1.get(i).text();
				String str = localElements2.get(i).attr("href");
				if ((!str.startsWith("http")) && (!str.startsWith("www")))
					str = "http://www.vitacademics.in" + str;
				localItem.links[i] = str.replace(" ", "%20");
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return localItem;
	}

	private Item getLatestItem(Document paramDocument) {
		Item localItem = new Item();
		try {
			Elements localElements1 = paramDocument
					.select("div.iceaccordion > h4");
			Elements localElements2 = paramDocument
					.select("div.accordion_content.iceaccordion_content_1 > div.padding > a");
			localItem.texts = new String[localElements1.size()];
			localItem.links = new String[localElements2.size()];
			for (int i = 0; i < localElements1.size(); i++) {
				localItem.texts[i] = localElements1.get(i).text();
				String str = localElements2.get(i).attr("href");
				if ((!str.startsWith("http")) && (!str.startsWith("www")))
					str = "http://www.vitacademics.in" + str;
				localItem.links[i] = str.replace(" ", "%20");
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return localItem;
	}

	private Item getStudentItem(Document paramDocument) {
		Item localItem = new Item();
		try {
			Elements localElements1 = paramDocument
					.select("li#iceMenu_109 li.iceMenuLiLevel_2 a");
			Elements localElements2 = paramDocument
					.select("li#iceMenu_109 li.iceMenuLiLevel_2 a");
			localItem.texts = new String[localElements1.size()];
			localItem.links = new String[localElements2.size()];
			for (int i = 0; i < localElements1.size(); i++) {
				localItem.texts[i] = localElements1.get(i).text();
				String str = localElements2.get(i).attr("href");
				if ((!str.startsWith("http")) && (!str.startsWith("www")))
					str = "http://www.vitacademics.in" + str;
				localItem.links[i] = str.replace(" ", "%20");
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return localItem;
	}

	public int getCount() {
		return 4;
	}

	public Fragment getItem(int paramInt) {
		Bundle localBundle = new Bundle();
		ContentFragment localContentFragment = new ContentFragment(this.context);
		switch (paramInt) {
		case 0:
			this.item = getLatestItem(this.vitDoc);
			break;
		case 1:
			this.item = getDownloadItem(this.vitDoc);
			break;
		case 2:
			this.item = getExamItem(this.vitDoc);
			break;
		case 3:
			this.item = getStudentItem(this.vitDoc);
			break;
		}
		localBundle.putCharSequenceArray("texts", this.item.texts);
		localBundle.putCharSequenceArray("links", this.item.links);
		localContentFragment.setArguments(localBundle);
		return localContentFragment;
	}

	public CharSequence getPageTitle(int paramInt) {
		String str = null;
		switch (paramInt) {

		case 0:
			str = "Latest";
			break;
		case 1:
			str = "Downloads";
			break;
		case 2:
			str = "Exam Section";
			break;
		case 3:
			str = "Students Corner";
			break;
		}

		return str;

	}

	class Item {
		String[] links;
		String[] texts;

		Item() {
		}
	}
}