package com.example.mob201_lab05;

public class RSSItems {
	private CharSequence Title;
	private CharSequence Link;
	private CharSequence Description;

	public RSSItems() {
		Title = "";
		Link = "";
		Description = "";
	}

	public RSSItems(CharSequence title, CharSequence link,	CharSequence description) {
		Title = title;
		Link = link;
		Description = description;
	}
	

	public CharSequence getDescription() {
		return Description;
	}

	public void setDescription(CharSequence description) {
		Description = description;
	}

	public CharSequence getLink() {
		return Link;
	}

	public void setLink(CharSequence link) {
		Link = link;
	}

	public CharSequence getTitle() {
		return Title;
	}

	public void setTitle(CharSequence title) {
		Title = title;
	}
}
