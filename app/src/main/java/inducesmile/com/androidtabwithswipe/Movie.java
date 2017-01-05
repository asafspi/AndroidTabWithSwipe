package inducesmile.com.androidtabwithswipe;

public class Movie {

	private String id, title, channel, url, duration;

	public Movie(String title, String channel, String url, String id, String duration) {
	
		this.title = title;
		this.channel = channel;
		this.url = url;
		this.id = id;
		this.duration = duration;

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDuration() {return duration;}

	public void setDuration(String duration) {this.duration = duration;}
	
}
