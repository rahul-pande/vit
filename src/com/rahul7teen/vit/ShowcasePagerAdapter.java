package com.rahul7teen.vit;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class ShowcasePagerAdapter extends FragmentStatePagerAdapter {
	String[] caseTexts;
	String[] caseURLs;
	Document vitDoc;

	public ShowcasePagerAdapter(FragmentManager paramFragmentManager,
			Document paramDocument) {
		super(paramFragmentManager);
		this.vitDoc = paramDocument;
		this.caseURLs = getCaseImageURLs(paramDocument);
		this.caseTexts = getCaseTexts(paramDocument);
	}

	private String[] getCaseImageURLs(Document paramDocument) {
		String[] arrayOfString = null;
		try {
			Elements localElements = paramDocument
					.select("div.ice-main-wapper > div.ice-main-item > div.ice-description > img");
			arrayOfString = new String[localElements.size()];
			for (int i = 0; i < localElements.size(); i++)
				arrayOfString[i] = localElements.get(i).attr("src")
						.replaceAll(" ", "%20");
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return arrayOfString;
	}

	private String[] getCaseTexts(Document paramDocument) {
		String[] arrayOfString = null;
		try {
			Elements localElements = paramDocument
					.select("div.ice-main-wapper > div.ice-main-item > div.ice-description > h3");
			arrayOfString = new String[localElements.size()];
			for (int i = 0; i < localElements.size(); i++)
				arrayOfString[i] = localElements.get(i).text();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return arrayOfString;
	}

	public int getCount() {
		return this.caseTexts.length;
	}

	public Fragment getItem(int paramInt) {
		ShowcaseFragment localShowcaseFragment = new ShowcaseFragment();
		Bundle localBundle = new Bundle();
		localBundle.putString("url", this.caseURLs[paramInt]);
		localBundle.putString("title", this.caseTexts[paramInt]);
		localShowcaseFragment.setArguments(localBundle);
		return localShowcaseFragment;
	}
}