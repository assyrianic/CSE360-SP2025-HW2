import java.util.*;

public class HW2 {
	public static String InputGetStr(Scanner kb, String msg, boolean no_empty) {
		System.out.println(msg);
		for(;;) {
			kb.nextLine();
			var input = kb.nextLine();
			if( no_empty && input.length() <= 0 ) {
				System.out.println("input can't be empty, try again.");
				continue;
			}
			return input;
		}
	}
	
	public static int InputGetInt(Scanner kb, int default_value, String msg, String err_msg) {
		var choice = default_value;
		while( choice==default_value ) {
			System.out.println(msg);
			try {
				choice = kb.nextInt();
			} catch( InputMismatchException e ) {
				kb.next();
			}
			if( choice==default_value && err_msg != "" ) {
				System.out.println(err_msg);
			}
		}
		return choice;
	}
	
	public static double InputGetDouble(Scanner kb, double default_value, String msg, String err_msg) {
		var choice = default_value;
		while( choice==default_value ) {
			System.out.println(msg);
			try {
				choice = kb.nextDouble();
			} catch( InputMismatchException e ) {
				kb.next();
			}
			if( choice==default_value && err_msg != "" ) {
				System.out.println(err_msg);
			}
		}
		return choice;
	}
	
	public static void DisplayQuestionMini(Questions questions, int q_id) {
		final var title = questions.GetTitle(q_id);
		System.out.printf("Question ID: %d | '%s' (%d unread answers) [resolved?: %s]\n", q_id, title, questions.GetAnswers(q_id).NumberUnread(), questions.IsResolved(q_id) > 0? "Yes" : "No");
	}
	
	public static void DisplayQuestionFull(Questions questions, int q_id) {
		var answers = questions.GetAnswers(q_id);
		final var title = questions.GetTitle(q_id);
		System.out.printf("Question ID: %d | Title: '%s' | Unread Answers: %d", q_id, title, answers.NumberUnread());
		final var tags = questions.GetTags(q_id);
		if( tags.size() > 0 ) {
			System.out.printf("\nTags: ");
			for( var tag : tags ) {
				System.out.printf("[%s]", tag);
			}
			System.out.printf("\n");
		}
		final var body = questions.GetBody(q_id);
		if( body.length() > 0 ) {
			System.out.printf("\n'%s'\n", body);
		}
	}
	
	public static void InputQuestion(Scanner kb, Questions questions) {
		var title = InputGetStr(kb, "Enter your question title:", true);
		for( var i=0; i < questions.NumberOf(); i++ ) {
			if( questions.GetTitle(i).equalsIgnoreCase(title) ) {
				System.out.println("Duplicate question title, please try again.");
				return;
			}
		}
		
		var body = InputGetStr(kb, "Enter your question body [optional, enter to skip]:", false);
		var tags = InputGetStr(kb, "Enter question tags [optional, enter to skip]:", false);
		if( questions.Add(title, body, tags.split(",.;:| ")) < 0 ) {
			System.out.println("Failed to ask question, please try again.");
		}
	}
	public static void ProcessFindingQuestion(Scanner kb, Questions questions) {
		final var search_option = InputGetInt(kb, -1, "Find Question by...:\n[1]: Title\n[2]: Tags\n[3]: Question ID\n[0]: Go Back", "Invalid selection, try again");
		switch( search_option ) {
			case 1, 2: {
				var opt_strs = new String[]{"Enter words to search by title:", "Enter words to search by tag:"};
				var input_str = InputGetStr(kb, opt_strs[search_option-1], true);
				final var questions_found = search_option==1? questions.FindByTitle(input_str) : questions.FindByTag(input_str.split(",.;:| "));
				if( questions_found.size() > 0 ) {
					System.out.printf("found %d questions\n\nQuestions:\n", questions_found.size());
					for( var i=0 ; i < questions_found.size(); i++ ) {
						final var q_idx = questions_found.get(i);
						var title = questions.GetTitle(q_idx);
						System.out.printf("[%d]: '%s' (%d unread answers) [resolved?: %s]\n", i+1, title, questions.GetAnswers(q_idx).NumberUnread(), questions.IsResolved(q_idx) > 0? "Yes" : "No");
					}
					
					var question_option = InputGetInt(kb, -1, "Select a question to read [0 to go back]:", "Invalid selection, try again");
					question_option--;
					if( question_option >= 0 ) {
						final var q_id = questions_found.get(question_option);
						DisplayQuestionFull(questions, q_id);
						ReadOrPutAnswer(kb, questions, q_id);
					}
				} else {
					System.out.printf("found no questions with the words '%s'\n", input_str);
				}
				break;
			}
			case 3: {
				final var q_id = InputGetInt(kb, -1, "enter Question ID:", "Invalid selection, try again");
				if( q_id >= 0 && q_id < questions.NumberOf() ) {
					DisplayQuestionFull(questions, q_id);
					ReadOrPutAnswer(kb, questions, q_id);
				} else {
					System.out.printf("found no question with the ID '%d'\n", q_id);
				}
			}
		}
	}
	
	public static void ReadOrPutAnswer(Scanner kb, Questions questions, int question_idx) {
		final var answer_option = InputGetInt(kb, -1, "\n[1]: Answer Question\n[2]: Read Answers\n[3]: Go Back", "Invalid selection, try again");
		switch( answer_option ) {
			case 1: {
				var post = InputGetStr(kb, "enter your answer post:", true);
				questions.AddAnswer(question_idx, post);
				break;
			}
			case 2: {
				var curr_answer = 0;
				var answers = questions.GetAnswers(question_idx);
				while( curr_answer < answers.NumberOf() ) {
					var is_resolved = questions.IsResolved(question_idx)==1;
					final var post = answers.GetPost(curr_answer);
					System.out.printf("[1]: Next Answer | [2]: Prev. Answer | ");
					if( is_resolved ) {
						System.out.printf("[3]: Go to Resolving Answer");
					} else {
						System.out.printf("[3]: Mark Resolved");
					}
					System.out.printf(" | [0]: Stop Reading Answers\nAnswer[%d]:\n'%s'\n", curr_answer, post);
					answers.MarkRead(curr_answer);
					final var controller = InputGetInt(kb, -1, "", "Invalid selection, try again");
					switch( controller ) {
						case 0: return;
						case 1: curr_answer++; break;
						case 2: curr_answer--; break;
						case 3: {
							if( !is_resolved ) {
								questions.MarkResolved(question_idx, curr_answer);
							} else {
								curr_answer = questions.GetResolver(question_idx);
							}
						}
					}
					
					/// clamp answer index range
					if( curr_answer < 0 ) {
						curr_answer = answers.NumberOf() - 1;
					} else if( curr_answer >= answers.NumberOf() ) {
						curr_answer = 0;
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		var questions = new Questions();
		System.out.println("Welcome to the Student Question & Answer CLI Program.\n\n");
		for(;;) {
			final var option = InputGetInt(kb, -1, "Make a selection:\n[1]: Ask a Question\n[2]: Find Question\n[3]: List Questions\n[0]: Quit", "Invalid selection, try again");
			switch( option ) {
				case 1: {
					InputQuestion(kb, questions);
					break;
				}
				case 2: {
					ProcessFindingQuestion(kb, questions);
					break;
				}
				case 3: {
					System.out.printf("There are %d Questions\n\n", questions.NumberOf());
					var curr_question = 0;
					var stop = false;
					while( curr_question < questions.NumberOf() && !stop ) {
						DisplayQuestionMini(questions, curr_question);
						final var controller = InputGetInt(kb, -1, "[1]: Next Question | [2]: Prev. Question | [3]: Read/Put Answer | [0]: Go Back\n", "Invalid selection, try again");
						switch( controller ) {
							case 0: stop = true; break;
							case 1: curr_question++; break;
							case 2: curr_question--; break;
							case 3: ReadOrPutAnswer(kb, questions, curr_question);
						}
						
						/// clamp the question controller.
						if( curr_question < 0 ) {
							curr_question = questions.NumberOf() - 1;
						} else if( curr_question >= questions.NumberOf() ) {
							curr_question = 0;
						}
					}
					break;
				}
				case 0: {
					return;
				}
			}
		}
	}
}