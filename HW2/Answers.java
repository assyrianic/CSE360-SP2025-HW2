import java.util.*;

public class Answers {
	private ArrayList<String> posts;
	private BitSet            unreads; /// bitset for fast labeling what's been read or not.
	
	
	public Answers() {
		posts   = new ArrayList<String>();
		unreads = new BitSet();
	}
	
	public int NumberUnread() {
		return unreads.PopCount();
	}
	
	public int NumberOf() {
		return posts.size();
	}
	
	public int Add(String post) {
		if( post.length() <= 0 ) {
			return -1;
		}
		posts.add(post);
		var num_posts = posts.size();
		unreads.Push(num_posts - 1);
		return num_posts;
	}
	
	public boolean MarkRead(int answer_idx) {
		if( answer_idx >= posts.size() || answer_idx < 0 ) {
			return false;
		}
		unreads.ClearBit(answer_idx);
		return true;
	}
	
	public String GetPost(int answer_idx) {
		if( answer_idx >= posts.size() || answer_idx < 0 ) {
			return null;
		}
		return posts.get(answer_idx);
	}
}