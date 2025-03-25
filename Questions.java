import java.util.*;

/**
 * questions class that supports storing all current questions as well as any subset of the questions (e.g., a  subset reflecting the results of a search)
 */
public class Questions {
	private ArrayList<String>
		/// title of the question
		titles,
		
		/// the text of the question in detail
		bodies
	;
	private ArrayList<HashSet<String>> qtags;
	private ArrayList<Answers>         answers;
	private ArrayList<Integer>         resolver;
	
	
	public Questions() {
		titles   = new ArrayList<String>();
		bodies   = new ArrayList<String>();
		qtags    = new ArrayList<HashSet<String>>();
		
		resolver = new ArrayList<Integer>();
		answers  = new ArrayList<Answers>();
	}
	
	private boolean _IdxGood(int idx) {
		return idx >= 0 && idx < titles.size();
	}
	
	public String GetTitle(int question_idx) {
		if( question_idx >= titles.size() || question_idx < 0 ) {
			return null;
		}
		return titles.get(question_idx);
	}
	
	public String GetBody(int question_idx) {
		if( (question_idx >= titles.size() || question_idx < 0) ) {
			return null;
		}
		return bodies.get(question_idx);
	}
	
	public HashSet<String> GetTags(int question_idx) {
		if( (question_idx >= titles.size() || question_idx < 0) ) {
			return null;
		}
		return qtags.get(question_idx);
	}
	
	public Answers GetAnswers(int question_idx) {
		if( (question_idx >= titles.size() || question_idx < 0) ) {
			return null;
		}
		return answers.get(question_idx);
	}
	
	public int AddAnswer(int question_idx, String post) {
		if( (question_idx >= titles.size() || question_idx < 0) ) {
			return -1;
		}
		return answers.get(question_idx).Add(post);
	}
	
	/// have to use 'int' so we can have an error code.
	public int IsResolved(int question_idx) {
		if( (question_idx >= titles.size() || question_idx < 0) ) {
			return -1;
		}
		return resolver.get(question_idx) >= 0? 1 : 0;
	}
	
	public int GetResolver(int question_idx) {
		if( (question_idx >= titles.size() || question_idx < 0) ) {
			return -1;
		}
		return resolver.get(question_idx);
	}
	
	/// returns the index of a question.
	/// -1 if errored.
	public int Add(String title, String body, String[] tags) {
		/// title can't be empty
		if( title.length() <= 0 ) {
			return -1;
		}
		
		titles.add(title.toLowerCase());
		bodies.add(body.toLowerCase());
		
		var tag_set = new HashSet<String>();
		for( var tag : tags ) {
			tag_set.add(tag.toLowerCase());
		}
		qtags.add(tag_set);
		resolver.add(-1);
		answers.add(new Answers());
		return titles.size() - 1;
	}
	
	/// returns the index of a question.
	/// -1 if errored.
	public boolean Delete(int question_idx) {
		if( (question_idx >= titles.size() || question_idx < 0) ) {
			return false;
		}
		
		titles.remove(question_idx);
		bodies.remove(question_idx);
		qtags.remove(question_idx);
		resolver.remove(question_idx);
		answers.remove(question_idx);
		return true;
	}
	
	/// Find questions by a substring of their title.
	/// Many questions can have the same characters in a title, so we return an array of indexes.
	public ArrayList<Integer> FindByTitle(String to_find) {
		var results = new ArrayList<Integer>();
		for( var i=0; i < titles.size(); i++ ) {
			if( titles.get(i).contains(to_find.toLowerCase()) ) {
				results.add(i);
			}
		}
		return results;
	}
	
	/// Find questions by their tags.
	/// This one returns a hashset since many questions can have similar tags.
	public ArrayList<Integer> FindByTag(String[] tags) {
		var results = new ArrayList<Integer>();
		for( var i=0; i < qtags.size(); i++ ) {
			for( var tag : tags ) {
				if( qtags.get(i).contains(tag.toLowerCase()) && !results.contains(i) ) {
					results.add(i);
				}
			}
		}
		return results;
	}
	
	public int NumberOf() {
		return titles.size();
	}
	
	/// mark off a question as resolved and store the answer index that resolved the question.
	public boolean MarkResolved(int question_idx, int answer_idx) {
		if( (question_idx >= titles.size() || question_idx < 0) || answer_idx < 0 || answer_idx >= answers.get(question_idx).NumberOf() ) {
			return false;
		}
		resolver.set(question_idx, answer_idx);
		return true;
	}
}